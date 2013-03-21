package ist.meic.pa;

import java.util.ArrayList;
import java.util.TreeMap;

public class ArrayAdvisor {
	
	TreeMap<Object, ArrayList<Integer>> initVariables = new TreeMap<Object, ArrayList<Integer>>();

	public static int readInt(Object array, int index) {
		return ((int[])array)[index];
	}

	public static void writeInt(Object array, int index, int element) {
		((int[])array)[index] = element;
	}

	public static long readLong(Object array, int index) {
		return ((long[])array)[index];
	}

	public static void writeLong(Object array, int index, long element) {
		((long[])array)[index] = element;
	}

	public static short readShort(Object array, int index) {
		return ((short[])array)[index];
	}

	public static void writeShort(Object array, int index, short element) {
		((short[])array)[index] = element;
	}

	public static double readDouble(Object array, int index) {
		return ((double[])array)[index];
	}

	public static void writeDouble(Object array, int index, double element) {
		((double[])array)[index] = element;
	}

	public static float readFloat(Object array, int index) {
		return ((float[])array)[index];
	}

	public static void writeFloat(Object array, int index, float element) {
		((float[])array)[index] = element;
	}

	public static byte readByteOrBoolean(Object array, int index) {
		return ((byte[])array)[index];
	}

	public static void writeByteOrBoolean(Object array, int index, byte element) {
		((byte[])array)[index] = element;
	}

	public static char readChar(Object array, int index) {
		return ((char[])array)[index];
	}

	public static void writeChar(Object array, int index, char element) {
		((char[])array)[index] = element;
	}

	public static Object readObject(Object array, int index) {
		return ((Object[])array)[index];
	}

	public static void writeObject(Object array, int index, Object element) {
		((Object[])array)[index] = element;
	}

}