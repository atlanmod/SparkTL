/**
 */
package org.atlanmod.relationalModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.atlanmod.relationalModel.Table#getId <em>Id</em>}</li>
 *   <li>{@link org.atlanmod.relationalModel.Table#getName <em>Name</em>}</li>
 *   <li>{@link org.atlanmod.relationalModel.Table#getColumns <em>Columns</em>}</li>
 * </ul>
 *
 * @see org.atlanmod.relationalModel.RelationalPackage#getTable()
 * @model
 * @generated
 */
public interface Table extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * The default value is <code>"\"\""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.atlanmod.relationalModel.RelationalPackage#getTable_Id()
	 * @model default="\"\"" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.atlanmod.relationalModel.Table#getId <em>Id</em>}' attribute.
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
	 * @see org.atlanmod.relationalModel.RelationalPackage#getTable_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.atlanmod.relationalModel.Table#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Columns</b></em>' containment reference list.
	 * The list contents are of type {@link org.atlanmod.relationalModel.Column}.
	 * It is bidirectional and its opposite is '{@link org.atlanmod.relationalModel.Column#getReference <em>Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Columns</em>' containment reference list.
	 * @see org.atlanmod.relationalModel.RelationalPackage#getTable_Columns()
	 * @see org.atlanmod.relationalModel.Column#getReference
	 * @model opposite="reference" containment="true" ordered="false"
	 * @generated
	 */
	EList<Column> getColumns();

} // Table
