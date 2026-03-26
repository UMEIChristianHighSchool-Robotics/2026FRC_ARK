// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakePivotSubsystem;
import frc.robot.subsystems.IntakeRollerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
 import frc.robot.Constants.ShooterConstants;

public class TwoPieceAutoCommand extends SequentialCommandGroup {


    public TwoPieceAutoCommand(
            DriveSubsystem drive,
            IntakePivotSubsystem intakePivot,
            IntakeRollerSubsystem intakeRoller,
            ShooterSubsystem shooter) {
        
      
        addCommands(
          
          //shoots pre-load
          shooter.autoShoot(ShooterConstants.kTargetSpeed),
          
          //Drive & Intake second piece
          new ParallelDeadlineGroup(
            drive.driveForwardMeters(3),
            new SequentialCommandGroup(
                new IntakeDownCommand(intakePivot),
                new RunIntakeRollerCommand(intakeRoller)
            )
          ),
      
          //Stop roller & Retract Intake
          new InstantCommand(intakeRoller::stopRoller,intakeRoller),
          new IntakeTravelCommand(intakePivot),

          //shoot second piece
          shooter.autoShoot(ShooterConstants.kTargetSpeed),
          
          //Reposition for TeleOp
          drive.turnRelative(-45),
          drive.driveForwardMeters(-2)

        );
    }
  }

