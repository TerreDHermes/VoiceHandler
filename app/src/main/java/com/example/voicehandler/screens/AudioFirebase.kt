package com.example.voicehandler.screens

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
        FileList(fileNames, navController, viewModel)
    }
}

@Composable
fun FileList(
    fileNames: List<String>,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        fileNames.forEach { fileName ->
            // Кнопка с названием файла
            Button(
                onClick = {
                    // Переход к экрану прослушивания записи, передача имени файла
                    //navController.navigate(Screen.AudioPlayer.route + "/$fileName")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = fileName)
            }
        }
    }
}