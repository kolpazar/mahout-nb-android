package edu.teco.naivebayes.classifier;

import edu.teco.naivebayes.data.Dataset;
import edu.teco.naivebayes.data.Row;
import edu.teco.naivebayes.math.Vector;

public class NaiveBayesClassifier {

	private NaiveBayesModel model;

	public NaiveBayesClassifier(NaiveBayesModel model) {
		this.model = model;
	}

	public double test(Dataset data) {
		Vector r = model.createScoringVector();
		Row row;
		int resultCorrect = 0;
		int resultIncorrect = 0;
		for (int i = 0; i < data.size(); i++) {
			row = data.getRow(i);
			r = classifyFull(r, row.getFeatures());
			if (((r.get(0) >= r.get(1)) && (row.getLabel() == 0)) ||
					((r.get(1) >= r.get(0)) && (row.getLabel() == 1))) {
				resultCorrect++;
			} else {
				resultIncorrect++;
			}
		}
		return (100.0 * resultCorrect) / (resultCorrect + resultIncorrect);
	}

	public String testRandom(Dataset data) {
		String s = "Classified as: ";
		Vector r = model.createScoringVector();
		int rowIndex = (int) (Math.random() * data.size());
		Row row = data.getRow(rowIndex);
		r = classifyFull(r, row.getFeatures());
		int classifiedLabel = (r.get(0) >= r.get(1)) ? 0 : 1;
		s += data.getLabelByIndex(classifiedLabel);
		s += "\n" + ((classifiedLabel == row.getLabel()) ? "CORRECT" : "INCORRECT");
		return s;
	}

	protected NaiveBayesModel getModel() {
		return model;
	}

	public double getScoreForLabelFeature(int label, int feature) {
		NaiveBayesModel model = getModel();
		return computeWeight(model.weight(label, feature), model.labelWeight(label), model.alphaI(),
				model.numFeatures());
	}

	public static double computeWeight(double featureLabelWeight, double labelWeight, double alphaI,
			double numFeatures) {
		double numerator = featureLabelWeight + alphaI;
		double denominator = labelWeight + alphaI * numFeatures;
		return Math.log(numerator / denominator);
	}
	
	protected double getScoreForLabelInstance(int label, Vector instance) {
		double result = 0.0;
		for (Integer i: instance.nonZeroes()) {
			result += instance.get(i) * getScoreForLabelFeature(label, i);
		}
		return result;
	}

	public int numCategories() {
		return model.numLabels();
	}

	public Vector classifyFull(Vector instance) {
		return classifyFull(model.createScoringVector(), instance);
	}

	public Vector classifyFull(Vector r, Vector instance) {
		for (int label = 0; label < model.numLabels(); label++) {
			r.set(label, getScoreForLabelInstance(label, instance));
		}
		return r;
	}

}
