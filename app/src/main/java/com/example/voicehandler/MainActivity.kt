package com.example.voicehandler

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.voicehandler.navigation.NavRoute
import com.example.voicehandler.navigation.NotesNavHost
import com.example.voicehandler.ui.theme.VoiceHandlerTheme
import com.example.voicehandler.utils.DB_TYPE

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val mViewModel: MainViewModel =
                viewModel(factory = MainViewModelFactory(context.applicationContext as Application))
            val navController = rememberNavController()
            VoiceHandlerTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                                        ){
                                    Text(text = "Voice App")
                                    if(DB_TYPE.value.isNotEmpty()){
                                        Icon(
                                            imageVector = Icons.Default.ExitToApp,
                                            contentDescription = "",
                                            modifier = Modifier.clickable {
                                                mViewModel.signOut {
                                                    navController.navigate(NavRoute.Start.route)
                                                    {
                                                        popUpTo(NavRoute.Start.route){
                                                            inclusive = true
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }

                                }
                            },
                            backgroundColor = Color.Blue,
                            contentColor = Color.White,
                            elevation = 12.dp
                        )

                    },
                    content = {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            NotesNavHost(mViewModel, navController)
                        }
                    }
                )

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VoiceHandlerTheme() {

    }
}