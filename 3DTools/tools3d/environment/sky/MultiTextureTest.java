package tools3d.environment.sky;

import java.applet.Applet;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.TransparencyAttributes;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

public class MultiTextureTest extends Applet implements ItemListener
{
	private TextureUnitState textureUnitState[] = new TextureUnitState[2];

	private Texture skyTexture;

	private Texture sunTexture;

	private java.net.URL stoneImage = null;

	private java.net.URL skyImage = null;

	public BranchGroup createSceneGraph()
	{
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		Appearance ap = new Appearance();

		// load textures
		TextureAttributes texAttr1 = new TextureAttributes();
		texAttr1.setTextureMode(TextureAttributes.REPLACE);
		TextureAttributes texAttr2 = new TextureAttributes();
		texAttr2.setTextureMode(TextureAttributes.DECAL);

		TextureLoader tex = new TextureLoader(stoneImage, new String("RGBA"), this);
		if (tex == null)
			return null;
		
		skyTexture = tex.getTexture();

		tex = new TextureLoader(skyImage, new String("RGBA"), this);
		if (tex == null)
			return null;
		sunTexture = tex.getTexture();

		textureUnitState[0] = new TextureUnitState(skyTexture, texAttr1, null);
		textureUnitState[0].setCapability(TextureUnitState.ALLOW_STATE_WRITE);

		textureUnitState[1] = new TextureUnitState(sunTexture, texAttr2, null);
		textureUnitState[1].setCapability(TextureUnitState.ALLOW_STATE_WRITE);

		ap.setTextureUnitState(textureUnitState);

		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(TransparencyAttributes.NICEST);
		ta.setTransparency(0);
		ap.setTransparencyAttributes(ta);

		//Create a Box
		Box BoxObj = new Box(1.5f, 1.5f, 0.8f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, ap, 2);
		// add it to the scene graph.
		objRoot.addChild(BoxObj);

		return objRoot;
	}

	public MultiTextureTest()
	{
	}

	public MultiTextureTest(java.net.URL stoneURL, java.net.URL skyURL)
	{
		stoneImage = stoneURL;
		skyImage = skyURL;
	}

	public void itemStateChanged(ItemEvent e)
	{
		int index = 0;

		switch (index)
		{
			case 1: /* stone */
				textureUnitState[0].setTexture(skyTexture);
				textureUnitState[1].setTexture(null);
				break;
			case 3: /* sky */
				textureUnitState[0].setTexture(null);
				textureUnitState[1].setTexture(sunTexture);
				//TODO: using this type of thing give it a tex coord gen to change the sun's position
				break;
			case 4: /* stone + sky */
				textureUnitState[0].setTexture(skyTexture);
				textureUnitState[1].setTexture(sunTexture);
				break;
			default: /* both */
				break;
		}
	}

	public static void main(String argv[])
	{
		java.net.URL stoneURL = null;
		java.net.URL skyURL = null;
		// the path to the image for an application
		try
		{
			stoneURL = new java.net.URL("file:../images/bg.jpg");
			skyURL = new java.net.URL("file:../images/sunSky.gif");
		}
		catch (java.net.MalformedURLException ex)
		{
			System.out.println(ex.getMessage());
			System.exit(1);
		}
		new MainFrame(new MultiTextureTest(stoneURL, skyURL), 750, 750);
	}
}
