package old.navigation.rules.deprecated.debug;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleStripArray;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import tools3d.navigation.AvatarCollisionInfo;
import tools3d.universe.VisualPhysicalUniverse;
import tools3d.utils.PhysAppearance;
import tools3d.utils.scenegraph.EasyTransformGroup;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import old.navigation.rules.deprecated.NavigationPositionRule;
import old.navigation.rules.deprecated.picking.CorrectCylinderPicker;
import old.navigation.rules.deprecated.picking.IntersectionUtils;
import old.utils.leafnode.VectorShape3D;

public class NavigationPositionRuleDisplayer implements NavigationPositionRule
{

	private static float triMeshPickerBoundsRadius = 3.0f;

	private static float gravityPickerRadiusRatio = 0.75f;

	private AvatarCollisionInfo avatarCollisionInfo;

	private Vector3f testLocation = new Vector3f();

	private JFrame f = new JFrame();

	private JTextArea l = new JTextArea();

	private BranchGroup currentPickerBranchGroup;

	private BranchGroup currentPickedBranchGroup;

	private boolean active = true;

	private PickTool collisionPickTool;

	private PhysAppearance physAppearancePickerBound = new PhysAppearance();

	private PhysAppearance physAppearancePickerGrav = new PhysAppearance();

	private PhysAppearance physAppearancePickerColl = new PhysAppearance();

	private PhysAppearance physAppearancePicked = new PhysAppearance();

	private String output = "";

	private String lastCollisonOutput = "";

	private String lastCollisonOutput2 = "";

	public NavigationPositionRuleDisplayer(AvatarCollisionInfo avatarCollisionInfo)
	{
		setAvatarCollisionInfo(avatarCollisionInfo);

		f.setSize(400, 400);
		f.setLocation(-400, 0);
		f.getContentPane().setLayout(new GridLayout(1, 1));
		f.getContentPane().add(new JScrollPane(l));
		//f.setVisible(true);

		ColoringAttributes colorAtt = new ColoringAttributes(1.0f, 0.1f, 1.0f, ColoringAttributes.FASTEST);
		physAppearancePickerBound.setColoringAttributes(colorAtt);
		colorAtt = new ColoringAttributes(1.0f, 0.2f, 1.0f, ColoringAttributes.FASTEST);
		physAppearancePickerGrav.setColoringAttributes(colorAtt);
		colorAtt = new ColoringAttributes(1.0f, 0.3f, 1.0f, ColoringAttributes.FASTEST);
		physAppearancePickerColl.setColoringAttributes(colorAtt);

		colorAtt = new ColoringAttributes(0.0f, 1.0f, 0.0f, ColoringAttributes.FASTEST);
		physAppearancePicked.setColoringAttributes(colorAtt);
	}

	public NavigationPositionRuleDisplayer(AvatarCollisionInfo avatarCollisionInfo, BranchGroup physicalBranch)
	{
		this(avatarCollisionInfo);
		setPhysicalBranch(physicalBranch);

	};

	public void setPhysicalBranch(BranchGroup physicalBranch)
	{
		collisionPickTool = new PickTool(physicalBranch);
		collisionPickTool.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
	}

	public void setAvatarCollisionInfo(AvatarCollisionInfo avatarCollisionInfo)
	{
		this.avatarCollisionInfo = avatarCollisionInfo;
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public void setActive(boolean active)
	{
		this.active = active;
		if (!active)
		{
			// time to clean up
			if (currentPickerBranchGroup != null)
			{
				currentPickerBranchGroup.detach();
			}
			if (currentPickedBranchGroup != null)
			{
				currentPickedBranchGroup.detach();
			}
		}
	}

	@Override
	public Vector3f applyRule(Vector3f avatarDesiredTranslation, Vector3f avatarLocation)
	{
		// celar the output so any thing that wants to can write tot eh frame
		output = "";
		output += " \n avartarYHeight " + avatarCollisionInfo.getAvatarYHeight() + " avartarYStepOverHeight "
				+ avatarCollisionInfo.getAvatarYStepOverHeight() + " getAvatarXZRadius " + avatarCollisionInfo.getAvatarXZRadius();
		output += "\n avatarLocation " + avatarLocation;
		output += "\n avatarDesiredTranslation " + avatarDesiredTranslation;
		testLocation.add(avatarLocation, avatarDesiredTranslation);

		output += "\n testLocation " + testLocation;

		Point3d boundsCenter = new Point3d(testLocation);
		boundsCenter.y += avatarCollisionInfo.getAvatarYHeight() / 2f;

		output += "\n boundsCenter " + boundsCenter;
		output += "\n triMeshPickerBoundsRadius " + triMeshPickerBoundsRadius;

		collisionPickTool.setShapeBounds(new BoundingSphere(boundsCenter, triMeshPickerBoundsRadius), boundsCenter);

		if (currentPickerBranchGroup != null)
		{
			currentPickerBranchGroup.detach();
		}
		currentPickerBranchGroup = new BranchGroup();

		addGeneralTriangleBoundsSphere();
		addGravityDownPickerShape();
		addCollisionPickerShape(avatarDesiredTranslation, avatarLocation);

		checkCollisionRule(avatarDesiredTranslation, avatarLocation);

		//now we've added children add to the live graph
		currentPickerBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		VisualPhysicalUniverse.staticTestLocale.addBranchGraph(currentPickerBranchGroup);

		if (currentPickedBranchGroup != null)
		{
			currentPickedBranchGroup.detach();
		}

		PickResult[] pickResults = collisionPickTool.pickAll();
		if (pickResults != null)
		{
			currentPickedBranchGroup = new BranchGroup();

			int totalVertexes = 0;
			output += "\n triMeshPickerBounds pickResults.length " + pickResults.length;
			for (PickResult pickResult : pickResults)
			{
				Shape3D shape = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
				//	output += " \n---------------------------------------------";
				//	output += " \n Shape3D  " + shape;
				//	output += " \n shape.numGeometries " + shape.numGeometries() + " pickResult.numGeometryArrays " + pickResult.numGeometryArrays();

				addHighlightOfPickedShape(shape);

				for (GeometryArray geometryArray : pickResult.getGeometryArrays())
				{
					//	output += " \n " + "geometryArray " + geometryArray;
					//	output += " \n " + "ValidVertexCount " + geometryArray.getValidVertexCount();
					totalVertexes += geometryArray.getValidVertexCount();
					if (geometryArray instanceof TriangleStripArray)
					{
						//	TriangleStripArray triangleStripArray = (TriangleStripArray) geometryArray;
						//	output += " \n " + "triangleStripArray   NumStrips  " + triangleStripArray.getNumStrips();

					}
					if (geometryArray instanceof TriangleArray)
					{
						//	TriangleArray triangleArray = (TriangleArray) geometryArray;
						//	output += " \n " + "triangleStripArray";
					}

				}

			}
			//now we've added children add to the live graph
			currentPickedBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
			VisualPhysicalUniverse.staticTestLocale.addBranchGraph(currentPickedBranchGroup);

			//update the output display
			output += " \n totalVertexes " + totalVertexes;

			output += lastCollisonOutput;
			output += lastCollisonOutput2;
			l.setText(output);
			if (!f.isVisible())
				f.setVisible(true);
		}

		//return an unchanged desired translation (as we aren't a real rule at all)
		return new Vector3f(avatarDesiredTranslation);
	}

	private void addHighlightOfPickedShape(Shape3D sourceShape)
	{
		Transform3D t = new Transform3D();
		sourceShape.getLocalToVworld(t);
		Shape3D shape = new Shape3D();
		shape.setAppearance(physAppearancePicked);
		for (int i = 0; i < sourceShape.numGeometries(); i++)
		{
			shape.setGeometry(sourceShape.getGeometry(i), i);
		}
		EasyTransformGroup etg = new EasyTransformGroup(t);
		etg.addChild(shape);
		BranchGroup bg = new BranchGroup();
		bg.addChild(etg);
		currentPickedBranchGroup.addChild(bg);
	}

	private void addGeneralTriangleBoundsSphere()
	{
		Sphere pickerSphere = new Sphere(triMeshPickerBoundsRadius, physAppearancePickerBound);
		Point3d boundsCenter = new Point3d(testLocation);
		boundsCenter.y += avatarCollisionInfo.getAvatarYHeight() / 2f;
		EasyTransformGroup pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), new Vector3f(boundsCenter), 1f);
		pickerEtg.addChild(pickerSphere);
		BranchGroup bg = new BranchGroup();
		bg.addChild(pickerEtg);
		currentPickerBranchGroup.addChild(bg);
	}

	private void addGravityDownPickerShape()
	{
		//lets add a gravity down ward test shape display for fun
		// we really want a ray but let's add a cylinder
		float gravityPickerDepth = 4.0f;
		Cylinder c = new Cylinder(avatarCollisionInfo.getAvatarXZRadius() * gravityPickerRadiusRatio, gravityPickerDepth, physAppearancePickerGrav);
		Vector3f etgV = new Vector3f(testLocation);
		// make it from the lowest poitn downward
		etgV.y -= gravityPickerDepth / 2f;
		// lift it to show the overstep height
		etgV.y += avatarCollisionInfo.getAvatarYStepOverHeight();

		EasyTransformGroup pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), etgV, 1f);
		pickerEtg.addChild(c);
		BranchGroup bg = new BranchGroup();
		bg.addChild(pickerEtg);
		currentPickerBranchGroup.addChild(bg);
	}

	private Vector3f trans = new Vector3f();

	private void addCollisionPickerShape(Vector3f desiredTranslation, Vector3f currentLocation)
	{

		if (desiredTranslation.length() > 0)
		{
			lastCollisonOutput = "\n last collison info";
			// start with a simple arrow from the pelvis forward, to deal with any "holes" the
			// cylinders might go through the middle of 

			float length = desiredTranslation.length();
			int segments = (int) Math.ceil(length / avatarCollisionInfo.getAvatarXZRadius());
			float segmentLength = length / segments;

			trans.set(desiredTranslation);

			for (int i = 1; i <= segments; i++)
			{
				trans.normalize();
				trans.scale(i * segmentLength);

				testLocation.add(currentLocation, trans);

				//show the collision bounds from top of head down to overstep height
				Cylinder cyl = new Cylinder(avatarCollisionInfo.getAvatarXZRadius(), avatarCollisionInfo.getAvatarYHeight()
						- avatarCollisionInfo.getAvatarYStepOverHeight(), physAppearancePickerColl);
				Vector3f etgV = new Vector3f(testLocation);
				etgV.y += (avatarCollisionInfo.getAvatarYHeight() / 2f) + (avatarCollisionInfo.getAvatarYStepOverHeight() / 2f);

				lastCollisonOutput += " \n testLocation " + testLocation;
				lastCollisonOutput += " \n trans " + trans;
				lastCollisonOutput += " \n etgV " + etgV;

				EasyTransformGroup pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), etgV, 1f);
				pickerEtg.addChild(cyl);
				BranchGroup bg = new BranchGroup();
				bg.addChild(pickerEtg);
				currentPickerBranchGroup.addChild(bg);

				/*
				// cube for top of head
				ColorCube cc = new ColorCube(0.05f);
				etgV = new Vector3f(currentLocation);
				etgV.y += avatarCollisionInfo.getAvatarYHeight();
				pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), etgV, 1f);
				pickerEtg.addChild(cc);
				bg = new BranchGroup();
				bg.addChild(pickerEtg);
				currentPickerBranchGroup.addChild(bg);

				// cube for camera
				cc = new ColorCube(0.05f);
				etgV = new Vector3f(currentLocation);
				etgV.y += avatarCollisionInfo.getAvatarHeadCameraYHeight();
				pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), etgV, 1f);
				pickerEtg.addChild(cc);
				bg = new BranchGroup();
				bg.addChild(pickerEtg);
				currentPickerBranchGroup.addChild(bg);

				// cube for over step
				cc = new ColorCube(0.05f);
				etgV = new Vector3f(currentLocation);
				etgV.y += avatarCollisionInfo.getAvatarYStepOverHeight();
				pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), etgV, 1f);
				pickerEtg.addChild(cc);
				bg = new BranchGroup();
				bg.addChild(pickerEtg);
				currentPickerBranchGroup.addChild(bg);*/

			}
		}

	}

	public void checkCollisionRule(Vector3f desiredTranslation, Vector3f currentLocation)
	{
		Vector3f bottom = new Vector3f();
		Vector3f top = new Vector3f();
		Vector3f hips = new Vector3f();

		float length = desiredTranslation.length();
		// 3 times as many segmetns as our radius
		int segments = (int) Math.ceil((length / avatarCollisionInfo.getAvatarXZRadius()) * 3);
		float segmentLength = length / segments;

		trans.set(desiredTranslation);

		boolean done = false;
		ArrayList<Vector3d> netNormalContributors = new ArrayList<Vector3d>();
		for (int i = 1; i <= segments && !done; i++)
		{
			trans.normalize();
			trans.scale(i * segmentLength);

			//	System.out.println("i * segmentLength " + (i * segmentLength));
			//	System.out.println("iter " + i + " " + trans);

			testLocation.add(currentLocation, trans);
			bottom.set(testLocation);
			bottom.y += avatarCollisionInfo.getAvatarYStepOverHeight();
			top.set(testLocation);
			top.y += avatarCollisionInfo.getAvatarYHeight();
			hips.set(testLocation);
			hips.y += avatarCollisionInfo.getAvatarYStepOverHeight() / 2;

			PickResult[] pickResults = CorrectCylinderPicker.pickAll(collisionPickTool, new Point3d(bottom), new Point3d(top), avatarCollisionInfo
					.getAvatarXZRadius());

			if (pickResults != null)
			{

				for (PickResult pickResult : pickResults)
				{
					if (pickResult.numIntersections() > 0)
					{
						for (int j = 0; j < pickResult.numIntersections(); j++)
						{
							PickIntersection pickIntersection = pickResult.getIntersection(j);

							// must use capsule/flat filter
							if (CorrectCylinderPicker.isWithinDoubleHalfSpace(new Point3d(bottom), new Point3d(top), pickIntersection
									.getPointCoordinatesVW()))
							{
								lastCollisonOutput2 = "\n lastCollisonOutput2";
								lastCollisonOutput2 += "\n pickResult " + pickResult.getNode(PickResult.SHAPE3D);
								lastCollisonOutput2 += "\n numIntersections " + pickResult.numIntersections();
								lastCollisonOutput2 += "\n getDistance " + pickIntersection.getDistance();
								lastCollisonOutput2 += "\n getPrimitiveCoordinatesVW " + pickIntersection.getPrimitiveCoordinatesVW()[0];
								lastCollisonOutput2 += "\n getPrimitiveCoordinatesVW " + pickIntersection.getPrimitiveCoordinatesVW()[1];
								lastCollisonOutput2 += "\n getPrimitiveCoordinatesVW " + pickIntersection.getPrimitiveCoordinatesVW()[2];

								// cube for testy
								ColorCube cc = new ColorCube(0.02f);
								Vector3f closestVertexCoordVW0 = new Vector3f(pickIntersection.getPrimitiveCoordinatesVW()[0]);
								EasyTransformGroup pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), closestVertexCoordVW0, 1f);
								pickerEtg.addChild(cc);
								BranchGroup bg = new BranchGroup();
								bg.addChild(pickerEtg);
								currentPickerBranchGroup.addChild(bg);

								cc = new ColorCube(0.02f);
								Vector3f closestVertexCoordVW1 = new Vector3f(pickIntersection.getPrimitiveCoordinatesVW()[1]);
								pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), closestVertexCoordVW1, 1f);
								pickerEtg.addChild(cc);
								bg = new BranchGroup();
								bg.addChild(pickerEtg);
								currentPickerBranchGroup.addChild(bg);

								cc = new ColorCube(0.02f);
								Vector3f closestVertexCoordVW2 = new Vector3f(pickIntersection.getPrimitiveCoordinatesVW()[2]);
								pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), closestVertexCoordVW2, 1f);
								pickerEtg.addChild(cc);
								bg = new BranchGroup();
								bg.addChild(pickerEtg);
								currentPickerBranchGroup.addChild(bg);

								Sphere s = new Sphere(0.04f);
								Vector3f intersectCoordVW = new Vector3f(pickIntersection.getPointCoordinatesVW());
								pickerEtg = new EasyTransformGroup(new Quat4f(0, 0, 0, 1), intersectCoordVW, 1f);
								pickerEtg.addChild(s);
								bg = new BranchGroup();
								bg.addChild(pickerEtg);
								currentPickerBranchGroup.addChild(bg);

								// now lets make a normal up
								Vector3f line1 = new Vector3f(closestVertexCoordVW1);
								line1.sub(closestVertexCoordVW0);
								Vector3f line2 = new Vector3f(closestVertexCoordVW2);
								line2.sub(closestVertexCoordVW0);
								Vector3f cross = new Vector3f();
								cross.cross(line1, line2);

								Vector3d triNormal = IntersectionUtils.getNormal(pickIntersection.getPrimitiveCoordinatesVW(), new Point3f(hips));

								// add a visual of the normal
								Point3d triangleCentroid = IntersectionUtils.getTriangleCentroid(pickIntersection.getPrimitiveCoordinatesVW());
								VectorShape3D vs = new VectorShape3D(triangleCentroid, triNormal);
								bg = new BranchGroup();
								bg.addChild(vs);
								currentPickerBranchGroup.addChild(bg);

								// now to show the net effect of all the normals
								netNormalContributors.add(triNormal);
							}
						}

					}
					else
					{
						System.out.println("eh pickResult[0].numIntersections() < 1?");
					}

				}

				// must break and the trans will have been set by the previous
				// loop or no move if this is the first i
				done = true;

			}

		}

		if (length > 0)
		{
			Vector3d netNormal = new Vector3d();
			for (Vector3d netNormalContributor : netNormalContributors)
			{
				netNormal.x += netNormalContributor.x / netNormalContributors.size();
				//netNormal.y += netNormalContributor.y / netNormalContributors.size();// we don't want y in the slidy
				netNormal.z += netNormalContributor.z / netNormalContributors.size();
			}

			netNormal.normalize();

			// add a visual of the normal
			VectorShape3D vsn = new VectorShape3D(new Point3d(currentLocation), netNormal);
			BranchGroup bg = new BranchGroup();
			bg.addChild(vsn);
			currentPickerBranchGroup.addChild(bg);

			// add a visual of the desired translate
			Vector3d desiredTransNormal = new Vector3d(desiredTranslation);
			desiredTransNormal.normalize();
			VectorShape3D vsdt = new VectorShape3D(new Point3d(currentLocation), desiredTransNormal);
			bg = new BranchGroup();
			bg.addChild(vsdt);
			currentPickerBranchGroup.addChild(bg);

			lastCollisonOutput2 += "\n desiredTranslation " + desiredTranslation;
			lastCollisonOutput2 += "\n netNormal " + netNormal;
		}
	}

}
