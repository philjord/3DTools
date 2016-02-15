package tools.compressedtexture.dds.utils;

/**
 * 24 bit 888 RGB color
 *
 * @author Lado Garakanidze
 * @version $Id$
 */

public class Color24
{
	/**
	 * The red color component.
	 */
	public int r = 0;

	/**
	 * The green color component.
	 */
	public int g = 0;

	/**
	 * The blue color component.
	 */
	public int b = 0;

	// as a regular pixel 888 format
	public int pix888 = 0;

	/**
	 * Creates a 24 bit 888 RGB color with all values set to 0.
	 */
	public Color24()
	{

	}

	public void set(int r, int g, int b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		pix888 = (r << 16 | g << 8 | b);
	}

	private static Color24 fromShort565(Color24 out, short pixel)
	{
		//out.set((int) (((long) pixel) & 0xf800) >>> 8, (int) (((long) pixel) & 0x07e0) >>> 3, (int) (((long) pixel) & 0x001f) << 3);
		out.set((pixel & 0xf800) >>> 8, (pixel & 0x07e0) >>> 3, (pixel & 0x001f) << 3);
		return out;
	}

	/**
	 * for DXT1 only
	 * the short need to be treated as unsigned and value compared
	 * c0  and c1 is a signed short, but we need to treat it as unsigned for comparision (it's 16 bits of info not a short at all)
	 * @param c0
	 * @param c1
	 * @return
	 */
	public static boolean hasAlphaBit(short c0, short c1)
	{
		// & 0xFFFF makes it an int of unsigned short value
		return (c0 & 0xFFFF) <= (c1 & 0xFFFF);
	}

	/**
	 * for DXT1 only
	 * If inC0 > inC1 c4 will be left as black to be used as full transparent
	 * http://en.wikipedia.org/wiki/S3_Texture_Compression
	 * @param inC0
	 * @param inC1
	 * @return
	 */
	public static Color24[] expandLookupTableAlphable(Color24[] out, short inC0, short inC1)
	{
		fromShort565(out[0], inC0);
		fromShort565(out[1], inC1);
		out[2].set((out[0].r + out[1].r) / 2, (out[0].g + out[1].g) / 2, (out[0].b + out[1].b) / 2);
		out[3].set(0, 0, 0);

		return out;
	}

	public static Color24[] expandLookupTable(Color24[] out, short inC0, short inC1)
	{
		fromShort565(out[0], inC0);
		fromShort565(out[1], inC1);
		out[2].set((2 * out[0].r + out[1].r) / 3, (2 * out[0].g + out[1].g) / 3, (2 * out[0].b + out[1].b) / 3);
		out[3].set((out[0].r + 2 * out[1].r) / 3, (out[0].g + 2 * out[1].g) / 3, (out[0].b + 2 * out[1].b) / 3);
		return out;

	}

}
