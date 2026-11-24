package com.ng.s33590850.medtrack.data.medications

import android.content.Context
import com.ng.s33590850.medtrack.data.MTDatabase
import com.ng.s33590850.medtrack.data.patients.Patient
import kotlinx.coroutines.flow.Flow

class MedicationRepository(context: Context) {
    private val medicationDao = MTDatabase.getDatabase(context).medicationDao()

    suspend fun insertMedication(medication: Medication) {
        medicationDao.insert(medication)
    }

    fun getMedicationByPatientID(patientID: String): Flow<List<Medication>>{
        return medicationDao.getMedicationByPatientID(patientID)
    }

    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAllMedications()
}