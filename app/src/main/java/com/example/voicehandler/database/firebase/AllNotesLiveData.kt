package com.example.voicehandler.database.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.voicehandler.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AllNotesLiveData : LiveData<List<Note>>(){
   // private val mAuth = FirebaseAuth.getInstance()
    //private val database = Firebase.database.reference
      //  .child(mAuth.currentUser?.uid.toString())

    private val listener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val notes = mutableListOf<Note>()
            snapshot.children.map {
                notes.add(it.getValue(Note::class.java)?: Note())
            }
            value= notes
        }

        override fun onCancelled(error: DatabaseError) {}

    }



    override fun onActive() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val database = Firebase.database.reference.child(currentUser.uid)
            Log.d("checkData", "UIDDDDD: ${currentUser.uid}")
            database.addValueEventListener(listener)
            super.onActive()
        } else{
            Log.d("CheckData", "Current user is null")
        }
    }

    override fun onInactive() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val database = Firebase.database.reference.child(currentUser.uid)
            database.removeEventListener(listener)
            super.onInactive()
        } else{
            Log.d("CheckData", "Current user is null")
        }
    }
}
