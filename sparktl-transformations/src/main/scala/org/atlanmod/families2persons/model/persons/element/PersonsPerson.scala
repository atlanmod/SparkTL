package org.atlanmod.families2persons.model.persons.element

import org.atlanmod.families2persons.model.persons.PersonsElement

abstract class PersonsPerson(classname: String) extends PersonsElement(classname: String){

    def getFullName(): String

}
