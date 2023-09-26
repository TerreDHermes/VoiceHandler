package com.example.voicehandler.screens

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.voicehandler.MainViewModel
import com.example.voicehandler.MainViewModelFactory
import com.example.voicehandler.database.firebase.AppFirebaseRepository
import com.example.voicehandler.navigation.NavRoute
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.DB_TYPE
import com.example.voicehandler.utils.LOGIN
import com.example.voicehandler.utils.PASSWORD
import com.example.voicehandler.utils.TYPE_FIREBASE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LogInScreen(navController: NavHostController, viewModel: MainViewModel) {
    var login by remember { mutableStateOf(Constants.Keys.EMPTY) }
    var password by remember { mutableStateOf(Constants.Keys.EMPTY) }

    val context = LocalContext.current
    val mViewModel: MainViewModel =
        viewModel(factory = MainViewModelFactory(context.applicationContext as Application))



    //val user = AppFirebaseRepository.getCurrentUser()
    val message = "Вы успешно вошли в аккаунт!" // Замените это сообщение на то, которое вам нужно показать
    val duration = Toast.LENGTH_LONG // Вы можете изменить длительность уведомления
    val toast = Toast.makeText(LocalContext.current, message, duration)
    toast.setGravity(Gravity.TOP, 0, 0)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 32.dp)
        ) {
            Text(
                text = Constants.Keys.LOG_IN,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = login,
                onValueChange = {login=it},
                label = { Text(text = Constants.Keys.LOGIN_TEXT) },
                isError = login.isEmpty()
            )
            OutlinedTextField(
                value = password,
                onValueChange = {password=it},
                label = { Text(text = Constants.Keys.PASSWORD_TEXT) },
                isError = password.isEmpty()
            )
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    LOGIN = login
                    PASSWORD = password
                    viewModel.initDatabase(TYPE_FIREBASE) {
                        val mAuth = FirebaseAuth.getInstance()
                        val currentUser = mAuth.currentUser
                        if (currentUser != null) {
                            Log.d("checkData", "UID: ${currentUser.uid}")
                            Log.d("checkData", "Email: ${currentUser.email}")
                        } else {
                            Log.d("checkData", "Current user is null")
                        }

                        toast.show()
                        DB_TYPE.value = TYPE_FIREBASE
                        navController.navigate(NavRoute.Main.route){
                            popUpTo(NavRoute.Main.route) {
                                inclusive = true
                            }
                        }
                        }


                },
                enabled = login.isNotEmpty() && password.isNotEmpty()
            ) {
                Text(text = Constants.Keys.LOG_IN)
            }
        }
    }
}