package tools3d.utils.scenegraph;

import java.util.Enumeration;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;

public class TransformedBranchGroup extends BranchGroup
{
	private EasyTransformGroup transformGroup = new EasyTransformGroup();

	public TransformedBranchGroup()
	{
		setCapability(BranchGroup.ALLOW_DETACH);
		super.addChild(transformGroup);
	}

	public EasyTransformGroup getEasyTransformGroup()
	{
		return transformGroup;
	}

	public void addChild(Node child)
	{
		transformGroup.addChild(child);
	}

	public Enumeration<?> getAllChildren()
	{
		return transformGroup.getAllChildren();
	}

	public Node getChild(int index)
	{
		return transformGroup.getChild(index);
	}

	public int indexOfChild(Node child)
	{
		return transformGroup.indexOfChild(child);
	}

	public void insertChild(Node child, int index)
	{
		transformGroup.insertChild(child, index);
	}

	public int numChildren()
	{
		return transformGroup.numChildren();
	}

	public void removeAllChildren()
	{
		transformGroup.removeAllChildren();
	}

	public void removeChild(int index)
	{
		transformGroup.removeChild(index);
	}

	public void removeChild(Node child)
	{
		transformGroup.removeChild(child);
	}

	public void setChild(Node child, int index)
	{
		transformGroup.setChild(child, index);
	}

}
