// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
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
                   
          //Feed & shoot ~ 2.5 sec
          new ParallelDeadlineGroup(
            new WaitUntilCommand(() -> shooter.atSpeed()),
            new HoldShootCommand(shooter)
          ),

          new ParallelCommandGroup(
            new HoldShootCommand(shooter).withTimeout(1.5),
            new RunIntakeRollerCommand(intakeRoller).withTimeout(1.5)
          ),

          //Drive & Intake ~ 3 sec
          new ParallelDeadlineGroup(
            drive.driveForwardMeters(3),
            new SequentialCommandGroup(
                new IntakeDownCommand(intakePivot),
                new RunIntakeRollerCommand(intakeRoller)
            )
          ),

          //Retract Intake ~ 1 sec
          new IntakeTravelCommand(intakePivot),

          //Feed & shoot ~ 2.5 seconds
          new ParallelDeadlineGroup(
            new WaitUntilCommand(() -> shooter.atSpeed()),
            new HoldShootCommand(shooter)
          ),
          new ParallelCommandGroup(
            new HoldShootCommand(shooter).withTimeout(1.5),
            new RunIntakeRollerCommand(intakeRoller).withTimeout(1.5)
          ),

          new InstantCommand(shooter::stop, shooter),
          new InstantCommand(intakeRoller::stopRoller, intakeRoller),

          //Reposition for TeleOp ~ 3 sec
          drive.turnRelative(-45),
          drive.driveForwardMeters(-2)

        );
    }
  }

