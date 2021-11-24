package org.atlanmod.tl.model.impl.emf.serializable.string

import org.eclipse.emf.ecore.{EClass, EReference}

trait EMFStringConverter {

    def stringToEClass(str: String): EClass

    def EClassToString(eclass: EClass): String

    def stringFromEClass(str: EClass): String

    def EClassFromString(str: String): EClass

    def stringToEReference(str: String): EReference

    def EReferenceToString(eclass: EReference): String

    def stringFromEReference(str: EReference): String

    def EReferenceFromString(str: String): EReference

}
