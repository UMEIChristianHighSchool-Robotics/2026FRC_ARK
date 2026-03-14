package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystem;

public class RunIntakeRollerCommand extends Command {

  private final IntakeSubsystem intake;

  public RunIntakeRollerCommand(IntakeSubsystem intake) {
    this.intake = intake;
    addRequirements(intake);
  }

  @Override
  public void execute() {
    intake.runRollerPercent(IntakeConstants.kFeederSpeed);
  }

  @Override
  public void end(boolean interrupted) {
    intake.stopRoller();
  }

  @Override
  public boolean isFinished() {
    return false;  // runs until button released
  }
}