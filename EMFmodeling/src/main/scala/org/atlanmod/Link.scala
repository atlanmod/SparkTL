package org.atlanmod

trait Link[ST, RT, TT] extends Serializable {
    /*
    * ST: Source Type
    * RT: Reference Type
    * TT: Target Type
    * */
    def getSource(): ST
    def getReference(): RT
    def getTarget(): TT
}
