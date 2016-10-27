package tools.compressedtexture.astc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jogamp.java3d.ASTCImageComponent2D;
import org.jogamp.java3d.ImageComponent;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TextureUnitState;

import javaawt.image.BufferedImage;
import tools.compressedtexture.CompressedTextureLoader;

/**
 * See DDSTextureLoader
 */
public class ASTCTextureLoader extends CompressedTextureLoader
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
					.println("" + ASTCTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
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
					.println("" + ASTCTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
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
				ASTCImage astcImage = new ASTCImage(toByteBuffer(inputStream));

				Texture2D tex = createTexture(filename, astcImage);
				ret_val = tex;
			}
			catch (IOException e)
			{
				System.out.println(
						"" + ASTCTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
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
			ASTCImage astcImage = new ASTCImage(inputBuffer);
			Texture2D tex = createTexture(filename, astcImage);
			ret_val = tex;
		}
		return ret_val;
	}

	private static Texture2D createTexture(String filename, ASTCImage astcImage)
	{
		Texture2D tex = new Texture2D(astcImage.getNumMipMaps() <= 1 ? Texture.BASE_LEVEL : Texture.MULTI_LEVEL_MIPMAP, Texture.RGBA,
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
		if (anisotropicFilterDegree > 0)
		{
			tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
			tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
		}

		BufferedImage image = new ASTCBufferedImage(astcImage, 0, filename);
		tex.setImage(0, new ASTCImageComponent2D(ImageComponent.FORMAT_RGBA, image));

		cacheTexture(filename, tex);

		return tex;
	}

}
