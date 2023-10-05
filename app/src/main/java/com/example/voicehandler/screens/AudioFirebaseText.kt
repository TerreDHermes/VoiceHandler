package com.example.voicehandler.screens

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.voicehandler.MainViewModel
import com.example.voicehandler.R
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.DESTINATION_URI
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import android.provider.Settings
import android.view.Gravity
import android.widget.Toast


@Composable
fun AudioFirebaseTextScreen(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current


    //var destinationUri23 by remember { mutableStateOf(Constants.Keys.EMPTY)

    val storage = Firebase.storage

    // State для хранения списка названий файлов
    var fileNames by remember { mutableStateOf(emptyList<String>()) }

    // Загрузка списка названий файлов из Firebase Storage
    LaunchedEffect(Unit) {
        val storageReference = storage.reference.child("email/text")
        val listResult = storageReference.listAll().await()
        fileNames = listResult.items.map { it.name }
    }

    // Отображение списка названий файлов
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Список расшифровок:")
        Spacer(modifier = Modifier.height(16.dp))
        FileList2(fileNames, navController, viewModel, context)
    }
}

@Composable
fun FileList2(
    fileNames: List<String>,
    navController: NavHostController,
    viewModel: MainViewModel,
    context: Context
) {
    val scrollState = rememberScrollState()
    val message = "Файл загружен в папку Dowloads!" // Замените это сообщение на то, которое вам нужно показать
    val message2 = "Запись загружается!"
    val message3 = "Неверный  VERIFICATION_CODE"
    val duration = Toast.LENGTH_LONG // Вы можете изменить длительность уведомления
    val toast = Toast.makeText(LocalContext.current, message, duration)
    val toast2 = Toast.makeText(LocalContext.current, message2, duration)
    val toast3 = Toast.makeText(LocalContext.current, message3, duration)
    toast.setGravity(Gravity.TOP, 0, 0)

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        fileNames.forEach { fileName ->
            // Кнопка с названием файла
            Button(
                onClick = {
                    val storageReference = Firebase.storage.reference.child("email/text/$fileName")
                    val downloadUrl2 = storageReference.downloadUrl.toString()
                    Log.d("DownloadFile", "Начало загрузки файла: $downloadUrl2")
                    toast2.show()
                    // Получаем URL файла
                    storageReference.downloadUrl.addOnSuccessListener { fileUrl ->
                        // Вызываем функцию для загрузки файла на телефон
                        toast.show()
                        downloadFile(context, fileName, fileUrl.toString())
                        Log.d("DownloadFile", "Загрузка файла завершена: $downloadUrl2")

                    }.addOnFailureListener { exception ->
                        Log.e("DownloadFile", "Ошибка загрузки файла: $downloadUrl2", exception)

                        // Обработка ошибки, если не удалось получить URL файла
                        // Здесь можно вывести сообщение об ошибке
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = fileName)
            }
        }
    }
}

fun downloadFile(context: Context, fileName: String, fileUrl: String) {
    val request = DownloadManager.Request(Uri.parse(fileUrl))
    request.setTitle("Загрузка файла $fileName")
    request.setDescription("Загрузка файла с Firebase Storage")

    // Указываем путь для сохранения файла на устройстве
    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val destinationUri = Uri.withAppendedPath(Uri.fromFile(directory), fileName)
    DESTINATION_URI = destinationUri
    request.setDestinationUri(destinationUri)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}