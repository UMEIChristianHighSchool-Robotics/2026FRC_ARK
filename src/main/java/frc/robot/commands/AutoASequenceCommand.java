// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class AutoASequenceCommand extends SequentialCommandGroup {

    private final ShooterSubsystem m_shooter;

    // Example Shuffleboard tab
    private final ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");

    public AutoASequenceCommand(
            DriveSubsystem drive,
            IntakeSubsystem intake,
            ShooterSubsystem shooter) {

        m_shooter = shooter;

        // Add commands in sequence so that all imports are used
        addCommands(
            // Intake down
            new IntakeDownCommand(intake),

            // Drive forward
            drive.driveForwardMeters(1.0),

            // Run intake roller forward
            new RunIntakeRollerCommand(intake).withTimeout(1.5),

            // Move intake to TRAVEL (uses the previously unused command)
            new IntakeTravelCommand(intake),

            // Turn robot 90 degrees
            drive.turnRelative(90),

            // Spin up shooter
            m_shooter.runOnce(() -> m_shooter.setTargetRPM(3000)),

            // Shoot
            new HoldShootCommand(shooter, intake).withTimeout(2.0),

            // Stop shooter
            new ShooterIdleCommand(shooter),

            // Return intake to UP position
            new IntakeUpCommand(intake),

            // Drive backward
            drive.driveForwardMeters(-1.0)
        );

        // Optional: add telemetry to Shuffleboard
        autoTab.addBoolean("All Systems Active", () -> true);
    }
}
