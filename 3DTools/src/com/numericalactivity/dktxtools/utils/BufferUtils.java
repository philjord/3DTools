package com.numericalactivity.dktxtools.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.numericalactivity.dktxtools.TextureFormat;

/**
 * Utilitaire pour les buffers
 */
public class BufferUtils {
    protected BufferUtils() {}

    /**
     * Converti un tableau de float en FloatBuffer
     * @param array
     * @return
     */
    public static FloatBuffer getFloatBuffer(float[] array) {
        FloatBuffer buffer = ByteBuffer
            .allocateDirect(array.length * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        ;
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    /**
     * Converti un tableau de short en ShortBuffer
     * @param array
     * @return
     */
    public static ShortBuffer getShortBuffer(short[] array) {
        ShortBuffer buffer = ByteBuffer
            .allocateDirect(array.length * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        ;
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    /**
     * Converti un tableau de byte en ByteBuffer
     * @param array
     * @return
     */
    public static ByteBuffer getByteBuffer(byte[] array) {
        ByteBuffer buffer = ByteBuffer
            .allocateDirect(array.length)
            .order(ByteOrder.nativeOrder())
        ;
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    /**
     * Converti un tableau de short en ByteBuffer
     * @param array
     * @return
     */
    public static ByteBuffer getByteBuffer(short[] array) {
        ByteBuffer byteBuffer   = ByteBuffer
            .allocateDirect(array.length * 2)
            .order(ByteOrder.nativeOrder())
        ;
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(array);
        shortBuffer.position(0);
        byteBuffer.position(0);
        return byteBuffer;
    }

    /**
     * Converti un tableau d'integer en ByteBuffer
     * @param array
     * @return
     */
    public static ByteBuffer getByteBuffer(int[] array) {
        ByteBuffer byteBuffer   = ByteBuffer
            .allocateDirect(array.length * 4)
            .order(ByteOrder.nativeOrder())
        ;
        IntBuffer intBuffer     = byteBuffer.asIntBuffer();
        intBuffer.put(array);
        intBuffer.position(0);
        byteBuffer.position(0);
        return byteBuffer;
    }

    /**
     * Converti un tableau de float en ByteBuffer
     * @param array
     * @return
     */
    public static ByteBuffer getByteBuffer(float[] array) {
        ByteBuffer byteBuffer   = ByteBuffer
            .allocateDirect(array.length * 4)
            .order(ByteOrder.nativeOrder())
        ;
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(array);
        floatBuffer.position(0);
        byteBuffer.position(0);
        return byteBuffer;
    }

    /**
     * Créer un ByteBuffer et alloue 'size' bytes
     * @param size
     * @return
     */
    public static ByteBuffer getEmptyByteBuffer(int size) {
        ByteBuffer buffer = ByteBuffer
            .allocateDirect(size)
            .order(ByteOrder.nativeOrder())
        ;
        buffer.position(0);
        return buffer;
    }

    /**
     * Converti un tableau de pixels en ByteBuffer.
     * Les pixels sont converti en tableau de byte, où chaque byte correspond à une composante.
     * @param pixels tableau de pixels au format RGBA
     * @param format une des constante TextureFormat.GL_* (formats non compressés)
     * @return tableau de byte dans lequel chaque byte correspond à une composante au format 'format'
     */
    public static ByteBuffer getPixelBuffer(int[] pixels, int format) {
        byte[] newPixels    = null;
        int i               = 0;

        switch (format) {
            case TextureFormat.GL_ALPHA:
            case TextureFormat.GL_LUMINANCE:
                newPixels   = new byte[pixels.length];

                for (int pixel : pixels) {
                    newPixels[i]  = (byte) ((pixel >> 16) & 0xFF); // red
                    i++;
                }
                break;

            case TextureFormat.GL_LUMINANCE_ALPHA:
                newPixels   = new byte[pixels.length * 2];

                for (int pixel : pixels) {
                    newPixels[(i * 2) + 0]  = (byte) ((pixel >> 16) & 0xFF); // red
                    newPixels[(i * 2) + 1]  = (byte) ((pixel >> 24) & 0xFF); // alpha
                    i++;
                }
                break;

                // TODO tester
            case TextureFormat.GL_RGB:
                newPixels   = new byte[pixels.length * 3];
                
                for (int pixel : pixels) {
                    newPixels[(i * 3) + 0]  = (byte) ((pixel >> 16) & 0xFF); // red
                    newPixels[(i * 3) + 1]  = (byte) ((pixel >> 8) & 0xFF); // green
                    newPixels[(i * 3) + 2]  = (byte) (pixel & 0xFF); // blue
                    i++;
                }
                break;

            case TextureFormat.GL_RGBA:
            default:
                newPixels   = new byte[pixels.length * 4];

                for (int pixel : pixels) {
                    newPixels[(i * 4) + 0]  = (byte) ((pixel >> 16) & 0xFF); // red
                    newPixels[(i * 4) + 1]  = (byte) ((pixel >> 8) & 0xFF); // green
                    newPixels[(i * 4) + 2]  = (byte) (pixel & 0xFF); // blue
                    newPixels[(i * 4) + 3]  = (byte) ((pixel >> 24) & 0xFF); // alpha
                    i++;
                }
        }

        return BufferUtils.getByteBuffer(newPixels);
    }
}
