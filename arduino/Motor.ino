bool pid_on = true;
bool debug_motor = false;
//
//double kp = 0.0, ki = 0.0, kd = 0.00;
//double kp_RIGHT = 0.0 , ki_RIGHT = 0.0, kd_RIGHT = 0.00;
//double kp_LEFT = 0.0, ki_LEFT = 0.0, kd_LEFT = 0.000;
//
double kp = 2.0, ki = 0.0, kd = 0.001;
double kp_RIGHT = 2.0 , ki_RIGHT = 0.0, kd_RIGHT = 0.001;
double kp_LEFT = 2.0, ki_LEFT = 0.0, kd_LEFT = 0.001;


double PID_RPM_R = 0;
double tick_R = 0;                                // To Keep Track of the Number of Ticks for Right Motor
double tick_L = 0;                                // To Keep Track of the Number of Ticks for Left Motor
double RPM_L = 0;                                 // To Store RPM of Left Motor
double RPM_R = 0;                                 // To Store RPM of Right Motor

double speed_L = 290;

PID myPID(&RPM_R, &PID_RPM_R, &RPM_L, kp, ki, kd, DIRECT);
PID rightPID(&RPM_R, &PID_RPM_R, &RPM_L, kp_RIGHT, ki_RIGHT, kd_RIGHT, DIRECT);
PID leftPID(&RPM_R, &PID_RPM_R, &RPM_L, kp_LEFT, ki_LEFT, kd_LEFT, DIRECT);

void setupPID() {
  double sample_time = 1;

  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(-350, 350);
  myPID.SetSampleTime(sample_time);

  rightPID.SetMode(AUTOMATIC);
  rightPID.SetOutputLimits(-350, 350);
  rightPID.SetSampleTime(sample_time);

  leftPID.SetMode(AUTOMATIC);
  leftPID.SetOutputLimits(-350, 350);
  leftPID.SetSampleTime(sample_time);

  Serial.println("Deployed:");
  Serial.print("kp:");
  Serial.println(kp);
  Serial.print("ki:");
  Serial.println(ki);
  Serial.print("kd:");
  Serial.println(kd);
  Serial.println("===========");
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

int getDegreeTicks(float degree) {               // Function return ticks required for specified degree
  double diameter_wheel = 10;
  double constant_conversion = 3.14 * (diameter_wheel) * (degree / 360);
  return ceil(degree * 4.68);
}

void debug_print_ticks(int tick_l, int tick_r, int pid_rpm_r, int speed_r) {
  Serial.print(RPM_L);
  Serial.print(" ");
  Serial.print(RPM_R);
  Serial.print(" ");
  Serial.print("PID RPM R : ");
  Serial.print(pid_rpm_r);
  Serial.print(" Tick Left : ");
  Serial.print(tick_l);
  Serial.print(" Tick Right : ");
  Serial.print(tick_r);
  Serial.print(" Speed Right wheel : ");
  Serial.println(speed_r);
  return;
}

void debug_graph_ticks(int tick_l, int tick_r, int pid_rpm_r, int speed_r) {

  Serial.print(tick_l);
  Serial.print(" ");
  Serial.print(tick_r);
  Serial.print(" ");
  Serial.print(pid_rpm_r);
  Serial.print(" ");
  Serial.print(speed_r);
}

double get_avg(int speed_r, int Ticks) {
  double error_temp = 0;
  double error = 0;
  int iter = 2;
  for (int i = 0; i < iter; i++) {
    initMove();
    initStart();
    int speed_R = speed_r + i;
    motor.setSpeeds(speed_L, speed_R);
    while (tick_R <= Ticks && tick_L <= Ticks ) {
      GetRPM();
      myPID.Compute();
      speed_R += (1) * PID_RPM_R;
      error = error + abs(RPM_R - RPM_L);
      motor.setSpeeds(speed_L, speed_R);
    }
    error_temp = error / Ticks;
    initEnd();
    delay(500);
  }
  error_temp = error_temp / iter;
  return error_temp;
}

void figure_motor_speed(double MoveDist) {

  PID_RPM_R = 0;

  int speed_R_initial = 298;
  int Ticks = getTicks(MoveDist);

  double error = 0;

  for (int i = 0 ; i < 5; i++) {
    int speed_R = speed_R_initial + i;
    error = get_avg(speed_R, Ticks);
    Serial.print("Speed R Initial : ");
    Serial.print(speed_R);
    Serial.print(" ");
    Serial.println(error) ;
    initEnd();
    delay(500);
  }
}
void moveDif(double Ticks) {
  if (Ticks < 3) {
    return;
  }
  int speed_R = 150;
  initMove();
  initStart();
  motor.setSpeeds(-150, speed_R);
  while (tick_R < Ticks / 3 ) {
    GetRPM();
    if (debug_motor == true) {
      Serial.print(tick_L);
      Serial.print(" ");
      Serial.println(tick_R);
    }
  }
  initEnd();
}

void moveF(double MoveDist) {
  //figure_motor_speed(MoveDist);

  PID_RPM_R = 0;

  int Ticks = getTicks(MoveDist);
  initMove();
  initStart();

  int speed_R = 300;
  motor.setSpeeds(speed_L, speed_R);
  while (tick_R <= Ticks && tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    speed_R += (1) * PID_RPM_R;
    if (debug_motor == true) {
      debug_print_ticks(tick_L, tick_R, PID_RPM_R, speed_R);
    }
    motor.setSpeeds(speed_L, speed_R);//speed_R
  }
  double dif = abs(tick_R - tick_L);
  moveDif(dif);
  initEnd();
}

void moveR() {
  PID_RPM_R = 0;
  int Ticks = getDegreeTicks(79.5); //81.5 for speed 350, for robot to turn right 90
  initMove();
  initStart();
  int speed_R = -300;
  motor.setSpeeds(speed_L, speed_R);
  while (tick_R <= Ticks && tick_L <= Ticks ) {
    GetRPM();
    rightPID.Compute();
    if (debug_motor == true) {
      debug_print_ticks(tick_L, tick_R, PID_RPM_R, speed_R);
    }
    speed_R += (-1) * PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  double dif = abs(tick_R - tick_L);
  moveDif(dif);
  initEnd();
}

void moveL() {
  int Ticks = getDegreeTicks(79.5);

  PID_RPM_R = 0;

  initMove();
  initStart();

  int speed_R = 300;

  motor.setSpeeds(-speed_L, speed_R);

  while (tick_R <= Ticks && tick_L <= Ticks ) {
    GetRPM();
    leftPID.Compute();
    if (debug_motor == true) {
      debug_print_ticks(tick_L, tick_R, PID_RPM_R, speed_R);
    }
    speed_R += PID_RPM_R;
    motor.setSpeeds(-speed_L, speed_R);
  }
  double dif = abs(tick_R - tick_L);
  moveDif(dif);
  initEnd();
}

void moveReverse() {
  PID_RPM_R = 0;
  int Ticks = getDegreeTicks(164); //81.5 for speed 350, for robot to turn right 90
  initMove();
  initStart();
  int speed_R = -300;
  motor.setSpeeds(speed_L, speed_R);

  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    if (debug_motor == true) {
      debug_print_ticks(tick_L, tick_R, PID_RPM_R, speed_R);
    }
    speed_R += (-1) * PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();

}

void moveB(double MoveDist) {
  PID_RPM_R = 0;
  int speed_R = -350;
  int Ticks = getTicks(MoveDist);

  initMove();
  initStart();
  motor.setSpeeds(-speed_L, speed_R);

  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    speed_R += (-1) * PID_RPM_R;
    motor.setSpeeds(-speed_L, speed_R);
  }
  initEnd();
}

void moveL45(float degree) { //80.5 for a 90 degree turn

  PID_RPM_R = 0;
  int speed_R = 275;
  int Ticks = getDegreeTicks(degree);

  initMove();
  initStart();

  motor.setSpeeds(-speed_L, speed_R);

  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    speed_R += PID_RPM_R;
    if (debug_motor == true) {
      debug_print_ticks(tick_L, tick_R, PID_RPM_R, speed_R);
    }
    motor.setSpeeds(-speed_L, speed_R);
  }
  initEnd();
}
void moveR45(float degree) { //80.5 for a 90 degree turn
  PID_RPM_R = 0;
  int speed_R = -275;
  int Ticks = getDegreeTicks(degree);

  initMove();
  initStart();
  motor.setSpeeds(300, -275);
  while (tick_R <= Ticks || tick_L <= Ticks ) {
    GetRPM();
    myPID.Compute();
    speed_R += (-1) * PID_RPM_R;
    motor.setSpeeds(300, speed_R);
  }
  initEnd();
}

void setupMotorEncoder() {
  motor.init();

  /**
     PIN 3 : Right Motor
     PIN 11 : Left Motor
  */
  pinMode(3, INPUT);
  pinMode(11, INPUT);

  enableInterrupt(3, leftMotorTime, RISING);
  enableInterrupt(11, rightMotorTime, RISING);
}

/**
   calculate ticks for LEFT motor
*/
void leftMotorTime() {
  tick_L++;
}

/**
   calculate ticks for RIGHT motor
*/
void rightMotorTime() {
  tick_R++;
}


int getTicks(double cm) {                       // Function return ticks required for specified distance
  return ceil(cm * 28.7);
}

void GetRPM() {
  float duration_L = pulseIn(3, HIGH); // pulseIn returns length of pulse in microseconds
  float duration_R = pulseIn(11, HIGH);

  if (isinf(duration_L) || isinf(duration_R)) {
    Serial.println("Infinity in GetRPM()");
    return;
  }

  if ((duration_L) == 0 || (duration_R) == 0) {
    Serial.println(" Zero in GetRPM()");
    return;
  }

  duration_L = duration_L * 2 ;
  duration_L = duration_L / 1000000; //converts length of pulse to seconds
  duration_R = duration_R * 2 ;
  duration_R = duration_R / 1000000;

  RPM_L = ((1 / duration_L) / (562 / 60)); //converts length of pulse to mins
  RPM_R = ((1 / duration_R) / (562 / 60)); // formula for rpm = (counts/min) / (counts/rev)
}
