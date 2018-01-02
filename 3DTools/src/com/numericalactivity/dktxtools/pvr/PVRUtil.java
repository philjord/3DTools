package com.numericalactivity.dktxtools.pvr;

final class PVRUtil {

    public static final byte BLOCK_SIZE = 8;

    private PVRUtil() {}

    /**
     * Retourne la taille d'une image (en byte) compressée en PVRTC
     * @param width largeur de l'image
     * @param height hauteur de l'image
     * @param pixelFormat pixelFormat de l'image correspondant au format compressé
     * @return
     */
    public static int getCompressedSize(int width, int height, int pixelFormat) {
        byte blockWidth     = 4;
        byte blockHeight    = 4;

        if (PVRPixelFormat.PVRTC2BPP_RGB == pixelFormat || PVRPixelFormat.PVRTC2BPP_RGBA == pixelFormat) {
            blockWidth = 8;
        }

        return Math.max(1, width / blockWidth) * Math.max(1, height / blockHeight) * BLOCK_SIZE;
    }
}
