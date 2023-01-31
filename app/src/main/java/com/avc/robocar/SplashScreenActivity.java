package com.avc.robocar;
import android.support.v7.app.*;
import android.os.*;
import android.content.*;

public class SplashScreenActivity extends AppCompatActivity
{
	AppCompatActivity ac;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//setContentView(R.drawable.splash_layout);
		ac = this;
		new Thread(){
			public void run(){
				try{
					startActivity(new Intent(ac,MainActivity.class));
					finish();
				}catch(Exception e){}
			}
		}.start();
		
	}
	
}
