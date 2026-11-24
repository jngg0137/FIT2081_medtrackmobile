package com.ng.s33590850.medtrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ng.s33590850.medtrack.data.medications.Medication
import com.ng.s33590850.medtrack.data.medications.MedicationDao
import com.ng.s33590850.medtrack.data.patients.Patient
import com.ng.s33590850.medtrack.data.patients.PatientDao
import com.ng.s33590850.medtrack.data.symptoms.Symptom
import com.ng.s33590850.medtrack.data.symptoms.SymptomDao

@Database(entities = [Patient::class, Medication::class, Symptom::class], version = 1, exportSchema = false)
/**
 * Abstract class representing the college database.
 * It extends RoomDatabase and provides access to the DAO interfaces for the entities.
 */
abstract class MTDatabase : RoomDatabase() {
    /**
     * Provides access to the PatientDao interface for performing
     * database operations on Patient entities.
     * @return PatientDao instance.
     */
    abstract fun patientDao(): PatientDao

    /**
     * Provides access to the MedicationDao interface for
     * performing database operations on Medication entities.
     * @return MedicationDao instance.
     */
    abstract fun medicationDao(): MedicationDao

    /**
     * Provides access to the SymptomDao interface for
     * performing database operations on Medication entities.
     * @return SymptomDao instance.
     */
    abstract fun symptomDao(): SymptomDao

    companion object {
        // Singleton instance of the database
        @Volatile
        private var Instance: MTDatabase? = null

        /**
         * Retrieves the singleton instance of the database.
         * If an instance already exists, it returns the existing
         * instance. Otherwise, it creates a new instance of the database.
         * @param context The context of the application.
         * @return The singleton instance of CollegeDatabase.
         */
        fun getDatabase(context: Context): MTDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MTDatabase::class.java, "medication_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}