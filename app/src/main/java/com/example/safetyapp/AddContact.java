package com.example.safetyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddContact extends AppCompatActivity {

    Button add, update, remove;
    ArrayList<String> contactList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView layoutList;
    int onClickPos = -1;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        db = new DatabaseHelper(this);

        contactList = db.getContact();



        add = findViewById(R.id.add);
        layoutList = findViewById(R.id.contactlist);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactList);
        layoutList.setAdapter(adapter);

        layoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickPos = position;
            }
        });

        remove = findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickPos!=-1){
                    db.removeContact(contactList.get(onClickPos));
                    contactList.remove(onClickPos);
                    adapter.notifyDataSetChanged();
                    onClickPos=-1;
                }else{
                    Toast.makeText(AddContact.this, "Select a contact to remove", Toast.LENGTH_LONG).show();
                }
            }
        });

        update = findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if(resultCode== Activity.RESULT_OK){

                    Uri uriContact = data.getData();
                    String contactNumber = null;
                    String contactID="";

                    // getting contacts ID
                    Cursor cursorID = getContentResolver().query(uriContact,
                            new String[]{ContactsContract.Contacts._ID},
                            null, null, null);

                    if (cursorID.moveToFirst()) {

                        contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
                    }

                    cursorID.close();

                    Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                            new String[]{contactID},
                            null);

                    if (cursorPhone.moveToFirst()) {
                        contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    cursorPhone.close();


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    String contactName = null;

                    // querying contact data store
                    Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

                    if (cursor.moveToFirst()) {

                        // DISPLAY_NAME = The display name for the contact.
                        // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

                        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    }

                    cursor.close();

                    Toast.makeText(this, contactName+", "+contactNumber, Toast.LENGTH_LONG).show();
                    contactList.add(contactName+", "+contactNumber);
                    adapter.notifyDataSetChanged();
                    db.addContact(contactName+", "+contactNumber);
                }
                break;
        }
    }
}