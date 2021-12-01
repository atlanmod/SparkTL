package org.atlanmod

import org.atlanmod.tl.model.impl.dynamic.DynamicModel

class ExecutionResult[ME, ML](tls_method: String, links_method: String, case_ : String, input: DynamicModel,
                              n_executor: Int, n_core_executor: Int, n_partition: Int, storageLevel: String,
                              timeResult: TimeResult, modelResult: ModelResult[ME, ML],
                              sleeping_guard: Long = 0L, sleeping_instantiate: Long = 0L, sleeping_apply: Long = 0L) {

    def csv_header: String =
        "tls_solution,links_solution,case,input_elements,input_links,executor,core,partition,storageLevel," +
        "sleeping_guard,sleeping_instantiate,sleeping_apply," +
        "total_time,time_tuples,time_instantiate,time_extract,time_broadcast,time_apply," +
        "output_element,output_link"

    def csv: String =
        List(tls_method, links_method, case_, input.numberOfElements, input.numberOfLinks, n_executor, n_core_executor,
            n_partition, storageLevel, sleeping_guard, sleeping_instantiate, sleeping_apply,
            timeResult.get_total_time(), timeResult.get_tuples_time(), timeResult.get_instantiate_time(),
            timeResult.get_extract_time(), timeResult.get_broadcast_time(), timeResult.get_apply_time(),
            modelResult.elements_size(), modelResult.links_size()
        ).mkString(",")

}