package com.gabra.deliverybot

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gabra.deliverybot.databinding.ActivityMainBinding
import com.gabra.deliverybot.databinding.ViewFloatingControlPanelBinding
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var floatingPanelBinding: ViewFloatingControlPanelBinding
    private lateinit var viewModel: DeliveryBotViewModel
    private var isServiceEnabled = false
    private var isTestingMode = false
    private val serviceIntent: Intent by lazy {
        Intent(this, DeliveryBotService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DeliveryBotViewModel::class.java]

        // Initialize the floating control panel
        initializeFloatingPanel()
        
        // Check and request permissions
        checkPermissions()

        // Observe ViewModel state changes
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.serviceState.collectLatest { enabled ->
                isServiceEnabled = enabled
                updateServiceState()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.testingMode.collectLatest { testing ->
                isTestingMode = testing
                updateServiceState()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.log.collectLatest { log ->
                floatingPanelBinding.tvLog.text = log
            }
        }
    }

    private fun initializeFloatingPanel() {
        floatingPanelBinding = ViewFloatingControlPanelBinding.inflate(layoutInflater)
        binding.controlPanel.addView(floatingPanelBinding.root)

        // Initialize UI elements
        floatingPanelBinding.apply {
            btnToggleService.setOnClickListener { toggleService() }
            btnTestMode.setOnClickListener { toggleTestMode() }
            tvStatus.text = getString(R.string.status_stopped)
        }

        // Update UI based on service state
        updateServiceState()
    }

    private fun toggleService() {
        if (!isServiceEnabled) {
            if (checkAccessibilityPermission()) {
                startService(serviceIntent)
                viewModel.setServiceState(true)
                Toast.makeText(this, R.string.service_started, Toast.LENGTH_SHORT).show()
            }
        } else {
            stopService(serviceIntent)
            viewModel.setServiceState(false)
            Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleTestMode() {
        isTestingMode = !isTestingMode
        viewModel.setTestingMode(isTestingMode)
        val service = getSystemService(Context.ACCESSIBILITY_SERVICE) as? DeliveryBotService
        service?.setTestingMode(isTestingMode)
        updateServiceState()
    }

    private fun updateServiceState() {
        floatingPanelBinding.apply {
            tvStatus.text = when {
                !isServiceEnabled -> getString(R.string.status_stopped)
                isTestingMode -> getString(R.string.status_paused)
                else -> getString(R.string.status_running)
            }
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        val permissionRequests = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionRequests.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionRequests.toTypedArray(), 1)
        }
    }

    private fun checkAccessibilityPermission(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            android.provider.Settings.Secure.ACCESSIBILITY_ENABLED,
            0
        ) > 0

        if (!accessibilityEnabled) {
            showAccessibilityPermissionDialog()
            return false
        }
        return true
    }

    private fun showAccessibilityPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_title)
            .setMessage(R.string.permission_message)
            .setPositiveButton(R.string.permission_enable) { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(R.string.permission_cancel, null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "تم منح الصلاحيات", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "يجب منح الصلاحيات", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
