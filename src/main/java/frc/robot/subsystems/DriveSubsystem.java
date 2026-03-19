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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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

  //Encoders
  private RelativeEncoder leftEncoder;
  private RelativeEncoder rightEncoder;

  //Distance PID Controller
  private PIDController distancePID =
    new PIDController(
      DriveConstants.kDistanceP,
      DriveConstants.kDistanceI,
      DriveConstants.kDistanceD
    );
  
  //Turn PID Controller
   private PIDController turnPID =
    new PIDController(
      DriveConstants.kTurnP,
      DriveConstants.kTurnI,
      DriveConstants.kTurnD
    );

  //Declare configurations
  private SparkMaxConfig leftLeaderConfig = new SparkMaxConfig();
  private SparkMaxConfig rightLeaderConfig = new SparkMaxConfig();
  private SparkMaxConfig rightFollowerConfig = new SparkMaxConfig();
  private SparkMaxConfig leftFollowerConfig = new SparkMaxConfig();

  private final DifferentialDrive m_drive = new DifferentialDrive(leftLeader,rightLeader);

  //Create a tab in Shuffleboard
  private ShuffleboardTab driveTab= Shuffleboard.getTab("Drive");
  private ShuffleboardTab autoTab= Shuffleboard.getTab("Auto");

  // declare a variable called driveScaleChooser and initilaize an instance for selecting desired drive speed/scale/power
  private SendableChooser<Double> driveScaleChooser = new SendableChooser<>();

  
  @SuppressWarnings("removal")
  public DriveSubsystem() {
    //pull in the built-in encoders & closed loop control inside the constructor
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
    
 
    // list drive scale/power/speed options for Shuffleboard
    driveScaleChooser.addOption("100%", 1.0);
    driveScaleChooser.setDefaultOption("75%", 0.75);
    driveScaleChooser.addOption("50%", 0.5);
    driveScaleChooser.addOption("25%", 0.25);

    // Telemetry
    driveTab.add("Drivetrain Speed", driveScaleChooser);
    driveTab.addDouble("Left Encoder",()-> leftEncoder.getPosition());
    driveTab.addDouble("Right Encoder",()-> rightEncoder.getPosition());
    driveTab.addDouble("Distance (m)", this::getDistanceMeters);
    autoTab.addDouble("Distance (m)", this::getDistanceMeters);

}

public double getDriveScale() {
    if (driveScaleChooser.getSelected() != null) {
        return driveScaleChooser.getSelected();
    } else {
        return 1.0; // default scale if nothing selected
    }
}

public double getDistanceMeters() {
    return (leftEncoder.getPosition() + rightEncoder.getPosition()) / 2.0;
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

 
public Command DriveCommand(DoubleSupplier left, DoubleSupplier right) {
  return run(() -> {
    double scale = driveScaleChooser.getSelected();

    m_drive.tankDrive(
        MathUtil.applyDeadband(left.getAsDouble(), OperatorConstants.kDeadband) * scale,
        MathUtil.applyDeadband(right.getAsDouble(), OperatorConstants.kDeadband) * scale
    );
  });
}

  public Command arcadeDrive(DoubleSupplier xSpeed, DoubleSupplier zRotation)
  {
    return run(() -> m_drive.arcadeDrive(xSpeed.getAsDouble(), zRotation.getAsDouble()));
  }

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