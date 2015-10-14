package javax.media.j3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
 

/**
 * Arraylist average speed for inserts average speed for removes
 * 
 * Java3D contains some arraylist that are super fast for adds, but kill removeChild because of slow removal
 * @author phil
 *
 */
public class BalancedArrayList<E> extends ArrayList<E>
{
	public LinkedHashSet<E> actualData = new LinkedHashSet<E>();

	public BalancedArrayList(int initialCapacity)
	{
		super(initialCapacity);

	}

	public BalancedArrayList()
	{
		this(10);
	}

	public Iterator<E> iterator()
	{
		return actualData.iterator();
	}

	public void trimToSize()
	{
		throw new UnsupportedOperationException();
	}

	public void ensureCapacity(int minCapacity)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		return actualData.size();
	}

	public boolean isEmpty()
	{
		return actualData.isEmpty();
	}

	public boolean contains(Object o)
	{
		return actualData.contains(o);
	}

	public int indexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}

	public int lastIndexOf(Object o)
	{
		throw new UnsupportedOperationException();
	}

	public Object clone()
	{
		throw new UnsupportedOperationException();
	}

	public Object[] toArray()
	{
		throw new UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] a)
	{
		throw new UnsupportedOperationException();
	}

	public E get(int index)
	{
		throw new UnsupportedOperationException();
	}

	public E set(int index, E element)
	{
		throw new UnsupportedOperationException();
	}

	public boolean add(E e)
	{
		return actualData.add(e);
	}

	public void add(int index, E element)
	{
		throw new UnsupportedOperationException();
	}

	public E remove(int index)
	{
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o)
	{
		return actualData.remove(o);
	}

	public void clear()
	{
		actualData.clear();
	}

	public boolean addAll(Collection<? extends E> c)
	{
		return actualData.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		throw new UnsupportedOperationException();
	}
}
