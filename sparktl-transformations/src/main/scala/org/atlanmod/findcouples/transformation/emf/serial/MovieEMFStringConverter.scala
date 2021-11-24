package org.atlanmod.findcouples.transformation.emf.serial

import movies.MoviesPackage
import org.atlanmod.tl.model.impl.emf.serializable.string.EMFStringConverter
import org.eclipse.emf.ecore.{EClass, EReference}

object MovieEMFStringConverter extends EMFStringConverter with Serializable {

    final val pack = MoviesPackage.eINSTANCE

    final val MOVIE = "Movie"
    final val GROUP = "Group"
    final val COUPLE = "Couple"
    final val CLIQUE = "Clique"
    final val PERSON = "Person"
    final val ACTOR = "Actor"
    final val ACTRESS = "Actress"
    final val ROOT = "Root"

    final val CLIQUE_PERSONS: String = "clique_persons"
    final val COUPLE_P1: String = "couple_p1"
    final val COUPLE_P2: String = "couple_p2"
    final val GROUP_MOVIES: String = "group_commonmovies"
    final val PERSON_MOVIES: String = "person_movies"
    final val MOVIE_PERSONS: String = "movie_persons"

    override def stringToEClass(str: String): EClass =
        str match {
            case ACTOR => pack.getActor
            case ACTRESS => pack.getActress
            case CLIQUE => pack.getClique
            case COUPLE => pack.getCouple
            case GROUP => pack.getGroup
            case MOVIE => pack.getMovie
            case PERSON => pack.getPerson
            case ROOT => pack.getRoot
        }

    override def EClassToString(eclass: EClass): String = {
        if (eclass.equals(pack.getActor)) ACTOR
        else if (eclass.equals(pack.getActress)) ACTRESS
        else if (eclass.equals(pack.getClique)) CLIQUE
        else if (eclass.equals(pack.getCouple)) COUPLE
        else if (eclass.equals(pack.getGroup)) GROUP
        else if (eclass.equals(pack.getMovie)) MOVIE
        else if (eclass.equals(pack.getPerson)) PERSON
        else if (eclass.equals(pack.getRoot)) ROOT
        else throw new Exception("Cannot convert " + eclass + " intro string value.")
    }

    override def stringFromEClass(str: EClass): String = EClassToString(str)
    override def EClassFromString(str: String): EClass = stringToEClass(str)

    override def stringToEReference(str: String): EReference =
        str match {
            case CLIQUE_PERSONS => pack.getClique_Persons
            case COUPLE_P1 => pack.getCouple_P1
            case COUPLE_P2 => pack.getCouple_P2
            case GROUP_MOVIES => pack.getGroup_CommonMovies
            case PERSON_MOVIES => pack.getPerson_Movies
            case MOVIE_PERSONS => pack.getMovie_Persons
        }

    override def EReferenceToString(eref: EReference): String = {
        if (eref.equals(pack.getClique_Persons)) CLIQUE_PERSONS
        else if (eref.equals(pack.getCouple_P1)) COUPLE_P1
        else if (eref.equals(pack.getCouple_P2)) COUPLE_P2
        else if (eref.equals(pack.getGroup_CommonMovies)) GROUP_MOVIES
        else if (eref.equals(pack.getPerson_Movies)) PERSON_MOVIES
        else if (eref.equals(pack.getMovie_Persons)) MOVIE_PERSONS
        else throw new Exception("Cannot convert " + eref + " intro string value.")
    }

    override def stringFromEReference(str: EReference): String = EReferenceToString(str)
    override def EReferenceFromString(str: String): EReference = stringToEReference(str)

}
