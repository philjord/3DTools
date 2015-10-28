package old.utils.leafnode;

import java.io.Serializable;

import javax.media.j3d.Link;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import tools3d.utils.Utils3D;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.picking.PickTool;

/**
 * @author pj
 * Created on 12/02/2004
 * 
 */
public class PlacementLeaf extends TransformGroup implements Serializable
{
	private static boolean VISIBLE_PLACERS = true;

	private Node placer;

	public PlacementLeaf(float x, float y, float z)
	{
		if (VISIBLE_PLACERS)
		{
			placer = new ColorCube(0.05);
			PickTool.setCapabilities(placer, PickTool.INTERSECT_TEST);
		}
		else
		{
			placer = new Link();
		}
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		placer.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		Transform3D location = Utils3D.createTransform(new Vector3f(x, y, z));
		setTransform(location);
		addChild(placer);
	}

	public Point3f getPlacementVWPoint()
	{
		return Utils3D.getPointFromNode(placer);
	}

	// memory deburner
	private Transform3D trans = new Transform3D();

	public Vector3f getRelativeVector()
	{
		this.getTransform(trans);
		Vector3f transV = new Vector3f();
		trans.get(transV);
		return transV;
	}

}
