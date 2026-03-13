// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.subsystems.DriveSubsystem;

import static frc.robot.Constants.OperatorConstants;

public class DriveCommand extends Command {
 
    private final DriveSubsystem m_drive;
    private final CommandXboxController m_controller;

    // declare a driveChooser variable and instantiate by assigning it to a string or enum SendableChooser
    SendableChooser<String> driveChooser = new SendableChooser<String>();

     public DriveCommand(DriveSubsystem drive, CommandXboxController controller) {
        addRequirements(drive);
        m_drive = drive;
        m_controller = controller;
        


        // label for SmartDashboard
        SmartDashboard.putString("Drive Type ", "Select");

        // objects that set the options for drive mode
        driveChooser.setDefaultOption("Arcade Drive", "Arcade Drive");
        driveChooser.addOption("Tank Drive", "Tank Drive");

        // maps the Drive Mode key to the sendable DriveChooser variable
        SmartDashboard.putData("Drive Mode", driveChooser);
    
    }
    
    @Override
    public void initialize() {
        // one-time initialization code for DriveCommand here
    }
    
    @Override
    public void execute() {
        double scale = m_drive.getDriveScale(); // get drive scale from SmartDashboard
        
         // Arcade drive
        double turnPower = MathUtil.applyDeadband(m_controller.getLeftX(), OperatorConstants.kDeadband) * scale;


        // Optional: Tank drive
        double leftPower = -MathUtil.applyDeadband(m_controller.getLeftY(), OperatorConstants.kDeadband) * scale;
        double rightPower = -MathUtil.applyDeadband(m_controller.getRightY(), OperatorConstants.kDeadband) * scale;

       
        String driveMode = driveChooser.getSelected();
        if (driveMode == null) driveMode = "Arcade Drive";

        if (driveMode.equals("Arcade Drive")) {
            m_drive.setArcadePower(leftPower, turnPower);
            } else {
            m_drive.setTankPower(leftPower, rightPower);
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