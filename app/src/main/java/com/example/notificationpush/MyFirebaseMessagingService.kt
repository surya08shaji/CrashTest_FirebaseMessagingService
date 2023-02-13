package com.example.notificationpush

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)

        val TAG = "Service"
        val title = remoteMessage.notification!!.title
        val message = remoteMessage.notification!!.body

        Log.i(TAG, "onMessageReceived: title : $title")
        Log.i(TAG, "onMessageReceived: message : $message")

        val intent = Intent(this, NextActivity::class.java)

        val CHANNEL_ID = "MYCHANNEL"
        val notificationChannel = NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext)
        .setContentTitle(title)
        .setContentText(message)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.sym_action_chat,"Title",pendingIntent)
        .setChannelId(CHANNEL_ID)
        .setSmallIcon(R.drawable.sym_action_chat)

        val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)
    notificationManager.notify(0, mBuilder.build())

    }



    private fun handleMessage(remoteMessage: RemoteMessage) {
        val handler = Handler(Looper.getMainLooper())

        handler.post(Runnable {
            Toast.makeText(baseContext, getString(R.string.copy),
                Toast.LENGTH_LONG).show()

                remoteMessage.notification?.let {
                    val intent = Intent("MyData")
                    intent.putExtra("message", remoteMessage.data["text"])
                    broadcaster?.sendBroadcast(intent)
                }
        })
    }
    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }
}