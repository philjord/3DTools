package com.numericalactivity.dktxtools.utils;

public class TextureUtils {
    /**
     * Retourne le nombre de niveaux mipmaps nécessaire pour avoir une chaine de mipmaps complète.
     * La plus grande valeur entre 'width' et 'height' est prise en compte comme valeur de base.
     * @param width
     * @param height
     * @return
     */
    public static byte getNumberOfMipmaps(int width, int height) {
        short dimension = (short) Math.max(width, height);
        return (byte) (Math.log(dimension) / Math.log(2) + 1);
    }

    /**
     * Retourne une dimension (largeur, hauteur, profondeur) pour un niveau mipmap donné
     * @param mipmapLevel niveau mipmap pour lequel calculer la dimension
     * @param baseDimension dimension du niveau mipmap 0
     * @return
     */
    public static short getDimensionForMipmapLevel(int mipmapLevel, int baseDimension) {
        return (short) ((mipmapLevel > 0) ? Math.max(1, baseDimension / Math.pow(2, mipmapLevel)) : baseDimension);
    }
}
