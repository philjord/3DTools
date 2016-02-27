package com.numericalactivity.dktxtools.dds;

final class DDSUtil {

	private DDSUtil() {		
	}

	/**
	 * Retourne la taille d'un block (en byte) pour le format de compression spécifié
	 * @param fourCC attribut "fourCC" des entêtes spécifiant le format de compression
	 */
	public static int getCompressedBlockSize(int fourCC) {
		switch (fourCC) {
    		case DDSFourCC.FOURCC_DXT1:
    		case DDSFourCC.FOURCC_ATC:
    		case DDSFourCC.FOURCC_ETC1:
    			return 8;

			default:
			    return 16;
		}
	}

	/**
	 * Retourne la taille d'une image (en byte) compressée
	 * @param width    largeur de l'image
	 * @param height   hauteur de l'image
	 * @param fourCC   fourCC de l'image correspondant au format compressé
	 * @return
	 */
	public static int getCompressedSize(int width, int height, int fourCC) {
	    return Math.max(1, width / 4) * Math.max(1, height / 4) * getCompressedBlockSize(fourCC);
	}

	/**
	 * Retourne la taille d'une image (en byte) non compressée
	 * @param width        largeur de l'image
	 * @param height       hauteur de l'image
	 * @param bitsPerPixel nombre de bits par pixels
	 * @return
	 */
	public static int getUncompressedSize(int width, int height, int bitsPerPixel) {
	    return ((width * bitsPerPixel + 7) / 8) * height;
	}
	
}
