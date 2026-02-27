// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import frc.robot.commands.DriveCommand;
import frc.robot.subsystems.DiffDriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

import static frc.robot.Constants.OperatorConstants;

public class RobotContainer {
  
  //Subsystems
  public final DiffDriveSubsystem m_driveSubsystem = new DiffDriveSubsystem();
  public final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  
  //Controllers
  public final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);
 
  //Commands
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_driverController);
 
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

 }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link
   * CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    //new Trigger(m_exampleSubsystem::exampleCondition)
    //    .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is
    // pressed,
    // cancelling on release.
   // m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
 m_driverController.y().toggleOnTrue(
    new StartEndCommand(
        () -> m_intakeSubsystem.runIntake(),
        () -> m_intakeSubsystem.stop(),
        m_intakeSubsystem
    )
);
     
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
  return -m_driverController.getLeftY();
}

public double getRightYValue() {
  return -m_driverController.getRightY();
}


}
