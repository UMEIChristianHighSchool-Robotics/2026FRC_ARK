package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OperatorConstants;

public class DriveSubsystem extends SubsystemBase {

  //Declare and initialize the motor controllers
  private SparkMax leftLeader = new SparkMax(DriveConstants.kLeftLeaderCANID, MotorType.kBrushless);
  private SparkMax leftFollower = new SparkMax(DriveConstants.kLeftFollowerCANID, MotorType.kBrushless);
  private SparkMax rightLeader = new SparkMax(DriveConstants.kRightLeaderCANID, MotorType.kBrushless);
  private SparkMax rightFollower = new SparkMax(DriveConstants.kRightFollowerCANID, MotorType.kBrushless);

  //Declare Encoders
  private RelativeEncoder leftEncoder;
  private RelativeEncoder rightEncoder;

  //Declare and Initialize Distance PID Controller
  private PIDController distancePID =
    new PIDController(
      DriveConstants.kDistanceP,
      DriveConstants.kDistanceI,
      DriveConstants.kDistanceD
    );
  
  //Declare and Initialize Turn PID Controller
   private PIDController turnPID =
    new PIDController(
      DriveConstants.kTurnP,
      DriveConstants.kTurnI,
      DriveConstants.kTurnD
    );

  //Declare and Initialize motor controller configurations
  private SparkMaxConfig leftLeaderConfig = new SparkMaxConfig();
  private SparkMaxConfig rightLeaderConfig = new SparkMaxConfig();
  private SparkMaxConfig rightFollowerConfig = new SparkMaxConfig();
  private SparkMaxConfig leftFollowerConfig = new SparkMaxConfig();

  //Declare and Initialize DifferentialDrive system
  private final DifferentialDrive m_drive = new DifferentialDrive(leftLeader,rightLeader);
  
     
   //Set default Speed Mode to Drive
  private OperatorConstants.SpeedSelect currentSpeed = OperatorConstants.SpeedSelect.DRIVE;
  
  // --- Forward and turn scaling (fields, accessible by commands) --- 
  private double forwardScale = currentSpeed.driveScale;
  private double turnScale = currentSpeed.turnScale;

   //Create tabs in Shuffleboard
  private ShuffleboardTab driveTab= Shuffleboard.getTab("Drive");
  private ShuffleboardTab autoTab= Shuffleboard.getTab("Auto");



  @SuppressWarnings("removal") //for the required API call that is scheduled for removal in 2027


  public DriveSubsystem() {
    
    leftEncoder = leftLeader.getEncoder();
    rightEncoder = rightLeader.getEncoder();
   
    //Configure motor controllers inside the constructor
        
    leftLeaderConfig  
      .inverted(DriveConstants.kLeftInverted)  
      .smartCurrentLimit(DriveConstants.kCurrentLimit)
      .openLoopRampRate(DriveConstants.kRampRate)
      .voltageCompensation(DriveConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
      
    rightLeaderConfig
      .inverted(DriveConstants.kRightInverted)
      .smartCurrentLimit(DriveConstants.kCurrentLimit)
      .openLoopRampRate(DriveConstants.kRampRate)
      .voltageCompensation(DriveConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
    
    leftFollowerConfig
      .follow(DriveConstants.kLeftLeaderCANID,false)
      .smartCurrentLimit(DriveConstants.kCurrentLimit)
      .openLoopRampRate(DriveConstants.kRampRate)
      .voltageCompensation(DriveConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
    
    rightFollowerConfig
      .follow(DriveConstants.kRightLeaderCANID,false)
      .smartCurrentLimit(DriveConstants.kCurrentLimit)
      .openLoopRampRate(DriveConstants.kRampRate)
      .voltageCompensation(DriveConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
 
    //Set position conversion factors in constructor
    leftLeaderConfig.encoder
      .positionConversionFactor(DriveConstants.kMetersPerRotation);
    rightLeaderConfig.encoder
      .positionConversionFactor(DriveConstants.kMetersPerRotation);
  
    //Set PID tolerances in the constructor
    distancePID.setTolerance(DriveConstants.kDistanceTolerance);
    turnPID.setTolerance(DriveConstants.kTurnTolerance);

    //Apply configurations to the motors in the constructor
    leftLeader.configure(leftLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    rightLeader.configure(rightLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    leftFollower.configure(leftFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    rightFollower.configure(rightFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    m_drive.setSafetyEnabled(true);
    
    // Telemetry
    driveTab.addDouble("Left Encoder",()-> leftEncoder.getPosition());
    driveTab.addDouble("Right Encoder",()-> rightEncoder.getPosition());
    driveTab.addDouble("Distance (m)", this::getDistanceMeters);
    driveTab.addString("Current Speed Mode", () -> currentSpeed.name());
    autoTab.addDouble("Distance (m)", this::getDistanceMeters);
}

public double getDistanceMeters() {
    return (leftEncoder.getPosition() + rightEncoder.getPosition()) / 2.0;
}

public double getForwardScale() {
    return forwardScale;
}

public double getTurnScale() {
    return turnScale;
}

public void setForwardScale(double scale) {
    forwardScale = scale;
}

public void setTurnScale(double scale) {
    turnScale = scale;
}

public void setDriveScales(double forward, double turn) {
    forwardScale = forward;
    turnScale = turn;
}

// Set the current speed mode
public void setSpeedMode(OperatorConstants.SpeedSelect speed) {
    currentSpeed = speed;
}

/** Sets left and right motor power directly for tank drive */
public void setTankPower(double left, double right) {
    m_drive.tankDrive(left, right);
}

/** Sets speed and rotation for arcade drive */
public void setArcadePower(double xSpeed, double zRotation) {
    m_drive.arcadeDrive(xSpeed, zRotation);
}

public void resetEncoders(){
  leftEncoder.setPosition(0);
  rightEncoder.setPosition(0);
}

 // Tank drive command with scaling
public Command DriveCommand(DoubleSupplier left, DoubleSupplier right) {
    return run(() -> {
        double leftInput = MathUtil.applyDeadband(left.getAsDouble(), OperatorConstants.kDeadband) * getForwardScale();
        double rightInput = MathUtil.applyDeadband(right.getAsDouble(), OperatorConstants.kDeadband) * getTurnScale();
        m_drive.tankDrive(leftInput, rightInput);
    });
}

// Arcade drive command with scaling
public Command arcadeDrive(DoubleSupplier xSpeed, DoubleSupplier zRotation) {
    return run(() -> {
        double forward = MathUtil.applyDeadband(xSpeed.getAsDouble(), OperatorConstants.kDeadband) * getForwardScale();
        double turn = MathUtil.applyDeadband(zRotation.getAsDouble(), OperatorConstants.kDeadband) * getTurnScale();
        m_drive.arcadeDrive(forward, turn);
    });
}

// 

//method to return drive forward command for autonomous sequence use
  public Command driveForwardMeters(double meters) {
    SlewRateLimiter distanceLimiter = new SlewRateLimiter(DriveConstants.kDistanceSlewRateLimit); // optional

    return run(() -> {
        double current = getDistanceMeters();
        double output = distancePID.calculate(current, meters);
        output = MathUtil.clamp(distanceLimiter.calculate(output), -0.6, 0.6);
        m_drive.arcadeDrive(output, 0);
    })
    .beforeStarting(() -> {
        resetEncoders();
        distancePID.reset();
    })
    .until(distancePID::atSetpoint)
    .finallyDo(interrupted -> stop());
}

 //method to return turn to angle command for autonomous sequence use
 public Command turnRelative(double degrees) {
    
    double wheelDistance = (degrees / 360.0) * DriveConstants.kTurnCircumference;

    return run(() -> {
        double current = leftEncoder.getPosition();
        double output = turnPID.calculate(current, wheelDistance);
  
        output = MathUtil.clamp(output,-0.6,0.6);

        //Left forward, right backward
        setTankPower(output, -output);

        double leftTarget = leftEncoder.getPosition() + wheelDistance;
        double rightTarget = rightEncoder.getPosition() - wheelDistance;

        double leftOutput = MathUtil.clamp(leftTarget - leftEncoder.getPosition(), -0.6, 0.6);
        double rightOutput = MathUtil.clamp(rightTarget - rightEncoder.getPosition(), -0.6, 0.6);

        setTankPower(leftOutput, rightOutput);
    })
    .beforeStarting(this::resetEncoders)
    .until(() -> Math.abs(leftEncoder.getPosition() - wheelDistance) < 0.01
              && Math.abs(rightEncoder.getPosition() + wheelDistance) < 0.01)
    .finallyDo(interrupted -> stop());
}

  public Command stop()
  {
    return run(m_drive::stopMotor);
  }

  @Override
  public void periodic()
  {
  
  }

  @Override
  public void simulationPeriodic()
  {
  }
}