package tools3d.mixed3d2d.overlay.swing;

import java.awt.AlphaComposite;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import tools.GlobalKeyCatcher;
import tools3d.mixed3d2d.Canvas3D2D;

/**
 * The other *3D classes in this package should be used in place of the swing versions
 * 
 * {@link JPanel3D} and {@link JInternalFrame3D} should be the container classes
 * 
 * Note the jcomponents in order added so increasing z for rendering, but interaction is searched backward front z to back
 * is the first to be intersected with and interacted, so add backgrounds and static stuff first
 * 
 * Tooltips are working now, the toolTip component is basically a 1 member glass pane  
 * 
 * Use .setOpaque(false); for back ground show through, JLabels start off non opaque in some lafs
 * 
 * Borders work fine
 * setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "a title"));
 * 
 * Supports 
 * AbstractButton: 
 * 		JButton, 
 * 		JRadioButton, 
 * 		JToggleButton, 
 * 		JCheckBox mint
 * Button Groups work as usual			
 * 	
 * JLabel mint see EnumeratedLabel for icon system
 * 
 * JTextComponents: 
 * 		JTextField accepts input works well, caret does not flash
 *  	JTextArea  no testing done yet
 *		JEditorPane
 * html formatted output
 * JEditorPane bottomText = new JEditorPane();		
 * bottomText.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
 * bottomText.setText("<a href=\"http://www.yahoo.com\">Yahoo</a>");
 * 
 * {@link JSlider} works mint 
 * 
 * {@link JList} - single selection only so far (though ctl and shift click should work eventually)
 *  
 * {@link JProgressBar} - need to call redraw(true) when value is altered (mo testing required)
 * 		
 * {@link JPanel3D} auto adds removes child jcomponents  can be setOpaque(false), needs doLayout called in constructor, DONOT setBorder
 * JPanel - Don't use!
 * 
 * {@link JInternalFrame3D} good layout, awesome draggin in all, closable
 * JInternalFrame - Dont use!
 *   
 *  
 * JSplitPane3D? not done 
 * JScrollPane3D? not done but notice that the interior panel must have a resonable size set
 *  
 *
 * JTable - no selection 
 *  		no jscrollpane makes for no headers
 *  		jscrollpane renders badly
 * JTree? jtree and jtable both need solid scrollpanes to work
 * JSpinner? not needed I think
 * JComboBox? probably never
 * JColorChooser
 * JFileChooser
 * JMenuBar
 * JToolBar
 * JTabbedPane
 *      
 * @author philip
 *
 */

//TODO: JTextComponent does correctly render text selection? but does in fact select it

public class Panel3D implements MouseListener, MouseMotionListener, KeyListener
{

	private boolean enabled = true;

	private BufferedImage currentBufferedImage;

	private BufferedImage bufferedImage1;

	private BufferedImage bufferedImage2;

	private Graphics2D currentGraphics;

	private Graphics2D graphics1;

	private Graphics2D graphics2;

	private int x = 0;

	private int y = 0;

	private int width = -1;

	private int height = -1;

	private Canvas3D2D canvas3D2D;

	private JTextComponent currentKeyboardTarget = null;

	private JComponent compDownButton = null;

	private Point dragStartPoint = null;

	private JComponent dragComp = null;

	private ArrayList<JComponent> comps = new ArrayList<JComponent>();

	private ArrayList<JComponent> updateRequired = new ArrayList<JComponent>();

	public static GlobalKeyCatcher globalKeyCatcher = new GlobalKeyCatcher();

	private ToolTipTimer toolTipTimer = new ToolTipTimer();

	// defined here so mouse to component checks can ignore
	private JLabel toolTip = new JLabel();

	private boolean fullSized = false;

	/**
	 * fullScreen
	 */
	public Panel3D()
	{
		setSize(1, 1);//resized when the setConfig happens

		globalKeyCatcher.addKeyListener(this);
		toolTipTimer.start();
		fullSized = true;
	}

	public Panel3D(int width, int height)
	{
		setSize(width, height);
		globalKeyCatcher.addKeyListener(this);
		toolTipTimer.start();
	}

	public void setSize(int newWidth, int newHeight)
	{
		if (width != newWidth || height != newHeight)
		{
			width = newWidth == 0 ? 1 : newWidth;
			height = newHeight == 0 ? 1 : newHeight;
			resetBuffers();
		}
	}

	public void resetBuffers()
	{
		bufferedImage1 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		graphics1 = bufferedImage1.createGraphics();
		bufferedImage2 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		graphics2 = bufferedImage2.createGraphics();
		currentBufferedImage = bufferedImage1;
		currentGraphics = graphics1;
	}

	public BufferedImage getBufferedImage()
	{
		return currentBufferedImage;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public void finalize()
	{
		toolTipTimer.setStop();
	}

	public synchronized void setConfig(Canvas3D2D _canvas3D2D)
	{
		// de-register on the old canvas
		if (canvas3D2D != null)
		{
			canvas3D2D.removePanel3D(this);
			canvas3D2D.removeMouseListener(this);
			canvas3D2D.removeMouseMotionListener(this);
		}

		canvas3D2D = _canvas3D2D;
		// set up new canvas
		if (canvas3D2D != null)
		{
			canvas3D2D.addPanel3D(this);
			canvas3D2D.addMouseListener(this);
			canvas3D2D.addMouseMotionListener(this);
			canvas3D2D.addComponentListener(new ComponentAdapter()
			{
				@Override
				public void componentResized(ComponentEvent e)
				{
					screenResized();
				}

				@Override
				public void componentShown(ComponentEvent e)
				{
					screenResized();
				}

			});
			screenResized();
		}

	}

	private void screenResized()
	{
		if (fullSized && canvas3D2D != null)
		{
			setSize(canvas3D2D.getWidth(), canvas3D2D.getHeight());
			redraw(true);
		}
	}

	public synchronized void addJComponent(JComponent c)
	{
		if (!comps.contains(c))
		{
			c.setDoubleBuffered(false);
			comps.add(c);
			updateRequired.add(c);
		}

		//tinyLAF requires buttons to have parent, odd?
		if (c instanceof AbstractButton && c.getParent() == null)
		{
			JPanel p = new JPanel();
			p.add(c);
		}
		else if (c instanceof JTextComponent)
		{
			DefaultCaret dc = (DefaultCaret) ((JTextComponent) c).getCaret();
			dc.setBlinkRate(0); //turns off blinking
		}

		this.setEnabled(comps.size() != 0);
		redraw(false);
	}

	public synchronized void removeJComponent(JComponent c)
	{
		comps.remove(c);
		if (currentKeyboardTarget == c)
		{
			currentKeyboardTarget = null;
		}

		if (compDownButton == c)
		{
			compDownButton = null;
		}

		if (dragComp == c)
		{
			dragComp = null;
			dragStartPoint = null;
		}

		this.setEnabled(comps.size() != 0);
		redraw(true);
	}

	private void clearImage(Graphics2D g)
	{
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, width, height);
		g.setComposite(AlphaComposite.SrcOver);

	}

	public synchronized void redraw(boolean forceAll)
	{
		// get the back buffer pointer (opposite of current)
		Graphics2D g = (currentGraphics == graphics1) ? graphics2 : graphics1;

		if (forceAll)
		{
			clearImage(g);
		}

		for (JComponent c : comps)
		{
			if (forceAll || updateRequired.contains(c))
			{
				adjustGraphicsLocation(true, g, c);
				try
				{

					if (c instanceof JScrollPane)
					{
						//TODO: posibly all children should be painted like this?
						// scroll bars arent painting
						// painting children doesn't work because of the translated graphics bounds
						/*for (Component child : c.getComponents())
						{
							System.out.println("" + child);
							System.out.println("childdim " + child.getSize());
							child.paint(g);
						}*/
					}
					else
					{

						c.paint(g);
					}
				}
				catch (NullPointerException e)
				{
					// happens due to crazy getVolatileOffscreenBuffer  etc
				}
				adjustGraphicsLocation(false, g, c);
			}
		}
		updateRequired.clear();

		//swap the back buffer to front
		currentBufferedImage = g == graphics1 ? bufferedImage1 : bufferedImage2;
	}

	private static void adjustGraphicsLocation(boolean prePaint, Graphics2D g, JComponent c)
	{
		Point ap = getAbsoluteLocation(c);
		if (prePaint)
		{
			g.translate(ap.x, ap.y);
		}
		else
		{
			g.translate(-ap.x, -ap.y);
		}
	}

	private static Point getAbsoluteLocation(JComponent c)
	{
		int x = c.getX();
		int y = c.getY();
		Container parent = c.getParent();

		while (parent != null)
		{
			if (parent instanceof JPanel3D || parent instanceof BasicInternalFrameTitlePane)
			{
				x += parent.getX() + parent.getInsets().left;
				y += parent.getY() + parent.getInsets().top;
			}
			else if (parent instanceof JInternalFrame3D)
			{
				JInternalFrame3D jif = (JInternalFrame3D) parent;
				Point compPosition = jif.getComponentPosition(c);
				x += jif.getX() + compPosition.x;
				y += jif.getY() + compPosition.y;
			}

			parent = parent.getParent();
		}

		return new Point(x, y);
	}

	private synchronized JComponent findComponent(MouseEvent e)
	{
		Point mp = mouseToHudPoint(e);

		//search backwards for interaction at highest z
		for (int i = comps.size() - 1; i >= 0; i--)
		{
			JComponent c = comps.get(i);
			//ignore tooltip for picking
			if (c != toolTip)
			{
				Point p = pointInC(mp, c);
				if (c.contains(p.x, p.y))
				{
					return c;
				}
			}
		}
		return null;
	}

	private static Point pointInC(Point p, JComponent c)
	{
		Point ap = getAbsoluteLocation(c);
		Point ret = new Point(p);
		ret.x -= ap.x;
		ret.y -= ap.y;
		return ret;
	}

	private Point mouseToHudPoint(MouseEvent e)
	{
		// adjust for hud location
		int xLoc = getX();
		int yLoc = getY();
		Point p = e.getPoint();
		p.x -= xLoc;
		p.y -= yLoc;
		return p;
	}

	public synchronized void removeAll()
	{
		comps.clear();
		redraw(true);
	}

	@Override
	public synchronized void mousePressed(MouseEvent e)
	{
		currentKeyboardTarget = null;
		dragStartPoint = null;
		dragComp = null;
		compDownButton = null;
		JComponent c = findComponent(e);
		if (c != null)
		{
			if (c instanceof JList)
			{
				compDownButton = c;
			}
			else if (c instanceof JTextComponent)
			{
				currentKeyboardTarget = (JTextComponent) c;
				((DefaultCaret) ((JTextComponent) c).getCaret()).focusGained(new FocusEvent(c, -1));
			}

			if (c instanceof BasicInternalFrameTitlePane || c instanceof JInternalFrame3D)
			{
				// doesn't like getting events
			}
			else
			{
				// Note used for everything
				e.setSource(c);
				Point p = pointInC(mouseToHudPoint(e), c);
				MouseEvent e2 = new MouseEvent(c, e.getID(), 0, e.getModifiers(), (int) p.getX(), (int) p.getY(), e.getClickCount(),
						e.isPopupTrigger(), e.getButton());
				c.dispatchEvent(e2);
				updateRequired.add(c);
			}
			redraw(false);
		}
	}

	@Override
	public synchronized void mouseReleased(MouseEvent e)
	{
		currentKeyboardTarget = null;
		dragStartPoint = null;
		dragComp = null;

		JComponent c = findComponent(e);
		if (c != null)
		{
			if (c instanceof AbstractButton)
			{
				if (((AbstractButton) c).getModel().isRollover())
				{
					((AbstractButton) c).getModel().setRollover(false);
					updateRequired.add(c);
				}
			}
			else if (c instanceof JList)
			{
				if (compDownButton == c)
				{
					Point p = pointInC(mouseToHudPoint(e), c);
					JList jList = (JList) c;
					for (int i = 0; i < jList.getModel().getSize(); i++)
					{
						Rectangle rec = jList.getCellBounds(i, i);
						if (rec.contains(p))
						{
							jList.setSelectedIndex(i);
							updateRequired.add(c);
						}
					}
				}
			}
			else if (c instanceof JTextComponent)
			{
				currentKeyboardTarget = (JTextComponent) c;
				((DefaultCaret) ((JTextComponent) c).getCaret()).focusGained(new FocusEvent(c, -1));
			}

			//send click to everything
			e.setSource(c);
			Point p = pointInC(mouseToHudPoint(e), c);
			MouseEvent e2 = new MouseEvent(c, e.getID(), 0, e.getModifiers(), (int) p.getX(), (int) p.getY(), e.getClickCount(),
					e.isPopupTrigger(), e.getButton());
			c.dispatchEvent(e2);
			updateRequired.add(c);
		}

		for (JComponent c2 : comps)
		{
			if (c2 != c)
			{
				if (c2 instanceof JTextComponent)
				{
					((DefaultCaret) ((JTextComponent) c2).getCaret()).focusLost(new FocusEvent(c2, -1));
				}
			}
		}

		compDownButton = null;
		redraw(false);
	}

	@Override
	public synchronized void mouseDragged(MouseEvent e)
	{
		JComponent c = findComponent(e);
		if (c != null)
		{
			if (c instanceof AbstractButton)
			{
				if (!((AbstractButton) c).getModel().isArmed())
				{
					((AbstractButton) c).getModel().setArmed(true);
					updateRequired.add(c);
				}
			}
			else if (c instanceof BasicInternalFrameTitlePane || c instanceof JInternalFrame3D)
			{
				JInternalFrame3D jif = null;
				if (c instanceof JInternalFrame3D)
				{
					jif = (JInternalFrame3D) c;
				}
				else
				{
					jif = (JInternalFrame3D) c.getParent();
				}
				dragComp = jif;

			}
			else
			{
				e.setSource(c);
				Point p = pointInC(mouseToHudPoint(e), c);
				MouseEvent e2 = new MouseEvent(c, e.getID(), 0, e.getModifiers(), (int) p.getX(), (int) p.getY(), e.getClickCount(),
						e.isPopupTrigger(), e.getButton());
				c.dispatchEvent(e2);
				updateRequired.add(c);
			}

		}

		for (JComponent c2 : comps)
		{
			if (c2 != c)
			{
				if (c2 instanceof AbstractButton)
				{
					if (((AbstractButton) c2).getModel().isArmed())
					{
						((AbstractButton) c2).getModel().setArmed(false);
						updateRequired.add(c2);
					}
				}
			}
		}

		if (dragComp != null)
		{
			if (dragStartPoint != null)
			{
				Point dragDiff = new Point(e.getPoint());
				dragDiff.x -= dragStartPoint.x;
				dragDiff.y -= dragStartPoint.y;

				dragComp.setLocation(dragComp.getX() + dragDiff.x, dragComp.getY() + dragDiff.y);
			}
			dragStartPoint = e.getPoint();
		}

		toolTipTimer.setCurrentHoverTarget(c, mouseToHudPoint(e));

		redraw(false);
	}

	@Override
	public synchronized void mouseMoved(MouseEvent e)
	{
		JComponent c = findComponent(e);

		if (c != null)
		{
			if (c instanceof AbstractButton)
			{
				if (!((AbstractButton) c).getModel().isRollover())
				{
					((AbstractButton) c).getModel().setRollover(true);
					updateRequired.add(c);
				}
			}
			else
			{
				e.setSource(c);
				Point p = pointInC(mouseToHudPoint(e), c);
				MouseEvent e2 = new MouseEvent(c, e.getID(), 0, e.getModifiers(), (int) p.getX(), (int) p.getY(), e.getClickCount(),
						e.isPopupTrigger(), e.getButton());
				c.dispatchEvent(e2);

				// not entirely sure about this, makes teh screen flash too much
				//	updateRequired.add(c);
			}
		}

		for (JComponent c2 : comps)
		{
			if (c != c2)
			{
				if (c2 instanceof AbstractButton)
				{
					if (((AbstractButton) c2).getModel().isRollover())
					{
						((AbstractButton) c2).getModel().setRollover(false);
						updateRequired.add(c2);
					}
				}

			}
		}

		toolTipTimer.setCurrentHoverTarget(c, mouseToHudPoint(e));

		redraw(false);

	}

	@Override
	public synchronized void mouseEntered(MouseEvent e)
	{
		JComponent c = findComponent(e);
		if (c != null)
		{
			e.setSource(c);
			Point p = pointInC(mouseToHudPoint(e), c);
			MouseEvent e2 = new MouseEvent(c, e.getID(), 0, e.getModifiers(), (int) p.getX(), (int) p.getY(), e.getClickCount(),
					e.isPopupTrigger(), e.getButton());
			c.dispatchEvent(e2);
			updateRequired.add(c);
		}

	}

	@Override
	public synchronized void mouseExited(MouseEvent e)
	{
		JComponent c = findComponent(e);

		if (c != null)
		{
			e.setSource(c);
			Point p = pointInC(mouseToHudPoint(e), c);
			MouseEvent e2 = new MouseEvent(c, e.getID(), 0, e.getModifiers(), (int) p.getX(), (int) p.getY(), e.getClickCount(),
					e.isPopupTrigger(), e.getButton());
			c.dispatchEvent(e2);
			updateRequired.add(c);
		}
	}

	@Override
	public synchronized void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public synchronized void keyTyped(KeyEvent e)
	{
		if (currentKeyboardTarget != null && comps.contains(currentKeyboardTarget) && e.getSource() == canvas3D2D)
		{
			e.setSource(currentKeyboardTarget);
			KeyEvent e2 = new KeyEvent(currentKeyboardTarget, e.getID(), 0, e.getModifiers(), e.getKeyCode(), e.getKeyChar());
			currentKeyboardTarget.dispatchEvent(e2);
			updateRequired.add(currentKeyboardTarget);
			redraw(false);
		}
	}

	@Override
	public synchronized void keyPressed(KeyEvent e)
	{
		if (currentKeyboardTarget != null && comps.contains(currentKeyboardTarget) && e.getSource() == canvas3D2D)
		{
			e.setSource(currentKeyboardTarget);
			KeyEvent e2 = new KeyEvent(currentKeyboardTarget, e.getID(), 0, e.getModifiers(), e.getKeyCode(), e.getKeyChar());
			currentKeyboardTarget.dispatchEvent(e2);
			updateRequired.add(currentKeyboardTarget);
			redraw(false);
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e)
	{
		if (currentKeyboardTarget != null && comps.contains(currentKeyboardTarget) && e.getSource() == canvas3D2D)
		{
			e.setSource(currentKeyboardTarget);
			KeyEvent e2 = new KeyEvent(currentKeyboardTarget, e.getID(), 0, e.getModifiers(), e.getKeyCode(), e.getKeyChar());
			currentKeyboardTarget.dispatchEvent(e2);
			updateRequired.add(currentKeyboardTarget);
			redraw(false);
		}
	}

	private class ToolTipTimer extends Thread
	{
		private int TOOLTIP_DELAY = 600;

		private JComponent currentHoverTarget = null;

		private long hoverStartTime = 0;

		private Point hoverPoint = new Point();

		private boolean stop = false;

		private boolean isToolTipShowing = false;

		public ToolTipTimer()
		{
			this.setDaemon(true);
		}

		@Override
		public void run()
		{
			while (!stop)
			{
				if (currentHoverTarget != null && (System.currentTimeMillis() - hoverStartTime) > TOOLTIP_DELAY
						&& currentHoverTarget.getToolTipText() != null && currentHoverTarget.getToolTipText().length() > 0)
				{
					if (!isToolTipShowing)
					{
						//TODO: move hover point intn screen so we can show the tool tip if it runs off screen
						toolTip.setText(currentHoverTarget.getToolTipText());
						toolTip.setLocation(hoverPoint);
						toolTip.setOpaque(true);
						toolTip.setSize(toolTip.getPreferredSize());
						addJComponent(toolTip);
						isToolTipShowing = true;
					}
				}
				else if (isToolTipShowing)
				{
					removeJComponent(toolTip);
					isToolTipShowing = false;
				}

				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
			}
		}

		public void setStop()
		{
			this.stop = true;
		}

		public void setCurrentHoverTarget(JComponent ht, Point p)
		{
			hoverPoint.setLocation(p);
			if (ht == null)
			{
				this.currentHoverTarget = null;
				hoverStartTime = 0;
			}
			else if (ht != currentHoverTarget)
			{
				this.currentHoverTarget = ht;
				hoverStartTime = System.currentTimeMillis();
			}
		}
	}

}
