package com.avc.robocar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.*;
import android.widget.*;
import android.view.*;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

import android.util.*;

public class Bluetooth {
	public static final String UUID_String = "00000000-0000-1000-8000-00805F9B34FB";
	private BluetoothAdapter ba;
	private AppCompatActivity activity;
	private Set<BluetoothDevice> pairedDevices;
	public List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
	public List<String> devices = new ArrayList<String>();
	private OnDeviceDiscoveredListener listener;
	private OnStateChangedListener state_listener;
	public BluetoothSocket socket;
	public boolean IS_CONNECTED = false;
	public String CONNECTED_DEVICE = null;

	Bluetooth(AppCompatActivity a) {
		activity = a;
		BluetoothManager bluetoothManager = a.getSystemService(BluetoothManager.class);
		ba = bluetoothManager.getAdapter();
		if (ba == null) {
			// Device doesn't support Bluetooth
			Toast.makeText(a, "This.device doesnt support Bluetooth", Toast.LENGTH_SHORT).show();
		}
		//ba = BluetoothAdapter.getDefaultAdapter();

		// Register for broadcasts when a device is discovered.
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		a.registerReceiver(receiver, filter);
	}

	public BluetoothAdapter getAdapter() {
		return ba;
	}

	public boolean send(String data) {
		Toast.makeText(activity, "Sending : " + data, Toast.LENGTH_SHORT).show();
		if (!IS_CONNECTED) return false;
		try {
			socket.getOutputStream().write(data.getBytes());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// MainActivity.vControler.speak("Oops! an error occured",true);
			Toast.makeText(activity, "Oops! an error occured", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@SuppressLint("MissingPermission")
	public void on() {
		if (!ba.isEnabled()) {
			Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(turnOn, 0);
			Toast.makeText(activity.getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(activity.getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
		}
	}

	@SuppressLint("MissingPermission")
	public void off() {
		ba.disable();
		Toast.makeText(activity, "Bluetooth turned off", Toast.LENGTH_LONG).show();
	}

	@SuppressLint("MissingPermission")
	public void visible() {
		Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		activity.startActivityForResult(getVisible, 0);
	}

	@SuppressLint("MissingPermission")
	public boolean scan() {
		if (listener != null) listener.onStart();
		if (ba.startDiscovery()) {
			Toast.makeText(activity, "Discovering ...", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			Toast.makeText(activity, "Unable to discover devices", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@SuppressLint("MissingPermission")
	public Set<BluetoothDevice> list() {

		pairedDevices = ba.getBondedDevices();

		for (BluetoothDevice bt : pairedDevices) devices.add(bt.getName());

		// Toast.makeText(activity.getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

		return pairedDevices;
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Discovery has found a device. Get the BluetoothDevice
				// object and its info from the Intent.
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				discoveredDevices.add(device);
				//String deviceName = device.getName();
				//String deviceHardwareAddress = device.getAddress(); // MAC address
				if (listener != null) listener.onDiscover(device);
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (state) {
					case BluetoothAdapter.STATE_ON:
						if (state_listener != null) state_listener.onTurnON();
						break;
					case BluetoothAdapter.STATE_OFF:
						if (state_listener != null) state_listener.onTurnOFF();
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						if (state_listener != null) state_listener.onTurningON();
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						if (state_listener != null) state_listener.onTurningOFF();
						break;
				}
			} else if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
				switch (state) {
					case BluetoothAdapter.STATE_CONNECTING:
						if (state_listener != null) state_listener.onConnecting();
						break;
					case BluetoothAdapter.STATE_CONNECTED:
						if (state_listener != null) state_listener.onConnected();
						break;
				}
			}
			else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				if (state_listener != null) state_listener.onConnected();
			}
		}
	};

	public void setOnDeviceDiscoverListener(OnDeviceDiscoveredListener l) {
		listener = l;
	}

	public interface OnDeviceDiscoveredListener {
		void onDiscover(BluetoothDevice device);

		void onStart();
	}

	public void setOnStateChangedListener(OnStateChangedListener l) {
		state_listener = l;
	}

	public interface OnStateChangedListener {
		void onTurnON();

		void onTurnOFF();

		void onTurningON();

		void onTurningOFF();

		void onConnecting();

		void onConnected();
	}

	public void connect(final BluetoothDevice device) {
		Toast.makeText(activity, "Connecting ....", Toast.LENGTH_SHORT).show();
		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
			activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN},9999);
			return;
		}
		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
			activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT},9999);
			return;
		}
		ba.cancelDiscovery();
		try {
			UUID uuid = device.getUuids()[0].getUuid();
			socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
			Log.i("BLUETOOTH_SOCKET", "Socket Creaetd");
		} catch (IOException e) {
			Log.e("BLUETOOTH_SOCKET", "Error creating socket  :" + e);
			Toast.makeText(activity, "Error creating socket", Toast.LENGTH_SHORT).show();
		}
		new Thread() {
			@SuppressLint("MissingPermission")
			public void run() {
				try {
					socket.connect();
					IS_CONNECTED = true;
					CONNECTED_DEVICE = device.getAddress();
					MainActivity.toast(activity,"Connected to "+device.getName());
				}catch(IOException e){
					Log.e("BLUETOOTH_ERROR","Error connecting. caused because of : "+e);
					e.printStackTrace();
					MainActivity.toast(activity,"Unable to connect ");
				}
			}	
		}.start();
	}
	
	public void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
			IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			activity.registerReceiver(mPairReceiver, intent);
        } catch (Exception e) {
			Toast.makeText(activity,"Unable to pair", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
	
	private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
				final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

				if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
					Toast.makeText(activity,"Paired", Toast.LENGTH_SHORT).show();
				} else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
					Toast.makeText(activity,"Unpaired", Toast.LENGTH_SHORT).show();
				}

            }
        }
    };
}
