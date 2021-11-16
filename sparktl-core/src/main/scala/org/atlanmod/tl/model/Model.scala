package org.atlanmod.tl.model

trait Model[ME, ML]  extends Serializable {

    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    */
    def allModelElements: Iterator[ME]

    def allModelLinks: Iterator[ML]
}