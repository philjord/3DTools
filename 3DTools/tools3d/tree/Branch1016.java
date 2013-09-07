package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Branch1016
{
	public BezierSpline bezierSpline6000;

	public BezierSpline bezierSpline6001;

	public BezierSpline bezierSpline6002;

	public BezierSpline bezierSpline6003;

	public BezierSpline bezierSpline6004;

	public BezierSpline bezierSpline6005;

	public BezierSpline bezierSpline6006;

	public BezierSpline bezierSpline6007;

	public int int6008; // ints between 1 and 42

	public int int6009; // ints between 1 and 42

	public int int6010;

	public int int6011;

	public int int6012;

	public int int6013;

	public int int6014;

	public byte byte6015; // these appear to be booleans 0 or 1 in all cases

	public byte byte6016; // these appear to be booleans 0 or 1 in all cases

	public BezierSpline bezierSpline6017;

	public Branch1016(InputStream in) throws IOException
	{
		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 1017)
		{
			if (id == 6000)
				bezierSpline6000 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6001)
				bezierSpline6001 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6002)
				bezierSpline6002 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6003)
				bezierSpline6003 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6004)
				bezierSpline6004 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6005)
				bezierSpline6005 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6006)
				bezierSpline6006 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6007)
				bezierSpline6007 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));
			else if (id == 6008)
				int6008 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6009)
				int6009 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6010)
				int6010 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6011)
				int6011 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6012)
				int6012 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6008)
				int6008 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6013)
				int6013 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6014)
				int6014 = LittleEndianPrimitiveBytes.readInt(in);
			else if (id == 6015)
				byte6015 = LittleEndianPrimitiveBytes.readByte(in);
			else if (id == 6016)
				byte6016 = LittleEndianPrimitiveBytes.readByte(in);
			else if (id == 6017)
				bezierSpline6017 = new BezierSpline(LittleEndianPrimitiveBytes.readSizedString(in));

			id = LittleEndianPrimitiveBytes.readInt(in);
		}

	}
}
