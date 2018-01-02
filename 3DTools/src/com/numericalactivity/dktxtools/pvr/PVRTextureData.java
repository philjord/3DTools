package com.numericalactivity.dktxtools.pvr;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.numericalactivity.dktxtools.utils.TextureUtils;

public abstract class PVRTextureData {

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

    public void setNumberOfMipmapLevels(int numberOfMipmapLevels) throws PVRFormatException {
        setNumberOfMipmapLevelsAndFaces(numberOfMipmapLevels, _numberOfFaces);
    }

    public void setNumberOfFaces(int numberOfFaces) throws PVRFormatException {
        setNumberOfMipmapLevelsAndFaces(_numberOfMipmapLevels, numberOfFaces);
    }

    public void setNumberOfMipmapLevelsAndFaces(int numberOfMipmapLevels, int numberOfFaces) throws PVRFormatException {
        numberOfMipmapLevels    = (numberOfMipmapLevels == 0) ? 1 : numberOfMipmapLevels;
        numberOfFaces           = (numberOfFaces == 0) ? 1 : numberOfFaces;

        // si les valeurs sont les mêmes on ne recrée pas les variables
        if (numberOfMipmapLevels == _numberOfMipmapLevels && numberOfFaces == _numberOfFaces) {
            return;
        }

        _numberOfMipmapLevels   = (byte) numberOfMipmapLevels;
        _numberOfFaces          = (byte) numberOfFaces;
        _textureData            = new ByteBuffer[_numberOfMipmapLevels][_numberOfFaces];
        _imageSize              = new int[_numberOfMipmapLevels];
        _width                  = new short[_numberOfMipmapLevels];
        _height                 = new short[_numberOfMipmapLevels];

        // on contrôle le nombre de niveaux mipmap
        if (_numberOfMipmapLevels < 0) {
            throw new PVRFormatException("numberOfMipmapLevels can't be negative");
        }

        // on contrôle le nombre de faces
        if (_numberOfFaces < 0 || _numberOfFaces > 6) {
            throw new PVRFormatException("numberOfFaces must be between 0 and 6");
        }
    }

    /**
     * Défini le buffer contenant l'image correspondant au niveau mipmap 'mipmapLevel'
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
     * Défini le buffer contenant l'image correspondant au niveau mipmap 'mipmapLevel' et à la face 'face'
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
     * Classe qui permet de lire les données des textures contenues dans un fichier PVR
     */
    public static class Reader extends PVRTextureData {
        /**
         * Constructeur
         */
        Reader() {
        }

        /**
         * Constructeur.
         * Lit les données de texture du fichier.
         * @param in le pointeur doit être placé au début des données
         * @param pvrHeader headers du fichier
         * @throws IOException
         */
        Reader(BufferedInputStream in, PVRHeader pvrHeader) throws IOException {
            read(in, pvrHeader);
        }

        /**
         * Constructeur.
         * Lit les données de texture du fichier.
         * @param in le pointeur doit être placé au début des données
         * @param pvrHeader headers du fichier
         * @throws IOException
         */
        void read(BufferedInputStream in, PVRHeader pvrHeader) throws IOException {
            // on initialise les tableaux
            // on ne les recrée pas s'ils ont déjà été initialisés et que le nombre de niveaux mipmap est identique
            byte numberOfMipmapLevels = (byte) pvrHeader._numberOfMipmapLevels;

            if (null == _textureData || numberOfMipmapLevels != _textureData.length) {
                _textureData    = new ByteBuffer[numberOfMipmapLevels][pvrHeader._numberOfFaces];
                _imageSize      = new int[numberOfMipmapLevels];
                _width          = new short[numberOfMipmapLevels];
                _height         = new short[numberOfMipmapLevels];
            }

            // on récupère les données des textures
            _numberOfMipmapLevels   = numberOfMipmapLevels;
            _numberOfFaces          = (byte) pvrHeader._numberOfFaces;
            _width[0]               = (short) pvrHeader._width;
            _height[0]              = (short) pvrHeader._height;
            int pixelFormat1        = pvrHeader._pixelFormat1;
            byte[] faceData;

            for (byte mipmapLevel = 0; mipmapLevel < _numberOfMipmapLevels; mipmapLevel++) {
                // on défini les dimensions et la taille des données pour ce niveau mipmap
                if (0 == _imageSize[mipmapLevel]) {
                    _width[mipmapLevel]     = TextureUtils.getDimensionForMipmapLevel(mipmapLevel, _width[0]);
                    _height[mipmapLevel]    = TextureUtils.getDimensionForMipmapLevel(mipmapLevel, _height[0]);
                    _imageSize[mipmapLevel] = PVRUtil.getCompressedSize(_width[mipmapLevel], _height[mipmapLevel], pixelFormat1);
                }

                for (byte face = 0; face < _numberOfFaces; face++) {
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

}
