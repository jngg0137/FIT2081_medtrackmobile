package com.ng.s33590850.medtrack.data.medications

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ng.s33590850.medtrack.data.patients.Patient

/**
 * Represents a medication entity within the Room database.

 */
@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Medication(
    // Primary key with auto-generation
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientID: String, // foreign key
    val name: String,
    val dosage: String,
    val frequency: String,
    val type: String,
    val notes: String = "", //optional
    val time: String
)