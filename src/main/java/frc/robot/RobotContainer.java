// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
//import edu.wpi.first.wpilibj2.command.RunCommand;
//import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
//import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.AutoDriveCommand;
import frc.robot.commands.AutoSequenceCommand;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.EjectCommand;
import frc.robot.commands.LaunchSequenceCommand;
import frc.robot.commands.SpinUpCommand;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.LaunchCommand;
import frc.robot.subsystems.DiffDriveSubsystem;
import frc.robot.subsystems.SnowPlowSubsystem;

import static frc.robot.Constants.OperatorConstants;

public class RobotContainer {
  
 /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */

  //Subsystems
  public final DiffDriveSubsystem m_driveSubsystem = new DiffDriveSubsystem();
  public final SnowPlowSubsystem m_intakeSubsystem = new SnowPlowSubsystem();
  
  //Xbox Controller
  public final CommandXboxController m_controller = new CommandXboxController(OperatorConstants.kControllerPort);

  // The autonomous chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  //Commands
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_controller);
  public final IntakeCommand m_intakeCommand = new IntakeCommand(m_intakeSubsystem);
  public final EjectCommand m_ejectCommand = new EjectCommand(m_intakeSubsystem);
  public final LaunchSequenceCommand m_launchSequenceCommand = new LaunchSequenceCommand(m_intakeSubsystem);
  public final LaunchCommand m_launchCommand = new LaunchCommand(m_intakeSubsystem);
  public final SpinUpCommand m_SpinUpCommand = new SpinUpCommand(m_intakeSubsystem);
  public final AutoDriveCommand m_autoDriveCommand = new AutoDriveCommand(m_driveSubsystem, OperatorConstants.kxSpeed, OperatorConstants.kzRotation);
  public final AutoSequenceCommand m_autoSequenceCommand = new AutoSequenceCommand(m_driveSubsystem, m_intakeSubsystem);

  public RobotContainer() {
    // This is the constructor of the robot container
     m_driveSubsystem.setDefaultCommand(m_driveCommand);
     m_intakeSubsystem.setDefaultCommand(new RunCommand(m_intakeSubsystem::stop, m_intakeSubsystem));

    // Configure the trigger bindings
    configureBindings();
 
    
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
    autoChooser.addOption("Autonomous", m_autoSequenceCommand);
 
  }

  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    //new Trigger(m_exampleSubsystem::exampleCondition)
    //    .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is
    // pressed,
    // cancelling on release.
   // m_controller.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
 
   // While the left bumper on operator controller is held, intake Fuel
    m_controller.leftBumper().whileTrue(new IntakeCommand(m_intakeSubsystem));
    // While the right bumper on the operator controller is held, spin up for 1
    // second, then launch fuel. When the button is released, stop.
    m_controller.rightBumper().whileTrue(new LaunchSequenceCommand(m_intakeSubsystem));
    // While the A button is held on the operator controller, eject fuel back out
    // the intake
    m_controller.a().whileTrue(new EjectCommand(m_intakeSubsystem));

  // Set the default command for the drive subsystem to the command provided by
    // factory with the values provided by the joystick axes on the driver
    // controller. The Y axis of the controller is inverted so that pushing the
    // stick away from you (a negative value) drives the robot forwards (a positive
    // value)
    m_driveSubsystem.setDefaultCommand(new DriveCommand(m_driveSubsystem, m_controller));

    m_intakeSubsystem.setDefaultCommand(m_intakeSubsystem.run(() -> m_intakeSubsystem.stop()));
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
