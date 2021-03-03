package org.atlanmod.tl.model

trait Metamodel[ME, ML, MC, MR] extends Serializable {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    *  MC: ModelClass
    *  MR: ModelReference
    */

//    def denoteClass(sc: MC): MC

    def toModelClass(sc: MC, se: ME): Option[ME]
    def toModelReference(sr: MR, sl: ML): Option[ML]

    def equals(that: Any): Boolean

    def hasType(t: MC, e: ME) : Boolean ={
        toModelClass(t, e) match {
            case Some(e) => true
            case _ => false
        }
    }
}