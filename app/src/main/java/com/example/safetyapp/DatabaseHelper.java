package com.example.safetyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int db_v = 1;
    private static final String dbName = "ContactsDB";
    private static final String tbName = "Contacts";
    private static final String name = "name";
    private static final String phno = "phno";


    public DatabaseHelper(Context context) {
        super(context, dbName, null, db_v);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String create_contact_table = "CREATE TABLE "+tbName+"("+name+" TEXT,"+phno+" TEXT PRIMARY KEY"+")";
        db.execSQL(create_contact_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+tbName);

        onCreate(db);

    }

    public void addContact(String data){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String[] temp = data.split(",");
        values.put(name, temp[0].trim());
        values.put(phno, temp[1].trim());
        db.insert(tbName, null, values);

        db.close();

    }

    public void removeContact(String key){
        String[] temp = key.split(",");
        String finalKey = temp[1].trim();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tbName, phno+" =?", new String[]{finalKey});
        db.close();
    }

    public ArrayList<String> getContact(){
        ArrayList<String> ContactList = new ArrayList<>();

        String query = "SELECT * FROM "+tbName;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);


        if(cursor.moveToFirst()){
            do{
                ContactList.add(cursor.getString(0)+", "+cursor.getString(1));
            }while(cursor.moveToNext());
        }

        cursor.close();

        return ContactList;
    }
}
