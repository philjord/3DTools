package com.numericalactivity.dktxtools.dds;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.numericalactivity.dktxtools.utils.BufferUtils;
import com.numericalactivity.dktxtools.utils.FlagsUtils;

public abstract class DDSHeader10 {

	public static final int HEADER_LENGTH                  = 20; // taille des entêtes

	public static final int DDS_RESOURCE_MISC_TEXTURECUBE  = 0x4;

	public static final int DDS_DIMENSION_TEXTURE1D        = 2;
	public static final int DDS_DIMENSION_TEXTURE2D        = 3;
	public static final int DDS_DIMENSION_TEXTURE3D        = 4;
	
	protected String _dxgiFormatString;
	int _dxgiFormat;
	int _resourceDimension;
	int _miscFlag;	
	int _arraySize;
	int _reserved;

	@Override
    public String toString() {
	    // flags
        String miscFlag = FlagsUtils.toHexString(FlagsUtils.getFlags(_miscFlag));

        // chaine de retour
        return String.format(
            "%s\n    dxgiFormat=[%d, '%s']\n    resourceDimension=%d\n    miscFlag=[%s]\n    arraySize=%d\n    reserved=%d",
            getClass().getCanonicalName(), _dxgiFormat, getDxgiFormatString(),  _resourceDimension, miscFlag, _arraySize, _reserved
        );
    }

	/**
     * Retourne la chaine de caractères formée par le paramètre DxgiFormat
     * @return
     */
    public String getDxgiFormatString() {
        // on ne construit la chaine qu'une seule fois
        if (null != _dxgiFormatString) {
            return _dxgiFormatString;
        }

        _dxgiFormatString = DDSFourCC.getFourCCString(_dxgiFormat);
        return _dxgiFormatString;
    }
        
    /*
     * Getters
     */
    public int getDxgiFormat() {
        return _dxgiFormat;
    }

    public int getResourceDimension() {
        return _resourceDimension;
    }

    public int getArraySize() {
        return _arraySize;
    }

    public int getReserved() {
        return _reserved;
    }

    public boolean hasMiscFlags(int f) {
        return (_miscFlag & f) == f;
    }

    /*
     * Setters
     */
    public void setDxgiFormat(int dxgiFormat) {
        _dxgiFormat         = dxgiFormat;
        _dxgiFormatString   = null;
    }

    public void setResourceDimension(int resourceDimension) {
        _resourceDimension = resourceDimension;
    }

    public void setArraySize(int arraySize) {
        _arraySize = arraySize;
    }

    public void setReserved(int reserved) {
        _reserved = reserved;
    }

    public void setMiscFlags(int flag) {
        _miscFlag = flag;
    }

    public void addMiscFlag(int flag) {
        _miscFlag |= flag;
    }

    public void removeMiscFlag(int flag) {
        _miscFlag &= ~flag;
    }

    /**
     * Une classe qui permet de lire les entêtes additionnelles d'un fichier DDS
     */
	public static class Reader extends DDSHeader10 {
	    /**
         * Lit les entêtes additionnelles du fichier
         * @param in le pointeur doit être placé au début des entêtes additionnelles
         * @throws IOException 
         */
	    protected Reader(BufferedInputStream in) throws IOException {
	        read(in);
	    }

	    /**
         * Lit les entêtes additionnelles du fichier
         * @param buffer buffer contenant les données des entêtes additionnelles. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes additionnelles. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
         * @throws DDSFormatException 
         */
	    protected Reader(ByteBuffer buffer) {
	        read(buffer);
	    }

	    /**
	     * Lit les entêtes additionnelles du fichier
	     * @param in le pointeur doit être placé au début des entêtes additionnelles
	     * @throws IOException 
	     */
	    protected void read(BufferedInputStream in) throws IOException {
	        ByteBuffer buffer   = BufferUtils.getEmptyByteBuffer(HEADER_LENGTH);
            byte[] data         = new byte[HEADER_LENGTH];
            in.read(data, 0, HEADER_LENGTH);
            buffer.put(data);
            buffer.position(0);
            read(buffer);
	    }

	    /**
	     * Lit les entêtes additionnelles du fichier
	     * @param buffer buffer contenant les données des entêtes additionnelles. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes additionnelles. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
	     * @throws DDSFormatException 
	     */
	    protected void read(ByteBuffer buffer) {
	        // on garde en mémoire l'ordre actuel du ByteBuffer
            ByteOrder oldOrder = buffer.order();

            // on défini l'ordre du buffer
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // on récupère les entêtes additionnelles
            _dxgiFormat         = buffer.getInt();
            _resourceDimension  = buffer.getInt();
            _miscFlag           = buffer.getInt();
            _arraySize          = buffer.getInt();
            _reserved           = buffer.getInt();

            // on remet le buffer à son ordre d'origine
            buffer.order(oldOrder);
	    }
	}

	/**
     * Une classe qui permet d'écrire des entêtes additionnelles de fichier DDS dans un flux
     */
	public static class Writer extends DDSHeader10 {
	    /**
         * Constructeur
         */
	    protected Writer() {
	    }

	    protected boolean check() throws DDSFormatException {
	        // array size
            if (_arraySize != 0) {
                throw new DDSFormatException("Texture arrays are not supported");
            }

            // dxgiFormat
            if (DDSFourCC.FOURCC_ATC != _dxgiFormat
                    && DDSFourCC.FOURCC_ATCA != _dxgiFormat
                    && DDSFourCC.FOURCC_ATCI != _dxgiFormat
                    && DDSFourCC.FOURCC_DX10 != _dxgiFormat
                    && DDSFourCC.FOURCC_DXT1 != _dxgiFormat
                    && DDSFourCC.FOURCC_DXT3 != _dxgiFormat
                    && DDSFourCC.FOURCC_DXT5 != _dxgiFormat
                    && DDSFourCC.FOURCC_ETC1 != _dxgiFormat) {
                throw new DDSFormatException("Invalid dxgiFormat value 0x" + Integer.toHexString(_dxgiFormat) + " - " + getDxgiFormatString());
            }

            // resourceDimension
            if (DDS_DIMENSION_TEXTURE3D == _resourceDimension) {
                throw new DDSFormatException("resourceDimension is set to DDS_DIMENSION_TEXTURE3D but 3D textures are not supported");
            }

	        return true;
	    }

	    /**
         * Écrit les entêtes additionnelles dans un flux
         * @param out
         * @throws IOException
         */
        protected void write(BufferedOutputStream out) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH);
            write(buffer);
            out.write(buffer.array());
        }

        /**
         * Rempli un ByteBuffer avec les entêtes additionnelles définies
         * @param buffer
         */
        protected void write(ByteBuffer buffer) {
            // on garde en mémoire l'ordre actuel du ByteBuffer
            ByteOrder oldOrder = buffer.order();

            // on replace le buffer à la position 0
            buffer.position(0);

            // on défini l'ordre du buffer
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // on ajoute les entêtes additionnelles
            buffer.putInt(_dxgiFormat);
            buffer.putInt(_resourceDimension);
            buffer.putInt(_miscFlag);
            buffer.putInt(_arraySize);
            buffer.putInt(_reserved);

            // on remet le buffer à son ordre d'origine
            buffer.order(oldOrder);
        }
	}
	  
}
