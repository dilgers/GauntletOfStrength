package com.example.saira_000.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;

public class MainActivity extends Activity {
    Button b1, b2;
    static boolean beingCalled = false;
    Activity acti = this;

    public static OutputStream outputStream;
    public static InputStream inStream;
    // Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;

    Thread thread2 = new Thread() {
        public void run() {
            Looper.prepare();
        // void connectToBluetooth() {
            Log.e("error", "Feeehler.");
            Log.e("error", "Initiation.");
            BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
            if (blueAdapter != null) {
                if (blueAdapter.isEnabled()) {
                    Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                    if(bondedDevices.size() > 0){
                        //BluetoothDevice[] devices = (BluetoothDevice[])
                        // BluetoothDevice[] devices = (BluetoothDevice[])
                        for (int i = 0; i < bondedDevices.toArray().length; i++) {
                            try {
                                Log.e("error", "Pairable device found.");
                                BluetoothDevice  device = (BluetoothDevice) bondedDevices.toArray()[i];
                                device.getBluetoothClass();

                                if (device.getName() != " SeeedBTSlave") {
                                    Log.e("Der name ist:", device.getName());

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

                                for (int j = 0; j < 500; j++) {
                                    bwriter.write("5");
                                    bwriter.newLine();
                                    bwriter.flush();
                                }

                                while (true) {

                                    String readInputLine = br.readLine();
                                    Log.e("Der input ist:", readInputLine);


                                    // werden wir angerufen?
                                    String toWriteViaBluetooth= "";
                                    if (beingCalled) {
                                        toWriteViaBluetooth += "1";
                                        Log.e("Wir werden angerufen: ", "true");
                                    } else {
                                        toWriteViaBluetooth += "0";
                                    }

                                    toWriteViaBluetooth += ",";

                                    // Multimediabutton drücken
                                    toWriteViaBluetooth += "";

                                    toWriteViaBluetooth += ",";
                                    // lauter
                                    toWriteViaBluetooth += ",";
                                    // leiser
                                    toWriteViaBluetooth += ",";
                                    // vibration links
                                    toWriteViaBluetooth += ",";
                                    // vibration rechts
                                    toWriteViaBluetooth += ",";


                                    bwriter.write(toWriteViaBluetooth);
                                    bwriter.newLine();
                                    bwriter.flush();


                                }

    /*
                                while (true) {
                                    String readInputLine = br.readLine();
                                    Log.e("Der input ist:", readInputLine);
                                }

    */
                            } catch (Throwable t) {
                                Log.e("error", "No appropriate paired devices.");
                            }
                        }
                        /*BluetoothDevice device = devices[0];
                        ParcelUuid[] uuids = device.getUuids();
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();*/
                    }

                    Log.e("error", "No appropriate paired devices.");
                }else{
                    Log.e("error", "Bluetooth is disabled.");
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        b1=(Button)findViewById(R.id.b1);

        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyMgr.listen(new CallStateListener(this), PhoneStateListener.LISTEN_CALL_STATE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });


        b2=(Button)findViewById(R.id.b2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread2.start();// connectToBluetooth();
                /* MyBluetoothThread mBT = new MyBluetoothThread(acti);
                Thread thread3 = new Thread(mBT);
                thread3.run(); //in current thread*/
            }
        });


        // DialogInterface.OnClickListener Listener = new OnClickListener();
        // thread2.start();



    }


/*

    Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                while (true) {
                    try {
                        if (beingCalled) {
                            // do something here
                            Toast.makeText(getApplicationContext(), "im being called", Toast.LENGTH_LONG).show();
                            Thread.sleep(1000);
                        } else {
                            // Toast.makeText(getApplicationContext(), "im not called", Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        // Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        thread.start();



        // connectToBluetooth();
    }

*/
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


class MyBluetoothThread implements Runnable {
    Activity act;
    MyBluetoothThread(Activity act_) {
        act = act_;
    }
    @Override
    public void run() {

        // check if it's run in main thread, or background thread
        if(Looper.getMainLooper().getThread()==Thread.currentThread()){
            //in main thread
            // textInfo.setText("in main thread");
            Log.e("bluetooth", "In main Thread");
        }else{
            //in background thread

            act.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.e("bluetooth", "In background Thread");
                }

            });
        }

    }

}

