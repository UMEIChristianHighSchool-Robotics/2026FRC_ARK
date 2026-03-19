// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.TaxiOnlyAutoCommand;
import frc.robot.commands.TwoPieceAutoCommand;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.HoldShootCommand;
import frc.robot.commands.IntakeDownCommand;
import frc.robot.commands.IntakeTravelCommand;
import frc.robot.commands.IntakeUpCommand;
import frc.robot.commands.ReverseIntakeRollerCommand;
import frc.robot.commands.RunIntakeRollerCommand;
import frc.robot.commands.ShooterIdleCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakeSubsystem.IntakeState;

public class RobotContainer {

  //Subsystems
  public final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  public final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  public final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
  
  //Xbox Controller
  public final CommandXboxController m_controller = new CommandXboxController(OperatorConstants.kControllerPort);

  //The autonomous chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  //Commands
  public final TaxiOnlyAutoCommand m_TaxiOnlyAutoCommand = new TaxiOnlyAutoCommand(m_driveSubsystem);
  public final TwoPieceAutoCommand m_TwoPieceAutoCommand = new TwoPieceAutoCommand(m_driveSubsystem, m_intakeSubsystem, m_shooterSubsystem);
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_controller);
  public final RunIntakeRollerCommand m_runIntakeRollerCommand = new RunIntakeRollerCommand(m_intakeSubsystem);
  public final ReverseIntakeRollerCommand m_reverseIntakeRollerCommand = new ReverseIntakeRollerCommand(m_intakeSubsystem);
  public final IntakeDownCommand m_intakeDownCommand = new IntakeDownCommand(m_intakeSubsystem);
  public final IntakeUpCommand m_intakeUpCommand = new IntakeUpCommand(m_intakeSubsystem);
  public final IntakeTravelCommand m_intakeTravelCommand = new IntakeTravelCommand(m_intakeSubsystem);
  public final HoldShootCommand m_shootCommand = new HoldShootCommand(m_shooterSubsystem, m_intakeSubsystem);
  public final ShooterIdleCommand m_shooterIdleCommand = new ShooterIdleCommand(m_shooterSubsystem);
  
  public RobotContainer() {
    
    // Set default subsystem commands in the constructor
    m_driveSubsystem.setDefaultCommand(m_driveCommand);
    m_intakeSubsystem.setDefaultCommand(
        new RunCommand(m_intakeSubsystem::stopRoller, m_intakeSubsystem));
    m_shooterSubsystem.setDefaultCommand(
        new ShooterIdleCommand(m_shooterSubsystem)
      );

    // Set default intake state when the robot initializes
    m_intakeSubsystem.setState(IntakeState.TRAVEL);

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
    autoChooser.addOption("Mobility Only Auto", m_TaxiOnlyAutoCommand);
    //autoChooser.addOption("Score + Mobility Auto", m_TaxiOnlyAutoCommand);
    autoChooser.addOption("Two Piece Auto", m_TwoPieceAutoCommand);
 
  }

  private void configureBindings() {
    //A button: Intake DOWN
    m_controller.a().onTrue(new IntakeDownCommand(m_intakeSubsystem));

    //B button: Intake TRAVEL
    m_controller.b().onTrue(new IntakeTravelCommand(m_intakeSubsystem));

    //Y button: Intake UP
    m_controller.y().onTrue(new IntakeUpCommand(m_intakeSubsystem));

    //Left trigger: intake roller
    m_controller.leftTrigger().whileTrue(new RunIntakeRollerCommand(m_intakeSubsystem));
    
    //Left bumber: reverse intake roller to clear jams 
    m_controller.leftBumper().whileTrue(new ReverseIntakeRollerCommand(m_intakeSubsystem));
  
    //Right trigger: shoot
    m_controller.rightTrigger()
    .whileTrue(new HoldShootCommand(m_shooterSubsystem, m_intakeSubsystem));
  
   }

  
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
