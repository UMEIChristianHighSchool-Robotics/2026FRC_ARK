// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.commands.HoldShootCommand;
import frc.robot.commands.IntakeDownCommand;
import frc.robot.commands.IntakeTravelCommand;
import frc.robot.commands.IntakeUpCommand;
import frc.robot.commands.RunIntakeRollerCommand;
import frc.robot.commands.ShooterIdleCommand;

public class AutoSequenceCommand extends SequentialCommandGroup {

private final DriveSubsystem m_drive;
private final IntakeSubsystem m_intake;
private final ShooterSubsystem m_shooter;

  /** Creates a new AutoRoutine. */
public AutoSequenceCommand(DriveSubsystem drive,
                  IntakeSubsystem intake,
                  ShooterSubsystem shooter) {
  addRequirements(drive,intake,shooter);
        m_drive = drive;
        m_intake = intake;
        m_shooter = shooter;
     
        
        addCommands(
            new IntakeDownCommand(intake),

            drive.driveForwardMeters(1.5),

            new RunIntakeRollerCommand(intake).withTimeout(2.0),

            drive.turnRelative(90),

            new HoldShootCommand(shooter,intake).withTimeout(2.0),

            new ShooterIdleCommand(shooter)
        );
    }
}