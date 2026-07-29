package tools3d.camera.simple;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.vecmath.Vector3d;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;

public class MyKeyboardMover {
	private double			defaultMove	= 1d;

	private TransformGroup	transformGroup;

	private Transform3D		currentT3D	= new Transform3D();
	private Vector3d		loc			= new Vector3d();

	public MyKeyboardMover(Canvas3D c, TransformGroup transformGroup) {
		this.transformGroup = transformGroup;
		c.getGLWindow().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				transformGroup.getTransform(currentT3D);
				double moveAmount = defaultMove;

				// shift for careful move
				if (e.isShiftDown()) {
					moveAmount = defaultMove * 0.2;
				}

				Vector3d forward = new Vector3d(0, 0, -1);
				Vector3d left = new Vector3d(-1, 0, 0);
				Vector3d up = new Vector3d(0, 1, 0);
				Vector3d m = new Vector3d(0, 0, 0);

				if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
					m.set(forward);
				} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
					m.set(forward);
					m.negate();
				} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD9) {
					m.set(up);
				} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
					m.set(up);
					m.negate();
				} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
					m.set(left);
				} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD6) {
					m.set(left);
					m.negate();
				} else {
					m.set(0, 0, 0);
				}

				m.scale(moveAmount);

				currentT3D.get(loc);
				loc.add(m);
				currentT3D.setTranslation(loc);

				transformGroup.setTransform(currentT3D);

			}

		});

		System.out.println("NumPad to move Model");

	}

}
