package org.atlanmod

object MakeDatasetClass {

    val NPATTERN_DEFAULT = 1
    var npattern = NPATTERN_DEFAULT
    val NAME_DEFAULT = "model_"+ npattern
    var name = NAME_DEFAULT

    def parse_args(args: List[String]): Unit = {
        args match {
            case "--name" :: n :: args2 => {
                name = n
                parse_args(args2)
            }
            case "--size" :: n :: args2 => {
                npattern = n.toInt
                parse_args(args2)
            }
            case _ :: args2 => parse_args(args2)
        }
    }

    def main(args: Array[String]): Unit = {
        parse_args(args.toList)
    }
}
