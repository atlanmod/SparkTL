package org.atlanmod.dblpinfo.transformation.dynamic

import org.atlanmod.dblpinfo.model.authorinfo.{AuthorInfoElement, AuthorInfoLink, AuthorInfoModel}
import org.atlanmod.dblpinfo.model.authorinfo.element.AuthorInfoAuthor
import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpInProceedings, DblpJournal}
import org.atlanmod.dblpinfo.model.dblp.link.{ArticleToJournal, AuthorToRecords, JournalToArticles, RecordToAuthors}
import org.atlanmod.dblpinfo.model.dblp.{DblpElement, DblpLink, DblpMetamodel, DblpModel}
import org.atlanmod.dblpinfo.tranformation.dynamic.ICMTAuthors
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.scalatest.funsuite.AnyFunSuite

class TestDblpInfo extends AnyFunSuite {

    def metamodel = DblpMetamodel

    def makeAuthorInfoModel: (List[DynamicElement], List[DynamicLink]) => AuthorInfoModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new AuthorInfoModel(e.asInstanceOf[List[AuthorInfoElement]], l.asInstanceOf[List[AuthorInfoLink]])


    val loli = new DblpAuthor("Loli")
    val antonio = new DblpAuthor("Antonio")
    val manuel = new DblpAuthor("Manuel")
    val javi = new DblpAuthor("Javi")
    val martin = new DblpAuthor("Martin")

    val inproc1 = new DblpInProceedings("", "", "inproc1", "", "ICMT 2005", 2005, 0, 0, "" )
    val inproc2 = new DblpInProceedings("", "", "inproc2", "", "ICMT 2013", 2013, 0, 0, "" )
    val inproc3 = new DblpInProceedings("", "", "inproc3", "", "ICMT 2000", 2000, 0, 0, "" )
    val inproc4 = new DblpInProceedings("", "", "inproc4", "", "MODELS", 2013, 0, 0, "" )
    val inproc2_1 = new DblpInProceedings("", "", "inproc2", "", "ICSE", 2014, 0, 0, "" )
    val inproc5 = new DblpInProceedings("", "", "inproc5", "", "TOOLS", 2006, 0, 0, "" )

    val art1 = new DblpArticle("", "", "art1", "", "art1", 0, 0, 0, "", "", 2006)
    val art2 = new DblpArticle("", "", "art2", "", "art2", 0, 0, 0, "", "", 2014)
    val art3 = new DblpArticle("", "", "art3", "", "", 0, 0, 0, "", "", 2010)
    val art4 = new DblpArticle("", "", "art4", "", "", 0, 0, 0, "", "", 2009)
    val art5 = new DblpArticle("", "", "art5", "", "", 0, 0, 0, "", "", 2000)
    val art6 = new DblpArticle("", "", "art6", "", "", 0, 0, 0, "", "", 2013)

    val j1 = new DblpJournal("Information & Software Technology")
    val j2 = new DblpJournal("Transactions on Software Engineering")
    val j3 = new DblpJournal("Computer Standards & Interface")

    def model: DblpModel = {

        val authors = List(loli, antonio, manuel, javi, martin)
        val ips = List(inproc1, inproc2, inproc2_1, inproc3, inproc4, inproc5)
        val articles = List(art1, art2, art3, art4, art5, art6)
        val journals = List(j1, j2, j3)

        val loli_records = new AuthorToRecords(loli, List(inproc1, inproc2, inproc2_1, art2, art4))
        val antonio_records = new AuthorToRecords(antonio, List(inproc2, inproc5, art2, art3))
        val manuel_records = new AuthorToRecords(manuel, List(inproc3, inproc4, inproc2_1, art4))
        val javi_records = new AuthorToRecords(javi, List(art1, art3))
        val martin_records = new AuthorToRecords(martin, List(art5, art6))

        val author_records = List(loli_records, antonio_records, manuel_records, javi_records, martin_records)

        val inproc1_authors = new RecordToAuthors(inproc1, List(loli))
        val inproc2_authors = new RecordToAuthors(inproc2, List(loli, antonio))
        val inproc3_authors = new RecordToAuthors(inproc3, List(manuel))
        val inproc4_authors = new RecordToAuthors(inproc4, List(manuel))
        val inproc2_1_authors = new RecordToAuthors(inproc2_1, List(loli, manuel))
        val inproc5_authors = new RecordToAuthors(inproc5, List(antonio))
        val inproc_authors = List(inproc1_authors, inproc2_authors, inproc2_1_authors, inproc3_authors, inproc4_authors, inproc5_authors)

        val art1_authors = new RecordToAuthors(art1, List(javi))
        val art2_authors = new RecordToAuthors(art2, List(loli, antonio))
        val art3_authors = new RecordToAuthors(art3, List(javi, antonio))
        val art4_authors = new RecordToAuthors(art4, List(manuel, loli))
        val art5_authors = new RecordToAuthors(art5, List(martin))
        val art6_authors = new RecordToAuthors(art6, List(martin))
        val article_authors = List(art1_authors, art2_authors, art3_authors, art4_authors, art5_authors, art6_authors)

        val art1_journal = new ArticleToJournal(art1, j1)
        val art2_journal = new ArticleToJournal(art2, j2)
        val art3_journal = new ArticleToJournal(art3, j3)
        val art4_journal = new ArticleToJournal(art4, j1)
        val art5_journal = new ArticleToJournal(art5, j1)
        val art6_journal = new ArticleToJournal(art6, j3)
        val article_journal = List(art1_journal, art2_journal, art3_journal, art4_journal, art5_journal, art6_journal)

        val j1_article = new JournalToArticles(j1, List(art1, art4, art5))
        val j2_article = new JournalToArticles(j2, List(art2))
        val j3_article = new JournalToArticles(j3, List(art3, art6))
        val journal_articles = List(j1_article, j2_article, j3_article)

        val elements : List[DblpElement] = authors ++ ips ++ articles ++ journals
        val links : List[DblpLink] = author_records ++ inproc_authors ++ article_authors ++ article_journal ++ journal_articles
        new DblpModel(elements, links)
    }

    def run_(tr: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink]): AuthorInfoModel =
        org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(ICMTAuthors.find, model,
            DblpMetamodel.metamodel, makeModel = makeAuthorInfoModel).asInstanceOf[AuthorInfoModel]


    test("test getRecordsOfAuthor loli"){
        val exp = Some(List(inproc1, inproc2, inproc2_1, art2, art4))
        val res = metamodel.getRecordsOfAuthor(model, loli)
        assert(exp.equals(res))
    }

    test("test getRecordsOfAuthor manuel"){
        val exp = Some(List(inproc3, inproc4, inproc2_1, art4))
        val res = metamodel.getRecordsOfAuthor(model, manuel)
        assert(exp.equals(res))
    }


    test("test getRecordsOfAuthor error"){
        val jolan = new DblpAuthor("Jolan")
        val exp = None
        val res = metamodel.getRecordsOfAuthor(model, jolan)
        assert(res.equals(exp))
    }

    test("test getAuthorOfRecords inProceeding"){
        val exp = Some(List(loli))
        val res = metamodel.getAuthorsOfRecord(model, inproc1)
        assert(res.equals(exp))
    }


    test("test getAuthorOfRecords Articles"){
        val exp = Some(List(javi))
        val res = metamodel.getAuthorsOfRecord(model, art1)
        assert(res.equals(exp))
    }


    test("test getJournalOfArticle"){
        val exp = Some(j1)
        val res = metamodel.getJournalOfArticle(model, art1)
    }

    test("test helper_numOfPapers"){
        assert(ICMTAuthors.helper_numOfPapers(model, loli).equals(2))
        assert(ICMTAuthors.helper_numOfPapers(model, antonio).equals(1))
        assert(ICMTAuthors.helper_numOfPapers(model, manuel).equals(1))
        assert(ICMTAuthors.helper_numOfPapers(model, javi).equals(0))
        assert(ICMTAuthors.helper_numOfPapers(model, martin).equals(0))
    }

    test("test ICMT Authors"){
        val exp = List(loli,manuel,antonio).map(a => new AuthorInfoAuthor(a.getName, ICMTAuthors.helper_numOfPapers(model, a)))
        val res = run_(ICMTAuthors.find).allModelElements
        for (e <- exp) assert(res.contains(e))
    }

    test("test ICMT Active Authors"){
        val exp = List(loli,manuel).map(a => new AuthorInfoAuthor(a.getName, active=true))
        val res = run_(ICMTAuthors.find).allModelElements
        for (e <- exp) assert(res.contains(e))
    }

}
