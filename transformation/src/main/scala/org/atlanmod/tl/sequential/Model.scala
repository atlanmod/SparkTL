package org.atlanmod.tl.sequential

trait Model[ME, ML] {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    */
    def allElements: List[ME]
    def allLinks: List[ML]
}
