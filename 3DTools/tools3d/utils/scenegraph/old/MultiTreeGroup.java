/*
 * Created on Jan 10, 2006
 */
package tools3d.utils.scenegraph.old;

import javax.media.j3d.Transform3D;

import tools3d.utils.scenegraph.TransformedBranchGroup;

/**
 * @author Administrator
 *
 */
public class MultiTreeGroup
{
	private TransformedBranchGroup visualGroup = new TransformedBranchGroup();

	private TransformedBranchGroup physicsGroup = new TransformedBranchGroup();

	//private TransformedBranchGroup rainShadowGroup = new TransformedBranchGroup();

	public MultiTreeGroup()
	{

	}

	public void getTransform(Transform3D transform)
	{
		// just use the visual one as they are both identical
		visualGroup.getEasyTransformGroup().getTransform(transform);
	}

	public void setTransform(Transform3D transform)
	{
		visualGroup.getEasyTransformGroup().setTransform(transform);
		physicsGroup.getEasyTransformGroup().setTransform(transform);
	}

	/**
	 * 
	 */
	public void detach()
	{
		visualGroup.detach();
		physicsGroup.detach();
	}

	public TransformedBranchGroup getVisualGroup()
	{
		return visualGroup;
	}

	public TransformedBranchGroup getPhysicalGroup()
	{
		return physicsGroup;
	}

}
