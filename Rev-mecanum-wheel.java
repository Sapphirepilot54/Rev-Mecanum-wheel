package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Rev-mecanum-wheel", group = "Teleop")
public class Rev-mecanum-wheel extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotorEx flywheel;
    private DcMotor lift;
    private Servo servoX;   // servo controlled by X

    // for toggle behavior
    private boolean prevY = false;
    private boolean flywheelOn = false;

    private boolean prevX = false;
    private boolean liftOn = false;

    private boolean prevX2 = false;
    private boolean servoExtended = false;

    @Override
    public void runOpMode() {

        // Map hardware – make sure these names match your RC configuration
        frontLeft  = hardwareMap.get(DcMotor.class, "FrontLeft");
        backLeft   = hardwareMap.get(DcMotor.class, "BackLeft");
        frontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        backRight  = hardwareMap.get(DcMotor.class, "BackRight");
        flywheel   = hardwareMap.get(DcMotorEx.class, "flywheel");
        lift       = hardwareMap.get(DcMotor.class, "lift");
        servoX     = hardwareMap.get(Servo.class, "servoX"); // change name to match config

        // Motor directions – adjust if your robot drives backwards or strafes wrong
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setDirection(DcMotor.Direction.REVERSE);

        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Start with servo retracted
        servoX.setPosition(0.0);

        waitForStart();

        while (opModeIsActive()) {

            // -------- Mecanum drive (gamepad1) --------
            double drive  = -gamepad1.left_stick_y;   // forward/back
            double strafe =  gamepad1.left_stick_x;   // left/right
            double turn   =  gamepad1.right_stick_x;  // rotate

            double flPower = drive + strafe + turn;
            double blPower = drive - strafe + turn;
            double frPower = drive - strafe - turn;
            double brPower = drive + strafe - turn;

            // Normalize if any power is > 1.0
            double max = Math.max(
                    Math.max(Math.abs(flPower), Math.abs(frPower)),
                    Math.max(Math.abs(blPower), Math.abs(brPower))
            );
            if (max > 1.0) {
                flPower /= max;
                frPower /= max;
                blPower /= max;
                brPower /= max;
            }

            frontLeft.setPower(flPower);
            frontRight.setPower(frPower);
            backLeft.setPower(blPower);
            backRight.setPower(brPower);

            // -------- Flywheel on Y (toggle, gamepad1) --------
            boolean yNow = gamepad1.y;
            if (yNow && !prevY) {
                // rising edge: flip state
                flywheelOn = !flywheelOn;
                if (flywheelOn) {
                    flywheel.setPower(1.0);  // full power; adjust as needed
                } else {
                    flywheel.setPower(0.0);
                }
            }
            prevY = yNow;

            // -------- Lift on X (hold, gamepad1) --------
            // If you want hold-to-move instead of toggle, this is simpler:
            if (gamepad1.x) {
                lift.setPower(1.0);   // up
            } else if (gamepad1.b) {
                lift.setPower(-1.0);  // down
            } else {
                lift.setPower(0.0);
            }

            // -------- Servo on X (toggle, gamepad2) --------
            boolean x2Now = gamepad2.x;
            if (x2Now && !prevX2) {
                servoExtended = !servoExtended;
                if (servoExtended) {
                    servoX.setPosition(1.0);   // extended position
                } else {
                    servoX.setPosition(0.0);   // retracted position
                }
            }
            prevX2 = x2Now;

            // Basic telemetry
            telemetry.addData("FL", flPower);
            telemetry.addData("FR", frPower);
            telemetry.addData("BL", blPower);
            telemetry.addData("BR", brPower);
            telemetry.addData("FlywheelOn", flywheelOn);
            telemetry.addData("ServoExtended", servoExtended);
            telemetry.update();
        }
    }
}

