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
    public static final int kCurrentLimit = 40;
    public static final double kRampRate = 0.7;
    public static final double kVoltCompensation = 12.0;

    //Motor inversion
    public static final boolean kLeftInverted = false;
    public static final boolean kRightInverted = true;
  }
  
  public static final class IntakeConstants {

    //intake CANID
    public static final int kFeederRollerCANID = 8;
    public static final int kIntakeLauncherCANID = 9;
    
    //Intake motor controller configurations
    public static final int kCurrentLimit = 40;
    public static final double kRampRate = 0.7;
    public static final double kVoltCompensation = 12.0;
    public static final double kIntakeSpeed = 0.75;

    //Intake Motor inversion
    public static final boolean kFeederInverted = false;
    public static final boolean kLauncherInverted = true;

  }

  public static final class OperatorConstants {

    //Joystick USB port
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1;

    //Joystick buttons
    public static final int kLeftYAxis = 1;
    public static final int kRightYAxis = 5;
    public static final int kRightXAxis = 2;
    public static final int kYButton = 4;

    //Speed and deadband
    public static final double kDriveSpeedScale = 0.8;
    public static final double kDeadband = 0.05;
  }

}