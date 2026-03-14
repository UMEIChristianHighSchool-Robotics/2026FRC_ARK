package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystem;

//This command is for running the intake in reverse to clear jams  
public class ReverseIntakeRollerCommand extends Command {

  private final IntakeSubsystem intake;

  public ReverseIntakeRollerCommand(IntakeSubsystem intake) {
    this.intake = intake;
    addRequirements(intake);
  }

  @Override
  public void execute() {
    intake.runRoller(-IntakeConstants.kRollerVoltage);
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