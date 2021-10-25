package org.atlanmod.dblpinfo.model

import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpInProceedings, DblpJournal}
import org.atlanmod.dblpinfo.model.dblp.link.{ArticleToJournal, AuthorToRecords, JournalToArticles, RecordToAuthors}
import org.atlanmod.dblpinfo.model.dblp.{DblpElement, DblpLink, DblpModel}

object ModelSamples {

    def getReplicatedSimple(n : Int) = {
        val IST = new DblpJournal("Information & Software Technology")
        val TSE = new DblpJournal("Transactions on Software Engineering")
        val CSI = new DblpJournal("Computer Standards & Interface")
        var IST_article : List[DblpArticle] = List()
        var TSE_article : List[DblpArticle] = List()
        var CSI_article : List[DblpArticle] = List()

        var elements : List[DblpElement] = List(IST,TSE,CSI)
        var links : List[DblpLink] = List()

        val mod: Double = (n-3) % 17
        val loop: Int = scala.math.max(1, if (mod >= 8) ((n-3) / 17) + 1 else (n-3) / 17)
        for(i <- 1 to loop) {
            val author1 = new DblpAuthor("author1_"+i)
            val author2 = new DblpAuthor("author2_"+i)
            val author3 = new DblpAuthor("author3_"+i)
            val author4 = new DblpAuthor("author4_"+i)
            val author5 = new DblpAuthor("author5_"+i)

            val inproc1 = new DblpInProceedings("", "", "inproc1_"+i, "", "ICMT 2005", 2005, 0, 0, "")
            val inproc2 = new DblpInProceedings("", "", "inproc2_"+i, "", "ICMT 2013", 2013, 0, 0, "")
            val inproc3 = new DblpInProceedings("", "", "inproc3_"+i, "", "ICMT 2000", 2000, 0, 0, "")
            val inproc4 = new DblpInProceedings("", "", "inproc4_"+i, "", "MODELS", 2013, 0, 0, "")
            val inproc2_1 = new DblpInProceedings("", "", "inproc2_1_"+i, "", "ICSE", 2014, 0, 0, "")
            val inproc5 = new DblpInProceedings("", "", "inproc5_"+i, "", "TOOLS", 2006, 0, 0, "")

            val art1 = new DblpArticle("", "", "art1_"+i, "", "art1", 0, 0, 0, "", "", 2006)
            val art2 = new DblpArticle("", "", "art2_"+i, "", "art2", 0, 0, 0, "", "", 2014)
            val art3 = new DblpArticle("", "", "art3_"+i, "", "", 0, 0, 0, "", "", 2010)
            val art4 = new DblpArticle("", "", "art4_"+i, "", "", 0, 0, 0, "", "", 2009)
            val art5 = new DblpArticle("", "", "art5_"+i, "", "", 0, 0, 0, "", "", 2000)
            val art6 = new DblpArticle("", "", "art6_"+i, "", "", 0, 0, 0, "", "", 2013)
            val authors = List(author1, author2, author3, author4, author5)
            val ips = List(inproc1, inproc2, inproc2_1, inproc3, inproc4, inproc5)
            val articles = List(art1, art2, art3, art4, art5, art6)
            elements = authors ++ ips ++ articles ++ elements

            val author1_records = new AuthorToRecords(author1, List(inproc1, inproc2, inproc2_1, art2, art4))
            val author2_records = new AuthorToRecords(author2, List(inproc2, inproc5, art2, art3))
            val author3_records = new AuthorToRecords(author3, List(inproc3, inproc4, inproc2_1, art4))
            val author4_records = new AuthorToRecords(author4, List(art1, art3))
            val author5_records = new AuthorToRecords(author5, List(art5, art6))

            val inproc1_authors = new RecordToAuthors(inproc1, List(author1))
            val inproc2_authors = new RecordToAuthors(inproc2, List(author1, author2))
            val inproc3_authors = new RecordToAuthors(inproc3, List(author3))
            val inproc4_authors = new RecordToAuthors(inproc4, List(author3))
            val inproc2_1_authors = new RecordToAuthors(inproc2_1, List(author1, author3))
            val inproc5_authors = new RecordToAuthors(inproc5, List(author2))
            val art1_authors = new RecordToAuthors(art1, List(author4))
            val art2_authors = new RecordToAuthors(art2, List(author1, author2))
            val art3_authors = new RecordToAuthors(art3, List(author4, author2))
            val art4_authors = new RecordToAuthors(art4, List(author3, author1))
            val art5_authors = new RecordToAuthors(art5, List(author5))
            val art6_authors = new RecordToAuthors(art6, List(author5))

            val art1_journal = new ArticleToJournal(art1, IST)
            val art2_journal = new ArticleToJournal(art2, TSE)
            val art3_journal = new ArticleToJournal(art3, CSI)
            val art4_journal = new ArticleToJournal(art4, IST)
            val art5_journal = new ArticleToJournal(art5, IST)
            val art6_journal = new ArticleToJournal(art6, CSI)

            val article_authors = List(art1_authors, art2_authors, art3_authors, art4_authors, art5_authors, art6_authors)
            val inproc_authors = List(inproc1_authors, inproc2_authors, inproc2_1_authors, inproc3_authors, inproc4_authors, inproc5_authors)
            val article_journal = List(art1_journal, art2_journal, art3_journal, art4_journal, art5_journal, art6_journal)
            val author_records = List(author1_records, author2_records, author3_records, author4_records, author5_records)

            links = links ++ author_records ++ inproc_authors ++ article_authors ++ article_journal

            IST_article = List(art1, art4, art5) ++ IST_article
            TSE_article = List(art2) ++ TSE_article
            CSI_article = List(art3, art6) ++ CSI_article
        }

        val j1_article = new JournalToArticles(IST, IST_article)
        val j2_article = new JournalToArticles(TSE, TSE_article)
        val j3_article = new JournalToArticles(CSI, CSI_article)
        val journal_articles = List(j1_article, j2_article, j3_article)
        links = links ++ journal_articles

        new DblpModel(elements, links)

    }
}
