package tools3d.environment;

import java.util.Enumeration;
import java.util.Random;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.audio.SoundEngine;
import tools3d.utils.FlippableAlpha;
import tools3d.utils.LockStepTranslationBehavior;
import tools3d.utils.Utils3D;
import tools3d.utils.scenegraph.LocationUpdateListener;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

/**
 * @author pj
 *
 *
 */
public class RainyArea extends BranchGroup implements LocationUpdateListener
{
	private final int RAIN_OFF = 0;

	private final int RAIN_ON = 1;

	private final int RAIN_STARTING = 2;

	private final int RAIN_STOPING = 3;

	private final float START_Y = 10;

	private final float END_Y = -10;

	private TransformGroup hoverTransform = new TransformGroup();

	private LineArray waterLines = null;

	private GeometryUpdater geometryUpdater = new WaterUpdater(hoverTransform);

	private UpdateWaterBehavior behave;

	private FlippableAlpha rainAlpha;

	private int state = RAIN_OFF;

	private float topY = START_Y;

	private float bottomY = END_Y;

	private int numOfDrops = 3000;

	private Color3f rainColor = new Color3f(0.6f, 0.6f, 0.65f);

	private float rainWidth = 3.5f;

	private float rainTransparency = 0.8f;

	private int rainUpdateFrequency = 40;

	private Vector3f windDirection = new Vector3f(0.0f, 0f, 0f); //Note y is ignored

	private float xSize = 6f;

	private float zSize = 6f;

	private float fallDistance = 0.5f;

	private long timeToRampUp = 20000;

	private BranchGroup rainySound;

	private PickTool collisionPickTool;

	private boolean isRaining = false;

	public RainyArea(float topY, float bottomY, int numOfDrops, Color3f rainColor, float rainWidth, float rainTransparency,
			int rainUpdateFrequency, Vector3f windDirection, float xSize, float zSize, float fallIn1Second, long timeToRampUp)
	{
		this.topY = topY;
		this.bottomY = bottomY;
		this.numOfDrops = numOfDrops;
		this.rainColor = rainColor;
		this.rainWidth = rainWidth;
		this.rainTransparency = rainTransparency;
		this.rainUpdateFrequency = rainUpdateFrequency;
		this.windDirection = windDirection;
		this.xSize = xSize;
		this.zSize = zSize;
		this.fallDistance = fallIn1Second / (1000 / rainUpdateFrequency);
		this.timeToRampUp = timeToRampUp;
		setUp();
	}

	public RainyArea()
	{
		// just use the defaults
		setUp();
	}

	private void setUp()
	{
		hoverTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		hoverTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setCapability(Group.ALLOW_CHILDREN_EXTEND);
		setCapability(Group.ALLOW_CHILDREN_WRITE);
		addChild(hoverTransform);
		rainAlpha = new FlippableAlpha(timeToRampUp, timeToRampUp / 4);
		// create the water model
		Appearance appearance = new Appearance();
		appearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.BLENDED, rainTransparency));
		appearance.setColoringAttributes(new ColoringAttributes(rainColor, ColoringAttributes.FASTEST));
		LineAttributes lineAttrib = new LineAttributes();
		lineAttrib.setLineWidth(rainWidth);
		appearance.setLineAttributes(lineAttrib);
		Shape3D waterShape = new Shape3D(createWaterGeometry(), appearance);
		addChild(waterShape);

		// create and add the water behaviour
		behave = new UpdateWaterBehavior();
		behave.setSchedulingBounds(Utils3D.defaultBounds);
		behave.setEnable(false);
		addChild(behave);

	}

	public void setPhysicalBranch(BranchGroup physicalBranch)
	{
		collisionPickTool = new PickTool(physicalBranch);
		collisionPickTool.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
	}

	private float[] dropStopY;

	private Geometry createWaterGeometry()
	{
		waterLines = new LineArray(numOfDrops * 2, GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE);
		waterLines.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
		waterLines.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
		waterLines.setCapability(GeometryArray.ALLOW_COUNT_READ);

		float[] dropCoordinates = new float[numOfDrops * 3 * 2];
		dropStopY = new float[numOfDrops];

		for (int p = 0; p < numOfDrops; p += 2)
		{ // for each particle 
			dropCoordinates[p * 3 + 0] = 0.0f;
			dropCoordinates[p * 3 + 1] = bottomY - 400;
			dropCoordinates[p * 3 + 2] = 0.0f;
			dropCoordinates[p * 3 + 3] = 0.0f;
			dropCoordinates[p * 3 + 4] = bottomY - 400;
			dropCoordinates[p * 3 + 5] = 0.0f;
		}
		waterLines.setCoordRefFloat(dropCoordinates);

		return waterLines;
	}

	public void hoverOver(TransformGroup source)
	{
		// create and add the behaviour
		LockStepTranslationBehavior behaviour = new LockStepTranslationBehavior(source, hoverTransform);
		behaviour.setSchedulingBounds(Utils3D.defaultBounds);
		behaviour.setEnable(true);
		addChild(behaviour);
	}

	public void toggleRain()
	{
		if (isRaining)
		{
			turnRainOff();
		}
		else
		{
			turnRainOn();
		}
	}

	public synchronized void turnRainOn()
	{
		if (state == RAIN_OFF)
		{
			rainAlpha.setStartTime(System.currentTimeMillis());
			state = RAIN_STARTING;
			behave.setEnable(true);
		}
		else if (state == RAIN_STOPING)
		{
			rainAlpha.flipAlpha();
			state = RAIN_STARTING;
		}

		if (rainySound == null)
		{
			rainySound = SoundEngine.createBackgroundSound("media/sounds/rain.wav");
			rainySound.setCapability(BranchGroup.ALLOW_DETACH);
			addChild(rainySound);
		}

		isRaining = true;
	}

	public synchronized void turnRainOff()
	{

		if (state == RAIN_ON)
		{
			state = RAIN_STOPING;
			rainAlpha.setStartTime(System.currentTimeMillis());
		}
		else if (state == RAIN_STARTING)
		{
			rainAlpha.flipAlpha();
			state = RAIN_STOPING;
		}

		if (rainySound != null)
		{
			rainySound.detach();
			rainySound = null;
		}
		isRaining = false;
	}

	class UpdateWaterBehavior extends Behavior
	{
		WakeupOnElapsedTime wakeUp = new WakeupOnElapsedTime(rainUpdateFrequency);

		public UpdateWaterBehavior()
		{
		}

		public void initialize()
		{
			wakeupOn(wakeUp);
		}

		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		public void processStimulus(Enumeration critiria)
		{
			if (state == RAIN_STARTING && rainAlpha.value() == 1f)
			{
				state = RAIN_ON;
			}
			else if (state == RAIN_STOPING && (System.currentTimeMillis() - rainAlpha.getStartTime()) > timeToRampUp + 5000)
			{
				state = RAIN_OFF;
				setEnable(false);
			}
			waterLines.updateData(geometryUpdater);
			wakeupOn(wakeUp);
		}

	}

	public class WaterUpdater implements GeometryUpdater
	{
		private Random random = new Random();

		private TransformGroup hoverT;

		private Transform3D t = new Transform3D();

		private Vector3f vec = new Vector3f();

		public WaterUpdater(TransformGroup hoverTransform)
		{
			this.hoverT = hoverTransform;
		}

		//deburners
		Point3d start = new Point3d();

		Point3d end = new Point3d();

		Vector3d dir = new Vector3d();

		public void updateData(Geometry geometry)
		{
			hoverT.getTransform(t);
			t.get(vec);

			GeometryArray geometryArray = (GeometryArray) geometry;
			float[] coords = geometryArray.getCoordRefFloat();
			int N = geometryArray.getValidVertexCount();

			int i;
			float x, y, z, l, lengthRatio;

			float halfXSize = xSize / 2;
			float halfZSize = zSize / 2;

			float vecXLessHalfXSize = vec.x - halfXSize;
			float vecXPlusHalfXSize = vec.x + halfXSize;
			float vecZLessHalfZSize = vec.z - halfZSize;
			float vecZPlusHalfZSize = vec.z + halfZSize;

			float point8fall = fallDistance * 0.8f;
			float point4fall = fallDistance * 0.4f;

			float bottomYLess400 = bottomY - 400;

			int maxToSpawn = 0;
			switch (state)
			{
				case RAIN_ON:
					maxToSpawn = N;
					break;
				case RAIN_OFF:
					maxToSpawn = 0;
					break;
				case RAIN_STARTING:
					maxToSpawn = (int) (N * rainAlpha.value());
					break;
				case RAIN_STOPING:
					maxToSpawn = (int) (N * (1 - rainAlpha.value()));
					break;
			}

			// for each particle
			for (i = 0; i < N; i += 2)
			{
				if (coords[i * 3 + 1] > bottomYLess400)
				{
					// update active particles 
					coords[i * 3 + 0] += windDirection.x; //x1
					coords[i * 3 + 1] -= fallDistance; //y1
					coords[i * 3 + 2] += windDirection.z; //z1
					coords[i * 3 + 3] += windDirection.x; //x2
					coords[i * 3 + 4] -= fallDistance; //y2
					coords[i * 3 + 5] += windDirection.z; //z2

					if (coords[i * 3 + 4] < dropStopY[i / 2])
					{
						// reset drop position
						coords[i * 3 + 0] = 0.0f; //x1
						coords[i * 3 + 1] = bottomYLess400; //y1
						coords[i * 3 + 2] = 0.0f; //z1
						coords[i * 3 + 3] = 0.0f; //x2
						coords[i * 3 + 4] = bottomYLess400; //y2
						coords[i * 3 + 5] = 0.0f; //z2
					}

					else
					{
						// if the x and y square has moved we need to take any drops 
						// that have fallen out of the area and put them back in the other side, 
						// this means that all x's and z's wrap into the x and z of the area.
						if (coords[i * 3 + 3] < vecXLessHalfXSize)
						{
							coords[i * 3 + 0] += xSize;
							coords[i * 3 + 3] += xSize;
						}
						else if (coords[i * 3 + 3] > vecXPlusHalfXSize)
						{
							coords[i * 3 + 0] -= xSize;
							coords[i * 3 + 3] -= xSize;
						}
						if (coords[i * 3 + 5] < vecZLessHalfZSize)
						{
							coords[i * 3 + 2] += zSize;
							coords[i * 3 + 5] += zSize;
						}
						else if (coords[i * 3 + 5] > vecZPlusHalfZSize)
						{
							coords[i * 3 + 2] -= zSize;
							coords[i * 3 + 5] -= zSize;
						}
					}
				}
				else
				{
					// are we allowed to let any more out?
					if (i < maxToSpawn)
					{
						x = vecXLessHalfXSize + (xSize * random.nextFloat());
						y = vec.y + topY + (fallDistance * random.nextFloat());
						z = vecZLessHalfZSize + (zSize * random.nextFloat());
						l = (point8fall * random.nextFloat()) + point4fall;

						lengthRatio = l / fallDistance;

						coords[i * 3 + 0] = x + (lengthRatio * windDirection.x); //x1
						coords[i * 3 + 1] = y - l; //y
						coords[i * 3 + 2] = z + (lengthRatio * windDirection.z); //z1
						coords[i * 3 + 3] = x; //x2
						coords[i * 3 + 4] = y; //y2
						coords[i * 3 + 5] = z; //z2

						// regardless stop at the bottom
						dropStopY[i / 2] = bottomY;

						end.set(coords[i * 3 + 0], coords[i * 3 + 1], coords[i * 3 + 2]);
						start.set(coords[i * 3 + 3], coords[i * 3 + 4], coords[i * 3 + 5]);
						dir.sub(end, start);

						// now to record it's end y value
						if (collisionPickTool != null)
						{
							collisionPickTool.setShapeRay(start, dir);
							PickResult pr = collisionPickTool.pickClosest();
							if (pr != null)
							{
								dropStopY[i / 2] = (float) pr.getIntersection(0).getPointCoordinates().y;
							}
						}
					}

				}
			}
		}
	}

	/**
	 * @see tools3d.utils.scenegraph.LocationUpdateListener#locationUpdated(Quat4f, Vector3f)
	 */
	//	de burners
	private Transform3D destTran = new Transform3D();

	public void locationUpdated(Quat4f rot, Vector3f trans)
	{
		// get the transforms 			
		hoverTransform.getTransform(destTran);

		// now set the translation component from source into dest	 
		destTran.set(trans);

		// and set the destination
		hoverTransform.setTransform(destTran);
	}

	/**
	 * @return
	 */
	public boolean isRaining()
	{
		return isRaining;
	}

}
