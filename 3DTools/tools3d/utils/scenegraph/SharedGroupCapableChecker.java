package tools3d.utils.scenegraph;

import javax.media.j3d.AlternateAppearance;
import javax.media.j3d.Background;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingLeaf;
import javax.media.j3d.Clip;
import javax.media.j3d.Fog;
import javax.media.j3d.Group;
import javax.media.j3d.Leaf;
import javax.media.j3d.Light;
import javax.media.j3d.Link;
import javax.media.j3d.ModelClip;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.Sound;
import javax.media.j3d.Soundscape;
import javax.media.j3d.ViewPlatform;

public class SharedGroupCapableChecker
{

	/**
	 * A shared subgraph may contain any group node, except an embedded SharedGroup node (SharedGroup nodes cannot have parents). 
	 * However, only the following leaf nodes may appear in a shared subgraph:

	 * Light
	 * Link
	 * Morph
	 * Shape3D
	 * Sound

	 An IllegalSharingException is thrown if any of the following leaf nodes appear in a shared subgraph:

	 * AlternateAppearance
	 * Background
	 * Behavior
	 * BoundingLeaf
	 * Clip
	 * Fog
	 * ModelClip
	 * Soundscape
	 * ViewPlatform
	 * 
	 * @param root
	 * @return
	 */

	public static boolean canBeShared(Group root)
	{
		if (root instanceof Unsharable)
			return false;

		for (int i = 0; i < root.numChildren(); i++)
		{
			Node c = root.getChild(i);

			if (c instanceof SharedGroup)
			{
				return false;
			}
			else if (c instanceof Group)
			{
				boolean childCan = canBeShared((Group) c);
				if (!childCan)
				{
					return false;
				}
			}
			else if (c instanceof AlternateAppearance || c instanceof Background || c instanceof Behavior || c instanceof BoundingLeaf
					|| c instanceof Clip || c instanceof Fog || c instanceof ModelClip || c instanceof Soundscape
					|| c instanceof ViewPlatform)
			{
				return false;
			}
			else if (c instanceof Leaf && (c instanceof Light || c instanceof Link || c instanceof Shape3D || c instanceof Sound))
			{
				//these are allowed, don't fail yet			
			}
			else
			{
				return false;
			}
		}

		return true;
	}
}
