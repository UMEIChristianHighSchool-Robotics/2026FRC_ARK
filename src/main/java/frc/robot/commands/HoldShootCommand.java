// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakeSubsystem;


public class HoldShootCommand extends Command {

  private final ShooterSubsystem m_shooterSubsystem;
  private final IntakeSubsystem m_intakeSubsystem;

  public HoldShootCommand(ShooterSubsystem m_shooterSubsystem,
                      IntakeSubsystem m_intakeSubsystem) {

    this.m_shooterSubsystem = m_shooterSubsystem;
    this.m_intakeSubsystem = m_intakeSubsystem;
    
    addRequirements(m_shooterSubsystem,m_intakeSubsystem);
  }

    @Override
  public void initialize() {
    m_shooterSubsystem.setTargetRPM(ShooterConstants.kTargetSpeed);
  }

    @Override
  public void execute() {

    // Keep shooter spinning
    m_shooterSubsystem.setTargetRPM(ShooterConstants.kTargetSpeed);

    // Only feed when ready
    if (m_shooterSubsystem.atSpeed()) {
      m_intakeSubsystem.runRoller(IntakeConstants.kRollerVoltage);
    } else {
      m_intakeSubsystem.stopRoller();
    }
  }

   @Override
  public void end(boolean interrupted) {
    m_shooterSubsystem.stop();
    m_intakeSubsystem.stopRoller();
  }

  @Override
  public boolean isFinished() {
    return false; // runs until button released
  }
}

