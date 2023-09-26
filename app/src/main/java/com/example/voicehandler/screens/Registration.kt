package com.example.voicehandler.screens

import android.annotation.SuppressLint
import android.app.Application
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.voicehandler.MainViewModel
import com.example.voicehandler.MainViewModelFactory
import com.example.voicehandler.navigation.NavRoute
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.DB_TYPE
import com.example.voicehandler.utils.LOGIN
import com.example.voicehandler.utils.PASSWORD
import com.example.voicehandler.utils.TYPE_FIREBASE
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegistrationScreen(navController: NavHostController, viewModel: MainViewModel) {
    var login by remember { mutableStateOf(Constants.Keys.EMPTY) }
    var password by remember { mutableStateOf(Constants.Keys.EMPTY) }

    val context = LocalContext.current
    val mViewModel: MainViewModel =
        viewModel(factory = MainViewModelFactory(context.applicationContext as Application))

    val message = "Аккаунт успешно создан!" // Замените это сообщение на то, которое вам нужно показать
    val message2 = "Аккаунт не был создан (Проверьте правильность почты и пароля)!"
    val duration = Toast.LENGTH_LONG // Вы можете изменить длительность уведомления
    val toast = Toast.makeText(LocalContext.current, message, duration)
    val toast2 = Toast.makeText(LocalContext.current, message2, duration)
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
                label = { Text(text = Constants.Keys.LOGIN_TEXT)},
                isError = login.isEmpty()
            )
            OutlinedTextField(
                value = password,
                onValueChange = {password=it},
                label = { Text(text = Constants.Keys.PASSWORD_TEXT)},
                isError = password.isEmpty()
            )
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    LOGIN = login
                    PASSWORD = password
                    try {
                        viewModel.RegistrationDatabase(TYPE_FIREBASE) {
                            DB_TYPE.value = TYPE_FIREBASE
                            mViewModel.signOut {
                                toast.show()
                                navController.navigate(NavRoute.LogReg.route)
                                {
                                    popUpTo(NavRoute.LogReg.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    } catch (e: FirebaseAuthInvalidCredentialsException){
                        toast2.show()
                    }

                },
                enabled = login.isNotEmpty() && password.isNotEmpty()
            ) {
                Text(text = Constants.Keys.REGISTRATION)
            }
        }
    }
}