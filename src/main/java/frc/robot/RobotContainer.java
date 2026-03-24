// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.TaxiOnlyAutoCommand;
import frc.robot.commands.TaxiShootAutoCommand;
import frc.robot.commands.TwoPieceAutoCommand;
import frc.robot.commands.SweepFromLeftEdgeStart;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.FloorDownCommand;
import frc.robot.commands.FloorUpCommand;
import frc.robot.commands.HoldShootCommand;
import frc.robot.commands.IntakeDownCommand;
import frc.robot.commands.IntakeTravelCommand;
import frc.robot.commands.IntakeUpCommand;
import frc.robot.commands.ReverseIntakeRollerCommand;
import frc.robot.commands.RunIntakeRollerCommand;
import frc.robot.commands.ShooterIdleCommand;
import frc.robot.commands.SetDriveScaleCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FloorLifterSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakeSubsystem.IntakeState;
import frc.robot.subsystems.FloorLifterSubsystem.FloorLifterState;

public class RobotContainer {

  //Subsystems
  public final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  public final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  public final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
  public final FloorLifterSubsystem m_floorLifterSubsystem = new FloorLifterSubsystem();
  
  //Xbox Controllers
  public final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);
  public final CommandXboxController m_operatorController = new CommandXboxController(OperatorConstants.kOperatorControllerPort);


  //The autonomous chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  //Commands
  public final TaxiOnlyAutoCommand m_TaxiOnlyAutoCommand = new TaxiOnlyAutoCommand(m_driveSubsystem);
  public final TaxiShootAutoCommand m_TaxiShootAutoCommand = new TaxiShootAutoCommand(m_driveSubsystem, m_intakeSubsystem, m_shooterSubsystem);
  public final TwoPieceAutoCommand m_TwoPieceAutoCommand = new TwoPieceAutoCommand(m_driveSubsystem, m_intakeSubsystem, m_shooterSubsystem);
  public final SweepFromLeftEdgeStart m_SweepFromLeftEdgeStart = new SweepFromLeftEdgeStart(m_driveSubsystem, m_intakeSubsystem, m_shooterSubsystem);
  public final SetDriveScaleCommand m_SetDriveScaleCommand = new SetDriveScaleCommand(m_driveSubsystem, 0, 0);
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_driverController);
  public final RunIntakeRollerCommand m_runIntakeRollerCommand = new RunIntakeRollerCommand(m_intakeSubsystem);
  public final ReverseIntakeRollerCommand m_reverseIntakeRollerCommand = new ReverseIntakeRollerCommand(m_intakeSubsystem);
  public final IntakeDownCommand m_intakeDownCommand = new IntakeDownCommand(m_intakeSubsystem);
  public final IntakeUpCommand m_intakeUpCommand = new IntakeUpCommand(m_intakeSubsystem);
  public final IntakeTravelCommand m_intakeTravelCommand = new IntakeTravelCommand(m_intakeSubsystem);
  public final HoldShootCommand m_shootCommand = new HoldShootCommand(m_shooterSubsystem);
  public final ShooterIdleCommand m_shooterIdleCommand = new ShooterIdleCommand(m_shooterSubsystem);
  public final FloorUpCommand m_floorUpCommand = new FloorUpCommand(m_floorLifterSubsystem);
  public final FloorDownCommand m_floorDownCommand = new FloorDownCommand(m_floorLifterSubsystem);  
  
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
 
   
    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("Mobility Only Auto", m_TaxiOnlyAutoCommand);
    autoChooser.addOption("Score + Mobility Auto", m_TaxiOnlyAutoCommand);
    autoChooser.addOption("Two Piece Auto", m_TwoPieceAutoCommand);
    autoChooser.addOption("Left Aligned Sweep Auto", m_SweepFromLeftEdgeStart);

    Shuffleboard.getTab("Auto").add("Auto Chooser", autoChooser); 
    
  }

  private void configureBindings() {
    
    //------------Driver Controller------------//
    //D-Pad Speed Selector
    m_driverController.povUp().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.FAST)));
    m_driverController.povRight().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.DRIVE)));
    m_driverController.povDown().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.SLOW)));
    m_driverController.povLeft().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.CRAWL)));

    //Left Trigger: Deploy and run intake roller
    m_driverController.leftTrigger()
      .whileTrue(
        new ParallelCommandGroup(
          new IntakeDownCommand(m_intakeSubsystem),
          new RunIntakeRollerCommand(m_intakeSubsystem)
        ))
      .onFalse(new IntakeTravelCommand(m_intakeSubsystem));

    //Right trigger: shoot
    m_driverController.rightTrigger()
    .whileTrue(new HoldShootCommand(m_shooterSubsystem));
  
    //A button: STOP everything
    m_driverController.a().onTrue(new InstantCommand(() -> CommandScheduler.getInstance().cancelAll()));


    //------------Operator Controller------------//
   
    //Floor Lifter Positions
    m_operatorController.a().onTrue(new FloorDownCommand(m_floorLifterSubsystem)); //A is "Down"
    m_operatorController.y().onTrue(new FloorUpCommand(m_floorLifterSubsystem)); //Y is "Up"

    //Intake Positions
    m_operatorController.povDown().onTrue(new IntakeDownCommand(m_intakeSubsystem)); //D-Pad down is "Down"
    m_operatorController.povRight().onTrue(new IntakeTravelCommand(m_intakeSubsystem)); //D-Pad right is "Travel"
    m_operatorController.povUp().onTrue(new IntakeUpCommand(m_intakeSubsystem)); //D-Pad up is "Up"
   
    //Intake Rollers
    m_operatorController.rightTrigger().whileTrue(new RunIntakeRollerCommand(m_intakeSubsystem)); //Right: Forward
    m_operatorController.leftTrigger().whileTrue(new ReverseIntakeRollerCommand(m_intakeSubsystem)); //Left: Reverse
  
   }

  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
   return autoChooser.getSelected();
  }
}
