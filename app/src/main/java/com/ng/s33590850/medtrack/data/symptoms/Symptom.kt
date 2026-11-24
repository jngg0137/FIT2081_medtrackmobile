package com.ng.s33590850.medtrack.data.symptoms

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ng.s33590850.medtrack.data.patients.Patient

/**
 * Represents a symptom entity within the Room database.

 */
@Entity(
    tableName = "symptoms",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Symptom(
    // Primary key with auto-generation
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientID: String,
    val category: String,
    val severity: Int,
    val notes: String = "", // optional
    val dateTime: String
)