package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

public class Sec1001
{
	public Sec1001(InputStream in) throws IOException
	{
		int id = ByteConvert2.readInt(in);
		while (id != 22000 && id != 0)
		{
			if (id == 8000)
			{
			}
			else if (id == 8002)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 8003)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 8004)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 8005)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 8006)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 8007)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 8008)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 8009)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 8001)
			{
			}

			if (id == 9000)
			{
			}
			else if (id == 9002)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9003)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9004)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9005)
			{
			}
			else if (id == 9006)
			{
			}
			else if (id == 9007)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9008)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9009)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9010)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9011)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9012)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9013)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9014)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 9001)
			{
			}

			if (id == 11000)
			{
			}
			else if (id == 11002)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 11001)
			{
			}

			if (id == 10000)
			{
			}
			else if (id == 10002)
			{
				int count = ByteConvert2.readInt(in);
				for (int i = 0; i < count * 8; i++)
					ByteConvert2.readInt(in);
			}
			else if (id == 10003)
			{
				for (int i = 0; i < 9; i++)
					ByteConvert2.readInt(in);
			}
			else if (id == 10004)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 10001)
			{

			}

			if (id == 13000)
			{
			}
			else if (id == 13002)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13003)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13004)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13005)
			{
				for (int i = 0; i < 27; i++)
					ByteConvert2.readInt(in);
			}
			else if (id == 13006)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13007)
			{
				ByteConvert2.readByte(in);
			}
			else if (id == 13008)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13009)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13010)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13011)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13012)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13013)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 13001)
			{

			}

			if (id == 14000)
			{
			}
			else if (id == 14002)
			{
				ByteConvert2.readString(in);
			}
			else if (id == 14003)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 14004)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 14005)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 14006)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 14007)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 14008)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 14001)
			{

			}

			if (id == 15000)
			{
			}
			else if (id == 15002)
			{
				ByteConvert2.readByte(in);
			}
			else if (id == 15003)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 15001)
			{

			}

			if (id == 16000)
			{
			}
			else if (id == 16002)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16003)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16004)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16005)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16006)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16007)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16008)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16009)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16010)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16011)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16012)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16013)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 16014)
			{
				ByteConvert2.readInt(in);
			}

			else if (id == 16001)
			{

			}

			if (id == 18000)
			{
			}
			else if (id == 18002)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 18003)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 18004)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 18005)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 18001)
			{

			}

			if (id == 19000)
			{
			}
			else if (id == 19002)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 19001)
			{
			}

			if (id == 20000)
			{
			}
			else if (id == 20002)
			{
				byte count = ByteConvert2.readByte(in);

				for (int i = 0; i < count; i++)
					ByteConvert2.readByte(in);

				ByteConvert2.readByte(in);
				ByteConvert2.readByte(in);
				ByteConvert2.readByte(in);
			}
			else if (id == 20003)
			{
				ByteConvert2.readByte(in);
			}
			else if (id == 20004)
			{
				ByteConvert2.readByte(in);
			}
			else if (id == 20005)
			{
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
				ByteConvert2.readInt(in);
			}
			else if (id == 20001)
			{
			}

			if (id == 21000)
			{
				ByteConvert2.readInt(in);
			}
			else if (id == 21001)
			{
				ByteConvert2.readInt(in);
			}

			id = ByteConvert2.readInt(in);
		}
	}
}
