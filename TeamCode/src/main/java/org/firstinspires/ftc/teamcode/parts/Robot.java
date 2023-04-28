package org.firstinspires.ftc.teamcode.parts;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.parts.Lifter;
import org.firstinspires.ftc.teamcode.parts.MecanumBot;
import org.firstinspires.ftc.teamcode.parts.Plunger;

public class Robot
{
    public MecanumBot bot = null;
    public Lifter lifter = null;
    public Plunger plunger = null;
    //add other hardware here

    public void onUpdate(Gamepad gp1, Gamepad gp2)
    {
        bot.onUpdate(gp1, gp2);
        lifter.onUpdate(gp1, gp2);
        plunger.onUpdate(gp1, gp2);
    }

    public Robot(HardwareMap map)
    {
        bot = new MecanumBot(map);
        lifter = new Lifter(map);
        plunger = new Plunger(map);
    }
}
