package com.numericalactivity.dktxtools.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    protected static final String ALGORITHM = "md5";

    protected static MessageDigest _digest;

    /**
     * Retourne la somme de contrôle d'un fichier
     * @param file chemin vers le fichier
     * @return
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    public static byte[] getChecksum(String file) throws NoSuchAlgorithmException, IOException {
        return getChecksum(new File(file));
    }

    /**
     * Retourne la somme de contrôle d'un fichier
     * @param file
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     */
    public static byte[] getChecksum(File file) throws NoSuchAlgorithmException, IOException {
        if (null == _digest) {
            _digest = MessageDigest.getInstance(ALGORITHM);
        }

        BufferedInputStream in  = new BufferedInputStream(new FileInputStream(file));
        byte[] data             = new byte[1024];
        int readedBytes;

        do {
            readedBytes = in.read(data);

            if (readedBytes > 0) {
                _digest.update(data, 0, readedBytes);
            }
        } while(readedBytes != -1);

        in.close();
        return _digest.digest();
    }

    /**
     * Retourne la somme de contrôle des données contenues dans un ByteBuffer
     * @param buffer
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     */
    public static byte[] getChecksum(ByteBuffer buffer) throws NoSuchAlgorithmException {
        if (null == _digest) {
            _digest = MessageDigest.getInstance(ALGORITHM);
        }
        
        byte[] data = new byte[buffer.capacity()];
        buffer.get(data);
        _digest.update(data);
        return _digest.digest();
    }

    /**
     * Retourne la somme de contrôle md5 des données contenues dans un byte array
     * @param array
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     */
    public static byte[] getChecksum(byte[] array) throws NoSuchAlgorithmException {
        if (null == _digest) {
            _digest = MessageDigest.getInstance(ALGORITHM);
        }

        _digest.update(array);
        return _digest.digest();
    }

    /**
     * Retourne true si les deux fichiers sont égaux
     * @param file1
     * @param file2
     * @return
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    public static boolean isEqual(String file1, String file2) throws NoSuchAlgorithmException, IOException {
        return isEqual(new File(file1), new File(file2));
    }

    /**
     * Retourne true si les deux fichiers sont égaux
     * @param file1
     * @param file2
     * @return
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    public static boolean isEqual(File file1, File file2) throws NoSuchAlgorithmException, IOException {
        return MessageDigest.isEqual(getChecksum(file1), getChecksum(file2));
    }

    /**
     * Retourne true si les données contenues dans les deux ByteBuffer sont égales
     * @param buffer1
     * @param buffer2
     * @return
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    public static boolean isEqual(ByteBuffer buffer1, ByteBuffer buffer2) throws NoSuchAlgorithmException, IOException {
        return MessageDigest.isEqual(getChecksum(buffer1), getChecksum(buffer2));
    }

    /**
     * Retourne true si les données contenues dans les deux byte array sont égales
     * @param array1
     * @param array2
     * @return
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    public static boolean isEqual(byte[] array1, byte[] array2) throws NoSuchAlgorithmException, IOException {
        return MessageDigest.isEqual(getChecksum(array1), getChecksum(array2));
    }

    /**
     * Retourne true si les données contenues dans le ByteBuffer et le byte array sont égales
     * @param array1
     * @param array2
     * @return
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    public static boolean isEqual(ByteBuffer buffer, byte[] array) throws NoSuchAlgorithmException, IOException {
        return MessageDigest.isEqual(getChecksum(buffer), getChecksum(array));
    }

    /**
     * Retourne true si la date de dernière modification du fichier 'compare' est plus récente que celle de 'reference'
     * @param reference
     * @param compare
     * @return
     */
    public static boolean isNewerThan(File compare, File reference) {
        return compare.lastModified() > reference.lastModified();
    }
}
