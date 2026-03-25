// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Constants.FloorLifterConstants;

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
  private RelativeEncoder floorLifterEncoder;
  
  //Create PID Controller object 
  private final PIDController floorPID =
    new PIDController(
      FloorLifterConstants.kP,
      FloorLifterConstants.kI,
      FloorLifterConstants.kD
    ); 

  public double getAngleRadians(){
    return floorLifterEncoder.getPosition();
  }

  private FloorLiftState currentState = FloorLiftState.DOWN;

  //Create Tab in Shuffleboard
  private final ShuffleboardTab FloorLiftTab = Shuffleboard.getTab("Floor Lifter");
  
  
  public FloorLifterSubsystem() {

    floorLifterEncoder = floorLifterMotor.getEncoder();

    // Convert motor rotations → mechanism radians
    floorLifterConfig.encoder.positionConversionFactor(
      2.0 * Math.PI / FloorLifterConstants.kGearRatio
    );

    //Configure motor controller
    floorLifterConfig
      .inverted(FloorLifterConstants.kFloorLifterInverted)
      .smartCurrentLimit(FloorLifterConstants.kCurrentLimit)
      .openLoopRampRate(FloorLifterConstants.kRampRate)
      .voltageCompensation(FloorLifterConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
    floorLifterMotor.configure(
      floorLifterConfig, 
      com.revrobotics.ResetMode.kResetSafeParameters, 
      com.revrobotics.PersistMode.kPersistParameters);

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
    FloorLiftTab.addNumber("Floor Lift kP", () -> FloorLifterConstants.kP);
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
      getAngleRadians() - currentState.radians
    ) < FloorLifterConstants.kLifterTolerance;
  }

  public void setState(FloorLiftState newState){
    if (newState == currentState) {
      return;
    }
    currentState = newState;
  }

  @Override
  public void periodic() {
    double currentAngle = getAngleRadians();
    double targetAngle = currentState.radians;
    double pidOutput = floorPID.calculate(currentAngle, targetAngle);
    pidOutput = MathUtil.clamp(pidOutput, -12.0,12.0);

    //apply soft limit
     if ((currentAngle >= FloorLifterConstants.kUpSoftLimit && pidOutput > 0) ||
      (currentAngle <= FloorLifterConstants.kDownSoftLimit && pidOutput < 0)) {
        pidOutput = 0;
    }

    if (DriverStation.isEnabled()){
      floorLifterMotor.setVoltage(pidOutput);
    }
    else {
      floorLifterMotor.stopMotor();
    }
  }
}
