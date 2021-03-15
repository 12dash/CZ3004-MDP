 /**
 * Done by: Group 2 of 19/20 Sem 2
 * Peter Vickram & Tay Kee Kong
 * For more info check out our wiki
 * 
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
double movconst = 9.6;
double delayconst = 0;
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




void printIRReading1(double cur0, double cur1, double cur2, double cur3, double cur4, double cur5)
{
  //Serial.print("Sensor 1 "); // DOne
  //Serial.println(Sensor1(cur5));
  
  //Serial.print("Sensor 2 "); // Done
  //Serial.println(Sensor2(cur1)); 
  
  //Serial.print("Sensor 4 "); // Recalibrate aka spoilt
  //Serial.println(Sensor4(cur3));
  
  //Serial.print("Sensor 5 "); //Done
  //Serial.println(Sensor5(cur0));

  //Serial.print("Sensor 3 "); // Done
  //Serial.println(Sensor3(cur2));
  
  //Serial.print("Sensor 6 "); // Done
  //Serial.println(Sensor6(cur4));

  
  //FL > FC > FR > LC > LL > RR
  String stringOne = "pc|" + String(Sensor5(cur0)) + ';' + String(Sensor3(cur2)) + ';' + String(Sensor6(cur4)) + ';' + String(Sensor2(cur1)) + ';' + String(Sensor4(cur3)) + ';' + String(Sensor1(cur5)) + ';' ;
  Serial.println(stringOne);
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


void caliFront()
{
  
}

void testcaliFront(){
  double left = getReading2();
  double right = getReading3();
  if(left==1.0 && right==1.0){
    caliFront('L','R');
  }else if((left==1.0 ^ right==1.0) && getReading5()==1.0){
    if(left == 1.0){
      caliFront('L','M');
    }else{
      caliFront('M','R');
    }
  }else{
    Serial.println("Block not detected");  
  }
  delay(500);
}  


void loop()
{
    double V0 = analogRead(A0); // Read voltage
    double V1 = analogRead(A1);
    double V2 = analogRead(A2);
    double V3 = analogRead(A3);
    double V4 = analogRead(A4);
    double V5 = analogRead(A5); 
    double oldFiltered0 =-1; double curFiltered0;
    double oldFiltered1 =-1; double curFiltered1;
    double oldFiltered2 =-1; double curFiltered2;
    double oldFiltered3 =-1; double curFiltered3;
    double oldFiltered4 =-1; double curFiltered4;
    double oldFiltered5 =-1; double curFiltered5;

    if(oldFiltered0 == -1) // sanity check for t=0
      oldFiltered0 = V0;
    curFiltered0 = filter(V0, oldFiltered0); // Exponential filter
    oldFiltered0 = curFiltered0;

    if(oldFiltered1 == -1) // sanity check for t=0
      oldFiltered1 = V1;
    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1;

    if(oldFiltered2 == -1) // sanity check for t=0
      oldFiltered2 = V2;
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;

    if(oldFiltered3 == -1) // sanity check for t=0
      oldFiltered3 = V3;
    curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
    oldFiltered3 = curFiltered3;

    if(oldFiltered4 == -1) // sanity check for t=0
      oldFiltered4 = V4;
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;

    if(oldFiltered5 == -1) // sanity check for t=0
      oldFiltered5 = V5;
    curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
    oldFiltered5 = curFiltered5;
    
   //main code
   //Pre-run calibration

   //Serial.println("New");
   //printIRReading();
   //delay(500);
   while (test == 1 ) {
     Serial.flush();
      
      delay(10);
       moveL(); //9 for 1 grid movement
       delay(10);
       moveL();

       delay(10);
//       caliFront();    //calibration
       delay(100);
       moveR();
       delay(10);
//       caliFront();    //calibration
       delay(100);
       moveR();
       test = 0;
       
        
  }
//  delay(3000);
    
   double setdistance [20] = {9.6, 19.1, 29.1, 37.8, 48.1, 58.1, 68.6, 79.2, 89.2, 99.2 //}
      ,108.8, 118.8, 128.8, 138.8, 148.8, 158.8, 168.8, 178.8, 188.8}; 
   // custom distance for fastest path {9.65, 19.4, 29.4, 38.6, 48.5, 58.5, 69.2, 79.2, 89.6, 99.6} 
   // 10 onwards unchecked

      arguments = Serial.read();
      if(arguments == '\0'){
        return;
      }

      int moveblock = -1;

      switch(arguments){        
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
      
      if (arguments>= '0' && arguments <= '9' ){
        moveblock = int(arguments - '0');
      }
      
      if (moveblock > -1){
        double movedist = setdistance[moveblock];
        moveF(movedist);
        
        printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
        
               }

        //delay(200);<<<<<<<<<<<<<<<<<
        
      switch(arguments){
         // Take in sensor reading from starting position right when exploration starts
        case 'E': 
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  //test123(curFiltered4);
                  break;
         //rotate 180
        case 'I': moveReverse();
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break; 
                  
        // 1 grid forward       
        /*case 'F': moveF(movconst);
                  caliRight();
                  delay(10);
                  if(SFR_IR()==1 && SFL_IR()==1)
                          caliFront();
                  //delay(10);
                  printIRReading();
                //Serial.println("R");
                  break;*/

         case 'F': moveF(movconst);
                  delay(delayconst);
                  if(SFR_IR()==1 && SFL_IR()==1)
                          caliFront();
                  //delay(10);
                  
                //Serial.println("R");
                  
                  break;         

         case 'H': caliFront();
                   break;

         // 1 grid forward       
  /*      case 'F': moveF(movconst);
                  delay(100);
                  if(SFR_IR()==1 && SFL_IR()==1)
                          caliFront();
                  else
                  {
                    if(fcor==3)
                    {
                      moveF(3);
                      fcor=0;
                    }
                    else
                      fcor++;
                  }
                  delay(100);
                  printIRReading();
                //Serial.println("R");
                  break;
*/
                  
                  
        // turn 90 left
        case 'L': moveL(); 
                  delay(delayconst);           
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break;
                  
        // turn 90 right
        case 'R': moveR(); 
                  delay(delayconst); 
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break;
                  
        // turn 90 right Fastest Path
        case 'U': moveRF(); 
                  delay(delayconst); 
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break;

        // turn degree left
        case'X' : degree= Serial.read();
                  degreeconst = (int)degree* 10;
                  //Serial.flush();
                  degree = Serial.read();
                  degreeconst+= (int)degree;
                  degreeconst = ((degreeconst*4.1236)/ 4.68);
                  moveL45F(degreeconst);
                  delay(delayconst);
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break;

         // turn degree right
        case'Y' : degree= Serial.read();
                  degreeconst = (int)degree* 10;
                  //Serial.flush();
                  degree = Serial.read();
                  degreeconst+= (int)degree;
                  degreeconst = ((degreeconst*4.1548)/ 4.68);
                  moveR45F(degreeconst);
                  delay(delayconst);
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break;
                  
                  
        // right hug calibrate
       case 'A': moveR(); 
                  delay(delayconst);
                  caliFront();
                  delay(delayconst);
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  //delay(10);
                  moveL();
                  delay(20);
                  //Serial.println("PC,AR,OK");  
                  break;

    /*  case 'A': caliRight();
       delay(10);
                  //Serial.println("PC,AR,OK");  
            */      break;
       
      

        // left hug calibrate
        case 'B': moveL(); 

                  delay(delayconst);
                  caliFront();
                  delay(delayconst);
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  //delay(10);
                  moveR();
                  delay(20);
                  //Serial.println("PC,AR,OK");  
                  break;

        //CaliRight
        case 'T'://moveF(movconst);
                  delay(delayconst);
                  if(SFR_IR()==1 && SFL_IR()==1)
                          caliFront();
                  else 
                          caliRight();
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break; 
        
        // end exploration calibration          
        case 'C': delay(delayconst);
                  moveL();
                  delay(delayconst);
                  moveL();
                  delay(delayconst);
                  caliFront();    
                  //delay(10);
                  moveR();
                  delay(delayconst);
                  caliFront();    
                  //delay(10);
                  moveR();
                  break;

        // 2-grid forward movement          
        case 'D': moveFB(movconst);
                  delay(10);
                  printIRReading();
                  moveFB2(5.15);
                  delay(10);
                   if(SFR_IR()==1 && SFL_IR()==1)
                          caliFront();
                  else 
                          caliRight();
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break;

        case 'Z':  
        
                  moveFB(movconst);
                  delay(10);
                  printIRReading();
                  moveFB2(5.15);
                   if(SFR_IR()==1 && SFL_IR()==1)
                          caliFront();
                  delay(10);
                  printIRReading1(curFiltered0,curFiltered1,curFiltered2,curFiltered3,curFiltered4,curFiltered5);
                  break;
                
        
        default: break;
        
      } // end switch 
} // end main loop



void loop7(){

  delay(5000);
  
  bool left = false;
  bool center = false;
  bool right = false;
  
  int blocksAwayLeft = (int)getReading3();
  int blocksAwayCenter = (int)getReading5();
  int blocksAwayRight = (int)getReading2();

  double degreeconst = 60;
  degreeconst= 0.9456919 * degreeconst + 1.375979;
  
  int blocksAway = 3;
  if(!(blocksAwayLeft > -1 && blocksAwayCenter > -1 && blocksAwayRight > -1)){
      blocksAway = 20;
      if(blocksAwayLeft != -1 && blocksAwayLeft < blocksAway){
        blocksAway = blocksAwayLeft;
        left = true;
      }
      if(blocksAwayCenter != -1 && blocksAwayCenter < blocksAway){
        blocksAway = blocksAwayCenter;
        center = true;
      }
      if(blocksAwayRight != -1 && blocksAwayRight < blocksAway){
        blocksAway = blocksAwayRight;
        right = true;
      }
  }

  double setdistance [10] = {9.6, 19.1, 29.1, 37.8, 48.1, 58.1, 68.6, 79.2, 89.2, 99.2}; // custom distance for fastest path {9.65, 19.4, 29.4, 38.6, 48.5, 58.5, 69.2, 79.2, 89.6, 99.6}
  if(blocksAway > 10){
//    moveF(setdistance[9]);
    blocksAway = blocksAway - 10;
  }
  if(blocksAway-3 < 0){
    blocksAway = 3;
  }
  moveF(setdistance[blocksAway-3]-4.8);
  if(!(right || left || center)){
    return;
  }
  // LRRL - Left/Center
  // RLLR - Right
  
  if(right || center){
    moveL45(45);
  }else{
    moveR45(45);
  }
  
  moveF(setdistance[2+2]);
  if(right || center){
        moveR45(90);
  }else{
        moveL45(90);
  }
  moveF(setdistance[2+2]);
  if(right || center){
        moveL45(45);
  }else{
        moveR45(45);
  }
  moveF(setdistance[3]);
  delay(50000);
}

void loop6(){

  delay(5000);
  
  bool left = false;
  bool center = false;
  bool right = false;
  
  int blocksAwayLeft = (int)getReading3();
  int blocksAwayCenter = (int)getReading5();
  int blocksAwayRight = (int)getReading2();
  
  int blocksAway = 3;
  if(!(blocksAwayLeft > -1 && blocksAwayCenter > -1 && blocksAwayRight > -1)){
      blocksAway = 20;
      if(blocksAwayLeft != -1 && blocksAwayLeft < blocksAway){
        blocksAway = blocksAwayLeft;
        left = true;
      }
      if(blocksAwayCenter != -1 && blocksAwayCenter < blocksAway){
        blocksAway = blocksAwayCenter;
        center = true;
      }
      if(blocksAwayRight != -1 && blocksAwayRight < blocksAway){
        blocksAway = blocksAwayRight;
        right = true;
      }
  }

  double setdistance [10] = {9.6, 19.1, 29.1, 37.8, 48.1, 58.1, 68.6, 79.2, 89.2, 99.2}; // custom distance for fastest path {9.65, 19.4, 29.4, 38.6, 48.5, 58.5, 69.2, 79.2, 89.6, 99.6}
  if(blocksAway > 10){
//    moveF(setdistance[9]);
    blocksAway = blocksAway - 10;
  }
  moveF(setdistance[blocksAway-2]-4.8);
  if(!(right || left || center)){
    return;
  }
  // LRRL - Left/Center
  // RLLR - Right
  
  if(right || center){
    moveL();
  }else{
    moveR();
  }
  
  moveF(setdistance[1]);
  if(right || center){
        moveR();
  }else{
        moveL();
  }
  moveF(setdistance[2+2]);
  if(right || center){
        moveR();
  }else{
        moveL();
  }
  moveF(setdistance[1]);
  if(right || center){
        moveL();
  }else{
        moveR();
  }
  moveF(setdistance[3]);
  delay(50000);
}








































//int printIRReading2(double old4)
//{
//  return filter4(old4);
//}
//
//int printIRReadingM(double cur4)
//{
//  //Serial.print("Sensor 6 "); // Done
//  return(getDist6(cur4 * 0.0049));
//}
//
//int filter4(double oldFiltered4) 
//{   
//      double curFiltered4;
//      medno = 30;
//      RunningMedian med = RunningMedian(medno); 
//       
//      for(int i = 0;i<30;i++)
//      {
//       double V4 = analogRead(A4);
//      if(oldFiltered4 == -1) // sanity check for t=0
//      oldFiltered4 = V4;
//    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
//    oldFiltered4 = curFiltered4;
//    med.add(printIRReadingM(curFiltered4));
//    Serial.println(printIRReadingM(curFiltered4));
//      }
//    return med.getMedian();
//}


int Sensor6(double old4) 
{       
    medno = 50;
    double V4;
    RunningMedian med = RunningMedian(medno); 
    double oldFiltered4 =old4; double curFiltered4;
    for(int x =0;x<medno;x++)
    {
    V4 = analogRead(A4);
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;
    med.add(getDist6(curFiltered4 * 0.0049));
    //Serial.println(getDist6(curFiltered4*0.0049));
    }
    return (med.getMedian()) ;

}

int Sensor5(double old0) 
{       
    medno = 50;
    double V0;
    RunningMedian med = RunningMedian(medno); 
    double oldFiltered0 =old0; double curFiltered0;
    for(int x =0;x<medno;x++)
    {
    V0 = analogRead(A0);
    curFiltered0 = filter(V0, oldFiltered0); // Exponential filter
    oldFiltered0 = curFiltered0;
    med.add(getDist5(curFiltered0 * 0.0049));
    //Serial.println(getDist5(curFiltered0*0.0049));
    }
    return (med.getMedian()) ;

}

int Sensor3(double old2) 
{       
    medno = 50;
    double V2;
    RunningMedian med = RunningMedian(medno); 
    double oldFiltered2 =old2; double curFiltered2;
    for(int x =0;x<medno;x++)
    {
    V2 = analogRead(A2);
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;
    med.add(getDist4(curFiltered2 * 0.0049));
    //Serial.println(getDist4(curFiltered2*0.0049));
    }
    return (med.getMedian()) ;

}

int Sensor1(double old5) 
{       
    medno = 50;
    double V5;
    RunningMedian med = RunningMedian(medno); 
    double oldFiltered5 =old5; double curFiltered5;
    for(int x =0;x<medno;x++)
    {
    V5 = analogRead(A5);
    curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
    oldFiltered5 = curFiltered5;
    med.add(getDist1(curFiltered5 * 0.0049));
    //Serial.println(getDist1(curFiltered5*0.0049));
    }
    return (med.getMedian()) ;

}

int Sensor2(double old1) 
{       
    medno = 50;
    double V1;
    RunningMedian med = RunningMedian(medno); 
    double oldFiltered1 =old1; double curFiltered1;
    for(int x =0;x<medno;x++)
    {
    V1 = analogRead(A1);
    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1;
    med.add(getDist2(curFiltered1 * 0.0049));
    //Serial.println(getDist2(curFiltered1*0.0049));
    }
    return (med.getMedian()) ;

}

int Sensor4(double old3) 
{       
    medno = 50;
    double V3;
    RunningMedian med = RunningMedian(medno); 
    double oldFiltered3 = old3; double curFiltered3;
    for(int x =0;x<medno;x++)
    {
    V3 = analogRead(A3);
    curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
    oldFiltered3 = curFiltered3;
    med.add(getDist3(curFiltered3 * 0.0049));
    //Serial.println(getDist3(curFiltered3*0.0049));
    }
    return (med.getMedian()) ;

}






















void printIRReading() {

  // FL > FC > FR > LC > LL > RR
  //Serial.print("RR");
  //Serial.println(getReading1());
  
  //Serial.print("FL");
  //Serial.println(getReading2());
   
  //Serial.print("FC");
  //Serial.println(getReading5());

  //Serial.print("FR");  
  //Serial.println(getReading3());

  //Serial.print("LC");
  //Serial.println(getReading4());

  //Serial.print("LL");  
  //Serial.println(getReading6());


  // 2,5,3,4,6,1
//    String stringOne = "pc|" + String(getReading2()) + ';' + String(getReading5()) + ';' + String(getReading3()) + ';' + String(getReading4()) + ';' + String(getReading6()) + ';' + String(getReading1()) + ';' ; 
//   Serial.println(stringOne);

//  Serial.flush();
//  delay(10);
}
