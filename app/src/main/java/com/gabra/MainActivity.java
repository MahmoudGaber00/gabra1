package com.gabra;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gabra.service.GabraService;

public class MainActivity extends AppCompatActivity {
    private Switch switchService;
    private Button btnTest;
    private TextView logView;
    private GabraService gabraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchService = findViewById(R.id.switch_service);
        btnTest = findViewById(R.id.btn_test);
        logView = findViewById(R.id.log_view);

        switchService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableAccessibilityService();
            } else {
                disableAccessibilityService();
            }
        });

        btnTest.setOnClickListener(v -> {
            if (gabraService != null) {
                gabraService.setTestingMode(true);
            }
        });
    }

    private void enableAccessibilityService() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private void disableAccessibilityService() {
        // Implementation for disabling service
    }

    private void updateLog(String message) {
        logView.append(message + "\n");
    }
}
