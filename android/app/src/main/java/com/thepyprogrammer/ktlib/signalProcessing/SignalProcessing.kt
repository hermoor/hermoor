package com.thepyprogrammer.ktlib.signalProcessing

import div
import normalise
import slice
import sum
import timesConj
import toComplex
import transpose
import kotlin.math.pow
import kotlin.math.roundToInt

const val SR = 64.0
const val stepSize = 1
const val windowLength = 256

private val f_nr_LBs: Int = (0.5 * windowLength / SR).roundToInt()
private val f_nr_LBe: Int = (3 * windowLength / SR).roundToInt()
private val f_nr_FBs: Int = (3 * windowLength / SR).roundToInt()
private val f_nr_FBe: Int = (8 * windowLength / SR).roundToInt()

val powerTH = 2.0.pow(11.5)

/**
 * Do numerical integration of x
 */
fun numIntegration(x: Array<Double>): Double {
    val term1: Double = x.slice(1, x.size).sum()
    val term2: Double = x.slice(0, -1).sum()
    return (term1 + term2) / (SR * 2)
}

/**
 * Moore's Algorithm
 * Compute the Freeze Index for a certain axis
 */
fun freeze(data: Array<Double>): Array<Double> {
    var jPos: Int
    val iMax: Int = (data.size / stepSize)

    val freezeIndex = mutableListOf<Double>()
    for (i in 0..iMax) {
        // Time (sample nr) of this window
        val jStart = i * stepSize
        jPos = jStart + windowLength


        // compute com.thepyprogrammer.gaitanalyzer.model.signalProcessing.FFT (Fast Fourier Transform)
        val y = FFT.fft(
            // get the signal in the window
            data.slice(jStart, jPos)
                // make signal zero-mean (mean normalization)
                .normalise()
                .toComplex()
        )
        val pyy: Array<Double> = y.timesConj() / windowLength


        //--- calculate sumLocoFreeze and com.thepyprogrammer.gaitanalyzer.model.signalProcessing.freezeIndex ---
        val areaLocoBand = numIntegration(pyy.slice(f_nr_LBs, f_nr_LBe, 1))
        val areaFreezeBand = numIntegration(pyy.slice(f_nr_FBs, f_nr_FBe, 1))

        // Extension of Baechlin to handle low-energy situations (e.g. standing)
        freezeIndex.add(
            if (areaFreezeBand + areaLocoBand >= powerTH)
                areaFreezeBand / areaLocoBand
            else 0.0
        )
    }

    return freezeIndex.toTypedArray()
}

/**
 * Computes the Freeze Index for all 3 axes
 */
fun freezeIndex(data: Array<Array<Double>>): Array<Array<Double>> {
    val fis = mutableListOf<Array<Double>>()

    val dat = data.transpose()

    for (axis in 1..5) {
        fis.add(freeze(dat[axis]))
    }

    return fis.toTypedArray()
}
