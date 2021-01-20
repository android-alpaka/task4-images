package com.example.images

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    var bound = false
    lateinit var myService: ImageDownloadingService
    private val broadcastReceiver = PictureBroadcastReceiver()
    private var sConn : ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val intentDownloading = Intent(
            this,
            ImageDownloadingService::class.java
        ).putExtra("url", intent.extras?.getString("url"))
        sConn = object : ServiceConnection {
            override fun onServiceConnected(name : ComponentName, binder : IBinder) {
                bound = true
                myService = (binder as ImageDownloadingService.MyBinder).getMyService()
            }
            override fun onServiceDisconnected(name : ComponentName) {
                bound = false
            }
        }
        startService(intentDownloading)
        bindService(intentDownloading, sConn as ServiceConnection, 0)
        val intentFilter = IntentFilter(
            "IMAGE DOWNLOADED"
        )
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    inner class PictureBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if(myService.bimage != null)
                image_view.setImageBitmap(myService.bimage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        if (bound){
            sConn?.let { unbindService(it) }
        }
    }
}