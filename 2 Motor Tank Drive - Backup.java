package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "REV 2 Motor Tank Drive", group = "TeleOp")
public class RevTwoMotorTankDrive extends LinearOpMode {

    private DcMotor leftMotor;
    private DcMotor rightMotor;

    @Override
    public void runOpMode() {

        // Hardware mapping — these names MUST match your Robot Controller configuration
        leftMotor  = hardwareMap.get(DcMotor.class, "left");
        rightMotor = hardwareMap.get(DcMotor.class, "right");

        // Typical REV 2-motor tank directions
        // Left motor is almost always reversed on REV chassis
        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setDirection(DcMotor.Direction.FORWARD);

        // Run without encoders for basic teleop
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Brake mode helps the robot stop quickly when sticks are released
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        while (opModeIsActive()) {

            double drive = -gamepad1.left_stick_y;   // Forward / Backward
            double turn  =  gamepad1.right_stick_x;  // Turn left / right

            // Tank / Arcade style power calculation
            double leftPower  = drive + turn;
            double rightPower = drive - turn;

            // Normalize so powers stay between -1.0 and 1.0
            double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (max > 1.0) {
                leftPower  /= max;
                rightPower /= max;
            }

            leftMotor.setPower(leftPower);
            rightMotor.setPower(rightPower);

            telemetry.addData("Left Power", "%.2f", leftPower);
            telemetry.addData("Right Power", "%.2f", rightPower);
            telemetry.addData("Drive", "%.2f", drive);
            telemetry.addData("Turn", "%.2f", turn);
            telemetry.update();
        }
    }
}
