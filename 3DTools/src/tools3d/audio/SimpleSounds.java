package tools3d.audio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import javax.media.j3d.AudioDevice3D;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PointSound;
import javax.media.j3d.Sound;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import tools3d.audio.converter.ConverterBB;
import tools3d.camera.Camera;

/**NOTE! this string is a URL this is how you specify a relative url.
*MediaContainer sample = new MediaContainer("file:media/sounds/factCREAK.au");
*
*Play an mp3 like this JLayerPlayer.playMP3("resources/sounds/Known as Dune.mp3");
*/

public class SimpleSounds
{

	// Muhahaha! should only use what it uses and no more, dear god what have I done?
	private static ByteBuffer bb = ByteBuffer.allocateDirect(1000000);

	/**Not to be used for background sounds! use the system supplied direct 
	* mp3 play capacity, this guy is for spatialized sounds only!
	* JOAL error code: 40961 seems fine?
	*/
	public static BranchGroup createPointSoundMp3(InputStream is, int maximumAttenuationDistance, int loopCount)
	{
		return createPointSoundMp3(is, maximumAttenuationDistance, loopCount, 1);

	}

	public static BranchGroup createPointSoundMp3(InputStream is, float maximumAttenuationDistance, int loopCount, float gain)
	{
		ConverterBB conv = new ConverterBB();
		int detail = Converter.PrintWriterProgressListener.VERBOSE_DETAIL;
		ConverterBB.ProgressListener listener = new ConverterBB.PrintWriterProgressListener(new PrintWriter(System.out, true), detail);

		try
		{
			synchronized (bb)
			{
				bb.clear();
				conv.convert(is, bb, listener, null);

				bb.rewind();
				byte[] buffer = new byte[bb.remaining()];
				bb.get(buffer);

				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

				MediaContainer mc = new MediaContainer(bais);

				return createPointSound(mc, maximumAttenuationDistance, loopCount, gain);
			}
		}
		catch (JavaLayerException ex)
		{
			System.err.println("Convertion failure: " + ex);
		}
		return null;
	}

	public static BranchGroup createPointSound(MediaContainer mc, float maximumAttenuationDistance, int loopCount)
	{
		return createPointSound(mc, maximumAttenuationDistance, loopCount, 1);

	}

	public static BranchGroup createPointSound(MediaContainer mc, float maximumAttenuationDistance, int loopCount, float gain)
	{

		// only 0 and -1 supported!
		if (loopCount == 1)
			loopCount = 0;
		
		PointSound ps = new PointSound();
		ps.setSoundData(mc);
		ps.setPosition(new Point3f(0, 0, 0));
		//float staticAttenuation = 10;
		//float maxGain = staticAttenuation / 100f;
		float maxGain = gain;
		ps.setInitialGain(gain);
		int minimumAttenuationDistance = 1;
		ps.setDistanceGain(new float[] { 0, minimumAttenuationDistance, maximumAttenuationDistance }, new float[] { maxGain, maxGain, 0 });
		ps.setEnable(true);
		ps.setPause(false);

		ps.setSchedulingBounds(new BoundingSphere(new Point3d(), maximumAttenuationDistance));
		ps.setLoop(loopCount);
		ps.setContinuousEnable(true);

		BranchGroup bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.addChild(ps);

		return bg;
	}

	/**
	 * FIXME: dump this rubbish, expensive and pointless
	 * use teh system provided mp3 players
	 * @param is
	 * @param loopCount
	 * @param gain
	 * @return
	 */
	public static void playBackgroundSoundMp3(InputStream is, int loopCount, float gain)
	{

		ConverterBB conv = new ConverterBB();
		int detail = Converter.PrintWriterProgressListener.VERBOSE_DETAIL;
		ConverterBB.ProgressListener listener = new ConverterBB.PrintWriterProgressListener(new PrintWriter(System.out, true), detail);

		try
		{
			synchronized (bb)
			{
				bb.clear();
				conv.convert(is, bb, listener, null);

				bb.rewind();
				byte[] buffer = new byte[bb.remaining()];
				bb.get(buffer);

				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

				MediaContainer mc = new MediaContainer(bais);

				playBackgroundSound(mc, loopCount, gain);
			}
		}
		catch (JavaLayerException ex)
		{
			System.err.println("Convertion failure: " + ex);
		}

	}

	public static void playBackgroundSound(MediaContainer mc, int loopCount, float gain)
	{
		// only 0 and -1 supported!
		if (loopCount == 1)
			loopCount = 0;

		int sound = Camera.mixer.prepareSound(AudioDevice3D.BACKGROUND_SOUND, mc);
		Camera.mixer.setSampleGain(sound, gain);
		Camera.mixer.setLoop(sound, loopCount);
		Camera.mixer.startSample(sound);

		/*
		BackgroundSound sound = new BackgroundSound();
		
		BoundingSphere soundBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY);
		sound.setSchedulingBounds(soundBounds);
		
		sound.setLoop(loopCount);
		sound.setContinuousEnable(false);
		sound.setReleaseEnable(false);
		sound.setSoundData(mc);
		sound.setInitialGain(gain);
		sound.setEnable(true);
		
		BranchGroup soundGroup = new BranchGroup();
		soundGroup.addChild(sound);
		return soundGroup;*/

	}

	public static void playMp3(String fileName, float volume)
	{
		if (mp3SystemMediaPlayer != null)
		{
			mp3SystemMediaPlayer.playAnMp3(fileName, volume);
		}
		else
		{
			System.err.println("mp3SystemMediaPlayer is null!");
		}

	}

	public static Mp3SystemMediaPlayer mp3SystemMediaPlayer = null;

	public interface Mp3SystemMediaPlayer
	{
		// name not same as above cos dex dies
		public void playAnMp3(String fileName, float volume);
	}
}