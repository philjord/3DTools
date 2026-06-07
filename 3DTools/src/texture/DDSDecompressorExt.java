package texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.jogamp.java3d.NioImageBuffer;
import org.jogamp.java3d.NioImageBuffer.ImageType;

import com.jogamp.opengl.GL;

import compressedtexture.DDSImage;

/**
 * Extension to the base DDSDecompressor, to get it working, I'll merge them later later NOTE: no BufferedImage in this
 * one, put them back later Converts DDS files and streams into {@code NioImageBuffer}
 * {@link http://en.wikipedia.org/wiki/S3_Texture_Compression}
 */
public class DDSDecompressorExt {
	public static final int		BLOCK_SIZE	= 4;

	public DDSImage				ddsImage;

	private int					mipNumber;

	private DDSImage.ImageInfo	imageInfo;

	private ByteBuffer			buffer;

	private boolean				ignoreAlpha	= false;

	private int					width;

	private int					height;

	private String				imageName	= "";

	private boolean				opaque		= true;

	/**
	 * 
	 * @param ddsImage
	 * @param mipNumber
	 * @param imageName
	 */
	public DDSDecompressorExt(DDSImage ddsImage, int mipNumber, String imageName) {
		this.imageName = imageName;
		this.ddsImage = ddsImage;
		this.mipNumber = mipNumber;
		this.imageInfo = ddsImage.getAllMipMaps()[mipNumber];
		this.width = imageInfo.getWidth();
		this.height = imageInfo.getHeight();
		this.buffer = imageInfo.getData();
		this.opaque = true;
	}

	public String getImageName() {
		return imageName;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMipNumber() {
		return mipNumber;
	}

	/**
	 * Can only be queried after decompression
	 * @return
	 */
	public boolean decompressedIsOpaque() {
		return opaque;
	}

	/**
	 * 
	 * @return a new non-cached {@code BufferedImage}
	 */
	public NioImageBuffer convertImageNio() {

		//can't use width or height as it's been corrected to 1 already
		if (imageInfo.getWidth() < 1 || imageInfo.getHeight() < 1) {
			return new NioImageBuffer(1, 1, ImageType.TYPE_INT_ARGB);
		}

		//prep the buffer
		buffer.rewind();
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		int fmt = ddsImage.getPixelFormat();
		if (fmt == DDSImage.D3DFMT_DXT1 || fmt == DDSImage.D3DFMT_DXT2) {
			//Notice DXT2 is just premulitpied alpha versions of 1 
			return decodeDxt1BufferNio();			
		} else if (fmt == DDSImage.D3DFMT_DXT3|| fmt == DDSImage.D3DFMT_DXT4) {
			//Notice DXT4 is just premulitpied alpha versions of 3 
			return decodeDxt3BufferNio();
		} else if (fmt == DDSImage.D3DFMT_DXT5) {
			return decodeDxt5BufferNio();
		} else if (fmt == DDSImage.D3DFMT_R8G8B8) {
			return decodeR8G8B8Nio();
		} else if (fmt == DDSImage.D3DFMT_L8) {
			return decodeL8Nio();
		} else if (fmt == DDSImage.D3DFMT_A8L8) {
			return decodeA8L8Nio();
		} else if (fmt == DDSImage.D3DFMT_A4R4G4B4) {
			return decodeA4R4G4B4Nio();
		} else if (fmt == DDSImage.D3DFMT_A8R8G8B8) {
			return decodeARGB8Nio();
		} else if (fmt == DDSImage.D3DFMT_A8B8G8R8) {
			return decodeA8B8G8R8Nio();
		} else if (fmt == DDSImage.D3DFMT_X8R8G8B8) {
			return decodeARGB8Nio();
		} else if (fmt == DDSImage.D3DFMT_A16B16G16R16F) {
			return decodeRGBA16FNio();
		} else if (fmt == DDSImage.D3DFMT_A16B16G16R16) {
			return decodeRGBA16Nio();
		} else if (fmt == DDSImage.D3DFMT_R5G6B5) {
			return decodeR5G6B5Nio();
		} else if (fmt == DDSImage.D3DFMT_ATI1 || fmt == DDSImage.D3DFMT_BC4U) {
			//Equivalent as seen on this page
			//https://learn.microsoft.com/en-us/windows/win32/direct3d11/texture-block-compression-in-direct3d-11
			//https://learn.microsoft.com/en-us/windows/win32/direct3d10/d3d10-graphics-programming-guide-resources-block-compression
			return decodeBC4UNio();
		} else if (fmt == DDSImage.D3DFMT_BC4S) {
			return decodeBC4SNio();
		} else if (fmt == DDSImage.D3DFMT_ATI2 || fmt == DDSImage.D3DFMT_BC5U) {
			return decodeBC5UNio();
		} else if (fmt == DDSImage.D3DFMT_BC5S) {
				return decodeBC5SNio();
		} else if (fmt == DDSImage.D3DFMT_DX10) {
			//D3DFMT_DX10 - this isn't a thing, need to investigate where the foramt is 
			// cos directX 10 has BC4 and BC5 then Direct X 11 add BC6H and BC7 crazy foramts
						
			//BC6H
			//https://learn.microsoft.com/en-us/windows/win32/direct3d11/bc6h-format
			
			//BC7
			//https://learn.microsoft.com/en-us/windows/win32/direct3d11/bc7-format
			System.err.println(" DXT10+ format!! NioImageBuffer convertImageNio() " + ddsImage.getPixelFormat()+ " "
					+ ddsImageFromat(ddsImage.getPixelFormat()));
			return null;
		} else {
			// Here is the best info of all, not the formats but the concepts
			//https://en.wikipedia.org/wiki/S3_Texture_Compression
			
			//Possibly we have got an L8 format, 
			//D:\game_media\Oblivion\Oblivion - Textures - Compressed\textures\architecture\anvil\arcanesymbol01_g.dds
			//L8 seems to have a single channel that's repeat across RGB to show are a grey scale results
			//_g.dds must be glow

			//https://www.gamedev.net/forums/topic/575505-d3dfmt_l8-to-argb-color/575505/
			//https://learn.microsoft.com/en-us/windows/win32/direct3d9/d3dformat
			System.err.println("BAD DXT format!! NioImageBuffer convertImageNio() " + ddsImage.getPixelFormat() + " "
								+ ddsImageFromat(ddsImage.getPixelFormat()));

			return null;
		}
	}

	private NioImageBuffer decodeR5G6B5Nio() {
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 3).order(ByteOrder.nativeOrder());

		Color24 c = new Color24();

		// reverse to flip Y 
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				//header.pfRBitMask == 0xF800 && header.pfGBitMask == 0x07E0&& header.pfBBitMask == 0x001F)
				Color24.fromShort565(c, buffer.getShort());
				directBuffer.put((byte)c.r);
				directBuffer.put((byte)c.g);
				directBuffer.put((byte)c.b);
			}
		}

		return new NioImageBuffer(width, height, ImageType.TYPE_3BYTE_RGB, directBuffer);
	}

	private NioImageBuffer decodeR8G8B8Nio() {
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 3).order(ByteOrder.nativeOrder());

		// reverse to flip Y
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				directBuffer.put(buffer.get());
				directBuffer.put(buffer.get());
				directBuffer.put(buffer.get());
			}
		}
		//NOTE disagrees with fixed getType below
		return new NioImageBuffer(width, height, ImageType.TYPE_3BYTE_RGB, directBuffer);
	}

	private NioImageBuffer decodeL8Nio() {
		ByteBuffer pixels = ByteBuffer.allocateDirect(width * height * 1).order(ByteOrder.nativeOrder());

		// reverse to flip Y
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				pixels.put(buffer.get());
			}
		}
		return new NioImageBuffer(width, height, ImageType.TYPE_BYTE_GRAY, pixels);
	}

	private NioImageBuffer decodeA8L8Nio() {
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		IntBuffer pixels = directBuffer.asIntBuffer();

		// reverse to flip Y - noting I'm undoing the reverse just below?? odd?
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				byte A = buffer.get();
				byte L = buffer.get();

				int pixel8888 = (L & 0xff) << 24 | (L & 0xff) << 16 | (L & 0xff) << 8 | (A & 0xff) << 0;
				pixels.put((y * width) + x, pixel8888);
			}
		}

		//wastefully repeat L8 into the RGB slots
		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, pixels);
	}

	private NioImageBuffer decodeA4R4G4B4Nio() {
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		IntBuffer pixels = directBuffer.asIntBuffer();

		// reverse to flip Y
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {

				byte bg = buffer.get();
				int b = ((bg & 0xF0) >> 4) * 17;
				int g = (bg & 0x0F) * 17;
				byte ra = buffer.get();
				int r = ((ra & 0xF0) >> 4) * 17;
				int a = (ra & 0x0F) * 17;

				int pixel8888 = (r & 0xff) << 24 | (g & 0xff) << 16 | (b & 0xff) << 8 | (a & 0xff) << 0;
				pixels.put(pixel8888);
			}
		}

		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	private NioImageBuffer decodeARGB8Nio() {
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		// reverse to flip Y
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				byte a = buffer.get(); // swapped to end			
				directBuffer.put(buffer.get());
				directBuffer.put(buffer.get());
				directBuffer.put(buffer.get());
				directBuffer.put(a);
			}
		}
		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	private NioImageBuffer decodeA8B8G8R8Nio() {
		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		// reverse to flip Y
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				byte a = buffer.get(); // swapped to end			
				byte b = buffer.get();
				byte g = buffer.get();
				byte r = buffer.get();
				directBuffer.put(r);
				directBuffer.put(g);
				directBuffer.put(b);
				directBuffer.put(a);
			}
		}
		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	private NioImageBuffer decodeRGBA16FNio() {
		//https://moderngl.readthedocs.io/en/latest/topics/texture_formats.html		

		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		IntBuffer pixelBuffer = directBuffer.asIntBuffer();

		//maxr = 6024.0 maxg = 6008.0 maxb = 5912.0 maxa = 1.000122
		//maxr = 30.65625 maxg = 64.25 maxb = 64.25 maxa = 1.000122
		//maxr = 209.0 maxg = 258.75 maxb = 266.5 maxa = 1.000122
		//float maxr = 0;	float maxg = 0;	float maxb = 0;	float maxa = 0;
		float scale = 6042; // by empirical testing I get lots of different numbers
		for (int y = height - 1; y >= 0; y--) {
			//System.out.println("y = " + y);
			for (int x = 0; x < width; x++) {

				// set of numbers like this  68.3125, 167.625, 291.0, 1.000122
				// or 0.0028018951 0.0025596619 0.0023937225 1.000122, 
				// so nice precision on the blacks for non moiring black				

				// perhaps my half float code isn't exactly the RGBA16F code?
				//https://stackoverflow.com/questions/28357977/gl-half-float-with-opengl-rendering-and-glsl

				float r = FP16.toFloat((buffer.getShort())) / scale;
				float g = FP16.toFloat((buffer.getShort())) / scale;
				float b = FP16.toFloat((buffer.getShort())) / scale;
				float a = FP16.toFloat((buffer.getShort()));

				//maxr=r>maxr?r:maxr;	maxg=g>maxg?g:maxg;	maxb=b>maxb?b:maxb;	maxa=a>maxa?a:maxa;

				int cr = (int)(r * 255 + 0.5);
				int cg = (int)(g * 255 + 0.5);
				int cb = (int)(b * 255 + 0.5);
				int ca = (int)(a * 255 + 0.5);
				int pixel8888 = (ca << 24) | (cb << 16) | (cg << 8) | (cr << 0);
				pixelBuffer.put(pixel8888);
			}
		}

		//System.out.println("maxr = " + maxr + " maxg = " + maxg + " maxb = " + maxb + " maxa = " + maxa );

		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	//TODO: this is untested
	private NioImageBuffer decodeRGBA16Nio() {

		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		IntBuffer pixelBuffer = directBuffer.asIntBuffer();

		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				byte r = (byte)(buffer.getShort() / 255);
				byte g = (byte)(buffer.getShort() / 255);
				byte b = (byte)(buffer.getShort() / 255);
				byte a = (byte)(buffer.getShort() / 255);

				int pixel8888 = (a << 24) | (b << 16) | (g << 8) | (r << 0);
				pixelBuffer.put(pixel8888);
			}
		}
		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	private NioImageBuffer decodeDxt1BufferNio() {
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		IntBuffer pixels = directBuffer.asIntBuffer();

		//One copy of table to minimises new calls
		Color24[] table = new Color24[4];
		table[0] = new Color24();
		table[1] = new Color24();
		table[2] = new Color24();
		table[3] = new Color24();

		for (int row = 0; row < numBlocksHigh; row++) {
			for (int col = 0; col < numBlocksWide; col++) {
				short c0 = buffer.getShort();
				short c1 = buffer.getShort();
				int colorIndexMask = buffer.getInt();

				//http://en.wikipedia.org/wiki/S3_Texture_Compression
				if (ignoreAlpha || !Color24.hasAlphaBit(c0, c1)) {
					Color24[] lookupTable = Color24.expandLookupTable(table, c0, c1);
					for (int br = 0; br < blockHeight; br++) {
						for (int bc = 0; bc < blockWidth; bc++) {
							int k = (br * blockWidth) + bc;
							int colorIndex = (colorIndexMask >>> k * 2) & 0x03;

							Color24 color = lookupTable[colorIndex];
							int pixel8888 = (0xFF << 24) | (color.b << 16) | (color.g << 8) | (color.r << 0);

							pixels.put(((row * blockHeight) * (numBlocksWide * blockWidth)) // previous columns and row
										+ (br * (numBlocksWide * blockWidth)) + (col * blockWidth) + bc,
									pixel8888);
						}
					}
				} else {
					Color24[] lookupTable = Color24.expandLookupTableAlphable(table, c0, c1);
					for (int br = 0; br < blockHeight; br++) {
						for (int bc = 0; bc < blockWidth; bc++) {
							int k = (br * blockWidth) + bc;
							int colorIndex = (colorIndexMask >>> k * 2) & 0x03;
							int alpha = (colorIndex == 3) ? 0x00 : 0xFF;

							//255 pixel is opaque
							if (alpha != 255)
								opaque = false;

							Color24 color = lookupTable[colorIndex];
							int pixel8888 = (alpha << 24) | (color.b << 16) | (color.g << 8) | (color.r << 0);

							pixels.put(((row * blockHeight) * (numBlocksWide * blockWidth)) // previous columns and row
										+ (br * (numBlocksWide * blockWidth)) + (col * blockWidth) + bc,
									pixel8888);
						}
					}
				}
			}
		}
		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	private NioImageBuffer decodeDxt3BufferNio() {
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		IntBuffer pixels = directBuffer.asIntBuffer();

		//One copy of table to minimises new calls
		Color24[] table = new Color24[4];
		table[0] = new Color24();
		table[1] = new Color24();
		table[2] = new Color24();
		table[3] = new Color24();

		for (int row = 0; row < numBlocksHigh; row++) {
			for (int col = 0; col < numBlocksWide; col++) {
				long alphaData = buffer.getLong();
				short minColor = buffer.getShort();
				short maxColor = buffer.getShort();
				int colorIndexMask = buffer.getInt();

				//-1 is all bits on which is 255 for all pixels or opaque
				if (alphaData != -1)
					opaque = false;

				Color24[] lookupTable = Color24.expandLookupTable(table, minColor, maxColor);

				for (int br = 0; br < blockHeight; br++) {
					for (int bc = 0; bc < blockWidth; bc++) {
						int k = (br * blockWidth) + bc;
						int alpha = (int)(alphaData >>> (k * 4)) & 0xF; // Alphas are just 4 bits per pixel
						// the original code is like *16 =>   alpha <<= 4;
						// but 0-15 needs *17 for 15==255
						// Here, we should really multiply by 17 instead of 16. This can
						// be done by just copying the four lower bits to the upper ones
						// while keeping the lower bits.
						alpha = (byte)(alpha | (alpha << 4));

						int colorIndex = (colorIndexMask >>> k * 2) & 0x03;

						Color24 color = lookupTable[colorIndex];

						//0xFF0000FF = red
						//0xFF00FF00 = green						
						//0xFFFF0000 = blue
						//ABGR! so can't use the color24 ARGB system
						int pixel8888 = (alpha << 24) | (color.b << 16) | (color.g << 8) | (color.r << 0);

						pixels.put(((row * blockHeight) * (numBlocksWide * blockWidth)) // previous columns and row
									+ (br * (numBlocksWide * blockWidth)) + (col * blockWidth) + bc,
								pixel8888);
					}
				}
				//notice notice no flipping for theNioImageBuffer version!
			}

		}

		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	private NioImageBuffer decodeDxt5BufferNio() {
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
		IntBuffer pixels = directBuffer.asIntBuffer();

		//One copy of table to minimises new calls
		Color24[] table = new Color24[4];
		table[0] = new Color24();
		table[1] = new Color24();
		table[2] = new Color24();
		table[3] = new Color24();

		for (int row = 0; row < numBlocksHigh; row++) {
			for (int col = 0; col < numBlocksWide; col++) {
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

				for (int br = 0; br < blockHeight; br++) {
					for (int bc = 0; bc < blockWidth; bc++) {
						int k = (br * blockWidth) + bc;

						int alphaCode = (int)(alphaBits >> (3 * k) & 0x07); // bottom 3 bits

						int alpha = 0;

						if (alphaCode == 0) {
							alpha = alpha0;
						} else if (alphaCode == 1) {
							alpha = alpha1;
						} else if (alpha0 > alpha1) {
							alpha = ((8 - alphaCode) * alpha0 + (alphaCode - 1) * alpha1) / 7;
						} else {
							if (alphaCode == 6)
								alpha = 0;
							else if (alphaCode == 7)
								alpha = 255;
							else
								alpha = ((6 - alphaCode) * alpha0 + (alphaCode - 1) * alpha1) / 5;

						}

						//255 pixel is opaque
						if (alpha != 255)
							opaque = false;

						int colorIndex = (colorIndexMask >>> k * 2) & 0x03;

						Color24 color = lookupTable[colorIndex];
						int pixel8888 = (alpha << 24) | (color.b << 16) | (color.g << 8) | (color.r << 0);

						pixels.put(((row * blockHeight) * (numBlocksWide * blockWidth)) // previous columns and row
									+ (br * (numBlocksWide * blockWidth)) + (col * blockWidth) + bc,
								pixel8888);
					}
				}
			}

		}
		return new NioImageBuffer(width, height, ImageType.TYPE_4BYTE_RGBA, directBuffer);
	}

	private NioImageBuffer decodeBC4UNio() {
		return decodeBC4Nio(false);
	}

	private NioImageBuffer decodeBC4SNio() {
		return decodeBC4Nio(true);
	}

	private NioImageBuffer decodeBC4Nio(boolean signed) {

		//https://learn.microsoft.com/en-us/windows/win32/direct3d10/d3d10-graphics-programming-guide-resources-block-compression
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		ByteBuffer pixels = ByteBuffer.allocateDirect(width * height * 1).order(ByteOrder.nativeOrder());
		
		for (int row = 0; row < numBlocksHigh; row++) {
			for (int col = 0; col < numBlocksWide; col++) {
				int red0 = buffer.get(); //signed byte
				int red1 = buffer.get(); //signed byte
				
				if(!signed) {
					red0 &= 0xff; //unsigned byte
					red1 &= 0xff; //unsigned byte
				}
					

				// next 6 bytes are a look up list (note long casts, important!)
				long redBits = (buffer.get() & 0xffL) << 0L //
									| (buffer.get() & 0xffL) << 8L //
									| (buffer.get() & 0xffL) << 16L//
									| (buffer.get() & 0xffL) << 24L //
									| (buffer.get() & 0xffL) << 32L //
									| (buffer.get() & 0xffL) << 40L;//
			

				for (int br = 0; br < blockHeight; br++) {
					for (int bc = 0; bc < blockWidth; bc++) {
						int k = (br * blockWidth) + bc;

						int redCode = (int)(redBits >> (3 * k) & 0x07); // bottom 3 bits

						int red = 0;

						if (redCode == 0) {
							red = red0;
						} else if (redCode == 1) {
							red = red1;
						} else if (red0 > red1) {
							red = ((8 - redCode) * red0 + (redCode - 1) * red1) / 7;
						} else {
							if (redCode == 6)
								red = signed ? -127 : 0;
							else if (redCode == 7)
								red =  signed ? 126 : 255;
							else
								red = ((6 - redCode) * red0 + (redCode - 1) * red1) / 5;
						}
					
						//FIXME: should I ever keep -1 to 1 range? also test signed data not tested
						//normalised -1 to 1 range of SNORM to 0 to 1
						if(signed)
							red += 255;
						
						pixels.put(((row * blockHeight) * (numBlocksWide * blockWidth)) // previous columns and row
									+ (br * (numBlocksWide * blockWidth)) + (col * blockWidth) + bc,
								(byte)red); 
					}
				}
			}

		}
		return new NioImageBuffer(width, height, ImageType.TYPE_BYTE_GRAY, pixels);
	}

	//https://en.wikipedia.org/wiki/S3_Texture_Compression
	//BC5 	Interpolated two-channel
	private NioImageBuffer decodeBC5UNio() {
		return decodeBC4Nio(false);
	}

	private NioImageBuffer decodeBC5SNio() {
		return decodeBC4Nio(true);
	}

	private NioImageBuffer decodeBC5Nio(boolean signed) {

		//https://learn.microsoft.com/en-us/windows/win32/direct3d10/d3d10-graphics-programming-guide-resources-block-compression
		int numBlocksWide = width / BLOCK_SIZE;
		int numBlocksHigh = height / BLOCK_SIZE;

		// always at least 1x1 tile
		numBlocksWide = numBlocksWide < 1 ? 1 : numBlocksWide;
		numBlocksHigh = numBlocksHigh < 1 ? 1 : numBlocksHigh;

		int blockWidth = Math.min(width, BLOCK_SIZE);
		int blockHeight = Math.min(height, BLOCK_SIZE);

		ByteBuffer directBuffer = ByteBuffer.allocateDirect(width * height * 2).order(ByteOrder.nativeOrder());
		ShortBuffer pixels = directBuffer.asShortBuffer();
		
		for (int row = 0; row < numBlocksHigh; row++) {
			for (int col = 0; col < numBlocksWide; col++) {
				int red0 = buffer.get(); //signed byte
				int red1 = buffer.get(); //signed byte
				
				if(!signed) {
					red0 &= 0xff; //unsigned byte
					red1 &= 0xff; //unsigned byte
				}
					

				// next 6 bytes are a look up list (note long casts, important!)
				long redBits = (buffer.get() & 0xffL) << 0L //
									| (buffer.get() & 0xffL) << 8L //
									| (buffer.get() & 0xffL) << 16L//
									| (buffer.get() & 0xffL) << 24L //
									| (buffer.get() & 0xffL) << 32L //
									| (buffer.get() & 0xffL) << 40L;//
				
				int green0 = buffer.get(); //signed byte
				int green1 = buffer.get(); //signed byte
				
				if(!signed) {
					green0 &= 0xff; //unsigned byte
					green1 &= 0xff; //unsigned byte
				}
					

				// next 6 bytes are a look up list (note long casts, important!)
				long greenBits = (buffer.get() & 0xffL) << 0L //
									| (buffer.get() & 0xffL) << 8L //
									| (buffer.get() & 0xffL) << 16L//
									| (buffer.get() & 0xffL) << 24L //
									| (buffer.get() & 0xffL) << 32L //
									| (buffer.get() & 0xffL) << 40L;//
			

				for (int br = 0; br < blockHeight; br++) {
					for (int bc = 0; bc < blockWidth; bc++) {
						int k = (br * blockWidth) + bc;

						int redCode = (int)(redBits >> (3 * k) & 0x07); // bottom 3 bits

						int red = 0;

						if (redCode == 0) {
							red = red0;
						} else if (redCode == 1) {
							red = red1;
						} else if (red0 > red1) {
							red = ((8 - redCode) * red0 + (redCode - 1) * red1) / 7;
						} else {
							if (redCode == 6)
								red = signed ? -127 : 0;
							else if (redCode == 7)
								red =  signed ? 126 : 255;
							else
								red = ((6 - redCode) * red0 + (redCode - 1) * red1) / 5;
						}
						
						int greenCode = (int)(greenBits >> (3 * k) & 0x07); // bottom 3 bits

						int green = 0;

						if (greenCode == 0) {
							green = red0;
						} else if (greenCode == 1) {
							green = green1;
						} else if (green0 > green1) {
							green = ((8 - greenCode) * green0 + (greenCode - 1) * green1) / 7;
						} else {
							if (greenCode == 6)
								green = signed ? -127 : 0;
							else if (greenCode == 7)
								green =  signed ? 126 : 255;
							else
								green = ((6 - greenCode) * green0 + (greenCode - 1) * green1) / 5;
						}
						
					
						//FIXME: should I ever keep -1 to 1 range? also test signed data not tested
						//normalised -1 to 1 range of SNORM to 0 to 1
						if(signed) {
							red += 255;
							green += 255;
						}
						
						short pixel88 = (short)((green << 8) | (red << 0));
						pixels.put(((row * blockHeight) * (numBlocksWide * blockWidth)) // previous columns and row
									+ (br * (numBlocksWide * blockWidth)) + (col * blockWidth) + bc,
									pixel88); 
					}
				}
			}

		}
		return new NioImageBuffer(width, height, ImageType.TYPE_BYTE_RG, pixels);
	}

	/**
	 * 24 bit 888 RGB color
	 *
	 * @author Lado Garakanidze
	 * @version $Id$
	 */

	public static class Color24 {
		/**
		 * The red color component.
		 */
		public int	r		= 0;

		/**
		 * The green color component.
		 */
		public int	g		= 0;

		/**
		 * The blue color component.
		 */
		public int	b		= 0;

		// as a regular pixel 888 format
		public int	pix888	= 0;

		/**
		 * Creates a 24 bit 888 RGB color with all values set to 0.
		 */
		public Color24() {

		}

		public void set(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
			pix888 = (r << 16 | g << 8 | b);
		}

		private static Color24 fromShort565(Color24 out, short pixel) {
			//out.set((int) (((long) pixel) & 0xf800) >>> 8, (int) (((long) pixel) & 0x07e0) >>> 3, (int) (((long) pixel) & 0x001f) << 3);
			out.set((pixel & 0xf800) >>> 8, (pixel & 0x07e0) >>> 3, (pixel & 0x001f) << 3);
			return out;
		}

		/**
		 * for DXT1 only the short need to be treated as unsigned and value compared c0 and c1 is a signed short, but we
		 * need to treat it as unsigned for comparision (it's 16 bits of info not a short at all)
		 * @param c0
		 * @param c1
		 * @return
		 */
		public static boolean hasAlphaBit(short c0, short c1) {
			// & 0xFFFF makes it an int of unsigned short value
			return (c0 & 0xFFFF) <= (c1 & 0xFFFF);
		}

		/**
		 * for DXT1 only If inC0 > inC1 c4 will be left as black to be used as full transparent
		 * http://en.wikipedia.org/wiki/S3_Texture_Compression
		 * @param inC0
		 * @param inC1
		 * @return
		 */
		public static Color24[] expandLookupTableAlphable(Color24[] out, short inC0, short inC1) {
			fromShort565(out[0], inC0);
			fromShort565(out[1], inC1);
			out[2].set((out[0].r + out[1].r) / 2, (out[0].g + out[1].g) / 2, (out[0].b + out[1].b) / 2);
			out[3].set(0, 0, 0);

			return out;
		}

		public static Color24[] expandLookupTable(Color24[] out, short inC0, short inC1) {
			fromShort565(out[0], inC0);
			fromShort565(out[1], inC1);
			out[2].set((2 * out[0].r + out[1].r) / 3, (2 * out[0].g + out[1].g) / 3, (2 * out[0].b + out[1].b) / 3);
			out[3].set((out[0].r + 2 * out[1].r) / 3, (out[0].g + 2 * out[1].g) / 3, (out[0].b + 2 * out[1].b) / 3);
			return out;

		}

	}
	
	/**
	 * COpied from DDSImage.debugPrint for convnience, keep u to date
	 * @param fmt
	 * @return
	 */
	private String ddsImageFromat(int fmt)
	{
		switch (fmt)
		{
			case DDSImage.D3DFMT_R8G8B8:
				return "D3DFMT_R8G8B8";
			case DDSImage.D3DFMT_A8R8G8B8:
				return "D3DFMT_A8R8G8B8";
			case DDSImage.D3DFMT_A8B8G8R8:
				return "D3DFMT_A8B8G8R8";
			case DDSImage.D3DFMT_X8R8G8B8:
				return "D3DFMT_X8R8G8B8";
			case DDSImage.D3DFMT_A16B16G16R16F:
				return "D3DFMT_A16B16G16R16F";
			case DDSImage.D3DFMT_A16B16G16R16:
				return "D3DFMT_A16B16G16R16";
			case DDSImage.D3DFMT_DXT1:
				return "D3DFMT_DXT1";
			case DDSImage.D3DFMT_DXT2:
				return "D3DFMT_DXT2";
			case DDSImage.D3DFMT_DXT3:
				return "D3DFMT_DXT3";
			case DDSImage.D3DFMT_DXT4:
				return "D3DFMT_DXT4";
			case DDSImage.D3DFMT_DXT5:
				return "D3DFMT_DXT5";
			case DDSImage.D3DFMT_ATI1:
				return "D3DFMT_ATI1";
			case DDSImage.D3DFMT_BC4U:
				return "D3DFMT_BC4U";
			case DDSImage.D3DFMT_BC4S:
				return "D3DFMT_BC4S";
			case DDSImage.D3DFMT_ATI2:
				return "D3DFMT_ATI2";
			case DDSImage.D3DFMT_BC5U:
				return "D3DFMT_BC5U";
			case DDSImage.D3DFMT_BC5S:
				return "D3DFMT_BC5S";
			case DDSImage.D3DFMT_L8:
				return "D3DFMT_L8";
			case DDSImage.D3DFMT_UNKNOWN:
				return "D3DFMT_UNKNOWN";
			case GL.GL_R8:
				return " GL.GL_R8 / DXGI_FORMAT_R8_UNORM";
			case DDSImage.D3DFMT_DX10:
				return "D3DFMT_DX10";
			default:
				return "(unknown pixel format " + fmt + ")";
		} 
	}

}
