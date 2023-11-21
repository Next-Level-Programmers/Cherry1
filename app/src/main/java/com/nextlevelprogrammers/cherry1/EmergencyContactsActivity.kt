package com.nextlevelprogrammers.cherry1

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class EmergencyContactsActivity : AppCompatActivity() {

    private val CONTACTS_READ_REQUEST = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_READ_REQUEST
            )
        } else {
            getContacts()
        }

    }

    fun onPickContactsClicked(view: View) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, PICK_CONTACT_REQUEST)
    }

    fun onAddContactClicked(view: View) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, PICK_CONTACT_REQUEST)
    }

    companion object {
        private const val PICK_CONTACT_REQUEST = 102
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            val contactData = data?.data

            contactData?.let {
                val cursor = contentResolver.query(it, null, null, null, null)

                cursor?.let {
                    val displayNameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)

                    if (displayNameIndex != -1 && idIndex != -1) {
                        if (it.moveToFirst()) {
                            val name = it.getString(displayNameIndex)
                            val contactId = it.getString(idIndex)

                            val phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(contactId),
                                null
                            )

                            phoneCursor?.let { phoneCur ->
                                val phoneNumberIndex =
                                    phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                if (phoneCur.moveToFirst() && phoneNumberIndex != -1) {
                                    val phoneNumber = phoneCur.getString(phoneNumberIndex)

                                    // Update the CardView with contact information
                                    findViewById<TextView>(R.id.nameTextView).text = "Name: $name"
                                    findViewById<TextView>(R.id.numberTextView).text = "Number: $phoneNumber"
                                }
                                phoneCur.close()
                            }
                        }
                        it.close()
                    } else {
                        // Handle case where columns are not found
                    }
                }
            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_READ_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts()
            }
        }
    }

    private fun getContacts() {
        val contentResolver: ContentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.let {
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)

            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) else ""
                val contactId = if (idIndex != -1) it.getString(idIndex) else ""

                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(contactId),
                    null
                )

                phoneCursor?.let { phoneCur ->
                    if (phoneCur.moveToFirst()) {
                        val phoneIndex = phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phoneNumber = if (phoneIndex != -1) phoneCur.getString(phoneIndex) else ""

                        // Do something with name and phoneNumber (e.g., display, store, etc.)
                        println("Name: $name, Phone Number: $phoneNumber")
                    }
                    phoneCur.close()
                }
            }
            it.close()
        }
    }
}
