/**
  Done by: Group 2 of 19/20 Sem 2
  Peter Vickram & Tay Kee Kong
  For more info check out our wiki

*/

// Isaac's Notes
// only did getCm for sensor 5/3/6
// getCmX(current_filter_y) x = sensor number, y = filter number aka port number

// Libraries used                                                                                                                                                                                                                                                                                                                                                                                                                                                #include "DualVNH5019MotorShield.h"
#include <EnableInterrupt.h>
#include <PID_v1.h>
#include "DualVNH5019MotorShield.h"
#include <math.h>
#include <string.h>
#include <RunningMedian.h>
/* setting up global variables
*/
DualVNH5019MotorShield motor;
byte data;
float RPM = 0;
int test = 0;
int fcor = 0;
double movconst = 9.0;
double delayconst = 50;
char arguments;
char degree;
double degreeconst = 0;
bool debug = false;
int medno = 100;

void setup()
{
  /* Hardware setup */
  Serial.begin(115200);
  //Serial.println("Dual VNH5019 Motor Shield");
  setupMotorEncoder();
  setupPID();
}

double min1 = 1000, max1 = -1;
double min2 = 1000, max2 = -1;
double min3 = 1000, max3 = -1;
double min4 = 1000, max4 = -1;
double min5 = 1000, max5 = -1;
double min6 = 1000, max6 = -1;

double mintest(double v1, double v2) {
  if (v1 < v2) {
    return v1;
  }
  return v2;
}

double maxtest(double v1, double v2) {
  if (v1 > v2) {
    return v1;
  }
  return v2;
}


void printIRReading1(double cur0, double cur1, double cur2, double cur3, double cur4, double cur5)
{
  //FL > FC > FR > LC > LL > RR
  //  String stringOne = "pc|" + String(ZEROORONE(Sensor5(cur0))) + ';' + String(ZEROORONE(Sensor3(cur2))) + ';' + String(ZEROORONE(Sensor6(cur4))) + ';' + String((Sensor2(cur1))) + ';' + String(ZEROORONE(Sensor4(cur3))) + ';' + String(ZEROORONE(Sensor1(cur5))) + ';' ;

  String stringOne = "pc|" + String(cmAvg(5, cur0)) + ';' + String(cmAvg(3, cur2)) + ';' + String(cmAvg(6, cur4)) + ';' + String(cmAvg(2, cur1)) + ';' + String(cmAvg(4, cur3)) + ';' + String(cmAvg(1, cur5)) + ';' ;
  Serial.println(stringOne);

  //  double s1  = cmAvg(1,cur5);
  //min1 = mintest(min1, s1);
  //max1 = maxtest(max1, s1);
  //double err1= max1 - min1;

  //
  //double s2  = cmAvg(2,cur1);
  //min2 = mintest(min2, s2);
  //max2 = maxtest(max2, s2);
  //double err2= max2 - min2;
  //
  //
  //double s3  = cmAvg(3,cur2);
  //min3 = mintest(min3, s3);
  //max3 = maxtest(max3, s3);
  //double err3= max3 - min3;
  //
  //
  //double s4  = cmAvg(4,cur3);
  //min4 = mintest(min4, s4);
  //max4 = maxtest(max4, s4);
  //double err4= max4 - min4;
  //
  //
  //
  //  double s5  = cmAvg(5,cur0);
  //  min5 = mintest(min5, s5);
  //  max5 = maxtest(max5, s5);
  //  double err5= max5 - min5;
  //
  //
  //double s6  = cmAvg(6,cur4);
  //min6 = mintest(min6, s6);
  //max6 = maxtest(max6, s6);
  //double err6= max6 - min6;


  //Serial.print("FL: " + String(s5) + " | " + String(err5) + " ") ;
  //Serial.print("FC: " + String(s3) + "|" + String(err3) + " ");
  //Serial.print("FR: " + String(s6) + "|" + String(err6) + " ");
  //Serial.print("LC(LONG RANGE): " + String(s2) + "|" + String(err2) + " ");
  //Serial.print("LL: " + String(s4) + "|" + String(err4) + " ");
  //Serial.println("RR: " + String(s1) + "|" + String(err1) + " ");


  //  Serial.print("FL: " + String(cmAvg(5,cur0)) + " ") ;
  //  Serial.print("FC: " + String(cmAvg(3,cur2)) + " ");
  //  Serial.print("FR: " + String(cmAvg(6,cur4)) + " ");
  //  Serial.print("LC(LONG RANGE): " + String(cmAvg(2,cur1)) + " ");
  //  Serial.print("LL: " + String(cmAvg(4,cur3)) + " ");
  //  Serial.println("RR: " + String(cmAvg(1,cur5)) + " ");
  //  Serial.println("-------------------------------");
}


//  1   2*   3   4   5   6
//  RR  LC*  FC  LL  FL  FR


int getBlock(double cm, int sensor) {
  if (sensor == 1) {
    if (cm < 10) {
      return cm / 10;
    }
  } else if (sensor == 2) {
    if (cm < 70 && cm > 20) {
      return cm / 10;
    }
  } else if (sensor == 3) {
    if (cm < 10) {
      return cm / 10;
    }
  } else if (sensor == 4) {
    if (cm < 10) {
      return cm / 10;
    }
  } else if (sensor == 5) {
    if (cm < 10) {
      return cm / 10;
    }
  } else if (sensor == 6) {
    if (cm < 10) {
      return cm / 10;
    }
  }
  return -1;

}


double cmAvg(int sensor, double filter) {
  int avgcount = 5;
  double total = 0;
  for (int i = 0; i < avgcount; i++) {
    if (sensor == 1) {
      total = total + Sensor1(filter);

    } else if (sensor == 2) {
      total = total + Sensor2(filter);

    } else if (sensor == 3) {
      total = total + Sensor3(filter);

    } else if (sensor == 4) {
      total = total + Sensor4(filter);

    } else if (sensor == 5) {
      total = total + Sensor5(filter);

    } else if (sensor == 6) {
      total = total + Sensor6(filter);
    }
  }
  double avg = total / avgcount;

  return getBlock(avg, sensor);
  //return total/avgcount;

}

void loopAngle()
{
  // 1080- 1023

  // 720 - 683
  // 360 - 338
  // 270 - 256
  // 180 - 175
  // 90  -  88

  delay(1000);
  moveReverse();
  delay(5000);
}


void loopStraight()//StraightChecklist
{
  delay(3000);
  double setdistance [10] = {9.6, 19.1, 29.1, 37.8, 48.1, 58.1, 68.6, 79.2, 89.2, 99.2}; // custom distance for fastest path {9.65, 19.4, 29.4, 38.6, 48.5, 58.5, 69.2, 79.2, 89.6, 99.6}
  moveF(setdistance[3]);
  delay(50000);
}

void loopTestDirection()//TestDirection
{
  delay(3000);
  moveR();
  delay(3000);
  moveR();
}


void caliFront(double old0, double old2, double old4)
{
  double left = Sensor5(old0);
  double right = Sensor6(old4);
  double center = Sensor3(old2);

  double distR = FrontRightReading('R', old4);
  double distL = FrontLeftReading('L', old0);
  double distM = FrontLeftReading('M', old2);

  if ((distR < 10) && (distL < 10)) {
    caliFront1('L', 'R', old0, old4, left);
  }
  else if ((distL < 10) && (distM < 10)) {
    caliFront1('L', 'M', old0, old2, left);
  }
  else if ((distR < 10) && (distM < 10)) {
    caliFront1('M', 'R', old2, old4, right);
  }
  delay(5);
}
void debugCaliFront(double old0, double old2, double old4) {
  return;
}

void debugcaliFront(double old0, double old2, double old4) {
  while (1) {
    int medno = 50;
    double totalL = 0;
    double totalC = 0;
    double totalR = 0;

    for (int i = 0; i < medno; i++) {
      totalL = totalL + ReadCm5(old0);
      totalC = totalC + ReadCm3(old2);
      totalR = totalR + ReadCm6(old4);
    }
    //  Serial.print(totalL/medno);
    //  Serial.print(" ");
    //  Serial.print(totalC/medno);
    //  Serial.print(" ");
    //  Serial.println(totalR/medno);
    Serial.flush();
  }
}


void loop()
{

  bool printIR = false;
  bool anything = false;

  double oldFiltered0 = -1; double curFiltered0 = -1;
  double oldFiltered1 = -1; double curFiltered1 = -1;
  double oldFiltered2 = -1; double curFiltered2 = -1;
  double oldFiltered3 = -1; double curFiltered3 = -1;
  double oldFiltered4 = -1; double curFiltered4 = -1;
  double oldFiltered5 = -1; double curFiltered5 = -1;

  double setdistance [20] = {9.0, 19.1, 29.1, 37.8, 48.1, 58.1, 68.6, 79.2, 89.2, 99.2
                             , 108.8, 118.8, 128.8, 138.8, 148.8, 158.8, 168.8, 178.8, 188.8
                            };
  arguments = Serial.read();
  //mototrarguments = '0';
  if (arguments == '\0') {
    return;
  }

  int moveblock = -1;

  switch (arguments) {
    case '!': moveblock = 10; break;
    case '@': moveblock = 11; break;
    case '#': moveblock = 12; break;
    case '$': moveblock = 13; break;
    case '%': moveblock = 14; break;
    case '^': moveblock = 15; break;
    case '&': moveblock = 16; break;
    case '*': moveblock = 17; break;
    case '(': moveblock = 18; break;
  }

  if (arguments >= '0' && arguments <= '9' ) {
    moveblock = int(arguments - '0');
  }

  if (moveblock > -1) {
    double movedist = setdistance[moveblock];
    moveF(movedist);
    printIRReading1(curFiltered0, curFiltered1, curFiltered2, curFiltered3, curFiltered4, curFiltered5);
  }

  switch (arguments) {
    // Take in sensor reading from starting position right when exploration starts
    case 'E':
      //Sensor Reading
      printIR = true;
      break;

    case 'I':
      //Turn 180
      moveReverse();
      printIR = true;
      break;

    case 'F':
      //Move Forward
      moveF(movconst);
      delay(delayconst);
      printIR = true;
      break;

    case 'H':
      //Front Calliberation
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      anything = true;
      break;

    case 'T':
      //Right Calliberation
      moveR();
      delay(10);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      delay(10);
      moveL();
      delay(movconst);
      anything = true;
      break;

    case 'J':
      //Left Calliberation
      moveL();
      delay(100);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      moveR();
      delay(movconst);
      anything = true;
      break;

    case 'K':
      //Right and Front Calliberation
      moveR();
      delay(10);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      moveL();
      delay(10);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      anything = true;
      break;

    case 'M':
      //Left Front Calliberation
      moveL();
      delay(10);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      moveR();
      delay(10);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      anything = true;
      break;

    case 'L':
      //Turn Left
      moveL();
      delay(delayconst);
      printIR = true;
      break;


    case 'R':
      //Turn Right
      moveR();
      delay(delayconst);
      printIR = true;
      break;

    // right hug calibrate
    case 'A': moveR();
      delay(delayconst);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      delay(delayconst);
      printIRReading1(curFiltered0, curFiltered1, curFiltered2, curFiltered3, curFiltered4, curFiltered5);
      moveL();
      delay(20);
      break;

    // left hug calibrate
    case 'B':
      moveL();
      delay(delayconst);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      delay(delayconst);
      printIRReading1(curFiltered0, curFiltered1, curFiltered2, curFiltered3, curFiltered4, curFiltered5);
      moveR();
      delay(20);
      break;

    // end exploration calibration
    case 'C': delay(delayconst);
      moveL();
      delay(delayconst);
      moveL();
      delay(delayconst);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      moveR();
      delay(delayconst);
      caliFront(curFiltered0, curFiltered2, curFiltered4);
      moveR();
      break;
    default: break;
  }

  if (printIR) {
    printIRReading1(curFiltered0, curFiltered1, curFiltered2, curFiltered3, curFiltered4, curFiltered5);
  }
  else if (anything) {
    Serial.println("pc|A");
  }
  Serial.flush();
}
