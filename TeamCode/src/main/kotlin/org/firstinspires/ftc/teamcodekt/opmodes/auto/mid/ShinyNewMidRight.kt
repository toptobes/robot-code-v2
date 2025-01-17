package org.firstinspires.ftc.teamcodekt.opmodes.auto.mid

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ftc.rogue.blacksmith.Anvil
import ftc.rogue.blacksmith.units.GlobalUnits
import ftc.rogue.blacksmith.util.toIn
import ftc.rogue.blacksmith.util.toRad
import org.firstinspires.ftc.teamcode.AutoData
import org.firstinspires.ftc.teamcode.AutoData.liftOffsets
import org.firstinspires.ftc.teamcode.roadrunner.drive.DriveConstants
import org.firstinspires.ftc.teamcodekt.components.LIFT_LOW
import org.firstinspires.ftc.teamcodekt.components.LIFT_MID
import org.firstinspires.ftc.teamcodekt.opmodes.auto.RogueBaseAuto
import org.firstinspires.ftc.teamcodekt.util.CycleException

@Autonomous
class ShinyNewMidRight : RogueBaseAuto() {
    override val startPose = GlobalUnits.pos(91, -163, 90)

    override fun mainTraj(startPose: Pose2d) =
        Anvil.forgeTrajectory(bot.drive, startPose)
            .addTemporalMarker {
                bot.lift.targetHeight = LIFT_LOW - 95
                bot.claw.close()
                bot.arm.setToForwardsDownPos()
                bot.wrist.setToForwardsPos()
            }

            .addTemporalMarker(1420) {
                bot.lift.targetHeight = LIFT_MID
                bot.arm.setToForwardsPos()
            }
            .initialGoToDeposit()
            .initialDeposit()

            .doTimes(NUM_CYCLES) {
                when (it) {
                    LAST_CYCLE -> fastIntakePrep(it)
                    else -> regularIntakePrep(it)
                }

                goToIntake(it)

//                setPoseEstimateInTemporalMarker(-150) {
//                    val correction = bot.camera.tapeDetectorPipeline.correction.toIn()
//
//                    val (x, _y, h) = bot.drive.localizer.poseEstimate
//                    val y = _y - correction
//
//                    Pose2d(x, y, h)
//                }

                when (it) {
                    LAST_CYCLE -> awaitFastIntake()
                    else -> awaitRegularIntake()
                }

                goToDeposit(it)
                deposit(it)
            }

            .thenRun(::parkTraj)

    private fun Anvil.initialGoToDeposit() = this
        .splineTo(82.25, -52, 215)

    private fun Anvil.goToDeposit(it: Int) = when (it) {
        0 -> splineTo(-2.0 + 78 + poleOffset.x, -46.0 + poleOffset.y, 180+50)
        1 -> splineTo(-1.5 + 78 + poleOffset.x, -41.0 + poleOffset.y, 180+50)
        2 -> splineTo(0.25 + 77 + poleOffset.x, -41.0 + poleOffset.y, 180+47)
        3 -> splineTo(-1.5 + 76.5 + poleOffset.x, -38.0 + poleOffset.y, 180+48)
        4 -> splineTo(-0.3 + 76 + poleOffset.x, -37 + poleOffset.y, 180+47)
        else -> throw CycleException()
    }

    private fun Anvil.goToIntake(it: Int) = when (it) {
        0 -> splineTo(160.9 + .8, -28.25, 0)
        1 -> splineTo(160.7 + .6, -23.75, 0)
        2 -> splineTo(160.4 + 0.3, -21.5, 0)
        3 -> splineTo(160.4 + 0.3, -21.5, 0)
        4 -> splineTo(160.2 + 0.3, -21.5, 0)
        else -> throw CycleException()
    }.doInReverse()

    private fun Anvil.awaitRegularIntake() = this
        .addTemporalMarker(-170) {
            bot.intake.disable()
        }

        .addTemporalMarker(-60) {
            bot.claw.close()
        }

        .addTemporalMarker(190) {
            bot.lift.goToMid()
            bot.arm.setToForwardsPos()

        }

        .addTemporalMarker(450) {
            bot.wrist.setToForwardsPos()
        }

        .waitTime(80)

    private fun Anvil.awaitFastIntake() = this
        .addTemporalMarker(-275) {
            bot.intake.disable()
        }

        .addTemporalMarker(-75) {
            bot.claw.close()
        }

        .addTemporalMarker(60) {
            bot.lift.goToMid()
        }

        .addTemporalMarker(200) {
            bot.arm.setToForwardsPos()

        }

        .addTemporalMarker(410) {
            bot.wrist.setToForwardsPos()
        }
        .waitTime(0)



    private fun Anvil.initialDeposit() = this
        .addTemporalMarker(-120) {
            bot.lift.targetHeight -= AutoData.DEPOSIT_DROP_AMOUNT
            bot.arm.setToForwardsDownPos()
        }
        .addTemporalMarker(-25) {
            bot.claw.openForDeposit()
//            bot.intake.enable()
        }
        .addTemporalMarker(300) {
            bot.lift.targetHeight = liftOffsets[0]
        }

    private fun Anvil.deposit(iterations: Int) = this.apply {
        addTemporalMarker(-265) {
            bot.lift.targetHeight -= AutoData.DEPOSIT_DROP_AMOUNT
            bot.arm.setToForwardsDownPos()
        }

        val durationOffset = if (iterations < 4) -80 else -100

        addTemporalMarker(durationOffset) {
            bot.claw.openForDeposit()
        }
    }

    private fun Anvil.regularIntakePrep(iterations: Int) = this
        .addTemporalMarker(185) {
//            when (iterations) {
//                0 -> bot.lift.targetHeight = liftOffsets[iterations]-2
//                1 -> bot.lift.targetHeight = liftOffsets[iterations]-8
//                2 -> bot.lift.targetHeight = liftOffsets[iterations]-8
//                3 -> bot.lift.targetHeight = liftOffsets[iterations]-8
//                4 -> bot.lift.targetHeight = liftOffsets[iterations]-8
//            }
            bot.lift.targetHeight = liftOffsets[iterations]-25
            bot.wrist.setToBackwardsPos()
        }

        .addTemporalMarker(325) {
            bot.claw.openForIntakeWide()
//            bot.intake.enable()
        }

        .addTemporalMarker(500) {
            bot.arm.targetAngle = 43.0
        }

    private fun Anvil.fastIntakePrep(iterations: Int) = this
        .addTemporalMarker(185) {
            bot.lift.targetHeight = liftOffsets[iterations]

            bot.wrist.setToBackwardsPos()

            bot.claw.openForIntakeNarrow()
            bot.intake.enable()
        }

        .addTemporalMarker(500) {
            bot.arm.targetAngle = 41.5
        }

    private fun parkTraj(startPose: Pose2d) =
        Anvil.forgeTrajectory(bot.drive, startPose) {
            resetBot()

            when (signalID) {
                1 -> inReverse {
                    lineToLinearHeading(90.50, -17, 0)
                    setVelConstraint(100, 260.toRad(), DriveConstants.TRACK_WIDTH)
                    setAccelConstraint(80)
                    lineToLinearHeading(30, -13, 0)


                }
                2 -> {
                    setVelConstraint(100, 260.toRad(), DriveConstants.TRACK_WIDTH)
                    setAccelConstraint(80)
                    lineToLinearHeading(90.50, -17, 0)
                }
                3 -> {
                    lineToLinearHeading(90.50, -17, 180)
                    setVelConstraint(100, 260.toRad(), DriveConstants.TRACK_WIDTH)
                    setAccelConstraint(80)
                    lineToLinearHeading(160, -17, 180)
                }
            }

            this
        }

    private fun Anvil.resetBot() = this
        .addTemporalMarker {
            bot.arm.goToRest()
            bot.wrist.setToRestingPos()
            bot.lift.goToZero()
            bot.claw.close()
        }
}
