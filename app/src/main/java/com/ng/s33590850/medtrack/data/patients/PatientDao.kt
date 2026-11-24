package com.ng.s33590850.medtrack.data.patients

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert
    suspend fun insert(patient: Patient)

    @Query("UPDATE patients SET password = :newPassword WHERE id = :patientID")
    suspend fun changePassword(newPassword: String, patientID: String)

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getPatientByPhone(phoneNumber: String): Patient?

    @Query("SELECT * FROM patients WHERE id = :patientID LIMIT 1")
    suspend fun getPatientByPatientID(patientID: String): Patient?

    @Query("SELECT password FROM patients WHERE id = :patientID LIMIT 1")
    suspend fun getPasswordByPatientID(patientID: String): String?

    @Query("SELECT * FROM patients WHERE id = :patientID AND password = :password LIMIT 1")
    suspend fun getPatientByPatientIDAndPassword(patientID: String,password: String): Patient?

    @Query("SELECT * FROM patients WHERE id = :patientID AND phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getPatientByPatientIDAndPhoneNumber(patientID: String,phoneNumber: String): Patient?

    @Query("SELECT * FROM patients WHERE phoneNumber = :phoneNumber AND password = :password")
    suspend fun getPatientByPhoneAndPassword(phoneNumber: String,password: String): Patient?

    @Query("""SELECT MAX(CAST(SUBSTR(id, 2) AS INTEGER))FROM patients""")
    suspend fun getLargestPatientID(): Int

    @Query("SELECT name FROM patients WHERE id = :patientID")
    fun getPatientNameByID(patientID: String): Flow<String>

    @Query("SELECT phoneNumber FROM patients WHERE id = :patientID")
    fun getPatientPhoneNumberByID(patientID: String): Flow<String>
}