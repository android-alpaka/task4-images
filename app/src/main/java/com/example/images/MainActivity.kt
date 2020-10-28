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
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var imagesList: List<Image>? = null

    private class ImageDescrptionURLLoader(val activity: MainActivity) :
        AsyncTask<String, Void, List<Image>>() {

        override fun doInBackground(vararg params: String): List<Image> {
            val imagesList = mutableListOf<Image>()
            val url =
                "https://api.vk.com/method/photos.search?q=${params[1]}&access_token=${params[0]}&count=1000&v=5.77"
            Log.i("connect", "Connecting to $url")
            try {
                InputStreamReader(
                    URL(url)
                        .openConnection()
                        .getInputStream()
                ).use {
                    val parser = JSONParser()
                    val root = parser.parse(it.readText()) as JSONObject
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
                }
            } catch (e: IOException) {
                Log.e("connect", "Connection failed: ${e.message}", e)
                e.printStackTrace()
            }
            return imagesList
        }

        override fun onPostExecute(res: List<Image>) {
            activity.onLoadCompleted(res)
        }

    }

    internal fun onLoadCompleted(res: List<Image>) {
        val viewManager = LinearLayoutManager(this)
        imagesList = res
        images.apply {
            layoutManager = viewManager
            adapter = ImageAdapter(res) {
                startActivity(
                    Intent(this@MainActivity, ImageActivity::class.java).putExtra(
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
    }

    override fun onResume() {
        super.onResume()
        if (imagesList == null) {
            ImageDescrptionURLLoader(this).execute(
                "2758906627589066275890663d272c1386227582758906678d722c4923d8ea364a189b4",
                "nature"
            )
        }
    }
}