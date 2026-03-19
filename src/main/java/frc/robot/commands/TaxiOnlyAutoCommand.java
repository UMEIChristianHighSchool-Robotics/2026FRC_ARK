// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveSubsystem;

public class TaxiOnlyAutoCommand extends SequentialCommandGroup {

    private final DriveSubsystem m_drive;
    private final ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");

    public TaxiOnlyAutoCommand(DriveSubsystem drive) {
        m_drive = drive;
        addRequirements(drive);
       
       
        addCommands(
            drive.driveForwardMeters(1.0));

    //telemetry
    autoTab.add("Distance (m)", m_drive::getDistanceMeters);
     
    }
}
