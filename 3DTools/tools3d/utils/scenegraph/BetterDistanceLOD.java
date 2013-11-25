package tools3d.utils.scenegraph;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;

//OH MY GOD! switches pointing to links don't refresh properly!
// I also notice that shared grup appear to be massively inefficient
//allows a transition zone of fadness sent out to 2 nodes attached

public class BetterDistanceLOD extends Behavior
{
	private float FADE_RANGE = 3f; //TODO: better as a percent of min dist?

	private WakeupOnElapsedFrames wakeupFrame = new WakeupOnElapsedFrames(10, true);

	private int prevIndex = -1;

	double[] distances;

	private Group parent;

	private ArrayList<BranchGroup> roots;

	// deburners
	private Point3d viewPosition = new Point3d();

	private Transform3D xform = new Transform3D();

	static final double EPSILON = 1.0e-6;

	public BetterDistanceLOD(Group parent, ArrayList<BranchGroup> roots, float[] distances)
	{

		this.parent = parent;
		parent.setCapability(Group.ALLOW_CHILDREN_WRITE);
		parent.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		parent.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		this.roots = roots;
		for (BranchGroup bg : roots)
		{
			bg.setCapability(BranchGroup.ALLOW_DETACH);
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
		wakeupOn(wakeupFrame);

	}

	public void setDistance(int whichDistance, double distance)
	{
		distances[whichDistance] = distance;
	}

	@SuppressWarnings("rawtypes")
	public void processStimulus(Enumeration criteria)
	{

		if (parent == null)
		{
			//System.out.println("somefing null");
			wakeupOn(wakeupFrame);
			return;
		}

		//  get viewplatforms's location in virutal world
		View v = this.getView();
		if (v == null)
		{
			//System.out.println("view null");
			wakeupOn(wakeupFrame);
			return;
		}
		Canvas3D canvas = v.getCanvas3D(0);

		// rotate about axis
		canvas.getCenterEyeInImagePlate(viewPosition);
		canvas.getImagePlateToVworld(xform); // xform is imagePlateToLocal
		xform.transform(viewPosition);

		parent.getLocalToVworld(xform);

		xform.invert(); // xform is now vWorldToLocal

		// transform the eye position into the billboard's coordinate system
		xform.transform(viewPosition);

		// I wager viewPosition is the eye point in the local transforms coordinates, I wager?
		// so let's just use the length for setting the wakeup
		double viewDistance = Math.sqrt((viewPosition.x * viewPosition.x) + (viewPosition.y * viewPosition.y)
				+ (viewPosition.z * viewPosition.z));
		//System.out.println("viewDistance" + viewDistance);

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

		// have we change indexes
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

		//simple case not near interface, just for a plain model
		if (distDiff > FADE_RANGE)
		{
			// ensure no fade for new index
			if (newIndex < roots.size() && roots.get(newIndex) != null && roots.get(newIndex) instanceof Fadable)
			{
				((Fadable) roots.get(newIndex)).fade(0f);
			}

			// and neighbour not added
			ensureRemoved(newIndex + 1);

		}
		else if (distDiff <= 0)
		{
			System.out.println("BetterDistanceLOD can this ever happen? " + distDiff);
		}
		else
		{
			float fade = (float) (distDiff / FADE_RANGE);

			if (newIndex < roots.size() && roots.get(newIndex) != null && roots.get(newIndex) instanceof Fadable)
			{
				((Fadable) roots.get(newIndex)).fade(1f - fade);
			}

			if (newIndex + 1 < roots.size() && roots.get(newIndex + 1) instanceof Fadable)
			{
				//ensureAdded(newIndex + 1, false);
				//((Fadable) roots.get(newIndex + 1)).fade(fade);//inverse

				// one side looks better all round (neighbour is always opaque)
				ensureAdded(newIndex + 1, true);
			}
		}

		// Insert wakeup condition into queue
		wakeupOn(wakeupFrame);
	}

	private void ensureAdded(int idx, boolean removeFade)
	{
		if (idx < roots.size())
		{
			BranchGroup bg = roots.get(idx);
			if (bg != null && !bg.isLive())
			{
				parent.addChild(bg);
				if (removeFade && bg instanceof Fadable)
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
				if (bg instanceof Fadable)
				{
					((Fadable) bg).fade(0f);
				}
			}
		}
	}
}
