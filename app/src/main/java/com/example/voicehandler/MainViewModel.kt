package com.example.voicehandler
import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voicehandler.database.room.AppRoomDatabase
import com.example.voicehandler.database.room.repository.RoomRepository
import com.example.voicehandler.model.Note
import com.example.voicehandler.utils.REPOSITORY
import com.example.voicehandler.utils.TYPE_FIREBASE
import com.example.voicehandler.utils.TYPE_ROOM

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val context = application


    fun initDatabase(type: String, onSuccess: ()->Unit){
        Log.d("CheckData", "MainViewModel initDatabase with type: $type")
        when(type){
            TYPE_ROOM -> {
                val dao = AppRoomDatabase.genInstance(context = context).getRoomDao()
                REPOSITORY = RoomRepository(dao)
                onSuccess()
            }
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

