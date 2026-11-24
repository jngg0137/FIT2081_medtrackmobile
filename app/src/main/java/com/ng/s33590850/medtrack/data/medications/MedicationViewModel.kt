package com.ng.s33590850.medtrack.data.medications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MedicationViewModel(context: Context): ViewModel(){
    val repository = MedicationRepository(context)
    val allMedications = repository.getAllMedications()

    fun insertMedication(medication: Medication) = viewModelScope.launch {
        repository.insertMedication(medication)
    }

    fun getMedicationByPatientID(patientID: String): Flow<List<Medication>> {
        return repository.getMedicationByPatientID(patientID)
    }

    class MedicationViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MedicationViewModel(context) as T
    }
}