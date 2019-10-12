# Arduino [ ![Download](https://api.bintray.com/packages/omaflak/maven/arduino/images/download.svg) ](https://bintray.com/omaflak/maven/arduino/_latestVersion)
A short Android library to communicate with Arduino through usb.

# Gradle

build.gradle Project

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

build.gradle Module

```gradle
implementation 'me.aflak.libraries:arduino:1.4.4'
```

# Usage

```java
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
        
        @Override
        public void onUsbPermissionDenied() {
            // Permission denied, display popup then
            arduino.reopen();
        }
    });
}
```

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    arduino.unsetArduinoListener();
    arduino.close();
}
```

# Custom vendor id

The library currently filters the vendor id 9025, but you can add your own filter by calling :

```java
Arduino arduino = new Arduino(Context);
arduino.addVendorId(1234);
```

# Arduino Side

Example code which sends back every character received

```C
void setup() {
    Serial.begin(9600);
}

void loop() {
    if(Serial.available()){
        char c = Serial.read();
        Serial.print(c);
    }
}
```

# Sample Code

See **[MainActivity.java](https://github.com/omaflak/Arduino/blob/master/app/src/main/java/me/aflak/libraries/MainActivity.java)**

# Special thanks

This code is using UsbSerial library : [https://github.com/felHR85/UsbSerial](https://github.com/felHR85/UsbSerial)

# License

    MIT License
    
    Copyright (c) 2017 Michel Omar Aflak
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.