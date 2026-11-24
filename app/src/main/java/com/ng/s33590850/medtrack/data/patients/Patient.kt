package com.ng.s33590850.medtrack.data.patients

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a patient entity within the Room database.

 */
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val password: String?
)