package tools.compressedtexture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TextureUnitState;

import tools.WeakValueHashMap;
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
public abstract class CompressedTextureLoader
{
	protected static int anisotropicFilterDegree = 0;

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
	private static WeakValueHashMap<String, TextureUnitState> loadedTextureUnitStates = new WeakValueHashMap<String, TextureUnitState>();

	//private static RequestStats requestStats = new RequestStats(loadedTextures);

	/**
	* Called to early out in case of cache hit, very likely to return null!
	* cached with no extension, so double up in type will be a problem
	* @param filename
	* @return Possibly a pre-loaded Texture, does not load if not found
	*/
	public static Texture checkCachedTexture(String filename)
	{
		//enable to test is caching is good
		//requestStats.request(filename);

		return loadedTextures.get(filename);
	}

	/**
	 * cached with no extension, so double up in type will be a problem
	 * @param filename
	 * @return
	 */
	public static TextureUnitState checkCachedTextureUnitState(String filename)
	{
		//enable to test is caching is good
		//requestStats.request(filename);

		return loadedTextureUnitStates.get(filename.replace(".dss", "").replace(".ktx", "").replace(".atc", ""));
	}

	public static void cacheTexture(String filename, Texture2D tex)
	{
		loadedTextures.put(filename.replace(".dss", "").replace(".ktx", "").replace(".atc", ""), tex);
	}

	public static void cacheTextureUnitState(String filename, TextureUnitState tus)
	{
		loadedTextureUnitStates.put(filename.replace(".dss", "").replace(".ktx", "").replace(".atc", ""), tus);
	}

	/**
	 * For debug purposes
	 */
	public static void clearCache()
	{
		loadedTextures.clear();
		loadedTextureUnitStates.clear();
	}

	protected static int computeLog(int value)
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
			//NOTE there is no performance gain from this, but a definite copy time loss
			//ByteBuffer out = ByteBuffer.allocateDirect(((FastByteArrayInputStream) in).getBuf().length);
			//out.order(ByteOrder.nativeOrder());
			//out.put(((FastByteArrayInputStream) in).getBuf());
			//out.rewind();
			//return out;
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

}
