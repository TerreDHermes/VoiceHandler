package com.example.voicehandler
import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.unit.Constraints
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voicehandler.database.firebase.AppFirebaseRepository
import com.example.voicehandler.database.room.AppRoomDatabase
import com.example.voicehandler.database.room.repository.RoomRepository
import com.example.voicehandler.model.Note
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.DB_TYPE
import com.example.voicehandler.utils.REPOSITORY
import com.example.voicehandler.utils.TYPE_FIREBASE
import com.example.voicehandler.utils.TYPE_ROOM
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val context = application

    fun RegistrationDatabase(type: String, onSuccess: ()->Unit){
        Log.d("CheckData", "MainViewModel RegistrationDatabase with type: $type")
        when(type){
            TYPE_FIREBASE -> {
                REPOSITORY = AppFirebaseRepository()
                REPOSITORY.registrationInDatabase(
                    {onSuccess()},
                    {
                        Log.d("checkData", "Error: ${it}")
                        val errorMessage = "Error: $it"
                        viewModelScope.launch(Dispatchers.Main) {
                            val context = getApplication<Application>().applicationContext
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }
    fun initDatabase(type: String, onSuccess: ()->Unit){
        Log.d("CheckData", "MainViewModel initDatabase with type: $type")
        when(type){
            TYPE_ROOM -> {
                val dao = AppRoomDatabase.genInstance(context = context).getRoomDao()
                REPOSITORY = RoomRepository(dao)
                onSuccess()
            }
            TYPE_FIREBASE -> {
                REPOSITORY = AppFirebaseRepository()
                REPOSITORY.connectToDatabase(
                    {onSuccess()},
                    {
                        Log.d("checkData", "Error: ${it}")
                        val errorMessage = "Error: $it"
                        viewModelScope.launch(Dispatchers.Main) {
                            val context = getApplication<Application>().applicationContext
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    fun addNote(note: Note, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            REPOSITORY.create(note = note){
                viewModelScope.launch(Dispatchers.Main){
                    onSuccess()
                }
            }
        }
    }

    fun updateNote(note: Note, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            REPOSITORY.update(note = note){
                viewModelScope.launch(Dispatchers.Main){
                    onSuccess()
                }
            }
        }
    }

    fun deleteNote(note: Note, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            REPOSITORY.delete(note = note){
                viewModelScope.launch(Dispatchers.Main){
                    onSuccess()
                }
            }
        }
    }

    fun reaAllNotes() = REPOSITORY.readAll

    fun signOut(onSuccess: () -> Unit){
        when(DB_TYPE.value){
            TYPE_FIREBASE,
                TYPE_ROOM ->{
                    REPOSITORY.signOut()
                DB_TYPE.value = Constants.Keys.EMPTY
                onSuccess()
                }
            else -> {Log.d("checkData", "signOut: Else: ${DB_TYPE.value}")}
        }
    }

}

class MainViewModelFactory(private val application: Application):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application = application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
}
}

