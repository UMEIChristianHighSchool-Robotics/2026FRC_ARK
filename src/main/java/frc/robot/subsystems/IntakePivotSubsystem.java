// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Constants.IntakePivotConstants;

public class IntakePivotSubsystem extends SubsystemBase {

  //------ create Enum to setup states with positions-------/
  public enum IntakePivotState{
    IN(IntakePivotConstants.kInRadians),
    TRAVEL (IntakePivotConstants.kTravelRadians),
    OUT(IntakePivotConstants.kOutRadians);

    public final double radians;

    IntakePivotState(double radians){
      this.radians = radians;
    }
  }

  //-------Declare and initialize the motor controllers-----------/
  // Pivot motor: NEO V1 on Spark MAX controller
  private SparkMax intakePivotMotor = new SparkMax(IntakePivotConstants.kIntakePivotCANID, MotorType.kBrushless);
  
  //---------Declare and initialize the motor controller configurations
  private SparkMaxConfig intakePivotConfig = new SparkMaxConfig();

  //Relative Encoder (built-in accessed via sparkmax)
  private RelativeEncoder intakePivotEncoder;
  
  private final PIDController intakePivotPID=
    new PIDController(
      IntakePivotConstants.kP,
      IntakePivotConstants.kI,
      IntakePivotConstants.kD
    );

  private double getAngleRadians(){
    return intakePivotEncoder.getPosition();
  }

  private IntakePivotState currentState = IntakePivotState.IN;
  
  private final ShuffleboardTab IntakeTab = Shuffleboard.getTab("Intake");
  
  public IntakePivotSubsystem() {

    intakePivotEncoder = intakePivotMotor.getEncoder();

    // Convert motor rotations → mechanism radians
    intakePivotConfig.encoder.positionConversionFactor(
      2.0 * Math.PI / IntakePivotConstants.kGearRatio
    );

    //Configure motor controller
    intakePivotConfig
      .inverted(IntakePivotConstants.kIntakePivotInverted)
      .smartCurrentLimit(IntakePivotConstants.kCurrentLimit)
      .openLoopRampRate(IntakePivotConstants.kRampRate)
      .voltageCompensation(IntakePivotConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);

    intakePivotMotor.configure(
      intakePivotConfig, 
      com.revrobotics.ResetMode.kResetSafeParameters, 
      com.revrobotics.PersistMode.kPersistParameters);

    //Configure Positioning
    intakePivotPID.setTolerance(IntakePivotConstants.kIntakePivotTolerance);
    
    //ensure software stat matches reallife on boot
    double angle = getAngleRadians();
    if (Math.abs(angle - IntakePivotState.IN.radians) < IntakePivotConstants.kIntakePivotTolerance) {
      currentState = IntakePivotState.IN;
    }
    else if (Math.abs(angle - IntakePivotState.TRAVEL.radians) < IntakePivotConstants.kIntakePivotTolerance) {
      currentState = IntakePivotState.TRAVEL;
    }
    else if (Math.abs(angle - IntakePivotState.OUT.radians) < IntakePivotConstants.kIntakePivotTolerance) {
      currentState = IntakePivotState.OUT;
    }
  

  //Telemetry
    IntakeTab.addNumber("Floor Lift kP", () -> IntakePivotConstants.kP);
    IntakeTab.addDouble("Angle Error", () -> currentState.radians - getAngleRadians());
    IntakeTab.addDouble("Current Angle (rad)", this::getAngleRadians);
    IntakeTab.addDouble("Target Angle (rad)", () -> currentState.radians);
    IntakeTab.addBoolean("At Setpoint", intakePivotPID::atSetpoint);
  }

  //method to check what state the floor lifter is in
  public boolean isNearState(IntakePivotState state) {
    return Math.abs(getAngleRadians() - state.radians) 
      < IntakePivotConstants.kIntakePivotTolerance;
  }

  //method to ensure floor lift system is in the IN position to start
  public void ensureInOnEnable() {
    if (!isNearState(IntakePivotState.IN)) {
      setState(IntakePivotState.IN);
    }
  }

  //method to change floor lift states
  public boolean isAtTarget() {
    return Math.abs(
      getAngleRadians() - currentState.radians
    ) < IntakePivotConstants.kIntakePivotTolerance;
  }

  public void setState(IntakePivotState newState){
    if (newState == currentState) {
      return;
    }
    currentState = newState;
  }

  @Override
  public void periodic() {
    double currentAngle = getAngleRadians();
    double targetAngle = currentState.radians;
    double pidOutput = intakePivotPID.calculate(currentAngle, targetAngle);
    pidOutput = MathUtil.clamp(pidOutput, -12.0,12.0);

    //apply soft limit
     if ((currentAngle >= IntakePivotConstants.kInSoftLimit && pidOutput > 0) ||
      (currentAngle <= IntakePivotConstants.kOutSoftLimit && pidOutput < 0)) {
        pidOutput = 0;
    }

    if (DriverStation.isEnabled()){
      intakePivotMotor.setVoltage(pidOutput);
    }
    else {
      intakePivotMotor.stopMotor();
    }
  }
}