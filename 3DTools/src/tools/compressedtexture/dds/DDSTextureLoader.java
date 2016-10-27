package tools.compressedtexture.dds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jogamp.java3d.DDSImageComponent2D;
import org.jogamp.java3d.ImageComponent;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TextureUnitState;

import javaawt.image.BufferedImage;
import tools.compressedtexture.CompressedTextureLoader;

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
public class DDSTextureLoader extends CompressedTextureLoader
{

	public static TextureUnitState getTextureUnitState(File file)
	{
		String filename = file.getAbsolutePath();
		try
		{
			return getTextureUnitState(filename, new FileInputStream(file));
		}
		catch (IOException e)
		{
			System.out
					.println("" + DDSTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
			return null;
		}
	}

	public static TextureUnitState getTextureUnitState(String filename, InputStream inputStream)
	{
		TextureUnitState ret_val = checkCachedTextureUnitState(filename);

		if (ret_val == null)
		{
			Texture tex = getTexture(filename, inputStream);
			//notice nulls are fine

			TextureUnitState tus = new TextureUnitState();
			tus.setTexture(tex);
			tus.setName(filename);
			cacheTextureUnitState(filename, tus);
			ret_val = tus;
		}
		return ret_val;
	}

	public static TextureUnitState getTextureUnitState(String filename, ByteBuffer inputBuffer)
	{
		TextureUnitState ret_val = checkCachedTextureUnitState(filename);

		if (ret_val == null)
		{
			Texture tex = getTexture(filename, inputBuffer);
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
		// Check the cache for an instance first
		Texture ret_val = checkCachedTexture(filename);

		if (ret_val == null)
		{
			try
			{
				DDSImage ddsImage = DDSImage.read(toByteBuffer(inputStream));
				Texture2D tex = createTexture(filename, ddsImage);
				ret_val = tex;
			}
			catch (IOException e)
			{
				System.out.println(
						"" + DDSTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
				return null;
			}
		}
		return ret_val;
	}

	/**
	 * Note avoid mappedbytebufffers as that will push texture loading (disk activity) onto the j3d thread
	 * which is bad, pull everything into byte arrays on the current thread
	 * @param filename
	 * @param inputBuffer
	 * @return
	 */

	public static Texture getTexture(String filename, ByteBuffer inputBuffer)
	{
		// Check the cache for an instance first
		Texture ret_val = checkCachedTexture(filename);

		if (ret_val == null)
		{
			try
			{
				DDSImage ddsImage = DDSImage.read(inputBuffer);
				Texture2D tex = createTexture(filename, ddsImage);
				ret_val = tex;
			}
			catch (IOException e)
			{
				System.out.println(
						"" + DDSTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
				return null;
			}
		}
		return ret_val;
	}

	private static Texture2D createTexture(String filename, DDSImage ddsImage)
	{

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

		int mipMapMode = ddsImage.getNumMipMaps() <= 1 ? Texture.BASE_LEVEL : Texture.MULTI_LEVEL_MIPMAP;
		//note Texture.RGBA is not used on the pipeline for compressed image, the buffered image holds that info
		Texture2D tex = new Texture2D(mipMapMode, Texture.RGBA, ddsImage.getWidth(), ddsImage.getHeight());

		tex.setName(filename);

		tex.setBaseLevel(0);
		tex.setMaximumLevel(levels - 1);

		tex.setBoundaryModeS(Texture.WRAP);
		tex.setBoundaryModeT(Texture.WRAP);

		// better to let machine decide
		tex.setMinFilter(Texture.NICEST);
		tex.setMagFilter(Texture.NICEST);

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

		cacheTexture(filename, tex);

		ddsImage.close();

		return tex;

	}

}
