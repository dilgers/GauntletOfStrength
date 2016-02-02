#include <SoftwareSerial.h>   //Software Serial Port
#define RxD 0
#define TxD 1

SoftwareSerial blueToothSerial(RxD,TxD);

int ledPin = 13;

// Delay time for reading finger pins
int timout = 5;

// Pins for finger and array lenght
int inPin[] = {23,22,21,20,14,15};
int lenght = 6;

// Pins for vibrating motors
int vipPin[] = {11,12};

// Global variable for establishing first bluetooth comunication
int comun = 0;

void setup()
{
  // Set vibration motors and LED pin as off
  pinMode(ledPin, OUTPUT);
  pinMode(vipPin[0], OUTPUT);
  pinMode(vipPin[1], OUTPUT);
  digitalWrite(vipPin[0], HIGH);
  digitalWrite(vipPin[1], HIGH);

  // Light indicator LED
  digitalWrite(ledPin, HIGH);

  // Set all pins with finger conection to INPUT mode
  for (int i = 0; i < lenght; ++i) {
    pinMode(inPin[i], INPUT);
  }

  // Comunication Terminal (Serieller Monitor)
  Serial.begin(9600);

  // Set blutooth pin of comunication and initialize module
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  setupBlueToothConnection();
}

void loop()
{
  String fingerPattern = getFingerPattern();

  // -----------------------------------------------
  // Send and Recieve Data over Bluetooth
  // START BluetoothCom
  // -----------------------------------------------
  char recvChar;
  if(blueToothSerial.available()){//check if there's any data sent from the remote bluetooth shield
    recvChar = blueToothSerial.read();
    Serial.print("I got: ");
    Serial.println(recvChar);
    // Syncroniese devises, by receiving a Code (5)
    if(recvChar == '5')
      comun = 1;
  }
  if(Serial.available()){//check if there's any data sent from the local serial terminal, you can add the other applications here
    recvChar  = Serial.read();
    blueToothSerial.print(recvChar);
  }
  if (comun) {
    // Send Data if comunication is established
    Serial.println(fingerPattern);
    blueToothSerial.println(fingerPattern);
  }
  Serial.println(fingerPattern);
  // -----------------------------------------------
  // END BluetoothCom
  // -----------------------------------------------
}


// _______________________________________________________________________________
// Create Finger Pattern
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
        if (i == 1 && j == 3) {
          // Start vibration motors
          // digitalWrite(vipPin[0], LOW);
          // digitalWrite(vipPin[1], LOW);
        }
      }
    }
    digitalWrite(inPin[i], HIGH);
    delay(timout);
    pinMode(inPin[i], INPUT);
  }
  return pattern;
}


// _______________________________________________________________________________
// Function to set bluetooth module for transmitting and recieving data
void setupBlueToothConnection() {
  blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
  blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
  blueToothSerial.print("\r\n+STNA=SeeedBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
  blueToothSerial.print("\r\n+STPIN=0000\r\n");//Set SLAVE pincode"0000"
  blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
  blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
  delay(2000); // This delay is required.
  blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable 
  Serial.println("The slave bluetooth is inquirable!");
  delay(2000); // This delay is required.
  blueToothSerial.flush();
}
