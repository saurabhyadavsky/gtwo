package com.example.gtwo.notifications;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.example.gtwo.ChatActivity;
import com.example.gtwo.PostDetailsActivity;
import com.example.gtwo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {

   /// private static final String ADMIN_CHANNEL_ID ="admin_channel" ;
    private final String ADMIN_CHANNEL_ID = getString(R.string.admin_channel_id);
    private static final String CHAT_NOTIFICATION = "ChatNotification";
    private static final String POST_NOTIFICATION = "POST_NOTIFICATION";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        String saveCurrentUser = sp.getString("CURRENT_USERID", "None");

        String notificationType = remoteMessage.getData().get("notificationType");
        if (notificationType != null) {
            if (notificationType.equals(CHAT_NOTIFICATION)) {
                handleChatNotification(remoteMessage, saveCurrentUser);
            } else if (notificationType.equals(POST_NOTIFICATION)) {
                handlePostNotification(remoteMessage, saveCurrentUser);
            }
        }
    }

    private void handleChatNotification(RemoteMessage remoteMessage, String saveCurrentUser) {
        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null && sent != null && sent.equals(user1.getUid())) {
            if (!saveCurrentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendAboveNotificationd(remoteMessage);
                } else {
                    sendNormalNotification(remoteMessage);
                }
            }
        }
    }
    private void handlePostNotification(RemoteMessage remoteMessage, String saveCurrentUser) {
        String sender = remoteMessage.getData().get("sender");
        String pId = remoteMessage.getData().get("pId");
        String pTitle = remoteMessage.getData().get("pTitle");
        String pDescription = remoteMessage.getData().get("pDescription");
        if (!sender.equals(saveCurrentUser)) {
            showPostNotification("" + pId, "" + pTitle, "" + pDescription);
        }
    }

    private void showPostNotification(String pid, String title, String description) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpPostNotification(notificationManager);
        }
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra("pid", pid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent intent1 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, ADMIN_CHANNEL_ID)
                    .setSmallIcon(R.drawable.profile_image)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSound(notificationUri)
                    .setContentIntent(intent1);
        }
        notificationManager.notify(notificationId, builder.build());
    }

    private void setUpPostNotification(NotificationManager notificationManager) {
        CharSequence sequence = "New Notification";
        String channelDescription = "Device To Device Post Notification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ADMIN_CHANNEL_ID, sequence, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendAboveNotificationd(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       OreoAndAboveNotification oreoAndAboveNotification=new OreoAndAboveNotification(this);
       Notification.Builder builder=oreoAndAboveNotification.getOnNotificationBuilder(title,body,pendingIntent,sounduri,icon);

        int j=0;
        if(i>0){
            j=1;
        }
       oreoAndAboveNotification.getNotificationManager().notify(j,builder.build());

    }
    private void sendNormalNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sounduri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=1;
        }
        notificationManager.notify(j,builder.build());


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            updateToken(s);
        }
    }

    private void updateToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
            Token tokena = new Token(token);
            reference.child(user.getUid()).setValue(tokena);
        }
    }

}
