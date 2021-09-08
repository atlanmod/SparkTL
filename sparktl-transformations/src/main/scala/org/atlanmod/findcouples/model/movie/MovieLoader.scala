package org.atlanmod.findcouples.model.movie

import java.io.{ByteArrayOutputStream, File, ObjectOutputStream}
import java.security.MessageDigest

import javax.xml.bind.DatatypeConverter
import org.atlanmod.findcouples.model.movie.element.{MovieActor, MovieActress, MovieMovie, MovieType}
import org.eclipse.emf.common.util.{EList, URI}
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.resource.{Resource, ResourceSet}
import org.eclipse.emf.ecore.xmi.impl.{EcoreResourceFactoryImpl, XMIResourceFactoryImpl}
import org.eclipse.emf.ecore.{EAttribute, EClass, EObject, EPackage}

import scala.collection.mutable

object MovieLoader{

    object Checksum {
        def getMD5(o: Any): String = {
            var baos: ByteArrayOutputStream = null
            var oos: ObjectOutputStream = null
            try {
                baos = new ByteArrayOutputStream()
                oos = new ObjectOutputStream(baos)
                oos.writeObject(o)

                val md : MessageDigest = MessageDigest.getInstance("MD5")
                val digest: Array[Byte] = md.digest(baos.toByteArray)
                DatatypeConverter.printHexBinary(digest)

            } finally {
                oos.close()
                baos.close()
            }
        }
    }

    private var resSet : ResourceSet = null

    private def instantiateRS():Unit =  {
        if (resSet != null) return
        val reg = Resource.Factory.Registry.INSTANCE
        val m = reg.getExtensionToFactoryMap
        m.put("xmi", new XMIResourceFactoryImpl)
        m.put("ecore", new EcoreResourceFactoryImpl)
        resSet = new ResourceSetImpl
    }

    def key(obj : Object): String = {
        Checksum.getMD5(obj)
    }

    def load(xmi_file: String, ecore_file: String) : MovieModel = {
        // Instantiate global ResourceSet
        instantiateRS()

        // Register the ecore file (containing the metamodel)
        val absolutePath_metamodel = new File(ecore_file).getAbsolutePath
        val uri_metamodel = URI.createURI(absolutePath_metamodel)
        val resource_ecore: Resource = resSet.getResource(uri_metamodel, true)
        val pack : EPackage = resource_ecore.getContents.get(0).asInstanceOf[EPackage]
        val packageRegistry = resSet.getPackageRegistry()
        packageRegistry.put(pack.getNsURI, pack)

        // Register the xmi file (containing the model)
        val absolutePath_model = new File(xmi_file).getAbsolutePath
        val uri_model = URI.createURI(absolutePath_model)
        val resource : Resource = resSet.getResource(uri_model, true)

        var elements: List[MovieElement] = List()
        var links: List[MovieLink] = List()
        var map: mutable.Map[String, MovieElement] = new mutable.HashMap[String, MovieElement]()

        for (i <- 0 until resource.getContents.size()){
//        for (i <- 0 until 2){
            val obj: EObject = resource.getContents.get(i)
            val cl: EClass = obj.eClass()
            val attributes: EList[EAttribute] = cl.getEAllAttributes
            obj.eClass().getName match {
                case MovieMetamodel.MOVIE =>
                    val title = obj.eGet(attributes.get(0)).asInstanceOf[String]
                    val rating = obj.eGet(attributes.get(1)).asInstanceOf[Double]
                    val year = obj.eGet(attributes.get(2)).asInstanceOf[Int]
                    val type_ = MovieType.stringToMovieTypeVal(obj.eGet(attributes.get(3)).asInstanceOf[String])
                    val movie = new MovieMovie(title, rating, year, type_)
                    elements = movie :: elements
                    map.put(key(movie), movie)
                case MovieMetamodel.ACTOR =>
                    val name = obj.eGet(attributes.get(0)).asInstanceOf[String]
                    val actor = new MovieActor(name)
                    elements = actor :: elements
                    map.put(key(actor), actor)
                case MovieMetamodel.ACTRESS =>
                    val name = obj.eGet(attributes.get(0)).asInstanceOf[String]
                    val actress = new MovieActress(name)
                    elements = actress :: elements
                    map.put(key(actress), actress)
            }
        }

        for (i <- 0 until resource.getContents.size()){
            resource.getContents.get(i).eClass().getName match {
                case MovieMetamodel.MOVIE => //TODO build the MovieToPersons
                case MovieMetamodel.ACTOR => //TODO build the PersonToMovies
                case MovieMetamodel.ACTRESS => //TODO build the PersonToMovies
                case MovieMetamodel.COUPLE => //TODO build the Couple, the CoupleToP1 and CoupleToP2
                case MovieMetamodel.CLIQUE => //TODO build the Clique, the CliqueToPersons
            }
        }
        println(elements.size)
        println(links.size)
        new MovieModel(elements, links)
    }

}