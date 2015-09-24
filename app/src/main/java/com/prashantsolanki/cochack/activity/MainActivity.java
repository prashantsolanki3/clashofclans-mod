package com.prashantsolanki.cochack.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.prashantsolanki.cochack.R;
import com.prashantsolanki.cochack.service.GetPositions;
import com.prashantsolanki.cochack.service.TouchService;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Status","Touch "+TouchService.isRunning+" Pos "+GetPositions.isRunning );
        if(TouchService.isRunning)
            stopService(new Intent(this,TouchService.class));
        if(GetPositions.isRunning)
            stopService(new Intent(this,GetPositions.class));
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(100);
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


   public void openSetting(View view){
        startActivity(new Intent(this, PreferenceActivity.class));
    }

    public void openHowTo(View view){
        startActivity(new Intent(this, Main2Activity.class));

    }
   public void startService(View view){
        sendNotification(1);
        Intent i;
        PackageManager manager = getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage("com.supercell.clashofclans");
            if (i == null)
                throw new PackageManager.NameNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
            finish();
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this,"Clash of Clans not Installed",Toast.LENGTH_LONG).show();
        }
    }
}
