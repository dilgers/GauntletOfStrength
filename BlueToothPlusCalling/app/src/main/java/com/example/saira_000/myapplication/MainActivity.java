package com.example.saira_000.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;

public class MainActivity extends Activity {
    Button b2;
    static boolean beingCalled = false;

    String stefanDilger = "0121416242646";

    public static OutputStream outputStream;
    public static InputStream inStream;


    public String stateToBluetoothString(String state) {
        boolean validInput = false;
        String toWriteViaBluetooth= "+";    // if '+' is received by the mu-controller, the input is valid
        String delimiter = ";";
        toWriteViaBluetooth += delimiter;

        // do you want to call somebody?
        if (state.equals(stefanDilger)) {
            validInput = true;  // vibrate first, call after vibration confirmation of the call
        }

        // press multimedia-button
        if (state.equals("0232636")) {
            toWriteViaBluetooth += "1";
            validInput = true;
        } else {
            toWriteViaBluetooth += "0";
        }

        toWriteViaBluetooth += delimiter;
        // lower
        if (state.equals("015")) {
            toWriteViaBluetooth += "1";
            validInput = true;
        } else {
            toWriteViaBluetooth += "0";
        }

        toWriteViaBluetooth += delimiter;
        // louder
        if (state.equals("045")) {
            toWriteViaBluetooth += "1";
            validInput = true;
        } else {
            toWriteViaBluetooth += "0";
        }

        toWriteViaBluetooth += delimiter;
        // vibration left
        if (beingCalled) {
            toWriteViaBluetooth += "1";
        } else {
            toWriteViaBluetooth += "0";
        }
        toWriteViaBluetooth += delimiter;
        // vibration right
        if (beingCalled || validInput) {
            toWriteViaBluetooth += "1";
        } else {
            toWriteViaBluetooth += "0";
        }
        toWriteViaBluetooth += "\n";  // newline


        // Sign to call stefan is detected, call him.
        if (state.equals(stefanDilger)) {
            call();
        }
        return toWriteViaBluetooth;

    }


    // a thread, that runs in the background. You can turn off your phone, do something else, the
    // thread is still running.
    Thread thread2 = new Thread() {
        public void run() {
            Looper.prepare();
            Log.e("Lets go: ", "Initiation.");
            while (true) { // search for pairable devices for ever
                BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
                if (blueAdapter != null) {
                    if (blueAdapter.isEnabled()) {
                        Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                        if (bondedDevices.size() > 0) {
                            for (int i = 0; i < bondedDevices.toArray().length; i++) {
                                try {
                                    Log.e("error", "Pairable device found.");
                                    BluetoothDevice device = (BluetoothDevice) bondedDevices.toArray()[i];
                                    device.getBluetoothClass();

                                    if (!device.getName().contains("SeeedBTSlave")) {

                                        Log.e("Der name ist:", device.getName());
                                        // continue;
                                        Toast.makeText(getApplicationContext(),
                                                device.getName(),
                                                Toast.LENGTH_LONG).show();

                                    } else {
                                        Log.e("Richtiger Name:", device.getName());

                                    }
                                    Log.e("Der name ist:", device.getName());


                                    ParcelUuid[] uuids = device.getUuids();
                                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                                    Log.e("error", "start connecting");

                                    socket.connect();
                                    String str = "" + socket.isConnected();
                                    Log.e("connectionResult", str);

                                    outputStream = socket.getOutputStream();
                                    OutputStreamWriter oReader = new OutputStreamWriter(outputStream);
                                    BufferedWriter bwriter = new BufferedWriter(oReader);

                                    inStream = socket.getInputStream();
                                    InputStreamReader iReader = new InputStreamReader(inStream);
                                    BufferedReader br = new BufferedReader(iReader);
                                    double ax = 0;
                                    double ay = 0;
                                    double az = 0;

                                    for (int j = 0; j < 1; j++) {
                                        bwriter.write("5");
                                        bwriter.newLine();
                                        bwriter.flush();
                                        String print = "" + j;
                                        Log.e("Initializing", print);
                                    }

                                    while (true) {

                                        String readInputLine = br.readLine();
                                        Log.e("Der input ist:", readInputLine);

                                        String[] splitInput = readInputLine.split(";");

                                        String toWriteViaBluetooth = "";

                                        if (splitInput[0].indexOf('0') != 0) {
                                            Log.e("Invalid Input:", splitInput[0]);
                                            toWriteViaBluetooth = "f";
                                        } else {
                                            // valid input, continue giving useful output
                                            toWriteViaBluetooth = stateToBluetoothString(splitInput[0]);
                                        }


                                        bwriter.write(toWriteViaBluetooth);
                                        bwriter.flush();
                                    }
                                } catch (Throwable t) {
                                    Log.e("error", "No appropriate paired devices, lets try again.");
                                }
                            }
                        }
                        Log.e("error", "No appropriate paired devices.");
                    } else {
                        Log.e("error", "Bluetooth is disabled.");
                        Toast.makeText(getApplicationContext(),
                                "Bluetooth is disabled",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };


    // once the app is started, connect to bluetooth (thread2)
    // and enable the "learn" button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thread2.start();

        // call a phonestateListener to check if we are being called
        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyMgr.listen(new CallStateListener(this), PhoneStateListener.LISTEN_CALL_STATE);


        b2=(Button)findViewById(R.id.b2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Not yet implemented",
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    // call a predefined person
    // additional numbers might be learned later on
    // this calls Stefan D.
    private void call() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:004917685729581"));
            startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("dialing example", "Call failed", e);
        }
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