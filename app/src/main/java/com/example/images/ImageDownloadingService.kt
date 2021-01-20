package com.example.images

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.lang.ref.WeakReference
import java.net.URL
import java.io.IOException

class ImageDownloadingService : Service() {
    private var lastURL : String? = null
    var bimage : Bitmap? = null
    private val binder = MyBinder()

    private class ImageLoader(service: ImageDownloadingService) : AsyncTask<String, Void, Bitmap>() {

        private val serviceRef = WeakReference(service)

        override fun doInBackground(vararg params: String): Bitmap? {
            val imageUrl = params[0]
            Log.i("connect", "Connecting to $imageUrl")
            var bimage: Bitmap? = null
            try {
                val inputStream = URL(imageUrl).openStream()
                bimage = BitmapFactory.decodeStream(inputStream)
                Log.i("INFO","AAAAAAAAAAAAAAAAAAA")
            } catch (e: IOException) {
                Log.e("image", "Failed to load image ${e.message}", e)
                e.printStackTrace()
            }
            return bimage
        }

        override fun onPostExecute(result: Bitmap) {
            val activity = serviceRef.get()
            activity?.bimage=result
            activity?.sendBroadcast(Intent("IMAGE DOWNLOADED").addCategory(Intent.CATEGORY_DEFAULT))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("INFO","BBBBBBBBBBBBBBBBBBB")
        return START_NOT_STICKY

    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("INFO","CCCCCCCCCCCCCCCCCCCCCC")
        if(!lastURL.equals(intent.getStringExtra("url"))) {
            lastURL = intent.getStringExtra("url")
            ImageLoader(this).execute(
                lastURL
            )
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun onRebind(intent: Intent?) {
        if (!intent?.getStringExtra("url").equals(lastURL)) {
            lastURL = intent?.getStringExtra("url")
            ImageLoader(this).execute(lastURL)
        } else {
            sendBroadcast(Intent("IMAGE DOWNLOADED").addCategory(Intent.CATEGORY_DEFAULT))
        }
    }

    inner class MyBinder: Binder() {
        fun getMyService() = this@ImageDownloadingService
    }
}