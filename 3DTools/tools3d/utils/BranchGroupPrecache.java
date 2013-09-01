package tools3d.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.DanglingReferenceException;

import com.sun.j3d.utils.scenegraph.io.NamedObjectException;
import com.sun.j3d.utils.scenegraph.io.SceneGraphStreamReader;
import com.sun.j3d.utils.scenegraph.io.SceneGraphStreamWriter;

public class BranchGroupPrecache
{
	public static void precache(String fileName, BranchGroup bg)
	{
		try
		{
			File outFile = new File(fileName);
			if (outFile.exists())
			{
				outFile.delete();
			}
			outFile.createNewFile();

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile), (int) (outFile.length() + 10));
			SceneGraphStreamWriter sgsw = new SceneGraphStreamWriter(bos);

			long start = System.currentTimeMillis();

			try
			{
				sgsw.writeBranchGraph(bg, new HashMap<String, String>());
			}
			catch (DanglingReferenceException e1)
			{
				e1.printStackTrace();
			}
			catch (NamedObjectException e1)
			{
				e1.printStackTrace();
			}

			bos.flush();
			sgsw.close();
			System.out.println("preprocessed written in " + (System.currentTimeMillis() - start));

		}
		catch (IOException e)
		{
			System.out.println("bad file name? " + fileName);
			e.printStackTrace();
		}
	}

	public static BranchGroup getPrecached(String fileName)
	{
		File inFile = new File(fileName);

		if (inFile.exists())
		{
			try
			{
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile), (int) (inFile.length() + 10));
				SceneGraphStreamReader sgsr = new SceneGraphStreamReader(bis);

				long start = System.currentTimeMillis();

				BranchGroup bg = sgsr.readBranchGraph(null);
				bis.close();

				System.out.println("preprocessed " + fileName + " read in " + (System.currentTimeMillis() - start));
				return bg;

			}
			catch (IOException e)
			{
				System.out.println("bad file name? " + fileName);
				e.printStackTrace();
			}
		}

		return null;
	}

	public static boolean isPrecached(String fileName)
	{
		File inFile = new File(fileName);
		return inFile.exists();
	}

}
