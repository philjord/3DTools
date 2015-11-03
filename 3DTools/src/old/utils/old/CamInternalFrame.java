package old.utils.old;

import java.awt.GridLayout;

import javax.swing.event.InternalFrameEvent;

import tools3d.camera.CameraPanel;

public class CamInternalFrame extends Canvas3DInternalFrame
{
	private CameraPanel camPanel;

	public CamInternalFrame(String title, CameraPanel camPanel)
	{
		super(title);
		this.camPanel = camPanel;
		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(camPanel);
	}

	public void internalFrameDeiconified(InternalFrameEvent e)
	{
		if (!camPanel.isRendering())
		{
			camPanel.startRendering();
		}
	}

	public void internalFrameIconified(InternalFrameEvent e)
	{
		if (camPanel.isRendering())
		{
			camPanel.stopRendering();
		}
	}
}
