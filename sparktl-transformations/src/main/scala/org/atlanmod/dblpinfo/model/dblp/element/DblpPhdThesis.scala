package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.dblpinfo.model.dblp.DblpMetamodel

class DblpPhdThesis extends DblpRecord(DblpMetamodel.PHDTHESIS) {

    def this(ee:String, url:String, key: String, mdate: String,
             name: String, year: Int, month: String) {
        this()
        super.eSetProperty("ee", ee)
        super.eSetProperty("url", url)
        super.eSetProperty("key", key)
        super.eSetProperty("mdate", mdate)
        super.eSetProperty("name", name)
        super.eSetProperty("year", year)
        super.eSetProperty("month", month)
    }

    override def getEe: String = super.eGetProperty("ee").asInstanceOf[String]
    override def getUrl: String = super.eGetProperty("url").asInstanceOf[String]
    override def getKey: String = super.eGetProperty("key").asInstanceOf[String]
    override def getMdate: String = super.eGetProperty("mdate").asInstanceOf[String]

    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def getYear: Int = super.eGetProperty("year").asInstanceOf[Int]
    def getMonth: String = super.eGetProperty("month").asInstanceOf[String]

    override def toString: String = getName + "(" + getMonth + "/" + getYear + ")"

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DblpPhdThesis =>
                super.equals(o) & obj.getName.equals(getName) & obj.getYear.equals(getYear) & obj.getMonth.equals(getMonth)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)

}