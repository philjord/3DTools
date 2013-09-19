package tools3d.utils;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;

public class YawPitch
{
	private double yaw = 0;

	private double pitch = 0;

	// used for setting from transform3Ds and AAs
	private Quat4d q = new Quat4d();

	private Vector3d v = new Vector3d();

	//TODO: I should be using the setEuler method in transform3D for all this work

	/**
	 * Note Yaw is is radians and goes CCW not compass CW it also ranges from -PI to +PI
	 * 
	 * 
	 * NOTE We are using -z as "north" so 0 yaw points that direction
	 * 
	 * Pitch is up and down, so 0 is level PI is straight up -PI is straight down
	 */
	public YawPitch()
	{
	}

	public YawPitch(YawPitch yawPitch)
	{
		this.yaw = yawPitch.yaw;
		this.pitch = yawPitch.pitch;
	}

	public YawPitch(double yaw, double pitch)
	{
		this.yaw = yaw;
		this.pitch = pitch;
		wrapAndClamp();
	}

	public YawPitch(AxisAngle4d aa)
	{
		q.set(aa);
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public YawPitch(Quat4d q)
	{
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public YawPitch(Quat4f q)
	{
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public YawPitch(Transform3D t)
	{
		// NOTE we MUST normalize in case of tiny errors
		t.get(q);

		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	public YawPitch add(YawPitch yawPitch)
	{
		this.yaw += yawPitch.yaw;
		this.pitch += yawPitch.pitch;
		wrapAndClamp();
		return this;
	}

	public YawPitch sub(YawPitch yawPitch)
	{
		this.yaw -= yawPitch.yaw;
		this.pitch -= yawPitch.pitch;
		wrapAndClamp();
		return this;
	}

	public YawPitch set(YawPitch yawPitch)
	{
		this.yaw = yawPitch.yaw;
		this.pitch = yawPitch.pitch;
		wrapAndClamp();
		return this;
	}

	public void setPitch(double p)
	{
		pitch = p;
		wrapAndClamp();
	}

	public void setYaw(double y)
	{
		yaw = y;
		wrapAndClamp();
	}

	public void addPitch(double p)
	{
		pitch += p;
		wrapAndClamp();
	}

	public void addYaw(double y)
	{
		yaw += y;
		wrapAndClamp();
	}

	public void set(double y, double p)
	{
		this.yaw = y;
		this.pitch = p;
		wrapAndClamp();
	}

	public void add(double y, double p)
	{
		this.yaw += y;
		this.pitch += p;
		wrapAndClamp();
	}

	public double getPitch()
	{
		return pitch;
	}

	public double getYaw()
	{
		return yaw;
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
		// NOTE we MUST normalize in case of tiny errors
		t.normalizeCP();
		t.get(q);
		rotateEyePoint(q, v);
		this.yaw = getYaw(v);
		this.pitch = getPitch(v);
	}

	// deburners
	private Quat4d qxd = new Quat4d();

	private Quat4d qyd = new Quat4d();

	public Quat4d get(Quat4d qOut)
	{
		qyd.set(0, Math.sin(yaw / 2f), 0, Math.cos(yaw / 2f));
		qxd.set(Math.sin(pitch / 2f), 0, 0, Math.cos(pitch / 2f));
		qyd.mul(qxd);
		qOut.set(qyd);
		return qOut;
	}

	// deburners
	private Quat4f qxf = new Quat4f();

	private Quat4f qyf = new Quat4f();

	public Quat4f get(Quat4f qOut)
	{
		qyf.set(0, (float) Math.sin(yaw / 2f), 0, (float) Math.cos(yaw / 2f));
		qxf.set((float) Math.sin(pitch / 2f), 0, 0, (float) Math.cos(pitch / 2f));
		qyf.mul(qxf);
		qOut.set(qyf);
		return qOut;
	}

	public String toString()
	{
		return "YawPitch( " + yaw + ", " + pitch + " )";
	}

	public static YawPitch parse(String in)
	{
		double y = Double.parseDouble(in.substring(in.indexOf("(") + 1, in.indexOf(",")));
		double p = Double.parseDouble(in.substring(in.indexOf(",") + 1, in.indexOf(")")));
		return new YawPitch(y, p);
	}

	/**
	 * Internal to keep pitch from -PI/2 to +PI/2 by clamp and yaw in range of 0 to 2PI by wrap
	 */
	private void wrapAndClamp()
	{
		pitch = pitch > Math.PI / 2 ? Math.PI / 2 : pitch < -Math.PI / 2 ? -Math.PI / 2 : pitch;
		yaw = yaw > 2 * Math.PI ? yaw % (2 * Math.PI) : yaw < 0 ? (2 * Math.PI) - (-yaw % (2 * Math.PI)) : yaw;

	}

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

	// from
	// http://books.google.co.nz/books?id=_IJjKSRS558C&pg=PA218&lpg=PA218&dq=java3d+euler+pitch+quat4f&source=web&ots=BQTJF-lTT4&sig=34kQlJX8GN90rc9Xu5aMIGPK9Eg&hl=en
	public static Vector3d quatToEuler(Quat4d q1)
	{
		double sqw = q1.w * q1.w;
		double sqx = q1.x * q1.x;
		double sqy = q1.y * q1.y;
		double sqz = q1.z * q1.z;
		double yaw = Math.atan2(2.0 * (q1.x * q1.y - q1.z * q1.w), (sqx - sqy - sqz + sqw));
		double roll = Math.atan2(2.0 * (q1.y * q1.z + q1.x * q1.w), (-sqx - sqy + sqz + sqw));
		double pitch = Math.asin(-2.0 * (q1.x * q1.z - q1.y * q1.w));
		return new Vector3d(pitch, yaw, roll);
	}

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
	 * To sort out how to turn by say 0.01 radians use this to get from current lookat point to new look at point as an
	 * axis angle rotation. Then just make it a bit of an angle and make a quat of it!
	 * 
	 * This is easiest to calculate using Axis Angle because:
	 * 
	 * the angle is given by arcos of the dot product of the two (normalised) vectors: v1•v2 = |v1||v2| cos(angle) the
	 * axis is given by the cross product of the two vectors, the length of this axis is given by |v1 x v2| = |v1||v2|
	 * sin(angle).
	 * 
	 * as explained here this is taken from this discussion. So, if v1 and v2 are normalised so that |v1|=|v2|=1, then,
	 * angle = arcos(v1•v2) axis = norm(v1 x v2)
	 */

	public static double getYRotAngle(Quat4d q)
	{
		Vector3d newLookAt = rotateEyePoint(q);
		return getYaw(newLookAt);
	}

	public static double getXRotAngle(Quat4d q)
	{
		Vector3d newLookAt = rotateEyePoint(q);
		return getPitch(newLookAt);
	}

	
	///OLDER methods from BipedUtil
	/*
		Yaw is the wind direction the camera is looking at if you'd be standing
		on planet earth, and the ground has vectors X and Z, while Y points to
		the sky
			   N
			   ^ 
			   |z 
			   |   x        Looking in the direction Z = North (0°, 0 rad)
		W -----+-----> E    Looking in the direction X = East (+90°, +1.57 rad)
			   |            Looking in negative Z = South (180°, 3.1415 rad)
			   |            Looking in negative X = West (-90°, -1.57 rad)
			   |
			   S
			   
			   NOTE We are using -1Z as "north"
		*/

	//	Rotate vector v around arbitrary axis for angle radians
	//	It can only rotate around an axis through our object, to rotate around another axis:
	//	first translate the object to the axis, then use this function, then translate back in the new direction.
	public static Vector3d rotateAroundArbitrary(Vector3d v, Vector3d axis, double angle)
	{
		if ((v.x == 0) && (v.y == 0) && (v.z == 0))
			return new Vector3d();

		Vector3d w = new Vector3d();
		double c, s, t;

		axis.normalize();

		//calculate parameters of the rotation matrix
		c = Math.cos(angle);
		s = Math.sin(angle);
		t = 1 - c;

		//multiply v with rotation matrix
		w.x = (t * axis.x * axis.x + c) * v.x + (t * axis.x * axis.y + s * axis.z) * v.y + (t * axis.x * axis.z - s * axis.y) * v.z;

		w.y = (t * axis.x * axis.y - s * axis.z) * v.x + (t * axis.y * axis.y + c) * v.y + (t * axis.y * axis.z + s * axis.x) * v.z;

		w.z = (t * axis.x * axis.z + s * axis.y) * v.x + (t * axis.y * axis.z - s * axis.x) * v.y + (t * axis.z * axis.z + c) * v.z;

		w.normalize();
		w.scale(v.length());

		return w;
	}

	public static AxisAngle4d angleBetween(Vector3d v1, Vector3d v2)
	{
		//	axis - angle version sfrotation angleBetween(sfvec3f v1, sfvec3f v2)

		// turn vectors into unit vectors 
		Vector3d n1 = new Vector3d(v1);
		n1.normalize();
		Vector3d n2 = new Vector3d(v2);
		n2.normalize();

		double angle = Math.acos(n1.dot(n2));

		// if no noticable rotation is available return zero rotation
		// this way we avoid Cross product artifacts 
		if (Math.abs(angle) < 0.0001)
			return new AxisAngle4d(0, 0, 1, 0);

		Vector3d axis = n1;

		axis.cross(n1, n2);

		return new AxisAngle4d(axis.x, axis.y, axis.z, angle);

	}

	//Simply rotates point about a quat
	public static Vector3d rotate(Vector3d p1, Quat4d q)
	{
		Vector3d p2 = new Vector3d();

		p2.x = q.w * q.w * p1.x + 2 * q.y * q.w * p1.z - 2 * q.z * q.w * p1.y + q.x * q.x * p1.x + 2 * q.y * q.x * p1.y + 2 * q.z * q.x
				* p1.z - q.z * q.z * p1.x - q.y * q.y * p1.x;

		p2.y = 2 * q.x * q.y * p1.x + q.y * q.y * p1.y + 2 * q.z * q.y * p1.z + 2 * q.w * q.z * p1.x - q.z * q.z * p1.y + q.w * q.w * p1.y
				- 2 * q.x * q.w * p1.z - q.x * q.x * p1.y;

		p2.z = 2 * q.x * q.z * p1.x + 2 * q.y * q.z * p1.y + q.z * q.z * p1.z - 2 * q.w * q.y * p1.x - q.y * q.y * p1.z + 2 * q.w * q.x
				* p1.y - q.x * q.x * p1.z + q.w * q.w * p1.z;

		return p2;
	}

	/**
	 * rotate the 0,0,-1 point about the supplied quat, such that yaw and pitch might be extracted
	 * copy of the above with x=0,y=0,z=-1 encode into the amths
	 * @param q
	 * @return
	 */

	public static Vector3d rotateEyePoint(Quat4d q)
	{
		Vector3d p2 = new Vector3d();
		p2.x = (2 * q.y * q.w * -1) + (2 * q.z * q.x * -1);
		p2.y = (2 * q.z * q.y * -1) - (2 * q.x * q.w * -1);
		p2.z = (q.z * q.z * -1) - (q.y * q.y * -1) - (q.x * q.x * -1) + (q.w * q.w * -1);

		return p2;
	}

}
