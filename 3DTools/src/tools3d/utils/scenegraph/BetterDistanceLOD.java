package tools3d.utils.scenegraph;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.J3dUtil;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3f;

//Note Bug 1331 - Shared nodes under switch nodes, the link doesn’t work properly 

//allows a transition zone of fadness sent out to 2 nodes attached

public class BetterDistanceLOD extends Behavior
{
	public static float FADE_RANGE_DIVISOR = 15f;

	private float MIN_FADE_RANGE = 1f; //TODO: better as a percent of min dist?

	private WakeupOnElapsedFrames wakeupFrame0 = new WakeupOnElapsedFrames(0, true);

	private WakeupOnElapsedFrames wakeupFrame2 = new WakeupOnElapsedFrames(2, true);

	private WakeupOnElapsedFrames wakeupFrame5 = new WakeupOnElapsedFrames(5, true);

	private WakeupOnElapsedFrames wakeupFrame10 = new WakeupOnElapsedFrames(10, true);

	private int prevIndex = -1;

	double[] distances;

	private Group parent;

	private ArrayList<BranchGroup> roots;

	private boolean popOnly = false;// if set true then no fading at all, only detach/attach

	static final double EPSILON = 1.0e-6;

	public BetterDistanceLOD(Group parent, ArrayList<BranchGroup> roots, float[] distances)
	{
		this(parent, roots, distances, false);
	}

	public BetterDistanceLOD(Group parent, ArrayList<BranchGroup> roots, float[] distances, boolean popOnly)
	{
		this.parent = parent;
		this.popOnly = popOnly;
		parent.setCapability(Group.ALLOW_CHILDREN_WRITE);
		parent.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		parent.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		this.roots = roots;
		for (BranchGroup bg : roots)
		{
			if (bg != null)
			{
				bg.setCapability(BranchGroup.ALLOW_DETACH);
				
				if (!popOnly && bg instanceof Fadable)
				{
					// tell the group to prepare itself for fading
					((Fadable) bg).fade(-1f);
				}
			}
		}

		this.distances = new double[distances.length];

		for (int i = 0; i < distances.length; i++)
		{
			this.distances[i] = distances[i];
		}
	}

	@Override
	public void initialize()
	{
		wakeupOn(wakeupFrame0);

	}

	public void setDistance(int whichDistance, double distance)
	{
		distances[whichDistance] = distance;
	}

	// variables for processStimulus
	private Point3f center = new Point3f();

	private Point3f viewPosition = new Point3f();

	// deburners
	//private Point3d viewPosition = new Point3d();
	//private Transform3D xform = new Transform3D();
	private Transform3D localToWorldTrans = new Transform3D();

	@Override
	public void processStimulus(Enumeration criteria)
	{
		if (parent == null)
		{
			//System.out.println("somefing null");
			wakeupOn(wakeupFrame10);
			return;
		}

		//  get viewplatforms's location in virutal world
		View v = this.getView();
		if (v == null)
		{
			//System.out.println("view null");
			wakeupOn(wakeupFrame10);
			return;
		}
		///////////////////////////
		ViewPlatform vp = v.getViewPlatform();
		if (vp == null)
		{
			wakeupOn(wakeupFrame10);
			return;
		}

		J3dUtil.getViewPosition(vp, viewPosition);

		J3dUtil.getCurrentLocalToVworld(this, localToWorldTrans);
		center.set(0, 0, 0);
		localToWorldTrans.transform(center);
		double viewDistance = center.distance(viewPosition);
		///////////////////////////
		/*	Canvas3D canvas = v.getCanvas3D(0);
			// rotate about axis
			canvas.getCenterEyeInImagePlate(viewPosition);
			// transform the points to the Billboard's space
			if (v.getViewPlatform() instanceof LocatableViewPlatform)
			{
				((LocatableViewPlatform) v.getViewPlatform()).getShortCutGroup().getTransform(xform);
			}
			else if (v.getCompatibilityModeEnable())
			{
				v.getViewPlatform().getLocalToVworld(xform);
			}
			else
			{
				canvas.getImagePlateToVworld(xform); // xform is ImagePlateToVworld
			}
			xform.transform(viewPosition);
		
			parent.getLocalToVworld(xform);
		
			xform.invert(); // xform is now vWorldToLocal
		
			// transform the eye position into the billboard's coordinate system
			xform.transform(viewPosition);
		
			// I wager viewPosition is the eye point in the local transforms coordinates, I wager?
			// so let's just use the length 
			double viewDistance = Math.sqrt((viewPosition.x * viewPosition.x) + (viewPosition.y * viewPosition.y)
					+ (viewPosition.z * viewPosition.z));
		
			//TODO: if there is a scale transform node above this one then the view distance is scaled and wrong
			// see bloated float sign, test
			viewDistance = viewDistance / xform.getScale();
			//System.out.println("viewDistance " + viewDistance);
			 
			 */

		int newIndex = distances.length; // viewDistance > distances[n-1]
		double distDiff = 999; // nothing happens beyond the last switch, - is beyond cut off

		if (viewDistance <= distances[0])
		{
			newIndex = 0;
			distDiff = distances[0] - viewDistance;
		}
		else
		{
			for (int i = 1; i < distances.length; i++)
			{
				if ((viewDistance > distances[i - 1]) && (viewDistance <= distances[i]))
				{
					newIndex = i;
					distDiff = distances[i] - viewDistance;
					break;
				}
			}
		}

		// have we changed indexes
		if (newIndex != prevIndex)
		{
			//remove prev and it's neighbour if added
			if (prevIndex != -1)
			{
				ensureRemoved(prevIndex);
				ensureRemoved(prevIndex + 1);
			}

			// ensure new is added, neigh only added if needed below
			ensureAdded(newIndex, true);

			prevIndex = newIndex;
		}

		float fadeRange = (float) (MIN_FADE_RANGE + (viewDistance / FADE_RANGE_DIVISOR));
		//System.out.println("viewDistance " + viewDistance + ": distDiff " + distDiff + ": fadeRange " + fadeRange);
		//simple case not near interface, just for a plain model
		if (distDiff > fadeRange)
		{
			if (!popOnly)
			{
				// ensure no fade for new index
				if (newIndex < roots.size() && roots.get(newIndex) != null && roots.get(newIndex) instanceof Fadable)
				{
					((Fadable) roots.get(newIndex)).fade(0f);
				}
			}

			// and neighbour not added
			ensureRemoved(newIndex + 1);

		}
		//else if (distDiff <= 0)
		//{
			//Yes!
			//System.out.println("BetterDistanceLOD can this ever happen? " + distDiff);
		//}
		else
		{
			if (!popOnly)
			{
				float fade = (float) (distDiff / fadeRange);
				//	System.out.println("fade " + fade);
				if (newIndex < roots.size() && roots.get(newIndex) != null && roots.get(newIndex) instanceof Fadable)
				{
					((Fadable) roots.get(newIndex)).fade(1f - fade);
				}
			}

			if (newIndex + 1 < roots.size() && roots.get(newIndex + 1) instanceof Fadable)
			{
				//ensureAdded(newIndex + 1, false);
				//((Fadable) roots.get(newIndex + 1)).fade(fade);//inverse

				// one side looks better all round (neighbour is always opaque)
				ensureAdded(newIndex + 1, true);
			}

		}

		// Insert wakeup condition into queueif based on proximity to interesting ness
		if (distDiff < 1.5)

			wakeupOn(wakeupFrame0);
		else if (distDiff < 5)
			wakeupOn(wakeupFrame2);
		else if (distDiff < 12)
			wakeupOn(wakeupFrame5);
		else
			wakeupOn(wakeupFrame10);

		//FIXME: debug
		//wakeupOn(new WakeupOnElapsedFrames(100, true)); 
	}

	private void ensureAdded(int idx, boolean removeFade)
	{
		if (idx < roots.size())
		{
			BranchGroup bg = roots.get(idx);
			if (bg != null && !bg.isLive())
			{
				parent.addChild(bg);
				if (!popOnly && removeFade && bg instanceof Fadable)
				{
					((Fadable) bg).fade(0f);
				}
			}
		}
	}

	private void ensureRemoved(int idx)
	{
		if (idx < roots.size())
		{
			BranchGroup bg = roots.get(idx);
			if (bg != null && bg.isLive())
			{
				parent.removeChild(bg);
				if (!popOnly && bg instanceof Fadable)
				{
					((Fadable) bg).fade(0f);
				}
			}
		}
	}
}
