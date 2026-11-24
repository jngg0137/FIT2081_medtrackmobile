package com.ng.s33590850.medtrack

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.ng.s33590850.medtrack.data.patients.PatientViewModel
import com.ng.s33590850.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.launch

class AccountClaimActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val patientViewModel: PatientViewModel = ViewModelProvider(this,
                PatientViewModel.PatientViewModelFactory(this@AccountClaimActivity)
            )[PatientViewModel::class.java]
            MedTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AccountClaimScreen(modifier = Modifier.padding(innerPadding),patientViewModel)
                }
            }
        }
    }
}

@Composable
fun AccountClaimScreen(modifier: Modifier = Modifier,patientViewModel: PatientViewModel){
    var dialog by remember { mutableStateOf("") }
    var inputPatientID by remember { mutableStateOf("") }
    var inputPhoneNumber by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }
    var accountFound by remember { mutableStateOf(false) }
    var showInput by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Account Claiming", fontSize = 30.sp)
            Spacer(modifier.height(5.dp))

            // patient ID input
            OutlinedTextField(
                value = inputPatientID,
                onValueChange = {inputPatientID = it},
                label = { Text(text = "Patient ID")},
                modifier = Modifier.fillMaxWidth(),
                readOnly = accountFound
            )

            // patient phone number input
            OutlinedTextField(
                value = inputPhoneNumber,
                onValueChange = {inputPhoneNumber = it},
                label = { Text(text = "Phone number")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = accountFound
            )

            if (accountFound){
                OutlinedTextField(
                    value = inputPassword,
                    onValueChange = {inputPassword = it},
                    label = { Text(text = "Password")},
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation =
                        if (showInput){
                            VisualTransformation.None
                        } else{
                            PasswordVisualTransformation()
                        },
                    trailingIcon = {
                        IconButton(onClick = {showInput = !showInput}) {
                            Icon(
                                if (showInput) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Show/hide input"
                            )
                        }
                    }
                )
            }

            Spacer(modifier.height(10.dp))

            Button(
                onClick = {context.startActivity(Intent(context, LoginActivity::class.java))}
            )
            {Text("Back") }

            if (!accountFound) {
                Button(
                    onClick = {
                        dialog = ""

                        // check if both fields are null, empty or have whitespace
                        if (!inputPatientID.isNullOrBlank() || !inputPhoneNumber.isNullOrBlank()) {
                            coroutineScope.launch {
                                val patientByPatientID =
                                    patientViewModel.getPatientByPatientID(inputPatientID)
                                val patientByPatientIDAndPhoneNumber =
                                    patientViewModel.getPatientByPatientIDAndPhoneNumber(
                                        inputPatientID,
                                        inputPhoneNumber
                                    )
                                if (patientByPatientID == null) {
                                    inputError = true
                                    dialog += "No account found with this patient ID\n"
                                }
                                if (patientByPatientIDAndPhoneNumber == null) {
                                    inputError = true
                                    dialog += "No account found with this patient ID and password"
                                }

                                if (patientByPatientID != null && patientByPatientIDAndPhoneNumber != null) {
                                    if (patientViewModel.getPasswordByPatientID(inputPatientID).isNullOrBlank()) {
                                        accountFound = true
                                        inputError = false
                                    } else {
                                        inputError = true
                                        dialog += "Account already claimed"
                                    }
                                }
                            }
                        } else {
                            inputError = true
                            dialog += "Fill in both fields"
                        }

                    }
                ) { Text("Find account") }
            }

            if (inputError){
                Text(text = dialog)
            }

            if (accountFound){
                Button(
                    onClick = {
                        dialog = ""
                        val passwordContainsChar = inputPassword.any { it.isLetter() }
                        val passwordContainsNum = inputPassword.any { it.isDigit() }
                        if (inputPassword.length < 8 || !(passwordContainsChar && passwordContainsNum)){
                            inputError = true
                            dialog = "Password must have at least 8 characters, " +
                                    "and 1 number and 1 letter"
                        }
                        else
                        {
                            coroutineScope.launch {
                                patientViewModel.changePassword(inputPassword,inputPatientID)
                            }
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                )
                {Text("Set password, then press here to claim account")}
            }
        }
    }
}