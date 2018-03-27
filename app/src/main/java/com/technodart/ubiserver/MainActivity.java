package com.technodart.ubiserver;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
    private static final String TAG="debugging";
    Button sendBtn;
    EditText txtphoneNo;
    EditText txtMessage;
    String phoneNo, message, msgnum, add, sendMessage, commodity, address, sms;
   // String ;
    long msg, tempamt;
    DatabaseReference databaseproduct;
    PriceDetail dt;
   // String ;
    char one;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mMSPRef = mRootRef.child("msp");
    DatabaseReference mPriceRef=mMSPRef.child("price");
    DatabaseReference mAddressRef=mMSPRef.child("address");
    DatabaseReference mRegionRef;
    DatabaseReference mCommodityRef;
    int msp;
Double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
      /*  Bundle extras = getIntent().getExtras();
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
        }*/
    }




    @SuppressWarnings("deprecation")
    private void sendSMS(String phoneNumber, String message)
    {
        if(message==null)
        {
            return;
        }
        Log.v("phoneNumber",phoneNumber);
        Log.v("message",message);
       // Log.v("i",Integer.toString(i));
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this,Dummy.class), 0);
        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber, null, message, pi, null);
       // ++i;

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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                final String sender = intent.getStringExtra("sender");
                String s = message;
                String[] words = s.split("#");
               /* for (int i = 0; i < words.length; i++) {
                    // You may want to check for a non-word character before blindly
                    // performing a replacement
                    // It may also be necessary to adjust the character class
                    words[i] = words[i].replaceAll("[^\\w]", "");
                    Log.d(TAG,"words["+i+"]="+words[i]);
                }*/
                if(message.charAt(0)!='#')
                {
                    fairPriceProcessing(message,sender);

                }
                else {
                    switch (words[2]) {
                        case "Gram":
                            commodity = "Gram";
                            break;
                        case "Paddy":
                            commodity = "Paddy";
                            break;
                        case "Sugarcane":
                            commodity = "Sugarcane";
                            break;
                        case "Wheat":
                            commodity = "Wheat";
                            break;
                        default:
                            Log.d(TAG, "default executes because words[1]=" + words[1]);
                            sendSMS(sender, "#ubi#error");
                            break;
                    }
                    latitude = Double.parseDouble(words[3]);
                    longitude = Double.parseDouble(words[4]);

                    // Log.d(TAG,"online execution");
                    // Toast.makeText(this.getContext(), "You are connected to Internet", Toast.LENGTH_SHORT).show();

                    mCommodityRef = mPriceRef.child(commodity);
                    mCommodityRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            msp = dataSnapshot.getValue(Integer.class);
                            Log.d(TAG, Integer.toString(msp));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    // Prompt the user for permission.

                    if (latitude > 28.00) {
                        Log.d(TAG,"north child, latitude = "+latitude);
                        mRegionRef = mAddressRef.child("north");
                    } else if (latitude > 14.00) {
                        if (longitude < 80.00) {
                            mRegionRef = mAddressRef.child("west");
                        } else {
                            mRegionRef = mAddressRef.child("east");
                        }
                    } else {
                        mRegionRef = mAddressRef.child("south");
                    }
                    mRegionRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            address = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "msp and address at this point : " + msp + address);
                            sms = formSMS(msp, address);
                            sendSMS(sender, sms);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //sms=formSMS(msp,address);
                    //Log.d(TAG,sms);
                    Log.d(TAG, "before sending sms = " + sms);
                    //sendSMS(sender,sms);
                }
                }

        }
    };
    private void fairPriceProcessing(String message,String sender)
    {
        add = message;
        msgnum = sender;

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
    private String formSMS(int msp,String address)
    {
        String sms;
        sms="#ubimsp#"+msp+"#"+address;
        Log.d(TAG,"returned SMS = "+sms);
        return sms;
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }
}
