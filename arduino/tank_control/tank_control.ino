#include <Servo.h>
#include <FastLED.h>

FASTLED_USING_NAMESPACE

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


// Blinky Lights
#define DATA_PIN    7
#define LED_TYPE    WS2811
#define COLOR_ORDER GRB
#define NUM_LEDS    8
#define BRIGHTNESS          96
#define FRAMES_PER_SECOND  120
CRGB leds[NUM_LEDS];
uint8_t gHue = 0; // rotating "base color" used by blinky lights

void setup(void)
{
  for(int i=5;i<=8;i++) {
    pinMode(i, OUTPUT);
  }

  FastLED.addLeds<LED_TYPE,DATA_PIN,COLOR_ORDER>(leds, NUM_LEDS).setCorrection(TypicalLEDStrip);
  FastLED.setBrightness(BRIGHTNESS);

  servo.attach(SERVO_PIN);
  servo.write(currentServoAngle);
      
  Serial.begin(9600);
  Serial.println("setup");

  blinkyLights(CRGB::Aqua);
}

void loop(void)
{
  if (Serial.available() > 0) {
    handleSerial(Serial.read());
  }
}

void blinkyLights(CRGB color) {
  fill_solid(leds, NUM_LEDS, color);
  FastLED.show();
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
  FastLED.delay(ms);
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
    case 'r':
    case 'R':
      blinkyLights(CRGB::Red);
      break;
    case 'g':
    case 'G':
      blinkyLights(CRGB::Green);
      break;
    default:
      stop();
      break;
  }
}
