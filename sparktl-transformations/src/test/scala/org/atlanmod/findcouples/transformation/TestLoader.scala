package org.atlanmod.findcouples

import org.atlanmod.findcouples.model.movie.MovieLoader
import org.scalatest.funsuite.AnyFunSuite

class TestLoader extends AnyFunSuite {

    val path = "/home/jolan/Scala/SparkTL/SparkTL/sparktl-transformations/src/main/resources/imdb_models/"

    test("test loader 100k"){
        val t1 =  System.currentTimeMillis()
        val filepath = path+"imdb-0.1.xmi"
        val ecore = path+"movies.ecore"
        val model = MovieLoader.load(filepath, ecore)
        val duration = (System.currentTimeMillis() - t1) / 1000
        println("A model with " + model.allModelElements.size + " elements, and " + model.allModelLinks.size + " links have been loaded in " + duration + " seconds")
    }
}
