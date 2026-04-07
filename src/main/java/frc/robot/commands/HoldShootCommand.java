// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.ShooterSubsystem;

public class HoldShootCommand extends Command {
 private final ShooterSubsystem m_shooterSubsystem;

  public HoldShootCommand(ShooterSubsystem m_shooterSubsystem) {

    this.m_shooterSubsystem = m_shooterSubsystem;
    
    addRequirements(m_shooterSubsystem);
  }

    @Override
  public void initialize() {
    m_shooterSubsystem.setTargetRPM(ShooterConstants.kTargetSpeed);
  }

    @Override
  public void execute() {
  }

   @Override
  public void end(boolean interrupted) {
    m_shooterSubsystem.stop();
  }

  @Override
  public boolean isFinished() {
    return false; // runs until button released
  }
}

