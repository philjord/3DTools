/*
 * Created on Aug 31, 2006
 */
package tools3d.tree;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import tools.io.LittleEndianPrimitiveBytes;

/**
 * @author Administrator
 * 
 */
public class TreeLoader
{

	public static void main(String[] args)
	{
		test();
	}

	public static void test()
	{

		JFileChooser fc = new JFileChooser("F:/oblivion/");
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		fc.showOpenDialog(new JFrame());

		if (fc.getSelectedFile() != null)
		{
			File f = fc.getSelectedFile();
			System.out.println("Selected file: " + f);
			if (f.isDirectory())
			{
				processDir(f);
			}
			else if (f.isFile())
			{
				loadTree(f);
			}
		}

	}

	private static void processDir(File dir)
	{
		System.out.println("Processing directory " + dir);
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++)
		{
			try
			{
				if (fs[i].isFile() && fs[i].getName().endsWith(".spt"))
				{
					System.out.println("\tFile: " + fs[i]);
					loadTree(fs[i]);
				}
				else if (fs[i].isDirectory())
				{
					processDir(fs[i]);
				}

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static SptFileData loadTree(File file)
	{
		try
		{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), (int) (file.length() + 10));
			SptFileData sptFileData = new SptFileData();
			int sectionId = LittleEndianPrimitiveBytes.readInt(in);
			while (sectionId != 1 && sectionId != 0)
			{
				System.out.println("section to load " + sectionId);
				if (sectionId == 1000)
				{
					sptFileData.sec1000 = new Sec1000(in);
				}
				else if (sectionId == 1002)
				{
					sptFileData.trunk1002 = new Trunk1002(in);
				}
				else if (sectionId == 1004)
				{
					sptFileData.sec1004 = new Sec1004(in);
				}
				else if (sectionId == 1005)
				{
					// what does 1005 represent??
				}
				else if (sectionId == 1006)
				{
					sptFileData.sec1006 = new Sec1006(in);
				}
				else if (sectionId == 1011)
				{
					sptFileData.sec1011 = new Sec1011(in);
				}
				else if (sectionId == 1001)
				{
					sptFileData.sec1001 = new Sec1001(in);
				}

				sectionId = LittleEndianPrimitiveBytes.readInt(in);
			}
			return sptFileData;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		return null;
	}
}
