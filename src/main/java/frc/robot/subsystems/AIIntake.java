package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {

  // Deploy motor: NEO V1 on Spark MAX
  private final SparkMax deployMotor =
      new SparkMax(IntakeConstants.kDeployMotorCANID, MotorType.kBrushless);

  // Roller motor: NEO Vortex on Spark Flex
  private final SparkFlex rollerMotor =
      new SparkFlex(IntakeConstants.kRollerMotorCANID, MotorType.kBrushless);

  private final SparkMaxConfig deployMotorConfig = new SparkMaxConfig();
  private final SparkFlexConfig rollerMotorConfig = new SparkFlexConfig();

  // Through-bore absolute encoder on roboRIO DIO
  private final DutyCycleEncoder deployAbsoluteEncoder =
      new DutyCycleEncoder(IntakeConstants.kDeployAbsoluteEncoderDIO);

  // PID for deploy position control
  private final PIDController deployPID =
      new PIDController(
          IntakeConstants.kDeploykP,
          IntakeConstants.kDeploykI,
          IntakeConstants.kDeploykD);

  private double targetAngleDeg = IntakeConstants.kReadyAngleDeg;
  private boolean positionControlEnabled = true;

  public IntakeSubsystem() {
    deployMotorConfig
        .inverted(IntakeConstants.kDeployInverted)
        .smartCurrentLimit(IntakeConstants.kDeployCurrentLimit)
        .openLoopRampRate(IntakeConstants.kRampRate)
        .voltageCompensation(IntakeConstants.kVoltCompensation)
        .idleMode(IdleMode.kBrake);

    rollerMotorConfig
        .inverted(IntakeConstants.kRollerInverted)
        .smartCurrentLimit(IntakeConstants.kRollerCurrentLimit)
        .openLoopRampRate(IntakeConstants.kRampRate)
        .voltageCompensation(IntakeConstants.kVoltCompensation)
        .idleMode(IdleMode.kBrake);

    deployMotor.configure(
        deployMotorConfig,
        ResetMode.kResetSafeParameters,
        PersistMode.kPersistParameters);

    rollerMotor.configure(
        rollerMotorConfig,
        ResetMode.kResetSafeParameters,
        PersistMode.kPersistParameters);

    deployPID.setTolerance(IntakeConstants.kDeployToleranceDeg);

    SmartDashboard.putNumber("Deploy Angle Raw", 0.0);
    SmartDashboard.putNumber("Deploy Angle", 0.0);
    SmartDashboard.putNumber("Deploy Target Angle", targetAngleDeg);
    SmartDashboard.putNumber("Deploy PID Output", 0.0);
    SmartDashboard.putBoolean("Deploy In Exclusion Zone", false);
    SmartDashboard.putNumber("Deploy Control Target", 0.0);
    SmartDashboard.putNumber("Deploy Control Measurement", 0.0);
  }

  public double getDeployAngleDeg() {
    double rawAngleDeg = deployAbsoluteEncoder.get() * 360.0;
    double correctedAngleDeg = rawAngleDeg - IntakeConstants.kEncoderOffsetDeg;

    correctedAngleDeg %= 360.0;
    if (correctedAngleDeg < 0) {
      correctedAngleDeg += 360.0;
    }

    return correctedAngleDeg;
  }

  private double normalize0To360(double angleDeg) {
    angleDeg %= 360.0;
    if (angleDeg < 0) {
      angleDeg += 360.0;
    }
    return angleDeg;
  }

  private boolean isInExclusionZone(double angleDeg) {
    angleDeg = normalize0To360(angleDeg);
    return angleDeg > IntakeConstants.kExclusionZoneStartDeg
        && angleDeg < IntakeConstants.kExclusionZoneEndDeg;
  }

  private double getControlTargetDeg(double currentAngleDeg, double targetAngleDeg) {
    currentAngleDeg = normalize0To360(currentAngleDeg);
    targetAngleDeg = normalize0To360(targetAngleDeg);

    // If target is retracted and current is on the safe side,
    // force travel downward through 0/360 instead of through the exclusion zone.
    if (targetAngleDeg >= IntakeConstants.kExclusionZoneEndDeg
        && currentAngleDeg <= IntakeConstants.kExclusionZoneStartDeg) {
      return targetAngleDeg - 360.0;
    }

    return targetAngleDeg;
  }

  private double getControlMeasurementDeg(double currentAngleDeg, double controlTargetDeg) {
    currentAngleDeg = normalize0To360(currentAngleDeg);

    // If target is negative (example: 323 becomes -37),
    // map high-end measurements like 359 to -1, 350 to -10, etc.
    if (controlTargetDeg < 0.0 && currentAngleDeg >= IntakeConstants.kExclusionZoneEndDeg) {
      return currentAngleDeg - 360.0;
    }

    return currentAngleDeg;
  }

  public void setTargetAngleDeg(double angleDeg) {
    targetAngleDeg = normalize0To360(angleDeg);
    positionControlEnabled = true;
  }

  public void holdCurrentPosition() {
    targetAngleDeg = getDeployAngleDeg();
    positionControlEnabled = true;
  }

  public boolean atTarget() {
    return deployPID.atSetpoint();
  }

  public void stopAndHoldDeploy() {
    holdCurrentPosition();
  }

  public void runRollerIn() {
    rollerMotor.setVoltage(IntakeConstants.kRollerIntakeVoltage);
  }

  public void runRollerOut() {
    rollerMotor.setVoltage(IntakeConstants.kRollerEjectVoltage);
  }

  public void stopRoller() {
    rollerMotor.setVoltage(0.0);
  }

  public void stopAll() {
    stopRoller();
    holdCurrentPosition();
  }

  @Override
  public void periodic() {
    double currentAngleDeg = getDeployAngleDeg();

    // Hard safety stop if somehow the mechanism enters the forbidden range
    if (isInExclusionZone(currentAngleDeg)) {
      deployMotor.setVoltage(0.0);

      SmartDashboard.putBoolean("Deploy In Exclusion Zone", true);
      SmartDashboard.putNumber("Deploy Angle Raw", deployAbsoluteEncoder.get() * 360.0);
      SmartDashboard.putNumber("Deploy Angle", currentAngleDeg);
      SmartDashboard.putNumber("Deploy Target Angle", targetAngleDeg);
      SmartDashboard.putNumber("Deploy PID Output", 0.0);
      SmartDashboard.putNumber("Deploy Control Target", 0.0);
      SmartDashboard.putNumber("Deploy Control Measurement", currentAngleDeg);
      return;
    } else {
      SmartDashboard.putBoolean("Deploy In Exclusion Zone", false);
    }

    if (positionControlEnabled) {
      double controlTargetDeg = getControlTargetDeg(currentAngleDeg, targetAngleDeg);
      double controlMeasurementDeg = getControlMeasurementDeg(currentAngleDeg, controlTargetDeg);

      double output = deployPID.calculate(controlMeasurementDeg, controlTargetDeg);

      output = MathUtil.clamp(
          output,
          -IntakeConstants.kDeployMaxVoltage,
          IntakeConstants.kDeployMaxVoltage);

      deployMotor.setVoltage(output);

      SmartDashboard.putNumber("Deploy PID Output", output);
      SmartDashboard.putNumber("Deploy Control Target", controlTargetDeg);
      SmartDashboard.putNumber("Deploy Control Measurement", controlMeasurementDeg);
    } else {
      deployMotor.setVoltage(0.0);
      SmartDashboard.putNumber("Deploy PID Output", 0.0);
      SmartDashboard.putNumber("Deploy Control Target", 0.0);
      SmartDashboard.putNumber("Deploy Control Measurement", currentAngleDeg);
    }

    SmartDashboard.putNumber("Deploy Angle Raw", deployAbsoluteEncoder.get() * 360.0);
    SmartDashboard.putNumber("Deploy Angle", currentAngleDeg);
    SmartDashboard.putNumber("Deploy Target Angle", targetAngleDeg);
  }
}