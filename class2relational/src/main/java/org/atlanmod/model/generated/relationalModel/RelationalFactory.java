package org.atlanmod.model.generated.relationalModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the create_simple_model.
 * It provides a create method for each non-abstract class of the create_simple_model.
 * <!-- end-user-doc -->
 * @see org.atlanmod.model.generated.relationalModel.RelationalPackage
 * @generated
 */
public interface RelationalFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RelationalFactory eINSTANCE = org.atlanmod.model.generated.relationalModel.impl.RelationalFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Table</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Table</em>'.
	 * @generated
	 */
	Table createTable();

	/**
	 * Returns a new object of class '<em>Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Column</em>'.
	 * @generated
	 */
	Column createColumn();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	RelationalPackage getRelationalPackage();

} //RelationalFactory
