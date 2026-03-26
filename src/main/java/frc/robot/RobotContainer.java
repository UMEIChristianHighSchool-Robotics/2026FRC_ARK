// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.RobotController;
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
import frc.robot.subsystems.IntakePivotSubsystem;
import frc.robot.subsystems.IntakeRollerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakePivotSubsystem.IntakeState;

public class RobotContainer {

  //Subsystems
  public final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  public final IntakeRollerSubsystem m_intakeRollerSubsystem = new IntakeRollerSubsystem();
  public final IntakePivotSubsystem m_intakePivotSubsystem = new IntakePivotSubsystem();
  public final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
  public final FloorLifterSubsystem m_floorLifterSubsystem = new FloorLifterSubsystem();
  
  //Xbox Controllers
  public final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);
  public final CommandXboxController m_operatorController = new CommandXboxController(OperatorConstants.kOperatorControllerPort);


  //The autonomous chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  //Commands
  public final TaxiOnlyAutoCommand m_TaxiOnlyAutoCommand = new TaxiOnlyAutoCommand(m_driveSubsystem);
  public final TaxiShootAutoCommand m_TaxiShootAutoCommand = new TaxiShootAutoCommand(m_driveSubsystem, m_shooterSubsystem);
  public final TwoPieceAutoCommand m_TwoPieceAutoCommand = new TwoPieceAutoCommand(m_driveSubsystem, m_intakePivotSubsystem, m_intakeRollerSubsystem, m_shooterSubsystem);
   public final SetDriveScaleCommand m_SetDriveScaleCommand = new SetDriveScaleCommand(m_driveSubsystem, 0, 0);
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_driverController);
  public final RunIntakeRollerCommand m_runIntakeRollerCommand = new RunIntakeRollerCommand(m_intakeRollerSubsystem);
  public final ReverseIntakeRollerCommand m_reverseIntakeRollerCommand = new ReverseIntakeRollerCommand(m_intakeRollerSubsystem);
  public final IntakeDownCommand m_intakeDownCommand = new IntakeDownCommand(m_intakePivotSubsystem);
  public final IntakeUpCommand m_intakeUpCommand = new IntakeUpCommand(m_intakePivotSubsystem);
  public final IntakeTravelCommand m_intakeTravelCommand = new IntakeTravelCommand(m_intakePivotSubsystem);
  public final HoldShootCommand m_shootCommand = new HoldShootCommand(m_shooterSubsystem);
  public final ShooterIdleCommand m_shooterIdleCommand = new ShooterIdleCommand(m_shooterSubsystem);
  public final FloorUpCommand m_floorUpCommand = new FloorUpCommand(m_floorLifterSubsystem);
  public final FloorDownCommand m_floorDownCommand = new FloorDownCommand(m_floorLifterSubsystem);  
  
  public RobotContainer() {
    
    // Set default subsystem commands in the constructor
    m_driveSubsystem.setDefaultCommand(m_driveCommand);
    m_intakeRollerSubsystem.setDefaultCommand(
        new RunCommand(m_intakeRollerSubsystem::stopRoller, m_intakeRollerSubsystem));
    m_shooterSubsystem.setDefaultCommand(
        new ShooterIdleCommand(m_shooterSubsystem)
      );

    // Set default intake state when the robot initializes
    m_intakePivotSubsystem.setState(IntakeState.TRAVEL);

    // Configure the trigger bindings
    configureBindings();
 
   
    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("Two Piece Auto", m_TwoPieceAutoCommand);
    autoChooser.addOption("Move + Shoot", m_TaxiShootAutoCommand);
    autoChooser.addOption("Move Forward", m_TaxiOnlyAutoCommand);
   
    Shuffleboard.getTab("Auto").add("Auto Chooser", autoChooser); 
     Shuffleboard.getTab("Auto").addDouble("Battery Voltage", RobotController::getBatteryVoltage); 
    
    
  }

  private void configureBindings() {
    

    //------------Driver Controller------------//
    //D-Pad Speed Selector
    m_driverController.povUp().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.FAST)));
    m_driverController.povRight().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.DRIVE)));
    m_driverController.povDown().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.SLOW)));
    m_driverController.povLeft().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.CRAWL)));

    //Right Trigger: Deploy and run intake roller
    m_driverController.rightTrigger()
      .whileTrue(
        new ParallelCommandGroup(
          new IntakeDownCommand(m_intakePivotSubsystem),
          new RunIntakeRollerCommand(m_intakeRollerSubsystem)
        ))
      .onFalse(new IntakeTravelCommand(m_intakePivotSubsystem));

  
    
    //Right bumper: reverse intake roller to clear jams 
    m_driverController.rightBumper().whileTrue(new ReverseIntakeRollerCommand(m_intakeRollerSubsystem));
  
    //Left trigger: shoot
    m_driverController.leftTrigger()
    .whileTrue(new HoldShootCommand(m_shooterSubsystem));
  
    //X button: STOP everything
    m_driverController.x().onTrue(new InstantCommand(() -> CommandScheduler.getInstance().cancelAll()));


    //------------Operator Controller------------//
   
    //Floor Lifter Positions
    m_operatorController.a().onTrue(new FloorDownCommand(m_floorLifterSubsystem)); //A is "Down"
    m_operatorController.y().onTrue(new FloorUpCommand(m_floorLifterSubsystem)); //Y is "Up"

    //Intake Positions
    m_operatorController.povDown().onTrue(new IntakeDownCommand(m_intakePivotSubsystem)); //D-Pad down is "Down"
    m_operatorController.povRight().onTrue(new IntakeTravelCommand(m_intakePivotSubsystem)); //D-Pad right is "Travel"
    m_operatorController.povUp().onTrue(new IntakeUpCommand(m_intakePivotSubsystem)); //D-Pad up is "Up"
   
    //Intake Rollers
    m_operatorController.rightTrigger().whileTrue(new RunIntakeRollerCommand(m_intakeRollerSubsystem)); //Right: Forward
    m_operatorController.leftTrigger().whileTrue(new ReverseIntakeRollerCommand(m_intakeRollerSubsystem)); //Left: Reverse
  
   }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
