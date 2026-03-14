// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

//Spin up, then wait, feed and stop.

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ShootCommand extends SequentialCommandGroup {

  public ShootCommand(ShooterSubsystem m_shooterSubsystem,
                      IntakeSubsystem m_intakeSubsystem) {

    addCommands(

      // Spin up
      new InstantCommand(() -> m_shooterSubsystem.setTargetRPM(ShooterConstants.kShooterSpinUp), m_shooterSubsystem),

      // Wait until at speed
      new WaitUntilCommand(m_shooterSubsystem::atSpeed),

      // Feed for 0.5 seconds
      new RunCommand(
          () -> m_intakeSubsystem.runRoller(IntakeConstants.kRollerVoltage),
          m_intakeSubsystem
      ).withTimeout(0.5),

      // Stop everything
      new InstantCommand(m_shooterSubsystem::stop, m_shooterSubsystem),
      new InstantCommand(m_intakeSubsystem::stopRoller, m_intakeSubsystem)
    );
  }
}