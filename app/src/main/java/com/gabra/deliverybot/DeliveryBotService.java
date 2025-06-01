package com.gabra.deliverybot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeliveryBotService extends AccessibilityService {
    private static final String TAG = "DeliveryBotService";
    private boolean isServiceEnabled = false;
    private boolean isTestMode = false;
    private SharedPreferences sharedPreferences;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Service connected");
        
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        
        isServiceEnabled = true;
        Intent intent = new Intent(ACTION_UPDATE_STATUS);
        intent.putExtra("status", true);
        sendBroadcast(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isServiceEnabled) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            processWindowEvent(event);
        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            processContentEvent(event);
        }
    }

    private void processWindowEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) return;
        
        if (event.getPackageName().toString().contains("com.mistermandob")) {
            Log.d(TAG, "Mister Mandob window detected");
            processDeliveryRequest();
        }
    }

    private void processContentEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) return;
        
        if (event.getPackageName().toString().contains("com.mistermandob")) {
            Log.d(TAG, "Content changed in Mister Mandob");
            processDeliveryRequest();
        }
    }

    private void processDeliveryRequest() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        // Find distance node
        List<AccessibilityNodeInfo> distanceNodes = root.findAccessibilityNodeInfosByText("كم");
        if (distanceNodes.isEmpty()) {
            Log.d(TAG, "No distance found");
            return;
        }

        // Extract distance
        String distanceText = distanceNodes.get(0).getText().toString();
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?) كم");
        Matcher matcher = pattern.matcher(distanceText);
        if (!matcher.find()) {
            Log.d(TAG, "Invalid distance format");
            return;
        }

        double distance = Double.parseDouble(matcher.group(1));
        Log.d(TAG, "Distance: " + distance + " km");

        // Check if distance is acceptable
        if (distance <= 8.0) {
            // Find and click "تفاصيل" button
            List<AccessibilityNodeInfo> detailsButtons = root.findAccessibilityNodeInfosByText("تفاصيل");
            if (!detailsButtons.isEmpty()) {
                AccessibilityNodeInfo detailsButton = detailsButtons.get(0);
                if (detailsButton.isClickable()) {
                    detailsButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(TAG, "Clicked تفاصيل");

                    // Wait and scroll
                    try {
                        Thread.sleep(1000);
                        root.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        Log.d(TAG, "Scrolled");

                        // Find and click "قبول" button
                        List<AccessibilityNodeInfo> acceptButtons = root.findAccessibilityNodeInfosByText("قبول");
                        if (!acceptButtons.isEmpty()) {
                            AccessibilityNodeInfo acceptButton = acceptButtons.get(0);
                            if (acceptButton.isClickable()) {
                                if (!isTestMode) {
                                    acceptButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    Log.d(TAG, "Accepted order");
                                } else {
                                    Log.d(TAG, "Test mode: Would have accepted order");
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Error during processing", e);
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
        isServiceEnabled = false;
        Intent intent = new Intent(ACTION_UPDATE_STATUS);
        intent.putExtra("status", false);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        isServiceEnabled = false;
        Intent intent = new Intent(ACTION_UPDATE_STATUS);
        intent.putExtra("status", false);
        sendBroadcast(intent);
    }

    private static final String ACTION_UPDATE_STATUS = "com.gabra.deliverybot.UPDATE_STATUS";
}
