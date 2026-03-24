// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.FloorLifterConstants;
import frc.robot.Constants.IntakeConstants;

public class FloorLifterSubsystem extends SubsystemBase {

  //Create Enum to define states (positions) for the floor lifting mechanism
  public enum FloorLiftState{
    UP(FloorLifterConstants.kUpRadians),
    DOWN(FloorLifterConstants.kDownRadians);
  
    public final double radians;

    FloorLiftState(double radians){
      this.radians=radians;
    }
  }
  //---------------Declare and Initialize----------//
  
  //Motor controller (NEO on SPARK MAX)
  private SparkMax floorLifterMotor = new SparkMax(FloorLifterConstants.kFloorLifterCANID, MotorType.kBrushless);

  //Configuration for Motor Controller 
  private SparkMaxConfig floorLifterConfig = new SparkMaxConfig();

  //Relative Encoder (built-in accessed via sparkmax)
  private RelativeEncoder floorLifterEncoder = new SparkBase.getEncoder();
  
  //Create PID Controller object 
  private final PIDController floorPID =
    new PIDController(
      FloorLifterConstants.kP,
      FloorLifterConstants.kI,
      FloorLifterConstants.kD
    ); 

  //Create feedforward object
  private final ArmFeedforward feedforward=
    new ArmFeedforward(
      FloorLifterConstants.kSFeedForward,
      FloorLifterConstants.kGFeedForward,
      FloorLifterConstants.kVFeedForward
    );

  public double getAngleRadians(){
    return floorLifterEncoder.getPosition();
  }

  private FloorLiftState currentState;

  //Create Tab in Shuffleboard
  private final ShuffleboardTab FloorLiftTab = Shuffleboard.getTab("Floor Lifter")

  @SuppressWarnings("removal")
  
  
  public FloorLifterSubsystem() {

    //Configure motor controller
    floorLifterConfig
      .inverted(FloorLifterConstants.kFloorLifterInverted)
      .smartCurrentLimit(FloorLifterConstants.kCurrentLimit)
      .openLoopRampRate(FloorLifterConstants.kRampRate)
      .voltageCompensation(FloorLifterConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
    floorLifterMotor.configure(floorLifterConfig, com.revrobotics.ResetMode.kResetSafeParameters, com.revrobotics.PersistMode.kPersistParameters);

    //Configure Positioning
    floorPID.setTolerance(FloorLifterConstants.kLifterTolerance);
    
    //ensure software stat matches reallife on boot
    double angle = getAngleRadians();
    if (Math.abs(angle - FloorLiftState.UP.radians) < FloorLifterConstants.kLifterTolerance) {
      currentState = FloorLiftState.UP;
    }
    else if (Math.abs(angle - FloorLiftState.DOWN.radians) < FloorLifterConstants.kLifterTolerance) {
      currentState = FloorLiftState.DOWN;
    }
    
    //Telemetry
    FloorLiftTab.add("Floor Lift kP",FloorLifterConstants.kP);
    FloorLiftTab.add("Floor Lift kGff",FloorLifterConstants.kGFeedForward);
    FloorLiftTab.addDouble("Angle Error", () -> currentState.radians - getAngleRadians());
    FloorLiftTab.addDouble("Current Angle (rad)", this::getAngleRadians);
    FloorLiftTab.addDouble("Target Angle (rad)", () -> currentState.radians);
    FloorLiftTab.addBoolean("At Setpoint", floorPID::atSetpoint);
  }

  //method to check what state the floor lifter is in
  public boolean isNearState(FloorLiftState state) {
    return Math.abs(getAngleRadians() - state.radians) 
      < FloorLifterConstants.kLifterTolerance;
  }

  //method to ensure floor lift system is in the down position to start
  public void ensureDownOnEnable() {
    if (!isNearState(FloorLiftState.DOWN)) {
      setState(FloorLiftState.DOWN);
    }
  }

  //method to change floor lift states
  public boolean isAtTarget() {
    return Math.abs(
      floorLifterEncoder.get() - currentState.radians
    ) < 0.05;
  }

  public void setState(FloorLiftState newState){
    if (newState == currentState) {
      return;
    }
    currentState = newState;
  }

  @Override
  public void periodic() {
    double currentAngle = floorLifterEncoder.get();
    double targetAngle = currentState.radians;
    double pidOutput = floorPID.calculate(currentAngle, targetAngle);
    double ff = feedforward.calculate(currentAngle,0.0); //feedforward
    double outputVolts = pidOutput + ff;
    outputVolts = MathUtil.clamp(outputVolts, -12.0,12.0);

    //apply soft limit
     if ((currentAngle >= FloorLifterConstants.kForwardSoftLimit && outputVolts > 0) ||
      (currentAngle <= FloorLifterConstants.kReverseSoftLimit && outputVolts < 0)) {
        outputVolts = 0;
    }

    if (DriverStation.isEnabled()){
      floorLifterMotor.setVoltage(outputVolts);
    }
    else {
      floorLifterMotor.stopMotor();
    }
  }
}
