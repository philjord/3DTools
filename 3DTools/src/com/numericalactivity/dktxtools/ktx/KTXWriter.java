package com.numericalactivity.dktxtools.ktx;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.numericalactivity.dktxtools.TextureFormat;
import com.numericalactivity.dktxtools.dds.DDSFormatException;
import com.numericalactivity.dktxtools.utils.TextureUtils;

/**
 * Classe qui permet de créer un fichier KTX
 */
public class KTXWriter
{

	protected KTXHeader.Writer _headers;
	protected KTXMetadata.Writer _metas;
	protected KTXTextureData.Writer _textureData;

	/**
	 * Constructeur
	 */
	public KTXWriter()
	{
	}

	public KTXWriter(boolean mipmapped, boolean isCubemap, int width, int height) throws KTXFormatException
	{
		initialize(mipmapped, isCubemap, width, height);
	}

	/**
	 * Initialise l'objet
	 * @param mipmapped indique si la texture est mipmappée
	 * @param isCubemap indique si la texture est un cubemap à 6 faces
	 * @param width largeur de la texture
	 * @param height hauteur de la texture
	 * @throws KTXFormatException
	 */
	public void initialize(boolean mipmapped, boolean isCubemap, int width, int height) throws KTXFormatException
	{
		// on calcule le nombre de niveaux mipmaps
		int numberOfMipmapLevels = mipmapped ? TextureUtils.getNumberOfMipmaps(width, height) : 1;

		// on calcule le nombre de faces
		// si une texture est un cubemap, il y aura 6 faces, sinon 1.
		byte numberOfFaces = (byte) (isCubemap ? 6 : 1);

		// on crée les objet header, metadata et textureData
		_metas = new KTXMetadata.Writer();
		_headers = new KTXHeader.Writer(_metas);
		_textureData = new KTXTextureData.Writer(_headers, numberOfMipmapLevels, numberOfFaces);
		setNumberOfMipmaps(numberOfMipmapLevels);
		setCubemap(isCubemap);
		setDimensions(width, height);
	}

	/**
	 * Défini les dimensions de la texture
	 * @param width
	 * @param height
	 */
	public void setDimensions(int width, int height)
	{
		_headers.setDimensions(width, height, 0);
		_textureData.setDimensions(0, width, height);
	}

	/**
	 * Défini le format OpenGL pour les textures compressées
	 * @param internalFormat une des constantes TextureFormat.GL_* correspondant à un format de texture compressée
	 */
	public void setCompressedFormat(int internalFormat)
	{
		int baseInternalFormat = 0;

		switch (internalFormat)
		{
		case TextureFormat.GL_ATC_RGB_AMD:
		case TextureFormat.GL_COMPRESSED_RGB_PVRTC_2BPPV1_IMG:
		case TextureFormat.GL_COMPRESSED_RGB_PVRTC_4BPPV1_IMG:
		case TextureFormat.GL_COMPRESSED_RGB_S3TC_DXT1_EXT:
		case TextureFormat.GL_ETC1_RGB8:
			baseInternalFormat = TextureFormat.GL_RGB;
			break;

		case TextureFormat.GL_ATC_RGBA_EXPLICIT_ALPHA_AMD:
		case TextureFormat.GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD:
		case TextureFormat.GL_COMPRESSED_RGBA_PVRTC_2BPPV1_IMG:
		case TextureFormat.GL_COMPRESSED_RGBA_PVRTC_4BPPV1_IMG:
		case TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT:
		case TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT:
		case TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT:
			baseInternalFormat = TextureFormat.GL_RGBA;
			break;
		}

		_headers.setCompressedGLFormat(internalFormat, baseInternalFormat);
	}

	/**
	 * Défini le format OpenGL pour les textures non compressées
	 * @param internalFormat une des constantes TextureFormat.GL_* correspondant à un format de texture non compressée
	 */
	public void setUncompressedFormat(int format)
	{
		// on détermine l'internalFormat
		int internalFormat = 0;

		if (TextureFormat.GL_ALPHA == format)
		{
			internalFormat = TextureFormat.GL_ALPHA8;
		}
		else if (TextureFormat.GL_LUMINANCE == format)
		{
			internalFormat = TextureFormat.GL_LUMINANCE8;
		}
		else if (TextureFormat.GL_LUMINANCE_ALPHA == format)
		{
			internalFormat = TextureFormat.GL_LUMINANCE8_ALPHA8;
		}
		else if (TextureFormat.GL_RGB == format)
		{
			internalFormat = TextureFormat.GL_RGB8;
		}
		else if (TextureFormat.GL_RGBA == format)
		{
			internalFormat = TextureFormat.GL_RGBA8;
		}

		_headers.setGLFormat(internalFormat, format, format, TextureFormat.GL_UNSIGNED_BYTE, 1);
	}

	/**
	 * Défini le format OpenGL pour les textures non compressées
	 * @param internalFormat une des constantes TextureFormat.GL_* correspondant à un format de texture non compressée
	 * @param baseInternalFormat format interne de base (ex: LUMINANCE4_ALPHA4 a un format de base LUMINANCE_ALPHA)
	 * @param format correspond au paramètre 'format' de 'glTexImage2D()'
	 */
	public void setUncompressedFormat(int internalFormat, int baseInternalFormat, int format)
	{
		_headers.setGLFormat(internalFormat, baseInternalFormat, format, TextureFormat.GL_UNSIGNED_BYTE, 1);
	}

	/**
	 * Défini le nombre de niveaux mipmap. Défini automatiquement les bonnes entêtes en conséquence.
	 * @param numberOfMipmapLevels
	 * @throws DDSFormatException
	 */
	public void setNumberOfMipmaps(int numberOfMipmapLevels) throws KTXFormatException
	{
		if (numberOfMipmapLevels > 0)
		{
			_headers.setNumberOfMipmapLevels(numberOfMipmapLevels);
			_textureData.setNumberOfMipmapLevels(numberOfMipmapLevels);
		}
		else
		{
			throw new KTXFormatException("numberOfMipmaps can't be negative");
		}
	}

	/**
	 * Défini si la texture est un cubemap et défini automatiquement les bonnes entêtes en conséquence.
	 * Si isCubemap est a true, 6 faces seront déclarées dans les entêtes.
	 * @param isCubemap
	 * @throws DDSFormatException
	 */
	public void setCubemap(boolean isCubemap) throws KTXFormatException
	{
		if (isCubemap)
		{
			_headers.setNumberOfFaces(6);
			_textureData.setNumberOfFaces(6);
		}
		else
		{
			_headers.setNumberOfFaces(1);
			_textureData.setNumberOfFaces(1);
		}
	}

	/**
	 * Écrit le fichier
	 * @param out
	 * @throws IOException 
	 * @throws KTXFormatException 
	 */
	public void write(OutputStream out) throws IOException, KTXFormatException
	{
		// on crée un flux bufferisé à partir du flux passé en entrée
		if (!(out instanceof BufferedOutputStream))
		{
			out = new BufferedOutputStream(out);
		}

		_headers.write((BufferedOutputStream) out);
		_metas.write((BufferedOutputStream) out);
		_textureData.write((BufferedOutputStream) out);
		out.flush();
		out.close();
	}

	/**
	 * Retourne les headers
	 * @return
	 */
	public KTXHeader.Writer getHeaders()
	{
		return _headers;
	}

	/**
	 * Retourne les métadonnées
	 * @return
	 */
	public KTXMetadata.Writer getMetadata()
	{
		return _metas;
	}

	/**
	 * Retourne les données de texture
	 * @return
	 */
	public KTXTextureData.Writer getTextureData()
	{
		return _textureData;
	}

}
