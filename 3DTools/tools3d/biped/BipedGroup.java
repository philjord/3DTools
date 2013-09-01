/*
 * Created on Jan 2, 2004
 */
package tools3d.biped;

import javax.media.j3d.BranchGroup;

/**
 * @author Administrator
 *
 */
public abstract class BipedGroup extends BranchGroup
{
	public static String AREA_UPPER = "upper";

	public static String AREA_LOWER = "lower";

	public static String AREA_HEAD = "head";

	public static String AREA_HANDS = "hands";

	public abstract void setAnimation(int animationType);

	public abstract void setHeadPitch(double pitch);
}
