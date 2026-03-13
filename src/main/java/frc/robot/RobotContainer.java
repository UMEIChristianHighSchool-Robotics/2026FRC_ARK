// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
//import edu.wpi.first.wpilibj2.command.RunCommand;
//import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
//import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.AutoDriveCommand;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.ShootCommand;
import frc.robot.commands.SpinUpShooterCommand;
import frc.robot.commands.RunIntakeRollerCommand;
import frc.robot.commands.IntakeDownCommand;
import frc.robot.commands.IntakeTravelCommand;
import frc.robot.commands.IntakeUpCommand;
import frc.robot.commands.ReverseIntakeRollerCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakeSubsystem.IntakeState;
import frc.robot.commands.DeployAndIntakeCommand;
import frc.robot.commands.ShooterIdleCommand;
import frc.robot.commands.StopShooterCommand;

import static frc.robot.Constants.OperatorConstants;

public class RobotContainer {
  
 /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */

  //Subsystems
  public final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  public final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  public final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
  
  //Xbox Controller
  public final CommandXboxController m_controller = new CommandXboxController(OperatorConstants.kControllerPort);

  //The autonomous chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  //Commands
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_controller);
  public final RunIntakeRollerCommand m_runIntakeRollerCommand = new RunIntakeRollerCommand(m_intakeSubsystem);
  public final ReverseIntakeRollerCommand m_reverseIntakeRollerCommand = new ReverseIntakeRollerCommand(m_intakeSubsystem);
  public final IntakeDownCommand m_intakeDownCommand = new IntakeDownCommand(m_intakeSubsystem);
  public final DeployAndIntakeCommand m_deployAndIntakeCommand = new DeployAndIntakeCommand(m_intakeSubsystem);
  public final IntakeUpCommand m_intakeUpCommand = new IntakeUpCommand(m_intakeSubsystem);
  public final IntakeTravelCommand m_intakeTravelCommand = new IntakeTravelCommand(m_intakeSubsystem);
  public final ShootCommand m_shootCommand = new ShootCommand(m_shooterSubsystem, m_intakeSubsystem);
  public final ShooterIdleCommand m_shooterIdleCommand = new ShooterIdleCommand();public final SpinUpShooterCommand m_spinUpShooterCommand = new SpinUpShooterCommand(m_shooterSubsystem, ShooterConstants.kShooterSpinUp);
  public final StopShooterCommand m_stopShooterCommand = new StopShooterCommand(m_shooterSubsystem);
  public final AutoDriveCommand m_autoDriveCommand = new AutoDriveCommand(m_driveSubsystem, OperatorConstants.kxSpeed, OperatorConstants.kzRotation);
  
  public RobotContainer() {
    // This is the constructor of the robot container
    m_driveSubsystem.setDefaultCommand(m_driveCommand);
    m_intakeSubsystem.setDefaultCommand(new RunCommand(m_intakeSubsystem::stopRoller, m_intakeSubsystem));
  
    // Configure the trigger bindings
    configureBindings();
 
    //when the robot initializes
    m_intakeSubsystem.setState(IntakeState.TRAVEL);
    
  // Shuffleboard display for monitoring and Troubleshooting

    Shuffleboard.getTab("DriveTrainDisplay")
      .addNumber("Left Y", this::getLeftYValue)
      .withPosition(6, 3);

    Shuffleboard.getTab("DriveTrainDisplay")
      .addNumber("Right Y", this::getRightYValue)
      .withPosition(6, 4);
 
      // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("TeleOp", m_driveCommand);
   //autoChooser.addOption("Autonomous", m_autoSequenceCommand);
 
  }

  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    //new Trigger(m_exampleSubsystem::exampleCondition)
    //    .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is
    // pressed,
    // cancelling on release.
    // m_controller.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
 
  
  
    // Press the a button to lower the intake to the down position
    m_controller.a().onTrue(new IntakeDownCommand(m_intakeSubsystem));
    //Press the b button to lift the intake to the up position
    m_controller.b().onTrue(new IntakeUpCommand(m_intakeSubsystem));
    
    //Hold the left trigger to turn on the intake roller and intake balls 
    m_controller.leftTrigger().whileTrue(new RunIntakeRollerCommand(m_intakeSubsystem));
    //Hold the left bumper to reverse the direction of the intake roller to clear jams 
    m_controller.leftBumper().whileTrue(new ReverseIntakeRollerCommand(m_intakeSubsystem));
  
    //Hold the right bumper to spin up the launcher roller
    m_controller.rightBumper()
    .whileTrue(new SpinUpShooterCommand(m_shooterSubsystem, ShooterConstants.kShooterSpinUp));
  
   }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return m_driveCommand;
  }

  // creates methods which read the joystick values for display on the
  // shuffleboard
  public double getLeftYValue() {
  return -m_controller.getLeftY();
}

public double getRightYValue() {
  return -m_controller.getRightY();
}


}
