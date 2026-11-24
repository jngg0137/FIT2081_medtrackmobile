package com.ng.s33590850.medtrack

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ng.s33590850.medtrack.data.AuthManager
import com.ng.s33590850.medtrack.data.medications.Medication
import com.ng.s33590850.medtrack.data.medications.MedicationViewModel
import com.ng.s33590850.medtrack.data.patients.PatientViewModel
import com.ng.s33590850.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.sequences.forEach

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val patientID = AuthManager.getPatientId(LocalContext.current)
            val patientViewModel: PatientViewModel = ViewModelProvider(this, PatientViewModel.PatientViewModelFactory(this@HomeActivity)
            )[PatientViewModel::class.java]
            val medicationViewModel: MedicationViewModel = ViewModelProvider(this, MedicationViewModel.MedicationViewModelFactory(this@HomeActivity)
            )[MedicationViewModel::class.java]

            val medications by medicationViewModel.getMedicationByPatientID(patientID).collectAsState(initial = emptyList())
            val patientName by patientViewModel.getPatientNameByID(patientID).collectAsState(initial = "")

            MedTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding),medications,patientName)
                }
            }
        }
    }
}

// home screen
@Composable
fun HomeScreen(modifier: Modifier = Modifier,medications: List<Medication>,patientName: String){
    val context = LocalContext.current
    val totalMedication = medications.count()
    var takenCount by remember { mutableStateOf(0) }
    val summaryDialog = "$takenCount of $totalMedication medications taken today"

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // show greetings & date
            Text(text = "Hello, $patientName")
            Text(text = getCurrentTime())

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, AddMedicationActivity::class.java))
                }
            ){
                Text("+ ADD MEDICATION")
            }

            // show summary bar & medication cards, if there's any medicine to take
            if (!medications.isNullOrEmpty()) {
                // show summary bar
                Text(summaryDialog)

                LazyColumn() {
                    items(medications) { item ->
                        val name = item.name
                        val dosage = item.dosage
                        val frequency = item.frequency
                        val time = item.time
                        var taken by remember { mutableStateOf(false) }
                        var style = TextStyle()
                        if (taken){style = TextStyle(textDecoration = TextDecoration.LineThrough)}

                        Card(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Bold,
                                    style = style
                                )
                                Text(text = dosage,style = style)
                                Text(text = frequency,style = style)
                                Text(text = time,style = style)

                                Switch(
                                    checked = taken,
                                    onCheckedChange = {
                                        taken = it
                                        if (it) {
                                            takenCount += 1
                                        } else {
                                            takenCount -= 1
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            else{
                Text("No medications scheduled.")
            }
        }
        Row(modifier =
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom)
        {
            Button(onClick = { /* Already at home so this does nothing */ })
            { Text("Home") }

            Button(onClick = {context.startActivity(Intent(context, SymptomsActivity::class.java))})
            { Text("Symptoms") }

            Button(onClick = {/* Pending medcoach */})
            {Text("MedCoach")}

            Button(onClick = {context.startActivity(Intent(context, SettingsActivity::class.java))})
            {Text("Settings")}
        }
    }
}

// function to get the current time
fun getCurrentTime(): String{
    val time = LocalDate.now()
    val formattedTime = time.format(DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy"))

    return formattedTime
}