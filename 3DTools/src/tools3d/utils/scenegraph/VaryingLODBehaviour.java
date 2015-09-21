package tools3d.utils.scenegraph;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.J3dUtil;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector4d;

/** A behavior that updates on a frequency based on distance from viewer, 
 * so more distance animations can run slower
 * current 1 frame 5 frames 15 frames and 50 frame
 * 
 * @author philip
 *
 */
public abstract class VaryingLODBehaviour extends Behavior
{
	// Wakeup condition for node
	private WakeupOnElapsedFrames wakeupFrameFast;

	private WakeupOnElapsedFrames wakeupFrame2;

	private WakeupOnElapsedFrames wakeupFrame8;

	private WakeupOnElapsedFrames wakeupFrame20;

	// Node to operate on.
	protected Node node = null;

	private float[] dists = null;

	public static final double EPSILON = 1.0e-6;

	private boolean frustumOnly = false;

	/**
	 * if node is null this will be used for distance check
	 * Dists must be 3 floats! 40,120,280 is fine
	 * Defaults to passive=true
	 *TODO: like knots add frames and dist arrays 
	 * @param node
	 */
	public VaryingLODBehaviour(Node node, float[] dists, boolean passive, boolean frustumOnly)
	{
		this.frustumOnly = frustumOnly;
		this.node = node;
		this.dists = dists;

		if (this.node == null)
			this.node = this;

		this.node.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
		if (frustumOnly)
			this.node.setCapability(Node.ALLOW_BOUNDS_READ);

		wakeupFrameFast = new WakeupOnElapsedFrames(1, passive);
		wakeupFrame2 = new WakeupOnElapsedFrames(2, passive);
		wakeupFrame8 = new WakeupOnElapsedFrames(8, passive);
		wakeupFrame20 = new WakeupOnElapsedFrames(20, passive);
	}

	/**
	 * Initialize method that sets up initial wakeup criteria.
	 */
	public void initialize()
	{
		// Insert wakeup condition into queue
		wakeupOn(wakeupFrameFast);
	}

	//  deburners
	private Point3f center = new Point3f();

	private Point3f viewPosition = new Point3f();

	// deburners
	//private Point3d viewPosition = new Point3d();

	//private Transform3D xform = new Transform3D();

	//deburners
	private Vector4d t = new Vector4d();

	private Point3d c = new Point3d();

	private Point3d c2 = new Point3d();

	private Transform3D mv = new Transform3D();

	private Transform3D pj = new Transform3D();

	private Transform3D pr = new Transform3D(); //ignored 

	/**
	 * Process stimulus method that computes appropriate transform.
	 * @param criteria an enumeration of the criteria that caused the
	 * stimulus
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void processStimulus(Enumeration criteria)
	{

		if (node == null || dists == null || !node.isLive())
		{
			//System.out.println("somefing null");
			wakeupOn(wakeupFrame20);
			return;
		}

		View view = this.getView();
		if (view == null)
		{
			//System.out.println("view null");
			wakeupOn(wakeupFrame20);
			return;
		}

		///////////////////////////
		ViewPlatform vp = view.getViewPlatform();
		if (vp == null)
		{
			wakeupOn(wakeupFrame20);
			return;
		}

		J3dUtil.getViewPosition(vp, viewPosition);

		Transform3D localToWorldTrans = new Transform3D();
		J3dUtil.getCurrentLocalToVworld(this, localToWorldTrans);
		center.set(0, 0, 0);
		localToWorldTrans.transform(center);
		double viewDistance = center.distance(viewPosition);
		///////////////////////////
		//  get viewplatforms's location in virutal world
		/*Canvas3D canvas = v.getCanvas3D(0);

		// rotate about axis
		canvas.getCenterEyeInImagePlate(viewPosition);
		// transform the points to the Billboard's space
		if (this.getView().getCompatibilityModeEnable())
		{
			this.getView().getViewPlatform().getLocalToVworld(xform);
		}
		else
		{
			canvas.getImagePlateToVworld(xform); // xform is ImagePlateToVworld
		}
		xform.transform(viewPosition);

		node.getLocalToVworld(xform);

		xform.invert(); // xform is now vWorldToLocal

		// transform the eye position into the billboard's coordinate system
		xform.transform(viewPosition);

		// I wager viewPosition is the eye point in the local transforms coordinates, I wager?
		//so let's just use the length for setting the wakeup
		double dist = Math.sqrt((viewPosition.x * viewPosition.x) + (viewPosition.y * viewPosition.y) + (viewPosition.z * viewPosition.z));
		//System.out.println("dist " + dist);
		*/

		if (!frustumOnly)
		{
			process();
		}
		else
		{
			//New code for in frustum only	
			try
			{
				node.getLocalToVworld(mv);// put bounds from local node coords to vworld coords				
				BoundingSphere b = new BoundingSphere(node.getBounds());// cheap if sphere or box
				b.transform(mv);

				//NifCharacter is good
				//J3dNiController seems normally to be a 50 (bones good, but flip texture no so)
				//if (b.getRadius() == 50)

				// get center
				b.getCenter(c);
				// get trans from world to clip
				view.getCanvas3D(0).getVworldProjection(pj, pr);
				//use a 4tuple to allow perspective to be returned
				t.set(c.x, c.y, c.z, 1);
				// trans center from vworld to clip coords
				pj.transform(t);
				// Perspective division (don't forget this step)
				c.x = t.x / t.w;
				c.y = t.y / t.w;
				c.z = t.z / t.w;

				// now a very long radius scale operation against perspective
				b.getCenter(c2);
				t.set(c2.x, c2.y, c2.z - b.getRadius(), 1);
				pj.transform(t);
				c2.x = t.x / t.w;
				c2.y = t.y / t.w;
				c2.z = t.z / t.w;
				c2.x -= c.x;
				c2.y -= c.y;
				c2.z -= c.z;

				double r2 = Math.sqrt((c2.x * c2.x) + (c2.y * c2.y) + (c2.z * c2.z));

				if (sphereIntersectUnitBox(c, r2))
				{
					process();
				}
			}
			catch (NullPointerException e)
			{
				//If you set non auto compute bounds, but never set a bounds you'll get an NPE one day	
				// I had set non-auto then just overrode the getBounds call, which is nothing like setting a bounds
				e.printStackTrace();
			}
		}

		//		Insert wakeup condition into queue
		if (viewDistance < dists[0])
		{
			wakeupOn(wakeupFrameFast);
		}
		else if (viewDistance < dists[1])
		{
			wakeupOn(wakeupFrame2);
		}
		else if (viewDistance < dists[2])
		{
			wakeupOn(wakeupFrame8);
		}
		else
		{
			wakeupOn(wakeupFrame20);
		}

		//FIXME: debug
		//wakeupOn(new WakeupOnElapsedFrames(100, true)); 
	}

	private boolean sphereIntersectUnitBox(Point3d sphereCenter, double radius)
	{
		// Get the center of the sphere relative to the center of the box
		double scx = sphereCenter.x;
		double scy = sphereCenter.y;
		double scz = sphereCenter.z;

		// Point on surface of box that is closest to the center of the sphere
		double bpx = 0;
		double bpy = 0;
		double bpz = 0;

		// Check sphere center against box along the X axis alone. 
		// If the sphere is off past the left edge of the box, 
		// then the left edge is closest to the sphere. 
		// Similar if it's past the right edge. If it's between 
		// the left and right edges, then the sphere's own X 
		// is closest, because that makes the X distance 0, 
		// and you can't get much closer than that :)

		if (scx < -1)
			bpx = -1;
		else if (scx > 1)
			bpx = 1;
		else
			bpx = scx;

		// ...same for Y axis
		if (scy < -1)
			bpy = -1;
		else if (scy > 1)
			bpy = 1;
		else
			bpy = scy;

		// ... same for Z axis
		if (scz < -1)
			bpz = -1;
		else if (scz > 1)
			bpz = 1;
		else
			bpz = scz;

		// Now we have the closest point on the box, so get the distance from 
		// that to the sphere center, and see if it's less than the radius
		double distx = scx - bpx;
		double disty = scy - bpy;
		double distz = scz - bpz;

		//orignal had ^2 of radius instead of sqrt but doesn't work for < 1 radius!
		if (Math.sqrt((distx * distx) + (disty * disty) + (distz * distz)) < radius)
			return true;
		else
			return false;
	}

	protected abstract void process();

}
