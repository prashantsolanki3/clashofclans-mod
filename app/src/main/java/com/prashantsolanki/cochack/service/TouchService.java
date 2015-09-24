package com.prashantsolanki.cochack.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.prashantsolanki.cochack.Allpositions;
import com.prashantsolanki.cochack.Position;
import com.prashantsolanki.cochack.PrefManager;
import com.prashantsolanki.cochack.R;
import com.prashantsolanki.cochack.Utils;
import com.prashantsolanki.cochack.activity.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Prashant on 5/17/2015.
 */
public class TouchService extends Service {

    final Handler handler = new Handler();
    final Handler handler1 = new Handler();
    public static boolean isRunning=false;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TouchService.class.getSimpleName(), "onCreate");
        isRunning=true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startResourceTouch();
                Toast.makeText(getApplicationContext(),"Next Collection in "+PrefManager.getResoucrceTime()/60000+" mins.",Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, PrefManager.getResoucrceTime());
            }
        };
        handler.postDelayed(runnable, 1000);

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> envList = new ArrayList<>();
                Map<String, String> envMap = System.getenv();
                for (String envName : envMap.keySet()) {
                    envList.add(envName + "=" + envMap.get(envName));
                }
                String[] environment = envList.toArray(new String[0]);
                try {

                    Runtime.getRuntime().exec(
                            new String[]{"su", "-c", "\n\n\n/system/bin/input tap 0 0"},
                            environment);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                handler1.postDelayed(this,PrefManager.getScreenTime());
            }
        }, 1500);

    }



    private void startResourceTouch(){
        List<String> envList = new ArrayList<>();
        Map<String, String> envMap = System.getenv();
        for (String envName : envMap.keySet()) {
            envList.add(envName + "=" + envMap.get(envName));
        }
        String[] environment = envList.toArray(new String[0]);


      /*  String command = "/system/bin/input tap 150 300\n" +
                "/system/bin/input tap 250 450\n" +
                "/system/bin/input tap 250 450\n" +
                "/system/bin/input tap 250 400\n" +
                "/system/bin/input tap 200 400\n";*/
        try {
            Runtime.getRuntime().exec(
                    new String[] { "su", "-c", createCommand() },
                    environment);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TouchService.class.getSimpleName(),"onStartCommand");
        if (intent!=null)
        if(intent.getAction().equals("StopTouchingService")) {
            sendNotification(1);
            handler.removeCallbacksAndMessages(null);
            handler1.removeCallbacksAndMessages(null);
            stopSelf();

        }else{
            sendNotification(3);
        }
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


    private String createCommand(){
        String command= "\n\n\n";
        Gson gson=new Gson();
        for(Position position:gson.fromJson(Utils.getCache("PositionList"), Allpositions.class).getPositions()) {
            Log.d("Pos",position.getX()+" "+position.getY());
           command=command.concat("/system/bin/input tap " + Math.round(position.getX()) + " " + Math.round(position.getY()) + "\n");
        }
        Log.d("Command",command);
        return command;
    }

    @Override
    public void onDestroy() {
        Log.d(TouchService.class.getSimpleName(),"onDestroy");
        isRunning=false;
        handler.removeCallbacksAndMessages(null);
        handler1.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
