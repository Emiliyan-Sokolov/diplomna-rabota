#include <BH1750FVI.h>
#include <Wire.h>
#include <RBL_services.h>
#include <SPI.h>
#include <EEPROM.h>
#include <boards.h>
#include <RBL_nRF8001.h>
#include <Thread.h>
#include <ThreadController.h>

BH1750FVI LightSensor;

#define forward 6  // Pin 6 - Forward
#define backward 5 	// Pin 5 - Backward
#define right 4 //Pin 4 - Right
#define left 3 //Pin 3 - Left
#define trigPin 7 //sensor
#define echoPin 2 //sensor



// ThreadController that will controll all threads
ThreadController controll = ThreadController();

Thread distanceThread = Thread();
Thread lightThread = Thread();

void setup()
{
 
/*     Light Sensor pins
 
        VCC >>> 3.3V
        SDA >>> A4
        SCL >>> A5
        addr >> A3
        Gnd >>>Gnd
*/
    
  ble_set_name("My RC Car"); // The name have to be under 10 letters

  Serial.begin(9600);
  LightSensor.begin();

  LightSensor.SetAddress(Device_Address_H);//Address 0x5C
  LightSensor.SetMode(Continuous_H_resolution_Mode);
   
  pinMode(echoPin,INPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(forward, OUTPUT);
  pinMode(backward, OUTPUT);
  pinMode(right, OUTPUT);
  pinMode(left, OUTPUT);

  distanceThread.onRun(distance_sensor);
  distanceThread.setInterval(500);
  lightThread.onRun(light_sensor);
  lightThread.setInterval(300);
  controll.add(&distanceThread);
  controll.add(&lightThread);
  
  ble_begin();
}

unsigned char val;
unsigned int loop_count = 0;
int light_sen;

void go_forward() {
  digitalWrite(forward, HIGH);
}

void go_backward(){
  digitalWrite(backward, HIGH);
}

void go_right(){
  digitalWrite(right, HIGH);
}

void go_left(){
  digitalWrite(left, HIGH);
}

void stop_right() {
  digitalWrite(right, LOW);
}

void stop_left() {
  digitalWrite(left, LOW);
}

void stop_forward() {
  digitalWrite(forward, LOW);
}

void stop_backward() {
  digitalWrite(backward, LOW);
}

void distance_sensor() {
  String dst;
  char buff[5];
  int duration, distance;
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(1000); //pauses the program for 1 millisecond
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin,HIGH);
  distance = (duration/2) / 29.1;
  dst ="d" + String(distance) + "    ";
  dst.toCharArray(buff,5);
  Serial.print("Distance: ");
  Serial.print(dst);
  Serial.print(" cm");
  Serial.print('\n');
  //byte d = (byte)distance;
  //ble_write(d);
  ble_write_bytes((unsigned char *)buff, 4); 
}

void light_sensor(){
  uint16_t light_sen_lux = LightSensor.GetLightIntensity();// Get Lux value
  Serial.print("Light: ");
  Serial.print(light_sen_lux);
  Serial.print(" lux");
  Serial.print("\n");
  byte l = (byte)light_sen_lux;
 // ble_write(l);
}

void loop()
{
  if (ble_available())
  {
    //ble_write(val); //sends data to android device (one char)
    //ble_read(); //receives data from android device (one char)
    switch(ble_read()){
      case 'f': go_forward();
      break;
      case 'b': go_backward();
      break;
      case 'r': go_right();
      break;
      case 'l': go_left();
      break;
      case 'k': stop_forward();
      break;
      case 'g': stop_backward();
      break;
      case 'j': stop_right();
      break;
      case 'h': stop_left();
      break; 
    }
  }
  ble_do_events();
  controll.run();
}

