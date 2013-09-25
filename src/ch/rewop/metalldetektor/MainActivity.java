package ch.rewop.metalldetektor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager sensorManager;
	private Sensor magnetSensor;
	private int	messwert;
	private TextView anzeigeFeld;
	private ProgressBar anzeigeBar;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        anzeigeFeld = (TextView) findViewById(R.id.sensorData);
        anzeigeBar = (ProgressBar) findViewById(R.id.progressBar1);

        
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetSensor = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
        
        anzeigeBar.setMax(600);
        System.out.println("" + magnetSensor.getMaximumRange());
        //sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
        

      
        //anzeigeFeld.setText(magnetSensor);
        
    }
    
    protected void onPause(){
    	super.onPause();
    	sensorManager.unregisterListener(this);
    }
    
    protected void onResume(){
    	super.onResume();
    	
    	sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		messwert = (int) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
		anzeigeFeld.setText("MagnetFeld: " + messwert + " mikroTesla");
		anzeigeBar.setProgress((int) messwert);
	}
    
}
