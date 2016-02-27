package com.example.danielolguin.ejemploubicacionmensajes.Utilidades;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.danielolguin.ejemploubicacionmensajes.MainActivity;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        try {
            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs = null;

            String str = "";

            if (bundle != null) {
                // Retrieve the SMS Messages received
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];

                // For every SMS message received
                for (int i = 0; i < msgs.length; i++) {
                    // Convert Object array
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    // Sender's phone number
                    str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                    // Fetch the text message
                    str += msgs[i].getMessageBody().toString();
                    // Newline <img src="http://codetheory.in/wp-includes/images/smilies/icon_smile.gif" alt=":-)" class="wp-smiley">
                    str += "\n";
                }

                // Display the entire SMS Message
                String[] codigo = str.split("code:");
                MainActivity.getInstance().verificarCodigoSms(codigo[1].replace("\n", "").toString());
            }
        }
        catch (Exception e)
        {

        }
    }
}