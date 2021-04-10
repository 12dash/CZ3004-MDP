package com.example.mdpbluetoothtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // UI Components
    ImageView arrowUpImageView, arrowLeftImageView, arrowRightImageView;
    TextView currentStatusTextView;
    ToggleButton setWayPointToggleBtn, setStartPointToggleBtn;
    ImageButton obstacleImageBtn, clearImageBtn;
    Button resetMapBtn, fastestPathBtn, explorationPathBtn, f1Btn, f2Btn, updateBtn, irBtn, calibrateBtn;
    Switch manualAutoToggleBtn, tiltSwitch;
    MazeView mazeView;
    ReconfigureFragment reconfigureFragment = new ReconfigureFragment();
    StringFragment stringFragment = new StringFragment();

    // Non UI Components - Sensors, Services
    BluetoothConnectionService bluetoothConnectionService;
    SensorManager sensorManager;
    Sensor accelerometerSensor;
    SensorEventListener tiltSensorEventListener;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static boolean manualUpdateRequest = false;
    private boolean tiltIsAllowedFlag = true;

    public static ArrayList<ArrayList<Integer>> imagecoordList = new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<Integer>> returnArrayList(){
        return imagecoordList;
    }

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
        sharedPreferences = this.getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);



        f1Btn = findViewById(R.id.btn_F1);
        f1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String F1 = sharedPreferences.getString("F1", "");
                Log.d(TAG, F1);
                if (F1.equals("L") || F1.equals("R") || F1.equals("0")) {
                    moveRobotUI(F1);
                }
                else {
                    bluetoothConnectionService.write(F1);
                }
            }
        });

        f2Btn = findViewById(R.id.btn_F2);
        f2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String F2 = sharedPreferences.getString("F2", "");
                if (F2.equals("L") || F2.equals("R") || F2.equals("0")) {
                    moveRobotUI(F2);
                }
            }
        });

        // Setup UI and Eventlistener
        currentStatusTextView = findViewById(R.id.currentStatusTextView);
        mazeView = findViewById(R.id.mazeView);

        arrowLeftImageView = findViewById(R.id.arrowLeftBtn);
        arrowLeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveRobotUI("L");
            }
        });

        arrowRightImageView = findViewById(R.id.arrowRightBtn);
        arrowRightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveRobotUI("R");
            }
        });

        arrowUpImageView = findViewById(R.id.arrowUpBtn);
        arrowUpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveRobotUI("0");
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
                        moveRobotUI("ar|0");
                    } else if (y > 2) {
                        Log.d(TAG, "onSensorChanged: " + "Sensor Move Backward Detected");
                    } else if (x > 2) {
                        Log.d(TAG, "onSensorChanged: " + "Sensor Move Left Detected");
                        moveRobotUI("ar|L");
                    } else if (x < -2) {
                        Log.d(TAG, "onSensorChanged: " + "Sensor Move Right Detected");
                        moveRobotUI("ar|R");
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
                bluetoothConnectionService.write("pc|start:FS");
                updateStatus("Fastest Path Started");
            }
        });

        explorationPathBtn = findViewById(R.id.explorationPathBtn);
        explorationPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnectionService.write("pc|start:ES");
                updateStatus("Exploration Path Started");
            }
        });

        irBtn = findViewById(R.id.irBtn);
        irBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnectionService.write("pc|start:IR");
                updateStatus("Image Recognition Started");
            }
        });

        calibrateBtn = findViewById(R.id.calibrateButton);
        calibrateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnectionService.write("ar|T");
                updateStatus("Calibrating Robot...");
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

        // If performing checklist, comment out (String message = MDF String) and (setReceivedJsonObject) line.
        // If performing fastest path, comment out "sendArena" line
        updateBtn = findViewById(R.id.updateButton);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualUpdateRequest = true;
                bluetoothConnectionService.write("pc|{\"sendArena\" : \"true\"}");
                try {
                    //try p1 p2 string here
                    String message = "{\"p1\":\"C7000C001C008003000600000000000000000000000000000000000000000000000000000003\",\"p2\":\"2918\"}";
                    mazeView.setReceivedJsonObject(new JSONObject(message));
                    mazeView.updateMapInformation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        //ALWAYS SET STARTPOINT/ROBOT FIRST.
        manualAutoToggleBtn = findViewById(R.id.manualAutoToggleBtn);
        manualAutoToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manualAutoToggleBtn.getText().equals("MANUAL")) {
                    try {
                        mazeView.setAutoUpdate(true);
                        mazeView.toggleCheckedBtn("None");
                        updateBtn.setClickable(false);
                        updateBtn.setTextColor(Color.GRAY);
//                        ControlFragment.getCalibrateButton().setClickable(false);
//                        ControlFragment.getCalibrateButton().setTextColor(Color.GRAY);
                        manualAutoToggleBtn.setText("AUTO");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Auto Mode", Toast.LENGTH_SHORT).show();
                }
                else if (manualAutoToggleBtn.getText().equals("AUTO")) {
                    try {
                        mazeView.setAutoUpdate(false);
                        mazeView.toggleCheckedBtn("None");
                        updateBtn.setClickable(true);
                        updateBtn.setTextColor(Color.WHITE);
//                        ControlFragment.getCalibrateButton().setClickable(true);
//                        ControlFragment.getCalibrateButton().setTextColor(Color.BLACK);
                        manualAutoToggleBtn.setText("MANUAL");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Manual Mode", Toast.LENGTH_SHORT).show();
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
                stringFragment.show(getFragmentManager(), "String Fragment");
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
            try {
                // Converts AMD "grid" json to Rpi's "map" json by setting setReceivedJsonObject(messageValue) -> updateMapInformation()
                // Note MDF String might be reversed format from pdf
                if (messageValue.length() > 7 && messageValue.substring(2,6).equals("grid")) {
                    String resultString = "";
                    String amdString = messageValue.substring(11,messageValue.length()-2);
                    Log.d(TAG,"amdString: " + amdString);
                    BigInteger hexBigIntegerExplored = new BigInteger(amdString, 16);
                    String exploredString = hexBigIntegerExplored.toString(2);

                    while (exploredString.length() < 300)
                        exploredString = "0" + exploredString;

                    for (int i=0; i<exploredString.length(); i=i+15) {
                        int j=0;
                        String subString = "";
                        while (j<15) {
                            subString = subString + exploredString.charAt(j+i);
                            j++;
                        }
                        resultString = subString + resultString;
                    }
                    hexBigIntegerExplored = new BigInteger(resultString, 2);
                    resultString = hexBigIntegerExplored.toString(16);

                    JSONObject amdObject = new JSONObject();
                    amdObject.put("p1", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    amdObject.put("p2", resultString);
                    JSONObject amdMessage = new JSONObject();
                    messageValue = String.valueOf(amdObject);
                    Log.d(TAG,"Executed for AMD message, message: " + messageValue);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                // This branch is only for demo-ing. Allows Status to be updated even when in Manual Mode.
                if (messageValue.length() > 8 && messageValue.substring(2,8).equals("status") && !mazeView.getAutoUpdate()){
                    JSONObject statusJsonObject = new JSONObject(messageValue);
                    currentStatusTextView.setText(statusJsonObject.getString("status"));
                }
//                else if (messageValue.startsWith("{\"image\"")) {
                else if (messageValue.length() > 8 && messageValue.substring(2,7).equals("image")){
                    JSONObject jsonObject = new JSONObject(messageValue);
                    JSONArray jsonArray = jsonObject.getJSONArray("image");
                    int xCoordinate = jsonArray.getInt(0);
                    int yCoordinate = jsonArray.getInt(1);
                    int imageId = jsonArray.getInt(2);

                    boolean exists = false;
                    for(ArrayList<Integer> imageInfo : imagecoordList) {
                        if(imageInfo.get(2) == imageId){ // If imageId already exists
                            exists = true;

                            //Change the block status to obstacle, Note the + 1, since the coordinate system is problematic
                            int oldX = imageInfo.get(0) + 1;
                            int oldY = imageInfo.get(1) + 1;
                            mazeView.setObstacleCoord(oldX, oldY);

                            imageInfo.set(0, xCoordinate);
                            imageInfo.set(1, yCoordinate);
                        }
                    }

                    if (!exists) {
                        ArrayList<Integer> imgCoord = new ArrayList<Integer>();
                        imgCoord.add(xCoordinate);
                        imgCoord.add(yCoordinate);
                        imgCoord.add(imageId);
                        imagecoordList.add(imgCoord);
                    }

                    mazeView.drawImageNumberCell(jsonArray.getInt(0),jsonArray.getInt(1),jsonArray.getInt(2));

                    editor = sharedPreferences.edit();
                    editor.putString("IMAGE", imagecoordList.toString());
                    editor.commit();
                    Log.d("IMAGE", imagecoordList.toString());
                    Log.d(TAG,jsonArray.getInt(0)+ "," + jsonArray.getInt(1));
                }
            }
            catch (JSONException e){
                Log.d(TAG,"Adding Image Failed");
            }

            if (mazeView.getAutoUpdate() || MainActivity.manualUpdateRequest) {
                try {
                    mazeView.setReceivedJsonObject(new JSONObject(messageValue));
                    mazeView.updateMapInformation();
                    MainActivity.manualUpdateRequest = false;
                    Log.d(TAG, "Decode successfully");
                }
                catch (JSONException e){
                    Log.d(TAG, "Decode unsuccessfully");
                }
            }
        }
    };

    private void moveRobotUI(String direction) {
        String bluetoothDirectionMsg = "";

        if (mazeView.getAutoUpdate())
            updateStatus("Please press 'MANUAL'");
        else if (mazeView.getCanDrawRobot() && !mazeView.getAutoUpdate()) {
            mazeView.moveRobot(direction);
            switch(direction) {
                case "L":
                    updateStatus("turning " + direction);
                    bluetoothDirectionMsg = "L";
                    break;
                case "R":
                    updateStatus("turning " + direction);
                    bluetoothDirectionMsg = "R";
                    break;
                case "0":
                    if (mazeView.getValidPosition()) {
                        updateStatus("moving forward");
                        bluetoothDirectionMsg = "0";
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