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

#define forward 6
#define backward 5 
#define right A0
#define left 2
#define trigPin 7 
#define echoPin A1
#define left_headlight 9
#define right_headlight 3
#define tail_lights 4
#define battery A2

// ThreadController that will controll all threads
ThreadController controll = ThreadController();

Thread distanceThread = Thread();
Thread lightThread = Thread();
Thread batteryThread = Thread();

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
  ble_set_pins(10,8); // setting the REQN, RDYN pins
  
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
  pinMode(left_headlight, OUTPUT);
  pinMode(right_headlight, OUTPUT);
  pinMode(tail_lights, OUTPUT);
  pinMode(battery, INPUT);
  distanceThread.onRun(distance_sensor);
  distanceThread.setInterval(500);
  lightThread.onRun(light_sensor);
  lightThread.setInterval(300);
  batteryThread.onRun(measure_battery);
  batteryThread.setInterval(3000);
  controll.add(&distanceThread);
  controll.add(&lightThread);
  controll.add(&batteryThread);
  
  ble_begin();
}

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

void car_short_lights_on() {
  analogWrite(left_headlight, 40);
  analogWrite(right_headlight, 40);
  digitalWrite(tail_lights, HIGH);
}

void car_long_lights_on() {
  analogWrite(left_headlight, 255);
  analogWrite(right_headlight, 255);
  digitalWrite(tail_lights, HIGH);
}

void car_lights_off() {
   analogWrite(left_headlight, 0);
   analogWrite(right_headlight, 0);
   digitalWrite(tail_lights, LOW);
}

void measure_battery() {
  int sensor_value = analogRead(battery);
  float voltage = sensor_value * (5.0 / 1023.0); // converting sensor value to voltage
  String str ="b" + String(voltage) + " ";
  int buff_size = str.length();
  char buff[buff_size + 1];
  
  str.toCharArray(buff, buff_size + 1);
  ble_write_bytes((unsigned char *)buff, buff_size);
  
 // Serial.print("\n");
  //Serial.print("Battery Voltage: ");
  //Serial.print(voltage);
 // Serial.print("\n");
}
 
void distance_sensor() {
  String dst;
  //char buff[5];
  int duration, distance;
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(1000); //pauses the program for 1 millisecond
  digitalWrite(trigPin, LOW);
  
  duration = pulseIn(echoPin,HIGH);
  distance = (duration/2) / 29.1;
  dst ="d" + String(distance) + " ";
  
  int buff_size = dst.length();
  char buff[buff_size + 1];
  
  dst.toCharArray(buff,buff_size + 1);
  ble_write_bytes((unsigned char *)buff, buff_size);
  
  //Serial.print("Distance: ");
  //Serial.print(dst);
  //Serial.print(" cm");
 // Serial.print('\n');
  
}

void light_sensor(){
  uint16_t light_sen_lux = LightSensor.GetLightIntensity();// Get Lux value
  String light ="l" + String(light_sen_lux) + " ";
  
  int buff_size = light.length();
  char buff[buff_size + 1];
  
  light.toCharArray(buff, buff_size + 1);
  ble_write_bytes((unsigned char *)buff, buff_size);
  
  //Serial.print("Light: ");
  //Serial.print(light_sen_lux);
  //Serial.print(" lux");
  //Serial.print("\n");
}

void loop()
{
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
    case 'n': car_short_lights_on();
    break;
    case 'm': car_long_lights_on();
    break;
    case 'v': car_lights_off();
    break;
  }
  ble_do_events();
  controll.run();
}

