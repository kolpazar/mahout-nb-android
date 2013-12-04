package edu.teco.naivebayes.math;

public class Matrix {

	private int rowSize;
	private int columnSize;
	
	private Vector[] values;
	
	public Matrix(int rowSize, int columnSize) {
		this.rowSize = rowSize;
		this.columnSize = columnSize;
		values = new Vector[rowSize];
	}
	
	public double get(int rowIndex, int columnIndex) {
		return values[rowIndex].get(columnIndex);
	}
	
	public int numRows() {
		return rowSize;
	}
	
	public int numColumns() {
		return columnSize;
	}
	
	public void assignRow(int rowIndex, Vector vector) {
		values[rowIndex] = vector;
	}
}
