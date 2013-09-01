package tools3d.utils.scenegraph.old;

/**
 * @author main
 *
 */
public class BGStructTransformed extends BGStruct
{
	//This line basically defines the struct
	private SimpleTG simpleTG = new SimpleTG(getBottomBG());

	public BGStructTransformed()
	{
		addChildToTop(simpleTG);
	}

	public SimpleTG getTransform()
	{
		return simpleTG;
	}
}
