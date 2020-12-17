package org.atlanmod

import org.eclipse.emf.ecore.{EObject, EReference}

class ELink(src: EObject, ref: EReference, trg: Object) extends Link[EObject, EReference, Object] {

    def getSource: EObject = src
    def getReference: EReference = ref
    def getTarget: Object = trg

    override def toString: String = "(" + src.toString + ", " + ref.toString + ", " + trg.toString + ")"

    def toString(ntab : Int = 0): String = {
        ntab match {
            case 0 => "(" + src.toString + ", " + ref.toString + ", " + trg.toString + ")"
            case n if n != 0 =>
                "\t" * n + src.toString + "\n" +
                  "\t" * n + ref.toString + "\n" +
                  "\t" * n + trg.toString + "\n"
        }
    }
}
