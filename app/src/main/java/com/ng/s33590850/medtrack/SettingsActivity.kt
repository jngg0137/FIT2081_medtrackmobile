package com.ng.s33590850.medtrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.ng.s33590850.medtrack.data.AuthManager
import com.ng.s33590850.medtrack.data.patients.PatientViewModel
import com.ng.s33590850.medtrack.ui.theme.MedTrackTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val patientViewModel: PatientViewModel = ViewModelProvider(this, PatientViewModel.PatientViewModelFactory(this@SettingsActivity)
            )[PatientViewModel::class.java]
            MedTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        val context = LocalContext.current
                        val patientID = AuthManager.getPatientId(context)
                        val patientName by patientViewModel.getPatientNameByID(patientID).collectAsState(initial = "")
                        val patientPhoneNumber by patientViewModel.getPatientPhoneNumberByID(patientID).collectAsState(initial = "")

                        Text("Settings")
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Patient ID: $patientID")
                        Text("Patient Name: $patientName")
                        Text("Patient Phone Number: $patientPhoneNumber")

                        Spacer(modifier = Modifier.height(30.dp))

                        Button(onClick ={
                            AuthManager.logout(context)
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        })
                        {Text("Logout")}

                        Spacer(modifier = Modifier.height(30.dp))

                        Button(onClick = {/* Pending clinician login */}) {Text("Clinician Login")}

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier =
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom)
                        {
                            Button(onClick = {context.startActivity(Intent(context, HomeActivity::class.java))})
                            { Text("Home") }

                            Button(onClick = {context.startActivity(Intent(context, SymptomsActivity::class.java))})
                            { Text("Symptoms") }

                            Button(onClick = {/* Pending medcoach */})
                            {Text("MedCoach")}

                            Button(onClick = {/* Already at settings */})
                            {Text("Settings")}
                        }
                    }
                }
            }
        }
    }
}