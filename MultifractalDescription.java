package fraclac.writers;

import fraclac.analyzer.MultifractalAperture;
import fraclac.utilities.ArrayMethods;
import fraclac.utilities.DataFilter;
import fraclac.utilities.Symbols;
import fraclac.utilities.Utils;
import ij.IJ;

/**
 * FracLacProject
 *
 * @author Audrey Karperien akarpe01@postoffice.csu.edu.au
 *
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 244 $
 * @version Revision $Id: MultifractalDescription.java 170 2015-02-11 19:53:30Z
 * audrey $
 */
final public class MultifractalDescription extends Symbols
{

  private double[] daQs;
  /**
   *
   */
  private double[][] daBestGeneralizedDimension = null;
  private double dThisSum = 0;
  private String sThisIsAHump = UNKNOWN_CURVE;
  private boolean bThisDQNeverIncreases = false;
  private boolean bThisAlphaNeverIncreases = false;
  private String sThisDqOrdering = UNKNOWN_ORDER;
  private double dThisDif = 0f;
  MultifractalAperture mfAperture;
  private double dApertureLength = 0f;
  private double dQAt0MinusQAt1XForAperture;
  public double dNumRedRises;
  public double dCrossOverOfGreenAlphaAtMax;
  public int iThisGrid;
  /**
   * A measure of how dispersed the values for &fnof;&alpha; were over the part
   * of the graph where <i>Q</i> &gt;0. It is calculated within the
   * {@link #setFlip() method} which makes an internal call to
   * {@link #getGreenDivergence(double[], double[], double) greenDivergence}.
   */
  public double dGreenDivergence;
  public double dFlippancy;

  public MultifractalDescription()
  {
    super();
  }

  public MultifractalDescription(final double[] pdaQs,
                                 final double[][] pd2dGenDimension,
                                 final int piGrid)
  {
    super();
    getMultifractalDescription(pdaQs,
                               pd2dGenDimension);
    iThisGrid = piGrid;
  }

  /**
   * Returns <code>true</code> if {@link #dGreenDivergence} &lt; {@value
   * Symbols#GREEN_DIVERGENCE_BELOW_WHICH_IT_IS_NON_OR_MONO}, otherwise
   * <code>false</code>.
   *
   * @return boolean
   */
  public boolean isProbablyNotMF()
  {
    if (dGreenDivergence < GREEN_DIVERGENCE_BELOW_WHICH_IT_IS_NON_OR_MONO) {
      return true;
    }
    if ((isFlipped())) {
      return true;
    }
    if (dGreenDivergence > GREEN_DIVERGENCE_ABOVE_WHICH_IT_IS_MF) {
      return false;
    }
    return false;
  }

  /**
   * Returns {@value #NOT_ORDERED},
   *
   * {@value #IS_ORDERED}, or {@value #UNKNOWN_ORDER} for the ordering of
   * elements in pdaDimensions over the passed interval.
   * <p>
   *
   * {@value #IS_ORDERED} means never increases over a certain interval. The
   * interval is defined by values in the pdaQ array, and ordering is defined
   * based on values at the corresponding indices in the pdaDimensions array. In
   * multifractal analysis, this pertains to dimensional ordering, whereby the
   * Generalized Dimension function over the interval
   * <i>Q</i>=0 to <i>Q</i>=2 is flat or sigmoidally decreasing (i.e., never
   * increasing). The criterion for ordering is that the difference between
   * successive elements in the pdaDimensions array within the specified
   * Q-interval is &lt; {@value Symbols#D_TOLERANCE_FOR_GEN_DIM_ORDERING} (the
   * constant {@link Symbols#D_TOLERANCE_FOR_GEN_DIM_ORDERING}).
   *
   * Order is established by mapping the elements in pdaDimensions to those in
   * pdaQ, only considering those within the inclusive range specified in the
   * passed min and max values. In the overloaded version of this method, those
   * are set to {@value #qminForOrdering} to {@value #qmaxForOrdering}. Thus,
   * the method assumes the Q array is ordered from least to greatest and the
   * elements in the pdaDimensions array correspond to the Q array by position.
   * As an example, the following inputs return {@value Symbols#IS_ORDERED} over
   * the interval 0 to 2, but {@value Symbols#NOT_ORDERED} over the interval 2
   * to 0:
   * <table><body align="right">
   * <tr><th>Q</th>
   * <td>-3</td><td>2</td><td>1</td><td>0</td><td>1</td><td>2</td><td>3</td></tr>
   * <tr><th>D</th>
   * </td><td>1.4</td><td>1.2</td><td>1.3</td><td>1.3</td><td>1.2</td>
   * <td>1.1</td><td>1.15</td></tr>
   * </table>
   *
   * Prior to checking the ordering, the arrays are {@link
   * DataFilter#filterBadEntries filtered together} to remove NaN and isInfinite
   * values. If the filtered arrays have 0 length, the method returns
   * {@value #UNKNOWN_ORDER};
   *
   * @param pdaDimensions double []
   * @param pdaQ double []
   * @param pdQmin minimum value to consider in Q array
   * @param pdQmax maximum value to consider in Q array
   *
   * @return String describing dimensional ordering
   */
  public static final String neverIncreasedOverInterval(
      final double[] pdaDimensions,
      final double[] pdaQ,
      final double pdQmin,//0
      final double pdQmax)//2
  {
    // -------------------------------------------------------------------
    // Get copies of the passed arrays, with unreadable values removed.
    // -------------------------------------------------------------------
    final double[][] ld2dFiltered
        = DataFilter.filterBadEntries(pdaDimensions,
                                      pdaQ);
    final double[] ldaFilteredF = ld2dFiltered[0];
    final double[] ldaFilteredQ = ld2dFiltered[1];
    if (!ArrayMethods.isIncreasing(ldaFilteredQ)) {
      IJ.log("Q array is not increasing" + MultifractalDescription.class.
          getSimpleName() + ":"
          + new Exception().getStackTrace()[0].getLineNumber());
      return ERROR_IN_Q;
    }
    // --------------------------------------------------------------------
    // If there were no readable values, return a value indicating 
    // nothing could be determined.
    // --------------------------------------------------------------------
    if (ldaFilteredF.length == 0 || ldaFilteredQ.length == 0) {
      return UNKNOWN_ORDER;
    }
    // --------------------------------------------------------------------
    // Otherwise, test the ordering of values in the F array only 
    // for those that correspond to values in the matched Q array 
    // where Q is between the passed min and max inclusive.
    // --------------------------------------------------------------------
//    if (debug) {
//      System.out.println("Df " + printArray(ldaFilteredF)
//          + newline + "Q  "
//          + printArray(ldaFilteredQ));
//    }
    boolean lbWeHaveNotEnteredTheTestInterval = true;
    boolean lbWeHaveFoundMoreThanOneEntryWithinTheTestInterval = false;
    for (int liQindex = 1; liQindex < ldaFilteredQ.length; liQindex++) {
      // -----------------------------------------------------------
      // We are going through by index, comparing one entry with 
      // the previous, except for the first entry within 
      // the test interval, which we don't compare but use as the start
      // of the testing range by flagging to begin.
      // -----------------------------------------------------------
      double ldQ = ldaFilteredQ[liQindex];
      // For all values in the interval do one of 2 things.       
      if ((ldQ >= pdQmin) && (ldQ <= pdQmax)) {
        // If we haven't started yet, mark it as the start if appropriate. 
        if (lbWeHaveNotEnteredTheTestInterval) {
          if (theseAreWithin0001(ldQ,
                                 pdQmin)) {
            lbWeHaveNotEnteredTheTestInterval = false;
          }
        } else // but if we have started, then test it.
        {
          lbWeHaveFoundMoreThanOneEntryWithinTheTestInterval = true;
          if ((ldaFilteredF[liQindex] - ldaFilteredF[liQindex - 1])
              > D_TOLERANCE_FOR_GEN_DIM_ORDERING) {
            return (NOT_ORDERED);//it increased}

          }
        }
      }
    }
    // --------------------------------------------------------------------
    // If we got here, assume it is ordered because we already responded 
    // to 0 length arrays and increasing arrays.
    // --------------------------------------------------------------------
    return (!lbWeHaveFoundMoreThanOneEntryWithinTheTestInterval)
        ? UNKNOWN_ORDER : IS_ORDERED;
  }

  /**
   * Returns
   * {@link #neverIncreasedOverInterval(double[], double[], double, double)}
   * using the passed arrays and default values of
   * {@link #qminForOrdering min}({@value #qminForOrdering}) and
   * {@link #qmaxForOrdering max} ({@value #qmaxForOrdering}).
   *
   * @param pdaDimensions
   * @param pdaQ
   * @return String
   */
  public static final String neverIncreasedOverInterval(
      final double[] pdaDimensions,
      final double[] pdaQ)//2
  {
    return neverIncreasedOverInterval(pdaDimensions,
                                      pdaQ,
                                      qminForOrdering,
                                      qmaxForOrdering);
  }

  public static final double qminForOrdering = 0, qmaxForOrdering = 2;

  /**
   *
   * @return
   */
  public double setFlip()
  {
    int liIndexClosestTo0 = mfAperture.has0()
        ? mfAperture.getiQIs0()
        : firstIndexGreaterThanOrEqualToTestValue(0,
                                                  daQs);

    return setFlip(
        daBestGeneralizedDimension[MF_FAT_ALPHA_Q_INDEX],
        daQs,
        daBestGeneralizedDimension[MF_ALPHA_Q_INDEX],
        liIndexClosestTo0);
  }

  /**
   *
   * @param pdaF double [] representing &#x0192;(&alpha;)
   * @param pdaQ double [] representing {@link fraclac.analyzer.Vars#daQs Q}
   * exponent in multifractal analysis
   * @param pdaA
   * @param piIndexClosestTo0
   * @return boolean <code>true</code> if the graph is flipped,
   * <code>false</code> otherwise
   */
  public double setFlip(double[] pdaF,
                        double[] pdaQ,
                        double[] pdaA,
                        int piIndexClosestTo0)
  {
    // If the green part rises anywhere or the red falls it is flipped
    int liLengthOFQs = pdaQ.length;
    int liLengthOfNegativeQs = piIndexClosestTo0 + 1;
    int liLengthOfPositiveQs = liLengthOFQs - piIndexClosestTo0;
    // ----------------------------------------------------------------------
    double[] ldaGreenFAtAlphaGreaterThan0 = new double[liLengthOfPositiveQs];
    System.arraycopy(pdaF,
                     piIndexClosestTo0,
                     ldaGreenFAtAlphaGreaterThan0,
                     0,
                     ldaGreenFAtAlphaGreaterThan0.length);
    // -----------------------------------------------------------------------
    double[] ldaGreenAlphaGreaterThan0 = new double[liLengthOfPositiveQs];
    System.arraycopy(pdaA,
                     piIndexClosestTo0,
                     ldaGreenAlphaGreaterThan0,
                     0,
                     ldaGreenAlphaGreaterThan0.length);
    // -----------------------------------------------------------------------
    double[] ldaRedFAtAlphaLessThan0 = new double[liLengthOfNegativeQs];
    System.arraycopy(pdaF,
                     0,
                     ldaRedFAtAlphaLessThan0,
                     0,
                     ldaRedFAtAlphaLessThan0.length);
    // -----------------------------------------------------------------------
    double[] ldaRedAlphaAtQLessThan0 = new double[liLengthOfNegativeQs];
    System.arraycopy(pdaA,
                     0,
                     ldaRedAlphaAtQLessThan0,
                     0,
                     liLengthOfNegativeQs);
    double[] ldaQsForRed = new double[liLengthOfNegativeQs];
    System.arraycopy(pdaQ,
                     0,
                     ldaQsForRed,
                     0,
                     liLengthOfNegativeQs);
    // -----------------------------------------------------------------------
    double ldMaxF = maxInArray(ldaGreenFAtAlphaGreaterThan0);
    double ldFAtQIs0 = pdaF[piIndexClosestTo0];
    double ldAlphaATQIs0 = pdaA[piIndexClosestTo0];
    double ldMinAlphaGT0 = minArray(ldaGreenAlphaGreaterThan0);
    double ldCountInLowerLeftQuadrant = 0;
    double ldCountInLowerHalf = 0;
    dNumRedRises = percentRedRises(ldaRedAlphaAtQLessThan0,
                                   ldaQsForRed);
    dGreenDivergence = getGreenDivergence(ldaGreenAlphaGreaterThan0,
                                          ldaGreenFAtAlphaGreaterThan0,
                                          ldaRedAlphaAtQLessThan0,
                                          ldaRedFAtAlphaLessThan0);
    dCrossOverOfGreenAlphaAtMax = getGreenCrossOver(
        ldaGreenAlphaGreaterThan0,
        ldaGreenFAtAlphaGreaterThan0,
        ldaRedAlphaAtQLessThan0,
        ldaRedFAtAlphaLessThan0);
    //**********************************************************************
    //    Count all data points in the lower quadrant, and 
    //    all in the left lower quadrant, then divide that by 
    //    the total, to get a weighted percent of 
    //    of the amount that are in the right place.
    //**********************************************************************
    for (int i = 0; i < liLengthOfPositiveQs; i++) {
      if ((ldaGreenFAtAlphaGreaterThan0[i] <= ldFAtQIs0)
          && (ldaGreenAlphaGreaterThan0[i] >= ldMinAlphaGT0)
          && (ldaGreenAlphaGreaterThan0[i] <= ldAlphaATQIs0)) {
        ldCountInLowerLeftQuadrant++;
      }
      if ((ldaGreenFAtAlphaGreaterThan0[i] <= ldFAtQIs0)) {
        ldCountInLowerHalf++;
      }

    }
    double ldRedInLowerHalf = 0;
    for (int i = 0; i < liLengthOfNegativeQs; i++) {
      if ((ldaRedFAtAlphaLessThan0[i] <= ldFAtQIs0)) {
        ldRedInLowerHalf++;
      }
    }
    if (DEBUG) {
      System.out.println(ldCountInLowerHalf + "/" + liLengthOfPositiveQs
          + " Green Lower Half; "
          + ldCountInLowerLeftQuadrant + "/" + liLengthOfPositiveQs
          + " Green Lower Left; "
          + ldRedInLowerHalf + "/" + liLengthOfNegativeQs + " Red Lower Half");
    }
    double ldFlippiness
        = ((ldCountInLowerLeftQuadrant / ((double) liLengthOfPositiveQs))
        + (ldRedInLowerHalf / (double) liLengthOfNegativeQs)
        + (ldCountInLowerHalf / (double) liLengthOfPositiveQs)) / 3.0f;

    dFlippancy = 1.00f - ldFlippiness;

    setThisIsHumpy(
        dFlippancy == 0 && dCrossOverOfGreenAlphaAtMax == 0 ? IS_CURVED : NOT_CURVED);
    return dFlippancy;
    //
//    boolean lbBigDifference = (ldMaxF - FAtQIs0) > 0.01;
//    double ldThisTolerance = 0.001;
//    boolean lbGreenFIncreased = !neverIncreasing(
//        ldaGreenFAtAlphaGreaterThan0,
//        ldThisTolerance);
//    boolean lbAlphaIncreased = !neverIncreasing(pdaA,
//                                                ldThisTolerance);
//    boolean lbRedDecreased
//        = !neverDecreasesToTheRightAfterIndex(ldaRedFAtAlphaLessThan0,
//                                              0);
//
//    if (lbRedDecreased) {
//      if (debug) {
//        System.out.println("red decreased");
//        for (int i = 0; i < ldaRedFAtAlphaLessThan0.length; i++) {
//          System.out.print(ldaRedFAtAlphaLessThan0[i] + TAB);
//        }
//      }
//      return FLIPPED;
//    }
//    if ((!lbGreenFIncreased) && (!lbRedDecreased) && (!lbAlphaIncreased)) {
//      return NOT_FLIPPED;
//    }
//    if (lbGreenFIncreased) {
//      if (debug) {
//        System.out.println("green increased");
//        for (int i = 0; i < ldaGreenFAtAlphaGreaterThan0.length; i++) {
//          System.out.print(ldaGreenFAtAlphaGreaterThan0[i] + TAB);
//        }
//      }
//      return FLIPPED;
//    }
//    if (lbAlphaIncreased) {
//      if (debug) {
//        System.out.println("alpha increased");
//        for (int i = 0; i < pdaA.length; i++) {
//          System.out.print(pdaA[i] + TAB);
//        }
//      }
//      return FLIPPED;
//    }
//    if (lbBigDifference) {
//      return FLIPPED;
//    }
//    return NOT_FLIPPED;
  }

  double dGeneralizedDimensionAmplitudeQ0ToQ2,
      dGeneralizedDimensionAmplitudeFromNeg1;

  public static double percentRedRises(double[] pdaRedAlphaAtQLessThan0,
                                       double[] pdaQsForRed)
  {
    //return rises in interval over which it rises
    //define a q interval starting at the most negative
    double ldStartQ = pdaQsForRed[0];
    double ldThisRise = 0;
    double ldRises = 0;
    int liNumQs = pdaQsForRed.length;
    double ldThisValue = pdaRedAlphaAtQLessThan0[0], ldLastValue = ldThisValue;
    final double ldInterval = 1;
    for (int li = 0; li < liNumQs; li++) {
      double ldEndQ = ldStartQ + ldInterval;
      double ldThisQ = pdaQsForRed[li];
      ldThisValue = pdaRedAlphaAtQLessThan0[li];
      if ((ldThisQ > ldEndQ)) {
        ldStartQ += ldEndQ;
      }
      if ((ldThisValue > ldLastValue)) {
        ldThisRise += ldThisValue;
        ldRises++;
      }
      ldLastValue = ldThisValue;
    }
    // ================================================================
    return ldRises / liNumQs;
  }

  /**
   *
   * @param pdaGreenAlpha
   * @param pdaGreenFAtAlpha
   * @param pdaRedAlpha
   * @param pdaRedFAtAlpha
   * @return
   */
  double getGreenDivergence(double[] pdaGreenAlpha,
                            double[] pdaGreenFAtAlpha,
                            double[] pdaRedAlpha,
                            double[] pdaRedFAtAlpha)
  {
    // ----------------------------------------------------------------------
    int liLength = Math.min(pdaGreenAlpha.length,
                            pdaRedAlpha.length);
    double ldGreenArea, ldRedArea;
    // ----------------------------------------------------------------------
    // Measure Divergence as the box containing all green, relative to 
    // the box containing all red.
    // ----------------------------------------------------------------------
    green:
    {
      double ldRightEdgeOfBox = minInArray(pdaGreenAlpha,
                                           liLength);
      double ldLeftEdgeOfBox = maxInArray(pdaGreenAlpha,
                                          liLength);
      double ldTopEdgeOfBox = minInArray(pdaGreenFAtAlpha,
                                         liLength);
      double ldBottomEdgeOfBox = maxInArray(pdaGreenFAtAlpha,
                                            liLength);
      double ldWidth = ldRightEdgeOfBox - ldLeftEdgeOfBox;
      double ldHeight = ldTopEdgeOfBox - ldBottomEdgeOfBox;
      ldGreenArea = ldWidth * ldHeight;
    }
    Red:
    {
      double ldRightEdgeOfBox = minArray(pdaRedAlpha);
      double ldLeftEdgeOfBox = maxInArray(pdaRedAlpha);
      double ldTopEdgeOfBox = minArray(pdaRedFAtAlpha);
      double ldBottomEdgeOfBox = maxInArray(pdaRedFAtAlpha);
      double ldWidth = ldRightEdgeOfBox - ldLeftEdgeOfBox;
      double ldHeight = ldTopEdgeOfBox - ldBottomEdgeOfBox;
      ldRedArea = ldWidth * ldHeight;
    }
    double dDivergence = 100 * ldGreenArea / ldRedArea;
    return dDivergence;

  }

  /**
   *
   * @param pdaGreenAlpha
   * @param pdaGreenFAtAlpha
   * @param pdaRedAlpha
   * @param pdaRedFAtAlpha
   * @return
   */
  double getGreenCrossOver(final double[] pdaGreenAlpha,
                           final double[] pdaGreenFAtAlpha,
                           final double[] pdaRedAlpha,
                           final double[] pdaRedFAtAlpha)
  {

    // ----------------------------------------------------------------------
    // Determine if the green max fAtAlpha's corresponding alpha is 
    // larger than the red max fAtAlpha's corresponding alpha.
    // ----------------------------------------------------------------------
    int liMaxGreenFAtAlphaIndex = indexOfMaxInArray(pdaGreenFAtAlpha);
    double ldMaxGreenCorrespondingAlpha = pdaGreenAlpha[liMaxGreenFAtAlphaIndex];

    int liMaxRedFAtAlphaIndex = indexOfMaxInArray(pdaRedFAtAlpha);
    double ldMaxRedCorrespondingAlpha = pdaRedAlpha[liMaxRedFAtAlphaIndex];

    return ldMaxRedCorrespondingAlpha - ldMaxGreenCorrespondingAlpha;

  }

  /**
   *
   * @return
   */
  public double getAmplitude()
  {
    return dGeneralizedDimensionAmplitudeQ0ToQ2;
  }

  /**
   *
   * @param pdAmplitude
   */
  public void setAmplitude(double pdAmplitude)
  {
    dGeneralizedDimensionAmplitudeQ0ToQ2 = pdAmplitude;
  }

  /**
   *
   * @param pda
   */
  public void setAmplitudeInfo(double[] pda)
  {
    dGeneralizedDimensionAmplitudeQ0ToQ2
        = (mfAperture.has2() && mfAperture.has0())
            ? (pda[mfAperture.getiQIs2()] - pda[mfAperture.getiQIs0()])
            : DUD;//
    //The amplitude is the difference 
    // between the y value of Dq at 0 and y at Dq at 2; the 
    // full amplitude is calculated for -1 and 2;
    dGeneralizedDimensionAmplitudeFromNeg1
        = (mfAperture.hasNeg1() && mfAperture.has0())
            ? (pda[mfAperture.getiQIs2()] - pda[mfAperture.getiQIsNeg1()])
            : DUD;
  }

  /**
   *
   */
  public void loadQAt0MinusQAt1X()
  {
    dQAt0MinusQAt1XForAperture = mfAperture.getQAt0MinusQAt1X();
  }

  /**
   *
   * @param pdValue
   */
  public void setQAt0MinusQAt1X(double pdValue)
  {
    dQAt0MinusQAt1XForAperture = pdValue;
  }

  /**
   *
   * @return
   */
  public double getQAt0MinusQAt1X()
  {
    return dQAt0MinusQAt1XForAperture;
  }

  /**
   *
   * @param pdaF
   * @param pdaQ
   *
   * @return
   */
  public static double sumFOfAlphaIfPositiveQ(
      double[] pdaF,
      double[] pdaQ)
  {
    double ldSum = 0;
    for (int liIndex = 0; liIndex < pdaF.length; liIndex++) {
      if (pdaQ[liIndex] > 0) {
        ldSum += pdaF[liIndex];
      }
    }

    return ldSum;
  }

  /**
   *
   * @param pDescription
   */
  public final void update(MultifractalDescription pDescription)
  {
    setLdaQs(pDescription.getLdaQs());
    setd2dMFSpectraArray(pDescription.copyd2dMFSpectraArray());
    setLbThisAlphaNeverIncreases(pDescription.isAlphaNeverIncreases());
    setLbThisDQNeverIncreases(pDescription.isDQNeverIncreased());
    setThisIsHumpy(pDescription.isSmoothHumpedCurve());
    setLdThisDif(pDescription.getLdThisDif());
    setLdThisSum(pDescription.lowestPositiveSum());
    setSThisDqOrdering(pDescription.getDimensionalOrdering());
    setApertureSlope(pDescription.getApertureLength());
    setQAt0MinusQAt1X(pDescription.getQAt0MinusQAt1X());
    mfAperture = pDescription.mfAperture;
    setAmplitude(pDescription.getAmplitude());
    dGeneralizedDimensionAmplitudeFromNeg1
        = pDescription.dGeneralizedDimensionAmplitudeFromNeg1;
    iThisGrid = pDescription.iThisGrid;
    setFlip();

  }

  /**
   *
   * @param pdaQs
   * @param pd2dGenDimension
   */
  public void getMultifractalDescription(final double[] pdaQs,
                                         final double[][] pd2dGenDimension)
  {

    setLdaQs(pdaQs);

    setd2dMFSpectraArray(pd2dGenDimension);

    setLdThisSum(sumFOfAlphaIfPositiveQ(pd2dGenDimension[MF_FAT_ALPHA_Q_INDEX],
                                        getLdaQs()));

    //setThisIsHumpy(isHumped(pd2dGenDimension[MF_FAT_ALPHA_Q_INDEX]));
    setLbThisDQNeverIncreases(neverIncreasing(pd2dGenDimension[MF_DQ_INDEX],
                                              D_TOLERANCE_FOR_GEN_DIM_ORDERING));

    setLbThisAlphaNeverIncreases(neverIncreasing(
        pd2dGenDimension[MF_ALPHA_Q_INDEX],
        D_TOLERANCE_FOR_GEN_DIM_ORDERING));

    setLdThisDif(differenceBetweenMaxAndWhereQisEqualTo0(
        getLdaQs(),
        pd2dGenDimension[MF_FAT_ALPHA_Q_INDEX]));

    setSThisDqOrdering(
        neverIncreasedOverInterval(pd2dGenDimension[MF_DQ_INDEX],
                                   getLdaQs()));
    setApertureInfo(pdaQs,
                    pd2dGenDimension[MF_FAT_ALPHA_Q_INDEX],
                    pd2dGenDimension[MF_ALPHA_Q_INDEX]);

    setAmplitudeInfo(
        pd2dGenDimension[MF_DQ_INDEX]);

    setFlip();

  }

  /**
   *
   * @param pdaQs
   * @param pdaFatAlphas
   * @param pdAlphas
   */
  void setApertureInfo(double[] pdaQs,
                       double[] pdaFatAlphas,
                       double[] pdAlphas)
  {
    mfAperture = new MultifractalAperture(pdaQs,
                                          pdaFatAlphas,
                                          pdAlphas);

    bApertureIsValid = mfAperture.isFullyValid();

    if (bApertureIsValid) {
      dApertureLength = mfAperture.dDistanceBetweenQPos1AndQNeg1;
      dQAt0MinusQAt1XForAperture = mfAperture.getQAt0MinusQAt1X();
    } else {
      dApertureLength = Double.NEGATIVE_INFINITY;
      dQAt0MinusQAt1XForAperture = Double.NEGATIVE_INFINITY;
    }
  }
  boolean bApertureIsValid = false;

  /**
   *
   * @return
   */
  double getApertureLength()
  {
    if (bApertureIsValid) {
      return dApertureLength;
    }
    return Double.NEGATIVE_INFINITY;
  }

  /**
   * Returns the absolute value of the difference between the maximum value in
   * the passed FAtAlpha array and its value at the position corresponding to
   * the position in the passed Qs array when the element in the Qs array is
   * equal to 0.
   *
   * Logs an error if the two arrays are not of equal length. Skips values that
   * are infinite or NaN.
   *
   *
   * @param pdaQs double array
   * @param pdaFAtAlpha double array
   *
   *
   * @return the signedDistance between the max and the value at Q=0
   */
  public static final double differenceBetweenMaxAndWhereQisEqualTo0(
      final double[] pdaQs,
      final double[] pdaFAtAlpha)
  {
    if (pdaQs.length != pdaFAtAlpha.length) {

      IJ.log("Error. " + MultifractalDescription.class
          .getSimpleName()
          + (new Exception().getStackTrace()[0].getLineNumber()));

    }

    double ldFAlphaMax = Float.NEGATIVE_INFINITY;

    double ldFAtQIs0 = pdaFAtAlpha[0];

    // load the first valid value
    for (int liQIndex = 0; liQIndex < pdaQs.length; liQIndex++) {
      if (!Double.isNaN(pdaFAtAlpha[liQIndex])
          && !Double.isInfinite(pdaFAtAlpha[liQIndex])) {
        ldFAlphaMax = pdaFAtAlpha[liQIndex];
        liQIndex = pdaQs.length;
      }
    }

    for (int liQInd = 0; liQInd < pdaQs.length; liQInd++) {

      if (!Double.isNaN(pdaFAtAlpha[liQInd])
          && !Double.isInfinite(pdaFAtAlpha[liQInd])) {
        ldFAlphaMax = Math.max(pdaFAtAlpha[liQInd],
                               ldFAlphaMax);
      }

      if (pdaQs[liQInd] == 0) {
        ldFAtQIs0 = pdaFAtAlpha[liQInd];
      }

    }

    return Math.abs(ldFAlphaMax - ldFAtQIs0);
  }
//==============================================================================
//******************************************************************************
//==============================================================================

  /**
   *
   * @param pdaF
   *
   * @return
   */
  public final static int findNumberOfIncreasesRightOfMax(final double[] pdaF)
  {
    double ldMax = pdaF[0];
    int liMaxIndex = 0;
    for (int i = 0; i < pdaF.length; i++) {
      if (pdaF[i] > ldMax) {
        ldMax = pdaF[i];
        liMaxIndex = i;
      }
    }
    int liCount = 0;
    for (int i = liMaxIndex; i < pdaF.length; i++) {
      if (pdaF[i] > pdaF[i - 1]) {
        liCount++;
      }
    }
    return liCount;
  }
//==============================================================================
//******************************************************************************
//==============================================================================

  /**
   *
   * @param pda
   *
   * @return
   */
  public final static String oldIsHumped(double[] pda)
  {
    //.........................................................................
    //.......remove bad entries and return false if none are valid.............
    //.........................................................................
    //
    double[] ldaFiltered = DataFilter.filterBadEntries(pda);
    if (ldaFiltered.length == 0) {
      return UNKNOWN_CURVE;
    }
    //..........................................................................
    //......................    Assess the array.    ...........................
    //..........................................................................
    double ldMax = Float.NEGATIVE_INFINITY;
    int liMaxIndex = Integer.MIN_VALUE;

    //==============================================================
    //find the first valid number
    for (int liIndex = 0; liIndex < ldaFiltered.length; liIndex++) {
      if (Utils.isNumberAndNotInfite(ldaFiltered[liIndex])) {
        ldMax = ldaFiltered[liIndex];
        liMaxIndex = liIndex;
        liIndex = ldaFiltered.length + 2;
      }
    }

    if (ldMax == Float.NEGATIVE_INFINITY) {
      return UNKNOWN_CURVE;
    }
    //==============================================================
    for (int liIndex2 = 1; liIndex2 < ldaFiltered.length; liIndex2++) {
      if (Utils.areNumbersAndNotInfinite(ldaFiltered[liIndex2],
                                         ldaFiltered[liIndex2 - 1])) {
        if (ldaFiltered[liIndex2] > ldaFiltered[liIndex2 - 1]) {
          liMaxIndex = liIndex2;
        }
      }
    }

    if (!neverDecreasesToTheRightBeforeIndex(ldaFiltered,
                                             liMaxIndex)) {
      return NOT_CURVED;
    }
    if (!neverIncreasesToTheRightAfterIndex(ldaFiltered,
                                            liMaxIndex)) {
      return NOT_CURVED;
    }
    if (!decreasesSomewhereToTheRightAferIndex(ldaFiltered,
                                               liMaxIndex)) {
      return NOT_CURVED;
    }
    if (!increasesSomewhereToTheRightBeforeIndex(ldaFiltered,
                                                 liMaxIndex)) {
      return NOT_CURVED;
    }

    return IS_CURVED;

    //==============================================================
  }

  public static boolean decreasesSomewhereToTheRightAferIndex(
      double[] ldaFiltered,
      int liMaxIndex)
  {
    for (int i = liMaxIndex; i < ldaFiltered.length; i++) {
      if (ldaFiltered[i] < ldaFiltered[i - 1]) {
        return true;
      }
    }
    return false;
  }

  public static
      boolean increasesSomewhereToTheRightBeforeIndex(double[] ldaFiltered,
                                                      int liMaxIndex)
  {
    for (int i = 1; i < liMaxIndex; i++) {
      if (ldaFiltered[i] > ldaFiltered[i - 1]) {
        return true;
      }

    }
    return false;
  }

  /**
   *
   * @param pda
   * @param piIndex
   *
   * @return
   */
  public static final boolean neverDecreasesToTheRightAfterIndex(double[] pda,
                                                                 int piIndex)
  {
    if (piIndex >= pda.length) {
      return false;
    }

    for (int liIndex = piIndex + 1; liIndex < pda.length; liIndex++) {
      if (pda[liIndex] < pda[liIndex - 1]) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param pda
   * @param piIndex
   *
   * @return
   */
  public static final boolean neverIncreasesToTheRightAfterIndex(double[] pda,
                                                                 int piIndex)
  {
    if (piIndex >= pda.length) {
      return false;
    }

    for (int liIndex = piIndex + 1; liIndex < pda.length; liIndex++) {
      if (pda[liIndex] > pda[liIndex - 1]) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param pdaF
   * @param piMaxIndex
   *
   * @return
   */
  final public static boolean neverDecreasesToTheRightBeforeIndex(
      double[] pdaF,
      int piMaxIndex)
  {
    //could be NAN or infinite//fixme if (maxindex>=f.length) return true;
    if (piMaxIndex == 0) {
      return false;
    }
    for (int liIndex = 1; liIndex < piMaxIndex; liIndex++) {
      if (pdaF[liIndex] < pdaF[liIndex - 1]) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param pda
   * @param piIndex
   *
   * @return
   */
  final public static boolean xalwaysDecreasesToTheRightAfterfIndex(
      double[] pda,
      int piIndex)
  {
    if (piIndex >= pda.length) {
      return false;
    }

    for (int liLoopIndex = piIndex + 1;
        liLoopIndex < pda.length; liLoopIndex++) {
      if (pda[liLoopIndex] >= pda[liLoopIndex - 1]) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param pda
   * @param piMaxIndex
   *
   * @return
   */
  public static final boolean xalwaysIncreasesToTheRightBeforeIndex(
      double[] pda,
      int piMaxIndex)
  {
    //could be NAN or infinite//fixme if (maxindex>=f.length) return true;
    if (piMaxIndex == 0) {
      return false;
    }
    for (int liIndex = 1; liIndex < piMaxIndex; liIndex++) {
      if (pda[liIndex] <= pda[liIndex - 1]) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param pda
   * @param pdTolerance
   *
   * @return
   */
  public static final boolean neverIncreasing(double pda[],
                                              double pdTolerance)
  {

    for (int liIndex = 1; liIndex < pda.length; liIndex++) {
      if ((pda[liIndex] - pda[liIndex - 1]) > pdTolerance) {
        return false;
      }
    }
//return false if there is ever an increase
    //could be flat
    return true;
  }

  /**
   * @return the ldaQs
   */
  public double[] getLdaQs()
  {
    double[] ldaNewQs = new double[daQs.length];
    System.arraycopy(daQs,
                     0,
                     ldaNewQs,
                     0,
                     daQs.length);
    return ldaNewQs;
  }

  /**
   * @param pdaQs
   */
  public void setLdaQs(double[] pdaQs)
  {
    daQs = new double[pdaQs.length];
    System.arraycopy(pdaQs,
                     0,
                     daQs,
                     0,
                     pdaQs.length);
  }

  /**
   * @param pd2da
   */
  public void setd2dMFSpectraArray(double[][] pd2da)
  {
    daBestGeneralizedDimension = new double[pd2da.length][];
    for (int liCounter = 0; liCounter < pd2da.length; liCounter++) {

      daBestGeneralizedDimension[liCounter]
          = new double[pd2da[liCounter].length];

      System.arraycopy(pd2da[liCounter],
                       0,
                       daBestGeneralizedDimension[liCounter],
                       0,
                       pd2da[liCounter].length);

    }
  }

  /**
   * @return
   */
  public double[][] copyd2dMFSpectraArray()
  {
    double[][] ld2d = new double[daBestGeneralizedDimension.length][];
    for (int liCounter = 0;
        liCounter < daBestGeneralizedDimension.length;
        liCounter++) {
      ld2d[liCounter]
          = new double[daBestGeneralizedDimension[liCounter].length];
      System.arraycopy(daBestGeneralizedDimension[liCounter],
                       0,
                       ld2d[liCounter],
                       0,
                       daBestGeneralizedDimension[liCounter].length);
    }

    return ld2d;
  }

  /**
   * @return the ldThisSum
   */
  public double lowestPositiveSum()
  {
    return dThisSum;
  }

  /**
   * @param ldThisSum the ldThisSum to set
   */
  public void setLdThisSum(double ldThisSum)
  {
    this.dThisSum = ldThisSum;
  }

  /**
   * @return the lbThisIsHumpy
   */
  public String isSmoothHumpedCurve()
  {
    return sThisIsAHump;
  }

  public boolean isHumpy()
  {
    return IS_CURVED == sThisIsAHump;
  }

  /**
   */
  public void setThisIsHumpy(String psThisIsHumpy)
  {
    this.sThisIsAHump = psThisIsHumpy;
  }

  public void setApertureSlope(double pdApertureSlope)
  {
    this.dApertureLength = pdApertureSlope;
  }

  /**
   * @return the lbThisDQNeverIncreases
   */
  public boolean isDQNeverIncreased()
  {
    return bThisDQNeverIncreases;
  }

  /**
   * @param lbThisDQNeverIncreases the lbThisDQNeverIncreases to set
   */
  public void setLbThisDQNeverIncreases(boolean lbThisDQNeverIncreases)
  {
    this.bThisDQNeverIncreases = lbThisDQNeverIncreases;
  }

  /**
   * @return the lbThisAlphaNeverIncreases
   */
  public boolean isAlphaNeverIncreases()
  {
    return bThisAlphaNeverIncreases;
  }

  /**
   * @param lbThisAlphaNeverIncreases the lbThisAlphaNeverIncreases to set
   */
  public void setLbThisAlphaNeverIncreases(boolean lbThisAlphaNeverIncreases)
  {
    this.bThisAlphaNeverIncreases = lbThisAlphaNeverIncreases;
  }

  /**
   * @return the liThisDqOrdering
   */
  public String getDimensionalOrdering()
  {
    return sThisDqOrdering;
  }

  /**
   * @param psThisDqOrdering the liThisDqOrdering to set
   */
  public void setSThisDqOrdering(String psThisDqOrdering)
  {
    this.sThisDqOrdering = psThisDqOrdering;
  }

  /**
   * @return the ldThisDif
   */
  public double getLdThisDif()
  {
    return dThisDif;
  }

  /**
   * @param ldThisDif the ldThisDif to set
   */
  public void setLdThisDif(double ldThisDif)
  {
    this.dThisDif = ldThisDif;
  }

  /**
   *
   * @param pbDecideOnMultifractality
   * @return
   */
  public final StringBuilder[] getDescriptionAsSbaHeadings0Description1(
      boolean pbDecideOnMultifractality)
  {
    StringBuilder lsbDescriptionForIndex1 = new StringBuilder();
    lsbDescriptionForIndex1
        .append(scaling())
        .append(TAB)
        .append(Utils.fnum(dGeneralizedDimensionAmplitudeQ0ToQ2))
        .append(TAB)
        .append(Utils.fnum(dGeneralizedDimensionAmplitudeFromNeg1))
        .append(TAB)
        .append(Utils.fnum(dThisSum))
        .append(TAB)
        .append(Utils.fnum(getLdThisDif()))
        .append(TAB)
        .append(sThisIsAHump)
        .append(TAB)
        .append(bThisDQNeverIncreases ? "Yes" : "No")
        .append(TAB)
        .append(bThisAlphaNeverIncreases ? "Yes" : "No")
        .append(TAB)
        .append(sThisDqOrdering)
        .append(TAB)
        .append(Utils.fnum(dFlippancy))
        .append(TAB)
        .append(Utils.fnum(dGreenDivergence))
        .append(TAB)
        .append(Utils.fnum(dNumRedRises))
        .append(TAB)
        .append(Utils.fnum(dCrossOverOfGreenAlphaAtMax));

    StringBuilder lsbHeadingsForIndex0 = new StringBuilder();
    lsbHeadingsForIndex0
        .append("Suggested Scaling")
        .append(TAB)
        .append("D(Q) Amplitude Q=(0 to 2)")
        .append(TAB)
        .append("D(Q) Amplitude Q=(-1 to 2)")
        .append(TAB)
        .append(fOfAlpha + ": Summed for Q>0")
        .append(TAB)
        .append("Max-" + fOf + "(Q=0)")
        .append(TAB)
        .append(MF_HEADING_CURVED)
        .append(TAB)
        .append(MF_HEADING_DQ_NOT_INCREASING)
        .append(TAB)
        .append(alpha + " Not Increasing")
        .append(TAB)
        .append(MF_HEADING_DIMENSIONALLY_ORDERED)
        .append(TAB)
        .append("Flip Error")
        .append(TAB)
        .append("Divergence > 0")
        .append(TAB)
        .append("Red Error < 0 ")
        .append(TAB)
        .append("Cross Over < 0 ");

    return new StringBuilder[]{lsbHeadingsForIndex0, lsbDescriptionForIndex1};
  }

  /**
   * Returns <i>Scaling=Mono/Non</i> or else <i>Scaling=Multifractal</i>,
   * depending on the result of calling {@link #isProbablyNotMF}.
   *
   * @return String
   */
  public String scalingString()
  {
    return "Scaling=" + scaling();
  }

  /**
   * Returns <i>{@value Symbols#SCALING_MONO_OR_NON}</i> or else
   * <i>{@value Symbols#SCALING_MF}</i>, depending on the result of calling
   * {@link #isProbablyNotMF}.
   *
   * @return String
   */
  public String scaling()
  {
    return ((isProbablyNotMF())
        ? SCALING_MONO_OR_NON : SCALING_MF);
  }

  public String greenDivergence()
  {
    return "Green Divergence=" + Utils.fnum(dGreenDivergence,
                                            2);
  }

  public boolean isFlipped()
  {
    return dFlippancy > FLIPPED;
  }

  /**
   * "Flip=00.00% {@value Symbols#NOT_OPTIMIZED_RESCAN}
   *
   * @return
   */
  public String flipString()
  {
    return "Flip=" + Utils.fnum(100f * dFlippancy,
                                2) + "% "
        + (isFlipped()
            ? NOT_OPTIMIZED_RESCAN
            : "");
  }
}
