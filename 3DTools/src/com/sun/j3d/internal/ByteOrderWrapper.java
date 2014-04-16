package com.sun.j3d.internal;

import java.nio.ByteOrder;

//COPY of j3d org code for use by my copy of GeometryInfo
public class ByteOrderWrapper
{

	private final String enum_name;

	private ByteOrderWrapper(String name)
	{
		enum_name = name;
	}

	public static final ByteOrderWrapper BIG_ENDIAN = new ByteOrderWrapper("BIG_ENDIAN");

	public static final ByteOrderWrapper LITTLE_ENDIAN = new ByteOrderWrapper("LITTLE_ENDIAN");

	public String toString()
	{
		return enum_name;
	}

	public static ByteOrder nativeOrder()
	{
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
		{
			return ByteOrder.BIG_ENDIAN;
		}
		else
			return ByteOrder.LITTLE_ENDIAN;
	}

}
