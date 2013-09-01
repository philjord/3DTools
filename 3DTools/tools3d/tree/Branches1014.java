package tools3d.tree;

import java.io.IOException;
import java.io.InputStream;

public class Branches1014
{
	public int countOfBranches;

	public Branch1016[] branches;

	public Branches1014(InputStream in) throws IOException
	{
		countOfBranches = ByteConvert2.readInt(in);
		branches = new Branch1016[countOfBranches];
		int idx = 0;
		int id = ByteConvert2.readInt(in);
		while (id != 1015)
		{

			if (id == 1016)
			{
				branches[idx] = new Branch1016(in);
				idx++;
			}
			id = ByteConvert2.readInt(in);
		}
	}
}
