package org.atlanmod.dblpinfo.tranformation.dynamic

import org.atlanmod.dblpinfo.model.authorinfo.element.AuthorInfoConference
import org.atlanmod.dblpinfo.model.dblp.{DblpMetamodel, DblpModel}
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpInProceedings}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}

import scala.collection.mutable

object InactiveICMTButActiveAuthors {

    def conferences: mutable.HashMap[String, AuthorInfoConference] = new mutable.HashMap[String, AuthorInfoConference]()

    def helper_booktitle(model: DblpModel, ip: DblpInProceedings) : String = ip.getBookTitle

    def helper_year(model: DblpModel, ip: DblpInProceedings) : Int = ip.getYear

    def helper_active(model: DblpModel, author: DblpAuthor) : Boolean =
        DblpMetamodel.getRecordsOfAuthor(model, author)
          .filter(r => r.isInstanceOf[DblpInProceedings])
          .map(r => r.asInstanceOf[DblpInProceedings])
          .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") > 0 && helper_year(model, ip) > 2008)

    def helper_nowPublishingIn(model: DblpModel, author: DblpAuthor) : Seq[String] =
        DblpMetamodel.getRecordsOfAuthor(model, author)
          .filter(r => r.isInstanceOf[DblpInProceedings])
          .map(r => r.asInstanceOf[DblpInProceedings])
          .filter(ip => helper_booktitle(model, ip).indexOf("ICMT") > 0 && helper_year(model, ip) > 2008)
          .map(ip => helper_booktitle(model, ip))

    def find: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
            new RuleImpl(
                name = "icmt",
                types = List(),
                to = List()
            ),
            new RuleImpl(
                name = "conf",
                types = List(),
                to = List()
            )
        ))
}

/* TODO implement the following rules

rule icmt {
    from
        a : MM!Author (a.records->select(r | r.oclIsTypeOf(MM!InProceedings))->exists(ip | ip.booktitle().indexOf('ICMT')>=0) and not a.active())
    to
        out : MM1!Author (
          name <- a.name,
          active <- a.active()
        )
    do {
        for (cName in a.nowPublishingIn()) {
            if (thisModule.conferences.get(cName).oclIsUndefined()){
                thisModule.conf(cName);
            }
            out.publishesInC <- out.publishesInC->append(thisModule.conferences.get(cName));
        }
    }
}

rule conf(cName : String) {
    to
        c : MM1!Conference (
          name <- cName
        )
    do {
        thisModule.conferences.put(cName, c);
    }
}
*/