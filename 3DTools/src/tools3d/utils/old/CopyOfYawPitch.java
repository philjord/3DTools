package tools3d.utils.old;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class CopyOfYawPitch
{
	// TODO: this calls does CCW not the CW around the Y as descibed below
	private double yaw = 0;

	private double pitch = 0;

	// used for setting from transform3Ds and AAs
	private Quat4d q = new Quat4d();

	private Vector3d v = new Vector3d();

	public CopyOfYawPitch()
	{
	}

	public CopyOfYawPitch(CopyOfYawPitch yawPitch)
	{
		this.yaw = yawPitch.yaw;
		this.pitch = yawPitch.pitch;
	}

	public CopyOfYawPitch(double yaw, double pitch)
	{
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public CopyOfYawPitch(AxisAngle4d aa)
	{
		q.set(aa);
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public CopyOfYawPitch(Quat4d q)
	{
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public CopyOfYawPitch(Quat4f q)
	{
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public CopyOfYawPitch(Transform3D t)
	{
		t.get(q);
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public void set(double yaw, double pitch)
	{
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public void set(AxisAngle4d aa)
	{
		q.set(aa);
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public void set(Quat4d q)
	{
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public void set(Quat4f q)
	{
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public void set(Transform3D t)
	{
		t.get(q);
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public double getPitch()
	{
		return pitch;
	}

	/**
	 * Note Yaw is is radians and goes CCW not compass CW it also ranges from -Pi to + Pi
	 * 
	 * @return yaw
	 */
	public double getYaw()
	{
		return yaw;
	}

	public void get(AxisAngle4d aa)
	{
		get(q);
		aa.set(q);

		/*
		 * Vector3d v1 = new Vector3d(0, 0, -1); double newY = Math.sin(pitch);
		 * 
		 * double newX = -Math.sin(yaw); double newZ = -Math.cos(yaw);
		 * 
		 * Vector3d v2 = new Vector3d(newX, newY, newZ);
		 * 
		 * double angle = Math.acos(v1.dot(v2));
		 * 
		 * Vector3d axis = new Vector3d(); axis.cross(v1, v2); axis.normalize();
		 * 
		 * aa.set(axis, angle);
		 * 
		 * To sort out how to turn by say 0.01 radians use this to get from current lookat point to new look at point as
		 * an axis angle rotation. Then just make it a bit of an angle and make a quat of it!
		 * 
		 * This is easiest to calculate using Axis Angle because:
		 * 
		 * the angle is given by arcos of the dot product of the two (normalised) vectors: v1•v2 = |v1||v2| cos(angle)
		 * the axis is given by the cross product of the two vectors, the length of this axis is given by |v1 x v2| =
		 * |v1||v2| sin(angle).
		 * 
		 * as explained here this is taken from this discussion. So, if v1 and v2 are normalised so that |v1|=|v2|=1,
		 * then, angle = arcos(v1•v2) axis = norm(v1 x v2)
		 */

	}

	public void get(Quat4d qOut)
	{
		// two AA method
		AxisAngle4d yawAA = new AxisAngle4d(new Vector3d(0, 1, 0), yaw);
		Transform3D t1 = new Transform3D();
		t1.set(yawAA);
		AxisAngle4d pitchAA = new AxisAngle4d(new Vector3d(1, 0, 0), pitch);
		Transform3D t2 = new Transform3D();
		t2.set(pitchAA);

		t1.mul(t2);
		t1.get(qOut);
	}

	public void get(Quat4f qOut)
	{
		yaw = yaw == Math.PI ? yaw - 0.001f : yaw;
		yaw = yaw == -Math.PI ? yaw + 0.001f : yaw;

		// two AA method
		AxisAngle4f yawAA = new AxisAngle4f(new Vector3f(0, 1, 0), (float) yaw);
		Transform3D t1 = new Transform3D();
		t1.set(yawAA);
		AxisAngle4f pitchAA = new AxisAngle4f(new Vector3f(1, 0, 0), (float) pitch);
		Transform3D t2 = new Transform3D();
		t2.set(pitchAA);

		t1.mul(t2);
		t1.get(qOut);
	}

	// deburners
	// private Quat4f qx = new Quat4f();
	//
	// private Quat4f qy = new Quat4f();
	//
	// public void get(Quat4f qOut)
	// {
	// qx.set((float) Math.cos(pitch / 2f), (float) Math.sin(pitch / 2f), 0, 0);
	// qy.set((float) Math.cos(yaw / 2f), 0, (float) Math.sin(yaw / 2f), 0);
	// Quat4f qt = new Quat4f();
	// qt.set(qy);
	// qt.mul(qx);
	// qOut.set(qt);
	// }

	public void setPitch(double d)
	{
		pitch = d;
	}

	public void setYaw(double d)
	{
		yaw = d;
	}

	public String toString()
	{
		return "YawPitch( " + yaw + ", " + pitch + " )";
	}

	/*
	 * Yaw is the wind direction the camera is looking at if you'd be standing on planet earth, and the ground has
	 * vectors X and Z, while Y points to the sky N ^ |z | x Looking in the direction Z = North (0°, 0 rad) W
	 * -----+-----> E Looking in the direction X = East (+90°, +1.57 rad) | Looking in negative Z = South (180°, 3.1415
	 * rad) | Looking in negative X = West (-90°, -1.57 rad) | S
	 * 
	 * NOTE We are using -z as "north"
	 */

	private static double getYaw(Vector3d dir)
	{
		// the atan2 function returns the angle of a 2D point (like from polar coordinates),
		// so here it gives angle of dir projected on XZ plane, which is what we want for the yaw
		return (Math.atan2(-dir.x, -dir.z));
	}

	private static double getPitch(Vector3d dir)
	{
		// Project dir on the XZ plane
		// Then find angle between dir and projected dir
		// With atan2: angle of the point (lengthof2Dvector(dir.x, dir.z), dir.y)
		return Math.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z));
	}

	/**
	 * rotate the 0,0,-1 point about the supplied quat, such that yaw and pitch might be extracted copy of the above
	 * with x=0,y=0,z=-1 encode into the maths
	 * 
	 * @param q
	 * @return
	 */
	private static void rotateEyePoint(Quat4d q, Vector3d returnVec)
	{

		returnVec.x = (2 * q.y * q.w * -1) + (2 * q.z * q.x * -1);
		returnVec.y = (2 * q.z * q.y * -1) - (2 * q.x * q.w * -1);
		returnVec.z = (q.z * q.z * -1) - (q.y * q.y * -1) - (q.x * q.x * -1) + (q.w * q.w * -1);
	}

	private static void rotateEyePoint(Quat4f q, Vector3d returnVec)
	{

		returnVec.x = (2 * q.y * q.w * -1) + (2 * q.z * q.x * -1);
		returnVec.y = (2 * q.z * q.y * -1) - (2 * q.x * q.w * -1);
		returnVec.z = (q.z * q.z * -1) - (q.y * q.y * -1) - (q.x * q.x * -1) + (q.w * q.w * -1);
	}
	/*
	 * heres some code for direct from ypr to quats In Java looks like this:
	 * 
	 * public static void setQ( Quat4f q, float pitch, float yaw, float roll ) { Quat4f qx = new Quat4f((float)
	 * Math.cos(pitch/2f), (float) Math.sin(pitch/2f), 0, 0); Quat4f qy = new Quat4f((float) Math.cos(yaw/2f), 0,
	 * (float) Math.sin(yaw/2f),0); Quat4f qz = new Quat4f((float) Math.cos(roll/2f), 0, 0, (float) Math.sin(roll/2f) );
	 * Quat4f qt = new Quat4f(); qt.set( qx ); qt.mul( qy ); qt.mul( qz ); q.set( qt ); }
	 * 
	 */
}
