package com.example.images

import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.LruCache
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    companion object {
        private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        private val cacheSize = maxMemory / 8
        val memoryCache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(cacheSize)
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
        Log.i("IMAGE_ACTIVITY", "IMAGE_ACTIVITY ${url.subSequence(11, 15)} created")
        //val intentDownloading = Intent(
        //    this@ImageActivity,
        //    ImageDownloadingService::class.java
        //).putExtra("url", url)
        val intentFilter = IntentFilter(Intent.ACTION_ANSWER)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(broadcastReceiver, intentFilter)
        if(memoryCache.get(url)==null){
            //progressBar.visibility = View.VISIBLE
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
        //bound = true
        //bindService(intentDownloading, sConn as ServiceConnection, 0)
    }

    inner class PictureBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            image_view.setImageBitmap(memoryCache.get(url))
            progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}