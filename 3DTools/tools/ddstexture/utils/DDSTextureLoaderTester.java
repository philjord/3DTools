package tools.ddstexture.utils;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import tools.ddstexture.DDSImage;
import tools.ddstexture.DDSTextureLoader;
import tools.swing.DetailsFileChooser;

/**
 * dds image loading tester, note this use teh decompress to buffered image util system
 * not the jogl compressed call
 * @author philip
 *
 */
public class DDSTextureLoaderTester
{
	private static Preferences prefs;

	public static void main(String[] args)
	{
		prefs = Preferences.userNodeForPackage(DDSTextureLoaderTester.class);

		DetailsFileChooser dfc = new DetailsFileChooser(prefs.get("DDSToTexture", ""), new DetailsFileChooser.Listener()
		{
			@Override
			public void directorySelected(File dir)
			{
				prefs.put("DDSToTexture", dir.getAbsolutePath());
				System.out.println("Selected dir: " + dir);
				processDir(dir);
			}

			@Override
			public void fileSelected(File file)
			{
				prefs.put("DDSToTexture", file.getAbsolutePath());
				System.out.println("Selected file: " + file);
				showImage(file, 15000);
			}
		});

		dfc.setFileFilter(new FileNameExtensionFilter("dds", "dds"));
	}

	private static void processDir(File dir)
	{
		System.out.println("Processing directory " + dir);
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++)
		{
			try
			{
				if (fs[i].isFile() && fs[i].getName().endsWith(".dds"))
				{
					System.out.println("\tFile: " + fs[i]);
					showImage(fs[i], 5000);

					//pause between each show to gve it a chance to show
					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException e)
					{
					}
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

	public static void showImage(File file, long stayTime)
	{
		String filename = file.getAbsolutePath();
		try
		{
			showImage(filename, new FileInputStream(file), stayTime);
		}
		catch (IOException e)
		{
			System.out.println("" + DDSTextureLoaderTester.class + " had a  IO problem with " + filename + " : " + e.getMessage());
		}

	}

	public static void showImage(String filename, InputStream inputStream, final long stayTime)
	{
		final JFrame f = new JFrame();
		f.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		f.getContentPane().setBackground(new Color(255, 0, 255));

		DDSImage ddsImage;
		try
		{
			ddsImage = DDSImage.read(DDSTextureLoader.toByteBuffer(inputStream));
			ddsImage.debugPrint();
		}
		catch (IOException e)
		{
			System.out.println("" + DDSTextureLoaderTester.class + " had a  IO problem with " + filename + " : " + e.getMessage());
			return;
		}

		DDSImage.ImageInfo[] infos = ddsImage.getAllMipMaps();

		int height = -1;
		int width = 0;
		for (int i = 0; i < infos.length; i++)
		{
			DDSDecompressor image = new DDSDecompressor(ddsImage, i, filename);
			if (image != null)
			{
				if (height == -1)// height of first big one only
					height = image.getHeight();
				width += image.getWidth();

				//Flip because of the desire to have yUp on
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -image.getHeight());
				BufferedImage mine = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
				((Graphics2D) mine.getGraphics()).drawImage(image.convertImage(), tx, null);

				ImageIcon icon = new ImageIcon(mine);
				f.getContentPane().add(new JLabel(icon));
			}
		}

		ddsImage.close();
		f.setTitle(filename);
		f.setVisible(true);
		f.setSize(width + f.getInsets().left + f.getInsets().right, height + f.getInsets().top + f.getInsets().bottom);

		Thread t = new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(stayTime);
				}
				catch (InterruptedException e)
				{
				}
				f.dispose();
			}
		};
		t.start();
	}

}
