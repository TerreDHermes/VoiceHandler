package com.example.voicehandler.screens

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.voicehandler.MainViewModel
import com.example.voicehandler.MainViewModelFactory
import com.example.voicehandler.navigation.NavRoute
import com.example.voicehandler.ui.theme.VoiceHandlerTheme
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.TYPE_FIREBASE
import com.example.voicehandler.utils.TYPE_ROOM


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartScreen(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val mViewModel: MainViewModel =
        viewModel(factory = MainViewModelFactory(context.applicationContext as Application))
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
                         mViewModel.initDatabase(TYPE_ROOM){
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
                   mViewModel.initDatabase(TYPE_FIREBASE){
                       navController.navigate(route = NavRoute.Main.route)
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


