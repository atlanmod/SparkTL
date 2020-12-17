package org.atlanmod.model.generated.classModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the create_simple_model.
 * It provides a create method for each non-abstract class of the create_simple_model.
 * <!-- end-user-doc -->
 * @see org.atlanmod.model.generated.classModel.ClassPackage
 * @generated
 */
public interface ClassFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ClassFactory eINSTANCE = org.atlanmod.model.generated.classModel.impl.ClassFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Class</em>'.
	 * @generated
	 */
	Class createClass();

	/**
	 * Returns a new object of class '<em>Attribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attribute</em>'.
	 * @generated
	 */
	Attribute createAttribute();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ClassPackage getClassPackage();

} //ClassFactory
