package com.example.mdpbluetoothtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    // UI Components
    ImageView arrowUpImageView, arrowLeftImageView, arrowRightImageView;
    TextView currentStatusTextView;
    ToggleButton setWayPointToggleBtn, setStartPointToggleBtn;
    ImageButton obstacleImageBtn, clearImageBtn;
    Button resetMapBtn, fastestPathBtn, explorationPathBtn;
    Switch manualAutoToggleBtn, tiltSwitch;
    MazeView mazeView;
    ReconfigureFragment reconfigureFragment = new ReconfigureFragment();

    // Non UI Components - Sensors, Services
    BluetoothConnectionService bluetoothConnectionService;
    SensorManager sensorManager;
    Sensor accelerometerSensor;
    SensorEventListener tiltSensorEventListener;

    private static boolean autoUpdate = false;
    private boolean tiltIsAllowedFlag = true;

    // String Constants
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Non UI Components
        bluetoothConnectionService = new BluetoothConnectionService(this);
        IntentFilter incomingMessageIntentFilter = new IntentFilter("IncomingMessage");
        LocalBroadcastManager.getInstance(this).registerReceiver(incomingMessageReceiver, incomingMessageIntentFilter);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);



        // Setup UI and Eventlistener
        currentStatusTextView = findViewById(R.id.currentStatusTextView);
        mazeView = findViewById(R.id.mazeView);

        arrowLeftImageView = findViewById(R.id.arrowLeftBtn);
        arrowLeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveRobotUI("left");
            }
        });

        arrowRightImageView = findViewById(R.id.arrowRightBtn);
        arrowRightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveRobotUI("right");
            }
        });

        arrowUpImageView = findViewById(R.id.arrowUpBtn);
        arrowUpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveRobotUI("forward");
            }
        });

        tiltSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent){
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];

                if(tiltIsAllowedFlag) {
                    if (y < -2) {
                        Log.d(TAG, "onSensorChanged: " + "Sensor Move Forward Detected");
                        moveRobotUI("forward");
                    } else if (y > 2) {
                        Log.d(TAG, "onSensorChanged: " + "Sensor Move Backward Detected");
                    } else if (x > 2) {
                        Log.d(TAG, "onSensorChanged: " + "Sensor Move Left Detected");
                        moveRobotUI("left");
                    } else if (x < -2) {
                        Log.d(TAG, "onSensorChanged: " + "Sensor Move Right Detected");
                        moveRobotUI("right");
                    }

                    tiltIsAllowedFlag = false;
                    // Set flag to true X milliseconds later to limit the rate of accelerator update
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tiltIsAllowedFlag = true;
                        }
                    }, 1000);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                return;
            }
        };

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tiltSwitch = findViewById(R.id.phoneTiltSwitch);
        tiltSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(tiltSwitch.isChecked()){
                    Toast.makeText(MainActivity.this, "Tilt Controls are turned On", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCheckedChanged: Tilt is turned On");

                    sensorManager.registerListener(tiltSensorEventListener, accelerometerSensor, 1000 * 1000 * 1);
//                    sensorManager.registerListener(tiltSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                else {
                    sensorManager.unregisterListener(tiltSensorEventListener, accelerometerSensor);
                    updateStatus("Tilt is turned offed");
                }
            }
        });

        fastestPathBtn = findViewById(R.id.fastestPathBtn);
        fastestPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnectionService.write("Fastest Path");
                updateStatus("Fastest Path Started");
            }
        });

        explorationPathBtn = findViewById(R.id.explorationPathBtn);
        explorationPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnectionService.write("Exploration Path");
                updateStatus("Exploration Path Started");
            }
        });

        setStartPointToggleBtn = findViewById(R.id.setStartPointToggleBtn);
        setStartPointToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setStartPointToggleBtn.getText().equals("Set Startpoint")){
                    updateStatus("Cancelled setting start point");
                }
                else if (setStartPointToggleBtn.getText().equals("Cancel") && !mazeView.getAutoUpdate()) {
                    updateStatus("Please select starting point");
                    mazeView.setStartCoordStatus(true);
                    mazeView.toggleCheckedBtn("setStartPointToggleBtn");
                } else
                    updateStatus("Please select manual mode");
            }
        });

        setWayPointToggleBtn = findViewById(R.id.setWayPointToggleBtn);
        setWayPointToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setWayPointToggleBtn.getText().equals("Set Waypoint"))
                    updateStatus("Cancelled selecting waypoint");
                else if (setWayPointToggleBtn.getText().equals("Cancel")) {
                    updateStatus("Please plot waypoint on map");
                    mazeView.setWaypointStatus(true);
                    mazeView.toggleCheckedBtn("setWaypointToggleBtn");
                }
                else
                    Toast.makeText(MainActivity.this, "Please select manual mode", Toast.LENGTH_SHORT).show();
            }
        });

        resetMapBtn = findViewById(R.id.resetMapBtn);
        resetMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus("Reseting map...");
                mazeView.resetMap();
            }
        });

        obstacleImageBtn = findViewById(R.id.obstacleImageBtn);
        obstacleImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mazeView.getSetObstacleStatus()) {
                    updateStatus("Please plot obstacles");
                    mazeView.setSetObstacleStatus(true);
                    mazeView.toggleCheckedBtn("obstacleImageBtn");
                }
                else if (mazeView.getSetObstacleStatus())
                    mazeView.setSetObstacleStatus(false);
            }
        });

        clearImageBtn = findViewById(R.id.clearImageBtn);
        clearImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mazeView.getUnSetCellStatus()) {
                    updateStatus("Please remove cells");
                    mazeView.setUnSetCellStatus(true);
                    mazeView.toggleCheckedBtn("clearImageBtn");
                }
                else if (mazeView.getUnSetCellStatus())
                    mazeView.setUnSetCellStatus(false);
            }
        });

        manualAutoToggleBtn = findViewById(R.id.manualAutoToggleBtn);
        manualAutoToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manualAutoToggleBtn.getText().equals("MANUAL")) {
                    try {
                        mazeView.setAutoUpdate(true);
                        autoUpdate = true;
                        mazeView.toggleCheckedBtn("None");
//                        updateButton.setClickable(false);
//                        updateButton.setTextColor(Color.GRAY);
                        manualAutoToggleBtn.setText("AUTO");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateStatus("AUTO mode");
                }
                else if (manualAutoToggleBtn.getText().equals("AUTO")) {
                    try {
                        mazeView.setAutoUpdate(false);
                        autoUpdate = false;
                        mazeView.toggleCheckedBtn("None");
//                        updateButton.setClickable(true);
//                        updateButton.setTextColor(Color.BLACK);
                        manualAutoToggleBtn.setText("MANUAL");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateStatus("MANUAL mode");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_bluetooth:
                intent = new Intent(this, BluetoothActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_map:
//                intent = new Intent(this, BluetoothActivity.class);
//                return true;
                Toast.makeText(this, "Go to Map though", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_reconfigure:
                reconfigureFragment.show(getFragmentManager(), "Reconfigure Fragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver incomingMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messageValue = intent.getStringExtra("Message Value");
            // TODO : Standardise Status messages. Are they JSON or just string etc.

            if(messageValue.startsWith("{\"status\""))
                currentStatusTextView.setText(messageValue);
            else if (messageValue.startsWith("{\"image\""))
                // TODO : Update Map with correct image and coorindates
                Log.d(TAG, "onReceive: " + messageValue);
        }
    };

    private void moveRobotUI(String direction) {
        String bluetoothDirectionMsg = "";

        if (mazeView.getAutoUpdate())
            updateStatus("Please press 'MANUAL'");
        else if (mazeView.getCanDrawRobot() && !mazeView.getAutoUpdate()) {
            mazeView.moveRobot(direction);
            switch(direction) {
                case "left":
                    updateStatus("turning " + direction);
                    bluetoothDirectionMsg = "A";
                    break;
                case "right":
                    updateStatus("turning " + direction);
                    bluetoothDirectionMsg = "D";
                    break;
                case "forward":
                    if (mazeView.getValidPosition()) {
                        updateStatus("moving forward");
                        bluetoothDirectionMsg = "W";
                    }
                    else
                        updateStatus("Unable to move forward");
                    break;
                default :
                    bluetoothDirectionMsg = "";

            }

            if (bluetoothConnectionService != null && !bluetoothDirectionMsg.isEmpty()) {
                bluetoothConnectionService.write(bluetoothDirectionMsg);
            }
        }
        else
            updateStatus("Please set starting point first");
    }

    private void updateStatus(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0, 0);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            sensorManager.unregisterListener(tiltSensorEventListener, accelerometerSensor);
        }
        catch (Exception e ) {
            Log.e(TAG, "onDestroy: Failed to unregister Accelerometer Listener ? Possibly not registered in the first place");
        }

        try {
            unregisterReceiver(incomingMessageReceiver);
        }
        catch (Exception e ) {
            Log.e(TAG, "onDestroy: Failed to unregister incomingMessageReceiver ? Possibly not registered in the first place");
        }
    }
}