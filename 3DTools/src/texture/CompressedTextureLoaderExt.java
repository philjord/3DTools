package texture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jogamp.java3d.CompressedImageComponent2D;
import org.jogamp.java3d.ImageComponent;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.NioImageBuffer;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TextureUnitState;
import org.jogamp.java3d.compressedtexture.FastByteArrayInputStream;
import org.jogamp.java3d.compressedtexture.dktxtools.dds.DDSDecompressor;
import org.jogamp.java3d.compressedtexture.etcpack.ETCPack.FORMAT;
import org.jogamp.java3d.compressedtexture.etcpack.QuickETC;

import compressedtexture.ASTCImage;
import compressedtexture.CompressedBufferedImage;
import compressedtexture.DDSImage;
import compressedtexture.KTXImage;
import javaawt.Graphics2D;
import javaawt.image.BufferedImage;
import tools.WeakValueHashMap;

/**
 * DDS support onto the jogl pipeline.
 * 
 * The following changes/additions to core java3d(jogl2) classes {@code JoglPipeline} has changes (to simply call
 * glCompressedImage) {@code TextureRetained}; has changes (to correctly deal with max mip map levels)
 * {@code DDSImageComponent}; a new subclass for ImageComponent {@code DDSImageComponentRetained}; a new subclass for
 * ImageComponent2DRetained
 * 
 * The following DDS loading classes {@code DDSTextureLoader}, {@code DDSBufferedImage}, {@code DDSImage} and
 * {@code DxtFlipper}
 * 
 * Also some helpful utils for debug (not needed to use DXT textures) {@code DSSTextureLoaderTester},
 * {@code DDSDecompressor}, {@code Color24} and {@code MiniFloat}
 */
public abstract class CompressedTextureLoaderExt {
	//Set this to true where S3TC support not available, this is big and slow
	public static boolean	RETURN_DECOMPRESSED_DDS	= false;
	public static boolean	DROP_0_MIP				= false;
	
	public static String 	DROPPED_MIP_0_PREFIX = "NoMip0";

	protected static int	anisotropicFilterDegree	= 0;

	public static void setAnisotropicFilterDegree(int d) {
		System.out.println("setAnisotropicFilterDegree=" + d);
		anisotropicFilterDegree = d;
	}

	/**
	 * A hashmap of the loaded {@code Texture2D} instances. Weak so that we can discard them if they are not in use by
	 * at least one Appearence node in the scene graph Note WeakValueHashMap are self expunging
	 */
	private static WeakValueHashMap<String, Texture2D>			loadedTextures			= new WeakValueHashMap<String, Texture2D>();
	private static WeakValueHashMap<String, TextureUnitState>	loadedTextureUnitStates	= new WeakValueHashMap<String, TextureUnitState>();

	//private static RequestStats requestStats = new RequestStats(loadedTextures);

	/**
	 * Called to early out in case of cache hit, very likely to return null! cached with no extension, so double up in
	 * type will be a problem
	 * @param filename
	 * @return Possibly a pre-loaded Texture, does not load if not found
	 */
	public static Texture checkCachedTexture(String filename) {
		return checkCachedTexture(filename, false);
	}
	public static Texture checkCachedTexture(String filename, boolean dropMip0) {
		//enable to test is caching is good
		//requestStats.request(filename);
		filename = dropMip0 ? filename + DROPPED_MIP_0_PREFIX : filename;
		return loadedTextures.get(filename);
	}

	/**
	 * cached with no extension, so double up in type will be a problem
	 * @param filename
	 * @return
	 */
	public static TextureUnitState checkCachedTextureUnitState(String filename) {
		return checkCachedTextureUnitState(filename, false);
	}
	public static TextureUnitState checkCachedTextureUnitState(String filename, boolean dropMip0) {
		//enable to test is caching is good
		//requestStats.request(filename);
		filename = dropMip0 ? filename + DROPPED_MIP_0_PREFIX : filename;

		return loadedTextureUnitStates.get(filename.replace(".dss", "").replace(".ktx", "").replace(".atc", ""));
	}

	public static void cacheTexture(String filename, Texture2D tex) {
		cacheTexture(filename, tex, false);
	}
	public static void cacheTexture(String filename, Texture2D tex, boolean dropMip0) {
		filename = dropMip0 ? filename + DROPPED_MIP_0_PREFIX : filename;
		loadedTextures.put(filename.replace(".dss", "").replace(".ktx", "").replace(".atc", ""), tex);
	}

	public static void cacheTextureUnitState(String filename, TextureUnitState tus) {
		cacheTextureUnitState(filename, tus, false);
	}
	public static void cacheTextureUnitState(String filename, TextureUnitState tus, boolean dropMip0) {
		filename = dropMip0 ? filename + DROPPED_MIP_0_PREFIX : filename;
		loadedTextureUnitStates.put(filename.replace(".dss", "").replace(".ktx", "").replace(".atc", ""), tus);
	}

	/**
	 * For debug purposes
	 */
	public static void clearCache() {
		loadedTextures.clear();
		loadedTextureUnitStates.clear();
	}

	protected static int computeLog(int value) {
		int i = 0;

		if (value == 0)
			return -1;
		for (;;) {
			if (value == 1)
				return i;
			value >>= 1;
			i++;
		}
	}

	private static int BUFSIZE = 16000;

	public static ByteBuffer toByteBuffer(InputStream in) throws IOException {
		if (in instanceof FastByteArrayInputStream) {
			//NOTE there is no performance gain from this, but a definite copy time loss
			//ByteBuffer out = ByteBuffer.allocateDirect(((FastByteArrayInputStream) in).getBuf().length);
			//out.order(ByteOrder.nativeOrder());
			//out.put(((FastByteArrayInputStream) in).getBuf());
			//out.rewind();
			//return out;
			return ByteBuffer.wrap(((FastByteArrayInputStream)in).getBuf());
		} else {
			//note toByteArray trims to size
			ByteArrayOutputStream out = new ByteArrayOutputStream(BUFSIZE);
			byte[] tmp = new byte[BUFSIZE];
			while (true) {
				int r = in.read(tmp);
				if (r == -1)
					break;

				out.write(tmp, 0, r);
			}

			return ByteBuffer.wrap(out.toByteArray());
		}
	}
	
	public static class UNKNOWN extends CompressedTextureLoaderExt {
		public static Texture getTexture(String filename, InputStream inputStream) {
			try {
				return getTexture(filename, toByteBuffer(inputStream));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		public static Texture getTexture(String filename, ByteBuffer inputBuffer) {
			// KTX most likely then DDS then ASTC, each will just fail with exception
			inputBuffer.mark();
			try {
				new KTXImage(inputBuffer);
				inputBuffer.reset();
				return KTX.getTexture(filename, inputBuffer);
			} catch (IOException e1) {
				try {
					inputBuffer.reset();
					DDSImage.read(inputBuffer);
					inputBuffer.reset();
					return DDS.getTexture(filename, inputBuffer);
				} catch (IOException e2) {
					try {
						inputBuffer.reset();
						new ASTCImage(inputBuffer);
						inputBuffer.reset();
						return ASTC.getTexture(filename, inputBuffer);
					} catch (IOException e) {
						System.out.println("IO problem with " + filename + " : it is not KTX, DDS, nor ASTC");
						return null;
					}
				}
			}
		}
	}

	//ASTC = https://en.wikipedia.org/wiki/Adaptive_scalable_texture_compression can be found in a ktx container
	public static class ASTC extends CompressedTextureLoaderExt {
		public static TextureUnitState getTextureUnitState(File file) {
			String filename = file.getAbsolutePath();
			try {
				return getTextureUnitState(filename, new FileInputStream(file));
			} catch (IOException e) {
				System.out.println(""	+ ASTC.class + " had a  IO problem with " + filename + " : " + e + " "
									+ e.getStackTrace() [0]);
				return null;
			}
		}

		public static TextureUnitState getTextureUnitState(String filename, InputStream inputStream) {
			return getTextureUnitState(filename, inputStream, false);
		}
		public static TextureUnitState getTextureUnitState(String filename, InputStream inputStream, boolean dropMip0) {
			TextureUnitState ret_val = checkCachedTextureUnitState(filename, dropMip0);

			if (ret_val == null) {
				Texture tex = getTexture(filename, inputStream, dropMip0);
				//notice nulls are fine

				TextureUnitState tus = new TextureUnitState();
				tus.setTexture(tex);
				tus.setName(filename);
				cacheTextureUnitState(filename, tus);
				ret_val = tus;
			}
			return ret_val;
		}

		public static TextureUnitState getTextureUnitState(String filename, ByteBuffer inputBuffer) {
			return getTextureUnitState(filename, inputBuffer, false);
		}
		public static TextureUnitState getTextureUnitState(String filename, ByteBuffer inputBuffer, boolean dropMip0) {
			TextureUnitState ret_val = checkCachedTextureUnitState(filename, dropMip0);

			if (ret_val == null) {
				Texture tex = getTexture(filename, inputBuffer, dropMip0);
				//notice nulls are fine

				TextureUnitState tus = new TextureUnitState();
				tus.setTexture(tex);
				tus.setName(filename);
				cacheTextureUnitState(filename, tus);
				ret_val = tus;
			}
			return ret_val;
		}

		/**
		 * Returns the associated Texture object or null if the image failed to load Note it may return a Texture loaded
		 * earlier
		 * @param file a dds image file
		 * @return A {@code Texture} with the associated DDS image or null if the image failed to load
		 */
		public static Texture getTexture(File file) {
			return getTexture(file, false);		
		}
		public static Texture getTexture(File file, boolean dropMip0) {
			String filename = file.getAbsolutePath();
			try {
				return getTexture(filename, new FileInputStream(file), dropMip0);
			} catch (IOException e) {
				System.out.println(""	+ ASTC.class + " had a  IO problem with " + filename + " : " + e + " "
									+ e.getStackTrace() [0]);
				return null;
			}
		}

		/**
		 * Returns the associated Texture object or null if the image failed to load Note it may return a Texture loaded
		 * earlier
		 * @param filename just a useful name for teh inputstreams source
		 * @param inputStream which is fully read into a {@code ByteBuffer} and must contain a dds texture
		 * @return A {@code Texture} with the associated DDS image
		 */
		public static Texture getTexture(String filename, InputStream inputStream) {
			return getTexture(filename, inputStream, false);
		}
		public static Texture getTexture(String filename, InputStream inputStream, boolean dropMip0) {
			// Check the cache for an instance first
			Texture ret_val = checkCachedTexture(filename, dropMip0);

			if (ret_val == null) {
				try {
					ASTCImage astcImage = new ASTCImage(toByteBuffer(inputStream));
					Texture2D tex = createTexture(filename, astcImage, dropMip0);
					ret_val = tex;
				} catch (IOException e) {
					System.out.println(""	+ ASTC.class + " had a  IO problem with " + filename + " : " + e + " "
										+ e.getStackTrace() [0]);
					return null;
				}
			}
			return ret_val;
		}

		/**
		 * Note avoid mappedbytebufffers as that will push texture loading (disk activity) onto the j3d thread which is
		 * bad, pull everything into byte arrays on the current thread
		 * @param filename
		 * @param inputBuffer
		 * @return
		 */
		public static Texture getTexture(String filename, ByteBuffer inputBuffer) {
			return getTexture(filename, inputBuffer, false);
		}
		public static Texture getTexture(String filename, ByteBuffer inputBuffer, boolean dropMip0) {
			// Check the cache for an instance first
			Texture ret_val = checkCachedTexture(filename, dropMip0);

			if (ret_val == null) {
				try {
					ASTCImage astcImage = new ASTCImage(inputBuffer);
					Texture2D tex = createTexture(filename, astcImage, dropMip0);
					ret_val = tex;
				} catch (IOException e) {
					System.out.println(""	+ ASTC.class + " had a  IO problem with " + filename + " : " + e + " "
										+ e.getStackTrace() [0]);
					return null;
				}
			}
			return ret_val;
		}
		protected static Texture2D createTexture(String filename, ASTCImage astcImage) {
			return createTexture(filename, astcImage, false);
		}
		/**
		 * Note dropMip0 ignored for now for ASTC
		 * @param filename
		 * @param astcImage
		 * @param dropMip0
		 * @return
		 */
		protected static Texture2D createTexture(String filename, ASTCImage astcImage, boolean dropMip0) {
			Texture2D tex = new Texture2D(
					astcImage.getNumMipMaps() <= 1 ? Texture.BASE_LEVEL : Texture.MULTI_LEVEL_MIPMAP, Texture.RGBA,
					astcImage.getWidth(), astcImage.getHeight());

			tex.setName(filename);

			tex.setBaseLevel(0);
			tex.setMaximumLevel(0);

			tex.setBoundaryModeS(Texture.WRAP);
			tex.setBoundaryModeT(Texture.WRAP);

			// better to let machine decide
			tex.setMinFilter(Texture.NICEST);
			tex.setMagFilter(Texture.NICEST);

			//defaults to Texture.ANISOTROPIC_NONE
			if (anisotropicFilterDegree > 0) {
				tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
				tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
			}

			BufferedImage image = new CompressedBufferedImage.ASTC(astcImage, 0, filename);
			tex.setImage(0, new CompressedImageComponent2D(ImageComponent.FORMAT_RGBA, image));

			cacheTexture(filename, tex);

			return tex;
		}
	}

	//DDS is https://en.wikipedia.org/wiki/DirectDraw_Surface but the compression is S3 so S3TC
	public static class DDS extends CompressedTextureLoaderExt {

		public static TextureUnitState getTextureUnitState(File file) {
			return getTextureUnitState(file, false);
		}
		public static TextureUnitState getTextureUnitState(File file, boolean dropMip0) {
			String filename = file.getAbsolutePath();
			try {
				return getTextureUnitState(filename, new FileInputStream(file), dropMip0);
			} catch (IOException e) {
				System.out.println(""	+ DDS.class + " had a  IO problem with " + filename + " : " + e + " "
									+ e.getStackTrace() [0]);
				return null;
			}
		}
		public static TextureUnitState getTextureUnitState(String filename, InputStream inputStream) {
			return getTextureUnitState(filename, inputStream, false);
		}
		public static TextureUnitState getTextureUnitState(String filename, InputStream inputStream, boolean dropMip0) {
			TextureUnitState ret_val = checkCachedTextureUnitState(filename, dropMip0);

			if (ret_val == null) {
				Texture tex = getTexture(filename, inputStream, dropMip0);
				//notice nulls are fine

				TextureUnitState tus = new TextureUnitState();
				tus.setTexture(tex);
				tus.setName(filename);
				cacheTextureUnitState(filename, tus);
				ret_val = tus;
			}
			return ret_val;
		}
		
		public static TextureUnitState getTextureUnitState(String filename, ByteBuffer inputBuffer) {
			return getTextureUnitState(filename, inputBuffer, false);
		}

		public static TextureUnitState getTextureUnitState(String filename, ByteBuffer inputBuffer, boolean dropMip0) {
			TextureUnitState ret_val = checkCachedTextureUnitState(filename, dropMip0);

			if (ret_val == null) {
				Texture tex = getTexture(filename, inputBuffer, dropMip0);
				//notice nulls are fine

				TextureUnitState tus = new TextureUnitState();
				tus.setTexture(tex);
				tus.setName(filename);
				cacheTextureUnitState(filename, tus);
				ret_val = tus;
			}
			return ret_val;
		}

		/**
		 * Returns the associated Texture object or null if the image failed to load Note it may return a Texture loaded
		 * earlier
		 * @param file a dds image file
		 * @return A {@code Texture} with the associated DDS image or null if the image failed to load
		 */
		public static Texture getTexture(File file) {
			return getTexture(file, false);
		}
		public static Texture getTexture(File file, boolean dropMip0) {
			String filename = file.getAbsolutePath();
			try {
				return getTexture(filename, new FileInputStream(file), dropMip0);
			} catch (IOException e) {
				System.out.println(""	+ DDS.class + " had a  IO problem with " + filename + " : " + e + " "
									+ e.getStackTrace() [0]);
				return null;
			}
		}

		/**
		 * Returns the associated Texture object or null if the image failed to load Note it may return a Texture loaded
		 * earlier
		 * @param filename just a useful name for teh inputstreams source
		 * @param inputStream which is fully read into a {@code ByteBuffer} and must contain a dds texture
		 * @return A {@code Texture} with the associated DDS image
		 */
		public static Texture getTexture(String filename, InputStream inputStream) {
			return getTexture(filename, inputStream, false);
		}
		public static Texture getTexture(String filename, InputStream inputStream, boolean dropMip0) {
			// Check the cache for an instance first
			Texture ret_val = checkCachedTexture(filename, dropMip0);

			if (ret_val == null) {
				try {
					DDSImage ddsImage = DDSImage.read(toByteBuffer(inputStream));
					Texture2D tex = createTexture(filename, ddsImage, dropMip0);
					ret_val = tex;
				} catch (IOException e) {
					System.out.println(""	+ DDS.class + " had a  IO problem with " + filename + " : " + e + " "
										+ e.getStackTrace() [0]);
					return null;
				}
			}
			return ret_val;
		}

		/**
		 * Note avoid mappedbytebufffers as that will push texture loading (disk activity) onto the j3d thread which is
		 * bad, pull everything into byte arrays on the current thread
		 * @param filename
		 * @param inputBuffer
		 * @return
		 */
		public static Texture getTexture(String filename, ByteBuffer inputBuffer) {
			return getTexture(filename, inputBuffer, false);
		}
		public static Texture getTexture(String filename, ByteBuffer inputBuffer, boolean dropMip0) {
			// Check the cache for an instance first
			Texture ret_val = checkCachedTexture(filename, dropMip0);

			if (ret_val == null) {
				try {
					DDSImage ddsImage = DDSImage.read(inputBuffer);
					Texture2D tex = createTexture(filename, ddsImage, dropMip0);
					ret_val = tex;
				} catch (IOException e) {
					System.out.println(""	+ DDS.class + " had a  IO problem with " + filename + " : " + e + " "
										+ e.getStackTrace() [0]);
					return null;
				}
			}
			return ret_val;
		}

		protected static Texture2D createTexture(String filename, DDSImage ddsImage) {
			return createTexture(filename, ddsImage, false);
		}
		protected static Texture2D createTexture(String filename, DDSImage ddsImage, boolean dropMip0) {

			// return null for unsupported types
			int pixelFormat = ddsImage.getPixelFormat();
			if ( pixelFormat == DDSImage.D3DFMT_DXT2 //
				|| pixelFormat == DDSImage.D3DFMT_DXT4 //
				|| pixelFormat == DDSImage.D3DFMT_UNKNOWN) {
				System.out.println("CompressedTextureLoader.createTexture - Unsupported DDS format " + ddsImage.getPixelFormat() + " for file " + filename);
				return null;
			}

			int levels = ddsImage.getNumMipMaps();
			// now check how big it should be! sometime these things run out with 0 width or 0 height size images
			int levels2 = Math.min(computeLog(ddsImage.getWidth()), computeLog(ddsImage.getHeight())) + 1;
			// use the lower of the two, to avoid 0 sizes going to the driver
			levels = levels > levels2 ? levels2 : levels;

			// always 1 level
			levels = levels == 0 ? 1 : levels;

			int mipMapMode = ddsImage.getNumMipMaps() <= 1 ? Texture.BASE_LEVEL : Texture.MULTI_LEVEL_MIPMAP;
			
			Texture2D tex;
			
			if (!(DROP_0_MIP || dropMip0)|| levels == 1) {

				//note Texture.RGBA is not used on the pipeline for compressed image, the buffered image holds that info
				tex = new Texture2D(mipMapMode, Texture.RGBA, ddsImage.getWidth(), ddsImage.getHeight());

				tex.setName(filename);

				tex.setBaseLevel(0);
				tex.setMaximumLevel(levels - 1);

				tex.setBoundaryModeS(Texture.WRAP);
				tex.setBoundaryModeT(Texture.WRAP);

				// better to let machine decide
				tex.setMinFilter(Texture.NICEST);
				tex.setMagFilter(Texture.NICEST);

				//defaults to Texture.ANISOTROPIC_NONE
				if (anisotropicFilterDegree > 0) {
					tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
					tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
				}

				for (int i = 0; i < levels; i++) {
					if (!RETURN_DECOMPRESSED_DDS) {
						BufferedImage image = new CompressedBufferedImage.DDS(ddsImage, i, filename);
						tex.setImage(i, new CompressedImageComponent2D(ImageComponent.FORMAT_RGBA, image));
					} else {

						//this would be the BufferedImage version, but Nio is faster
						//	BufferedImage decompressedImage = new DDSDecompressor(ddsImage, i, filename).convertImage();
						//	int format = decompressedImage
						//			.getType() == BufferedImage.TYPE_INT_RGB ? ImageComponent.FORMAT_RGB : ImageComponent.FORMAT_RGBA;
						//	tex.setImage(i, new ImageComponent2D(format, decompressedImage));

						NioImageBuffer decompressedImage = new DDSDecompressor(ddsImage, i, filename).convertImageNio();
						int format = decompressedImage
								.getImageType() == NioImageBuffer.ImageType.TYPE_INT_RGB ? ImageComponent.FORMAT_RGB : ImageComponent.FORMAT_RGBA;
						tex.setImage(i, new ImageComponent2D(format, decompressedImage, true, true));
					}
				}
				 
			} else {

				//ONLY for far textures where they do't exist == only for TES3
				tex = new Texture2D(mipMapMode, Texture.RGBA, ddsImage.getMipMap(1).getWidth(),
						ddsImage.getMipMap(1).getHeight());

				tex.setName(filename);

				tex.setBaseLevel(0);
				tex.setMaximumLevel(levels > 2 ? levels - 2 : 0);

				tex.setBoundaryModeS(Texture.WRAP);
				tex.setBoundaryModeT(Texture.WRAP);

				// better to let machine decide
				tex.setMinFilter(Texture.NICEST);
				tex.setMagFilter(Texture.NICEST);

				//defaults to Texture.ANISOTROPIC_NONE
				if (anisotropicFilterDegree > 0) {
					tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
					tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
				}

				// pull one higher and generally ruin the look of everything
				for (int i = 0; i < levels - 1; i++) {
					NioImageBuffer decompressedImage = new DDSDecompressor(ddsImage, i + 1, filename).convertImageNio();
					int format = decompressedImage
							.getImageType() == NioImageBuffer.ImageType.TYPE_INT_RGB ? ImageComponent.FORMAT_RGB : ImageComponent.FORMAT_RGBA;

					tex.setImage(i, new ImageComponent2D(format, decompressedImage, true, true));
				}
				 
			}
			cacheTexture(filename, tex, dropMip0);		
			

			ddsImage.close();

			return tex;

		}
	}

	//KTX is a continer for an image, the implied compress here is ETC2 https://en.wikipedia.org/wiki/Ericsson_Texture_Compression
	public static class KTX extends CompressedTextureLoaderExt {
		
		public static TextureUnitState getTextureUnitState(File file) {
			return getTextureUnitState(file, false);
		}
		public static TextureUnitState getTextureUnitState(File file, boolean dropMip0) {
			String filename = file.getAbsolutePath();
			try {
				return getTextureUnitState(filename, new FileInputStream(file), dropMip0);
			} catch (IOException e) {
				System.out.println(""	+ KTX.class + " had a  IO problem with " + filename + " : " + e + " "
									+ e.getStackTrace() [0]);
				return null;
			}
		}

		public static TextureUnitState getTextureUnitState(String filename, InputStream inputStream) {
			return getTextureUnitState(filename, inputStream, false);
		}
		public static TextureUnitState getTextureUnitState(String filename, InputStream inputStream, boolean dropMip0) {
			TextureUnitState ret_val = checkCachedTextureUnitState(filename, dropMip0);

			if (ret_val == null) {
				Texture tex = getTexture(filename, inputStream, dropMip0);
				//notice nulls are fine

				TextureUnitState tus = new TextureUnitState();
				tus.setTexture(tex);
				tus.setName(filename);
				cacheTextureUnitState(filename, tus);
				ret_val = tus;
			}
			return ret_val;
		}
		public static TextureUnitState getTextureUnitState(String filename, ByteBuffer inputBuffer) {
			return getTextureUnitState(filename, inputBuffer, false);
		}
		public static TextureUnitState getTextureUnitState(String filename, ByteBuffer inputBuffer, boolean dropMip0) {
			TextureUnitState ret_val = checkCachedTextureUnitState(filename, dropMip0);

			if (ret_val == null) {
				Texture tex = getTexture(filename, inputBuffer, dropMip0);
				//notice nulls are fine

				TextureUnitState tus = new TextureUnitState();
				tus.setTexture(tex);
				tus.setName(filename);
				cacheTextureUnitState(filename, tus);
				ret_val = tus;
			}
			return ret_val;
		}

		/**
		 * Returns the associated Texture object or null if the image failed to load Note it may return a Texture loaded
		 * earlier
		 * @param file a dds image file
		 * @return A {@code Texture} with the associated DDS image or null if the image failed to load
		 */
		public static Texture getTexture(File file) {
			return getTexture(file, false);
		}
		public static Texture getTexture(File file, boolean dropMip0) {
			String filename = file.getAbsolutePath();
			try {
				return getTexture(filename, new FileInputStream(file), dropMip0);
			} catch (IOException e) {
				System.out.println(""	+ KTX.class + " had a  IO problem with " + filename + " : " + e + " "
									+ e.getStackTrace() [0]);
				return null;
			}
		}

		/**
		 * Returns the associated Texture object or null if the image failed to load Note it may return a Texture loaded
		 * earlier
		 * @param filename just a useful name for teh inputstreams source
		 * @param inputStream which is fully read into a {@code ByteBuffer} and must contain a dds texture
		 * @return A {@code Texture} with the associated DDS image
		 */
		public static Texture getTexture(String filename, InputStream inputStream) {
			return getTexture(filename, inputStream, false);
		}
		public static Texture getTexture(String filename, InputStream inputStream, boolean dropMip0) {
			// Check the cache for an instance first
			Texture ret_val = checkCachedTexture(filename, dropMip0);

			if (ret_val == null) {
				try {
					KTXImage ktxImage = new KTXImage(toByteBuffer(inputStream));
					ret_val = createTexture(filename, ktxImage, dropMip0);
				} catch (IOException e) {
					System.out.println(""	+ KTX.class + " had a  IO problem with " + filename + " : " + e + " "
										+ e.getStackTrace() [0]);
					return null;
				}
			}
			return ret_val;
		}

		/**
		 * Note avoid mappedbytebufffers as that will push texture loading (disk activity) onto the j3d thread which is
		 * bad, pull everything into byte arrays on the current thread
		 * @param filename
		 * @param inputBuffer
		 * @return
		 */
		public static Texture getTexture(String filename, ByteBuffer inputBuffer) {
			return getTexture(filename, inputBuffer, false);
		}
		public static Texture getTexture(String filename, ByteBuffer inputBuffer, boolean dropMip0) {
			// Check the cache for an instance first
			Texture ret_val = checkCachedTexture(filename, dropMip0);

			if (ret_val == null) {
				try {
					KTXImage ktxImage = new KTXImage(inputBuffer);
					ret_val = createTexture(filename, ktxImage, dropMip0);
				} catch (IOException e) {
					System.out.println(""	+ KTX.class + " had a  IO problem with " + filename + " : " + e + " "
										+ e.getStackTrace() [0]);
					return null;
				}
			}
			return ret_val;

		}
		protected static Texture2D createTexture(String filename, KTXImage ktxImage) {
			return createTexture(filename, ktxImage, false);
		}
		protected static Texture2D createTexture(String filename, KTXImage ktxImage, boolean dropMip0) {
			// unsupported type will have failed already		

			int levels = ktxImage.getNumMipMaps();
			// now check how big it should be! sometime these things run out with 0 width or 0 height size images
			int levels2 = Math.min(computeLog(ktxImage.getWidth()), computeLog(ktxImage.getHeight())) + 1;
			// use the lower of the two, to avoid 0 sizes going to the driver
			levels = levels > levels2 ? levels2 : levels;

			// always 1 level
			levels = levels == 0 ? 1 : levels;

			int mipMapMode = ktxImage.getNumMipMaps() <= 1 ? Texture.BASE_LEVEL : Texture.MULTI_LEVEL_MIPMAP;
			Texture2D tex;
			
			if (!(DROP_0_MIP || dropMip0)|| levels == 1) {
	
				//note Texture.RGBA is not used on the pipeline for compressed image, the buffered image holds that info
				tex = new Texture2D(mipMapMode, Texture.RGBA, ktxImage.getWidth(), ktxImage.getHeight());
	
				tex.setName(filename);
	
				tex.setBaseLevel(0);
				tex.setMaximumLevel(levels - 1);
	
				tex.setBoundaryModeS(Texture.WRAP);
				tex.setBoundaryModeT(Texture.WRAP);
	
				// better to let machine decide
				tex.setMinFilter(Texture.NICEST);
				tex.setMagFilter(Texture.NICEST);
	
				//defaults to Texture.ANISOTROPIC_NONE
				if (anisotropicFilterDegree > 0) {
					tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
					tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
				}
	
				for (int i = 0; i < levels; i++) {
					BufferedImage image = new CompressedBufferedImage.KTX(ktxImage, i, filename);
					tex.setImage(i, new CompressedImageComponent2D(ImageComponent.FORMAT_RGBA, image));
				}

				 
			} else {

				//ONLY for far textures where they do't exist == only for TES3
				tex = new Texture2D(mipMapMode, Texture.RGBA, ktxImage.getWidth()/2, ktxImage.getHeight()/2);

				tex.setName(filename);

				tex.setBaseLevel(0);
				tex.setMaximumLevel(levels > 2 ? levels - 2 : 0);

				tex.setBoundaryModeS(Texture.WRAP);
				tex.setBoundaryModeT(Texture.WRAP);

				// better to let machine decide
				tex.setMinFilter(Texture.NICEST);
				tex.setMagFilter(Texture.NICEST);

				//defaults to Texture.ANISOTROPIC_NONE
				if (anisotropicFilterDegree > 0) {
					tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
					tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
				}

				// pull one higher and generally ruin the look of everything
				for (int i = 0; i < levels - 1; i++) {
					BufferedImage image = new CompressedBufferedImage.KTX(ktxImage, i + 1, filename);
					tex.setImage(i, new CompressedImageComponent2D(ImageComponent.FORMAT_RGBA, image));			
				}
				
			}			
			cacheTexture(filename, tex, dropMip0);		

			return tex;
		}
		
		public static Texture getTexture(String filename, BufferedImage image) {

			// grab the raw bits and convert to ktx
			if (image.getType() != BufferedImage.TYPE_INT_RGB && image.getType() != BufferedImage.TYPE_INT_ARGB) {
				// Transform as TYPE_INT_ARGB or TYPE_INT_RGB (much faster than calling image.getRGB())
				BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), image
						.getType() == BufferedImage.TYPE_4BYTE_ABGR || image.getType() == BufferedImage.TYPE_4BYTE_ABGR_PRE ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D)tmp.getGraphics();
				g.drawImage(image, null, 0, 0);
				g.dispose();
				image = tmp;
			}
			int[] imageBits = image.getRaster().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);

			// muck about to get ints to bytes
			IntBuffer ib = IntBuffer.wrap(imageBits);
			ByteBuffer bb = ByteBuffer.allocateDirect(imageBits.length * 4);
			bb.asIntBuffer().put(ib);
			bb.rewind();

			//ok so now find the RGB or RGBA byte buffers
			//FIXME: ETCPAck needs a single 4 channel method for compression
			byte[] img = null;
			byte[] imgalpha = null;
			if (image.getType() == BufferedImage.TYPE_INT_RGB) {
				img = new byte[(bb.capacity() / 4) * 3];
				for (int i = 0; i < img.length / 3; i++) {
					bb.get();// discard alpha (it's from a 4 byte int buffer)
					img [i * 3 + 0] = bb.get();
					img [i * 3 + 1] = bb.get();
					img [i * 3 + 2] = bb.get();
				}
			} else if (image.getType() == BufferedImage.TYPE_INT_ARGB) {
				// copy RGB 3 sets out then 1 sets of alpha 
				img = new byte[(bb.capacity() / 4) * 3];
				imgalpha = new byte[(bb.capacity() / 4)];
				for (int i = 0; i < img.length / 3; i++) {
					imgalpha [i] = bb.get();
					img [i * 3 + 0] = bb.get();
					img [i * 3 + 1] = bb.get();
					img [i * 3 + 2] = bb.get();
				}
			}

			FORMAT format = image
					.getType() == BufferedImage.TYPE_INT_RGB ? FORMAT.ETC2PACKAGE_RGB : FORMAT.ETC2PACKAGE_RGBA;
			QuickETC ep = new QuickETC();
			ByteBuffer ktxBB = ep.compressImageToByteBuffer(img, imgalpha, image.getWidth(), image.getHeight(), format,
					true);

			Texture texture = CompressedTextureLoaderExt.KTX.getTexture(filename, ktxBB);
			return texture;
		}
	}
}
