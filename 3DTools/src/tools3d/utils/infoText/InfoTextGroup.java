package tools3d.utils.infoText;
//<AND>DISABLED for andy
//<AND>import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
//<AND>import javax.media.j3d.Font3D;
//<AND>import javax.media.j3d.FontExtrusion;
//<AND>import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.OrientedShape3D;
//<AND>import javax.media.j3d.Text3D;
import javax.vecmath.Point3f;

/**
 * @author pj
 *
 */
public class InfoTextGroup extends BranchGroup
{
	//<AND>private Text3D text3D;

	private OrientedShape3D textShape = new OrientedShape3D();

	public InfoTextGroup()
	{
		//TODO: the object itself should say where this text sits
		Appearance apText = new Appearance();
		Material m = new Material();
		m.setLightingEnable(false);
		apText.setMaterial(m);

		//<AND>Font3D f3d = new Font3D(new Font("LucidaSansRegular", Font.PLAIN, 4), new FontExtrusion());
		//<AND>Point3f textPt = new Point3f(0.0f, 5.0f, 0.0f);
		//<AND>text3D = new Text3D(f3d, "", textPt, Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT);

		textShape.setConstantScaleEnable(true);
		textShape.setScale(0.25f);
		//<AND>textShape.setGeometry(text3D);
		textShape.setAppearance(apText);

		textShape.setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
		// text is centered around 0, 3, 0. Make it rotate around 0,5,0
		Point3f rotationPt = new Point3f(0.0f, 5.0f, 0.0f);
		textShape.setRotationPoint(rotationPt);

		// allow pick tests
		//<AND>text3D.setCapability(Geometry.ALLOW_INTERSECT);
		// allow the string to change
		//<AND>text3D.setCapability(Text3D.ALLOW_STRING_WRITE);

		addChild(textShape);

	}

	public void setMainText(String newText)
	{
		//<AND>text3D.setString(newText);
	}
}
