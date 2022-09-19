            /*Libraries*/
#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>

#include <FirebaseESP8266.h>
#include <WiFiClient.h> 
#include <WiFiManager.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h> //ESP Web Server Library to host a web page


/*---------------Firebase Setup---------------*/
#define WIFI_SSID "Saadat"
#define WIFI_PASSWORD "myloveismylord1234"
#define FIREBASE_HOST "arc-water-tank-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "AIzaSyB1DiVSm6VtSiwxr3UUkH37C_ROoWgdkX0"

FirebaseData fbdo;
ESP8266WebServer server(80);
/*---------------Motor Setup------------------*/
const int trigPin = D3; // Trigger Pin of Ultrasonic Sensor
const int echoPin = D4; // Echo Pin of Ultrasonic Sensor
int motor = D5;
int autoLED = D6;
int motorLED = D7;
bool PumpStatus = false;
bool automatic = false;

int upperLimit;
int lowerLimit;
int percantage;

void setup() {
  
   String wifipush="low";
   Serial.begin(115200); // Starting Serial Terminal
   pinMode(trigPin , OUTPUT); 
   pinMode(echoPin, INPUT);
   pinMode(motor, OUTPUT);
   pinMode(autoLED,OUTPUT);
   pinMode(motorLED,OUTPUT);
   
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();


  //3. Set your Firebase info
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  //4. Enable auto reconnect the WiFi when connection lost
  Firebase.reconnectWiFi(true);
 
}
void loop() {

  String motorStatus ;
  long distance = waterLevel();
  Firebase.getString(fbdo, "/MotoStatus", motorStatus);
  Firebase.getInt(fbdo, "/Limit/lower", lowerLimit);
  Firebase.getInt(fbdo, "/Limit/upper", upperLimit);
  if(motorStatus == "Auto"){
    automatic = true;
   digitalWrite(autoLED, HIGH);
  }
  if(motorStatus == "Manual"){
    automatic = false;
   digitalWrite(autoLED, LOW);
  }
  if(motorStatus == "ON"){
    handleMotorOn();
  }
  if(motorStatus == "OFF"){
    handleMotorOff();
  }

  if(automatic){
    long distance;
    distance = waterLevel();
    autoValueCheck(distance);
  }
  Firebase.getString(fbdo, "/Wifi/Reset", wifipush);
  if (wifipush == "high"){
  Firebase.setString(fbdo, "/Wifi/Reset", wifipush);                       
  server.stop();
  WiFiManager wifiManager;
  if (!wifiManager.startConfigPortal("ARC")){
    Serial.println("Timeout");
    delay(1000);
    ESP.reset();
    delay(1000);
    }
    Serial.print("Connected.... to ");
    Serial.println(WiFi.localIP());
    server.begin();
  }
  
     Serial.println();
     delay(1000);

}


void handleMotorOn() { 
 if(automatic == false){
  long distance = waterLevel();
  MotorOn();
  if(distance < upperLimit){
       MotorOff();
     }
 }
}

void handleMotorOff() { 
 if(automatic == false){
  MotorOff();
  waterLevel();
 }
}


long waterLevel(){
  long duration,cm;
  
  digitalWrite(trigPin , LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin , HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin , LOW);
  duration = pulseIn(echoPin, HIGH);
  cm = (duration/2) / 29.1;
  Serial.print(cm);
  Serial.println(" cm ");
  percantage = (cm/float(lowerLimit))*100;
  Serial.print(percantage);
  Serial.println(" %");
  if(percantage <101){
  Firebase.setFloat(fbdo, "/WaterLevel", percantage);
    }
  return cm;
}
void MotorOff(){
  PumpStatus = false;
  digitalWrite(motor, HIGH);
  Serial.println("MOTOR OFF");
  digitalWrite(motorLED, LOW);
}

void MotorOn(){
  PumpStatus = true;
  digitalWrite(motor, LOW);
  digitalWrite(motorLED, HIGH);
  Serial.println("MOTOR ON");
}
void autoValueCheck(long distance){
  bool chk = true;
   if(distance > lowerLimit){
     PumpStatus = true;
     while(chk && PumpStatus){       
       MotorOn();
       distance = waterLevel();
     if(distance < upperLimit){
       MotorOff();
       chk = false;
     }
    }
   }  
}
