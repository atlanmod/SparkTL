package org.atlanmod.tl.sequential

trait Metamodel[ME, ML, MC, MR] {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    *  MC: ModelClass
    *  ME: ModelReference
    */

    def equals(that: Any): Boolean
}
