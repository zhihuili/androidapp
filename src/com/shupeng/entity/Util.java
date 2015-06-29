package com.shupeng.entity;

import java.util.List;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

public class Util {
	 Context context;
	 public static boolean flag;

	/**
	 * @param args
	 */
	public static String logStringCache="";
	/**
	 * TTS 文字转语音
	 * @param mWord  ：要转换的文字
	 */
	public  void SpeakWord(String mWord){
		 SpeechSynthesizer  mSpeechSynthesizer=new SpeechSynthesizer(context,"holder",new SpeechSynthesizerListener() {
			
			@Override
			public void onSynthesizeFinish(SpeechSynthesizer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartWorking(SpeechSynthesizer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSpeechStart(SpeechSynthesizer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSpeechResume(SpeechSynthesizer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSpeechProgressChanged(SpeechSynthesizer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSpeechPause(SpeechSynthesizer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSpeechFinish(SpeechSynthesizer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNewDataArrive(SpeechSynthesizer arg0, byte[] arg1,
					boolean arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(SpeechSynthesizer arg0, SpeechError arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCancel(SpeechSynthesizer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onBufferProgressChanged(SpeechSynthesizer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		 mSpeechSynthesizer.setApiKey(
					"AZV5g8jKngGZ8lKXlSi2S7I2",
					"6f0bf5dc3509d67bb25a8334da7c2a1b");
			mSpeechSynthesizer.speak(mWord.toString());
	}
	/**
	 * 判断当前应用是否正在运行
	 */
	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100); 
		for (RunningTaskInfo info : list) { 
		    if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) { 
		        return true;
		    } 
		    else return false;
		}
		return false;
	}
}
		