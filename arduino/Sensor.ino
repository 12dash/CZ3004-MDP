#include <ZSharpIR.h>
#include <RunningMedian.h>
#include "Math.h"

ZSharpIR SFL_S = ZSharpIR(A0, 1080);
ZSharpIR SR1_S = ZSharpIR(A1, 1080);
ZSharpIR SR2_S = ZSharpIR(A2, 1080);
ZSharpIR SFR_S = ZSharpIR(A4, 1080);
ZSharpIR SL_S = ZSharpIR(A3, 20150);
ZSharpIR SFM_S= ZSharpIR(A5,1080);


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
     // med.add(ReadCm5());
//      return ReadCm5();
    }
  }
  return (med.getMedian());
}

double RightFrontReading(){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
      //med.add(ReadCm5());
  }
  return(med.getMedian());
}

double RightRearReading(){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
      //med.add(ReadCm5());
  }
  return(med.getMedian());
}

double FrontRightReading(char leftBlock){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
    if(leftBlock=='R'){
//      med.add(ReadCm3());
//      return ReadCm5();
    }else if(leftBlock == 'C'){
//      return ReadCm5();
     // med.add(ReadCm5());
    }
  }
  return(med.getMedian());
}

double FrontFrontReading(){
  medno = 10;
  RunningMedian med = RunningMedian(medno);
  for(int i = 0;i<medno;i++){
      //med.add(ReadCm5());
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


int getReading2()
{
  return 0;
}

int getReading3()
{
  return 0;
}

int getReading5()
{
  return 0;
}


double ReadCm2(){
  }



double getCm2(double x)
{

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




// Returns sensor 3 readingsin Cm
double ReadCm3(double old2){
  medno = 50;
  RunningMedian med = RunningMedian(medno); 
  double V2;
  double oldFiltered = old2; double curFiltered2;
    for(int x =0;x<medno;x++)
    {
    V2 = analogRead(A2);
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;
    med.add(getCm3(curFiltered2 * 0.0049));
    //Serial.println(getCm3(curFiltered2 * 0.0049));
    }
    return (med.getMedian()) ;
}

double getCm3(double x)
{
  double temp = 30.442*pow(x,-1.076) -6 ;
  return round(temp);
}


// Returns sensor 5 readings in cm 
double ReadCm5(double old0){
  medno = 50;
  RunningMedian med = RunningMedian(medno); 
  double V0;
  double oldFiltered = old0; double curFiltered0;
    for(int x =0;x<medno;x++)
    {
    V0 = analogRead(A0);
    curFiltered0 = filter(V0, oldFiltered0); // Exponential filter
    oldFiltered0 = curFiltered0;
    med.add(getCm5(curFiltered0 * 0.0049));
    //Serial.println(getCm5(curFiltered0 * 0.0049));
    }
    return (med.getMedian()) ;
}

double getCm5(double x) {
  double temp = 27.537*pow(x,-0.976) -6 ;
  return round(temp);
}

double ReadCm6(double old4){
  medno = 50;
  RunningMedian med = RunningMedian(medno); 
  double V4;
  double oldFiltered = old4; double curFiltered4;
    for(int x =0;x<medno;x++)
    {
    V4 = analogRead(A4);
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;
    med.add(getCm6(curFiltered4 * 0.0049));
    Serial.println(getCm6(curFiltered4 * 0.0049));
    }
    return (med.getMedian()) ;
}

double getCm6(double x) {
  double temp = 31.130*pow(x,-1.046) -6;
  return round(temp);
}




// Returns sensor 1 readings in units
double getDist1(double x){
 double temp = 30.027*pow(x,-1.017) ;
 int temp1 = temp;
 //Serial.println(temp1);
 if(temp1 > 40)
 return -1; // out of range)
 return temp1/10;

}

// Returns sensor 2 readings in units
double getDist2(double x){
 double temp = 5.5679*pow(x,4)-53.073*pow(x,3)+ 189.98*pow(x,2) - 319.54*pow(x,1) + 248.95 -1;
 //double temp = 71.086*pow(x,-1.127) -1;
 int temp1 = temp;
 //Serial.println(temp1);
 if(temp1 <= 20)
 return 0;
 if(temp1 > 80)
 return -1; // out of range)
 return temp1/10;
}

// Returns sensor 3 (FR) readings in units
double getDist3(double x){
 double temp = 30.442*pow(x,-1.076) -6 ;
 int temp1 = temp;
 if(temp1 > 40)
 return -1; // out of range)
 
 return temp1/10;

}


// Returns sensor 4 readings in units
double getDist4(double x){
 double temp = 32.022*pow(x,-1.052) - 5;
 int temp1 = temp;
 if(temp1 == 28 || temp1 ==29)
 temp1 = temp1 + 2;
 //Serial.println(temp1);
 if(temp1 >= 37)
 return -1; // out of range)
 return temp1/10;
}

// Returns sensor 5 readings in units
double getDist5(double x){
 double temp = 27.537*pow(x,-0.976) -4 ;
 int temp1 = temp;
 if(temp1 == 28 || temp1 == 29 || temp1 == 27)
 temp1 = temp1 + 3;
 //Serial.println(temp1);
 if(temp1 >= 35)
 return -1; // out of range)
 return temp1/10;
}


// Returns sensor 6 readings in units
double getDist6(double x){
 int temp = 31.130*pow(x,-1.046) -4;
 int temp1 = temp;
 if(temp1 == 29)
 temp1 = temp1 + 2;
 //Serial.println(temp1);
 if(temp > 38)
 return -1; // out of range)
 return temp1/10;
}

double alpha = 0.1; // Smoothing Factor
double filter(double volt, double oldVal){
  return (alpha*volt) + (1-alpha)*oldVal;
}
