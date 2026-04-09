// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.subsystems.DriveSubsystem;

import static frc.robot.Constants.OperatorConstants;

public class DriveCommand extends Command {
 
    private final DriveSubsystem m_drive;
    private final CommandXboxController m_controller;

    //Create a tab in Shuffleboard
    private ShuffleboardTab driveTab= Shuffleboard.getTab("Drive");

    // declare a driveChooser variable and instantiate by assigning it to a string or enum SendableChooser
    private SendableChooser<String> driveChooser = new SendableChooser<String>();

     public DriveCommand(DriveSubsystem drive, CommandXboxController controller) {
        addRequirements(drive);
        m_drive = drive;
        m_controller = controller;
        
        // objects that set the options for drive mode
        driveChooser.setDefaultOption("Arcade Drive", "Arcade Drive");
        driveChooser.addOption("Tank Drive", "Tank Drive");

        // maps the Drive Mode key to the sendable DriveChooser variable
        driveTab.add("Drive Mode", driveChooser);
    
    }
    
    @Override
    public void initialize() {
        // one-time initialization code for DriveCommand here
    }
    
    @Override
    public void execute() {
        String driveMode = driveChooser.getSelected();

        if (driveMode == null) driveMode = "Arcade Drive";

        if (driveMode.equals("Arcade Drive")) {
            double forward = -MathUtil.applyDeadband(m_controller.getLeftY(), OperatorConstants.kDeadband) * m_drive.getForwardScale();
            double turn = -MathUtil.applyDeadband(m_controller.getLeftX(), OperatorConstants.kDeadband) * m_drive.getTurnScale();
            m_drive.setArcadePower(forward, turn);
        } else {
            double left = MathUtil.applyDeadband(m_controller.getLeftY(), OperatorConstants.kDeadband) * m_drive.getForwardScale();
            double right = MathUtil.applyDeadband(m_controller.getRightY(), OperatorConstants.kDeadband) * m_drive.getForwardScale();
            m_drive.setTankPower(left, right);
        }

    }

    // called once the command ends or is interrupted
    @Override
    public void end(final boolean interrupted) {
        m_drive.stop();
    }
    
    // returns true or false when the command should end
    @Override
    public boolean isFinished() {
        return false; // false = command will never finish (we don't want it to)
    }
}