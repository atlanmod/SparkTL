/**
 */
package org.atlanmod.relationalModel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Column</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.atlanmod.relationalModel.Column#getId <em>Id</em>}</li>
 *   <li>{@link org.atlanmod.relationalModel.Column#getName <em>Name</em>}</li>
 *   <li>{@link org.atlanmod.relationalModel.Column#getReference <em>Reference</em>}</li>
 * </ul>
 *
 * @see org.atlanmod.relationalModel.RelationalPackage#getColumn()
 * @model
 * @generated
 */
public interface Column extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.atlanmod.relationalModel.RelationalPackage#getColumn_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.atlanmod.relationalModel.Column#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.atlanmod.relationalModel.RelationalPackage#getColumn_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.atlanmod.relationalModel.Column#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Reference</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.atlanmod.relationalModel.Table#getColumns <em>Columns</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reference</em>' container reference.
	 * @see #setReference(Table)
	 * @see org.atlanmod.relationalModel.RelationalPackage#getColumn_Reference()
	 * @see org.atlanmod.relationalModel.Table#getColumns
	 * @model opposite="columns" transient="false" ordered="false"
	 * @generated
	 */
	Table getReference();

	/**
	 * Sets the value of the '{@link org.atlanmod.relationalModel.Column#getReference <em>Reference</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference</em>' container reference.
	 * @see #getReference()
	 * @generated
	 */
	void setReference(Table value);

} // Column
