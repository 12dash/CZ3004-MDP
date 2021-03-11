#include <ZSharpIR.h>
#include <RunningMedian.h>
#include "Math.h"

ZSharpIR SFL_S = ZSharpIR(A0, 1080);
ZSharpIR SR1_S = ZSharpIR(A1, 1080);
ZSharpIR SR2_S = ZSharpIR(A2, 1080);
ZSharpIR SFR_S = ZSharpIR(A4, 1080);
ZSharpIR SL_S = ZSharpIR(A3, 20150);
ZSharpIR SFM_S= ZSharpIR(A5,1080);

int medno=100;

double SFM_R2()
{
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
    {
        med.add(SFM_S.distance());
    }
  double ar = med.getMedian();
  return (ar/10)-2;
}



double SL_R2()
{
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
    {
        med.add(SL_S.distance());
    }
  double ar = med.getMedian();
  return (ar/10)-10;
}

double SR2_R2()
{
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
    {
        med.add(SR2_S.distance());
    }
  double ar = med.getMedian();
  return (ar/10)-5;
}

double SR1_R2()
{
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
    {
        med.add(SR1_S.distance());
    }
  double ar = med.getMedian();
  return (ar/10)-5;
}

double SFL_R2()
{
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
    {
        med.add(SFL_S.distance());
    }
  double ar = med.getMedian();
  return (ar/10)-4;
}

double SFR_R2()
{
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
    {
        med.add(SFR_S.distance());
    }
  double ar = med.getMedian();
  return (ar/10)-4.2;
}

int SFL_IR() {
 double ans;
     ans=SFL_R2();
  
    if (ans > 0 && ans <= 10){
      ans=1;
    } 
    else if (ans > 10 && ans <= 18){
      ans=2;
    } 
    else{
      ans=-1;
    }
  return ans;
}


int SFR_IR() {
 double ans;
     ans=SFR_R2();

    
  
    if (ans > 0 && ans <= 9){
      ans=1;
    } 
    else if (ans > 9 && ans <= 18){
      ans=2;
    } 
    else{
      ans=-1;
    }
  return ans;

}
int SFM_IR() {
 double ans;
     ans=SFM_R2();

    
  
    if (ans > 0 && ans <= 10){
      ans=1;
    } 
    else if (ans > 10 && ans <= 20){
      ans=2;
    } 
    else{
      ans=-1;
    }
  return ans;
}

int SR1_IR() {
 double ans;
     ans=SR1_R2();
    
  
    if (ans > 0 && ans <= 11.3){
      ans=1;
    } 
    else if (ans > 11.3 && ans <= 18.5){
      ans=2;
    } 
    else{
      ans=-1;
    }
  return ans;
}
int SR2_IR() {
 double ans;
    ans=SR2_R2();
  
    if (ans > 0 && ans <= 9.5){
      ans=1;
    } 
    else if (ans > 9.5 && ans <= 16.5){
      ans=2;
    } 
    else{
      ans=-1;
    }
  return ans;
}

int SL_IR() {
 double ans;
    ans=SL_R2();
  
    if (ans > 0 && ans <= 11.55){
      ans=1;
    } 
    else if (ans > 11.55 && ans <= 20.25){
      ans=2;
    } 
else if (ans > 20.25 && ans <= 32.4){
      ans=3;
    } 
    else if (ans > 32.4 && ans< 42){
      ans=4;
    } 
    else if (ans >= 42 && ans<=54){
      ans=5;
    } 
    else{
      ans=-1;
    }
  return ans;
}

double FrontLeftReading(char leftBlock){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
    if(leftBlock=='L'){
      med.add(ReadCm2());
//      return ReadCm2();
    }else if(leftBlock == 'C'){
      med.add(ReadCm5());
//      return ReadCm5();
    }
  }
  return (med.getMedian());
}

double RightFrontReading(){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
      med.add(ReadCm5());
  }
  return(med.getMedian());
}

double RightRearReading(){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
      med.add(ReadCm5());
  }
  return(med.getMedian());
}

double FrontRightReading(char leftBlock){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
    if(leftBlock=='R'){
      med.add(ReadCm3());
//      return ReadCm5();
    }else if(leftBlock == 'C'){
//      return ReadCm5();
      med.add(ReadCm5());
    }
  }
  return(med.getMedian());
}

double FrontFrontReading(){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
      med.add(ReadCm5());
//    }
  }
  return(med.getMedian());
}


void caliFront(char leftBlock, char rightBlock) {

  leftBlock = 'L';
  rightBlock = 'R';
  double distL = FrontLeftReading(leftBlock);
  double distR = FrontRightReading(rightBlock);
  double diff = distL-distR;
  double distF = (distL+distR)/2;
  
  Serial.print(distL);
  Serial.print(" ");
  Serial.print(distR);
  Serial.print(" ");
  Serial.println(diff);
//  return;
  
    //while ((( diff>=0.1 && diff<=5.8 ) || (diff<=-0.05 && diff>=-5.4)) && (distL<10 && distL<10))
//    while ((diff>=-5.4 && diff<=5.8) && (distL<10 && distR<10))
    for(int i = 0;i<5;i++){
//      while (abs(diff)>=0.02)
      while (false)
      {
        if (diff <=-0.05)
        {
          moveL45(0.000001);
          //delay(30);
        }
        else if (diff >= 0.1)
        {
          moveR45(0.000001);
          //delay(30);
        }

//        Serial.print(distL);
//        Serial.print(" ");
//        Serial.print(distR);
//        Serial.print(" ");
//        Serial.println(diff);
        
        distL = FrontLeftReading(leftBlock);
        distR = FrontRightReading(rightBlock);
        diff = distL-distR;
      }
    }
    
   do
   {
      distL = FrontLeftReading(leftBlock);
      distR = FrontRightReading(rightBlock);
      double distC = FrontFrontReading();
      distF = (distL+distR+distC)/3;

      

      Serial.print(distL);
      Serial.print(" ");
      Serial.print(distR);
      Serial.print(" ");
      Serial.print(distC);
      Serial.print(" ");
      Serial.println(distF);
      
      if(distF<=4.5){
//        moveB(0.000001);
      }else if(distF>=5.5){
//        moveF(0.000001);
      }
      if(distF > 11 || distF < 2)
      {
        delay(5);
      }
//    }while(distF<=4.5 || distF>=5.5);
    }while(true);
}

void caliRight()
{
  delay(10);
  if(SR1_R2()<10 && SR2_R2()<10)
  {while(1)
  {
      double d = SR1_R2()-SR2_R2();
      if(d<=0.1 && d>= -0.1)
        break;
      if(d< -0.1 )
        moveL45(0.0000001);
      else 
        moveR45(0.0000001);
      
  }}
//
//  double distL = RightFrontReading(leftBlock);
//  double distR = RightRearReading(rightBlock);
//  double diff = distL-distR;
//  
//  Serial.print(distL);
//  Serial.print(" ");
//  Serial.print(distR);
//  Serial.print(" ");
//  Serial.println(diff);
////  return;
//  
//    //while ((( diff>=0.1 && diff<=5.8 ) || (diff<=-0.05 && diff>=-5.4)) && (distL<10 && distL<10))
////    while ((diff>=-5.4 && diff<=5.8) && (distL<10 && distR<10))
//    for(int i = 0;i<5;i++){
//      while (abs(diff)>=0.02)
//      {
//        if (diff <=-0.05)
//        {
//          moveL45(0.000001);
//          //delay(30);
//        }
//        else if (diff >= 0.1)
//        {
//          moveR45(0.000001);
//          //delay(30);
//        }
//
//        Serial.print(distL);
//        Serial.print(" ");
//        Serial.print(distR);
//        Serial.print(" ");
//        Serial.println(diff);
//        
//        distL = FrontLeftReading(leftBlock);
//        distR = FrontRightReading(rightBlock);
//        diff = distL-distR;
//      }
//    }
}



// Define model and input pin:
//define model 1080
//define model1 20150

#define model GP2Y0A21YK0F
#define model1 GP2Y0A02YK0F
// Create variable to store the distance:
int distance_cm, distance_cm2 ,distance_cm3 ,distance_cm4 ,distance_cm5 ,distance_cm6;


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


//SharpIR mySensor(SharpIR:: model, A0);
//SharpIR mySensor2(SharpIR:: model, A1);
//SharpIR mySensor3(SharpIR:: model, A2);
//SharpIR mySensor4(SharpIR:: model, A3);
//SharpIR mySensor5(SharpIR:: model, A4);
//SharpIR mySensor6(SharpIR:: model1, A5);
double V0; // Read voltage
double V1;
double V2;
double V3;
double V4;
double V5; 
double oldFiltered0 =-1; double curFiltered0;
double oldFiltered1 =-1; double curFiltered1;
double oldFiltered2 =-1; double curFiltered2;
double oldFiltered3 =-1; double curFiltered3;
double oldFiltered4 =-1; double curFiltered4;
double oldFiltered5 =-1; double curFiltered5;

double getReading1() {
  //===== change according to pin (A0 = PS1, A1 = PS2, etc) =====
  medno = 30;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
  {
    V0 = analogRead(A0);
    if(oldFiltered0 == -1) // sanity check for t=0
      oldFiltered0 = V0;
    curFiltered0 = filter(V0, oldFiltered0); // Exponential filter
    oldFiltered0 = curFiltered0; // get old value
//    if(i==10){
      med.add(getDist1(curFiltered0*0.0049));
      if(getDist1(curFiltered0*0.0049) == 0)
      return 0;
//    }
    delay(5);
  }
  return(med.getMedian());
  
}

double getReading2() {
  //===== change according to pin (A0 = PS1, A1 = PS2, etc) =====
  medno = 40;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
  {
    V1 = analogRead(A1);
    if(oldFiltered1 == -1) // sanity check for t=0
      oldFiltered1 = V1;
    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1; // get old value
//    if(i==10){
      med.add(getDist2(curFiltered1*0.0049));
      //if(getDist2(curFiltered0*0.0049) == 0)
       // return 0;
      //if(getDist2(curFiltered0*0.0049) == -1)
        //return -1;
      
//    }
    delay(5);
  }
  return(med.getMedian());
  
}

double getReading3() {
  //===== change according to pin (A0 = PS1, A1 = PS2, etc) =====
  medno = 30;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
  {
    V2 = analogRead(A2);
    if(oldFiltered2 == -1) // sanity check for t=0
      oldFiltered2 = V2;
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2; // get old value
//    if(i==10){
      med.add(getDist3(curFiltered2*0.0049));
      //if(getDist3(curFiltered0*0.0049) == 0)
      //return 0;
      //if(getDist3(curFiltered0*0.0049) == -1)
      //return -1;
      
//    }
    delay(5);
  }
  return(med.getMedian());
  
}

double getReading4() {
  //===== change according to pin (A0 = PS1, A1 = PS2, etc) =====
  medno = 30;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
  {
    V3 = analogRead(A3);
    if(oldFiltered3 == -1) // sanity check for t=0
      oldFiltered3 = V3;
    curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
    oldFiltered3 = curFiltered3; // get old value
//    if(i==10){
      med.add(getDist4(curFiltered3*0.0049));
      
//    }
    delay(5);
  }
  return(med.getMedian());
  
}

double getReading5() {
  //===== change according to pin (A0 = PS1, A1 = PS2, etc) =====
  medno = 30;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
  {
    V4 = analogRead(A4);
    if(oldFiltered4 == -1) // sanity check for t=0
      oldFiltered4 = V4;
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4; // get old value
//    if(i==10){
      med.add(getDist5(curFiltered4*0.0049));
      //if(getDist5(curFiltered0*0.0049) == 0)
      //return 0;
      //if(getDist5(curFiltered0*0.0049) == -1)
      //return -1;
      
//    }
    delay(5);
  }
  return(med.getMedian());
}

double getReading6() {
  //===== change according to pin (A0 = PS1, A1 = PS2, etc) =====
  medno = 30;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++)
  {
    V5 = analogRead(A5);
    if(oldFiltered5 == -1) // sanity check for t=0
      oldFiltered5 = V5;
    curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
    oldFiltered5 = curFiltered5; // get old value
//    if(i==10){
      med.add(getDist6(curFiltered5*0.0049));
      
//    }
    delay(5);
  }
  return(med.getMedian());
}

// Returns sensor 1 readings in units
double getDist1(double x){
 int temp = 29.117*pow(x,-1.083) -7.1;
  if(temp < 10)
 return 0; // obstacle is directly beside it
 if(temp > 60)
 return -1; // out of range)
 return temp/10;

}

// Returns sensor 2 readings in units
double getDist2(double x){
  
 int temp = 29.57*pow(x,-1.015) -8 +5;
 if(temp > 31)
 return -1; // out of range)
 
 return temp/10;
}

// Returns sensor 2 readingsin Cm
double ReadCm2(){
    medno = 30;
    RunningMedian med = RunningMedian(medno);
    V1 = analogRead(A1);
    for(int x = 0; x<30;x++)
    {
      if(oldFiltered1 == -1) // sanity check for t=0
      oldFiltered1 = V1;
    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1; // get old value
//    if(i==10){
      med.add((getCm2(curFiltered1*0.0049)));
    }
      //return round(getCm2(curFiltered1*0.0049));

      return round(med.getMedian());
}



double getCm2(double x)
{
  double temp = 29.57*pow(x,-1.015) -8;
  //double temp = 29.57*pow(x,-1.015);
  return round(temp); // 8 = offset
}




// Returns sensor 3 (FR) readings in units
double getDist3(double x){
 int temp = 33.359*pow(x,-1.159) -8;
 // 33.359*pow(x,-1.159)
 
 if(temp < 5)
 return 0; 
 if(temp > 31)
 return -1; // out of range)
 
 return temp/10;

}

// Returns sensor 3 readingsin Cm
double ReadCm3(){
    medno = 30;
    RunningMedian med = RunningMedian(medno);
    V2 = analogRead(A2);
    for(int x = 0; x<30;x++)
    {
      if(oldFiltered2 == -1) // sanity check for t=0
      oldFiltered2 = V2;
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2; // get old value
//    med.add((getCm2(curFiltered1*0.0049)));
      med.add(getCm3(curFiltered2*0.0049));
    }
      //return round(getCm2(curFiltered1*0.0049));

      return med.getMedian();
}

double getCm3(double x)
{
  double temp = 33.359*pow(x,-1.159) - 8;
  //double temp = 33.359*pow(x,-1.159);
  return temp; 
}


// Returns sensor 4 readings in units
double getDist4(double x){
 int temp = 26.423*pow(x,-1.267) +4 ;
 if(temp < 10)
 return 0; //0 obstacle is directly beside it
 if(temp > 40)
 return -1; // out of range)
 return temp/10;
}

// Returns sensor 5 readings in units
double getDist5(double x){
 int temp = 28.396*pow(x,-1.121) -8;
 //Serial.println(temp);
 //delay(50);
 if(temp > 31)
 return -1; // out of range)
 return temp/10;
}

// Returns sensor 5 readings in cm 
double getCm5(double x){
  double temp = 28.396*pow(x,-1.121) - 8;
  // 28.396*pow(x,-1.121);
  return temp;
}

double ReadCm5() {
  
  V4 = analogRead(A4);
  for(int x =0 ; x<30;x++)
  {
    if(oldFiltered4 == -1) // sanity check for t=0
    oldFiltered4 = V4;
  curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
  oldFiltered4 = curFiltered4; // get old value
  }
  
  return round(getCm5(curFiltered4*0.0049));
}



// Returns sensor 6 readings in units
double getDist6(double x){
 int temp = -6.1503*pow(x,4) + 25.403*pow(x,3) + 0.1423*pow(x,2) - 122.19*x + 169.76 ;
 temp = temp - 6;
 if(temp < 20)
 return 0; // obstacle is directly beside it
 if(temp > 60)
 return -1; // out of range)
 return temp/10;
}

double alpha = 0.1; // Smoothing Factor
double filter(double volt, double oldVal){
  return (alpha*volt) + (1-alpha)*oldVal;
}
