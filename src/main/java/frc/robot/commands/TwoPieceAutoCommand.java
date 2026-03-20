// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class TwoPieceAutoCommand extends SequentialCommandGroup {


    public TwoPieceAutoCommand(
            DriveSubsystem drive,
            IntakeSubsystem intake,
            ShooterSubsystem shooter) {
        
      
        addCommands(
            // Shoot stored balls
            new HoldShootCommand(shooter).withTimeout(2.5),

            // Drive over ramp
            drive.driveForwardMeters(1.5),

            // Move intake to DOWN
            new IntakeDownCommand(intake),

            // Drive forward while running intake roller
            new ParallelDeadlineGroup(
                drive.driveForwardMeters(1.5),
                new RunIntakeRollerCommand(intake)
            ),

            //Move intake to UP
            new IntakeUpCommand(intake),

            //Reverse
             drive.driveForwardMeters(-1.5),

            // Shoot any balls picked up
            new HoldShootCommand(shooter).withTimeout(2.5)
        );
    }
}
