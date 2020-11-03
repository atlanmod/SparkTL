package org.atlanmod.tl.sequential.spec

trait Model[ME, ML] {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    */
    def allModelElements: List[ME]

    def allModelLinks: List[ML]
}
