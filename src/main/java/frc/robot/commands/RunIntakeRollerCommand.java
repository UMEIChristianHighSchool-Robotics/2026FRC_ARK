package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeRollerSubsystem;

public class RunIntakeRollerCommand extends Command {

  private final IntakeRollerSubsystem intakeRoller;

  public RunIntakeRollerCommand(IntakeRollerSubsystem intakeRoller) {
    this.intakeRoller = intakeRoller;
    addRequirements(intakeRoller);
  }

  @Override
  public void execute() {
    intakeRoller.runRoller(-IntakeConstants.kRollerVoltage);
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