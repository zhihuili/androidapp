package com.shupeng.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.example.android_robot.MainActivity;
import com.example.android_robot.R;
import com.shupeng.util.Utils;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 
 *  0 - Success
 *  10001 - Network Problem
 *  30600 - Internal Server Error
 *  30601 - Method Not Allowed 
 *  30602 - Request Params Not Valid
 *  30603 - Authentication Failed 
 *  30604 - Quota Use Up Payment Required 
 *  30605 - Data Required Not Found 
 *  30606 - Request Time Expires Timeout 
 *  30607 - Channel Token Timeout 
 *  30608 - Bind Relation Not Found 
 *  30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = MyPushMessageReceiver.class
            .getSimpleName();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);
        System.out.println(responseString);
        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (errorCode == 0) {
            Utils.setBind(context, true);
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
      //  updateContent(context, responseString);
        StorageReceiverInfo(context, userId,channelId);
    }

    /**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
       // Toast.makeText(context, messageString, Toast.LENGTH_SHORT).show();
        Log.d(TAG, messageString);
        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
       boolean flag= Util.isRunning(context, "com.example.android_robot");
       if(flag==false){
    	   String svcName = Context.NOTIFICATION_SERVICE;  
	        NotificationManager notificationManager;  
	        notificationManager = (NotificationManager)context.getSystemService(svcName);  
	        //通过使用通知管理器，可以触发新的通知，修改现有的通知或者删除那些不再需要的通知。  
	          
	        /**Android提供了使用通知向用户传递信息的多种方式。 
	         * 1.状态栏图标 
	         * 2.扩展的通知状态绘制器 
	         * 3.额外的效果，比如声音和振动 
	         */  
	        //创建一个通知  
	        //选择一个Drawable来作为状态栏图标的显示  
	        int icon = R.drawable.ic_launcher;  
	        //当启动通知时在状态栏显示的文本  
	        String tickerText = "秦哲，你好。";  
	        //扩展的状态栏按时间顺序排序通知  
	        long when = System.currentTimeMillis();  
	        Notification notification = new Notification(icon, tickerText, when);  
	          
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;  
	        //展开的状态窗口中显示的文本  
	        String expandedText = "我爱你";  
	        //展开的状态的标题  
	        String expandedTitle = "ez";  
	        //当单击展开的文本时，用于启动一个活动的意图。  
	        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);  
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        Util.logStringCache=messageString;
	        PendingIntent launchIntent = PendingIntent.getActivity(context, 0, intent, 0);  
	        notification.setLatestEventInfo(context, expandedTitle, expandedText, launchIntent);  
	        //要触发一个通知，需要把它和一个整型的引用ID一起传递给NotificationManager的notify方法  
	        int notificationRef = 1;  
	        notificationManager.notify(notificationRef, notification);  
	        return;
       }else if(flag==true){
    	   StartIntent(context,message);
    	   return;
       }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
         
    }

    private void StartIntent(Context context, String messageString) {
		// TODO Auto-generated method stub
    	Util.logStringCache=messageString;
    	Intent intent = new Intent();
      intent.setClass(context.getApplicationContext(), MainActivity.class);
      intent.putExtra("content", messageString);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.getApplicationContext().startActivity(intent);
		
	}

	/**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                    Toast.makeText(context, myvalue, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        StartIntent(context, description);
    }

    /**
     * setTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
      //  updateContent(context, responseString);
    }

    /**
     * delTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
      //  updateContent(context, responseString);
    }

    /**
     * listTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
       // updateContent(context, responseString);
    }

    /**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            Utils.setBind(context, false);
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
       // updateContent(context, responseString);
    }

//    private void updateContent(Context context, String content) {
//        Log.d(TAG, "updateContent");
//       SharedPreferences mSharedPreferences= context.getSharedPreferences("TAG", Activity.MODE_PRIVATE);
//       SharedPreferences.Editor editor = mSharedPreferences.edit(); 
//       editor.putString("channled", content); 
//       editor.putString("userid", content); 
//       editor.commit(); 
//       Toast.makeText(context, "SUCCESS", Toast.LENGTH_LONG).show();
//        String logText = "" + Utils.logStringCache;
//
//        if (!logText.equals("")) {
//            logText += "\n";
//        }
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
//        logText += sDateFormat.format(new Date()) + ": ";
//        logText += content;
//
//        Utils.logStringCache = logText;
//
//        Intent intent = new Intent();
//        intent.setClass(context.getApplicationContext(), PushDemoActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(intent);
//    }
    private void StorageReceiverInfo(Context context, String userId,String channelId) {
    	// TODO Auto-generated method stub
    		 SharedPreferences mSharedPreferences= context.getSharedPreferences("TuiSongIC", Activity.MODE_PRIVATE);
    	     SharedPreferences.Editor editor = mSharedPreferences.edit(); 
    	       editor.putString("userid", userId); 
    	       editor.putString("channelid", channelId); 
    	       editor.commit(); 
    }
}
