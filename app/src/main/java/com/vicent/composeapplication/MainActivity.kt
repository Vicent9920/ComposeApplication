package com.vicent.composeapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vicent.composeapplication.dao.SudokuLevel
import com.vicent.composeapplication.model.*
import com.vicent.composeapplication.mvi.*
import com.vicent.composeapplication.ui.theme.*
import com.vicent.composeapplication.viewmodel.SudokuViewModel
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val viewModel: SudokuViewModel by viewModels()

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeApplicationTheme {
                SudokuPage(viewModel)
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.currentState.collect {
                Log.e(TAG, it.javaClass.simpleName)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.sendEvent(InLifeEvent(true))
    }

    override fun onPause() {
        super.onPause()
        viewModel.sendEvent(InLifeEvent(false))
    }


}

@ExperimentalFoundationApi
@Composable
fun SudokuPage(viewModel: SudokuViewModel) {
    val dateTime: String by viewModel.time.collectAsState("")
    val title: String by viewModel.title.collectAsState("")
    viewModel.getLevelData()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(text = title, fontSize = 18.sp,fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = dateTime, fontSize = 14.sp,fontWeight = FontWeight.Thin)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {//TODO 返回关卡
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            //todo 暂停、音效、震动、玩法、主题
                        }
                    ) {
                        Icon(Icons.Filled.Settings, null)
                    }
                }
            )
        },
    ) {
        Crossfade(viewModel.currentState.collectAsState().value) {
            when (it) {
                is SudokuLoading -> Loading()
                is SudokuPlay -> {
                    viewModel.runTimer()
                    Play(viewModel = viewModel)

                }
                is SudokuHome -> ShowHome(viewModel)
            }
        }
        Crossfade(viewModel.showDone.collectAsState().value) {
            if (it) ShowDoneDialog(viewModel)
        }
        Crossfade(viewModel.showError.collectAsState().value) {
            if (it) ShowErrorDialog(viewModel)
        }


    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeApplicationTheme {
        SudokuPage(SudokuViewModel())
    }
}

@Composable
private fun Loading() {
    Dialog(
        onDismissRequest = { }
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column {
                CircularProgressIndicator()
                Text("加载中 ing...", color = MaterialTheme.colors.primary)
            }
        }
    }

}

@ExperimentalFoundationApi
@Composable
private fun Play(viewModel: SudokuViewModel) {
    val sudokuEffect by viewModel.pageData.collectAsState()
    val deleteEnable by viewModel.deleteEnable.collectAsState()
    val inputEnable by viewModel.inputEnable.collectAsState()
    val backEnable by viewModel.backEnable.collectAsState()
    Box(
        modifier = Modifier
            .background(color = Color.White)
            .padding(32.dp, 14.dp)
    ) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(9)
        ) {
            val data = sudokuEffect.data
            items(81) { index ->
                val item = data[index]
                val text = if (data[index].value == 0) "" else item.value.toString()
                when (data[index].state) {
                    NormalTarget -> Text(
                        text = text,
                        color = if (item.onlyRead) Color.Black else MaterialTheme.colors.primary,
                        modifier = Modifier
                            .aspectRatio(1f, true)
                            .background(color = Gray1)
                            .clickable {
                                viewModel.sendEvent(SudokuClickEvent(index))
                            },
                        textAlign = TextAlign.Center
                    )
                    ErrorAssociation -> Text(
                        text = text,
                        color = SoftRed,
                        modifier = Modifier
                            .aspectRatio(1f, true)
                            .background(color = Gray)
                            .clickable {
                                viewModel.sendEvent(SudokuClickEvent(index))
                            },
                        textAlign = TextAlign.Center
                    )
                    ErrorTarget -> Text(
                        text = text,
                        color = Color.White,
                        modifier = Modifier
                            .aspectRatio(1f, true)
                            .background(color = SoftRed)
                            .clickable {
                                viewModel.sendEvent(SudokuClickEvent(index))
                            },
                        textAlign = TextAlign.Center
                    )
                    TheSameAssociation -> Text(
                        text = text,
                        color = Color.White,
                        modifier = Modifier
                            .aspectRatio(1f, true)
                            .background(color = MaterialTheme.colors.primary)
                            .clickable {
                                viewModel.sendEvent(SudokuClickEvent(index))
                            },
                        textAlign = TextAlign.Center
                    )
                    NotEmptyTarget -> Text(
                        text = text,
                        color = Color.White,
                        modifier = Modifier
                            .aspectRatio(1f, true)
                            .background(color = PaleGreen1)
                            .clickable {
                                viewModel.sendEvent(SudokuClickEvent(index))
                            },
                        textAlign = TextAlign.Center
                    )
                    WeakTarget -> Text(
                        text = "",
                        modifier = Modifier
                            .aspectRatio(1f, true)
                            .background(color = PaleGreen2)
                            .clickable {
                                viewModel.sendEvent(SudokuClickEvent(index))
                            },
                        textAlign = TextAlign.Center
                    )
                    WeakAssociation -> Text(
                        text = text,
                        color = if (item.onlyRead) Color.Black else MaterialTheme.colors.primary,
                        modifier = Modifier
                            .aspectRatio(1f, true)
                            .background(color = Gray)
                            .clickable {
                                viewModel.sendEvent(SudokuClickEvent(index))
                            },
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
        GridBg()

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.fillMaxHeight(0.55f))
            Row(
                modifier = Modifier
                    .padding(32.dp, 0.dp, 32.dp, 14.dp)
            ) {

                Button(
                    enabled = deleteEnable.enable,
                    onClick = { viewModel.sendEvent(DeleteClickEvent(deleteEnable.position)) }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    // 添加间隔
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("擦除")
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(enabled = backEnable, onClick = { viewModel.sendEvent(BackClickEvent) }) {
                    Icon(
                        Icons.Sharp.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    // 添加间隔
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("撤回")
                }


            }
            LazyVerticalGrid(
                cells = GridCells.Fixed(9)
            ) {
                items(9) { index ->
                    Text(text = "${index + 1}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (inputEnable.enable) Color.Black else Color.Gray,
                        modifier = Modifier.clickable {
                            if (inputEnable.enable) {
                                viewModel.sendEvent(InputValue(inputEnable.position, index + 1))
                            }
                        })
                }
            }


        }


    }


}

/**
 * 九宫格背景线条
 */
@Composable
fun GridBg() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.width
        val tickMarks = canvasWidth / 3
        val hairlines = canvasWidth / 9
        // left |
        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = 0f, y = canvasHeight),
            color = Color.Black,
            strokeWidth = 4f
        )
        arrayOf(1, 2, 4, 5, 7, 8).forEach {
            drawLine(
                start = Offset(x = hairlines * it - 1, y = 0f),
                end = Offset(x = hairlines * it - 1, y = canvasHeight),
                color = Color.Gray,
                strokeWidth = 4f
            )
        }


        drawLine(
            start = Offset(x = tickMarks - 4, y = 0f),
            end = Offset(x = tickMarks - 4, y = canvasHeight),
            color = Color.Black,
            strokeWidth = 4f
        )
        drawLine(
            start = Offset(x = tickMarks * 2 - 4, y = 0f),
            end = Offset(x = tickMarks * 2 - 4, y = canvasHeight),
            color = Color.Black,
            strokeWidth = 4f
        )
        //right |
        drawLine(
            start = Offset(x = canvasWidth, y = 0f),
            end = Offset(x = canvasWidth, y = canvasHeight),
            color = Color.Black,
            strokeWidth = 4f
        )
        // top -
        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = canvasWidth, y = 0f),
            color = Color.Black,
            strokeWidth = 4f
        )
        arrayOf(1, 2, 4, 5, 7, 8).forEach {
            drawLine(
                start = Offset(x = 0f, y = hairlines * it - 1),
                end = Offset(x = canvasWidth, y = hairlines * it - 1),
                color = Color.Gray,
                strokeWidth = 4f
            )
        }
        drawLine(
            start = Offset(x = 0f, y = tickMarks - 4),
            end = Offset(x = canvasWidth, y = tickMarks - 4),
            color = Color.Black,
            strokeWidth = 4f
        )
        drawLine(
            start = Offset(x = 0f, y = tickMarks * 2 - 4),
            end = Offset(x = canvasWidth, y = tickMarks * 2 - 4),
            color = Color.Black,
            strokeWidth = 4f
        )
        // bottom -
        drawLine(
            start = Offset(x = 0f, y = canvasHeight),
            end = Offset(x = canvasWidth, y = canvasHeight),
            color = Color.Black,
            strokeWidth = 4f
        )
    }
}

/**
 * 提示用户完成游戏闯关
 */
@Composable
fun ShowDoneDialog(viewModel: SudokuViewModel) {
    AlertDialog(onDismissRequest = {
        // 当用户点击对话框以外的地方或者按下系统返回键将会执行的代码
    },
        title = {
            Text(
                text = "恭喜闯关成功",
                fontWeight = FontWeight.W700,
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Text(
                text = "恭喜你闯关成功，本关耗时5分钟",
                fontSize = 16.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.sendEvent(DoneDialogClick(false))
                },
            ) {
                Text(
                    "继续闯关",
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.button
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.sendEvent(DoneDialogClick(true))
                }
            ) {
                Text(
                    "返回主页",
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.button,
                    color = Color.Gray
                )
            }
        }

    )
}

/**
 * 提示用户重新闯关
 */
@Composable
fun ShowErrorDialog(viewModel: SudokuViewModel) {
    AlertDialog(
        onDismissRequest = {

        },
        title = {
            Text(
                text = "很遗憾，闯关失败！",
                fontWeight = FontWeight.W700,
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Text(
                text = "失败是成功之母，要不在 try try?",
                fontSize = 16.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.sendEvent(ErrorDialogClick(false))
                },
            ) {
                Text(
                    "Try again?",
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.button
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.sendEvent(ErrorDialogClick(true))
                }
            ) {
                Text(
                    "返回主页",
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.button,
                    color = Color.Gray
                )
            }
        }
    )
}

@ExperimentalFoundationApi
@Composable
fun ShowHome(viewModel: SudokuViewModel) {
    val levelDats by viewModel.levelDats.collectAsState()
    val image = ImageBitmap.imageResource(id = R.mipmap.ic_lock)
    LazyVerticalGrid(
        cells = GridCells.Fixed(5),
        modifier = Modifier.border(2.dp, color = Color.LightGray)
    ) {
        items(levelDats.size) { index ->
            val level = levelDats[index]
            Card(modifier = Modifier.padding(3.dp,3.dp)) {
                Button(onClick = {
                    viewModel.createTable(level.id)
                }, enabled = index == 0,modifier = Modifier.drawWithContent {
                    drawContent()
                    if(!level.isPlay && level.time == 0){
                        drawImage(image = image,topLeft = Offset(size.width*0.7f,size.height*0.15f))
                    }

                }) {

                    Column {
                        Text(
                            text = if (index >= 9) "${index+1}" else "0${index+1}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Text(text = viewModel.getTime(level.time.toLong()))
                    }

                }

            }

        }
    }
}