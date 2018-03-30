package com.technodart.ubiserver;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final String TAG="debugging";
    Button sendBtn;
    EditText txtphoneNo;
    EditText txtMessage;
    String pincode,state,phoneNo, message, msgnum, add, sendMessage, commodity, address, sms, cityField, updatedField, detailsField, currentTemperatureField, humidity_field, pressure_field, textWeather, notificationTitle, notificationBody, oo;
    Spanned weatherIcon;
    String AES="AES", s, addressFairPrice;
Long childrenCount;
    // String ;
    long msg, tempamt;
    DatabaseReference databaseproduct;
    PriceDetail dt;
   // String ;
   int flag = 0;
    char one;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mMSPRef = mRootRef.child("msp");
    DatabaseReference mPriceRef=mMSPRef.child("price");
    DatabaseReference mAddressRef=mMSPRef.child("address");
    DatabaseReference mNotificationRef = mRootRef.child("notifications");
    DatabaseReference mRegionRef;
    DatabaseReference mCommodityRef;
  //  DatabaseReference mFairPriceRef=mRootRef.child("fpsaddress");
 //   Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

    int msp, p;
Double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"oo generated");
      //  oo=GenerateRandomString.randomString(30);
oo="ubi";
        super.onCreate(savedInstanceState);
        p=0;
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
        flag=1;
        Log.d(TAG,"sendSMS called");
        if(message==null)
        {
            Log.d(TAG,"message is null");
            return;
        }
        Log.d(TAG,phoneNumber);
        Log.d(TAG,message);
       // Log.v("i",Integer.toString(i));
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this,Dummy.class), 0);
        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage("5554", null, message, pi, null);
        Log.d(TAG,"success");
       // ++i;

    }
    private void sendLongSMS(String number,String sms)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(sms);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);

        smsManager.sendMultipartTextMessage(number, null, parts, sendList, deliverList);
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
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }



    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                Log.d(TAG,"onReceive called");
                final String message = intent.getStringExtra("message");
                final String sender = intent.getStringExtra("sender");
                try {
                    s = decrpt(message, oo);
                    Log.d(TAG,"s assigned "+s);
                }catch(Exception e)
                {
                    Log.d(TAG,"exception!!!! "+e.getMessage());

                }
                if(!s.contains("ubi")){
                    Log.d(TAG,"doesn't contain ubi");
                    return;
                }

                String[] words = s.split("#");
               /* for (int i = 0; i < words.length; i++) {
                    // You may want to check for a non-word character before blindly
                    // performing a replacement
                    // It may also be necessary to adjust the character class
                    words[i] = words[i].replaceAll("[^\\w]", "");
                    Log.d(TAG,"words["+i+"]="+words[i]);
                }*/
                Log.d(TAG,"split happens");
                if(words[1].equalsIgnoreCase("ubimarket"))
                {
                    Log.d(TAG,"words[1] is "+words[1]);
                    //flag=0;
                    /*flag=0;
                    latitude = Double.parseDouble(words[2]);
                    longitude = Double.parseDouble(words[3]);
                    //Geocoder for state and city


                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);

                        if (addresses.size() > 0) {



                            //     city.setText(addresses.get(0).getLocality());
                            state = addresses.get(0).getAdminArea();
                            pincode =addresses.get(0).getPostalCode();
                            Log.d(TAG,pincode);
                            //   mFarePriceReference=mRootRef.child("fareprice")
                            //    addr.setText(addresses.get(0).getAddressLine(0));

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mFairPriceRef.child(pincode);
                    mCommodityRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            addressFairPrice = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "addressFairaddressFairPrice);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                    fairPriceProcessing(s,sender);

                }
                else if(words[1].equalsIgnoreCase("ubiweather")) {
                    flag=0;
                    Log.d(TAG,"else if called");
                    latitude = Double.parseDouble(words[2]);
                    longitude = Double.parseDouble(words[3]);
                    Function.placeIdTask asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
                        public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                            cityField = weather_city;
                            if(cityField==null) {
                                Log.d(TAG, "cityField here is null");
                            }
                            if(flag==1)
                            {
                                return;
                            }
                            updatedField = weather_updatedOn;
                            detailsField = weather_description;
                            currentTemperatureField = weather_temperature;
                            humidity_field = "Humidity: " + weather_humidity;
                            pressure_field = "Pressure: " + weather_pressure;
                            weatherIcon = Html.fromHtml(weather_iconText);
                            textWeather=weather_iconText;
                            sms=formSMS(cityField,updatedField,detailsField,currentTemperatureField,humidity_field,pressure_field,textWeather);
                            sendSMS(sender,sms);
                            flag=1;
                        }

                    });
                    Log.d(TAG,"async called");
                    asyncTask.execute(Double.toString(latitude), Double.toString(longitude)); //  asyncTask.execute("Latitude", "Longitude")
                   /* sms=formSMS(cityField,updatedField,detailsField,currentTemperatureField,humidity_field,pressure_field,textWeather);
                    sendSMS(sender,sms);*/
                }
                else if(words[1].equalsIgnoreCase("ubimsp")){
                    switch (words[2]) {
                        case "Gram":
                            commodity = "Gram";
                            break;
                        case "Paddy"://commit check
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
                            sendSMS(sender, "#ubimsp#error");
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
                    flag=0;
                    mRegionRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            address = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "msp and address at this point : " + msp + address);
                            sms = formSMS(msp, address);
                            if(flag==0) {
                                sendSMS(sender, sms);
                            }
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
                else if(words[1].equalsIgnoreCase("ubinotifications"))
                {
                    mNotificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        int i=0;
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            childrenCount=dataSnapshot.getChildrenCount();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                //  Log.d("xyzr22",commodity);
                                i++;
                                Log.d(TAG,"i is "+i);
                                Log.d(TAG, "this executes!");
                                try {
                                    notificationTitle = snapshot.getKey();
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                                Log.d(TAG, "no problem here");
                                notificationBody = snapshot.getValue(String.class);
                                Log.d(TAG, "body gets value " + notificationBody + "title gets value " + notificationTitle);
                                if(i==1) {
                                    formLongSMS(notificationTitle, notificationBody, childrenCount);
                                    Log.d(TAG,"childrenCount i = "+i+" sms = "+sms);
                                }
                                else {
                                    formLongSMS(notificationTitle, notificationBody);
                                    Log.d(TAG,"other form is called sms = "+sms);
                                }

                                sendLongSMS("5554",sms);
                                Log.d(TAG, "append works fine");

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    sendSMS(sender, "#ubierror");

                }
                }

        }
    };
    private void fairPriceProcessing(String message,String sender)
    {
        p=1;
        add = sender;
        state = "Bihar";
        pincode= "411008";
       // tv.setText(add + msgnum);

        String[] words = message.split("#");

        dt = new PriceDetail();


            //Geocoder for state and city

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

        try {
            List<Address> addresses = geocoder.getFromLocation(Double.valueOf(words[2]), Double.valueOf(words[3]), 1);

            if (addresses.size() > 0) {



                //     city.setText(addresses.get(0).getLocality());
                state = addresses.get(0).getAdminArea();
                pincode =addresses.get(0).getPostalCode();
                Log.d(TAG,pincode);
             //   mFarePriceReference=mRootRef.child("fareprice")
                //    addr.setText(addresses.get(0).getAddressLine(0));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

      //  Toast.makeText(this, pincode+state, Toast.LENGTH_SHORT).show();
        databaseproduct = FirebaseDatabase.getInstance().getReference().child("fareprice").child(state);

        sendMessage ="#ubimarket";
        flag=0;
        databaseproduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot ) {
                int i =0;
                if(flag==1){
                    return;
                }
                for(DataSnapshot gr:dataSnapshot.getChildren()) {
                    // String useridstr = usrid.getKey();
                    i++;
                    dt = gr.getValue(PriceDetail.class);
                    if (dt != null) {
                        flag=1;
                        Toast.makeText(MainActivity.this ,String.valueOf(i), Toast.LENGTH_SHORT ).show();
                        sendMessage= sendMessage.concat("#" +dt.getCommodity());
                        sendMessage= sendMessage.concat("#" +dt.getPrice());
                        sendMessage= sendMessage.concat("#" +dt.getArrived());
                        sendMessage= sendMessage.concat("#" +dt.getRemained());


                    }
                }

                forAddress(sendMessage.concat("#" + state), pincode);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });




    }

    public void forAddress(final String sendmsg, String pin){


        databaseproduct= FirebaseDatabase.getInstance().getReference().child("fpsaddress").child(pin).child("address");

        databaseproduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot ) {

                Log.d(TAG,"onDataChange called");
               // Toast.makeText(MainActivity.this ,sendmsg, Toast.LENGTH_SHORT ).show();
                sendSMS("5554",sendmsg.concat("#"+dataSnapshot.getValue(String.class)));
          //      Log.d(TAG,"addressFairPrice is : "+addressFairPrice);
               /* if(addressFairPrice==null)
                {
                    addressFairPrice="address for pincode "+pincode+"not set.";
                }*/
                /*sendmsg.concat("#address");
                Log.d(TAG,"sendmsg is "+sendmsg);
                sendSMS(add ,sendmsg);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }



        });
    }
    private String formSMS(int msp,String address)
    {

        String sms, intermediateSMS;
        Log.d(TAG,"ubimsp called");
        sms="#ubimsp#"+msp+"#"+address;
        Log.d(TAG,"returned SMS = "+sms);
        return sms;
    }
    private String formSMS(String cityField,String updatedField,String detailsField,String currentTemperatureField,String humidity_field,String pressure_field,String textWeather)
    {
        Log.d(TAG,"formSMS for weather called");
        String sms;
        sms="#ubiweather#"+cityField+"#"+detailsField+"#"+currentTemperatureField+"#"+humidity_field;
        Log.d(TAG,"returned SMS = "+sms);
        return sms;
    }
    private void formLongSMS(String title,String body)
    {
        Log.d(TAG,"other form sms before concatenation = "+sms);
        sms.concat("#"+title+"#"+body);
        Log.d(TAG,"other form sms after concatenation = "+sms);

       /* try {
          //  sms = encrpt(intermediateSMS, oo);
            Log.d(TAG,"returned SMS = "+sms);
            return sms;
        }catch(Exception e)
        {
            e.printStackTrace();
        }*/

        //return "";
    }
    private void formLongSMS(String title,String body,long childrenCount)
    {

        sms="#ubinotifications#"+childrenCount+"#"+title+"#"+body;

       /* try {
          //  sms = encrpt(intermediateSMS, oo);
            Log.d(TAG,"returned SMS = "+sms);
            return sms;
        }catch(Exception e)
        {
            e.printStackTrace();
        }*/

        //return "";
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }
    private String decrpt(String out, String s) throws Exception {
        Log.d(TAG,"decrypt called ");
        SecretKeySpec key=generateKey(s);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] dval= Base64.decode(out,Base64.DEFAULT);
        byte[] decval=c.doFinal(dval);
        String  dvalue=new String(decval);
        Log.d(TAG,"dvalue = "+dvalue);
        return dvalue;

    }
    private SecretKeySpec generateKey(String pas) throws Exception {
        Log.d(TAG,"SecretKeySpec called with pas = "+pas);
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        Log.d(TAG,"MessageDigest 1");
        byte[] bytes =pas.getBytes("UTF-8");
        Log.d(TAG,"bytes[]");
        digest.update(bytes,0,bytes.length);
        Log.d(TAG,"digest");
        byte[] key=digest.digest();
        Log.d(TAG,"key = "+key+" and algorithm = "+AES);
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }/*private String encrpt(String in,String p) throws Exception {
        SecretKeySpec key= generateKey(p);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encv=c.doFinal(in.getBytes());
        String eval= Base64.encodeToString(encv,Base64.DEFAULT);
        return eval;

    }*/

}
