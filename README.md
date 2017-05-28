# Arduino [ ![Download](https://api.bintray.com/packages/omaflak/maven/arduino/images/download.svg) ](https://bintray.com/omaflak/maven/arduino/_latestVersion)
A short Android library to communicate with Arduino through usb.

# Gradle

build.gradle Project

    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
    }

build.gradle Module

    compile 'me.aflak.libraries:arduino:X.X'

# Usage

    Arduino arduino = new Arduino(Context);

    @Override
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(new ArduinoListener() {
	        @Override
	        public void onArduinoAttached(UsbDevice device) {
	            arduino.open(device);
	        }

	        @Override
	        public void onArduinoDetached() {
	            // arduino detached from phone
	        }

	        @Override
	        public void onArduinoMessage(byte[] bytes) {
	            String message = new String(bytes);
	            // new message received from arduino
	        }

	        @Override
	        public void onArduinoOpened() {
	            // you can start the communication
	            String str = "Hello Arduino !";
	            arduino.send(str.getBytes());
	        }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

# Arduino Side

Example code which sends back every character received

    void setup() {
        Serial.begin(9600);
    }

    void loop() {
        if(Serial.available()){
            char c = Serial.read();
            Serial.print(c);
        }
    }

# Sample Code

See **[MainActivity.java](https://github.com/omaflak/Arduino/blob/master/app/src/main/java/me/aflak/libraries/MainActivity.java)**

# Special thanks

This code is using UsbSerial library : [https://github.com/felHR85/UsbSerial](https://github.com/felHR85/UsbSerial)
