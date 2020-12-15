/**
 */
package org.atlanmod.generated.classModel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.atlanmod.generated.classModel.Attribute#getId <em>Id</em>}</li>
 *   <li>{@link org.atlanmod.generated.classModel.Attribute#isDerived <em>Derived</em>}</li>
 *   <li>{@link org.atlanmod.generated.classModel.Attribute#getName <em>Name</em>}</li>
 *   <li>{@link org.atlanmod.generated.classModel.Attribute#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see org.atlanmod.generated.classModel.ClassPackage#getAttribute()
 * @model
 * @generated
 */
public interface Attribute extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.atlanmod.generated.classModel.ClassPackage#getAttribute_Id()
	 * @model default="-1"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.atlanmod.generated.classModel.Attribute#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Derived</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Derived</em>' attribute.
	 * @see #setDerived(boolean)
	 * @see org.atlanmod.generated.classModel.ClassPackage#getAttribute_Derived()
	 * @model default="true"
	 * @generated
	 */
	boolean isDerived();

	/**
	 * Sets the value of the '{@link org.atlanmod.generated.classModel.Attribute#isDerived <em>Derived</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Derived</em>' attribute.
	 * @see #isDerived()
	 * @generated
	 */
	void setDerived(boolean value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>"\"\""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.atlanmod.generated.classModel.ClassPackage#getAttribute_Name()
	 * @model default="\"\"" required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.atlanmod.generated.classModel.Attribute#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.atlanmod.generated.classModel.Class#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' container reference.
	 * @see #setType(org.atlanmod.generated.classModel.Class)
	 * @see org.atlanmod.generated.classModel.ClassPackage#getAttribute_Type()
	 * @see org.atlanmod.generated.classModel.Class#getAttributes
	 * @model opposite="attributes" transient="false" ordered="false"
	 * @generated
	 */
	org.atlanmod.generated.classModel.Class getType();

	/**
	 * Sets the value of the '{@link org.atlanmod.generated.classModel.Attribute#getType <em>Type</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' container reference.
	 * @see #getType()
	 * @generated
	 */
	void setType(org.atlanmod.generated.classModel.Class value);

} // Attribute
