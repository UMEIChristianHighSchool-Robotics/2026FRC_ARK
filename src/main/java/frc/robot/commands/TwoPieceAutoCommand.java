// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class TwoPieceAutoCommand extends SequentialCommandGroup {

    private final DriveSubsystem m_drive;
    private final IntakeSubsystem m_intake;
    private final ShooterSubsystem m_shooter;
    private final ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");

    public TwoPieceAutoCommand(
            DriveSubsystem drive,
            IntakeSubsystem intake,
            ShooterSubsystem shooter) {
        
        m_drive = drive;
        m_intake = intake;
        m_shooter = shooter;
        
        addCommands(
            // Shoot stored balls
            new HoldShootCommand(shooter,intake).withTimeout(2.5),

            // Drive over ramp
            drive.driveForwardMeters(1.5),

            // Move intake to DOWN
            new IntakeDownCommand(intake),

            // Drive forward while running intake roller
            new ParallelDeadlineGroup(
                m_drive.driveForwardMeters(1.5),
                new RunIntakeRollerCommand(m_intake)
            ),

            //Move intake to UP
            new IntakeUpCommand(intake),

            //Reverse
             drive.driveForwardMeters(-1.5),

            // Shoot any balls picked up
            new HoldShootCommand(shooter, intake).withTimeout(2.5)
        );

         //telemetry
        autoTab.addDouble("AutoDistance (m)", m_drive::getDistanceMeters);
        autoTab.addBoolean("Shooter at speed", m_shooter::atSpeed);
    }
}
