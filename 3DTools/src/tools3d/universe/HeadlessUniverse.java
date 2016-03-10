package tools3d.universe;

import javax.media.j3d.Canvas3D;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class HeadlessUniverse extends SimpleUniverse
{
	public static Viewer createView()
	{
		GLCapabilities cap = new GLCapabilities(GLProfile.get(GLProfile.GL2GL3));
		GLWindow glwindow = GLWindow.create(cap);
		glwindow.setSize(1, 1);
		glwindow.setVisible(true);
		Viewer viewer = new Viewer(new Canvas3D(glwindow));
		viewer.getView().setMinimumFrameCycleTime(5);
		return viewer;

	}

	public HeadlessUniverse()
	{
		super(new ViewingPlatform(), createView());
		this.getCanvas().addNotify();
	}

}
