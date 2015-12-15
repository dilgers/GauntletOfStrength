// Simple LED blink
int out1 = 16;
int out2 = 15;
int out3 = 14;

int in1 = 10;
int in2 = 11;
int in3 = 12;

const int led = LED_BUILTIN;

void setup() {
  pinMode(led, OUTPUT);
  digitalWrite(led, LOW);
  
  pinMode(out1, INPUT);
  digitalWrite(out1, LOW);  // let it float
  pinMode(out2, INPUT);
  digitalWrite(out2, LOW);  // let it float
  pinMode(out3, INPUT);
  digitalWrite(out3, LOW);  // let it float
  
  pinMode(in1, INPUT);
  pinMode(in2, INPUT);
  pinMode(in3, INPUT);
}

void loop() {
 if (!digitalRead(in1)) {
    pinMode(out1, OUTPUT);
    digitalWrite(out1, LOW);
 } else {
    pinMode(out1, INPUT);
    digitalWrite(out1, LOW);       // turn off pullup resistor
 }
 
 if (!digitalRead(in2)) {
    pinMode(out2, OUTPUT);
    digitalWrite(out2, LOW);
 } else {
    pinMode(out2, INPUT);
    digitalWrite(out2, LOW);       // turn off pullup resistor
 }
 
 if (!digitalRead(in3)) {
    pinMode(out3, OUTPUT);
    digitalWrite(out3, LOW);
 } else {
    pinMode(out3, INPUT);
    digitalWrite(out3, LOW);       // turn off pullup resistor
 }
  
  if (digitalRead(in1) && digitalRead(in2) && digitalRead(in3)) {
    digitalWrite(led, HIGH);
    //delay(1000);
    digitalWrite(led, LOW);
    //delay(1000);
  } else {
  digitalWrite(led, LOW);
  }
}

