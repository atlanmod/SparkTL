/**
 */
package org.atlanmod.classModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.atlanmod.classModel.Class#getId <em>Id</em>}</li>
 *   <li>{@link org.atlanmod.classModel.Class#getName <em>Name</em>}</li>
 *   <li>{@link org.atlanmod.classModel.Class#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @see org.atlanmod.classModel.ClassPackage#getClass_()
 * @model
 * @generated
 */
public interface Class extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.atlanmod.classModel.ClassPackage#getClass_Id()
	 * @model default="-1" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.atlanmod.classModel.Class#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>"\"\""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.atlanmod.classModel.ClassPackage#getClass_Name()
	 * @model default="\"\"" required="true" ordered="false"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.atlanmod.classModel.Class#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
	 * The list contents are of type {@link org.atlanmod.classModel.Attribute}.
	 * It is bidirectional and its opposite is '{@link org.atlanmod.classModel.Attribute#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' containment reference list.
	 * @see org.atlanmod.classModel.ClassPackage#getClass_Attributes()
	 * @see org.atlanmod.classModel.Attribute#getType
	 * @model opposite="type" containment="true" ordered="false"
	 * @generated
	 */
	EList<Attribute> getAttributes();

} // Class