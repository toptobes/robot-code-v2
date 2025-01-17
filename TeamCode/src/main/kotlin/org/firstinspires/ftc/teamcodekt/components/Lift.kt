@file:Config

package org.firstinspires.ftc.teamcodekt.components

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.acmerobotics.roadrunner.profile.MotionProfile
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator
import com.acmerobotics.roadrunner.profile.MotionState
import com.arcrobotics.ftclib.controller.PIDController
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime
import ftc.rogue.blacksmith.BlackOp.Companion.hwMap
import ftc.rogue.blacksmith.BlackOp.Companion.mTelemetry
import ftc.rogue.blacksmith.util.kalman.KalmanFilter
import ftc.rogue.blacksmith.util.kt.clamp
import ftc.rogue.blacksmith.util.kt.invoke
import org.firstinspires.ftc.teamcodekt.components.meta.DeviceNames
import kotlin.math.abs


@JvmField
var LIFT_ZERO = 0
@JvmField
var LIFT_INC = 5
@JvmField
var LIFT_LOW = 250
@JvmField
var LIFT_MID = 465
@JvmField
var LIFT_HIGH = 710


@JvmField
var ANGLED_LIFT_LOW = 210
@JvmField
var ANGLED_LIFT_MID = 340
@JvmField
var ANGLED_LIFT_HIGH = 615

// Teleop PID with no motion profiling
@JvmField
var TELEOP_RAW_P = 0.02
@JvmField
var TELEOP_RAW_I = 0.039
@JvmField
var TELEOP_RAW_D = 0.0003

@JvmField
var AUTO_P = 0.0085
@JvmField
var AUTO_I = 0.0175
@JvmField
var AUTO_D = 0.00038

// Motion profiling PID values for teleop
@JvmField
var TELEOP_MP_P = 0.003
@JvmField
var TELEOP_MP_I = 0.0001
@JvmField
var TELEOP_MP_D = 0.00015

// Motion profiling PID values for auto
@JvmField
var AUTO_MP_P = 0.043
@JvmField
var AUTO_MP_I = 0.0001
@JvmField
var AUTO_MP_D = 0.0025

// Kalman Filter Values
@JvmField
var PROCESS_NOISE = 99999999.0
@JvmField
var MEASUREMENT_NOISE = 99999999.0

// Max velocity, acceleration, and jerk for motion profiling
@JvmField
var MAX_V = 130_000.0  // 3/14 AM values: 20000.0
@JvmField
var MAX_A = 100_000.0  // 20000.0
@JvmField
var MAX_J = 70_000.0  // 20000.0

/**
 * Lift object representing the lift on our V2 robot.
 * Uses a Kalman Filter with motion profiling to achieve a +/- 1 tick accuracy during auto.
 */
class Lift {
    private val liftMotor = hwMap<DcMotorSimple>(DeviceNames.LIFT_MOTOR)

    private val liftNormalPID = PIDController(TELEOP_RAW_P, TELEOP_RAW_I, TELEOP_RAW_D)
    private val autoNormalPID = PIDController(AUTO_P, AUTO_I, AUTO_D)

    private val liftMotionProfilePID =
        PIDFController(PIDCoefficients(TELEOP_MP_P, TELEOP_MP_I, TELEOP_MP_D))
    private val autoLiftMotionProfilePID =
        PIDFController(PIDCoefficients(AUTO_MP_P, AUTO_MP_I, AUTO_MP_D))
    private var motionProfile: MotionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
        MotionState(0.0, 0.0, 0.0),
        MotionState(0.0, 0.0, 0.0),
        MAX_V,
        MAX_A,
        MAX_J
    )
    private var moveTime = ElapsedTime()

    private val liftFilter = KalmanFilter(PROCESS_NOISE, MEASUREMENT_NOISE)

    private val liftEncoder = Motor(hwMap, DeviceNames.LIFT_ENCODER)
        .apply(Motor::resetEncoder)

    private var drivenCorrection = 0.0

    private var state = MotionState(0.0, 0.0)

    var targetHeight: Int = 0
        set(height) {
            field = height
//            generateMotionProfile()
        }
    var clippedHeight: Int
        get() = targetHeight
        set(height) {
            targetHeight = height.clamp(LIFT_ZERO, LIFT_HIGH)
        }

    fun incHeight() {
        targetHeight += LIFT_INC
    }

    fun decHeight() {
        targetHeight -= LIFT_INC
    }

    fun goToZero() {
        targetHeight = LIFT_ZERO
    }

    fun goToLow() {
        targetHeight = LIFT_LOW
    }

    fun goToMid() {
        targetHeight = LIFT_MID
    }

    fun goToHigh() {
        targetHeight = LIFT_HIGH
    }

    fun goToAngledLow() {
        targetHeight = ANGLED_LIFT_LOW
    }

    fun goToAngledMid() {
        targetHeight = ANGLED_LIFT_MID
    }

    fun goToAngledHigh() {
        targetHeight = ANGLED_LIFT_HIGH
    }

    fun generateMotionProfile() {
        motionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
            MotionState(liftHeight.toDouble(), 0.0, 0.0),
            MotionState(targetHeight.toDouble(), 0.0, 0.0),
            MAX_V,
            MAX_A,
            MAX_J
        )
        moveTime.reset()
    }

    fun updateAutoLiftNormalPID(){
        val liftHeight = liftHeight.toDouble()
        val targetHeight = targetHeight.toDouble()

        val correction = autoNormalPID.calculate(liftHeight, targetHeight)

        drivenCorrection = correction
        liftMotor.power = correction

    }


    fun updateMotionProfile(auto: Boolean) {
        state = motionProfile[moveTime.milliseconds() / 1000]


        val correction: Double
        if (!auto) {
            liftMotionProfilePID.apply {
                targetPosition = state.x
                targetVelocity = state.v
                targetAcceleration = state.a
            }
            correction = liftMotionProfilePID.update(liftHeight.toDouble())
        } else {
            autoLiftMotionProfilePID.apply {
                targetPosition = state.x
                targetVelocity = state.v
                targetAcceleration = state.a
            }
            correction = autoLiftMotionProfilePID.update(liftHeight.toDouble())
        }

        liftMotor.power += liftFilter.filter(correction)
    }

    fun updateTeleopNormalPID(useDeadzone: Boolean) {
        val inDeadzone = !useDeadzone || abs(targetHeight - liftHeight) > 3

        if (inDeadzone) {
            val liftHeight = liftHeight.toDouble()
            val targetHeight = targetHeight.toDouble()

            val correction = liftNormalPID.calculate(liftHeight, targetHeight)
            val filteredCorrection = liftFilter.filter(correction)

            drivenCorrection = filteredCorrection
            liftMotor.power = filteredCorrection
        } else {
            drivenCorrection = 0.0
            liftMotor.power = 0.0
        }
    }

    fun resetEncoder() {
        liftEncoder.resetEncoder()
    }

    private val liftHeight: Int
        get() = -liftEncoder.currentPosition

    fun printLiftTelem() {
        mTelemetry.addData("_Motion Profile height:", state.x)
        mTelemetry.addData("_Current lift height:", liftHeight)
        mTelemetry.addData("_Lift target height:", targetHeight)
        mTelemetry.addData("Driven motor power:", drivenCorrection)
    }
}
