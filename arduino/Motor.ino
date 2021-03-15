// const double kp = 1.0, ki = 0.0, kd = 0.0;
//0.090 - 0.0909

const double kp = 0.0910, ki = 0.0, kd = 0.0;
//const double kp = 0.105, ki = 0.0, kd = 0.0;
//const double kp = 0.1, ki = 0.001, kd = 0.0;
//const double kp = 0.09, ki = 0.001, kd = 0.0;
//const double kp = 0.0, ki = 0.0, kd = 0.0;
//const double kp = 0.0808, ki = 0.001, kd = 0.0;
//const double kp = 0, ki = 0, kd = 0;
//0.25 to 0.5

// setMotor(Right,Left)
// FRONT IS SWITCH
// REMEMBER TO TURN ON BATTERY
// BEFORE PLUGGING IN TO LAPTOP
// IF IT GOES LEFT
// LOWER KP
// IF IT GOES RIGHT
// RAISE KP
// IF IT WOBBLES
// WAY TOO HIGH PID
// NINE GRID IS MAX LENGTH OF CABLE

double PID_RPM_R = 0;
double tick_R = 0;                                // To Keep Track of the Number of Ticks for Right Motor 
double tick_L = 0;                                // To Keep Track of the Number of Ticks for Left Motor 
double RPM_L = 0;                                 // To Store RPM of Left Motor 
double RPM_R = 0;                                 // To Store RPM of Right Motor 
PID myPID(&RPM_R, &PID_RPM_R, &RPM_L, kp, ki, kd, DIRECT);

void setupPID(){
  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(-350, 350);
  myPID.SetSampleTime(10);
  
  Serial.println("Deployed:");
  Serial.print("kp:");
  Serial.println(kp);
  Serial.print("ki:");
  Serial.println(ki);
  Serial.print("kd:");
  Serial.println(kd);
  Serial.println("===========");
}


void moveF(double MoveDist) {
  PID_RPM_R = 0;
  
  int Ticks = getTicks(MoveDist);
  
  initMove();
  initStart();
  int speed_R = 272; 
  motor.setSpeeds(300, 272);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    speed_R += (1)*PID_RPM_R;
//    if(debug){
//      Serial.print(tick_L);
//      Serial.print(" ");
//      Serial.println(tick_R);
//    }
    motor.setSpeeds(300, speed_R);//speed_R
  }
  initEnd(); 
  delay(10);                                                 
}

void moveR() {
  PID_RPM_R = 0;
  //76 to 78
  //75.75
  int Ticks = getDegreeTicks(79.1); //81.5 for speed 350, for robot to turn right 90
  initMove();
  initStart();
  int speed_R = -273;
  motor.setSpeeds(300, -273);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
//    Serial.print(tick_L);
//    Serial.print(" ");
//    Serial.println(tick_R);
    speed_R += (-1)*PID_RPM_R;
    motor.setSpeeds(300, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}

void moveReverse(){
  PID_RPM_R = 0;
  //76 to 78
  //75.75
  int Ticks = getDegreeTicks(164); //81.5 for speed 350, for robot to turn right 90
  initMove();
  initStart();
  int speed_R = -290;
  motor.setSpeeds(300, speed_R);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
//    Serial.print(tick_L);
//    Serial.print(" ");
//    Serial.println(tick_R);
    speed_R += (-1)*PID_RPM_R;
    motor.setSpeeds(300, speed_R);
  }
  initEnd(); 
  delay(10);   
  
  
}

//383 416
//380 410
// 382 398
void moveL() {
  PID_RPM_R = 0;
  
  //79
//  int Ticks = getDegreeTicks(79.0); //82.5?? for speed 350, for robot to turn 90 left
  //75 to 77
//  int Ticks = getDegreeTicks(76.0); 
//  int Ticks = getDegreeTicks(77.75); 
//78.5
  int Ticks = getDegreeTicks(79.5); 
  // 78.6
  initMove();
  initStart();
  int speed_R = 288;
//  int sp0eed_R = 300;
  motor.setSpeeds(-300, speed_R);
//  Serial.println(Ticks);
      // 9,L,9
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
//    Serial.print(tick_L);
//    Serial.print(" ");
//    Serial.println(tick_R);
    speed_R += PID_RPM_R;
    motor.setSpeeds(-300, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}

void moveL45(float degree) { //80.5 for a 90 degree turn
  PID_RPM_R = 0;
  int speed_R = 275;
  int Ticks = getDegreeTicks(degree);
  
  initMove();
  initStart();
  motor.setSpeeds(-300, 274);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += PID_RPM_R;
//    Serial.print(tick_L);
//    Serial.print(" ");
//    Serial.println(tick_R);
    motor.setSpeeds(-300, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}

void moveR45(float degree) { //80.5 for a 90 degree turn
  PID_RPM_R = 0;
  int speed_R = -289;
  int Ticks = getDegreeTicks(degree);
  
  initMove();
  initStart();
  motor.setSpeeds(300, -289);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += (-1)* PID_RPM_R;
    motor.setSpeeds(300, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}

void moveB(double MoveDist) {
  PID_RPM_R = 0;
  int speed_R = -350;
  int Ticks = getTicks(MoveDist);
  
  initMove();
  initStart();
  motor.setSpeeds(-330, -350);
   
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += (-1) *PID_RPM_R;
    motor.setSpeeds(-330,speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}

void moveBB(double MoveDist) {
  PID_RPM_R = 0;
  int speed_R = -100;
  int Ticks = getTicks(MoveDist);
  
  initMove();
  initStart();
  motor.setSpeeds(-350, -15);
   
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += (-1) *PID_RPM_R;
    motor.setSpeeds(-110,speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}




void moveLCust(double k) {
  PID_RPM_R = 0;
  int speed_R = 350;
  int Ticks = getDegreeTicks(k); //82.5?? for speed 350, for robot to turn 90 lef
  
  initMove();
  initStart();
  motor.setSpeeds(-350, 350);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += PID_RPM_R;
    motor.setSpeeds(-350, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}



void moveRF() {
  PID_RPM_R = 0;
  int speed_R = -350;
  int Ticks = getDegreeTicks(79.7); //81.5 for speed 350, for robot to turn right 90
  
  initMove();
  initStart();
  motor.setSpeeds(350, -350);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += (-1)*PID_RPM_R;
    motor.setSpeeds(350, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}


void moveFF(double MoveDist) {
  PID_RPM_R = 0;
  int speed_R = 100; 
  int Ticks = getTicks(MoveDist);
  
  initMove();
  initStart();
  motor.setSpeeds(100, 110);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    speed_R += (1)*PID_RPM_R;
    
    motor.setSpeeds(100, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}


void moveL45F(float degree) { //80.5 for a 90 degree turn
  PID_RPM_R = 0;
  int speed_R = -350;
  int Ticks = getDegreeTicks(degree);
  
  initMove();
  initStart();
  motor.setSpeeds(-350, 350);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += PID_RPM_R;
    motor.setSpeeds(-350, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}




void moveR45F(float degree) { //80.5 for a 90 degree turn
  PID_RPM_R = 0;
  int speed_R = -350;
  int Ticks = getDegreeTicks(degree);
  
  initMove();
  initStart();
  motor.setSpeeds(350, -350);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    
    speed_R += (-1)* PID_RPM_R;
    motor.setSpeeds(350, speed_R);
  }
  initEnd(); 
  delay(10);                                                 
}

void setupMotorEncoder() {
  motor.init();

  /**
   * PIN 3 : Right Motor
   * PIN 11 : Left Motor
   */
  pinMode(3, INPUT);
  pinMode(11, INPUT);                              

  enableInterrupt(3, leftMotorTime, RISING);
  enableInterrupt(11, rightMotorTime, RISING);
}

/**
 * calculate ticks for LEFT motor
 */
void leftMotorTime() {                            
  tick_L++;
}

/**
 * calculate ticks for RIGHT motor
 */
void rightMotorTime() {                           
  tick_R++;
}

void initMove() {                             
  tick_R = 0;                                    
  tick_L = 0;                                
  RPM_L = 0;
  RPM_R = 0;
}

void initStart() {                  
  motor.setSpeeds(0, 0);
  motor.setBrakes(0, 0);
}

void initEnd() {                     
  motor.setSpeeds(0, 0);
  motor.setBrakes(400, 400);
  delay(20);
}

int getDegreeTicks(float degree){                // Function return ticks required for specified degree
  return ceil(degree * 4.68);
}

int getTicks(double cm){                        // Function return ticks required for specified distance
  return ceil(cm * 29.8);
}

void GetRPM(){                  
   float duration_L = pulseIn(3, HIGH); // pulseIn returns length of pulse in microseconds
   float duration_R = pulseIn(11, HIGH);

  if(isinf(duration_L) || isinf(duration_R)){
    Serial.println("Infinity in GetRPM()");
    return;
  }

  if((duration_L) == 0 || (duration_R) == 0){
    Serial.print(duration_L);
    Serial.print(" ");
    Serial.print(duration_R);
    Serial.println(" Zero in GetRPM()");
    return;
  }

   duration_L = duration_L * 2 ;            
   duration_L = duration_L/1000000; //converts length of pulse to seconds                   
   duration_R = duration_R * 2 ;            
   duration_R = duration_R/1000000;       
                
   RPM_L = ((1/duration_L)/(562/60));  //converts length of pulse to mins
   RPM_R = ((1/duration_R)/(562/60)); // formula for rpm = (counts/min) / (counts/rev)
} 
void moveFB(double MoveDist) {
  PID_RPM_R = 0;
  int speed_R = 300; 
  int Ticks = getTicks(MoveDist);
  
  initMove();
  initStart();
  motor.setSpeeds(300, 300);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    speed_R += (1)*PID_RPM_R;
    
    motor.setSpeeds(300, speed_R);
  }
  delay(10);                                                 
}
void moveFB2(double MoveDist) {
  int speed_R = 300; 
  int Ticks = getTicks(MoveDist);
  
  initMove();
  initStart();
  motor.setSpeeds(300, 300);
      
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();

    speed_R += (1)*PID_RPM_R;
    
    motor.setSpeeds(300, speed_R);
  }
  initEnd();
  delay(10);                                                 
}
