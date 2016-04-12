package tools3d.utils.scenegraph;

import javax.media.j3d.Group;
import javax.media.j3d.MultipleParentException;
import javax.media.j3d.Node;

/** NOTE! j3d does not allow multi threaded access to add and remove groups
// It can cause deadlocks, betterdistanceLOD on the behavior thread is
// doing structure change addChild, removeChild etc, 
 so these queuing callbacks need to be on the behavior thread as well.
 
 Notice for non live scene graphs this system immediately makes the call and does not queue anything
 
 https://java.net/jira/browse/JAVA3D-193 
*/
public class StructureUpdateBehavior extends QueuingCallbackBehavior implements QueuingCallbackBehavior.CallBack
{
	public StructureUpdateBehavior()
	{
		super();
		this.setCallBack(this);
	}

	public synchronized void add(Group parent, Node child)
	{
		if (parent.isLive())
			this.addToQueue(new StructureUpdate(StructureUpdate.TYPE.ADD, parent, child));
		else
			parent.addChild(child);
	}

	public synchronized void remove(Group parent, Node child)
	{
		if (parent.isLive())
			this.addToQueue(new StructureUpdate(StructureUpdate.TYPE.REMOVE, parent, child));
		else
			parent.removeChild(child);
	}

	public synchronized void remove(Node child)
	{
		remove((Group) child.getParent(), child);
	}

	@Override
	public synchronized void addToQueue(Object parameter)
	{
		if (parameter instanceof StructureUpdate)
		{
			super.addToQueue(parameter);
		}
		else
		{
			throw new IllegalArgumentException("Parameter must be instanceof StructureUpdate not " + parameter);
		}
	}

	@Override
	public void run(Object parameter)
	{
		if (parameter instanceof StructureUpdate)
		{
			long startTime = System.currentTimeMillis();
			StructureUpdate structureUpdate = (StructureUpdate) parameter;
			if (structureUpdate.type == StructureUpdate.TYPE.ADD)
			{
				try
				{
					structureUpdate.parent.addChild(structureUpdate.child);
				}
				catch (MultipleParentException e)
				{
					System.out.println("MultipleParentException parent =" + structureUpdate.parent + " child = " + structureUpdate.child);
				}
			}
			else if (structureUpdate.type == StructureUpdate.TYPE.REMOVE)
			{
				structureUpdate.parent.removeChild(structureUpdate.child);
			}

			if ((System.currentTimeMillis() - startTime) > 5)
				System.out.println("Structure (" + structureUpdate.type.name() + " " + structureUpdate.parent.getName() + "-"
						+ structureUpdate.child.getName() + ") update took " + (System.currentTimeMillis() - startTime) + "ms");
		}
		else
		{
			throw new IllegalArgumentException("Parameter must be instanceof StructureUpdate not " + parameter);
		}

	}

	public static class StructureUpdate
	{
		public enum TYPE
		{
			ADD, REMOVE
		};

		public TYPE type;

		public Group parent = null;

		public Node child = null;

		public StructureUpdate(TYPE type, Group parent, Node child)
		{
			this.type = type;
			this.parent = parent;
			this.child = child;
		}

		public String toString()
		{
			return "StructureUpdate: t=" + type + " p=" + parent + " c=" + child;
		}
	}

}
