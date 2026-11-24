package com.ng.s33590850.medtrack

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ng.s33590850.medtrack.data.AuthManager
import com.ng.s33590850.medtrack.data.symptoms.Symptom
import com.ng.s33590850.medtrack.data.symptoms.SymptomViewModel
import com.ng.s33590850.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar
import kotlin.collections.get
import kotlin.sequences.forEach

class SymptomsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val symptomViewModel: SymptomViewModel = ViewModelProvider(this, SymptomViewModel.SymptomViewModelFactory(this@SymptomsActivity)
            )[SymptomViewModel::class.java]
            MedTrackTheme {
                SymptomScreen(symptomViewModel)
            }
        }
    }
}

// symptoms screen
@Composable
fun SymptomScreen(symptomViewModel: SymptomViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding))
        {
            LogSymptomSection(snackbarHostState,scope,symptomViewModel)
            Spacer(modifier = Modifier.height(50.dp))
            Row()
            {
                Button(onClick = {context.startActivity(Intent(context, HomeActivity::class.java))})
                { Text("Home") }

                Button(onClick = {/* Already at symptoms */})
                { Text("Symptoms") }

                Button(onClick = {/* Pending medcoach */})
                {Text("MedCoach")}

                Button(onClick = {context.startActivity(Intent(context, SettingsActivity::class.java))})
                {Text("Settings")}
            }
            Text("Symptom history")
            SymptomHistorySection(symptomViewModel)
        }
    }
}

// top part of symptoms screen
@Composable
fun LogSymptomSection(snackbarHostState: SnackbarHostState,scope: CoroutineScope,symptomViewModel: SymptomViewModel){
    val patientID = AuthManager.getPatientId(LocalContext.current)
    var validationError by remember { mutableStateOf(false)}
    var category by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(0f) }
    var notes by remember { mutableStateOf("") }
    var date = remember { mutableStateOf("") }
    var time = remember { mutableStateOf("") }
    var datePickerDialog = DatePickerFunction(date)
    var timePickerDialog = TimePickerFunction(time)
    var categoryErrorDialog by remember { mutableStateOf("") }
    var severityErrorDialog by remember { mutableStateOf("") }
    var dateTimeErrorDialog by remember { mutableStateOf("") }
    var noteErrorDialog by remember { mutableStateOf("") }

    Column() {
        // symptom input (dropdown)
        Text("Category: $category")
        var expandedCategory by remember { mutableStateOf(false) }
        val dropdownItemsType = listOf("Pain","Nausea","Dizziness","Fatigue","Headache","Skin Reaction","Other")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(onClick = {
                expandedCategory = !expandedCategory
            }) { Text("Choose category") }
            DropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                dropdownItemsType.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            category = option
                            expandedCategory = false
                        }
                    )
                }
            }
        }
        Text(categoryErrorDialog, color = Color.Red)
        Spacer(modifier = Modifier.height(30.dp))

        // severity input (slider)
        var sliderColor = Color.Green
        if (severity >= 4 && severity < 7){
            sliderColor = Color.Yellow
        }
        else if (severity >= 7){
            sliderColor = Color.Red
        }
        Text("Severity: ${severity.toInt()}")
        Slider(
            value = severity,
            onValueChange = {severity = it},
            valueRange = 0f..10.5f,
            colors = SliderColors(sliderColor,sliderColor,sliderColor,sliderColor,sliderColor,sliderColor,sliderColor,sliderColor,sliderColor,sliderColor)
        )
        Text(severityErrorDialog, color = Color.Red)
        Spacer(modifier = Modifier.height(30.dp))

        // date time input (time picker)
        Text("Date & Time: ${date.value} ${time.value}")
        Row() {
            Button(onClick = {datePickerDialog.show()}) {Text("Pick date")}
            Button(onClick = {timePickerDialog.show()}) {Text("Pick time")}
        }
        Text(dateTimeErrorDialog, color = Color.Red)
        Spacer(modifier = Modifier.height(30.dp))

        // notes input  (text field)
        val characterCount = notes.length
        Text("Additional notes (optional)\n" +
                "Character count: $characterCount")
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(text = "") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(noteErrorDialog, color = Color.Red)
        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            validationError = false
            categoryErrorDialog = ""
            severityErrorDialog = ""
            dateTimeErrorDialog = ""
            noteErrorDialog = ""

            if (category.isEmpty()){
                validationError = true
                categoryErrorDialog = "Choose a category"
            }

            if (severity.toInt() < 1 ){
                validationError = true
                severityErrorDialog = "Severity must be between 1 to 10"
            }

            if (date.value.isEmpty() || time.value.isEmpty()){
                validationError = true
                dateTimeErrorDialog = "Choose a date & time"
            }

            if (characterCount > 200){
                validationError = true
                noteErrorDialog = "Notes can only have 200 characters maximum"
            }

            if (!validationError){
                val dateTime = "${date.value} ${time.value}"
                symptomViewModel.insertSymptom(Symptom(
                    patientID = patientID,
                    category = category,
                    severity = severity.toInt(),
                    notes = notes,
                    dateTime = dateTime
                ))
                scope.launch { snackbarHostState.showSnackbar("Symptom successfully added") }

                category = ""
                severity = 0f
                notes  = ""
                date.value = ""
                time.value = ""
            }
        },
            modifier = Modifier.align(Alignment.CenterHorizontally))
        { Text("Save Symptom") }
    }
}

// bottom part of symptoms screen
@Composable
fun SymptomHistorySection(symptomViewModel: SymptomViewModel){
    val patientID = AuthManager.getPatientId(LocalContext.current)
    val history by symptomViewModel.getSymptomsByPatientID(patientID).collectAsState(initial = emptyList())

    if (history.isEmpty()){
        Text("No symptoms logged yet.")
    }
    else{
        LazyColumn(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .height(200.dp)) {
            items(history) { item ->
                val category = item.category
                val severity = item.severity
                val dateTime = item.dateTime
                val notes = item.notes
                var severityColor = Color.Green
                var severityText by remember { mutableStateOf("Mild") }

                if (severity >= 4 && severity < 7){
                    severityColor = Color.Yellow
                    severityText = "Moderate"
                }
                else if (severity >= 7){
                    severityColor = Color.Red
                    severityText = "Severe"
                }

                Card(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()) {
                    Text(
                        text = category,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Severity: $severityText ($severity)",
                        color = severityColor
                    )
                    Text(dateTime)
                    Text(notes)
                }
            }
        }
    }
}

// function to call dialog to pick a date
@Composable
fun DatePickerFunction(mDate: MutableState<String>): DatePickerDialog {
    val mContext = LocalContext.current

    // Initialize variables to hold the current year, month, and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Get a calendar instance
    val mCalendar = Calendar.getInstance()

    // Extract the current year, month, and day from the calendar
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    // Create and return a DatePickerDialog
    return DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = String.format("%04d/%02d/%02d",mYear,mMonth+1,mDayOfMonth)
        }, mYear, mMonth, mDay
    )
}