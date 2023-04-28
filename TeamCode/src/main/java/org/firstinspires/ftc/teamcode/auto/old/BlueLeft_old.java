package org.firstinspires.ftc.teamcode.auto.old;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.auto.AutoAbstract;

@Disabled
@Autonomous(name = "Blue Left")
public class BlueLeft_old extends AutoAbstract
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

        right(22.5, 6.0);

        forward(25.0, 3.0);

        left(11.5, 5.0);

        lifter.set(MED_SCORING);
        sleep(500);

        forward(5.0, 3.0);

        plunger.open();

        sleep(500);

        backward(4.5, 3.0);

        lifter.release();
        sleep(200);

        switch (camera.scanner.position)
        {
            case RED:
            {
                left(12.5 + 22.5, 6.0);
            } break;

            case GREEN:
            {
                left(12.5, 6.0);
            } break;

            case BLUE:
            {
                right(11.5, 5.0);
            } break;
        }
    }
}
