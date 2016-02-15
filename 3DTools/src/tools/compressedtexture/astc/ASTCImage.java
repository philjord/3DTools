package tools.compressedtexture.astc;

import java.nio.ByteBuffer;

public class ASTCImage
{
	/*
	http://stackoverflow.com/questions/22600678/determine-internal-format-of-given-astc-compressed-image-through-its-header
	 struct astc_header
	{
	uint8_t magic [ 4 ];
	uint8_t blockdim_x;
	uint8_t blockdim_y;
	uint8_t blockdim_z ;
	uint8_t xsize [ 3 ];
	uint8_t ysize [ 3 ];
	uint8_t zsize [ 3 ];
	};
	
	notice this does not describe sRGB or linear
	internalFormat information obtained to pass my ASTC texture to glCompressedTexImage2D(). 
	In other words, for eg. I want to know whether my internal format is 
	COMPRESSED_RGBA_ASTC_4x4_KHR 
	or
	COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR 
	from the header of any ASTC compressed image. Any clues?
	
	source code! woot
	https://github.com/ARM-software/astc-encoder
	 */

	public AstcHeader hdr;
	public ByteBuffer byteBuffer;

	public static int MAGIC_FILE_CONSTANT = 0x5CA1AB13;//1554098963

	public static class AstcHeader
	{
		//uint8_t = unsigned char!
		//uint8_t magic[4];
		public int magic = -1;
		//uint8_t blockdim_x;
		public int blockdim_x = 0;
		//uint8_t blockdim_y;
		public int blockdim_y = 0;
		//uint8_t blockdim_z;
		public int blockdim_z = 0;
		//uint8_t xsize[3];			// x-size = xsize[0] + xsize[1] + xsize[2]
		public int xsize = 0;
		//uint8_t ysize[3];			// x-size, y-size and z-size are given in texels;
		public int ysize = 0;
		//uint8_t zsize[3];			// block count is inferred
		public int zsize = 0;

		//derived block count
		public int xblocks;
		public int yblocks;
		public int zblocks;

		public String toString()
		{
			return "ASTC Image magic " + magic + " blockdim_x "//
					+ blockdim_x + " blockdim_y "//
					+ blockdim_y + " blockdim_z "//
					+ blockdim_z + " xsize "//
					+ xsize + " ysize "//
					+ ysize + " zsize "//
					+ zsize + " xblocks "//
					+ xblocks + " yblocks "//
					+ yblocks + " zblocks "//
					+ zblocks;

		}
	}

	public ASTCImage(ByteBuffer bb)
	{
		this.hdr = new AstcHeader();
		this.byteBuffer = bb;

		byte[] tmp = new byte[4];
		bb.get(tmp);
		hdr.magic = tmp[0] + 256 * (tmp[1] & 0xff) + 65536 * (tmp[2] & 0xff) + 16777216 * (tmp[3] & 0xff);

		//hdr.magic = bb.getInt();

		if (hdr.magic != MAGIC_FILE_CONSTANT)
		{

			System.out.println("File not recognized\n");
			return;
		}

		hdr.blockdim_x = (bb.get() & 0xff);
		hdr.blockdim_y = (bb.get() & 0xff);
		hdr.blockdim_z = (bb.get() & 0xff);

		int xdim = hdr.blockdim_x;
		int ydim = hdr.blockdim_y;
		int zdim = hdr.blockdim_z;

		if (xdim < 3 || xdim > 12 || ydim < 3 || ydim > 12 || (zdim < 3 && zdim != 1) || zdim > 12)
		{

			System.out.println("File %s not recognized %d %d %d\n" + xdim + " " + ydim + " " + zdim);
			return;
		}

		tmp = new byte[3];
		bb.get(tmp);
		hdr.xsize = (tmp[0] & 0xff) + 256 * (tmp[1] & 0xff) + 65536 * (tmp[2] & 0xff);
		bb.get(tmp);
		hdr.ysize = (tmp[0] & 0xff) + 256 * (tmp[1] & 0xff) + 65536 * (tmp[2] & 0xff);
		bb.get(tmp);
		hdr.zsize = (tmp[0] & 0xff) + 256 * (tmp[1] & 0xff) + 65536 * (tmp[2] & 0xff);

		int xsize = hdr.xsize;
		int ysize = hdr.ysize;
		int zsize = hdr.zsize;

		hdr.xblocks = (xsize + xdim - 1) / xdim;
		hdr.yblocks = (ysize + ydim - 1) / ydim;
		hdr.zblocks = (zsize + zdim - 1) / zdim;

		// in theory I can now hand the buffer including the header to opengl and all is good???

		bb.rewind();
	}

	public int getNumMipMaps()
	{
		// no mip maps in these files
		return 0;
	}

	public int getWidth()
	{
		return hdr.xsize;
	}

	public int getHeight()
	{
		return hdr.ysize;
	}
}
