// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
  
  //Declare and initialize the motor controllers
  private SparkMax shooterMotor = new SparkMax(ShooterConstants.kShooterCANID, MotorType.kBrushless);
  
  //Declare built-in encoders and closed loop controllers to use them for shooting adjustments
  private RelativeEncoder shooterEncoder;
  private SparkClosedLoopController shooterClosedLoopControl;

  //shooter state (SETPOINT)
  private double targetRPM=0.0;
    
  //Declare and initialize the motor controller configurations
  private SparkMaxConfig shooterMotorConfig = new SparkMaxConfig();
 
  //Create a tab in Shuffleboard
  ShuffleboardTab shooterTab= Shuffleboard.getTab("Shooter");
  ShuffleboardTab autoTab= Shuffleboard.getTab("Auto");
  
  @SuppressWarnings("removal")
  public ShooterSubsystem() {

    //pull in the built-in encoders & closed loop control inside the constructor
    shooterEncoder = shooterMotor.getEncoder();
    shooterClosedLoopControl = shooterMotor.getClosedLoopController();
    
    //Configure motor controller inside the constructor   
    shooterMotorConfig  
      .inverted(ShooterConstants.kShooterInverted)  
      .smartCurrentLimit(ShooterConstants.kCurrentLimit)
      .openLoopRampRate(ShooterConstants.kRampRate)
      .voltageCompensation(ShooterConstants.kVoltCompensation)
      .idleMode(IdleMode.kCoast);

    //closed loop (PID) configurations inside the constructor
    shooterMotorConfig.closedLoop
      .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        // Set PID values for velocity control
      .p(ShooterConstants.kP)
      .i(ShooterConstants.kI)
      .d(ShooterConstants.kD)
      .outputRange(-1, 1);
       
    //feedforward config in the constructor
    shooterMotorConfig.closedLoop.feedForward.kV(ShooterConstants.kFF);
   
    //apply the 3 configurations to the motors   
    shooterMotor.configure(shooterMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
    //Telemetry
    shooterTab.addDouble("Right Shooter Speed", shooterEncoder::getVelocity);
    shooterTab.addBoolean("Shooter at speed", this::atSpeed);
    autoTab.addBoolean("Shooter at speed", this::atSpeed);
  }

 // A method to set the velocity of the launching rollers; command can set velocity
  public void setTargetRPM(double rpm) {
    targetRPM = rpm;
   
    shooterClosedLoopControl.setSetpoint(rpm,ControlType.kVelocity);
  }

  //Add an at speed method for autos and consistency; command can wait
  public boolean atSpeed() {
    double tolerance = ShooterConstants.kSpeedTolerance;
    boolean shooterAtSpeed =
      Math.abs(shooterEncoder.getVelocity() - targetRPM) < tolerance;
    return shooterAtSpeed;
  }

  // A method to stop the launching rollers
  public void stop() {
    targetRPM = 0.0;
    shooterMotor.stopMotor();
  }

  @Override
  public void periodic() {
  }
}
