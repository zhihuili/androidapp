package com.example.android_robot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.shupeng.entity.Config;
import com.shupeng.entity.Contants;
import com.shupeng.entity.Util;

import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private static final int CHAT_ME = 0;
	private static final int CHAT_ROBOT = 1;
	private static final int CHAT_ITEM_XML[] = { R.layout.list_me,
			R.layout.list_other };
	private List<HashMap<String, Object>> mChatList = new ArrayList<HashMap<String, Object>>();
	private int CHAT_WEIGHT[] = { R.id.chatlist_image_me,
			R.id.chatlist_text_me, R.id.chatlist_image_other,
			R.id.chatlist_text_other };
	private ListView mChatList_View;
	private Context mContext;

	private String mUserId;
	private String mChannelId;
	private EditText mChatList_TvWord;
	public LocationManager lm;
	private LocationClient locationClient;
	private BDLocation mBDLocation;
	private String mVoiceToWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initWeight();// ��ʼ���ؼ�
		// initXunfeiSDK();
		initBaiduPuskSDK();// �ж�sharepred�Ƿ�Ϊ�գ�������ֱ�Ӷ�ȡ
		initSharedPreferences();
		// Intent intent=getIntent();
		// String content=intent.getStringExtra("content");
		// if(content==null){
		// return;
		// }else{
		// SpeechSynthesizer mSpeechSynthesizer=new
		// SpeechSynthesizer(MainActivity.this,
		// "holder",mSpeechSynthesizerListener );
		// mSpeechSynthesizer.setApiKey(
		// "AZV5g8jKngGZ8lKXlSi2S7I2",
		// "6f0bf5dc3509d67bb25a8334da7c2a1b");
		// mSpeechSynthesizer.speak(content.toString());
		// if(content.toString()!=""||content.toString()!=null){
		// AddChatContent(content.toString(),CHAT_ROBOT);
		// }
		// }
	}

	private void initSharedPreferences() {
		// �������ļ����˺�ȥ�ж��Ƿ�Ϊ��
		SharedPreferences mSharedPreferences = getSharedPreferences(
				"TuiSongIC", Activity.MODE_PRIVATE);
		String userId = mSharedPreferences.getString("userid", "");
		String channelId = mSharedPreferences.getString("channelid", "");
		if (userId == "" && channelId == "") {
			initBaiduPuskSDK();
			SharedPreferences mSharedPreferences_ = getSharedPreferences(
					"TuiSongIC", Activity.MODE_PRIVATE);
			mUserId = mSharedPreferences_.getString("userid", "");
			mChannelId = mSharedPreferences_.getString("channelid", "");

		} else {
			SharedPreferences mSharedPreferencesInfo = getSharedPreferences(
					"TuiSongIC", Activity.MODE_PRIVATE);
			mUserId = mSharedPreferencesInfo.getString("userid", "");
			mChannelId = mSharedPreferencesInfo.getString("channelid", "");
		}
	}

	private void initBaiduPuskSDK() {
		String pkgName = this.getPackageName();
		Resources resource = this.getResources();
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, "UNHymwX9PMI4OnxYxsUaLDVk");
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
				getApplicationContext(), resource.getIdentifier(
						"notification_custom_builder", "layout", pkgName),
				resource.getIdentifier("notification_icon", "id", pkgName),
				resource.getIdentifier("notification_title", "id", pkgName),
				resource.getIdentifier("notification_text", "id", pkgName));
		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE);
		cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		cBuilder.setLayoutDrawable(resource.getIdentifier(
				"simple_notification_icon", "drawable", pkgName));
		PushManager.setNotificationBuilder(this, 1, cBuilder);
	}

	private void BaiduVoiceSDK() {
		// TODO Auto-generated method stub
		Util.flag = true;
		BaiduASRDigitalDialog mDialog;
		Bundle params = new Bundle();
		// ���ÿ�����ƽ̨API Key
		params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
				"AZV5g8jKngGZ8lKXlSi2S7I2"); // ���ÿ�����ƽ̨Secret Key
		params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
				"6f0bf5dc3509d67bb25a8334da7c2a1b"); // ����ʶ������:���������롢��ͼ������......,��ѡ��Ĭ��Ϊ���롣
		params.putInt(BaiduASRDigitalDialog.PARAM_PROP,
				VoiceRecognitionConfig.PROP_INPUT);
		// ������������:������ͨ��,��������,Ӣ��,��ѡ��Ĭ��Ϊ������ͨ��
		params.putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
				VoiceRecognitionConfig.LANGUAGE_CHINESE);
		// params.putString(
		// BaiduASRDigitalDialog.PARAM_LANGUAGE,VoiceRecognitionConfig.LANGUAGE_ENGLISH);
		// �����Ҫ�������,�����·�����������Ϊ���벻֧��
		// params.putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE,false);
		// ���öԻ�������,��ѡ��BaiduASRDigitalDialog �ṩ�����������졢�̡���������ɫ,ÿ���� ɫ�ַ�����������ɫ������ 8
		// ������,�����߿��԰���ѡ��,ȡֵ�ο� BaiduASRDigitalDialog �� ǰ׺Ϊ THEME_�ĳ�����Ĭ��Ϊ����ɫ
		params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
				BaiduASRDigitalDialog.SPEECH_MODE_INPUT);
		mDialog = new BaiduASRDigitalDialog(MainActivity.this, params);
		mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP,
				Config.CURRENT_PROP);
		mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
				Config.getCurrentLanguage());
		mDialog.getParams().putBoolean(
				BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE,
				Config.PLAY_START_SOUND);
		mDialog.getParams().putBoolean(
				BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE,
				Config.PLAY_END_SOUND);
		mDialog.getParams().putBoolean(
				BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE,
				Config.DIALOG_TIPS_SOUND);
		mDialog.setDialogRecognitionListener(new DialogRecognitionListener() {
			@Override
			public void onResults(Bundle arg0) {
				// TODO Auto-generated method stub
				ArrayList<String> rs = arg0 != null ? arg0
						.getStringArrayList(RESULTS_RECOGNITION) : null;
				if (rs != null) { // �˴�����ʶ����,ʶ���������ж��,�����ŶȴӸߵ�������,��һ��Ԫ�������Ŷ���ߵĽ��
					String mStr = rs.toString().replace("[", "");
					mVoiceToWord = mStr.replace("]", "");
					AddChatContent(mVoiceToWord.toString(), CHAT_ME);
					locationClient = new LocationClient(getApplicationContext());
					// ���ö�λ����
					LocationClientOption option = new LocationClientOption();
					option.setOpenGps(true); // �Ƿ��GPS
					option.setCoorType("bd09ll"); // ���÷���ֵ���������͡�
					// option.setPriority(LocationClientOption.MIN_SCAN_SPAN_NETWORK);
					// //���ö�λ���ȼ�
					option.setProdName("LocationDemo"); // ���ò�Ʒ�����ơ�ǿ�ҽ�����ʹ���Զ���Ĳ�Ʒ�����ƣ����������Ժ�Ϊ���ṩ����Ч׼ȷ�Ķ�λ����
					option.setScanSpan(500); // ���ö�ʱ��λ��ʱ��������λ����
					locationClient.setLocOption(option);
					locationClient
							.registerLocationListener(new BDLocationListener() {
								@Override
								public void onReceiveLocation(BDLocation arg0) {
									if (arg0 != null) {
										System.out.println("����");
										mBDLocation = arg0;
										new ChatAsyncTask(mVoiceToWord
												.toString(), mBDLocation)
												.execute();
										locationClient.stop();
									} else {
										Toast.makeText(MainActivity.this,
												"�Բ��𣬻�ȡ��������λ����Ϣ",
												Toast.LENGTH_SHORT).show();
										return;
									}

								}
							});
					locationClient.start();
					locationClient.requestLocation();
				}
			}
		});

		mDialog.show();
	}

	private void initWeight() {
		mChatList_View = (ListView) findViewById(R.id.chat_list);
		LinearLayout mLinearSend = (LinearLayout) findViewById(R.id.send);
		mLinearSend.setOnClickListener(this);
		LinearLayout mLinearTVSend = (LinearLayout) findViewById(R.id.tv_send);
		mLinearTVSend.setOnClickListener(this);
		mChatList_TvWord = (EditText) findViewById(R.id.content);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Util.logStringCache != "") {
			Toast.makeText(MainActivity.this, "onResume", Toast.LENGTH_SHORT)
					.show();
			UpdateUI();
			Util.logStringCache = "";
		}
		super.onResume();
	}

	private void UpdateUI() {
		SpeechSynthesizer mSpeechSynthesizer = new SpeechSynthesizer(
				MainActivity.this, "holder", mSpeechSynthesizerListener);
		mSpeechSynthesizer.setApiKey("AZV5g8jKngGZ8lKXlSi2S7I2",
				"6f0bf5dc3509d67bb25a8334da7c2a1b");
		mSpeechSynthesizer.speak(Util.logStringCache.toString());
		AddChatContent(Util.logStringCache.toString(), CHAT_ROBOT);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Util.logStringCache = "";

		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			System.exit(0);
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.send:// �������
			// ����ת������
			BaiduVoiceSDK();
			break;
		case R.id.tv_send:// �������
			// ����ת������
			String mTvWord = mChatList_TvWord.getText().toString().trim();

			AddChatContent(mTvWord.toString(), CHAT_ME);
			// new ChatAsyncTask(rs.toString()).execute();
			// �����ϳ�
			SpeechSynthesizer mSpeechSynthesizer = new SpeechSynthesizer(
					MainActivity.this, "holder", mSpeechSynthesizerListener);
			mSpeechSynthesizer.setApiKey("AZV5g8jKngGZ8lKXlSi2S7I2",
					"6f0bf5dc3509d67bb25a8334da7c2a1b");
			mSpeechSynthesizer.speak(mTvWord.toString());
			AddChatContent(mTvWord.toString(), CHAT_ROBOT);
			break;
		default:
			break;
		}
	}

	// ��ȡ��ǰgps
	public Location getLocation(Context context) {
		LocationManager locMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locMan
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		Log.i("DMOE", "---location:" + location);
		return location;
	}

	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int arg0) {
			if (arg0 != ErrorCode.SUCCESS) {
				Toast.makeText(MainActivity.this, "��ʼ��ʧ�ܣ������룺" + arg0,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	// �첽���������
	class ChatAsyncTask extends AsyncTask<String, Void, String> {
		private String ANDROID = "android";
		private String mVoiceToWord;
		private String mReceiverInfo;
		private BDLocation mBDLocation;

		public ChatAsyncTask(String mVoiceToWord, BDLocation mBDLocation) {
			// TODO Auto-generated constructor stub
			this.mVoiceToWord = mVoiceToWord;
			this.mBDLocation = mBDLocation;
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				URL mUrl = new URL(Contants.ROBOT_HTTP_PATH + mUserId
						+ "&text=" + mVoiceToWord + "&gps="
						+ mBDLocation.getLongitude() + ","
						+ mBDLocation.getLatitude() + "&os=android");
				System.out.println("���ʵ�ַΪ��" + Contants.ROBOT_HTTP_PATH
						+ mUserId + "&text=" + mVoiceToWord + "&gps="
						+ mBDLocation.getLongitude() + ","
						+ mBDLocation.getLatitude() + "&os=android");
				HttpURLConnection conn = (HttpURLConnection) mUrl
						.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				byte data[] = new RobotUtil().inputStreamExchangedString(is);
				mVoiceToWord = new String(data);
				return mVoiceToWord;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				Toast.makeText(MainActivity.this, "Sorry URL Question",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result != null) {
				// ���½���
				// ת������
				// new Util().SpeakWord(result);
				SpeechSynthesizer mSpeechSynthesizer = new SpeechSynthesizer(
						MainActivity.this, "holder", mSpeechSynthesizerListener);
				mSpeechSynthesizer.setApiKey("AZV5g8jKngGZ8lKXlSi2S7I2",
						"6f0bf5dc3509d67bb25a8334da7c2a1b");
				mSpeechSynthesizer.speak(result.toString());
				AddChatContent(result.toString(), CHAT_ROBOT);

			}
		}
	}

	// ����ת�����֡��ο�Ѷ��sdk
	public String VoiceToWord() {
		RecognizerDialog iatDiaLog = new RecognizerDialog(MainActivity.this,
				null);

		iatDiaLog.setParameter(SpeechConstant.DOMAIN, "iat");
		iatDiaLog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		iatDiaLog.setParameter(SpeechConstant.ACCENT, "mandarin ");
		iatDiaLog.setListener(new RecognizerDialogListener() {

			@Override
			public void onResult(RecognizerResult arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT)
						.show();

			}

			@Override
			public void onError(SpeechError arg0) {
				// TODO Auto-generated method stub

			}
		});
		iatDiaLog.show();
		return "Hello World";
	}

	// ����������ݣ����½���ui
	public void AddChatContent(String mChatContent, int mWho) {
		HashMap<String, Object> mChatHaspMap = new HashMap<String, Object>();
		mChatHaspMap.put("Sex", mWho);
		mChatHaspMap.put("Content", mChatContent);
		mChatHaspMap.put("Head", mWho == CHAT_ME ? R.drawable.ic_launcher
				: R.drawable.ic_launcher);
		mChatList.add(mChatHaspMap);
		ChatAdapter mChatAdapter = new ChatAdapter(mContext, mChatList);
		mChatList_View.setAdapter(mChatAdapter);
		mChatAdapter.notifyDataSetChanged();
		mChatList_View.setSelection(mChatList.size() - 1);
	}

	// ������
	class ChatAdapter extends BaseAdapter {
		List<HashMap<String, Object>> mChatList;
		Context context;

		private static final int ITEM_TYPE_FROM = 0;
		private static final int ITEM_TYPE_TO = 1;
		private static final int ITEM_TYPE_COUNT = ITEM_TYPE_TO + 1;

		public ChatAdapter(Context context,
				List<HashMap<String, Object>> mChatList) {
			// TODO Auto-generated constructor stub
			this.mChatList = mChatList;
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mChatList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			int mSex = (Integer) mChatList.get(arg0).get("Sex");
			ChatViewHolder mViewHolder;
			// if(arg1==null){
			arg1 = LayoutInflater.from(MainActivity.this).inflate(
					CHAT_ITEM_XML[mSex == CHAT_ME ? 0 : 1], null);
			mViewHolder = new ChatViewHolder();
			mViewHolder.mImageView = (ImageView) arg1
					.findViewById(CHAT_WEIGHT[mSex * 2 + 0]);
			mViewHolder.mTextView = (TextView) arg1
					.findViewById(CHAT_WEIGHT[mSex * 2 + 1]);
			// arg1.setTag(mViewHolder);
			// }
			// //
			// localhost:8088/ask?userid=2545535b30537ba130b673750d38dcb12485b7aa56a3845c7fc796b36187fb46&text=hellonananizaiganma&gps=22.212,23.525,542.2&os=ios
			// else{
			// mViewHolder=(ChatViewHolder) arg1.getTag();
			mViewHolder.mTextView.setText(mChatList.get(arg0).get("Content")
					.toString());
			return arg1;

		}
	}

	class ChatViewHolder {
		ImageView mImageView;
		TextView mTextView;
	}

	SpeechSynthesizerListener mSpeechSynthesizerListener = new SpeechSynthesizerListener() {
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
		public void onError(SpeechSynthesizer arg0,
				com.baidu.speechsynthesizer.publicutility.SpeechError arg1) {
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
	};

	private void registerGPS() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// �ж�GPS�Ƿ���������
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "�뿪��GPS����...", Toast.LENGTH_SHORT).show();
			// ���ؿ���GPS�������ý���
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
			return;
		}
		String bestProvider = lm.getBestProvider(getCriteria(), true);
		Location location = lm.getLastKnownLocation(bestProvider);
		// updateView(location);
		// ����״̬
		lm.addGpsStatusListener(listener);
		// ����2��λ����Ϣ�������ڣ���λ����
		// ����3��λ�ñ仯��С���룺��λ�þ���仯������ֵʱ��������λ����Ϣ
		// ��ע������2��3���������3��Ϊ0�����Բ���3Ϊ׼������3Ϊ0����ͨ��ʱ������ʱ���£�����Ϊ0������ʱˢ��
		// 1�����һ�Σ�����Сλ�Ʊ仯����1�׸���һ�Σ�
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
				locationListener);
	}

	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(false);
		criteria.setBearingRequired(false);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}

	private LocationListener locationListener = new LocationListener() {

		/**
		 * λ����Ϣ�仯ʱ����
		 */
		public void onLocationChanged(Location location) {
			// updateView(location);
			// Log.i(TAG, "ʱ�䣺"+location.getTime());
			// Log.i(TAG, "���ȣ�"+location.getLongitude());
			// Log.i(TAG, "γ�ȣ�"+location.getLatitude());
			// Log.i(TAG, "���Σ�"+location.getAltitude());
		}

		/**
		 * GPS״̬�仯ʱ����
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS״̬Ϊ�ɼ�ʱ
			case LocationProvider.AVAILABLE:
				Log.i("GPS", "��ǰGPS״̬Ϊ�ɼ�״̬");
				break;
			// GPS״̬Ϊ��������ʱ
			case LocationProvider.OUT_OF_SERVICE:
				Log.i("GPS", "��ǰGPS״̬Ϊ��������״̬");
				break;
			// GPS״̬Ϊ��ͣ����ʱ
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i("GPS", "��ǰGPS״̬Ϊ��ͣ����״̬");
				break;
			}
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderEnabled(String provider) {
			Location location = lm.getLastKnownLocation(provider);
			// updateView(location);
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderDisabled(String provider) {
			// updateView(null);
		}
	};

	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// ��һ�ζ�λ
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i("GPS", "��һ�ζ�λ");
				break;
			// ����״̬�ı�
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i("GPS", "����״̬�ı�");
				// ��ȡ��ǰ״̬
				GpsStatus gpsStatus = lm.getGpsStatus(null);
				// ��ȡ���ǿ�����Ĭ�����ֵ
				int maxSatellites = gpsStatus.getMaxSatellites();
				// ����һ��������������������
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("��������" + count + "������");
				// mTv.setText("��������"+count+"������");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i("GPS", "��λ����");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i("GPS", "��λ����");
				break;
			}
		};
	};
}
