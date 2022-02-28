package org.atlanmod

import org.atlanmod.tl.engine.Parameters
import org.atlanmod.tl.model.Model
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicModel}

class ExecutionResult[SME <: DynamicElement, SML <: DynamicLink, TME <: DynamicElement, TML <: DynamicLink]
(case_ : String, input: DynamicModel, output: DynamicModel, time: TimeResult, config: Parameters.Config){

    def this(case_ : String, input: Model[SME, SML], output: Model[TME, TML],
             time: TimeResult, config: Parameters.Config) = {
        this(case_, new DynamicModel(input.allModelElements, input.allModelLinks),
            new DynamicModel(output.allModelElements, output.allModelLinks), time, config)
    }

    private def YES_NO (b: Boolean): String = if (b) "YES" else "NO"

    val params: List[(String, String)] = List (
        ("case", case_ ), ("input_element", input.numberOfElements.toString), ("input_links", input.numberOfLinks.toString),
        ("output_element", output.numberOfElements.toString), ("output_links", output.numberOfLinks.toString),
        ("executors", config._nexecutors.toString), ("cores", config._ncores.toString), ("partitions", config._npartitions.toString), ("storage", config._storageLevel_string), // Spark parameters
        ("links", config._link_type), ("tls", config._tls), // parameters for used structures
        ("tuples", config._tuples), ("apply", config._distinctApply), // parameters for "distinct"
        ("collect", config._collect), ("broadcast", config._bcast), // parameters for communication
        ("memoization", YES_NO(config._memoization)), ("tracerules", YES_NO(config._tracerule)), // Boolean values
        ("sleepGuard", config._sleepGuard + "ms"), ("sleepInstantiate", config._sleepInstantiate + "ms"), ("sleepApply", config._sleepApply + "ms"), // Model computation time parameters
    )

    val results: List[(String, String)] =
        List(
            ("total_time", time.get_total_time() + "ms"), ("instantiate_time", time.get_instantiate_time() + "ms"),
            ("extract_time", time.get_extract_time() + "ms"), ("broadcast_time", time.get_broadcast_time() + "ms"),
            ("apply_time", time.get_apply_time() + "ms")
        )

    val csv_header : String = (params ++ results).map(e => e._1).mkString(",")
    val csv_line : String = (params ++ results).map(e => e._2).mkString(",")
}