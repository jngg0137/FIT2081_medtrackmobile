package com.ng.s33590850.medtrack.data.patients

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PatientViewModel(context: Context): ViewModel(){
    val repository = PatientRepository(context)
    val allPatients = repository.getAllPatients()

    fun insertPatient(patient: Patient) = viewModelScope.launch {
        repository.insertPatient(patient)
    }

    suspend fun changePassword(newPassword: String, patientID: String){
        repository.changePassword(newPassword,patientID)
    }

    suspend fun getPatientByPhone(phoneNumber: String): Patient?{
        return repository.getPatientByPhone(phoneNumber)
    }

    suspend fun getPatientByPatientID(patientID: String): Patient?{
        return repository.getPatientByPatientID(patientID)
    }

    suspend fun getPatientByPatientIDAndPassword(patientID: String,password: String): Patient?{
        return repository.getPatientByPatientIDAndPassword(patientID,password)
    }

    suspend fun getPatientByPatientIDAndPhoneNumber(patientID: String,phoneNumber: String): Patient?{
        return repository.getPatientByPatientIDAndPhoneNumber(patientID,phoneNumber)
    }

    suspend fun getPatientByPhoneAndPassword(phoneNumber: String,password: String): Patient?{
        return repository.getPatientByPhoneAndPassword(phoneNumber,password)
    }

    suspend fun getLargestPatientID(): Int{
        return repository.getLargestPatientID()
    }

    fun getPatientNameByID(patientID: String): Flow<String> {
        return repository.getPatientNameByID(patientID)
    }

    suspend fun getPasswordByPatientID(patientID: String): String?{
        return repository.getPasswordByPatientID(patientID)
    }

    fun getPatientPhoneNumberByID(patientID: String): Flow<String>{
        return repository.getPatientPhoneNumberByID(patientID)
    }

    class PatientViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PatientViewModel(context) as T
    }
}