package org.atlanmod.dblpinfo.tranformation.dynamic

import org.atlanmod.dblpinfo.model.authorinfo.element.AuthorInfoConference
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpInProceedings}
import org.atlanmod.dblpinfo.model.dblp.{DblpMetamodel, DblpModel}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{RuleImpl, TransformationImpl}

import scala.collection.mutable

object JournalICTActiveAuthors {

    def journals: mutable.HashMap[String, AuthorInfoConference] = new mutable.HashMap[String, AuthorInfoConference]()

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
                name = "jour",
                types = List(),
                to = List()
            )
        ))
}

/* TODO implement the following rules

rule icmt {
	from
		a : MM!Author (a.records->select(r | r.oclIsTypeOf(MM!Article))->exists(a | a.journal().indexOf('Information & Software Technology')>=0) and a.active())
	to
		out : MM1!Author (
			name <- a.name,
			active <- a.active()
		)
	do {
		for (jName in a.nowPublishingIn()) {
			if (jName<>''){
				if (thisModule.journals.get(jName).oclIsUndefined()){
					thisModule.jour(jName);
				}
				out.publishesInJ <- out.publishesInJ->append(thisModule.journals.get(jName));
			}
		}
	}
}

rule jour(jName : String) {
	to
		c : MM1!Journal (
			name <- jName
		)
	do {
		thisModule.journals.put(jName, c);
	}
}




*/