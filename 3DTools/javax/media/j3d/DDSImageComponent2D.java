package javax.media.j3d;

import java.awt.image.RenderedImage;

/**
 * This class extends a core class, the jars for java3d must not be sealed
 * This means java3d 1.5.2 install in lib\ext will cause a crash
 * run
 * //DDS requires no installed java3D
 *		if (QueryProperties.checkForInstalledJ3d())
 *		{
 *			System.exit(0);
 *		}
 * If you want to use this class, it will tell the user to uninstall
 * @author philip
 *
 */
public class DDSImageComponent2D extends ImageComponent2D
{
	private static boolean byRef = true;

	private static boolean yUp = true;

	/**
	 * Note ByREf adn YUp are force to true to ensure no image copies happen
	 * @param format
	 * @param image
	 */
	public DDSImageComponent2D(int format, RenderedImage image)
	{
		super(format, image, byRef, yUp);
	}

	@Override
	void createRetained()
	{
		this.retained = new DDSImageComponent2DRetained();
		this.retained.setSource(this);
	}

}
