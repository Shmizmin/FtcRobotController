package org.firstinspires.ftc.teamcode.parts;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Lifter
{
    public DcMotor motor;
    public boolean buttonOperated = false;
    public ElapsedTime debouncer = null;

    public void set(int counts)
    {
        motor.setTargetPosition(counts);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.6);
    }

    public void release()
    {
        motor.setPower(0.0);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public Lifter(HardwareMap map)
    {
        debouncer = new ElapsedTime();
        debouncer.reset();

        motor = map.get(DcMotor.class, "lifter");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void onUpdate(Gamepad gp1, Gamepad gp2)
    {
        if (gp2.a && (debouncer.milliseconds() > 500))
        {
            if (buttonOperated)
            {
                //motor.setMode(DcMotor.RunMode.RUN)
                release();
            }

            buttonOperated = !buttonOperated;
            debouncer.reset();
        }

        if (buttonOperated)
        {
            if (gp2.x)
            {
                debouncer.reset();
                set(-614);
            }

            else if (gp2.y)
            {
                debouncer.reset();
                set(-875);
            }

            else if (gp2.b)
            {
                debouncer.reset();
                set(-1145);
            }
        }

        else
        {
            motor.setPower((gp2.left_trigger - gp2.right_trigger) * 0.5);
        }
    }
}
