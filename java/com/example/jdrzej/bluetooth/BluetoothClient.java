package com.example.jdrzej.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by Jędrzej on 2017-01-04.
 */

public class BluetoothClient extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    EditText tekst;
    Activity mainActivity;

    public BluetoothClient(BluetoothDevice device, EditText tekst, Activity activity) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        mainActivity = activity;
        this.tekst = tekst; //this.tekst to zmienna tej klasy, a tekst to parametr konstruktora
        try {
            UUID uuid = UUID.fromString("0fee0450-e95f-11e5-a837-0800200c9a66");
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {
        }
        mmSocket = tmp;
    }


    public void run() {
            try {

                    Log.d("INFO", "Próba połączenia....");
                    mmSocket.connect();
                    Log.d("INFO", "Połączono z serwerem!");
                    if (mmSocket.isConnected()) {
                        Log.d("INFO", "Połączono z serwerem! w opór");
                    }

                    String str = "powiazano";
//          wysyłanie
//            mmSocket.getOutputStream().write(str.getBytes("US-ASCII")); // or UTF-8 or any other applicable encoding..
                    mmSocket.getOutputStream().write(tekst.getText().toString().getBytes("US-ASCII"));
//          odbieranie
//            pętla while działa w nowym wtątku, wiec można ją zapętlić w nieskończoność

                    Log.d("wchodze", "wchodze do petli");
                    InputStream in = mmSocket.getInputStream();
                    InputStreamReader inn = new InputStreamReader(in);
                    BufferedReader innn = new BufferedReader(inn);

//            int bytes = in.read(x);
//            Log.d("ilosc bajtów", Integer.toString(bytes));

                while (true) {
                    final String wiadomosc = innn.readLine();
//          String wiadomosc = new String(x, "US-ASCII");
//                    jak chcemy wykonywac operacje na obiektach graficznych, ktore znajduja sie w innej klasie to trzeba uzyc runOnUiThread
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tekst.setText(wiadomosc);
                        }
                    });

                    Log.d("wiadomosc", wiadomosc);

                }
            } catch (Exception ce) {
                ce.printStackTrace();
            }

        }
    }

