// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.ShooterConstants;

public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new Intake. */
  
  //create Enum inside IntakeSubsystem to set up states with positions
  public enum IntakeState{
    UP(IntakeConstants.kUpRadians),
    TRAVEL(IntakeConstants.kTravelRadians),
    INTAKE(IntakeConstants.kIntakeRadians);

    public final double radians;

    IntakeState(double radians){
      this.radians = radians;
    }
  }

  //set the state it thinks it is in
  private IntakeState currentState = IntakeState.UP; 

  //Declare and initialize the motor controllers
  private SparkFlex intakePivot = new SparkFlex(IntakeConstants.kIntakePivotCANID, MotorType.kBrushless);
  private AbsoluteEncoder pivotEncoder = intakePivot.getAbsoluteEncoder();
  private SparkClosedLoopController pidController = intakePivot.getClosedLoopController();
  private SparkFlex intakeRoller = new SparkFlex(IntakeConstants.kIntakeRollerCANID, MotorType.kBrushless);
  
  //Declare and initialize the motor controller configurations
  private SparkFlexConfig intakePivotConfig = new SparkFlexConfig();
  private SparkFlexConfig intakeRollerConfig = new SparkFlexConfig();

  //create feedforward object
  private final ArmFeedforward feedforward =
    new ArmFeedforward(
        IntakeConstants.kSFeedForward,  // kS (start with 0)
        IntakeConstants.kGFeedForward,  // kG (GUESS starting value)
        IntakeConstants.kVFeedForward   // kV (not needed for position hold initially)
    );
  
  public IntakeSubsystem() {
 
    //Configure motor controllers inside the constructor
    //---------Configure intake roller---------
    intakeRollerConfig  
      .inverted(IntakeConstants.kFeederInverted)  
      .smartCurrentLimit(IntakeConstants.kFeederCurrentLimit)
      .openLoopRampRate(IntakeConstants.kRampRate)
      .voltageCompensation(IntakeConstants.kVoltCompensation)
      .idleMode(IdleMode.kCoast);
  
    //apply configuration to the motor  
    intakeRoller.configure(intakeRollerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  
    //---------Configure intake pivot---------
    intakePivotConfig
      .smartCurrentLimit(IntakeConstants.kPivotCurrentLimit)
      .idleMode(IdleMode.kBrake);
    
    intakePivotConfig.softLimit
      .forwardSoftLimit(IntakeConstants.kForwardSoftLimit)
      .reverseSoftLimit(IntakeConstants.kReverseSoftLimit)
      .forwardSoftLimitEnabled(IntakeConstants.kForwardSoftLimitEnabled)
      .reverseSoftLimitEnabled(IntakeConstants.kReverseSoftLimitEnabled);
        
    intakePivotConfig.absoluteEncoder
        .zeroOffset(IntakeConstants.kAbsoluteZeroOffset)
        .positionConversionFactor(2 * Math.PI)   // rotations → radians
        .velocityConversionFactor(2 * Math.PI);  // RPS → rad/s
    
    //apply configuration to the motor  
    intakePivot.configure(intakePivotConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // configure closed-loop PID on the controller directly
    intakePivotConfig.closedLoop
      .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
        // Set PID values for velocity control
      .p(IntakeConstants.kP)
      .i(IntakeConstants.kI)
      .d(IntakeConstants.kD)
      .outputRange(-1, 1);

    SmartDashboard.putNumber("Intaking feeder roller value", IntakeConstants.kIntakingFeederVoltage);
    SmartDashboard.putNumber("Intaking intake roller value", IntakeConstants.kIntakingIntakeVoltage);
    SmartDashboard.putNumber("Spin-up feeder roller value", IntakeConstants.kSpinUpFeederVoltage);
  }

  //method to change states
  
  public boolean isAtTarget() {
    return Math.abs(
        pivotEncoder.getPosition() - currentState.radians
    ) < 0.05;
  }

  public void setState(IntakeState newState) {

    if (newState == currentState) return;

    currentState = newState;
}

// A method to control the intake roller
public void runRoller(double volts) {
  intakeRoller.setVoltage(volts);
}

public void stopRoller() {
  intakeRoller.stopMotor();
}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    //use periodic for sensor checking
   
    double currentAngle =pivotEncoder.getPosition();
    
    double ff = feedforward.calculate(currentAngle,0);
  
    //applying feedforward only when robot is enabled helps eliminate arm twitch while disabled
    if (DriverStation.isEnabled()) {
      pidController.setSetpoint(
      currentState.radians,
      ControlType.kPosition,
      ClosedLoopSlot.kSlot0,
      ff
    );}
    
    SmartDashboard.putNumber("Pivot Angle (rad)", currentAngle);
    SmartDashboard.putNumber("Pivot Angle (deg)", Math.toDegrees(currentAngle));
    SmartDashboard.putString("Intake State", currentState.toString());
  }
}