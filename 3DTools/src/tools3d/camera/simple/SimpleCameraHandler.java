package tools3d.camera.simple;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.CapabilityNotSetException;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import tools3d.utils.YawPitch;

import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.newt.MouseRotate;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class SimpleCameraHandler extends BranchGroup
{
	private final ViewingPlatform viewingPlatform;

	private final Canvas3D canvas3D;

	private MyMouseRotater freeLookMouseRotate;

	private MouseRotate modelRotateMouseRotate;

	private PickCanvas selectPickCanvas;

	private double defaultMove = 1d;

	private boolean isFreeLook = true;

	private Transform3D viewTransform = new Transform3D();

	private JFrame parentFrame;

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
		System.out.println("SimpleCameraHandler right mouse to change from model spin to freelook");
		this.viewingPlatform = _viewingPlatform;
		this.canvas3D = _canvas3D;
		parentFrame = (JFrame) SwingUtilities.getWindowAncestor(canvas3D);
		freeLookMouseRotate = new MyMouseRotater(canvas3D.getGLWindow(), viewingPlatform.getViewPlatformTransform());

		modelRotateTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		modelRotateTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		 freeLookMouseRotate.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
		 freeLookMouseRotate.setEnable(true);
		 addChild(freeLookMouseRotate);

		 modelRotateMouseRotate = new MouseRotate(canvas3D.getGLWindow(), modelRotateTransformGroup);
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
				parentFrame.setTitle("Rot = " + yawPitch + " loc = " + loc);
			}
		});

		TransformGroup tg = viewingPlatform.getViewPlatformTransform();
		tg.setTransform(viewTransform);

		selectPickCanvas = new PickCanvas(canvas3D, modelGroup);
		selectPickCanvas.setMode(PickInfo.PICK_GEOMETRY);
		selectPickCanvas.setTolerance(0.0f);
		selectPickCanvas.setFlags(PickInfo.CLOSEST_INTERSECTION_POINT);

		canvas3D.addMouseListener(new MouseAdapter() {
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

		});

		canvas3D.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getWheelRotation() < 0)
				{
					forward();
				}
				else
				{
					back();
				}
			}
		});

		canvas3D.addKeyListener(new KeyAdapter() {

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
				if (parentFrame != null)
					parentFrame.setTitle("Rot = " + yawPitch + " loc = " + loc);
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
		if (parentFrame != null)
			parentFrame.setTitle("Rot = " + yawPitch + " loc = " + loc);
	}

	private void back()
	{
		viewTransform.get(loc);
		loc.z += -1.0;
		viewTransform.setTranslation(loc);
		viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);
		if (parentFrame != null)
			parentFrame.setTitle("Rot = " + yawPitch + " loc = " + loc);

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
		}

		t.lookAt(eye, center, up);
		t.invert();
		viewTransform.set(t);
		viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);
		parentFrame = (JFrame) SwingUtilities.getWindowAncestor(canvas3D);
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
				eye.z = boundingSphere.getRadius() * 4d;
			}
			else
			{
				eye.z = 10;
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
