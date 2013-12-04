package edu.teco.naivebayes.data;

import edu.teco.naivebayes.math.Vector;

public class Row {

	private int label;
	private Vector features;
	
	public Row(int numFeatures) {
		features = new Vector(numFeatures);
	}
	
	public void setLabel(int label) {
		this.label = label;
	}
	
	public void setFeature(int index, double value) {
		features.set(index, value);
	}

	public int getLabel() {
		return label;
	}

	public Vector getFeatures() {
		return features;
	}
	
}
