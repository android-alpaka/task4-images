package com.example.images

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.simple.*
import org.json.simple.parser.JSONParser
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var imagesList = ArrayList<Image>()
    private var imagesListAsString = "null"

    private class ImageDescriptionURLLoader(activity: MainActivity) :  AsyncTask<String, Void, String>() {
        private val activityRef = WeakReference(activity)

        override fun doInBackground(vararg params: String): String {
            val url =
                "https://api.vk.com/method/photos.search?q=${params[1]}&access_token=${params[0]}&count=20&v=5.77"
            Log.i("CONNECT", "Connecting to $url")
            try {
                InputStreamReader(
                    URL(url)
                        .openConnection()
                        .getInputStream()
                ).use {
                    return it.readText()
                }
            } catch (e: IOException) {
                Log.e("connect", "Connection failed: ${e.message}", e)
                e.printStackTrace()
                return ""
            }
        }

        override fun onPostExecute(res: String) {
            val activity = activityRef.get()
            if (activity != null) {
                activity.imagesListAsString=res
                activity.fillRecyclerView()
            }
        }
    }

    private fun fillRecyclerView() {
        val parser = JSONParser()
        val root = parser.parse(imagesListAsString) as JSONObject
        val response = root["response"] as JSONObject
        val items = response["items"] as JSONArray
        for (item in items) {
            item as JSONObject
            val text = item["text"] as String
            val sizes = item["sizes"] as JSONArray
            val size = sizes.last() as JSONObject
            val imageUrl = size["url"] as String
            imagesList.add(Image(text, imageUrl))
        }
        val viewManager = LinearLayoutManager(this)
        images.apply {
            layoutManager = viewManager
            adapter = ImageAdapter(imagesList) {
                startActivity(
                    Intent(this@MainActivity,ImageActivity::class.java).putExtra(
                        "url",
                        it.url
                    )
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imagesListAsString = savedInstanceState?.getString("LIST").toString()
        if (imagesListAsString == "null") {
            ImageDescriptionURLLoader(this).execute(
                "2758906627589066275890663d272c1386227582758906678d722c4923d8ea364a189b4",
                "nature"
            )
        } else {
            fillRecyclerView()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("LIST", imagesListAsString)
        //Log.i("BUNDLE","saved $imagesListAsString")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imagesListAsString = savedInstanceState.getString("LIST").toString()
        //Log.i("BUNDLE","restored $imagesListAsString")
    }
}