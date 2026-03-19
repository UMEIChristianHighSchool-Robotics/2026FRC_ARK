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
      public static final double kDistanceSlewRateLimit=2.0; //controls acceleration to target
      
      //PID for autonomous turn to angle
      public static final double kTurnP=1.0;
      public static final double kTurnI=0;
      public static final double kTurnD=0;
      public static final double kTurnTolerance=0.01; //meters
      public static final double kTurnSlewRateLimit = 3.0;//max output change per second
      
  }
  
  public static final class IntakeConstants {

      //--------------- Motor IDs and Inversion --------------//
      /*Hardware references */
    
      // Motor CAN IDs
      public static final int kIntakeRollerCANID = 10;
      public static final int kIntakeDeployCANID = 6;
    
      // Motor inversion
      public static final boolean kRollerInverted = false;
      public static final boolean kDeployInverted = true;

      //Encoder roboRIO DIO port
      public static final int kDeployEncoderDIOPort = 0;

      //---------------- Motor Configuration --------------//
      /*How the motor runs*/

      //Roller motor controller configuration
      public static final int kRollerCurrentLimit = 60;
      public static final double kRollerRampRate = 0.3;
      public static final double kRollerVoltCompensation = 10.0; //keeps commanded voltage constant depite battery load
    
      //Intake deploy motor controller configurations 
      public static final int kDeployCurrentLimit = 40;
      public static final double kDeployRampRate = 0.3;
      public static final double kDeployVoltCompensation = 12.0; //keeps commanded voltage constant depite battery load
   
      //--------------------- States------------------------//
      /*Where the intake can be (pivot angles) */
    
                            //Intake deploy position radians
                            public static final double kUpRadians = 6.2;
                            public static final double kTravelRadians = 3.75;
                            public static final double kIntakeRadians = 3.2;
    
      //Zero offset for encoder calibration
      public static final double kZeroOffset = 0.00;//to find this value put intake in the UP position physically. Print encoder value to dashboard. That value becomes your offset.
    
      //---------------------PID / Feedforward------------------------//
      /*Tuning the pivot motor PID and gravity compensation */
    
      //PID constants
      public static final double kP = 1.0;
      public static final double kI = 0.0;
      public static final double kD = 0.08;
    
      //Feedforward for gravity compendation
      public static final double kSFeedForward = 0.0;
      public static final double kGFeedForward = 0.55; 
      public static final double kVFeedForward = 0.0; 
       
      //PID tolerance (radians)
      public static final double kDeployTolerance = 0.1;

      //---------------------Software limits------------------------//
    
      //Pivot soft limits (radians)
      /*Move the move intake to up/down positions manually, print encoder radians to dashboard, add/subtract 0.1 rad for safety */
      public static final double kForwardSoftLimit = 6.6; // down
      public static final double kReverseSoftLimit = 2.0; // up
      public static final boolean kForwardSoftLimitEnabled = true;//test and confirm limits first
      public static final boolean kReverseSoftLimitEnabled = true;//test and confirm limits first

      //---------------------Optional Tunables------------------------//
      public static final double kRollerVoltage = 8;
      public static final double kDeployVoltage = 12;
   
  }

  public static final class ShooterConstants {

    //Shooter mechanism CANIDs
    public static final int kShooterRightCANID = 12;
    public static final int kShooterLeftCANID = 13;

    //Shooter mechanism motor controller configurations
    public static final int kCurrentLimit = 40;
    public static final double kRampRate = 0.7;
    public static final double kVoltCompensation = 12.0;

    public static final double kP = 0.002;//recommend 0.015-0.003 starting
    public static final double kI = 0;//leave 0
    public static final double kD = 0;// leave 0
    public static final double kFF = 0.0022;//recommend 0.0021-0.0023 starting

    public static final double kSpeedTolerance = 125; //RPM
    public static final double kTargetSpeed = 4000; //RPM

    //Shooter motor inversion
    public static final boolean kLeftInverted = false;
    public static final boolean kRightInverted = true;

    //Shoot Command
    public static final double kTimeout = 0.6; //shooter feed timout (seconds)
  }

  public static final class OperatorConstants {

    //Joystick USB port
    public static final int kControllerPort = 0;

    //Joystick buttons
    public static final int kLeftYAxis = 1;
    public static final int kRightYAxis = 5;
    public static final int kRightXAxis = 2;
    public static final int kYButton = 4;

    //Speed and deadband
    public static final double kDeadband = 0.05;
    public enum SpeedSelect{
      CRAWL(0.10,0.10),
      SLOW(0.30,0.15),
      DRIVE(0.70,0.50),
      FAST(1.00,0.30);

      public final double driveScale;
      public final double turnScale;

      SpeedSelect(double driveScale, double turnScale){
        this.driveScale=driveScale;
        this.turnScale=turnScale;
      }
    }
  }

}