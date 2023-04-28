package org.firstinspires.ftc.teamcode.parts;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Plunger
{
    public Servo servo = null;

    private final static double CLOSED = 1.0,
                                OPEN   = 0.0;

    public void close()
    {
        servo.setPosition(CLOSED);
    }

    public void open()
    {
        servo.setPosition(OPEN);
    }

    public Plunger(HardwareMap map)
    {
        servo = map.get(Servo.class, "turner");
    }

    public void onUpdate(Gamepad gp1, Gamepad gp2)
    {
        if (gp2.dpad_left)
        {
            open();
        }

        else if (gp2.dpad_right)
        {
            close();
        }
    }
}
