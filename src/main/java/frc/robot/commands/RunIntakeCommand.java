// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class RunIntakeCommand extends Command {
  /** Creates a new RunIntakeCommand. */
  private final IntakeSubsystem c_intake;
  private final DoubleSupplier c_intakeVoltage; 

  public RunIntakeWheels(IntakeSubsystem intake, DoubleSupplier intakeVoltage) {
    c_intake = intake;
    c_intakeVoltage = intakeVoltage;
 
    addRequirements(c_intake);
      // use addRequirements( here to declare subsystem dependency
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
        c_intake.setIntakeWheels(c_intakeVoltage.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
      c_intake.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
