package com.numericalactivity.dktxtools.ktx;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.numericalactivity.dktxtools.utils.BufferUtils;

/**
 * Classe de gestion des métadata d'un fichier KTX
 */
public abstract class KTXMetadata implements Iterable<Entry<String, byte[]>> {

    final Map<String, byte[]> _meta = new HashMap<String, byte[]>(); // liste des métadata du fichier
    int _bytesOfKeyValueData; // taille des metadata

    /**
     * Retourne le nombre de byte nécessaire pour stocker les métadata
     * @return
     */
    public int getBytesOfKeyValueData() {
        // si aucune donnée n'a été ajoutée depuis le dernier calcule il est inutile de recalculer
        if (0 != _bytesOfKeyValueData) {
            return _bytesOfKeyValueData;
        }

        // si aucune métadonnée n'est insérée on peut renvoyer 0 tout de suite
        if (0 == _meta.size()) {
            return 0;
        }

        // on initialise les variables
        byte[] keyBytes;
        byte[] value;
        String key;
        byte padding;
        int keyAndValueByteSize;
        Set<Entry<String, byte[]>> entrySet = _meta.entrySet();
        int bytes                           = 0;

        // on itère sur chaque entrée
        for (Entry<String, byte[]> entry : entrySet) {
            key     = entry.getKey();
            value   = entry.getValue();

            try {
                keyBytes = key.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                keyBytes = key.getBytes();
            }

            keyAndValueByteSize = keyBytes.length + 1 + value.length;
            padding             = KTXUtil.align4(keyAndValueByteSize);
            bytes              += keyAndValueByteSize + padding + 4;
        }

        bytes += KTXUtil.align4(bytes);
        return bytes;
    }

    /**
     * Transforme une valeur en String
     * @param value valeur d'un métadata
     * @return
     */
    protected String valueAsString(byte[] value) {
        String string;
        int length = value.length;

        // le caractère de fin est censé être un caractère NULL. Si c'est le cas il doit être exclus.
        if (0 == value[length - 1]) {
            length -= 1;
        }

        // on transforme le tableau en String
        try {
            string = new String(value, 0, length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            string = new String(value, 0, length);
        }

        return string;
    }

    /**
     * Retourne la liste des clés contenus dans les métadata
     * @return
     */
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(_meta.keySet());
    }

    /**
     * Retourne la valeur associée à la clé 'key'
     * @param key
     * @return
     */
    public byte[] get(String key) {
        return _meta.get(key);
    }

    /**
     * Retourne la valeur associée à la clé 'key' après l'avoir transformée en String
     * @param key
     * @return
     */
    public String getString(String key) {
        return valueAsString(_meta.get(key));
    }

    /**
     * Défini une valeur
     * @param key clé à associer à la valeur
     * @param value valeur
     */
    public void set(String key, byte[] value) {
        _meta.put(key, value);
        _bytesOfKeyValueData = 0;
    }

    /**
     * Défini une valeur
     * @param key clé à associer à la valeur
     * @param value valeur qui sera transformée en tableau de byte et un caractère NULL sera ajouté à la fin
     */
    public void set(String key, String value) {
        byte[] valueBytes;
        char[] nullChar = {0x0};
        value           = value.concat(new String(nullChar));

        try {
            valueBytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            valueBytes = value.getBytes();
        }

        _meta.put(key, valueBytes);
    }

    @Override
    public Iterator<Entry<String, byte[]>> iterator() {
        return _meta.entrySet().iterator();
    }

    @Override
    public String toString() {
        Set<String> keys    = getKeys();
        StringBuilder sb    = new StringBuilder(getClass().getCanonicalName());

        for (String key : keys) {
            sb.append("\n    ");
            sb.append(key);
            sb.append(": ");
            sb.append(getString(key));
        }

        return sb.toString();
    }

    /**
     * Remet l'objet à son état d'origine
     */
    public void reset() {
        _meta.clear();
        _bytesOfKeyValueData = 0;
    }

    /**
     * Une classe qui permet de lire les métadata d'un fichier KTX
     */
    public static class Reader extends KTXMetadata {

        /**
         * Constructeur
         */
        Reader() {
        }

        /**
         * Constructeur.
         * Lit les métadata du fichier.
         * @param in le pointeur doit être placé au début des métadata
         * @param ktxHeader headers du fichier
         * @throws KTXFormatException
         * @throws IOException
         */
        Reader(BufferedInputStream in, KTXHeader ktxHeader) throws KTXFormatException, IOException {
            read(in, ktxHeader);
        }

        /**
         * Constructeur.
         * Lit les métadata du fichier.
         * @param buffer buffer contenant les données des métadata. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des métadata. D'autres données peuvent être présentes à la suite des métadata sans incidence.
         * @param ktxHeader headers du fichier
         * @throws UnsupportedEncodingException
         * @throws KTXFormatException
         */
        Reader(ByteBuffer buffer, KTXHeader ktxHeader) throws UnsupportedEncodingException, KTXFormatException {
            // le ByteBuffer doit soit ne contenir que les metadata, soit avoir sa position au début des metadata
            read(buffer, ktxHeader);
        }

        /**
         * Lit les métadata du fichier
         * @param in le pointeur doit être placé au début des métadata
         * @param ktxHeader headers du fichier
         * @throws KTXFormatException
         * @throws IOException
         */
        void read(BufferedInputStream in, KTXHeader ktxHeader) throws KTXFormatException, IOException {
            // attention à la position du pointeur de l'input stream!
            int length          = ktxHeader._bytesOfKeyValueData;
            ByteBuffer buffer   = BufferUtils.getEmptyByteBuffer(length);
            byte[] data         = new byte[length];
            in.read(data, 0, length);
            buffer.put(data);
            buffer.position(0);
            read(buffer, ktxHeader);
        }

        /**
         * Lit les métadata du fichier
         * @param buffer buffer contenant les données des métadata. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des métadata. D'autres données peuvent être présentes à la suite des métadata sans incidence.
         * @param ktxHeader headers du fichier
         * @throws UnsupportedEncodingException
         * @throws KTXFormatException
         */
        void read(ByteBuffer in, KTXHeader ktxHeader) throws KTXFormatException {
            // on détermine la vraie taille des données, qui doit être alignée sur 4 byte
            int length          = ktxHeader._bytesOfKeyValueData;
            
            ByteBuffer buffer = in.slice();

            // on redéfini l'ordre du ByteBuffer et sa limite
            buffer.order(ktxHeader._byteOrder);
            buffer.limit(length);

            // on réinitialise la liste des métadata
            _meta.clear();

            // on initialise les variables
            byte b;
            int i;
            int keyBytes;
            int keyAndValueByteSize;
            byte padding;
            String key;
            byte[] val;
            byte[] temp = new byte[length];

            while (buffer.remaining() >= 4) {
                // on récupère la taille de l'entrée
                keyAndValueByteSize = buffer.getInt();

                // on lit la clé
                keyBytes            = 0;
                b                   = 0;

                while ((b = buffer.get()) != 0) {
                    temp[keyBytes++] = b;
                }

                try {
                    key = new String(temp, 0, keyBytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    key = new String(temp, 0, keyBytes);
                }

                // on lit la valeur
                val                 = new byte[keyAndValueByteSize - keyBytes - 1];
                buffer.get(val);

                // padding
                padding             = KTXUtil.align4(keyAndValueByteSize);

                for (i = 0; i < padding; i++) {
                    buffer.get();
                }

                // on insère la valeur dans la liste des metadata
                _meta.put(key, val);
            }
        }
    }

    /**
     * Une classe qui permet d'écrire des métadata de fichier KTX dans un flux ou un ByteBuffer
     */
    public static class Writer extends KTXMetadata {

        /**
         * Initialise la liste des métadata
         */
        protected Writer() {
        }

        /**
         * Écrit les métadata dans un flux
         * @param out
         * @throws IOException
         */
        public void write(BufferedOutputStream out) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(getBytesOfKeyValueData());
            write(buffer);
            out.write(buffer.array());
        }

        /**
         * Rempli un ByteBuffer avec les métadata définies
         * @param buffer
         */
        public void write(ByteBuffer buffer) {
            String key;
            byte[] keyBytes;
            byte[] value;
            int keyAndValueByteSize;
            byte padding;
            byte i;
            Set<Entry<String, byte[]>> entrySet = _meta.entrySet();
            buffer.order(ByteOrder.nativeOrder());

            // on itère sur chaque entrée
            for (Entry<String, byte[]> entry : entrySet) {
                key     = entry.getKey();
                value   = entry.getValue();

                try {
                    keyBytes = key.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    keyBytes = key.getBytes();
                }

                keyAndValueByteSize = keyBytes.length + 1 + value.length;
                padding             = KTXUtil.align4(keyAndValueByteSize);
                buffer.putInt(keyAndValueByteSize);
                buffer.put(keyBytes);
                buffer.put((byte) 0);
                buffer.put(value);

                for (i = 0; i < padding; i++) {
                    buffer.put((byte) 0);
                }
            }
        }
    }

}
