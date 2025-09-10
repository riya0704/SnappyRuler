package com.example.snappyruler

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.snappyruler.ui.SnappyRulerApp
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // ignore; permission requested before export
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SnappyRulerApp(onExport = { bitmap: Bitmap ->
                        // Save to gallery and share
                        val uri = saveBitmapToMediaStore(bitmap)
                        uri?.let {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                            }
                            startActivity(Intent.createChooser(share, "Share drawing"))
                        }
                    })
                }
            }
        }
    }

    private fun saveBitmapToMediaStore(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "snappy_ruler", "Exported from SnappyRuler")
        return if (path != null) Uri.parse(path) else null
    }
}
