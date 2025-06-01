package com.gabra.deliverybot;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.gabra.deliverybot.viewmodel.DeliveryBotViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DeliveryBotViewModel viewModel;
    private Button toggleServiceBtn;
    private Button testModeBtn;
    private TextView statusText;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(DeliveryBotViewModel.class);

        toggleServiceBtn = findViewById(R.id.toggleServiceBtn);
        testModeBtn = findViewById(R.id.testModeBtn);
        statusText = findViewById(R.id.statusText);
        logView = findViewById(R.id.logView);

        toggleServiceBtn.setOnClickListener(v -> toggleService());
        testModeBtn.setOnClickListener(v -> toggleTestMode());

        observeViewModel();

        // Check permissions
        checkPermissions();
    }

    private void observeViewModel() {
        viewModel.getServiceStatus().observe(this, status -> {
            updateUI(status);
        });

        viewModel.getLogMessages().observe(this, logs -> {
            updateLogs(logs);
        });
    }

    private void updateUI(boolean isServiceEnabled) {
        String status = isServiceEnabled ? getString(R.string.status_running) : getString(R.string.status_stopped);
        statusText.setText(status);
        toggleServiceBtn.setText(isServiceEnabled ? getString(R.string.toggle_service) : getString(R.string.toggle_service));
    }

    private void updateLogs(List<String> logs) {
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        logView.setText(sb.toString());
    }

    private void toggleService() {
        if (viewModel.isServiceEnabled()) {
            viewModel.stopService();
        } else {
            if (isAccessibilityServiceEnabled()) {
                viewModel.startService();
            } else {
                showAccessibilityServiceDialog();
            }
        }
    }

    private void toggleTestMode() {
        viewModel.toggleTestMode();
    }

    private boolean isAccessibilityServiceEnabled() {
        AccessibilityServiceInfo info = viewModel.getAccessibilityServiceInfo();
        if (info == null) {
            return false;
        }
        return info.isEnabled();
    }

    private void showAccessibilityServiceDialog() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, R.string.permission_message, Toast.LENGTH_LONG).show();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SYSTEM_ALERT_WINDOW},
                    1);
        }
    }
}
