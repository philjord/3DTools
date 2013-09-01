package tools3d.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PointSound;
import javax.media.j3d.Sound;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

//NOTE! this string is a URL this is how you specify a relative url.
//MediaContainer sample = new MediaContainer("file:media/sounds/factCREAK.au");		 

public class SoundEngine extends BranchGroup
{
	// the name of an mp3 to play
	private String soundTrack = "media/music/Tribute.mp3";

	public SoundEngine()
	{
		if (false)
			playMP3(soundTrack);
	}

	public void playMP3(String musicTrack)
	{

		// just a nice sound track please in a new thread too.  
		try
		{
			FileInputStream fin = new FileInputStream(musicTrack);
			BufferedInputStream bin = new BufferedInputStream(fin);

			AudioDevice audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
			final Player player = new Player(bin, audioDevice);

			// play() is a blocking call
			Thread t = new Thread()
			{
				public void run()
				{
					try
					{
						player.play();
					}
					catch (JavaLayerException e)
					{
						System.out.println(e);
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}
		catch (Exception ex)
		{
			System.out.println("Problem playing file " + musicTrack + " " + ex);
		}

	}

	public static BranchGroup createPointSound(String soundURL)
	{
		PointSound sound = new PointSound();

		BoundingSphere soundBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		sound.setSchedulingBounds(soundBounds);

		MediaContainer sample = new MediaContainer("file:" + soundURL);
		sample.setCapability(MediaContainer.ALLOW_URL_WRITE);
		sample.setCapability(MediaContainer.ALLOW_URL_READ);

		sound.setDistanceGain(createLinearAttentuation(100f, 20));

		sound.setLoop(Sound.INFINITE_LOOPS);
		sound.setContinuousEnable(false);
		sound.setReleaseEnable(false);
		sound.setSoundData(sample);
		sound.setInitialGain(1.0f);
		Point3f sound2Pos = new Point3f(0.0f, 0.0f, 0.0f);
		sound.setPosition(sound2Pos);
		sound.setEnable(true);

		BranchGroup soundGroup = new BranchGroup();
		//	soundGroup.addChild(sound);
		return soundGroup;

	}

	public static BranchGroup createBackgroundSound(String soundURL)
	{

		BackgroundSound sound = new BackgroundSound();

		BoundingSphere soundBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY);
		sound.setSchedulingBounds(soundBounds);

		MediaContainer sample = new MediaContainer("file:" + soundURL);
		sample.setCapability(MediaContainer.ALLOW_URL_WRITE);
		sample.setCapability(MediaContainer.ALLOW_URL_READ);

		sound.setLoop(Sound.INFINITE_LOOPS);
		sound.setContinuousEnable(false);
		sound.setReleaseEnable(false);
		sound.setSoundData(sample);
		sound.setInitialGain(1.0f);
		sound.setEnable(true);

		BranchGroup soundGroup = new BranchGroup();
		//		soundGroup.addChild(sound);
		return soundGroup;

	}

	private static Point2f[] createLinearAttentuation(float maxDist, int numPoints)
	{
		Point2f[] returnArray = new Point2f[numPoints];

		for (float i = 0; i < numPoints; i++)
		{
			float dist = (maxDist / numPoints) * i;
			float att = 1 - (i / numPoints);
			Point2f currentSet = new Point2f(dist, att);
			returnArray[(int) i] = currentSet;
		}

		return returnArray;
	}

}