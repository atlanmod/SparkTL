/**
 */
package movies;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Person</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link movies.Person#getMovies <em>Movies</em>}</li>
 *   <li>{@link movies.Person#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see movies.MoviesPackage#getPerson()
 * @model abstract="true"
 * @generated
 */
public interface Person extends ContainedElement {
	/**
	 * Returns the value of the '<em><b>Movies</b></em>' reference list.
	 * The list contents are of type {@link movies.Movie}.
	 * It is bidirectional and its opposite is '{@link movies.Movie#getPersons <em>Persons</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Movies</em>' reference list.
	 * @see movies.MoviesPackage#getPerson_Movies()
	 * @see movies.Movie#getPersons
	 * @model opposite="persons"
	 * @generated
	 */
	EList<Movie> getMovies();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see movies.MoviesPackage#getPerson_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link movies.Person#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // Person
