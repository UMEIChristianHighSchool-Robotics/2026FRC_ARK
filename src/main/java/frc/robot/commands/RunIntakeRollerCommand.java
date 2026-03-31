// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeRollerConstants;
import frc.robot.subsystems.IntakeRollerSubsystem;

public class RunIntakeRollerCommand extends Command {
  
  private final IntakeRollerSubsystem intakeRoller;

  public RunIntakeRollerCommand(IntakeRollerSubsystem intakeRoller) {
    this.intakeRoller = intakeRoller;
    addRequirements(intakeRoller);
  }

  @Override
  public void initialize() {
    intakeRoller.setTargetRPM(IntakeRollerConstants.kTargetSpeed);
  }

  @Override
  public void execute() {
  }

  @Override
  public void end(boolean interrupted) {
    intakeRoller.stopRoller();
  }

  @Override
  public boolean isFinished() {
    return false;  // runs until button released
  }
}