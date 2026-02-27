// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import static frc.robot.Constants.IntakeConstants;


public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new IntakeSubsystem. */
  
  //Declare and initialize the motor controller
  private SparkMax intakeMotor = new SparkMax(IntakeConstants.kIntakeCANID, SparkLowLevel.MotorType.kBrushless);
  
  // declare configuration
  private SparkMaxConfig intakeConfig = new SparkMaxConfig();
  
  public IntakeSubsystem() {

    //Configure motor controllers inside the constructor
        
    intakeConfig  
      .inverted(IntakeConstants.kInverted)  
      .smartCurrentLimit(IntakeConstants.kCurrentLimit)
      .openLoopRampRate(IntakeConstants.kRampRate)
      .voltageCompensation(IntakeConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
  
    intakeMotor.configure(intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

   public void runIntake() {
    intakeMotor.set(IntakeConstants.kIntakeSpeed);
  }

   public void stop() {
    intakeMotor.set(IntakeConstants.kIntakeSpeed);
  }
}
