package com.ng.s33590850.medtrack.data.medications

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ng.s33590850.medtrack.data.patients.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Insert
    suspend fun insert(medication: Medication)

    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE patientID = :patientID")
    fun getMedicationByPatientID(patientID: String): Flow<List<Medication>>
}