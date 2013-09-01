package tools.texture;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.prefs.Preferences;

import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import tools.WeakValueHashMap;
import tools.swing.DetailsFileChooser;

/*http://en.wikipedia.org/wiki/S3_Texture_Compression*/
public class DDSToTextureOld
{

	public static final int BLOCK_SIZE = 4;

	private static Preferences prefs;

	public static void main(String[] args)
	{
		prefs = Preferences.userNodeForPackage(DDSToTextureOld.class);

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
				showImage(file, 50000);
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
			System.out.println("" + DDSToTextureOld.class + " had a  IO problem with " + filename + " : " + e.getMessage());
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
		}
		catch (IOException e)
		{
			System.out.println("" + DDSToTextureOld.class + " had a  IO problem with " + filename + " : " + e.getMessage());
			return;
		}

		DDSImage.ImageInfo[] infos = ddsImage.getAllMipMaps();

		int height = -1;
		int width = 0;
		for (int i = 0; i < infos.length; i++)
		{
			BufferedImage image = convertImage(ddsImage, i);
			if (image != null)
			{
				if (height == -1)// height of first big one only
					height = image.getHeight();
				width += image.getWidth();

				ImageIcon icon = new ImageIcon(image);
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

	//TODO: public calls to this image loader is skipping chaching for now
	public static BufferedImage convertImage(DDSImage ddsImage, int mipNumber)
	{

		if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1)
		{
			//System.out.println("DXT1");
			if (!ddsImage.isPixelFormatFlagSet(DDSImage.DDPF_ALPHAPIXELS))
			{
				//TODO: how do I discover no alpha flag? 
				//C:\game media\Black Prophecy\Textures\avatar_ai_pilot_f3_05.dds wants no alpha flag
				//possibly no mips maps indicates this?
				return decodeDxt1Buffer(ddsImage.getMipMap(mipNumber), ddsImage.getNumMipMaps() == 0);
			}
			else
			{
				System.out.println("Alpha present in DXT1!; mip num = " + mipNumber);
				// eg retun decompressRGBA_S3TC_DXT1_EXT(ddsImage.getMipMap(mipNumber));
			}
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT2)
		{
			System.out.println("DXT2 not supported; mip num = " + mipNumber);
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT3)
		{
			//System.out.println("DXT3");
			return decodeDxt3Buffer(ddsImage.getMipMap(mipNumber));
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT4)
		{
			System.out.println("DXT4 not supported; mip num = " + mipNumber);
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT5)
		{
			//System.out.println("DXT5");
			return decompressRGBA_S3TC_DXT5_EXT(ddsImage.getMipMap(mipNumber));
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8)
		{
			//System.out.println("D3DFMT_R8G8B8");
			return decodeR8G8B8(ddsImage.getMipMap(mipNumber));
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8)
		{
			//System.out.println("D3DFMT_A8R8G8B8");
			return decodeA8R8G8B8(ddsImage.getMipMap(mipNumber));
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8)
		{
			//System.out.println("D3DFMT_X8R8G8B8");
			//is X different from A?
			return decodeA8R8G8B8(ddsImage.getMipMap(mipNumber));
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_UNKNOWN)
		{
			System.out.println("D3DFMT_UNKNOWN not supported; mip num = " + mipNumber);
		}
		else if (ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F)
		{
			//System.out.println("DDS_A16B16G16R16F");
			return decodeA16R16G16B16(ddsImage.getMipMap(mipNumber));
		}
		else
		{
			System.out.println("not DDS format " + ddsImage.getPixelFormat() + "; mip num = " + mipNumber);
		}
		return null;
	}

	public static BufferedImage decodeR8G8B8(DDSImage.ImageInfo imageInfo)
	{
		int width = imageInfo.getWidth();
		int height = imageInfo.getHeight();

		// zero sized need to return a valid bufferedimage (should investiage if null could be better)
		if (width < 1 || height < 1)
		{
			return new BufferedImage(width < 1 ? 1 : width, height < 1 ? 1 : height, BufferedImage.TYPE_INT_RGB);
		}

		ByteBuffer buffer = imageInfo.getData();

		//always ensure little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				result.setRGB(x, y, ((buffer.get() & 0xff) << 24 | (buffer.get() & 0xff) << 16 | (buffer.get() & 0xff) << 8));
			}
		}
		return result;
	}

	public static BufferedImage decodeA8R8G8B8(DDSImage.ImageInfo imageInfo)
	{
		int width = imageInfo.getWidth();
		int height = imageInfo.getHeight();

		// zero sized need to return a valid bufferedimage (should investiage if null could be better)
		if (width < 1 || height < 1)
		{
			return new BufferedImage(width < 1 ? 1 : width, height < 1 ? 1 : height, BufferedImage.TYPE_INT_RGB);
		}

		ByteBuffer buffer = imageInfo.getData();

		//always ensure little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[width * height];
		for (int k = 0; k < width * height; k++)
		{
			pixels[k] = buffer.getInt();
		}
		result.setRGB(0, 0, width, height, pixels, 0, width);

		return result;
	}

	public static BufferedImage decodeA16R16G16B16(DDSImage.ImageInfo imageInfo)
	{

		// take a look at http://www.fourcc.org/rgb.php#BI_RLE8
		int width = imageInfo.getWidth();
		int height = imageInfo.getHeight();

		// zero sized need to return a valid bufferedimage (should investiage if null could be better)
		if (width < 1 || height < 1)
		{
			return new BufferedImage(width < 1 ? 1 : width, height < 1 ? 1 : height, BufferedImage.TYPE_INT_RGB);
		}

		ByteBuffer buffer = imageInfo.getData();

		//always ensure little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		//TODO: this is a dodgy layout here tested good on black prophecy images onyl
		//2&4 good for smalls
		//2&64 for bigs

		int numBlocksWide = width / 2;
		int numBlocksHigh = 1;//height / 64;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, 2);
		int blockHeight = height;//Math.min(height, 64);

		int[] pixels = new int[blockWidth * blockHeight];

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int col = 0; col < numBlocksWide; col++)
		{
			for (int row = 0; row < numBlocksHigh; row++)
			{
				float r = MiniFloat.toFloat((buffer.getShort()));
				float g = MiniFloat.toFloat((buffer.getShort()));
				float b = MiniFloat.toFloat((buffer.getShort()));
				float a = MiniFloat.toFloat((buffer.getShort()));
				Color c = new Color(r, g, b, a);
				for (int k = blockWidth * blockHeight - 1; k >= 0; k--)
				{
					pixels[k] = c.getRGB();
				}
				result.setRGB(col * blockWidth, row * blockHeight, blockWidth, blockHeight, pixels, 0, blockWidth);
			}
		}

		return result;
	}

	public static BufferedImage decodeDxt1Buffer(DDSImage.ImageInfo imageInfo, boolean ignoreAlpha)
	{

		int width = imageInfo.getWidth();
		int height = imageInfo.getHeight();

		// zero sized need to return a valid bufferedimage (should investiage if null could be better)
		if (width < 1 || height < 1)
		{
			return new BufferedImage(width < 1 ? 1 : width, height < 1 ? 1 : height, BufferedImage.TYPE_INT_RGB);
		}

		ByteBuffer buffer = imageInfo.getData();
		//always ensure little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		int[] pixels = new int[blockWidth * blockHeight];

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		//One copy of table to minimises new calls
		Color24[] table = new Color24[4];
		table[0] = new Color24();
		table[1] = new Color24();
		table[2] = new Color24();
		table[3] = new Color24();

		for (int row = 0; row < numBlocksHigh; row++)
		{
			for (int col = 0; col < numBlocksWide; col++)
			{
				short c0 = buffer.getShort();
				short c1 = buffer.getShort();
				int colorIndexMask = buffer.getInt();

				//http://en.wikipedia.org/wiki/S3_Texture_Compression
				if (ignoreAlpha || !Color24.hasAlphaBit(c0, c1))
				{
					Color24[] lookupTable = Color24.expandLookupTable(table, c0, c1);
					for (int k = blockWidth * blockHeight - 1; k >= 0; k--)
					{
						int colorIndex = (colorIndexMask >>> k * 2) & 0x03;
						pixels[k] = (0xFF << 24) | lookupTable[colorIndex].pix888;
					}
				}
				else
				{
					Color24[] lookupTable = Color24.expandLookupTableAlphable(table, c0, c1);
					for (int k = blockWidth * blockHeight - 1; k >= 0; k--)
					{
						int colorIndex = (colorIndexMask >>> k * 2) & 0x03;
						int alpha = (colorIndex == 3) ? 0x00 : 0xFF;
						pixels[k] = (alpha << 24) | lookupTable[colorIndex].pix888;
					}
				}

				result.setRGB(col * blockWidth, row * blockHeight, blockWidth, blockHeight, pixels, 0, blockWidth);
			}
		}

		return result;

	}

	public static BufferedImage decodeDxt3Buffer(DDSImage.ImageInfo imageInfo)
	{
		int width = imageInfo.getWidth();
		int height = imageInfo.getHeight();

		// zero sized need to return a valid bufferedimage (should investiage if null could be better)
		if (width < 1 || height < 1)
		{
			return new BufferedImage(width < 1 ? 1 : width, height < 1 ? 1 : height, BufferedImage.TYPE_INT_RGB);
		}

		ByteBuffer buffer = imageInfo.getData();
		//always ensure little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		int[] pixels = new int[blockWidth * blockHeight];

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		//One copy of table to minimises new calls
		Color24[] table = new Color24[4];
		table[0] = new Color24();
		table[1] = new Color24();
		table[2] = new Color24();
		table[3] = new Color24();

		for (int row = 0; row < numBlocksHigh; row++)
		{
			for (int col = 0; col < numBlocksWide; col++)
			{
				long alphaData = buffer.getLong();
				short minColor = buffer.getShort();
				short maxColor = buffer.getShort();
				int colorIndexMask = buffer.getInt();

				Color24[] lookupTable = Color24.expandLookupTable(table, minColor, maxColor);

				for (int k = blockWidth * blockHeight - 1; k >= 0; k--)
				{
					int alpha = (int) (alphaData >>> (k * 4)) & 0xF; // Alphas are just 4 bits per pixel
					alpha <<= 4;

					int colorIndex = (colorIndexMask >>> k * 2) & 0x03;

					Color24 color = lookupTable[colorIndex];
					int pixel8888 = (alpha << 24) | color.pix888;

					pixels[k] = pixel8888;
				}
				result.setRGB(col * blockWidth, row * blockHeight, blockWidth, blockHeight, pixels, 0, blockWidth);
			}

		}
		return result;
	}

	public static BufferedImage decompressRGBA_S3TC_DXT5_EXT(DDSImage.ImageInfo imageInfo)
	{
		int width = imageInfo.getWidth();
		int height = imageInfo.getHeight();

		// zero sized need to return a valid bufferedimage (should investiage if null could be better)
		if (width < 1 || height < 1)
		{
			return new BufferedImage(width < 1 ? 1 : width, height < 1 ? 1 : height, BufferedImage.TYPE_INT_RGB);
		}

		ByteBuffer buffer = imageInfo.getData();
		//always ensure little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		int[] pixels = new int[blockWidth * blockHeight];

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		//One copy of table to minimises new calls
		Color24[] table = new Color24[4];
		table[0] = new Color24();
		table[1] = new Color24();
		table[2] = new Color24();
		table[3] = new Color24();

		for (int row = 0; row < numBlocksHigh; row++)
		{
			for (int col = 0; col < numBlocksWide; col++)
			{
				int alpha0 = buffer.get() & 0xff; //unsigned byte
				int alpha1 = buffer.get() & 0xff; //unsigned byte

				// next 6 bytes are a look up list (note long casts, important!)
				long alphaBits = (buffer.get() & 0xffL) << 0L //
						| (buffer.get() & 0xffL) << 8L //
						| (buffer.get() & 0xffL) << 16L//
						| (buffer.get() & 0xffL) << 24L //
						| (buffer.get() & 0xffL) << 32L //
						| (buffer.get() & 0xffL) << 40L;//

				short minColor = buffer.getShort();
				short maxColor = buffer.getShort();
				int colorIndexMask = buffer.getInt();

				Color24[] lookupTable = Color24.expandLookupTable(table, minColor, maxColor);

				for (int k = blockWidth * blockHeight - 1; k >= 0; k--)
				{
					int alphaCode = (int) (alphaBits >> (3 * k) & 0x07); // bottom 3 bits

					int alpha = 0;

					if (alphaCode == 0)
					{
						alpha = alpha0;
					}
					else if (alphaCode == 1)
					{
						alpha = alpha1;
					}
					else if (alpha0 > alpha1)
					{
						alpha = ((8 - alphaCode) * alpha0 + (alphaCode - 1) * alpha1) / 7;
					}
					else
					{
						if (alphaCode == 6)
							alpha = 0;
						else if (alphaCode == 7)
							alpha = 255;
						else
							alpha = ((6 - alphaCode) * alpha0 + (alphaCode - 1) * alpha1) / 5;

					}

					int colorIndex = (colorIndexMask >>> k * 2) & 0x03;

					Color24 color = lookupTable[colorIndex];
					int pixel8888 = (alpha << 24) | color.pix888;

					pixels[k] = pixel8888;
				}
				result.setRGB(col * blockWidth, row * blockHeight, blockWidth, blockHeight, pixels, 0, blockWidth);
			}

		}

		return result;
	}

	/**
	 * A hashmap of the loaded image instances. Weak so that we can discard them if they are not in use by at least 1 shape
	 */
	private static WeakValueHashMap<String, Texture2D> loadedTextures = new WeakValueHashMap<String, Texture2D>();

	//private static ReferenceQueue<Texture2D> refQueue = new ReferenceQueue<Texture2D>();

	/**
	 * Returns the associated Texture object or null if the image failed to load
	 * 
	 * @return The associated Texture object
	 */

	public static int HIGH_TEXTURE = 0;

	public static int MEDIUM_TEXTURE = 1;

	public static int LOW_TEXTURE = 2;

	public static int TEXTURE_SETTING = HIGH_TEXTURE;

	public static void clearCache()
	{
		loadedTextures.clear();
	}

	public static Texture getTexture(File file)
	{
		String filename = file.getAbsolutePath();
		try
		{
			return getTexture(filename, new FileInputStream(file));
		}
		catch (IOException e)
		{
			System.out.println("" + DDSToTextureOld.class + " had a  IO problem with " + filename + " : " + e.getMessage());
			return null;
		}
	}

	/**
	 * Called to early out n case of cache hit, very likely to return null!
	 * @param filename
	 * @return
	 */
	public static Texture checkCachedTexture(String filename)
	{
		return loadedTextures.get(filename);
	}

	public static Texture getTexture(String filename, InputStream inputStream)
	{

		// Check the cache for an instance first
		Texture ret_val = loadedTextures.get(filename);

		if (ret_val == null)
		{
			DDSImage ddsImage;
			try
			{
				ddsImage = DDSImage.read(toByteBuffer(inputStream));
			}
			catch (IOException e)
			{
				System.out.println("" + DDSToTextureOld.class + " had a  IO problem with " + filename + " : " + e.getMessage());
				return null;
			}

			// return null for unsupproted types
			if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT2 || ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT4
					|| ddsImage.getPixelFormat() == DDSImage.D3DFMT_UNKNOWN)
			{
				System.out.println("Unsupported DDS format " + ddsImage.getPixelFormat() + " for file " + filename);
				return null;
			}

			// is it un mipmapped?

			if (ddsImage.getNumMipMaps() <= 1)
			{
				BufferedImage image = convertImage(ddsImage, 0);
				if (image != null)
				{
					int imageComponentFormat = ImageComponent.FORMAT_RGBA;
					int textureFormat = Texture.RGBA;

					// non alpha images will be just RGB
					if (image.getType() == BufferedImage.TYPE_INT_RGB)
					{
						imageComponentFormat = ImageComponent.FORMAT_RGB;
						textureFormat = Texture.RGB;
					}

					Texture2D tex = new Texture2D(Texture.BASE_LEVEL, textureFormat, image.getWidth(), image.getHeight());

					tex.setBoundaryModeS(Texture.WRAP);
					tex.setBoundaryModeT(Texture.WRAP);

					tex.setImage(0, new ImageComponent2D(imageComponentFormat, image));

					loadedTextures.put(filename, tex);
					ret_val = tex;
				}
				ddsImage.close();
			}
			else if (ddsImage.getNumMipMaps() > 1)
			{
				int removedTextures = TEXTURE_SETTING;

				DDSImage.ImageInfo[] infos = ddsImage.getAllMipMaps();

				if (infos.length - removedTextures < 1)
				{
					removedTextures = infos.length - 1;
				}

				BufferedImage[] images = new BufferedImage[infos.length - removedTextures];

				for (int i = 0; i < infos.length - removedTextures; i++)
				{

					BufferedImage image = convertImage(ddsImage, i + removedTextures);

					if (image != null)
					{
						images[i] = image;
					}
					else
					{
						System.out.println("null image in file " + filename + " image number " + i);
					}
				}

				if (images[0] != null)
				{
					int imageComponentFormat = ImageComponent.FORMAT_RGBA;
					int textureFormat = Texture.RGBA;

					// non alpha images will be just RGB
					if (images[0].getType() == BufferedImage.TYPE_INT_RGB)
					{
						imageComponentFormat = ImageComponent.FORMAT_RGB;
						textureFormat = Texture.RGB;
					}

					int levels = Math.max(computeLog(images[0].getWidth()), computeLog(images[0].getHeight())) + 1;

					Texture2D tex = new Texture2D(Texture.MULTI_LEVEL_MIPMAP, textureFormat, images[0].getWidth(), images[0].getHeight());

					tex.setName(filename);
					tex.setBaseLevel(0);
					tex.setMaximumLevel(levels - 1);
					tex.setMinFilter(Texture.MULTI_LEVEL_LINEAR);
					tex.setMagFilter(Texture.BASE_LEVEL_LINEAR);

					tex.setBoundaryModeS(Texture.WRAP);
					tex.setBoundaryModeT(Texture.WRAP);

					int w = images[0].getWidth();
					int h = images[0].getHeight();
					for (int i = 0; i < levels; i++)
					{
						if (i < images.length)
						{
							tex.setImage(i, new ImageComponent2D(imageComponentFormat, images[i]));

							//tex.setImage(i, new DDSImageComponent2D(imageComponentFormat, images[i], true, true));

						}
						else if (images.length > 0)
						{
							BufferedImage image = images[images.length - 1].getSubimage(0, 0, w, h);
							tex.setImage(i, new ImageComponent2D(imageComponentFormat, image));

							//tex.setImage(i, new DDSImageComponent2D(imageComponentFormat, image, true, true));

						}
						w >>= 1;
						w = w < 1 ? 1 : w;
						h >>= 1;
						h = h < 1 ? 1 : h;
					}

					loadedTextures.put(filename, tex);
					ret_val = tex;
				}
				ddsImage.close();
			}

		}

		return ret_val;
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

	private static int BUFSIZE = 8000;

	public static ByteBuffer toByteBuffer(InputStream in) throws IOException
	{

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
