package tools3d.navigation;

public class AvatarCollisionInfo
{
	private static final float DEFAULT_AVATAR_XZ_RADIUS = 0.5f;

	private static final float DEFAULT_AVATAR_Y_HEIGHT = 1.8f;

	private static final float DEFAULT_AVATAR_Y_STEP_OVER_HEIGHT = 0.5f;

	/** The height of the avatar above the terrain */
	private float avatarXZRadius = DEFAULT_AVATAR_XZ_RADIUS;

	private float avatarYHeight = DEFAULT_AVATAR_Y_HEIGHT;

	private float avatarYStepOverHeight = DEFAULT_AVATAR_Y_STEP_OVER_HEIGHT;

	private float cameraAbovePelvisHeight = (DEFAULT_AVATAR_Y_HEIGHT/2) - 0.1f;

	//this is just a convinietn transport machanism for now
	private AvatarLocation avatarLocation;

	/**
	 * Camera Y height is height about half way mark, so for a 1.8 char wanting camera at 1.7 this should be
	 * 1.8/2 =0.9  1.7 - 0.9 = 0.8
	 * @param avatarLocation
	 * @param avatarXZRadius
	 * @param avatarYHeight
	 * @param avatarYStepOverHeight
	 * @param cameraAbovePelvisHeight
	 */
	public AvatarCollisionInfo(AvatarLocation avatarLocation, float avatarXZRadius, float avatarYHeight, float avatarYStepOverHeight,
			float cameraAbovePelvisHeight)
	{
		this.avatarLocation = avatarLocation;
		this.avatarXZRadius = avatarXZRadius;
		this.avatarYHeight = avatarYHeight;
		this.avatarYStepOverHeight = avatarYStepOverHeight;
		this.cameraAbovePelvisHeight = cameraAbovePelvisHeight;

		if (avatarXZRadius < 0 || avatarYHeight < 0 || avatarYStepOverHeight < 0 || avatarYHeight < avatarYStepOverHeight)
		{
			System.out.println("bad avartar values ");
			System.out.println("avatarXZRadius " + avatarXZRadius);
			System.out.println("avatarYHeight " + avatarYHeight);
			System.out.println("avatarYStepOverHeight " + avatarYStepOverHeight);
		}

	}

	public AvatarLocation getAvatarLocation()
	{
		return avatarLocation;
	}

	public float getAvatarXZRadius()
	{
		return avatarXZRadius;
	}

	public float getAvatarYHeight()
	{
		return avatarYHeight;
	}

	public float getAvatarYStepOverHeight()
	{
		return avatarYStepOverHeight;
	}

	public float getCameraAbovePelvisHeight()
	{
		return cameraAbovePelvisHeight;
	}
}
