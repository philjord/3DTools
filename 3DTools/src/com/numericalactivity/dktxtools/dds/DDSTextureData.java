package com.numericalactivity.dktxtools.dds;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.numericalactivity.dktxtools.utils.TextureUtils;

/**
 * Classe de gestion des données des textures contenues dans un fichier DDS
 */
public abstract class DDSTextureData {

    ByteBuffer[][] _textureData; // données des textures classées par niveau mipmap puis par face
    int[] _imageSize; // taille des images classées par niveau mipmap
    short[] _width; // largeur des images classées par niveau mipmap
    short[] _height; // hauteur des images classées par niveau mipmap
    byte _numberOfMipmapLevels; // nombre de niveaux mipmap
    byte _numberOfFaces; // nombre de face par niveau mipmap

    /**
     * Retourne le nombre de niveaux mipmap
     * @return
     */
    public int getNumberOfMipmapLevels() {
        return _numberOfMipmapLevels;
    }

    /**
     * Retourne le nombre de face par niveau mipmap
     * @return
     */
    public int getNumberOfFaces() {
        return _numberOfFaces;
    }

    /**
     * Retourne la taille des données (en bytes) de l'image d'un niveau mipmap
     * @param mipmapLevel
     * @return
     */
    public int getImageSize(int mipmapLevel) {
        if (mipmapLevel > _numberOfMipmapLevels - 1) {
            throw new ArrayIndexOutOfBoundsException("No mipmap level " + String.valueOf(mipmapLevel));
        }

        return _imageSize[mipmapLevel];
    }

    /**
     * Retourne la taille (en bytes) de chaque niveau mipmap
     * @return
     */
    public int[] getAllImageSizes() {
        return _imageSize;
    }

    /**
     * Retourne la largeur d'un niveau mipmap
     * @param mipmapLevel
     * @return
     */
    public int getWidth(int mipmapLevel) {
        if (mipmapLevel > _numberOfMipmapLevels - 1) {
            throw new ArrayIndexOutOfBoundsException("No mipmap level " + String.valueOf(mipmapLevel));
        }
        
        return _width[mipmapLevel];
    }

    /**
     * Retourne la hauteur d'un niveau mipmap
     * @param mipmapLevel
     * @return
     */
    public int getHeight(int mipmapLevel) {
        if (mipmapLevel > _numberOfMipmapLevels - 1) {
            throw new ArrayIndexOutOfBoundsException("No mipmap level " + String.valueOf(mipmapLevel));
        }
        
        return _height[mipmapLevel];
    }

    /**
     * Retourne la largeur de chaque niveau mipmap
     * @return
     */
    public short[] getAllWidth() {
        return _width;
    }

    /**
     * Retourne la hauteur de chaque niveau mipmap
     * @return
     */
    public short[] getAllHeight() {
        return _height;
    }

    /**
     * Retourne le buffer contenant l'image correspondant au niveau mipmap 'mipmapLevel'
     * @param mipmapLevel
     * @return
     */
    public ByteBuffer get(int mipmapLevel) {
        if (mipmapLevel > _numberOfMipmapLevels - 1) {
            throw new ArrayIndexOutOfBoundsException("No mipmap level " + String.valueOf(mipmapLevel));
        }

        return _textureData[mipmapLevel][0];
    }

    /**
     * Retourne le buffer contenant l'image correspondant au niveau mipmap 'mipmapLevel' et à la face 'face'
     * @param mipmapLevel
     * @param face
     * @return
     */
    public ByteBuffer get(int mipmapLevel, int face) {
        if (mipmapLevel > _numberOfMipmapLevels - 1) {
            throw new ArrayIndexOutOfBoundsException("No mipmap level " + String.valueOf(mipmapLevel));
        } else if (face > _numberOfFaces - 1) {
            throw new ArrayIndexOutOfBoundsException("No face " + String.valueOf(mipmapLevel));
        }
        
        return _textureData[mipmapLevel][face];
    }

    /**
     * Retourne les buffers contenant les images de la première face de chaque niveau mipmap
     * @return
     */
    public ByteBuffer[] getAllOneFace() {
        ByteBuffer[] buffers = new ByteBuffer[_numberOfMipmapLevels];

        for (byte i = 0; i < _numberOfMipmapLevels; i++) {
            buffers[i] = _textureData[i][0];
        }

        return buffers;
    }

    /**
     * Retourne tous les buffers contenant les images
     * @return
     */
    public ByteBuffer[][] getAll() {
        return _textureData;
    }

    public void setNumberOfMipmapLevels(int numberOfMipmapLevels) throws DDSFormatException {
        setNumberOfMipmapLevelsAndFaces(numberOfMipmapLevels, _numberOfFaces);
    }

    public void setNumberOfFaces(int numberOfFaces) throws DDSFormatException {
        setNumberOfMipmapLevelsAndFaces(_numberOfMipmapLevels, numberOfFaces);
    }

    public void setNumberOfMipmapLevelsAndFaces(int numberOfMipmapLevels, int numberOfFaces) throws DDSFormatException {
        numberOfMipmapLevels    = (numberOfMipmapLevels == 0) ? 1 : numberOfMipmapLevels;
        numberOfFaces           = (numberOfFaces == 0) ? 1 : numberOfFaces;

        // si les valeurs sont les mêmes on ne recrée pas les variables
        if (numberOfMipmapLevels == _numberOfMipmapLevels && numberOfFaces == _numberOfFaces) {
            return;
        }

        // on initialise certaines variables
        _numberOfMipmapLevels   = (byte) numberOfMipmapLevels;
        _numberOfFaces          = (byte) numberOfFaces;
        _textureData            = new ByteBuffer[_numberOfMipmapLevels][_numberOfFaces];
        _imageSize              = new int[_numberOfMipmapLevels];
        _width                  = new short[_numberOfMipmapLevels];
        _height                 = new short[_numberOfMipmapLevels];

        // on contrôle le nombre de niveaux mipmap
        if (_numberOfMipmapLevels < 0) {
            throw new DDSFormatException("numberOfMipmapLevels can't be negative");
        }

        // on contrôle le nombre de faces
        if (_numberOfFaces < 0 || _numberOfFaces > 6) {
            throw new DDSFormatException("numberOfFaces must be between 0 and 6");
        }
    }

    /**
     * Défini le buffer contenant l'image correspondant au niveau mipmap 'mipmapLevel'.
     * Toutes les images d'un même niveau mipmap doivent être de même taille (en byte) et de même dimensions (en pixels).
     * Les dimensions de l'image doivent être conforme aux dimensions fournies avec "setDimensions()".
     * @param mipmapLevel
     * @param buffer
     */
    public void set(int mipmapLevel, ByteBuffer buffer) {
        if (mipmapLevel > _numberOfMipmapLevels - 1) {
            throw new ArrayIndexOutOfBoundsException("No mipmap level " + String.valueOf(mipmapLevel));
        }

        _textureData[mipmapLevel][0]    = buffer;
        _imageSize[mipmapLevel]         = buffer.capacity();
    }

    /**
     * Défini le buffer contenant l'image correspondant au niveau mipmap 'mipmapLevel' et à la face 'face'.
     * Toutes les images d'un même niveau mipmap doivent être de même taille (en byte) et de même dimensions (en pixels).
     * Les dimensions de l'image doivent être conforme aux dimensions fournies avec "setDimensions()".
     * @param mipmapLevel
     * @param face
     * @param buffer
     */
    public void set(int mipmapLevel, int face, ByteBuffer buffer) {
        if (mipmapLevel > _numberOfMipmapLevels - 1) {
            throw new ArrayIndexOutOfBoundsException("No mipmap level " + String.valueOf(mipmapLevel));
        } else if (face > _numberOfFaces - 1) {
            throw new ArrayIndexOutOfBoundsException("No face " + String.valueOf(mipmapLevel));
        }

        _textureData[mipmapLevel][face] = buffer;
        _imageSize[mipmapLevel]         = buffer.capacity();
    }

    /**
     * Défini les dimensions de l'image d'un niveau mipmap
     * @param mipmapLevel
     * @param width
     * @param height
     */
    public void setDimensions(int mipmapLevel, int width, int height) {
        _width[mipmapLevel]     = (short) width;
        _height[mipmapLevel]    = (short) height;
    }

    /**
     * Remet l'objet à son état d'origine
     */
    public void reset() {
        byte face;

        for (byte mipmapLevel = 0; mipmapLevel < _numberOfMipmapLevels; mipmapLevel++) {
            _imageSize[mipmapLevel] = 0;
            _width[mipmapLevel]     = 0;
            _height[mipmapLevel]    = 0;

            for (face = 0; face < _numberOfFaces; face++) {
                _textureData[mipmapLevel][face] = null;
            }
        }
    }

    /**
     * Classe qui permet de lire les données des textures contenues dans un fichier DDS
     */
    public static class Reader extends DDSTextureData {
        /**
         * Lit les données de texture du fichier
         * @param in le pointeur doit être placé au début des données
         * @param ddsHeader headers du fichier
         * @throws IOException
         * @throws DDSFormatException 
         */
        protected Reader(BufferedInputStream in, DDSHeader ddsHeader) throws DDSFormatException, IOException {
            read(in, ddsHeader);
        }

        /**
         * Lit les données de texture du fichier
         * @param in le pointeur doit être placé au début des données
         * @param ddsHeader headers du fichier
         * @throws IOException
         * @throws DDSFormatException 
         */
        protected void read(BufferedInputStream in, DDSHeader ddsHeader) throws DDSFormatException, IOException {
            // on calcule le nombre de faces si la texture est un cubemap
            _numberOfFaces = 1;

            if (ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP)) {
                _numberOfFaces = 0;

                if (ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEX)) {
                    _numberOfFaces++;
                }
                if (ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEY)) {
                    _numberOfFaces++;
                }
                if (ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEZ)) {
                    _numberOfFaces++;
                }
                if (ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEX)) {
                    _numberOfFaces++;
                }
                if (ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEY)) {
                    _numberOfFaces++;
                }
                if (ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEZ)) {
                    _numberOfFaces++;
                }

                if (0 == _numberOfFaces) {
                    throw new DDSFormatException("Header 'DDSCAPS2' not properly defined. It indicates a cube map, but no faces.");
                }
            }

            // on initialise les tableaux
            // on ne les recrée pas s'ils ont déjà été initialisés et que le nombre de niveaux mipmap est identique
            byte numberOfMipmapLevels = (byte) ddsHeader._mipmapCount;

            if (null == _textureData || numberOfMipmapLevels != _textureData.length) {
                _textureData    = new ByteBuffer[numberOfMipmapLevels][_numberOfFaces];
                _imageSize      = new int[numberOfMipmapLevels];
                _width          = new short[numberOfMipmapLevels];
                _height         = new short[numberOfMipmapLevels];
            }

            // on initialise les variables
            _numberOfMipmapLevels   = (byte) ddsHeader._mipmapCount;
            _textureData            = new ByteBuffer[_numberOfMipmapLevels][_numberOfFaces];
            _imageSize              = new int[_numberOfMipmapLevels];
            _width                  = new short[_numberOfMipmapLevels];
            _height                 = new short[_numberOfMipmapLevels];

            _width[0]               = (short) ddsHeader._width;
            _height[0]              = (short) ddsHeader._height;

            // on lance la récupération des données
            if (ddsHeader.hasPixelFormatFlags(DDSHeader.DDPF_FOURCC)) {
                // texture compressée
                readCompressed(in, ddsHeader);
            } else {
                // texture non compressée
                readUncompressed(in, ddsHeader);
            }
        }

        /**
         * Lit les données de texture du fichier. A utiliser pour des textures compressées.
         * @param in le pointeur doit être placé au début des données
         * @param ddsHeader headers du fichier
         * @throws IOException
         */
        protected void readCompressed(BufferedInputStream in, DDSHeader ddsHeader) throws IOException {
            byte blockSize = (byte) ((DDSFourCC.FOURCC_DX10 == ddsHeader._pixelFormatfourCC) ? DDSUtil.getCompressedBlockSize(ddsHeader._extendedHeaders._dxgiFormat)  : DDSUtil.getCompressedBlockSize(ddsHeader._pixelFormatfourCC));
            byte[] faceData;

            for (byte face = 0; face < _numberOfFaces; face++) {
                for (byte mipmapLevel = 0; mipmapLevel < _numberOfMipmapLevels; mipmapLevel++) {
                    // on défini les dimensions et la taille des données pour ce niveau mipmap
                    if (0 == _imageSize[mipmapLevel]) {
                        _width[mipmapLevel]     = TextureUtils.getDimensionForMipmapLevel(mipmapLevel, _width[0]);
                        _height[mipmapLevel]    = TextureUtils.getDimensionForMipmapLevel(mipmapLevel, _height[0]);
                        _imageSize[mipmapLevel] = Math.max(1, _width[mipmapLevel] / 4) * Math.max(1, _height[mipmapLevel] / 4) * blockSize;
                    }

                    // on récupère les données
                    faceData                        = new byte[_imageSize[mipmapLevel]];
                    _textureData[mipmapLevel][face] = ByteBuffer.allocateDirect(_imageSize[mipmapLevel]);
                    in.read(faceData);
                    _textureData[mipmapLevel][face].put(faceData);
                    _textureData[mipmapLevel][face].position(0);
                    _textureData[mipmapLevel][face].order(ByteOrder.LITTLE_ENDIAN);
                }
            }
        }

        /**
         * Lit les données de texture du fichier. A utiliser pour des textures non compressées.
         * @param in le pointeur doit être placé au début des données
         * @param ddsHeader headers du fichier
         * @throws IOException
         */
        protected void readUncompressed(BufferedInputStream in, DDSHeader ddsHeader) throws IOException {
            byte bitsPerPixel = (byte) ddsHeader._pixelFormatRgbBitCount;
            byte[] faceData;

            for (byte face = 0; face < _numberOfFaces; face++) {
                for (byte mipmapLevel = 0; mipmapLevel < _numberOfMipmapLevels; mipmapLevel++) {
                    // on défini les dimensions et la taille des données pour ce niveau mipmap
                    if (0 == _imageSize[mipmapLevel]) {
                        _width[mipmapLevel]     = (short) ((mipmapLevel > 0) ? Math.max(1, _width[0] / Math.pow(2, mipmapLevel)) : _width[0]);
                        _height[mipmapLevel]    = (short) ((mipmapLevel > 0) ? Math.max(1, _height[0] / Math.pow(2, mipmapLevel)) : _height[0]);
                        _imageSize[mipmapLevel] = ((_width[mipmapLevel] * bitsPerPixel + 7) / 8) * _height[mipmapLevel];
                    }

                    // on récupère les données
                    faceData                        = new byte[_imageSize[mipmapLevel]];
                    _textureData[mipmapLevel][face] = ByteBuffer.allocateDirect(_imageSize[mipmapLevel]);
                    in.read(faceData);
                    _textureData[mipmapLevel][face].put(faceData);
                    _textureData[mipmapLevel][face].position(0);
                    _textureData[mipmapLevel][face].order(ByteOrder.LITTLE_ENDIAN);
                }
            }
        }
    }

    /**
     * Classe qui permet d'écrire les données des textures selon le format DDS dans un flux
     */
    public static class Writer extends DDSTextureData {
        protected DDSHeader _headers;

        /**
         * Initialise les données des textures
         * @param numberOfMipmapLevels  nombre de niveaux mipmap
         * @param numberOfFaces         nombre de faces par niveau mipmap
         * @throws DDSFormatException 
         */
        protected Writer(DDSHeader headers, int numberOfMipmapLevels, int numberOfFaces) throws DDSFormatException {
            _headers = headers;
            setNumberOfMipmapLevelsAndFaces(numberOfMipmapLevels, numberOfFaces);
        }

        /**
         * Contrôle l'integrité des données qui vont être insérées
         * @return
         * @throws DDSFormatException 
         */
        protected boolean check() throws DDSFormatException {
            for (byte mipmapLevel = 0; mipmapLevel < _numberOfMipmapLevels; mipmapLevel++) {
                // on vérifie que des données existent
                if (null == _textureData[mipmapLevel] || 0 == _imageSize[mipmapLevel]) {
                    throw new DDSFormatException("No data defined for mipmap level " + String.valueOf(mipmapLevel));
                }

                //
                for (byte face = 0; face < _numberOfFaces; face++) {
                    // on vérifie que des données existent
                    if (null == _textureData[mipmapLevel][face]) {
                        throw new DDSFormatException("No data defined for face " + String.valueOf(face) + " of mipmap level " + String.valueOf(mipmapLevel));
                    }
                }
            }

            // on vérifie que le nombre de faces et de niveaux mipmap correspond à ce qui est declaré dans les entêtes
            byte declaredFaces = 1;

            if (_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP)) {
                declaredFaces = 0;

                if (_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEX)) {
                    declaredFaces++;
                }
                if (_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEY)) {
                    declaredFaces++;
                }
                if (_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEZ)) {
                    declaredFaces++;
                }
                if (_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEX)) {
                    declaredFaces++;
                }
                if (_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEY)) {
                    declaredFaces++;
                }
                if (_headers.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEZ)) {
                    declaredFaces++;
                }
            }

            if (declaredFaces != _textureData[0].length) {
                throw new DDSFormatException(String.valueOf(declaredFaces) + " faces defined in headers, but " + String.valueOf(_textureData[0].length) + " have texture data");
            }

            if (_textureData.length != _headers._mipmapCount) {
                throw new DDSFormatException(String.valueOf(_headers._mipmapCount) + " mipmap levels defined in headers, but " + String.valueOf(_textureData.length) + " have texture data");
            }

            return true;
        }

        /**
         * Écrit les données des textures dans un flux
         * @param out
         * @throws IOException
         * @throws DDSFormatException 
         */
        public void write(BufferedOutputStream out) throws IOException, DDSFormatException {
            check();

            // écriture
            byte[] data;

            for (byte face = 0; face < _numberOfFaces; face++) {
                for (byte mipmapLevel = 0; mipmapLevel < _numberOfMipmapLevels; mipmapLevel++) {
                    data = new byte[_textureData[mipmapLevel][face].capacity()];
                    _textureData[mipmapLevel][face].position(0);
                    _textureData[mipmapLevel][face].get(data);
                    out.write(data);
                }
            }
        }
    }

}
