package edu.teco.naivebayes;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import edu.teco.naivebayes.classifier.NaiveBayesClassifier;
import edu.teco.naivebayes.classifier.NaiveBayesModel;
import edu.teco.naivebayes.data.Dataset;
import edu.teco.naivebayes.data.Row;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	private SensorManager sensorManager;
	private NaiveBayesModel model;
	private Dataset data;
	private Timer timer;
	private NaiveBayesClassifier classifier;
	
	private double ax, ay, az, gx, gy, gz, ox, oy, oz;
	
	private final String MODEL_FILENAME = "naiveBayesModel.bin";
	private final String DATA_FILENAME = "sensoroutput.txt";
	
	class NaiveBayesLoader extends AsyncTask<Void, Void, Void> {
		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this, "Please wait",
					"Loading model and dataset...", true, false);
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				model = NaiveBayesModel.loadModel(getAssets().open(MODEL_FILENAME));
				data = new Dataset(getAssets().open(DATA_FILENAME));
				classifier = new NaiveBayesClassifier(model);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			startSensorReading();
		}
	}
	
	class SensorTask extends TimerTask {
		
		private CheckBox checkClassifySensor;
		
		@Override
		public void run() {
			if (checkClassifySensor == null) {
				checkClassifySensor = (CheckBox) findViewById(R.id.checkClassifySensor);
			}
			final String result;
			if (checkClassifySensor.isChecked()) {
				Row row = Dataset.createRow(ax, ay, az, gx, gy, gz, ox, oy, oz);
				result = classifier.classify(data, row);
			} else {
				result = "";
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					TextView textSensor = (TextView) findViewById(R.id.textSensor);
					textSensor.setText("ax=" + ax + "\nay=" + ay + "\naz=" + az + "\n"+ "gx=" + gx + "\ngy=" + gy + "\ngz=" + gz + "\n"+ "ox=" + ox + "\noy=" + oy + "\noz=" + oz);
					if (!result.equals("")) {
						TextView textResult = (TextView) findViewById(R.id.textResult);
						textResult.setText(result);
					}
				}
			});
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonClassify = (Button) findViewById(R.id.buttonClassify);
        buttonClassify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				double result = classifier.test(data);
				TextView textResult = (TextView) findViewById(R.id.textResult);
				textResult.setText("Classification result: " + result);
			}
		});


        Button buttonClassifyRandom = (Button) findViewById(R.id.buttonClassifyRandom);
        buttonClassifyRandom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = classifier.testRandom(data);
				TextView textResult = (TextView) findViewById(R.id.textResult);
				textResult.setText(result);
			}
		});
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    	if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
    		ax = event.values[0];
    		ay = event.values[1];
    		az = event.values[2];
    	} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
    		gx = event.values[0];
    		gy = event.values[1];
    		gz = event.values[2];
    	} else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
    		ox = event.values[0];
    		oy = event.values[1];
    		oz = event.values[2];
    	}
    }

    private void startSensorReading() {
    	if (sensorManager == null) {
    		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	}
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
        timer = new Timer();
        timer.schedule(new SensorTask(), 1000, 1000);
    }
    
    @Override
    protected void onResume() {
      super.onResume();
      if ((model == null) && (data == null)) {
    	  new NaiveBayesLoader().execute();
      } else {
    	  startSensorReading();
      }
    }

    @Override
    protected void onPause() {
      // unregister listener
      super.onPause();
      if (sensorManager != null) {
    	  sensorManager.unregisterListener(this);
      }
      if (timer != null) {
    	  timer.cancel();
    	  timer = null;
      }
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
