/*
 * Created on Jan 3, 2004
 */
package tools3d.camera.old;

import java.awt.image.BufferedImage;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingLeaf;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Point3d;

import tools3d.universe.VisualPhysicalUniverse;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * @author Administrator
 *
 */
public class OffScreenCam
{
	// front and back clips, should be ratio of no more than 3000
	public static float FRONT_CLIP = 0.2f;

	public static float BACK_CLIP = 10000f;

	private Viewer viewer;

	private ViewingPlatform viewingPlatform;

	private TransformGroup transformGroup;

	private Canvas3D canvas3D;

	private BufferedImage bImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

	private ImageComponent2D imageCom = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);

	public OffScreenCam(VisualPhysicalUniverse universe)
	{
		canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration(), true);
		canvas3D.setOffScreenBuffer(imageCom);

		// NOTE: the size, physical width, and physical height of the associated Screen3D must be set explicitly prior to rendering.
		// Failure to do so will result in an exception.
		canvas3D.getScreen3D().setSize(256, 256);
		canvas3D.getScreen3D().setPhysicalScreenWidth(0.5);
		canvas3D.getScreen3D().setPhysicalScreenHeight(0.5);

		// create a viewer with the given canvas, physical environemnt and physical body are defaulted
		viewer = new Viewer(canvas3D);

		viewingPlatform = new ViewingPlatform();
		viewer.setViewingPlatform(viewingPlatform);

		transformGroup = viewingPlatform.getMultiTransformGroup().getTransformGroup(0);

		//set up the view
		viewer.getView().setFrontClipPolicy(View.VIRTUAL_EYE);

		//actually add our viewer tot he universe
		universe.addViewingPlatform(viewingPlatform);

		System.out.println("field  = " + viewer.getView().getFieldOfView());
		viewer.getView().setFieldOfView(90);

		Background back = new Background();
		BoundingSphere bs = new BoundingSphere(new Point3d(), 1);
		BoundingLeaf bl = new BoundingLeaf(bs);

		BranchGroup bg = new BranchGroup();
		back.setApplicationBounds(bs);
		bg.addChild(back);
		bg.addChild(bl);
		viewingPlatform.addChild(bg);

	}

	public ImageComponent2D renderImage(Transform3D camTransform, float frontClip, float backClip)
	{
		transformGroup.setTransform(camTransform);
		viewer.getView().setBackClipDistance(backClip);
		viewer.getView().setFrontClipDistance(frontClip);

		canvas3D.renderOffScreenBuffer();
		canvas3D.waitForOffScreenRendering();
		return imageCom;
	}
}
