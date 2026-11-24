package com.ng.s33590850.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.ng.s33590850.medtrack.data.AuthManager
import com.ng.s33590850.medtrack.data.medications.Medication
import com.ng.s33590850.medtrack.data.medications.MedicationViewModel
import com.ng.s33590850.medtrack.data.patients.Patient
import com.ng.s33590850.medtrack.data.patients.PatientViewModel
import com.ng.s33590850.medtrack.data.symptoms.Symptom
import com.ng.s33590850.medtrack.data.symptoms.SymptomViewModel
import com.ng.s33590850.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.jvm.java
import kotlin.sequences.forEach

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val patientViewModel: PatientViewModel = ViewModelProvider(this, PatientViewModel.PatientViewModelFactory(this@MainActivity)
            )[PatientViewModel::class.java]
            val medicationViewModel: MedicationViewModel = ViewModelProvider(this, MedicationViewModel.MedicationViewModelFactory(this@MainActivity)
            )[MedicationViewModel::class.java]
            val symptomViewModel: SymptomViewModel = ViewModelProvider(this, SymptomViewModel.SymptomViewModelFactory(this@MainActivity)
            )[SymptomViewModel::class.java]
            addCSVData(LocalContext.current,patientViewModel,medicationViewModel,symptomViewModel)
            MedTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// welcome screen
@Preview(showBackground = true)
@Composable
fun WelcomeScreen(modifier: Modifier = Modifier){
    val context = LocalContext.current
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (AuthManager.getPatientId(context) == "") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.foundation.Image(
                    // add logo
                    painter = painterResource(id = R.drawable.medtrack_logo),
                    contentDescription = "Medtrack Logo",
                    modifier = Modifier.size(200.dp)
                )
                Text("Ng Jiunn Chieh (33590850)")
                Spacer(modifier = Modifier.height(30.dp))

                // disclaimer
                Text("This app is for tracking purposes only and doesn't replace medical advice.")
                Spacer(modifier = Modifier.height(10.dp))

                // login button
                Button(onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            LoginActivity::class.java
                        )
                    )
                }) { Text("Go to Login") }

                // button that links to monash health clinic
                val browserIntent = Intent(Intent.ACTION_VIEW, "http://monashhealth.org/".toUri())
                Button(
                    onClick = { context.startActivity(browserIntent) }
                )
                { Text("Link to Monash Health Clinic") }
            }
        }
        else{context.startActivity(Intent(context, HomeActivity::class.java))}
    }
}

fun addCSVData(context: Context, patientViewModel: PatientViewModel,
               medicationViewModel: MedicationViewModel,symptomViewModel: SymptomViewModel){
    val sharedPref = context.getSharedPreferences("medtrack", Context.MODE_PRIVATE)
    val loadedCSV = sharedPref.getBoolean("loaded_csv",false)
    CoroutineScope(Dispatchers.IO).launch {
        Log.d("MainActivity", "CSV loaded? $loadedCSV")
    }
    if (!loadedCSV){
        addCSVPatients(context,patientViewModel)
        addCSVMedications(context,medicationViewModel)
        addCSVSymptoms(context,symptomViewModel)
        val sharedPref = context.getSharedPreferences("medtrack", Context.MODE_PRIVATE)
            .edit()
        sharedPref.putBoolean("loaded_csv",true)
        sharedPref.apply()
    }
}

fun addCSVPatients(context: Context, patientViewModel: PatientViewModel) {
    var assets = context.assets
    val inputStream = assets.open("patients.csv")
    val reader = BufferedReader(InputStreamReader(inputStream))

    // skip header and split each line into values
    reader.useLines { lines -> lines.drop(1).forEach { line ->
        val values = line.split(",")
        val id = values[0].trim()
        val phoneNumber = values[1].trim()
        val name = values[2].trim()

        val newPatient = Patient(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            password = null,
        )

        CoroutineScope(Dispatchers.IO).launch {
            patientViewModel.insertPatient(newPatient)  // Insert patient into database
            Log.d("MainActivity", "Added Patient: $newPatient")  // Log success for debugging
        }
    } }
}

/**
 * Add contents of medications.csv to Room
 */
fun addCSVMedications(context: Context, medicationViewModel: MedicationViewModel) {
    var assets = context.assets
    val inputStream = assets.open("medications.csv")
    val reader = BufferedReader(InputStreamReader(inputStream))

    // skip header and split each line into values
    reader.useLines { lines -> lines.drop(1).forEach { line ->
        val values = line.split(",")
        val patientID = values[0].trim()
        val name = values[1].trim()
        val dosage = values[2].trim()
        val frequency = values[3].trim()
        val time = values[4].trim()
        val type = values[5].trim()
        val notes = values[6].trim()

        val newMedication = Medication(
            patientID = patientID,
            name = name,
            dosage = dosage,
            frequency = frequency,
            type = type,
            time = time,
            notes = notes
        )

        CoroutineScope(Dispatchers.IO).launch {
            medicationViewModel.insertMedication(newMedication) // insert medication into database
            Log.d("MainActivity", "Added medication: $newMedication")  // Log success for debugging
        }
    } }
}

/**
 * Add contents of symptoms.csv to Room
 *
 */
fun addCSVSymptoms(context: Context, symptomViewModel: SymptomViewModel){
    var assets = context.assets
    val inputStream = assets.open("symptoms.csv")
    val reader = BufferedReader(InputStreamReader(inputStream))

    // skip header and split each line into values
    reader.useLines { lines -> lines.drop(1).forEach { line ->
        val values = line.split(",")
        val patientID = values[0].trim()
        val category = values[1].trim()
        val severity = values[2].toInt()
        val notes = values[3].trim()
        val dateTime = values[4].trim()

        val newSymptom = Symptom(
            patientID = patientID,
            category = category,
            severity = severity,
            notes = notes,
            dateTime = dateTime
        )

        CoroutineScope(Dispatchers.IO).launch {
            symptomViewModel.insertSymptom(newSymptom) // insert symptom into database
            Log.d("MainActivity", "Added symptom: $newSymptom")  // Log success for debugging
        }
    } }
}