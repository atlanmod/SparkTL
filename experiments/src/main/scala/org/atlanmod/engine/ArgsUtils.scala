package org.atlanmod.engine

object ArgsUtils {

    private def getAll(args: List[String], arg: String) : List[String] =
        args match {
            case key :: value :: tail if key.equals(arg) => value :: getAll(tail, arg)
            case key :: _ :: tail if !key.equals(arg) => getAll(tail, arg)
            case _ :: List() => List()
            case List() => List()
        }

    def getAll(args: Array[String], str: String) : List[String] =
        getAll(args.toList, str)

    private def getOrElse[A](args: List[String], arg: String, default: A, convert: String => A): A =
        args match {
            case key :: value :: _ if key.equals(arg) => convert(value)
            case key :: _ :: tail if !key.equals(arg) => getOrElse(tail, arg, default, convert)
            case _ :: List() => default
            case List() => default
        }

    def getOrElse[A](args: Array[String], arg: String, default: A, convert: String => A): A =
        getOrElse(args.toList, arg, default, convert)

    private def get[A](args: List[String], arg: String, convert: String => A): Option[A] =
        args match {
            case key :: value :: _ if key.equals(arg) => Some(convert(value))
            case key :: _ :: tail if !key.equals(arg) => get(tail, arg, convert)
            case _ :: List() => None
            case List() => None
        }

    def get[A](args: Array[String], arg: String, convert: String => A): Option[A] =
        get(args.toList, arg, convert)

    def getOrElse[A](args: Array[String], arg: List[String], default: A, convert: String => A): A = {
        arg match {
            case h :: t =>
                get(args, h, convert) match {
                    case Some(a) => a
                    case _ => getOrElse(args, t, default, convert)
                }
            case _ => default
        }
    }

    private def has(args: List[String], arg: String): Boolean =
        args match {
            case key :: _ if key.equals(arg) => true
            case key :: tail if !key.equals(arg) => has(tail, arg)
            case List() => false
        }

    def has(args: Array[String], arg: String): Boolean =
        has(args.toList, arg)

}
