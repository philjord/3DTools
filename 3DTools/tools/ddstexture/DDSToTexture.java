package tools.ddstexture;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.prefs.Preferences;

import javax.media.j3d.DDSImageComponent2D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import tools.WeakValueHashMap;
import tools.io.FastByteArrayInputStream;
import tools.swing.DetailsFileChooser;

/*http://en.wikipedia.org/wiki/S3_Texture_Compression*/
public class DDSToTexture
{
	public static final int BLOCK_SIZE = 4;

	private static Preferences prefs;

	public static void main(String[] args)
	{
		prefs = Preferences.userNodeForPackage(DDSToTexture.class);

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
			System.out.println("" + DDSToTexture.class + " had a  IO problem with " + filename + " : " + e.getMessage());
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
			ddsImage = DDSImage.read(toByteBuffer(inputStream));
			ddsImage.debugPrint();
		}
		catch (IOException e)
		{
			System.out.println("" + DDSToTexture.class + " had a  IO problem with " + filename + " : " + e.getMessage());
			return;
		}

		DDSImage.ImageInfo[] infos = ddsImage.getAllMipMaps();

		int height = -1;
		int width = 0;
		for (int i = 0; i < infos.length; i++)
		{
			DDSBufferedImage image = new DDSBufferedImage(ddsImage, i, filename);
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

	/**
	 * A hashmap of the loaded image instances. Weak so that we can discard them if they are not in use by at least 1 shape
	 * Note self expunging
	 */
	private static WeakValueHashMap<String, Texture2D> loadedTextures = new WeakValueHashMap<String, Texture2D>();

	//private static ReferenceQueue<Texture2D> refQueue = new ReferenceQueue<Texture2D>();

	/**
	 * Called to early out in case of cache hit, very likely to return null!
	 * @param filename
	 * @return
	 */
	public static Texture checkCachedTexture(String filename)
	{
		return loadedTextures.get(filename);
	}

	public static void clearCache()
	{
		loadedTextures.clear();
	}

	/**
	 * Returns the associated Texture object or null if the image failed to load
	 * 
	 * @return The associated Texture object
	 */
	public static Texture getTexture(File file)
	{
		String filename = file.getAbsolutePath();
		try
		{
			return getTexture(filename, new FileInputStream(file));
		}
		catch (IOException e)
		{
			System.out.println("" + DDSToTexture.class + " had a  IO problem with " + filename + " : " + e.getMessage());
			return null;
		}
	}

	public static Texture getTexture(String filename, InputStream inputStream)
	{
		try
		{
			// Check the cache for an instance first
			Texture ret_val = loadedTextures.get(filename);

			if (ret_val == null)
			{
				DDSImage ddsImage = DDSImage.read(toByteBuffer(inputStream));

				// return null for unsupproted types
				if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT2 //
						|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT4 //
						|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_UNKNOWN)
				{
					System.out.println("Unsupported DDS format " + ddsImage.getPixelFormat() + " for file " + filename);
					return null;
				}

				// is it un mipmapped?
				if (ddsImage.getNumMipMaps() <= 1)
				{
					BufferedImage image = new DDSBufferedImage(ddsImage, 0, filename);

					if (image != null)
					{
						int imageComponentFormat = ImageComponent.FORMAT_RGBA;
						int textureFormat = Texture.RGBA;

						// non alpha images will be just RGB
						if (!image.getColorModel().hasAlpha())
						{
							imageComponentFormat = ImageComponent.FORMAT_RGB;
							textureFormat = Texture.RGB;
						}

						Texture2D tex = new Texture2D(Texture.BASE_LEVEL, textureFormat, image.getWidth(), image.getHeight());

						tex.setMinFilter(Texture.NICEST);
						tex.setMagFilter(Texture.NICEST);

						tex.setBoundaryModeS(Texture.WRAP);
						tex.setBoundaryModeT(Texture.WRAP);

						tex.setImage(0, new DDSImageComponent2D(imageComponentFormat, image));

						loadedTextures.put(filename, tex);
						ret_val = tex;
					}
					ddsImage.close();
				}
				else if (ddsImage.getNumMipMaps() > 1)
				{

					//DDSImage.ImageInfo[] infos = ddsImage.getAllMipMaps();

					int imageComponentFormat = ImageComponent.FORMAT_RGBA;
					int textureFormat = Texture.RGBA;

					int levels = ddsImage.getNumMipMaps();
					// now check how big it should be! sometime these things run out with 0w or 0h size images
					int levels2 = Math.min(computeLog(ddsImage.getWidth()), computeLog(ddsImage.getHeight())) + 1;
					levels = levels > levels2 ? levels2 : levels;

					Texture2D tex = new Texture2D(Texture.MULTI_LEVEL_MIPMAP, textureFormat, ddsImage.getWidth(), ddsImage.getHeight());

					tex.setName(filename);
					tex.setBaseLevel(0);
					tex.setMaximumLevel(levels - 1);

					// better to let machine decide, then settings option to go fastest one day
					tex.setMinFilter(Texture.NICEST);//Texture.MULTI_LEVEL_LINEAR);
					tex.setMagFilter(Texture.NICEST);//Texture.BASE_LEVEL_LINEAR);

					tex.setBoundaryModeS(Texture.WRAP);
					tex.setBoundaryModeT(Texture.WRAP);

					//E:\game media\Oblivion\meshes\architecture\imperialcity\icauouterwall01_far.nif 7 good
					//E:\game media\Fallout\meshes\landscape\trees\treedead01.nif should be 8 is 10?

					/*BufferedImage[] images = new BufferedImage[infos.length];

					for (int i = 0; i < infos.length; i++)
					{
						

						if (image != null)
						{
							images[i] = image;
						}
						else
						{
							System.out.println("null image in file " + filename + " image number " + i);
						}
					}*/

					//int w = images[0].getWidth();
					//int h = images[0].getHeight();
					for (int i = 0; i < levels; i++)
					{
						//if (i < ddsImage.getNumMipMaps())
						{
							BufferedImage image = new DDSBufferedImage(ddsImage, i, filename);
							tex.setImage(i, new DDSImageComponent2D(imageComponentFormat, image));
						}
						//else
						{
							//BufferedImage image = images[images.length - 1].getSubimage(0, 0, w, h);
							//trying to sort out darks lines in ST and fallout 3
							//System.out.println("Not enough mip maps");
							//throw new UnsupportedOperationException();
							//BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
							//tex.setImage(i, new DDSImageComponent2D(imageComponentFormat, image));
						}
						//w >>= 1;
						//w = w < 1 ? 1 : w;
						//h >>= 1;
						//h = h < 1 ? 1 : h;
					}

					loadedTextures.put(filename, tex);
					ret_val = tex;
				}
				ddsImage.close();

			}

			return ret_val;
		}
		catch (IOException e)
		{
			System.out.println("" + DDSToTexture.class + " had a  IO problem with " + filename + " : " + e.getMessage());
			return null;
		}

	}

	private static int BUFSIZE = 16000;

	public static ByteBuffer toByteBuffer(InputStream in) throws IOException
	{
		if (in instanceof FastByteArrayInputStream)
		{
			FastByteArrayInputStream bais = (FastByteArrayInputStream) in;
			return ByteBuffer.wrap(bais.getBuf());
		}
		else
		{
			//note toByteArray trims to size
			ByteArrayOutputStream out = new ByteArrayOutputStream(BUFSIZE);
			byte[] tmp = new byte[BUFSIZE];
			while (true)
			{
				int r = in.read(tmp);
				if (r == -1)
					break;

				out.write(tmp, 0, r);
			}

			return ByteBuffer.wrap(out.toByteArray());
		}
	}

	private static int computeLog(int value)
	{
		int i = 0;

		if (value == 0)
			return -1;
		for (;;)
		{
			if (value == 1)
				return i;
			value >>= 1;
			i++;
		}
	}
}
