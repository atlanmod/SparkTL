package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.dblpinfo.model.dblp.DblpMetamodel

class DblpBook extends DblpRecord(DblpMetamodel.BOOK) {

    def this(ee: String, url: String, key: String, mdate: String,
             title: String, month: String, volume: Int = 0, series: String, edition: Int = 0, isbn: String) {
        this()
        super.eSetProperty("ee", ee)
        super.eSetProperty("url", url)
        super.eSetProperty("key", key)
        super.eSetProperty("mdate", mdate)
        super.eSetProperty("title", title)
        super.eSetProperty("volume", volume)
        super.eSetProperty("month", month)
        super.eSetProperty("series", series)
        super.eSetProperty("edition", edition)
        super.eSetProperty("isbn", isbn)
    }

    def getTitle: String = super.eGetProperty("title").asInstanceOf[String] //
    def getMonth: String = super.eGetProperty("month").asInstanceOf[String]

    def getVolume: Int = super.eGetProperty("volume").asInstanceOf[Int] //
    def getSeries: String = super.eGetProperty("series").asInstanceOf[String] //
    def getEdition: Int = super.eGetProperty("edition").asInstanceOf[Int] //
    def getIsbn: String = super.eGetProperty("isbn").asInstanceOf[String] //

    override def getEe: String = super.eGetProperty("ee").asInstanceOf[String]

    override def getUrl: String = super.eGetProperty("url").asInstanceOf[String]

    override def getKey: String = super.eGetProperty("key").asInstanceOf[String]

    override def getMdate: String = super.eGetProperty("mdate").asInstanceOf[String]

    override def toString: String =
        getTitle + ". " + getEdition + "(" + getSeries + ") vol." + getVolume + " (" + getMdate + ") " + " ." +
          " <" + getUrl + ">. <" + getKey + ">. " + getIsbn

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DblpBook =>
                super.equals(o) & obj.getTitle.equals(getTitle) & obj.getMonth.equals(getMonth) & obj.getVolume.equals(getVolume) &
                  obj.getSeries.equals(getSeries) & obj.getEdition.equals(getEdition) &
                  obj.getIsbn.equals(getIsbn)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)

}