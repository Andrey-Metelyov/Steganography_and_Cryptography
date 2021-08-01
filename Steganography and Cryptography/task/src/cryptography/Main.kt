package cryptography

import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (readLine()!!) {
            "exit" -> exit()
            "hide" -> hide()
            else -> showError()
        }
    }
}

fun showError() {
    println("Error")
}

fun hide() {
    println("Input image file:")
    val inputFileName = readLine()!!
    println("Output image file:")
    val outputFileName = readLine()!!
    try {
        val inputFile = File(inputFileName)
        val outputFile = File(outputFileName)
        println("Input Image: $inputFileName")
        println("Output Image: $outputFileName")
        val img = ImageIO.read(inputFile)
        for (row in 0 until img.height) {
            for (col in 0 until img.width) {
                var pixel = img.getRGB(col, row)
                pixel = pixel.or(0b00000000_00000001_00000001_00000001)
                img.setRGB(col, row, pixel)
            }
        }
        ImageIO.write(img, "png", outputFile)
        println("Image $outputFile is saved.")
    } catch (e: Exception) {
        println("exception: ${e.message}")
    }
}

fun exit() {
    println("Bye!")
    exitProcess(0)
}
