package tools3d.universe;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.java3d.utils.universe.ViewingPlatform;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

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
