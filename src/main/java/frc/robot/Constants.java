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

//----------------------------------INTAKE ROLLER-----------------------------------------//

  public static final class IntakeRollerConstants {
    
    //CANIDs
    public static final int kRollerRightCANID = 12;
    public static final int kRollerLeftCANID = 13;

    //Motor inversion
    public static final boolean kRightInverted = true;
   
    //Speed control
    public static final double kP = 0.002;//recommend 0.015-0.003 starting
    public static final double kI = 0;//leave 0
    public static final double kD = 0;// leave 0
    public static final double kFF = 0.0022;//recommend 0.0021-0.0023 starting
    public static final double kSpeedTolerance = 200; //RPM
    public static final double kTargetSpeed = 3000; //RPM

    //Other Motor control
    public static final int kCurrentLimit = 60;
    public static final double kRampRate = 0.7;
    public static final double kVoltCompensation = 12.0;
    
    //Timeout for Auto intake Command
    public static final double kTimeout = 0.6; //intake feed timeout (seconds)
  }

//----------------------------------INTAKE PIVOT-----------------------------------------//

  public static final class IntakePivotConstants {
    
    //CANIDs
    public static final int kIntakePivotCANID = 6;
    
    //Motor inversion
    public static final boolean kIntakePivotInverted = true;
   
    //Position States
    public static final double kInRadians = -.5;
    public static final double kTravelRadians = -18;
    public static final double kOutRadians = -28.5;
    
    //Pivot soft limits (radians)
    /*Move the move intake to up/down positions manually, print encoder radians to dashboard, add/subtract 0.1 rad for safety */
    public static final double kInSoftLimit = 100;
    public static final double kOutSoftLimit = -29; 
    public static final boolean kInSoftLimitEnabled = true;//test and confirm limits first
    public static final boolean kOutSoftLimitEnabled = true;//test and confirm limits first

    //Position control
    public static final double kP = .007;//recommend 0.015-0.003 starting
    public static final double kI = 0;//leave 0
    public static final double kD = 0;// leave 0
    public static final double kG = 0;
    public static final double kIntakePivotTolerance = 0.05; // radians
    public static final double kManualVoltageScale = 8.0; // scale to 4-6 V max for smooth control
    public static final double kOffset = 0; // if 0rad is not arm horizontal 

    //Other
    public static final int kCurrentLimit = 60;
    public static final double kRampRate = 0.7;
    public static final double kVoltCompensation = 12.0;
    public static final double kGearRatio = 18.0;

}
 

//------------------------------------------SHOOTER CONSTANTS-----------------------------------------//


  public static final class ShooterConstants {

    //Shooter mechanism CANIDs
    //neo v1 on spark max
    public static final int kShooterCANID = 7;
  
    //Shooter mechanism motor controller configurations
    public static final int kCurrentLimit = 60;
    public static final double kRampRate = 0.5;
    public static final double kVoltCompensation = 12.0;

    public static final double kP = 0.009;//recommend 0.015-0.003 starting
    public static final double kI = 0;//leave 0
    public static final double kD = 0;// leave 0
    public static final double kFF = 0.0022;//recommend 0.0021-0.0023 starting

    public static final double kSpeedTolerance = 200; //RPM
    public static final double kTargetSpeed = 6600; //RPM

    //Shooter motor inversion
    public static final boolean kShooterInverted = true;
  
    //Shoot Command
    public static final double kTimeout = 1.6; //shooter feed timout (seconds)
  }

//------------------------------------------XBOX CONTROLLERS-----------------------------------------//

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