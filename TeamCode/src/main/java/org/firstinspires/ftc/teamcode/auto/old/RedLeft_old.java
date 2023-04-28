package org.firstinspires.ftc.teamcode.auto.old;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.auto.AutoAbstract;

@Disabled
@Autonomous(name = "Red Left")
public class RedLeft_old extends AutoAbstract
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        onInit("rightWebcam");

        plunger.close();

        sleep(2000);

        camera.webcam.stopStreaming();

        lifter.set(-200);
        sleep(500);

        forward(1.5, 2.0);

        right(11.5, 4.0);

        lifter.set(LOW_SCORING);
        sleep(500);

        forward(5.5, 3.0);

        plunger.open();

        sleep(500);

        backward(5.5, 3.0);

        lifter.release();
        sleep(200);

        left(11.5, 4.0);

        switch (camera.scanner.position)
        {
            case RED:
            {
                left(24.0, 5.0);
            } break;

            case GREEN:
            {
                // no movement for now
            } break;

            case BLUE:
            {
                right(24.0, 5.0);
            } break;
        }

        forward(30.0, 10.0);
    }
}
