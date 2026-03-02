// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import static frc.robot.Constants.IntakeConstants;


public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new IntakeSubsystem. */
  
  //Declare and initialize the motor controller
  private SparkMax feederRoller = new SparkMax(IntakeConstants.kFeederRollerCANID, MotorType.kBrushless);
  private SparkMax intakeLauncherRoller = new SparkMax(IntakeConstants.kIntakeLauncherCANID, MotorType.kBrushless);
  
  // declare configuration
  private SparkMaxConfig feederRollerConfig = new SparkMaxConfig();
  private SparkMaxConfig intakeLauncherConfig = new SparkMaxConfig();
  

  public IntakeSubsystem() {

    //Configure motor controllers inside the constructor
        
    feederRollerConfig  
      .inverted(IntakeConstants.kFeederInverted)  
      .smartCurrentLimit(IntakeConstants.kCurrentLimit)
      .openLoopRampRate(IntakeConstants.kRampRate)
      .voltageCompensation(IntakeConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
  
    feederRoller.configure(feederRollerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
    intakeLauncherConfig 
      .inverted(IntakeConstants.kLauncherInverted)  
      .smartCurrentLimit(IntakeConstants.kCurrentLimit)
      .openLoopRampRate(IntakeConstants.kRampRate)
      .voltageCompensation(IntakeConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
  
    intakeLauncherRoller.configure(intakeLauncherConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  
    SmartDashboard.putNumber("Intaking feeder roller value", INTAKING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Intaking intake roller value", INTAKING_INTAKE_VOLTAGE);
    SmartDashboard.putNumber("Launching feeder roller value", LAUNCHING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE);
    SmartDashboard.putNumber("Spin-up feeder roller value", SPIN_UP_FEEDER_VOLTAGE);
  }
  
 // A method to set the voltage of the intake roller
  public void setIntakeLauncherRoller(double voltage) {
    intakeLauncherRoller.setVoltage(voltage);
  }

  // A method to set the voltage of the intake roller
  public void setFeederRoller(double voltage) {
    feederRoller.setVoltage(voltage);
  }

  // A method to stop the rollers
  public void stop() {
    feederRoller.set(0);
    intakeLauncherRoller.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}

