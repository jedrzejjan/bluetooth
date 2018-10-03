package com.example.jdrzej.bluetooth;

import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by Jędrzej on 2017-01-07.
 */

public class Sms {
    public Sms (String odbiorca, String wiadomosc){
//      stworzony zostaje obiekt klasy SmsManager, czyli narzędzie służące do wysyłania SMS
        if (odbiorca.equals("")) {
            Log.d("blad", "brak odbiorcy");
        }
        else {
            SmsManager smsJedrzej = null;
            smsJedrzej = SmsManager.getDefault();
            smsJedrzej.sendTextMessage(odbiorca, null, wiadomosc, null, null);
        }
        }
    }

