package com.numericalactivity.dktxtools.ktx;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Classe qui permet de lire un fichier KTX
 */
public class KTXReader
{
	protected KTXHeader.Reader _headers;
	protected KTXMetadata.Reader _metas;
	protected KTXTextureData.Reader _textureData;

	/**
	 * Constructeur
	 */
	public KTXReader()
	{
	}

	public KTXReader(InputStream in, boolean loadMetadatas) throws IOException, KTXFormatException
	{
		read(in, loadMetadatas);
	}

	public KTXReader(ByteBuffer in, boolean loadMetadatas) throws IOException, KTXFormatException
	{
		read(in, loadMetadatas);
	}

	/**
	 * Récupère et parse les données du fichier KTX
	 * @param in flux pointant sur le fichier KTX. Le pointeur doit être placé au début du fichier
	 * @param loadMetadatas indique si les métadonnées doivent être chargées
	 * @throws IOException
	 * @throws KTXFormatException
	 */
	public void read(InputStream in, boolean loadMetadatas) throws IOException, KTXFormatException
	{
		// on ne crée les objets qu'une seule fois pour pouvoir profiter du pool
		_headers = new KTXHeader.Reader();
		_metas = new KTXMetadata.Reader();
		_textureData = new KTXTextureData.Reader();

		// chargement des entêtes
		_headers.read(in);

		if (loadMetadatas)
		{
			// on charge les métadonnées
			_metas.read((BufferedInputStream) in, _headers);
		}
		else
		{
			// si on ne charge pas les métadonnées, il faut faire avancer le pointeur de l'inputstream
			in.skip(_headers.getBytesOfKeyValueData());
		}

		// chargement des données de texture
		_textureData.read(in, _headers);
		in.close();
	}

	public void read(ByteBuffer in, boolean loadMetadatas) throws IOException, KTXFormatException
	{
		// on ne crée les objets qu'une seule fois pour pouvoir profiter du pool
		_headers = new KTXHeader.Reader();
		_metas = new KTXMetadata.Reader();
		_textureData = new KTXTextureData.Reader();

		// chargement des entêtes
		_headers.read(in);

		if (loadMetadatas)
		{
			// on charge les métadonnées
			_metas.read(in, _headers);
		}
		else
		{
			// si on ne charge pas les métadonnées, il faut faire avancer le pointeur de l'inputstream
			in.position(in.position() + _headers.getBytesOfKeyValueData());
		}

		// chargement des données de texture
		_textureData.read(in, _headers);

	}

	/**
	 * Retourne les headers
	 * @return
	 */
	public KTXHeader getHeaders()
	{
		return _headers;
	}

	/**
	 * Retourne les métadonnées
	 * @return
	 */
	public KTXMetadata getMetadata()
	{
		return _metas;
	}

	/**
	 * Retourne les données de texture
	 * @return
	 */
	public KTXTextureData getTextureData()
	{
		return _textureData;
	}

	/**
	 * Retourne true si la texture est compressée
	 * @return
	 */
	public boolean isCompressed()
	{
		return 0 == _headers._glFormat;
	}

}
