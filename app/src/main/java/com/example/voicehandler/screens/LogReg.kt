package com.example.voicehandler.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.voicehandler.MainViewModel
import com.example.voicehandler.navigation.NavRoute
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.DB_TYPE
import com.example.voicehandler.utils.TYPE_ROOM

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LogRegScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = Constants.Keys.WHAT_DO_YOU_WANT)
            Button(
                onClick = {
                          navController.navigate(route = NavRoute.LogIn.route)
                },
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = Constants.Keys.LOG_IN)
            }
            Button(
                onClick = {
                          navController.navigate(route = NavRoute.Registration.route)
                },
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = Constants.Keys.REGISTRATION)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                          navController.navigate(route = NavRoute.Start.route)
                },
                modifier = Modifier
                    .width(100.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = Constants.Keys.BACK)
            }
        }
    }
}