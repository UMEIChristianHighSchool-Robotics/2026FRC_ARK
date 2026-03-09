// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

//import java.util.function.DoubleSupplier;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
//import com.revrobotics.spark.SparkLowLevel;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
//import edu.wpi.first.wpilibj2.command.Command;

import static frc.robot.Constants.SnowPlowConstants;


public class SnowPlowSubsystem extends SubsystemBase {
  
  //Declare and initialize the motor controllers
  private SparkMax feederRoller = new SparkMax(SnowPlowConstants.kFeederRollerCANID, MotorType.kBrushless);
  private SparkFlex intakeLauncherRoller = new SparkFlex(SnowPlowConstants.kIntakeLauncherCANID, MotorType.kBrushless);
  
  // declare configuration
  private SparkMaxConfig feederRollerConfig = new SparkMaxConfig();
  private SparkFlexConfig intakeLauncherConfig = new SparkFlexConfig();
  

  public SnowPlowSubsystem() {

    //Configure motor controllers inside the constructor
        
    feederRollerConfig  
      .inverted(SnowPlowConstants.kFeederInverted)  
      .smartCurrentLimit(SnowPlowConstants.kFeederCurrentLimit)
      .openLoopRampRate(SnowPlowConstants.kRampRate)
      .voltageCompensation(SnowPlowConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
  
    feederRoller.configure(feederRollerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
    intakeLauncherConfig 
      .inverted(SnowPlowConstants.kLauncherInverted)  
      .smartCurrentLimit(SnowPlowConstants.kLauncherCurrentLimit)
      .openLoopRampRate(SnowPlowConstants.kRampRate)
      .voltageCompensation(SnowPlowConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
  
    intakeLauncherRoller.configure(intakeLauncherConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  
    SmartDashboard.putNumber("Intaking feeder roller value", SnowPlowConstants.kIntakingFeederVoltage);
    SmartDashboard.putNumber("Intaking intake roller value", SnowPlowConstants.kIntakingIntakeVoltage);
    SmartDashboard.putNumber("Launching feeder roller value", SnowPlowConstants.kLaunchingFeederVoltage);
    SmartDashboard.putNumber("Launching launcher roller value", SnowPlowConstants.kLaunchingLauncherVoltage);
    SmartDashboard.putNumber("Spin-up feeder roller value", SnowPlowConstants.kSpinUpFeederVoltage);
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

