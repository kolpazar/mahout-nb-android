package edu.teco.naivebayes.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Dataset {

	private List<Row> rows;
	private List<String> labels;
	
	public Dataset(InputStream stream) throws IOException {
		rows = new LinkedList<Row>();
		labels = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String wholeLine;
		String[] lines = null;
		String[] parts = null;
		double[] values = null;
		
		while ((wholeLine = reader.readLine()) != null) {
			lines = wholeLine.split(";");
			for (String line: lines) {
				parts = line.split(",");
				if (parts.length != 13) {
					continue;
				}
				String label = parts[1];
				values = extractValues(parts);
				Row row = new Row(6);
				if (!labels.contains(label)) {
					labels.add(label);
				}
				row.setLabel(labels.indexOf(label));
				row.setFeature(0, calculateAverage(values, 0, 3));
				row.setFeature(1, calculateAverage(values, 3, 3));
				row.setFeature(2, calculateAverage(values, 6, 3));
				
				row.setFeature(3, calculateVariance(values, 0, 3));
				row.setFeature(4, calculateVariance(values, 3, 3));
				row.setFeature(5, calculateVariance(values, 6, 3));
				
				rows.add(row);
			}
		}
	}
	
	private double[] extractValues(String[] parts) {
		double[] result = new double[9];
		result[0] = Double.parseDouble(parts[3]);
		result[1] = Double.parseDouble(parts[4]);
		result[2] = Double.parseDouble(parts[5]);
		result[3] = Double.parseDouble(parts[6]);
		result[4] = Double.parseDouble(parts[7]);
		result[5] = Double.parseDouble(parts[8]);
		result[6] = Double.parseDouble(parts[10]);
		result[7] = Double.parseDouble(parts[11]);
		result[8] = Double.parseDouble(parts[12]);
		return result;
	}
	
	private static double calculateAverage(double[] values, int index, int length) {
		double result = 0;
		for (int i = index; i < index + length; i++) {
			result += values[i];
		}
		return result / length;
	}
	
	private static double calculateVariance(double[] values, int index, int length) {
		double result = 0;
		double average = calculateAverage(values, index, length);
		for (int i = index; i < index + length; i++) {
			result += (values[i] - average) * (values[i] - average); 
		}
		return result / length;
	}
	
	public int size() {
		return rows.size();
	}
	
	public Row getRow(int index) {
		return rows.get(index);
	}
	
	public String getLabelByIndex(int index) {
		return labels.get(index);
	}

	public static Row createRow(double ax, double ay, double az, double gx,
			double gy, double gz, double ox, double oy, double oz) {
		Row row = new Row(6);
		row.setFeature(0, calculateAverage(new double[]{ax, ay, az}, 0, 3));
		row.setFeature(0, calculateAverage(new double[]{gx, gy, gz}, 0, 3));
		row.setFeature(0, calculateAverage(new double[]{ox, oy, oz}, 0, 3));
		row.setFeature(3, calculateVariance(new double[]{ax, ay, az}, 0, 3));
		row.setFeature(4, calculateVariance(new double[]{gx, gy, gz}, 0, 3));
		row.setFeature(5, calculateVariance(new double[]{ox, oy, oz}, 0, 3));
		return row;
	}
}
