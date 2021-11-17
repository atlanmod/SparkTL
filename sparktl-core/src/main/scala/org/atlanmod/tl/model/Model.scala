package org.atlanmod.tl.model

trait Model[ME, ML, MC] extends Serializable {

    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    *  MC: ModelClass
    */
    def allModelElements: Iterator[ME]

    def allModelLinks: Iterator[ML]

    def allElementsOfType(cl: MC): Iterator[ME]

}