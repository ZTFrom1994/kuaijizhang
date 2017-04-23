package org.itst.service;

import java.util.Timer;
import java.util.TimerTask;






import org.itst.applications.MyApplication;
import com.example.kuaijizhang.MainActivity;
import com.example.kuaijizhang.R;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class MyService extends Service {

	private final MyServiceBinder myServiceBinder=new MyServiceBinder();
	private Timer timer=null;
	private TimerTask task=null;
	private int i=0;
	public void startTimer(){
		if(timer==null){
			timer=new Timer();
			task=new TimerTask() {
				@Override
				public void run() {
					i++;
					System.out.println(i);
				}
			};
			timer.schedule(task, 1000,1000);
		}
	}
	public void stopTimer(){
		if(timer!=null){
			task.cancel();
			timer.cancel();
			task=null;
			timer=null;
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
        System.out.println("onbind!");
		return myServiceBinder;
	}
	public class MyServiceBinder extends Binder{
		public MyService getMyService(){
			return MyService.this;
		}
	}
	public int getCurrentNumber(){
		return i;
	}
	private NotificationManager manager;
	private Notification.Builder builder;
	@Override
	public void onCreate() {
		System.out.println("onCreate");
		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.mynotification);
		remoteViews.setImageViewResource(R.id.imageview,
				R.drawable.ic_launcher);
		remoteViews.setTextViewText(R.id.title, "您已经一天没有记账啦！");
		remoteViews.setTextViewText(R.id.text, "快来吧今天的花费记录一下吧");

		Intent intent = new Intent(MyApplication.getContextObject(),
				MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				MyApplication.getContextObject(), 0, intent, 0);
		builder.setContentIntent(pendingIntent);
		builder.setContent(remoteViews);
		builder.setTicker("notification is coming");
		Notification notification = builder.build();
		manager.notify(1001, notification);
		
		startTimer();
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		System.out.println("onDestory");
		stopTimer();
		super.onDestroy();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
