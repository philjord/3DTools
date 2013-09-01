package tools3d.utils.scenegraph.old;

import java.util.Enumeration;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.PickShape;
import javax.media.j3d.SceneGraphPath;

/**
 * This class simply allows the delegation of all "below" methods
 * ie methods that affect the graph below this BG to another BG which
 * is attached to this BG (the top) in some manner.
 * This way any struct can be easily imposed between the two
 * eg a transform.
 * NOTE usual use of this class will call getBottomBG() and attach it to this
 * @author main
 *
 */
public abstract class BGStruct extends BranchGroup
{
	private BranchGroup bottomBG = new BranchGroup();

	public BGStruct()
	{
		setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		bottomBG.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		bottomBG.setCapability(Group.ALLOW_CHILDREN_WRITE);
	}

	public BranchGroup getBottomBG()
	{
		return bottomBG;
	}

	public void addChildToTop(Node node)
	{
		super.addChild(node);
	}

	public void addChild(Node node)
	{
		bottomBG.addChild(node);
	}

	@SuppressWarnings("unchecked")
	public Enumeration<?> getAllChildren()
	{
		return bottomBG.getAllChildren();
	}

	public Node getChild(int idx)
	{
		return bottomBG.getChild(idx);
	}

	public int indexOfChild(Node node)
	{
		return bottomBG.indexOfChild(node);
	}

	public void insertChild(Node node, int idx)
	{
		bottomBG.insertChild(node, idx);
	}

	public int numChildren()
	{
		return bottomBG.numChildren();
	}

	public SceneGraphPath[] pickAll(PickShape pickShape)
	{
		return bottomBG.pickAll(pickShape);
	}

	public SceneGraphPath[] pickAllSorted(PickShape pickShape)
	{
		return bottomBG.pickAllSorted(pickShape);
	}

	public SceneGraphPath pickAny(PickShape pickShape)
	{
		return bottomBG.pickAny(pickShape);
	}

	public SceneGraphPath pickClosest(PickShape pickShape)
	{
		return bottomBG.pickClosest(pickShape);
	}

	public void removeAllChildren()
	{
		bottomBG.removeAllChildren();
	}

	public void removeChild(int idx)
	{
		bottomBG.removeChild(idx);
	}

	public void removeChild(Node node)
	{
		bottomBG.removeChild(node);
	}

	public void setChild(Node node, int idx)
	{
		bottomBG.setChild(node, idx);
	}
}
