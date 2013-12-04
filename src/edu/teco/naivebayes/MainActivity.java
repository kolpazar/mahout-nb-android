package edu.teco.naivebayes;

import java.io.IOException;

import edu.teco.naivebayes.classifier.NaiveBayesClassifier;
import edu.teco.naivebayes.classifier.NaiveBayesModel;
import edu.teco.naivebayes.data.Dataset;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private NaiveBayesModel model;
	private Dataset data;
	
	private final String MODEL_FILENAME = "naiveBayesModel.bin";
	private final String DATA_FILENAME = "sensoroutput.txt";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonLoadModel = (Button) findViewById(R.id.buttonLoadModel);
        buttonLoadModel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					model = NaiveBayesModel.loadModel(getAssets().open(MODEL_FILENAME));
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		});

        Button buttonLoadData = (Button) findViewById(R.id.buttonLoadData);
        buttonLoadData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					data = new Dataset(getAssets().open(DATA_FILENAME));
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		});

        Button buttonClassify = (Button) findViewById(R.id.buttonClassify);
        buttonClassify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NaiveBayesClassifier classifier = new NaiveBayesClassifier(model);
				double result = classifier.test(data);
				TextView textResult = (TextView) findViewById(R.id.textResult);
				textResult.setText("Classification result: " + result);
			}
		});


        Button buttonClassifyRandom = (Button) findViewById(R.id.buttonClassifyRandom);
        buttonClassifyRandom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NaiveBayesClassifier classifier = new NaiveBayesClassifier(model);
				String result = classifier.testRandom(data);
				TextView textResult = (TextView) findViewById(R.id.textResult);
				textResult.setText(result);
			}
		});
    }
    

}
