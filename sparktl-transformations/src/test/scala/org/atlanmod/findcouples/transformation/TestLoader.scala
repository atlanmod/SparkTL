package org.atlanmod.findcouples

import org.atlanmod.findcouples.model.movie.MovieLoader
import org.scalatest.funsuite.AnyFunSuite

class TestLoader extends AnyFunSuite {

    val path = "/home/jolan/Scala/SparkTL/SparkTL/sparktl-transformations/src/main/resources/imdb_models/"

    test("test loader"){
        val filepath = path+"imdb-0.2.xmi"
        val ecore = path+"movies.ecore"
        MovieLoader.load(filepath, ecore)
    }

}
