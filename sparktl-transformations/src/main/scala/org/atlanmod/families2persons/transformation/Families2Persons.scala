package org.atlanmod.families2persons.transformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.families2persons.model.families.FamiliesModel
import org.atlanmod.families2persons.model.families.element.FamiliesMember
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodel
import org.atlanmod.families2persons.model.persons.element.{PersonsFemale, PersonsMale}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}

import scala.util.Random

object Families2Persons {

    val random: Random.type = scala.util.Random

    final val MALE_PATTERN : String = "male"
    final val FEMALE_PATTERN : String = "female"

    def families2persons(metamodel: FamiliesMetamodel, sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl(List(
            new RuleImpl(name = "Member2Female",
                types = List(metamodel.MEMBER),
                from = (model, pattern) => { my_sleep(sleeping_guard, random.nextInt())
                    Some(FamiliesHelper.isFemale(model.asInstanceOf[FamiliesModel], pattern.head.asInstanceOf[FamiliesMember], metamodel))},
                to = List(
                    new OutputPatternElementImpl(name = FEMALE_PATTERN,
                        elementExpr = (i, sm, pattern) => {
                            val model = sm.asInstanceOf[FamiliesModel]
                            val member = pattern.head.asInstanceOf[FamiliesMember]
                            Some(new PersonsFemale(member.getFirstName() + " " + FamiliesHelper.familyName(model, member, metamodel)))
                        }
                    )
                )
            ),
            new RuleImpl(name = "Member2Male",
                types = List(metamodel.MEMBER),
                from = (model, pattern) => { my_sleep(sleeping_guard, random.nextInt())
                    Some(!FamiliesHelper.isFemale(model.asInstanceOf[FamiliesModel], pattern.head.asInstanceOf[FamiliesMember], metamodel))},
                to = List(new OutputPatternElementImpl(name = MALE_PATTERN,
                    elementExpr = (i, sm, pattern) => {
                        val model = sm.asInstanceOf[FamiliesModel]
                        val member = pattern.head.asInstanceOf[FamiliesMember]
                        Some(new PersonsMale(member.getFirstName() + " " + FamiliesHelper.familyName(model, member, metamodel)))
                    }
                )))
        ))
    }

}
