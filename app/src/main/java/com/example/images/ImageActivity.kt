package com.example.images

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    var bound = false
    lateinit var url : String
    lateinit var myService: ImageDownloadingService
    private val broadcastReceiver = PictureBroadcastReceiver()
    private var sConn = object : ServiceConnection {
        override fun onServiceConnected(name : ComponentName, binder : IBinder) {
            bound = true
            myService = (binder as ImageDownloadingService.MyBinder).getMyService()
        }
        override fun onServiceDisconnected(name : ComponentName) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        url = intent.extras?.getString("url").toString()
        val intentDownloading = Intent(
            this,
            ImageDownloadingService::class.java
        ).putExtra("url", url)
        val intentFilter = IntentFilter(
            "IMAGE $url DOWNLOADED"
        )
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(broadcastReceiver, intentFilter)
        bound = true
        bindService(intentDownloading, sConn as ServiceConnection, 0)
    }

    inner class PictureBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            image_view.setImageBitmap(myService.cache[url])
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        if (bound){
            unbindService(sConn)
        }
    }
}