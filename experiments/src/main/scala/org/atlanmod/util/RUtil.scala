package org.atlanmod.util

object RUtil {

    def r_dataframe(name: String, values: List[Any], row_names: String ): String = {
        var res = name + " <- data.frame("
        for (v <- values) res = res + v +  ","
        res += " row.names = " + row_names + ")"
        res
    }

    def r_vector(name: String, values: List[Any]) : String ={
        var res = name + " <- c("
        for (r <- values.indices)
            res = res + values(r) + (if (r == values.size - 1) ")" else ",")
        res
    }

    def r_vectors_step(method: String, size: String, results: List[(Double, List[Double])]) : List[String] = {
        var res: List[String] = List()
        val nstep = results.head._2.size
        var names_of_vectors : List[String] = List()

        // raw results
        val global_times = results.map(p => p._1)
        val name_total = method + "." + size + ".total"
        names_of_vectors = name_total  :: names_of_vectors
        res = RUtil.r_vector(name = name_total, global_times) :: res
        val steps_times = results.map(p => p._2)
        for(i <- 0 until nstep){
            val currstep_time = steps_times.map(l => l(i))
            val name_step =  method + "." + size + ".step" + (i + 1)
            names_of_vectors = name_step  :: names_of_vectors
            res = RUtil.r_vector(name = name_step, currstep_time) :: res
        }

        // condensed results
        for (name <- names_of_vectors){
            res = name + ".mean <- mean(" + name + ")" :: res
            res = name + ".sd <- sd(" + name + ")" :: res
        }

        // all means and sd for method + size
        res = r_vector(name =  method + "." + size + ".means", names_of_vectors.map(s => s + ".mean") ) :: res
        res = r_vector(name =  method + "." + size + ".sds", names_of_vectors.map(s => s + ".sd") ) :: res

        res.reverse
    }

}
