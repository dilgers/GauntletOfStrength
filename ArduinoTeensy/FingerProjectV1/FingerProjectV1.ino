/* 
 *  =============================================
 *  I2Cdev device library code is placed under the MIT license
 *  Copyright (c) 2012 Jeff Rowberg
 *  =============================================
 *  =============================================
 *  University of Freiburg
 *  Copyright (c) 2016 Stefan Dilger
 *                     Stefan Koeck
 *  =============================================
*/

// Arduino Wire library is required if I2Cdev I2CDEV_ARDUINO_WIRE implementation
// is used in I2Cdev.h
#include <i2c_t3.h>

// I2Cdev and MPU6050 must be installed as libraries, or else the .cpp/.h files
// for both classes must be in the include path of your project
#include "I2Cdev.h"

#include <SoftwareSerial.h>   //Software Serial Port
#include <string.h>
#define RxD 0
#define TxD 1

#define MultiMediaPin 2
#define LauterPin 3
#define LeiserPin 4

#define LED_PIN 13


#include "MPU6050_9Axis_MotionApps41.h"

MPU6050 mpu;

bool blinkState = false;

// MPU control/status vars
bool dmpReady = false;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;    // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;  // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;   // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer

// orientation/motion vars
// uses the quaternion components in a [w, x, y, z] format
Quaternion q;       // [w, x, y, z]     quaternion container

SoftwareSerial blueToothSerial(RxD, TxD);

// Delay time for reading finger pins
int timout = 5;

// Pins for finger and array lenght
int inPin[] = {23, 22, 21, 20, 14, 15};
int lenght = 6;

// Pins for vibrating motors
int vipPin[] = {11, 12};

// Global variable for establishing first bluetooth comunication
int comun = 0;

String str = "";


// ================================================================
// ===              INTERRUPT DETECTION ROUTINE                 ===
// ================================================================

volatile bool mpuInterrupt = false;  // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
  mpuInterrupt = true;
}


// ================================================================
// ===              INITIAL SETUP                               ===
// ================================================================

void setup()
{
  // ================================================================
  // ===              INITIAL SETUP FOR MPU9050                   ===
  // ================================================================

  Wire.begin();

  delay(timout);

  // initialize device
  mpu.initialize();

  // load and configure the DMP
  devStatus = mpu.dmpInitialize();

  // make sure it worked (returns 0 if so)
  if (devStatus == 0) {
    // turn on the DMP, now that it's ready
    mpu.setDMPEnabled(true);

    // enable Arduino interrupt detection
    attachInterrupt(0, dmpDataReady, RISING);
    mpuIntStatus = mpu.getIntStatus();

    // set our DMP Ready flag so the main loop() function knows it's okay to use it
    dmpReady = true;

    // get expected DMP packet size for later comparison
    packetSize = mpu.dmpGetFIFOPacketSize();
  }

  // ================================================================
  // ===              INITIAL SETUP FOR FINGER PINS               ===
  // ================================================================

  // configure LED for output
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, HIGH);

  // Set vibration motors as off
  pinMode(vipPin[0], OUTPUT);
  pinMode(vipPin[1], OUTPUT);
  digitalWrite(vipPin[0], HIGH);
  digitalWrite(vipPin[1], HIGH);


  pinMode(MultiMediaPin, OUTPUT);
  pinMode(LauterPin, OUTPUT);
  pinMode(LeiserPin, OUTPUT);

  // Set all pins with finger conection to INPUT mode
  for (int i = 0; i < lenght; ++i) {
    pinMode(inPin[i], INPUT);
  }


  // ================================================================
  // ===              INITIAL SETUP FOR BLUETOOTH                 ===
  // ================================================================

  // Set blutooth pin of comunication and initialize module
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  setupBlueToothConnection();
}

void loop()
{
  char recvChar;

  while (blueToothSerial.available() && comun == 0) {
    //check if there's any data sent from the remote bluetooth shield
    recvChar = blueToothSerial.read();

    // Syncroniese devises, by receiving a Code (5)
    if (recvChar == '5') {
      comun = 1;
    }
  }

  if (comun) {
    while (blueToothSerial.available()) {//check if there's any data sent from the remote bluetooth shield
      recvChar = blueToothSerial.read();
      str += recvChar;

      // Syncroniese devises, by receiving a Code (\n)
      if (recvChar == '\n') {

        String fingerPattern = getFingerPattern();

        // ================================================================
        // ===     Get Quaternion from MPU9050                          ===
        // ================================================================

        // if programming failed, don't try to do anything
        if (!dmpReady) return;

        // reset interrupt flag and get INT_STATUS byte
        mpuInterrupt = false;
        mpuIntStatus = mpu.getIntStatus() & 0x12;

        while (!mpuIntStatus) {
          mpuIntStatus = mpu.getIntStatus() & 0x12;
        }

        // get current FIFO count
        fifoCount = mpu.getFIFOCount();

        // check for overflow (this should never happen unless our code is too inefficient)
        if ((mpuIntStatus & 0x10) || fifoCount == 1024) {
          // reset so we can continue cleanly
          mpu.resetFIFO();
          fingerPattern += "F";
          delay(timout);  //  otherwise it starts reading in wrong place

          // otherwise, check for DMP data ready interrupt (this should happen frequently)
        } else if (mpuIntStatus & 0x02) {
          // wait for correct available data length, should be a VERY short wait
          while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

          // read a packet from FIFO
          mpu.getFIFOBytes(fifoBuffer, packetSize);

          // track FIFO count here in case there is > 1 packet available
          // (this lets us immediately read more without waiting for an interrupt)
          fifoCount -= packetSize;

          // display quaternion values in easy matrix form: w x y z
          mpu.dmpGetQuaternion(&q, fifoBuffer);

          fingerPattern += q.w;
          fingerPattern += ",";
          fingerPattern += q.x;
          fingerPattern += ",";
          fingerPattern += q.y;
          fingerPattern += ",";
          fingerPattern += q.z;

          // blink LED to indicate activity
          blinkState = !blinkState;
          digitalWrite(LED_PIN, blinkState);
        }




        // ================================================================
        // ===     Send and Recieve Data over Bluetooth                 ===
        // ================================================================

        // Send Data if comunication is established
        blueToothSerial.println(fingerPattern);


        // ================================================================
        // ===     Interpretate recieved pattern                        ===
        // ================================================================

        char *p = new char[str.length() + 1];
        strcpy(p, str.c_str());
        char *str2;
        char recived[6];
        int countNumRecieved = 0;

        // get all chars and combine them to singel String
        while ((str2 = strtok_r(p, ";", &p)) != NULL) { // delimiter is the semicolon
          recived[countNumRecieved] = str2[0];
          ++countNumRecieved;
        }
        if (countNumRecieved != 6) {
          // Not enouch Data Recieved
        } else if ('+' != recived[0]) {
          // Mixed up Data received
        } else {

          if ('1' == recived[1]) {
            // Multimedia Button
            digitalWrite(MultiMediaPin, LOW);
          } else {
            digitalWrite(MultiMediaPin, HIGH);
          }

          if ('1' == recived[2]) {
            // Lower Volume
            digitalWrite(LeiserPin, LOW);
          } else {
            digitalWrite(LeiserPin, HIGH);
          }
          if ('1' == recived[3]) {
            // Higher Volume
            digitalWrite(LauterPin, LOW);
          } else {
            digitalWrite(LauterPin, HIGH);
          }

          if ('1' == recived[4]) {
            // Vibrate Left
            digitalWrite(vipPin[0], LOW);
          } else {
            digitalWrite(vipPin[0], HIGH);
          }
          if ('1' == recived[5]) {
            // Vibrate Right
            digitalWrite(vipPin[1], LOW);
          } else {
            digitalWrite(vipPin[1], HIGH);
          }
        }

        // 1. Mutimedia
        // 2. Leiser
        // 3. Lauter
        // 4. Vib links
        // 5. Vib rechts

        str = "";
      }
    }
  }
}


// ================================================================
// ===              CREATE FINGER PATTERN                       ===
// ================================================================
String getFingerPattern() {
  String pattern = "0";  // start each pattern with 0
  for (int i = 0; i < lenght; ++i) {
    pinMode(inPin[i], OUTPUT);
    digitalWrite(inPin[i], LOW);
    delay(timout);
    for (int j = i + 1; j < lenght; ++j) {
      digitalWrite(inPin[j], HIGH);
      if ((digitalRead(inPin[j]) == LOW)) {
        pattern += (i + 1);
        pattern += (j + 1);
        }
      }
    }
    digitalWrite(inPin[i], HIGH);
    delay(timout);
    pinMode(inPin[i], INPUT);
  }
  pattern += ";";
  return pattern;
}


// ================================================================
// ===              SET BLUETOOTH MODULE FOR COMUNICATION       ===
// ================================================================
void setupBlueToothConnection() {
  blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
  blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
  blueToothSerial.print("\r\n+STNA=SeeedBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
  blueToothSerial.print("\r\n+STPIN=0000\r\n");//Set SLAVE pincode"0000"
  blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
  blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
  delay(2000); // This delay is required.
  blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable
// Serial.println("The slave bluetooth is inquirable!");
  delay(2000); // This delay is required.
  blueToothSerial.flush();
}
