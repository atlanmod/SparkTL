package org.atlanmod.util

import java.util.Calendar

object TimeUtil {

    def strLocalTime: String = {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH) + 1
        val day = now.get(Calendar.DAY_OF_MONTH)
        val hour = now.get(Calendar.HOUR)
        val minute = now.get(Calendar.MINUTE)
        val second = now.get(Calendar.SECOND)
        year + ":" + month + ":" + day + "_" + hour + ":" + minute + ":" + second
    }

}
