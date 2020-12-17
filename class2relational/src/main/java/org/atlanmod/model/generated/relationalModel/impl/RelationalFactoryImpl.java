package org.atlanmod.model.generated.relationalModel.impl;

import org.atlanmod.model.generated.relationalModel.*;

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
public class RelationalFactoryImpl extends EFactoryImpl implements RelationalFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RelationalFactory init() {
		try {
			RelationalFactory theRelationalFactory = (RelationalFactory) EPackage.Registry.INSTANCE
					.getEFactory(RelationalPackage.eNS_URI);
			if (theRelationalFactory != null) {
				return theRelationalFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new RelationalFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RelationalFactoryImpl() {
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
		case RelationalPackage.TABLE:
			return createTable();
		case RelationalPackage.COLUMN:
			return createColumn();
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
	public Table createTable() {
		return new TableImpl();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Column createColumn() {
		return new ColumnImpl();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RelationalPackage getRelationalPackage() {
		return (RelationalPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static RelationalPackage getPackage() {
		return RelationalPackage.eINSTANCE;
	}

} //RelationalFactoryImpl
