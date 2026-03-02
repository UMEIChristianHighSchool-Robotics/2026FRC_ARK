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
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.Eject;
import frc.robot.commands.Launch;
import frc.robot.commands.Intake;
import frc.robot.commands.SpinUp;
import frc.robot.subsystems.DiffDriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;



import static frc.robot.Constants.OperatorConstants;

public class RobotContainer {
  
  //Subsystems
  public final DiffDriveSubsystem m_driveSubsystem = new DiffDriveSubsystem();
  public final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  
  //Driver Controller
  public final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);

  //Operator Controller
  public final CommandXboxController m_operatorController = new CommandXboxController(OperatorConstants.kOperatorControllerPort);

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
 
   // While the left bumper on operator controller is held, intake Fuel
    m_operatorController.leftBumper().whileTrue(new Intake(IntakeSubsystem));
    // While the right bumper on the operator controller is held, spin up for 1
    // second, then launch fuel. When the button is released, stop.
    m_operatorController.rightBumper().whileTrue(new LaunchSequence(IntakeSubsystem));
    // While the A button is held on the operator controller, eject fuel back out
    // the intake
    m_operatorController.a().whileTrue(new Eject(IntakeSubsystem));

  // Set the default command for the drive subsystem to the command provided by
    // factory with the values provided by the joystick axes on the driver
    // controller. The Y axis of the controller is inverted so that pushing the
    // stick away from you (a negative value) drives the robot forwards (a positive
    // value)
    m_driveSubsystem.setDefaultCommand(new Drive(m_driveSubsystem, m_driverController));

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
  return -m_driverController.getLeftY();
}

public double getRightYValue() {
  return -m_driverController.getRightY();
}


}
