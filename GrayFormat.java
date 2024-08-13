package fraclac.writers;

import fraclac.analyzer.Vars;
import ij.IJ;

/**
 * Methods for creating strings describing {@link fraclac.analyzer.GrayCounter
 * grayscale} box counting calculations and results.
 *
 *
 * @author Audrey Karperien
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 239 $
 * @version Revision $Id: GrayFormat.java 239 2015-03-18 08:32:49Z audrey $
 */
public class GrayFormat extends DataStringFormatter
{

  /**
   * Returns a string describing how each sample was measured during box
   * counting.
   * <ul>
   * <li>The string always starts with either "=
   * &delta;&#x026a;&#x208d;&oslash;&#x1d62;&#x208e; | &delta;&#x026a; = " if
   * bAvg is true, or else "=
   * &delta;&#x026a;&#x208d;&#x262;,&oslash;&#x1d62;&#x208e; | &delta;&#x026a; =
   * "
   *
   * <li>The string always ends in the definitions phrase, which describes the
   * variables: "&#x026a;=intensity, i=sample, &oslash;=size"
   *
   * <li>The middle of the string depends on the passed booleans:
   * <ol>
   * <li>if (!bIs2DSquaredGray) it is (&#x026a;max-&#x026a;min+1)
   * <li>if (bIs2DAdd1ToGrayIntensity) it is
   * (&#x026a;max-&#x026a;min+1)&#x00d7;area
   * <li>Otherwise, (i.e., if the scan is 2D but not using the Add1 method), the
   * string is (&#x026a;max-&#x026a;min)&#x00d7;area
   * </ol>
   *
   * </ul>
   *
   *
   * @param piGrayScaleOption
   * @param pbOneGRID
   * @param pbUseOval boolean
   *
   * @return String &oslash;
   */
  public static String stringForIntensityCalculation(
      String piGrayScaleOption,
      boolean pbUseOval,
      boolean pbOneGRID)
  {
    String lsSuchThatDeltaIIs = sSuchThat + delta_I + is;
    String lsdeltaIGridAndSIZEAtI = small_delta_I_at_SIZE_AND_GRID_AND_i
        + lsSuchThatDeltaIIs;
    String lsdeltaISIZEAtI = small_delta_I_at_SIZE_AND_i
        + lsSuchThatDeltaIIs;
    String lsStart = pbOneGRID ? lsdeltaISIZEAtI : lsdeltaIGridAndSIZEAtI;

    String lsDefineIiSIZE = ": " + s_Intensity + "=intensity, i=sample, "
        + sSizesSetMember + "=size";
    String lsDif = s_Intensity + "max" + MINUS + s_Intensity + "min";
    String lsMaxMinusMin = "(" + lsDif + ")";
    String lsMaxMinusMinPlus1 = "(" + lsDif + "+1)";
    String lsMaxMinusMinPlus1XArea = lsMaxMinusMinPlus1 + TIMES + "area";
    String lsMaxMinusMinXArea = lsMaxMinusMin + TIMES + "area";

    if (piGrayScaleOption.equals(SCAN_GRAY_DIFFERENTIAL)) {
      return lsStart + lsMaxMinusMinPlus1 + lsDefineIiSIZE;
    }
    if (piGrayScaleOption.equals(SCAN_GRAY_SQUARED_VOL_VAR_PLUS_1)) {
      return lsStart + lsMaxMinusMinPlus1XArea + lsDefineIiSIZE;
    }
    if (piGrayScaleOption.equals(SCAN_GRAY_SQUARED_VOL_VARIATION)) {
      return lsStart + lsMaxMinusMinXArea + lsDefineIiSIZE;
    }
    IJ.log("NO GRAY SCAN DEFINED" + GrayFormat.class.getSimpleName());
    return "Error - NO GRAY SCAN DEFINED";

  }

  /**
   * Overloaded method calls
   * {@link #stringForIntensityCalculation(boolean, boolean, boolean, boolean)}
   * using
   *
   * <pre>
   * pVars.bIs2DSqGray,
   * pVars.bAdd1Todelta_I,
   * pVars.bUseOvalForInnerSampleNotOuterSubscan,
   * false
   * </pre>
   *
   * @param pVars Vars
   *
   * @return String describing how the value per sample was determined for box
   * counting
   */
  public static String stringForIntensityCalculation(Vars pVars)
  {
    return stringForIntensityCalculation(pVars.getsBinaryOrGrayScanMethod(),
                                         pVars.bUseOvalForInnerSampleNotOuterSubscan,
                                         pVars.isMvsD());
  }

  /**
   * Overloaded method calls
   * {@link #stringForIntensityCalculation(Vars, boolean)} using
   *
   * <pre>
   * Vars pVars,
   * boolean pbFILTERAvgCover
   * </pre>
   *
   * @param pVars Vars
   *
   * @param pbFAvgCover boolean true to return the string for an average cover
   * filter
   *
   * @return String describing how the value per sample was determined for box
   * counting
   */
  public static String stringForIntensityCalculation(Vars pVars,
                                                     boolean pbFAvgCover)
  {
    return stringForIntensityCalculation(pVars.getsBinaryOrGrayScanMethod(),
                                         pVars.bUseOvalForInnerSampleNotOuterSubscan,
                                         pbFAvgCover
                                         || pVars.isMvsD());
  }

  /**
   * Returns a string describing the formula used to calculate the fractal
   * dimension for grayscale scans.
   *
   *
   * @param psGrayScaleOption
   * @see fraclac.analyzer.GrayCounter#graydeltaIThisXYThisSIZE
   * @see #GRAY2D_FRACTAL_DIMENSION_FORMULA
   * @see #GRAY_PLAIN_DIFFERENTIAL_FRACTAL_DIMENSION_FORMULA
   * @see #GRAY2D_PLUS1_FRACTAL_DIMENSION_FORMULA
   * @return string describing fractal dimension formula used
   */
  public static String grayFractalDimensionString(String psGrayScaleOption)
  {
    if (psGrayScaleOption.equals(SCAN_GRAY_SQUARED_VOL_VARIATION))//
    {
      return GRAY2D_FRACTAL_DIMENSION_FORMULA;
    } // .....................................................................
    else if (psGrayScaleOption.equals(SCAN_GRAY_SQUARED_VOL_VAR_PLUS_1)) //
    {
      return GRAY2D_PLUS1_FRACTAL_DIMENSION_FORMULA;
    } // .....................................................................
    else if (psGrayScaleOption.equals(SCAN_GRAY_DIFFERENTIAL)) {
      return GRAY_PLAIN_DIFFERENTIAL_FRACTAL_DIMENSION_FORMULA;
    } else {
      IJ.log("GRAYSCALE TYPE IS UNDEFINED"
          + GrayFormat.class.getSimpleName());
      return "ERROR";
    }
  }

  /**
   * Method calls {@link #grayFractalDimensionString}.
   *
   *
   *
   * @param pVars Vars
   *
   * @return String
   */
  public String stringForGrayFractalDimensionCalculationFormula(Vars pVars)
  {
    return grayFractalDimensionString(pVars.getsBinaryOrGrayScanMethod());
  }

}
