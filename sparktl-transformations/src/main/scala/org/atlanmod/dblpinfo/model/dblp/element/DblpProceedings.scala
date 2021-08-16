package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.dblpinfo.model.dblp.DblpMetamodel

class DblpProceedings extends DblpRecord(DblpMetamodel.PROCEEDINGS) {

    def this(ee:String, url:String, key: String, mdate: String,
             title: String, year: Int = 0, month: String, isbn: String){
        this()
        super.eSetProperty("ee", ee)
        super.eSetProperty("url", url)
        super.eSetProperty("key", key)
        super.eSetProperty("mdate", mdate)

        super.eSetProperty("title", title)
        super.eSetProperty("year", year)
        super.eSetProperty("month", month)
        super.eSetProperty("isbn", isbn)
    }

    def getTitle: String = super.eGetProperty("title").asInstanceOf[String] //
    def getMonth: String = super.eGetProperty("month").asInstanceOf[String]
    def getYear: Int = super.eGetProperty("year").asInstanceOf[Int] //
    def getIsbn: String = super.eGetProperty("isbn").asInstanceOf[String]

    override def getEe: String = super.eGetProperty("ee").asInstanceOf[String]
    override def getUrl: String = super.eGetProperty("url").asInstanceOf[String]
    override def getKey: String = super.eGetProperty("key").asInstanceOf[String]
    override def getMdate: String = super.eGetProperty("mdate").asInstanceOf[String]

    override def toString: String =
        getTitle + ". (" + getMdate + ") " + getYear + "." + " <" + getUrl +">. <" + getKey + "> " + getIsbn

}