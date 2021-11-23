package org.atlanmod.tl.model

trait Model[ME, ML] extends Serializable {

    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    *  MC: ModelClass
    */
    def allModelElements: List[ME]

    def allModelLinks: List[ML]

}