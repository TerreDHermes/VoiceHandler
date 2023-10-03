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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.google.firebase.database.ktx.database
import java.util.concurrent.TimeUnit


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
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userUid = currentUser.uid
            val noteId = note.firebaseId
            val mapNotes = hashMapOf<String, Any>()
            mapNotes[FIREBASE_ID] = noteId
            mapNotes[Constants.Keys.TITLE] = note.title
            mapNotes[Constants.Keys.SUBTITLE] = note.subtitle

            database.child(userUid).child(noteId)
                .updateChildren(mapNotes)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { Log.d("CheckData", "Failed to update note") }
        } else {
            Log.d("CheckData", "Current user is null")
        }

    }

    override suspend fun delete(note: Note, onSuccess: () -> Unit) {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userUid = currentUser.uid
            database.child(userUid).child(note.firebaseId).removeValue()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { Log.d("CheckData", "Failed to delete note") }
        } else {
            Log.d("CheckData", "Current user is null")
        }
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



//    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            // This callback will be invoked in two situations:
//            // 1) Instant verification. In some cases, the phone number can be
//            //    instantly verified without needing to send or enter a verification
//            //    code. You can disable this feature by calling
//            //    PhoneAuthOptions.builder#requireSmsValidation(true) when building
//            //    the options to pass to PhoneAuthProvider#verifyPhoneNumber().
//            // 2) Auto-retrieval. On some devices, Google Play services can
//            //    automatically detect the incoming verification SMS and perform
//            //    verification without user action.
//            //this@AppFirebaseRepository.credential = credential
//        }
//
//        override fun onVerificationFailed(e: FirebaseException) {
//            // This callback is invoked in response to invalid requests for
//            // verification, like an incorrect phone number.
//            if (e is FirebaseAuthInvalidCredentialsException) {
//                Log.d("checkData", "Invalid request")
//                // Invalid request
//                // ...
//            } else if (e is FirebaseTooManyRequestsException) {
//                Log.d("checkData", "The SMS quota for the project has been exceeded")
//                // The SMS quota for the project has been exceeded
//                // ...
//            }
//            Log.d("checkData", "Show a message and update the UI")
//            // Show a message and update the UI
//            // ...
//        }
//
//        override fun onCodeSent(
//            verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken
//        ) {
//            // The SMS verification code has been sent to the provided phone number.
//            // We now need to ask the user to enter the code and then construct a
//            // credential by combining the code with a verification ID.
//            // Save the verification ID and resending token for later use.
//            Log.d("checkData", "start - VERIFICATION_ID: ")
//            VERIFICATION_ID = verificationId
//            Log.d("checkData", "end - VERIFICATION_ID: ")
//            FORCE_RESENDING_TOKEN = forceResendingToken
//            //this@MainActivity.verificationId = verificationId
//            //this@MainActivity.forceResendingToken = forceResendingToken
//            // ...
//        }
//    }


//    override fun registrationInDatabaseWithPhone(onSuccess: () -> Unit, onFail: (String) -> Unit) {
//        val currentUser = mAuth.currentUser
//        if (currentUser != null) {
//            Log.d("checkData", "connectToDatabase with Phone... email: ${currentUser.email}")
//            currentUser.multiFactor.session
//                .addOnCompleteListener{ task ->
//                    if (task.isSuccessful){
//                        val multiFactorSession = task.result
//                        val phoneAuthOptions = PhoneAuthOptions.newBuilder()
//                            .setPhoneNumber(PHONE_NUMBER)
//                            .setTimeout(30L, TimeUnit.SECONDS)
//                            .setMultiFactorSession(multiFactorSession)
//                            .setCallbacks(callbacks)
//                            .build()
//                        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
//                    }
//                }
//            Log.d("checkData", "VERIFICATION_ID end end: ")
//            val credential = PhoneAuthProvider.getCredential(VERIFICATION_ID, VERIFICATION_CODE)
//            val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
//
//            FirebaseAuth.getInstance()
//                .currentUser
//                ?.multiFactor
//                ?.enroll(multiFactorAssertion, "My personal phone number")
//                ?.addOnCompleteListener { onSuccess() }
//                ?.addOnFailureListener { onFail(it.message.toString()) }
//
//        } else {
//            onFail("Current user is null")
//        }
//    }

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