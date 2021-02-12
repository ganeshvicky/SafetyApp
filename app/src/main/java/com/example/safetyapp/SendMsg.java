package com.example.safetyapp;

import android.telephony.SmsManager;

import java.util.ArrayList;


public class SendMsg {

    ArrayList<String> contactList;
    String msg = "This is an alert message sent from the app!";

    public SendMsg(ArrayList<String> contactList) {
        this.contactList = contactList;
    }



    protected void send(){
        SmsManager smsManager = SmsManager.getDefault();

        for(int i=0; i<contactList.size(); i++){
            String[] temp = contactList.get(i).split(",");
            String phno = temp[1];
            smsManager.sendTextMessage(phno, null, msg, null, null);
        }


        //Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
    }

}
