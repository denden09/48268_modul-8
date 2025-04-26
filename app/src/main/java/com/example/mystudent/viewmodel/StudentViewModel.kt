package com.example.mystudent.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mystudent.model.Student
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class StudentViewModel : ViewModel() {

    private val db = Firebase.firestore
    var students by mutableStateOf(listOf<Student>())
        private set

    init {
        fetchStudents()
    }

    fun addStudent(student: Student) {
        val studentDocRef = db.collection("students").document(student.id)

        val studentData = hashMapOf(
            "id" to student.id,
            "name" to student.name,
            "program" to student.program
        )

        studentDocRef.set(studentData)
            .addOnSuccessListener {
                Log.d("Firestore", "Student added with ID: ${student.id}")

                // Tambahkan nomor telepon ke subcollection 'phones'
                student.phones.forEach { phone ->
                    val phoneData = hashMapOf(
                        "number" to phone
                    )
                    studentDocRef.collection("phones")
                        .add(phoneData)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Phone number added: $phone")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error adding phone", e)
                        }
                }
                fetchStudents()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding student", e)
            }
    }

    private fun fetchStudents() {
        db.collection("students")
            .get()
            .addOnSuccessListener { result ->
                val tempStudents = mutableListOf<Student>()
                val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>() // Untuk tracking pengambilan phones

                for (document in result.documents) {
                    val id = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val program = document.getString("program") ?: ""

                    val student = Student(id, name, program)
                    tempStudents.add(student)

                    // Fetch phone numbers dari subcollection 'phones'
                    val phonesTask = document.reference.collection("phones")
                        .get()
                        .addOnSuccessListener { phoneResult ->
                            val phones = phoneResult.documents.mapNotNull { it.getString("number") }
                            val updatedStudent = student.copy(phones = phones)

                            tempStudents.replaceAll { if (it.id == updatedStudent.id) updatedStudent else it }
                        }

                    tasks.add(phonesTask)
                }

                // Setelah semua phone selesai diambil
                com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener {
                        students = tempStudents
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting students.", exception)
            }
    }
}
