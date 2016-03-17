package com.numericalactivity.dktxtools.dds;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.numericalactivity.dktxtools.TextureFormat;

/**
 * Classe qui permet de lire un fichier DDS
 */
public class DDSReader
{

	protected DDSHeader.Reader _headers;
	protected DDSTextureData.Reader _textureData;
	protected int _openglFormat;
	protected boolean _isCompressed;

	/**
	 * Constructeur
	 */
	public DDSReader()
	{
	}

	/**
	 * Récupère et parse les données du fichier DDS
	 * @param in flux pointant sur le fichier DDS. Le pointeur doit être placé au début du fichier.
	 * @throws IOException
	 * @throws DDSFormatException
	 */
	public void read(InputStream in) throws IOException, DDSFormatException
	{
		// on crée un flux bufferisé à partir du flux passé en entrée
		if (!(in instanceof BufferedInputStream))
		{
			in = new BufferedInputStream(in);
		}

		_headers = new DDSHeader.Reader((BufferedInputStream) in);
		_textureData = new DDSTextureData.Reader((BufferedInputStream) in, _headers);
		in.close();

		// on détermine si les données sont compressées
		_isCompressed = _headers.hasPixelFormatFlags(DDSHeader.DDPF_FOURCC);

		// on détermine le format OpenGL
		if (_isCompressed)
		{
			// format OpenGL pour les textures compressées
			int fourcc = _headers._pixelFormatfourCC;

			if (DDSFourCC.FOURCC_DX10 == fourcc)
			{
				fourcc = _headers._extendedHeaders._dxgiFormat;
			}

			switch (fourcc)
			{
			case DDSFourCC.FOURCC_ATC:
				_openglFormat = TextureFormat.GL_ATC_RGB_AMD;
				break;

			case DDSFourCC.FOURCC_ATCA:
				_openglFormat = TextureFormat.GL_ATC_RGBA_EXPLICIT_ALPHA_AMD;
				break;

			case DDSFourCC.FOURCC_ATCI:
				_openglFormat = TextureFormat.GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD;
				break;

			case DDSFourCC.FOURCC_DXT1:
				_openglFormat = TextureFormat.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
				break;

			case DDSFourCC.FOURCC_DXT3:
				_openglFormat = TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
				break;

			case DDSFourCC.FOURCC_DXT5:
				_openglFormat = TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
				break;

			case DDSFourCC.FOURCC_ETC1:
				_openglFormat = TextureFormat.GL_ETC1_RGB8;
				break;

			default:
				throw new DDSFormatException("FourCC " + Integer.toHexString(fourcc) + " is not supported");
			}
		}
		else
		{
			// TODO attention aux bitmasks
			// format OpenGL pour les textures non compressées
			boolean hasAlpha = _headers.hasPixelFormatFlags(DDSHeader.DDPF_ALPHAPIXELS);
			boolean hasRgb = _headers.hasPixelFormatFlags(DDSHeader.DDPF_RGB);
			boolean hasLuminance = _headers.hasPixelFormatFlags(DDSHeader.DDPF_LUMINANCE);

			if (hasLuminance && hasAlpha)
			{
				_openglFormat = TextureFormat.GL_LUMINANCE_ALPHA;
			}
			else if (hasLuminance)
			{
				_openglFormat = TextureFormat.GL_LUMINANCE;
			}
			else if (hasRgb && hasAlpha)
			{
				_openglFormat = TextureFormat.GL_RGBA;
			}
			else if (hasRgb)
			{
				_openglFormat = TextureFormat.GL_RGB;
			}
			else if (hasAlpha)
			{
				_openglFormat = TextureFormat.GL_ALPHA;
			}
		}
	}

	/**
	 * Retourne les headers
	 * @return
	 */
	public DDSHeader.Reader getHeaders()
	{
		return _headers;
	}

	/**
	 * Retourne les données de texture
	 * @return
	 */
	public DDSTextureData.Reader getTextureData()
	{
		return _textureData;
	}

	/**
	 * Retourne si la texture chargée est compressée
	 * @return
	 */
	public boolean isCompressed()
	{
		return _isCompressed;
	}

	/**
	 * Retourne le format OpenGL de la texture chargée
	 * @return une des constantes TextureFormat.GL_*
	 */
	public int getOpenglFormat()
	{
		return _openglFormat;
	}

}
