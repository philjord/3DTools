package tools3d.utils;

class DoubleSlot
{
	final int value1;

	final int value2;

	public DoubleSlot(int value1, int value2)
	{
		this.value1 = value1;
		this.value2 = value2;
	}
}

public class EscapeTest
{

	static int slotValue(DoubleSlot slot)
	{
		return slot.value1 + slot.value2;
	}

	static int sum(int[] values)
	{
		int sum = 0;
		int length = values.length;
		for (int i = 1; i < length; i++)
		{
			DoubleSlot slot = new DoubleSlot(values[i - 1], values[i]);
			sum += slotValue(slot);
		}
		return sum;
	}

	static void test(int[] values)
	{
		long start = System.nanoTime();
		int value = sum(values);
		long end = System.nanoTime();
		System.out.println("time " + (end - start) + " " + value);
	}

	public static void main(String[] args)
	{
		int[] values = new int[1000000];
		for (int i = 0; i < values.length; i++)
		{
			values[i] = i;
		}

		for (int i = 0; i < 100; i++)
			sum(values);

		for (int i = 0; i < 100; i++)
			test(values);
	}
}
