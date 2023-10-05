package com.example.voicehandler.screens

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
import androidx.navigation.NavHostController
import com.example.voicehandler.MainViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import android.provider.Settings
import android.view.Gravity
import android.widget.Toast
import java.io.File

@Composable
fun AudioFirebaseScreen(navController: NavHostController, viewModel: MainViewModel) {


    val context = LocalContext.current
    val storage = Firebase.storage

    // State для хранения списка названий файлов
    var fileNames by remember { mutableStateOf(emptyList<String>()) }

    // Загрузка списка названий файлов из Firebase Storage
    LaunchedEffect(Unit) {
        val storageReference = storage.reference.child("email/audio")
        val listResult = storageReference.listAll().await()
        fileNames = listResult.items.map { it.name }
    }

    // Отображение списка названий файлов
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Список аудиофайлов:")
        Spacer(modifier = Modifier.height(16.dp))
        FileList(fileNames, navController, viewModel,context)
    }
}

@Composable
fun FileList(
    fileNames: List<String>,
    navController: NavHostController,
    viewModel: MainViewModel,
    context: Context
) {

    val message = "Запись загружена в папку Dowloads!" // Замените это сообщение на то, которое вам нужно показать
    val message2 = "Запись загружается!"
    val message3 = "Неверный  VERIFICATION_CODE"
    val duration = Toast.LENGTH_LONG // Вы можете изменить длительность уведомления
    val toast = Toast.makeText(LocalContext.current, message, duration)
    val toast2 = Toast.makeText(LocalContext.current, message2, duration)
    val toast3 = Toast.makeText(LocalContext.current, message3, duration)
    toast.setGravity(Gravity.TOP, 0, 0)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        fileNames.forEach { fileName ->
            // Кнопка с названием файла
            Button(
                onClick = {
                    val storageReference = Firebase.storage.reference.child("email/audio/$fileName")
                    val downloadUrl2 = storageReference.downloadUrl.toString()
                    Log.d("DownloadFile", "Начало загрузки файла: $downloadUrl2")
                    toast2.show()
                    storageReference.downloadUrl.addOnSuccessListener { fileUrl ->
                            // Вызываем функцию для загрузки файла на телефон
                            toast.show()
                            downloadFile2(context, fileName, fileUrl.toString())
                            Log.d("DownloadFile", "Загрузка файла завершена: $downloadUrl2")
                        }.addOnFailureListener { exception ->
                            Log.e("DownloadFile", "Ошибка загрузки файла: $downloadUrl2", exception)
                            // Обработка ошибки, если не удалось получить URL файла
                            // Здесь можно вывести сообщение об ошибке
                        }
//                    if (!isNotificationPermissionGranted(context)) {
//                        // Если разрешение на отправку уведомлений отсутствует, запросите его
//                        createNotificationChannel(context)
//                        requestNotificationPermission(context)
//                    } else {
//                        // Получаем URL файла и начинаем загрузку
//                        storageReference.downloadUrl.addOnSuccessListener { fileUrl ->
//                            // Вызываем функцию для загрузки файла на телефон
//                            Log.d("DownloadFile", "Загрузка файла завершена: $downloadUrl2")
//                            downloadFile2(context, fileName, fileUrl.toString())
//                        }.addOnFailureListener { exception ->
//                            Log.e("DownloadFile", "Ошибка загрузки файла: $downloadUrl2", exception)
//                            // Обработка ошибки, если не удалось получить URL файла
//                            // Здесь можно вывести сообщение об ошибке
//                        }
//                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = fileName)
            }
        }
    }
}
fun downloadFile2(context: Context, fileName: String, fileUrl: String) {
    val request = DownloadManager.Request(Uri.parse(fileUrl))
    request.setTitle("Загрузка файла $fileName")
    request.setDescription("Загрузка файла с Firebase Storage")

    // Указываем путь для сохранения файла на устройстве
    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val destinationUri = Uri.withAppendedPath(Uri.fromFile(directory), fileName)
    request.setDestinationUri(destinationUri)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
//val downloadId = downloadManager.enqueue(request)
//    val onCompleteReceiver = DownloadReceiver(context, downloadId)
//    onCompleteReceiver.setFileOpenAction {
//        // Этот код будет выполнен при нажатии на уведомление для открытия файла
//        val filePath = destinationUri.toString()
//        val fileUri = Uri.fromFile(File(filePath))
//        val openIntent = Intent(Intent.ACTION_VIEW)
//        openIntent.setDataAndType(fileUri, "audio/*")
//        openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
//        context.startActivity(openIntent)
//    }
}


fun requestNotificationPermission(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = notificationManager.getNotificationChannel("default") // Замените "default" на ID вашего канала
        return channel.importance != NotificationManager.IMPORTANCE_NONE
    }
    return true // Верните true для версий Android до 8.0
}

class DownloadReceiver(private val context: Context, private val downloadId: Long) {

    private var fileOpenAction: (() -> Unit)? = null

    fun setFileOpenAction(action: () -> Unit) {
        fileOpenAction = action
    }

    fun onDownloadComplete() {
        // Вызываем действие для открытия файла
        fileOpenAction?.invoke()
    }
}
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "default" // Идентификатор вашего канала
        val channelName = "Default Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}