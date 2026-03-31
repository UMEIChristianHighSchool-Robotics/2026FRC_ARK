// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.


package frc.robot;

public final class Constants {
 
  
  public static final class DriveConstants {
 
      //drivetrain CANIDs
      public static final int kLeftLeaderCANID = 4;
      public static final int kLeftFollowerCANID = 5;
      public static final int kRightLeaderCANID = 2;
      public static final int kRightFollowerCANID = 3;

      //Motor inversion
      public static final boolean kLeftInverted = false;
      public static final boolean kRightInverted = true;

      //Motor controller configurations
      public static final int kCurrentLimit = 60;
      public static final double kRampRate = 0.7; //smooths joystick control
      public static final double kVoltCompensation = 12.0;

      //Approximate: meters per rotation for autonomous drive forward  
      public static final double kWheelDiameterMeters = 0.1524; // 6 inches
      public static final double kGearRatio = 8.45;             //KitBot reduction
      public static final double kMetersPerRotation =
        (Math.PI * kWheelDiameterMeters) / kGearRatio;          // ~0.0567 m

      // Approximate: turn circumference for turn
      public static final double kTrackWidthMeters = 0.673; // distance between wheels estimated from kitbot docs
      public static final double kTurnCircumference =
        (Math.PI * kTrackWidthMeters); // full 360° rotation
      
      //PID for autonomous drive forward meters
      public static final double kDistanceP=1.0;
      public static final double kDistanceI=0;
      public static final double kDistanceD=0;
      public static final double kDistanceTolerance=0.01;
      public static final double kDistanceSlewRateLimit=3.0; //controls acceleration to target
      public static final double kAutoClamp = 0.7;
      
      //PID for autonomous turn to angle
      public static final double kTurnP=1.0;
      public static final double kTurnI=0;
      public static final double kTurnD=0;
      public static final double kTurnTolerance=0.01; //meters
      public static final double kTurnSlewRateLimit = 4.0;//max output change per second
      
  }
  
  public static final class FloorLifterConstants {
    
    //Floor Lifter mechanism CANIDs
    public static final int kFloorLifterCANID = 14;

    //Motor control
    public static final boolean kFloorLifterInverted= false;
    public static final int kCurrentLimit = 40;
    public static final double kRampRate = 0.7;
    public static final double kVoltCompensation = 12.0;
   
    //-------------- States (floor lifter positions)-------------//
    public static final double kGearRatio=9.0;
    
    //Floor Lifter position radians
    public static final double kUpRadians = -3.25;// extended out
    public static final double kDownRadians = 0.0; // in starting position
    //PID
    public static final double kP = 1.0;
    public static final double kI = 0.0;
    public static final double kD = 0.2;
    public static final double kLifterTolerance = 0.05;

//Feedforward for gravity compensation
      public static final double kSFeedForward = 0.30; //static friction, predicts power needed to accelerate 
      public static final double kGFeedForward = 0.48; //hold arm up against gravity; too high: arm lifts when it shouldn't, jumpy motion; too low: arm droops, PID works hard, motor buzzes
      public static final double kVFeedForward = 0.0; // power for moving at a certain speed; too low: motion feels weak, PID does most of the work; to high: arm moves too aggressively


    public static final double kUpSoftLimit = 1.0;
    public static final double kDownSoftLimit = -0.02;
    public static final boolean kUpSoftLimitEnabled = true;//test and confirm limits first
    public static final boolean kDownSoftLimitEnabled = true;//test and confirm limits first

  }
  
  public static final class OperatorConstants {

    //Joystick USB ports
    public static final int kDriverControllerPort = 0;
        public static final int kOperatorControllerPort = 1; 

    //Joystick buttons
    public static final int kLeftYAxis = 1;
    public static final int kRightYAxis = 5;
    public static final int kRightXAxis = 2;
    public static final int kYButton = 4;

    //Speed and deadband
    public static final double kDeadband = 0.05;
    public enum SpeedSelect{
      CRAWL(0.20,0.35),
      SLOW(0.40,0.45),
      DRIVE(0.70,0.45),
      FAST(1.00,0.45);

      public final double driveScale;
      public final double turnScale;

      SpeedSelect(double driveScale, double turnScale){
        this.driveScale=driveScale;
        this.turnScale=turnScale;
      }
    }
  }

}