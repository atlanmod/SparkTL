package org.atlanmod.findcouples.model.movie

object MovieXMItoJSON{
//    private var resSet : ResourceSet = null
////
//    private def instantiateRS():Unit =  {
//        if (resSet != null) return
//        val reg = Resource.Factory.Registry.INSTANCE
//        val m = reg.getExtensionToFactoryMap
//        m.put("xmi", new XMIResourceFactoryImpl)
//        m.put("ecore", new EcoreResourceFactoryImpl)
//        resSet = new ResourceSetImpl
//    }
//
//    private def getPersons(objects: EList[EObject], map: mutable.Map[Int, MovieElement]): Option[List[MoviePerson]] = {
//        var result: List[MoviePerson] = List()
//        val it = objects.iterator()
//        while(it.hasNext){
//            val obj = it.next()
//            map.get(obj.hashCode()) match {
//                case Some(person : MoviePerson) => result = person :: result
//                case _ =>
//            }
//        }
//        if (result.isEmpty) None else Some(result)
//    }
//
//    private def getMovies(objects: EList[EObject], map:  mutable.Map[Int, MovieElement]): Option[List[MovieMovie]] = {
//        var result: List[MovieMovie] = List()
//        val it = objects.iterator()
//        while(it.hasNext){
//            val obj = it.next()
//            map.get(obj.hashCode()) match {
//                case Some(movie: MovieMovie) => result = movie :: result
//                case _ =>
//            }
//        }
//        if (result.isEmpty) None else Some(result)
//    }
//
//    def load_elements(resource: Resource):  (List[MovieElement], mutable.Map[Int, MovieElement]) = {
//        var elements: List[MovieElement] = List()
//        val map: mutable.Map[Int, MovieElement] = new mutable.HashMap[Int, MovieElement]()
//        for (i <- 0 until resource.getContents.size()){
//            val obj: EObject = resource.getContents.get(i)
//            val cl: EClass = obj.eClass()
//            val attributes: EList[EAttribute] = cl.getEAllAttributes
//            obj.eClass().getName match {
//
//                case MovieMetamodel.MOVIE => // A movie : (title, rating, year, type)
//                    val title = obj.eGet(attributes.get(0)).asInstanceOf[String]
//                    val rating = obj.eGet(attributes.get(1)).asInstanceOf[Double]
//                    val year = obj.eGet(attributes.get(2)).asInstanceOf[Int]
//                    val type_ = MovieType.stringToMovieTypeVal(obj.eGet(attributes.get(3)).asInstanceOf[String])
//                    val movie = new MovieMovie(title, rating, year, type_)
//                    elements = movie :: elements // add the new movie to model elements
//                    map.put(obj.hashCode(), movie) // link obj to the MovieMovie instance
//
//                case MovieMetamodel.ACTOR => // An actor : (name)
//                    val name = obj.eGet(attributes.get(0)).asInstanceOf[String]
//                    val actor = new MovieActor(name)
//                    elements = actor :: elements // add the new actor to model elements
//                    map.put(obj.hashCode(), actor) // link obj to the MovieActor instance
//
//                case MovieMetamodel.ACTRESS =>  // An actress : (name)
//                    val name = obj.eGet(attributes.get(0)).asInstanceOf[String]
//                    val actress = new MovieActress(name)
//                    elements = actress :: elements // add the new actress to model elements
//                    map.put(obj.hashCode(), actress)  // link obj to the MovieActress instance
//            }
//        }
//        (elements, map)
//    }
//
//    def load_links(resource:Resource, map: mutable.Map[Int, MovieElement]): (List[MovieElement], List[MovieLink]) = {
//        var elements: List[MovieElement] = List()
//        var links: List[MovieLink] = List()
//        for (i <- 0 until resource.getContents.size()){
//            val obj: EObject = resource.getContents.get(i)
//            val cl: EClass = obj.eClass()
//            val references: EList[EReference] = cl.getEAllReferences
//            val attributes: EList[EAttribute] = cl.getEAllAttributes
//            resource.getContents.get(i).eClass().getName match {
//
//                case MovieMetamodel.MOVIE =>
//                    val e_persons : EList[EObject] = obj.eGet(references.get(0)).asInstanceOf[EList[EObject]]
//                    (map.get(obj.hashCode()), getPersons(e_persons, map)) match {
//                        case (Some(movie: MovieMovie), Some(persons: List[MoviePerson])) => links = new MovieToPersons(movie, persons) :: links
//                        case _ =>
//                    }
//
//                case MovieMetamodel.ACTOR =>
//                    val e_movies : EList[EObject] = obj.eGet(references.get(0)).asInstanceOf[EList[EObject]]
//                    (map.get(obj.hashCode()), getMovies(e_movies, map)) match {
//                        case (Some(actor: MovieActor), Some(movies)) => links = new PersonToMovies(actor, movies) :: links
//                        case _ =>
//                    }
//
//                case MovieMetamodel.ACTRESS =>
//                    val e_movies : EList[EObject] = obj.eGet(references.get(0)).asInstanceOf[EList[EObject]]
//                    (map.get(obj.hashCode()), getMovies(e_movies, map)) match {
//                        case (Some(actress: MovieActress), Some(movies)) => links = new PersonToMovies(actress, movies) :: links
//                        case _ =>
//                    }
//
//                case MovieMetamodel.COUPLE =>
//                    val e_p1 : EObject = obj.eGet(references.get(0)).asInstanceOf[EObject]
//                    val e_p2 : EObject = obj.eGet(references.get(1)).asInstanceOf[EObject]
//                    (map.get(e_p1.hashCode()), map.get(e_p2.hashCode())) match {
//                        case (Some(p1 : MoviePerson), Some(p2 : MoviePerson)) =>
//                            val avg_rating : Double = obj.eGet(attributes.get(0)).asInstanceOf[Double]
//                            val couple: MovieCouple = new MovieCouple(p1.getName + " & " + p2.getName, avg_rating)
//                            elements = couple :: elements
//                            links = new CoupleToPersonP1(couple, p1) :: new CoupleToPersonP2(couple, p2) :: links
//                        case _ =>
//                    }
//
//                case MovieMetamodel.CLIQUE =>
//                    val e_persons : EList[EObject] = obj.eGet(references.get(0)).asInstanceOf[EList[EObject]]
//                    getPersons(e_persons, map) match {
//                        case Some(persons : List[MoviePerson]) =>
//                            val avg_rating : Double = obj.eGet(attributes.get(0)).asInstanceOf[Double]
//                            val clique = new MovieClique(persons.mkString(" & "), avg_rating)
//                            elements = clique :: elements
//                            links = new CliqueToPersons(clique, persons) :: links
//                        case _ =>
//                    }
//                case _ =>
//            }
//        }
//        (elements, links)
//    }
//
//    def load(xmi_file: String, ecore_file: String) : MovieModel = {
//        // Instantiate global ResourceSet
//        instantiateRS()
//
//        // Register the ecore file (containing the metamodel)
//        val absolutePath_metamodel = new File(ecore_file).getAbsolutePath
//        val uri_metamodel = URI.createURI(absolutePath_metamodel)
//        val resource_ecore: Resource = resSet.getResource(uri_metamodel, true)
//        val pack : EPackage = resource_ecore.getContents.get(0).asInstanceOf[EPackage]
//        val packageRegistry = resSet.getPackageRegistry()
//        packageRegistry.put(pack.getNsURI, pack)
//
//        // Register the xmi file (containing the model)
//        val absolutePath_model = new File(xmi_file).getAbsolutePath
//        val uri_model = URI.createURI(absolutePath_model)
//        val resource : Resource = resSet.getResource(uri_model, true)
//
//        // Load elements, and links
//        val elements_and_map: (List[MovieElement], mutable.Map[Int, MovieElement]) = load_elements(resource)
//        val elements_and_links: (List[MovieElement], List[MovieLink]) = load_links(resource, elements_and_map._2)
//        new MovieModel(elements_and_map._1 ++ elements_and_links._1, elements_and_links._2)
//    }
//
//    def write_actors(model: MovieModel, filename: String): (mutable.Map[MovieElement, Int], Int) = {
//        val pw = new PrintWriter(new File("actors_"+filename+".json" ))
//        val map: mutable.Map[MovieElement, Int] = new mutable.HashMap[MovieElement, Int]()
//        var id = 0
//        val movieActors = MovieMetamodel.getAllActors(model)
//        pw.write("[\n")
//        for(i <- movieActors.indices){
//            val actor = movieActors(i)
//            map.put(actor, id)
//            val coma = if (i == movieActors.size - 1) "" else ","
//            val line = "\t{\"id\":"+id+",\"name\":\""+actor.getName.replaceAll("\"","")+"\"}"+coma+"\n"
//            id = id + 1
//            pw.write(line)
//        }
//        pw.write("]")
//        pw.close()
//        (map, id)
//    }
//
//    def write_movies(model: MovieModel, map: mutable.Map[MovieElement, Int], start: Int, filename: String): mutable.Map[MovieElement, Int] = {
//        val pw = new PrintWriter(new File("movies_"+filename+".json"))
//        var id = start
//        val movieMovies = MovieMetamodel.getAllMovies(model)
//        pw.write("[\n")
//        for(i <- movieMovies.indices){
//            val movie: MovieMovie = movieMovies(i)
//            map.put(movie, id)
//            val coma = if (i == movieMovies.size - 1) "" else ","
//            val line = "\t{\"id\":"+id+",\"title\": \""+ movie.getTitle.replaceAll("\"","") +"\",\"rating\":"+ movie.getRating +",\"year\":"+ movie.getYear +",\"movieType\":\""+ movie.getMovieType +"\"}"+coma+"\n"
//            id = id + 1
//            pw.write(line)
//        }
//        pw.write("]")
//        pw.close()
//        map
//    }
//
//   def remove_some(maybeInts: List[Option[Int]]): List[Int] = {
//       maybeInts match {
//           case h :: t =>
//               h match {
//                   case Some(n) => n :: remove_some(t)
//                   case None => remove_some(t)
//               }
//           case List() => List()
//       }
//   }
//
//    def write_links(movieModel: MovieModel, map: mutable.Map[MovieElement, Int], filename: String): Unit = {
//        val pw = new PrintWriter(new File("links_"+filename+".txt"))
//        for (link: MovieLink <- movieModel.allModelLinks){
//            val source: String = map.get(link.getSource.asInstanceOf[MovieElement]).getOrElse(-1).toString
//            val target_option : List[Option[Int]] = link.getTarget.map(e => map.get(e.asInstanceOf[MovieElement]))
//            val target: String = remove_some(target_option).mkString("[",",","]")
//            val label: String = link.getType
//            val line = source + "\t" + target + "\t" + label + "\n"
//            pw.write(line)
//        }
//        pw.close()
//    }
//
//    def write(movieModel: MovieModel, filename: String): Unit = {
//        val res_1 = write_actors(movieModel, filename)
//        val res_2 = write_movies(movieModel, res_1._1, res_1._2, filename)
//        write_links(movieModel, res_2, filename)
//    }
//
//    def main(args: Array[String]) : Unit = {
//        val files = List("imdb-0.5","imdb-1.0")
//        for(file <- files) {
//            val m = load("/home/jolan/Scala/SparkTL/SparkTL/deployment/g5k/" + file + ".xmi", "/home/jolan/Scala/SparkTL/SparkTL/deployment/g5k/movies.ecore")
//            write(m, file)
//        }
//    }

}