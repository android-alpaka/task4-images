package com.example.images

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*
import java.lang.ref.WeakReference
import java.net.URL

class ImageActivity : AppCompatActivity() {

    internal fun onLoadCompleted(res: Bitmap?) {
        image_view.setImageBitmap(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
    }
}