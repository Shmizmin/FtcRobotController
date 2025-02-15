package org.firstinspires.ftc.teamcode.auto;

import static org.firstinspires.ftc.teamcode.parts.MecanumBot.BL;
import static org.firstinspires.ftc.teamcode.parts.MecanumBot.BR;
import static org.firstinspires.ftc.teamcode.parts.MecanumBot.FL;
import static org.firstinspires.ftc.teamcode.parts.MecanumBot.FR;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.parts.Lifter;
import org.firstinspires.ftc.teamcode.parts.Plunger;
import org.firstinspires.ftc.teamcode.parts.CameraOpenCV;
import org.firstinspires.ftc.teamcode.util.IMU;
import org.firstinspires.ftc.teamcode.parts.MecanumBot;
import org.firstinspires.ftc.teamcode.util.PIDController;

abstract public class AutoAbstract extends LinearOpMode
{
    private static final double COUNTS_PER_MOTOR_REV = 537.7;    //gobilda 5203 motor, andymark?
    private static final double DRIVE_GEAR_REDUCTION = 0.9375;   //for gobilda 1:1, for old robot ?
    private static final double WHEEL_DIAMETER_INCHES = 3.77953; //gobilda 96mm wheels
    private static final double COUNTS_PER_INCH = ((COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /  (WHEEL_DIAMETER_INCHES * Math.PI));

    //will be used for the rev imu
    private Orientation last_angles = new Orientation();
    private double global_angle = 0.0;

    private IMU imu = null;

    //used for making accurate turns, stored as a member variable in case several functions need access to it in the future
    private PIDController pid_rotate;

    protected CameraOpenCV camera = null;
    protected MecanumBot robot = null;
    protected Lifter lifter = null;
    protected Plunger plunger = null;

    protected static final int LOW_SCORING = -490, MED_SCORING = -737;

    //sourced from https://stemrobotics.cs.pdx.edu/node/7265
    //zeroes out the measured angle from the imu
    protected void resetAngle()
    {
        last_angles = imu.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        global_angle = 0.0;
    }

    //sourced from https://stemrobotics.cs.pdx.edu/node/7265
    //modified and inspired from https://ftcforum.usfirst.org/forum/ftc-technology/53477-rev-imu-questions?p=53481#post53481
    //takes IMU (-180,180) angle range and converts it to (-inf,inf)
    //obtains the angle that the robot is on
    protected double get_angle()
    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        /*
            We want to measure error in degrees later on, so take degrees from the IMU.
            For axes ordering, we want to use intrinsic angles. This is due to the nature of how the system works.
            Instrinsic angles are defined as rotations relative to the already transformed state of something.
            Extrinsic angles are defined as rotations relative to the global coordinate system.
            Because instrinsic angles are inherently reliant upon each other, we can simply use the a different
            axis swizzling to avoid having to use Euler Angles/Spherical projection manually.s
        */

        Orientation angles = imu.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = (angles.firstAngle - last_angles.firstAngle);

             if (deltaAngle < -180) deltaAngle += 360;
        else if (deltaAngle > +180) deltaAngle -= 360;

        global_angle += deltaAngle;
        last_angles = angles;

        return global_angle;
    }

    //sourced from  https://stemrobotics.cs.pdx.edu/node/7265
    //rotates the robot the specified number of degrees
    protected void rotate(int degree)
    {
        double power = 0.55;
        double degrees = -degree;
        // restart imu angle tracking.
        resetAngle();

        // if degrees > 359 we cap at 359 with same sign as original degrees.
        degrees = Math.max(-359, Math.min(359, degrees));

        // start pid controller. PID controller will monitor the turn angle with respect to the
        // target angle and reduce power as we approach the target angle. This is to prevent the
        // robots momentum from overshooting the turn after we turn off the power. The PID controller
        // reports onTarget() = true when the difference between turn angle and target angle is within
        // 1% of target (tolerance) which is about 1 degree. This helps prevent overshoot. Overshoot is
        // dependant on the motor and gearing configuration, starting power, weight of the robot and the
        // on target tolerance. If the controller overshoots, it will reverse the sign of the output
        // turning the robot back toward the setpoint value.

        pid_rotate.reset();
        pid_rotate.setSetpoint(degrees);
        pid_rotate.setInputRange(0, degrees);
        pid_rotate.setOutputRange(0, power);
        pid_rotate.m_tolerance = 1;
        pid_rotate.enable();

        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
        // clockwise (right).

        // rotate until turn is completed.

        if (degrees < 0)
        {
            // On right turn we have to get off zero first.
            while (opModeIsActive() && get_angle() == 0)
            {
                robot.setPowerLeft(power);
                robot.setPowerRight(-power);

                sleep(100);
            }

            do
            {
                power = pid_rotate.performPID(get_angle());

                robot.setPowerLeft(-power);
                robot.setPowerRight(power);
            } while (opModeIsActive() && !pid_rotate.onTarget());
        }
        else // left turn.
        {
            do
            {
                power = pid_rotate.performPID(get_angle());

                robot.setPowerLeft(power);
                robot.setPowerRight(-power);
            } while (opModeIsActive() && !pid_rotate.onTarget());
        }

        // turn the motors off.
        robot.setPower(0.0);

        // wait for rotation to stop.
        sleep(250);

        // reset angle tracking on new heading.
        resetAngle();
    }

    protected void forward(double inches, double timeout)
    {
        encoderDrive(inches, inches, inches, inches, timeout);
    }

    protected void backward(double inches, double timeout)
    {
        encoderDrive(-inches, -inches, -inches, -inches, timeout);
    }

    //this is necessary because we don't have the spacing the same as on the model kit
    //this means that strafing action has a different effective gear ratio than forward or backward motion
    //all this does is use the calculated values (inputted distance / what distance the robot actually moves) to correct
    private double correction(double input)
    {
        final double factor = 40.0 / 32.5;
        return (input * factor);
    }

    protected void left(double inches, double timeout)
    {
        final double distance = correction(inches);
        encoderDrive(-distance, distance, distance, -distance, timeout);
    }

    protected void right(double inches, double timeout)
    {
        final double distance = correction(inches);
        encoderDrive(distance, -distance, -distance, distance, timeout);
    }

    //creates and initializes all the robot hardware, including the motors and camera
    protected void onInit(String webcam)
    {
        robot = new MecanumBot(hardwareMap);
        lifter = new Lifter(hardwareMap);
        plunger = new Plunger(hardwareMap);

        // TODO: tune these values better
        pid_rotate = new PIDController(0.003, 0.000015, 0.00005);


        camera = new CameraOpenCV(webcam, hardwareMap);
        imu = new IMU("imu", hardwareMap);

        //wait for the IMU to calibrate before proceeding
        while (!imu.imu.isGyroCalibrated())
        {
            sleep(50);
            idle();
        }

        waitForStart();
    }

    protected void encoderDrive(double fl_inches,
                                double fr_inches,
                                double bl_inches,
                                double br_inches, double timeout)
    {
        ElapsedTime drive_time = new ElapsedTime();
        drive_time.reset();

        double angle = get_angle();

        PIDController pid_drive = new PIDController(0.005, 0.0001, 0.0002);

        pid_drive.setSetpoint(0.0);
        pid_drive.setOutputRange(0, 0.8);
        pid_drive.setInputRange(-90, 90);
        pid_drive.enable();

        int fl_target = 0, fr_target = 0,
            bl_target = 0, br_target = 0;

        double power = 0.6;

        if (opModeIsActive())
        {
            robot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            fl_target = -(robot.motors[FL].getCurrentPosition() + (int)(fl_inches * COUNTS_PER_INCH));
            fr_target = -(robot.motors[FR].getCurrentPosition() + (int)(fr_inches * COUNTS_PER_INCH));
            bl_target = -(robot.motors[BL].getCurrentPosition() + (int)(bl_inches * COUNTS_PER_INCH));
            br_target = -(robot.motors[BR].getCurrentPosition() + (int)(br_inches * COUNTS_PER_INCH));

            //set up motor encoder drive targets, change their operating modes to run until they hit their targets, and start movement
            robot.motors[FL].setTargetPosition(fl_target);
            robot.motors[FR].setTargetPosition(fr_target);
            robot.motors[BL].setTargetPosition(bl_target);
            robot.motors[BR].setTargetPosition(br_target);

            robot.setPower(power);
            robot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        //use isBusy || isBusy if all motors need to reach their targets
        //using this mode can cause bugs relating to over turning targets inconsistently
        while (opModeIsActive() && (drive_time.seconds() < timeout) &&
                (robot.motors[FL].isBusy() ||
                 robot.motors[FR].isBusy() ||
                 robot.motors[BL].isBusy() ||
                 robot.motors[BR].isBusy()))
        {
            //output internal encoder data to user in the opmode
            telemetry.addData("Target", "Running to %7d :%7d :%7d :%7d",
                    fl_target,
                    fr_target,
                    bl_target,
                    br_target);

            telemetry.addData("Current", "Running at %7d :%7d :%7d :%7d",
                    robot.motors[FL].getCurrentPosition(),
                    robot.motors[FR].getCurrentPosition(),
                    robot.motors[BL].getCurrentPosition(),
                    robot.motors[BR].getCurrentPosition());

            telemetry.update();
        }

        robot.setPower(0.0);

        //reset encoder mode to the standard operating mode
        robot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //small delay between instructions, gives robot time to stop
        //make smaller if need autonomous to go faster, longer if the robot is not stopping between each call of this function
        sleep(1000);
    }
}
