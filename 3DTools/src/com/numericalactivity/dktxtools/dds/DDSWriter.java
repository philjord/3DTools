package com.numericalactivity.dktxtools.dds;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.numericalactivity.dktxtools.TextureFormat;
import com.numericalactivity.dktxtools.utils.TextureUtils;

/**
 * Classe qui permet de créer un fichier DDS
 */
public class DDSWriter
{

	protected DDSHeader.Writer _headers;
	protected DDSTextureData.Writer _textureData;

	/**
	 * Constructeur
	 */
	public DDSWriter()
	{
	}

	/**
	 * Initialise l'objet
	 * @param mipmapped indique si la texture est mipmappée
	 * @param isCubemap indique si la texture est un cubemap à 6 faces
	 * @param width largeur de la texture
	 * @param height hauteur de la texture
	 * @throws IOException
	 * @throws DDSFormatException
	 */
	public void initialize(boolean mipmapped, boolean isCubemap, int width, int height) throws DDSFormatException
	{
		// on calcule le nombre de niveaux mipmaps
		int numberOfMipmapLevels = mipmapped ? TextureUtils.getNumberOfMipmaps(width, height) : 1;

		// on calcule le nombre de faces
		// si une texture est un cubemap, il y aura 6 faces, sinon 1.
		byte numberOfFaces = (byte) (isCubemap ? 6 : 1);

		// on crée les objet header et textureData
		_headers = new DDSHeader.Writer();
		_textureData = new DDSTextureData.Writer(_headers, numberOfMipmapLevels, numberOfFaces);
		setNumberOfMipmaps(numberOfMipmapLevels);
		setCubemap(isCubemap);
		setDimensions(width, height);

		// on défini les flags obligatoires
		_headers.addCaps(DDSHeader.DDSCAPS_TEXTURE);
		_headers.addFlag(DDSHeader.DDSD_CAPS);
		_headers.addFlag(DDSHeader.DDSD_WIDTH);
		_headers.addFlag(DDSHeader.DDSD_HEIGHT);
		_headers.addFlag(DDSHeader.DDSD_PIXELFORMAT);
	}

	/**
	 * Défini le pixel format pour une texture compressée
	 * @param fourcc une des constante DDSFourCC.FOURCC_*
	 */
	public void setCompressedPixelFormat(int fourcc)
	{
		_headers.setPixelFormatFourCC(fourcc);
		_headers.removeFlag(DDSHeader.DDSD_PITCH);
		_headers.addFlag(DDSHeader.DDSD_LINEARSIZE);
		_headers.setPixelFormatFlags(DDSHeader.DDPF_FOURCC);
	}

	/**
	 * Défini le pixel format pour une texture non compressée
	 * @param openglFormat une des constante TextureFormat.GL_ALPHA, TextureFormat.GL_LUMINANCE, TextureFormat.GL_LUMINANCE_ALPHA, TextureFormat.GL_RGB, TextureFormat.GL_RGBA
	 * @throws DDSFormatException
	 */
	public void setUncompressedPixelFormat(int openglFormat) throws DDSFormatException
	{
		_headers.removeFlag(DDSHeader.DDSD_LINEARSIZE);
		_headers.addFlag(DDSHeader.DDSD_PITCH);

		switch (openglFormat)
		{
		case TextureFormat.GL_ALPHA:
			_headers.setPixelFormatRgbBitCount(8);
			_headers.setPixelFormatFlags(DDSHeader.DDPF_ALPHA | DDSHeader.DDPF_ALPHAPIXELS);
			_headers.setPixelFormatBitMask(0, 0, 0, 0xff);
			break;

		case TextureFormat.GL_LUMINANCE:
			_headers.setPixelFormatRgbBitCount(8);
			_headers.setPixelFormatFlags(DDSHeader.DDPF_LUMINANCE);
			_headers.setPixelFormatBitMask(0xff, 0, 0, 0);
			break;

		case TextureFormat.GL_LUMINANCE_ALPHA:
			_headers.setPixelFormatRgbBitCount(16);
			_headers.setPixelFormatFlags(DDSHeader.DDPF_LUMINANCE | DDSHeader.DDPF_ALPHAPIXELS);
			_headers.setPixelFormatBitMask(0x00ff, 0, 0, 0xff00);
			break;

		case TextureFormat.GL_RGB:
			_headers.setPixelFormatRgbBitCount(24);
			_headers.setPixelFormatFlags(DDSHeader.DDPF_RGB);
			_headers.setPixelFormatBitMask(0x0000ff, 0x00ff00, 0xff0000, 0);
			break;

		case TextureFormat.GL_RGBA:
			_headers.setPixelFormatRgbBitCount(32);
			_headers.setPixelFormatFlags(DDSHeader.DDPF_RGB | DDSHeader.DDPF_ALPHAPIXELS);
			_headers.setPixelFormatBitMask(0x000000ff, 0x0000ff00, 0x00ff0000, 0xff000000);
			break;

		default:
			throw new DDSFormatException("Invalid format " + Integer.toHexString(openglFormat));
		}
	}

	/**
	 * Défini les dimensions de la texture
	 * @param width
	 * @param height
	 */
	public void setDimensions(int width, int height)
	{
		_textureData.setDimensions(0, width, height);
		_headers.setWidth(width);
		_headers.setHeight(height);
	}

	/**
	 * Défini le nombre de niveaux mipmap. Défini automatiquement les bonnes entêtes en conséquence.
	 * @param numberOfMipmapLevels
	 * @throws DDSFormatException
	 */
	public void setNumberOfMipmaps(int numberOfMipmapLevels) throws DDSFormatException
	{
		if (numberOfMipmapLevels > 1)
		{
			// il y a des mipmaps
			_headers.addCaps(DDSHeader.DDSCAPS_COMPLEX);
			_headers.addCaps(DDSHeader.DDSCAPS_MIPMAP);
			_headers.addFlag(DDSHeader.DDSD_MIPMAPCOUNT);
			_headers.setMipmapCount(numberOfMipmapLevels);
			_textureData.setNumberOfMipmapLevelsAndFaces(numberOfMipmapLevels, _textureData.getNumberOfFaces());
		}
		else if (numberOfMipmapLevels == 0 || numberOfMipmapLevels == 1)
		{
			// pas de mipmaps
			_headers.removeCaps(DDSHeader.DDSCAPS_MIPMAP);
			_headers.removeFlag(DDSHeader.DDSD_MIPMAPCOUNT);
			_headers.setMipmapCount(numberOfMipmapLevels);
			_textureData.setNumberOfMipmapLevelsAndFaces(numberOfMipmapLevels, _textureData.getNumberOfFaces());

			// on retire le flag DDSCAPS_COMPLEX si pas de cubemap et pas d'arraysize
			if (!_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP)
					&& (null == _headers.getHeader10() || _headers.getHeader10().getArraySize() <= 1))
			{
				_headers.removeCaps(DDSHeader.DDSCAPS_COMPLEX);
			}
		}
		else
		{
			throw new DDSFormatException("numberOfMipmaps can't be negative");
		}
	}

	/**
	 * Défini si la texture est un cubemap et défini automatiquement les bonnes entêtes en conséquence.
	 * Si isCubemap est a true, 6 faces seront déclarées dans les entêtes.
	 * @param isCubemap
	 * @throws DDSFormatException
	 */
	public void setCubemap(boolean isCubemap) throws DDSFormatException
	{
		if (isCubemap)
		{
			_headers.addCaps2(DDSHeader.DDSCAPS2_CUBEMAP);
			_headers.addCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEX);
			_headers.addCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEY);
			_headers.addCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEZ);
			_headers.addCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEX);
			_headers.addCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEY);
			_headers.addCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEZ);

			_headers.addCaps(DDSHeader.DDSCAPS_COMPLEX);
			_textureData.setNumberOfMipmapLevelsAndFaces(_textureData.getNumberOfMipmapLevels(), 6);

			if (null != _headers.getHeader10())
			{
				_headers.getHeader10().addMiscFlag(DDSHeader10.DDS_RESOURCE_MISC_TEXTURECUBE);
			}
		}
		else
		{
			_headers.setCaps2(0);
			_textureData.setNumberOfMipmapLevelsAndFaces(_textureData.getNumberOfMipmapLevels(), 1);

			if (null != _headers.getHeader10())
			{
				_headers.getHeader10().removeMiscFlag(DDSHeader10.DDS_RESOURCE_MISC_TEXTURECUBE);
			}

			// on retire le flag DDSCAPS_COMPLEX si pas de cubemap et pas d'arraysize
			if (!_headers.hasCaps(DDSHeader.DDSCAPS_MIPMAP)
					&& (null == _headers.getHeader10() || _headers.getHeader10().getArraySize() <= 1))
			{
				_headers.removeCaps(DDSHeader.DDSCAPS_COMPLEX);
			}
		}
	}

	/**
	 * Écrit le fichier
	 * @param out
	 * @throws DDSFormatException 
	 * @throws IOException 
	 */
	public void write(OutputStream out) throws DDSFormatException, IOException
	{
		// on crée un flux bufferisé à partir du flux passé en entrée
		if (!(out instanceof BufferedOutputStream))
		{
			out = new BufferedOutputStream(out);
		}

		_headers.write((BufferedOutputStream) out);
		_textureData.write((BufferedOutputStream) out);
		out.flush();
		out.close();
	}

	/**
	 * Retourne les headers
	 * @return
	 */
	public DDSHeader.Writer getHeaders()
	{
		return _headers;
	}

	/**
	 * Retourne les données de texture
	 * @return
	 */
	public DDSTextureData.Writer getTextureData()
	{
		return _textureData;
	}

}
