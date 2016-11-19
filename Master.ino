#include <DHT11.h>

DHT11 dht11(2);
int ledPin = 12;
int motion = 11;
String sensorsValue = "";

void setup(){
  pinMode(motion, INPUT);
  pinMode(ledPin, OUTPUT);
  Serial.begin(9600);
}
 
void loop(){
  sensorsValue = "";
  int err;
  float humi, temp;
  int pir = digitalRead(motion);
  int cds = analogRead(A0);
  if((err = dht11.read(humi, temp)) == 0){
    sensorsValue = String((int)humi) + "a" + String((int)temp) + "a" + String(cds) + "a" + String(pir);
  }
  else {
    sensorsValue = "Errora" + String(cds) + "a" + String(pir);
  }
  Serial.println(sensorsValue);
  delay(DHT11_RETRY_DELAY);
}
