// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

//This code was modified from suggestions from ChatGPT and Chief Delphi

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
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeRollerConstants;

public class IntakeRollerSubsystem extends SubsystemBase {
  //Declare and initialize the motor controllers & their configuration
  private SparkFlex rightLeader = new SparkFlex(IntakeRollerConstants.kRollerRightCANID, MotorType.kBrushless);
  private SparkFlex leftFollower = new SparkFlex(IntakeRollerConstants.kRollerLeftCANID, MotorType.kBrushless);
  private SparkFlexConfig rightLeaderConfig = new SparkFlexConfig();
  private SparkFlexConfig leftFollowerConfig = new SparkFlexConfig();

  //Declare built-in encoders and closed loop controllers to use them for shooting adjustments
  private RelativeEncoder rightLeaderEncoder;
  private SparkClosedLoopController rightLeaderClosedLoopControl;

  //Roller speed set point
  private double targetRPM=0.0;

  //Create a tab in Shuffleboard
  ShuffleboardTab intakeTab= Shuffleboard.getTab("Intake");
  ShuffleboardTab autoTab= Shuffleboard.getTab("Auto");

  @SuppressWarnings("removal")
  public IntakeRollerSubsystem() {

  //Configure motor controllers inside the constructor   
  rightLeaderConfig  
      .inverted(IntakeRollerConstants.kRightInverted)  
      .smartCurrentLimit(IntakeRollerConstants.kCurrentLimit)
      .openLoopRampRate(IntakeRollerConstants.kRampRate)
      .voltageCompensation(IntakeRollerConstants.kVoltCompensation)
      .idleMode(IdleMode.kCoast);
  rightLeaderConfig.closedLoop
      .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
      .p(IntakeRollerConstants.kP)
      .i(IntakeRollerConstants.kI)
      .d(IntakeRollerConstants.kD)
      .outputRange(-1, 1);
  rightLeaderConfig.closedLoop.feedForward.kV(IntakeRollerConstants.kFF);

  //apply the 3 configurations to the motors & invert the follower 
  rightLeader.configure(rightLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  leftFollowerConfig.follow(rightLeader,true);
  leftFollower.configure(leftFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
 
  //Encoder + PID on leader only
  rightLeaderEncoder = rightLeader.getEncoder();
  rightLeaderClosedLoopControl = rightLeader.getClosedLoopController();

  //Telemetry
  intakeTab.addDouble("Intake Speed", rightLeaderEncoder::getVelocity);
  intakeTab.addBoolean("Intake at speed", this::atSpeed);
  autoTab.addBoolean("Intake at speed", this::atSpeed);

  }

// A method to set the velocity of the launching rollers; command can set velocity
public void setTargetRPM(double rpm) {
  targetRPM = rpm;
   
  rightLeaderClosedLoopControl.setSetpoint(rpm,ControlType.kVelocity);
}

//Add an at speed method for autos and consistency; command can wait
public boolean atSpeed() {
  double tolerance = IntakeRollerConstants.kSpeedTolerance;

  boolean atSpeed =
    Math.abs(rightLeaderEncoder.getVelocity() - targetRPM) < tolerance;

  return atSpeed;
}

// A method to stop the intake roller
public void stopRoller() {
  rightLeader.stopMotor();
}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
