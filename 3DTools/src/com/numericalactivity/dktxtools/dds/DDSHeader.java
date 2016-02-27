package com.numericalactivity.dktxtools.dds;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.numericalactivity.dktxtools.utils.BufferUtils;
import com.numericalactivity.dktxtools.utils.FlagsUtils;
import com.numericalactivity.dktxtools.utils.TextureUtils;

/**
 * Classe de gestion des entêtes d'un fichier DDS
 */
public abstract class DDSHeader {

    public static final byte PIXEL_FORMAT_LENGTH        = 32; // taille des données du pixelFormat
    public static final short HEADER_LENGTH             = 124; // taille des entêtes
    public static final int FILE_IDENTIFIER             = 0x20534444; // identifiant du type de fichier DDS
    public static final short FILE_IDENTIFIER_LENGTH    = 4; // taille de l'identifiant du type de fichier DDS

    public static final int DDSD_CAPS                   = 0x1;
    public static final int DDSD_HEIGHT                 = 0x2;
    public static final int DDSD_WIDTH                  = 0x4;
    public static final int DDSD_PITCH                  = 0x8;
    public static final int DDSD_PIXELFORMAT            = 0x1000;
    public static final int DDSD_MIPMAPCOUNT            = 0x20000;
    public static final int DDSD_LINEARSIZE             = 0x80000;
    public static final int DDSD_DEPTH                  = 0x800000;

    public static final int DDSCAPS_COMPLEX             = 0x8;
    public static final int DDSCAPS_MIPMAP              = 0x400000;
    public static final int DDSCAPS_TEXTURE             = 0x1000;

    public static final int DDSCAPS2_CUBEMAP            = 0x200;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEX  = 0x400;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEX  = 0x800;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEY  = 0x1000;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEY  = 0x2000;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEZ  = 0x4000;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEZ  = 0x8000;
    public static final int DDSCAPS2_VOLUME             = 0x200000;

    public static final int DDPF_ALPHAPIXELS            = 0x1;
    public static final int DDPF_ALPHA                  = 0x2;
    public static final int DDPF_FOURCC                 = 0x4;
    public static final int DDPF_RGB                    = 0x40;
    public static final int DDPF_YUV                    = 0x200;
    public static final int DDPF_LUMINANCE              = 0x20000;

    DDSHeader10 _extendedHeaders;
    int _flags;
    int _height;
    int _width;
    int _pitchOrLinearSize;
    int _depth;
    int _mipmapCount;
    int[] _reserved1 = new int[11];
    int _caps;
    int _caps2;
    int _caps3;
    int _caps4;
    int _reserved2;

    int _pixelFormatFlags;
    int _pixelFormatfourCC;
    protected String _pixelFormatfourCCString;
    int _pixelFormatRgbBitCount;
    int _pixelFormatrBitMask;
    int _pixelFormatgBitMask;
    int _pixelFormatbBitMask;
    int _pixelFormataBitMask;

    /**
     * Retourne la chaine de caractères formée par le paramètre FourCC
     * @return
     */
    public String getFourCCString() {
        // on ne construit la chaine qu'une seule fois
        if (null != _pixelFormatfourCCString) {
            return _pixelFormatfourCCString;
        }

        _pixelFormatfourCCString = DDSFourCC.getFourCCString(_pixelFormatfourCC);
        return _pixelFormatfourCCString;
    }

    @Override
    public String toString() {
        // flags
        String flags            = FlagsUtils.toHexString(FlagsUtils.getFlags(_flags));
        String caps1            = FlagsUtils.toHexString(FlagsUtils.getFlags(_caps));
        String caps2            = FlagsUtils.toHexString(FlagsUtils.getFlags(_caps2));
        String caps3            = String.format("0x%08x", _caps3);
        String caps4            = String.format("0x%08x", _caps4);
        String reserved1String  = FlagsUtils.toHexString(_reserved1);
        String pixelFormatFlags = FlagsUtils.toHexString(FlagsUtils.getFlags(_pixelFormatFlags));

        // pixel format
        String pixelFormat = String.format(
            "        size=%d\n        flags=[%s]\n        fourCC=[0x%x, '%s']\n        rgbBitCount=%d\n        rBitMask=0x%x\n        gBitMask=0x%x\n        bBitMask=0x%x\n        aBitMask=0x%x",
            PIXEL_FORMAT_LENGTH, pixelFormatFlags, _pixelFormatfourCC, getFourCCString(), _pixelFormatRgbBitCount, _pixelFormatrBitMask,
            _pixelFormatgBitMask, _pixelFormatbBitMask, _pixelFormataBitMask
        );

        // chaine de retour
        String extendedHeaders = (null != _extendedHeaders) ? _extendedHeaders.toString() : "";

        return String.format(
            "%s\n    size=%d\n    flags=[%s]\n    height=%d\n    width=%d\n    pitchOrLinearSize=%d\n    depth=%d\n    mipmapCount=%d\n    reserved1=[%s]\n    caps=[%s]\n    caps2=[%s]\n    caps3=%s\n    caps4=%s\n    reserved2=%d\n    pixelFormat:\n%s\n%s",
            getClass().getCanonicalName(), HEADER_LENGTH, flags, _height, _width, _pitchOrLinearSize,
            _depth, _mipmapCount, reserved1String, caps1, caps2, caps3, caps4, _reserved2, pixelFormat, extendedHeaders
        );
    }
        
    /*
     * Getters
     */
    public int getSize() {
        return HEADER_LENGTH;
    }

    public int getFlags() {
        return _flags;
    }

    public int getHeight() {
        return _height;
    }

    public int getWidth() {
        return _width;
    }

    public int getPitchOrLinearSize() {
        return _pitchOrLinearSize;
    }

    public int getDepth() {
        return _depth;
    }

    public int getMipmapCount() {
        return _mipmapCount;
    }

    public int[] getReserved1() {
        return _reserved1.clone();
    }

    public int getCaps() {
        return _caps;
    }

    public int getCaps2() {
        return _caps2;
    }

    public int getCaps3() {
        return _caps3;
    }

    public int getCaps4() {
        return _caps4;
    }

    public int getReserved2() {
        return _reserved2;
    }

    public int getPixelFormatSize() {
        return PIXEL_FORMAT_LENGTH;
    }

    public int getPixelFormatFlags() {
        return _pixelFormatFlags;
    }

    public int getPixelFormatFourCC() {
        return _pixelFormatfourCC;
    }

    public int getPixelFormatRgbBitCount() {
        return _pixelFormatRgbBitCount;
    }

    public int getPixelFormatRBitMask() {
        return _pixelFormatrBitMask;
    }

    public int getPixelFormatGBitMask() {
        return _pixelFormatgBitMask;
    }

    public int getPixelFormatBBitMask() {
        return _pixelFormatbBitMask;
    }

    public int getPixelFormatABitMask() {
        return _pixelFormataBitMask;
    }

    public boolean hasFlags(int f) {
        return (_flags & f) == f;
    }

    public boolean hasCaps(int f) {
        return (_caps & f) == f;
    }

    public boolean hasCaps2(int f) {
        return (_caps2 & f) == f;
    }

    public boolean hasPixelFormatFlags(int f) {
        return (_pixelFormatFlags & f) == f;
    }

    public int getRealFourCC() {
        return (null == _extendedHeaders) ? _pixelFormatfourCC : _extendedHeaders.getDxgiFormat();
    }

    public DDSHeader10 getHeader10() {
        return _extendedHeaders;
    }

    public boolean isCompressed() {
        return (_pixelFormatFlags & DDPF_FOURCC) == DDPF_FOURCC;
    }

    public boolean isMipmapped() {
        return (_flags & DDSD_MIPMAPCOUNT) == DDSD_MIPMAPCOUNT;
    }

    public boolean isCubemap() {
        return (_caps2 & DDSCAPS2_CUBEMAP) == DDSCAPS2_CUBEMAP;
    }

    /*
     * Setters
     */
    public void setHeight(int height) {
        _height = height;
    }

    public void setWidth(int width) {
        _width = width;
    }

    public void setDepth(int depth) {
        _depth = depth;
    }

    public void setMipmapCount(int mipmapCount) {
        _mipmapCount = mipmapCount;
    }

    public void setPitchOrLinearSize(int pitchOrLinearSize) {
        _pitchOrLinearSize = pitchOrLinearSize;
    }

    public void setReserved1(int index, int reserved) {
        if (index > _reserved1.length - 1) {
            return;
        }

        _reserved1[index] = reserved;
    }

    public void setReserved2(int reserved) {
        _reserved2 = reserved;
    }

    public void setCaps3(int caps) {
        _caps3 = caps;
    }

    public void setCaps4(int caps) {
        _caps4 = caps;
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

    public void setCaps(int caps) {
        _caps = caps;
    }
    
    public void addCaps(int caps) {
        _caps |= caps;
    }

    public void removeCaps(int caps) {
        _caps &= ~caps;
    }

    public void setCaps2(int caps) {
        _caps2 = caps;
    }

    public void addCaps2(int caps) {
        _caps2 |= caps;
    }

    public void removeCaps2(int caps) {
        _caps2 &= ~caps;
    }

    public void setPixelFormatFlags(int flag) {
        _pixelFormatFlags = flag;
    }

    public void addPixelFormatFlag(int flag) {
        _pixelFormatFlags |= flag;
    }

    public void removePixelFormatFlag(int flag) {
        _pixelFormatFlags &= ~flag;
    }

    public void setPixelFormatFourCC(int fourcc) {
        _pixelFormatfourCC          = fourcc;
        _pixelFormatfourCCString    = null;
    }

    public void setPixelFormatRgbBitCount(int bitCount) {
        _pixelFormatRgbBitCount = bitCount;
    }

    public void setPixelFormatBitMask(int r, int g, int b, int a) {
        _pixelFormatrBitMask = r;
        _pixelFormatgBitMask = g;
        _pixelFormatbBitMask = b;
        _pixelFormataBitMask = a;
    }

    /**
     * Remet l'objet à son état d'origine
     */
    public void reset() {
        _extendedHeaders            = null;
        _flags                      = 0;
        _height                     = 0;
        _width                      = 0;
        _pitchOrLinearSize          = 0;
        _depth                      = 0;
        _mipmapCount                = 0;
        _caps                       = 0;
        _caps2                      = 0;

        _pixelFormatFlags           = 0;
        _pixelFormatfourCC          = 0;
        _pixelFormatfourCCString    = null;
        _pixelFormatRgbBitCount     = 0;
        _pixelFormatrBitMask        = 0;
        _pixelFormatgBitMask        = 0;
        _pixelFormatbBitMask        = 0;
        _pixelFormataBitMask        = 0;
    }

    /**
     * Une classe qui permet de lire les entêtes d'un fichier DDS
     */
    public static class Reader extends DDSHeader {
        /**
         * Lit les entêtes du fichier
         * @param in le pointeur doit être placé au début du fichier
         * @throws IOException 
         * @throws DDSFormatException 
         */
        protected Reader(BufferedInputStream in) throws IOException, DDSFormatException {
            read(in);
        }

        /**
         * Lit les entêtes du fichier
         * @param buffer buffer contenant les données des entêtes. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
         * @throws DDSFormatException 
         */
        protected Reader(ByteBuffer buffer) throws DDSFormatException {
            read(buffer);
        }

        /**
         * Lit les entêtes du fichier
         * @param in le pointeur doit être placé au début du fichier
         * @throws IOException 
         * @throws DDSFormatException 
         */
        protected void read(BufferedInputStream in) throws IOException, DDSFormatException {
            ByteBuffer buffer   = BufferUtils.getEmptyByteBuffer(HEADER_LENGTH + FILE_IDENTIFIER_LENGTH);
            byte[] data         = new byte[HEADER_LENGTH + FILE_IDENTIFIER_LENGTH];
            in.read(data, 0, HEADER_LENGTH + FILE_IDENTIFIER_LENGTH);
            buffer.put(data);
            buffer.position(0);
            read(buffer);

            // lecture des entêtes additionnelles
            if (null == _extendedHeaders && DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC) {
                _extendedHeaders = new DDSHeader10.Reader(in);
            }
        }

        /**
         * Lit les entêtes du fichier
         * @param buffer buffer contenant les données des entêtes. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
         * @throws DDSFormatException 
         */
        protected void read(ByteBuffer buffer) throws DDSFormatException {
            // on garde en mémoire l'ordre actuel du ByteBuffer
            ByteOrder oldOrder = buffer.order();

            // on défini l'ordre du buffer
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // on vérifie que l'identifiant du fichier correspond
            int fileIdentifier = buffer.getInt();

            if (FILE_IDENTIFIER != fileIdentifier) {
                throw new DDSFormatException("Input doesn't start with DDS file identifier");
            }

            // on récupère les entêtes
            int size            = buffer.getInt();

            if (HEADER_LENGTH != size) {
                throw new DDSFormatException("Unexpected size for headers: " + size);
            }

            _flags              = buffer.getInt();
            _height             = buffer.getInt();
            _width              = buffer.getInt();
            _pitchOrLinearSize  = buffer.getInt();
            _depth              = buffer.getInt();
            _mipmapCount        = buffer.getInt();

            for (int i = 0; i < 11; i++) {
                _reserved1[i] = buffer.getInt();
            }

            // pixel format
            int pixelFormatSize = buffer.getInt();

            if (PIXEL_FORMAT_LENGTH != pixelFormatSize) {
                throw new DDSFormatException("Unexpected size for pixel format: " + pixelFormatSize);
            }

            _pixelFormatFlags       = buffer.getInt();
            _pixelFormatfourCC      = buffer.getInt();
            _pixelFormatRgbBitCount = buffer.getInt();
            _pixelFormatrBitMask    = buffer.getInt();
            _pixelFormatgBitMask    = buffer.getInt();
            _pixelFormatbBitMask    = buffer.getInt();
            _pixelFormataBitMask    = buffer.getInt();

            // suite des entêtes
            _caps               = buffer.getInt();
            _caps2              = buffer.getInt();
            _caps3              = buffer.getInt();
            _caps4              = buffer.getInt();
            _reserved2          = buffer.getInt();

            // entêtes additionnelles
            if (buffer.hasRemaining() && DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC) {
                _extendedHeaders = new DDSHeader10.Reader(buffer);
            }

            // on remet le buffer à son ordre d'origine
            buffer.order(oldOrder);
        }
    }

    /**
     * Une classe qui permet d'écrire des entêtes de fichier DDS dans un flux
     */
    public static class Writer extends DDSHeader {
        /**
         * Constructeur
         */
        protected Writer() {
        }

        /**
         * Contrôle l'integrité des données qui vont être insérées
         * @return
         * @throws DDSFormatException
         */
        protected boolean check() throws DDSFormatException {
            // on vérifie le nombre de mipmaps en fonction de la hauteur et de la largeur
            byte numberOfMipmaps = TextureUtils.getNumberOfMipmaps(_width, _height);

            if (_mipmapCount > 1 && numberOfMipmaps != _mipmapCount) {
                throw new DDSFormatException(String.valueOf(numberOfMipmaps) + " mipmap levels must be set, but " + String.valueOf(_mipmapCount) + " are set");
            }

            // on vérifie les faces du cubemap
            if (hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP) && (
                    !hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEX) ||
                    !hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEX) ||
                    !hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEY) ||
                    !hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEY) ||
                    !hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEZ) ||
                    !hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEZ)
                    )) {
                // la texture est indiquée comme étant un cubemap donc les 6 faces doivent être définies
                throw new DDSFormatException("DDSCAPS2_CUBEMAP caps2 flag is set so all 6 faces must be defined");
            } else if (!hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP) && (
                    hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEX) ||
                    hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEX) ||
                    hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEY) ||
                    hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEY) ||
                    hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_NEGATIVEZ) ||
                    hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP_POSITIVEZ)
                    )) {
                // la texture est indiquée comme n'étant pas un cubemap donc aucune face ne doit être définie
                throw new DDSFormatException("DDSCAPS2_CUBEMAP caps2 flag is not set so no face must be defined");
            }

            // extended headers
            if (DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC && null == _extendedHeaders) {
                throw new DDSFormatException("FourCC is set to FOURCC_DX10 so extended headers must be defined");
            }

            // flags
            if (!hasFlags(DDSD_CAPS) || !hasFlags(DDSD_WIDTH) || !hasFlags(DDSD_HEIGHT) || !hasFlags(DDSD_PIXELFORMAT)) {
                throw new DDSFormatException("Flags DDSD_CAPS, DDSD_WIDTH, DDSD_HEIGHT and DDSD_PIXELFORMAT are required");
            }

            if (hasFlags(DDSD_MIPMAPCOUNT) && _mipmapCount <= 1) {
                throw new DDSFormatException("Flag DDSD_MIPMAPCOUNT is set but mipmapCount is set to " + String.valueOf(_mipmapCount));
            }

            if (hasFlags(DDSD_DEPTH)) {
                throw new DDSFormatException("Flag DDSD_DEPTH is set but 3D textures are not supported");
            }

            if ((hasFlags(DDSD_PITCH) && hasFlags(DDSD_LINEARSIZE)) || (!hasFlags(DDSD_PITCH) && !hasFlags(DDSD_LINEARSIZE))) {
                throw new DDSFormatException("Either flag DDSD_PITCH or DDSD_LINEARSIZE must be defined");
            }

            // width et height
            if (_width <= 0 || _height <= 0) {
                throw new DDSFormatException("Invalid width (" + String.valueOf(_width) + ") and/or height (" + String.valueOf(_height) + ")");
            }

            // caps
            if (!hasCaps(DDSCAPS_TEXTURE)) {
                throw new DDSFormatException("Caps flag DDSCAPS_TEXTURE is required");
            }

            if (hasFlags(DDSD_MIPMAPCOUNT) && (!hasCaps(DDSCAPS_MIPMAP) || !hasCaps(DDSCAPS_COMPLEX))) {
                throw new DDSFormatException("Flag DDSD_MIPMAPCOUNT is set, so caps flags DDSCAPS_MIPMAP and DDSCAPS_COMPLEX are required");
            } else if (!hasFlags(DDSD_MIPMAPCOUNT) && hasCaps(DDSCAPS_MIPMAP)) {
                throw new DDSFormatException("Flag DDSD_MIPMAPCOUNT is not set, so caps flags DDSCAPS_MIPMAP must not be set");
            }

            if (hasCaps2(DDSCAPS2_CUBEMAP) && !hasCaps(DDSCAPS_COMPLEX)) {
                throw new DDSFormatException("Caps2 flag DDSCAPS2_CUBEMAP is set, so caps flags DDSCAPS_COMPLEX must be set");
            }

            if (DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC && _extendedHeaders.getArraySize() > 1 && !hasCaps(DDSCAPS_COMPLEX)) {
                throw new DDSFormatException("Extended headers array size is set to " + String.valueOf(_extendedHeaders.getArraySize()) + ", so caps flags DDSCAPS_COMPLEX must be set");
            }

            // caps2
            if (hasCaps2(DDSCAPS2_VOLUME)) {
                throw new DDSFormatException("Caps2 flag DDSCAPS2_VOLUME is set, but 3D textures are not supported");
            }

            // fourcc (textures compressées)
            if (hasPixelFormatFlags(DDPF_FOURCC)
                    && DDSFourCC.FOURCC_ATC != _pixelFormatfourCC
                    && DDSFourCC.FOURCC_ATCA != _pixelFormatfourCC
                    && DDSFourCC.FOURCC_ATCI != _pixelFormatfourCC
                    && DDSFourCC.FOURCC_DX10 != _pixelFormatfourCC
                    && DDSFourCC.FOURCC_DXT1 != _pixelFormatfourCC
                    && DDSFourCC.FOURCC_DXT3 != _pixelFormatfourCC
                    && DDSFourCC.FOURCC_DXT5 != _pixelFormatfourCC
                    && DDSFourCC.FOURCC_ETC1 != _pixelFormatfourCC) {
                throw new DDSFormatException("Invalid fourCC value 0x" + Integer.toHexString(_pixelFormatfourCC) + " - " + getFourCCString());
            }

            // pixel format (textures non compressées)
            if (!hasPixelFormatFlags(DDPF_FOURCC)) {
                if (_pixelFormatRgbBitCount <= 0) {
                    throw new DDSFormatException("Invalid RGB bitcount (" + String.valueOf(_pixelFormatRgbBitCount) + ")");
                }

                if (hasPixelFormatFlags(DDPF_RGB) && (_pixelFormatrBitMask == 0 || _pixelFormatgBitMask == 0 || _pixelFormatbBitMask == 0)) {
                    throw new DDSFormatException("Red, green and blue bitmasks must be defined");
                }

                if ((hasPixelFormatFlags(DDPF_ALPHA) || hasPixelFormatFlags(DDPF_ALPHAPIXELS)) && _pixelFormataBitMask == 0) {
                    throw new DDSFormatException("Alpha bitmask must be defined");
                }

                if (hasPixelFormatFlags(DDPF_LUMINANCE) && _pixelFormatrBitMask == 0) {
                    throw new DDSFormatException("Red bitmask must be defined");
                }
            }

            // header10
            if (hasPixelFormatFlags(DDPF_FOURCC) && DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC) {
                // header10 miscFlags
                if (DDSHeader10.DDS_DIMENSION_TEXTURE2D == _extendedHeaders.getResourceDimension()
                        && _extendedHeaders.hasMiscFlags(DDSHeader10.DDS_RESOURCE_MISC_TEXTURECUBE)
                        && !hasCaps2(DDSCAPS2_CUBEMAP)) {
                    throw new DDSFormatException("Extended headers indicate a cubemap, but not regular headers");
                }

                ((DDSHeader10.Writer) _extendedHeaders).check();
            }

            return true;
        }

        /**
         * Écrit les entêtes dans un flux
         * @param out
         * @throws IOException
         * @throws DDSFormatException 
         */
        public void write(BufferedOutputStream out) throws IOException, DDSFormatException {
            ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + FILE_IDENTIFIER_LENGTH);
            write(buffer);
            out.write(buffer.array());

            // entêtes additionnelles
            if (DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC) {
                ((DDSHeader10.Writer) _extendedHeaders).write(out);
            }
        }

        /**
         * Rempli un ByteBuffer avec les entêtes définies
         * @param buffer
         * @throws DDSFormatException 
         */
        public void write(ByteBuffer buffer) throws DDSFormatException {
            check();

            // on garde en mémoire l'ordre actuel du ByteBuffer
            ByteOrder oldOrder = buffer.order();

            // on replace le buffer à la position 0
            buffer.position(0);

            // on défini l'ordre du buffer
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // calcul du pitchOrLinearSize
            int pitchOrLinearSize   = 0;
            int fourCC              = (DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC) ? _extendedHeaders.getDxgiFormat() : _pixelFormatfourCC;

            if (hasFlags(DDSD_PITCH)) {
                // calcul du linearSize pour les textures non compressées
                pitchOrLinearSize = (_width * _pixelFormatRgbBitCount + 7) / 8;
            } else {
                // calcul du linearSize pour les textures compressées
                pitchOrLinearSize = Math.max(1, _width / 4) * Math.max(1, _height / 4) * DDSUtil.getCompressedBlockSize(fourCC);
            }

            // on ajoute les entêtes
            buffer.putInt(FILE_IDENTIFIER);
            buffer.putInt(HEADER_LENGTH);
            buffer.putInt(_flags);
            buffer.putInt(_height);
            buffer.putInt(_width);
            buffer.putInt(pitchOrLinearSize);
            buffer.putInt(_depth);
            buffer.putInt(_mipmapCount);

            for (int reserved : _reserved1) {
                buffer.putInt(reserved);
            }

            // pixel format
            buffer.putInt(PIXEL_FORMAT_LENGTH);
            buffer.putInt(_pixelFormatFlags);
            buffer.putInt(_pixelFormatfourCC);
            buffer.putInt(_pixelFormatRgbBitCount);
            buffer.putInt(_pixelFormatrBitMask);
            buffer.putInt(_pixelFormatgBitMask);
            buffer.putInt(_pixelFormatbBitMask);
            buffer.putInt(_pixelFormataBitMask);

            // suite des entêtes
            buffer.putInt(_caps);
            buffer.putInt(_caps2);
            buffer.putInt(_caps3);
            buffer.putInt(_caps4);
            buffer.putInt(_reserved2);

            // entêtes additionnelles
            if (buffer.hasRemaining() && DDSFourCC.FOURCC_DX10 == _pixelFormatfourCC) {
                if (null != _extendedHeaders) {
                    ((DDSHeader10.Writer) _extendedHeaders).write(buffer);
                } else {
                    throw new DDSFormatException("Attempt to write extended headers without any extended headers defined");
                }
            }

            // on remet le buffer à son ordre d'origine
            buffer.order(oldOrder);
        }

        /**
         * Défini les entêtes additionnelles
         * @param header10
         */
        public void setHeader10(DDSHeader10.Writer header10) {
            _extendedHeaders    = header10;
            _pixelFormatfourCC  = DDSFourCC.FOURCC_DX10;
        }
    }

}
