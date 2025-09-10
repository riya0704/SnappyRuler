package com.example.snappyruler.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snappyruler.viewmodel.ToolViewModel
import com.example.snappyruler.viewmodel.ToolType

@Composable
fun Toolbox(vm: ToolViewModel) {
    Column(modifier = Modifier.padding(4.dp)) {
        Button(onClick = { vm.selectTool(ToolType.PEN) }) { Text("Pen") }
        Button(onClick = { vm.selectTool(ToolType.RULER) }) { Text("Ruler") }
        Button(onClick = { vm.selectTool(ToolType.SETSQUARE45) }) { Text("Set Sq 45°") }
        Button(onClick = { vm.selectTool(ToolType.SETSQUARE3060) }) { Text("Set Sq 30/60°") }
        Button(onClick = { vm.selectTool(ToolType.PROTRACTOR) }) { Text("Protractor") }
        Button(onClick = { vm.selectTool(ToolType.COMPASS) }) { Text("Compass") }
    }
}
