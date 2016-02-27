package com.numericalactivity.dktxtools.ktx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utilitaire pour la lecture/écriture des fichiers KTX
 */
abstract class KTXUtil {

    /**
     * Retourne le multiple de 4 directement supérieur à 'i'
     * @param i
     * @return
     */
    public static byte align4(int i) {
        return (byte) (3 - ((i + 3) % 4));
    }

    /**
     * Retourne le multiple de 4 directement supérieur à 'i'
     * @param i
     * @return
     */
    public static byte align4(long i) {
        return (byte) (3 - ((i + 3) % 4));
    }

    /**
     * Lit les 4 prochains byte d'un flux et retourne un entier
     * @param in flus à lire
     * @param tempBuffer buffer temporaire auquel on aura alloué 4 byte
     * @param order ordre du buffer
     * @return l'entier formé par les 4 prochains byte du flux
     * @throws IOException
     */
    public static int readInt(InputStream in, ByteBuffer tempBuffer, ByteOrder order) throws IOException {
        byte[] tempArray = new byte[4];
        in.read(tempArray);
        tempBuffer.position(0);
        tempBuffer.put(tempArray);
        tempBuffer.position(0);
        tempBuffer.order(order);
        return tempBuffer.getInt();
    }
   

}
