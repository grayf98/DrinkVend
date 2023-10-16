package com.example.btcontroll;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UsbActivity extends AppCompatActivity {
    private UsbSerialPort usbSerialPort;
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection usbConnection;
    private Button connectButton;
    private boolean permissionGranted = false;

    private static final String ACTION_USB_PERMISSION = "com.example.btcontroll.USB_PERMISSION";

    Button whiscoke, whisga, whislem, marg, teqoj, teqspri, teqlem, teqsw, vodcran, screw, vodsw, vodspri, vodlem, rumcoke, rumlem, rumga;
    Button splcran, splga, sploj, splsw, splspri, spllem, splcoke;

    Button shotwhis, shotteq, shotvod, shotrum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);
        initializeButtons();
        initializeSplashesButtons();
        initializeShotsButtons();
        clicklistners();

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Log.d("USB", "UsbManager: " + usbManager);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            Log.d("USB", "Device Name: " + device.getDeviceName() + " Product Name: " + device.getProductName());
        }

        registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION));

        UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Log.d("USB", "Retrieved device from intent: " + (device == null ? "null" : device.getDeviceName()));

        if (device != null && device.getInterfaceCount() > 0) {
            requestPermission(device);
        } else {
            Log.d("USB", "Either no device connected or device has no interfaces.");
        }

        Log.d("USB", "Device: " + device);

        // Hide the status bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        // Hide the navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Video
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.bars);
        videoView.setVideoURI(videoUri);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start(); // Restart video
            }
        });

    }
//Debug
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("USB", "New intent received: " + intent);
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Log.d("USB", "Device from new intent: " + (device == null ? "null" : device.getDeviceName()));
        if (device != null) {
            requestPermission(device);
        }
    }


    //    Buttons and sendData
    private void clicklistners() {
        whiscoke.setOnClickListener(v -> {
            sendData("1");
        });
        whisga.setOnClickListener(v -> {
            sendData("8");
        });
        whislem.setOnClickListener(v -> {
            sendData("9");
        });
        marg.setOnClickListener(v -> {
            sendData("2");
        });
        teqoj.setOnClickListener(v -> {
            sendData("a");
        });
        teqspri.setOnClickListener(v -> {
            sendData("b");
        });
        teqlem.setOnClickListener(v -> {
            sendData("c");
        });
        teqsw.setOnClickListener(v -> {
            sendData("d");
        });
        vodcran.setOnClickListener(v -> {
            sendData("3");
        });
        screw.setOnClickListener(v -> {
            sendData("4");
        });
        vodsw.setOnClickListener(v -> {
            sendData("5");
        });
        vodspri.setOnClickListener(v -> {
            sendData("6");
        });
        vodlem.setOnClickListener(v -> {
            sendData("7");
        });
        rumcoke.setOnClickListener(v -> {
            sendData("e");
        });
        rumlem.setOnClickListener(v -> {
            sendData("f");
        });
        rumga.setOnClickListener(v -> {
            sendData("g");
        });
//////////////////splash
        splcran.setOnClickListener(v -> {
            sendData("m");
        });
        splga.setOnClickListener(v -> {
            sendData("n");
        });
        sploj.setOnClickListener(v -> {
            sendData("o");
        });
        splsw.setOnClickListener(v -> {
            sendData("p");
        });
        splspri.setOnClickListener(v -> {
            sendData("q");
        });
        spllem.setOnClickListener(v -> {
            sendData("r");
        });
        splcoke.setOnClickListener(v -> {
            sendData("l");
        });

        ///////////////shots
        shotwhis.setOnClickListener(v -> {
            sendData("h");
        });
        shotteq.setOnClickListener(v -> {
            sendData("i");
        });
        shotvod.setOnClickListener(v -> {
            sendData("j");
        });
        shotrum.setOnClickListener(v -> {
            sendData("k");
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }

//    private void requestPermission(UsbDevice device) {
//
//        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
//        usbManager.requestPermission(device, permissionIntent);
//    }

    private void requestPermission(UsbDevice device) {
        if (usbManager.hasPermission(device)) {
            Log.d("USB", "Already have permission for device: " + device.getDeviceName());
            connectToDevice(device);
        } else {
            Log.d("USB", "Requesting permission for device: " + device.getDeviceName());
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(device, permissionIntent);
        }
    }

    private void sendData(String data) {
        if (usbDevice != null) {

            if (permissionGranted) {
                if (usbSerialPort != null && usbSerialPort.isOpen()) {
                    Log.d("USB", "Connection is already open");
                    try {
                        byte[] bytes = data.getBytes();
                        usbSerialPort.write(bytes, 1000); // Send data using the usb-serial-for-android library

                    } catch (IOException e) {
                        // Handle error during data sending
                        Log.e("USB", "Error sending data: " + e.getMessage());
                    }
                } else {
                    Log.e("USB", "Error: Port is not initialized.");
                }
            } else {
                requestPermission(usbDevice);
                Log.e("USB","Requesting permission");
            }
        } else {
            Log.e("USB", "USB Device is null (sendData)");
        }

    }

    private void connectToDevice(UsbDevice device) {
        usbDevice = device;
        if (usbSerialPort != null && usbSerialPort.isOpen()) {
            Log.d("USB", "Already connected to the device.");
            return;
        }

        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver != null) {
            Log.d("USB", "Driver found, trying to connect to the device: " + device.getDeviceName());
            UsbSerialPort port = driver.getPorts().get(0);
            UsbDeviceConnection connection = usbManager.openDevice(device);

            if (connection != null) {
                try {
                    usbSerialPort = port;
                    usbSerialPort.open(connection);
                    usbSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                    permissionGranted = true;
                    Log.d("USB", "Connected to device: " + device.getDeviceName());
                } catch (IOException e) {
                    Log.e("USB", "Error connecting to device: " + e.getMessage());
                }
            } else {
                Log.e("USB", "Connection is null");
            }
        } else {
            Log.e("USB", "No driver found for the device: " + device.getDeviceName());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (usbManager == null) {
            Log.e("USB", "USB manager is null");
            return; // Or handle this case as per your needs
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);

        registerReceiver(usbReceiver, filter);

        if (usbSerialPort != null && !usbSerialPort.isOpen()) {
            try {
                usbSerialPort.open(usbConnection);
            } catch (IOException e) {
                Log.e("USB", "Error reopening the connection: " + e.getMessage());
                // Consider informing the user
            }
        }

        if (usbDevice != null) {
            if (!usbManager.hasPermission(usbDevice)) {
                Log.d("USB", "Permission lost, requesting again");
                PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(usbDevice, permissionIntent);
            } else {
                Log.d("USB", "Permission retained");
            }
        } else {
            Log.d("USB", "USB device is null (onResume)");
        }

        Map<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        if (!usbDevices.isEmpty()) {
            Log.e("USB","USB Device Found.");
            for (UsbDevice device : usbDevices.values()) {
                Log.d("USB", "onResume - Device: " + device.toString());
                // Handle the device as per your requirements
            }
        } else {
            Log.d("USB", "onResume - No USB devices found");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(usbReceiver);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("USB", "Received action: " + action);

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d("USB", "Device attached: " + device.getDeviceName());
                // Check if connection gets established here

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d("USB", "Device detached: " + device.getDeviceName());
                // Check if this event is triggered unexpectedly

            } else if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Log.d("USB", "Permission granted for device " + device.getDeviceName());
                    } else {
                        Log.e("USB", "Permission denied for device " + device.getDeviceName());
                    }
                }
            }
        }
    };

    private void initializeShotsButtons() {
        shotwhis = findViewById(R.id.shotwhis);
        shotteq = findViewById(R.id.shotteq);
        shotvod = findViewById(R.id.shotvod);
        shotrum = findViewById(R.id.shotrum);
    }

    private void initializeSplashesButtons() {
        splcran = findViewById(R.id.splcran);
        splga = findViewById(R.id.splga);
        sploj = findViewById(R.id.sploj);
        splsw = findViewById(R.id.splsw);
        splspri = findViewById(R.id.splspri);
        spllem = findViewById(R.id.spllem);
        splcoke = findViewById(R.id.splcoke);
    }

    private void initializeButtons() {
        whiscoke = findViewById(R.id.whiscoke);
        whisga = findViewById(R.id.whisga);
        whislem = findViewById(R.id.whislem);
        marg = findViewById(R.id.marg);
        teqoj = findViewById(R.id.teqoj);
        teqspri = findViewById(R.id.teqspri);
        teqlem = findViewById(R.id.teqlem);
        teqsw = findViewById(R.id.teqsw);
        vodcran = findViewById(R.id.vodcran);
        screw = findViewById(R.id.screw);
        vodsw = findViewById(R.id.vodsw);
        vodspri = findViewById(R.id.vodspri);
        vodlem = findViewById(R.id.vodlem);
        rumcoke = findViewById(R.id.rumcoke);
        rumlem = findViewById(R.id.rumlem);
        rumga = findViewById(R.id.rumga);

    }
}