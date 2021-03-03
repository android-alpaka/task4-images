package com.example.images

import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*
import java.util.*

class ImageActivity : AppCompatActivity() {
    companion object {
        private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        private val cacheSize = maxMemory / 8
        val memoryCache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(cacheSize)
    }
    lateinit var url : String
    private val broadcastReceiver = PictureBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        url = intent.extras?.getString("url").toString()
        Log.i("IMAGE_ACTIVITY", "IMAGE_ACTIVITY ${url.subSequence(11, 15)} created")
        registerReceiver(broadcastReceiver, IntentFilter("android.intent.action.DOWNLOAD_ENDED"))
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
            image_view.setImageBitmap(memoryCache.get(url))
            progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}