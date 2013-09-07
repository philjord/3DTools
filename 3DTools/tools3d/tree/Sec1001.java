package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Sec1001
{
	public Sec1001(InputStream in) throws IOException
	{
		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 22000 && id != 0)
		{
			if (id == 8000)
			{
			}
			else if (id == 8002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8005)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8006)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8007)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8008)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8009)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 8001)
			{
			}

			if (id == 9000)
			{
			}
			else if (id == 9002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9005)
			{
			}
			else if (id == 9006)
			{
			}
			else if (id == 9007)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9008)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9009)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9010)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9011)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9012)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9013)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9014)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 9001)
			{
			}

			if (id == 11000)
			{
			}
			else if (id == 11002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 11001)
			{
			}

			if (id == 10000)
			{
			}
			else if (id == 10002)
			{
				int count = LittleEndianPrimitiveBytes.readInt(in);
				for (int i = 0; i < count * 8; i++)
					LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 10003)
			{
				for (int i = 0; i < 9; i++)
					LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 10004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 10001)
			{

			}

			if (id == 13000)
			{
			}
			else if (id == 13002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13005)
			{
				for (int i = 0; i < 27; i++)
					LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13006)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13007)
			{
				LittleEndianPrimitiveBytes.readByte(in);
			}
			else if (id == 13008)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13009)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13010)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13011)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13012)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13013)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 13001)
			{

			}

			if (id == 14000)
			{
			}
			else if (id == 14002)
			{
				LittleEndianPrimitiveBytes.readSizedString(in);
			}
			else if (id == 14003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 14004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 14005)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 14006)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 14007)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 14008)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 14001)
			{

			}

			if (id == 15000)
			{
			}
			else if (id == 15002)
			{
				LittleEndianPrimitiveBytes.readByte(in);
			}
			else if (id == 15003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 15001)
			{

			}

			if (id == 16000)
			{
			}
			else if (id == 16002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16005)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16006)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16007)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16008)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16009)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16010)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16011)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16012)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16013)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 16014)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}

			else if (id == 16001)
			{

			}

			if (id == 18000)
			{
			}
			else if (id == 18002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 18003)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 18004)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 18005)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 18001)
			{

			}

			if (id == 19000)
			{
			}
			else if (id == 19002)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 19001)
			{
			}

			if (id == 20000)
			{
			}
			else if (id == 20002)
			{
				byte count = LittleEndianPrimitiveBytes.readByte(in);

				for (int i = 0; i < count; i++)
					LittleEndianPrimitiveBytes.readByte(in);

				LittleEndianPrimitiveBytes.readByte(in);
				LittleEndianPrimitiveBytes.readByte(in);
				LittleEndianPrimitiveBytes.readByte(in);
			}
			else if (id == 20003)
			{
				LittleEndianPrimitiveBytes.readByte(in);
			}
			else if (id == 20004)
			{
				LittleEndianPrimitiveBytes.readByte(in);
			}
			else if (id == 20005)
			{
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 20001)
			{
			}

			if (id == 21000)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}
			else if (id == 21001)
			{
				LittleEndianPrimitiveBytes.readInt(in);
			}

			id = LittleEndianPrimitiveBytes.readInt(in);
		}
	}
}
