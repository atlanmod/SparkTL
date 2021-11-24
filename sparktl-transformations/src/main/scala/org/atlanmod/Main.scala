package org.atlanmod

import movies.MoviesPackage
import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.findcouples.model.transformation.emf.serial.SimpleIdentity
import org.atlanmod.tl.engine.TransformationSequential
import org.atlanmod.tl.model.impl.emf.{EMFMetamodel, EMFModel}

object Main {

    final val pack = MoviesPackage.eINSTANCE
    final val factory = pack.getMoviesFactory

    val conf = new SparkConf()
    conf.setAppName("")
    conf.setMaster("local[1]")
    val sc = new SparkContext(conf)

    def main(args: Array[String]): Unit = {
        val root = factory.createRoot()
        val m1 = factory.createMovie()
        m1.setTitle("episode 1")
        root.getChildren.add(m1)
        val m2 = factory.createMovie()
        m2.setTitle("episode 2")
        root.getChildren.add(m2)
        val sm = new EMFModel(root)
        val mm = new EMFMetamodel("Movies")
        val o = TransformationSequential.execute(SimpleIdentity.identity_imdb(), sm, mm)
//        val oprime = TransformationParallel.execute(SimpleIdentity.identity_imdb(), sm, mm, 1, sc)
        o.allModelElements.foreach(e => println(e))
    }

}
