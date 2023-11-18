package com.nextlevelprogrammers.cherry1

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.provider.ContactsContract
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


data class Contact(val name: String, val phoneNumber: String)

class EmergencyContactsActivity : AppCompatActivity() {

    private val MAX_CONTACTS = 10
    private var contactsList = mutableListOf<Contact>()

    private val READ_CONTACTS_REQUEST_CODE = 101

    private lateinit var addContactButton: Button

    private val pickContacts =
        registerForActivityResult(ActivityResultContracts.PickMultipleContacts()) { contacts ->
            contacts?.let {
                for (contactUri in contacts) {
                    val contactData = getContactData(contactUri)
                    contactData?.let { data ->
                        if (contactsList.size < MAX_CONTACTS) {
                            val contact = Contact(data.first, data.second)
                            contactsList.add(contact)
                            // Update UI or save the contact to storage
                        } else {
                            // Display a message that the maximum contacts limit is reached
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_emergency_contacts)

        addContactButton = findViewById(R.id.addContactButton)

        loadContacts()

        addContactButton.setOnClickListener {
            requestContactsPermission()
            if (contactsList.size < MAX_CONTACTS) {
                // Logic to add contacts manually from EditText
            } else {
                // Display a message that the maximum contacts limit is reached
            }
        }
        addContactButton.setOnClickListener {
            requestContactsPermission()
            pickContacts.launch(null)
        }


        addContactButton.setOnClickListener {
            pickContact.launch(null)
        }

        val contacts = listOf<Pair<String, String>>(
            "Contact 1" to "1234567890",
            "Contact 2" to "9876543210"
            // Add your contacts here as Pair(name, number)
        )

        val contactRecyclerView: RecyclerView = findViewById(R.id.contactRecyclerView)
        contactRecyclerView.layoutManager = LinearLayoutManager(this)
        contactRecyclerView.adapter = ContactAdapter(contacts)
    }

    private fun loadContacts() {
        // Load contacts from storage or initialize an empty list
    }

    private fun getContactData(contactUri: Uri): Pair<String, String>? {
        val cursor: Cursor? = contentResolver.query(contactUri, null, null, null, null)

        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                // Check if the columns are valid
                if (nameIndex != -1 && phoneNumberIndex != -1) {
                    val name = cursor.getString(nameIndex)
                    val phoneNumber = cursor.getString(phoneNumberIndex)
                    return Pair(name ?: "", phoneNumber ?: "")
                }
            }
        }

        return null
    }



    // ... other methods for managing contacts

    override fun onPause() {
        super.onPause()
        saveContacts()
    }

    private fun saveContacts() {
        // Save contacts to storage
    }

    fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_REQUEST_CODE
            )
        } else {
            // Permission already granted
            // Proceed with accessing contacts
            pickContacts.launch(null)
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing contacts
            } else {
                // Permission denied, handle accordingly (e.g., show explanation or disable functionality)
            }
        }
    }
}