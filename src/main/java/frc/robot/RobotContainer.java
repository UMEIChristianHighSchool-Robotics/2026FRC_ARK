// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.TaxiOnlyAutoCommand;
import frc.robot.commands.RunIntakeRollerCommand;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.SetDriveScaleCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeRollerSubsystem;

public class RobotContainer {

  //Subsystems
  public final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  public final IntakeRollerSubsystem m_intakeRoller = new IntakeRollerSubsystem();
  
  //Xbox Controllers
  public final CommandXboxController m_driverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);
  public final CommandXboxController m_operatorController = new CommandXboxController(OperatorConstants.kOperatorControllerPort);

  //The autonomous chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  //Commands
  public final TaxiOnlyAutoCommand m_TaxiOnlyAutoCommand = new TaxiOnlyAutoCommand(m_driveSubsystem);
  public final SetDriveScaleCommand m_SetDriveScaleCommand = new SetDriveScaleCommand(m_driveSubsystem, 0, 0);
  public final DriveCommand m_driveCommand = new DriveCommand(m_driveSubsystem,m_driverController);
  public final RunIntakeRollerCommand m_runIntakeRollerCommand = new RunIntakeRollerCommand(m_intakeRoller);
 
  public RobotContainer() {
    
    // Set default subsystem commands in the constructor
    m_driveSubsystem.setDefaultCommand(m_driveCommand);
    m_intakeRoller.setDefaultCommand(new RunCommand(m_intakeRoller::stopRoller,m_intakeRoller));

    // Configure the trigger bindings
    configureBindings();
 
    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("Move Forward", m_TaxiOnlyAutoCommand);

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
         new RunIntakeRollerCommand(m_intakeRoller)
        );


    //X button: STOP everything
    m_driverController.x().onTrue(new InstantCommand(() -> CommandScheduler.getInstance().cancelAll()));

    //------------Operator Controller------------//
   
   
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
