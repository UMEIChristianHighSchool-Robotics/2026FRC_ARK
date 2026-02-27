package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.MathUtil;

import static frc.robot.Constants.OperatorConstants;
import static frc.robot.Constants.DriveConstants;

public class DiffDriveSubsystem extends SubsystemBase {

  //Declare and initialize the motor controllers
  private SparkMax leftLeader = new SparkMax(DriveConstants.kLeftLeaderCANID, SparkLowLevel.MotorType.kBrushless);
  private SparkMax leftFollower = new SparkMax(DriveConstants.kLeftFollowerCANID, SparkLowLevel.MotorType.kBrushless);
  private SparkMax rightLeader = new SparkMax(DriveConstants.kRightLeaderCANID, SparkLowLevel.MotorType.kBrushless);
  private SparkMax rightFollower = new SparkMax(DriveConstants.kRightFollowerCANID, SparkLowLevel.MotorType.kBrushless);
 
// declare configurations
  private SparkMaxConfig leftLeaderConfig = new SparkMaxConfig();
  private SparkMaxConfig rightLeaderConfig = new SparkMaxConfig();
  private SparkMaxConfig rightFollowerConfig = new SparkMaxConfig();
  private SparkMaxConfig leftFollowerConfig = new SparkMaxConfig();

  private final DifferentialDrive m_drive = new DifferentialDrive(leftLeader,rightLeader);

  // declare a variable called driveScaleChooser and initilaize an instance for selecting desired drive speed/scale/power
  private SendableChooser<Double> driveScaleChooser = new SendableChooser<>();

  
  public DiffDriveSubsystem() {
  
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
      .follow(DriveConstants.kLeftLeaderCANID,DriveConstants.kLeftInverted)
      .smartCurrentLimit(DriveConstants.kCurrentLimit)
      .openLoopRampRate(DriveConstants.kRampRate)
      .voltageCompensation(DriveConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
    
    rightFollowerConfig
      .follow(DriveConstants.kRightLeaderCANID,DriveConstants.kLeftInverted)
      .smartCurrentLimit(DriveConstants.kCurrentLimit)
      .openLoopRampRate(DriveConstants.kRampRate)
      .voltageCompensation(DriveConstants.kVoltCompensation)
      .idleMode(IdleMode.kBrake);
 
    leftLeader.configure(leftLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    rightLeader.configure(rightLeaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    leftFollower.configure(leftFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    rightFollower.configure(rightFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    m_drive.setSafetyEnabled(true);
  
     // label for SmartDashboard
    SmartDashboard.putString("Drivetrain ", "Select Power");

     // list drive scale/power/speed options for SmartDashboard
    driveScaleChooser.addOption("100%", 1.0);
    driveScaleChooser.setDefaultOption("75%", 0.75);
    driveScaleChooser.addOption("50%", 0.5);
    driveScaleChooser.addOption("25%", 0.25);

    // set the selected driveScaleChooser value
     SmartDashboard.putData("Drivetrain Speed", driveScaleChooser);
    
}

public double getDriveScale() {
    if (driveScaleChooser.getSelected() != null) {
        return driveScaleChooser.getSelected();
    } else {
        return 1.0; // default scale if nothing selected
    }
}
 
/** Sets left and right motor power directly for tank drive */
public void setTankPower(double left, double right) {
    m_drive.tankDrive(left, right);
}
/** Sets speed and rotation for arcade drive */
public void setArcadePower(double speed, double rotation) {
    m_drive.arcadeDrive(speed, rotation);
}

 public Command tankDrive(DoubleSupplier left, DoubleSupplier right) {
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