package com.example.btcontroll;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.sdk.reader.ReaderSdk;
import com.squareup.sdk.reader.authorization.AuthorizationManager;
import com.squareup.sdk.reader.authorization.AuthorizationState;
import com.squareup.sdk.reader.authorization.DeauthorizeErrorCode;
import com.squareup.sdk.reader.authorization.Location;
import com.squareup.sdk.reader.checkout.AdditionalPaymentType;
import com.squareup.sdk.reader.checkout.CheckoutErrorCode;
import com.squareup.sdk.reader.checkout.CheckoutManager;
import com.squareup.sdk.reader.checkout.CheckoutParameters;
import com.squareup.sdk.reader.checkout.CheckoutResult;
import com.squareup.sdk.reader.checkout.CurrencyCode;
import com.squareup.sdk.reader.checkout.Money;
import com.squareup.sdk.reader.core.CallbackReference;
import com.squareup.sdk.reader.core.Result;
import com.squareup.sdk.reader.core.ResultError;
import com.squareup.sdk.reader.hardware.ReaderManager;
import com.squareup.sdk.reader.hardware.ReaderSettingsErrorCode;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = CheckoutActivity.class.getSimpleName();
    private CallbackReference deauthorizeCallbackRef;
    private CallbackReference checkoutCallbackRef;
    private CallbackReference readerSettingsCallbackRef;
    private boolean waitingForActivityStart = false;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkout_activity);

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


        View settingsButton = findViewById(R.id.settings_button);
        settingsButton.setVisibility(View.VISIBLE);
        settingsButton.setBackgroundColor(Color.TRANSPARENT);
        settingsButton.setOnLongClickListener(view -> {
            promptForPassword();
            return true;
        });

        View back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> finish());

//        TODO: FIX SETTINGS VISIBILITY OR CREATE TEST CHECKOUT CLASS
//        Remove line below to show settings button
//        settingsButton.setVisibility(View.GONE);

        CheckoutManager checkoutManager = ReaderSdk.checkoutManager();
        checkoutCallbackRef = checkoutManager.addCheckoutActivityCallback(this::onCheckoutResult);

        ReaderManager readerManager = ReaderSdk.readerManager();
        readerSettingsCallbackRef =
                readerManager.addReaderSettingsActivityCallback(this::onReaderSettingsResult);

        AuthorizationManager authorizationManager = ReaderSdk.authorizationManager();
        deauthorizeCallbackRef = authorizationManager.addDeauthorizeCallback(this::onDeauthorizeResult);

        Intent intent = getIntent();
        int price = intent.getIntExtra("PRICE", 100);
        String drinkName = intent.getStringExtra("DRINK_NAME");

        if (!authorizationManager.getAuthorizationState().isAuthorized()) {
            goToAuthorizeActivity();
        } else {
//            100 is smallest amount of money in current denomination of current
            Money checkoutAmount = new Money(price, CurrencyCode.current());

            TextView startCheckoutButton = findViewById(R.id.start_checkout_button);
            startCheckoutButton.setOnClickListener(view -> startCheckout(checkoutAmount));
            startCheckoutButton.setText(getString(R.string.start_checkout, checkoutAmount.format()));
        }

    }

    private void promptForPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String password = input.getText().toString();
            if (isPasswordCorrect(password)) {
                showSettingsBottomSheet();
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private boolean isPasswordCorrect(String password) {
//        Change password here
        return "2023".equals(password);
    }

    private void showSettingsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.settings_sheet, null);

        AuthorizationManager authorizationManager = ReaderSdk.authorizationManager();
        AuthorizationState authorizationState = authorizationManager.getAuthorizationState();
        Location location = authorizationState.getAuthorizedLocation();
        String locationText = getString(R.string.location_view_format, location.getName());
        TextView locationView = sheetView.findViewById(R.id.location_view);
        locationView.setText(locationText);

        sheetView.findViewById(R.id.reader_settings_button)
                .setOnClickListener(v -> {
                    dialog.dismiss();
                    startReaderSettings();
        });

        sheetView.findViewById(R.id.deauthorize_button)
                .setOnClickListener(v -> {
            dialog.dismiss();
            deauthorize();
        });

        dialog.setContentView(sheetView);

        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) sheetView.getParent());
        dialog.setOnShowListener(dialogInterface -> behavior.setPeekHeight(sheetView.getHeight()));

        dialog.show();
    }

    private void goToAuthorizeActivity() {
        if (waitingForActivityStart) {
            return;
        }
        waitingForActivityStart = true;
        Intent intent = new Intent(this, StartAuthorizeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startCheckout(Money checkoutAmount) {
        if (waitingForActivityStart) {
            return;
        }
        waitingForActivityStart = true;
        CheckoutManager checkoutManager = ReaderSdk.checkoutManager();
        CheckoutParameters.Builder params = CheckoutParameters.newBuilder(checkoutAmount);
//        params.additionalPaymentTypes(AdditionalPaymentType.CASH);
        params.note("Thank you!");
        checkoutManager.startCheckoutActivity(this, params.build());
    }

    private void onCheckoutResult(Result<CheckoutResult, ResultError<CheckoutErrorCode>> result) {
        Intent data = new Intent();
        // Add this before you set the result to make sure it's included in the Intent you're sending back.
        data.putExtra("DATA_TO_SEND", getIntent().getStringExtra("DATA_TO_SEND"));

        if (result.isSuccess()) {
            CheckoutResult checkoutResult = result.getSuccessValue();
            String totalAmount = checkoutResult.getTotalMoney().format();
            showDialog(getString(R.string.checkout_success_dialog_title, totalAmount),
                    getString(R.string.checkout_success_dialog_message));
            Log.d(TAG, "\n" + checkoutResult.toString() + "\n");

            data.putExtra("EXTRA_SUCCESSFUL_CHECKOUT", true);
            setResult(RESULT_OK, data);
            finish();
        } else {
            ResultError<CheckoutErrorCode> error = result.getError();

            data.putExtra("EXTRA_SUCCESSFUL_CHECKOUT", false);
            setResult(RESULT_CANCELED, data);
            finish();

            switch (error.getCode()) {
                case SDK_NOT_AUTHORIZED:
                    goToAuthorizeActivity();
                    break;
                case CANCELED:
                    Toast.makeText(this, R.string.checkout_canceled_toast, Toast.LENGTH_SHORT).show();
                    break;
                case USAGE_ERROR:
                    showErrorDialog(error);
                    break;
            }
        }
    }


    private void startReaderSettings() {
        if (waitingForActivityStart) {
            return;
        }
        waitingForActivityStart = true;
        ReaderManager readerManager = ReaderSdk.readerManager();
        readerManager.startReaderSettingsActivity(this);
    }

    private void onReaderSettingsResult(Result<Void, ResultError<ReaderSettingsErrorCode>> result) {
        if (result.isError()) {
            ResultError<ReaderSettingsErrorCode> error = result.getError();
            switch (error.getCode()) {
                case SDK_NOT_AUTHORIZED:
                    goToAuthorizeActivity();
                    break;
                case USAGE_ERROR:
                    showErrorDialog(error);
                    break;
            }
        }
    }

    private void deauthorize() {
        AuthorizationManager authorizationManager = ReaderSdk.authorizationManager();
        if (authorizationManager.getAuthorizationState().canDeauthorize()) {
            authorizationManager.deauthorize();
        } else {
            showDialog(getString(R.string.cannot_deauthorize_dialog_title),
                    getString(R.string.cannot_deauthorize_dialog_message));
        }
    }

    private void onDeauthorizeResult(Result<Void, ResultError<DeauthorizeErrorCode>> result) {
        if (result.isSuccess()) {
            goToAuthorizeActivity();
        } else {
            showErrorDialog(result.getError());
        }
    }

    private void showErrorDialog(ResultError<?> error) {
        String dialogMessage = error.getMessage();
        if (BuildConfig.DEBUG) {
            dialogMessage += "\n\nDebug Message: " + error.getDebugMessage();
            Log.d("Checkout", error.getDebugCode() + ", " + error.getDebugMessage());
        }
        showDialog(getString(R.string.error_dialog_title), dialogMessage);
    }

    private void showDialog(CharSequence title, CharSequence message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, null)
                .show();
    }

    @Override protected void onResume() {
        super.onResume();
        waitingForActivityStart = false;
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        readerSettingsCallbackRef.clear();
        checkoutCallbackRef.clear();
        deauthorizeCallbackRef.clear();
    }
}
