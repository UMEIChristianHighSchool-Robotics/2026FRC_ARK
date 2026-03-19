// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

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
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  //------ create Enum to setup states with positions-------/
  
  public enum IntakeState{
    UP(IntakeConstants.kUpRadians),
    TRAVEL(IntakeConstants.kTravelRadians),
    INTAKE(IntakeConstants.kIntakeRadians);

    public final double radians;

    IntakeState(double radians){
      this.radians = radians;
    }
 
 }

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
  
  private IntakeState currentState;

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
  
  //Create a tab in Shuffleboard
  private final ShuffleboardTab intakeTab= Shuffleboard.getTab("Intake");
  

  @SuppressWarnings("removal")
  public IntakeSubsystem() {
 
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

    //ensures software state matches reallife on boot
    double angle = getAngleRadians();

    if (Math.abs(angle - IntakeState.UP.radians) < IntakeConstants.kDeployTolerance) {
        currentState = IntakeState.UP;
    } 
    else if (Math.abs(angle - IntakeState.TRAVEL.radians) < IntakeConstants.kDeployTolerance) {
        currentState = IntakeState.TRAVEL;
    } 
    else if (Math.abs(angle - IntakeState.INTAKE.radians) < IntakeConstants.kDeployTolerance) {
    currentState = IntakeState.INTAKE;
    }
    else {
    // If it's somewhere unexpected, choose the safest position
    currentState = IntakeState.UP;
    }

  //Telemetry
    intakeTab.add("Intake kP",IntakeConstants.kP);
    intakeTab.add("Intake kGff",IntakeConstants.kGFeedForward);
    intakeTab.add("FWD Soft Limit",IntakeConstants.kForwardSoftLimit);
    intakeTab.add("REV Soft Limit",IntakeConstants.kReverseSoftLimit);

    intakeTab.addDouble("Angle Error", () -> currentState.radians - getAngleRadians());

    intakeTab.addDouble("Deploy Angle (rad)", this::getAngleRadians);
    intakeTab.addDouble("Target Angle (rad)", () -> currentState.radians);
    intakeTab.addBoolean("At Setpoint", deployPID::atSetpoint);
    intakeTab.addDouble("Roller Output", ()->intakeRoller.get());

  }

  //---------------Intake Roller Methods----------------------//

  public void runRoller(double volts) {
      intakeRoller.setVoltage(MathUtil.clamp(volts, -12.0, 12.0));
  }

  public void stopRoller() {
      intakeRoller.stopMotor();
  }

  //---------------Intake Deploy Methods----------------------//
  
  //method to check what state deploy system is in
  public boolean isNearState(IntakeState state) {
    return Math.abs(getAngleRadians() - state.radians) 
        < IntakeConstants.kDeployTolerance;
  }
  
  //method to ensure intake deploy system is in the UP position at startup
  public void ensureUpOnEnable() {
    if (!isNearState(IntakeState.UP)) {
      setState(IntakeState.UP);
    }
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

  
  @Override
  public void periodic() {
    
    double currentAngle = intakeDeployEncoder.get(); //reads current angle from encoder
    double targetAngle = currentState.radians;

    double pidOutput = deployPID.calculate(currentAngle, targetAngle); //calculates correction voltage to reach target
    double ff = feedforward.calculate(currentAngle, 0.0);//feedforward for gravity compensation
    double outputVolts = pidOutput + ff;

    // Minimum movement boost
    if (Math.abs(targetAngle-currentAngle) > 0.08) {
    outputVolts += Math.copySign(0.35, pidOutput);
    }

outputVolts = MathUtil.clamp(outputVolts, -12.0, 12.0);
   
    //apply software soft limits
    if ((currentAngle >= IntakeConstants.kForwardSoftLimit && outputVolts > 0) ||
      (currentAngle <= IntakeConstants.kReverseSoftLimit && outputVolts < 0)) {
        outputVolts = 0;
    }
    
    // Only apply voltage when robot is enabled
    if (DriverStation.isEnabled()) {
    intakeDeployMotor.setVoltage(outputVolts);
} else {
    intakeDeployMotor.stopMotor();
}
  }    
   
}