package org.atlanmod.util

object CSVUtil {

    def writeCSV (filename: String, header: String, lines: List[String]) : Unit = {
        val file = if (filename.size < 5 || filename.substring(filename.size - 4, filename.size) != ".csv")
            filename + ".csv" else filename
        FileUtil.write_content(file, header + "\n" + lines.mkString("\n"))
    }

}
