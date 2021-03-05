package com.example.images

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.IOException
import java.net.URL

class IntentImageDownloadingService : IntentService("IntentImageDownloadingService") {
    companion object {
        private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        private val cacheSize = maxMemory / 8
        val memoryCache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(cacheSize)
    }

    override fun onHandleIntent(intent: Intent?) {
        val imageUrl = intent?.getStringExtra("url")
        var bimage: Bitmap? = null
        try {
            val inputStream = URL(imageUrl).openStream()
            Log.i("SERVICE", "Image ${imageUrl?.subSequence(11, 15)} start download")
            bimage = BitmapFactory.decodeStream(inputStream)
            Log.i("SERVICE", "Image ${imageUrl?.subSequence(11, 15)} downloaded")
        } catch (e: IOException) {
            Log.e("SERVICE", "Failed to load image ${e.message}", e)
            e.printStackTrace()
        }
        if (bimage != null) {
            synchronized(memoryCache) { memoryCache.put(imageUrl, bimage) }
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("android.intent.action.DOWNLOAD_ENDED"))
            Log.i("RECEIVER","Message sent")
        }
    }
}