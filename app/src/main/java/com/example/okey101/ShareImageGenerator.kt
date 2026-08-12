package com.example.okey101

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareImageGenerator {

    fun shareGameResult(context: Context, winnerName: String, winnerScore: Int) {
        // 1. Inflate Layout
        val view = LayoutInflater.from(context).inflate(R.layout.layout_share_result, null)
        
        // 2. Populate Data
        val tvWinnerName = view.findViewById<TextView>(R.id.tvWinnerName)
        val tvWinnerScore = view.findViewById<TextView>(R.id.tvWinnerScore)
        
        tvWinnerName.text = winnerName
        tvWinnerScore.text = "$winnerScore Puan"

        // 3. Measure & Layout properties to capture bitmap (1080x1920)
        val width = 1080
        val height = 1920
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, width, height)

        // 4. Draw to Bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        // 5. Save to Cache
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream = FileOutputStream("$cachePath/result_share.png") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // 6. Get URI
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "com.example.okey101.fileprovider",
                File(cachePath, "result_share.png")
            )

            // 7. Share Intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Sonucu Paylaş"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
