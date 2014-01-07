package tools3d.camera;

import javax.media.j3d.InputDevice;
import javax.media.j3d.Sensor;

public class OcculusRift implements InputDevice
{
	Sensor sensor = new Sensor(this);

	@Override
	public boolean initialize()
	{
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void setNominalPositionAndOrientation()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void pollAndProcessInput()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void processStreamInput()
	{
		// empty

	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getProcessingMode()
	{
		return NON_BLOCKING;
	}

	@Override
	public void setProcessingMode(int mode)
	{
		// ignored

	}

	@Override
	public int getSensorCount()
	{
		return 1;
	}

	@Override
	public Sensor getSensor(int sensorIndex)
	{
		return sensor;
	}

}
