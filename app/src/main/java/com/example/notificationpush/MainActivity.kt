package com.example.notificationpush

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    lateinit var textViewNotification :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))



       // textViewNotification = findViewById(R.id.text_view_notification)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.w(ContentValues.TAG,"token failed",task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d(ContentValues.TAG,token)
            Toast.makeText(baseContext,token,Toast.LENGTH_SHORT).show()
        })
        if (checkGooglePlayServices()) {

        } else {
            Log.w(ContentValues.TAG, "Device doesn't have google play services")
        }
        val bundle = intent.extras
        if (bundle != null) {
            textViewNotification.text = bundle.getString("text")
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return if(status != ConnectionResult.SUCCESS){
            Log.e(ContentValues.TAG,"Error")
            false
        }else{
            Log.i(ContentValues.TAG,"Google play services updated")
            true
        }
    }
    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            textViewNotification.text = intent.extras?.getString("message")
        }
    }
    fun OnStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("MyData"))
    }
    fun OnStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

}