package org.atlanmod.engine

import org.atlanmod.class2relational.model.classmodel.metamodel.{ClassMetamodel, ClassMetamodelNaive, ClassMetamodelWithMap}
import org.atlanmod.class2relational.model.relationalmodel.metamodel.{RelationalMetamodel, RelationalMetamodelNaive, RelationalMetamodelWithMap}
import org.atlanmod.class2relational.transformation.{Class2Relational, Relational2Class}
import org.atlanmod.dblpinfo.model.dblp.metamodel.{DblpMetamodel, DblpMetamodelNaive, DblpMetamodelWithMap}
import org.atlanmod.dblpinfo.tranformation.{ICMTActiveAuthors, ICMTAuthors, InactiveICMTButActiveAuthors, JournalISTActiveAuthors}
import org.atlanmod.families2persons.model.families.metamodel.{FamiliesMetamodel, FamiliesMetamodelNaive, FamiliesMetamodelWithMap}
import org.atlanmod.families2persons.transformation.Families2Persons
import org.atlanmod.findcouples.ModelSamples
import org.atlanmod.findcouples.model.movie.MovieJSONLoader
import org.atlanmod.findcouples.model.movie.metamodel.{MovieMetamodel, MovieMetamodelNaive, MovieMetamodelWithMap}
import org.atlanmod.findcouples.transformation.dynamic.{FindCouples, Identity}
import org.atlanmod.tl.engine.Parameters
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}

object CaseUtils {

    final val CLASS2RELATIONAL: String = "Class2Relational"
    final val RELATIONAL2CLASS: String = "Relational2Class"
    final val FAMILIES2PERSONS: String = "Families2Persons"
    final val IMDBFINDCOUPLES: String = "IMDBFindCouples"
    final val IMDBIDENTITY: String = "IMDBIdentity"
    final val DBLP_V1: String = "DBLP_v1"
    final val DBLP_V2: String = "DBLP_v2"
    final val DBLP_V3: String = "DBLP_v3"
    final val DBLP_V4: String = "DBLP_v4"

    def getTransformation(name: String, config: Parameters.Config)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        name match {
            case CLASS2RELATIONAL => {
                val class_metamodel: ClassMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => ClassMetamodelNaive
                        case Parameters.LINKS_MAP => ClassMetamodelWithMap
                    }
                val rel_metamodel: RelationalMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => RelationalMetamodelNaive
                        case Parameters.LINKS_MAP => RelationalMetamodelWithMap
                    }
                Class2Relational.class2relational(class_metamodel, rel_metamodel, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case RELATIONAL2CLASS => {
                val class_metamodel: ClassMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => ClassMetamodelNaive
                        case Parameters.LINKS_MAP => ClassMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                val rel_metamodel: RelationalMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => RelationalMetamodelNaive
                        case Parameters.LINKS_MAP => RelationalMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                Relational2Class.relational2class(rel_metamodel, class_metamodel, sleeping_guard = config._sleepGuard, sleeping_instantiate = config._sleepInstantiate, sleeping_apply = config._sleepApply)
            }
            case IMDBFINDCOUPLES => {
                val metamodel: MovieMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => MovieMetamodelNaive
                        case Parameters.LINKS_MAP => MovieMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                FindCouples.findcouples_imdb(metamodel, config._memoization, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case IMDBIDENTITY => {
                val metamodel: MovieMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => MovieMetamodelNaive
                        case Parameters.LINKS_MAP => MovieMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                Identity.identity_imdb(metamodel, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case DBLP_V1 => {
                val metamodel: DblpMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => DblpMetamodelNaive
                        case Parameters.LINKS_MAP => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                ICMTAuthors.find(metamodel, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case DBLP_V2 => {
                val metamodel: DblpMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => DblpMetamodelNaive
                        case Parameters.LINKS_MAP => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                ICMTActiveAuthors.find(metamodel, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case DBLP_V3 => {
                val metamodel: DblpMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => DblpMetamodelNaive
                        case Parameters.LINKS_MAP => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                InactiveICMTButActiveAuthors.find(metamodel, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case DBLP_V4 => {
                val metamodel: DblpMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => DblpMetamodelNaive
                        case Parameters.LINKS_MAP => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                JournalISTActiveAuthors.find(metamodel, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case FAMILIES2PERSONS => {
                val metamodel: FamiliesMetamodel =
                    config._link_type match {
                        case Parameters.LINKS_LIST => FamiliesMetamodelNaive
                        case Parameters.LINKS_MAP => FamiliesMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
                    }
                Families2Persons.families2persons(metamodel, config._sleepGuard, config._sleepInstantiate, config._sleepApply)
            }
            case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + config._link_type)
        }
    }

    def getMetamodel(name: String): DynamicMetamodel[DynamicElement, DynamicLink] =
        name match {
            case CLASS2RELATIONAL => ClassMetamodelNaive.metamodel
            case RELATIONAL2CLASS => RelationalMetamodelNaive.metamodel
            case IMDBFINDCOUPLES => MovieMetamodelNaive.metamodel
            case IMDBIDENTITY => MovieMetamodelNaive.metamodel
            case DBLP_V1 => DblpMetamodelNaive.metamodel
            case DBLP_V2 => DblpMetamodelNaive.metamodel
            case DBLP_V3 => DblpMetamodelNaive.metamodel
            case DBLP_V4 => DblpMetamodelNaive.metamodel
            case FAMILIES2PERSONS => FamiliesMetamodelNaive.metamodel
            case _ => throw new Exception("Impossible to get the metamodel. Unknown transformation: " + name)
        }


    def getModel(mm: DynamicMetamodel[DynamicElement, DynamicLink], input: String, size: Int, files: List[String]): DynamicModel = {
        if (mm == ClassMetamodelNaive.metamodel)
            input match {
                case "size" => org.atlanmod.class2relational.model.ModelSamples.getReplicatedClassSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Class model from files is not supported yet")
            }
        else if (mm == RelationalMetamodelNaive.metamodel)
            input match {
                case "size" => org.atlanmod.class2relational.model.ModelSamples.getReplicatedRelationalSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Relational model from files is not supported yet")
            }
        else if (mm == FamiliesMetamodelNaive.metamodel)
            input match {
                case "size" => org.atlanmod.families2persons.model.ModelSamples.getFamiliesModel(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Relational model from files is not supported yet")
            }
        else if (mm == MovieMetamodelNaive.metamodel)
            input match {
                case "size" => ModelSamples.getReplicatedSimple(size).asInstanceOf[DynamicModel]
                case "files" =>
                    (files.find(f => f.contains("movie")), files.find(f => f.contains("actor")), files.find(f => f.contains("link"))) match {
                        case (Some(movie_file), Some(actor_file), Some(link_file)) =>
                            MovieJSONLoader.load(actor_file, movie_file, link_file)
                        case (None, _, _) => throw new Exception("JSON file containing movies not declared")
                        case (_, None, _) => throw new Exception("JSON file containing actors not declared")
                        case (_, _, None) => throw new Exception("TXT file containing links not declared")
                    }
            }
        else if (mm == DblpMetamodelNaive.metamodel)
            input match {
                case "size" => org.atlanmod.dblpinfo.model.ModelSamples.getReplicatedSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Dblp model from files is not supported yet")
            }
        else throw new Exception("Impossible to generate a model. Unknown metamodel: " + mm.getClass.getName)
    }

}
