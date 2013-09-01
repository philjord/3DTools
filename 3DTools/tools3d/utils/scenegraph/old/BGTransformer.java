package tools3d.utils.scenegraph.old;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author pj
 * Created on 22/01/2004
 * 
 */
public class BGTransformer extends BranchGroup
{
	private static Vector3f defaultLookAt = new Vector3f(0, 0, -1);

	private SimpleTG simpleTG;

	public BGTransformer(Vector3f translation, Vector3f lookAt, BranchGroup group)
	{
		// set it up for selection of the node
		setCapability(Node.ENABLE_PICK_REPORTING);
		setUserData(group);

		// set it up for deletion of the node
		setCapability(BranchGroup.ALLOW_DETACH);

		simpleTG = new SimpleTG(translation, lookAt, group);
		addChild(simpleTG);
	}

	public BGTransformer(Vector3f translation, BranchGroup group)
	{
		this(translation, defaultLookAt, group);
	}

	public BGTransformer(Point3f translation, BranchGroup group)
	{
		this(new Vector3f(translation), group);
	}

	public BGTransformer(Point3f translation, Point3f lookAt, BranchGroup group)
	{
		this(new Vector3f(translation), new Vector3f(lookAt), group);
	}

	public void translateAndReparent(Vector3f translation, BranchGroup newParent)
	{
		detach();
		setTranslation(translation);
		newParent.addChild(this);
	}

	public void translateAndReparent(Transform3D transform, BranchGroup newParent)
	{
		detach();
		setTransform(transform);
		newParent.addChild(this);
	}

	public void setTransform(Transform3D transform)
	{
		simpleTG.setTransform(transform);
	}

	public void setTranslation(Vector3f translation)
	{
		simpleTG.setTranslation(translation);
	}

	public Vector3f getTranslation()
	{
		Transform3D transform = new Transform3D();
		simpleTG.getTransform(transform);
		Vector3f returnVector = new Vector3f();
		transform.get(returnVector);
		return returnVector;
	}

}
