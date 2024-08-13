package fraclac.writers;

import fraclac.analyzer.Calculator;
import fraclac.analyzer.Data;
import fraclac.analyzer.DataProcessor;
import fraclac.utilities.ArrayMethods;
import fraclac.utilities.Utils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Methods that return Strings or StringBuilders for writing results from null
 * {@link fraclac.analyzer.Scan#scanBoxCount1SIZE1GRID1Slice box counting}
 * fractal analysis using the FracLac plugin. The class is for making one file
 * per image, in contrast to the {@link fraclac.writers.SummaryStringFormatter}
 * class, which is for writing files containing data from many images.
 *
 *
 * @author Audrey Karperien
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 242 $
 * @version Revision $Id: DataStringFormatter.java 49 2013-01-31 18:04:45Z
 * audrey $
 */
public class DataStringFormatter extends WriteUtilities
{

  /**
   *
   */
  public final static String BS_EP_COUNT_MEANMASS_STDDEV_CVS__L
      = ("sampling size"
      + TAB
      + epsilon
      + TAB
      + "Count"
      + TAB
      + "Mean Pixel Mass"
      + is
      + mu
      + TAB
      + "Std Dev for Mass"
      + is
      + sigma
      + TAB
      + "Lacunarity"
      + is
      + lambda + is + sSIGMA_OVER_MU_SQ + TAB + lambda + PLUS + "1.0" + TAB);

  /**
   *
   */
  static String nextColumn = TAB;

  /**
   * A tabbed string of 6 headings that ends with a TAB; is used for data file
   * {@link #part2ADataFileSlopeColumnsF_SS_ matrices}.
   * <ol>
   * <li>{@link #ELEMENT_SIZE_SIZE ELEMENT_SIZE_SIZE}
   * <li>{@link #COUNT COUNT}
   * <li>{@link #MEAN_MASS MEANMASS}
   * <li>{@link #STD_DEV_FOR_MASS STDDEV}
   * <li>"Squared Coefficient of Variation (" + sSIGMA_OVER_MU_SQ + ") for Mass"
   * + TAB
   * <li>{@link #sSIGMA_OVER_MU_SQ_PLUS_1 PLUS1}
   * </ol>
   */
  public final static String BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1
      = ELEMENT_SIZE_SIZE
      + TAB
      + COUNT
      + TAB
      + MEAN_MASS
      + TAB
      + STD_DEV_FOR_MASS
      + TAB
      + "Squared Coefficient of Variation ("
      + sSIGMA_OVER_MU_SQ
      + ") for Mass" + TAB + sSIGMA_OVER_MU_SQ_PLUS_1 + TAB;

  /**
   *
   */
  public static Map<Headings.EnumLacData, Object> //
      dataFileProbMap = new EnumMap<Headings.EnumLacData, Object>(
          Headings.EnumLacData.class),
      /**
       *
       */
      dataFilePDMap = new EnumMap<Headings.EnumLacData, Object>(
          Headings.EnumLacData.class),
      /**
       *
       */
      dataFileLacMap = new EnumMap<Headings.EnumLacData, Object>(
          Headings.EnumLacData.class);

  /**
   *
   */
  public static Map<Headings.EnumDataFile, Object> //
      dataFileCountMap = new EnumMap<Headings.EnumDataFile, Object>(
          Headings.EnumDataFile.class);
  public static Map<Headings.EnumDataFile, Object> /**
       *
       */
      dataFileMassMap = new EnumMap<Headings.EnumDataFile, Object>(
          Headings.EnumDataFile.class),
      /**
       *
       */
      dataFileSSMap = new EnumMap<Headings.EnumDataFile, Object>(
          Headings.EnumDataFile.class),
      /**
       *
       */
      dataFileSBMap = new EnumMap<Headings.EnumDataFile, Object>(
          Headings.EnumDataFile.class),
      /**
       *
       */
      dataFileMinMap = new EnumMap<Headings.EnumDataFile, Object>(
          Headings.EnumDataFile.class),
      /**
       *
       */
      dataFileMaxMap = new EnumMap<Headings.EnumDataFile, Object>(
          Headings.EnumDataFile.class);

  /**
   *
   */
  public static Map<Headings.EnumInfoData, Object> infoMap
      = new EnumMap<Headings.EnumInfoData, Object>(
          Headings.EnumInfoData.class);

  /**
   * Makes a StringBuilder of data to write to a text file; the data are from
   * the passed DataProcessor instance and include counts from box counting and
   * other types of scans done with FracLac for ImageJ.
   *
   * <h5>Basic Structure</h5> Iterates once for
   * {@link fraclac.analyzer.Vars#iNumGrids GRID} from the passed Vars.
   *
   * For each GRID, it calls 2 functions, passing in parameters from the
   * parameters passed in in the first place. These return a string that is
   * appended to the returned master string:
   * <ol>
   * <li>{@link #part1DataFileRowsThisGRID}
   * <li>{@link #part2DataFileColumnsBoxCount}
   * </ol>
   *
   *
   * @param psbaTitles SringBuilder array of image title and ROI information
   * @param psScanType string for describing the type of scan
   * @param pDP DataProcessor that has been primed by a scan
   * @param pi2dSIZEs array of sampling {@link fraclac.analyzer.GridSet#i2dSIZEs
   *            SIZEs} used
   * @param pd2dEpsilons array of sampling SIZEs relative to image size
   *
   *
   * @return StringBuilder for EnumDataFile
   *
   */
  public static StringBuilder[] get2PartDataStringAndHeadsSlice(
      StringBuilder[] psbaTitles,// fixme see pDP
      String psScanType,
      DataProcessor pDP,
      int[][] pi2dSIZEs,
      double[][] pd2dEpsilons)
  {
    // .........................................................................
    // ...................... Get part 1 ...........................
    // .........................................................................
    StringBuilder[] lsbData = new StringBuilder[3];
    lsbData[0] = new StringBuilder();
    lsbData[1] = new StringBuilder();
    lsbData[2] = new StringBuilder();

    for (int liGRID = 0; liGRID < pDP.scan.vars.iNumGrids; liGRID++) {

      String[] lsArray
          = part1DataFileRowsThisGRID(liGRID,
                                      psbaTitles[liGRID].toString(),
                                      psScanType,
                                      pDP);

      lsbData[0].append(lsArray[0])
          .append(newline);

      if (liGRID == 0) {
        lsbData[1].append(lsArray[1]);
      }
    }
    // .........................................................................
    // ...................... Get part 2 ...........................
    // .........................................................................
    lsbData[2].append(part2DataFileColumnsBoxCount(pi2dSIZEs,
                                                   pd2dEpsilons,
                                                   pDP.scan.vars.iNumGrids,
                                                   pDP));

    return lsbData;
  }

  /**
   * Returns a StringBuilder for the smoothed data file.
   *
   * The string is made by the following sequence:
   * <ol>
   * <li>{@link #part1DataFileRowsFMinFMaxOptionalFS rows of summary data} per
   * GRID
   * <li>all titles for all {@link fraclac.analyzer.Vars#iNumGrids GRID}s
   * <li>{@link #part2DataFileSlopeColumnsFMinFMaxCoverOptionalFs slopes and
   * columns of data}
   * </ol>
   *
   *
   * @param psbaTitles StringBuilder array
   * @param pDP FLDataProcessor
   * @param i2dSIZEs int array
   * @param d2dEpsilons double []
   * @param pdEpsilonFactor double
   *
   *
   * @return StringBuilder
   */
  public static StringBuilder get2PartDataFileThisSliceFMinCoverOptionalSmooth(
      StringBuilder[] psbaTitles,
      DataProcessor pDP,
      int[][] i2dSIZEs,
      double[][] d2dEpsilons,
      double pdEpsilonFactor)
  {
    int piGRIDs = pDP.scan.vars.iNumGrids;
    StringBuilder lsbDataString = new StringBuilder(0);
    // ==================== append rows of filtered data ======================
    lsbDataString.append(part1DataFileRowsFMinFMaxOptionalFS(piGRIDs,
                                                             pDP))
        // ======= append heading then title for each scan location ===========
        .append(newline)
        .append("FILENAME & Scan Position")
        .append(newline);
    for (int liGRID = 0; liGRID < piGRIDs; liGRID++) {
      lsbDataString
          .append(psbaTitles[liGRID])
          .append(newline);
    }
    // =============== append a line of slopes and columns of data =============
    lsbDataString.append(part2DataFileSlopeColumnsFMinFMaxCoverOptionalFs(
        pDP,
        pdEpsilonFactor));
    // ================== return the entire string=============================
    return lsbDataString;
  }

  /**
   * Returns a StringBuilder holding smoothed filtered data for the data file.
   *
   * Calls 3 functions:
   *
   * <ol>
   * <li>Calls {@link #part1DataFileRowsF_SS_AndFsBAtThisGRID} once for each
   * GRID.
   * <li>{@link #part2ADataFileSlopeColumnsF_SS_}
   * <li>{@link #part2BDataFileSlopeColumnsFsB}
   * </ol>
   *
   * @param sbaTitles
   * @param pDP
   * @param pdEpsilonFactor double
   *
   *
   * @return StringBuilder
   */
  public static StringBuilder get3partDataFileThisSliceF_SS_AndFsB(
      StringBuilder[] sbaTitles,
      DataProcessor pDP,
      double pdEpsilonFactor)
  {
    int liGRIDs = pDP.scan.vars.iNumGrids;

    StringBuilder lsbFilteredSmoothed = new StringBuilder("");
    // ============= append a summary string for each scan location=============
    for (int g = 0; g < liGRIDs; g++) {
      lsbFilteredSmoothed
          .append(
              part1DataFileRowsF_SS_AndFsBAtThisGRID(g,
                                                     sbaTitles[g].toString(),
                                                     pDP))
          .append(newline);
    }
    // ===== append lines of slopes and columns of data for each filter=========
    lsbFilteredSmoothed.append(part2ADataFileSlopeColumnsF_SS_(liGRIDs,
                                                               pDP,
                                                               pdEpsilonFactor,
                                                               sbaTitles)).
        append(part2BDataFileSlopeColumnsFsB(liGRIDs,
                                             pDP,
                                             pdEpsilonFactor,
                                             sbaTitles));
    // =======================return the string=================================
    return lsbFilteredSmoothed;
  }

  /**
   * Returns a String array with a tabbed string of data in the first position
   * and one of headings in the second, holding data for one
   * {@link fraclac.analyzer.Vars#iNumGrids GRID}.
   *
   * Prior to making the enumStrings it calls {@link #loadMaps}, then it calls
   * {@link #appendTabbedHeadingsForSingleGrid} for each map in the class and
   * appends the values to a StringBuilder.
   *
   *
   * @param piGRID int for the current {@link fraclac.analyzer.Vars#iNumGrids
   *            GRID}
   * @param psImageInfo String for the file information column
   * @param psScanType
   * @param pDP {@link DataProcessor} containing preprocessed statistics
   *
   *
   * @return String[]
   */
  public static String[] part1DataFileRowsThisGRID(int piGRID,
                                                   String psImageInfo,
                                                   String psScanType,
                                                   DataProcessor pDP)
  {
    // .........................................................................
    //                  Prepare maps and StringBuilders
    // .........................................................................
    loadMaps(psImageInfo,
             psScanType,
             pDP,
             piGRID);

    StringBuilder lsbData = new StringBuilder();
    StringBuilder lsbHeadings = new StringBuilder();
    // .........................................................................
    //  Make a string of data and one of headings.
    //  The headings string is only needed once, when the
    //  file is written or shown in a TextWindow, but is
    // generated with each call in case there is only one
    // string written before the file is made.
    // FIXME to flag the method to only make the headings when needed.
    // ================== append file name and info ============================
    lsbData.append(infoMap.get(Headings.EnumInfoData.INFO)).append(TAB);
    lsbHeadings.append(Headings.EnumInfoData.INFO).append(TAB);
    // ======================= append count data================================
    lsbData.append(appendTabbedData(dataFileCountMap));
    lsbHeadings
        .append(appendTabbedHeadingsForSingleGrid(
                dataFileCountMap.get(Headings.EnumDataFile.NAME).toString(),
                dataFileCountMap.keySet()));
    // ==================== append mass data====================================
    lsbData.append(appendTabbedData(dataFileMassMap));
    lsbHeadings.append(appendTabbedHeadingsForSingleGrid(dataFileMassMap
        .get(Headings.EnumDataFile.NAME).toString(),
                                                         dataFileMassMap
                                                         .keySet()));
    // ================append smoothed smallest filtered data===================
    lsbData.append(appendTabbedData(dataFileSSMap));
    lsbHeadings.append(appendTabbedHeadingsForSingleGrid(
        dataFileSSMap.get(Headings.EnumDataFile.NAME).toString(),
        dataFileSSMap.keySet()));
    // ============ append smoothed biggest filtered map========================
    lsbData.append(appendTabbedData(dataFileSBMap));
    lsbHeadings.append(appendTabbedHeadingsForSingleGrid(
        dataFileSBMap.get(Headings.EnumDataFile.NAME).toString(),
        dataFileSBMap.keySet()));
    // ================= append lacunarity data=================================
    lsbData.append(appendTabbedData(dataFileLacMap));
    lsbHeadings.append(appendTabbedHeadingsForSingleGrid(dataFileLacMap
        .get(Headings.EnumLacData.NAME).toString(),
                                                         dataFileLacMap
                                                         .keySet()));
    // =================== append probabilities lacunarity data=================
    lsbData.append(appendTabbedData(dataFileProbMap));
    lsbHeadings.append(appendTabbedHeadingsForSingleGrid(dataFileProbMap
        .get(Headings.EnumLacData.NAME).toString(),
                                                         dataFileProbMap
                                                         .keySet()));
    // ============ append probability distribution lacunarity data=============
    lsbData.append(appendTabbedData(dataFilePDMap));
    lsbHeadings.append(appendTabbedHeadingsForSingleGrid(
        dataFilePDMap.get(Headings.EnumLacData.NAME).toString(),
        dataFilePDMap.keySet()));
    // .........................................................................
    // ................return an array of data and headings.....................
    // .........................................................................
    return new String[]{lsbData.toString(), lsbHeadings.toString()};

  }

  /**
   * Calls functions to load the data maps in the class using the passed
   * DataProcessor, for the passed {@link fraclac.analyzer.Vars#iNumGrids }.
   *
   * @param psImageInfo
   * @param psScanType
   * @param pDP
   * @param piGRID
   */
  public static void loadMaps(String psImageInfo,
                              String psScanType,
                              DataProcessor pDP,
                              int piGRID)
  {
    infoMap.put(Headings.EnumInfoData.INFO,
                psImageInfo);

    dataFileCountMap.put(Headings.EnumDataFile.SCAN_TYPE,
                         psScanType
                         + " at " + sGSetMember + (piGRID + 1));

    loadFractalDimensionData(piGRID,
                             pDP);

    if (pDP.scan.vars.bDoSmoothed) {

      loadSmoothedFilterData(piGRID,
                             pDP);
    } else {
      primeSmoothedMaps();
    }

    loadLacunarityMap(piGRID,
                      pDP);

    if (pDP.scan.vars.iMaxFrequencies > 0) {

      loadProbabilityMaps(piGRID,
                          pDP);
      loadPDMaps(piGRID,
                 pDP);
    } else {
      primeProbMaps();
    }

    loadCountVsOMEGADataInDataString(piGRID,
                                     pDP);

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  static void loadCountVsOMEGADataInDataString(int piGRID,
                                               DataProcessor pDP)
  {

    infoMap.put(
        Headings.EnumInfoData.MEAN_CV_FOR_COUNT_FROM_ALL_GRID_SCANS,
        (pDP.data.daCvForCountOrSumDeltaIAtGRID[piGRID]));

    infoMap.put(
        Headings.EnumInfoData.MEAN_CV_COUNT_VS_MEAN_CV_OMEGA_ALL_GRIDS,
        (pDP.data.daCvForCountOrSumDeltaIAtGRID[piGRID]
        / pDP.data.daCvForOMEGACountAtGRID[piGRID]));

  }

  /**
   *
   * @param <EXT_EnumLacData>
   * @param piGRID
   * @param pDP
   */
  static <EXT_EnumLacData extends Headings.EnumLacData> void loadLacunarityMap(
      int piGRID,
      DataProcessor pDP)
  {
    Map<Headings.EnumLacData, Object> lMap = dataFileLacMap;

    lMap.put(EXT_EnumLacData.NAME,
             LAMBDA_AT_grid);

    lMap.put(EXT_EnumLacData.DEFINITION_OF_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);

    lMap.put(
        EXT_EnumLacData.DEFINITION_OF_lambda,
        "for "
        + (pDP.scan.vars.isGray() ? GrayFormat
            .stringForIntensityCalculation(pDP.scan.vars)
            : MASS));

    lMap.put(EXT_EnumLacData.MEAN_LAC,
             (pDP.data.daLAMBDAPixOrDeltaIMeanCvSqAtGRID[piGRID]));

    lMap.put(EXT_EnumLacData.CV_FOR_MEAN_LAC,
             (pDP.data.daCVForLAMBDACvSqForPixOrdeltaIAtGRID[piGRID]));

    lMap.put(
        EXT_EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE,
        (pDP.data.daLAMBDASlopeCvSqPlus1VsSIZEForPixOrdeltaIAtGRID[piGRID]));

    lMap.put(EXT_EnumLacData.MEAN_LAC_FOR_OMEGA,
             (pDP.data.daLAMBDAOMEGAMeanCvSqAtGRID[piGRID]));

    lMap.put(
        EXT_EnumLacData.CV_FOR_LAC_FOR_OMEGA,
        (pDP.data.daCVForLAMBDACvSqForOMEGAPixOrdeltaIAtGRID[piGRID]));

    lMap.put(EXT_EnumLacData.MEAN_LAMBDAD,
             (pDP.data.daLambdaDAtGrid[piGRID]));

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  public static void loadProbabilityMaps(int piGRID,
                                         DataProcessor pDP)
  {

    Map<Headings.EnumLacData, Object> lMap = dataFileProbMap;

    lMap.put(Headings.EnumLacData.NAME,
             AllGsHeadings.PROB);

    lMap.put(
        Headings.EnumLacData.MEAN_LAC,
        (pDP.data.daLAMBDAUnweightedProbMeanCvSqPixOrdeltaIAtGRID[piGRID]));

    lMap.put(
        Headings.EnumLacData.CV_FOR_MEAN_LAC,
        (pDP.data.daCVForLAMBDACvSqForPixOrdeltaIUnweightedProbAtGRID[piGRID]));

    lMap.put(
        Headings.EnumLacData.MEAN_LAC_FOR_OMEGA,
        (pDP.data.daLAMBDAOMEGAUnweightedProbMeanCvSqAtGRID[piGRID]));

    lMap.put(
        Headings.EnumLacData.CV_FOR_LAC_FOR_OMEGA,
        (pDP.data.daCVForLAMBDACvSqUnweightedProbForOMEGAAtGRID[piGRID]));

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  public static void loadPDMaps(int piGRID,
                                DataProcessor pDP)
  {

    Map<Headings.EnumLacData, Object> lMap = dataFilePDMap;
    lMap.put(Headings.EnumLacData.NAME,
             AllGsHeadings.PD);

    lMap.put(Headings.EnumLacData.MEAN_LAC,
             (pDP.data.daLAMBDAWeightedPDMeanCvSqAtGRID[piGRID]));

    lMap.put(
        Headings.EnumLacData.CV_FOR_MEAN_LAC,
        (pDP.data.daCVForLAMBDACvSqForPixOrdeltaIWeightedPDAtGRID[piGRID]));

    lMap.put(
        Headings.EnumLacData.MEAN_LAC_FOR_OMEGA,
        (pDP.data.daLAMBDAMeanCvSqWeightedPDForOMEGAAtGRID[piGRID]));

    lMap.put(
        Headings.EnumLacData.CV_FOR_LAC_FOR_OMEGA,
        (pDP.data.daCVForLAMBDACvSqWeightedPDForOMEGAAtGRID[piGRID]));

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  public static void loadFractalDimensionData(int piGRID,
                                              DataProcessor pDP)
  {
    loadCountMap(piGRID,
                 pDP);

    loadMassMap(piGRID,
                pDP);

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  static void loadMassMap(int piGRID,
                          DataProcessor pDP)
  {
    Map<Headings.EnumDataFile, Object> lMap = dataFileMassMap;

    lMap.put(Headings.EnumDataFile.NAME,
             Dm);

    lMap.put(Headings.EnumDataFile.OPTIMAL_FD,
             (-pDP.data.daDmForMeanPixOrDeltaIAtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.R_SQ_OPTIMAL,
             (pDP.data.daRSqForDmFromMeanPixOrDeltaIAtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.SE_OPTIMAL,
             (pDP.data.daStdErrForDmFromMeanPixOrDeltaIAtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.YINT_OPTIMAL,
             (pDP.data.daYIntForDmFromMeanPixOrDeltaIAtGRID[piGRID]));

    lMap.put(
        Headings.EnumDataFile.PREFACTOR,
        (pDP.data.daPrefactorForDmForMeanPixOrDeltaIAtGRID[piGRID]));

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  static void loadCountMap(int piGRID,
                           DataProcessor pDP)
  {
    Map<Headings.EnumDataFile, Object> lMap = dataFileCountMap;

    lMap.put(Headings.EnumDataFile.NAME,
             Db);

    lMap.put(Headings.EnumDataFile.OPTIMAL_FD,
             (pDP.data.daDBFromCountOrSumdeltaIAtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.R_SQ_OPTIMAL,
             (pDP.data.daRSqForDBFromCountOrSumdeltaIAtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.SE_OPTIMAL,
             (pDP.data.daStdErrForDBFromCountOrSumdeltaIAtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.YINT_OPTIMAL,
             (pDP.data.daYIntForDBFromCountOrSumdeltaIAtGRID[piGRID]));

    lMap.put(
        Headings.EnumDataFile.PREFACTOR,
        (pDP.data.daPrefactorForDBFromCountOrSumdeltaIAtGRID[piGRID]));

  }//
  
  
  /**
   *
   * @param piGRID
   * @param pDP
   */
  public static void loadSmoothedFilterData(int piGRID,
                                            DataProcessor pDP)
  {

    loadSSMap(piGRID,
              pDP);

    loadSBMap(piGRID,
              pDP);

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  static void loadSSMap(int piGRID,
                        DataProcessor pDP)
  {
    Map<Headings.EnumDataFile, Object> lMap = dataFileSSMap;

    lMap.put(Headings.EnumDataFile.NAME,
             DbFSS);

    lMap.put(Headings.EnumDataFile.SIZES,
             (pDP.data.d2dSIZEsForF_SS_AtSIZEOnGRID[piGRID].length));

    lMap.put(Headings.EnumDataFile.OPTIMAL_FD,
             (pDP.data.daDB_F_SS_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.R_SQ_OPTIMAL,
             (pDP.data.daRSqForDB_F_SS_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.SE_OPTIMAL,
             (pDP.data.daStdErrForDB_FSS_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.YINT_OPTIMAL,
             (pDP.data.daYIntForDB_F_SS_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.PREFACTOR,
             (pDP.data.daPrefactorForDB_FSS_AtGRID[piGRID]));

  }

  /**
   *
   * @param piGRID
   * @param pDP
   */
  static void loadSBMap(int piGRID,
                        DataProcessor pDP)
  {
    Map<Headings.EnumDataFile, Object> lMap = dataFileSBMap;

    lMap.put(Headings.EnumDataFile.NAME,
             DbFSB);

    lMap.put(Headings.EnumDataFile.SIZES,
             (pDP.data.d2dSIZEsForFsBAtSIZEOnGRID[piGRID].length));

    lMap.put(Headings.EnumDataFile.OPTIMAL_FD,
             (pDP.data.daDB_FSB_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.R_SQ_OPTIMAL,
             (pDP.data.daRSqForDB_FSB_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.SE_OPTIMAL,
             (pDP.data.daStdErrForDB_FSB_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.YINT_OPTIMAL,
             (pDP.data.daYIntForDB_FSB_AtGRID[piGRID]));

    lMap.put(Headings.EnumDataFile.PREFACTOR,
             (pDP.data.daPrefactorForDB_FSB_AtGRID[piGRID]));

  }

  /**
   *
   */
  static void primeSmoothedMaps()
  {
    Set<Headings.EnumDataFile> keySet = EnumSet.of(
        Headings.EnumDataFile.SIZES,
        Headings.EnumDataFile.OPTIMAL_FD,
        Headings.EnumDataFile.R_SQ_OPTIMAL,
        Headings.EnumDataFile.SE_OPTIMAL,
        Headings.EnumDataFile.YINT_OPTIMAL,
        Headings.EnumDataFile.PREFACTOR);

    Map<Headings.EnumDataFile, Object> lMap = dataFileSBMap;

    lMap.put(Headings.EnumDataFile.NAME,
             DbFSB);

    for (Headings.EnumDataFile key : keySet) {

      lMap.put(key,
               NC);
    }

    lMap = dataFileSSMap;

    lMap.put(Headings.EnumDataFile.NAME,
             DbFSS);

    for (Headings.EnumDataFile key : keySet) {
      lMap.put(key,
               NC);
    }

  }

  /**
   *
   */
  static void primeProbMaps()
  {
    Map<Headings.EnumLacData, Object> lMap = dataFileProbMap;

    lMap.put(Headings.EnumLacData.NAME,
             AllGsHeadings.PROB);

    Set<Headings.EnumLacData> keySet = EnumSet.of(
        Headings.EnumLacData.MEAN_LAC,
        Headings.EnumLacData.CV_FOR_MEAN_LAC,
        Headings.EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE,
        Headings.EnumLacData.MEAN_LAC_FOR_OMEGA,
        Headings.EnumLacData.CV_FOR_LAC_FOR_OMEGA);

    for (Headings.EnumLacData key : keySet) {
      lMap.put(key,
               NC);
    }

    lMap = dataFilePDMap;

    lMap.put(Headings.EnumLacData.NAME,
             AllGsHeadings.PD);

    for (Headings.EnumLacData key : keySet) {
      lMap.put(key,
               NC);
    }

  }

  /**
   * Returns a StringBuilder with from one to three lines of summary data (e.g.,
   * the fractal dimension, r&sup2;, y-intercept, coefficient of variation,
   * etc.) from the passed DataProcessor.
   *
   * Each line has 13 values. The first line is data for the Min-Max Cover over
   * all grid locations and then if the pbDoSmoothed is true, the next are for
   * smoothed smallest and smoothed biggest data.
   *
   *
   * @param i int
   * @param pDP dataProcessor
   *
   * @return StringBuilder
   */
  public static StringBuilder part1DataFileRowsFMinFMaxOptionalFS(
      int i,
      DataProcessor pDP)
  {
    StringBuilder data = new StringBuilder(
        "Min-Max Cover From All Locations"
        + TAB
        + (pDP.data.cFMinCover.dFractalDimension)
        + TAB
        + (pDP.data.cFMinCover.dRSq)
        + TAB
        + (pDP.data.cFMinCover.dYIntercept)
        + TAB
        + (pDP.data.cFMinCover.dPrefactor)
        + TAB
        + (pDP.data.cFMinCover.dStdErr)
        + TAB
        + (meanOfArray(pDP.data.dalambdaFMinCvSqPixPerSIZE))
        + TAB
        + (pDP.data.cFMaxCover.dFractalDimension)
        + TAB
        + (pDP.data.cFMaxCover.dRSq)
        + TAB
        + (pDP.data.cFMaxCover.dYIntercept)
        + TAB
        + (pDP.data.cFMaxCover.dPrefactor)
        + TAB
        + (pDP.data.cFMaxCover.dStdErr)
        + TAB
        + (meanOfArray(pDP.data.dalambdaFMaxCvSqPixPerSIZE))
        + TAB);

    if (pDP.scan.vars.bDoSmoothed) {// if smoothed and FMinCover together,
      // then print
      // minsb, minss, maxsb, maxss also
      // add newlines between sets so that headings line up
      data.append(newline)
          .append("Min-Max Cover" + SSF)
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dRSqForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dYintForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dPrefactorForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dSEForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(ArrayMethods
                  .meanOfArray(pDP.data.cFMinCover.daF_SS_CvSq)))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dRSqForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dYintForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dPrefactorForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dSEForDB_F_SS))
          .append(TAB)
          .append(Utils.fnum(ArrayMethods
                  .meanOfArray(pDP.data.cFMaxCover.daF_SS_CvSq)));

      data.append(newline + "Min-Max Cover" + FOR + SBF + TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dRSqDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dYintForDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dPrefactorDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMinCover.dSEForDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(ArrayMethods
                  .meanOfArray(pDP.data.cFMinCover.daF_SB_CvSq)))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dRSqDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dYintForDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dPrefactorDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(pDP.data.cFMaxCover.dSEForDB_F_SB))
          .append(TAB)
          .append(Utils.fnum(ArrayMethods
                  .meanOfArray(pDP.data.cFMaxCover.daF_SB_CvSq)));
    }
    return data;
  }

  /**
   *
   * @param piLoc
   * @param psTitle
   * @param pDP
   * @return
   */
  public static String part1DataFileRowsF_SS_AndFsBAtThisGRID(int piLoc,
                                                              String psTitle,
                                                              DataProcessor pDP)
  {
    String data = psTitle
        + TAB
        + Utils.fnum(pDP.data.daDB_F_SS_AtGRID[piLoc])
        + TAB
        + Utils.fnum(pDP.data.daYIntForDB_F_SS_AtGRID[piLoc])
        + TAB
        + Utils.fnum(pDP.data.daPrefactorForDB_FSS_AtGRID[piLoc])
        + TAB
        + Utils.fnum(pDP.data.daRSqForDB_F_SS_AtGRID[piLoc])
        + TAB
        + Utils.fnum(pDP.data.daStdErrForDB_FSS_AtGRID[piLoc])
        + TAB
        + Utils.fnum(pDP.data.d2dSIZEsForF_SS_AtSIZEOnGRID[piLoc].length)
        + TAB + Utils.fnum(pDP.data.daLAMBDAF_SS_MeanCvSqAtGRID[piLoc])
        + TAB + Utils.fnum(pDP.data.daDB_FSB_AtGRID[piLoc]) + TAB
        + Utils.fnum(pDP.data.daYIntForDB_FSB_AtGRID[piLoc]) + TAB
        + Utils.fnum(pDP.data.daPrefactorForDB_FSB_AtGRID[piLoc]) + TAB
        + Utils.fnum(pDP.data.daRSqForDB_FSB_AtGRID[piLoc]) + TAB
        + Utils.fnum(pDP.data.daStdErrForDB_FSB_AtGRID[piLoc]) + TAB
        + Utils.fnum(pDP.data.d2dSIZEsForFsBAtSIZEOnGRID[piLoc].length)
        + TAB + Utils.fnum(pDP.data.daLAMBDAFsBMeanCvSqAtGRID[piLoc]);

    return data;
  }

  /**
   * Returns a string of data for the box counts Data File.
   *
   * The string has
   *
   * {@link fraclac.writers.WriteUtilities#numberTabAndRepeatEachElementXTimes
   * headings} then {@link #stringOfNthElementsFromAllArrays columns} of
   * sampling sizes and &epsilon;s with corresponding data and
   * {@link #rowOfSlopes slopes} for various variables at each sampling size and
   * grid position.
   *
   * It is called by a driver {@link #get2PartDataStringAndHeadsSlice method}.
   *
   * <h5>Basic Format of String</h5>
   *
   * <table border = 0 >
   * <tr>
   * <td colspan = 6>
   * Slopes for Data vs &epsilon; <i>Note: &epsilon; is the passed Epsilons
   * array and each data type array is stored in the passed DataProcessor, where
   * there is an array of each data type for each grid (g)</i></td>
   * </tr>
   * <tr>
   * <td>Formula 1 g1</td>
   * <td>Formula 1 g2</td>
   * <td>...Formula 1 gN</td>
   * <td>Formula 2 g1</td>
   * <td>Formula 2 g2</td>
   * <td>...Formula 2 gN</td>
   * </tr>
   * <tr>
   * <td>Slope 1 g1 vs &epsilon;</td>
   * <td>Slope 1 g2 vs &epsilon;</td>
   * <td>...Slope 1 gN vs &epsilon;</td>
   * <td>Slope 2 g1 vs &epsilon;</td>
   * <td>Slope 2 g2 vs &epsilon;</td>
   * <td>...Slope 2 gN vs &epsilon;</td>
   * </tr>
   * <tr>
   * <td>DATA</td>
   * </tr>
   * <tr>
   * <td>Data Type Heading 1 g1</td>
   * <td>Data Type Heading 1 g2</td>
   * <td>...Data Type Heading 1 gN</td>
   * <td>Data Type Heading 2 g1</td>
   * <td>Data Type Heading 2 g2</td>
   * <td>...Data Type Heading 2 gN</td>
   * </tr>
   * <tr>
   * <td>DataPoint 1a g1</td>
   * <td>DataPoint 1a g2</td>
   * <td>...DataPoint 1a gN</td>
   * <td>DataPoint 2a g1</td>
   * <td>DataPoint 2a g2</td>
   * <td>...DataPoint 2a gN</td>
   * </tr>
   * <tr>
   * <td>DataPoint 1b g1</td>
   * <td>DataPoint 1b g2</td>
   * <td>...DataPoint 1b gN</td>
   * <td>DataPoint 2b g2</td>
   * <td>DataPoint 2b g2</td>
   * <td>...DataPoint 2b gN</td>
   * </tr>
   * <tr>
   * <td>DataPoint 1c g1</td>
   * <td>DataPoint 1c g2</td>
   * <td>...DataPoint1c gN</td>
   * <td>DataPoint 2c g2</td>
   * <td>DataPoint 2c g2</td>
   * <td>...DataPoint 2c gN</td>
   * </tr>
   * </table>
   *
   * The data types used from the DataProcessor are:
   * <ul>
   * <li> {@link Data#d2dCountAtSIZEOnGRID }
   * <li> {@link Data#d2dOMEGACountAtSIZEOnGRID }
   * <li> {@link Data#d2dMeanPixOrDeltaIPerSampleAtSIZEOnGRID }
   * <li> {@link Data#d2dStdDevPixOrdeltaIPerSampleAtSIZEOnGRID }
   * <li> {@link Data#d2dlambdaCvSqPixOrDeltaIPerSampleAtSIZEOnGRID }
   *
   * <li> {@link Data#d2dMeanOMEGAPixOrdeltaIAtSIZEOnGRID }
   * <li> {@link Data#d2dStdDevsForOMEGAPixOrdeltaIAtSIZEOnGRID }
   * <li> {@link Data#d2dlambdaCvSqsOMEGAPixOrdeltaIAtSIZEOnGRID }
   *
   * <li> {@link Data#d2dMeanOfUnweightedProbAtSIZEOnGRID }
   * <li> {@link Data#d2dStdDevOfUnweightedProbAtSIZEOnGRID }
   * <li> {@link Data#d2dlambdaUnweightedProbCvSqOverBinsAtSIZEOnGRID }
   *
   * <li> {@link Data#d2dMeanWeightedPDForOMEGAAtSIZEOnGRID }
   * <li> {@link Data#d2dStdDevWeightedPDForOMEGAAtSIZEOnGRID }
   * <li> {@link Data#d2dlambdaOMEGAWeightedPDCvSqOverBinsAtSIZEOnGRID }
   * </ul>
   *
   * For lacunarity values, marked * above, {@link ArrayMethods#plus1 adds 1}
   * prior to calculating.
   *
   *
   * @param pi2dSIZEs int [][]
   * @param pd2dEpsilons double [][]
   * @param piGRIDPositions int
   * @param pDP DataProcessor
   *
   *
   * @return String of multiple lines of data for display
   */
  public static String part2DataFileColumnsBoxCount(int[][] pi2dSIZEs,
                                                    double[][] pd2dEpsilons,
                                                    int piGRIDPositions,
                                                    DataProcessor pDP)
  {
    int liEpsilons = sizeOfLongestArray(pi2dSIZEs);
    int liGrids = piGRIDPositions;

    double[][][] ld3dArraysOfValuesAtEachOrientationAtEachSize
        = new double[][][]{
          intToDoubleArray(pi2dSIZEs),
          pd2dEpsilons,
          //
          pDP.data.d2dCountAtSIZEOnGRID,
          pDP.data.d2dOMEGACountAtSIZEOnGRID,
          //
          pDP.data.d2dMeanPixOrDeltaIPerSampleAtSIZEOnGRID,
          pDP.data.d2dStdDevPixOrdeltaIPerSampleAtSIZEOnGRID,
          ArrayMethods
          .plus1(pDP.data.d2dlambdaCvSqPixOrDeltaIPerSampleAtSIZEOnGRID),
          //
          pDP.data.d2dMeanOMEGAPixOrdeltaIAtSIZEOnGRID,
          pDP.data.d2dStdDevsForOMEGAPixOrdeltaIAtSIZEOnGRID,
          ArrayMethods
          .plus1(pDP.data.d2dlambdaCvSqsOMEGAPixOrdeltaIAtSIZEOnGRID),
          //
          pDP.data.d2dMeanOfUnweightedProbAtSIZEOnGRID,
          pDP.data.d2dStdDevOfUnweightedProbAtSIZEOnGRID,
          ArrayMethods
          .plus1(pDP.data.d2dlambdaUnweightedProbCvSqOverBinsAtSIZEOnGRID),
          //
          pDP.data.d2dMeanWeightedPDForOMEGAAtSIZEOnGRID,
          pDP.data.d2dStdDevWeightedPDForOMEGAAtSIZEOnGRID,
          ArrayMethods
          .plus1(pDP.data.d2dlambdaOMEGAWeightedPDCvSqOverBinsAtSIZEOnGRID)};

    String[] lsaHeadingsForRowsOfSlopes = {
      "sampling size SIZE",
      epsilon,
      //
      Db + "(counts) = " + slopeVsE("F") + ": ",
      slopeVsE(OMEGA) + ":",
      //
      Dm + " (mean M) = " + slopeVsE("F" + mu) + ": ",
      slopeVsE("F" + sigma) + ": ",
      //
      "F" + sSIGMA_OVER_MU_SQ + "1: " + slopeVsE("F" + lambda) + ": ",
      //
      slopeVsE(OMEGA + mu) + ": ",
      slopeVsE(OMEGA + sigma) + ": ",
      slopeVsE(OMEGA + lambda) + ": ",
      //
      "Dbp = " + slopeVsE("FB" + mu) + ": ",
      slopeVsE("FB" + sigma) + ": ",
      slopeVsE("FB" + lambda) + ": ",
      //
      slopeVsE(OMEGA + "B" + mu) + ": ",
      slopeVsE(OMEGA + "B" + sigma) + ": ",
      slopeVsE(OMEGA + "B" + lambda) + ": "};

    StringBuilder lsbData = new StringBuilder("");

    lsbData.append(newline)
        .append("Slopes for Data vs ")
        .append(epsilon)
        .append(newline)
        .append(WriteUtilities.numberTabAndRepeatEachElementXTimes(
                lsaHeadingsForRowsOfSlopes,
                liGrids,
                sGSetMember))
        .append(newline)
        .append(rowOfSlopes(
                ld3dArraysOfValuesAtEachOrientationAtEachSize,
                pd2dEpsilons))
        .append(newline)
        .append("DATA")
        .append(newline)
        .append(WriteUtilities.numberTabAndRepeatEachElementXTimes(
                Headings.SA_DATA_COLUMN_HEADINGS,
                liGrids,
                sGSetMember))
        .append(newline);

    // for each sampling size, write a line in each data column
    for (int liIndexOfEachEpsilon = 0;
        liIndexOfEachEpsilon < liEpsilons; liIndexOfEachEpsilon++) //
    {
      lsbData.append(
          stringOfNthElementFromAllArrays(
              ld3dArraysOfValuesAtEachOrientationAtEachSize,
              liIndexOfEachEpsilon,
              liEpsilons,
              liGrids))
          .append(newline);
    }
    lsbData.append(newline);
    return lsbData.toString();
  }

  /**
   *
   * @param string
   * @return
   */
  static String slopeVsE(String string)
  {
    return WriteUtilities.slopeVsE(string);
  }

  // ******************************************************************************
  /**
   * Returns a string for the data file from a box count optimized using the
   * smoothed smallest method. The string contains data from the following
   * dataTypes arrays for the passed number of grids:
   * <ol>
   * <li>{@link fraclac.analyzer.Data#d2dSIZEsForF_SS_AtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dCountsForF_SS_AtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dMeanPixF_SS_AtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dStdDevMassF_SS_AtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dlambdaF_SS_CvSqAtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dlambdaF_SS_CvSqPlus1AtSIZEOnGRID}
   * </ol>
   * Calls the {@link #makeSlopesMatrix makeSlopesMatrix} method, passing in an
   * array made of the above dataTypes arrays against sizes.
   *
   *
   * @param piGRIDPositions
   * @param pDP
   * @param pdEpsilonFactor
   * @param sbaTitles StringBuilder []
   *
   *
   * @return StringBuilder matrix of data and slopes
   */
  public static StringBuilder part2ADataFileSlopeColumnsF_SS_(
      int piGRIDPositions,
      DataProcessor pDP,
      double pdEpsilonFactor,
      StringBuilder[] sbaTitles)
  {
    int liGrids = piGRIDPositions;
    StringBuilder lsbMatrix = new StringBuilder(SSF);

    for (int liThisGrid = 0; liThisGrid < liGrids; liThisGrid++) {
      // once for each grid location (i.e., row of data)
      double[] ldaEpsilons = divideArrayBy(
          pDP.data.d2dSIZEsForF_SS_AtSIZEOnGRID[liThisGrid],
          pdEpsilonFactor);

      double[][] ld2dDataTypes = new double[][]{
        pDP.data.d2dSIZEsForF_SS_AtSIZEOnGRID[liThisGrid],
        pDP.data.d2dCountsForF_SS_AtSIZEOnGRID[liThisGrid],
        pDP.data.d2dMeanPixF_SS_AtSIZEOnGRID[liThisGrid],
        pDP.data.d2dStdDevMassF_SS_AtSIZEOnGRID[liThisGrid],
        pDP.data.d2dlambdaF_SS_CvSqAtSIZEOnGRID[liThisGrid],
        pDP.data.d2dlambdaF_SS_CvSqPlus1AtSIZEOnGRID[liThisGrid]};
      lsbMatrix
          .append(newline)
          .append(sbaTitles[liThisGrid])
          .append(newline)
          .append(makeSlopesMatrix(ld2dDataTypes,
                                   BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                   ldaEpsilons,
                                   epsilon));
    }// loop through for every grid
    lsbMatrix.append(newline);
    return lsbMatrix;
  }

  /**
   * Returns a matrix for each piGRIDPositions from the data stored in the
   * DataProcessor instance passed to the method. Uses data in the following
   * arrays:
   * <ol>
   * <li>{@link fraclac.analyzer.Data#d2dSIZEsForFsBAtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dCountForFsBAtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dMeanPixFsBAtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dStdDevMassFsBAtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dlambdaFsBCvSqAtSIZEOnGRID}
   * <li>{@link fraclac.analyzer.Data#d2dlambdaFsBCvSqPlus1AtSIZEOnGRID}
   * </ol>
   *
   *
   * @param piGRIDPositions
   * @param pDP DataProcessor holding the data arrays previously filled
   * @param pdEpsilonFactor
   * @param psbaTitles StringBuilder [] with the name of the image and the
   * coordinates of the grid position at [g]
   *
   *
   * @return StringBuilder
   */
  public static StringBuilder part2BDataFileSlopeColumnsFsB(
      int piGRIDPositions,
      DataProcessor pDP,
      double pdEpsilonFactor,
      StringBuilder[] psbaTitles)
  {
    int liGrids = piGRIDPositions;
    StringBuilder lsbFsBData = new StringBuilder(newline
        + "Smoothed (Biggest)" + newline);

    for (int liThisGrid = 0; liThisGrid < liGrids; liThisGrid++) {
      double[] ldaEpsilons = divideArrayBy(
          pDP.data.d2dSIZEsForFsBAtSIZEOnGRID[liThisGrid],
          pdEpsilonFactor);

      double[][] ld2dDataTypes = new double[][]{
        pDP.data.d2dSIZEsForFsBAtSIZEOnGRID[liThisGrid],
        pDP.data.d2dCountForFsBAtSIZEOnGRID[liThisGrid],
        pDP.data.d2dMeanPixFsBAtSIZEOnGRID[liThisGrid],
        pDP.data.d2dStdDevMassFsBAtSIZEOnGRID[liThisGrid],
        pDP.data.d2dlambdaFsBCvSqAtSIZEOnGRID[liThisGrid],
        pDP.data.d2dlambdaFsBCvSqPlus1AtSIZEOnGRID[liThisGrid]};
      // print the grid location and filename and a new line
      lsbFsBData
          .append(newline)
          .append(psbaTitles[liThisGrid])
          .append(newline)
          .append(makeSlopesMatrix(ld2dDataTypes,
                                   BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                   ldaEpsilons,
                                   epsilon));

    }// done each grid

    return lsbFsBData;
  }

  /**
   * Write a matrix offset by newlines before and after, with a line for slopes
   * of the dependent variables vs the independent, followed by the columns of
   * actual data aligned with their slopes. The first column is the independent
   * variable. <h5>Example</h5> <code>
   * double[][] pd2dDependentVariables =
   * {{4, 16, 64, 256}, {5, 25, 125, 625}};<br>
   * String psDependentVariablesTabbedHeadings = "datatype 1" + TAB + "datatype
   * 2";<br>
   * double[] pdaIndependentVariable = {3, 9, 27, 81};<br>
   * String psIndependentVariableHeading = "independents";<br><br>
   * fxn(pd2dDependentVariables, psDependentVariablesTabbedHeadings,
   * pdaIndependentVariable, psIndependentVariableHeading) gives:</code>
   *
   * <pre>
   * ln-ln Slope of Data Type vs independents   1.2619        1.4650
   * independents                               datatype 1    datatype 2
   * 3                                          4             5
   * 9                                          16            25
   * 27                                         64            125
   * 81                                         256           625
   * </pre>
   *
   *
   * @param pd2dDependentVariables double [] with arrays of data types to find
   * the slope against the independent variable array
   * @param psDependentVariablesTabbedHeadings headings for each data type
   * array, with tabs between but no TAB at the start
   * @param pdaIndependentVariable double [] of independent variables for the
   * bottom expression in ln (independent variables) /ln (dependent variables)
   * @param psIndependentVariableHeading string for the allGRID for the
   * independent variable column
   *
   *
   * @return string that is a matrix of slopes and their data beneath; the
   * matrix has a newline before and after
   */
  public static StringBuilder makeSlopesMatrix(
      double[][] pd2dDependentVariables,
      String psDependentVariablesTabbedHeadings,
      double[] pdaIndependentVariable,
      String psIndependentVariableHeading)
  {
    // .........................................................................
    // .......Start with a newline and a title for the first column.............
    // .......indicating that the slopes are against the independent variable..
    // .........................................................................
    StringBuilder lsbMatrix = new StringBuilder(newline);
    lsbMatrix.append("ln-ln Slope of Data Type vs ").append(
        psIndependentVariableHeading);
    // .........................................................................
    // .....Continue making the first row, putting the slope for................
    // .........each data type array in the dependent variables array...........
    // ............against the independent variables array......................
    // .........................................................................
    for (int liArrayNumber = 0;
        liArrayNumber < pd2dDependentVariables.length; liArrayNumber++) {
      Calculator lFS = new Calculator();
      lsbMatrix.append(nextColumn)
          .append(Utils.fnum(lFS.slopeOfPowerRegress(
                      pd2dDependentVariables[liArrayNumber],
                      pdaIndependentVariable)));
    }
    // .........................................................................
    // ......Write a newline then a row with the headings for the independent ..
    // ..................then dependent variables...............................
    // .........................................................................
    lsbMatrix.append(newline).append(psIndependentVariableHeading)
        .append(nextColumn).append(psDependentVariablesTabbedHeadings);
    // .........................................................................
    // ......Write columns under each slope and datatype allGRID...............
    // ..........corresponding to that datatype in the array....................
    // .........................................................................
    for (int row = 0; row < pdaIndependentVariable.length; row++) {

      lsbMatrix.append(newline).append(
          Utils.fnum(pdaIndependentVariable[row]));

      for (int column = 0; column < pd2dDependentVariables.length; column++) {
        lsbMatrix.append(nextColumn).append(
            Utils.fnum(pd2dDependentVariables[column][row]));
      }
    }
    // .......................finish with a blank new line......................
    lsbMatrix.append(newline);
    return lsbMatrix;
  }

  /**
   * Returns a StringBuilder with data for minimum and maximum cover algorithms,
   * ending in a newline. Calls the following sequence, appending the result of
   * each:
   * <ol>
   * <li>{@link #part2DataFileSlopeColumnsFMinCover}
   * <li>{@link #part2DataFileSlopeColumnsFMaxCover}
   * <li>if (pbDoSmoothed) {@link #part2DataFileSlopeColumnsFMinCoverF_SS_}
   * <li>{@link #part2DataFileSlopeColumnsFMaxCoverF_SS_}
   * <li>{@link #part2DataFileSlopeColumnsFMinCoverFsB}
   * <li>{@link #part2DataFileSlopeColumnsFMaxCoverFsB}
   *
   *
   * @param pDP DataProcessor
   * @param pdEpsilonFactor double
   *
   *
   * @return StringBuilder
   */
  public static StringBuilder //
      part2DataFileSlopeColumnsFMinFMaxCoverOptionalFs(DataProcessor pDP,
                                                       double pdEpsilonFactor)
  {
    StringBuilder lsBuilder = new StringBuilder("");

    lsBuilder.append(part2DataFileSlopeColumnsFMinCover(pDP,
                                                        pdEpsilonFactor))
        .append(newline)
        .append(part2DataFileSlopeColumnsFMaxCover(pDP,
                                                   pdEpsilonFactor))
        .append(newline);

    if (pDP.scan.vars.bDoSmoothed) {
      lsBuilder
          .append(part2DataFileSlopeColumnsFMinCoverF_SS_(pDP,
                                                          pdEpsilonFactor))
          .append(newline)
          .append(part2DataFileSlopeColumnsFMaxCoverF_SS_(pDP,
                                                          pdEpsilonFactor))
          .append(newline)
          .append(part2DataFileSlopeColumnsFMinCoverFsB(pDP,
                                                        pdEpsilonFactor))
          .append(newline)
          .append(part2DataFileSlopeColumnsFMaxCoverFsB(pDP,
                                                        pdEpsilonFactor))
          .append(newline);
    }
    return lsBuilder;
  }

  /**
   * Returns a StringBuilder as a {@link #makeSlopesMatrix matrix of data}.
   * <p>
   * The headings passed in to the matrix are
   * {@link #BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1} and the data arrays are:
   * <ul>
   * <li> {@link Data#daCountsForFMinCover}
   * <li> {@link Data#daMeanPixPerSIZEFMinCover}
   * <li> {@link Data#daStdDevForPixPerSIZEFMinCover}
   * <li> {@link Data#dalambdaFMinCvSqPixPerSIZE}
   * <li>{@link ArrayMethods#plus1 } ( {@link Data#dalambdaFMinCvSqPixPerSIZE
   * }
   * )
   * </ul>
   *
   *
   * @param pDP DataProcessor holding filled arrays including
   * {@link Data#daSIZEsForFMin}
   * @param pdEpsilonFactor double that sampling SIZE is divided by
   *
   *
   * @return StringBuilder
   */
  public static StringBuilder part2DataFileSlopeColumnsFMinCover(
      DataProcessor pDP,
      double pdEpsilonFactor)
  {
    StringBuilder lsbData = new StringBuilder(newline
        + "Minimum Cover from all Grids" + newline);

    double[] ldaEpsilons = divideArrayBy(pDP.data.daSIZEsForFMin,
        pdEpsilonFactor);

    double[][] ld2dDataTypes = new double[][]{pDP.data.daSIZEsForFMin,
      pDP.data.daCountsForFMinCover,
      pDP.data.daMeanPixPerSIZEFMinCover,
      pDP.data.daStdDevForPixPerSIZEFMinCover,
      pDP.data.dalambdaFMinCvSqPixPerSIZE,
      pDP.data.daCVSqPlus1FMin};

    lsbData.append(makeSlopesMatrix(ld2dDataTypes,
                                    BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                    ldaEpsilons,
                                    epsilon));
    return lsbData;
  }

  /**
   *
   * @param pDP
   * @param pdEpsilonFactor
   * @return
   */
  public static StringBuilder part2DataFileSlopeColumnsFMaxCover(
      DataProcessor pDP,
      double pdEpsilonFactor)
  {
    StringBuilder lsbData = new StringBuilder(
        "Maximum Cover from all Grids" + newline);

    double[] ldaEpsilons = divideArrayBy(
        pDP.data.daSIZEsForFMax,
        pdEpsilonFactor);

    double[][] ld2dDataTypes = new double[][]{pDP.data.daSIZEsForFMax,
      pDP.data.daCountsForFMaxCover,
      pDP.data.daMeanPixPerSIZEForFMaxCover,
      pDP.data.daStdDevForPixPerSIZEForFMaxCover,
      pDP.data.dalambdaFMaxCvSqPixPerSIZE, pDP.data.daCVSqPlus1FMax};

    lsbData.append(makeSlopesMatrix(ld2dDataTypes,
                                    BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                    ldaEpsilons,
                                    epsilon));
    return lsbData;
  }

  /**
   *
   * @param pDP
   * @param pdEpsilonFactor
   * @return
   */
  public static StringBuilder part2DataFileSlopeColumnsFMaxCoverF_SS_(
      DataProcessor pDP,
      double pdEpsilonFactor)
  {
    StringBuilder lsbData = new StringBuilder(
        "Maximum Cover (Smoothed-smallest) from all Grids" + newline);

    double[] ldaEpsilons = divideArrayBy(pDP.data.cFMaxCover.daF_SS_SIZEs,
                                         pdEpsilonFactor);

    double[][] ld2dDataTypes = new double[][]{
      pDP.data.cFMaxCover.daF_SS_SIZEs,
      pDP.data.cFMaxCover.daF_SS_Count,
      pDP.data.cFMaxCover.daF_SS_Mean,
      pDP.data.cFMaxCover.daF_SS_StdDev,
      pDP.data.cFMaxCover.daF_SS_CvSq,
      pDP.data.cFMaxCover.daF_SS_CvSqPlus1};

    lsbData.append(makeSlopesMatrix(ld2dDataTypes,
                                    BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                    ldaEpsilons,
                                    epsilon));
    return lsbData;
  }

  /**
   *
   * @param pDP
   * @param pdEpsilonFactor
   * @return
   */
  public static StringBuilder part2DataFileSlopeColumnsFMaxCoverFsB(
      DataProcessor pDP,
      double pdEpsilonFactor)
  {
    StringBuilder lsbData = new StringBuilder(
        "Maximum Cover (Smoothed-biggest) from all Grids" + newline);

    double[] ldaEpsilons = divideArrayBy(
        pDP.data.cFMaxCover.daF_SB_SIZEs,
        pdEpsilonFactor);

    double[][] ld2dDataTypes = new double[][]{
      pDP.data.cFMaxCover.daF_SB_SIZEs,
      pDP.data.cFMaxCover.daF_SB_Count,
      pDP.data.cFMaxCover.daF_SB_MeanPix,
      pDP.data.cFMaxCover.daF_SB_StdDev,
      pDP.data.cFMaxCover.daF_SB_CvSq,
      pDP.data.cFMaxCover.daF_SB_CvSqPlus1};

    lsbData.append(makeSlopesMatrix(ld2dDataTypes,
                                    BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                    ldaEpsilons,
                                    epsilon));
    return lsbData;
  }

  /**
   * Returns a StringBuilder with data from the minimum cover smoothed smallest
   * SIZE data arrays.
   *
   *
   * @param pDP FLDataProcessorpdEpsilonFactor@param pdEpsilonFactor double
   *
   *
   * @return StringBuilder
   */
  public static StringBuilder part2DataFileSlopeColumnsFMinCoverF_SS_(
      DataProcessor pDP,
      double pdEpsilonFactor)
  {
    StringBuilder lsb = new StringBuilder(
        "Minimum Cover (Smoothed-smallest) from all Grids" + newline);

    double[] ldaEpsilons = divideArrayBy(pDP.data.cFMinCover.daF_SS_SIZEs,
                                         pdEpsilonFactor);

    double[][] ld2dDataTypes = new double[][]{
      pDP.data.cFMinCover.daF_SS_SIZEs,
      pDP.data.cFMinCover.daF_SS_Count,
      pDP.data.cFMinCover.daF_SS_Mean,
      pDP.data.cFMinCover.daF_SS_StdDev,
      pDP.data.cFMinCover.daF_SS_CvSq,
      pDP.data.cFMinCover.daF_SS_CvSqPlus1};

    lsb.append(makeSlopesMatrix(ld2dDataTypes,
                                BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                ldaEpsilons,
                                epsilon));

    return lsb;
  }

  /**
   *
   * @param pDP
   * @param pdEpsilonFactor
   * @return
   */
  public static StringBuilder part2DataFileSlopeColumnsFMinCoverFsB(
      DataProcessor pDP,
      double pdEpsilonFactor)
  {
    StringBuilder lsbData = new StringBuilder(
        "Minimum Cover (Smoothed-biggest) from all Grids" + newline);

    double[] ldaEpsilons = divideArrayBy(
        pDP.data.cFMinCover.daF_SB_SIZEs,
        pdEpsilonFactor);

    double[][] ld2dDataTypes = new double[][]{
      pDP.data.cFMinCover.daF_SB_SIZEs,
      pDP.data.cFMinCover.daF_SB_Count,
      pDP.data.cFMinCover.daF_SB_MeanPix,
      pDP.data.cFMinCover.daF_SB_StdDev,
      pDP.data.cFMinCover.daF_SB_CvSq,
      pDP.data.cFMinCover.daF_SB_CvSqPlus1};

    lsbData.append(makeSlopesMatrix(ld2dDataTypes,
                                    BS_COUNT_MEANMASS_STDDEV_CVSQ_PLUS1,
                                    ldaEpsilons,
                                    epsilon));

    return lsbData;
  }

}
