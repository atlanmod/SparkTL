package org.atlanmod.tl.model

trait Model[ME, ML] {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    */
    def allModelElements: List[ME]

    def allModelLinks: List[ML]
}
