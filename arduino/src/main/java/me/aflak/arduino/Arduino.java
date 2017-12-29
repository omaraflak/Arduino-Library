package me.aflak.arduino;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;

/**
 * Created by Omar on 21/05/2017.
 */

public class Arduino implements UsbSerialInterface.UsbReadCallback{
    private Context context;
    private ArduinoListener listener;

    private UsbDeviceConnection connection;
    private UsbSerialDevice serialPort;
    private UsbReceiver usbReceiver;
    private UsbManager usbManager;
    private UsbDevice lastArduinoAttached;

    private boolean isOpened;

    private static final String ACTION_USB_DEVICE_PERMISSION = "me.aflak.arduino.USB_PERMISSION";
    private static final int ARDUINO_VENDOR_ID = 9025;

    public Arduino(Context context) {
        this.context = context;
        usbReceiver = new UsbReceiver();
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        isOpened = false;
    }

    public void setArduinoListener(ArduinoListener listener){
        this.listener = listener;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(ACTION_USB_DEVICE_PERMISSION);
        context.registerReceiver(usbReceiver, intentFilter);

        lastArduinoAttached = getAttachedArduino();
        if(lastArduinoAttached!=null && listener!=null){
            listener.onArduinoAttached(lastArduinoAttached);
        }
    }

    public void unsetArduinoListener(){
        this.listener = null;
    }

    public void open(UsbDevice device){
        PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_DEVICE_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, filter);
        usbManager.requestPermission(device, permissionIntent);
    }

    public void reopen(){
        open(lastArduinoAttached);
    }

    public void close(){
        if(serialPort!=null){
            serialPort.close();
        }
        if(connection!=null){
            connection.close();
        }

        isOpened = false;
        context.unregisterReceiver(usbReceiver);
    }

    public void send(byte[] bytes){
        if(serialPort!=null){
            serialPort.write(bytes);
        }
    }

    private class UsbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice device;
            if(intent.getAction()!=null) {
                switch (intent.getAction()) {
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (device.getVendorId() == ARDUINO_VENDOR_ID) {
                            lastArduinoAttached = device;
                            if(listener != null){
                                listener.onArduinoAttached(device);
                            }
                        }
                        break;
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (device.getVendorId() == ARDUINO_VENDOR_ID) {
                            if(listener != null){
                                listener.onArduinoDetached();
                            }
                        }
                        break;
                    case ACTION_USB_DEVICE_PERMISSION:
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            if (device.getVendorId() == ARDUINO_VENDOR_ID) {
                                connection = usbManager.openDevice(device);
                                serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                                if (serialPort != null) {
                                    if (serialPort.open()) {
                                        serialPort.setBaudRate(9600);
                                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                        serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                        serialPort.read(Arduino.this);

                                        isOpened = true;

                                        if(listener != null){
                                            listener.onArduinoOpened();
                                        }
                                    }
                                }
                            }
                        }
                        else if(listener!=null){
                            listener.onUsbPermissionDenied();
                        }
                        break;
                }
            }
        }
    }

    private UsbDevice getAttachedArduino(){
        HashMap<String, UsbDevice> map = usbManager.getDeviceList();
        for (UsbDevice device : map.values()){
            if (device.getVendorId()==ARDUINO_VENDOR_ID){
                return device;
            }
        }
        return null;
    }

    @Override
    public void onReceivedData(byte[] bytes) {
        if(listener != null && bytes.length!=0){
            listener.onArduinoMessage(bytes);
        }
    }

    public boolean isOpened() {
        return isOpened;
    }
}
