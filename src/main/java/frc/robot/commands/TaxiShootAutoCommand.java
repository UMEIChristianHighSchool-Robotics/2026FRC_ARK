  // Copyright (c) FIRST and other WPILib contributors.
  // Open Source Software; you can modify and/or share it under the terms of
  // the WPILib BSD license file in the root directory of this project.

  package frc.robot.commands;

  import edu.wpi.first.wpilibj2.command.InstantCommand;
  import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
  import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
  import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
  import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
  import frc.robot.subsystems.DriveSubsystem;
  import frc.robot.subsystems.IntakeRollerSubsystem;
  import frc.robot.subsystems.ShooterSubsystem;

  public class TaxiShootAutoCommand extends SequentialCommandGroup {

    public TaxiShootAutoCommand(
        DriveSubsystem drive,
        IntakeRollerSubsystem intakeRoller,
        ShooterSubsystem shooter) {
  
        addCommands(
          
          new HoldShootCommand(shooter).withTimeout(1.5),
          new InstantCommand(shooter::stop, shooter),
          //Drive forward
          drive.driveForwardMeters(2.5),
          new InstantCommand(intakeRoller::stopRoller, intakeRoller)
        );
    }
  }
