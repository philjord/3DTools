package tools3d.resolution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("all")
public class QueryProperties extends JFrame
{
	public static String ps = System.getProperty("path.separator");

	private JScrollPane jScrollPane1;

	private JTextArea myTextArea;

	private static void printProps(JTextArea textArea, Map map, String[] propList)
	{
		// Create an alphabetical list of keys
		List<String> keyList = new ArrayList<String>(map.keySet());
		Collections.sort(keyList);
		Iterator<String> it;

		// Collection used to remember the properties we've already
		// printed, so we don't print them twice
		HashSet<String> hs = new HashSet<String>();

		// Print out the values for the caller-specified properties
		String key;
		for (int i = 0; i < propList.length; i++)
		{
			int len = propList[i].length();
			int idxWild = propList[i].indexOf('*');
			if (idxWild < 0)
			{
				key = propList[i];
				if (!hs.contains(key))
				{
					textArea.append(key + " = " + map.get(key) + "\n");
					hs.add(key);
				}
			}
			else if (idxWild == len - 1)
			{
				String pattern = propList[i].substring(0, len - 1);
				it = keyList.iterator();
				while (it.hasNext())
				{
					key = it.next();
					if (key.startsWith(pattern) && !hs.contains(key))
					{
						textArea.append(key + " = " + map.get(key) + "\n");
						hs.add(key);
					}
				}
			}
			else
			{
				textArea.append(propList[i] + " = ERROR: KEY WITH EMBEDDED WILD CARD IGNORED\n");
			}
		}

		// Print out the values for those properties not already printed
		it = keyList.iterator();
		while (it.hasNext())
		{
			key = it.next();
			if (!hs.contains(key))
			{
				textArea.append(key + " = " + map.get(key) + "\n");
			}
		}

		//also bang everythign else out on teh console for fun
		Properties props = System.getProperties();
		props.list(System.out);

		//and output if java3d is sealed too for fun
		System.out.println("javax.media.j3d isSealed? " + Package.getPackage("javax.media.j3d").isSealed());
		checkForInstalledJ3d();
	}

	public static boolean checkForInstalledJ3d()
	{
		// check for java3d installed
		String extProp = System.getProperties().getProperty("java.ext.dirs");
		if (extProp != null)
		{
			String[] paths = extProp.split(ps);
			for (String path : paths)
			{
				File folder = new File(path);
				if (folder.exists() && folder.isDirectory())
				{
					File[] listOfFiles = folder.listFiles();
					for (int i = 0; i < listOfFiles.length; i++)
					{
						if (listOfFiles[i].isFile())
						{
							if (listOfFiles[i].getName().indexOf("j3d") != -1)
							{
								String mess = listOfFiles[i].getPath() + " looks like Java3d, it needs to be uninstalled.";
								System.out.println(mess);
								JOptionPane.showMessageDialog(null, mess);
								return true;
							}
						}
					}
				}
			}

		}

		return false;
	}

	public QueryProperties()
	{
		myTextArea = new JTextArea();
		jScrollPane1 = new JScrollPane(myTextArea);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("QueryProperties");
		jScrollPane1.setPreferredSize(new Dimension(400, 500));
		myTextArea.setColumns(20);
		myTextArea.setEditable(false);
		myTextArea.setRows(5);

		getContentPane().add(jScrollPane1, BorderLayout.CENTER);

		pack();
		printJ3DProps(myTextArea);
	}

	public static void printJ3DProps(JTextArea textArea)
	{

		//VirtualUniverse vu = new VirtualUniverse();
		Map vuMap = VirtualUniverse.getProperties();
		final String[] vuPropList =
		{ "j3d.version", "j3d.vendor", "j3d.specification.version", "j3d.specification.vendor", "j3d.*"
		// Just print all other properties in alphabetical order
		};

		printProps(textArea, vuMap, vuPropList);
		textArea.append("\n");

		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();

		/* We need to set this to force choosing a pixel format
		   that support the canvas.
		*/
		template.setStereo(GraphicsConfigTemplate.PREFERRED);
		template.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);

		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getBestConfiguration(template);

		Map c3dMap = new Canvas3D(config).queryProperties();
		final String[] c3dPropList =
		{ "native.*", "doubleBufferAvailable", "stereoAvailable", "sceneAntialiasing*", "compressedGeometry.majorVersionNumber",
				"compressedGeometry.minorVersionNumber", "compressedGeometry.*", "textureUnitStateMax", "textureWidthMax",
				"textureHeightMax",
		// Just print all other properties in alphabetical order
		};

		printProps(textArea, c3dMap, c3dPropList);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		//new Java3dLinker2();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				new QueryProperties().setVisible(true);
			}
		});
	}

}
