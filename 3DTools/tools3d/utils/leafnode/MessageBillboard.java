package tools3d.utils.leafnode;

import java.awt.Font;

import javax.media.j3d.Billboard;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import tools3d.utils.Utils3D;
import tools3d.utils.scenegraph.SimpleTransformGroup;

public class MessageBillboard extends BranchGroup
{
	private Color3f mainTextColor = new Color3f(0, 0f, 0f);

	private Color3f subTextColor = new Color3f(0.4f, 0.6f, 0.4f);

	private Color3f superTextColor = new Color3f(0.6f, 0.3f, 0.4f);

	//private MyText2Dnewer mainText;
	private MyText2D mainText;

	private MyText2D subText;

	private MyText2D superText;

	private SimpleTransformGroup mainTrans = new SimpleTransformGroup(new Vector3f(0, 0, 0));

	private SimpleTransformGroup subTrans = new SimpleTransformGroup(new Vector3f(0, -1, 0));

	private SimpleTransformGroup superTrans = new SimpleTransformGroup(new Vector3f(0, 1, 0));

	private TransformGroup billTrans = new TransformGroup();

	public MessageBillboard()
	{
		this(1f / 16f);
	}

	public MessageBillboard(float scaleFactor)
	{
		mainText = new MyText2D("", mainTextColor, "LucidaSansRegular", 32, Font.PLAIN); //, scaleFactor);
		mainText.setRectangleScaleFactor(scaleFactor);
		//mainText = new MyText2Dnewer("", mainTextColor, "LucidaSansRegular", 32, Font.PLAIN, scaleFactor);
		subText = new MyText2D("", subTextColor, "LucidaSansRegular", 8, Font.PLAIN); //, scaleFactor);
		superText = new MyText2D("", superTextColor, "LucidaSansRegular", 8, Font.PLAIN); //, scaleFactor);

		// TODO: also allow ROTATE_ABOUT_POINT type billboard, which doesn't work for multiple cameras properly
		billTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		billTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		addChild(billTrans);

		Billboard billBehave = new Billboard(billTrans);
		billBehave.setSchedulingBounds(Utils3D.defaultBounds);
		billBehave.setEnable(true);
		addChild(billBehave);

		mainTrans.addChild(mainText);
		billTrans.addChild(mainTrans);

		subTrans.addChild(subText);
		billTrans.addChild(subTrans);

		superTrans.addChild(superText);
		billTrans.addChild(superTrans);
	}

	public void setMainText(String newText)
	{
		mainText.setString(newText);
		mainTrans.setTranslation(new Vector3f(-mainText.getRectVisibleWidth() / 2, 0, 0));
	}

	public void setSubText(String newText)
	{
		subText.setString(newText);
		subTrans.setTranslation(new Vector3f(subText.getRectVisibleWidth() / 2, -1, 0));
	}

	public void setSuperText(String newText)
	{
		superText.setString(newText);
		superTrans.setTranslation(new Vector3f(superText.getRectVisibleWidth() / 2, 1, 0));
	}

}
