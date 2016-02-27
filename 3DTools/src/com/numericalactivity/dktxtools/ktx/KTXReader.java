package com.numericalactivity.dktxtools.ktx;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.numericalactivity.dktxtools.pool.Pool;
import com.numericalactivity.dktxtools.pool.PoolFactoryInterface;
import com.numericalactivity.dktxtools.pool.PoolInterface;

/**
 * Classe qui permet de lire un fichier KTX
 */
public class KTXReader implements PoolInterface {
    protected static final Pool<KTXReader> _pool    = new Pool<KTXReader>(5, new KTXReader.Factory());
    boolean _recyclable                             = false;

    protected KTXHeader.Reader _headers;
    protected KTXMetadata.Reader _metas;
    protected KTXTextureData.Reader _textureData;

    /**
     * Récupère et parse les données du fichier KTX
     * @param in flux pointant sur le fichier KTX. Le pointeur doit être placé au début du fichier
     * @throws IOException
     * @throws KTXFormatException
     */
    public static KTXReader getNew(InputStream in) throws IOException, KTXFormatException {
        KTXReader reader = _pool.get();
        reader.read(in, true);
        return reader;
    }
    
    public static KTXReader getNew(ByteBuffer in) throws IOException, KTXFormatException {
        KTXReader reader = _pool.get();
        reader.read(in, true);
        return reader;
    }

    /**
     * Récupère et parse les données du fichier KTX
     * @param in flux pointant sur le fichier KTX. Le pointeur doit être placé au début du fichier
     * @param loadMetadatas indique si les métadonnées doivent être chargées
     * @throws IOException
     * @throws KTXFormatException
     */
    public static KTXReader getNew(InputStream in, boolean loadMetadatas) throws IOException, KTXFormatException {
        KTXReader reader = _pool.get();
        reader.read(in, loadMetadatas);
        return reader;
    }
    
    public static KTXReader getNew(ByteBuffer in, boolean loadMetadatas) throws IOException, KTXFormatException {
        KTXReader reader = _pool.get();
        reader.read(in, loadMetadatas);
        return reader;
    }

    /**
     * Constructeur
     */
    KTXReader() {
    }

    /**
     * Récupère et parse les données du fichier KTX
     * @param in flux pointant sur le fichier KTX. Le pointeur doit être placé au début du fichier
     * @param loadMetadatas indique si les métadonnées doivent être chargées
     * @throws IOException
     * @throws KTXFormatException
     */
    protected void read(InputStream in, boolean loadMetadatas) throws IOException, KTXFormatException {
        // on ne crée les objets qu'une seule fois pour pouvoir profiter du pool
        if (null == _headers) {
            _headers        = new KTXHeader.Reader();
            _metas          = new KTXMetadata.Reader();
            _textureData    = new KTXTextureData.Reader();
        }

        // chargement des entêtes
        _headers.read( in);

        if (loadMetadatas) {
            // on charge les métadonnées
            _metas.read((BufferedInputStream) in, _headers);
        } else {
            // si on ne charge pas les métadonnées, il faut faire avancer le pointeur de l'inputstream
            in.skip(_headers.getBytesOfKeyValueData());
        }

        // chargement des données de texture
        _textureData.read(in, _headers);
        in.close();
    }

    protected void read(ByteBuffer in, boolean loadMetadatas) throws IOException, KTXFormatException {
        // on ne crée les objets qu'une seule fois pour pouvoir profiter du pool
        if (null == _headers) {
            _headers        = new KTXHeader.Reader();
            _metas          = new KTXMetadata.Reader();
            _textureData    = new KTXTextureData.Reader();
        }

        // chargement des entêtes
        _headers.read(in);

        if (loadMetadatas) {
            // on charge les métadonnées
            _metas.read(in, _headers);
        } else {
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
    public KTXHeader getHeaders() {
        return _headers;
    }

    /**
     * Retourne les métadonnées
     * @return
     */
    public KTXMetadata getMetadata() {
        return _metas;
    }

    /**
     * Retourne les données de texture
     * @return
     */
    public KTXTextureData getTextureData() {
        return _textureData;
    }

    /**
     * Retourne true si la texture est compressée
     * @return
     */
    public boolean isCompressed() {
        return 0 == _headers._glFormat;
    }

    @Override
    public void reset() {
        _recyclable = false;
        _headers.reset();
        _textureData.reset();
        _metas.reset();
    }

    @Override
    public void recycle() {
        _pool.add(this);
        _recyclable = true;
    }

    /**
     * Classe qui permet de créer une nouvelle instance de KTXReader
     */
    public static class Factory implements PoolFactoryInterface<KTXReader> {
        @Override
        public KTXReader factory() {
            return new KTXReader();
        }
    }
}
