
package com.numericalactivity.dktxtools;

public final class TextureFormat {

    // formats de base
    public static final int GL_ALPHA                            = 0x1906;
    public static final int GL_LUMINANCE                        = 0x1909;
    public static final int GL_LUMINANCE_ALPHA                  = 0x190a;
    public static final int GL_RGB                              = 0x1907;
    public static final int GL_RGBA                             = 0x1908;

    public static final int GL_ALPHA8                           = 0x803c;
    public static final int GL_LUMINANCE8                       = 0x8040;
    public static final int GL_LUMINANCE8_ALPHA8                = 0x8045;
    public static final int GL_RGB8                             = 0x8051;
    public static final int GL_RGBA8                            = 0x8058;

    // formats compressés
    public static final int GL_ETC1_RGB8                        = 0x8d64;

    public static final int GL_COMPRESSED_RGB_S3TC_DXT1_EXT     = 0x83f0;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT1_EXT    = 0x83f1;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT3_EXT    = 0x83f2;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT5_EXT    = 0x83f3;

    public static final int GL_COMPRESSED_RGB_PVRTC_4BPPV1_IMG  = 0x8C00;
    public static final int GL_COMPRESSED_RGB_PVRTC_2BPPV1_IMG  = 0x8C01;
    public static final int GL_COMPRESSED_RGBA_PVRTC_4BPPV1_IMG = 0x8C02;
    public static final int GL_COMPRESSED_RGBA_PVRTC_2BPPV1_IMG = 0x8C03;

    public static final int GL_ATC_RGB_AMD                      = 0x8C92;
    public static final int GL_ATC_RGBA_EXPLICIT_ALPHA_AMD      = 0x8C93;
    public static final int GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD  = 0x87EE;

    // types de données
    public static final int GL_UNSIGNED_BYTE                    = 0x1401;

    private TextureFormat(){}
}
