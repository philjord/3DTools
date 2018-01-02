package com.numericalactivity.dktxtools.pvr;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.numericalactivity.dktxtools.utils.BufferUtils;
import com.numericalactivity.dktxtools.utils.FlagsUtils;

/**
 * Classe de gestion des entêtes d'un fichier PVR
 */
public abstract class PVRHeader {
    public static final int HEADER_LENGTH       = 52; // taille de l'entête
    public static final int ENDIANNESS_OK       = 0x03525650; // endianness qui correspond à celui du programme qui lit le fichier
    public static final int ENDIANNESS_OPPOSITE = 0x50565203; // endianness opposé

    boolean _byteOrderNative;
    ByteOrder _byteOrder;
    int _flags;
    int _pixelFormat1;
    int _pixelFormat2;
    int _colourSpace;
    int _channelType;
    int _height;
    int _width;
    int _depth;
    int _numberOfSurfaces;
    int _numberOfFaces          = 1;
    int _numberOfMipmapLevels   = 1;
    int _metadataSize;

    /*
     * Getters
     */
    public ByteOrder getByteOrder() {
        return _byteOrder;
    }

    public boolean isByteOrderNative() {
        return _byteOrderNative;
    }

    public int getFlags() {
        return _flags;
    }

    public int getPixelFormat1() {
        return _pixelFormat1;
    }

    public int getPixelFormat2() {
        return _pixelFormat2;
    }

    public int getColourSpace() {
        return _colourSpace;
    }

    public int getChannelType() {
        return _channelType;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public int getDepth() {
        return _depth;
    }

    public int getNumberOfSurfaces() {
        return _numberOfSurfaces;
    }

    public int getNumberOfMipmapLevels() {
        return _numberOfMipmapLevels;
    }

    public int getNumberOfFaces() {
        return _numberOfFaces;
    }

    public int getMetadataSize() {
        return _metadataSize;
    }

    public boolean hasFlags(int f) {
        return (_flags & f) == f;
    }

    public boolean isMipmapped() {
        return _numberOfMipmapLevels > 1;
    }

    public boolean isCubemap() {
        return _numberOfFaces > 1;
    }

    /*
     * Setters
     */
    public void setByteOrder(ByteOrder order) {
        _byteOrder          = order;
        _byteOrderNative    = (order == ByteOrder.nativeOrder());
    }

    public void setPixelFormat1(int pixelFormat1) {
        _pixelFormat1 = pixelFormat1;
    }

    public void setPixelFormat2(int pixelFormat2) {
        _pixelFormat2 = pixelFormat2;
    }

    public void setColourSpace(int colourSpace) {
        _colourSpace = colourSpace;
    }

    public void setChannelType(int channelType) {
        _channelType = channelType;
    }

    public void setWidth(int width) {
        _width = width;
    }

    public void setHeight(int height) {
        _height = height;
    }

    public void setDepth(int depth) {
        _depth = depth;
    }

    public void setNumberOfSurfaces(int numberOfSurfaces) {
        _numberOfSurfaces = numberOfSurfaces;
    }

    public void setNumberOfMipmapLevels(int numberOfMipmapLevels) {
        _numberOfMipmapLevels = numberOfMipmapLevels;
    }

    public void setNumberOfFaces(int numberOfFaces) {
        _numberOfFaces = numberOfFaces;
    }

    public void setMetadataSize(int metadataSize) {
        _metadataSize = metadataSize;
    }

    public void setFlags(int flags) {
        _flags = flags;
    }

    public void addFlag(int flag) {
        _flags |= flag;
    }

    public void removeFlag(int flag) {
        _flags &= ~flag;
    }

    /**
     * Remet l'objet à son état d'origine
     */
    public void reset() {
        _byteOrder              = null;
        _byteOrderNative        = false;
        _flags                  = 0;
        _pixelFormat1           = 0;
        _pixelFormat2           = 0;
        _colourSpace            = 0;
        _channelType            = 0;
        _height                 = 0;
        _width                  = 0;
        _depth                  = 0;
        _numberOfSurfaces       = 0;
        _numberOfFaces          = 1;
        _numberOfMipmapLevels   = 1;
        _metadataSize           = 0;
    }

    @Override
    public String toString() {
        return String.format(
            "%s\n    flags=[%s]\n    pixelFormat1=%d\n    pixelFormat2=%d\n    colourSPace=%d\n    channelType=%d\n    width=%d\n    height=%d\n    depth=%d\n    numberOfSurfaces=%d\n    numberOfFaces=%d\n    numberOfMipmapLevels=%d\n    metadataSize=%d",
            getClass().getCanonicalName(), FlagsUtils.toHexString(FlagsUtils.getFlags(_flags)), _pixelFormat1, _pixelFormat2, _colourSpace, _channelType, _height, _width, _depth, _numberOfSurfaces, _numberOfFaces,
            _numberOfMipmapLevels, _metadataSize
        );
    }

    /**
     * Une classe qui permet de lire les entêtes d'un fichier PVR
     */
    public static class Reader extends PVRHeader {
        /**
         * Constructeur
         */
        Reader() {
        }

        /**
         * Constructeur.
         * Lit les entêtes du fichier.
         * @param in le pointeur doit être placé au début du fichier
         * @throws IOException
         * @throws PVRFormatException
         */
        Reader(BufferedInputStream in) throws IOException, PVRFormatException {
            read(in);
        }

        /**
         * Constructeur.
         * Lit les entêtes du fichier.
         * @param buffer buffer contenant les données des entêtes. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
         * @throws PVRFormatException
         */
        Reader(ByteBuffer buffer) throws PVRFormatException {
            read(buffer);
        }

        /**
         * Lit les entêtes du fichier
         * @param in le pointeur doit être placé au début du fichier
         * @throws IOException 
         * @throws PVRFormatException 
         */
        void read(BufferedInputStream in) throws IOException, PVRFormatException {
            ByteBuffer buffer   = BufferUtils.getEmptyByteBuffer(HEADER_LENGTH);
            byte[] data         = new byte[HEADER_LENGTH];
            in.read(data, 0, HEADER_LENGTH);
            buffer.put(data);
            buffer.position(0);
            read(buffer);
        }

        /**
         * Lit les entêtes du fichier
         * @param buffer buffer contenant les données des entêtes. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
         * @throws PVRFormatException 
         */
        void read(ByteBuffer buffer) throws PVRFormatException {
            // on garde en mémoire l'ordre actuel du ByteBuffer
            ByteOrder oldOrder = buffer.order();

            // on défini l'ordre du buffer à l'ordre natif
            buffer.order(ByteOrder.nativeOrder());

            // on vérifie l'endianess et modifie le sens du buffer si nécessaire
            int endianness  = buffer.getInt();
            _byteOrder      = buffer.order();

            if (endianness == ENDIANNESS_OK) {
                // endianness natif
                _byteOrderNative    = true;
            } else if (endianness == ENDIANNESS_OPPOSITE) {
                // endianness inversé
                _byteOrderNative    = false;
                _byteOrder          = (ByteOrder.BIG_ENDIAN == _byteOrder) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
                buffer.order(_byteOrder);
            } else {
                throw new PVRFormatException(String.format("Endianness field has an unexpected value: %08x", endianness));
            }

            // on récupère le reste des entêtes
            _flags                  = buffer.getInt();
            _pixelFormat1           = buffer.getInt();
            _pixelFormat2           = buffer.getInt();
            _colourSpace            = buffer.getInt();
            _channelType            = buffer.getInt();
            _height                 = buffer.getInt();
            _width                  = buffer.getInt();
            _depth                  = buffer.getInt();
            _numberOfSurfaces       = buffer.getInt();
            _numberOfFaces          = buffer.getInt();
            _numberOfMipmapLevels   = buffer.getInt();
            _metadataSize           = buffer.getInt();

            // on contrôle que pixelFormat est bien un format pvrtc
            if (PVRPixelFormat.PVRTC2BPP_RGB != _pixelFormat1
                    && PVRPixelFormat.PVRTC2BPP_RGBA != _pixelFormat1
                    && PVRPixelFormat.PVRTC4BPP_RGB != _pixelFormat1
                    && PVRPixelFormat.PVRTC4BPP_RGBA != _pixelFormat1) {
                throw new PVRFormatException("Pixel format " + String.valueOf(_pixelFormat1) + " is not supported");
            }

            // on remet le buffer à son ordre d'origine
            buffer.order(oldOrder);
        }
    }
}
