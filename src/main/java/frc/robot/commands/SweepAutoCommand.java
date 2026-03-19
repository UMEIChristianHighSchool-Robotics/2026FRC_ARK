// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class SweepAutoCommand extends SequentialCommandGroup {

    private final DriveSubsystem m_drive;
    private final IntakeSubsystem m_intake;

    public SweepAutoCommand(
            DriveSubsystem drive,
            IntakeSubsystem intake,
            ShooterSubsystem shooter) {
        
        m_drive = drive;
        m_intake = intake;
        
        addCommands(
            // Shoot stored balls
            new HoldShootCommand(shooter).withTimeout(2.5),

            // Drive over ramp
            drive.driveForwardMeters(1.5),

            // Move intake to DOWN
            new IntakeDownCommand(intake),

            // Drive a sweep pattern while running intake roller
            new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(2.0),
                new RunIntakeRollerCommand(m_intake)
            ),
             
            new ParallelDeadlineGroup(
                m_drive.turnRelative(90),
                new RunIntakeRollerCommand(m_intake)
            ),

             new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(2.0),
                new RunIntakeRollerCommand(m_intake)
            ),
            new ParallelDeadlineGroup(
                m_drive.turnRelative(90),
                new RunIntakeRollerCommand(m_intake)
            ),

             new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(1.0),
                new RunIntakeRollerCommand(m_intake)
            ),
             new ParallelDeadlineGroup(
                m_drive.turnRelative(90),
                new RunIntakeRollerCommand(m_intake)
            ),

             new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(4.0),
                new RunIntakeRollerCommand(m_intake)
            ),
            
            new ParallelDeadlineGroup(
                m_drive.turnRelative(-90),
                new RunIntakeRollerCommand(m_intake)
            ),

             new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(1.0),
                new RunIntakeRollerCommand(m_intake)
            ),
             new ParallelDeadlineGroup(
                m_drive.turnRelative(-90),
                new RunIntakeRollerCommand(m_intake)
            ),

             new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(4.0),
                new RunIntakeRollerCommand(m_intake)
            ),
              new ParallelDeadlineGroup(
                m_drive.turnRelative(-115),
                new RunIntakeRollerCommand(m_intake)
            ),

             new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(4.472),
                new RunIntakeRollerCommand(m_intake)
            ),

               new ParallelDeadlineGroup(
                m_drive.turnRelative(115),
                new RunIntakeRollerCommand(m_intake)
            ),

             new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(2.0),
                new RunIntakeRollerCommand(m_intake)
            ),
            
              new ParallelDeadlineGroup(
                m_drive.turnRelative(-90),
                new RunIntakeRollerCommand(m_intake)
            ),
 

            //Move intake to UP
            new IntakeUpCommand(intake),

            //Reverse
             drive.driveForwardMeters(-4),

            // Shoot any balls picked up
            new HoldShootCommand(shooter).withTimeout(2.5)
        );
    }
}
