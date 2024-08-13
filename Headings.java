package fraclac.writers;

import fraclac.utilities.Symbols;
import static fraclac.utilities.Symbols.s_TOTAL_PIX;

/**
 * Fixed enums and enum Strings and utility methods (e.g., for making tabbed
 * enumStrings) for storing data, making headings, and writing headings for
 * results files from fractal analysis in FracLac for ImageJ. The enumStrings
 * are templates used by the writer classes in this package.
 *
 *
 * @author Audrey Karperien
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 242 $
 * @version Revision $Id: Headings.java 242 2015-04-01 16:45:13Z audrey $
 */
public class Headings extends AllGsHeadings
{

  /**
   * <br> {@value}
   */
  public static final String[] SA_HULL_AND_CIRCLE_HEADINGS = {
    EnumInfoData.INFO.toString(),
    EnumInfoData.FOREGROUND_PIX.toString(),
    EnumInfoData.TOTAL_PIX.toString(),
    EnumInfoData.DENSITY.toString(),
    EnumInfoData.SPAN_RATIO.toString(),
    EnumInfoData.HULL_CENTRE_OF_MASS.toString(),
    EnumInfoData.HULL_MAX_SPAN.toString(),
    EnumInfoData.AREA.toString(),
    EnumInfoData.PERIMETER.toString(),
    EnumInfoData.CIRCULARITY.toString(),
    EnumInfoData.BOUNDING_RECT_WIDTH.toString(),
    EnumInfoData.BOUNDING_RECT_HEIGHT.toString(),
    EnumInfoData.HULL_MAX_RADIUS.toString(),
    EnumInfoData.HULL_MAX_OVER_MIN_RADII.toString(),
    EnumInfoData.HULL_CV_FOR_RADII.toString(),
    EnumInfoData.HULL_MEAN_RADIUS.toString(),
    EnumInfoData.CIRCLE_CENTRE.toString(),
    EnumInfoData.CIRCLE_DIAMETER.toString(),
    EnumInfoData.CIRCLE_MAX_RADIUS.toString(),
    EnumInfoData.CIRCLE_MAX_OVER_MIN.toString(),
    EnumInfoData.CIRCLE_CV_RADII.toString(),
    EnumInfoData.CIRCLE_MEAN_RADIUS.toString(),
    EnumInfoData.CIRCLE_METHOD.toString()};

  /**
   * String of 13 headings ending in a TAB. <br> {@value}
   */
  public static final String S_TABBED_DATA_FILE_HEADINGS_MIN_COVER
      = (EnumInfoData.INFO.toString()
      + TAB
      //
      + "FRACTAL DIMENSION from Minimum Cover filter = "
      + DbFMin
      + TAB
      + R_SQ
      + FOR
      + DbFMin
      + TAB
      + YINT
      + FOR
      + DbFMin
      + TAB
      + "Prefactor Inverse"
      + FOR
      + DbFMin
      + TAB
      + "Standard Error (SE)"
      + FOR
      + DbFMin
      + TAB
      + "Mean "
      + lambda
      + " over all "
      + epsilon
      + TAB
      //
      + "FRACTAL DIMENSION from Maximum Cover filter = "
      + DbFMax
      + TAB
      + R_SQ
      + FOR
      + DbFMax
      + TAB
      + YINT
      + FOR
      + DbFMax
      + TAB
      + "Prefactor Inverse"
      + FOR
      + DbFMax
      + TAB
      + SE
      + FOR
      + DbFMax
      + TAB + "Mean " + lambda + " over all " + epsilon + TAB);

  /**
   * String of 15 headings ending in no TAB and no newline. <br> {@value}
   */
  public static String S_TABBED_DATA_FILE_HEADINGS_SMOOTHED = (FILE_INFO_STRING
      + TAB
      + "FRACTAL DIMENSION"
      + FOR
      + SSF
      + " = "
      + DbFSS
      + TAB
      + "Y-intercept"
      + FOR
      + DbFSS
      + TAB
      + "Prefactor"
      + FOR
      + DbFSS
      + TAB
      + R_SQ
      + FOR
      + DbFSS
      + TAB
      + "Std Error"
      + FOR
      + DbFSS
      + TAB
      + "Number of Sizes"
      + FOR
      + DbFSS
      + TAB
      + "Mean "
      + lambda
      + " over all "
      + epsilon
      + TAB
      //
      + "FRACTAL DIMENSION"
      + FOR
      + SBF
      + " = "
      + DbFSB
      + TAB
      + "Y-intercept"
      + FOR
      + DbFSB
      + TAB
      + "Smoothed Prefactor"
      + FOR
      + DbFSB
      + TAB
      + R_SQ
      + FOR
      + DbFSB
      + TAB
      + "Std Error"
      + FOR
      + DbFSB
      + TAB
      + "Number of Sizes"
      + FOR
      + DbFSB
      + TAB
      + "Mean "
      + lambda + " over all " + epsilon);

  /**
   *
   */
  public static String[] SA_DATA_COLUMN_HEADINGS = {ELEMENT_SIZE_SIZE,
    epsilon + " = Sampling Element Size/Image Dimension",
    "F = " + COUNT + " Location ",
    OMEGA + " = all samples tested on image: Location ",
    "F" + mu + " = mean M (M = " + MASS + ")",
    "F" + sigma + " = standard deviation of M: ",
    "F" + slambda_EQUAL_SIGMA_OVER_MU_SQ_PLUS_1 + ": ",
    OMEGA + mu + " = mean M for " + OMEGA + ": ", OMEGA + sigma + ":",
    OMEGA + slambda_EQUAL_SIGMA_OVER_MU_SQ_PLUS_1 + ": ",
    "FPD" + mu + " = mean M from probability distribution (PD): ",
    "FPD" + sigma + ": ",
    "FPD" + slambda_EQUAL_SIGMA_OVER_MU_SQ_PLUS_1 + " : ",
    OMEGA + "PD" + mu + "= mean M for " + OMEGA + " PD  : ",
    OMEGA + "PD" + sigma + ": ",
    OMEGA + "PD" + slambda_EQUAL_SIGMA_OVER_MU_SQ_PLUS_1 + " : "};

  // =========================================================================
  // -------------------------METHODS-----------------------------------------
  // =========================================================================
  /**
   * Returns a string of headings corresponding to columns in
   * {@link fraclac.writers.MFWriter#matrixOfQsAndMultifractalSpectraData}.
   * <h5>Use</h5> The string holds headings for
   * {@link fraclac.analyzer.Vars#sbMultifractalSpectraFile multifractal data}
   * in the FracLac plugin. The headings are used in the
   * {@link ResultsFilesWriter#writeMf multifractal results writer}.
   * <p>
   * The string is made by iterating through {@link ENUM_MULTIFRACTAL_HEADINGS}
   * and returning each value for
   * {@link ENUM_MULTIFRACTAL_HEADINGS#sAllGRIDs()}. The constants for the
   * headings are:
   * <ul>
   * <li>{@link ENUM_MULTIFRACTAL_HEADINGS#MF_COLUMN_Q},
   * <li>{@link ENUM_MULTIFRACTAL_HEADINGS#MF_COLUMN_D}
   * <li>{@link ENUM_MULTIFRACTAL_HEADINGS#MF_COLUMN_TAU},
   * <li>{@link ENUM_MULTIFRACTAL_HEADINGS#MF_COLUMN_MEAN_TAU }
   * <li>{@link ENUM_MULTIFRACTAL_HEADINGS#MF_COLUMN_ALPHA}
   * <li>{@link ENUM_MULTIFRACTAL_HEADINGS#MF_COLUMN_F_Of_ALPHA }
   * </ul>
   * <!-- Update the above strings manually in the javadoc. -->
   *
   * @return String
   *
   * @see ENUM_MULTIFRACTAL_HEADINGS
   */
  public static StringBuilder headingsForMFSpectra()
  {

    StringBuilder lsbHeadings = new StringBuilder("");

    for (ENUM_MULTIFRACTAL_HEADINGS lval : ENUM_MULTIFRACTAL_HEADINGS
        .values()) {
      lsbHeadings
          .append(lval.sAllGRIDs())
          .append(TAB);
    }
    return lsbHeadings;
  }

  // ...........................................................................
  //                        Enums for column headings 
  // ...........................................................................
  /**
   * The constants in this enum represent column headings for general
   * information from a scan. The column headings are used in two ways: by
   * making strings used as the headings when writing files and by making data
   * maps that are later used unpacked using the desired headings as keys.
   *
   * It is used in
   * {@link fraclac.writers.SummaryStringFormatter#storeLongSummaryFileStringAndHeadings}.
   * Use the enum to first store and then print the information, by mapping
   * values stored in a Data object to the column heading constants, then
   * printing the map using a KeySet of the desired constants; There are several
   * constructors, each setting important Strings. The
   * {@link EnumInfoData#toString()} method has been overwritten.
   *
   */
  public enum EnumInfoData implements DataTypesInterface
  {

    /**
     * {@link Symbols#FILE_INFO_STRING}
     */
    INFO(FILE_INFO_STRING),
    /**
     * {@link Symbols#s_SCAN_POSITIONS} <br> {@value Symbols#s_SCAN_POSITIONS}
     * <br>
     * unicode = "Number (\u01e4\u0274) in SET (\u01e4) of SCAN POSITIONS
     * (\u0262)"
     */
    SCAN_POSITIONS(s_SCAN_POSITIONS),
    /**
     * "MEAN sigma_mu for Count = &sum;C &sigma;/&mu; / &#x262;
     */
    MEAN_CV_FOR_COUNT_FROM_ALL_GRID_SCANS(s_CV_MEAN + " for Count" + is
        + sumOverG("C " + sigma_over_mu)),
    /**
     * See {@link Symbols#s_MEAN_CV_COUNT_VS_MEAN_CV_OMEGA_ALL_GRIDS} <br>
     * Value = {@value Symbols#s_MEAN_CV_COUNT_VS_MEAN_CV_OMEGA_ALL_GRIDS} <br>
     * Unicode = "C\u035eV for Count\u2215C\u035eV\u200afor\u200a\u03a9"
     */
    MEAN_CV_COUNT_VS_MEAN_CV_OMEGA_ALL_GRIDS(
        s_MEAN_CV_COUNT_VS_MEAN_CV_OMEGA_ALL_GRIDS),
    /**
     * {@value Symbols#s_FOREGROUND_PIX}
     */
    FOREGROUND_PIX(s_FOREGROUND_PIX),
    /**
     * {@value #s_STD_DEV_FG_PIX}
     */
    STD_DEV_FG_PIX(s_STD_DEV_FG_PIX),
    /**
     * {@value Symbols#s_TOTAL_PIX}
     */
    TOTAL_PIX(s_TOTAL_PIX),
    /**
     * {@value Symbols#s_SCAN_ELEMENT}
     */
    SCAN_ELEMENT(s_SCAN_ELEMENT),
    /**
     * {@value Symbols#s_MIN_PIX_DENSITY}
     */
    MIN_PIX_DENSITY(s_MIN_PIX_DENSITY),
    /**
     * {@value Symbols#s_MAX_PIX_DENSITY}
     */
    MAX_PIX_DENSITY(s_MAX_PIX_DENSITY),
    /**
     * {@value #sSizesSetSize} <br>
     * Unicode = "\u00d8\u0274"
     */
    SIZES(sSizesSetSize),
    /**
     * <li>{@link Symbols#s_MIN_SIZE_MEAN} <li>{@link Symbols#s_MIN_SIZE}.
     */
    MIN_SIZE(s_MIN_SIZE_MEAN,
             s_MIN_SIZE),
    /**
     * <li>{@link Symbols#s_MAX_SIZE_MEAN} <li>{@link Symbols#s_MAX_SIZE}
     */
    MAX_SIZE(s_MAX_SIZE_MEAN,
             s_MAX_SIZE),
    /**
     * {@value #sSTD_DEV_IN_SIZES_FOR_ALL_GRIDS}
     */
    SIGMA_FOR_SIZES(sSTD_DEV_IN_SIZES_FOR_ALL_GRIDS),
    /**
     * {@value Symbols#s_SLIDE_X}
     */
    SLIDE_X(s_SLIDE_X),
    /**
     * {@value Symbols#s_SLIDE_Y}
     */
    SLIDE_Y(s_SLIDE_Y),
    /**
     * {@value Symbols#s_FOREGROUND_COLOUR}
     */
    FOREGROUND_COLOUR(s_FOREGROUND_COLOUR),
    DENSITY(s_DENSITY),
    SPAN_RATIO(s_SPAN_RATIO),
    HULL_CENTRE_OF_MASS(s_HULL_CENTRE_OF_MASS),
    HULL_MAX_SPAN(s_HULL_MAX_SPAN),
    AREA(s_AREA),
    PERIMETER(s_PERIMETER),
    CIRCULARITY(s_CIRCULARITY),
    BOUNDING_RECT_WIDTH(s_BOUNDING_RECT_WIDTH),
    BOUNDING_RECT_HEIGHT(s_BOUNDING_RECT_HEIGHT),
    HULL_MAX_RADIUS(s_HULL_MAX_RADIUS),
    HULL_MAX_OVER_MIN_RADII(s_HULL_MAX_OVER_MIN_RADII),
    HULL_CV_FOR_RADII(s_HULL_CV_FOR_RADII),
    HULL_MEAN_RADIUS(s_HULL_MEAN_RADIUS),
    CIRCLE_CENTRE(s_CIRCLE_CENTRE),
    CIRCLE_DIAMETER(s_CIRCLE_DIAMETER),
    CIRCLE_MAX_RADIUS(s_CIRCLE_MAX_RADIUS),
    CIRCLE_MAX_OVER_MIN(s_CIRCLE_MAX_OVER_MIN),
    CIRCLE_CV_RADII(s_CIRCLE_CV_RADII),
    CIRCLE_MEAN_RADIUS(s_CIRCLE_MEAN_RADIUS),
    CIRCLE_METHOD(s_CIRCLE_METHOD);

    // .........................................................................
    //                       Fields and Constructors
    // .........................................................................
    /**
     * String initialized to UNINITIALIZED.
     */
    public String sAllGRIDs = UNINITIALIZED,
        sOneGRID = UNINITIALIZED,
        sGrayAllGRIDs = UNINITIALIZED,
        sGrayOneGRID = UNINITIALIZED;

    /**
     *
     */
    public EnumString enumStrings = new EnumString();

    // =====================================================================
    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the passed
     * parameter.
     */
    EnumInfoData(String psAllGRIDs)
    {
      this.sAllGRIDs = psAllGRIDs;
    }

    // =====================================================================
    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter and {@link #sOneGRID} to the second.
     */
    EnumInfoData(String psAllGRIDs,
                 String psOneGRID)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
    }

    // =====================================================================
    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, and {@link #sGrayAllGRIDs} to
     * the third.
     */
    EnumInfoData(String psAllGRIDs,
                 String psOneGRID,
                 String psGrayAllGRIDs)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
    }

    // =====================================================================
    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, {@link #sGrayAllGRIDs} to the
     * third, and {@link #sGrayOneGRID} to the fourth.
     */
    EnumInfoData(String psAllGRIDs,
                 String psOneGRID,
                 String psGrayAllGRIDs,
                 String psGrayOneGRID)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
      this.sGrayOneGRID = psGrayOneGRID;
    }

    // .........................................................................
    // .................... Overridden Methods ...........................
    // .........................................................................
    @Override
    public String toString()
    {
      return this.sAllGRIDs();
    }

    @Override
    public String sAllGRIDs()
    {
      return this.sAllGRIDs;
    }

    @Override
    public String sOneGRID()
    {
      return enumStrings.sOneGRID(sAllGRIDs,
                                  sOneGRID,
                                  sGrayAllGRIDs,
                                  sGrayOneGRID);
    }

    @Override
    public String sGrayAllGRIDs()
    {
      return enumStrings.sGrayAllGRIDs(sAllGRIDs,
                                       sOneGRID,
                                       sGrayAllGRIDs,
                                       sGrayOneGRID);
    }

    @Override
    public String sGrayOneGRID()
    {

      return enumStrings.sGrayOneGRID(sAllGRIDs,
                                      sOneGRID,
                                      sGrayAllGRIDs,
                                      sGrayOneGRID);

    }

  }

  /**
   * Enum that specifies the order and information for fractal dimension data
   * presented in summary files. The enum is used as a template for generating
   * data maps and for headings for printing the maps. The enum overrides
   * toString() to return the first string value associated with each constant
   * and has other methods for returning the second, third, and fourth values.
   *
   *
   * @see EnumLacData for lacunarity data
   */
  public enum EnumDataFile implements DataTypesInterface
  {

    /**
     * "SCAN TYPE"
     */
    SCAN_TYPE("SCAN TYPE"),
    /**
     * "Formula for FRACTAL DIMENSION (D)"
     */
    FORMULA_FOR_FRACTAL_DIMENSION("Formula for FRACTAL DIMENSION (D)"),
    /**
     * {@link fraclac.writers.Headings#Y_AT_SIZE}.
     */
    DEFINITION_OF_Y(Y_AT_SIZE + " for the SET (" + sSizesSet + ") "
        + "of ALL (" + sSizesSetSize + ") " + "sampling ELEMENT SIZES "
        + "(" + sSizesSetMember + ")"),
    /**
     * {@link #sumOverG sumOverG} for D.
     */
    MEAN_FRACTAL_DIMENSION(s_MEAN_FRACTAL_DIMENSION,
                           FRACTAL_DIMENSION),
    /**
     * "STANDARD DEVIATION (&sigma;)".
     */
    STD_DEVIATION_FOR_FRACTAL_DIMENSIONS(
        "STANDARD DEVIATION (" + sigma + ") for D"),
    /**
     * .
     */
    MIN_FOR_FRACTAL_DIMENSIONS("MIN for D"),
    /**
     * "STANDARD DEVIATION (&sigma;)".
     */
    MAX_FOR_FRACTAL_DIMENSIONS(
        "MAX for D"),
    //
    /**
     *
     */
    COEFFICIENT_OF_VARIATION("CV (" + sigma + "/" + mu + ") for D "),
    //
    /**
     *
     */
    OPTIMAL_FD(sOPTIMUM + Df + " (" + Df + " with highest " + R_SQ + ")",
               FRACTAL_DIMENSION),
    //
    /**
     *
     */
    R_SQ_OPTIMAL(sOPTIMUM + R_SQ,
                 R_SQ),
    //
    /**
     *
     */
    SE_OPTIMAL(sOPTIMUM + SE,
               SE),
    //
    /**
     *
     */
    YINT_OPTIMAL(sOPTIMUM + YINT,
                 YINT),
    //
    /**
     * {@link fraclac.writers.AllGsHeadings#PREFACTOR}
     */
    PREFACTOR(Headings.PREFACTOR),
    /**
     * Constructed with
     * <ol>
     * <li>{@link EnumLacData.MEAN_LAC#toString()},
     * <li>{@link EnumLacData.MEAN_LAC#sOneGRID()}
     * </ol>
     */
    MEAN_LACUNARITY(EnumLacData.MEAN_LAC.toString(),
                    EnumLacData.MEAN_LAC.sOneGRID()),
    MIN_LACUNARITY(EnumLacData.MIN_LAC.toString(),
                   EnumLacData.MIN_LAC.sOneGRID()),
    MAX_LACUNARITY(EnumLacData.MAX_LAC.toString(),
                   EnumLacData.MAX_LAC.sOneGRID()),
    //
    /**
     *
     */
    FORMULA_FOR_LAMBDA_G("Formula for " + LAMBDA_AT_grid,
                         EnumLacData.DEFINITION_OF_LAMBDA_G.toString()),
    //
    /**
     *
     */
    FORMULA_FOR_lambda(EnumLacData.DEFINITION_OF_lambda.toString()),
    /**
     *
     */
    SLOPE_LACUNARITY(EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE.toString(),
                     EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE.sOneGRID,
                     EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE.sGrayAllGRIDs,
                     EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE.sGrayOneGRID),
    /**
     * Column headings for the mean number of sizes over all grid locations or
     * the sizes at one grid location.
     */
    SIZES("Mean Number Of Sizes = " + "(" + SUM + sSizesSetSize + sAt_grid
        + ")/" + sGSetSize,
          sSizesSetSize),
    /**
     */
    MIN_SIZE(EnumInfoData.MIN_SIZE.toString(),
             "Min SIZE"),
    //
    /**
     *
     */
    MAX_SIZE(EnumInfoData.MAX_SIZE.toString(),
             "Max SIZE"),
    /**
     *
     */
    STANDARD_DEVIATION_FOR_SIZES(EnumInfoData.SIGMA_FOR_SIZES.toString()),
    //
    /**
     *
     */
    NAME(""), //
    /**
     *
     */
    PREFACTOR_LAC("MEAN PREFACTOR LACUNARITY");

    // .........................................................................
    // .................... Fields and Constructors
    // .........................................................................
    /**
     * String initialized to {@link #UNINITIALIZED}=
     * {@value fraclac.writers.DataTypesInterface#UNINITIALIZED}.
     */
    String sAllGRIDs = UNINITIALIZED, sOneGRID = UNINITIALIZED,
        sGrayAllGRIDs = UNINITIALIZED, sGrayOneGRID = UNINITIALIZED;

    /**
     *
     */
    public EnumString enumStrings = new EnumString();

    // ==================================================================
    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the passed
     * parameter.
     */
    EnumDataFile(String psAllGRIDs)
    {
      this.sAllGRIDs = psAllGRIDs;

    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter and {@link #sOneGRID} to the second.
     */
    EnumDataFile(String psAllGRIDs,
                 String psOneGRID)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, and {@link #sGrayAllGRIDs} to
     * the third.
     */
    EnumDataFile(String psAllGRIDs,
                 String psOneGRID,
                 String psGrayAllGRIDs)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, {@link #sGrayAllGRIDs} to the
     * third, and {@link #sGrayOneGRID} to the fourth.
     */
    EnumDataFile(String psAllGRIDs,
                 String psOneGRID,
                 String psGrayAllGRIDs,
                 String psGrayOneGRID)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
      this.sGrayOneGRID = psGrayOneGRID;
    }

    // .........................................................................
    // .................... Overridden Methods ...........................
    // .........................................................................
    @Override
    public String toString()
    {
      return this.sAllGRIDs();
    }

    @Override
    public String sAllGRIDs()
    {
      return this.sAllGRIDs;
    }

    @Override
    public String sOneGRID()
    {
      return enumStrings.sOneGRID(sAllGRIDs,
                                  sOneGRID,
                                  sGrayAllGRIDs,
                                  sGrayOneGRID);

    }

    @Override
    public String sGrayAllGRIDs()
    {
      return enumStrings.sGrayAllGRIDs(sAllGRIDs,
                                       sOneGRID,
                                       sGrayAllGRIDs,
                                       sGrayOneGRID);
    }

    @Override
    public String sGrayOneGRID()
    {

      return enumStrings.sGrayOneGRID(sAllGRIDs,
                                      sOneGRID,
                                      sGrayAllGRIDs,
                                      sGrayOneGRID);
    }

  }

// ================================= ENUM=====================================
  /**
   * Enum with constants representing headings for columns holding data for
   * lacunarity analysis. The constants are used to print results of
   * {@link fraclac.analyzer.Data#d2dlambdaCvSqPixOrdeltaIPerSampleAtSIZEOnGRID lacunarity}
   * analysis by mapping values stored in a Data object to the column heading
   * constants, then printing the map using a KeySet of the desired constants.
   *
   */
  public enum EnumLacData implements DataTypesInterface
  {

    /**
     * toString = {@link #sLacunarityForImageFormula} <br>
     * sOneGRID = {@link EnumDataFile#MEAN_LAC }
     */
    MEAN_LAC(sLacunarityForImageFormula,
             "LACUNARITY for GRID ("
             + LAMBDA_AT_grid + is + s_lambda_BAR_AT_SIZE + ")"),
    /**
     * toString = "CV for
     * {@link fraclac.utilities.Symbols#BIG_L_SYMBOL_FOR_LAC_FOR_IMAGE}" <br>
     * sOneGRID = "CV for {@link fraclac.utilities.Symbols#LAMBDA_AT_grid}"
     */
    CV_FOR_MEAN_LAC("CV " + FOR + BIG_L_SYMBOL_FOR_LAC_FOR_IMAGE,
                    "CV " + FOR + LAMBDA_AT_grid),
    MIN_LAC("MIN " + FOR + BIG_L_SYMBOL_FOR_LAC_FOR_IMAGE,
            "MIN " + FOR + LAMBDA_AT_grid),
    MAX_LAC("MAX " + FOR + BIG_L_SYMBOL_FOR_LAC_FOR_IMAGE,
            "MAX " + FOR + LAMBDA_AT_grid),
    /**
     * "Formula for LAMBDA_AT_grid" <br>
     * OR <br> {@link #sFORMULA_FOR_LAMBDA_grid}
     */
    DEFINITION_OF_LAMBDA_G("Formula for " + LAMBDA_AT_grid,
                           sFORMULA_FOR_LAMBDA_grid),
    /**
     * Constant for the heading of columns that show what &lambda; was based on.
     * The constant has only one value = {@link #sFORMULA_FOR_lambda}.
     */
    DEFINITION_OF_lambda(sFORMULA_FOR_lambda),
    /**
     * toString = {@link Symbols#s_BIGL_lambda_SLOPE_MEAN}
     * <p>
     * or
     * <p>
     * sOneGRID = {@link Symbols#sLAMBDAGrid_PRIME_is_s_lambda_SLOPE}
     * <p>
     * {@value #s_BIGL_lambda_SLOPE_MEAN}
     * <p>
     *
     * @see #s_BIGL_lambda_SLOPE_MEAN
     */
    SLOPES_OF_CVSQ_PLUS1_VS_SIZE(s_BIGL_lambda_SLOPE_MEAN,
                                 sLAMBDAGrid_PRIME_is_s_lambda_SLOPE),
    /**
     *
     */
    CV_OF_ALL_MEANS_OF_SLOPES_OF_CVSQ_PLUS1_VS_SIZE(s_CV + FOR
        + sLAMBDAGrid_PRIME),
    /**
     *
     */
    SLOPES_OF_CVSQ_PLUS1_VS_SIZE_avgcover(sLAMBDAGrid_PRIME + "Avg Cover"
        + is + "MEAN " + s_lambda_SLOPE,
                                          sLAMBDAGrid_PRIME + is
                                          + s_lambda_SLOPE),
    /**
     * For lacunarity for the {@link fraclac.analyzer.Vars#Omega Omega} set.
     */
    MEAN_LAC_FOR_OMEGA(LLOMEGA + is + BIG_L_SYMBOL_FOR_LAC_FOR_IMAGE + FOR
        + lambdaOMEGA),
    /**
     *
     */
    DEFINITION_OF_lambda_FOR_OMEGA(lambdaOMEGA),
    /**
     *
     */
    CV_FOR_LAC_FOR_OMEGA(s_CV + FOR + LLOMEGA),
    /**
     *
     */
    MEAN_LAMBDAD("MEAN" + sp + D_LAMBDA_grid + is + "MEAN" + sp
        + LAMBDA_D_FORMULA),
    /**
     *
     */
    // PREFACTOR_LAC,
    NAME("");

    // .........................................................................
    // .................... Fields and Constructors
    // .........................................................................
    /**
     * String initialized to UNINITIALIZED.
     */
    String sAllGRIDs = UNINITIALIZED, sOneGRID = UNINITIALIZED,
        sGrayAllGRIDs = UNINITIALIZED, sGrayOneGRID = UNINITIALIZED,
        sForegroundLambda;

    /**
     *
     */
    public EnumString strings = new EnumString();

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the passed
     * parameter.
     */
    EnumLacData(String psAllGRIDs)
    {

      this.sAllGRIDs = psAllGRIDs;

    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter and {@link #sOneGRID} to the second.
     */
    EnumLacData(String psAllGRIDs,
                String psOneGRID)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, and {@link #sGrayAllGRIDs} to
     * the third.
     */
    EnumLacData(String psAllGRIDs,
                String psOneGRID,
                String psGrayAllGRIDs)
    {

      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, {@link #sGrayAllGRIDs} to the
     * third, and {@link #sGrayOneGRID} to the fourth.
     */
    EnumLacData(String psAllGRIDs,
                String psOneGRID,
                String psGrayAllGRIDs,
                String psGrayOneGRID)
    {

      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
      this.sGrayOneGRID = psGrayOneGRID;
    }

    // .........................................................................
    // .................... Overridden Methods ...........................
    // .........................................................................
    @Override
    public String toString()
    {
      return this.sAllGRIDs();
    }

    @Override
    public String sAllGRIDs()
    {
      return this.sAllGRIDs;
    }

    @Override
    public String sOneGRID()
    {
      return strings.sOneGRID(sAllGRIDs,
                              sOneGRID,
                              sGrayAllGRIDs,
                              sGrayOneGRID);

    }

    @Override
    public String sGrayAllGRIDs()
    {
      return strings.sGrayAllGRIDs(sAllGRIDs,
                                   sOneGRID,
                                   sGrayAllGRIDs,
                                   sGrayOneGRID);
    }

    @Override
    public String sGrayOneGRID()
    {

      return strings.sGrayOneGRID(sAllGRIDs,
                                  sOneGRID,
                                  sGrayAllGRIDs,
                                  sGrayOneGRID);
    }

  }

  /**
   * Enum with constants representing headings for columns holding data for
   * multifractal analysis. The column headings are used in two ways: by making
   * strings used as the headings when writing files and by making data maps
   * that are later used unpacked using the desired headings as keys.
   *
   * See constants:
   * <ul>
   * <li>{@link #MF_COLUMN_Q}
   * <li>{@link #MF_COLUMN_D}
   * <li>{@link #MF_COLUMN_MEAN_TAU}
   * <li>{@link #MF_COLUMN_ALPHA}
   * <li>{@link #MF_COLUMN_F_Of_ALPHA}
   * </ul>
   *
   * @see ResultsFilesWriter#writeMf(fraclac.analyzer.Vars)
   * @see fraclac.writers.MFWriter#matrixOfQsAndMultifractalSpectraData
   */
  public enum ENUM_MULTIFRACTAL_HEADINGS implements DataTypesInterface
  {

    // .........................................................................
    // ...Constants holding headings for multifractal spectra data points
    // .........................................................................
    /**
     * Heading for one of the multifractal spectra columns.
     * {@value #sMF_COLUMN_HEAD_QSET} <br> {@link #sMF_COLUMN_HEAD_QSET}
     */
    MF_COLUMN_Q(sMF_COLUMN_HEAD_QSET),
    /**
     * Heading for one of the multifractal spectra columns:
     * {@value #sMF_COLUMN_HEAD_D_AtQ_As_TAUX_OVER_QMINUS1} <br>
     * <br>
     * From {@link #sMF_COLUMN_HEAD_D_AtQ_As_TAUX_OVER_QMINUS1}
     */
    MF_COLUMN_D(sMF_COLUMN_HEAD_D_AtQ_As_TAUX_OVER_QMINUS1),
    /**
     * Heading for one of the multifractal spectra columns:
     * {@value #sMF_COLUMN_HEAD_TAU_DDX_AtQ_Q_TIMES_ALPHA_MINUS_F} <br>
     * <br>
     * From {@link #sMF_COLUMN_HEAD_TAU_DDX_AtQ_Q_TIMES_ALPHA_MINUS_F}
     */
    MF_COLUMN_TAU(sMF_COLUMN_HEAD_TAU_DDX_AtQ_Q_TIMES_ALPHA_MINUS_F),
    /**
     * Heading for one of the multifractal spectra columns:
     * {@value #sMF_COLUMN_HEAD_MEANTAU_AtQ_} <br>
     * <br>
     * From {@link #sMF_COLUMN_HEAD_MEANTAU_AtQ_}
     */
    MF_COLUMN_MEAN_TAU(sMF_COLUMN_HEAD_MEANTAU_AtQ_),
    /**
     * Heading for one of the multifractal spectra columns:
     * {@value #sMF_COLUMN_HEAD_alpha_AtQ} <br>
     * <br>
     * From {@link #sMF_COLUMN_HEAD_alpha_AtQ}
     */
    MF_COLUMN_ALPHA(sMF_COLUMN_HEAD_alpha_AtQ),
    /**
     * Heading for one of the multifractal spectra columns =
     * {@link Symbols#sMF_COLUMN_HEAD_fOfAlpha_AtQ} <br>
     * Unicode =
     * "\u2200r\u2208Q\u0307\u200a|\u200a\u0192\u208d\u03b1\u208e\u1d63 \u200a
     * =\u200a-slope of
     * \u0192(\u03b1,\u03b5)\u1d63\u200avs\u200aln(\u03b5\u207b\u00b9)
     * |\u200a\u0192(\u03b1,\u03b5)\u1d63\u200a
     * =\u200a\u2211[\u03bc\u00d7ln(\u03bc)]"
     */
    MF_COLUMN_F_Of_ALPHA(sMF_COLUMN_HEAD_fOfAlpha_AtQ);

    // .........................................................................
    // .................... Fields and Constructors
    // .........................................................................
    /**
     * String initialized to {@link #UNINITIALIZED} =
     * {@value #UNINITIALIZED}
     */
    public String sAllGRIDs = UNINITIALIZED, sOneGRID = UNINITIALIZED,
        sGrayAllGRIDs = UNINITIALIZED, sGrayOneGRID = UNINITIALIZED;

    /**
     *
     */
    EnumString strings = new EnumString();

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the passed
     * parameter.
     */
    ENUM_MULTIFRACTAL_HEADINGS(String psAllGRIDs)
    {
      this.sAllGRIDs = psAllGRIDs;

    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter and {@link #sOneGRID} to the second.
     */
    ENUM_MULTIFRACTAL_HEADINGS(String psAllGRIDs,
                               String psOneGRID)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, and {@link #sGrayAllGRIDs} to
     * the third.
     */
    ENUM_MULTIFRACTAL_HEADINGS(String psAllGRIDs,
                               String psOneGRID,
                               String psGrayAllGRIDs)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
    }

    /**
     * Constructor for enum constants sets {@link #sAllGRIDs} to the first
     * parameter, {@link #sOneGRID} to the second, {@link #sGrayAllGRIDs} to the
     * third, and {@link #sGrayOneGRID} to the fourth.
     */
    ENUM_MULTIFRACTAL_HEADINGS(String psAllGRIDs,
                               String psOneGRID,
                               String psGrayAllGRIDs,
                               String psGrayOneGRID)
    {
      this.sAllGRIDs = psAllGRIDs;
      this.sOneGRID = psOneGRID;
      this.sGrayAllGRIDs = psGrayAllGRIDs;
      this.sGrayOneGRID = psGrayOneGRID;
    }

    // .........................................................................
    // .................... Overridden Methods ...........................
    // .........................................................................
    @Override
    public String toString()
    {
      return this.sAllGRIDs();
    }

    @Override
    public String sAllGRIDs()
    {
      return this.sAllGRIDs;
    }

    @Override
    public String sOneGRID()
    {
      return strings.sOneGRID(sAllGRIDs,
                              sOneGRID,
                              sGrayAllGRIDs,
                              sGrayOneGRID);

    }

    @Override
    public String sGrayAllGRIDs()
    {
      return strings.sGrayAllGRIDs(sAllGRIDs,
                                   sOneGRID,
                                   sGrayAllGRIDs,
                                   sGrayOneGRID);
    }

    @Override
    public String sGrayOneGRID()
    {

      return strings.sGrayOneGRID(sAllGRIDs,
                                  sOneGRID,
                                  sGrayAllGRIDs,
                                  sGrayOneGRID);
    }

  }

}
