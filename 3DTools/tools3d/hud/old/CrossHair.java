package tools3d.hud.old;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;

import com.sun.j3d.utils.picking.PickTool;

public class CrossHair extends BranchGroup
{
	protected LineArray crossLines = null;

	public CrossHair(float size)
	{
		// create the water model
		Appearance app = new Appearance();

		app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.85f));
		app.setColoringAttributes(new ColoringAttributes(1f, 1f, 1f, ColoringAttributes.FASTEST));
		LineAttributes lineAttrib = new LineAttributes();
		lineAttrib.setLineWidth(2.0f);
		app.setLineAttributes(lineAttrib);
		Shape3D crossShape = new Shape3D(createCrossGeometryA(size), app);
		PickTool.setCapabilities(crossShape, PickTool.INTERSECT_TEST);
		addChild(crossShape);

	}

	private Geometry createCrossGeometryA(float size)
	{

		int N = 2; // number of lines in this model 
		crossLines = new LineArray(N * 2, GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE);
		crossLines.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		crossLines.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
		crossLines.setCapability(GeometryArray.ALLOW_COUNT_READ);

		float[] coordinates = new float[N * 3 * 2];

		coordinates[0] = 1f * size;
		coordinates[1] = 1f * size;
		coordinates[2] = 0.0f;

		coordinates[3] = -1f * size;
		coordinates[4] = -1f * size;
		coordinates[5] = 0.0f;

		coordinates[6] = -1f * size;
		coordinates[7] = 1f * size;
		coordinates[8] = 0.0f;

		coordinates[9] = 1f * size;
		coordinates[10] = -1f * size;
		coordinates[11] = 0.0f;

		crossLines.setCoordRefFloat(coordinates);

		return crossLines;
	}
}
