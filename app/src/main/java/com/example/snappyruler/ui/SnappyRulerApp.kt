package com.example.snappyruler.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snappyruler.ui.components.Toolbox
import com.example.snappyruler.ui.canvas.SnappyCanvas
import com.example.snappyruler.viewmodel.ToolViewModel

@Composable
fun SnappyRulerApp(onExport: (Bitmap)->Unit) {
    val vm = remember { ToolViewModel() }

    Box(modifier = Modifier.fillMaxSize()) {
        SnappyCanvas(
            modifier = Modifier.fillMaxSize(),
            viewModel = vm
        )

        Column(modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)) {
            Toolbox(vm)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { vm.undo() }) { Text("Undo") }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { vm.redo() }) { Text("Redo") }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { vm.exportBitmap()?.let { onExport(it) } }) { Text("Export") }
        }
    }
}
