package org.atlanmod.util

import java.io.{File, PrintWriter}

object FileUtil {

    def write_content(filename: String, content: String): Unit = {
        val pw = new PrintWriter(new File(filename))
        pw.write(content)
        pw.close()
    }

    def create_if_not_exits(dirname: String) : Unit = {
        val dir : File = new File(dirname)
        if (!dir.exists) dir.mkdirs()
    }
}
