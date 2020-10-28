package com.example.images

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.URL

class ImageActivity : AppCompatActivity() {

    private class ImageDescrptionURLLoader(val activity: ImageActivity) : AsyncTask<String, Void, Bitmap>() {

        private val activityRef = WeakReference(activity)

        override fun doInBackground(vararg params: String): Bitmap? {
            val imageURL=params[0]
            var bimage: Bitmap? = null
            try {
                val inputStream = URL(imageURL).openStream()
                bimage = BitmapFactory.decodeStream(inputStream)

            } catch (e: Exception) {
                Log.e("image", "Failed to load image ${e.message}", e)
                e.printStackTrace()
            }
            return bimage

        }

        override fun onPostExecute(res: Bitmap) {
            val activity = activityRef.get()
            activity?.onLoadCompleted(res)
        }

    }

    internal fun onLoadCompleted(res: Bitmap?) {
        image_view.setImageBitmap(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        ImageDescrptionURLLoader(this).execute(intent.extras?.getString("url"))
    }
}