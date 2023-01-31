package com.avc.robocar;
import android.app.*;
import android.os.*;
import android.support.v7.app.*;
import android.bluetooth.*;
import java.util.*;
import android.widget.*;
import android.support.v7.widget.*;
import android.view.*;

public class DeviceDialog extends Dialog
{
	private AppCompatActivity activity;
	private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	private List<String> deviceString = new ArrayList<String>();
	private RecyclerView rcView;
	private Button find;
	private Bluetooth blue;
	private DeviceAdapter adapter;
	private ProgressBar bar;
	DeviceDialog(AppCompatActivity a,Bluetooth b){
		super(a);
		activity = a;
		//devices = de;
		blue = b;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_selector);
		getWindow().setLayout(500,WindowManager.LayoutParams.WRAP_CONTENT);
		find = findViewById(R.id.device_selectorFind);
		bar = findViewById(R.id.device_selectorProgressBar);
		rcView = findViewById(R.id.device_selectorRecyclerView);
		adapter = new DeviceAdapter(activity,devices);
		rcView.setLayoutManager(new LinearLayoutManager(activity));
		for(BluetoothDevice dt: blue.list()) devices.add(dt);
		rcView.setAdapter(adapter);
		bar.setVisibility(View.GONE);
		adapter.setOnDeviceSelectedListener(new DeviceAdapter.OnDeviceSelectedListener(){
			public void onSelected(BluetoothDevice device){
				blue.pairDevice(device);
				blue.connect(device);
			}
		});
		find.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				blue.scan();
			}
		});
		blue.setOnDeviceDiscoverListener(new Bluetooth.OnDeviceDiscoveredListener(){
			public void onDiscover(BluetoothDevice device){
				devices.add(device);
				deviceString.add(device.getName());
				adapter.updateData(devices);
				bar.setVisibility(View.GONE);
				rcView.setVisibility(View.VISIBLE);
				//rcView.swapAdapter(adapter,true);
			}
			public void onStart(){
				bar.setVisibility(View.VISIBLE);
				rcView.setVisibility(View.GONE);
			}
		});
		
		//for(BluetoothDevice dt : devices) deviceString.add(dt.getName());
		/*adapter = new ArrayAdapter(activity,android.R.layout.simple_list_item_1,deviceString);
		
		listView.setAdapter(adapter);*/
	}
	
}
