package com.mrkanyoze.imagegengpt

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var voiceButton: Button
    private lateinit var progressDialog: ProgressDialog

    private val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    private val RECORD_AUDIO_REQUEST_CODE = 123

    private val dalleApiUrl = "https://api.dalle.com/v1/images"
    private val dalleApiKey = "YOUR_DALLE_API_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        voiceButton = findViewById(R.id.voiceButton)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Generating image...")
        progressDialog.setCancelable(false)

        voiceButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO_PERMISSION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startVoiceRecognition()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(RECORD_AUDIO_PERMISSION),
                    RECORD_AUDIO_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition()
            } else {
                Toast.makeText(
                    this,
                    "Recording audio permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your query...")
        startActivityForResult(intent, RECORD_AUDIO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val query = matches[0]
                queryDalleApi(query)
            }
        }
    }

    private fun queryDalleApi(query: String) {
        progressDialog.show()

        val client = OkHttpClient()
        val json = JSONObject()
        json.put("prompt", query)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url(dalleApiUrl)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $dalleApiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "API request failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string())
                    val imageUrl = jsonResponse.getString("image")
                    val bitmap = loadImageFromUrl(imageUrl)
                    runOnUiThread {
                        imageView.setImageBitmap(bitmap)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "API request unsuccessful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun loadImageFromUrl(url: String): Bitmap? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val inputStream = response.body?.byteStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
