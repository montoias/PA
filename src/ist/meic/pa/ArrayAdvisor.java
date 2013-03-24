package ist.meic.pa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.WeakHashMap;

public class ArrayAdvisor {
	
	private static WeakHashMap<Object, ArrayList<Integer>> initVariables = new WeakHashMap<Object, ArrayList<Integer>>();

	public static int arrayReadInt(Object array, int index) {
		getPositionInitialized(array, index);
		return ((int[])array)[index];
	}

	public static void arrayWriteInt(Object array, int index, int element) {
		setPositionInitialized(array, index);
		((int[])array)[index] = element;
	}

	public static long arrayReadLong(Object array, int index) {
		getPositionInitialized(array, index);
		return ((long[])array)[index];
	}

	public static void arrayWriteLong(Object array, int index, long element) {
		setPositionInitialized(array, index);
		((long[])array)[index] = element;
	}

	public static short arrayReadShort(Object array, int index) {
		getPositionInitialized(array, index);
		return ((short[])array)[index];
	}

	public static void arrayWriteShort(Object array, int index, short element) {
		setPositionInitialized(array, index);
		((short[])array)[index] = element;
	}

	public static double arrayReadDouble(Object array, int index) {
		getPositionInitialized(array, index);
		return ((double[])array)[index];
	}

	public static void arrayWriteDouble(Object array, int index, double element) {
		setPositionInitialized(array, index);
		((double[])array)[index] = element;
	}

	public static float arrayReadFloat(Object array, int index) {
		getPositionInitialized(array, index);
		return ((float[])array)[index];
	}

	public static void arrayWriteFloat(Object array, int index, float element) {
		setPositionInitialized(array, index);
		((float[])array)[index] = element;
	}

	public static byte arrayReadByteOrBoolean(Object array, int index) {
		getPositionInitialized(array, index);
		return ((byte[])array)[index];
	}

	public static void arrayWriteByteOrBoolean(Object array, int index, byte element) {
		setPositionInitialized(array, index);
		((byte[])array)[index] = element;
	}

	public static char arrayReadChar(Object array, int index) {
		getPositionInitialized(array, index);
		return ((char[])array)[index];
	}

	public static void arrayWriteChar(Object array, int index, char element) {
		setPositionInitialized(array, index);
		((char[])array)[index] = element;
	}

	public static Object arrayReadObject(Object array, int index) {
		getPositionInitialized(array, index);
		return ((Object[])array)[index];
	}

	public static void arrayWriteObject(Object array, int index, Object element) {
		setPositionInitialized(array, index);
		((Object[])array)[index] = element;
	}
	
	private static void setPositionInitialized(Object array, int index) {
		if(!initVariables.containsKey(array))
			initVariables.put(array, new ArrayList<Integer>());
		initVariables.get(array).add(index);
	}
	
	private static void getPositionInitialized(Object array, int index) {
		if(!initVariables.containsKey(array))
			throw new NullPointerException("Array has not been initialized");
		if(!initVariables.get(array).contains(index))
			throw new RuntimeException("Position " + index + " of array " + array + " has not been initialized");
	}

}