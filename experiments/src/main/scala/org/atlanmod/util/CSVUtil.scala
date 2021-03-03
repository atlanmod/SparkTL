package org.atlanmod.util

object CSVUtil {

    def writeCSV (filename: String, lines: List[String], header: String = "") : Unit = {
        val file = if (filename.length < 5 || filename.substring(filename.length - 4, filename.length) != ".csv")
            filename + ".csv" else filename
        FileUtil.write_content(file, (if (header != "") header + "\n" else "") + lines.mkString("\n"))
    }

}
