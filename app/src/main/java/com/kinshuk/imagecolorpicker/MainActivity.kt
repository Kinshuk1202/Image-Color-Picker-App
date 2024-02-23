package com.kinshuk.imagecolorpicker

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide
import com.kinshuk.imagecolorpicker.databinding.ActivityMainBinding
import java.lang.String
import kotlin.Int
import kotlin.arrayOf


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isImageSelected = false
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addImg.setOnClickListener {

            val options = arrayOf("Camera", "Gallery")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Using ")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> takePictureFromCamera()
                    1 -> chooseImageFromGallery()
                }
            }
            builder.show()
        }
        binding.imview.setOnTouchListener { _, event ->
            if (isImageSelected && event.action == MotionEvent.ACTION_DOWN) {
                // Get the x and y coordinates of the touch event
                val x = event.x.toInt()
                val y = event.y.toInt()

                // Ensure the coordinates are within the image bounds
                if (x >= 0 && x < binding.imview.width && y >= 0 && y < binding.imview.height) {
                    // Get the color at the touched point
                    val pixelColor = binding.imview.drawToBitmap().getPixel(x, y)
                    val hexColor = String.format("#%06X", 0xFFFFFF and pixelColor)
                    // Do something with the hex color, like display it in a TextView
                    binding.textcode.text = hexColor.toString()
                    binding.colorView.setBackgroundColor(Color.parseColor(hexColor))
                }
            }
            true // Consume the event
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,2)
    }

    private fun takePictureFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    isImageSelected = true
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageBitmap?.let { bitmap ->
                        Glide.with(this)
                            .load(bitmap)
                            .into(binding.imview)
                    }
                }
                2 -> {
                    isImageSelected = true
                    val imageUri = data?.data
                    Glide.with(this)
                        .load(imageUri)
                        .into(binding.imview)

                }
            }
        }
    }
}