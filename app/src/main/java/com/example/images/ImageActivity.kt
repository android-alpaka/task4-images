package com.example.images

import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.LruCache
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    companion object {
        private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        private val cacheSize = maxMemory / 8
        val memoryCache: LruCache<String, Bitmap> =
            object : LruCache<String, Bitmap>(cacheSize) {
                override fun sizeOf(key: String, bitmap: Bitmap): Int {
                    return bitmap.byteCount / 1024
                }
            }
    }
    lateinit var url : String
    private val broadcastReceiver = PictureBroadcastReceiver()
    //var bound = false
    //lateinit var myService: ImageDownloadingService
    //private var sConn = object : ServiceConnection {
    //    override fun onServiceConnected(name : ComponentName, binder : IBinder) {
    //        bound = true
    //        myService = (binder as ImageDownloadingService.MyBinder).getMyService()
    //    }
    //    override fun onServiceDisconnected(name : ComponentName) {
    //        bound = false
    //    }
    //}
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        url = intent.extras?.getString("url").toString()
        Log.i("IMAGE_ACTIVITY","IMAGE_ACTIVITY $url created")
        //val intentDownloading = Intent(
        //    this@ImageActivity,
        //    ImageDownloadingService::class.java
        //).putExtra("url", url)
        if(memoryCache.get(url)==null){
            val intentFilter = IntentFilter(
                "IMAGE $url DOWNLOADED"
            )
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
            registerReceiver(broadcastReceiver, intentFilter)

            startService(
                Intent(this, IntentImageDownloadingService::class.java).putExtra(
                    "url",
                    url
                )
            )
        } else {
            image_view.setImageBitmap(memoryCache.get(url))
        }
        //bound = true
        //bindService(intentDownloading, sConn as ServiceConnection, 0)
    }

    inner class PictureBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            image_view.setImageBitmap(memoryCache.get(url))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        //if (bound){ unbindService(sConn) }
    }
}