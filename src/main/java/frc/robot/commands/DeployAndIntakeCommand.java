// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.IntakeSubsystem;

public class DeployAndIntakeCommand extends SequentialCommandGroup {

  public DeployAndIntakeCommand(IntakeSubsystem intake) {

    addCommands(
      new InstantCommand(() -> intake.setState(IntakeSubsystem.IntakeState.INTAKE), intake),
      new WaitUntilCommand(intake::isAtTarget),
      new RunIntakeRollerCommand(intake)
    );
  }
}