package com.example.homegaben3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homegaben3.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val contactList = mutableListOf<Contact>()
    private val contactAdapter = ContactAdapter(contactList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.rvContacts.layoutManager = LinearLayoutManager(this)
        binding.rvContacts.adapter = contactAdapter

        // Fetch contacts from Firebase
        fetchContacts()

        // Add contact button functionality
        binding.btnAddContact.setOnClickListener {
            val contactName = binding.edtContactName.text.toString()
            val contactPhone = binding.edtContactPhone.text.toString()
            if (contactName.isNotEmpty() && contactPhone.isNotEmpty()) {
                addContact(contactName, contactPhone)
            } else {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchContacts() {
        db.collection("contacts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val contact = document.toObject(Contact::class.java)
                    contactList.add(contact)
                }
                contactAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting contacts: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addContact(name: String, phone: String) {
        val contact = Contact(name, phone)
        db.collection("contacts")
            .add(contact)
            .addOnSuccessListener {
                Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show()
                contactList.add(contact)
                contactAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error adding contact: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
