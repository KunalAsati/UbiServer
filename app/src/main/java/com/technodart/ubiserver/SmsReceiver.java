package com.technodart.ubiserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

 public class SmsReceiver extends BroadcastReceiver {
  private static final String Tag="Message Receivied";

     public void onReceive(Context context, Intent intent) {
         final Bundle pdubundle = intent.getExtras();

         Object[]  pdus=(Object[])pdubundle.get("pdus");

         SmsMessage message=SmsMessage.createFromPdu((byte[])pdus[0]);
         Toast.makeText(context,"sms rec from"+message.getOriginatingAddress()+message.getMessageBody(),Toast.LENGTH_LONG).show();

        // tv.setText(add + num);
         Intent si=new Intent(context ,MainActivity.class);
         si.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         si.putExtra("msgnum",message.getOriginatingAddress());
         si.putExtra("msg",message.getMessageBody());
         context.startActivity(si);
         Toast.makeText(context,"sms rec from"+message.getOriginatingAddress()+message.getMessageBody(),Toast.LENGTH_LONG).show();

     }
}
