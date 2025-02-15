package org.firstinspires.ftc.teamcode.parts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Objects;

public class CameraOpenCV
{
    public enum Position
    {
        RED,
        GREEN,
        BLUE,
    }

    public class ConeScanner extends OpenCvPipeline
    {
        private Mat working = new Mat();

        public Position position = Position.BLUE;

        public ConeScanner()
        {
        }

        @Override
        public final Mat processFrame(Mat in)
        {
            in.copyTo(working);

            if (working.empty())
                return in;

            int x = 190, y = 40,
                w = 30,  h = 70;

            Mat sub = working.submat(y, y + h, x, x +w);

            Imgproc.rectangle(working,
                    new Rect(x, y, w, h),
                        (position == Position.RED   ? new Scalar(255, 0, 0) :
                        (position == Position.GREEN ? new Scalar(0, 255, 0) :
                        (position == Position.BLUE  ? new Scalar(0, 0, 255) : new Scalar(40, 40, 40)))));

            double r = Core.sumElems(sub).val[0],
                   g = Core.sumElems(sub).val[1],
                   b = Core.sumElems(sub).val[2];

            double m = Math.max(r, Math.max(g, b));

                 if (r == m) position = Position.RED;
            else if (g == m) position = Position.GREEN;
            else if (b == m) position = Position.BLUE;

            return working;
        }
    }

    public OpenCvWebcam webcam;
    public ConeScanner scanner;

    public CameraOpenCV(String name, HardwareMap map)
    {
        //find the webcam hardware device on the control hub and obtain a handle to it
        int cmv_id = map.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", map.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(map.get(WebcamName.class, name), cmv_id);


        //set the calculation pipeline to the scanner class created
        scanner = new ConeScanner();
        webcam.setPipeline(scanner);

        // NOTE: timeout for obtaining permission is configurable
        webcam.setMillisecondsPermissionTimeout(2500);

        /*
            Opens up the camera device on a new thread. The synchronous single-threaded function is now deprecated, so we have to make
            a lambda function that handles success and failure of the camera opening asynchronously. If the camera is successfully opened,
            the streaming starts, which begins to repeatedly process frames through the pipeline, which is where we detect ducks/elements.
        */

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int error_code)
            {
                // TODO: add error handling to this
            }
        });
    }
}
