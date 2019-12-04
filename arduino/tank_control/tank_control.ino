#include <Servo.h>

// Motors
#define E1 6 // M1 Speed Control
#define E2 5 // M2 Speed Control
#define M1 8 // M1 Direction Control
#define M2 7 // M2 Direction Control

// Servo
#define MIN_ANGLE 100
#define MAX_ANGLE 180
#define SERVO_MOVE_INCREMENT 5
#define SERVO_PIN 13

Servo servo;
int currentServoAngle = 100;

void setup(void)
{
  for(int i=5;i<=8;i++) {
    pinMode(i, OUTPUT);
  }
  
  servo.attach(SERVO_PIN);
  servo.write(currentServoAngle);
      
  Serial.begin(9600);
  Serial.println("setup");
}

void loop(void)
{
  if (Serial.available() > 0) {
    handleSerial(Serial.read());
  }
}

void stop(void)
{
  digitalWrite(E1,LOW);
  digitalWrite(E2,LOW);
  Serial.println("stop");
}

void forward(char a,char b)
{
  analogWrite (E1,a);
  digitalWrite(M1,LOW);
  analogWrite (E2,b);
  digitalWrite(M2,LOW);
  Serial.println("forward");
}

void reverse (char a,char b)
{
  analogWrite (E1,a);
  digitalWrite(M1,HIGH);
  analogWrite (E2,b);
  digitalWrite(M2,HIGH);
  Serial.println("reverse");
}

void left (char a,char b)
{
  analogWrite (E1,a);
  digitalWrite(M1,HIGH);
  analogWrite (E2,b);
  digitalWrite(M2,LOW);
  Serial.println("left");
}

void left (char a,char b, int ms) {
  left(a,b);
  delay(ms);
  stop();  
}

void right (char a,char b)
{
  analogWrite (E1,a);
  digitalWrite(M1,LOW);
  analogWrite (E2,b);
  digitalWrite(M2,HIGH);
  Serial.println("right");
}

void right (char a,char b, int ms) {
  right(a,b);
  delay(ms);
  stop();
}

void moveServoUp(int amount) {
  currentServoAngle = min(MAX_ANGLE, currentServoAngle + amount);
  servo.write(currentServoAngle);
}

void moveServoDown(int amount) {
  currentServoAngle = max(MIN_ANGLE, currentServoAngle - amount);
  servo.write(currentServoAngle);
}

void handleSerial(char command) {
  int leftspeed = 255;   // 255 is maximum speed
  int rightspeed = 255;  
  switch(command) // Perform an action depending on the command
  {
    case 'w'://Move Forward
    case 'W':
      forward (leftspeed,rightspeed);
      break;
    case 's'://Move Backwards
    case 'S':
      reverse (leftspeed,rightspeed);
      break;
    case 'a'://Turn Left
    case 'A':
      left (leftspeed,rightspeed);
      break;
    case 'd'://Turn Right
    case 'D':
      right (leftspeed,rightspeed);
      break;
    case 'u': //Tilt UP
    case 'U':
      moveServoUp(SERVO_MOVE_INCREMENT);
      break;
    case 'j':
    case 'J':
      moveServoDown(SERVO_MOVE_INCREMENT);
      break;
    case 'h':
    case 'H':
      left(leftspeed,rightspeed,100);
      break;
    case 'k':
    case 'K':
      right(leftspeed, rightspeed, 100);
      break;
    default:
      stop();
      break;
  }
}
