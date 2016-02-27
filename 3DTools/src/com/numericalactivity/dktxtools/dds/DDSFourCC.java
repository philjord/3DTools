package com.numericalactivity.dktxtools.dds;

import java.io.UnsupportedEncodingException;

final public class DDSFourCC {

    public static final int FOURCC_ATC  = 0x20435441;
    public static final int FOURCC_ATCA = 0x41435441;
    public static final int FOURCC_ATCI = 0x49435441;
    public static final int FOURCC_ETC1 = 0x30435445;
	public static final int FOURCC_DX10 = 0x30315844;
	public static final int FOURCC_DXT1 = 0x31545844;
	public static final int FOURCC_DXT3 = 0x33545844;
	public static final int FOURCC_DXT5 = 0x35545844;
	
	private DDSFourCC() {}

	/**
     * Retourne la chaine de caractères formée par le paramètre FourCC (ou dxgiFormat dans le cas d'entêtes DX10)
     * @return
     */
    public static String getFourCCString(int fourCC) {
        byte[] bytes    = new byte[4];
        bytes[0]        = (byte)(fourCC);
        bytes[1]        = (byte)(fourCC >> 8);
        bytes[2]        = (byte)(fourCC >> 16);
        bytes[3]        = (byte)(fourCC >> 24);

        try {
            return new String(bytes, 0, bytes.length, "ASCII");
        } catch (UnsupportedEncodingException e) {
            return new String(bytes, 0, bytes.length);
        }
    }
	
}
