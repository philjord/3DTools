package tools3d.camera;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import android.widget.GridLayout;
import tools3d.audio.JOALMixer;
import tools3d.mixed3d2d.Canvas3D2D;
import tools3d.ovr.HmdDesc;
import tools3d.ovr.HmdInfo;
import tools3d.universe.VisualPhysicalUniverse;

public class HMDCameraPanel implements ICameraPanel
{
	public static float FRONT_CLIP = 0.1f;

	public static float BACK_CLIP = 5000f;

	protected VisualPhysicalUniverse universe;

	//private double FOV = 80;

	private float xfov = (float) (125f * Math.PI / 180.0);

	private JOALMixer mixer = null;

	private Canvas3D2D leftCanvas3D2D;

	private Canvas3D2D rightCanvas3D2D;

	private PhysicalBody physicalBody = null;

	private PhysicalEnvironment physicalEnvironment = null;

	private View leftView = null;

	private View rightView = null;

	private HMDCamDolly currentDolly;// viewPlatform

	private HmdInfo hmdInfo;
	private HmdDesc hmdDesc;

	private boolean isRendering = false;

	public HMDCameraPanel(VisualPhysicalUniverse universe)
	{
		this.universe = universe;
		hmdInfo = HMDCamDolly.getOculusRift().getHMDInfo();
		hmdDesc = HMDCamDolly.getOculusRift().getHmdDesc();

		physicalBody = new PhysicalBody(new Point3d(-0.033, 0, 0), new Point3d(0.033, 0, 0));
		physicalEnvironment = new PhysicalEnvironment();

		leftCanvas3D2D = new Canvas3D2D();
		leftCanvas3D2D.setMonoscopicViewPolicy(View.LEFT_EYE_VIEW);

		rightCanvas3D2D = new Canvas3D2D();
		rightCanvas3D2D.setMonoscopicViewPolicy(View.LEFT_EYE_VIEW);
		rightCanvas3D2D.isLeft = false;

		leftView = createView(leftCanvas3D2D);
		setProjectionMatrix(leftView, true);
		rightView = createView(rightCanvas3D2D);
		setProjectionMatrix(rightView, false);

		//doesn't work but same screen
		//	leftCanvas3D2D.getScreen3D().setPhysicalScreenHeight(hmd.VScreenSize);
		//	leftCanvas3D2D.getScreen3D().setPhysicalScreenWidth(hmd.HScreenSize * 0.5f);

		// non shared ctx causes texture cock ups
		if (System.getProperty("j3d.sharedctx") == null)
		{
			System.out.println("Don't forget -Dj3d.sharedctx=true! otherwise texture oddities");
		}

		//TODO:
		// mouse over, appears to point off to the right maybe?
		// turn on shaders again
		// post process on FBO, joglPipeline postProcessFrameBuffer
		// add interacting dashboard hud shape above my head in a no oculus position

	}

	private View createView(Canvas3D c)
	{
		// create a viewer with the given canvas, physical environemnt and physical body are defaulted
		View view = new View();
		view.addCanvas3D(c);
		view.setPhysicalBody(physicalBody);
		view.setPhysicalEnvironment(physicalEnvironment);
		view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

		// set up the view
		view.setFrontClipPolicy(View.VIRTUAL_EYE);
		view.setBackClipDistance(BACK_CLIP);
		view.setFrontClipDistance(FRONT_CLIP);

		float halfScreenDistanceH = (hmdInfo.HScreenSize / 2);
		xfov = (float) (2.0f * Math.atan(halfScreenDistanceH / hmdInfo.EyeToScreenDistance));
		view.setFieldOfView(xfov);
		System.out.println("FOV set to  " + xfov);

		//other wise restricted access exception
		if (view.getUserHeadToVworldEnable())
		{
			// create and adds a joalmixer as the audio device
			mixer = new JOALMixer(physicalEnvironment);
			boolean success = mixer.initialize();

			if (!success)
			{
				System.out.println("Open AL failed to init");
				// remove the audio device
				physicalEnvironment.setAudioDevice(null);
			}
		}
		return view;
	}

	@Override
	public void stopRendering()
	{
		if (leftCanvas3D2D.isRendererRunning())
		{
			//leftCanvas3D2D.stopRenderer();
			//if (this.isAncestorOf(leftCanvas3D2D))
			{
				//	remove(leftCanvas3D2D);
			}
			//rightCanvas3D2D.stopRenderer();
			//if (this.isAncestorOf(rightCanvas3D2D))
			{
				//	remove(rightCanvas3D2D);
			}
			isRendering = false;
		}
	}

	public void startRendering()
	{
		if (!isRendering())
		{
			//if (leftCanvas3D2D.getParent() != this)
			{
				//add(leftCanvas3D2D);
				//add(rightCanvas3D2D);
				//				validate();
				leftCanvas3D2D.startRenderer();
				rightCanvas3D2D.startRenderer();
			}

			isRendering = true;
		}
	}

	public void setPhysicalsVisible(boolean visible)
	{
		if (visible)
		{
			universe.setViewForPhysicalBranch(leftView);
		}
		else
		{
			universe.clearViewForPhysicalBranch();
		}
	}

	private void setHMDCamDolly(HMDCamDolly hmdCamDolly)
	{
		if (currentDolly != null)
		{
			universe.removeViewingPlatform(currentDolly);
		}
		currentDolly = hmdCamDolly;
		universe.addViewingPlatform(currentDolly);
		// it is assumed to be added to the scene graph itself
		// universe.addViewingPlatform(currentDolly);
		currentDolly.attachViews(leftView, rightView);

	}

	public boolean isRendering()
	{
		return isRendering;
	}

	/**
	 * Note only left canvas is handed back 
	 * @see tools3d.camera.ICameraPanel#getCanvas3D2D()
	 */
	@Override
	public Canvas3D2D getCanvas3D2D()
	{
		return leftCanvas3D2D;
	}

	@Override
	public void setSceneAntialiasingEnable(boolean aaRequired)
	{
		if (leftView != null)
		{
			System.out.println("setSceneAntialiasingEnable " + aaRequired);
			leftView.setSceneAntialiasingEnable(aaRequired);
			rightView.setSceneAntialiasingEnable(aaRequired);
		}
	}

	@Override
	public IDolly getDolly()
	{
		return currentDolly;
	}

	@Override
	public void setDolly(IDolly dolly)
	{
		setHMDCamDolly((HMDCamDolly) dolly);
	}

	private void setProjectionMatrix(View view, boolean left)
	{
		// Compute Aspect Ratio. Stereo mode cuts width in half.
		float aspectRatio = (hmdDesc.resolutionW * 0.5f) / hmdDesc.resolutionH;
		//System.out.println("aspectRatio " + aspectRatio);
		// Compute Vertical FOV based on distance.
		//float halfScreenDistance = (hmd.VScreenSize * 0.5f);
		//System.out.println("halfScreenDistance " + halfScreenDistance);
		//float yfov = (float) (2.0f * Math.atan(halfScreenDistance / hmd.EyeToScreenDistance));
		//System.out.println("yfov " + yfov);
		// Compute Horizontal FOV based on distance.
		//float halfHalfScreenDistanceH = (hmdInfo.HScreenSize * 0.5f * 0.5f); //for one screen not both
		//System.out.println("halfHalfScreenDistanceH " + halfHalfScreenDistanceH);
		//xfov = (float) (2.0f * Math.atan(halfHalfScreenDistanceH / hmdInfo.EyeToScreenDistance));
		//System.out.println("xfov " + xfov);
		xfov = hmdDesc.defaultEyeFovR + hmdDesc.defaultEyeFovL;

		// Post-projection viewport coordinates range from (-1.0, 1.0), with the
		// center of the left viewport falling at (1/4) of horizontal screen size.
		// We need to shift this projection center to match with the lens center.
		// We compute this shift in physical units (meters) to correct
		// for different screen sizes and then rescale to viewport coordinates.
		float viewCenter = hmdInfo.HScreenSize * 0.25f;
		//System.out.println("viewCenter " + viewCenter);
		float eyeProjectionShift = viewCenter - (hmdInfo.LensSeparationDistance * 0.5f);
		//System.out.println("eyeProjectionShift " + eyeProjectionShift);
		float projectionCenterOffset = (4.0f * eyeProjectionShift) / hmdInfo.HScreenSize;
		//System.out.println("projectionCenterOffset " + projectionCenterOffset);
		// Projection matrix for the "center eye", which the left/right matrices are based on.
		Transform3D projCenter = new Transform3D();
		projCenter.perspective(xfov, aspectRatio, 0.1f, 5000.0f);
		Transform3D projLeft = new Transform3D();
		projLeft.setTranslation(new Vector3f(projectionCenterOffset, 0, 0));
		projLeft.mul(projCenter);
		Transform3D projRight = new Transform3D();
		projRight.setTranslation(new Vector3f(-projectionCenterOffset, 0, 0));
		projRight.mul(projCenter);

		view.setCompatibilityModeEnable(true);
		view.setLeftProjection(left ? projLeft : projRight);
	}

	private void setProjectionMatrixB(View view, boolean left)
	{
		// Compute Aspect Ratio. Stereo mode cuts width in half.
		float aspectRatio = (hmdDesc.resolutionW * 0.5f) / hmdDesc.resolutionH;
		//System.out.println("aspectRatio " + aspectRatio);
		// Compute Vertical FOV based on distance.
		//float halfScreenDistance = (hmd.VScreenSize * 0.5f);
		//System.out.println("halfScreenDistance " + halfScreenDistance);
		//float yfov = (float) (2.0f * Math.atan(halfScreenDistance / hmd.EyeToScreenDistance));
		//System.out.println("yfov " + yfov);
		// Compute Horizontal FOV based on distance.
		float halfHalfScreenDistanceH = (hmdInfo.HScreenSize * 0.5f * 0.5f); //for one screen not both
		//System.out.println("halfHalfScreenDistanceH " + halfHalfScreenDistanceH);
		xfov = (float) (2.0f * Math.atan(halfHalfScreenDistanceH / hmdInfo.EyeToScreenDistance));
		//System.out.println("xfov " + xfov);
		// Post-projection viewport coordinates range from (-1.0, 1.0), with the
		// center of the left viewport falling at (1/4) of horizontal screen size.
		// We need to shift this projection center to match with the lens center.
		// We compute this shift in physical units (meters) to correct
		// for different screen sizes and then rescale to viewport coordinates.
		float viewCenter = hmdInfo.HScreenSize * 0.25f;
		//System.out.println("viewCenter " + viewCenter);
		float eyeProjectionShift = viewCenter - (hmdInfo.LensSeparationDistance * 0.5f);
		//System.out.println("eyeProjectionShift " + eyeProjectionShift);
		float projectionCenterOffset = (4.0f * eyeProjectionShift) / hmdInfo.HScreenSize;
		//System.out.println("projectionCenterOffset " + projectionCenterOffset);
		// Projection matrix for the "center eye", which the left/right matrices are based on.
		Transform3D projCenter = new Transform3D();
		projCenter.perspective(xfov, aspectRatio, 0.1f, 5000.0f);
		Transform3D projLeft = new Transform3D();
		projLeft.setTranslation(new Vector3f(projectionCenterOffset, 0, 0));
		projLeft.mul(projCenter);
		Transform3D projRight = new Transform3D();
		projRight.setTranslation(new Vector3f(-projectionCenterOffset, 0, 0));
		projRight.mul(projCenter);

		view.setCompatibilityModeEnable(true);
		view.setLeftProjection(left ? projLeft : projRight);
	}

}
