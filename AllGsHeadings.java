package fraclac.writers;

import fraclac.utilities.Symbols;

/**
 * 
 * @version FracLac 2014Jan $Rev: 226 $
 * @version Revision $Id: AllGsHeadings.java 226 2015-03-13 07:15:20Z coder $
 */
public abstract class AllGsHeadings extends Symbols {

	/**
	 * String to signify that the information on this line was from the listed
	 * file and slice at the stated ROI coordinates. {@value}
	 */
	public static final String FILE_INFO_STRING = FracLacV
			+ ": File Slice (ROI) start position";
	/**
   *
   */
	final public static String ELEMENT_SIZE_SIZE = "SIZE OF "
			+ Headings.EnumInfoData.SCAN_ELEMENT;

	/**
	 * "Standard Deviation (&sigma;) for Mass".
	 */
	public static final String STD_DEV_FOR_MASS = String.format(
			"Standard Deviation (%s) for Mass", sigma);

	// ...........................................................................
	// ...................... lambda DIMENSION ............................
	// ...........................................................................
	/**
	 * "(slope[lnsigma vs lnepsilon]) - ((slope[lnlambdaepsilon, vs
	 * lnepsilon])/2)"
	 */
	public static final String LAMBDA_D_FORMULA = "(slope[ln" + sigma
			+ " vs ln" + epsilon + "]) " + "- ((slope[ln" + lambda + epsilon
			+ ", vs ln" + epsilon + "])/2)";

	// ...........................................................................
	// ...................... METHODS ...........................
	// ...........................................................................
	/**
	 * Returns a String that describes how an average was found over all grid
	 * positions. It joins {@link Symbols#SUM} then the passed String then
	 * {@link Symbols#s_At_THIS_GRID_OVER_NUMBER__FOR_EACH_GRID_IN_SET}.
	 * 
	 * <p>
	 * For example, given "A", the returned String is: <br>
	 * &Sum;A\u208d\u0262\u208e\u2215\u01e4\u0274\u200a\u2200\u200a\u0262\u2208
	 * \u01e4
	 * 
	 * @param psValue
	 *            a string for what is being averaged
	 * 
	 * 
	 * @return string {@link Headings#SUM}
	 */
	public static String sumOverG(String psValue) {
		return String.format(SUM + "%s"
				+ s_At_THIS_GRID_OVER_NUMBER__FOR_EACH_GRID_IN_SET, psValue);
	}

}
