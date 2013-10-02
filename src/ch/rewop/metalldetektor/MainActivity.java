package ch.rewop.metalldetektor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager sensorManager;
	private Sensor magnetSensor;
	private int	messwert;
	private TextView anzeigeFeld;
	private ProgressBar anzeigeBar;
	
	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.add("In Logbuch eintragen");
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String qrCode = intent.getStringExtra("SCAN_RESULT");
				Toast.makeText(this, qrCode, Toast.LENGTH_LONG).show();
				sendlog(qrCode);
			}
		}
	}
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        anzeigeFeld = (TextView) findViewById(R.id.sensorData);
        anzeigeBar = (ProgressBar) findViewById(R.id.sensorBar);

        try{
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetSensor = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
        }
        catch(Exception e) {
        	Log.d(SENSOR_SERVICE, e.getMessage());
        	Toast.makeText(this, "No Magnetic-Field Sensor detected :0 - are you using an emulator?", Toast.LENGTH_LONG).show();
        }        
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
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		messwert = (int) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
		anzeigeFeld.setText("MagnetFeld: " + messwert + " mikroTesla");
		anzeigeBar.setProgress(messwert);
	}
	
	
	//eintrag in logbuch
	private void sendlog(String qrCode) {
		Intent intent = new Intent("ch.appquest.intent.LOG");
		 
		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
		Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
		return;
		}
		
		EditText codeText = (EditText) findViewById(R.id.codeEdit);
		String code = codeText.getText().toString();
		 
		intent.putExtra("ch.appquest.taskname", "REWOP.Metall-Detektor");
		intent.putExtra("ch.appquest.logmessage", qrCode + ": " + code);
		Toast.makeText(this, qrCode + ": " + code, Toast.LENGTH_LONG).show();
		
		 
		startActivity(intent);
		
	}
    
}
