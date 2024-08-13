package fraclac.analyzer;

import fraclac.utilities.DataFilter;
import fraclac.utilities.Statistics;
import fraclac.utilities.Symbols;
import static fraclac.utilities.Utils.fnum;

/**
 * Contains methods for calculating regression statistics on passed arrays and
 * their transformations, and fields for storing results for various types of
 * fractal analysis.
 *
 * <h5>Inputs</h5> Arrays of corresponding numbers suitable for finding
 * regression lines. In the FracLac plugin, the methods are used to find the
 * {@link #dFractalDimension fractal dimension} as the log vs log slope of the
 * regression line for box {@link fraclac.analyzer.Data#d2dCountAtSIZEOnGRID
 * count} or {@link fraclac.analyzer.BoxCount#d2dPixArraysAtSIZEs mass} against
 * sampling element size, {@link fraclac.analyzer.GridSet#i2dSIZEs SIZE}.
 *
 * <h5>Outputs</h5>
 *
 * Regression statistics useful for calculating fractal dimensions, multifractal
 * general dimensions, etc. from box counting data sampling and other data. The
 * stats are the following five values describing the regression line for the
 * original or transformed arrays:&#xa731;
 *
 * <ol>
 * <li>For the {@link #dFractalDimension Fractal Dimension}: slope of the
 * regression line = ((&#x0274;*&Sum;SC) - (&Sum;S*&Sum;C))
 * /((&#x0274;*&Sum;S&sup2;) - (&Sum;S*&Sum;S))
 *
 * <li>r&sup2; correlation coefficient = (&#x0274;*(&Sum;SC) - (&Sum;S*&Sum;C))
 * /&radic;(((&#x0274;*&Sum;S&sup2;) - (&Sum;S*&Sum;S)) ((&#x0274;*&Sum;C&sup2;)
 * - ((&Sum;C*&Sum;C))))
 *
 * <li>y intercept = (&Sum;C - (slope*&Sum;S))/ &#x0274;
 *
 * <li>inverse of prefactor dPrefactor = Euler's e<sup> y-int</sup>
 *
 * <li>and standard error of estimate as &radic; [ (&Sum;C&sup2; - y-int *
 * &Sum;C - slope*&Sum;SC)/ ( &#x0274; - 2 ) ]
 *
 * </ol>
 *
 * The class contains methods to transform the passed arrays using smoothing and
 * minimum cover filters. These filters optimize the data for fractal analysis.
 *
 * <h5>Needs:</h5>
 *
 * <ul>
 * <li>{@link fraclac.utilities.Statistics}</li>
 * </ul>
 *
 * <h5>Structure</h5>
 *
 * Constructors call {@link #getStats getStats method} to initialize variables
 * for fractal analysis but other methods must be called explicitly to return
 * the proper statistics.
 *
 * Transforms input arrays differently depending on the specific methods used.
 * For untransformed data, use the {@link #plainLinearRegression plain linear
 * regression} method or the {@link #logVsLogPowerRegression power regression}
 * method.
 *
 * For {@link fraclac.analyzer.Scan fractal analysis}, use
 * {@link #getFractalDimensionStats Find Df stats method} which uses the log of
 * C and the log of S<sup>-1</sup> for passed arrays C and S.
 *
 * For further information or to send comments, bugs, and feature requests,
 * contact{@link <a href="mailto:akarpe01@postoffice.csu.edu.au"> the
 * author</a>}
 *
 *
 * 2012 Calculator 2006
 *
 * @author Audrey Karperien (akarpe01@postoffice.csu.edu.au), Charles Sturt
 * University
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 244 $
 * @version Revision $Id: Calculator.java 244 2015-04-19 01:20:59Z audrey $
 */
public class Calculator extends Symbols
{
  /*
   * =========================================================================
   * ------------------------------VARIABLES----------------------------------
   * =========================================================================
   */

  // ..........................................................................
  // ..............For Plain Linear
  // Regression.................................
  // ..........................................................................
  /**
   * Slope of the linear regression line. Is changed in {@link #getStats get
   * stats} method.
   */
  public double dPlainRegressSlope;

  /**
   * Correlation coefficient squared or r&sup2; is set in the
   * {@link #getStats get stats} call.
   */
  public double dPlainRegressRSq;

  /**
   * y-intercept is set in the {@link #getStats get stats} call.
   */
  public double dPlainRegressYInt;

  /**
   * Inverse of prefactor. This variable is set in the
   * {@link #plainLinearRegression} method, called within the
   * {@link #getStats get stats} method.
   */
  public double dPlainRegressInverseOfPrefactor;

  /**
   * Standard error set by {@link #plainLinearRegression linear regression} in
   * call to {@link #getStats method} to get statistics.
   */
  public double dPlainRegressStdErr;

  // ..........................................................................
  // ........For fractal dimension regression
  // calculations.....................
  // ..........................................................................
  /**
   * Field for the fractal dimension or negative slope of the log-log regression
   * line for {@link fraclac.analyzer.BoxCount#daCountsAtSIZEs
   * counts} vs {@link fraclac.analyzer.BoxCount#daSIZEs sizes}, calculated in
   * the {@link #getStats getStats} method. Is used in the
   * {@link fraclac.analyzer.DataProcessor DataProcessor} and
   * {@link fraclac.writers.ResultsFilesWriter writers} packages to summarize
   * and display results from {@link fraclac.analyzer.Scan fractal analysis}.
   *
   * @see Symbols#Db
   */
  public double dFractalDimension;

  /**
   * Correlation (r&sup2;) for log-log regression line for the null
   * {@link fraclac.analyzer.Calculator#dFractalDimension fractal dimension},
   * set in {@link #getStats get stats}.
   */
  public double dRSq;

  /**
   * Standard error of log-log regression line for the null
   * {@link fraclac.analyzer.Calculator#dFractalDimension fractal dimension} set
   * in {@link #getStats get stats} call.
   */
  public double dStdErr;

  /**
   * Y-intercept of log-log regression line for the null
   * {@link fraclac.analyzer.Calculator#dFractalDimension fractal dimension},
   * set in {@link #getStats fractal dimension} method.
   */
  public double dYIntercept;

  /**
   * Inverse of prefactor A in AX<sup>D</sup> for the null
   * {@link fraclac.analyzer.Calculator#dFractalDimension fractal dimension} set
   * by {@link #getStats fractal dimension} function, where D is the fractal
   * dimension.
   */
  public double dPrefactor;

  // ..........................................................................
  // ..............Smoothed Smallest and Smoothed Biggest......................
  // ..........................................................................
  /**
   * For {@link #smoothF_SS_ smoothed smallest} fractal dimension statistics.
   */
  public double[] daF_SS_SIZEs, daF_SS_Count, daF_SS_Mean, daF_SS_CvSq,
      daF_SS_CvSqPlus1, daF_SS_StdDev, daF_S_SIZEs, daF_S_Count,
      daF_S_Mean, daF_S_CvSq, daF_S_CvSqPlus1, daF_S_StdDev;

  public double[] daF_SB_SIZEs, daF_SB_Count, daF_SB_Masses[],
      daF_SB_MeanPix, daF_SB_CvSq, daF_SB_CvSqPlus1, daF_SB_StdDev;

  public Statistics daF_SS_SIZEsStats, daF_SB_SIZEsStats,
      daF_S_SIZEsStats;

  /**
   * {@link #smoothF_SB FSB} and {@link #smoothF_SS_ F_SS_} filters.
   */
  public double dDB_F_SB;
  /**
   * Holds fractal dimension for data that has had a smoothing filter
   * applied.See {@link #smoothFsB FSB} and {@link #smoothF_SS_ F_SS_} filters.
   */
  /**
   * {@link #smoothF_SB FSB} and {@link #smoothF_SS_ F_SS_} filters.
   */
  public double dDB_F_SS;
  /**
   * Holds fractal dimension for data that has had a smoothing filter
   * applied.See {@link #smoothFsB FSB} and {@link #smoothF_SS_ F_SS_} filters.
   */
  /**
   * {@link #smoothF_SB FSB} and {@link #smoothF_SS_ F_SS_} filters.
   */
  public double dDB_F_S;

  /**
   * {@link #smoothF_SB bigger smoothing} and {@link #smoothF_SS_ smaller
   * smoothing} filters.
   */
  public double dYintForDB_F_SB;
  /**
   * y-intercept for smoothed regression lines.Set in {@link #smoothF_SB
   * bigger smoothing} and {@link #smoothF_SS_ smaller smoothing} filters.
   */
  public double dYintForDB_F_SS;
  /**
   * y-intercept for smoothed regression lines.Set in {@link #smoothF_SB
   * bigger smoothing} and {@link #smoothF_SS_ smaller smoothing} filters.
   */
  public double dYintForDB_F_S;

  /**
   * { @link#smoothF_SB bigger smoothing} and {@link #smoothF_SS_ smaller
   * smoothing} filters.
   */
  public double dPrefactorDB_F_SB;
  /**
   * Inverse of prefactor A in AX<sup>D</sup>, where D is the fractal dimension,
   * for data that has been filtered with a smoothing filter.Set in
   * {@link #smoothF_SB bigger smoothing} and {@link #smoothF_SS_ smaller
   * smoothing} filters.
   */
  public double dPrefactorForDB_F_SS;
  /**
   * Inverse of prefactor A in AX<sup>D</sup>, where D is the fractal dimension,
   * for data that has been filtered with a smoothing filter.Set in
   * {@link #smoothF_SB bigger smoothing} and {@link #smoothF_SS_ smaller
   * smoothing} filters.
   */
  public double dPrefactorForDB_F_S;

  public double dRSqDB_F_SB;
  /**
   * Squared correlation coefficient (r&sup2;> for regression line for smoothing
   * filters.Set in {@link #smoothF_SB bigger smoothing} and
   * {@link #smoothF_SS_ smaller smoothing} filters.
   */
  public double dRSqForDB_F_SS;
  /**
   * Squared correlation coefficient (r&sup2;> for regression line for smoothing
   * filters.Set in {@link #smoothF_SB bigger smoothing} and
   * {@link #smoothF_SS_ smaller smoothing} filters.
   */
  public double dRSqForDB_F_S;

  public double dSEForDB_F_SS;
  /**
   * Standard error of estimate for smoothing filters.Set in
   * {@link #smoothF_SB bigger smoothing} and {@link #smoothF_SS_ smaller
   * smoothing} filters.
   */
  public double dSEForDB_F_SB;
  /**
   * Standard error of estimate for smoothing filters.Set in
   * {@link #smoothF_SB bigger smoothing} and {@link #smoothF_SS_ smaller
   * smoothing} filters.
   */
  public double dSEForDB_F_S;

  // ..........................................................................
  // ..............Inverse Linear Regression Stats.............................
  // ..........................................................................
  /**
   * Slope of for {@link #invSizeLinRegress inverse linear regression} line set
   * in {@link #getStats get stats} call.
   */
  public double dInvLinRegressSlope;

  /**
   * Y-intercept of {@link #invSizeLinRegress inverse linear regression} line
   * set in {@link #getStats get stats} call.
   */
  public double dInvLinRegressYInt;

  /**
   * Correlation (r&sup2;) for {@link #invSizeLinRegress inverse linear
   * regression} line set in {@link #getStats get stats} call.
   */
  public double dInvLinRegressRSq;

  /**
   * Yint inverse parameter for {@link #invSizeLinRegress inverse linear
   * regression} line.
   */
  public double dInvLinRegPrefactorF;

  /**
   * Standard error of linear regression line set in {@link #getStats get
   * stats} call.
   */
  public double dInvLinRegressStdErr;

  // ======================================================================
  // ------------------------CONSTRUCTORS----------------------------------
  // ======================================================================
  /**
   * Constructor does nothing. See below for constructors that accept parameters
   * and calculate statistics.
   *
   *
   * @see #Calculator(double[], double[], int)
   *
   */
  public Calculator()
  {
  }

  /**
   * Constructor accepts arrays and calls {@link #getStats getStats} method.
   *
   *
   * @param pdaCount double [] corresponds to counts for fractal analysis
   * @param pdaSizes double [] corresponds to sizes for fractal analysis
   * @param piNumElementsToUse int for number of values to use in passed arrays
   */
  public Calculator(double[] pdaCount,
                    double[] pdaSizes,
                    int piNumElementsToUse)
  {
    double[] daAdjustedCount = new double[piNumElementsToUse];
    double[] daAdjustedSize = new double[piNumElementsToUse];
    for (int liIndexInArrays = 0;
        liIndexInArrays < piNumElementsToUse; liIndexInArrays++) {
      daAdjustedCount[liIndexInArrays] = pdaCount[liIndexInArrays];
      daAdjustedSize[liIndexInArrays] = pdaSizes[liIndexInArrays];
    }
    getStats(daAdjustedCount,
             daAdjustedSize,
             piNumElementsToUse);

  }

  /**
   * Constructor calls {@link #getStats} passing in the passed double [] and the
   * length of pdaSizes. This call automatically calculates the fractal
   * dimension and other stats.
   *
   * @param pdaCount
   * @param pdaSizes
   */
  public Calculator(double[] pdaCount,
                    double[] pdaSizes)
  {

    getStats(pdaCount,
             pdaSizes,
             pdaSizes.length);

  }

  /**
   * Constructor converts int array to double array then calls
   * {@link #getStats}.
   *
   * @param pdaCount
   * @param piaSizes
   */
  public Calculator(double[] pdaCount,
                    int[] piaSizes)
  {

    getStats(pdaCount,
             intToDoubleArray(piaSizes),
             piaSizes.length);

  }

  /*
   * =========================================================================
   * -----------------------------METHODS-------------------------------------
   * =========================================================================
   */
  /**
   * Returns the slope of the log log regression line for the passed arrays.
   *
   * @param pdaCounts double [] for counts
   * @param pdaSizes double [] for sizes
   *
   * @return double for slope
   */
  public double slopeOfPowerRegress(double[] pdaCounts,
                                    double[] pdaSizes)
  {
    double[] ldaLogLogStats = logVsLogPowerRegression(pdaCounts,
                                                      pdaSizes,
                                                      pdaCounts.length);
    return ldaLogLogStats[SLOPE_INDEX];
  }

  /**
   * Calculates stats but returns nothing so individual variables must be
   * accessed to get the stats. Is automatically called by constructors but must
   * be called again for any changes. This method calls a few functions to
   * calculate many types of statistics. If the only desired output is a set of
   * fractal dimension statistics for a log-log regression line, it may be more
   * appropriate to use {@link #getFractalDimensionStats
   * getFractalDimensionStats} instead.
   * <ol>
   * <li>Calls {@link #getFractalDimensionStats getFractalDimensionStats} on the
   * passed arrays and sets:
   * <ul>
   * <li> {@link #dFractalDimension}
   * <li> {@link #dRSq}
   * <li> {@link #dYIntercept},
   * <li> {@link #dPrefactor},
   * <li> {@link #dStdErr},
   * </ul>
   * </li>
   * <li>Then calls {@link #invSizeLinRegress inverse LinearRegression} method
   * and sets:
   * <ul>
   * <li>{@link #dInvLinRegressSlope}
   * <li>{@link #dInvLinRegressRSq}
   * <li>{@link #dInvLinRegressYInt}
   * <li>{@link #dInvLinRegPrefactorF}
   * <li>{@link #dInvLinRegressStdErr}
   * </ul>
   * </li>
   *
   * <li>Then calls {@link #plainLinearRegression plain} linear regression on
   * the passed arrays and sets:
   * <ul>
   * <li>{@link #dPlainRegressSlope}
   * <li>{@link #dPlainRegressRSq}
   * <li>{@link #dPlainRegressYInt}
   * <li>{@link #dPlainRegressInverseOfPrefactor}
   * <li>{@link #dPlainRegressStdErr}
   * </ul>
   * </li>
   * </ol>
   *
   *
   * @param pdaCounts double array of counts
   * @param pdaSIZEs integer array of sizes
   * @param piNumElementsToUse integer for number of values to use
   */
  public final void getStats(double[] pdaCounts,
                             double[] pdaSIZEs,
                             int piNumElementsToUse)
  {
    double[] ldaRegularDfStats = getFractalDimensionStats(pdaCounts,
                                                          pdaSIZEs,
                                                          piNumElementsToUse);

    dFractalDimension = ldaRegularDfStats[FRACTAL_DIMENSION_INDEX];
    dRSq = ldaRegularDfStats[R_SQ_INDEX];
    dYIntercept = ldaRegularDfStats[Y_INT_INDEX];
    dPrefactor = ldaRegularDfStats[PREFACTOR_INDEX];
    dStdErr = ldaRegularDfStats[STD_ERR_INDEX];

    double[] ldaLinearRegressionStats = invSizeLinRegress(pdaCounts,
                                                          pdaSIZEs,
                                                          piNumElementsToUse);

    dInvLinRegressSlope = ldaLinearRegressionStats[FRACTAL_DIMENSION_INDEX];
    dInvLinRegressRSq = ldaLinearRegressionStats[R_SQ_INDEX];
    dInvLinRegressYInt = ldaLinearRegressionStats[Y_INT_INDEX];
    dInvLinRegPrefactorF = ldaLinearRegressionStats[PREFACTOR_INDEX];
    dInvLinRegressStdErr = ldaLinearRegressionStats[STD_ERR_INDEX];

    double[] ldaRegularStats = plainLinearRegression(pdaCounts,
                                                     pdaSIZEs,
                                                     piNumElementsToUse);

    dPlainRegressSlope = ldaRegularStats[FRACTAL_DIMENSION_INDEX];
    dPlainRegressRSq = ldaRegularStats[R_SQ_INDEX];
    dPlainRegressYInt = ldaRegularStats[Y_INT_INDEX];
    dPlainRegressInverseOfPrefactor = ldaRegularStats[PREFACTOR_INDEX];
    dPlainRegressStdErr = ldaRegularStats[STD_ERR_INDEX];
  }

  /**
   * Filters the passed arrays using a smoothing
   * {@link fraclac.utilities.DataFilter FILTERss filter} and returns a double
   * array of statistics about the resultant arrays.
   * <p>
   * The algorithm's point is to optimize box counting data by removing areas of
   * horizontal slope for the count of samples having any mass versus SIZE, by
   * retaining from all intervals where count does not change, only the
   * corresponding smallest value for SIZE.
   *
   * Thus, the value in SIZE at the index that marks the start of the interval
   * is retained and the rest of the pairs of masses and SIZEs are discarded
   * over that interval.
   * <p>
   *
   * Returns an array of {@link #getFractalDimensionStats stats} based on the
   * smoothed set of arrays.
   *
   * The filter assumes that increases should be ignored, assuming that the
   * smallest possible box at a count holds density most efficiently and that
   * that is desirable. The function also assumes that the SIZEs are ordered
   * from smallest at index=0 to largest at index=length-1;
   *
   *
   * @param pdaSIZEs int [Os] to assess in calculating the regression line
   *
   * @param pd2dMasses double [SIZEs][masses] masses (e.g., pixels per SIZE)
   * @param pbLegacy
   *
   * @return double array from {@link #getFractalDimensionStats
   *         getFractalDimensionStats method} accessible using
   * FRACTAL_DIMENSION_INDEX=0, R_SQ_INDEX=1, Y_INT_INDEX=2, PREFACTOR_INDEX=3,
   * STDfERR_INDEX=4
   */
  public double[] smoothF_SS_(double[] pdaSIZEs,
                              double[][] pd2dMasses,
                              boolean pbLegacy)
  {
    BoxCount lbc = DataFilter.smoothFilter(pdaSIZEs,
                                           pd2dMasses,
                                           true,
                                           false,
                                           pbLegacy);

    // .........................................................................
    // ...... Initialize storage arrays to size of filtered data. ............
    // .........................................................................
    {
      int liSmLength = lbc.daSIZEs.length;
      daF_SS_SIZEs = new double[liSmLength];
      daF_SS_Count = new double[liSmLength];
      daF_SS_Mean = new double[liSmLength];
      daF_SS_StdDev = new double[liSmLength];
      daF_SS_CvSq = new double[liSmLength];
      daF_SS_CvSqPlus1 = new double[liSmLength];

      for (int liSIZE = 0; liSIZE < liSmLength; liSIZE++) {

        daF_SS_SIZEs[liSIZE] = lbc.daSIZEs[liSIZE];
        daF_SS_Count[liSIZE] = lbc.daCountsAtSIZEs[liSIZE];

        Statistics lstats = new Statistics(
            lbc.d2dPixArraysAtSIZEs[liSIZE],
            lbc.d2dPixArraysAtSIZEs[liSIZE].length,
            "");
        daF_SS_Mean[liSIZE] = lstats.dMean;
        daF_SS_StdDev[liSIZE] = lstats.dStdDev;
        daF_SS_CvSq[liSIZE] = lstats.dCVSq;
        daF_SS_CvSqPlus1[liSIZE] = lstats.dCVSq + 1.0f;
      }

      double[] ldaStats = getFractalDimensionStats(daF_SS_Count,
                                                   daF_SS_SIZEs,
                                                   liSmLength);

      dDB_F_SS = ldaStats[FRACTAL_DIMENSION_INDEX];
      dRSqForDB_F_SS = ldaStats[R_SQ_INDEX];
      dSEForDB_F_SS = ldaStats[STD_ERR_INDEX];
      dPrefactorForDB_F_SS = ldaStats[PREFACTOR_INDEX];
      dYintForDB_F_SS = ldaStats[Y_INT_INDEX];

      daF_SS_SIZEsStats = new Statistics(daF_SS_SIZEs,
                                         s_daF_SS_SIZEsStats);

      return ldaStats;
    }
  }

  /**
   *
   * @param pdaSIZEs
   * @param pd2dMasses
   * @param pbLegacy
   * @return
   */
  public double[] smoothF_S(double[] pdaSIZEs,
                            double[][] pd2dMasses,
                            boolean pbLegacy)
  {
    BoxCount lbch = DataFilter.smoothFilter(pdaSIZEs,
                                            pd2dMasses,
                                            true,
                                            true,
                                            pbLegacy);

    // .........................................................................
    // ...... Initialize storage arrays to size of filtered data. ............
    // .........................................................................
    {
      int liSmLength = lbch.daSIZEs.length;
      daF_S_SIZEs = new double[liSmLength];
      daF_S_Count = new double[liSmLength];
      daF_S_Mean = new double[liSmLength];
      daF_S_StdDev = new double[liSmLength];
      daF_S_CvSq = new double[liSmLength];
      daF_S_CvSqPlus1 = new double[liSmLength];

      for (int liSIZE = 0; liSIZE < liSmLength; liSIZE++) {

        daF_S_SIZEs[liSIZE] = lbch.daSIZEs[liSIZE];
        daF_S_Count[liSIZE] = lbch.daCountsAtSIZEs[liSIZE];

        Statistics lstats = new Statistics(
            lbch.d2dPixArraysAtSIZEs[liSIZE],
            lbch.d2dPixArraysAtSIZEs[liSIZE].length,
            "");
        daF_S_Mean[liSIZE] = lstats.dMean;
        daF_S_StdDev[liSIZE] = lstats.dStdDev;
        daF_S_CvSq[liSIZE] = lstats.dCVSq;
        daF_S_CvSqPlus1[liSIZE] = lstats.dCVSq + 1.0f;
      }

      double[] ldaStats = getFractalDimensionStats(daF_S_Count,
                                                   daF_S_SIZEs,
                                                   liSmLength);

      dDB_F_S = ldaStats[FRACTAL_DIMENSION_INDEX];
      dRSqForDB_F_S = ldaStats[R_SQ_INDEX];
      dSEForDB_F_S = ldaStats[STD_ERR_INDEX];
      dPrefactorForDB_F_S = ldaStats[PREFACTOR_INDEX];
      dYintForDB_F_S = ldaStats[Y_INT_INDEX];

      daF_S_SIZEsStats = new Statistics(daF_S_SIZEs,
                                        s_daF_S_SIZEsStats);

      return ldaStats;
    }
  }

  /**
   * Returns an array of and locally stores statistics for the passed data after
   * applying a {@link DataFilter#smoothFilter smoothing (biggest)} filter.
   *
   * Calculates and stores values for the following:
   * <ul>
   * <li>Smoothed Biggest {@link #daF_SB_SIZEs Size}
   * <li>Smoothed Biggest {@link #daF_SB_Count Count}
   * <li>Smoothed Biggest {@link #daF_SB_MeanPix Mean}
   * <li>Smoothed Biggest {@link #daF_SB_StdDev Standard Deviation}
   * <li>Smoothed Biggest {@link #daF_SB_CvSq Coefficient of Variation
   * Squared}
   * <li>Smoothed Biggest {@link #daF_SB_CvSqPlus1 Lacunarity}
   * </ul>
   *
   * <h5>Storage of Results</h5> Once the filtered array is made, fractal
   * dimension statistics are calculated by calling
   * {@link #getFractalDimensionStats}.
   *
   * The results are stored in the following local arrays:
   * <ul>
   * <li>{@link #dDB_F_SB}
   * <li>{@link #dRSqDB_F_SB}
   * <li>{@link #dSEForDB_F_SB}
   * <li>{@link #dPrefactorDB_F_SB}
   * <li>{@link #dYintForDB_F_SB}
   * </ul>
   *
   * @param pdaSIZEs double [SIZE] of samplig element sizes
   * @param pd2dMasses double [SIZE][masses] of masses per sample at each SIZE
   * @param pbLegacy
   *
   * @return double array of stats; access it using
   * <ul>
   * <li>
   * Slope or Fractal Dimension = result[
   * {@value fraclac.utilities.Symbols#FRACTAL_DIMENSION_INDEX}]
   * <li>
   * r&sup2; = result[{@value fraclac.utilities.Symbols#R_SQ_INDEX}]
   * <li>Standard error = result [
   * {@value fraclac.utilities.Symbols#STD_ERR_INDEX}]
   * <li>
   * Prefactor = result[ {@value fraclac.utilities.Symbols#PREFACTOR_INDEX}]
   * <li>
   * Y-intercept = result[ {@value fraclac.utilities.Symbols#Y_INT_INDEX}].
   * </ul>
   * Note: these values are also stored locally as explained in the method
   * description.
   */
  public double[] smoothF_SB(double[] pdaSIZEs,
                             double[][] pd2dMasses,
                             boolean pbLegacy)
  {

    BoxCount lbchFsB = DataFilter.smoothFilter(pdaSIZEs,
                                               pd2dMasses,
                                               false,
                                               false,
                                               pbLegacy);

    int liNewNumElements = lbchFsB.daSIZEs.length;

    initializeFsBArrays(liNewNumElements);
    transfer:
    { // .......................................................................
      // .......... Transfer the smoothed results to local variables. ..........
      // .......................................................................
      for (int lSIZE = 0; lSIZE < liNewNumElements; lSIZE++) {

        daF_SB_SIZEs[lSIZE] = lbchFsB.daSIZEs[lSIZE];
        daF_SB_Count[lSIZE] = lbchFsB.daCountsAtSIZEs[lSIZE];
        daF_SB_Masses[lSIZE]
            = new double[lbchFsB.d2dPixArraysAtSIZEs[lSIZE].length];

        System.arraycopy(lbchFsB.d2dPixArraysAtSIZEs[lSIZE],
                         0,
                         daF_SB_Masses[lSIZE],
                         0,
                         lbchFsB.d2dPixArraysAtSIZEs[lSIZE].length);

        Statistics lstatsMasses = new Statistics(
            lbchFsB.d2dPixArraysAtSIZEs[lSIZE],
            lbchFsB.d2dPixArraysAtSIZEs[lSIZE].length,
            "");

        daF_SB_MeanPix[lSIZE] = lstatsMasses.dMean;
        daF_SB_StdDev[lSIZE] = lstatsMasses.dStdDev;
        daF_SB_CvSq[lSIZE] = lstatsMasses.dCVSq;
        daF_SB_CvSqPlus1[lSIZE] = lstatsMasses.dCVSq + 1.0f;
      }
    }
    // .........................................................................
    // ....... Get fractal dimension and stats and both record them
    // .............
    // ....... locally and return them in an array.
    // .............................
    // .........................................................................
    double[] ldaSmoothedStats = getFractalDimensionStats(daF_SB_Count,
                                                         daF_SB_SIZEs,
                                                         daF_SB_SIZEs.length);

    dDB_F_SB = ldaSmoothedStats[FRACTAL_DIMENSION_INDEX];
    dRSqDB_F_SB = ldaSmoothedStats[R_SQ_INDEX];
    dSEForDB_F_SB = ldaSmoothedStats[STD_ERR_INDEX];
    dPrefactorDB_F_SB = ldaSmoothedStats[PREFACTOR_INDEX];
    dYintForDB_F_SB = ldaSmoothedStats[Y_INT_INDEX];

    daF_SB_SIZEsStats = new Statistics(daF_SB_SIZEs,
                                       "daF_SB_SIZEsStats");

    return ldaSmoothedStats;
  }

  /**
   *
   * @param piNumElements
   */
  void initializeFsBArrays(int piNumElements)
  {
    daF_SB_SIZEs = new double[piNumElements];
    daF_SB_Count = new double[piNumElements];
    daF_SB_MeanPix = new double[piNumElements];
    daF_SB_StdDev = new double[piNumElements];
    daF_SB_CvSq = new double[piNumElements];
    daF_SB_CvSqPlus1 = new double[piNumElements];
    daF_SB_Masses = new double[piNumElements][];
  }

  /**
   * Calculates the log-log plot of Counts and Sizes/Biggest Size. See
   * {@link #getFractalDimensionStats getFractalDimensionStats} for further
   * calculations.
   *
   *
   * @param pdaCounts double array
   * @param pdaSizes integer array
   * @param piNumElementsToUse integer for number of values to use
   *
   *
   * @return double array with 5 {@link #getStats statistics} accessible using
   * FRACTAL_DIMENSION_INDEX=0, R_SQ_INDEX=1, Y_INT_INDEX=2, PREFACTOR_INDEX=3,
   * STDfERR_INDEX=4
   */
  public double[] ratioFindDfStats(double[] pdaCounts,
                                   double[] pdaSizes,
                                   int piNumElementsToUse)
  {
    double ldNum = (double) piNumElementsToUse;
    double ldBiggest = pdaSizes[0];

    for (int q = 0; q < piNumElementsToUse; q++) {

      ldBiggest = Math.max(pdaSizes[q],
                           ldBiggest);
    }
    double ldSumSC = 0, ldSumS = 0, ldSumC = 0, ldSumSSq = 0, ldSumCSq = 0;

    for (int q = 0; q < piNumElementsToUse; q++) {/*
       * go through array and dSum
       * values for regression
       * line
       */

      // ///SUM THE LOG OF THE COUNT*THE LOG OF THE SIZE\
      // //use the scale factor, calculated as the
      // //box size divided by the largest size
      // That is, the maximum size is treated as 1 now
      ldSumSC += Math.log(pdaSizes[q] / ldBiggest)
          * Math.log(pdaCounts[q]);
      // /SUM THE LOG OF THE SIZE
      ldSumS += Math.log(pdaSizes[q] / ldBiggest);
      // SUM THE LOG OF THE COUNTS
      ldSumC += Math.log(pdaCounts[q]);
      // SUM THE LOG OF COUNTS TIMES LOG OF COUNTS
      ldSumCSq += Math.log(pdaCounts[q]) * Math.log(pdaCounts[q]);
      // SUM THE LOG OF SIZE TIMES LOG SIZE
      ldSumSSq += Math.log(pdaSizes[q] / ldBiggest)
          * Math.log(pdaSizes[q] / ldBiggest);
    }
    return calculateStats(ldSumSC,
                          ldSumS,
                          ldSumC,
                          ldSumSSq,
                          ldSumCSq,
                          ldNum);
  }

  /**
   * Returns an array of doubles from a call to {@link #calculateStats}.
   *
   * <h5>Typical Use</h5> When called in fractal analysis, returns the fractal
   * dimension and related statistics from box counting data. The passed arrays
   * are usually the number or
   * {@link fraclac.analyzer.Scan#measureThisSpot count} of sampling elements at
   * each size and the corresponding array of sizes (
   * {@link fraclac.analyzer.GridSet#i2dSIZEs SIZE}) used to efficiently cover a
   * pattern being analyzed.
   *
   * The returned array holds power law {@link #getStats regression} statistics
   * typically used in fractal analysis, calculated from the log of the passed
   * Counts array and the log of the inverse of the SIZEs area (i.e., 1/SIZE).
   * Thus, getFractalDimensionStats sums and transforms the data and passes the
   * {@link #getStats} method the following values:
   * <ul>
   * <li>&Sum;SC = &Sum; (ln (Sizes[q]<sup>-1</sup>)*ln Counts[q])</li>
   *
   * <li>&Sum;S = &Sum;ln (Sizes[q]<sup>-1</sup>)</li>
   * <li>&Sum;C = &Sum;ln Counts[q]</li>
   *
   * <li>&Sum;C&sup2; = &Sum;ln (Counts[q]&sup2;)</li>
   *
   * <li>&Sum;S&sup2; = &Sum;(ln (Sizes[q]<sup>-1</sup>)&sup2;)</li>
   * </ul>
   *
   *
   * @param pdaCounts array of counts
   * @param pdaSIZEs array of sampling element sizes; 1/SIZE[i] is used for the
   * calculations
   * @param piNumElementsToUse an integer for the number of values to use from
   * the passed arrays
   *
   *
   * @return double array with 5 {@link #getStats statistics} accessible using
   * FRACTAL_DIMENSION_INDEX=0, R_SQ_INDEX=1, Y_INT_INDEX=2, PREFACTOR_INDEX=3,
   * STDfERR_INDEX=4
   */
  public double[] getFractalDimensionStats(final double[] pdaCounts,
                                           final double[] pdaSIZEs,
                                           final int piNumElementsToUse)
  {

    double ldSumLogInvSizeXLogCount = 0,
        ldSumLogInvSIZE = 0,
        ldSumLogCount = 0,
        ldSumLogInvSIZEXLogInvSIZE = 0,
        ldSumLogCountXLogCount = 0;

    for (int liIndex = 0; liIndex < piNumElementsToUse; liIndex++) {

      double ldSIZE = (pdaSIZEs[liIndex]);
      double ldC = (pdaCounts[liIndex]);

      ldSumLogInvSizeXLogCount += Math.log(1.0f / ldSIZE) * Math.log(ldC);

      ldSumLogInvSIZE += Math.log(1.0f / ldSIZE);

      ldSumLogCount += Math.log(ldC);

      ldSumLogCountXLogCount += (Math.log(ldC) * Math.log(ldC));

      ldSumLogInvSIZEXLogInvSIZE += (Math.log(1.0f / ldSIZE)
          * Math.log(1.0f / ldSIZE));
    }

    return calculateStats(ldSumLogInvSizeXLogCount,
                          ldSumLogInvSIZE,
                          ldSumLogCount,
                          ldSumLogInvSIZEXLogInvSIZE,
                          ldSumLogCountXLogCount,
                          (double) piNumElementsToUse);

  }

  /**
   * Returns the slope of the {@link #logVsLogPowerRegression power law
   * regression} line from the log of Y versus the log of X. <li>
   * y=Ax<sup>slope</sup></li>
   *
   *
   * @param pdaY double [] of y-values for the regression
   * @param pdaX double [] of x-values for the regression
   * @param piNumSizes int for the number of entries to use in the passed X and
   * Y
   *
   *
   * @return double Slope of the regression line
   */
  public double slopeOfPowerRegression(final double[] pdaY,
                                       final double[] pdaX,
                                       final int piNumSizes)
  {
    double[] lV = logVsLogPowerRegression(pdaY,
                                          pdaX,
                                          piNumSizes);
    return lV[0];
  }

  /**
   *
   * @param pdaY
   * @param pdaX
   * @return
   */
  public double slopeOfPowerRegression(double[] pdaY,
                                       double[] pdaX)
  {
    double[] V = logVsLogPowerRegression(pdaY,
                                         pdaX,
                                         pdaX.length);
    return V[0];
  }

  /**
   * Returns the y-intercept of the {@link #logVsLogPowerRegression power law
   * regression} line from the log of Y versus the log of X
   *
   *
   * @param pdaY double [] of y-values for the regression
   * @param pdaX double [] of x-values for the regression
   * @param piNumSizes int for the number of entries to use in the passed X and
   * Y
   *
   *
   * @return double Y-intercept for the power regression
   */
  public double yInterceptOfPowerRegression(double[] pdaY,
                                            double[] pdaX,
                                            int piNumSizes)
  {
    double[] V = logVsLogPowerRegression(pdaY,
                                         pdaX,
                                         piNumSizes);
    return V[2];
  }

  /**
   * Returns the correlation of the {@link #logVsLogPowerRegression power law
   * regression} line from the log of Y versus the log of X.
   *
   *
   * @param pdaY double [] of y-values for the regression
   * @param pdaX double [] of x-values for the regression
   * @param piNumSizes int for the number of entries to use in the passed X and
   * Y
   *
   *
   * @return double r&sup2; correlation coefficient
   */
  public double rSquaredOfPowerRegression(double[] pdaY,
                                          double[] pdaX,
                                          int piNumSizes)
  {
    double[] V = logVsLogPowerRegression(pdaY,
                                         pdaX,
                                         piNumSizes);
    return V[1];
  }

  /**
   * Returns the Standard Error of the {@link #logVsLogPowerRegression power
   * law regression} line from the log of Y versus the log of X
   *
   *
   * @param pdaY double [] of y-values for the regression
   * @param pdaX double [] of x-values for the regression
   * @param piNumSizes int for the number of entries to use in the passed X and
   * Y
   *
   *
   * @return double standard error for regression line
   */
  public double stdErrOfPowerRegression(double[] pdaY,
                                        double[] pdaX,
                                        int piNumSizes)
  {
    double[] V = logVsLogPowerRegression(pdaY,
                                         pdaX,
                                         piNumSizes);
    return V[4];
  }

  /**
   * Returns the prefactor A=Euler's e^y-intercept of the
   * {@link #logVsLogPowerRegression power law regression} line from the log of
   * Y versus the log of X. <li>Note that A=<i>e</i><sup>y-intercept</sup>
   * and y=Ax<sup>slope</sup>
   *
   *
   * @param pdaY double [] of y-values for the regression
   * @param pdaX double [] of x-values for the regression
   * @param piNumElements int for the number of entries to use in the passed X
   * and Y
   *
   *
   * @return double PreFactor A for the regression line (y=Ax<sup>slope</sup>
   */
  public double prefactorOfPowerRegression(double[] pdaY,
                                           double[] pdaX,
                                           int piNumElements)
  {
    double[] ldaV = logVsLogPowerRegression(pdaY,
                                            pdaX,
                                            piNumElements);
    return ldaV[3];
  }

  /**
   * Returns {@link #getStats stats} array of power law regression data, using
   * log of Counts (Y) array and log of Sizes (X) array.
   *
   *
   * @param pdaCounts array of floats
   * @param pdaSizes array of integers
   * @param piNumSizes an integer for the number of values to use from the
   * passed arrays
   *
   *
   * @return double array with 5 {@link #getStats statistics} 0=slope, 1=r
   * squared, 2=y intercept, 3=prefactor, 4=dStdErr accessible using
   * FRACTAL_DIMENSION_INDEX=0, R_SQ_INDEX=1, Y_INT_INDEX=2, PREFACTOR_INDEX=3,
   * STDfERR_INDEX=4.
   */
  public double[] logVsLogPowerRegression(final double[] pdaCounts,
                                          final double[] pdaSizes,
                                          final int piNumSizes)
  {
    // takes an array for box sizes and an array for Counts and
    // the number of box sizes to check and calculates stats
    // using those arrays

    double ldN = (double) piNumSizes;
    double ldSumSC = 0, ldSumS = 0, ldSumC = 0, ldSumSSq = 0, ldSumCSq = 0;
    for (int q = 0; q < piNumSizes; q++) {/*
       * go through array and dSum values
       * for regression line
       */

      // SUM THE LOG OF THE COUNT*THE LOG OF THE SIZE
      ldSumSC += (Math.log(pdaSizes[q]) * Math.log(pdaCounts[q]));
      // SUM THE LOG OF THE SIZE
      ldSumS += Math.log(pdaSizes[q]);
      // SUM THE LOG OF THE COUNTS
      ldSumC += Math.log(pdaCounts[q]);
      // SUM THE LOG OF COUNTS TIMES LOG OF COUNTS
      ldSumCSq += Math.log(pdaCounts[q]) * Math.log(pdaCounts[q]);
      // SUM THE LOG OF SIZE TIMES LOG SIZE
      ldSumSSq += Math.log(pdaSizes[q]) * Math.log(pdaSizes[q]);
    }
    return calculateStats(ldSumSC,
                          ldSumS,
                          ldSumC,
                          ldSumSSq,
                          ldSumCSq,
                          ldN);
  }

  /**
   * Converts to log base 10.
   *
   *
   * @param pfNumber float to convert
   *
   *
   * @return float log base 10 of number
   */
  public float log10(float pfNumber)
  {
    return ((float) Math.log(pfNumber) / (float) Math.log(10.0f));
  }

  /**
   * Converts to log base 10.
   *
   *
   * @param piNumber int to convert
   *
   *
   * @return float log base 10 of number
   */
  public float log10(int piNumber)
  {
    return (float) Math.log((float) piNumber) / (float) Math.log(10.0f);
  }

  /**
   * Converts to log base 10.
   *
   *
   * @param pdNumber float to convert
   *
   *
   * @return float log base 10 of number
   */
  public float log10(double pdNumber)
  {
    return (float) Math.log((float) pdNumber) / (float) Math.log(10.0f);
  }

  /**
   * Returns array of statistics. Operates on the inverse of Sizes array.
   *
   *
   * @param pdaCounts float array for x axis (e.g., number of boxes that
   * contained pixels in fractal analysis)
   * @param pdaSizes float array for y axis (e.g., the corresponding array of
   * box sizes)
   * @param piNumSizes integer for the number of elements to use in both arrays
   *
   *
   * @return float array with 5 {@link #getStats statistics} accessible using
   * FRACTAL_DIMENSION_INDEX=0, R_SQ_INDEX=1, Y_INT_INDEX=2, PREFACTOR_INDEX=3,
   * STDfERR_INDEX=4
   */
  public double[] invSizeLinRegress(double[] pdaCounts,
                                    double[] pdaSizes,
                                    int piNumSizes)
  {
    double ldNum = (double) piNumSizes;
    double ldSumSC = 0, ldSumS = 0, ldSumC = 0, ldSumSSq = 0, ldSumCSq = 0;
    for (int liQ = 0; liQ < piNumSizes; liQ++) {
      // go through array and Sum values for regression line

      // ///SUM THE COUNT*THE SIZE
      ldSumSC += ((1.0d / pdaSizes[liQ]) * pdaCounts[liQ]);
      // /SUM THE SIZE
      ldSumS += (1.0d / pdaSizes[liQ]);
      // SUM THE COUNTS
      ldSumC += pdaCounts[liQ];
      // SUM THE COUNTS TIMES COUNTS
      ldSumCSq += ((pdaCounts[liQ]) * (pdaCounts[liQ]));
      // SUM THE SIZE TIMES SIZE
      ldSumSSq += ((1.0d / pdaSizes[liQ]) * (1.0d / pdaSizes[liQ]));
    }
    return calculateStats(ldSumSC,
                          ldSumS,
                          ldSumC,
                          ldSumSSq,
                          ldSumCSq,
                          ldNum);
  }

  /**
   * Returns array of regression statistics from the passed arrays. Also sets
   * internal fields for PlainLinearStats to the calculated values.
   *
   * <pre>
   *
   * For a results array called ldaRegularStats, it sets:
   *
   * dPlainRegressSlope = ldaRegularStats[FRACTAL_DIMENSION_INDEX];
   * dPlainRegressRSq = ldaRegularStats[R_SQ_INDEX];
   * dPlainRegressYInt = ldaRegularStats[Y_INT_INDEX];
   * dPlainRegressInverseOfPrefactor = ldaRegularStats[PREFACTOR_INDEX];
   * dPlainRegressStdErr = ldaRegularStats[STDfERR_INDEX];
   * </pre>
   *
   *
   * @param pdaY float array for x axis (e.g., count of boxes that contained
   * pixels in fractal analysis)
   * @param pdaX float array for y axis (e.g., the corresponding array of
   * sampling element sizes)
   * @param piNumElements integer for the number of elements to use in both
   * arrays
   *
   *
   * @return float array with 5 {@link #getStats statistics} for the linear
   * regression line for the passed arrays, accessible using
   * <ul>
   * <li>FRACTAL_DIMENSION or SLOPE_INDEX =
   * {@value fraclac.utilities.Symbols#FRACTAL_DIMENSION_INDEX}
   * <li>R_SQ_INDEX = {@value fraclac.utilities.Symbols#R_SQ_INDEX},
   * <li>Y_INT_INDEX = {@value fraclac.utilities.Symbols#Y_INT_INDEX},
   * <li>PREFACTOR_INDEX = {@value fraclac.utilities.Symbols#PREFACTOR_INDEX},
   * <li>STDfERR_INDEX = {@value fraclac.utilities.Symbols#STD_ERR_INDEX}
   * </ul>
   */
  public double[] plainLinearRegression(final double[] pdaY,
                                        final double[] pdaX,
                                        final int piNumElements)
  { /*
     * takes an array for box sizes and an array for
     * Counts and the number of box sizes to check
     * and calculates stats using those arrays
     */

    double ldN = (double) piNumElements;
    double fPlainSumSizeXCount = 0,
        fPlainSumSize = 0,
        fPlainSumCount = 0,
        fPlainSumSizeSquared = 0,
        fPlainSumCountSquared = 0;

    for (int liIndexInArrays = 0;
        liIndexInArrays < piNumElements; liIndexInArrays++) {

      fPlainSumSizeXCount += (pdaX[liIndexInArrays] * pdaY[liIndexInArrays]);

      fPlainSumSize += (pdaX[liIndexInArrays]);

      fPlainSumCount += (pdaY[liIndexInArrays]);

      fPlainSumCountSquared += (pdaY[liIndexInArrays] * pdaY[liIndexInArrays]);

      fPlainSumSizeSquared += (pdaX[liIndexInArrays] * pdaX[liIndexInArrays]);
    }

    double[] ldaRegularStats = calculateStats(fPlainSumSizeXCount,
                                              fPlainSumSize,
                                              fPlainSumCount,
                                              fPlainSumSizeSquared,
                                              fPlainSumCountSquared,
                                              ldN);

    dPlainRegressSlope = ldaRegularStats[FRACTAL_DIMENSION_INDEX];
    dPlainRegressRSq = ldaRegularStats[R_SQ_INDEX];
    dPlainRegressYInt = ldaRegularStats[Y_INT_INDEX];
    dPlainRegressInverseOfPrefactor = ldaRegularStats[PREFACTOR_INDEX];
    dPlainRegressStdErr = ldaRegularStats[STD_ERR_INDEX];

    return ldaRegularStats;
  }

  /**
   * Returns an array holding the slope, r&sup2;, y-intercept,
   * prefactor<sup>-1</sup> (prefactor is A in y=Ax<sup>b</sup>), and standard
   * error for power law regression lines calculated from the passed values.
   * <p>
   * This method is called by this class's other methods that prepare (for
   * example, transform and sum) arrays.
   * <h5>Calculations</h5>
   * <ol>
   * <li>
   * SLOPE OF THE REGRESSION LINE
   * <ul>
   * <li>m = ((&#x0274;*&Sum;SC)-(&Sum;S*&Sum;C))
   * /((&#x0274;*&Sum;S&sup2;)-(&Sum;S*&Sum;S));</li>
   * </ul>
   * </li>
   * <li>
   * SQUARED CORRELATION COEFFICIENT OF REGRESSION LINE
   * <ul>
   * <li>Correlation Coefficient = (&#x0274;*(&Sum;SC)-(&Sum;S*&Sum;C)) /&radic;
   * (((&#x0274;*&Sum;S&sup2;)-(&Sum;S*&Sum;S))*
   * ((&#x0274;*&Sum;C&sup2;)-((&Sum;C*&Sum;C))));</li>
   * <li>r&sup2; = Correlation Coefficient&sup2;;</li>
   * </ul>
   * </li>
   * <li>
   * Y INTERCEPT OF REGRESSION LINE
   * <ul>
   * <li>y-intercept = (&Sum;C-(m*&Sum;S))/n</li>
   * </ul>
   * </li>
   * <li>
   * PREFACTOR FOR THE REGRESSION LINE
   * <ul>
   * <li>A = 1/ &#8494<sup>y-intercept</sup></li>
   * <li>for a power law: y=Ax<sup>m</sup></li>
   * </ul>
   * </li>
   * <li>STANDARD ERROR FOR REGRESSION LINE
   * <ul>
   * <li>standard error of estimate= &radic; ((&Sum;C&sup2;
   * -y-intercept*&Sum;C-m*&Sum;SC)/(&#x0274;-2))</li>
   * </ul>
   * </li>
   * </ol>
   *
   *
   * @see #getFractalDimensionStats
   * @param pdSumSizeXCount double for sum of all S times C array
   * @param pdSumSize double for sum of all S array
   * @param pdSumCount double for sum of all C array
   * @param pdSumSizeXSize double for sum of all S&sup2;
   * @param pdSumCountXCount double for sum of all C&sup2;
   * @param pdNumElements double for number of values that were summed in one
   * array
   *
   *
   * @return float array holding slope, correlation r&sup2;, y-intercept,
   * prefactor's inverse, and standard error for regression line, accessible
   * using FRACTAL_DIMENSION_INDEX=
   * {@value fraclac.utilities.Symbols#FRACTAL_DIMENSION_INDEX},
   * R_SQ_INDEX={@value fraclac.utilities.Symbols#R_SQ_INDEX},
   * Y_INT_INDEX={@value fraclac.utilities.Symbols#Y_INT_INDEX},
   * PREFACTOR_INDEX= {@value fraclac.utilities.Symbols#PREFACTOR_INDEX},
   * STDfERR_INDEX={@value fraclac.utilities.Symbols#STD_ERR_INDEX}.
   */
  public double[] calculateStats(double pdSumSizeXCount,
                                 double pdSumSize,
                                 double pdSumCount,
                                 double pdSumSizeXSize,
                                 double pdSumCountXCount,
                                 double pdNumElements)
  {

    double[] ldaFiveStats = new double[5];

    ldaFiveStats[FRACTAL_DIMENSION_INDEX]
        = ((pdNumElements * pdSumSizeXCount) - (pdSumSize * pdSumCount))
        / ((pdNumElements * pdSumSizeXSize) - (pdSumSize * pdSumSize));

    double ldCorrelationCoefficientOfRegressionLine = (pdNumElements
        * (pdSumSizeXCount) - (pdSumSize * pdSumCount))
        / (float) Math
        .sqrt(((pdNumElements * pdSumSizeXSize) - (pdSumSize * pdSumSize))
            * ((pdNumElements * pdSumCountXCount)
            - ((pdSumCount * pdSumCount))));

    ldaFiveStats[R_SQ_INDEX] = ldCorrelationCoefficientOfRegressionLine
        * ldCorrelationCoefficientOfRegressionLine;

    ldaFiveStats[Y_INT_INDEX] = (pdSumCount / pdNumElements)
        - (ldaFiveStats[FRACTAL_DIMENSION_INDEX] * (pdSumSize) / pdNumElements);
    // = (DsumC-(b*DsumS))/n;

    ldaFiveStats[PREFACTOR_INDEX] = 1d / (Math
        .exp(ldaFiveStats[Y_INT_INDEX]));
		// Prefactor A = e to the Y-Intercept
    // for a power law: y=Ax^Df

    // standarderror
    ldaFiveStats[STD_ERR_INDEX] = Math
        .sqrt((pdSumCountXCount - ldaFiveStats[Y_INT_INDEX]
            * pdSumCount - ldaFiveStats[FRACTAL_DIMENSION_INDEX]
            * pdSumSizeXCount)
            / (pdNumElements - 2d));

    return ldaFiveStats;
  }

  /**
   * Returns result from {@link #getFractalDimensionStats
   * getFractalDimensionStats double[], double[]}.
   *
   *
   * @param pdaCounts double array
   * @param piaSizes integer array is converted to double
   * @param piNumSizes integer for number of values to use
   *
   *
   * @return float array of values based on passed arrays accessible using
   * FRACTAL_DIMENSION_INDEX=0, R_SQ_INDEX=1, Y_INT_INDEX=2, PREFACTOR_INDEX=3,
   * STDfERR_INDEX=4
   */
  public double[] getFractalDimensionStats(final double[] pdaCounts,
                                           final int[] piaSizes,
                                           final int piNumSizes)
  {

    if (piNumSizes == piaSizes.length && piNumSizes == pdaCounts.length) {
      return getFractalDimensionStats(pdaCounts,
                                      intToDoubleArray(piaSizes),
                                      piNumSizes);
    }
    double[] ldaC, ldaS;
    if ((piNumSizes == piaSizes.length)) {
      ldaC = new double[piNumSizes];
      System.arraycopy(pdaCounts,
                       0,
                       ldaC,
                       0,
                       piNumSizes);

      return getFractalDimensionStats(ldaC,
                                      intToDoubleArray(piaSizes),
                                      piNumSizes);
    } else {
      ldaS = new double[piNumSizes];
      System.arraycopy(piaSizes,
                       0,
                       ldaS,
                       0,
                       piNumSizes);
      if (piNumSizes == pdaCounts.length) {
        return getFractalDimensionStats(pdaCounts,
                                        ldaS,
                                        piNumSizes);
      }
      ldaC = new double[piNumSizes];
      System.arraycopy(pdaCounts,
                       0,
                       ldaC,
                       0,
                       piNumSizes);
      return getFractalDimensionStats(ldaC,
                                      ldaS,
                                      piNumSizes);
    }

  }

  /**
   *
   * @return
   */
  @Override
  public String toString()
  {
    return newline + TAB + "dFractalDimension = " + fnum(dFractalDimension)
        + newline + TAB + "dRSq = " + fnum(dRSq) + newline + TAB
        + "dStdErr  = " + fnum(dStdErr) + newline + TAB
        + "dYIntercept  = " + fnum(dYIntercept) + newline + TAB
        + "dDBFS = " + fnum(dDB_F_S) + newline + TAB + "dDB_FSB_ = "
        + fnum(dDB_F_SB) + newline + TAB + "dDB_FSS_ = "
        + fnum(dDB_F_SS) + newline;
  }

}
