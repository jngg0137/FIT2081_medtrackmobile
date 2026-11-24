package com.ng.s33590850.medtrack.data.symptoms

import android.content.Context
import com.ng.s33590850.medtrack.data.MTDatabase
import com.ng.s33590850.medtrack.data.medications.Medication
import com.ng.s33590850.medtrack.data.patients.Patient
import kotlinx.coroutines.flow.Flow

class SymptomRepository(context: Context) {
    private val symptomDao = MTDatabase.getDatabase(context).symptomDao()

    suspend fun insertSymptom(symptom: Symptom) {
        symptomDao.insert(symptom)
    }

    fun getAllSymptoms(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()

    fun getSymptomsByPatientID(patientID: String): Flow<List<Symptom>> =
        symptomDao.getSymptomsByPatientID(patientID)
}