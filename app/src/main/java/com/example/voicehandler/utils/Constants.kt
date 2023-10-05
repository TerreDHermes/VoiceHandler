package com.example.voicehandler.utils

import androidx.compose.runtime.mutableStateOf
import com.example.voicehandler.database.DatabaseRepository
import com.google.firebase.auth.PhoneAuthProvider

const val TYPE_DATABASE = "type_database"
const val TYPE_ROOM = "type_room"
const val TYPE_FIREBASE = "type_firebase"
const val FIREBASE_ID = "firebaseId"

lateinit var REPOSITORY: DatabaseRepository
lateinit var LOGIN:String
lateinit var PASSWORD:String
lateinit var VERIFICATION_CODE:String
lateinit var CHECK:String
lateinit var AUDIO_FILE_PATH:String
lateinit var AUDIO_FILE_NAME:String
//lateinit var PHONE_NUMBER:String
//lateinit var VERIFICATION_ID:String
//lateinit var VERIFICATION_CODE:String
//lateinit var FORCE_RESENDING_TOKEN: PhoneAuthProvider.ForceResendingToken



var DB_TYPE =  mutableStateOf("")

object Constants{
    object Keys{
        const val NOTE_DATABASE = "notes_database"
        const val NOTES_TABLE = "notes_table"
        const val ADD_NEW_NOTE = "Add new note"
        const val NOTE_TITLE = "Note title"
        const val NOTE_SUBTITLE = "Note subtitle"
        const val ADD_NOTE = "Add note"
        const val TITLE = "title"
        const val SUBTITLE = "subtitle"
        const val WHAT_WE_WILL_USE = "What we will use?"
        const val ROOM_DATABASE = "Local database"
        const val FIREBASE_DATABASE = "Firebase database"
        const val ID = "Id"
        const val NONE = "none"
        const val UPDATE  = "UPDATE"
        const val DELETE = "DELETE"
        const val NAV_BACK = "NAV_BACK"
        const val EDIT_NOTE = "Edit note"
        const val EMPTY = ""
        const val UPDATE_NOTE  = "Update note"
        const val SIGN_IN = "Sign In"
        const val LOG_IN = "Log In"
        const val LOGIN_TEXT = "Login (email)"
        const val PASSWORD_TEXT = "Password"
        const val REGISTRATION = "Registration"
        const val WHAT_DO_YOU_WANT = "What do you want to do?"
        const val BACK = "Back"
        const val PHONE_NUMBER = "Phone number"
        const val CHECK_VERIFICATION_CODE = "Check verification code"
        const val VERIFICATION_CODE = "Verification code"
        const val SEND = "Send"
        const val FILE_NAME = "Введите имя для записи"
        const val SAVE = "Сохранить"

    }

    object Screens{
        const val START_SCREEN = "start_screen"
        const val MAIN_SCREEN  = "main_screen"
        const val ADD_SCREEN = "add_screen"
        const val NOTE_SCREEN = "note_screen"
        const val LOGREG_SCREEN = "logreg_screen"
        const val REGISTRATION_SCREEN = "registration_screen"
        const val LOGIN_SCREEN = "login_screen"
        const val CHECK_EMAIL_SCREEN = "check_email_screen"
        const val CHECK_EMAIL_LOGIN_SCREEN = "check_email_login_screen"
        const val RECORD_SCREEN = "record_screen"
    }
}