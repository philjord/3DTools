package tools3d.utils;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnTransformChange;
import javax.vecmath.Vector3d;

/**
 * @author pj
 * Created on 11/01/2004
 * 
 */
public class LockStepTranslationBehavior extends Behavior
{
	private WakeupOnTransformChange wakeUp;

	private TransformGroup source;

	private TransformGroup destination;

	public LockStepTranslationBehavior(TransformGroup source, TransformGroup destination)
	{
		this.source = source;
		this.destination = destination;
		if (!source.isLive())
		{
			source.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		}
		if (!destination.isLive())
		{
			destination.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			destination.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		}

		wakeUp = new WakeupOnTransformChange(source);
	}

	public void initialize()
	{
		wakeupOn(wakeUp);
	}

	//de burners
	private Transform3D destTran = new Transform3D();

	private Transform3D sourceTran = new Transform3D();

	private Vector3d trans = new Vector3d();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processStimulus(Enumeration critiria)
	{
		// get the transforms 			
		destination.getTransform(destTran);
		source.getTransform(sourceTran);

		// now set the translation component from source into dest			
		sourceTran.get(trans);
		destTran.set(trans);

		// and set the destination
		destination.setTransform(destTran);

		//reset the wakeup
		wakeupOn(wakeUp);
	}

}