package edu.teco.naivebayes.classifier;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.teco.naivebayes.math.Matrix;
import edu.teco.naivebayes.math.Vector;

public class NaiveBayesModel {

	private final Vector weightsPerLabel;
	private final Vector perlabelThetaNormalizer;
	//  private final double minThetaNormalizer;
	private final Vector weightsPerFeature;
	private final Matrix weightsPerLabelAndFeature;
	private final float alphaI;
	private final double numFeatures;
	private final double totalWeightSum;

	public NaiveBayesModel(Matrix weightMatrix,
			Vector weightsPerFeature,
			Vector weightsPerLabel,
			Vector thetaNormalizer,
			float alphaI) {
		this.weightsPerLabelAndFeature = weightMatrix;
		this.weightsPerFeature = weightsPerFeature;
		this.weightsPerLabel = weightsPerLabel;
		this.perlabelThetaNormalizer = thetaNormalizer;
		this.numFeatures = weightsPerFeature.getSize();
		this.totalWeightSum = weightsPerLabel.zSum();
		this.alphaI = alphaI;
	}

	public double labelWeight(int label) {
		return weightsPerLabel.get(label);
	}

	public double featureWeight(int feature) {
		return weightsPerFeature.get(feature);
	}

	public double weight(int label, int feature) {
		return weightsPerLabelAndFeature.get(label, feature);
	}

	public float alphaI() {
		return alphaI;
	}

	public double numFeatures() {
		return numFeatures;
	}

	public double totalWeightSum() {
		return totalWeightSum;
	}

	public int numLabels() {
		return weightsPerLabel.getSize();
	}

	public Vector createScoringVector() {
		return weightsPerLabel.like();
	}

	public static NaiveBayesModel loadModel(InputStream stream) {
		Vector weightsPerLabel = null;
		Vector perLabelThetaNormalizer = null;
		Vector weightsPerFeature = null;
		Matrix weightsPerLabelAndFeature = null;
		float alphaI = 0;

		try {
			DataInputStream in = new DataInputStream(stream);
			alphaI = in.readFloat();
			weightsPerFeature = new Vector(in);
			weightsPerLabel = new Vector(in);
			perLabelThetaNormalizer = new Vector(in);

			weightsPerLabelAndFeature = new Matrix(weightsPerLabel.getSize(), weightsPerFeature.getSize());
			for (int label = 0; label < weightsPerLabelAndFeature.numRows(); label++) {
				weightsPerLabelAndFeature.assignRow(label, new Vector(in));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new NaiveBayesModel(weightsPerLabelAndFeature, weightsPerFeature, weightsPerLabel,
				perLabelThetaNormalizer, alphaI);
	}
}
