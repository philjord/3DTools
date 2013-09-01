package tools3d.utils;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * canvas 3d doesn't enjoy being in a Jdesktop internal frmae 
 * 
 * @author pj
 */
public class Canvas3DInternalFrame extends JInternalFrame implements InternalFrameListener
{
	public static int openFrameCount = 0;

	public static final int xOffset = 30;

	public static final int yOffset = 30;

	public Canvas3DInternalFrame(String title)
	{
		// name, resizable, closable, maximizable, iconifiable
		super(title, true, false, true, true);

		// set the window size probably to be overridden by the subclass
		setSize(300, 300);

		setDefaultLocation();

		addInternalFrameListener(this);
	}

	public void setDefaultLocation()
	{
		setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
	}

	public void internalFrameActivated(InternalFrameEvent e)
	{
	}

	public void internalFrameClosed(InternalFrameEvent e)
	{
		openFrameCount--;
	}

	public void internalFrameClosing(InternalFrameEvent e)
	{
	}

	public void internalFrameDeactivated(InternalFrameEvent e)
	{
	}

	public void internalFrameDeiconified(InternalFrameEvent e)
	{
	}

	public void internalFrameIconified(InternalFrameEvent e)
	{
	}

	public void internalFrameOpened(InternalFrameEvent e)
	{
		openFrameCount++;
	}

	/**
	 * Internal frame call addNotify and removeNotify for trivial event but the canvas3d renderer thinks it is being
	 * shown on screen and totally disposed, which is not good for resources (etc) mainly causes teh HUD to disappear
	 * 
	 */
	@Override
	public void setSelected(boolean selected) throws PropertyVetoException
	{

	}

	@Override
	public void moveToFront()
	{

	}

	@Override
	public void moveToBack()
	{
	}

	@Override
	public void setLayer(Integer layer)
	{
	}
}
