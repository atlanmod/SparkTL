package org.atlanmod.tl.model

trait Metamodel[ME, ML, MC, MR] {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    *  MC: ModelClass
    *  MR: ModelReference
    */

    def toModelClass(sc: MC, se: ME): Option[MC]
    def toModelReference(sr: MR, sl: ML): Option[MR]

    def equals(that: Any): Boolean

}
