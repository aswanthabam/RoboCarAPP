package com.avc.robocar;
import android.support.v7.app.*;
import android.speech.*;
import android.content.*;
import java.util.*;
import android.os.*;
import android.util.ArrayMap;
import android.widget.*;
import android.speech.tts.*;

public class VoiceControl
{
	private Bluetooth blue;
	private AppCompatActivity activity;
	private SpeechRecognizer reco;
	private Intent speechRecognizerIntent;
	private TextToSpeech tts;
	public static boolean IS_LISTENING = false;
	private Map<String,String> dataSet = new ArrayMap<String,String>();
	//private CheckBox micButton;
	VoiceControl(AppCompatActivity a,Bluetooth b){
		dataSet.put("move forward","F");
		dataSet.put("forward","F");
		dataSet.put("go forward","F");
		dataSet.put("come forward","F");
		dataSet.put("go straight","F");
		dataSet.put("come straight","F");
		dataSet.put("move backward","B");
		dataSet.put("come backward","B");
		dataSet.put("back","B");
		dataSet.put("backward","B");
		dataSet.put("go backward","B");
		dataSet.put("go back","B");
		dataSet.put("come back","B");
		dataSet.put("turn left","T");
		dataSet.put("left","T");
		dataSet.put("move left","T");
		dataSet.put("go left","T");
		dataSet.put("turn right","P");
		dataSet.put("right","P");
		dataSet.put("move right","P");
		dataSet.put("go right","P");
		dataSet.put("brake","S");
		dataSet.put("break","S");
		dataSet.put("stop","S");
		dataSet.put("stop moving","S");
		dataSet.put("obstacle mode","O");
		dataSet.put("switch to obstacle mode","O");
		dataSet.put("control mode","C");
		dataSet.put("switch to control mode","C");
		activity = a;
		blue = b;
		//micButton = btn;
		tts = new TextToSpeech(activity, new TextToSpeech.OnInitListener() { 
			@Override
			public void onInit(int i) { 
				// if No error is found then only it will run 
				if(i!=TextToSpeech.ERROR){  
					// To Choose language of speech 
					tts.setLanguage(Locale.UK);  
				} 
			} 
		}); 
		
		reco = SpeechRecognizer.createSpeechRecognizer(a);
		reco.setRecognitionListener(new RecognitionListener(){
			@Override
			public void onResults(Bundle bundle) {
				ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				reco.stopListening();
				for(String val : data){
					if(dataSet.containsKey(val.toLowerCase())){
						blue.send(dataSet.get(val.toLowerCase()));
						speak(val,true);
						break;
					}
				}
				if(IS_LISTENING) reco.startListening(speechRecognizerIntent);
				Toast.makeText(activity,""+data, Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onBeginningOfSpeech() {
				
			}
			public void onReadyForSpeech(Bundle bundle) {}
			public void onRmsChanged(float v) {}
			public void onBufferReceived(byte[] bytes) {}
			public void onEndOfSpeech() {}
			public void onError(int i) {
				switch(i){
					case SpeechRecognizer.ERROR_AUDIO:
					case SpeechRecognizer.ERROR_NO_MATCH:
						if(IS_LISTENING){
							speak("Say it once more",true);
							reco.startListening(speechRecognizerIntent);
						}
						break;
					case SpeechRecognizer.ERROR_NETWORK:
					case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
					case SpeechRecognizer.ERROR_SERVER:
						speak("Network error",true);
						break;
					case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
						if(IS_LISTENING)reco.startListening(speechRecognizerIntent);
						break;
				}
			}
			public void onPartialResults(Bundle bundle) {}
			public void onEvent(int i, Bundle bundle) {
				
			}
			
		});
		speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
	}
	
	public void startListening(){
		//Toast.makeText(activity,"Listening...",2000).show();
		// speak("Started Listening",true);
		reco.startListening(speechRecognizerIntent);
		IS_LISTENING=true;

	}
	public void stopListening(){
		//Toast.makeText(activity,"Stoping...",2000).show();
		reco.stopListening();
		IS_LISTENING = false;
		speak("Stoped Listening",true);
	}
	public void speak(String s,boolean b){
		tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
		if(b) Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
	}
	public void speak(String s){
		tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
	}
}
