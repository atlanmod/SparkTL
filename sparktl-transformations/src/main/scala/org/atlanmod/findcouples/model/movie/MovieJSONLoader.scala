package org.atlanmod.findcouples.model.movie

import net.liftweb.json.JsonAST.JString
import net.liftweb.json.{JDouble, JInt, JsonAST}
import org.atlanmod.findcouples.model.movie.element.{MovieActor, MovieMovie, MoviePerson}
import org.atlanmod.findcouples.model.movie.link.{MovieToPersons, PersonToMovies}

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.io.Source

object MovieJSONLoader{

    val map : mutable.Map[BigInt, MovieElement] = new HashMap[BigInt, MovieElement]

    def load(file_actors: String, file_movies: String, file_links: String): MovieModel = {

        // Load actors
        var actors: List[MovieActor] = List()
        var movies: List[MovieMovie] = List()

//        val json_actors = "[" + Source.fromFile(file_actors).getLines().mkString + "]"
        val json_actors = Source.fromFile(file_actors).getLines().mkString
        val parsed_actors = net.liftweb.json.parse(json_actors).asInstanceOf[JsonAST.JArray].arr
        for(j <- parsed_actors){
            val id : BigInt = j.\("id").asInstanceOf[JInt].num
            val name : String = j.\("name").asInstanceOf[JString].s
            val actor = new MovieActor(name)
            actors = actor :: actors
            map.put(id, actor)
        }

        val json_movies = Source.fromFile(file_movies).getLines().mkString
//        val json_movies = "[" + Source.fromFile(file_movies).getLines().mkString + "]"
        val parsed_movies = net.liftweb.json.parse(json_movies).asInstanceOf[JsonAST.JArray].arr
        for(m <- parsed_movies){
            val id : BigInt = m.\("id").asInstanceOf[JInt].num
            val title : String = m.\("title").asInstanceOf[JString].s
            val rating : Double = m.\("rating").asInstanceOf[JDouble].num
            val year: Int = m.\("year").asInstanceOf[JInt].num.toInt
            val movietype: String = m.\("movieType").asInstanceOf[JString].s
            val movie = new MovieMovie(title, rating, year, movietype)
            movies = movie :: movies
            map.put(id, movie)
        }
        val elements: List[MovieElement] = actors ++ movies

        var links: List[MovieLink] = List()
        for (line <- Source.fromFile(file_links).getLines) {
            val link = line.split("\t")
            val target_list: List[MovieElement] = link(1).substring(1, link(1).size - 1).split(",").flatMap(t => map.get(t.toLong)).toList
            (map.get(link(0).toLong), link(2)) match {
                case (Some(source: MoviePerson), label) if label == MovieMetamodel.PERSON_MOVIES =>
                    links = new PersonToMovies(source, target_list.asInstanceOf[List[MovieMovie]]) :: links
                case (Some(source: MovieMovie), label) if label == MovieMetamodel.MOVIE_PERSONS =>
                    links = new MovieToPersons(source, target_list.asInstanceOf[List[MoviePerson]]) :: links
                case _ =>
            }
        }
        new MovieModel(elements, links)
    }

}