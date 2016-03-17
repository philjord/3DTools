package com.numericalactivity.dktxtools.ktx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.numericalactivity.dktxtools.TextureFormat;
import com.numericalactivity.dktxtools.utils.BufferUtils;
import com.numericalactivity.dktxtools.utils.TextureUtils;

/**
 * Classe de gestion des entêtes d'un fichier KTX
 */
public abstract class KTXHeader
{

	public static final int HEADER_LENGTH = 64; // taille de l'entête
	public static final int ENDIANNESS_OK = 0x04030201; // endianness qui correspond à celui du programme qui lit le fichier
	public static final int ENDIANNESS_OPPOSITE = 0x01020304; // endianness opposé
	public static final byte[] FILE_IDENTIFIER = new byte[] { // identifiant du type de fichier KTX
			(byte) 0xAB, 0x4B, 0x54, 0x58, 0x20, 0x31, 0x31, (byte) 0xBB, 0x0D, 0x0A, 0x1A, 0x0A };

	protected ByteOrder _byteOrder;
	protected boolean _byteOrderNative;
	protected int _glType;
	protected int _glTypeSize;
	protected int _glFormat;
	protected int _glInternalFormat;
	protected int _glBaseInternalFormat;
	protected int _pixelWidth;
	protected int _pixelHeight;
	protected int _pixelDepth;
	protected int _numberOfArrayElements;
	protected int _numberOfFaces = 1;
	protected int _numberOfMipmapLevels = 1;
	protected int _bytesOfKeyValueData;

	/*
	 * Getters
	 */
	public ByteOrder getByteOrder()
	{
		return _byteOrder;
	}

	public boolean isByteOrderNative()
	{
		return _byteOrderNative;
	}

	public int getGLType()
	{
		return _glType;
	}

	public int getGLTypeSize()
	{
		return _glTypeSize;
	}

	public int getGLFormat()
	{
		return _glFormat;
	}

	public int getGLInternalFormat()
	{
		return _glInternalFormat;
	}

	public int getGLBaseInternalFormat()
	{
		return _glBaseInternalFormat;
	}

	public int getPixelWidth()
	{
		return _pixelWidth;
	}

	public int getPixelHeight()
	{
		return _pixelHeight;
	}

	public int getPixelDepth()
	{
		return _pixelDepth;
	}

	public int getNumberOfArrayElements()
	{
		return _numberOfArrayElements;
	}

	public int getNumberOfFaces()
	{
		return _numberOfFaces;
	}

	public int getNumberOfMipmapLevels()
	{
		return _numberOfMipmapLevels;
	}

	public boolean getAutoGenerateMipmap()
	{
		return _numberOfMipmapLevels == 0;
	}

	public int getBytesOfKeyValueData()
	{
		return _bytesOfKeyValueData;
	}

	public boolean isCompressed()
	{
		return 0 == _glFormat;
	}

	public boolean isMipmapped()
	{
		return _numberOfMipmapLevels > 1;
	}

	public boolean isCubemap()
	{
		return _numberOfFaces > 1;
	}

	/*
	 * Setters
	 */
	public void setByteOrder(ByteOrder order)
	{
		_byteOrder = order;
		_byteOrderNative = (order == ByteOrder.nativeOrder());
	}

	public void setGLFormat(int glInternalFormat, int glBaseInternalFormat, int glFormat, int glType, int glTypeSize)
	{
		_glInternalFormat = glInternalFormat;
		_glBaseInternalFormat = glBaseInternalFormat;
		_glFormat = glFormat;
		_glType = glType;
		_glTypeSize = glTypeSize;
	}

	public void setCompressedGLFormat(int glInternalFormat, int glBaseInternalFormat)
	{
		setGLFormat(glInternalFormat, glBaseInternalFormat, 0, 0, 1);
	}

	public void setDimensions(int w, int h, int d)
	{
		_pixelWidth = w;
		_pixelHeight = h;
		_pixelDepth = d;
	}

	public void setNumberOfArrayElements(int numberOfArrayElements)
	{
		_numberOfArrayElements = numberOfArrayElements;
	}

	public void setNumberOfFaces(int numberOfFaces)
	{
		_numberOfFaces = (numberOfFaces == 0) ? 1 : numberOfFaces;
	}

	public void setNumberOfMipmapLevels(int numberOfMipmapLevels)
	{
		_numberOfMipmapLevels = (numberOfMipmapLevels == 0) ? 1 : numberOfMipmapLevels;
	}

	public void setBytesOfKeyValueData(int bytesOfKeyValueData)
	{
		_bytesOfKeyValueData = bytesOfKeyValueData;
	}

	public void setType(int type)
	{
		_glType = type;
	}

	public void setTypeSize(int typeSize)
	{
		_glTypeSize = typeSize;
	}

	@Override
	public String toString()
	{
		return String.format(
				"%s\n    glType=%d\n    glTypeSize=%d\n    glFormat=%d\n    glInternalFormat=%d\n    glBaseInternalFormat=%d\n    pixelWidth=%d\n    pixelHeight=%d\n    pixelDepth=%d\n    numberOfArrayElements=%d\n    numberOfFaces=%d\n    numberOfMipmapLevels=%d\n    bytesOfKeyValueData=%d",
				getClass().getCanonicalName(), _glType, _glTypeSize, _glFormat, _glInternalFormat, _glBaseInternalFormat, _pixelWidth,
				_pixelHeight, _pixelDepth, _numberOfArrayElements, _numberOfFaces, _numberOfMipmapLevels, _bytesOfKeyValueData);
	}

	/**
	 * Remet l'objet à son état d'origine
	 */
	public void reset()
	{
		_byteOrder = null;
		_byteOrderNative = false;
		_glType = 0;
		_glTypeSize = 0;
		_glFormat = 0;
		_glInternalFormat = 0;
		_glBaseInternalFormat = 0;
		_pixelWidth = 0;
		_pixelHeight = 0;
		_pixelDepth = 0;
		_numberOfArrayElements = 0;
		_numberOfFaces = 1;
		_numberOfMipmapLevels = 1;
		_bytesOfKeyValueData = 0;
	}

	/**
	 * Une classe qui permet de lire les entêtes d'un fichier KTX
	 */
	public static class Reader extends KTXHeader
	{
		/**
		 * Constructeur
		 */
		public Reader()
		{
		}

		/**
		 * Constructeur.
		 * Lit les entêtes du fichier.
		 * @param in le pointeur doit être placé au début du fichier
		 * @throws IOException 
		 * @throws KTXFormatException 
		 */
		public Reader(BufferedInputStream in) throws IOException, KTXFormatException
		{
			read(in);
		}

		/**
		 * Constructeur.
		 * Lit les entêtes du fichier.
		 * @param buffer buffer contenant les données des entêtes. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
		 * @throws KTXFormatException 
		 */
		public Reader(ByteBuffer buffer) throws KTXFormatException
		{
			read(buffer);
		}

		/**
		 * Lit les entêtes du fichier
		 * @param in le pointeur doit être placé au début du fichier
		 * @throws IOException 
		 * @throws KTXFormatException 
		 */
		void read(InputStream in) throws IOException, KTXFormatException
		{
			ByteBuffer buffer = BufferUtils.getEmptyByteBuffer(HEADER_LENGTH);
			byte[] data = new byte[HEADER_LENGTH];
			in.read(data, 0, HEADER_LENGTH);
			buffer.put(data);
			buffer.position(0);
			read(buffer);
		}

		/**
		 * Lit les entêtes du fichier
		 * @param buffer buffer contenant les données des entêtes. Les données doivent être placées au début du buffer, ou la position du buffer doit être définie au début des données des entêtes. D'autres données peuvent être présentes à la suite des entêtes sans incidence.
		 * @throws KTXFormatException 
		 */
		void read(ByteBuffer buffer) throws KTXFormatException
		{
			// on garde en mémoire l'ordre actuel du ByteBuffer
			ByteOrder oldOrder = buffer.order();

			// on défini l'ordre du buffer à l'ordre natif
			buffer.order(ByteOrder.nativeOrder());

			// on vérifie que l'identifiant du fichier correspond
			byte[] fileIdentifier = new byte[FILE_IDENTIFIER.length];
			buffer.get(fileIdentifier);

			if (!Arrays.equals(fileIdentifier, FILE_IDENTIFIER))
			{
				throw new KTXFormatException("Input doesn't start with KTX file identifier");
			}

			// on vérifie l'endianess et modifie le sens du buffer si nécessaire
			int endianness = buffer.getInt();
			_byteOrder = buffer.order();

			if (endianness == ENDIANNESS_OK)
			{
				// endianness natif
				_byteOrderNative = true;
			}
			else if (endianness == ENDIANNESS_OPPOSITE)
			{
				// endianness inversé
				_byteOrderNative = false;
				_byteOrder = (ByteOrder.BIG_ENDIAN == _byteOrder) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
				buffer.order(_byteOrder);
			}
			else
			{
				throw new KTXFormatException(String.format("Endianness field has an unexpected value: %08x", endianness));
			}

			// on récupère les entêtes
			_glType = buffer.getInt();
			_glTypeSize = buffer.getInt();
			_glFormat = buffer.getInt();
			_glInternalFormat = buffer.getInt();
			_glBaseInternalFormat = buffer.getInt();
			_pixelWidth = buffer.getInt();
			_pixelHeight = buffer.getInt();
			_pixelDepth = buffer.getInt();
			_numberOfArrayElements = buffer.getInt();
			_numberOfFaces = buffer.getInt();
			_numberOfMipmapLevels = buffer.getInt();
			_bytesOfKeyValueData = buffer.getInt();

			// on contrôle la valeur de glTypeSize
			if (_glTypeSize != 1 && _glTypeSize != 2 && _glTypeSize != 4)
			{
				throw new KTXFormatException("glTypeSize not supported: " + String.valueOf(_glTypeSize));
			}

			// on remet le buffer à son ordre d'origine
			buffer.order(oldOrder);
		}
	}

	/**
	 * Une classe qui permet d'écrire des entêtes de fichier KTX dans un flux
	 */
	public static class Writer extends KTXHeader
	{
		KTXMetadata _metadata;

		/**
		 * Initialise les entêtes avec des valeurs par défaut
		 */
		protected Writer(KTXMetadata metadata)
		{
			_metadata = metadata;
		}

		/**
		 * Défini le nombre de faces et le nombre de niveaux mipmap depuis un objet KTXTextureData
		 * @param textureData
		 */
		public void setFromTextureData(KTXTextureData textureData)
		{
			_numberOfFaces = textureData._numberOfFaces;
			_numberOfMipmapLevels = textureData._numberOfMipmapLevels;
		}

		/**
		 * Défini l'objet KTXMetadata qui sera utilisé pour calculer la taille des métadonnées
		 * @param metadata
		 */
		public void setMetadata(KTXMetadata metadata)
		{
			_metadata = metadata;
		}

		/**
		 * Contrôle l'integrité des données qui vont être insérées
		 * @return
		 * @throws KTXFormatException
		 */
		protected void check() throws KTXFormatException
		{
			// on vérifie le nombre de mipmaps en fonction de la hauteur et de la largeur
			byte numberOfMipmaps = TextureUtils.getNumberOfMipmaps(_pixelWidth, _pixelHeight);

			if (_numberOfMipmapLevels > 1 && numberOfMipmaps != _numberOfMipmapLevels)
			{
				throw new KTXFormatException(String.valueOf(numberOfMipmaps) + " mipmap levels must be set, but "
						+ String.valueOf(_numberOfMipmapLevels) + " are set");
			}

			// on vérifie qu'il y a bien 1 ou 6 faces
			if (1 != _numberOfFaces && 6 != _numberOfFaces)
			{
				throw new KTXFormatException("Either 1 or 6 faces must be defined, but " + String.valueOf(_numberOfFaces) + " are defined");
			}

			// on vérifie que numberOfArrayElements vaut 0
			if (0 != _numberOfArrayElements)
			{
				throw new KTXFormatException("Texture arrays are not supported");
			}

			// typeSize doit être 1, 2 ou 4
			if (_glTypeSize > 0 && (1 != _glTypeSize && 2 != _glTypeSize && 4 != _glTypeSize))
			{
				throw new KTXFormatException(
						"glTypeSize must be either 1, 2 or 4 for non compressed textures. " + String.valueOf(_glTypeSize) + " given");
			}

			// on contrôle la valeur de glType
			if (_glType > 0 && TextureFormat.GL_UNSIGNED_BYTE != _glType)
			{
				throw new KTXFormatException("glType must be 0x" + Integer.toHexString(TextureFormat.GL_UNSIGNED_BYTE)
						+ " (TextureFormat.GL_UNSIGNED_BYTE) for nno compressed textures");
			}

			// on contrôle la valeur de format
			if (_glType > 0 && (TextureFormat.GL_ALPHA != _glFormat && TextureFormat.GL_LUMINANCE != _glFormat
					&& TextureFormat.GL_LUMINANCE_ALPHA != _glFormat && TextureFormat.GL_RGB != _glFormat
					&& TextureFormat.GL_RGBA != _glFormat))
			{
				throw new KTXFormatException("Invalid glFormat value 0x" + Integer.toHexString(_glFormat));
			}

			// on vérifie que glType et glFormat valent tous deux 0 ou ont tous deux une valeur supérieure à 0
			if ((0 == _glType && _glFormat > 0) || (_glType > 0 && 0 == _glFormat))
			{
				throw new KTXFormatException(
						"Both glType and glFormat must equal 0 for non compressed textures, and both must not equal 0 for compressed textures");
			}

			// on contrôle la valeur de internalFormat
			if (0 == _glType && (TextureFormat.GL_ATC_RGB_AMD != _glInternalFormat
					&& TextureFormat.GL_ATC_RGBA_EXPLICIT_ALPHA_AMD != _glInternalFormat
					&& TextureFormat.GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD != _glInternalFormat
					&& TextureFormat.GL_ETC1_RGB8 != _glInternalFormat && TextureFormat.GL_COMPRESSED_RGB_S3TC_DXT1_EXT != _glInternalFormat
					&& TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT != _glInternalFormat
					&& TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT != _glInternalFormat
					&& TextureFormat.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT != _glInternalFormat
					&& TextureFormat.GL_COMPRESSED_RGB_PVRTC_4BPPV1_IMG != _glInternalFormat
					&& TextureFormat.GL_COMPRESSED_RGB_PVRTC_2BPPV1_IMG != _glInternalFormat
					&& TextureFormat.GL_COMPRESSED_RGBA_PVRTC_4BPPV1_IMG != _glInternalFormat
					&& TextureFormat.GL_COMPRESSED_RGBA_PVRTC_2BPPV1_IMG != _glInternalFormat))
			{
				// textures compressées
				throw new KTXFormatException(
						"Invalid glInternalFormat value (compressed texture) 0x" + Integer.toHexString(_glInternalFormat));
			}
			else if (0 != _glType && (TextureFormat.GL_ALPHA8 != _glInternalFormat && TextureFormat.GL_LUMINANCE8 != _glInternalFormat
					&& TextureFormat.GL_LUMINANCE8_ALPHA8 != _glInternalFormat && TextureFormat.GL_RGB8 != _glInternalFormat
					&& TextureFormat.GL_RGBA8 != _glInternalFormat))
			{
				// textures non compressées
				throw new KTXFormatException(
						"Invalid glInternalFormat value (non compressed texture) 0x" + Integer.toHexString(_glInternalFormat));
			}

			// on contrôle la valeur de baseInternalFormat
			if (TextureFormat.GL_ALPHA != _glBaseInternalFormat && TextureFormat.GL_LUMINANCE != _glBaseInternalFormat
					&& TextureFormat.GL_LUMINANCE_ALPHA != _glBaseInternalFormat && TextureFormat.GL_RGB != _glBaseInternalFormat
					&& TextureFormat.GL_RGBA != _glBaseInternalFormat)
			{
				throw new KTXFormatException("Invalid glBaseInternalFormat value 0x" + Integer.toHexString(_glBaseInternalFormat));
			}
		}

		/**
		 * Écrit les entêtes dans un flux
		 * @param out
		 * @throws IOException
		 * @throws KTXFormatException 
		 */
		public void write(BufferedOutputStream out) throws IOException, KTXFormatException
		{
			ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH);
			write(buffer);
			out.write(buffer.array());
		}

		/**
		 * Rempli un ByteBuffer avec les entêtes définies
		 * @param buffer
		 * @throws KTXFormatException 
		 */
		public void write(ByteBuffer buffer) throws KTXFormatException
		{
			check();

			// on garde en mémoire l'ordre actuel du ByteBuffer
			ByteOrder oldOrder = buffer.order();

			// on replace le buffer à la position 0
			buffer.position(0);

			// on défini l'ordre du buffer à celui natif
			buffer.order(ByteOrder.nativeOrder());

			// on récupère la taille des métadonnées
			_bytesOfKeyValueData = _metadata.getBytesOfKeyValueData();

			// on ajoute les entêtes
			buffer.put(FILE_IDENTIFIER);
			buffer.putInt(ENDIANNESS_OK);

			buffer.putInt(_glType);
			buffer.putInt(_glTypeSize);
			buffer.putInt(_glFormat);
			buffer.putInt(_glInternalFormat);
			buffer.putInt(_glBaseInternalFormat);
			buffer.putInt(_pixelWidth);
			buffer.putInt(_pixelHeight);
			buffer.putInt(_pixelDepth);
			buffer.putInt(_numberOfArrayElements);
			buffer.putInt(_numberOfFaces);
			buffer.putInt(_numberOfMipmapLevels);
			buffer.putInt(_bytesOfKeyValueData);

			// on remet le buffer à son ordre d'origine
			buffer.order(oldOrder);
		}
	}

}
