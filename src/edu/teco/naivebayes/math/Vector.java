package edu.teco.naivebayes.math;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Vector {

	public static final int FLAG_DENSE = 0x01;
	public static final int FLAG_SEQUENTIAL = 0x02;
	public static final int FLAG_NAMED = 0x04;
	public static final int FLAG_LAX_PRECISION = 0x08;

	private int size;
	private double[] values;
	
	public Vector(int size) {
		this.size = size;
		this.values = new double[size];
	}
	
	public Vector(DataInputStream in) throws IOException {
		int flags = in.readByte();
		boolean dense = (flags & FLAG_DENSE) != 0;
		boolean sequential = (flags & FLAG_SEQUENTIAL) != 0;
		boolean named = (flags & FLAG_NAMED) != 0;
		boolean laxPrecision = (flags & FLAG_LAX_PRECISION) != 0;

		size = Varint.readUnsignedVarInt(in);
		values = new double[size];
		if (dense) {
			for (int i = 0; i < size; i++) {
				values[i] = laxPrecision ? in.readFloat() : in.readDouble();
			}
		} else {
			int numNonDefaultElements = Varint.readUnsignedVarInt(in);
			if (sequential) {
				int lastIndex = 0;
				for (int i = 0; i < numNonDefaultElements; i++) {
					int delta = Varint.readUnsignedVarInt(in);
					int index = lastIndex + delta;
					lastIndex = index;
					values[index] = laxPrecision ? in.readFloat() : in.readDouble();
				}
			} else {
				for (int i = 0; i < numNonDefaultElements; i++) {
					int index = Varint.readUnsignedVarInt(in);
					values[index] = laxPrecision ? in.readFloat() : in.readDouble();
				}
			}
		}
	}

	public int getSize() {
		return size;
	}
	
	public double get(int index) {
		return values[index];
	}
	
	public Vector like() {
		return new Vector(size);
	}
	
	public double zSum() {
		double result = 0;
		for (int i = 0; i < size; i++) {
			result += values[i];
		}
		return result;
	}
	
	public void set(int index, double value) {
		values[index] = value;
	}
	
	public List<Integer> nonZeroes() {
		List<Integer> result = new LinkedList<Integer>();
		for (int i = 0; i < size; i++) {
			if (values[i] != 0) {
				result.add(Integer.valueOf(i));
			}
		}
		return result;
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < size; i++) {
			s += Double.toString(values[i]);
			if (i < size - 1) {
				s += ", ";
			}
		}
		return "( " + s + " )";
	}
}
