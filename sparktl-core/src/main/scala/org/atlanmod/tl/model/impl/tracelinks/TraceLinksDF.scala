package org.atlanmod.tl.model.impl.tracelinks

import org.apache.spark.sql.types.{DataType, StructType}
import org.apache.spark.sql.{DataFrame, Encoder}
import org.atlanmod.tl.model.{TraceLink, TraceLinks}

import scala.reflect.ClassTag

case class TraceLinkRow[SME <: DataType, TME <: DataType](spin: (List[SME], Int, String), te: TME) extends Encoder[TraceLink[SME, TME]] {

    override def schema: StructType = ???
    //            StructType(
    //                StructField("spin",
    //                    StructType(
    //                        StructField("sme", ArrayType.apply(SME), false),
    //                        StructField("ite", IntegerType, false),
    //                        StructField("name", StringType, false)),
    //                    false
    //                ),
    //                StructField("te",TME,false)
    //            )

    override def clsTag: ClassTag[TraceLink[SME, TME]] = ??? // TraceLinkRow
}

class TraceLinksDF[SME, TME] (df: DataFrame) extends TraceLinks[SME, TME] {

    override def getSourcePatterns: Iterable[List[SME]] = ???

    override def getTargetElements: Iterable[TME] = ???

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] = ???

    override def filter(p: TraceLink[SME, TME] => Boolean): TraceLinks[SME, TME] = ???

    override def asList(): List[TraceLink[SME, TME]] = ???

    override def getIterableSeq(): Seq[Any] = ???
}
