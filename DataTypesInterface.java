package fraclac.writers;

/**
 * Marker interface for enums that hold variables for data maps. The interface
 * is intended to be used for writing data from fractal analysis scans done
 * using the FracLac plugin for ImageJ, in which case its methods are intended
 * to be implemented in specific ways. The general gist is to provide a
 * consistent hierarchical way of returning values stored in fields in enums
 * based on the interface. When used for printing data maps in the FracLac
 * plugin for ImageJ, implementations should use the {@link EnumString} class to
 * define the overridden methods to call correspondingly named methods in that
 * class.
 * 
 * @version FracLac 2014Jan $Rev: 226 $
 * @version Revision $Id: DataTypesInterface.java 54 2013-02-03 08:45:01Z audrey
 *          $
 */
public interface DataTypesInterface {

	/**
	 * Flag to be used for indicating that a variable has not been initialized.
	 * The value is {@value} .
	 */
	public final static String UNINITIALIZED = "2@339u%$";

	/**
	 * Method should be written to return {@link #sAllGRIDs} whens used for
	 * printing data maps in the FracLac plugin for ImageJ.
	 * 
	 * @return String
	 */
	@Override
	String toString();

	/**
	 * Interface method should return the string stored in {@link #sAllGRIDs
	 * sAllGRIDs}, when used for printing data maps in the FracLac plugin for
	 * ImageJ.
	 * 
	 * @return String
	 */
	String sAllGRIDs();

	/**
	 * Interface method that for consistency in the FracLac plugin should be
	 * defined to return one of the headings associated with this enum constant,
	 * by calling the {@link EnumString#sOneGRID} method in the
	 * {@link EnumString} class, when used for printing data maps in the FracLac
	 * plugin for ImageJ.
	 * 
	 * @return String
	 */
	String sOneGRID();

	/**
	 * Interface method that for consistency in the FracLac plugin should be
	 * defined to return one of the headings associated with this enum constant,
	 * by calling the {@link EnumString#sGrayAllGRIDs} method in the
	 * {@link EnumString} class.
	 * 
	 * @return String
	 */
	String sGrayAllGRIDs();

	/**
	 * Interface method that for consistency in the FracLac plugin should be
	 * defined to return one of the headings associated with this enum constant,
	 * by calling the {@link EnumString#sGrayOneGRID} method in the
	 * {@link EnumString} class.
	 * 
	 * @return String
	 */
	String sGrayOneGRID();

}
