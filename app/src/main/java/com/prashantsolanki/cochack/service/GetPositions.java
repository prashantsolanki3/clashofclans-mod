package com.prashantsolanki.cochack.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.prashantsolanki.cochack.Allpositions;
import com.prashantsolanki.cochack.Position;
import com.prashantsolanki.cochack.R;
import com.prashantsolanki.cochack.Utils;
import com.prashantsolanki.cochack.activity.MainActivity;

import java.util.ArrayList;

public class GetPositions extends Service {
    GetPositionsView mView;
    public static boolean isRunning=false;
    ArrayList<Position> positions = new ArrayList<>();

    @Override 
    public IBinder onBind(Intent intent) {
        return null; 
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", intent.getAction());

        if(intent.getAction().equals("StopPositioningService")){
            sendNotification(1);
            stopSelf();
        }else
        sendNotification(2);
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendNotification(int type) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.mipmap.icon);

        // This intent is fired when notification is clicked
        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://javatechig.com/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);*/

        // Set the intent that will fire when the user taps the notification.
        ///builder.setContentIntent(pendingIntent);


        Intent startPositionIntent = new Intent(this, GetPositions.class);
        startPositionIntent.setAction("StartPositioningService");
        PendingIntent startPositionPendingIntent = PendingIntent.getService(this, 0, startPositionIntent, 0);

        Intent stopPostionIntent = new Intent(this,GetPositions.class);
        stopPostionIntent.setAction("StopPositioningService");
        PendingIntent stopPostionPendingIntent = PendingIntent.getService(this,0,stopPostionIntent,0);

        Intent stopTouchIntent = new Intent(this,TouchService.class);
        stopTouchIntent.setAction("StopTouchingService");
        PendingIntent stopTouchPendingIntent = PendingIntent.getService(this,0,stopTouchIntent,0);

        Intent startTouchIntent = new Intent(this,TouchService.class);
        startTouchIntent.setAction("StartTouchingService");
        PendingIntent startTouchPendingIntent = PendingIntent.getService(this,0,startTouchIntent,0);

        builder.setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0));
        builder.setAutoCancel(true);

        if(type==2){
            builder.addAction(R.drawable.ic_check, "Save Positions", stopPostionPendingIntent);
        }else if(type==1){
            builder.addAction(R.drawable.ic_start_positioning,"Start Positioning",startPositionPendingIntent);
            builder.addAction(R.drawable.ic_start_service, "Start Collection", startTouchPendingIntent);
        }else if(type==3)
            builder.addAction(R.drawable.ic_stop_collection, "Stop Collection", stopTouchPendingIntent);

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(getResources().getString(R.string.app_name));
        // Content text, which appears in smaller text below the title
        builder.setContentText("2 Steps to make is work!");
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("1.Position\n"+"2.Collection");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(100, builder.build());
    }



    @Override
    public void onCreate() { 
        super.onCreate();
        isRunning=true;
        Toast.makeText(getApplicationContext(),"Click on your resource Collectors. Then Click Stop Positioning.",Toast.LENGTH_SHORT).show();
        mView = new GetPositionsView(this);
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("Touch Position", "X= " + event.getRawX() + " Y=" + event.getRawY());
                    positions.add(new Position(event.getRawX(), event.getRawY()));
                    Toast.makeText(getApplicationContext(),"Collector "+positions.size()+ "Added",Toast.LENGTH_SHORT).show();


                }

                return true;
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity =Gravity.END | Gravity.TOP;

        params.setTitle(getResources().getString(R.string.app_name));
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
    } 
 
    @Override 
    public void onDestroy() { 
        super.onDestroy(); 
        Allpositions allpositions = new Allpositions(positions);

        Gson gson = new Gson();
        Utils.addToCache(gson.toJson(allpositions),"PositionList");

        Log.d("PositionList", Utils.getCache("PositionList"));
        isRunning=false;
        if(mView != null)
        { 
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        } 
    } 
} 
 
class GetPositionsView extends ViewGroup {
    private Paint mLoadPaint;
 
    public GetPositionsView(Context context) {
        super(context);

        mLoadPaint = new Paint();
        mLoadPaint.setAntiAlias(true);
        mLoadPaint.setTextSize(10);
        mLoadPaint.setARGB(255, 255, 0, 0);
    } 

    @Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("", 0, 0, mLoadPaint);

    } 
 
    @Override 
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
    } 
 

}