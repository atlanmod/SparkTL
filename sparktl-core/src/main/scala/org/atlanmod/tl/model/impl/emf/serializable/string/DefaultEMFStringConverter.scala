package org.atlanmod.tl.model.impl.emf.serializable.string

import org.eclipse.emf.ecore.{EClass, EPackage, EReference}

class DefaultEMFStringConverter (pack: EPackage) extends EMFStringConverter {

    override def stringToEClass(str: String): EClass = pack.getEClassifier(str).asInstanceOf[EClass]
    override def EClassToString(eclass: EClass): String = eclass.toString

    override def stringFromEClass(str: EClass): String = EClassToString(str)
    override def EClassFromString(str: String): EClass = stringToEClass(str)

    override def stringToEReference(str: String): EReference = pack.getEClassifier(str).asInstanceOf[EReference]
    override def EReferenceToString(eclass: EReference): String = eclass.toString

    override def stringFromEReference(str: EReference): String = EReferenceToString(str)
    override def EReferenceFromString(str: String): EReference = stringToEReference(str)

}
