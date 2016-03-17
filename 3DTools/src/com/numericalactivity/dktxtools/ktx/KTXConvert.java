package com.numericalactivity.dktxtools.ktx;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;

import com.numericalactivity.dktxtools.dds.DDSHeader;
import com.numericalactivity.dktxtools.dds.DDSReader;
import com.numericalactivity.dktxtools.pvr.PVRHeader;
import com.numericalactivity.dktxtools.pvr.PVRReader;

public class KTXConvert {
    /**
     * Converti un fichier DDS en fichier KTX
     * @param ddsReader fichier DDS à convertir
     * @param out flux dans lequel sera écrit le fichier KTX
     * @throws KTXFormatException
     * @throws IOException
     */
    public static void convertDDS(DDSReader ddsReader, OutputStream out) throws KTXFormatException, IOException {
        convertDDS(ddsReader, out, null);
    }

    /**
     * Converti un fichier DDS en fichier KTX
     * @param ddsReader fichier DDS à convertir
     * @param out flux dans lequel sera écrit le fichier KTX
     * @param metadata métadonnées à insérer dans le fichier KTX
     * @throws KTXFormatException
     * @throws IOException
     */
    public static void convertDDS(DDSReader ddsReader, OutputStream out, HashMap<String, Object> metadata) throws KTXFormatException, IOException {
        // on crée un flux bufferisé à partir du flux passé en entrée
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }

        // on récupère les informations du fichier DDS
        DDSHeader ddsHeader             = ddsReader.getHeaders();
        byte numberOfMipmap             = (byte) ddsHeader.getMipmapCount();
        boolean mipmapped               = numberOfMipmap > 1;
        boolean isCubemap               = ddsHeader.hasCaps2(DDSHeader.DDSCAPS2_CUBEMAP);
        short width                     = (short) ddsHeader.getWidth();
        short height                    = (short) ddsHeader.getHeight();
        int glFormat                    = ddsReader.getOpenglFormat();
        ByteBuffer[][] buffers          = ddsReader.getTextureData().getAll();
        byte numberOfFaces              = (byte) ((buffers.length > 0) ? buffers[0].length : 0);

        // on crée le writer KTX
        KTXWriter ktxWriter             = new KTXWriter(mipmapped, isCubemap, width, height);
        KTXMetadata ktxMetadata         = ktxWriter.getMetadata();
        KTXTextureData ktxTextureData   = ktxWriter.getTextureData();

        if (ddsReader.isCompressed()) {
            ktxWriter.setCompressedFormat(glFormat);
        } else {
            ktxWriter.setUncompressedFormat(glFormat);
        }

        // on ajoute les métadonnées
        if (null != metadata) {
            Set<String> set = metadata.keySet();
            Object obj;

            for (String key : set) {
                obj = metadata.get(key);

                if (obj instanceof String) {
                    ktxMetadata.set(key, (String) obj);
                } else if (obj instanceof byte[]) {
                    ktxMetadata.set(key, (byte[]) obj);
                }
            }
        }

        // on défini les données de textures
        byte mipmapLevel        = 0;
        byte face               = 0;

        for (mipmapLevel = 0; mipmapLevel < numberOfMipmap; mipmapLevel++) {
            for (face = 0; face < numberOfFaces; face++) {
                ktxTextureData.set(mipmapLevel, face, buffers[mipmapLevel][face]);
            }
        }

        // écriture du fichier KTX
        ktxWriter.write(out);
 
    }

    /**
     * Converti un fichier PVR en fichier KTX
     * @param pvrReader fichier PVR à convertir
     * @param out flux dans lequel sera écrit le fichier KTX
     * @throws KTXFormatException
     * @throws IOException
     */
    public static void convertPVR(PVRReader pvrReader, OutputStream out) throws KTXFormatException, IOException {
        convertPVR(pvrReader, out, null);
    }

    // TODO tests u
    /**
     * Converti un fichier PVR en fichier KTX
     * @param pvrReader fichier PVR à convertir
     * @param out flux dans lequel sera écrit le fichier KTX
     * @param metadata métadonnées à insérer dans le fichier KTX
     * @throws KTXFormatException
     * @throws IOException
     */
    public static void convertPVR(PVRReader pvrReader, OutputStream out, HashMap<String, Object> metadata) throws KTXFormatException, IOException {
        // on crée un flux bufferisé à partir du flux passé en entrée
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        
        // on récupère les informations du fichier PVR
        PVRHeader pvrHeader             = pvrReader.getHeaders();
        byte numberOfMipmap             = (byte) pvrHeader.getNumberOfMipmapLevels();
        boolean mipmapped               = numberOfMipmap > 1;
        boolean isCubemap               = pvrHeader.isCubemap();
        short width                     = (short) pvrHeader.getWidth();
        short height                    = (short) pvrHeader.getHeight();
        int glFormat                    = pvrReader.getOpenglFormat();
        ByteBuffer[][] buffers          = pvrReader.getTextureData().getAll();
        byte numberOfFaces              = (byte) pvrHeader.getNumberOfFaces();
        
        // on crée le writer KTX
        KTXWriter ktxWriter             = new KTXWriter(mipmapped, isCubemap, width, height);
        KTXMetadata ktxMetadata         = ktxWriter.getMetadata();
        KTXTextureData ktxTextureData   = ktxWriter.getTextureData();
        
        if (pvrReader.isCompressed()) {
            ktxWriter.setCompressedFormat(glFormat);
        } else {
            ktxWriter.setUncompressedFormat(glFormat);
        }
        
        // on ajoute les métadonnées
        if (null != metadata) {
            Set<String> set = metadata.keySet();
            Object obj;
            
            for (String key : set) {
                obj = metadata.get(key);
                
                if (obj instanceof String) {
                    ktxMetadata.set(key, (String) obj);
                } else if (obj instanceof byte[]) {
                    ktxMetadata.set(key, (byte[]) obj);
                }
            }
        }
        
        // on défini les données de textures
        byte mipmapLevel        = 0;
        byte face               = 0;
        
        for (mipmapLevel = 0; mipmapLevel < numberOfMipmap; mipmapLevel++) {
            for (face = 0; face < numberOfFaces; face++) {
                ktxTextureData.set(mipmapLevel, face, buffers[mipmapLevel][face]);
            }
        }
        
        // écriture du fichier KTX
        ktxWriter.write(out);
        
    }
}
