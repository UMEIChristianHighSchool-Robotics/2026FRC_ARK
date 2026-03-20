// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class SweepFromLeftEdgeStart extends SequentialCommandGroup {

    private final DriveSubsystem m_drive;
    private final IntakeSubsystem m_intake;

    public SweepFromLeftEdgeStart(
            DriveSubsystem drive,
            IntakeSubsystem intake,
            ShooterSubsystem shooter) {
        
        m_drive = drive;
        m_intake = intake;
        
        addCommands(
           
            // Drive Forward + Prapare for intake                                    ~ 4.2 s
            new ParallelDeadlineGroup(
                drive.driveForwardMeters(2.1),                   // 4.2 sec
                new WaitCommand(2.8)
                    .andThen(new HoldShootCommand(shooter)
                    .withTimeout(1.2)),                         // 4.0 sec
                new WaitCommand(3.5)
                    .andThen(new IntakeDownCommand(intake))             // 4.0 sec
            ),
      
            // Large Curve Sweep                                                      ~ 3.2 sec
            new ParallelDeadlineGroup(
                new RunIntakeRollerCommand(intake),                     // cont.
                drive.turnRelative(40),                         // 1.1 sec
                drive.driveForwardMeters(1.6)                    // 3.2 sec
            ),

           

            // Move intake to DOWN
            new IntakeDownCommand(intake),
   
    
            new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(1.5),
                new RunIntakeRollerCommand(m_intake)
            ),
         
            new ParallelDeadlineGroup(
                m_drive.turnRelative(-90),
                new RunIntakeRollerCommand(m_intake)
            ),

            new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(4),
                new RunIntakeRollerCommand(m_intake)
            ),
            new ParallelDeadlineGroup(
                m_drive.turnRelative(90),
                new RunIntakeRollerCommand(m_intake)
            ),
            
            //Move intake to UP
            new IntakeUpCommand(intake),

            // Shoot any balls picked up
            new HoldShootCommand(shooter).withTimeout(1),

            //Move intake down
            new IntakeDownCommand(intake),

            // Position for TeleOp while running intake roller
             new ParallelDeadlineGroup(
                m_drive.turnRelative(-135),
                new RunIntakeRollerCommand(m_intake)
            ),
              new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(2.0),
                new RunIntakeRollerCommand(m_intake)
            ),
             new ParallelDeadlineGroup(
                m_drive.turnRelative(180),
                new RunIntakeRollerCommand(m_intake)
            )

        );
    }
}
