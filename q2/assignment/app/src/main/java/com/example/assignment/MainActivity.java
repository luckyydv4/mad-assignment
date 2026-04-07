package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etAmountFrom, etAmountTo;
    private Spinner spinnerFrom, spinnerTo;
    private Button btnSettings;

    private boolean isUpdating = false;

    // Hardcoded conversion rates relative to USD (approximate rates)
    private static final Map<String, Double> rates = new HashMap<>();
    static {
        rates.put("USD", 1.0);
        rates.put("INR", 83.0);
        rates.put("EUR", 0.92);
        rates.put("JPY", 150.0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyInitialTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAmountFrom = findViewById(R.id.et_amount_from);
        etAmountTo = findViewById(R.id.et_amount_to);
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo = findViewById(R.id.spinner_to);
        btnSettings = findViewById(R.id.btn_settings);

        setupListeners();

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void setupListeners() {
        etAmountFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating && etAmountFrom.hasFocus()) {
                    convert(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etAmountTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating && etAmountTo.hasFocus()) {
                    convert(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerFrom.setOnItemSelectedListener(spinnerListener);
        spinnerTo.setOnItemSelectedListener(spinnerListener);
    }

    private void convert(boolean fromToTo) {
        isUpdating = true;
        
        EditText sourceEt = fromToTo ? etAmountFrom : etAmountTo;
        EditText targetEt = fromToTo ? etAmountTo : etAmountFrom;
        String fromCurrency = fromToTo ? spinnerFrom.getSelectedItem().toString() : spinnerTo.getSelectedItem().toString();
        String toCurrency = fromToTo ? spinnerTo.getSelectedItem().toString() : spinnerFrom.getSelectedItem().toString();

        String amountStr = sourceEt.getText().toString();
        if (amountStr.isEmpty()) {
            targetEt.setText("");
            isUpdating = false;
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            double fromRate = rates.get(fromCurrency);
            double toRate = rates.get(toCurrency);

            double result = (amount / fromRate) * toRate;
            targetEt.setText(String.format(Locale.getDefault(), "%.2f", result));
        } catch (NumberFormatException e) {
            targetEt.setText("");
        }

        isUpdating = false;
    }

    private void applyInitialTheme() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        boolean currentlyDark = (currentMode == AppCompatDelegate.MODE_NIGHT_YES);
        
        if (isDarkMode != currentlyDark) {
            AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        }
    }
}