package com.ng.s33590850.medtrack.data.symptoms

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ng.s33590850.medtrack.data.patients.PatientViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SymptomViewModel(context: Context): ViewModel() {
    val repository = SymptomRepository(context)
    val allSymptoms = repository.getAllSymptoms()

    fun insertSymptom(symptom: Symptom) = viewModelScope.launch {
        repository.insertSymptom(symptom)
    }

    fun getSymptomsByPatientID(patientID: String): Flow<List<Symptom>>{
        return repository.getSymptomsByPatientID(patientID)
    }

    class SymptomViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SymptomViewModel(context) as T
    }
}