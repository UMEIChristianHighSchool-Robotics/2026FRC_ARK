// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
  /** Creates a new ShooterSubsystem. */

  //Declare and initialize the motor controllers
  private SparkFlex rightMotor = new SparkFlex(ShooterConstants.kShooterRightCANID, MotorType.kBrushless);
  private SparkFlex leftMotor = new SparkFlex(ShooterConstants.kShooterLeftCANID, MotorType.kBrushless);
  
  //Declare built-in encoders and closed loop controllers to use them for shooting adjustments
  private RelativeEncoder rightEncoder;
  private RelativeEncoder leftEncoder;
  private SparkClosedLoopController rightClosedLoopControl;
  private SparkClosedLoopController leftClosedLoopControl;

  //shooter state (SETPOINT)
  private double targetRPM=0.0;
    
  //Declare and initialize the motor controller configurations
  private SparkFlexConfig rightMotorConfig = new SparkFlexConfig();
  private SparkFlexConfig leftMotorConfig = new SparkFlexConfig();

  
  public ShooterSubsystem() {

    //pull in the built-in encoders & closed loop control inside the constructor
    rightEncoder = rightMotor.getEncoder();
    leftEncoder = leftMotor.getEncoder();

    rightClosedLoopControl = rightMotor.getClosedLoopController();
    leftClosedLoopControl = leftMotor.getClosedLoopController();

    //Configure motor controllers inside the constructor   
    rightMotorConfig  
      .inverted(ShooterConstants.kRightInverted)  
      .smartCurrentLimit(ShooterConstants.kCurrentLimit)
      .openLoopRampRate(ShooterConstants.kRampRate)
      .voltageCompensation(ShooterConstants.kVoltCompensation)
      .idleMode(IdleMode.kCoast);

    leftMotorConfig  
      .inverted(ShooterConstants.kLeftInverted)  
      .smartCurrentLimit(ShooterConstants.kCurrentLimit)
      .openLoopRampRate(ShooterConstants.kRampRate)
      .voltageCompensation(ShooterConstants.kVoltCompensation)  
      .idleMode(IdleMode.kCoast);

    //closed loop (PID) configurations inside the constructor

    rightMotorConfig.closedLoop
      .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        // Set PID values for velocity control
      .p(ShooterConstants.kP)
      .i(ShooterConstants.kI)
      .d(ShooterConstants.kD)
      .outputRange(-1, 1);
       
    leftMotorConfig.closedLoop
      .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        // Set PID values for velocity control
      .p(ShooterConstants.kP)
      .i(ShooterConstants.kI)
      .d(ShooterConstants.kD)
      .outputRange(-1, 1);
       
    //feedforward config in the constructor
    rightMotorConfig.closedLoop.feedForward.kV(ShooterConstants.kFF);
    leftMotorConfig.closedLoop.feedForward.kV(ShooterConstants.kFF);
   
    //apply the 3 configurations to the motors   
    rightMotor.configure(rightMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    leftMotor.configure(leftMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  
    // Initialize dashboard values
    SmartDashboard.setDefaultNumber("Target Position", 0);
    SmartDashboard.setDefaultNumber("Target Velocity", 0);
    SmartDashboard.setDefaultBoolean("Control Mode", false);
    SmartDashboard.setDefaultBoolean("Reset Encoder", false);
    SmartDashboard.putNumber("Shooter Target RPM", targetRPM);
    SmartDashboard.putNumber("Shooter Actual RPM", rightEncoder.getVelocity());
  
  }

  // A method to set the velocity of the launching rollers; command can set velocity
  public void setTargetRPM(double rpm) {
    targetRPM = rpm;
   
    rightClosedLoopControl.setSetpoint(rpm,ControlType.kVelocity);
    leftClosedLoopControl.setSetpoint(rpm,ControlType.kVelocity);
  }

  //Add an at speed method for autos and consistency; command can wait
 public boolean atSpeed() {
  double tolerance = ShooterConstants.kShooterTolerance;

  boolean rightAtSpeed =
      Math.abs(rightEncoder.getVelocity() - Math.abs(targetRPM)) < tolerance;

  boolean leftAtSpeed =
      Math.abs(leftEncoder.getVelocity() - Math.abs(targetRPM)) < tolerance;

  return rightAtSpeed && leftAtSpeed;
}

  // A method to stop the launching rollers
 public void stop() {
  rightClosedLoopControl.setSetpoint(0, ControlType.kVelocity);
  leftClosedLoopControl.setSetpoint(0, ControlType.kVelocity);
}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    rightClosedLoopControl.setSetpoint(targetRPM, ControlType.kVelocity);
leftClosedLoopControl.setSetpoint(targetRPM, ControlType.kVelocity);

    SmartDashboard.putNumber("Shooter RPM Right", rightEncoder.getVelocity());
    SmartDashboard.putNumber("Shooter RPM Left", leftEncoder.getVelocity());
  }
}
