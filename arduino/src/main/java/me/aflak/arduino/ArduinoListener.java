package me.aflak.arduino;

import android.hardware.usb.UsbDevice;

/**
 * Created by Omar on 21/05/2017.
 */

public interface ArduinoListener {
    void onArduinoAttached(UsbDevice device);
    void onArduinoDetached();
    void onArduinoMessage(byte[] bytes);
    void onArduinoOpened();
}
