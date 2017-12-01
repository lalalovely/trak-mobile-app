package subai.trak2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotifService extends Service {
    UserSessionManager sessionManager;
    String busNumber;
    static  boolean initialized = false;
    Uri sound;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        initialized = false;
        sessionManager = new UserSessionManager(this);
        busNumber = sessionManager.getBusNum();
        DatabaseReference Admin_mRef = FirebaseDatabase.getInstance().getReference().child("Admin_Messages").child(sessionManager.getBusNum());
        Admin_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (initialized) {
                    notif();
                } else{
                    initialized = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();

    }
    public void notif() {
        Intent intent = new Intent(NotifService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("fromNotif", "toMessageTab");
        PendingIntent pd = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(getApplicationContext());
        nfBuilder.setContentTitle("You have a message");
        nfBuilder.setContentText("Tap to open trak");
        nfBuilder.setAutoCancel(true);
        nfBuilder.setSmallIcon(R.drawable.logo_final);
        nfBuilder.setContentIntent(pd);
        nfBuilder.setSound(sound);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, nfBuilder.build());
    }
}