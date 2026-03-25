// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakePivotSubsystem;
import frc.robot.subsystems.IntakeRollerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class TwoPieceAutoCommand extends SequentialCommandGroup {


    public TwoPieceAutoCommand(
            DriveSubsystem drive,
            IntakePivotSubsystem intakePivot,
            IntakeRollerSubsystem intakeRoller,
            ShooterSubsystem shooter) {
        
      
        addCommands(
            // Shoot stored balls
            new HoldShootCommand(shooter).withTimeout(0.8),

            //Deploy intake while driving
            new ParallelDeadlineGroup(
                drive.driveForwardMeters(3),
                new IntakeDownCommand(intakePivot)
            ),
            
            // Drive forward while running intake roller
            new ParallelDeadlineGroup(
                drive.driveForwardMeters(1.5),
                new RunIntakeRollerCommand(intakeRoller)
            ),

            // Shoot
            new HoldShootCommand(shooter).withTimeout(1.5),
                  

            //Reposition for TeleOp
            new ParallelDeadlineGroup(
                drive.turnRelative(-90),    
                new IntakeDownCommand(intakePivot)
            )

        );
    }
}
