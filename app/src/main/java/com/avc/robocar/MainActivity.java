package com.avc.robocar;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.support.v7.app.*;
import android.content.*;
import android.bluetooth.*;
import android.support.v4.content.*;
import android.*;
import android.content.pm.*;
import android.support.v4.app.*;
import android.widget.CompoundButton.*;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener
{
	private Button right,left,front,back,brake;
	private RadioGroup mode;
	private CheckBox voice;
	private Bluetooth bluetooth;
	private LinearLayout status;
	private AppCompatActivity activity;
	private TextView textStatus;
	private ImageView textImg;
	public static VoiceControl vControler;
	private int state = 1;
	
	public static final int BLUETOOTH_OFF = 1;
	public static final  int BLUETOOTH_ON = 2;
	public static final  int BLUETOOTH_CONNECTED = 3;
	public static final  int BLUETOOTH_NOT_CONNECTED = 4;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
		bluetooth = new Bluetooth(this);
		activity = this;
		right = findViewById(R.id.btn_right);
		left = findViewById(R.id.btn_left);
		front = findViewById(R.id.btn_forward);
		back = findViewById(R.id.btn_backward);
		brake = findViewById(R.id.btn_brake);
		//gear = findViewById(R.id.check_gear);
		voice = findViewById(R.id.btn_voice);
		status = findViewById(R.id.linear_status);
		textStatus = findViewById(R.id.text_status);
		textImg = findViewById(R.id.text_statusImg);
		mode = findViewById(R.id.mainRadioGroup);
		vControler = new VoiceControl(this,bluetooth);
		
		mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup p1,int id){
				itemSelection(id);
			}
		});
		//selector = findViewById(R.id.spinner_selector);
		if(!bluetooth.getAdapter().isEnabled()){
			textStatus.setText("Turn on bluetooth");
			state = BLUETOOTH_OFF;
		}
		else{
			state = BLUETOOTH_ON;
			textStatus.setText("Not connected");
		}
		bluetooth.setOnStateChangedListener(new Bluetooth.OnStateChangedListener(){
			public void onTurnON(){
				state = BLUETOOTH_ON;
				textStatus.setText("Not Connected");
				setStatus(R.color.red);
			}
			public void onTurnOFF(){
				state = BLUETOOTH_OFF;
				textStatus.setText("Turn on bluetooth");
				setStatus(R.color.red);
			}
			public void onTurningON(){
				state = -1;
				textStatus.setText("Turning on bluetooth ...");
				setStatus(R.color.orange);
			}
			public void onTurningOFF(){
				state = -1;
				textStatus.setText("Turning off bluetooth ...");
				setStatus(R.color.orange);
			}
			public void onConnecting(){
				state = -1;
				textStatus.setText("Connecting ...");
				setStatus(R.color.orange);
			}
			public void onConnected(){
				state = -1;
				textStatus.setText("Connected");
				setStatus(R.color.green);
			}
		});
		
		status.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
				switch(state){
					case BLUETOOTH_OFF:
						bluetooth.on();
						break;
					case BLUETOOTH_ON:
						DeviceDialog dia = new DeviceDialog(activity,bluetooth);
						dia.show();
						break;
					case BLUETOOTH_CONNECTED:
						textStatus.setText("Connected");
						setStatus(R.color.green);
						break;
					
				}
				
			}
		});
		
		front.setOnTouchListener(this);
		back.setOnTouchListener(this);
		right.setOnTouchListener(this);
		left.setOnTouchListener(this);
		brake.setOnTouchListener(this);
		voice.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton btn,boolean che){
				if(che) itemSelection(R.id.btn_voice);
				else if(vControler.IS_LISTENING) vControler.stopListening();
			}
		});
		/*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bluetooth.list().toArray(new String[]{}));

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		
		selector.setAdapter(dataAdapter);*/
		
    }
	
	boolean voicePerm(){
		if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1000);
			}
			return false;
		}else return true;
	}
	
	void setStatus(int color){
		textImg.setColorFilter(ContextCompat.getColor(activity,color));
	}
	public void itemSelection(int id){itemSelection(id,null);}
	public void itemSelection(int id,MotionEvent event)
	{
		int action = -1;
		if(event != null) action = event.getAction();
		switch(id){
			case R.id.btn_forward:
				if(action == MotionEvent.ACTION_DOWN) bluetooth.send("F");
				else if(action == MotionEvent.ACTION_UP) bluetooth.send("X");
				//Toast.makeText(this,"tttt", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_backward:
				if(action == MotionEvent.ACTION_DOWN) bluetooth.send("B");
				else if(action == MotionEvent.ACTION_UP) bluetooth.send("X");
				break;
			case R.id.btn_right:
				if(action == MotionEvent.ACTION_DOWN) bluetooth.send("R");
				else if(action == MotionEvent.ACTION_UP) bluetooth.send("X");
				break;
			case R.id.btn_left:
				if(action == MotionEvent.ACTION_DOWN) bluetooth.send("L");
				else if(action == MotionEvent.ACTION_UP) bluetooth.send("X");
				break;
			case R.id.btn_voice:
				if(voicePerm()){
					vControler.startListening();
				}
				break;
			case R.id.btn_brake:
				if(action == MotionEvent.ACTION_DOWN) bluetooth.send("S");
				// else if(action == MotionEvent.ACTION_UP) bluetooth.send("X");
				break;
			case R.id.radio_control:
				bluetooth.send("C");
				break;
			case R.id.radio_obstacle:
				bluetooth.send("O");
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == 1000){
			if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
				itemSelection(R.id.btn_voice);
			}
		}
	}

	@Override
	public boolean onTouch(View p1,MotionEvent event)
	{
		itemSelection(p1.getId(),event);
		return true;
	}
	
	public static void toast(final Activity a,final String t){
		a.runOnUiThread(new Runnable(){
			@Override public void run(){
				Toast.makeText(a,t, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
