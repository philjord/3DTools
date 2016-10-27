package tools3d.universe;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Locale;
import org.jogamp.java3d.View;
import org.jogamp.java3d.ViewSpecificGroup;
import org.jogamp.java3d.VirtualUniverse;

/**
 * 
 */
public class VisualPhysicalUniverse extends VirtualUniverse
{

	/*
	 * NEVER USE THIS EVER!!!!!! EVER!!!! except for insane development and debug
	 * It is the last instantiated locale
	 */
	public static Locale staticTestLocale;

	// ////////////////////////////////////////////////////////////////////////////////

	protected Locale theLocale;

	private BranchGroup visualGroup = new BranchGroup();

	private BranchGroup behaviorGroup = new BranchGroup();

	private BranchGroup physicsGroup = new BranchGroup();

	private ViewSpecificGroup physicalViewSpecificGroup = new ViewSpecificGroup();

	private ViewSpecificGroup behaviorViewSpecificGroup = new ViewSpecificGroup();

	/**
	 * A universe with a locale created and the fog and skybox and some branches added
	 */
	public VisualPhysicalUniverse()
	{
		theLocale = new Locale(this);
		// set teh mad static member thing which should not be used
		staticTestLocale = theLocale;

		// set capabilities for the various branch groups
		behaviorGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		behaviorGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		behaviorGroup.setUserData("Behavior Group");
		visualGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		visualGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		visualGroup.setUserData("Visual Group");
		physicsGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		physicsGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		physicsGroup.setUserData("Physics Group");

		theLocale.addBranchGraph(visualGroup);

		// make the Physics locale viewable only to specified views
		physicalViewSpecificGroup.setCapability(ViewSpecificGroup.ALLOW_VIEW_WRITE);
		BranchGroup pvRoot = new BranchGroup();
		pvRoot.addChild(physicalViewSpecificGroup);
		physicalViewSpecificGroup.addChild(physicsGroup);
		theLocale.addBranchGraph(pvRoot);

		// no one looks at this view yet in all, could be like physics
		behaviorViewSpecificGroup.setCapability(ViewSpecificGroup.ALLOW_VIEW_WRITE);
		BranchGroup bgRoot = new BranchGroup();
		bgRoot.addChild(behaviorViewSpecificGroup);
		behaviorViewSpecificGroup.addChild(behaviorGroup);
		theLocale.addBranchGraph(bgRoot);
	}

	public void addViewingPlatform(BranchGroup viewingPlatform)
	{
		theLocale.addBranchGraph(viewingPlatform);
	}

	public void removeViewingPlatform(BranchGroup viewingPlatform)
	{
		theLocale.removeBranchGraph(viewingPlatform);
	}

	public BranchGroup getVisualBranch()
	{
		return visualGroup;
	}

	public void addToVisualBranch(Group newGroup)
	{
		visualGroup.addChild(newGroup);
	}

	public BranchGroup getPhysicalBranch()
	{
		return physicsGroup;
	}

	public void addToPhysicalBranch(Group group)
	{
		physicsGroup.addChild(group);
	}

	public void setViewForPhysicalBranch(View view)
	{
		try
		{
			physicalViewSpecificGroup.removeAllViews();
			physicalViewSpecificGroup.addView(view);
		}
		catch (Exception e)
		{
			System.err.println("setViewForPhysicalBranch(View view) failed with exception!");
		}
	}

	public void clearViewForPhysicalBranch()
	{
		physicalViewSpecificGroup.removeAllViews();
	}

	public void addToBehaviorBranch(Group newGroup)
	{
		behaviorGroup.addChild(newGroup);
	}

	public void removeFromBehaviorBranch(Group newGroup)
	{
		behaviorGroup.removeChild(newGroup);
	}
}
