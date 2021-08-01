package cryptography

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (readLine()!!) {
            "exit" -> exit()
            "hide" -> hide()
            "show" -> show()
            else -> showError()
        }
    }
}

fun show() {
    println("Input image file:")
    val inputFileName = readLine()!!
    try {
        val inputFile = File(inputFileName)
        val img = ImageIO.read(inputFile)
        println("Message: ${decrypt(img)}")
    } catch (e: Exception) {
        println("exception: ${e.message}")
    }
}

fun decrypt(image: BufferedImage): String {
    val bytes = mutableListOf<Byte>()

    for (byteNumber in 0 until image.width * image.width) {
        var byte = 0
        for (curBit in 7 downTo 0) {
            val bitNumber = byteNumber * 8 + 7 - curBit
            val col = (bitNumber) % image.width
            val row = (bitNumber) / image.width
            byte = byte.or(image.getRGB(col, row).and(1).shl(curBit))
        }
        System.err.println("add byte: $byte")
        bytes.add(byte.toByte())
        if (bytes.size > 2 &&
            bytes[bytes.lastIndex] == 3.toByte() &&
            bytes[bytes.lastIndex - 1] == 0.toByte() &&
            bytes[bytes.lastIndex - 2] == 0.toByte()) {
            break
        }

    }
    return bytes.subList(0, bytes.size - 3).toByteArray().toString(Charsets.UTF_8)
//    for (row in 0 until image.height) {
//        for (col in 0 until image.width) {
//            var pixel = image.getRGB(col, row)
//            pixel = pixel.or(0b00000000_00000000_00000000_00000001)
//            image.setRGB(col, row, pixel)
//        }
//    }
}

fun showError() {
    println("Error")
}

fun hide() {
    println("Input image file:")
    val inputFileName = readLine()!!
    println("Output image file:")
    val outputFileName = readLine()!!
    println("Message to hide:")
    val message = readLine()!!
    try {
        val inputFile = File(inputFileName)
        val outputFile = File(outputFileName)

        val img = ImageIO.read(inputFile)

        if (img.height * img.width < (message.length + 3) * 8) {
            println("The input image is not large enough to hold this message.")
            return
        }

        encrypt(img, message)

        ImageIO.write(img, "png", outputFile)
        println("Message saved in $outputFile image.")
    } catch (e: Exception) {
        println("exception: ${e.message}")
    }
}

fun encrypt(image: BufferedImage, message: String) {
    System.err.println("image (w:h): ${image.width}:${image.height}")
    System.err.println("message length: ${message.length}")

    val bytes = message.encodeToByteArray()

    for (i in 0..bytes.lastIndex) {
        encodeByte(i, image, bytes[i])
    }
    val ending = byteArrayOf(0, 0, 3)
    for (i in 0..ending.lastIndex) {
        encodeByte(i + bytes.size, image, ending[i])
    }
}

private fun encodeByte(byteNumber: Int, image: BufferedImage, byte: Byte) {
//    System.err.println("byte #$byteNumber: ${Integer.toBinaryString(byte.toInt())} ($byte)")
    for (curBit in 7 downTo 0) {
        val bitNumber = byteNumber * 8 + 7 - curBit
        val col = (bitNumber) % image.width
        val row = (bitNumber) / image.width
        val mask = 1.shl(curBit)
        val bit = byte.toInt().and(mask).shr(curBit)

        var pixel = image.getRGB(col, row)
//        System.err.println("pixel before: ${Integer.toBinaryString(pixel)} & ${1.and(bit)} mask: ${Integer.toBinaryString(mask)}")
        pixel = pixel.and(0b11111111_11111111_11111111_11111110.toInt()).or(1.and(bit))
//        System.err.println("pixel after : ${Integer.toBinaryString(pixel)}")
        image.setRGB(col, row, pixel)
//        System.err.println("pixel $col $row processed ($bit)")
    }
}

fun exit() {
    println("Bye!")
    exitProcess(0)
}
