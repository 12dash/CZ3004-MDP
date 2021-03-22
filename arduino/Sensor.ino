#include <ZSharpIR.h>
#include <RunningMedian.h>
#include "Math.h"

ZSharpIR SFL_S = ZSharpIR(A0, 1080);
ZSharpIR SR1_S = ZSharpIR(A1, 1080);
ZSharpIR SR2_S = ZSharpIR(A2, 1080);
ZSharpIR SFR_S = ZSharpIR(A4, 1080);
ZSharpIR SL_S = ZSharpIR(A3, 20150);
ZSharpIR SFM_S = ZSharpIR(A5, 1080);

int HARDLIMIT;
//  1   2*   3   4   5   6
//  RR  LC*  FC  LL  FL  FR

double offset1 = -7.2;
double offset2 = -1;
double offset3 = -12.2;
double offset4 = -5.5;
double offset5 = -7.4;
double offset6 = -12.7;

double SFM_R2()
{
  RunningMedian med = RunningMedian(medno);
  for (int i = 0; i < medno; i++)
  {
    med.add(SFM_S.distance());
  }
  double ar = med.getMedian();
  return (ar / 10) - 2;
}



double SL_R2()
{
  RunningMedian med = RunningMedian(medno);
  for (int i = 0; i < medno; i++)
  {
    med.add(SL_S.distance());
  }
  double ar = med.getMedian();
  return (ar / 10) - 10;
}

double SR2_R2()
{
  RunningMedian med = RunningMedian(medno);
  for (int i = 0; i < medno; i++)
  {
    med.add(SR2_S.distance());
  }
  double ar = med.getMedian();
  return (ar / 10) - 5;
}

double SR1_R2()
{
  RunningMedian med = RunningMedian(medno);
  for (int i = 0; i < medno; i++)
  {
    med.add(SR1_S.distance());
  }
  double ar = med.getMedian();
  return (ar / 10) - 5;
}

double SFL_R2()
{
  RunningMedian med = RunningMedian(medno);
  for (int i = 0; i < medno; i++)
  {
    med.add(SFL_S.distance());
  }
  double ar = med.getMedian();
  return (ar / 10) - 4;
}

double SFR_R2()
{
  RunningMedian med = RunningMedian(medno);
  for (int i = 0; i < medno; i++)
  {
    med.add(SFR_S.distance());
  }
  double ar = med.getMedian();
  return (ar / 10) - 4.2;
}

int SFL_IR() {
  double ans;
  ans = SFL_R2();

  if (ans > 0 && ans <= 10) {
    ans = 1;
  }
  else if (ans > 10 && ans <= 18) {
    ans = 2;
  }
  else {
    ans = -1;
  }
  return ans;
}


int SFR_IR() {
  double ans;
  ans = SFR_R2();

  if (ans > 0 && ans <= 9) {
    ans = 1;
  }
  else if (ans > 9 && ans <= 18) {
    ans = 2;
  }
  else {
    ans = -1;
  }
  return ans;

}
int SFM_IR() {
  double ans;
  ans = SFM_R2();

  if (ans > 0 && ans <= 10) {
    ans = 1;
  }
  else if (ans > 10 && ans <= 20) {
    ans = 2;
  }
  else {
    ans = -1;
  }
  return ans;
}

int SR1_IR() {
  double ans;
  ans = SR1_R2();


  if (ans > 0 && ans <= 11.3) {
    ans = 1;
  }
  else if (ans > 11.3 && ans <= 18.5) {
    ans = 2;
  }
  else {
    ans = -1;
  }
  return ans;
}
int SR2_IR() {
  double ans;
  ans = SR2_R2();

  if (ans > 0 && ans <= 9.5) {
    ans = 1;
  }
  else if (ans > 9.5 && ans <= 16.5) {
    ans = 2;
  }
  else {
    ans = -1;
  }
  return ans;
}

int SL_IR() {
  double ans;
  ans = SL_R2();

  if (ans > 0 && ans <= 11.55) {
    ans = 1;
  }
  else if (ans > 11.55 && ans <= 20.25) {
    ans = 2;
  }
  else if (ans > 20.25 && ans <= 32.4) {
    ans = 3;
  }
  else if (ans > 32.4 && ans < 42) {
    ans = 4;
  }
  else if (ans >= 42 && ans <= 54) {
    ans = 5;
  }
  else {
    ans = -1;
  }
  return ans;
}

double FrontLeftReading(char leftBlock, double leftFilter) {
  medno = 5;
  double totalLeft = 0;
  for (int i = 0; i < medno; i++) {
    if (leftBlock == 'L') {
      totalLeft = totalLeft + ReadCm5(leftFilter);
    } else if (leftBlock == 'M') {
      totalLeft = totalLeft + ReadCm3(leftFilter);
    }
  }
  return totalLeft / medno;
}

double FrontRightReading(char rightBlock, double rightFilter) {
  medno = 5;
  double totalRight = 0;
  for (int i = 0; i < medno; i++) {
    if (rightBlock == 'R') {
      totalRight = totalRight + ReadCm6(rightFilter);
    } else if (rightBlock == 'M') {
      totalRight = totalRight + ReadCm3(rightFilter);
    }
  }
  return totalRight / medno;
}

double FrontFrontReading() {
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for (int i = 0; i < medno; i++) {
    med.add(ReadCm3(-1));
  }
  return (med.getMedian());
}

void angleCali(char leftBlock, char rightBlock, double leftFilter, double rightFilter, int distance) {

  HARDLIMIT = 50;

  double distL = FrontLeftReading(leftBlock, leftFilter);
  double distR = FrontRightReading(rightBlock, rightFilter);
  double diff = distL - distR;

  double limit = 0.03;

  while (abs(diff) >= 0.08 && HARDLIMIT > 0)
  {
    if (diff <= -limit)
    {
      moveL45(0.000001);
    }
    else if (diff >= limit)
    {
      moveR45(0.000001);
    }
    distL = FrontLeftReading(leftBlock, leftFilter);
    distR = FrontRightReading(rightBlock, rightFilter);
    diff = distL - distR;
    HARDLIMIT = HARDLIMIT - 1;
  }
  distL = FrontLeftReading(leftBlock, leftFilter);
  distR = FrontRightReading(rightBlock, rightFilter);
  diff = distL - distR;
}

void distanceCali(char leftBlock, char rightBlock, double leftFilter, double rightFilter, int distance) {

  HARDLIMIT = 30;

  double distL = FrontLeftReading(leftBlock, leftFilter);
  double distR = FrontRightReading(rightBlock, rightFilter);
  double distF = (distL + distR) / 2;
  double diff = distF - 5.5;

  do
  {
    distL = FrontLeftReading(leftBlock, leftFilter);
    distR = FrontRightReading(rightBlock, rightFilter);
    distF = (distL + distR) / 2;
    diff = abs(distF - 5.5);
    if (distF < 5.4) {
      moveB(diff);
    }
    else if (distF > 5.6) {
      moveF(diff);
    }
    delay(5);
    HARDLIMIT = HARDLIMIT - 1;
  } while ((diff > 0.2) && HARDLIMIT > 0);
}

void caliFront1(char leftBlock, char rightBlock, double leftFilter, double rightFilter, int distance) {
  angleCali( leftBlock,  rightBlock,  leftFilter,  rightFilter,  distance);
  distanceCali( leftBlock,  rightBlock,  leftFilter,  rightFilter,  distance);
}


// Define model and input pin:
//define model 1080
//define model1 20150

#define model GP2Y0A21YK0F
#define model1 GP2Y0A02YK0F
// Create variable to store the distance:
int distance_cm, distance_cm2 , distance_cm3 , distance_cm4 , distance_cm5 , distance_cm6;


double curFiltered;
double oldFiltered = -1;
double V;

/* Model :
  GP2Y0A02YK0F --> 20150
  GP2Y0A21YK0F --> 1080
  GP2Y0A710K0F --> 100500
  GP2YA41SK0F --> 430
*/

// Create a new instance of the SharpIR class:

double V0; // Read voltage
double V1;
double V2;
double V3;
double V4;
double V5;
double oldFiltered0 = -1; double curFiltered0;
double oldFiltered1 = -1; double curFiltered1;
double oldFiltered2 = -1; double curFiltered2;
double oldFiltered3 = -1; double curFiltered3;
double oldFiltered4 = -1; double curFiltered4;
double oldFiltered5 = -1; double curFiltered5;



//========================================
// GET DIST FUNCTIONS
//=========================================

//  1   2*   3   4   5   6
//  RR  LC*  FC  LL  FL  FR

// Returns sensor 1 readings in units      // RR - SHORT RANGE
double getDist1(double x) {
  return 30.027 * pow(x, -1.017) + offset1;
}

// Returns sensor 2 readings in units   // LC - LONG RANGE
double getDist2(double x) {
  // return (5.5679*pow(x,4)-53.073*pow(x,3)+ 189.98*pow(x,2) - 319.54*pow(x,1) + 248.95) + offset2;
  return ((192.691 - 110.393 * x - 102.911 * pow(x, 2) + 140.156 * pow(x, 3) - 54.651 * pow(x, 4) + 7.120 * pow(x, 5)) + offset2);
}

// Returns sensor 3 (FR) readings in units   // FC - SHORT RANGE
double getDist3(double x) {
  return 32.022 * pow(x, -1.052) + offset3;
}


// Returns sensor 4 readings in units     // LL - SHORT RANGE
double getDist4(double x) {
  return 28.599 * pow(x, -1.009) + offset4 ;
}

// Returns sensor 5 readings in units     // FL - SHORT RANGE
double getDist5(double x) {

  return 27.537 * pow(x, -0.976) + offset5;
}

// Returns sensor 6 readings in units
double getDist6(double x) {
  return 31.130 * pow(x, -1.046) + offset6;
}


//========================================
//  SENSOR FUNCTIONS
//========================================

//  1   2*   3   4   5   6
//  RR  LC*  FC  LL  FL  FR

double Sensor1(double old5)   // RR - SHORT RANGE
{
  medno = 10;
  double V5;
  RunningMedian med = RunningMedian(medno);
  double oldFiltered5 = old5; double curFiltered5;
  for (int x = 0; x < medno; x++)
  {
    V5 = analogRead(A5);
    curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
    oldFiltered5 = curFiltered5;
    med.add(getDist1(curFiltered5 * 0.0049));
    //Serial.println(getDist1(curFiltered5*0.0049));
  }
  return (med.getMedian()) ;

}

double Sensor2(double old1) //LC - LONGRANGE
{
  medno = 10;
  double V1;
  RunningMedian med = RunningMedian(medno);
  double oldFiltered1 = old1; double curFiltered1;
  for (int x = 0; x < medno; x++)
  {
    V1 = analogRead(A1);
    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1;
    med.add(getDist2(curFiltered1 * 0.0049));
    //Serial.println(getDist2(curFiltered1*0.0049));
  }
  return (med.getMedian()) ;

}



double Sensor3(double old2)  //FC - SHORT RANGE
{
  medno = 20;
  double V2;
  RunningMedian med = RunningMedian(medno);
  double oldFiltered2 = old2; double curFiltered2;
  for (int x = 0; x < medno; x++)
  {
    V2 = analogRead(A2);
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;
    med.add(getDist3(curFiltered2 * 0.0049));
    //Serial.println(getDist4(curFiltered2*0.0049));
  }
  return (med.getMedian()) ;

}

double Sensor4(double old3)   //LL - SHORT RANGE
{
  medno = 10;
  double V3;
  RunningMedian med = RunningMedian(medno);
  double oldFiltered3 = old3; double curFiltered3;
  for (int x = 0; x < medno; x++)
  {
    V3 = analogRead(A3);
    curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
    oldFiltered3 = curFiltered3;
    med.add(getDist4(curFiltered3 * 0.0049));
    //Serial.println(getDist3(curFiltered3*0.0049));
  }
  return (med.getMedian()) ;

}


double Sensor5(double old0)     //FL - SHORT RANGE
{
  medno = 20;
  double V0;
  RunningMedian med = RunningMedian(medno);
  double oldFiltered0 = old0; double curFiltered0;
  for (int x = 0; x < medno; x++)
  {
    V0 = analogRead(A0);
    curFiltered0 = filter(V0, oldFiltered0); // Exponential filter
    oldFiltered0 = curFiltered0;
    med.add(getDist5(curFiltered0 * 0.0049));
    //Serial.println(getDist5(curFiltered0*0.0049));
  }
  return (med.getMedian()) ;

}


double Sensor6(double old4) //FR - SHORT RANGE
{
  medno = 20;
  double V4;
  RunningMedian med = RunningMedian(medno);
  double oldFiltered4 = old4; double curFiltered4;
  for (int x = 0; x < medno; x++)
  {
    V4 = analogRead(A4);
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;
    med.add(getDist6(curFiltered4 * 0.0049));
    //Serial.println(getDist6(curFiltered4*0.0049));
  }
  return (med.getMedian()) ;

}



//========================================
//  READ CM FUNCTIONS FUNCTIONS
//=========================================


double ReadCm3(double old2) {
  medno = 10;
  double V2;
  RunningMedian med = RunningMedian(medno);
  double oldFiltered2 = old2; double curFiltered2;
  for (int x = 0; x < medno; x++)
  {
    V2 = analogRead(A2);
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;
    med.add(getCm3(curFiltered2 * 0.0049));

  }
  return (med.getMedian()) ;
}


// Returns sensor 5 readings in cm
double ReadCm5(double old0) {
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  double V0;
  double oldFiltered = old0; double curFiltered0;

  for (int x = 0; x < medno; x++)
  {
    V0 = analogRead(A0);
    curFiltered0 = filter(V0, oldFiltered0); // Exponential filter
    oldFiltered0 = curFiltered0;
    med.add(getCm5(curFiltered0 * 0.0049));
    //Serial.println(getCm5(curFiltered0 * 0.0049));
  }
  return (med.getMedian()) ;
}

double ReadCm6(double old4) {
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  double V4;
  double oldFiltered = old4; double curFiltered4;
  for (int x = 0; x < medno; x++)
  {
    V4 = analogRead(A4);
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;
    med.add(getCm6(curFiltered4 * 0.0049 ));
    //Serial.println(getCm6(curFiltered4));
  }
  return (med.getMedian()) ;
}


//========================================
//  GET CM FUNCTIONS FUNCTIONS
//=========================================

double getCm3(double x) {
  // - 5
  double temp = 32.022 * pow(x, -1.052) - 12.40;
  return temp;
}


double getCm5(double x) {
  // -4
  double temp = 27.537 * pow(x, -0.976) - 8.1; //-6.71 -1.2 ;
  return temp;
}

double getCm6(double x) {
  // -5
  double temp = 31.130 * pow(x, -1.046) - 10.7; // -7.18-4.4;
  return temp;
}




double alpha = 0.1; // Smoothing Factor
double filter(double volt, double oldVal) {
  return volt;
  //  if(oldVal == -1){
  //    return volt;
  //  }
  //  return (alpha*volt) + (1-alpha)*oldVal;
}












// =======================================================================================
// =======================================================================================
// =======================================================================================
// =======================================================================================



// callback function for doing comparisons
int myCompareFunction (const void * arg1, const void * arg2)
{
  int * a = (int *) arg1;  // cast to pointers to integers
  int * b = (int *) arg2;

  // a less than b?
  if (*a < *b)
    return -1;

  // a greater than b?
  if (*a > *b)
    return 1;

  // must be equal
  return 0;
}  // end of myCompareFunction



double Sensor61(double old4)
{
  medno = 50;
  double V4;
  int total = 0;
  int temp [medno - 1] ;
  double oldFiltered4 = old4; double curFiltered4;
  for (int x = 0; x < medno; x++)
  {
    V4 = analogRead(A4);
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;

    //total = total + getDist6(curFiltered4*0.0049);
    temp[x] = getDist6(curFiltered4 * 0.0049);
    //Serial.println(getDist6(curFiltered4*0.0049));
  }

  qsort (temp, medno - 1, sizeof (int), myCompareFunction); // Sort numbers
  for (int y = 15; y < 35; y++)
  {
    total = temp[y] + total;
  }
  //total = total / medno-1;

  total = total / 20;
  //Serial.println(total);

  //    if(total >= 1 && total <= 10)
  //    return 0;
  //    else if(total >=10 && total <= 19)
  //    return 1;
  //    else if(total >=19 && total <= 29)
  //    return 2;
  //    else if(total >=19 && total <=30)
  //    return 3;
  //    else
  //    return -1;

  return (total / 10);

  //return (med.getMedian()) ;

}

double Sensor51(double old0)
{
  medno = 50;
  double V0;
  int total = 0;
  int temp [medno - 1] ;
  double oldFiltered0 = old0; double curFiltered0;
  for (int x = 0; x < medno; x++)
  {
    V0 = analogRead(A0);
    curFiltered0 = filter(V0, oldFiltered0); // Exponential filter
    oldFiltered0 = curFiltered0;
    //med.add(getDist5(curFiltered0 * 0.0049));
    temp[x] = getDist5(curFiltered0 * 0.0049);
    //total = total + getDist5(curFiltered0*0.0049);
    //Serial.println(getDist5(curFiltered0*0.0049));
  }
  qsort (temp, medno - 1, sizeof (int), myCompareFunction); // Sort numbers
  for (int y = 15; y < 35; y++)
  {
    total = temp[y] + total;
  }

  //total = total / medno-1;

  total = total / 20;
  //Serial.println(total);
  return (total / 10);

  //return (med.getMedian()) ;

}

double Sensor31(double old2)
{
  medno = 50;
  double V2;
  int total = 0;
  int temp [medno - 1] ;
  double oldFiltered2 = old2; double curFiltered2;
  for (int abc = 0; abc < 100; abc++)
  {
    V2 = analogRead(A2);
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;
  }
  for (int x = 0; x < medno; x++)
  {
    V2 = analogRead(A2);
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;
    //med.add(getDist4(curFiltered2 * 0.0049));
    temp[x] = getDist4(curFiltered2 * 0.0049);
    //total = total + getDist4(curFiltered2*0.0049);
    //Serial.println(getDist4(curFiltered2*0.0049));
  }

  qsort (temp, medno - 1, sizeof (int), myCompareFunction); // Sort numbers

  for (int y = 15; y < 35; y++)
  {
    total = temp[y] + total;
  }

  //total = total / medno-1;

  total = total / 20;
  //Serial.println(total);

  return (total / 10);

  //return (med.getMedian()) ;

}
