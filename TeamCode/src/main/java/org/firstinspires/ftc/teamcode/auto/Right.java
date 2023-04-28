package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.parts.MecanumBot;

@Autonomous(name = "Right")
public class Right extends AutoAbstract
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        onInit("leftWebcam");

        plunger.close();

        sleep(1000);
        camera.webcam.stopStreaming();

        lifter.set(LOW_SCORING - 60);

        robot.motors[MecanumBot.FR].setPower(-0.6);
        robot.motors[MecanumBot.BL].setPower(-0.6);
        robot.motors[MecanumBot.FL].setPower( 0.6);
        robot.motors[MecanumBot.BR].setPower( 0.6);

        sleep(75);

        robot.setPower(0.0);

        backward(19.5+1.5, 2.0);

        left(17.0, 2.0);

        forward(5.5, 1.0);
        plunger.open();
        sleep(250);
        backward(2.0, 1.0);

        left(36.0, 3.0);

        robot.setPowerLeft(-0.5);
        sleep(80);
        robot.setPowerRight(0.0);

        lifter.set(LOW_SCORING);

        forward(54.0, 4.0);

        sleep(100);
        lifter.release();
        sleep(600);

        plunger.close();
        sleep(100);

        lifter.set(LOW_SCORING - 70);
        sleep(500);

        backward(26.0, 3.0);

        right(10.0, 2.0);

        forward(5.0, 0.5);
        plunger.open();
        sleep(250);

        //backward(5.0, 0.5);

        //back
        robot.setPower(0.6);
        sleep(100);
        robot.setPower(0.0);

        sleep(150);

        //left
        robot.motors[MecanumBot.FR].setPower(-0.6);
        robot.motors[MecanumBot.BL].setPower(-0.6);
        robot.motors[MecanumBot.FL].setPower( 0.6);
        robot.motors[MecanumBot.BR].setPower( 0.6);

        sleep(540);

        robot.setPower(0.0);

        sleep(150);

        //left(12.0, 1.5);

        switch (camera.scanner.position)
        {
            case RED:
            {
                backward(24.0, 4.0);
            } break;

            case GREEN:
            {

            } break;

            case BLUE:
            {
                forward(24.0, 4.0);
            } break;
        }
    }
}
