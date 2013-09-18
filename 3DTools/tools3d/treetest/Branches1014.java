package tools3d.treetest;

import java.io.IOException;
import java.io.InputStream;

import tools.io.LittleEndianPrimitiveBytes;

public class Branches1014
{
	public int countOfBranches;

	public Branch1016[] branches;

	public Branches1014(InputStream in) throws IOException
	{
		countOfBranches = LittleEndianPrimitiveBytes.readInt(in);
		branches = new Branch1016[countOfBranches];
		int idx = 0;
		int id = LittleEndianPrimitiveBytes.readInt(in);
		while (id != 1015)
		{

			if (id == 1016)
			{
				branches[idx] = new Branch1016(in);
				idx++;
			}
			id = LittleEndianPrimitiveBytes.readInt(in);
		}
	}
}
