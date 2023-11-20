package com.nextlevelprogrammers.cherry1

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class EmergencyContactsActivity : AppCompatActivity() {
    private val selectedContacts = mutableListOf<String>()

    private val pickContactLauncher =
        registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
            contactUri?.let {
                contentResolver.query(it, null, null, null, null)?.use { c ->
                    if (c.moveToFirst()) {
                        val displayNameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val contactIdIndex = c.getColumnIndex(ContactsContract.Contacts._ID)

                        if (displayNameIndex >= 0) {
                            val contactName = c.getString(displayNameIndex)
                            val contactId = c.getString(contactIdIndex)

                            // Get phone number based on contact ID
                            contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(contactId),
                                null
                            )?.use { phoneC ->
                                if (phoneC.moveToFirst()) {
                                    val phoneNumberIndex =
                                        phoneC.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                                    // Check if PHONE_NUMBER column exists
                                    if (phoneNumberIndex >= 0) {
                                        val phoneNumber = phoneC.getString(phoneNumberIndex)

                                        // Do something with the phoneNumber, e.g., add it to the list
                                        if (selectedContacts.size < 10 && !selectedContacts.contains(contactName)) {
                                            selectedContacts.add("$contactName: $phoneNumber")
                                        }
                                    }
                                }
                            }
                        } else {
                            // Handle the case where DISPLAY_NAME column doesn't exist
                        }
                    }
                }
            }

            // Update the TextView with selected contacts
            val selectedContactsTextView: TextView = findViewById(R.id.selectedContactsTextView)
            selectedContactsTextView.text = selectedContacts.joinToString(", ")
            val contactNumbers: TextView = findViewById(R.id.contactNumber)
            contactNumbers.text = selectedContacts.joinToString("\n")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_emergency_contacts)

        val pickContactButton: Button = findViewById(R.id.pickContactButton)

        pickContactButton.setOnClickListener {
            pickContactLauncher.launch(null)
        }
    }
}