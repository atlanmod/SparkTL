package org.atlanmod.model.generated.classModel.impl;

import org.atlanmod.model.generated.classModel.Attribute;
import org.atlanmod.model.generated.classModel.ClassFactory;
import org.atlanmod.model.generated.classModel.ClassPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the create_simple_model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ClassFactoryImpl extends EFactoryImpl implements ClassFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ClassFactory init() {
		try {
			ClassFactory theClassFactory = (ClassFactory) EPackage.Registry.INSTANCE.getEFactory(ClassPackage.eNS_URI);
			if (theClassFactory != null) {
				return theClassFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ClassFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ClassFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case ClassPackage.CLASS:
			return createClass();
		case ClassPackage.ATTRIBUTE:
			return createAttribute();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public org.atlanmod.model.generated.classModel.Class createClass() {
		return new ClassImpl();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Attribute createAttribute() {
		return new AttributeImpl();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ClassPackage getClassPackage() {
		return (ClassPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ClassPackage getPackage() {
		return ClassPackage.eINSTANCE;
	}

} //ClassFactoryImpl
