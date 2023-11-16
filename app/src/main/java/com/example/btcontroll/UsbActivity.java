package com.example.btcontroll;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private static final int CHECKOUT_ACTIVITY_REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> checkoutActivityResultLauncher;
    private boolean waitingForActivityStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);
        initializeButtons();
        initializeSplashesButtons();
        initializeShotsButtons();
        clickListeners();

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

        startVideo();

        checkoutActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean checkoutSuccess = result.getData().getBooleanExtra("EXTRA_SUCCESSFUL_CHECKOUT", false);
                        if (checkoutSuccess) {
                            String dataToSend = result.getData().getStringExtra("DATA_TO_SEND");
                            sendData(dataToSend);
                        }
                    } else {
                        Toast.makeText(this, "Checkout failed or was canceled.", Toast.LENGTH_LONG).show();
                    }
                }
        );

    }

    private void startVideo() {
        VideoView videoView = findViewById(R.id.videoView);
        if (videoView != null) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/bars");
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    videoView.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.start(); // Restart video
                }
            });
        }
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

    private void startCheckoutActivity(int price, String drinkName, String dataToSend) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("PRICE", price);
        intent.putExtra("DRINK_NAME", drinkName);
        intent.putExtra("DATA_TO_SEND", dataToSend);
        checkoutActivityResultLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECKOUT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                boolean checkoutSuccess = data.getBooleanExtra("EXTRA_SUCCESSFUL_CHECKOUT", false);
                if (checkoutSuccess) {
                    // Only send USB data if the checkout was successful
                    String dataToSend = data.getStringExtra("DATA_TO_SEND");
                    sendData(dataToSend);
                }
            } else {
                // Handle checkout failure or cancellation
                Toast.makeText(this, "Checkout failed or was canceled.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //    Buttons
    private void clickListeners() {
        whiscoke.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("whiscoke");
            startCheckoutActivity(price, "WhisCoke", "1");
        });
        whisga.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("whisga");
            startCheckoutActivity(price, "WhisGa", "8");
        });
        whislem.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("whislem");
            startCheckoutActivity(price, "WhisLem", "9");
        });
        marg.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("marg");
            startCheckoutActivity(price, "Marg", "2");
        });
        teqoj.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("teqoj");
            startCheckoutActivity(price, "TeqOJ", "a");
        });
        teqspri.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("teqspri");
            startCheckoutActivity(price, "TeqSpri", "b");
        });
        teqlem.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("teqlem");
            startCheckoutActivity(price, "TeqLem", "c");
        });
        teqsw.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("teqsw");
            startCheckoutActivity(price, "TeqSw", "d");
        });
        vodcran.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("vodcran");
            startCheckoutActivity(price, "VodCran", "3");
        });
        screw.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("screw");
            startCheckoutActivity(price, "Screw", "4");
        });
        vodsw.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("vodsw");
            startCheckoutActivity(price, "VodSw", "5");
        });
        vodspri.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("vodspri");
            startCheckoutActivity(price, "VodSpri", "6");
        });
        vodlem.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("vodlem");
            startCheckoutActivity(price, "VodLem", "7");
        });
        rumcoke.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("rumcoke");
            startCheckoutActivity(price, "RumCoke", "e");
        });
        rumlem.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("rumlem");
            startCheckoutActivity(price, "RumLem", "f");
        });
        rumga.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("rumga");
            startCheckoutActivity(price, "RumGa", "g");
        });

        // For splash
        splcran.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("splcran");
            startCheckoutActivity(price, "SplCran", "m");
        });
        splga.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("splga");
            startCheckoutActivity(price, "SplGa", "n");
        });
        sploj.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("sploj");
            startCheckoutActivity(price, "Sploj", "o");
        });
        splsw.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("splsw");
            startCheckoutActivity(price, "SplSw", "p");
        });
        splspri.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("splspri");
            startCheckoutActivity(price, "SplSpri", "q");
        });
        spllem.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("spllem");
            startCheckoutActivity(price, "SplLem", "r");
        });
        splcoke.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("splcoke");
            startCheckoutActivity(price, "SplCoke", "l");
        });

        // For shots
        shotwhis.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("shotwhis");
            startCheckoutActivity(price, "ShotWhis", "h");
        });
        shotteq.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("shotteq");
            startCheckoutActivity(price, "ShotTeq", "i");
        });
        shotvod.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("shotvod");
            startCheckoutActivity(price, "ShotVod", "j");
        });
        shotrum.setOnClickListener(v -> {
            int price = DrinkPrices.getPrice("shotrum");
            startCheckoutActivity(price, "ShotRum", "k");
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }

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
        startVideo();
        waitingForActivityStart = false;


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
        VideoView videoView = findViewById(R.id.videoView);
        if (videoView != null && videoView.isPlaying()) {
            videoView.stopPlayback();
        }
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
//    Initialize Buttons
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