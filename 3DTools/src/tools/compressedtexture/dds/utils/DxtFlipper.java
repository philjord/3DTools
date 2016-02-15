package tools.compressedtexture.dds.utils;

import java.nio.ByteBuffer;

import tools.compressedtexture.dds.DDSImage;
import tools.compressedtexture.dds.DDSImage.ImageInfo;

/**
 * This can be used to flip a Dxt image upside down in order to ensure y-up. It is not needed by any projects currently.
 * @author phil
 *
 */
public class DxtFlipper
{

	static void FlipDXT1BlockFull(byte[] block, int p)
	{
		// A DXT1 block layout is:
		// [0-1] color0.
		// [2-3] color1.
		// [4-7] color bitmap, 2 bits per pixel.
		// So each of the 4-7 bytes represents one line, flipping a block is just
		// flipping those bytes.
		// Note that http://src.chromium.org/viewvc/chrome/trunk/src/o3d/core/cross/bitmap_dds.cc?view=markup&pathrev=21227
		// contains an error in the last line: data[6]=data[5] is a bug!
		byte tmp;

		tmp = block[p + 4];
		block[p + 4] = block[p + 7];
		block[p + 7] = tmp;

		tmp = block[p + +5];
		block[p + 5] = block[p + 6];
		block[p + 6] = tmp;

	}

	// Flips a full DXT3 block in the y direction.
	static void FlipDXT3BlockFull(byte[] block, int p)
	{
		// A DXT3 block layout is:
		// [0-7]  alpha bitmap, 4 bits per pixel.
		// [8-15] a DXT1 block.

		// We can flip the alpha bits at the byte level (2 bytes per line).
		byte tmp = block[p + 0];
		block[p + 0] = block[p + 6];
		block[p + 6] = tmp;
		tmp = block[p + 1];
		block[p + 1] = block[p + 7];
		block[p + 7] = tmp;
		tmp = block[p + 2];
		block[p + 2] = block[p + 4];
		block[p + 4] = tmp;
		tmp = block[p + 3];
		block[p + 3] = block[p + 5];
		block[p + 5] = tmp;

		// And flip the DXT1 block using the above function.
		FlipDXT1BlockFull(block, p + 8);
	}

	// From http://src.chromium.org/viewvc/chrome/trunk/src/o3d/core/cross/bitmap_dds.cc?view=markup&pathrev=21227
	// Original source contained bugs; fixed here, and altered to be java
	static void FlipDXT5BlockFull(byte[] block, int p)
	{

		// A DXT5 block layout is:
		// [0]    alpha0.
		// [1]    alpha1.
		// [2-7]  alpha bitmap, 3 bits per pixel.
		// [8-15] a DXT1 block.

		// The alpha bitmap doesn't easily map lines to bytes, so we have to
		// interpret it correctly.  Extracted from
		// http://www.opengl.org/registry/specs/EXT/texture_compression_s3tc.txt :
		//
		//   The 6 "bits" bytes of the block are decoded into one 48-bit integer:
		//
		//     bits = bits_0 + 256 * (bits_1 + 256 * (bits_2 + 256 * (bits_3 +
		//                   256 * (bits_4 + 256 * bits_5))))
		//
		//   bits is a 48-bit unsigned integer, from which a three-bit control code
		//   is extracted for a texel at location (x,y) in the block using:
		//
		//       code(x,y) = bits[3*(4*y+x)+1..3*(4*y+x)+0]
		//
		//   where bit 47 is the most significant and bit 0 is the least
		//   significant bit.
		//QBitDump(block+2,6);

		int line_0_1 = (int) ((block[p + 2] & 0xffL) << 0L //
				| (block[p + 3] & 0xffL) << 8L //
		| (block[p + 4] & 0xffL) << 16L);

		int line_2_3 = (int) ((block[p + 5] & 0xffL) << 0L //
				| (block[p + 6] & 0xffL) << 8L //
		| (block[p + 7] & 0xffL) << 16L);//

		// swap lines 0 and 1 in line_0_1.
		int line_1_0 = ((line_0_1 & 0x000fff) << 12) | ((line_0_1 & 0xfff000) >> 12);
		// swap lines 2 and 3 in line_2_3.
		int line_3_2 = ((line_2_3 & 0x000fff) << 12) | ((line_2_3 & 0xfff000) >> 12);

		block[p + 2] = (byte) (line_3_2 & 0xff);
		block[p + 3] = (byte) ((line_3_2 & 0xff00) >> 8);
		block[p + 4] = (byte) ((line_3_2 & 0xff0000) >> 16);
		block[p + 5] = (byte) (line_1_0 & 0xff);
		block[p + 6] = (byte) ((line_1_0 & 0xff00) >> 8);
		block[p + 7] = (byte) ((line_1_0 & 0xff0000) >> 16);

		// And flip the DXT1 block using the above function.		
		FlipDXT1BlockFull(block, p + 8);
	}

	public static void flip(DDSImage ddsImage, ImageInfo imageInfo)
	{
		int pixelFormat = ddsImage.getPixelFormat();
		if (pixelFormat == DDSImage.D3DFMT_DXT1 || //
				pixelFormat == DDSImage.D3DFMT_DXT3 || //
				pixelFormat == DDSImage.D3DFMT_DXT5)
		{
			ByteBuffer buffer = imageInfo.getData();

			int w = imageInfo.getWidth();
			int h = imageInfo.getHeight();

			// Block size default
			int blockSize = pixelFormat == DDSImage.D3DFMT_DXT1 ? 8 : 16;

			// Number of pixels DXTx
			int nBytes = ((w + 3) / 4) * ((h + 3) / 4) * blockSize;

			//TODO: I could halve this requirement by copying the bottom half out
			//then flipping downward to half then flipping from here down
			// or even more if I take out bottom row, flip top into bottom
			//flip bottom put into top move inwards by one step

			byte[] pixels = new byte[nBytes];

			// Flip & copy to actual pixel buffer
			int j, widBytes, k;

			widBytes = ((w + 3) / 4) * blockSize;
			byte[] s = buffer.array();
			int sp = buffer.arrayOffset();
			byte[] d = pixels;
			int dp = pixels.length;// ready for first wind back

			//d.position(d.limit()); 

			//int count=0;
			for (j = 0; j < ((h + 3) / 4); j++)
			{
				dp -= widBytes; // put d ready for copy into
				System.arraycopy(s, sp, d, dp, widBytes);

				for (k = 0; k < (widBytes / blockSize); k++)
				{
					if (pixelFormat == DDSImage.D3DFMT_DXT1)
					{
						FlipDXT1BlockFull(d, dp);
						dp += 8;
					}
					else if (pixelFormat == DDSImage.D3DFMT_DXT3)
					{
						FlipDXT3BlockFull(d, dp);
						dp += 16;
					}
					else if (pixelFormat == DDSImage.D3DFMT_DXT5)
					{
						FlipDXT5BlockFull(d, dp);
						dp += 16;
					}
				}

				sp += widBytes;
				dp -= widBytes; //wind back to intial position
			}

			// byte buffer for dds image is one big shared buffer, copy into position
			System.arraycopy(pixels, 0, buffer.array(), buffer.arrayOffset(), pixels.length);
		}
		else if (pixelFormat == DDSImage.D3DFMT_R8G8B8 || //
				pixelFormat == DDSImage.D3DFMT_A8R8G8B8 || //
				pixelFormat == DDSImage.D3DFMT_X8R8G8B8 || //
				pixelFormat == DDSImage.DDS_A16B16G16R16F)
		{
			ByteBuffer buffer = imageInfo.getData();

			int w = imageInfo.getWidth();
			int h = imageInfo.getHeight();

			int widBytes = w * (pixelFormat == DDSImage.D3DFMT_R8G8B8 ? 3 : pixelFormat == DDSImage.DDS_A16B16G16R16F ? 8 : 4);

			byte[] s = buffer.array();
			byte[] d = new byte[buffer.limit()];
			int sp = buffer.arrayOffset();
			int dp = d.length;// ready for first wind back
			for (int j = 0; j < h; j++)
			{
				dp -= widBytes; // put d ready for copy into
				System.arraycopy(s, sp, d, dp, widBytes);
				sp += widBytes;
			}

			// byte buffer for dds image is one big shared buffer, copy into position
			System.arraycopy(d, 0, buffer.array(), buffer.arrayOffset(), d.length);
		}

	}
}
