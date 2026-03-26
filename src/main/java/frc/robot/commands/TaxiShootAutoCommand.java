  // Copyright (c) FIRST and other WPILib contributors.
  // Open Source Software; you can modify and/or share it under the terms of
  // the WPILib BSD license file in the root directory of this project.

  package frc.robot.commands;

  import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
  import frc.robot.subsystems.DriveSubsystem;
  import frc.robot.subsystems.ShooterSubsystem;
  import static frc.robot.Constants.ShooterConstants;

  public class TaxiShootAutoCommand extends SequentialCommandGroup {

    public TaxiShootAutoCommand(
        DriveSubsystem drive,
        ShooterSubsystem shooter) {
    
        addCommands(
          shooter.autoShoot(ShooterConstants.kTargetSpeed).withTimeout(3),
          //Drive forward
          drive.driveForwardMeters(3)
        );
    }

  }
