package com.example.mystudent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mystudent.model.Student
import com.example.mystudent.viewmodel.StudentViewModel

@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel = viewModel()) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    var currentPhone by remember { mutableStateOf("") }
    var phoneList by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        TextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = program,
            onValueChange = { program = it },
            label = { Text("Program") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = currentPhone,
                onValueChange = { currentPhone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (currentPhone.isNotBlank()) {
                    phoneList = phoneList + currentPhone
                    currentPhone = ""
                }
            }) {
                Text("Add")
            }
        }

        if (phoneList.isNotEmpty()) {
            Text(
                text = "Phone Numbers:",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            phoneList.forEach { phone ->
                Text("- $phone")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (studentId.isNotBlank() && name.isNotBlank() && program.isNotBlank()) {
                    viewModel.addStudent(Student(studentId, name, program, phoneList))
                    studentId = ""
                    name = ""
                    program = ""
                    phoneList = listOf()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Student List", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(viewModel.students) { student ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("ID: ${student.id}")
                    Text("Name: ${student.name}")
                    Text("Program: ${student.program}")

                    if (student.phones.isNotEmpty()) {
                        Text("Phones:")
                        student.phones.forEach { phone ->
                            Text("- $phone")
                        }
                    }

                    Divider()
                }
            }
        }
    }
}
