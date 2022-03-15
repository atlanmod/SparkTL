package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.IdGenerator
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class DblpArticle extends DblpRecord(DblpMetamodelNaive.ARTICLE) {

    def this(id: Long, ee:String, url:String, key: String, mdate: String,
             title: String, fromPage: Int, toPage: Int, number: Int,
             volume: String, month: String, year: Int){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("ee", ee)
        super.eSetProperty("url", url)
        super.eSetProperty("key", key)
        super.eSetProperty("mdate", mdate)
        super.eSetProperty("title", title)
        super.eSetProperty("fromPage", fromPage)
        super.eSetProperty("toPage", toPage)
        super.eSetProperty("number", number)
        super.eSetProperty("volume", volume)
        super.eSetProperty("month", month)
        super.eSetProperty("year", year)
    }

    def this(ee:String, url:String, key: String, mdate: String,
             title: String, fromPage: Int, toPage: Int, number: Int,
             volume: String, month: String, year: Int){
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("ee", ee)
        super.eSetProperty("url", url)
        super.eSetProperty("key", key)
        super.eSetProperty("mdate", mdate)
        super.eSetProperty("title", title)
        super.eSetProperty("fromPage", fromPage)
        super.eSetProperty("toPage", toPage)
        super.eSetProperty("number", number)
        super.eSetProperty("volume", volume)
        super.eSetProperty("month", month)
        super.eSetProperty("year", year)
    }

    def getTitle: String = super.eGetProperty("title").asInstanceOf[String] //
    def getFromPage: Int = super.eGetProperty("fromPage").asInstanceOf[Int] //
    def getToPage: Int = super.eGetProperty("ToPage").asInstanceOf[Int] //
    def getNumber: Int = super.eGetProperty("number").asInstanceOf[Int] //
    def getVolume: String = super.eGetProperty("volume").asInstanceOf[String] //
    def getMonth: String = super.eGetProperty("month").asInstanceOf[String]
    def getYear: Int = super.eGetProperty("year").asInstanceOf[Int] //

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
    override def getEe: String = super.eGetProperty("ee").asInstanceOf[String]
    override def getUrl: String = super.eGetProperty("url").asInstanceOf[String]
    override def getKey: String = super.eGetProperty("key").asInstanceOf[String]
    override def getMdate: String = super.eGetProperty("mdate").asInstanceOf[String]

    override def toString: String =
        getTitle + ". " + getVolume + " (" + getMdate + ", " + getNumber + ") " + getYear + ", pp." + getFromPage +
          "--" + getToPage + " ." + " <" + getUrl +">. <" + getKey + ">"

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DblpArticle =>
                super.equals(o) & obj.getTitle.equals(getTitle) &  obj.getFromPage.equals(getFromPage) &  obj.getToPage.equals(getToPage) &
                  obj.getNumber.equals(getNumber) &  obj.getVolume.equals(getVolume) &  obj.getMonth.equals(getMonth) &
                  obj.getYear.equals(getYear)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)

}
