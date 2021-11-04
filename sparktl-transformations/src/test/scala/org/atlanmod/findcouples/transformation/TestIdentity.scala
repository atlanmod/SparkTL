package org.atlanmod.findcouples.transformation

import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.findcouples.model.movie.link.{MovieToPersons, PersonToMovies}
import org.atlanmod.findcouples.model.movie.{MovieElement, MovieLink, MovieMetamodel, MovieModel}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.scalatest.funsuite.AnyFunSuite

class TestIdentity extends AnyFunSuite {

    val billy_dee_williams = new MovieActor("Billy Dee Williams")
    val nathalie_portman = new MovieActress("Nathalie Portman")
    val ewan_mcgregor = new MovieActor("Ewan Mcgregor")
    val hayden_christensen = new MovieActor("Hayden Christensen")
    val harrison_ford = new MovieActor("Harrison Ford")
    val mark_hamill = new MovieActor("Mark Hamill")
    val carrie_fisher = new MovieActress("Carrie Fisher")
    val the_phantom_menace = new MovieMovie("The Phantom Menace", 6.5, 1999, MovieType.MOVIE)
    val attack_of_the_clones = new MovieMovie("Attack of the Clones", 6.0, 2002, MovieType.MOVIE)
    val revenge_of_the_sith = new MovieMovie("Revenge of the Sith", 9.0, 2005, MovieType.MOVIE)
    val a_new_hope = new MovieMovie("A New Hope", 8.0, 1977, MovieType.MOVIE)
    val the_empire_strikes_back = new MovieMovie("The Empire Strikes Back", 9.0, 1980, MovieType.MOVIE)
    val return_of_the_jedi = new MovieMovie("The Return of the Jedi", 9.5, 1983, MovieType.MOVIE)

    def StarWarsModel : MovieModel = {
        // Actors + actresses
        val actors: List[MoviePerson] = List(billy_dee_williams, nathalie_portman, ewan_mcgregor, harrison_ford,
            mark_hamill, carrie_fisher, hayden_christensen)
        val movies: List[MovieMovie] = List(the_empire_strikes_back, the_phantom_menace, attack_of_the_clones,
            revenge_of_the_sith, a_new_hope, return_of_the_jedi)
        // Movies to persons
        val tpm_actors = new MovieToPersons(the_phantom_menace, List(nathalie_portman, ewan_mcgregor))
        val aotc_actors = new MovieToPersons(attack_of_the_clones, List(nathalie_portman, ewan_mcgregor, hayden_christensen))
        val rots_actors = new MovieToPersons(revenge_of_the_sith, List(nathalie_portman, ewan_mcgregor, hayden_christensen))
        val anh_actors = new MovieToPersons(a_new_hope, List(harrison_ford, carrie_fisher, mark_hamill))
        val tesb_actors = new MovieToPersons(the_empire_strikes_back, List(harrison_ford, carrie_fisher, mark_hamill, billy_dee_williams))
        val rotj_actors = new MovieToPersons(return_of_the_jedi, List(harrison_ford, carrie_fisher, mark_hamill, billy_dee_williams))
        val m2p: List[MovieToPersons] = List(tpm_actors, aotc_actors, rots_actors, anh_actors, tesb_actors, rotj_actors)

        // Person to Movies
        val billy_dee_williams_movies = new PersonToMovies(billy_dee_williams, List(the_empire_strikes_back, return_of_the_jedi))
        val nathalie_portman_movies = new PersonToMovies(nathalie_portman, List(the_phantom_menace, attack_of_the_clones, revenge_of_the_sith))
        val ewan_mcgregor_movies = new PersonToMovies(ewan_mcgregor, List(the_phantom_menace, attack_of_the_clones, revenge_of_the_sith))
        val hayden_christensen_movies = new PersonToMovies(hayden_christensen, List(attack_of_the_clones, revenge_of_the_sith))
        val harrison_ford_movies = new PersonToMovies(harrison_ford, List(a_new_hope,the_empire_strikes_back, return_of_the_jedi))
        val mark_hamill_movies = new PersonToMovies(mark_hamill, List(a_new_hope,the_empire_strikes_back, return_of_the_jedi))
        val carrie_fisher_movies = new PersonToMovies(carrie_fisher, List(a_new_hope,the_empire_strikes_back, return_of_the_jedi))
        val p2m: List[PersonToMovies] = List(billy_dee_williams_movies,nathalie_portman_movies, ewan_mcgregor_movies,
            hayden_christensen_movies, harrison_ford_movies, mark_hamill_movies, carrie_fisher_movies)

        // Elements and links
        val elements: List[MovieElement] = actors ++ movies
        val links: List[MovieLink] = m2p ++ p2m

        new MovieModel(elements, links)
    }

    def makeMovieModel: (List[DynamicElement], List[DynamicLink]) => MovieModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new MovieModel(e.asInstanceOf[List[MovieElement]], l.asInstanceOf[List[MovieLink]])

    test("identity sw") {
        val metamodel = MovieMetamodel.metamodel
        val tr_identity = Identity.identity_imdb()
        val exp = StarWarsModel
        val res: MovieModel =  org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(tr_identity,
            StarWarsModel, metamodel, makeModel = makeMovieModel).asInstanceOf[MovieModel]
        assert(exp.equals(res))
    }

}
