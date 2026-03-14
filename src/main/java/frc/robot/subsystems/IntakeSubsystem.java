// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  //------ create Enum to set up states with positions-------/
  
  public enum IntakeState{
    UP(IntakeConstants.kUpRadians),
    TRAVEL(IntakeConstants.kTravelRadians),
    INTAKE(IntakeConstants.kIntakeRadians);

    public final double radians;

    IntakeState(double radians){
      this.radians = radians;
    }
  }

  //-------Declare the state it thinks it is in; Drivers have to put the mechanism in the up position-------/
  private IntakeState currentState = IntakeState.UP; 

  //-------Declare and initialize the motor controllers-----------/
  
  // Roller motor: NEO Vortex on Spark Flex
  private SparkFlex intakeRoller = new SparkFlex(IntakeConstants.kIntakeRollerCANID, MotorType.kBrushless);
 
  // Deploy motor: NEO V1 on Spark MAX controller
  private SparkMax intakeDeployMotor = new SparkMax(IntakeConstants.kIntakeDeployCANID, MotorType.kBrushless);
  
  //---------Declare and initialize the motor controller configurations
  private SparkMaxConfig intakeDeployConfig = new SparkMaxConfig();
  private SparkFlexConfig intakeRollerConfig = new SparkFlexConfig();

  //Through-bore encoder connected to roboRIO DIO -> use DutyCycleEncoder 
  private DutyCycleEncoder intakeDeployEncoder =
      new DutyCycleEncoder(IntakeConstants.kDeployEncoderDIOPort,
        2.0 * Math.PI,  // full range
        IntakeConstants.kZeroOffset  //mechanical zero
      ); 
  
  public double getAngleRadians() {
    return intakeDeployEncoder.get();
  }
  
  //create feedforward object
  private final ArmFeedforward feedforward =
    new ArmFeedforward(
        IntakeConstants.kSFeedForward,  
        IntakeConstants.kGFeedForward,
        IntakeConstants.kVFeedForward 
    );
  
  //create PID Controller object
  private final PIDController deployPID =
    new PIDController(
        IntakeConstants.kP,  
        IntakeConstants.kI,
        IntakeConstants.kD
    );
  
  public IntakeSubsystem() {
 
    //Configure motor controllers inside the constructor
    //---------Configure intake roller---------
    intakeRollerConfig  
      .inverted(IntakeConstants.kFeederInverted)  
      .smartCurrentLimit(IntakeConstants.kFeederCurrentLimit)
      .openLoopRampRate(IntakeConstants.kFeederRampRate)
      .voltageCompensation(IntakeConstants.kFeederVoltCompensation)
      .idleMode(IdleMode.kCoast);
  
    //apply configuration to the motor  
    intakeRoller.configure(intakeRollerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  
    //---------Configure intake deploy motor---------
    intakeDeployConfig
      .inverted(IntakeConstants.kDeployInverted)  
      .smartCurrentLimit(IntakeConstants.kDeployCurrentLimit)
      .openLoopRampRate(IntakeConstants.kDeployRampRate)
      .voltageCompensation(IntakeConstants.kDeployVoltCompensation)
      .idleMode(IdleMode.kBrake);
    
    //apply configuration to the motor  
    intakeDeployMotor.configure(intakeDeployConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    deployPID.setTolerance(IntakeConstants.kDeployTolerance);

    SmartDashboard.putNumber("Intaking feeder roller value", IntakeConstants.kIntakingFeederVoltage);
    SmartDashboard.putNumber("Intaking intake roller value", IntakeConstants.kIntakingIntakeVoltage);
    SmartDashboard.putNumber("Spin-up feeder roller value", IntakeConstants.kSpinUpFeederVoltage);
  }

  //method to change deploy states
  
  public boolean isAtTarget() {
    return Math.abs(
        intakeDeployEncoder.get() - currentState.radians
    ) < 0.05;
  }

  public void setState(IntakeState newState) {
    if (newState == currentState) {
        return;
    }
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
    
    //read current angle from DIO encoder
    double currentAngle = intakeDeployEncoder.get();
    
    // PID calculates correction voltage to reach target
    double PIDOutput = deployPID.calculate(currentAngle,currentState.radians);
   
    // Feedforward for gravity compensation
    double ff = feedforward.calculate(currentState.radians,0);
    
    // Combine PID and feedforward
    double outputVolts = PIDOutput + ff;
    
    //apply software soft limits
    if ((currentAngle >= IntakeConstants.kForwardSoftLimit && outputVolts > 0) ||
      (currentAngle <= IntakeConstants.kReverseSoftLimit && outputVolts < 0)) {
        outputVolts = 0;
    }
    
    // Clamp to motor voltage range
     outputVolts = MathUtil.clamp(outputVolts, -12, 12);

    // Only apply voltage when robot is enabled
    if (DriverStation.isEnabled()) {
        intakeDeployMotor.setVoltage(outputVolts);
    }
    
    // Optional: SmartDashboard telemetry
    SmartDashboard.putNumber("Deploy Angle (rad)", currentAngle);
    SmartDashboard.putNumber("Deploy Angle (deg)", Math.toDegrees(currentAngle));
    SmartDashboard.putString("Intake State", currentState.toString());
  }
}