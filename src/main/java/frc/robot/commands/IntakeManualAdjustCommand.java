// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakePivotConstants;
import frc.robot.subsystems.IntakePivotSubsystem;

public class IntakeManualAdjustCommand extends Command {
  private final IntakePivotSubsystem intakePivot;
  private final DoubleSupplier joystick; 
  private final SlewRateLimiter limiter = new SlewRateLimiter(IntakePivotConstants.kManualSlewRateLimiter);

  public IntakeManualAdjustCommand(IntakePivotSubsystem intakePivot, DoubleSupplier joystick) {
    this.intakePivot= intakePivot;
    this.joystick = joystick;
    addRequirements(intakePivot);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double value = limiter.calculate(MathUtil.applyDeadband(joystick.getAsDouble(), 0.1));
    
    if (Math.abs(value) > 0.01) {
        intakePivot.setManualOutput(value);
    } else if (intakePivot.isManualMode()) {
        intakePivot.disableManual();
    }
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    intakePivot.disableManual();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;//runs while held
  }
}