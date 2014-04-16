package tools3d.audio;

import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PointSound;
import javax.media.j3d.Sound;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

/**NOTE! this string is a URL this is how you specify a relative url.
*MediaContainer sample = new MediaContainer("file:media/sounds/factCREAK.au");
*
*Play an mp3 lik this JLayerPlayer.playMP3("resources/sounds/Known as Dune.mp3");
*/

public class SimpleSounds
{

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