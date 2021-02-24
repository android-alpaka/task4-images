package com.example.images

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException
import java.net.URL

class IntentImageDownloadingService : IntentService("IntentImageDownloadingService") {

    override fun onHandleIntent(intent: Intent?) {
        val imageUrl = intent?.getStringExtra("url")
        Log.i("SERVICE", "Connecting to $imageUrl")
        var bimage: Bitmap? = null
        try {
            val inputStream = URL(imageUrl).openStream()
            bimage = BitmapFactory.decodeStream(inputStream)
            Log.i("SERVICE", "Image $imageUrl downloaded")
        } catch (e: IOException) {
            Log.e("SERVICE", "Failed to load image ${e.message}", e)
            e.printStackTrace()
        }
        if (bimage != null) {
            ImageActivity.memoryCache.put(imageUrl, bimage)
            sendBroadcast(Intent("IMAGE $imageUrl DOWNLOADED").addCategory(Intent.CATEGORY_DEFAULT))
        }
    }
}