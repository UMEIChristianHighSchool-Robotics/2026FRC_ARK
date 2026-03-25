// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakePivotSubsystem;
import frc.robot.subsystems.IntakeRollerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class SweepFromLeftEdgeStart extends SequentialCommandGroup {

    public SweepFromLeftEdgeStart(
            DriveSubsystem drive,
            IntakePivotSubsystem intakePivot,
            IntakeRollerSubsystem intakeRoller,
            ShooterSubsystem shooter) {
        
        addCommands(
           
            
            
            // Drive Forward + Prapare for intake ~ 3.6 s
             new SetDriveScaleCommand(
                    drive,1.0, 1.0),
            new ParallelDeadlineGroup(
                drive.driveForwardMeters(2.1), // 3.6 sec
                new WaitCommand(2)
                    .andThen(new HoldShootCommand(shooter)
                    .withTimeout(1.2)), // 3.2 sec
                new WaitCommand(2.5)
                    .andThen(new IntakeDownCommand(intakePivot)) // 3.0 sec
            ),
      
            // Large Curve Sweep ~ 3.5 sec
            new SetDriveScaleCommand(
                    drive,0.83, 0.08),
            new ParallelDeadlineGroup(
                new RunIntakeRollerCommand(intakeRoller),             
                drive.turnRelative(40)
            ),
            drive.driveForwardMeters(1.6), // 3.5 sec
            // Small Curve Sweep ~ 3.5 sec
            
            new SetDriveScaleCommand(
                    drive,0.75, 0.28),
            new ParallelDeadlineGroup(
              
                new RunIntakeRollerCommand(intakeRoller),             
                drive.turnRelative(140) // 3.5 sec
        
            ),
        drive.driveForwardMeters(1.4), // 3.5 sec
            // Position to shoot ~ 1.9 sec
           new SetDriveScaleCommand(
                    drive,0.8, 0.6),
            new ParallelDeadlineGroup(
                
                new RunIntakeRollerCommand(intakeRoller),
                drive.turnRelative(140) // 1.9 sec
            ),  
           
            //Shoot ~ 1.5 sec
            new HoldShootCommand(shooter).withTimeout(1.5),  

            //Position for TeleOp ~ 2.5 sec
            new ParallelDeadlineGroup(
                new SetDriveScaleCommand(
                    drive,0.6, 0.25),
                drive.driveForwardMeters(-0.6), // 2.5 sec 
                drive.turnRelative(-55), // 2.5 sec
                new IntakeTravelCommand(intakePivot) // 1.5 sec
            )
        );
    }
}
