package com.ng.s33590850.medtrack.data

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object AuthManager {
    var _patientID: String = ""
    val preferenceName = "medtrack"
    val preferenceKey = "logged patient ID"

    fun login(context: Context, patientID: String) {
        _patientID = patientID
        val sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            .edit()
        sharedPref.putString(preferenceKey,_patientID).apply()
    }

    fun logout(context: Context) {
        _patientID = ""
        val sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            .edit()
        sharedPref.putString(preferenceKey,null).apply()
    }

    fun getPatientId(context: Context): String {
        if (_patientID != ""){return _patientID}

        val sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val sharedPrefResult = sharedPref.getString(preferenceKey,null)
        if (sharedPrefResult == null){
            return ""
        } else{
            return sharedPrefResult
        }
    }
}