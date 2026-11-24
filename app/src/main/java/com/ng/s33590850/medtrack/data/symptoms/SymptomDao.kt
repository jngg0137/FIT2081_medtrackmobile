package com.ng.s33590850.medtrack.data.symptoms

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ng.s33590850.medtrack.data.patients.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {
    @Insert
    suspend fun insert(symptom: Symptom)

    @Query("SELECT * FROM symptoms")
    fun getAllSymptoms(): Flow<List<Symptom>>

    @Query("SELECT * FROM symptoms WHERE patientID = :patientID")
    fun getSymptomsByPatientID(patientID: String): Flow<List<Symptom>>
}