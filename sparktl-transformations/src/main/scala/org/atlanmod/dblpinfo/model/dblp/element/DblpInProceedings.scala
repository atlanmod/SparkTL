package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.IdGenerator
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class DblpInProceedings extends DblpRecord(DblpMetamodelNaive.INPROCEEDINGS) {

    def this(id:Long, ee:String, url:String, key: String, mdate: String,
             bookTitle: String, year: Int, fromPage: Int, toPage: Int, month: String){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("ee", ee)
        super.eSetProperty("url", url)
        super.eSetProperty("key", key)
        super.eSetProperty("mdate", mdate)
        super.eSetProperty("bookTitle", bookTitle)
        super.eSetProperty("year", year)
        super.eSetProperty("fromPage", fromPage)
        super.eSetProperty("toPage", toPage)
        super.eSetProperty("month", month)
    }

    def this(ee:String, url:String, key: String, mdate: String,
             bookTitle: String, year: Int, fromPage: Int, toPage: Int, month: String){
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("ee", ee)
        super.eSetProperty("url", url)
        super.eSetProperty("key", key)
        super.eSetProperty("mdate", mdate)
        super.eSetProperty("bookTitle", bookTitle)
        super.eSetProperty("year", year)
        super.eSetProperty("fromPage", fromPage)
        super.eSetProperty("toPage", toPage)
        super.eSetProperty("month", month)
    }

    def getBookTitle: String = super.eGetProperty("bookTitle").asInstanceOf[String] //
    def getFromPage: Int = super.eGetProperty("fromPage").asInstanceOf[Int] //
    def getToPage: Int = super.eGetProperty("ToPage").asInstanceOf[Int] //
    def getMonth: String = super.eGetProperty("month").asInstanceOf[String]
    def getYear: Int = super.eGetProperty("year").asInstanceOf[Int] //

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
    override def getEe: String = super.eGetProperty("ee").asInstanceOf[String]
    override def getUrl: String = super.eGetProperty("url").asInstanceOf[String]
    override def getKey: String = super.eGetProperty("key").asInstanceOf[String]
    override def getMdate: String = super.eGetProperty("mdate").asInstanceOf[String]

    override def toString: String =
        getBookTitle + ". (" + getMdate + ") " + getYear + ", pp." + getFromPage + "--" + getToPage + " ." +
          " <" + getUrl +">. <" + getKey + ">"

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DblpInProceedings =>
                super.equals(o) & obj.getBookTitle.equals(getBookTitle) & obj.getYear.equals(getYear) &
                  obj.getFromPage.equals(getFromPage) &  obj.getToPage.equals(getToPage) & obj.getMonth.equals(getMonth)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)
}