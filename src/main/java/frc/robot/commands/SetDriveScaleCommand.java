// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.DriveSubsystem;


public class SetDriveScaleCommand extends InstantCommand {
  private final DriveSubsystem drive;
  private final double forwardScale;
  private final double turnScale;
  private double prevForward;
  private double prevTurn;

  public SetDriveScaleCommand(DriveSubsystem drive, double forwardScale,double turnScale) {
    this.drive = drive;
    this.forwardScale = forwardScale;
    this.turnScale = turnScale;
  }

  @Override
  public void initialize() {
    // Save current scales
    prevForward = drive.getForwardScale();
    prevTurn = drive.getTurnScale();

    // Apply new temporary scales
    drive.setForwardScale(forwardScale);
    drive.setTurnScale(turnScale);
  }

  @Override
  public void end(boolean interrupted) {
    // Restore previous scales
    drive.setForwardScale(prevForward);
    drive.setTurnScale(prevTurn);
    }
}
