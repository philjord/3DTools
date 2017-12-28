
package tools3d.audio.converter;

import java.nio.ByteBuffer;

/**
 * Class to manage RIFF byte buffers
 */
public class RiffByteBuffer
{
	class RiffChunkHeader
	{
		public int ckID = 0; // Four-character chunk ID
		public int ckSize = 0; // Length of data in chunk

		public RiffChunkHeader()
		{
		}
	}

	// DDCRET
	public static final int DDC_SUCCESS = 0; // The operation succeeded
	public static final int DDC_FAILURE = 1; // The operation failed for unspecified reasons
	public static final int DDC_OUT_OF_MEMORY = 2; // Operation failed due to running out of memory
	public static final int DDC_FILE_ERROR = 3; // Operation encountered file I/O error
	public static final int DDC_INVALID_CALL = 4; // Operation was called with invalid parameters
	public static final int DDC_USER_ABORT = 5; // Operation was aborted by the user
	public static final int DDC_INVALID_FILE = 6; // File format does not match

	// RiffFileMode
	public static final int RFM_UNKNOWN = 0; // undefined type (can use to mean "N/A" or "not open")
	public static final int RFM_WRITE = 1; // open for write
	public static final int RFM_READ = 2; // open for read

	private RiffChunkHeader riff_header; // header for whole file
	protected int fmode; // current  I/O mode
	protected ByteBuffer bytebuff; // I/O stream to use

	/**
	* Dummy Constructor
	*/
	public RiffByteBuffer()
	{
		bytebuff = null;
		fmode = RFM_UNKNOWN;
		riff_header = new RiffChunkHeader();

		riff_header.ckID = FourCC("RIFF");
		riff_header.ckSize = 0;
	}

	/**
	* Return File Mode.
	*/
	public int CurrentFileMode()
	{
		return fmode;
	}

	/**
	* Open a RIFF file.
	*/
	public int Open(ByteBuffer os, int NewMode)
	{
		int retcode = DDC_SUCCESS;

		if (fmode != RFM_UNKNOWN)
		{
			retcode = Close();
		}

		if (retcode == DDC_SUCCESS)
		{
			switch (NewMode)
			{
			case RFM_WRITE:

				bytebuff = os;

				// Write the RIFF header...
				// We will have to come back later and patch it!
				byte[] br = new byte[8];
				br[0] = (byte) ((riff_header.ckID >>> 24) & 0x000000FF);
				br[1] = (byte) ((riff_header.ckID >>> 16) & 0x000000FF);
				br[2] = (byte) ((riff_header.ckID >>> 8) & 0x000000FF);
				br[3] = (byte) (riff_header.ckID & 0x000000FF);

				byte br4 = (byte) ((riff_header.ckSize >>> 24) & 0x000000FF);
				byte br5 = (byte) ((riff_header.ckSize >>> 16) & 0x000000FF);
				byte br6 = (byte) ((riff_header.ckSize >>> 8) & 0x000000FF);
				byte br7 = (byte) (riff_header.ckSize & 0x000000FF);

				br[4] = br7;
				br[5] = br6;
				br[6] = br5;
				br[7] = br4;

				bytebuff.put(br, 0, 8);
				fmode = RFM_WRITE;

				break;

			case RFM_READ:
				bytebuff = os;

				// Try to read the RIFF header...   				   
				byte[] br2 = new byte[8];
				bytebuff.get(br2, 0, 8);
				fmode = RFM_READ;
				riff_header.ckID = ((br2[0] << 24) & 0xFF000000) | ((br2[1] << 16) & 0x00FF0000) | ((br2[2] << 8) & 0x0000FF00)
						| (br2[3] & 0x000000FF);
				riff_header.ckSize = ((br2[4] << 24) & 0xFF000000) | ((br2[5] << 16) & 0x00FF0000) | ((br2[6] << 8) & 0x0000FF00)
						| (br2[7] & 0x000000FF);

			default:
				retcode = DDC_INVALID_CALL;
			}
		}
		return retcode;
	}

	/**
	* Write NumBytes data.
	*/
	public int Write(byte[] Data, int NumBytes)
	{
		if (fmode != RFM_WRITE)
		{
			return DDC_INVALID_CALL;
		}

		bytebuff.put(Data, 0, NumBytes);
		fmode = RFM_WRITE;

		riff_header.ckSize += NumBytes;
		return DDC_SUCCESS;
	}

	/**
	* Write NumBytes data.
	*/
	public int Write(short[] Data, int NumBytes)
	{
		byte[] theData = new byte[NumBytes];
		int yc = 0;
		for (int y = 0; y < NumBytes; y = y + 2)
		{
			theData[y] = (byte) (Data[yc] & 0x00FF);
			theData[y + 1] = (byte) ((Data[yc++] >>> 8) & 0x00FF);
		}
		if (fmode != RFM_WRITE)
		{
			return DDC_INVALID_CALL;
		}

		bytebuff.put(theData, 0, NumBytes);
		fmode = RFM_WRITE;

		riff_header.ckSize += NumBytes;
		return DDC_SUCCESS;
	}

	/**
	* Write NumBytes data.
	*/
	public int Write(RiffChunkHeader Triff_header, int NumBytes)
	{
		byte[] br = new byte[8];
		br[0] = (byte) ((Triff_header.ckID >>> 24) & 0x000000FF);
		br[1] = (byte) ((Triff_header.ckID >>> 16) & 0x000000FF);
		br[2] = (byte) ((Triff_header.ckID >>> 8) & 0x000000FF);
		br[3] = (byte) (Triff_header.ckID & 0x000000FF);

		byte br4 = (byte) ((Triff_header.ckSize >>> 24) & 0x000000FF);
		byte br5 = (byte) ((Triff_header.ckSize >>> 16) & 0x000000FF);
		byte br6 = (byte) ((Triff_header.ckSize >>> 8) & 0x000000FF);
		byte br7 = (byte) (Triff_header.ckSize & 0x000000FF);

		br[4] = br7;
		br[5] = br6;
		br[6] = br5;
		br[7] = br4;

		if (fmode != RFM_WRITE)
		{
			return DDC_INVALID_CALL;
		}

		bytebuff.put(br, 0, NumBytes);
		fmode = RFM_WRITE;

		riff_header.ckSize += NumBytes;
		return DDC_SUCCESS;
	}

	/**
	* Write NumBytes data.
	*/
	public int Write(short Data, int NumBytes)
	{
		short theData = (short) (((Data >>> 8) & 0x00FF) | ((Data << 8) & 0xFF00));
		if (fmode != RFM_WRITE)
		{
			return DDC_INVALID_CALL;
		}

		bytebuff.asShortBuffer().put(theData);
		bytebuff.position(bytebuff.position() + 2);
		fmode = RFM_WRITE;

		riff_header.ckSize += NumBytes;
		return DDC_SUCCESS;
	}

	/**
	* Write NumBytes data.
	*/
	public int Write(int Data, int NumBytes)
	{
		short theDataL = (short) ((Data >>> 16) & 0x0000FFFF);
		short theDataR = (short) (Data & 0x0000FFFF);
		short theDataLI = (short) (((theDataL >>> 8) & 0x00FF) | ((theDataL << 8) & 0xFF00));
		short theDataRI = (short) (((theDataR >>> 8) & 0x00FF) | ((theDataR << 8) & 0xFF00));
		int theData = ((theDataRI << 16) & 0xFFFF0000) | (theDataLI & 0x0000FFFF);
		if (fmode != RFM_WRITE)
		{
			return DDC_INVALID_CALL;
		}

		bytebuff.asIntBuffer().put(theData);
		bytebuff.position(bytebuff.position() + 4);
		fmode = RFM_WRITE;

		riff_header.ckSize += NumBytes;
		return DDC_SUCCESS;
	}

	/**
	* Close Riff File.
	* Length is written too.
	*/
	public int Close()
	{
		int retcode = DDC_SUCCESS;

		switch (fmode)
		{
		case RFM_WRITE:

			//TODO: limit needs to be set however teh backpatch call below has already moved position
			// so if this method is called alone it
			// needs to deal with things
			bytebuff.position(0);

			byte[] br = new byte[8];
			br[0] = (byte) ((riff_header.ckID >>> 24) & 0x000000FF);
			br[1] = (byte) ((riff_header.ckID >>> 16) & 0x000000FF);
			br[2] = (byte) ((riff_header.ckID >>> 8) & 0x000000FF);
			br[3] = (byte) (riff_header.ckID & 0x000000FF);

			br[7] = (byte) ((riff_header.ckSize >>> 24) & 0x000000FF);
			br[6] = (byte) ((riff_header.ckSize >>> 16) & 0x000000FF);
			br[5] = (byte) ((riff_header.ckSize >>> 8) & 0x000000FF);
			br[4] = (byte) (riff_header.ckSize & 0x000000FF);
			bytebuff.put(br, 0, 8);

			break;

		case RFM_READ:

			break;
		}
		bytebuff = null;
		fmode = RFM_UNKNOWN;
		return retcode;
	}

	/**
	* Return File Position.
	*/
	public long CurrentFilePosition()
	{
		long position = bytebuff.position();

		return position;
	}

	/**
	* Write Data to specified offset.
	*/
	public int Backpatch(long FileOffset, RiffChunkHeader Data, int NumBytes)
	{
		if (bytebuff == null)
		{
			return DDC_INVALID_CALL;
		}

		// the bytebuffer needs to know  how big it is and we are about to reset the position backwards
		bytebuff.limit(bytebuff.position());
		bytebuff.position((int) FileOffset);

		return Write(Data, NumBytes);
	}

	/**
	* Error Messages.
	*/
	private static String DDCRET_String(int retcode)
	{
		switch (retcode)
		{
		case DDC_SUCCESS:
			return "DDC_SUCCESS";
		case DDC_FAILURE:
			return "DDC_FAILURE";
		case DDC_OUT_OF_MEMORY:
			return "DDC_OUT_OF_MEMORY";
		case DDC_FILE_ERROR:
			return "DDC_FILE_ERROR";
		case DDC_INVALID_CALL:
			return "DDC_INVALID_CALL";
		case DDC_USER_ABORT:
			return "DDC_USER_ABORT";
		case DDC_INVALID_FILE:
			return "DDC_INVALID_FILE";
		}
		return "Unknown Error";
	}

	/**
	* Fill the header.
	*/
	public static int FourCC(String ChunkName)
	{
		byte[] p = { 0x20, 0x20, 0x20, 0x20 };
		ChunkName.getBytes(0, 4, p, 0);
		int ret = (((p[0] << 24) & 0xFF000000) | ((p[1] << 16) & 0x00FF0000) | ((p[2] << 8) & 0x0000FF00) | (p[3] & 0x000000FF));
		return ret;
	}

}
