package com.numericalactivity.dktxtools.utils;

abstract public class FlagsUtils {
    /**
     * Retrouve les flags contenus dans un entier
     * @param flags entier contenant les flags
     * @return liste des flags
     */
    public static int[] getFlags(int flags) {
        // TODO optimiser
        // on cherche combien de flags différents sont définis
        short n = 0;

        for (int i = 1; i < Integer.MAX_VALUE && i > 0; i *= 2) {
            if ((i & flags) > 0) {
                n++;
            }
        }

        // on récupère la liste des flags
        int[] flagsList = new int[n];
        n               = 0;

        for (int i = 1; i < Integer.MAX_VALUE && i > 0; i *= 2) {
            if ((i & flags) > 0) {
                flagsList[n] = i;
                n++;
            }
        }

        return flagsList;
    }

    /**
     * Transforme une liste de valeurs en chaine de caractères
     * @param flags liste de valeurs
     * @return chaine de caractères représentant les valeurs (en hexadécimal)
     */
    public static String toHexString(int[] flags) {
        // aucun flag
        if (0 == flags.length) {
            return "0x0";
        }

        // on crée un StringBuffer en l'initialisant à la taille maximale (10 caractères par valeur, plus la ponctuation)
        StringBuffer stringBuffer   = new StringBuffer(flags.length * 10 + (flags.length - 1) * 2);
        short i                     = 0;

        for (int flag : flags) {
            // on ajoute une virgule de séparation
            if (0 != i) {
                stringBuffer.append(", ");
            }

            stringBuffer.append(String.format("0x%x", flag));
            i++;
        }

        return stringBuffer.toString();
    }
}
