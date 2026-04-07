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
import frc.robot.commands.RunIntakeRollerCommand;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.HoldShootCommand;
import frc.robot.commands.IntakeINCommand;
import frc.robot.commands.IntakeManualAdjustCommand;
import frc.robot.commands.IntakeOUTCommand;
import frc.robot.commands.IntakeTRAVELCommand;
import frc.robot.commands.SetDriveScaleCommand;
import frc.robot.commands.ShooterIdleCommand;
import frc.robot.commands.ReverseIntakeRollerCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeRollerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakePivotSubsystem;
import frc.robot.subsystems.IntakePivotSubsystem.IntakePivotState;


public class RobotContainer {

  //Subsystems
  public final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  public final IntakeRollerSubsystem m_intakeRoller = new IntakeRollerSubsystem();
  public final IntakePivotSubsystem m_intakePivot = new IntakePivotSubsystem();
  public final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();

  //Xbox Controllers
  public final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);
  public final CommandXboxController m_operatorController = new CommandXboxController(OperatorConstants.kOperatorControllerPort);

  //The autonomous chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  //Commands
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_driverController);
  public final HoldShootCommand m_holdShootCommand = new HoldShootCommand(m_shooterSubsystem);
  public final IntakeINCommand m_intakeINCommand = new IntakeINCommand(m_intakePivot);
  public final IntakeOUTCommand m_intakeOUTCommand = new IntakeOUTCommand(m_intakePivot);
  public final IntakeTRAVELCommand m_intakeTRAVELCommand = new IntakeTRAVELCommand(m_intakePivot);
  public final ReverseIntakeRollerCommand m_reverseIntakeRollerCommand = new ReverseIntakeRollerCommand(m_intakeRoller);
  public final RunIntakeRollerCommand m_runIntakeRollerCommand = new RunIntakeRollerCommand(m_intakeRoller);
  public final SetDriveScaleCommand m_setDriveScaleCommand = new SetDriveScaleCommand(m_driveSubsystem, 0, 0);
  public final ShooterIdleCommand m_shooterIdleCommand = new ShooterIdleCommand(m_shooterSubsystem);
  public final TaxiOnlyAutoCommand m_taxiOnlyAutoCommand = new TaxiOnlyAutoCommand(m_driveSubsystem);
  
  public RobotContainer() {
    
    // Set default subsystem commands in the constructor
    m_driveSubsystem.setDefaultCommand(m_driveCommand);
    m_intakePivot.setDefaultCommand(
      new IntakeManualAdjustCommand(
          m_intakePivot,
          () -> -m_operatorController.getLeftY()  // invert joystick
      )
    );
    m_intakeRoller.setDefaultCommand(new RunCommand(m_intakeRoller::stopRoller,m_intakeRoller));
    m_shooterSubsystem.setDefaultCommand(
        new ShooterIdleCommand(m_shooterSubsystem)
      );

    // Set default intake state when the robot initializes
    m_intakePivot.setState(IntakePivotState.IN);

    // Configure the trigger bindings
    configureBindings();
 
    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("Move Forward", m_taxiOnlyAutoCommand);

    Shuffleboard.getTab("Auto").add("Auto Chooser", autoChooser); 
    
  }

  private void configureBindings() {
    
    //------------Driver Controller------------//
    //D-Pad Speed Selector
    m_driverController.povUp().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.FAST)));
    m_driverController.povRight().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.DRIVE)));
    m_driverController.povDown().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.SLOW)));
    m_driverController.povLeft().onTrue(new InstantCommand(() -> m_driveSubsystem.setSpeedMode(OperatorConstants.SpeedSelect.CRAWL)));

    //Right Trigger: deploy and run intake roller
    m_driverController.rightTrigger()
      .whileTrue(
        new ParallelCommandGroup(
          new IntakeOUTCommand(m_intakePivot),  
          new RunIntakeRollerCommand(m_intakeRoller)
        ))
      .onFalse(new IntakeTRAVELCommand(m_intakePivot));


    //Right Bumper: Reverse intake roller
    m_driverController.rightBumper()
      .whileTrue(
         new ReverseIntakeRollerCommand(m_intakeRoller)
        );

    //Left trigger: shoot
    m_driverController.leftTrigger()
    .whileTrue(new HoldShootCommand(m_shooterSubsystem));
  

    //X button: STOP everything
    m_driverController.x().onTrue(new InstantCommand(() -> CommandScheduler.getInstance().cancelAll()));

    //------------Operator Controller------------//
    //Intake Positions
    m_operatorController.povDown()
        .debounce(0.2)
        .onTrue(new IntakeOUTCommand(m_intakePivot)); //D-Pad down is "Down"
    m_operatorController.povRight()
        .debounce(0.2)
        .onTrue(new IntakeTRAVELCommand(m_intakePivot)); //D-Pad right is "Travel"
    m_operatorController.povUp()
        .debounce(0.2)
        .onTrue(new IntakeINCommand(m_intakePivot)); //D-Pad up is "Up"
   
    //Intake Rollers
    m_operatorController.rightTrigger().whileTrue(new RunIntakeRollerCommand(m_intakeRoller)); //Right: Forward
    m_operatorController.leftTrigger().whileTrue(new ReverseIntakeRollerCommand(m_intakeRoller)); //Left: Reverse
   
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
