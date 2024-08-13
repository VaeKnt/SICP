package fraclac.writers;

import fraclac.analyzer.DataProcessor;
import fraclac.analyzer.GridSet;
import fraclac.analyzer.Vars;
import fraclac.utilities.Utils;

/**
 * Methods for creating StringBuilders from processed data from<!-->
 *
 * {@link fraclac.analyzer.Scan#scanSLACThisSIZEThisGRIDThisSlice sliding box
 * lacunarity} fractal analysis with FracLac.
 *
 * Use the matching headings in the {@link fraclac.writers.Headings Headings}
 * class and the {@link fraclac.writers.WriteUtilities} class in this package to
 * write or show the StringBuilders.
 *
 *
 * @author Audrey Karperien
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 241 $
 * @version Revision $Id: SLACWriter.java 241 2015-03-26 06:58:56Z audrey $
 */
public class SLACWriter extends DataStringFormatter
{

  /**
   * String of headings used in displaying sliding box lacunarity (SLac)
   * results. The results corresponding to the headings are stored in
   * {@link fraclac.analyzer.Vars#sbDataFileForEachGRIDOnThisSlice}.
   * <p>
   * It is an uninitialized String [] unless it has been filled, which is done
   * during execution of an
   * {@link fraclac.analyzer.Scan#scanSLACThisSIZEThisGRIDThisSlice SLac scan}
   * through a call to
   * {@link fraclac.writers.SLACWriter#loadDataArraysAndHeadingsForSLacDataFile}.
   */
  public static StringBuilder sbTabbedSLacDataFileHeadings;

  /**
   * Returns a string of data gathered during a sliding box lacunarity scan. It
   * also updates the corresponding {@link #sbTabbedSLacDataFileHeadings
   * headings} String [], but does not return it.
   *
   * Calls {@link #informationPhrasesFor1stColumnInSLacDataFile} to fill the
   * first column, and {@link #loadDataArraysAndHeadingsForSLacDataFile} to
   * prepare headings and get data to use to fill the data columns.
   *
   * <!---- Describe uses and dependencies-----> <h5>Use</h5> Within the FracLac
   * plugin, this method is called by a
   *
   * {@link fraclac.analyzer.Scan#
   * appendDataFileStringsForEachGRIDOnThisSlice
   * controller} to fill
   * {@link fraclac.analyzer.Vars#sbDataFileForEachGRIDOnThisSlice}.
   * <p>
   * If used independently of the plugin, the Vars and <a href = #belowa>data
   * arrays</a> in the passed Data Processor must be filled ahead of time.
   *
   * <!---- Describe format ----->
   * <h5>Format of Returned String</h5>
   *
   * <table border = 1>
   * <tr>
   * <td rowspan = "2">Fractal Dimension (D) = -
   * {@link fraclac.analyzer.Data#daDmForMeanPixOrDeltaIAtGRID D array}[0]</td>
   * <td colspan = "3" >
   * <i>The area below is a matrix of columns that goes to the last data type*,
   * with columns holding values for every element i in each data array[0]. The
   * first 3 data types are shown here, the rest are <a href =
   * #belowa>below</a> the table</i></td>
   * </tr>
   * <td>passed SIZES[0][i]</td>
   * <td>passed epsilonsigma[0][i]</td>
   * <td> {@link fraclac.analyzer.Data#
   * d2dMeanPixOrdeltaIPerSampleAtSIZEOnGRID &mu; mass} [0][i]</td>
   * </tr>
   * <tr>
   * <td>&nbsp;&nbsp;&nbsp;&nbsp;(D = lim[ln &mu;F vs ln &epsilon;] = -slope of
   * regression line )</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>&nbsp;&nbsp;&nbsp;&nbsp;(&mu;F = mean foreground pixels per sample at
   * one sampling SIZE (or &epsilon;))</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>Correlation Coefficient (r&sup2;) for regression line =
   * {@link fraclac.analyzer.Data#daRSqForDmFromMeanPixOrDeltaIAtGRID r&sup2;}
   * [0]</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>Prefactor for D =
   *
   * {@link fraclac.analyzer.Data#daPrefactorForDmForMeanPixOrDeltaIAtGRID
   * prefactors}[0],</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>y-intercept for regression line =
   *
   * {@link fraclac.analyzer.Data#daYIntForDmFromMeanPixOrDeltaIAtGRID
   * yis_dotnt}[0]</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>Pixels slid horizontally between samples =
   * {@link fraclac.analyzer.Vars#iPixelsToSlideHorizontally x slide}</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>Pixels slid vertically between samples =
   * {@link fraclac.analyzer.Vars#iPixelsToSlideVertically y slide}</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>Number of Bins in Probability Distribution =
   * {@link fraclac.analyzer.Vars#iMaxFrequencies bins}</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td>Symbols in Data Matrix: &sigma; = standard deviation; &mu; = mean</td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td></td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>
   * </tr>
   * <tr>
   * <td></td>
   * <td>SIZE data[0][i]</td>
   * <td>&epsilon; data[0][i]</td>
   * <td>mass data[0][i]</td>>
   * </tr>
   * <tr>
   * <td>lim [ln Data Type vs ln &epsilon;] = slope from regression line</td>
   * <td>slope above vs &epsilon;</td>
   * <td>slope above vs &epsilon;</td>
   * <td>slope above vs &epsilon;</td>
   * </tr>
   * </table>
   *
   *
   * <a name = belowa>The data columns*</a> are made from calls to a column
   * writing {@link fraclac.writers.WriteUtilities#
   * stringOfNthElementsFromAllArrays
   * function}. In addition to the 3 columns listed above, the columns include
   * the data arrays listed below.
   * <p>
   * Note that for arrays that refer to
   * {@value fraclac.writers.AllGsHeadings#PROB} or
   * {@value fraclac.writers.AllGsHeadings#PD}, the value stored in the data
   * types array is null unless {@link fraclac.analyzer.Vars#iMaxFrequencies
   * bins} in the passed Vars instance is &gt;0. <!---- regularly update to
   * match code ----->
   * <ol start = 4>
   * <!--4-->
   * <li>{@link fraclac.analyzer.Data#d2dStdDevPixOrdeltaIPerSampleAtSIZEOnGRID
   * &sigma;}[0] for mass
   *
   * <!--5-->
   * <li> {@link fraclac.analyzer.Data#d2dCountAtSIZEOnGRID Count}[0]
   *
   * <!--6-->
   * <li> {@link Data#d2dlambdaCvSqPixOrdeltaIPerSampleAtSIZEOnGRID}
   * (&sigma;/&mu;)&sup2;}[0] + 1 for mass
   *
   * <!--7-->
   * <li>
   * {@link fraclac.analyzer.Data#d2dMeanOMEGAPixOrdeltaIAtSIZEOnGRID &mu;}[0]
   * for Omega
   *
   * <!--8-->
   * <li>{@link fraclac.analyzer.Data#d2dStdDevsForOMEGAPixOrdeltaIAtSIZEOnGRID
   * &sigma;}[0] for Omega
   *
   * <!--9-->
   * <li> {@link fraclac.analyzer.Data#d2dOMEGACountAtSIZEOnGRID count}[0] for
   * Omega
   *
   * <!--10-->
   * <li> {@link
   * fraclac.analyzer.Data#d2dlambdaCvSqsOMEGAPixOrdeltaIAtSIZEOnGRID
   * (&sigma;/&mu;)&sup2;} [0] + 1 for Omega
   *
   * <!--11-->
   * <li>{@link fraclac.analyzer.Data#d2dMeanOfUnweightedProbAtSIZEOnGRID
   * &mu;}[0] {@value fraclac.writers.AllGsHeadings#PROB}
   *
   * <!--12-->
   * <li>{@link fraclac.analyzer.Data#d2dStdDevOfUnweightedProbAtSIZEOnGRID
   * &sigma;}[0] {@value fraclac.writers.AllGsHeadings#PROB}
   *
   * <!--13-->
   * <li>{@link fraclac.analyzer.Data#
   * d2dlambdaUnweightedProbCvSqOverBinsAtSIZEOnGRID
   * adj lacunarity}[0] + 1 for {@value fraclac.writers.AllGsHeadings#PROB}
   *
   * <!--14-->
   * <li>
   * {@link fraclac.analyzer.Data#d2dMeanWeightedPDForOMEGAAtSIZEOnGRID &mu;}
   * [0] for Omega {@value fraclac.writers.AllGsHeadings#PD}
   *
   * <!--15-->
   * <li>{@link fraclac.analyzer.Data#d2dStdDevWeightedPDForOMEGAAtSIZEOnGRID
   * (&sigma;/&mu;)&sup2;}[0] for Omega for
   * {@value fraclac.writers.AllGsHeadings#PD}
   *
   * <!--16-->
   * <li> {@link fraclac.analyzer.Data#d2dlambdaOMEGAWeightedPDCvSqOverBinsAtSIZEOnGRID
   * special lac}[0] + 1 for Omega for {@value fraclac.writers.AllGsHeadings#PD}
   * </ol>
   *
   *
   * @param psTitle String of information about the image
   * @param pVars Vars with fields prefilled
   * @param pd2dEpsilons double [][] of SIZE/larger dimension of image; only the
   * array at index 0 is accessed
   * @param pDP DataProcessor with data arrays prefilled
   * @param pi2dSIZEs int [][] of {@link GridSet#i2dSIZEs SIZEs} of
   *
   * {@link fraclac.analyzer.Vars#bUseOvalForInnerSampleNotOuterSubscan
   *            sampling elements} used in the scan for which
   *
   * the data are being put into a string; only the array at index 0 is accessed
   *
   * @return String of sliding box lacunarity data
   *
   * @see "Plotnick, Gardner, O'Neill, Landscape Ecology,8:3:201-211"
   * @see "Smith, Lange, Marks"
   */
  public static String getSLacDataFileAndMakeHeadings(String psTitle,
                                                      Vars pVars,
                                                      DataProcessor pDP,
                                                      int[][] pi2dSIZEs,
                                                      double[][] pd2dEpsilons)
  {
    String[] lsaInformationPhrasesFor1stColumn
        = informationPhrasesFor1stColumnInSLacDataFile(
            psTitle,
            pDP,
            pVars);

    double[][] ld2d16DataTypesToPrintIn2ndAndGreaterColumns
        = loadDataArraysAndHeadingsForSLacDataFile(
            pVars,
            pi2dSIZEs[0],
            pd2dEpsilons[0],
            pDP);

    // .........................................................................
    // .....Print a newline then rows so that several aligned columns
    // .....appear. The first is a column of the phrases in the
    // .....string array of info or when all of those phrases are
    // .....printed, just a blank space and a TAB. The rest are from
    // dataTypes.
    // .....Each row aligns with the row beneath, each column containing its
    // own
    // .....array's corresponding elements (e.g., if the array is
    // {1,2,3}{a,b,c}
    // .....the printout is a column of 1,2,3 beside a column of a,b,c).
    // .........................................................................
    int liGreaterOfInfoPhrasesOrSIZEsLength
        = Math.max(pi2dSIZEs[0].length,
                   lsaInformationPhrasesFor1stColumn.length);

    String lsData = "";

    for (int liRowIndex = 0;
        liRowIndex < liGreaterOfInfoPhrasesOrSIZEsLength; liRowIndex++) //
    {

      lsData = lsData
          + newline
          + (liRowIndex < lsaInformationPhrasesFor1stColumn.length
              ? lsaInformationPhrasesFor1stColumn[liRowIndex]
              + TAB
              : "" + TAB)
          + (liRowIndex < pi2dSIZEs[0].length
              ? stringOfNthElementsFromAllArrays(
                  ld2d16DataTypesToPrintIn2ndAndGreaterColumns,
                  liRowIndex) : " ");
    }

    // .....Below the columns made from the datatypes array,
    // .....print a row of the slope of each column against
    // .....the epsilons array. They will line up because
    // .....the datatypes array includes a copy of the epsilons array
    // .....at the start, so that it makes the first column
    // .....which lies beneath the info column and will have a slope of 1
    lsData = lsData
        + newline
        + "lim [ln Data Type vs ln "
        + epsilon
        + "]"
        + newline
        + " = Slopes (from power regressions)"
        + TAB
        + stringOfSlopeYVsEpsilonForYArrays(
            ld2d16DataTypesToPrintIn2ndAndGreaterColumns,
            pd2dEpsilons);

    return lsData;
  }

  /**
   *
   * @param psTitle
   * @param pDP
   * @param pVars
   * @return
   */
  static String[] informationPhrasesFor1stColumnInSLacDataFile(
      String psTitle,
      DataProcessor pDP,
      Vars pVars)
  {

    return new String[]{
      psTitle,
      "Fractal Dimension (D) = "
      + Utils.fnum(-pDP.data.daDmForMeanPixOrDeltaIAtGRID[0]),
      //
      "  (D = lim[ln " + mu + "F vs ln " + epsilon
      + "] = -slope of regression" + " line )",
      //
      "  (" + mu + "F = mean foreground pixels per sample at one "
      + "sampling size (" + epsilon + "))",
      //
      "Correlation Coefficient ("
      + R_SQ
      + ") for regression line = "
      + Utils.fnum(pDP.data.daRSqForDmFromMeanPixOrDeltaIAtGRID[0]),
      //
      "Prefactor for D = "
      + Utils.fnum(pDP.data.daPrefactorForDmForMeanPixOrDeltaIAtGRID[0]),
      //
      "y-intercept for regression line = "
      + Utils.fnum(pDP.data.daYIntForDmFromMeanPixOrDeltaIAtGRID[0]),
      //
      "Pixels slid horizontally between samples = "
      + pVars.iPixelsToSlideHorizontally,
      //
      "Pixels slid vertically between samples = "
      + pVars.iPixelsToSlideVertically,
      //
      "Number of Bins in Probability Distribution = "
      + pVars.iMaxFrequencies,
      //
      "Symbols in Data Matrix: " + sigma + " = standard deviation; "
      + mu + " = mean"};

  }

  /**
   * Loads headings into a local variable for headings for sliding box
   * lacunarity data files and returns a 2d array of arrays of doubles to print
   * under each heading. The heading string has one extra column at the
   * beginning, so the headings align with the arrays using DataIndex =
   * HeadingsIndex+1.
   *
   * @param pVars
   * @param piaSIZEs
   * @param pdaEpsilons
   * @param pDP DataProcessor
   *
   * @return double[][]
   */
  static double[][] loadDataArraysAndHeadingsForSLacDataFile(
      Vars pVars,
      int[] piaSIZEs,
      double[] pdaEpsilons,
      DataProcessor pDP)
  {

    sbTabbedSLacDataFileHeadings = new StringBuilder(17);
    sbTabbedSLacDataFileHeadings.append(FILE_INFO_STRING).append(
        " & Summary Information");

    double[][] ld2d16DataTypesToPrintIn2ndAndGreaterColumns = new double[16][];

    int liDataIndex = 0;
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = intToDoubleArray(piaSIZEs);
    sbTabbedSLacDataFileHeadings.append(TAB).append(ELEMENT_SIZE_SIZE);// 1

    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++] = pdaEpsilons;

    sbTabbedSLacDataFileHeadings.append(TAB).append(epsilon + " = ")
        .append(ELEMENT_SIZE_SIZE).append("/Larger Image Dimension");
    // 2
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = pDP.data.d2dMeanPixOrDeltaIPerSampleAtSIZEOnGRID[0];
    sbTabbedSLacDataFileHeadings.append(TAB + "Mean (" + mu
        + ") Foreground pixels per box (" + FMASS + ")");
    // 3
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = pDP.data.d2dStdDevPixOrdeltaIPerSampleAtSIZEOnGRID[0];
    sbTabbedSLacDataFileHeadings.append(TAB + "Standard deviation ("
        + sigma + ")" + FMASS);
    // 4
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = pDP.data.d2dCountAtSIZEOnGRID[0];
    sbTabbedSLacDataFileHeadings.append(TAB).append(COUNT);
    // 5
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = plus1(pDP.data.d2dlambdaCvSqPixOrDeltaIPerSampleAtSIZEOnGRID[0]);
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        sSIGMA_OVER_MU_SQ_PLUS_1 + FOR + FMASS);
    // 6
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = pDP.data.d2dMeanOMEGAPixOrdeltaIAtSIZEOnGRID[0];
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        mu + FMASS + FOR + LLOMEGA);
    // 7
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = pDP.data.d2dStdDevsForOMEGAPixOrdeltaIAtSIZEOnGRID[0];
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        sigma + FMASS + FOR + LLOMEGA);
    // 8
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = pDP.data.d2dOMEGACountAtSIZEOnGRID[0];
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        "Total count of samples for " + LLOMEGA);
    // 9
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = plus1(pDP.data.d2dlambdaCvSqsOMEGAPixOrdeltaIAtSIZEOnGRID[0]);
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        sSIGMA_OVER_MU_SQ_PLUS_1 + FOR + FMASS + FOR + LLOMEGA);
    // 10
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = (pVars.iMaxFrequencies > 0)
            ? pDP.data.d2dMeanOfUnweightedProbAtSIZEOnGRID[0]
            : null;
    sbTabbedSLacDataFileHeadings.append(TAB)
        .append(mu + FMASS + FOR + PROB);
    // 11
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = (pVars.iMaxFrequencies > 0)
            ? pDP.data.d2dStdDevOfUnweightedProbAtSIZEOnGRID[0]
            : null;
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        sigma + FMASS + FOR + PROB);
    // 12
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = (pVars.iMaxFrequencies > 0)
            ? plus1(pDP.data.d2dlambdaUnweightedProbCvSqOverBinsAtSIZEOnGRID[0])
            : null;
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        sSIGMA_OVER_MU_SQ_PLUS_1 + FOR + FMASS + FOR + OVER_BINS + FOR
        + PROB);
    // 13
    //
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = (pVars.iMaxFrequencies > 0)
            ? pDP.data.d2dMeanWeightedPDForOMEGAAtSIZEOnGRID[0]
            : null;
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        mu + FMASS + FOR + PD + FOR + LLOMEGA);
    // 14
    //
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = (pVars.iMaxFrequencies > 0)
            ? pDP.data.d2dStdDevWeightedPDForOMEGAAtSIZEOnGRID[0]
            : null;
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        sigma + FMASS + FOR + LLOMEGA + FOR + PD);
    // 15
    ld2d16DataTypesToPrintIn2ndAndGreaterColumns[liDataIndex++]
        = (pVars.iMaxFrequencies > 0)
            ? plus1(pDP.data.d2dlambdaOMEGAWeightedPDCvSqOverBinsAtSIZEOnGRID[0])
            : null;
    sbTabbedSLacDataFileHeadings.append(TAB).append(
        OVER_BINS + " + 1" + FOR + PD + FOR + LLOMEGA);
    // 16

    return ld2d16DataTypesToPrintIn2ndAndGreaterColumns;
  }

}
