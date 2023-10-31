package com.example.btcontroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.view.View;
import android.view.WindowManager;


import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Controlling extends Activity {
    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;

    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;


    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;



    private ProgressDialog progressDialog;

    Button whiscoke, whisga, whislem, marg, teqoj, teqspri, teqlem, teqsw, vodcran, screw, vodsw, vodspri, vodlem, rumcoke, rumlem, rumga;
    Button splcran, splga, sploj, splsw, splspri, spllem, splcoke;

    Button shotwhis, shotteq, shotvod, shotrum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);

        // Hide the status bar (notification bar) - Optional
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        // Hide the navigation bar (soft keys)
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

        ActivityHelper.initialize(this);
        // mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        initializeButtons();
        initializeSplashesButtons();
        initializeShotsButtons();


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        Log.d(TAG, "Ready");

        clicklistners();

    }

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

    private void initializeShotsButtons() {
        shotwhis =findViewById(R.id.shotwhis);
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

    private void sendData(String s) {
        try {
            mBTSocket.getOutputStream().write(s.toString().getBytes());
            Toast.makeText(Controlling.this, "sent", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */


                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    if (ActivityCompat.checkSelfPermission(Controlling.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
