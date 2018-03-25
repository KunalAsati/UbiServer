package com.technodart.ubiserver;

import java.util.ArrayList;

import android.accounts.Account;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Button sendBtn;
    EditText txtphoneNo;
    EditText txtMessage;
    String phoneNo;
    String message, msgnum, add, sendMessage;
    long msg, tempamt;
    DatabaseReference databaseproduct;
    PriceDetail dt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            add = extras.getString("msgnum");
            msgnum = extras.getString("msg");

            TextView tv = (TextView) findViewById(R.id.tv1);
            tv.setText(add + msgnum);


            sendMessage = "#ubi";
            dt = new PriceDetail();
            databaseproduct = FirebaseDatabase.getInstance().getReference().child("fareprice").child(msgnum);

            databaseproduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot ) {
                    int i =0;
                    for(DataSnapshot gr:dataSnapshot.getChildren()) {
                        // String useridstr = usrid.getKey();
                        i++;
                        dt = gr.getValue(PriceDetail.class);
                        if (dt != null) {
                            Toast.makeText(MainActivity.this ,String.valueOf(i), Toast.LENGTH_SHORT ).show();
                            sendMessage= sendMessage.concat(" #" + String.valueOf(i));
                            sendMessage= sendMessage.concat(" " +dt.getCommodity());
                            sendMessage= sendMessage.concat(" " +dt.getPrice());
                            sendMessage= sendMessage.concat(" " +dt.getArrived());
                            sendMessage= sendMessage.concat(" " +dt.getRemained());


                        }
                    }

                    Toast.makeText(MainActivity.this ,sendMessage, Toast.LENGTH_SHORT ).show();
                    sendSMS(add ,sendMessage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }




    @SuppressWarnings("deprecation")
    private void sendSMS(String phoneNumber, String message)
    {
        Log.v("phoneNumber",phoneNumber);
        Log.v("message",message);

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber, null, message, null, null);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }



    }
}
