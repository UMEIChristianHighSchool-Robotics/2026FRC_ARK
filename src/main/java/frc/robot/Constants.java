// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
 
  
  public static final class DriveConstants {
 
    //drivetrain CANIDs
    public static final int kLeftLeaderCANID = 4;
    public static final int kLeftFollowerCANID = 5;
    public static final int kRightLeaderCANID = 2;
    public static final int kRightFollowerCANID = 3;

    //Motor controller configurations
    public static final int kCurrentLimit = 60;
    public static final double kRampRate = 0.7;
    public static final double kVoltCompensation = 12.0;

    //Motor inversion
    public static final boolean kLeftInverted = false;
    public static final boolean kRightInverted = true;
  }
  
  public static final class IntakeConstants {

    //Intake mechanism CANIDs
    public static final int kIntakeRollerCANID = 10;
    public static final int kIntakeDeployCANID = 6;

    //Intake deploy roboRIO DIO port
    public static final int kDeployEncoderDIOPort = 0;

    //Intake roller motor controller configurations
    public static final int kFeederCurrentLimit = 60;
    public static final double kFeederRampRate = 0.3;
    public static final double kFeederVoltCompensation = 12.0;
    public static final double kFeederSpeed = 0.75; //speed percent (-1.0 to 1.0)

    //Intake deploy motor controller configurations 
    public static final int kDeployCurrentLimit = 40;
    public static final double kDeployRampRate = 0.3;
    public static final double kDeployVoltCompensation = 12.0;
    public static final double kIntakeDeploySpeed = 0.5; //speed percent (-1.0 to 1.0)

    //Intake deploy position radians
    public static final double kUpRadians = 0.0;
    public static final double kTravelRadians = 0.6;
    public static final double kIntakeRadians = 1.3;
    public static final double kZeroOffset = 0.02;//to find this value put intake in the UP position physically. Print encoder value to dashboard. That value becomes your offset.
    
    //Feedforward
    public static final double kSFeedForward = 0.0; // kS (start with 0)
    public static final double kGFeedForward = 0.4; // guess to start
    public static final double kVFeedForward = 0.0; // usually stays at 0
    public static final double kGearRatio = 12.0; // if gear ratio is 12:1 accounting for gearing between motor and deploy mechanism
   
    //Intake deploy encoder configurations
    public static final double kP = 1.0;
    public static final double kI = 0;// leave 0
    public static final double kD = 0;// leave 0
    public static final double kDeployTolerance = 0.05;

    public static final double kForwardSoftLimit = 1.52;//radians move intake to lowest safe position manually, print encoder radians to dashboard, subtract 0.1 rad for safety
    public static final double kReverseSoftLimit = -0.1;//move intake to up position manually, print encoder radians to dashboard, subtract 0.1 rad for safety
    public static final boolean kForwardSoftLimitEnabled = true;//test and confirm limits first
    public static final boolean kReverseSoftLimitEnabled = true;//test and confirm limits first

    // Voltage values for various intake operations. These values may need to be tuned
    // based on exact robot construction.
    // See the Software Guide for tuning information
    public static final double kIntakingFeederVoltage = -8;
    public static final double kIntakingIntakeVoltage = 8;
    public static final double kSpinUpFeederVoltage = -4;
    public static final double kSpinUpSeconds = 1;

    //Intake mechanism Motor inversion
    public static final boolean kFeederInverted = false;
    public static final boolean kDeployInverted = false;
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

    public static final double kShooterTolerance = 100; //RPM
    public static final double kShooterSpinUp = 4000; //RPM

    //Shooter motor inversion
    public static final boolean kLeftInverted = false;
    public static final boolean kRightInverted = true;

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
    public static final double kDriveSpeedScale = 0.8;
    public static final double kRotationScale = 0.8;
    public static final double kDeadband = 0.05;

    //auto drive
    public static final double kxSpeed = 0.5;
    public static final double kzRotation = 0.0;
  }

}