#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#include <ESP8266WebServer.h> //ESP Web Server Library to host a web page

const int trigPin = D2; // Trigger Pin of Ultrasonic Sensor
const int echoPin = D4; // Echo Pin of Ultrasonic Sensor
int motor = D5;
int textLED = D6;
bool PumpStatus = false;
bool automatic = false;

//Our HTML webpage contents in program memory
const char MAIN_page[] PROGMEM = R"=====(
<!DOCTYPE html>
<html>
<body>
<center>
<h1>WiFi LED on off demo: 1</h1><br>
Ciclk to turn <a href="MotorOnMobile" target="myIframe">ON</a><br>
Ciclk to turn <a href="MotorOffMobile" target="myIframe">OFF</a><br>
Ciclk to turn <a href="automatic" target="myIframe">Automatic</a><br>
Ciclk to turn <a href="Manual" target="myIframe">Manual</a><br>
LED State:<iframe name="myIframe" width="250" height="100" frameBorder="0"><br>
<hr>
</center>

</body>
</html>
)=====";
 

//SSID and Password of your WiFi router
const char* ssid = "Saadat";
const char* password = "myloveismylord1234";

//Declare a global object variable from the ESP8266WebServer class.
ESP8266WebServer server(80); //Server on port 80

//===============================================================
// This routine is executed when you open its IP in browser
//===============================================================
void handleRoot() {
 Serial.println("You called root page");
 String s = MAIN_page; //Read HTML contents
 server.send(200, "text/html", s); //Send web page
}

void handleMotorOn() { 
 if(automatic == false){
  Serial.println("MOTOR ON");
  long distance = waterLevel();
  digitalWrite(motor, LOW);
  digitalWrite(textLED, HIGH);
  Serial.print(distance);
  Serial.print("cm "); //Motor is connected in reverse.Motor on
  server.send(200, "text/html", "Motor is ON"); //Send ADC value only to client ajax request
  if(distance < 7){
       MotorOff(distance);
     }
 }
}

void handleMotorOff() { 
 if(automatic == false){
  Serial.println("MOTOR OFF");
  digitalWrite(motor, HIGH);
  digitalWrite(textLED, LOW);
  Serial.print(waterLevel());
  Serial.print("cm "); //Motor is connected in reverse.Motor off
  server.send(200, "text/html", "Motor is OFF"); //Send ADC value only to client ajax request
 }
}

void handleMotorAutoON(){
  if(automatic == false){
    server.send(200, "text/html", "Motor Automatic Mode");
   automatic = true;
   }
}
void handleMotorAutoOFF(){
  if(automatic == true){
    server.send(200, "text/html", "Motor Manual Mode");
    automatic = false;
   }
}

void setup() {
   Serial.begin(115200); // Starting Serial Terminal
   pinMode(trigPin , OUTPUT); 
   pinMode(echoPin, INPUT);
   pinMode(motor, OUTPUT);
   pinMode(textLED, OUTPUT);

   digitalWrite(motor, HIGH);
   
   WiFi.begin(ssid, password);     //Connect to your WiFi router
   Serial.println("");
  
  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  //If connection successful show IP address in serial monitor
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //IP address assigned to your ESP
 
  server.on("/", handleRoot);      //Which routine to handle at root location. This is display page
  server.on("/MotorOnMobile", handleMotorOn); //as Per  <a href="ledOn">, Subroutine to be called
  server.on("/MotorOffMobile", handleMotorOff);
  server.on("/automatic", handleMotorAutoON);
  server.on("/Manual", handleMotorAutoOFF);
  

  server.begin();                  //Start server
  Serial.println("HTTP server started");
}
long waterLevel(){
  long duration,cm;
  
  digitalWrite(trigPin , LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin , HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin , LOW);
  duration = pulseIn(echoPin, HIGH);
  cm = duration * 0.034 / 2;
  return cm;
}
void MotorOff(long distance){
  PumpStatus = false;
  digitalWrite(motor, HIGH);
  digitalWrite(textLED, LOW);
  Serial.print(distance);
  Serial.print(" cm");
}
void MotorOn(long distance){
  PumpStatus = true;
  digitalWrite(motor, LOW);
  digitalWrite(textLED, HIGH);
  Serial.print(distance);
  Serial.print("cm ");
}
void autoValueCheck(long distance){
  bool chk = true;
   if(distance > 20){
     PumpStatus = true;
     while(chk && PumpStatus){       
       MotorOn(distance);
       distance = waterLevel();
     if(distance < 7){
       MotorOff(distance);
       chk = false;
     }
     Serial.println();
     delay(1000);
    }
   }  
}

void loop() {
  server.handleClient();
  if(automatic == true){
    long distance;
    distance = waterLevel();
    autoValueCheck(distance);
  }
}
