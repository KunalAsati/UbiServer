package com.technodart.ubiserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



public class SmsReceiver extends BroadcastReceiver {
  private static final String TAG="xyzd22";
     @Override
     public void onReceive(Context context, Intent intent) {
         Log.d(TAG,"onReceiveUbiServer");
         Bundle bundle = intent.getExtras();
         SmsMessage[] smsm = null;
         String sms_str ="";
         if(bundle!=null) {
             Object[] pdus = (Object[]) bundle.get("pdus");

             smsm = new SmsMessage[pdus.length];
             for (int i = 0; i < smsm.length; i++) {
                 smsm[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                /*sms_str+=smsm[i].getOriginatingAddress();
                sms_str+=" : ";*/
                 //sms_str += "\r\nMessage: ";
                 sms_str += smsm[i].getMessageBody().toString();
                 //sms_str+= "\r\n";

                 String Sender = smsm[i].getOriginatingAddress();
                 //Check here sender is yours
                 Intent smsIntent = new Intent("otp");
                 smsIntent.putExtra("sender", Sender);
                 smsIntent.putExtra("message", sms_str);

                 LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);

             }
         }
     }
}
