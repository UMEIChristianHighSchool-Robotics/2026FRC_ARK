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
                            public static final double kUpRadians = 3.45;
                            public static final double kTravelRadians = 1.3;
                            public static final double kIntakeRadians = 0.0;
    
      //Zero offset for encoder calibration
      public static final double kZeroOffset = 4.8 ;//to find this value put intake in the UP position physically. Print encoder value to dashboard. That value becomes your offset.
    
      //---------------------PID / Feedforward------------------------//
      /*Tuning the pivot motor PID and gravity compensation */
    
      //PID constants Look at error between current position and target position and adjust 
      public static final double kP = 2.2; //Move to position power- Too high: overshoots, oscillates/jitters; too low: arm feels weak, stops short or droops
      public static final double kI = 0.0; //Fixes small errors over time; should stay at 0 and correct with feedforward
      public static final double kD = 0.08; //Damping/shock absorber slows things down - how fast is the error changing; too low: overshoot/bouncy motion; too high: feels sluggish, can twitch
    
      //Feedforward for gravity compensation
      public static final double kSFeedForward = 0.30; //static friction, predicts power needed to accelerate 
      public static final double kGFeedForward = 0.48; //hold arm up against gravity; too high: arm lifts when it shouldn't, jumpy motion; too low: arm droops, PID works hard, motor buzzes
      public static final double kVFeedForward = 0.0; // power for moving at a certain speed; too low: motion feels weak, PID does most of the work; to high: arm moves too aggressively
       
      //PID tolerance (radians)
      public static final double kDeployTolerance = 0.05;

      //---------------------Software limits------------------------//
    
      //Pivot soft limits (radians)
      /*Move the move intake to up/down positions manually, print encoder radians to dashboard, add/subtract 0.1 rad for safety */
                             public static final double kForwardSoftLimit = 4; // up
                            public static final double kReverseSoftLimit = 0; // down
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
    public static final double kVoltCompensation = 11.0;

    public static final double kP = 0.002;//recommend 0.015-0.003 starting
    public static final double kI = 0;//leave 0
    public static final double kD = 0;// leave 0
    public static final double kFF = 0.0022;//recommend 0.0021-0.0023 starting

    public static final double kSpeedTolerance = 200; //RPM
    public static final double kTargetSpeed = 3000; //RPM

    //Shooter motor inversion
    public static final boolean kLeftInverted = false;
    public static final boolean kRightInverted = true;

    //Shoot Command
    public static final double kTimeout = 0.6; //shooter feed timout (seconds)
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
    public static final double kUpRadians = 0.82;
    public static final double kDownRadians = 0.0;    
    //PID
    public static final double kP = 3.0;
    public static final double kI = 0.0;
    public static final double kD = 0.2;
    public static final double kLifterTolerance = 0.05;

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
      CRAWL(0.30,0.35),
      SLOW(0.40,0.45),
      DRIVE(0.70,0.60),
      FAST(0.80,0.45);

      public final double driveScale;
      public final double turnScale;

      SpeedSelect(double driveScale, double turnScale){
        this.driveScale=driveScale;
        this.turnScale=turnScale;
      }
    }
  }

}