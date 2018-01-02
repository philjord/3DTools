package com.numericalactivity.dktxtools.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {
    /**
     * Compresse le contenu d'un flux
     * @param out flux dans lequel les données compressées seront écrite
     * @param in flux contenant les données à compresser
     * @throws IOException
     */
    public static void encode(OutputStream out, InputStream in) throws IOException {
        // on crée un flux bufferisé à partir du flux passé en entrée
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }

        // on crée un flux bufferisé à partir du flux passé en entrée
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }

        GZIPOutputStream gzipOut = new GZIPOutputStream(out);
        int readedBytes;
        int available;
        byte[] data;

        do {
            available   = Math.max(1, in.available());
            data        = new byte[available];
            readedBytes = in.read(data);

            if (readedBytes > 0) {
                gzipOut.write(data);
            }
        } while(readedBytes != -1);

        gzipOut.finish();
        gzipOut.flush();
        gzipOut.close();
        in.close();
    }

    /**
     * Compresse le contenu d'un fichier
     * @param outFile fichier dans lequel les données compressées seront écrite
     * @param inFile fichier contenant les données à compresser
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void encode(File outFile, File inFile) throws FileNotFoundException, IOException {
        encode(new FileOutputStream(outFile), new FileInputStream(inFile));
    }

    /**
     * Décompresse le contenu d'un flux
     * @param out flux dans lequel les données compressées seront écrite
     * @param in flux contenant les données à compresser
     * @throws IOException
     */
    public static void decode(OutputStream out, InputStream in) throws IOException {
        // on crée un flux bufferisé à partir du flux passé en entrée
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }

        // on crée un flux bufferisé à partir du flux passé en entrée
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }

        GZIPInputStream gzipIn = new GZIPInputStream(in);
        int readedBytes;
        int available;
        byte[] data;

        do {
            available   = Math.max(1, gzipIn.available());
            data        = new byte[available];
            readedBytes = gzipIn.read(data);

            if (readedBytes > 0) {
                out.write(data);
            }
        } while(readedBytes != -1);

        out.flush();
        out.close();
        gzipIn.close();
    }

    /**
     * Décompresse le contenu d'un fichier
     * @param outFile fichier dans lequel les données compressées seront écrite
     * @param inFile flux contenant les données à compresser
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void decode(File outFile, File inFile) throws FileNotFoundException, IOException {
        decode(new FileOutputStream(outFile), new FileInputStream(inFile));
    }
}
