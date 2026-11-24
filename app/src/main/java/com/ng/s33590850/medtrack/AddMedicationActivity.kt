package com.ng.s33590850.medtrack

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.ng.s33590850.medtrack.data.AuthManager
import com.ng.s33590850.medtrack.data.medications.Medication
import com.ng.s33590850.medtrack.data.medications.MedicationViewModel
import com.ng.s33590850.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class AddMedicationActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val medicationViewModel: MedicationViewModel = ViewModelProvider(this, MedicationViewModel.MedicationViewModelFactory(this@AddMedicationActivity)
            )[MedicationViewModel::class.java]
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            var name by remember { mutableStateOf("") }
            var dosage by remember { mutableStateOf("") }
            var frequency by remember { mutableStateOf("") }
            var type by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }
            var time = remember { mutableStateOf("") }
            var timePickerDialog = TimePickerFunction(time)
            var validationError by remember { mutableStateOf(false) }
            var nameError by remember { mutableStateOf("") }
            var dosageError by remember { mutableStateOf("") }
            var timeError by remember { mutableStateOf("") }
            var frequencyError by remember { mutableStateOf("") }
            var typeError by remember { mutableStateOf("") }

            MedTrackTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
                    modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        val context = LocalContext.current
                        val patientID = AuthManager.getPatientId(context)

                        // medication name input (text field)
                        Text("Medication Name")
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(text = "") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(nameError, color = Color.Red)
                        Spacer(modifier = Modifier.height(15.dp))

                        // dosage input (text field)
                        Text("Dosage")
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = { Text(text = "") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(dosageError, color = Color.Red)
                        Spacer(modifier = Modifier.height(30.dp))

                        // frequency input (dropdown)
                        Text("Frequency: $frequency")
                        var expandedFrequency by remember { mutableStateOf(false) }
                        val dropdownItemsFrequency =
                            listOf("Once daily", "Twice daily", "Three times daily", "As needed")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Button(onClick = {
                                expandedFrequency = !expandedFrequency
                            }) { Text("Choose frequency") }
                            DropdownMenu(
                                expanded = expandedFrequency,
                                onDismissRequest = { expandedFrequency = false }
                            ) {
                                dropdownItemsFrequency.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            frequency = option
                                            expandedFrequency = false
                                        }
                                    )
                                }
                            }
                        }
                        Text(frequencyError, color = Color.Red)

                        // time input (time picker)
                        Text("Time: ${time.value}")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Button(onClick = {
                                timePickerDialog.show()
                            }) { Text(text = "Choose time") }
                        }
                        Text(timeError, color = Color.Red)

                        // type input (dropdown)
                        Text("Medication type: $type")
                        var expandedType by remember { mutableStateOf(false) }
                        val dropdownItemsType = listOf("Tablet","Capsule","Liquid","Injection","Topical","Other")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Button(onClick = {
                                expandedType = !expandedType
                            }) { Text("Choose type") }
                            DropdownMenu(
                                expanded = expandedType,
                                onDismissRequest = { expandedType = false }
                            ) {
                                dropdownItemsType.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            type = option
                                            expandedType = false
                                        }
                                    )
                                }
                            }
                        }
                        Text(typeError, color = Color.Red)

                        // notes input  (text field)
                        Text("Notes (optional)")
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text(text = "") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(30.dp))

                        Row(modifier =
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom)
                        {
                            Button(onClick = {
                                validationError = false
                                nameError = ""
                                dosageError = ""
                                frequencyError = ""
                                timeError = ""
                                typeError = ""

                                if(name.isEmpty()){
                                    validationError = true
                                    nameError = "This field must be filled in"
                                }

                                // needs a positive number and 1 of the 3 units at the back
                                val regexPattern = Regex("^\\d+(\\.\\d+)?(mg|ml|g)$")
                                if(dosage.isEmpty()){
                                    validationError = true
                                    dosageError = "This field must be filled in"
                                }
                                else if(!regexPattern.matches(dosage.trim().lowercase())){
                                    validationError = true
                                    dosageError = "Input must be a positive number followed by unit " +
                                            "(e.g. 500mg, 10ml)"
                                }

                                if (frequency.isEmpty()){
                                    validationError = true
                                    frequencyError="This field must be filled in"
                                }

                                if (time.value.isEmpty()){
                                    validationError = true
                                    timeError = "This field must be filled in"
                                }

                                if (type.isEmpty()){
                                    validationError = true
                                    typeError = "This field must be filled in"
                                }

                                if (!validationError){
                                    medicationViewModel.insertMedication(
                                        Medication(
                                            patientID = patientID,
                                            name = name,
                                            dosage = dosage,
                                            frequency = frequency,
                                            type = type,
                                            notes = notes,
                                            time = time.value,
                                            )
                                        )
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message = "Medication successfully added. Going back to home screen")
                                        delay(2000)
                                        context.startActivity(Intent(context, HomeActivity::class.java))
                                    }
                                    name = ""
                                    dosage = ""
                                    frequency = ""
                                    time = mutableStateOf("")
                                    type = ""
                                    notes = ""
                                }
                            })
                            { Text("Save") }

                            Button(onClick = {
                                name = ""
                                dosage = ""
                                frequency = ""
                                time = mutableStateOf("")
                                type = ""
                                notes = ""
                            })
                            { Text("Clear") }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier =
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom)
                        {
                            Button(onClick = {context.startActivity(Intent(context, HomeActivity::class.java))})
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
            }
        }
    }
}

// function to call dialog to pick a time
@Composable
fun TimePickerFunction(mTime: MutableState<String>): TimePickerDialog {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Get the current hour and minute
    val mHour = calendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = calendar.get(Calendar.MINUTE)

    // Set the calendar's time to the current time
    calendar.time = Calendar.getInstance().time

    return TimePickerDialog(
        context,
        { _, mHour: Int, mMinute: Int ->
            mTime.value = String.format("%02d:%02d",mHour,mMinute)
        }, mHour, mMinute, false
    )
}