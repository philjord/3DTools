package tools3d.swingoverlay;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import tools.GridLayout2;

public class Panel3DTester
{
	//TODO: I should grab the tiny laf demo screen full of components
	/**
	 * JPanel3D sub class should call doLayout() in the constructor, even with no layout manager
	add {@link JSlider}  
	add {@link JList}  
	add{@link JProgressBar}
	JEditorPane 
	JRadioButton, 
	* 		JToggleButton, 
	* 		JCheckBox mint
	 * @param overlay
	 */

	public Panel3DTester(Panel3D overlay)
	{
		final JPanel3D testy = new JPanel3D(overlay);

		testy.setLocation(300, 400);
		testy.setSize(100, 40);
		testy.setLayout(new GridLayout2(-1, 1));

		JButton b = new JButton("Hello world");
		b.setToolTipText("A button that will disappear soon");
		testy.add(b);
		JLabel l = new JLabel("booms!");
		l.setOpaque(true);
		testy.add(l);
		testy.doLayout();

		testy.setVisible(true);

		//TODO: add 
		//lists internal panes
		//textfields
		//textareas
		//check boxes
		//sliders

		final JInternalFrame3D testy2 = new JInternalFrame3D(overlay, "Aframe", 100, 300, true);

		testy2.setLocation(400, 400);
		testy2.setLayout(new GridLayout2(-1, 1));

		JList list = new JList(new String[]
		{ "a", "b", "c" });
		testy2.add(list);
		testy2.setVisible(true);

		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(3000);
					}
					catch (InterruptedException e)
					{
					}
					testy.setVisible(!testy.isVisible());
				}
			}
		});

		t.start();

		JPanel3D testy3 = new JPanel3D(overlay);
		testy3.setLocation(100, 400);
		testy3.setSize(100, 40);
		JTextArea lotsOText = new JTextArea(
				"oajhfosdfhdshfskljf\ndjifhsdifjhsdfgdsfg\nsdofjhsdf\nsdofihsdhf\nsdfuhsdjih\nisdfhisdf\nsdfjhsdiofh\n");

		lotsOText.setSize(500, 500);
		JScrollPane js = new JScrollPane(lotsOText);

		testy3.add(js);
		js.setSize(200, 50);
		js.setLocation(0, 0);
		js.doLayout();
		testy3.setVisible(true);
	}
}
