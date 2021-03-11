 /**
 * Done by: Group 2 of 19/20 Sem 2
 * Peter Vickram & Tay Kee Kong
 * For more info check out our wiki
 * 
 */


// (Reading - 8) / 10
// Offset 7.1 for now due to tilting



  
// Libraries used                                                                                                                                                                                                                                                                                                                                                                                                                                                #include "DualVNH5019MotorShield.h"
#include <EnableInterrupt.h>
#include <PID_v1.h> 
#include "DualVNH5019MotorShield.h"
#include <math.h>
#include <string.h>
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

void setup()
{
  /* Hardware setup */
  Serial.begin(115200);
  //Serial.println("Dual VNH5019 Motor Shield");
  setupMotorEncoder();
  setupPID();
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
    String stringOne = "pc|" + String(getReading2()) + ';' + String(getReading5()) + ';' + String(getReading3()) + ';' + String(getReading4()) + ';' + String(getReading6()) + ';' + String(getReading1()) + ';' ; 
   Serial.println(stringOne);

//  Serial.flush();
//  delay(10);
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
      ,110.8, 121.7, 132.8, 144.1, 155.5, 167.1, 178.8, 190.6, 214.7}; 
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
        printIRReading();
      }
      
      if (moveblock > -1){
        double movedist = setdistance[moveblock];
        moveF(movedist);
      }

        //delay(200);<<<<<<<<<<<<<<<<<
        
      switch(arguments){

         // Take in sensor reading from starting position right when exploration starts
        case 'E': printIRReading();
                  break;
         //rotate 180
        case 'I': moveReverse();
                  printIRReading();
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
                  printIRReading();
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
                  printIRReading();
                  break;
                  
        // turn 90 right
        case 'R': moveR(); 
                  delay(delayconst); 
                  printIRReading();
                  break;
                  
        // turn 90 right Fastest Path
        case 'U': moveRF(); 
                  delay(delayconst); 
                  printIRReading();
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
                  printIRReading();
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
                  printIRReading();
                  break;
                  
                  
        // right hug calibrate
       case 'A': moveR(); 
                  delay(delayconst);
                  caliFront();
                  delay(delayconst);
                  printIRReading();
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
                  printIRReading();
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
                  printIRReading();
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
                  printIRReading();
                  break;

        case 'Z':  
        
                  moveFB(movconst);
                  delay(10);
                  printIRReading();
                  moveFB2(5.15);
                   if(SFR_IR()==1 && SFL_IR()==1)
                          caliFront();
                  delay(10);
                  printIRReading();
                  break;
                
        
        default: break;
        
      } // end switch 
} // end main loop

// FL: -1, FC: -1, FR: -1, LT: -1, LB: -1, LR: -1
// Frontleft, frontcenter, frontright, lefttop, leftbottom, leftrear

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
