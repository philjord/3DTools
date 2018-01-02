package com.numericalactivity.dktxtools.pvr;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.numericalactivity.dktxtools.TextureFormat;

// TODO tests u
/**
 * Classe qui permet de lire un fichier PVR.
 * Seuls les formats de texture PVRTC sont supportés.
 */
public class PVRReader
{

	protected PVRHeader.Reader _headers;
	protected PVRTextureData.Reader _textureData;
	protected int _openglFormat;
	protected boolean _isCompressed;

	/**
	 * Constructeur
	 */
	public PVRReader()
	{
	}

	/**
	 * Récupère et parse les données du fichier PVR
	 * @param in flux pointant sur le fichier PVR. Le pointeur doit être placé au début du fichier.
	 * @throws IOException
	 * @throws PVRFormatException
	 */
	public void read(InputStream in) throws IOException, PVRFormatException
	{
		// on crée un flux bufferisé à partir du flux passé en entrée
		if (!(in instanceof BufferedInputStream))
		{
			in = new BufferedInputStream(in);
		}

		// on lit les headers
		_headers = new PVRHeader.Reader((BufferedInputStream) in);

		// on lit les métadonnées
		byte[] metadata = new byte[_headers.getMetadataSize()];
		in.read(metadata);

		// on lit les données de textures
		_textureData = new PVRTextureData.Reader((BufferedInputStream) in, _headers);
		in.close();

		// on défini si les données sont compressées
		_isCompressed = true;

		// on détermine le format OpenGL
		if (_isCompressed)
		{
			// format OpenGL pour les textures compressées
			switch (_headers._pixelFormat1)
			{
			case PVRPixelFormat.PVRTC2BPP_RGB:
				_openglFormat = TextureFormat.GL_COMPRESSED_RGB_PVRTC_2BPPV1_IMG;
				break;

			case PVRPixelFormat.PVRTC2BPP_RGBA:
				_openglFormat = TextureFormat.GL_COMPRESSED_RGBA_PVRTC_2BPPV1_IMG;
				break;

			case PVRPixelFormat.PVRTC4BPP_RGB:
				_openglFormat = TextureFormat.GL_COMPRESSED_RGB_PVRTC_4BPPV1_IMG;
				break;

			case PVRPixelFormat.PVRTC4BPP_RGBA:
				_openglFormat = TextureFormat.GL_COMPRESSED_RGBA_PVRTC_4BPPV1_IMG;
				break;

			default:
				throw new PVRFormatException("Pixel format " + String.valueOf(_headers._pixelFormat1) + " is not supported");
			}
		}
	}

	/**
	 * Retourne les headers
	 * @return
	 */
	public PVRHeader.Reader getHeaders()
	{
		return _headers;
	}

	/**
	 * Retourne les données de texture
	 * @return
	 */
	public PVRTextureData.Reader getTextureData()
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
