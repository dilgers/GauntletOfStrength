package com.example.sairamkrishna.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;

public class MainActivity extends Activity {



private OutputStream outputStream;
private InputStream inStream;
    Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;


    public void on(View v) throws IOException {
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
                            Log.e("Der name ist:", device.getName());



                            ParcelUuid[] uuids = device.getUuids();
                            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            Log.e("error", "start connecting");

                            socket.connect();
                            String str = "" + socket.isConnected();
                            Log.e("connectionResult", str);
                            outputStream = socket.getOutputStream();
                            inStream = socket.getInputStream();
                            InputStreamReader iReader = new InputStreamReader(inStream);
                            BufferedReader br = new BufferedReader(iReader);
                            double ax = 0.0;
                            double ay = 0.0;
                            double az = 0.0;
                            while (true) {
                                String readInputLine = br.readLine();
                                String[] splitLine = readInputLine.split(";");
                                for (int g = 0; g < splitLine.length; g++) {
                                    if (g == 1) Log.e("element", splitLine[g]);
                                }
                                //Log.e("Der input ist:", readInputLine);
                            }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
                Log.e("error", "Feeehler.");
            // init();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        b1.setX(-200);
        b1.setY(0);
        b2 = (Button) findViewById(R.id.button2);
        b2.setX(-50);
        b2.setY(0);
        b3 = (Button) findViewById(R.id.button3);
        b3.setX(100);
        b3.setY(0);
        b4 = (Button) findViewById(R.id.button4);
        b4.setX(250);
        b4.setY(0);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView) findViewById(R.id.listView);
    }


private void init() throws IOException {
    Log.e("error", "Initiation.");
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
        if (blueAdapter.isEnabled()) {
        Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

        if(bondedDevices.size() > 0){
        BluetoothDevice[] devices = (BluetoothDevice[]) bondedDevices.toArray();
        BluetoothDevice device = devices[0];
        ParcelUuid[] uuids = device.getUuids();
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
        socket.connect();
        outputStream = socket.getOutputStream();
        inStream = socket.getInputStream();
        }

        Log.e("error", "No appropriate paired devices.");
        }else{
        Log.e("error", "Bluetooth is disabled.");
        }
        }
        }

public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
        }

public void run() {
    Log.e("error", "run method started");
final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;
    Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        while (true) {
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            System.out.println("test");
        try {
            bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            String answer = "hallo";
            write(answer);
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
        }
        }
/*

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity  {
    Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        b1.setX(-200);
        b1.setY(0);
        b2=(Button)findViewById(R.id.button2);
        b2.setX(-50);
        b2.setY(0);
        b3=(Button)findViewById(R.id.button3);
        b3.setX(100);
        b3.setY(0);
        b4=(Button)findViewById(R.id.button4);
        b4.setX(250);
        b4.setY(0);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
    }

    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(),"Turned off" ,Toast.LENGTH_LONG).show();
    }

    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void list(View v){
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
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




    // -----------------------------Additional Methods----------------------------------------------
    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(getApplicationContext(),"Found device " + device.getName(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
*/