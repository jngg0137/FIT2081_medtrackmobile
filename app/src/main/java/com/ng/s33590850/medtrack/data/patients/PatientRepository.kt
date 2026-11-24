package com.ng.s33590850.medtrack.data.patients

import android.content.Context
import com.ng.s33590850.medtrack.data.MTDatabase
import kotlinx.coroutines.flow.Flow

class PatientRepository(context: Context) {
    private val patientDao = MTDatabase.getDatabase(context).patientDao()

    suspend fun insertPatient(patient: Patient) {
        patientDao.insert(patient)
    }

    suspend fun changePassword(newPassword: String, patientID: String){
        patientDao.changePassword(newPassword,patientID)
    }

    suspend fun getPatientByPhone(phoneNumber: String): Patient?{
        return patientDao.getPatientByPhone(phoneNumber)
    }

    suspend fun getPatientByPatientID(patientID: String): Patient?{
        return patientDao.getPatientByPatientID(patientID)
    }

    suspend fun getPatientByPatientIDAndPassword(patientID: String,password: String): Patient?{
        return patientDao.getPatientByPatientIDAndPassword(patientID,password)
    }

    suspend fun getPatientByPatientIDAndPhoneNumber(patientID: String,phoneNumber: String): Patient?{
        return patientDao.getPatientByPatientIDAndPhoneNumber(patientID,phoneNumber)
    }

    suspend fun getPatientByPhoneAndPassword(phoneNumber: String,password: String): Patient?{
        return patientDao.getPatientByPhoneAndPassword(phoneNumber,password)
    }

    suspend fun getLargestPatientID(): Int{
        return patientDao.getLargestPatientID()
    }

    fun getPatientNameByID(patientID: String): Flow<String>{
        return patientDao.getPatientNameByID(patientID)
    }

    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    suspend fun getPasswordByPatientID(patientID: String): String?{
        return patientDao.getPasswordByPatientID(patientID)
    }

    fun getPatientPhoneNumberByID(patientID: String): Flow<String>{
        return patientDao.getPatientPhoneNumberByID(patientID)
    }
}