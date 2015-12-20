package tools.ddstexture.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import tools.MiniFloat;
import tools.ddstexture.DDSImage;

/**
 * Converts DDS files and streams into {@code BufferedImage}
 * {@link http://en.wikipedia.org/wiki/S3_Texture_Compression}
 * */
public class DDSDecompressor
{
	public static final int BLOCK_SIZE = 4;

	public DDSImage ddsImage;

	private int mipNumber;

	private DDSImage.ImageInfo imageInfo;

	private ByteBuffer buffer;

	private boolean ignoreAlpha = false;

	private int width;

	private int height;

	private String imageName = "";

	/**
	 * 
	 * @param ddsImage
	 * @param mipNumber
	 * @param imageName
	 */
	public DDSDecompressor(DDSImage ddsImage, int mipNumber, String imageName)
	{
		this.imageName = imageName;
		this.ddsImage = ddsImage;
		this.mipNumber = mipNumber;
		this.imageInfo = ddsImage.getAllMipMaps()[mipNumber];
		this.width = imageInfo.getWidth();
		this.height = imageInfo.getHeight();
		this.buffer = imageInfo.getData();
	}

	public String getImageName()
	{
		return imageName;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getType()
	{
		return BufferedImage.TYPE_INT_ARGB;
	}

	/**
	 * 
	 * @return a new non-cached {@code BufferedImage}
	 */
	public BufferedImage convertImage()
	{

		//can't use width or height as it's been corrected to 1 already
		if (imageInfo.getWidth() < 1 || imageInfo.getHeight() < 1)
		{
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}

		//prep the buffer
		buffer.rewind();
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1)
		{
			//System.out.println("DXT1");
			if (!ddsImage.isPixelFormatFlagSet(DDSImage.DDPF_ALPHAPIXELS))
			{
				//TODO: how do I discover no alpha flag? 
				//C:\game media\Black Prophecy\Textures\avatar_ai_pilot_f3_05.dds wants no alpha flag
				//possibly no mips maps indicates this?
				return decodeDxt1Buffer();
			}
			else
			{
				System.out.println("Alpha present in DXT1!; mip num = " + mipNumber);
				//return decompressRGBA_S3TC_DXT1_EXT(ddsImage.getMipMap(mipNumber));
			}
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT3)
		{
			return decodeDxt3Buffer();
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT5)
		{
			return decompressRGBA_S3TC_DXT5_EXT();
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_ATI2)
		{
			// NOT correct but it gives you the idea a bit
			return decompressRGBA_S3TC_DXT5_EXT();
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_R8G8B8)
		{
			return decodeR8G8B8();
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8)
		{
			return decodeA8R8G8B8();
		}
		else if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_X8R8G8B8)
		{
			return decodeA8R8G8B8();
		}
		else if (ddsImage.getPixelFormat() == DDSImage.DDS_A16B16G16R16F)
		{
			return decodeA16R16G16B16();
		}
		System.err.println("BAD DXT format!! " + ddsImage.getPixelFormat());
		return null;
	}

	private BufferedImage decodeR8G8B8()
	{
		//NOTE disagrees with fixed getType below
		BufferedImage delegate = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] pixels = new int[width * height];
		// reverse to flip Y
		for (int y = height - 1; y >= 0; y--)
		{
			for (int x = 0; x < width; x++)
			{
				pixels[(y * width) + x] = ((buffer.get() & 0xff) << 24 | (buffer.get() & 0xff) << 16 | (buffer.get() & 0xff) << 8);
			}
		}
		delegate.setRGB(0, 0, width, height, pixels, 0, width);
		return delegate;
	}

	private BufferedImage decodeA8R8G8B8()
	{
		BufferedImage delegate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[width * height];
		// reverse to flip Y
		for (int y = height - 1; y >= 0; y--)
		{
			for (int x = 0; x < width; x++)
			{
				pixels[(y * width) + x] = buffer.getInt();
			}
		}
		delegate.setRGB(0, 0, width, height, pixels, 0, width);
		return delegate;

	}

	private BufferedImage decodeA16R16G16B16()
	{
		//TODO: this is a dodgy layout here tested good on black prophecy images only		
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

		BufferedImage delegate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int col = 0; col < numBlocksWide; col++)
		{
			for (int row = 0; row < numBlocksHigh; row++)
			{
				float r = MiniFloat.toFloat((buffer.getShort()));
				float g = MiniFloat.toFloat((buffer.getShort()));
				float b = MiniFloat.toFloat((buffer.getShort()));
				float a = MiniFloat.toFloat((buffer.getShort()));
				Color c = new Color(r, g, b, a);
				for (int br = 0; br < blockHeight; br++)
				{
					for (int bc = 0; bc < blockWidth; bc++)
					{
						// for yUp must flip it so NOT pixels[br  * blockWidth + bc] = c.getRGB();
						pixels[((blockHeight - 1) - br) * blockWidth + bc] = c.getRGB();
					}
				}
				delegate.setRGB(col * blockWidth, (height - blockHeight) - (row * blockHeight), blockWidth, blockHeight, pixels, 0,
						blockWidth);
			}
		}
		return delegate;
	}

	private BufferedImage decodeDxt1Buffer()
	{
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		int[] pixels = new int[blockWidth * blockHeight];

		BufferedImage delegate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

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
					for (int br = 0; br < blockHeight; br++)
					{
						for (int bc = 0; bc < blockWidth; bc++)
						{
							int k = (br * blockWidth) + bc;
							int colorIndex = (colorIndexMask >>> k * 2) & 0x03;
							// for yUp must flip it so NOT pixels[br  * blockWidth + bc] = (0xFF << 24) | lookupTable[colorIndex].pix888;
							pixels[((blockHeight - 1) - br) * blockWidth + bc] = (0xFF << 24) | lookupTable[colorIndex].pix888;
						}
					}
				}
				else
				{
					Color24[] lookupTable = Color24.expandLookupTableAlphable(table, c0, c1);
					for (int br = 0; br < blockHeight; br++)
					{
						for (int bc = 0; bc < blockWidth; bc++)
						{
							int k = (br * blockWidth) + bc;
							int colorIndex = (colorIndexMask >>> k * 2) & 0x03;
							int alpha = (colorIndex == 3) ? 0x00 : 0xFF;
							// for yUp must flip it , so NOT pixels[br  * blockWidth + bc] = (alpha << 24) | lookupTable[colorIndex].pix888;
							pixels[((blockHeight - 1) - br) * blockWidth + bc] = (alpha << 24) | lookupTable[colorIndex].pix888;
						}
					}
				}

				delegate.setRGB(col * blockWidth, (height - blockHeight) - (row * blockHeight), blockWidth, blockHeight, pixels, 0,
						blockWidth);
			}
		}
		return delegate;
	}

	private BufferedImage decodeDxt3Buffer()
	{
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		int[] pixels = new int[blockWidth * blockHeight];

		BufferedImage delegate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

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

				for (int br = 0; br < blockHeight; br++)
				{
					for (int bc = 0; bc < blockWidth; bc++)
					{
						int k = (br * blockWidth) + bc;
						int alpha = (int) (alphaData >>> (k * 4)) & 0xF; // Alphas are just 4 bits per pixel
						alpha <<= 4;

						int colorIndex = (colorIndexMask >>> k * 2) & 0x03;

						Color24 color = lookupTable[colorIndex];
						int pixel8888 = (alpha << 24) | color.pix888;

						// for yUp must flip it , so NOT pixels[br  * blockWidth + bc] = pixel8888;
						pixels[((blockHeight - 1) - br) * blockWidth + bc] = pixel8888;
					}
				}
				//notice vertical flipping there
				delegate.setRGB(col * blockWidth, (height - blockHeight) - (row * blockHeight), blockWidth, blockHeight, pixels, 0,
						blockWidth);
			}

		}
		return delegate;
	}

	private BufferedImage decompressRGBA_S3TC_DXT5_EXT()
	{
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		int[] pixels = new int[blockWidth * blockHeight];

		BufferedImage delegate = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

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

				for (int br = 0; br < blockHeight; br++)
				{
					for (int bc = 0; bc < blockWidth; bc++)
					{
						int k = (br * blockWidth) + bc;

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
						// for yUp must flip it , so NOT pixels[br  * blockWidth + bc] = pixel8888;
						pixels[((blockHeight - 1) - br) * blockWidth + bc] = pixel8888;
					}
				}
				//notice vertical flipping there
				delegate.setRGB(col * blockWidth, (height - blockHeight) - (row * blockHeight), blockWidth, blockHeight, pixels, 0,
						blockWidth);
			}

		}

		return delegate;
	}

	/*	BELOW ARE MY CRAZY NOTES OVER LONG YEARS OF R&D, all of them redundant now the jogl pipeline supports DXT
	 * 
	 * This class does some crazy things to optomises memory versus speed
	 * The source data is compress at a 1:4 ratio of the required buffered image
	 * However the buffered image raster data is pulled out and sent to the GPU so we have at least a double 
	 * up of each image usage, bummer.
	 * Most images are called once and then their Texture reference is shared, so the majority (80%?) 
	 * need to uncompress once and discard the source data and in fact teh uncompressed data
	 * However if we have no data copy then if a second get raster is called we'd have to go to the 
	 * disk again and getRaster is in the render pipeline so it MUST be super fast.
	 * We need a system to work out a head of time if we will ever see getRaster called twice
	 * But I can't work that out, it's probably related to how many Appearances use the texture and what
	 * other attributes there are (like transparency for one).
	 * The constructor call is often on a  seperate thread form the renderer, so we want the first call to uncompress the
	 * image data at least, then on first getRaster we discard the uncompressed. On second get Raster we uncompress
	 * And keep the compressed data ready for the third etc call.
	 * If however I was to make the 3rd getRaster call the one to tenure the raster data, I'd save 200MB memory
	 * as there are more 3+ getRaster call textures 
	 * Only now I basically keep a weak reference to the handed out raster
	 * 
	 * getRasterCountForAll 1700
	 * Call Count 0 0
	 * Call Count 1 172
	 * Call Count 2 23
	 * Call Count 3 81
	 * 
	 * 
	 * 
	 * Note non BufferedIamge ARGB might end up not being treated by ref properly so there might be saving to be 
	 * had to make every this ARGB but 
	 * 
	 * private SoftReference<Object> ints;

	private Object firstTimeIntsRef = null;

	public Object getInts()
	{
		// oh my god!, After the first getRaster from J3d, it'll ask me for it again
		// only it turns out it's still holding the ref from the first time, so I can weakly hold it too
		// and hand it back whenever I'm asked for it! crazy. But sometimes it's let go of it, possibly soft?

		//FCUK!! possibly small gain from GC not clearing up fast, does work like I suggested, nobody holds a ref to 
		// returned raster at all

		// for first time only use constructors hard ref, and then drop it to weak
		if (firstTimeIntsRef != null)
		{
			Object ret = firstTimeIntsRef;
			ints = new SoftReference<Object>(ret);
			firstTimeIntsRef = null;
			return ret;
		}
		else
		{
			if (ints != null)
			{
				Object prevWr = ints.get();
				if (prevWr != null)
				{
					return prevWr;
				}
			}

			// don't have it any more so re-create it
			Object ret = ((DataBufferInt) convertImage().getRaster().getDataBuffer()).getData();
			ints = new SoftReference<Object>(ret);

			return ret;
		}
	}*/

	/*	public int getRasterCount = 0;

		private WritableRaster firstTimeRasterRef = null;

		private SoftReference<WritableRaster> weakRasterRef;

		//private WeakReference<WritableRaster> weakRasterRef;

		@Override
		public WritableRaster getRaster()
		{
			getRasterCount++;

			//Output some stats
			//dealWithStats();

			// oh my god!, After the first getRaster from J3d, it'll askme for it again
			// only it turns out it's still holding the ref from the first time, so I can weakly hold it too
			// and hand it back whenever I'm asked for it! crazy. But sometimes it's let go of it, possibly soft?

			//FCUK!! possibly small gain from GC not clearing up fast, does work like I suggested, nobody holds a ref to 
			// returned raster at all

			// for first time only use constructors hard ref, and then drop it to weak
			if (firstTimeRasterRef != null)
			{
				weakRasterRef = new SoftReference<WritableRaster>(firstTimeRasterRef);
				//weakRasterRef = new WeakReference<WritableRaster>(firstTimeRasterRef);
				WritableRaster ret = firstTimeRasterRef;
				firstTimeRasterRef = null;
				return ret;
			}
			else
			{
				if (weakRasterRef != null)
				{
					WritableRaster prevWr = weakRasterRef.get();
					if (prevWr != null)
					{
						System.out.println("prev raster hit!");
						return prevWr;
					}
				}

				// don't have it any more so re-create it
				//System.out.println("Had to re create raster!");
				WritableRaster wr = convertImage().getRaster();
				weakRasterRef = new SoftReference<WritableRaster>(wr);
				//weakRasterRef = new WeakReference<WritableRaster>(wr);

				return wr;
			}
		}

		private static HashSet<DDSBufferedImage> allDDSBufferedImage = new HashSet<DDSBufferedImage>();

		private static int getRasterCountForAll = 0;

		private void dealWithStats()
		{
			if (mipNumber == 0)
			{
				getRasterCountForAll++;

				allDDSBufferedImage.add(this);

				if (getRasterCountForAll % 100 == 0)
				{
					System.out.println("getRasterCountForAll " + getRasterCountForAll);
					int[] callCountCounts = new int[11];//10 is 10 and up
					int countOfTenured = 0;
					int countTenuredOnly = 0;
					System.out.println("allDDSBufferedImage.size() " + allDDSBufferedImage.size());
					for (DDSBufferedImage im : allDDSBufferedImage)
					{
						if (im.getRasterCount > 1)
							System.out.println("DDSBufferedImage " + im.getRasterCount + " " + im.getImageName());

						if (im.getRasterCount < 10)
						{
							callCountCounts[im.getRasterCount]++;
						}
						else
						{
							callCountCounts[10]++;
						}

						//if (im.tenuredImage != null)
						//	countOfTenured++;

						if (im.ddsImage == null)
							countTenuredOnly++;
					}

					for (int i = 0; i < callCountCounts.length; i++)
					{
						System.out.println("Call Count " + i + " " + callCountCounts[i]);
					}
					System.out.println("countOfTenured " + countOfTenured);
					System.out.println("countTenuredOnly " + countTenuredOnly);
				}
			}

		}
		
		
	*/
}
