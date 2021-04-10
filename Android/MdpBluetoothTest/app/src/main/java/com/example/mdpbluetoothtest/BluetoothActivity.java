package com.example.mdpbluetoothtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private static final String  TAG = "MAIN_ACITIVTY_MDP";
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothConnectionService bluetoothConnectionService;
    BluetoothAdapter bluetoothAdapter;
    SensorManager sensorManager;
    Sensor accelerometerSensor;

    Button bluetoothToggleBtn, discoverableToggleBtn, scanDevicesBtn, startServerBtn, sendBtn;
    ImageView arrowUpImageView, arrowLeftImageView, arrowRightImageView;
    Switch tiltSwitch;
    EditText messageEditText;
    TextView chatboxMsgTextView;
    ListView lvDevices;
    ProgressDialog progressDialog;

    public ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;

    private final BroadcastReceiver  receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "STATE OFF", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DISCONNECTED");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "STATE ON", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };
    
    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (mode) {
                    //Device is in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;

                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled, But able to receive connections from previously paired devices");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }
            }
        }
    };

    // This Receiver is to listen for discoverable devices when scanning, then adds them to the btDevices arrayList -> then updates the ListView
    private final BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getAddress().startsWith("38:00:25:3C:FB:1C") || device.getAddress().startsWith("B8:27:EB:BA:8D:EE") || device.getAddress().startsWith("30:24:32:ED"))
                {
                    Toast.makeText(context, "added" + device.getAddress(), Toast.LENGTH_SHORT).show();
                    btDevices.add(0, device);
                }
                else
                    btDevices.add(device);
                Log.d(TAG, "Receiver3 : " + device.getName() + ": " + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, btDevices);
                lvDevices.setAdapter(deviceListAdapter);

            }
        }
    };

    // Redundant Receiver just to debug Bond/Paired status
    private final BroadcastReceiver receiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                Log.d(TAG, "onReceive: BONDSTATE CHANGE");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "Receiver4 : BOND_BONDED");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "Receiver4 : BOND_BONDING");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "Receiver4 : BOND_NONE");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver connectionStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("Status");

            if (status.equals("connected")){
                try {
                    progressDialog.dismiss();
                } catch(NullPointerException e){
                    e.printStackTrace();
                }
            }

            //TODO : Device Connected to X using Intent and parcelable ?

            else if (status.equals("disconnected")) {
                Toast.makeText(context, "The bluetooth connection was lost", Toast.LENGTH_SHORT).show();
                try {
                    progressDialog.show();
                }
                catch (Exception e) {
                    // TODO : May want to use an application Context instead, so that Dialog / Toasts can be shown in MainActivity as well.
                    Log.d(TAG, "onReceive: Showing Dialog failed, probably because it is currently in main activty, cant show dialog in another activity");
                }
                bluetoothConnectionService.startAcceptThread();
            }
        }
    };

    private BroadcastReceiver incomingMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messageValue = intent.getStringExtra("Message Value");
            chatboxMsgTextView.append("Received : " + messageValue + "\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);
        bluetoothConnectionService = new BluetoothConnectionService(BluetoothActivity.this);

        IntentFilter bluetoothToggleIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver1, bluetoothToggleIntentFilter);
        IntentFilter discoverableIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(receiver2, discoverableIntentFilter);
        IntentFilter scanDevicesIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver3, scanDevicesIntentFilter);
        IntentFilter bondIntentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver4, bondIntentFilter);
        IntentFilter connectionStatusIntentFilter = new IntentFilter("ConnectionStatus");
        LocalBroadcastManager.getInstance(this).registerReceiver(connectionStatusReceiver, connectionStatusIntentFilter);
        IntentFilter incomingMessageIntentFilter = new IntentFilter("IncomingMessage");
        LocalBroadcastManager.getInstance(this).registerReceiver(incomingMessageReceiver, incomingMessageIntentFilter);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lvDevices = findViewById(R.id.lvDevices);
        btDevices = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting for other device to reconnect...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        bluetoothToggleBtn = findViewById(R.id.bluetoothToggleBtn);
        bluetoothToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBluetooth();
            }
        });

        discoverableToggleBtn = findViewById(R.id.discoverableToggleBtn);
        discoverableToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDiscovery();
            }
        });

        scanDevicesBtn = findViewById(R.id.scanDeviceBtn);
        scanDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDevices();
            }
        });

        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                  //pairDevice(position);
                  BluetoothDevice device = btDevices.get(position);
                  startBTConnection(device);
            }
        });

        startServerBtn = findViewById(R.id.startServerBtn);
        startServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDiscovery();
                bluetoothConnectionService.startAcceptThread();
            }
        });

        messageEditText = findViewById(R.id.messageEditText);
        chatboxMsgTextView = findViewById(R.id.chatboxMsgTextView);

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothConnectionService != null) {
                    String messageToSend = messageEditText.getText().toString();
                    bluetoothConnectionService.write(messageToSend);
                    chatboxMsgTextView.append("Sent : " + messageToSend + "\n");
                    messageEditText.setText("");
                }
                else
                    Toast.makeText(BluetoothActivity.this, "Please Connect with another device first", Toast.LENGTH_SHORT).show();
            }
        });

        arrowLeftImageView = findViewById(R.id.arrowLeftBtn);
        arrowLeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothConnectionService != null) {
                    bluetoothConnectionService.write("ar|L");
                }
                else
                    Toast.makeText(BluetoothActivity.this, "Please Connect with another device first", Toast.LENGTH_SHORT).show();
            }
        });

        arrowRightImageView = findViewById(R.id.arrowRightBtn);
        arrowRightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothConnectionService != null) {
                    bluetoothConnectionService.write("ar|R");
                }
                else
                    Toast.makeText(BluetoothActivity.this, "Please Connect with another device first", Toast.LENGTH_SHORT).show();
            }
        });

        arrowUpImageView = findViewById(R.id.arrowUpBtn);
        arrowUpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothConnectionService != null) {
                    bluetoothConnectionService.write("ar|0");
                }
                else
                    Toast.makeText(BluetoothActivity.this, "Please Connect with another device first", Toast.LENGTH_SHORT).show();
            }
        });

        SensorEventListener tiltSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent){
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                Log.d(TAG, "onSensorChanged: " + String.format("(%.2f, %.2f)", x, y));

                if (y < -2) {
                    Log.d(TAG, "onSensorChanged: " + "Sensor Move Forward Detected");
                    bluetoothConnectionService.write("ar|0");
                } else if (y > 2) {
                    Log.d(TAG, "onSensorChanged: " + "Sensor Move Backward Detected");
                } else if (x > 2) {
                    Log.d(TAG, "onSensorChanged: " + "Sensor Move Left Detected");
                    bluetoothConnectionService.write("ar|L");
                } else if (x < -2) {
                    Log.d(TAG, "onSensorChanged: " + "Sensor Move Right Detected");
                    bluetoothConnectionService.write("ar|R");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                return;
            }
        };

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tiltSwitch = findViewById(R.id.tiltSwitch);
        tiltSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(tiltSwitch.isChecked()){
                    Toast.makeText(BluetoothActivity.this, "Tilt Controls are turned On", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCheckedChanged: Tilt is turned On");

                    sensorManager.registerListener(tiltSensorEventListener, accelerometerSensor, 1000 * 1000 * 1);
                }
                else {
                    sensorManager.unregisterListener(tiltSensorEventListener, accelerometerSensor);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver1);
            unregisterReceiver(receiver2);
            unregisterReceiver(receiver3);
            unregisterReceiver(receiver4);
            unregisterReceiver(connectionStatusReceiver);
        }
        catch (IllegalArgumentException e ) {
            Log.d(TAG, "onDestroy: Whoops didnt register receiver before unregistering");
        }
    }

    private void toggleBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Unable to get Bluetooth adapter", Toast.LENGTH_SHORT).show();
        }
        else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
        }
        else {
            bluetoothAdapter.disable();
        }
    }

    private void enableDiscovery() {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");
        
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    private void scanDevices() {
        if (!bluetoothAdapter.isEnabled()){
            Toast.makeText(this, "Please Turn on bluetooth first", Toast.LENGTH_SHORT).show();
            return;
        }

        btDevices.clear();
        lvDevices.setAdapter(null);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        checkBTPermissions();
        bluetoothAdapter.startDiscovery();
    }

    @SuppressLint("NewApi")
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    // This method is not used, we directly connect using the ConnectThread
    private void pairDevice(int position) {
        bluetoothAdapter.cancelDiscovery();

        String deviceName = btDevices.get(position).getName();
        String deviceAddress = btDevices.get(position).getAddress();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            btDevices.get(position).createBond();
        }
    }

    private void startBTConnection(BluetoothDevice device) {
        Log.d(TAG, "startBTConnection: Starting Connection with Device : " + device.getName());
        bluetoothConnectionService.startConnectThread(device);
    }


}