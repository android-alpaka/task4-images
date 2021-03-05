package com.example.images

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.images.IntentImageDownloadingService.Companion.memoryCache
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    lateinit var url : String
    private val broadcastReceiver = PictureBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        url = intent.extras?.getString("url").toString()
        Log.i("IMAGE_ACTIVITY", "IMAGE_ACTIVITY ${url.subSequence(11, 15)} created")
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("android.intent.action.DOWNLOAD_ENDED"))
        if(memoryCache.get(url)==null){
            startService(
                Intent(this, IntentImageDownloadingService::class.java).putExtra(
                    "url",
                    url
                )
            )
        } else {
            Log.i("IMAGE_ACTIVITY","IMAGE_ACTIVITY ${url.subSequence(11,15)} restored")
            image_view.setImageBitmap(memoryCache.get(url))
            progressBar.visibility = View.GONE
        }
    }

    inner class PictureBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.i("RECEIVER","Message received")
            synchronized(memoryCache){ image_view.setImageBitmap(memoryCache.get(url)) }
            progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}