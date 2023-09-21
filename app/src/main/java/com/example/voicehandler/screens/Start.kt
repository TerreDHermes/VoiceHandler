package com.example.voicehandler.screens

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.voicehandler.MainViewModel
import com.example.voicehandler.MainViewModelFactory
import com.example.voicehandler.navigation.NavRoute
import com.example.voicehandler.ui.theme.VoiceHandlerTheme
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.LOGIN
import com.example.voicehandler.utils.PASSWORD
import com.example.voicehandler.utils.TYPE_FIREBASE
import com.example.voicehandler.utils.TYPE_ROOM
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartScreen(navController: NavHostController, viewModel: MainViewModel) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    var login by remember { mutableStateOf(Constants.Keys.EMPTY) }
    var password by remember { mutableStateOf(Constants.Keys.EMPTY) }

    val message = "Обновление прошло успешно!" // Замените это сообщение на то, которое вам нужно показать
    val duration = Toast.LENGTH_LONG // Вы можете изменить длительность уведомления
    val toast = Toast.makeText(LocalContext.current, message, duration)
    toast.setGravity(Gravity.TOP, 0, 0)

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetElevation = 5.dp,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetContent = {
            Surface {
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
                           viewModel.initDatabase(TYPE_FIREBASE){
                               navController.navigate(NavRoute.Main.route)
                           }
                        },
                        enabled = login.isNotEmpty() && password.isNotEmpty()
                    ) {
                        Text(text = Constants.Keys.SIGN_IN)
                    }
                }

            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = Constants.Keys.WHAT_WE_WILL_USE)
                Button(
                    onClick = {
                        viewModel.initDatabase(TYPE_ROOM){
                            navController.navigate(route = NavRoute.Main.route)
                        }

                    },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = Constants.Keys.ROOM_DATABASE)
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = Constants.Keys.FIREBASE_DATABASE)
                }
            }
        }
    }

}
@Preview(showBackground = true)
@Composable
fun prevStartScreen(){
    VoiceHandlerTheme(){
        val context = LocalContext.current
        val mViewModel: MainViewModel =
            viewModel(factory = MainViewModelFactory(context.applicationContext as Application))
        StartScreen(navController = rememberNavController(), viewModel = mViewModel)
    }
}


