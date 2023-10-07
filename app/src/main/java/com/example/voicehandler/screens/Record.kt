package com.example.voicehandler.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.voicehandler.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import com.example.voicehandler.navigation.NavRoute
import com.example.voicehandler.utils.AUDIO_FILE_NAME
import com.example.voicehandler.utils.AUDIO_FILE_PATH
import com.example.voicehandler.utils.CHECK
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.LOGIN
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun uploadRecordingToFirebaseStorage(context: Context, audioFilePath: String, fileName: String) {
    var email = LOGIN
    if (email == null){
        email = "Null"
    }
    val storage = Firebase.storage
    val storageRef = storage.reference
    val audioRef = storageRef.child("$email/audio/$fileName") // Укажите путь, куда вы хотите сохранить файл

    //val file = Uri.fromFile(File(audioFilePath))
    val fileUri = FileProvider.getUriForFile(context, "com.example.voicehandler.fileprovider", File(audioFilePath))
    Log.d("checkData", "audioFilePath: ${audioFilePath}")
    Log.d("checkData", "file: ${fileUri}")

    val uploadTask = audioRef.putFile(fileUri)
    Log.d("checkData", "uploadTask: ${uploadTask}")

    uploadTask.addOnSuccessListener {
        Log.d("checkData", "Успешная загрузка")
        // Успешно загружено
        // Вы можете добавить здесь дополнительную логику, например, сохранение ссылки на файл в базу данных
    }.addOnFailureListener {
        Log.d("checkData", "ничего не загрузили")
        // Ошибка при загрузке
    }
}

fun getAudioFilePath(context: Context): String {
    val audioFolder = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS),
        "audio"
    )
    if (!audioFolder.exists()) {
        audioFolder.mkdirs()
    }

    val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
    val audioFileName = AUDIO_FILE_NAME
    val audioFile = File(audioFolder, audioFileName)

    return audioFile.absolutePath
}
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecordScreen(navController: NavHostController, viewModel: MainViewModel) {
    val message = "Запись успешно загружена в базу данных!" // Замените это сообщение на то, которое вам нужно показать
    val message2 = "Запись остановлена и сохранена!"
    val message3 = "Неверный  VERIFICATION_CODE"
    val duration = Toast.LENGTH_LONG // Вы можете изменить длительность уведомления
    val toast = Toast.makeText(LocalContext.current, message, duration)
    val toast2 = Toast.makeText(LocalContext.current, message2, duration)
    val toast3 = Toast.makeText(LocalContext.current, message3, duration)
    toast.setGravity(Gravity.TOP, 0, 0)

    var audio_file_name by remember { mutableStateOf(Constants.Keys.EMPTY) }
    var check_name by remember { mutableStateOf(Constants.Keys.EMPTY) }
    var isRecording by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(0L) }
    val timerJob = remember { mutableStateOf<Job?>(null) }
    val context = LocalContext.current

    var isTimeZero by remember { mutableStateOf(true) }

    FirebaseApp.initializeApp(context)

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            // Разрешение предоставлено, можно начать запись
            //startRecording(context, audioFilePath, isRecording, timerJob, viewModel)
        } else {
            // Пользователь отклонил разрешение, вы можете обработать это событие здесь
        }
    }

    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 10.dp),
        ) {

            Text(
                text = "Запись голоса",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 8.dp),


            )
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = audio_file_name,
                onValueChange = { audio_file_name = it },
                label = { Text(text = Constants.Keys.FILE_NAME) },
                isError = audio_file_name.isEmpty(),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.8f),
                enabled = check_name.isEmpty()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                onClick = {
                    AUDIO_FILE_NAME = "$audio_file_name.3gp"
                    check_name = audio_file_name
                },
                enabled = audio_file_name.isNotEmpty() && check_name.isEmpty()
            ) {
                Text(text = Constants.Keys.SAVE)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val formattedTime = remember(currentTime) {
            String.format("%.3f сек", currentTime / 1000f)
        }

        Text(text = formattedTime, fontSize = 24.sp)

        Button(
            onClick = {
                if (!isRecording) {
                    // Проверяем и запрашиваем разрешение на запись аудио
                    if (checkAudioRecordingPermission(context)) {
                        AUDIO_FILE_PATH = getAudioFilePath(context)
                        currentTime = 0L
                        Log.d("checkData", "audioFilePath: ${AUDIO_FILE_PATH}")
                        mediaRecorder = MediaRecorder()
                        mediaRecorder?.apply {
                            setAudioSource(MediaRecorder.AudioSource.MIC)
                            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                            setOutputFile(AUDIO_FILE_PATH)

                            try {
                                prepare()
                                start()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            isTimeZero = true

                            timerJob.value = viewModel.viewModelScope.launch {
                                while (isRecording) {
                                    delay(100)
                                    currentTime += 100
                                }
                            }
                        }

                        isRecording = true
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                } else {
                    isRecording = false
                    stopRecording(mediaRecorder, timerJob)
                    isTimeZero = false
                    toast2.show()
                    Log.d("checkData", "audioFilePath3: ${AUDIO_FILE_PATH}")
                }
            },
            enabled = check_name.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(if (!isRecording) "Начать новую запись" else "Остановить запись")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Сохранить запись
                //stopRecording(mediaRecorder, timerJob)
                uploadRecordingToFirebaseStorage(context, AUDIO_FILE_PATH, AUDIO_FILE_NAME)
                toast.show()
                // Дополнительные действия по сохранению записи
            },
            enabled = check_name.isNotEmpty() && !isTimeZero,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Сохранить запись (анализ)")
        }




    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .weight(1f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    navController.navigate(NavRoute.Start.route)
                    {
                        popUpTo(NavRoute.Start.route) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text("Назад",
                    textAlign = TextAlign.Center)
            }

            // Кнопка "Создать новую запись"
            Button(
                onClick = {
                    navController.navigate(NavRoute.Record.route)
                    {
                        popUpTo(NavRoute.Record.route) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text("Новая запись",
                    textAlign = TextAlign.Center)
            }

            // Кнопка "Старые записи"
            Button(
                onClick = {
                    navController.navigate(NavRoute.Storage.route)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text("Все записи",
                    textAlign = TextAlign.Center)
            }
        }
    }




    // Используем LaunchedEffect для обновления currentTime
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                currentTime += 100
                delay(100)
            }
        }
    }


}

private fun checkAudioRecordingPermission(context: Context): Boolean {
    return PackageManager.PERMISSION_GRANTED ==
            context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
}

private fun startRecording(
    context: Context,
    audioFilePath: String,
    isRecording: Boolean,
    timerJob: MutableState<Job?>,
    viewModel: MainViewModel
) {
    if (!isRecording) {
        // Начинаем запись
        val mediaRecorder = MediaRecorder()
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            timerJob.value = viewModel.viewModelScope.launch {
                while (isRecording) {
                    delay(100)
                }
            }
        }
    }
}

private fun stopRecording(mediaRecorder: MediaRecorder?, timerJob: MutableState<Job?>) {
    // Останавливаем запись
    timerJob.value?.cancel()

    mediaRecorder?.apply {
        try {
            stop()
            reset()
            release()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}