package tools3d.utils;

import javax.media.j3d.Alpha;

/**
 * @author pj
 * Created on 11/01/2004
 * 
 */
public class FlippableAlpha extends Alpha
{

	public FlippableAlpha(long increaseTime, long rampTime)
	{
		super(1, Alpha.INCREASING_ENABLE, 0, 0, increaseTime, rampTime, 1000, 0, 0, 0);
	}

	public void flipAlpha()
	{
		long currentTime = System.currentTimeMillis();
		long currentAlphaElapsed = currentTime - getStartTime();
		long flippedTime = getIncreasingAlphaDuration() - currentAlphaElapsed;
		setStartTime(currentTime - flippedTime);
	}

	public FlippableAlpha alterRampTime(long newIncreaseTime)
	{
		long oldIncreaseTime = getIncreasingAlphaDuration();
		long oldRampTime = getIncreasingAlphaRampDuration();

		float oldRampRatio = (float) oldRampTime / (float) oldIncreaseTime;

		long oldStartTime = getStartTime();

		long currentTime = System.currentTimeMillis();
		long oldAlphaElapsed = currentTime - oldStartTime;

		long newRampTime = (long) (newIncreaseTime * oldRampRatio);

		float oldAlphaElapsedRatio = (float) oldAlphaElapsed / (float) oldIncreaseTime;

		long newAlphaElapsed = (long) (newIncreaseTime * oldAlphaElapsedRatio);

		FlippableAlpha newAlpha = new FlippableAlpha(newIncreaseTime, newRampTime);
		newAlpha.setStartTime(currentTime - newAlphaElapsed);

		return newAlpha;

	}
}
