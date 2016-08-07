
package tools3d.audio.converter;

import java.nio.ByteBuffer;

import javazoom.jl.decoder.Obuffer;

/**
 * Implements an Obuffer by writing the data to
 * a file in RIFF WAVE format. 
 *  
 * @since 0.0
 */

public class WaveByteBufferObuffer extends Obuffer
{
	private short[] buffer;
	private short[] bufferp;
	private int channels;
	private WaveByteBuffer outWave;

	/**
	 * Creates a new WareFileObuffer instance. 
	 * 
	 * @param number_of_channels	
	 *				The number of channels of audio data
	 *				this buffer will receive. 
	 * 
	 * @param freq	The sample frequency of the samples in the buffer.
	 * 
	 * @param fileName	The filename to write the data to.
	 */
	public WaveByteBufferObuffer(int number_of_channels, int freq, ByteBuffer os)
	{
		if (os == null)
			throw new NullPointerException("OutputStream");

		buffer = new short[OBUFFERSIZE];
		bufferp = new short[MAXCHANNELS];
		channels = number_of_channels;

		for (int i = 0; i < number_of_channels; ++i)
			bufferp[i] = (short) i;

		outWave = new WaveByteBuffer();

		int rc = outWave.OpenForWrite(os, freq, (short) 16, (short) channels);
	}

	/**
	 * Takes a 16 Bit PCM sample.
	 */
	@Override
	public void append(int channel, short value)
	{
		buffer[bufferp[channel]] = value;
		bufferp[channel] += channels;
	}

	/**
	 * Write the samples to the file (Random Acces).
	 */
	short[] myBuffer = new short[2];

	@Override
	public void write_buffer(int val)
	{

		int k = 0;
		int rc = 0;

		rc = outWave.WriteData(buffer, bufferp[0]);
		// REVIEW: handle RiffFile errors. 
		/*
		for (int j=0;j<bufferp[0];j=j+2)
		{
		
		//myBuffer[0] = (short)(((buffer[j]>>8)&0x000000FF) | ((buffer[j]<<8)&0x0000FF00));
		//myBuffer[1] = (short) (((buffer[j+1]>>8)&0x000000FF) | ((buffer[j+1]<<8)&0x0000FF00));
		myBuffer[0] = buffer[j];
		myBuffer[1] = buffer[j+1];
		rc = outWave.WriteData (myBuffer,2);
		}
		*/
		for (int i = 0; i < channels; ++i)
			bufferp[i] = (short) i;
	}

	@Override
	public void close()
	{
		outWave.Close();
	}

	/**
	 *
	 */
	@Override
	public void clear_buffer()
	{
	}

	/**
	 *
	 */
	@Override
	public void set_stop_flag()
	{
	}

	/*
	 * Create STDOUT buffer
	 *
	 *
	public static Obuffer create_stdout_obuffer(MPEG_Args maplay_args)
	{
		Obuffer thebuffer = null;
	int mode = maplay_args.MPEGheader.mode();
	int which_channels = maplay_args.which_c;
	 if (mode == Header.single_channel || which_channels != MPEG_Args.both)
	    thebuffer = new FileObuffer(1,maplay_args.output_filename);
	 else
	    thebuffer = new FileObuffer(2,maplay_args.output_filename);
	 return(thebuffer);
	}
	*/
}
