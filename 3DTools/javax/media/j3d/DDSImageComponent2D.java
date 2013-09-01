package javax.media.j3d;

import java.awt.image.RenderedImage;

public class DDSImageComponent2D extends ImageComponent2D
{

	public DDSImageComponent2D(int format, RenderedImage image, boolean byRef, boolean yUp)
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
