package com.example.images

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.util.LruCache
import java.lang.ref.WeakReference
import java.net.URL
import java.io.IOException

class ImageDownloadingService : Service() {
    private val sample = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888)
    private val binder = MyBinder()
    internal var cache = LruCache<String, Bitmap>(10*1024*1024)

    private class ImageLoader(service: ImageDownloadingService) : AsyncTask<String, Void, Pair<String,Bitmap>>() {

        private val serviceRef = WeakReference(service)

        override fun doInBackground(vararg params: String): Pair<String,Bitmap> {
            val imageUrl = params[0]
            Log.i("CONNECT", "Connecting to $imageUrl")
            lateinit var bimage: Bitmap
            try {
                val inputStream = URL(imageUrl).openStream()
                bimage = BitmapFactory.decodeStream(inputStream)
                Log.i("SERVICE/CONNECT","Image $imageUrl downloaded")
            } catch (e: IOException) {
                Log.e("SERVICE/CONNECT", "Failed to load image ${e.message}", e)
                e.printStackTrace()
            }
            return Pair(imageUrl,bimage)
        }

        override fun onPostExecute(result: Pair<String,Bitmap>) {
            val service = serviceRef.get()
            service!!.cache.put(result.first,result.second)
            service.sendBroadcast(Intent("IMAGE ${result.first} DOWNLOADED").addCategory(Intent.CATEGORY_DEFAULT))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("SERVICE","Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("SERVICE","Task binded")
        val url = intent.getStringExtra("url")
        if(cache[url]!=sample){
            if(cache[url]==null){
                cache.put(url,sample)
                Log.i("SERVICE","Image downloading")
                ImageLoader(this).execute(url)
            } else {
                sendBroadcast(Intent("IMAGE $url DOWNLOADED").addCategory(Intent.CATEGORY_DEFAULT))
                Log.i("SERVICE","Image restored")
            }
        } else {
            Log.i("SERVICE","AsyncTask not completed")
        }
        return binder
    }

    override fun onUnbind(intent: Intent?) = true

    override fun onRebind(intent: Intent) {
        Log.i("SERVICE","Task rebinded")
        val url = intent.getStringExtra("url")
        if(cache[url]!=sample){
            if(cache[url]==null){
                cache.put(url,sample)
                Log.i("SERVICE","Image downloading")
                ImageLoader(this).execute(url)
            } else {
                sendBroadcast(Intent("IMAGE $url DOWNLOADED").addCategory(Intent.CATEGORY_DEFAULT))
                Log.i("SERVICE","Image restored")
            }
        } else {
            Log.i("SERVICE","AsyncTask not completed")
        }
    }

    inner class MyBinder: Binder() {
        fun getMyService() = this@ImageDownloadingService
    }
}