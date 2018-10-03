package com.example.jdrzej.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static android.R.attr.data;
import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    Button b1;
    Button b2;
    Button b3;
    Button b4;
    Button b5;
    Button b6;
    TextView t1;
    TextView t2;
    TextView kontakt;
    EditText tekst;
    EditText nrTelefonu;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    List<BluetoothDevice> deviceSet = new ArrayList<>();
    String adresat;
    String numer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Pozwolenia na wykonanie przez aplikację różych funkcji na telefonie, taskich jak wysłanie wiadomości, włączenie Bluetooth
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},2);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH}, 4);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, 5);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        Przypisywanie zmiennym z pliku MainActivity.java ID ich komponentom graficznym w pliku content_main.xml
//        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
//        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        b6 = (Button) findViewById(R.id.button6);
//        t1 = (TextView) findViewById(R.id.textView2);
//        t2 = (TextView) findViewById(R.id.textView3);
        kontakt = (TextView) findViewById(R.id.textView4);
        kontakt.setText("odbiorca");
        tekst = (EditText) findViewById(R.id.editText);
        nrTelefonu = (EditText) findViewById(R.id.editText2);



//        Zmienna, która przechowuje adresata wiadomości
        adresat = "null";
//        Zmienna, która przechowuje numer adresata
        numer = "null";

//        t1.setText("akcja");
//        t1.append(" ");


//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dajSieWykryc();
//            }
//        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetoothOnDevice();
                wykryjInne();
            }
        });
//        b3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pokazListe();
//            }
//        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polaczSie();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wyslij();
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wybierzOdbiorce();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

//    metoda włączająca urządzenie Bluetooth w przypadku gdu jest ono wyłączone
    private void enableBluetoothOnDevice()    {
        if( !mBluetoothAdapter.isEnabled()) {
            Log.d("INFO", "Bluetooth zostaje włączony");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 6);
        }
        else {
            Log.d("INFO", "Bluetooth jest już łączony");
        }
    }
//    motoda wykonywana przy kliknięciu przycisku ODBIORCA. Przekierowuje użytkownika do listy kontaktów
    private void wybierzOdbiorce()  {
        kontakt.setText("odbiorca");
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 7);
    }


    @Override public void onActivityResult(int reqCode, int resultCode, Intent data){ super.onActivityResult(reqCode, resultCode, data);
        switch(reqCode)
        {
            case (7):
                if (resultCode == Activity.RESULT_OK){
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()){
                        String nazwaKontaktu = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1"))
                        {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            kontakt.setText(nazwaKontaktu);
                            nrTelefonu.setText(cNumber);
                        }
                        else{
                            Log.d("brak wyboru", "nie wybrano kontaktu");
                        }
                    }
                    else{
                        Log.d("brak wyboru", "nie wybrano kontaktu");
                    }
                }
                else{
                    Log.d("brak wyboru", "uzytkownik opuscil liste kontaktow bez wyboru odbiorcy");
                }
        }
    }

    private final BroadcastReceiver odbiorca = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {

            String akcja = i.getAction();
//            t1.append("\n"+akcja.toString());
            if (BluetoothDevice.ACTION_FOUND.equals(akcja)) {
                BluetoothDevice device = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("znaleziono urządzenie", "Wykryto urządzenie " +device.getAddress().toString());
                if(!deviceSet.contains(device)){
                    deviceSet.add(device);
//                    t1.append("\n" + device.getAddress().toString());
                }
            }
        }
    };


    public void wykryjInne(){
//        t1.setText("");
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.registerReceiver(odbiorca, bluetoothFilter);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
//            t2.append("1");
        }
        if (!bluetoothAdapter.isEnabled()) {
//            t2.append("2");
        }
        if (bluetoothAdapter.isDiscovering()) {
//            t2.append("3");// cancel the discovery if it has already started
            bluetoothAdapter.cancelDiscovery();
        }
        Log.d("start","start");
        if (bluetoothAdapter.startDiscovery()) {
//            t2.append("4");// bluetooth has started discovery
        }
        Log.d("stop","stop");
    }

    private void pokazListe() {
//        t2.setText("");
//        t2.append(deviceSet.toString());
    }
//    public void dajSieWykryc() {
//        Intent pokazSie = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        pokazSie.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(pokazSie);
//    }
    private void polaczSie() {
        Log.d("connectrion start", "zaczynam się łączyć");
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice serwer;
        serwer = mBluetoothAdapter.getRemoteDevice("B8:27:EB:B5:64:1B");
        new BluetoothClient(serwer, tekst, this).start();

//        for (int i=0; i<deviceSet.size(); i++) {
//            if (deviceSet.get(i).getAddress().equals("B8:27:EB:B5:64:1B")) {
//                serwer = deviceSet.get(i);
//                Log.d("długość listy", "ilość stringów uuid wynosi "+serwer.getUuids().length);
//                for (int j=0; j<serwer.getUuids().length; j++) {
//                    Log.d("lista uuids", "element nr "+(j+1)+" to "+serwer.getUuids()[j].toString());
//                }
//                new BluetoothClient(serwer, tekst, this).start();
//                Log.d("połączone wygranko", "wychodzę z pętli");
//                break;
//            }
//            else {
//                Log.d("INFOBLuetooth", "nie wykryto urządzenia Raspberry Pi 3");
//            }
//        }

    }

    private void wyslij() {
        Log.d("wiadomosc", "wysyłam wiadomość");
        String odbiorca = null;
        String wiadomosc = null;
        numer = nrTelefonu.getText().toString();
        odbiorca = numer;
        wiadomosc = tekst.getText().toString();
        new Sms(odbiorca, wiadomosc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
