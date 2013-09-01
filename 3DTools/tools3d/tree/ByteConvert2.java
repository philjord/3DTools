package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ByteConvert2
{

	public static boolean toBool(byte[] b)
	{
		return b[0] != 0;
	}

	public static int toInt(byte[] b)
	{
		return (((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[0] & 0xff));
	}

	public static float toFloat(byte[] b)
	{
		return Float.intBitsToFloat(ByteConvert2.toInt(b));
	}

	public static short toShort(byte[] b)
	{
		return (short) (((b[1] & 0xff) << 8) | (b[0] & 0xff));
	}

	public static byte readByte(InputStream stream) throws IOException
	{
		byte b[] = new byte[1];
		stream.read(b, 0, 1);
		return b[0];
	}

	public static byte[] readBytes(int numBytes, InputStream stream) throws IOException
	{
		byte b[] = new byte[numBytes];
		stream.read(b, 0, numBytes);
		return b;
	}

	public static boolean readBool(InputStream stream) throws IOException
	{
		//TODO: note this only supprot the 20.0.0.5 version of bools, older ones are different
		/*	if ( version <= 0x04010001 ) {
		//Bools are stored as integers before version 4.1.0.1
		return (ReadUInt( in ) != 0);
		} else {
		//And as bytes from 4.1.0.1 on
		return (ReadByte( in ) != 0);
		}     */
		byte b[] = new byte[1];
		stream.read(b, 0, 1);
		return ByteConvert2.toBool(b);
	}

	public static int readInt(InputStream stream) throws IOException
	{
		byte b[] = new byte[4];
		stream.read(b, 0, 4);
		return ByteConvert2.toInt(b);
	}

	public static int[] readInts(int numInts, InputStream stream) throws IOException
	{
		int[] is = new int[numInts];
		for (int i = 0; i < numInts; i++)
		{
			is[i] = readInt(stream);
		}
		return is;
	}

	public static short readShort(InputStream stream) throws IOException
	{
		byte b[] = new byte[2];
		stream.read(b, 0, 2);
		return ByteConvert2.toShort(b);
	}

	public static short[] readShorts(int numShorts, InputStream stream) throws IOException
	{
		short[] ss = new short[numShorts];
		for (int i = 0; i < numShorts; i++)
		{
			ss[i] = readShort(stream);
		}
		return ss;
	}

	public static float readFloat(InputStream stream) throws IOException
	{
		byte b[] = new byte[4];
		stream.read(b, 0, 4);
		return ByteConvert2.toFloat(b);
	}

	public static float[] readFloats(int numFloats, InputStream stream) throws IOException
	{
		float[] fs = new float[numFloats];
		for (int i = 0; i < numFloats; i++)
		{
			fs[i] = readFloat(stream);
		}
		return fs;
	}

	public static String readString(InputStream stream) throws IOException
	{
		int len = readInt(stream);
		byte[] buffer = new byte[len];
		stream.read(buffer, 0, len);
		return new String(buffer);
	}

	public static String readShortString(InputStream stream) throws IOException
	{
		byte len = readByte(stream);
		byte[] buffer = new byte[len];
		stream.read(buffer, 0, len);
		return new String(buffer);
	}

}