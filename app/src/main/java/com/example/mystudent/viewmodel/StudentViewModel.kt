package com.example.mystudent.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mystudent.model.Student
import android.util.Log


class StudentViewModel : ViewModel() {

    private val db = Firebase.firestore
    var students by mutableStateOf(listOf<Student>())
        private set

    init {
        fetchStudents()
    }

    fun addStudent(student: Student) {
        val studentDoc = db.collection("students").document(student.id)

        val studentData = hashMapOf(
            "id" to student.id,
            "name" to student.name,
            "program" to student.program
        )

        studentDoc.set(studentData)
            .addOnSuccessListener {
                // Tambahkan nomor telepon ke subcollection
                val phonesCollection = studentDoc.collection("phones")
                student.phones.forEach { phone ->
                    val phoneData = hashMapOf(
                        "number" to phone
                    )
                    phonesCollection.add(phoneData)
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

                for (document in result) {
                    val id = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val program = document.getString("program") ?: ""

                    val student = Student(id, name, program)
                    tempStudents.add(student)
                }

                students = tempStudents
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting students.", exception)
            }
    }
}
