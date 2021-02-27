package com.example.images

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL

class IntentImageDownloadingService : IntentService("IntentImageDownloadingService") {

    override fun onHandleIntent(intent: Intent?) {
        val imageUrl = intent?.getStringExtra("url")
        DownloadPictureAsyncTask(this).execute(imageUrl)
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadPictureAsyncTask(service: IntentImageDownloadingService) : AsyncTask<String,Int,Pair<String,Bitmap?>>() {
        private val serviceRef = WeakReference(service)

        override fun doInBackground(vararg params: String): Pair<String, Bitmap?> {
            val imageUrl = params[0]
            var bimage: Bitmap? = null
            try {
                val inputStream = URL(imageUrl).openStream()
                bimage = BitmapFactory.decodeStream(inputStream)
                Log.i("SERVICE", "Image ${imageUrl.subSequence(11, 15)} downloaded")
            } catch (e: IOException) {
                Log.e("SERVICE", "Failed to load image ${e.message}", e)
                e.printStackTrace()
            }
            return Pair(imageUrl,bimage)
        }

        override fun onPostExecute(pair: Pair<String, Bitmap?>) {
            if (pair.second != null) {
                ImageActivity.memoryCache.put(pair.first, pair.second)
                serviceRef.get()?.sendBroadcast(Intent(Intent.ACTION_ANSWER).addCategory(Intent.CATEGORY_DEFAULT))
            }
        }
    }
}