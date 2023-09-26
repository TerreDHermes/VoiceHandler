package com.example.voicehandler.database.firebase


import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import com.example.voicehandler.database.DatabaseRepository
import com.example.voicehandler.model.Note
import com.example.voicehandler.utils.LOGIN
import com.example.voicehandler.utils.PASSWORD
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import android.view.Gravity
import android.widget.Toast
import com.example.voicehandler.utils.Constants
import com.example.voicehandler.utils.FIREBASE_ID
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database


class AppFirebaseRepository: DatabaseRepository {

    private val mAuth = FirebaseAuth.getInstance()

    private val database = Firebase.database.reference
        //.child(mAuth.currentUser?.uid.toString())

    override val readAll: LiveData<List<Note>> = AllNotesLiveData()
    //private var allNotesLiveData: AllNotesLiveData? = null
    override suspend fun create(note: Note, onSuccess: () -> Unit) {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userUid = currentUser.uid
            val noteId = database.push().key.toString()
            val mapNotes = hashMapOf<String, Any>()
            mapNotes[FIREBASE_ID] = noteId
            mapNotes[Constants.Keys.TITLE] = note.title
            mapNotes[Constants.Keys.SUBTITLE] = note.subtitle

            database.child(userUid).child(noteId)
                .updateChildren(mapNotes)
                .addOnSuccessListener {
                    //val mAuth = FirebaseAuth.getInstance()
                    val currentUser = mAuth.currentUser
                    if (currentUser != null) {
                        Log.d("checkData", "create note UID: ${currentUser.uid}")
                        Log.d("checkData", "create note Email: ${currentUser.email}")
                    } else {
                        Log.d("checkData", "create note - Current user is null")
                    }
                    onSuccess()
                }
                .addOnFailureListener { Log.d("CheckData", "Failed to add new note") }
        } else{
            Log.d("CheckData", "Current user is null")
        }
    }

    override suspend fun update(note: Note, onSuccess: () -> Unit) {
        val noteId = note.firebaseId
        val mapNotes = hashMapOf<String, Any>()
        mapNotes[FIREBASE_ID] = noteId
        mapNotes[Constants.Keys.TITLE] = note.title
        mapNotes[Constants.Keys.SUBTITLE] = note.subtitle

        database.child(noteId)
            .updateChildren(mapNotes)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { Log.d("CheckData", "Failed to update note") }

    }

    override suspend fun delete(note: Note, onSuccess: () -> Unit) {
        database.child(note.firebaseId).removeValue()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { Log.d("CheckData", "Failed to delete note") }
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun connectToDatabase(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        mAuth.signInWithEmailAndPassword(LOGIN, PASSWORD)
            .addOnSuccessListener {
                //val mAuth = FirebaseAuth.getInstance()
                val currentUser = mAuth.currentUser
                if (currentUser != null) {
                    Log.d("checkData", "connectToDatabase UID: ${currentUser.uid}")
                    Log.d("checkData", "connectToDatabase Email: ${currentUser.email}")
                } else {
                    Log.d("checkData", "connectToDatabase - Current user is null")
                }
                //allNotesLiveData = AllNotesLiveData()
                onSuccess()
            }
            .addOnFailureListener {onFail(it.message.toString())
                //mAuth.createUserWithEmailAndPassword(LOGIN, PASSWORD)
                  //  .addOnSuccessListener { onSuccess() }
                    //.addOnFailureListener { onFail(it.message.toString()) }
            }
    }


    override fun registrationInDatabase(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        mAuth.createUserWithEmailAndPassword(LOGIN, PASSWORD)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail(it.message.toString()) }
            }

//    override val readAll: LiveData<List<Note>>
//        get() {
//            if (allNotesLiveData == null) {
//                throw IllegalStateException("AllNotesLiveData is not initialized yet")
//            }
//            return allNotesLiveData!!
//        }
//

    fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }
}