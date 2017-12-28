package tools3d.camera.simple;

import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.Bounds;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.CapabilityNotSetException;
import org.jogamp.java3d.PickInfo;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.behaviors.mouse.MouseBehaviorCallback;
import org.jogamp.java3d.utils.behaviors.mouse.newt.MouseRotate;
import org.jogamp.java3d.utils.pickfast.PickCanvas;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Quat4d;
import org.jogamp.vecmath.Vector3d;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

import tools3d.utils.YawPitch;

public class SimpleCameraHandler extends BranchGroup
{
	private final ViewingPlatform viewingPlatform;

	private final Canvas3D canvas3D;

	private MyMouseRotater freeLookMouseRotate;

	private MouseRotate modelRotateMouseRotate;

	private PickCanvas selectPickCanvas;

	private double defaultMove = 1d;

	private boolean isFreeLook = true;

	private boolean topHalfOnly = true;

	private Transform3D viewTransform = new Transform3D();

	private YawPitch yawPitch = new YawPitch();

	private Vector3d loc = new Vector3d();

	public SimpleCameraHandler(ViewingPlatform viewingPlatform, Canvas3D canvas3D, BranchGroup modelGroup,
			TransformGroup modelRotateTransformGroup, boolean defaultToFreeLook, double defaultMove)
	{
		this(viewingPlatform, canvas3D, modelGroup, modelRotateTransformGroup, defaultToFreeLook);
		this.defaultMove = defaultMove;

	}

	public SimpleCameraHandler(ViewingPlatform _viewingPlatform, Canvas3D _canvas3D, BranchGroup modelGroup,
			TransformGroup modelRotateTransformGroup, boolean defaultToFreeLook)
	{
		this(_viewingPlatform, _canvas3D, modelGroup, modelRotateTransformGroup, defaultToFreeLook, false);
	}

	public SimpleCameraHandler(ViewingPlatform _viewingPlatform, Canvas3D _canvas3D, BranchGroup modelGroup,
			TransformGroup modelRotateTransformGroup, boolean defaultToFreeLook, boolean _topHalfOnly)
	{
		System.out.println("SimpleCameraHandler right mouse to change from model spin to freelook");
		this.viewingPlatform = _viewingPlatform;
		this.canvas3D = _canvas3D;
		this.topHalfOnly = _topHalfOnly;
		//parentFrame = (JFrame) SwingUtilities.getWindowAncestor(canvas3D);
		freeLookMouseRotate = new MyMouseRotater(canvas3D.getGLWindow(), viewingPlatform.getViewPlatformTransform());

		modelRotateTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		modelRotateTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		freeLookMouseRotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
		freeLookMouseRotate.setEnable(true);
		addChild(freeLookMouseRotate);

		modelRotateMouseRotate = new MouseRotate(canvas3D.getGLWindow(), modelRotateTransformGroup) {
			protected void doProcess(MouseEvent evt)
			{
				processMouseEvent(evt);
				if (!topHalfOnly || evt.getY() < canvas3D.getGLWindow().getHeight() / 2)
					super.doProcess(evt);

			}
		};
		modelRotateMouseRotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
		modelRotateMouseRotate.setEnable(false);
		addChild(modelRotateMouseRotate);

		freeLookMouseRotate.setupCallback(new MouseBehaviorCallback() {
			@Override
			public void transformChanged(int type, Transform3D transform)
			{
				// ensure yaw pitch only				
				viewingPlatform.getViewPlatformTransform().getTransform(viewTransform);
				yawPitch.set(viewTransform);
				viewTransform.setRotation(yawPitch.get(new Quat4d()));
				viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);
				canvas3D.getGLWindow().setTitle("Rot = " + yawPitch + " loc = " + loc);
			}
		});

		TransformGroup tg = viewingPlatform.getViewPlatformTransform();
		tg.setTransform(viewTransform);

		selectPickCanvas = new PickCanvas(canvas3D, modelGroup);
		selectPickCanvas.setMode(PickInfo.PICK_GEOMETRY);
		selectPickCanvas.setTolerance(0.0f);
		selectPickCanvas.setFlags(PickInfo.CLOSEST_INTERSECTION_POINT);

		canvas3D.getGLWindow().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					centerOnPickedObject(e);
				}
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					setFreeLook(!isFreeLook);
				}

			}

			public void mouseWheelMoved(MouseEvent e)
			{
				if (e.getRotation()[1] < 0)
				{
					forward();
				}
				else
				{
					back();
				}
			}

		});

		canvas3D.getGLWindow().addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e)
			{
				viewingPlatform.getViewPlatformTransform().getTransform(viewTransform);

				double moveAmount = defaultMove;
				if (e.isShiftDown())
				{
					moveAmount = defaultMove * 5;
				}

				Vector3d left = new Vector3d(-1, 0, 0);
				Vector3d up = new Vector3d(0, 1, 0);
				Vector3d m = new Vector3d(0, 0, 0);

				//viewTransform.transform(forward);

				//dir.negate();

				if (e.getKeyCode() == KeyEvent.VK_W)
				{
					m.set(up);
				}
				else if (e.getKeyCode() == KeyEvent.VK_S)
				{
					m.set(up);
					m.negate();
				}
				else if (e.getKeyCode() == KeyEvent.VK_A)
				{
					m.set(left);
				}
				else if (e.getKeyCode() == KeyEvent.VK_D)
				{
					m.set(left);
					m.negate();
				}
				else
				{
					m.set(0, 0, 0);
				}

				m.scale(moveAmount);

				viewTransform.get(loc);
				loc.add(m);
				viewTransform.setTranslation(loc);

				viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);

				if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_2)
				{
					forward();
				}
				else if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_8)
				{
					back();
				}

				canvas3D.getGLWindow().setTitle("Rot = " + yawPitch + " loc = " + loc);
			}

		});

		setFreeLook(defaultToFreeLook);

		System.out.println("Mouse wheel zoom in/out");
		System.out.println("W S A D move");

	}

	private void forward()
	{
		viewTransform.get(loc);
		loc.z += 1.0;
		viewTransform.setTranslation(loc);
		viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);

		canvas3D.getGLWindow().setTitle("Rot = " + yawPitch + " loc = " + loc);
	}

	private void back()
	{
		viewTransform.get(loc);
		loc.z += -1.0;
		viewTransform.setTranslation(loc);
		viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);

		canvas3D.getGLWindow().setTitle("Rot = " + yawPitch + " loc = " + loc);

	}

	private void setFreeLook(boolean b)
	{
		isFreeLook = b;
		System.out.println("free look set to " + isFreeLook);
		freeLookMouseRotate.setEnable(isFreeLook);
		modelRotateMouseRotate.setEnable(!isFreeLook);
	}

	protected void centerOnPickedObject(MouseEvent mouseEvent)
	{
		if (mouseEvent != null)
		{
			selectPickCanvas.setShapeLocation(mouseEvent);
		}

		PickInfo result = null;
		try
		{
			result = selectPickCanvas.pickClosest();
		}
		catch (CapabilityNotSetException e)
		{
			System.out.println(e.toString());
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}

		if (result != null)
		{
			Point3d eye = new Point3d(0, 0, 0);
			Point3d center = new Point3d(0, 0, 0);

			Point3d intPoint = new Point3d();
			result.getClosestIntersectionPoint().get(intPoint);
			intPoint.get(center);
			intPoint.get(eye);
			//eye.z += 5;
			setView(eye, center);
		}
	}

	public void setView(Point3d eye, Point3d center)
	{
		Transform3D t = new Transform3D();
		Vector3d up = new Vector3d(0, 1, 0);
		if (eye.x == center.x && eye.y == center.y)
		{
			center.y += 0.01;
			center.x += 0.01;
			center.z -= 0.01;
		}

		t.lookAt(eye, center, up);
		t.invert();
		viewTransform.set(t);
		viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);
		//parentFrame = (JFrame) SwingUtilities.getWindowAncestor(canvas3D);
	}

	public void viewBounds(Bounds bounds)
	{
		if (bounds instanceof BoundingSphere)
		{
			Point3d eye = new Point3d(0, 0, 0);
			Point3d center = new Point3d(0, 0, 0);
			BoundingSphere boundingSphere = (BoundingSphere) bounds;
			boundingSphere.getCenter(center);
			// some time radius is -1 or a massive number, ignore those
			if (boundingSphere.getRadius() < 50000 && boundingSphere.getRadius() > 0)
			{
				eye.z = boundingSphere.getRadius() * 2d;
			}
			else
			{
				eye.z = 5;
				System.out.println("boundingSphere.getRadius() " + boundingSphere.getRadius());
			}
			setView(eye, center);
		}
		else
		{
			System.out.println("bounds not handled " + bounds);
		}
	}

}
