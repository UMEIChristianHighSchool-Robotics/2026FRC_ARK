// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

//Adapted from CHAT GPT audit

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

// --- Enums --- 

  public enum IntakePivotState{
    IN(IntakePivotConstants.kInRadians),
    TRAVEL (IntakePivotConstants.kTravelRadians),
    OUT(IntakePivotConstants.kOutRadians);

    public final double radians;

    IntakePivotState(double radians){
      this.radians = radians;
    }
  }

// --- Hardware --- 
// NEO V1 on Spark MAX controller
  private SparkMax intakePivotMotor = new SparkMax(IntakePivotConstants.kIntakePivotCANID, MotorType.kBrushless);
  private SparkMaxConfig intakePivotConfig = new SparkMaxConfig();
  private RelativeEncoder intakePivotEncoder;
    
// --- PID ---
  private final PIDController intakePivotPID=
    new PIDController(
      IntakePivotConstants.kP,
      IntakePivotConstants.kI,
      IntakePivotConstants.kD
    );

// --- State tracking --- 
private IntakePivotState currentState = IntakePivotState.IN;
private boolean isManual = false; //field to allow for manual control on joystick
private double manualOutput = 0.0; //field to allow for manual control on joystick
private double holdAngleRadians = 0.0; //field to allow for the joystick position to hold on release

// --- Telemetry fields --- 
private final ShuffleboardTab IntakeTab = Shuffleboard.getTab("Intake");

// --- Contructor ---
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
  
  holdAngleRadians = getAngleRadians();

  //Telemetry
 // --- Telemetry / Shuffleboard ---
IntakeTab.addNumber("Current Angle (rad)", this::getAngleRadians);
IntakeTab.addNumber("Target Angle (rad)", () -> isManual ? getAngleRadians() : holdAngleRadians);
IntakeTab.addNumber("Error (rad)", () -> (isManual ? 0.0 : holdAngleRadians - getAngleRadians()));
IntakeTab.addBoolean("Manual Mode", () -> isManual);

// PID and Feedforward outputs
IntakeTab.addNumber("PID Output (V)", () -> {
    if (isManual) return 0.0;
    return intakePivotPID.calculate(getAngleRadians(), holdAngleRadians);
});
IntakeTab.addNumber("Gravity FF (V)", 
    () -> IntakePivotConstants.kG * Math.sin(getAngleRadians() - IntakePivotConstants.kOffset));
IntakeTab.addNumber("Total Voltage (V)", () -> {
    double pidOutput = isManual ? manualOutput * IntakePivotConstants.kManualVoltageScale
                                : intakePivotPID.calculate(getAngleRadians(), holdAngleRadians);
        double gravityFF = IntakePivotConstants.kG * Math.sin(holdAngleRadians);
    return MathUtil.clamp(pidOutput + gravityFF, -12.0, 12.0);
});

// Soft limits
IntakeTab.addBoolean("At Upper Soft Limit", () -> getAngleRadians() >= IntakePivotConstants.kInSoftLimit);
IntakeTab.addBoolean("At Lower Soft Limit", () -> getAngleRadians() <= IntakePivotConstants.kOutSoftLimit);

}

public double getAngleRadians(){
   return intakePivotEncoder.getPosition();
  }

//methods for manual control
public void setManualOutput(double output){
  isManual = true;
  manualOutput = output;
}

public void disableManual(){
  holdAngleRadians = getAngleRadians();
  intakePivotPID.reset();
  isManual = false;
}

public boolean isManualMode(){
  return isManual;
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
    holdAngleRadians = newState.radians;
    intakePivotPID.reset();
    isManual=false;
  }

  @Override
  public void periodic() {

    double currentAngle = getAngleRadians();
    double output;
   
    if(isManual){
      output = manualOutput*IntakePivotConstants.kManualVoltageScale;
    }
    else {
      double pidOutput = intakePivotPID.calculate(currentAngle,holdAngleRadians);
      double gravityFF = IntakePivotConstants.kG*Math.sin(currentAngle-IntakePivotConstants.kOffset);
      output = pidOutput + gravityFF;
    }

    output = MathUtil.clamp(output, -12.0,12.0);

    // --- Soft Limits ---
     if ((currentAngle >= IntakePivotConstants.kInSoftLimit && output > 0) ||
      (currentAngle <= IntakePivotConstants.kOutSoftLimit && output < 0)) {
        output = 0;
    }

    if (DriverStation.isEnabled()){
      intakePivotMotor.setVoltage(output);
    } else {
      intakePivotMotor.stopMotor();
    }

  }
}