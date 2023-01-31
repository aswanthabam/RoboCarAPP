package com.avc.robocar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.*;
import android.view.*;
import android.support.v7.app.*;
import android.bluetooth.*;

import java.util.*;

import android.widget.*;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
	private AppCompatActivity activity;
	private List<BluetoothDevice> devices;
	private OnDeviceSelectedListener listener;

	DeviceAdapter(AppCompatActivity a, List<BluetoothDevice> d) {
		activity = a;
		devices = d;
	}

	@Override
	public void onBindViewHolder(DeviceAdapter.ViewHolder p1, int p2) {
		// TODO: Implement this method

		p1.text.setText(devices.get(p2).getName());

	}

	@Override
	public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder( LayoutInflater.from(activity).inflate(R.layout.device_item_view,p1,false));
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return devices == null ? 0 : devices.size();
	}
	public void updateData(List<BluetoothDevice> n){
		devices = n;
		notifyDataSetChanged();
	}
	public class ViewHolder extends RecyclerView.ViewHolder{
		TextView text;
		ViewHolder(View v){
			super(v);
			text = v.findViewById(R.id.device_item_viewTextView);
			v.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(listener != null) listener.onSelected(devices.get(getAdapterPosition()));
				}
			});
		}
	}
	
	public void setOnDeviceSelectedListener(OnDeviceSelectedListener l){listener = l;}
	public interface OnDeviceSelectedListener{
		public void onSelected(BluetoothDevice device);
	}
}
