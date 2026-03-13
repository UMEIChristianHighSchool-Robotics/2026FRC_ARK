package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystem;

public class DeployIntakeCommand extends InstantCommand {
  public DeployIntakeCommand(IntakeSubsystem intakeSubsystem) {
    super(() -> intakeSubsystem.setTargetAngleDeg(IntakeConstants.kDeployedAngleDeg),
        intakeSubsystem);
  }
}
