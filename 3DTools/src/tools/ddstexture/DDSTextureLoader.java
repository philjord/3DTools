package tools.ddstexture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.media.j3d.DDSImageComponent2D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;

import tools.io.FastByteArrayInputStream;

/**
 * DDS support onto the jogl pipeline.
 * 
 * The following changes/additions to core java3d(jogl2) classes
 * {@code JoglPipeline} has changes (to simply call glCompressedImage)
 * {@code TextureRetained}; has changes (to correctly deal with max mip map levels) 
 * {@code DDSImageComponent}; a new subclass for ImageComponent
 * {@code DDSImageComponentRetained}; a new subclass for ImageComponent2DRetained
 * 
 * The following DDS loading classes
 * {@code DDSTextureLoader}, {@code DDSBufferedImage}, {@code DDSImage} and {@code DxtFlipper}
 * 
 * Also some helpful utils for debug (not needed to use DXT textures)
 * {@code DSSTextureLoaderTester}, {@code DDSDecompressor}, {@code Color24} and {@code MiniFloat}
 */
public class DDSTextureLoader
{
	public static final int BLOCK_SIZE = 4;

	private static int anisotropicFilterDegree = 0;

	//TODO: use this in the graphcs config settings
	public static void setAnisotropicFilterDegree(int d)
	{
		System.out.println("setAnisotropicFilterDegree=" + d);
		anisotropicFilterDegree = d;
	}

	/**
	 * A hashmap of the loaded {@code Texture2D} instances. Weak so that we can discard them if they are not
	 * in use by at least one Appearence node in the scene graph
	 * Note WeakValueHashMap are self expunging
	 */
	private static WeakValueHashMap<String, Texture2D> loadedTextures = new WeakValueHashMap<String, Texture2D>();

	/**
	 * Returns the associated Texture object or null if the image failed to load
	 * Note it may return a Texture loaded earlier
	 * @param file a dds image file
	 * @return A {@code Texture} with the associated DDS image or null if the image failed to load
	 */
	public static Texture getTexture(File file)
	{
		String filename = file.getAbsolutePath();
		try
		{
			return getTexture(filename, new FileInputStream(file));
		}
		catch (IOException e)
		{
			System.out
					.println("" + DDSTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
			return null;
		}
	}

	/**
	 * Returns the associated Texture object or null if the image failed to load
	 * Note it may return a Texture loaded earlier
	 * @param filename just a useful name for teh inputstreams source
	 * @param inputStream which is fully read into a {@code ByteBuffer} and must contain a dds texture
	 * @return A {@code Texture} with the associated DDS image
	 */
	public static Texture getTexture(String filename, InputStream inputStream)
	{
		try
		{
			// Check the cache for an instance first
			Texture ret_val = loadedTextures.get(filename);

			if (ret_val == null)
			{
				//TODO: if textures weren't compressed I could hand out mapped bytebuffers all the way through to jogl
				
				DDSImage ddsImage = DDSImage.read(toByteBuffer(inputStream));

				// return null for unsupproted types
				if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT2 //
						|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT4 //
						|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_UNKNOWN)
				{
					System.out.println("Unsupported DDS format " + ddsImage.getPixelFormat() + " for file " + filename);
					return null;
				}

				int levels = ddsImage.getNumMipMaps();
				// now check how big it should be! sometime these things run out with 0 width or 0 height size images
				int levels2 = Math.min(computeLog(ddsImage.getWidth()), computeLog(ddsImage.getHeight())) + 1;
				// use the lower of the two, to avoid 0 sizes going to the driver
				levels = levels > levels2 ? levels2 : levels;

				// always 1 level
				levels = levels == 0 ? 1 : levels;

				Texture2D tex = new Texture2D(ddsImage.getNumMipMaps() <= 1 ? Texture.BASE_LEVEL : Texture.MULTI_LEVEL_MIPMAP,
						Texture.RGBA, ddsImage.getWidth(), ddsImage.getHeight());

				tex.setName(filename);

				tex.setBaseLevel(0);
				tex.setMaximumLevel(levels - 1);

				tex.setBoundaryModeS(Texture.WRAP);
				tex.setBoundaryModeT(Texture.WRAP);

				// better to let machine decide
				tex.setMinFilter(Texture.NICEST);
				tex.setMagFilter(Texture.NICEST);

				//TODO: allow blur! how does this work?
				//tex.setMinFilter(Texture.FILTER4);
				//tex.setMagFilter(Texture.FILTER4);
				//tex.setFilter4Func(new float[]
				//{ 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,//
				//		0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, //
				//		0.5f });

				//defaults to Texture.ANISOTROPIC_NONE
				if (anisotropicFilterDegree > 0)
				{
					tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
					tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
				}

				for (int i = 0; i < levels; i++)
				{
					BufferedImage image = new DDSBufferedImage(ddsImage, i, filename);
					tex.setImage(i, new DDSImageComponent2D(ImageComponent.FORMAT_RGBA, image));
				}

				loadedTextures.put(filename, tex);
				ret_val = tex;

				ddsImage.close();

			}

			return ret_val;
		}
		catch (IOException e)
		{
			System.out
					.println("" + DDSTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
			return null;
		}

	}

	/**
	* Called to early out in case of cache hit, very likely to return null!
	* @param filename
	* @return Possibly a pre-loaded Texture, does not load if not found
	*/
	public static Texture checkCachedTexture(String filename)
	{
		return loadedTextures.get(filename);
	}

	/**
	 * For debug purposes
	 */
	public static void clearCache()
	{
		loadedTextures.clear();
	}

	private static int computeLog(int value)
	{
		int i = 0;

		if (value == 0)
			return -1;
		for (;;)
		{
			if (value == 1)
				return i;
			value >>= 1;
			i++;
		}
	}

	private static int BUFSIZE = 16000;

	public static ByteBuffer toByteBuffer(InputStream in) throws IOException
	{
		if (in instanceof FastByteArrayInputStream)
		{
			return ByteBuffer.wrap(((FastByteArrayInputStream) in).getBuf());
		}
		else
		{
			//note toByteArray trims to size
			ByteArrayOutputStream out = new ByteArrayOutputStream(BUFSIZE);
			byte[] tmp = new byte[BUFSIZE];
			while (true)
			{
				int r = in.read(tmp);
				if (r == -1)
					break;

				out.write(tmp, 0, r);
			}

			return ByteBuffer.wrap(out.toByteArray());
		}
	}

	private static class WeakValueHashMap<K, V>
	{
		private final ReferenceQueue<WeakReferenceKey<V>> queue = new ReferenceQueue<WeakReferenceKey<V>>();

		private LinkedHashMap<K, WeakReferenceKey<V>> map = new LinkedHashMap<K, WeakReferenceKey<V>>();

		public V get(K key)
		{
			expungeStaleEntries();
			WeakReferenceKey<V> ref = map.get(key);
			if (ref != null)
			{
				V v = ref.get();

				return v;
			}
			return null;

		}

		public V put(K key, V value)
		{
			expungeStaleEntries();
			V oldV = get(key);
			WeakReferenceKey<V> kv = new WeakReferenceKey<V>(key, value, queue);
			map.put(key, kv);
			return oldV;
		}

		public void clear()
		{
			map.clear();
		}

		@SuppressWarnings("unused")
		public int size()
		{
			return map.size();
		}

		@SuppressWarnings("unused")
		public Set<K> keySet()
		{
			return map.keySet();
		}

		/**
		* Expunges stale entries from the table.
		*/
		@SuppressWarnings("unchecked")
		private void expungeStaleEntries()
		{
			WeakReferenceKey<V> ref;
			while ((ref = (WeakReferenceKey<V>) queue.poll()) != null)
			{
				map.remove(ref.key);
			}
		}

		private static class WeakReferenceKey<Z> extends WeakReference<Z>
		{
			public Object key;

			@SuppressWarnings(
			{ "unchecked", "rawtypes" })
			public WeakReferenceKey(Object k, Z v, ReferenceQueue queue)
			{
				super(v, queue);
				this.key = k;
			}
		}

	}
}
