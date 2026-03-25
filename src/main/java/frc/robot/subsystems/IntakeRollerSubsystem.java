// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;
public class IntakeRollerSubsystem extends SubsystemBase {
 
 
    //-------Declare and initialize the motor controllers-----------/
  
  // Roller motor: NEO Vortex on Spark Flex
  private SparkFlex intakeRoller = new SparkFlex(IntakeConstants.kIntakeRollerCANID, MotorType.kBrushless);
   //---------Declare and initialize the motor controller configurations
   private SparkFlexConfig intakeRollerConfig = new SparkFlexConfig();

//Create a tab in Shuffleboard
  private final ShuffleboardTab intakeTab= Shuffleboard.getTab("Intake");
  
   public IntakeRollerSubsystem() {

        //Configure motor controllers inside the constructor
    //---------Configure intake roller---------
    intakeRollerConfig  
      .inverted(IntakeConstants.kRollerInverted)  
      .smartCurrentLimit(IntakeConstants.kRollerCurrentLimit)
      .openLoopRampRate(IntakeConstants.kRollerRampRate)
      .voltageCompensation(IntakeConstants.kRollerVoltCompensation)
      .idleMode(IdleMode.kCoast);
  
    //apply configuration to the motor  
    intakeRoller.configure(intakeRollerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  //Telemetry
 intakeTab.addDouble("Roller Output", ()->intakeRoller.get());

  }
 //---------------Intake Roller Methods----------------------//

  public void runRoller(double volts) {
      intakeRoller.setVoltage(MathUtil.clamp(volts, -12.0, 12.0));
  }

  public void stopRoller() {
      intakeRoller.stopMotor();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
