package fraclac.writers;

import fraclac.analyzer.CircStats;
import fraclac.analyzer.Data;
import fraclac.analyzer.DataProcessor;
import fraclac.analyzer.Vars;
import fraclac.gui.VarsInfo;
import fraclac.utilities.ArrayMethods;
import static fraclac.utilities.Symbols.*;
import static fraclac.utilities.Utils.fnum;
import fraclac.writers.Headings.EnumDataFile;
import fraclac.writers.Headings.EnumInfoData;
import fraclac.writers.Headings.EnumLacData;
import ij.gui.Roi;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * A data structure object for creating results files from fractal analysis done
 * with the FracLac plugin, that makes Strings for the files using
 * {@link Map maps} it makes from previously calculated data and results and
 * pre-set headings. When instantiated, it performs a task having 2 basic parts:
 * <ol><li>It puts values into maps by calling
 * {@link #makeMasterMap makeMasterMap}</li>
 * <li>It writes the maps into strings by creating a
 * {@link SummaryStringFormatter#SummaryStringFormatter(boolean, fraclac.writers.SummaryStringDataMapper, boolean, boolean, boolean, boolean) SummaryStringFormatter},
 * a class level object named {@link #stringFormatter stringFormatter}, passing
 * in itself and variables from the passed parameters.</li>
 * </ol>
 *
 * The values stored in the maps correspond to keys that contain headings (i.e.,
 * each map has a bunch of paired components <i>Heading</i>:<i>Value</i>). Once
 * it has been instantiated, the strings are accessed from the class's
 * {@link #stringFormatter stringFormatter}.
 *
 * @author Audrey Karperien
 * @version FracLac 2014Jan $Rev: 244 $
 * @version Revision $Id: SummaryStringDataMapper.java 54 2013-02-03 08:45:01Z
 * audrey $
 *
 */
public class SummaryStringDataMapper extends AllGsHeadings
{

  /**
   * Constructor calls a function to generate maps of data for printing results
   * to Strings then creates a new stringFormatter instance which can be
   * accessed to retrieve enumStrings ready for printing.
   *
   * @param pDp DataProcessor holding data arrays ready for printing to
   * enumStrings for results files.
   * @param pbRotateToIndividualFile
   * @param piNumSlices
   * @param psRotationSummaryType
   * @param pRoiFromMotherImage
   *
   * @see #makeMasterMap function to make maps
   */
  public SummaryStringDataMapper(DataProcessor pDp,
                                 boolean pbRotateToIndividualFile,
                                 int piNumSlices,
                                 String psRotationSummaryType,
                                 Roi pRoiFromMotherImage)
  {
    roiMotherRoi = pRoiFromMotherImage;
    boolean lbRotationSummary
        = ((!pbRotateToIndividualFile) && pDp.scan.vars.bRotate);
    makeMasterMap(pDp,
                  lbRotationSummary,
                  psRotationSummaryType,
                  piNumSlices);
    stringFormatter
        = new SummaryStringFormatter(pbRotateToIndividualFile,
                                     this,
                                     pDp.scan.vars.bDoFilterMinCover,
                                     pDp.scan.vars.bDoSmoothed,
                                     pDp.scan.vars.isMvsD(),
                                     pDp.scan.vars.isSLAC());
  }

  // ***********************************************************************
  //                              FIELDS
  // ***********************************************************************
  /**
   *
   */
  public SummaryStringFormatter stringFormatter;
  private final Roi roiMotherRoi;
  /**
   * A set of enum values used for mapping data from filtered scans, such as
   * minimum and maximum cover and smoothed filters. The values in the set are
   * <ul>
   * <li>{@link EnumDataFile#OPTIMAL_FD}</li>
   * <li>{@link EnumDataFile#R_SQ_OPTIMAL}</li>
   * <li>{@link EnumDataFile#SE_OPTIMAL}</li>
   * <li>{@link EnumDataFile#YINT_OPTIMAL}</li>
   * <li>{@link EnumDataFile#NAME}</li>
   * <li>{@link EnumDataFile#SCAN_TYPE}</li>
   * </ul>
   *
   */
  public static EnumSet<? extends DataTypesInterface> eSetFiltered = EnumSet
      .of(EnumDataFile.OPTIMAL_FD,
          EnumDataFile.R_SQ_OPTIMAL,
          EnumDataFile.SE_OPTIMAL,
          EnumDataFile.YINT_OPTIMAL,
          EnumDataFile.NAME,
          EnumDataFile.SCAN_TYPE);

  /**
   * A set of enum values for headings and results from smooth filtered fractal
   * analysis data.
   * <ul>
   *
   * <li>{@link EnumDataFile#MEAN_FRACTAL_DIMENSION }</li>
   * <li>{@link EnumDataFile#STD_DEVIATION_FOR_FRACTAL_DIMENSIONS }</li>
   * <li>{@link EnumDataFile#COEFFICIENT_OF_VARIATION }</li>
   * <li>{@link EnumDataFile#SIZES }</li>
   * <li>{@link EnumDataFile#OPTIMAL_FD }</li>
   * <li>{@link EnumDataFile#R_SQ_OPTIMAL}</li>
   * <li>{@link EnumDataFile#SE_OPTIMAL }</li>
   * <li>{@link EnumDataFile#YINT_OPTIMAL }</li>
   * <li>{@link EnumDataFile#NAME }</li>
   * <li>{@link EnumDataFile#SCAN_TYPE}</li>
   *
   * </ul>
   */
  public static EnumSet<? extends DataTypesInterface> eSetSmoothed = EnumSet
      .of(EnumDataFile.MEAN_FRACTAL_DIMENSION,
          EnumDataFile.STD_DEVIATION_FOR_FRACTAL_DIMENSIONS,
          EnumDataFile.COEFFICIENT_OF_VARIATION,
          EnumDataFile.SIZES,
          EnumDataFile.OPTIMAL_FD,
          EnumDataFile.R_SQ_OPTIMAL,
          EnumDataFile.SE_OPTIMAL,
          EnumDataFile.YINT_OPTIMAL,
          EnumDataFile.NAME,
          EnumDataFile.SCAN_TYPE);

  /**
   * EnumSet specifying the headings to use for printing the mass and count data
   * maps.
   *
   * <ul>
   * <li>{@link EnumDataFile#SCAN_TYPE}</li>
   * <li>{@link EnumDataFile#DEFINITION_OF_Y}</li>
   * <li>{@link EnumDataFile#FORMULA_FOR_FRACTAL_DIMENSION}</li>
   * <li>{@link EnumDataFile#MEAN_FRACTAL_DIMENSION}</li>
   * <li>{@link EnumDataFile#STD_DEVIATION_FOR_FRACTAL_DIMENSIONS}</li>
   * <li>{@link EnumDataFile#OPTIMAL_FD}</li>
   * <li>{@link EnumDataFile#R_SQ_OPTIMAL}</li>
   * <li>{@link EnumDataFile#SE_OPTIMAL}</li>
   * <li>{@link EnumDataFile#YINT_OPTIMAL}</li>
   * </ul>
   */
  public static EnumSet<? extends DataTypesInterface> eSetMassAndCount
      = EnumSet.of(EnumDataFile.SCAN_TYPE,
                   EnumDataFile.DEFINITION_OF_Y,
                   EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
                   EnumDataFile.MEAN_FRACTAL_DIMENSION,
                   EnumDataFile.STD_DEVIATION_FOR_FRACTAL_DIMENSIONS,
                   EnumDataFile.MIN_FOR_FRACTAL_DIMENSIONS,
                   EnumDataFile.MAX_FOR_FRACTAL_DIMENSIONS,
                   EnumDataFile.OPTIMAL_FD,
                   EnumDataFile.R_SQ_OPTIMAL,
                   EnumDataFile.SE_OPTIMAL,
                   EnumDataFile.YINT_OPTIMAL);

  /**
   * An enum set for binned probability lacunarity.
   * <ul>
   * <li>{@link EnumLacData#MEAN_LAC}</li>
   * <li>{@link EnumLacData#CV_FOR_MEAN_LAC}</li>
   * <li>{@link EnumLacData#MEAN_LAC_FOR_OMEGA}</li>
   * <li>{@link EnumLacData#CV_FOR_LAC_FOR_OMEGA}</li>
   * </ul>
   */
  public static EnumSet<? extends DataTypesInterface> eSetProbLac = EnumSet
      .of(EnumLacData.MEAN_LAC,
          EnumLacData.CV_FOR_MEAN_LAC,
          EnumLacData.MEAN_LAC_FOR_OMEGA,
          EnumLacData.CV_FOR_LAC_FOR_OMEGA);

  /**
   * Map holding Objects associated with keys, both from {@link EnumDataFile}.
   * There are several maps, one for each type of scan (e.g., binary or gray
   * scale) and one for the filtered scan results as well. There are also data
   * maps for {@link #mapLac lacunarity} scans.
   *
   * Before accessing it to read or write to it, the map has to be
   * {@link #nameMaps() named} and filled (i.e., it is empty until it is loaded
   * by a call to its loading method). It also must be added to the
   * {@link #addMapsToFractalDimensionMapLists(boolean) MapList}. The loading
   * methods include:
   * <ul><li>{@link #makeDmMap}</li>
   * <li>{@link #makeFAvgCoverMap}</li>
   * <li>{@link #makeFMaxCoverMap}</li>
   * <li>{@link #makeFMinCoverMap}</li>
   * <li>{@link #makeFSMap}</li>
   * <li>{@link #makeFSSMap}</li>
   * <li>{@link #makeFSSMaxCoverMap}</li>
   * <li>{@link #makeFSSMinCoverMap}</li>
   * <li>{@link #makeFSBMap}</li>
   * <li>{@link #makeFSBMaxCoverMap}</li>
   * <li>{@link #makeFSBMinCoverMap}</li>
   * <li>{@link #makeFractalDimensionMaps}</li></ul>
   */
  public Map<EnumDataFile, Object>//
      mapFAvgCover = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapDB = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapDm = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFMinCover = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFMaxCover = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFSS = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFS = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFSB = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFSSFMinCover = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFSBFMinCover = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFSSFMaxCover = new EnumMap<EnumDataFile, Object>(EnumDataFile.class),
      mapFSBFMaxCover = new EnumMap<EnumDataFile, Object>(EnumDataFile.class);

  /**
   * Map object that holds key-value pairs of headings and data to use for
   * creating results files from box counting lacunarity scans.
   *
   * The maps hold their data as Objects associated with keys from
   * {@link EnumLacData}. There are several maps, one for each type of scan
   * (e.g., binary or gray scale) and for filtered scan results as well. There
   * are also maps for fractal dimension data (e.g., see {@link #mapDB}).
   *
   * Before accessing it, the map has to be {@link #nameMaps() named} and filled
   * (i.e., it is empty until it is loaded by a call to its loading method
   * (e.g., {@link #makeLacMaps}. It also must be added to the
   * {@link #addMapsToLacList()}.
   */
  public Map<EnumLacData, Object> //
      mapProb = new EnumMap<EnumLacData, Object>(EnumLacData.class),
      mapPD = new EnumMap<EnumLacData, Object>(EnumLacData.class),
      mapProbOverBins = new EnumMap<EnumLacData, Object>(EnumLacData.class),
      mapPDOverBins = new EnumMap<EnumLacData, Object>(EnumLacData.class),
      mapLac = new EnumMap<EnumLacData, Object>(EnumLacData.class);

  /**
   *
   */
  public Map<EnumInfoData, Object> //
      mapGeneralInfo = new EnumMap<EnumInfoData, Object>(EnumInfoData.class);

  /**
   * ArrayList that accepts maps for fractal dimension data from the
   * DataProcessor passed in in the constructor.
   */
  public ArrayList<Map<? extends DataTypesInterface, Object>> //
      listFractalDimensionMaster
      = new ArrayList<Map<? extends DataTypesInterface, Object>>(),
      listMassAndCount
      = new ArrayList<Map<? extends DataTypesInterface, Object>>(),
      listFS
      = new ArrayList<Map<? extends DataTypesInterface, Object>>(),
      listFAverageCover
      = new ArrayList<Map<? extends DataTypesInterface, Object>>(),
      listFSFMinFMaxCover
      = new ArrayList<Map<? extends DataTypesInterface, Object>>(),
      listFMinFMaxCover
      = new ArrayList<Map<? extends DataTypesInterface, Object>>();

  /**
   * ArrayList that accepts maps for lacunarity data from the DataProcessor
   * passed in in the constructor.
   */
  public ArrayList<Map<? extends DataTypesInterface, Object>> // listLac =
      listProbLac = new ArrayList<Map<? extends DataTypesInterface, Object>>(),
      listRegLac = new ArrayList<Map<? extends DataTypesInterface, Object>>(),
      listLac = new ArrayList<Map<? extends DataTypesInterface, Object>>();

  /**
   * ArrayList that accepts maps for general information about a fractal
   * analysis scan from the DataProcessor passed in in the constructor.
   */
  public ArrayList<Map<? extends DataTypesInterface, Object>> //
      listGeneralInfo
      = new ArrayList<Map<? extends DataTypesInterface, Object>>();

  // ..........................................................................
  //                          Methods 
  // ..........................................................................
  /**
   * This is the key method called in the
   * {@link #SummaryStringDataMapper(fraclac.analyzer.DataProcessor, boolean, int, java.lang.String, ij.gui.Roi) constructor}
   * that fills the individual class data maps and adds them to the master lists
   * from which they can be read.
   * <ol>
   * <li>Adds maps to Master List:</li>
   * <ul>
   * <li>adds D<sub>F</sub> info to
   * {@link #addMapsToFractalDimensionMapLists fractal dimension list}</li>
   * <li>adds lacunarity info to its {@link #addMapsToLacList list}</li>
   * <li>adds {@link #mapGeneralInfo general info} to its
   * {@link #listGeneralInfo list}</li>
   * </ul>
   * <li>Fills maps
   * <ol>
   * <li>{@link #makeFractalDimensionMaps fractal dimension maps}</li>
   * <li>{@link #makeLacMaps lacunarity maps}</li>
   * <li>{@link #makeGeneralInfoMaps general info map}</li>
   * </ol>
   * </li>
   * </ol>
   *
   * @param pDp DataProcessor passed to functions to fill data maps; the
   * DataProcessor should be primed with processed
   * {@link fraclac.analyzer.Data data} from a fractal analysis
   * {@link fraclac.analyzer.Scan scan}
   * @param pbRotationSummary
   * @param psRotationSummaryType
   * @param piNumSlices
   */
  public final void makeMasterMap(DataProcessor pDp,
                                  boolean pbRotationSummary,
                                  String psRotationSummaryType,
                                  int piNumSlices)
  {
    // =======================================================================
    addMapsToFractalDimensionMapLists(pDp.scan.vars.isMvsD());
    addMapsToLacList();
    listGeneralInfo.add(mapGeneralInfo);
    // =======================================================================
    makeFractalDimensionMaps(pbRotationSummary,
                             pDp);
    makeLacMaps(pbRotationSummary,
                pDp);
    makeGeneralInfoMaps(psRotationSummaryType,
                        pDp,
                        piNumSlices);
    loadHullAndCircleMap(pDp.scan.circStatsForCircAndHull,
                         pDp.scan.vars);
    // =======================================================================
  }

  /**
   * Puts data into the {@link #mapGeneralInfo map} for general information. The
   * headings keys are of type {@link fraclac.writers.Headings.EnumInfoData} and
   * the values are taken chiefly from the passed in
   * {@link fraclac.analyzer.DataProcessor}, using its
   *
   * {@link fraclac.analyzer.Scan#vars},
   * {@link fraclac.analyzer.Data} and other values.
   */
  void makeGeneralInfoMaps(String pRotationSummaryType,
                           DataProcessor pDp,
                           int piNumSlices)
  {
    Map<EnumInfoData, Object> lMap = mapGeneralInfo;
    Roi lRoi = //pDp.scan.        roiFromMotherImage;
        roiMotherRoi;
    String lsCoordsOfRoi = "";
    if ((lRoi != null)) {
      lsCoordsOfRoi = String.format("(%s,%s)[%sx%s]",
                                    lRoi.getBounds().x,
                                    lRoi.getBounds().y,
                                    lRoi.getBounds().width,
                                    lRoi.getBounds().height);
    }
    // -------------------------------------------------------------------
    String lsTitle = (pDp.scan.sbaTitles[0].toString());

    if ((pRotationSummaryType == s_NetRotations
        || pRotationSummaryType == s_AveragedByRotations)) {
      String lsSlices = ((pRotationSummaryType == s_NetRotations)
          ? ("" + pDp.scan.vars.iNumGrids)
          : (piNumSlices + (piNumSlices > 1 ? " slices" : "slice")));

      lsTitle = String.format("%s(%s)%s%s",
                              pRotationSummaryType,
                              lsSlices,
                              pDp.scan.vars.sOriginalImageTitle,
                              lsCoordsOfRoi);
    }
    lMap.put(EnumInfoData.INFO,
             lsTitle);
    // --------------------------------------------------------------------
    /**
     * fixme22 part2 Commented code below was removed; see
     * {@link Scan#scanAllGRIDsThisSlice}
     */
    // + (pDp.fl.vars.originalRoi == null ? ""
    // : (" Original ROI: "
    // + pDp.fl.vars.originalRoi.getBounds().x + ", "
    // + pDp.fl.vars.originalRoi.getBounds().y + ": "
    // + pDp.fl.vars.originalRoi.getBounds().width
    // + " x "
    // + pDp.fl.vars.originalRoi.getBounds().height))
    // end fixme22 part 2

    lMap.put(EnumInfoData.SCAN_POSITIONS,
             (pDp.scan.vars.isDlc() ? pDp.scan.vars.getiDlcNumPixChecked()
                 : pDp.scan.vars.iNumGrids));

    lMap.put(EnumInfoData.SIZES,
             (pDp.data.dMeanSIZEs));

    lMap.put(EnumInfoData.MIN_SIZE,
             (pDp.data.iMinSIZE));

    lMap.put(EnumInfoData.MAX_SIZE,
             (pDp.data.iMaxSIZE));

    lMap.put(EnumInfoData.SIGMA_FOR_SIZES,
             (pDp.data.dStdDevSIZE));

    if (pDp.scan.vars.bCheckPixRatio) {

      lMap.put(EnumInfoData.MIN_PIX_DENSITY,
               (pDp.scan.vars.dUserMinDensity));

      lMap.put(EnumInfoData.MAX_PIX_DENSITY,
               (pDp.scan.vars.dUserMaxDensity));

    }
    // add element
    lMap.put(EnumInfoData.SCAN_ELEMENT,
             pDp.scan.vars.bUseOvalForInnerSampleNotOuterSubscan ? "Oval"
                 : "Rectangle");

    // add pixel information
    lMap.put(EnumInfoData.TOTAL_PIX,
             (pDp.scan.vars.getdTotalPixelsInImageArea()));

    lMap.put(EnumInfoData.FOREGROUND_PIX,
             (pDp.scan.vars.getdTotalForegroundPixels()));

    lMap.put(EnumInfoData.STD_DEV_FG_PIX,
             (pDp.data.statsForegroundPixels.dStdDev));

    if (!pDp.scan.vars.isGray() && !pDp.scan.vars.isMvsD()
        && !pDp.scan.vars.isDlc()) {

      lMap.put(EnumInfoData.MEAN_CV_FOR_COUNT_FROM_ALL_GRID_SCANS,
               (pDp.data.statsCVForCountsOrSumsdeltaIAllGRIDs.dMean));

      lMap.put(EnumInfoData.MEAN_CV_COUNT_VS_MEAN_CV_OMEGA_ALL_GRIDS,
               (pDp.data.statsCVForCountsOrSumsdeltaIAllGRIDs.dMean
               / pDp.data.statsCVForOMEGACountAllGRIDs.dMean));
    }

    if (pDp.scan.vars.isSLAC() || pDp.scan.vars.isDlc()) {
      lMap.put(EnumInfoData.SLIDE_X,
               (pDp.scan.vars.iPixelsToSlideHorizontally));
      lMap.put(EnumInfoData.SLIDE_Y,
               (pDp.scan.vars.iPixelsToSlideVertically));
    }

    lMap.put(EnumInfoData.FOREGROUND_COLOUR,
             pDp.scan.vars
             .getUserForeground() == WHITE_255 ? "white 255"
                 : "black 0");

  }

  public void loadHullAndCircleMap(CircStats pCircStats,
                                   Vars pVars)
  {
    Map<Headings.EnumInfoData, Object> lMap = mapGeneralInfo;

    lMap.put(EnumInfoData.DENSITY,
             fnum(pVars.getdTotalForegroundPixels()
                 / pVars.getdAreaOfHull()));

    lMap.put(EnumInfoData.SPAN_RATIO,
             pCircStats.sSpanRatio);
    lMap.put(EnumInfoData.HULL_CENTRE_OF_MASS,
             pCircStats.sMassCentre);
    lMap.put(EnumInfoData.HULL_MAX_SPAN,
             pCircStats.sMaxSpanOfPixels);
    lMap.put(EnumInfoData.AREA,
             pCircStats.sArea);
    lMap.put(EnumInfoData.PERIMETER,
             pCircStats.sPerimeter);
    lMap.put(EnumInfoData.CIRCULARITY,
             pCircStats.sCircularity);
    lMap.put(EnumInfoData.BOUNDING_RECT_WIDTH,
             pCircStats.sMargWidth);
    lMap.put(EnumInfoData.BOUNDING_RECT_HEIGHT,
             pCircStats.sMargHeight);
    lMap.put(EnumInfoData.HULL_MAX_RADIUS,
             pCircStats.sMaxRadius);
    lMap.put(EnumInfoData.HULL_MAX_OVER_MIN_RADII,
             pCircStats.sMaxOverMinRadii);
    lMap.put(EnumInfoData.HULL_CV_FOR_RADII,
             pCircStats.sCVRadii);
    lMap.put(EnumInfoData.HULL_MEAN_RADIUS,
             pCircStats.sMeanOfHullRadii);
    lMap.put(EnumInfoData.CIRCLE_CENTRE,
             pCircStats.sCircleCentre);
    lMap.put(EnumInfoData.CIRCLE_DIAMETER,
             pCircStats.sCircleDiameter);
    lMap.put(EnumInfoData.CIRCLE_MAX_RADIUS,
             pCircStats.sMaxRadiusCircle);
    lMap.put(EnumInfoData.CIRCLE_MAX_OVER_MIN,
             pCircStats.sMaxOverMinRadiiCircle);
    lMap.put(EnumInfoData.CIRCLE_CV_RADII,
             pCircStats.sCVRadiiCircle);
    lMap.put(EnumInfoData.CIRCLE_MEAN_RADIUS,
             pCircStats.sMeanOfHullCircleRadii);
    lMap.put(EnumInfoData.CIRCLE_METHOD,
             pVars.getsMethodUsedForBoundingCircle());
  }

  /**
   * Adds maps to lists. Adds lists to sublists as below, then adds sublists to
   * master list.
   *
   * <ol>
   * <li>Add to {@link #listMassAndCount Mass and Count List}:
   * <ul>
   * <li>box counting dimension {@link #mapDB map}
   * <li>mass dimension {@link #mapDm map}
   * </ul>
   * </li>
   * <li>Add to filtered {@link #listFS Smoothed List}
   * <ul>
   * <li>{@link #mapFSS smoothed (smallest) map}
   * <li> {@link #mapFSB smoothed (biggest) map}
   * <li> {@link #mapFS smoothed map}
   * </ul>
   * </li>
   * <li>Add to {@link #listFAverageCover Average Cover List}
   * <ul>
   * <li>average cover fractal dimension {@link #mapFAvgCover map}
   * </ul>
   * </li>
   * <li>Add to {@link #listFMinFMaxCover Min/Max Cover List}
   * <ul>
   * <li> {@link #mapFMinCover min cover fractal dimension map}
   * <li>{@link #mapFMaxCover max cover fractal dimension}
   * </ul>
   * </li>
   * <li>Add to {@link #listFSFMinFMaxCover filtered Smooth Min/Max Cover List}
   * <ul>
   * <li>{@link #mapFSSFMinCover smoothed (smallest) min cover}
   * <li>{@link #mapFSBFMinCover smoothed (biggest) min cover}
   * <li>{@link #mapFSSFMaxCover smoothed (smallest) max cover}
   * <li>{@link #mapFSBFMaxCover smoothed (biggest) max cover}
   * </ul>
   * </li>
   * <li>Add them all to the
   * {@link #listFractalDimensionMaster Fractal Dimension Master List}:
   * <ul>
   * <li>{@link #listMassAndCount Mass and Count List}
   * <li>{@link #listFAverageCover Average Cover List}
   * <li>{@link #listFSFMinFMaxCover Smooth Min/Max List}
   * <li>{@link #listFS Smoothed List}
   * </ul></li>
   * </ol>
   *
   */
  void addMapsToFractalDimensionMapLists(boolean pbIsMvd)
  {

    listMassAndCount.add(mapDB);
    listMassAndCount.add(mapDm);

    listFS.add(mapFSS);
    listFS.add(mapFSB);
    listFS.add(mapFS);

    listFAverageCover.add(mapFAvgCover);

    listFMinFMaxCover.add(mapFMinCover);
    listFMinFMaxCover.add(mapFMaxCover);

    listFSFMinFMaxCover.add(mapFSSFMinCover);
    listFSFMinFMaxCover.add(mapFSBFMinCover);
    listFSFMinFMaxCover.add(mapFSSFMaxCover);
    listFSFMinFMaxCover.add(mapFSBFMaxCover);

    listFractalDimensionMaster.addAll(listMassAndCount);
    listFractalDimensionMaster.addAll(listFAverageCover);
    listFractalDimensionMaster.addAll(listFSFMinFMaxCover);
    listFractalDimensionMaster.addAll(listFS);
    listFractalDimensionMaster.addAll(listFMinFMaxCover);

  }

  /**
   * Calls functions to load the fractal dimension maps with data.
   * <ul>
   * <li>{@link #nameMaps}
   * <li>{@link #makeFAvgCoverMap}
   * <li>{@link #makeDbMap}
   * <li>{@link #makeDmMap}
   * <li>{@link #makeFSMap}
   * <li>{@link #makeFSBMap}
   * <li>{@link #makeFSSMap}
   * <li>{@link #makeFMinCoverMap}
   * <li>{@link #makeFMaxCoverMap}
   * <li>{@link #makeFSSMinCoverMap}
   * <li>{@link #makeFSBMinCoverMap}
   * <li>{@link #makeFSSMaxCoverMap}
   * <li>{@link #makeFSBMaxCoverMap}
   * </ul>
   *
   *
   * @see #addMapsToFractalDimensionMapLists list of fractal dimension maps
   */
  void makeFractalDimensionMaps(boolean pbRotationSummary,
                                DataProcessor pDp)
  {
    nameMaps();

    if (!pDp.scan.vars.isSLAC() && !pDp.scan.vars.isMvsD()) {

      makeFAvgCoverMap(pbRotationSummary,
                       pDp);
    }

    if (!(pDp.scan.vars.isMvsD() && !pDp.scan.vars.isGray())) {
      makeDbMap(pbRotationSummary,
                pDp);
    }

    makeDmMap(pbRotationSummary,
              pDp);

    if (pDp.scan.vars.bDoSmoothed) {
      makeFSMap(pbRotationSummary,
                pDp);
      makeFSBMap(pbRotationSummary,
                 pDp);
      makeFSSMap(pbRotationSummary,
                 pDp);
    }

    if (pDp.scan.vars.bDoFilterMinCover) {

      makeFMinCoverMap(pbRotationSummary,
                       pDp);
      makeFMaxCoverMap(pbRotationSummary,
                       pDp);

      if (pDp.scan.vars.bDoSmoothed) {

        makeFSSMinCoverMap(pbRotationSummary,
                           pDp);
        makeFSBMinCoverMap(pbRotationSummary,
                           pDp);
        makeFSSMaxCoverMap(pbRotationSummary,
                           pDp);
        makeFSBMaxCoverMap(pbRotationSummary,
                           pDp);
      }
    }
  }

  /**
   * Puts data into the {@link #mapFMaxCover filtered max cover map} using data
   * stored mainly in {@link Data#cFMaxCover} in the passed
   * {@link DataProcessor pDp}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile} and the values are taken
   * chiefly from the passed in {@link fraclac.analyzer.DataProcessor}, using
   * its {@link fraclac.analyzer.Data#cFMaxCover},
   * {@link fraclac.analyzer.Data#dalambdaFMaxCvSqPixPerSIZE},
   * {@link fraclac.analyzer.Data#daSIZEsForFMax} and other values.
   *
   * * <p>
   * This method assumes that the data have already been processed using a
   * {@link fraclac.utilities.DataFilter#filterFMinOrFMaxCover maximum cover}.
   *
   */
  void makeFMaxCoverMap(boolean pbRotationSummary,
                        DataProcessor pDp)
  {
    Map<EnumDataFile, Object> lMap = mapFMaxCover;
    // =========================================================================
    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.cFMaxCover.dFractalDimension));

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.cFMaxCover.dFractalDimension));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.cFMaxCover.dRSq));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.cFMaxCover.dStdErr));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.cFMaxCover.dYIntercept));
    // =========================================================================
    lMap.put(EnumDataFile.PREFACTOR,
             (pDp.data.cFMaxCover.dPrefactor));
    // =========================================================================
    lMap.put(EnumDataFile.SIZES,
             pbRotationSummary
                 ? INF
                 : (pDp.data.daSIZEsForFMax.length));

    lMap.put(EnumDataFile.MIN_SIZE,
             pbRotationSummary
                 ? INF
                 : (pDp.data.daSIZEsForFMax[0]));

    lMap.put(
        EnumDataFile.MAX_SIZE,
        pbRotationSummary
            ? INF
            : (pDp.data.daSIZEsForFMax[pDp.data.daSIZEsForFMax.length - 1]));
    // =========================================================================
    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (ArrayMethods
             .meanOfArray(pDp.data.dalambdaFMaxCvSqPixPerSIZE)));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.dSlopeCvSqPlus1FMax));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));
    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================

    lMap.put(EnumDataFile.SCAN_TYPE,
             DbFMax + is + VarsInfo.scanInfo(pDp.scan.vars,
                                             false)
             + " " + MAXF);

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             // y = count per epsilon from max cover filtered data
             COUNT_FOR_COVER_FILTERS);
  }

  /**
   * Puts data into the {@link #mapFMinCover filtered min cover map} using data
   * stored mainly in {@link Data#cFMinCover} in the passed
   * {@link DataProcessor pDp}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile} and the values are taken
   * chiefly from the passed in {@link fraclac.analyzer.DataProcessor}, using
   * its {@link fraclac.analyzer.Data#cFMinCover},
   * {@link fraclac.analyzer.Data#dalambdaFMinCvSqPixPerSIZE},
   * {@link fraclac.analyzer.Data#daSIZEsForFMin} and other values.
   *
   * <p>
   * This method assumes that the data have already been processed using a
   * {@link fraclac.utilities.DataFilter#filterFMinOrFMaxCover minimum cover}.
   *
   */
  void makeFMinCoverMap(boolean pbRotationSummary,
                        DataProcessor pDp)
  {
    Map<EnumDataFile, Object> lMap = mapFMinCover;
    // =========================================================================

    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.cFMinCover.dFractalDimension));

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.cFMinCover.dFractalDimension));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.cFMinCover.dRSq));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.cFMinCover.dStdErr));
    // =========================================================================

    lMap.put(EnumDataFile.PREFACTOR,
             (pDp.data.cFMinCover.dPrefactor));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.cFMinCover.dYIntercept));
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (ArrayMethods
             .meanOfArray(pDp.data.dalambdaFMinCvSqPixPerSIZE)));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.dSlopeCvSqPlus1FMin));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================

    lMap.put(EnumDataFile.SIZES,
             pbRotationSummary
                 ? INF
                 : (pDp.data.daSIZEsForFMin.length));

    lMap.put(EnumDataFile.MIN_SIZE,
             pbRotationSummary
                 ? INF
                 : (pDp.data.daSIZEsForFMin[0]));

    lMap.put(EnumDataFile.MAX_SIZE,
             pbRotationSummary
                 ? INF
                 : (pDp.data.daSIZEsForFMin[pDp.data.daSIZEsForFMin.length - 1]));
    // =========================================================================

    lMap.put(EnumDataFile.SCAN_TYPE,
             DbFMin + is + VarsInfo.scanInfo(pDp.scan.vars,
                                             false)
             + " " + MINF);
    // =========================================================================

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             // y = count per epsilon from cover filtered data
             COUNT_FOR_COVER_FILTERS);
  }

  /**
   * Puts data into the {@link #mapFSS map smoothed (smallest) map} using data
   * stored mainly in {@link Data#statsDB_FSS_ForSlice} in the passed
   * {@link DataProcessor pDp}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile} and the values are taken
   * chiefly from the passed in {@link fraclac.analyzer.DataProcessor}, using
   * its {@link fraclac.analyzer.Data#statsDB_FSS_ForSlice},
   * {@link fraclac.analyzer.Data#statsLLMeanCvSqsF_SS_ForSlice},
   * {@link fraclac.analyzer.Data#d2dSIZEsForF_SS_AtSIZEOnGRID} and other
   * values.
   */
  void makeFSSMap(boolean pbRotationSummary,
                  DataProcessor pDp)
  {
    Map<EnumDataFile, Object> lMap = mapFSS;
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.statsDB_FSS_ForSlice.dMean));

    lMap.put(EnumDataFile.STD_DEVIATION_FOR_FRACTAL_DIMENSIONS,
             (pDp.data.statsDB_FSS_ForSlice.dStdDev));

    lMap.put(EnumDataFile.COEFFICIENT_OF_VARIATION,
             (pDp.data.statsDB_FSS_ForSlice.dCV));
    // =========================================================================

    lMap.put(EnumDataFile.MIN_SIZE,
             pbRotationSummary ? pDp.data.statsDBFSSForSliceSizes.dMin
                 : (minInArray(pDp.data.d2dSIZEsForF_SS_AtSIZEOnGRID)));

    lMap.put(
        EnumDataFile.SIZES,
        pbRotationSummary ? pDp.data.statsDBFSSForSliceSizes.dNum
            : (meanLengthOfArrays(pDp.data.d2dSIZEsForF_SS_AtSIZEOnGRID)));

    lMap.put(EnumDataFile.MAX_SIZE,
             pbRotationSummary ? pDp.data.statsDBFSSForSliceSizes.dMax
                 : (maxInArray(pDp.data.d2dSIZEsForF_SS_AtSIZEOnGRID)));

    lMap.put(
        EnumDataFile.STANDARD_DEVIATION_FOR_SIZES,
        pbRotationSummary ? pDp.data.statsDBFSSForSliceSizes.dStdDev
            : (stdDevForArrayLengths(pDp.data.d2dSIZEsForF_SS_AtSIZEOnGRID)));
    // =========================================================================
    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.optimizedDB_FSS_));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.optimizedRSqForDB_FSS_));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.optimizedSEForDB_FSS_));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.optimizedYintForDB_FSS_));
    // =========================================================================
    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (pDp.data.statsLLMeanCvSqsF_SS_ForSlice.dMean));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.statsLLSlopeCvSqPlus1F_SS_ForSlice.dMean));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================
    lMap.put(EnumDataFile.SCAN_TYPE,
             DbFSS + sAt_grid + is
             + VarsInfo.scanInfo(pDp.scan.vars,
                                 false) + " " + SSF);

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             // y = count per epsilon from cover filtered data
             COUNT_FOR_SMOOTHING_FILTERS);
  }

  /**
   * Puts data into the {@link #mapFS smoothed map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile} and the values are taken
   * chiefly from the passed in {@link fraclac.analyzer.DataProcessor}, using
   * its {@link fraclac.analyzer.Data#statsDB_FS_ForSlice},
   * {@link fraclac.analyzer.Data#optimizedDBFS},
   * {@link fraclac.analyzer.Data#statsDBFSForSliceSizes},
   * {@link fraclac.analyzer.Data#d2dSizesForFSAtSizeOnGRID} and other values.
   *
   * <p>
   * This method assumes that the data have already been processed using a
   * smoothing filter (i.e.,
   * {@link fraclac.analyzer.Calculator#smoothF_SS_(double[], double[][], boolean)}).
   *
   */
  void makeFSMap(boolean pbRotationSummary,
                 DataProcessor pDp)
  {
    Map<EnumDataFile, Object> lMap = mapFS;
    // =========================================================================
    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.statsDB_FS_ForSlice.dMean));

    lMap.put(EnumDataFile.STD_DEVIATION_FOR_FRACTAL_DIMENSIONS,
             (pDp.data.statsDB_FS_ForSlice.dStdDev));

    lMap.put(EnumDataFile.COEFFICIENT_OF_VARIATION,
             (pDp.data.statsDB_FS_ForSlice.dCV));
    // =========================================================================
    lMap.put(EnumDataFile.MIN_SIZE,
             pbRotationSummary ? pDp.data.statsDBFSForSliceSizes.dMin
                 : (ArrayMethods
                 .minInArray(pDp.data.d2dSizesForFSAtSizeOnGRID)));

    lMap.put(EnumDataFile.SIZES,
             pbRotationSummary ? pDp.data.statsDBFSForSliceSizes.dNum
                 : (ArrayMethods.meanLengthOfArrays(
                     pDp.data.d2dSizesForFSAtSizeOnGRID)));

    lMap.put(EnumDataFile.MAX_SIZE,
             pbRotationSummary ? pDp.data.statsDBFSForSliceSizes.dMax
                 : (ArrayMethods
                 .maxInArray(pDp.data.d2dSizesForFSAtSizeOnGRID)));

    lMap.put(EnumDataFile.STANDARD_DEVIATION_FOR_SIZES,
             pbRotationSummary ? pDp.data.statsDBFSForSliceSizes.dStdDev
                 : (ArrayMethods
                 .stdDevForArrayLengths(pDp.data.d2dSizesForFSAtSizeOnGRID)));
    // =========================================================================
    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.optimizedDBFS));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.optimizedRSqForDB_FS));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.optimizedSEForDB_FS));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.optimizedYintForDB_FS));
    // =========================================================================
    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (pDp.data.statsLLMeanCvSqsFSForSlice.dMean));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.statsLLSlopeCvSqPlus1FSForSlice.dMean));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================
    lMap.put(
        EnumDataFile.SCAN_TYPE,
        DbFS + sAt_grid + is
        + VarsInfo.scanInfo(pDp.scan.vars,
                            false) + " "
        + SF);

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             // y = count per epsilon from cover filtered data
             COUNT_FOR_SMOOTHING_FILTERS);
  }

  /**
   * Puts data into the smoothed (biggest) {@link #mapFSB  map}. The headings
   * keys are of type {@link fraclac.writers.Headings.EnumDataFile} and the
   * values are taken chiefly from the passed in
   * {@link fraclac.analyzer.DataProcessor}, using its {@link fraclac.analyzer.Data#statsDB_FSB_ForSlice},
   * {@link fraclac.analyzer.Data#optimizedDB_FSB_},
   * {@link fraclac.analyzer.Data#d2dSIZEsForFsBAtSIZEOnGRID} and other values.
   * <p>
   * This method assumes that the data have already been processed using a
   * smoothing filter (i.e.,
   * {@link fraclac.analyzer.Calculator#smoothF_SB(double[], double[][], boolean)}).
   *
   * @param pbRotationSummary boolean true specifies to use data from arrays fro
   * @param pDp DataProcessor
   */
  public void makeFSBMap(boolean pbRotationSummary,
                         DataProcessor pDp)
  {
    Map<EnumDataFile, Object> lMap = mapFSB;

    lMap.put(EnumDataFile.SCAN_TYPE,
             DbFSB + sAt_grid + is
             + VarsInfo.scanInfo(pDp.scan.vars,
                                 false) + " " + SBF);
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.statsDB_FSB_ForSlice.dMean));

    lMap.put(EnumDataFile.STD_DEVIATION_FOR_FRACTAL_DIMENSIONS,
             (pDp.data.statsDB_FSB_ForSlice.dStdDev));

    lMap.put(EnumDataFile.COEFFICIENT_OF_VARIATION,
             (pDp.data.statsDB_FSB_ForSlice.dCV));

    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.optimizedDB_FSB_));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.optimizedRSqForDB_FSB_));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.optimizedSEForDB_FSB_));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.optimizedYintForDB_FSB_));
    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             // y = count per epsilon from cover filtered data
             COUNT_FOR_SMOOTHING_FILTERS);
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (pDp.data.statsLLMeanCvSqsFsBForSlice.dMean));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.statsLLSlopeCvSqPlus1FsBForSlice.dMean));
    // =========================================================================

    lMap.put(EnumDataFile.MIN_SIZE,
             pbRotationSummary
                 ? pDp.data.statsDbFsbForSliceSizes.dMin
                 : (ArrayMethods.minInArray(
                     pDp.data.d2dSIZEsForFsBAtSIZEOnGRID)));

    lMap
        .put(EnumDataFile.SIZES,
             pbRotationSummary
                 ? pDp.data.statsDbFsbForSliceSizes.dNum
                 : (ArrayMethods
                 .meanLengthOfArrays(pDp.data.d2dSIZEsForFsBAtSIZEOnGRID)));

    lMap.put(EnumDataFile.MAX_SIZE,
             pbRotationSummary
                 ? pDp.data.statsDbFsbForSliceSizes.dMax
                 : (ArrayMethods
                 .maxInArray(pDp.data.d2dSIZEsForFsBAtSIZEOnGRID)));

    lMap.put(EnumDataFile.STANDARD_DEVIATION_FOR_SIZES,
             pbRotationSummary
                 ? pDp.data.statsDbFsbForSliceSizes.dStdDev
                 : (ArrayMethods
                 .stdDevForArrayLengths(pDp.data.d2dSIZEsForFsBAtSIZEOnGRID)));

  }

  /**
   * Puts data into the {@link #mapDm mass box counting dimension map}. The
   * headings keys are of type {@link fraclac.writers.Headings.EnumDataFile},
   * and the values are taken chiefly from the passed in
   * {@link fraclac.analyzer.DataProcessor} using its
   * {@link fraclac.analyzer.Data#statsDmAtSlice} and other values.
   */
  void makeDmMap(boolean pbRotationSummary,
                 DataProcessor pDp)
  {
    Map<EnumDataFile, Object> lMap = mapDm;

    lMap.put(
        EnumDataFile.SCAN_TYPE,
        Dm + sAt_grid + is
        + VarsInfo.scanInfo(pDp.scan.vars,
                            false));
    // =========================================================================

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             pDp.scan.vars.isGray() ? grStart(pDp.scan.vars.isMvsD())
                 + GrayFormat.stringForIntensityCalculation(pDp.scan.vars)
                 : pDp.scan.vars.isMvsD() ? Y_AT_SIZE + is + MASS
                     : pDp.scan.vars.isDlc() ? dlcY
                         : Y_At_GRID_and_SIZE + is + MEAN_MASS
                         + " at " + sGSetMember);

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (-pDp.data.statsDmAtSlice.dMean));
    lMap.put(EnumDataFile.STD_DEVIATION_FOR_FRACTAL_DIMENSIONS,
             (pDp.data.statsDmAtSlice.dStdDev));

    // This is reversed and negative 
    // because the mass dimension is left as negative.
    lMap.put(EnumDataFile.MIN_FOR_FRACTAL_DIMENSIONS,
             (-pDp.data.statsDmAtSlice.dMax));
    // This is reversed and negative 
    // because the mass dimension is left as negative.
    lMap.put(EnumDataFile.MAX_FOR_FRACTAL_DIMENSIONS,
             (-pDp.data.statsDmAtSlice.dMin));

    lMap.put(EnumDataFile.COEFFICIENT_OF_VARIATION,
             (pDp.data.statsDmAtSlice.dCV));
    // =========================================================================
    lMap.put(EnumDataFile.OPTIMAL_FD,
             (-pDp.data.optimizedDm));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.optimizedRSqForDm));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.optimizedSEForDm));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.optimizedYintForDm));
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (pDp.data.statsLLMeanCvSqsAtSlice.dMean));
    lMap.put(EnumDataFile.MIN_LACUNARITY,
             (pDp.data.statsLLMeanCvSqsAtSlice.dMin));
    lMap.put(EnumDataFile.MAX_LACUNARITY,
             (pDp.data.statsLLMeanCvSqsAtSlice.dMax));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(
        EnumDataFile.PREFACTOR_LAC,
        (pDp.scan.vars.isDlc() ? DataProcessor
            .calcPrefactorLac(pDp.data.daPrefactorForDLnc_)
            : pDp.data.dLLPrefactorDmForSlice));

    if (!(pDp.scan.vars.isMvsD() || pDp.scan.vars.isDlc())) {
      lMap.put(EnumDataFile.SLOPE_LACUNARITY,
               (pDp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dMean));
    }
    // =========================================================================
    lMap.put(EnumDataFile.SIZES,
             (pDp.data.dMeanSIZEs));

    lMap.put(EnumDataFile.MIN_SIZE,
             (pDp.data.iMinSIZE));

    lMap.put(EnumDataFile.MAX_SIZE,
             (pDp.data.iMaxSIZE));

    lMap.put(EnumDataFile.STANDARD_DEVIATION_FOR_SIZES,
             (pDp.data.dStdDevSIZE));

  }

  /**
   *
   * @param pbIsGray
   *
   * @return
   */
  String selectDeltaIOrF(boolean pbIsGray)
  {
    return "for " + (pbIsGray ? delta_I : "F");
  }

  /**
   *
   * @param pbIsMvD
   *
   * @return
   */
  String grStart(boolean pbIsMvD)
  {
    return pbIsMvD ? Y_AT_SIZE + is + "Mean " : Y_At_GRID_and_SIZE + is
        + "Mean ";
  }

  /**
   * Puts data into the box counting dimension (D<sub>B</sub>)
   * {@link #mapDB map} using mainly data from the
   * {@link fraclac.analyzer.Data#statsDBAtSlice} and
   * {@link fraclac.analyzer.Data#optimizedDB} (and related stats) as well as
   * other data from the passed in {@link fraclac.analyzer.DataProcessor}.
   *
   * <p>
   * The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}. This method puts values
   * matched to certain keys from binary or grayscale scans. It uses conditional
   * statements to choose between them so that it loads the correct info from
   * the DataProcessor passed in.
   *
   * <li>Loads values for Y, meaning the parameter that varies with box size in
   * the particular scan that was done.
   *
   * <li>Loads a key-value pair for the particular formula that was used to
   * calculate the fractal dimension for this type of scan done; this is a
   * phrase that shows a variable, Y, varying as box size in a regression
   * equation used to approximate the fractal dimension.
   *
   * <li>Load key-value pairs for the mean fractal dimension and two stats
   * associated with the sample of fractal dimensions, being the coefficient of
   * variation and standard deviation. It is assumed that at this point the data
   * structure contains the values for the appropriate scan type.
   *
   * <li>Loads key-value pairs for optimized data. Again, it is assumed that the
   * DataProcessor has calculated these for the gray or binary or whatever type
   * of scan was done.
   *
   * <li>Loads key-value pairs for the box sizes used in the scan.
   *
   * @param pbRotationSummary boolean
   * @param pDp DataProcessor
   */
  public void makeDbMap(boolean pbRotationSummary,
                        DataProcessor pDp)
  {
    // .....................................................................
    // Get a handle for the map that holds data for box counting for
    // binary or grayscale images.
    // .....................................................................
    Map<EnumDataFile, Object> lMap = mapDB;
    // .....................................................................
    // Put into that map a key-value pair for the type of scan.
    lMap.put(EnumDataFile.SCAN_TYPE,
             Db + sAt_grid + is + VarsInfo.scanInfo(pDp.scan.vars,
                                                    false));
    // .....................................................................
    // Load values for Y, meaning the parameter that varies with box size
    // in the particular scan that was done.
    // .....................................................................
    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             pDp.scan.vars.isGray() ? Y_At_GRID_and_SIZE + is
                 + BIG_DELTA_I_AT_SIZE_AND_GRID + is + SUM
                 + small_delta_I_at_SIZE_AND_GRID + "; "
                 + GrayFormat.stringForIntensityCalculation(pDp.scan.vars)
                 : pDp.scan.vars.isDlc() ? Y_At_GRID_and_SIZE
                     + " = Foreground pixels (F) per sample of size "
                     + sSizesSetMember
                     + " in connected set centered on each "
                     + sGSetMember
                     : Y_At_GRID_and_SIZE + is + C_at_grid_and_size
                     + is + COUNT + " at " + sGSetMember);
    // .........................................................................
    // Load a key-value pair for the particular formula that was used
    // to calculate the fractal dimension for this type of scan done;
    // this is a phrase that shows a variable, Y, varying as box size
    // in a regression equation used to approximate the fractal dimension.
    // .....................................................................
    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             (pDp.scan.vars.isGray() ? GrayFormat
                 .grayFractalDimensionString(
                     pDp.scan.vars.getsBinaryOrGrayScanMethod())
                 : NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE));
    // =========================================================================
    // Load the key-value pairs for the numbers calculated from the scan.   
    // =========================================================================
    // .........................................................................
    // Load key-value pairs for the mean fractal dimension and two
    // stats associated with the sample of fractal dimensions, being
    // the coefficient of variation and standard deviation. It is assumed
    // that at this point the data structure contains the values
    // for the appropriate scan type.
    // .....................................................................
    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.statsDBAtSlice.dMean));
    //pDp.data.da
    // .........................................................................
    lMap.put(EnumDataFile.COEFFICIENT_OF_VARIATION,
             (pDp.data.statsDBAtSlice.dCV));
    // .........................................................................
    lMap.put(EnumDataFile.STD_DEVIATION_FOR_FRACTAL_DIMENSIONS,
             (pDp.data.statsDBAtSlice.dStdDev));
    // .........................................................................
    lMap.put(EnumDataFile.MIN_FOR_FRACTAL_DIMENSIONS,
             (pDp.data.statsDBAtSlice.dMin));
    // .........................................................................
    lMap.put(EnumDataFile.MAX_FOR_FRACTAL_DIMENSIONS,
             (pDp.data.statsDBAtSlice.dMax));
    // .........................................................................
    // Load key-value pairs for optimized data. Again, it is assumed
    // that the DataProcessor has calculated these for the gray or binary
    // or whatever type of scan was done.
    // .........................................................................
    lMap.put(EnumDataFile.OPTIMAL_FD,// fractal dimension with highest r^2
             (pDp.data.optimizedDB));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,// r^2 for the regresson line
             (pDp.data.optimizedRSqForDB));

    lMap.put(EnumDataFile.SE_OPTIMAL,// standard error
             (pDp.data.optimizedSEForDB));

    lMap.put(EnumDataFile.YINT_OPTIMAL,// y intercept
             (pDp.data.optimizedYintForDB));
    // =========================================================================
    // .....................................................................
    // Load the prefactor lacunarity value; lacunarity for other types
    // of lacunarity calculations are loaded in the lac map; this one is
    // loaded here because it depends on the fractal dimension itself.
    // .....................................................................
    lMap.put(
        EnumDataFile.PREFACTOR_LAC,
        (pDp.scan.vars.isDlc()
            ? DataProcessor
            .calcPrefactorLac(pDp.data.daPrefactorForDlcPerPixel)
            : (pbRotationSummary ? INF : pDp.data.dLLPrefactorDBForSlice)));
    // .....................................................................
    // Load key-value pairs for the box sizes used in the scan.
    // .....................................................................
    lMap.put(EnumDataFile.SIZES,
             (pDp.data.dMeanSIZEs));

    lMap.put(EnumDataFile.MIN_SIZE,
             (pDp.data.iMinSIZE));

    lMap.put(EnumDataFile.MAX_SIZE,
             (pDp.data.iMaxSIZE));

    lMap.put(EnumDataFile.STANDARD_DEVIATION_FOR_SIZES,
             (pDp.data.dStdDevSIZE));
  }

  /**
   * Puts data into the {@link #mapFAvgCover filtered average cover map}. The
   * headings keys are of type {@link fraclac.writers.Headings.EnumDataFile}.
   * The values are taken mostly from the passed in {@link DataProcessor}, from
   * values for the
   *
   * {@link fraclac.analyzer.Data#fsCountsDavg counts FracStats},
   * {@link fraclac.analyzer.Data#fsCountsDavg.daSizesStats}, and
   *
   * {@link Data#statsLLisAlsoLAMBDAFromlambdaCvSqsForPixAtSIZEsFAvgCover
   * lacunarity}. For the individual value assigned to each key, see the code.
   */
  void makeFAvgCoverMap(boolean pbRotationSummary,
                        DataProcessor pDp)
  {

    Map<EnumDataFile, Object> lMap = mapFAvgCover;
    // =========================================================================
    lMap.put(EnumDataFile.SCAN_TYPE,
             D_OVERBAR_X + is + VarsInfo.scanInfo(pDp.scan.vars,
                                                  false)
             + " Average Cover");

    // =========================================================================
    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             Y_AT_SIZE
             + is
             + "S"
             + sSLASH
             + sGSetSize
             + sp
             + sSuchThat
             + sp
             + "S"
             + is
             + SUM
             + (pDp.scan.vars.isGray() ? delta_I
                 : pDp.scan.vars.isDlc() ? "(F in connected set)" : "C")
             + sAt_grid_and_size + sp + sForAllG);

    lMap.put(
        EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
        pDp.scan.vars.isGray()
            ? (GrayFormat.grayFractalDimensionString(
                pDp.scan.vars.getsBinaryOrGrayScanMethod()))
            : NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);
    // =========================================================================
    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.fsCountsDavg.dFractalDimension));

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.fsCountsDavg.dFractalDimension));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.fsCountsDavg.dRSq));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.fsCountsDavg.dStdErr));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.fsCountsDavg.dYIntercept));
    // =========================================================================
    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (pDp.data//
             .statsLLisAlsoLAMBDAFromlambdaCvSqsForPixAtSIZEsFAvgCover.dMean));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data//
             .fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover.dFractalDimension));
    // =========================================================================
    lMap.put(EnumDataFile.SIZES,
             pbRotationSummary ? pDp.data.fsCountsDavg.daSizesStats.dNum
                 : (pDp.data.fsCountsDavg.daSizes.length));

    lMap.put(EnumDataFile.MIN_SIZE,
             pbRotationSummary
                 ? pDp.data.fsCountsDavg.daSizesStats.dMin
                 : (pDp.data.fsCountsDavg.daSizes[0]));

    lMap.put(EnumDataFile.MAX_SIZE,
             pbRotationSummary
                 ? pDp.data.fsCountsDavg.daSizesStats.dMax
                 : (pDp.data//
                 .fsCountsDavg.daSizes//
                 [pDp.data.fsCountsDavg.daSizes.length - 1]));

    lMap.put(EnumDataFile.STANDARD_DEVIATION_FOR_SIZES,
             pbRotationSummary ? pDp.data.fsCountsDavg.daSizesStats.dStdDev
                 : (pDp.data.dStdDevSIZE));
    // =========================================================================
    lMap.put(EnumDataFile.PREFACTOR,
             pbRotationSummary ? null
                 : (pDp.data.fsCountsDavg.dPrefactor));

  }

  /**
   * Puts data into the
   * {@link #mapFSBFMaxCover smoothed (biggest) max cover map}. The headings
   * keys are of type {@link fraclac.writers.Headings.EnumDataFile}. The values
   * are taken mostly from the passed in {@link DataProcessor}, from its
   * smoothed biggest values in the
   * {@link fraclac.analyzer.Data#cFMaxCover maximum cover calculator}. For the
   * individual value assigned to each key, see the code.
   * <p>
   * This method assumes that the data have already been processed using a
   * {@link fraclac.utilities.DataFilter#filterFMinOrFMaxCover maximum cover}
   * and a smoothing filter (i.e.,
   * {@link fraclac.analyzer.Calculator#smoothF_SB}).
   *
   */
  private void makeFSBMaxCoverMap(boolean pbRotationSummary,
                                  DataProcessor pDp)
  {

    Map<EnumDataFile, Object> lMap = mapFSBFMaxCover;
    // =========================================================================

    lMap.put(EnumDataFile.SCAN_TYPE,
             DbFMaxSB + is + VarsInfo.scanInfo(pDp.scan.vars,
                                               false)
             + " " + MAXF + " with " + SBF);
    // =========================================================================

    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.cFMaxCover.dDB_F_SB));

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.cFMaxCover.dDB_F_SB));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.cFMaxCover.dRSqDB_F_SB));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.cFMaxCover.dYintForDB_F_SB));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.cFMaxCover.dSEForDB_F_SB));

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             COUNT_FOR_COVER_THEN_SMOOTHED_FILTERS);
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (meanOfArray(pDp.data.cFMaxCover.daF_SB_CvSq)));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.dSlopeCvSqPlus1FMaxsB));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));
    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================
    lMap.put(EnumDataFile.SIZES,
             pbRotationSummary
                 ? pDp.data.cFMaxCover.daF_SB_SIZEsStats.dNum
                 : (pDp.data.cFMaxCover.daF_SB_SIZEs.length));

    lMap.put(EnumDataFile.MAX_SIZE,
             pbRotationSummary
                 ? pDp.data.cFMaxCover.daF_SB_SIZEsStats.dMax
                 : (pDp.data.cFMaxCover.daF_SB_SIZEs//
                 [pDp.data.cFMaxCover.daF_SB_SIZEs.length - 1]));

    lMap.put(EnumDataFile.MIN_SIZE,
             pbRotationSummary
                 ? pDp.data.cFMaxCover.daF_SB_SIZEsStats.dMin
                 : (pDp.data.cFMaxCover.daF_SB_SIZEs[0]));
    //--------------------------------------------------------------------
    lMap.put(EnumDataFile.PREFACTOR,
             (pDp.data.cFMaxCover.dPrefactorDB_F_SB));

  }

  /**
   * Puts data into the
   * {@link #mapFSSFMaxCover smoothed (smallest) max cover map}. The headings
   * keys are of type {@link fraclac.writers.Headings.EnumDataFile}.The values
   * are taken mostly from the passed in {@link DataProcessor}, from its
   * smoothed smallest values in the
   * {@link fraclac.analyzer.Data#cFMaxCover maximum cover calculator}. For the
   * individual value assigned to each key, see the code.
   *
   * <p>
   * This method assumes that the data have already been processed using a
   * {@link fraclac.utilities.DataFilter#filterFMinOrFMaxCover maximum cover}
   * and a smoothing filter (i.e.,
   * {@link fraclac.analyzer.Calculator#smoothF_SS_}).
   *
   *
   */
  private void makeFSSMaxCoverMap(boolean pbRotationSummary,
                                  DataProcessor pDp)
  {

    Map<EnumDataFile, Object> lMap = mapFSSFMaxCover;

    lMap.put(EnumDataFile.SCAN_TYPE,
             DbFMaxSS + is + VarsInfo.scanInfo(pDp.scan.vars,
                                               false)
             + " " + MAXF + " with " + SSF);
    // =========================================================================

    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.cFMaxCover.dDB_F_SS));

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.cFMaxCover.dDB_F_SS));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.cFMaxCover.dRSqForDB_F_SS));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.cFMaxCover.dYintForDB_F_SS));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.cFMaxCover.dSEForDB_F_SS));

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             COUNT_FOR_COVER_THEN_SMOOTHED_FILTERS);
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_LACUNARITY,
             (meanOfArray(pDp.data.cFMaxCover.daF_SS_CvSq)));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.dSlopeCvSqPlus1FMaxss));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));
    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================

    lMap.put(EnumDataFile.SIZES,
             (pDp.data.cFMaxCover.daF_SS_SIZEsStats.dNum));
             //(pDp.data.cFMaxCover.daF_SS_SIZEsStats.length));

    //int liMaxSIZEIndex = pDp.data.cFMaxCover.daF_SS_SIZEs.length - 1;
    lMap.put(EnumDataFile.MAX_SIZE,
             //(pDp.data.cFMaxCover.daF_SS_SIZEs[liMaxSIZEIndex]));
             (pDp.data.cFMaxCover.daF_SS_SIZEsStats.dMax));

    lMap.put(EnumDataFile.MIN_SIZE,
             (pDp.data.cFMaxCover.daF_SS_SIZEsStats.dMin));
    //(pDp.data.cFMaxCover.daF_SS_SIZEs[0]));
    // =========================================================================

    lMap.put(EnumDataFile.PREFACTOR,
             (pDp.data.cFMaxCover.dPrefactorForDB_F_SS));

  }

  /**
   * Puts data into the
   * {@link #mapFSSFMinCover smoothe (smallest) min cover map}. The headings
   * keys are of type {@link fraclac.writers.Headings.EnumDataFile}. The values
   * are taken mostly from the passed in {@link DataProcessor}, from its
   * smoothed smallest values in the
   * {@link fraclac.analyzer.Data#cFMinCover minimum cover calculator}. For the
   * individual value assigned to each key, see the code.
   *
   * <p>
   * This method assumes that the data have already been processed using a
   * {@link fraclac.utilities.DataFilter#filterFMinOrFMaxCover minimum cover}
   * and a smoothing filter (i.e.,
   * {@link fraclac.analyzer.Calculator#smoothF_SS_}).
   *
   */
  private void makeFSSMinCoverMap(boolean pbRotationSummary,
                                  DataProcessor pDp)
  {

    Map<EnumDataFile, Object> lMap = mapFSSFMinCover;

    lMap.put(EnumDataFile.SCAN_TYPE,
             DbFMinSS + is + VarsInfo.scanInfo(pDp.scan.vars,
                                               false)
             + " " + MINF + " with " + SSF);
    // =========================================================================

    lMap.put(EnumDataFile.OPTIMAL_FD,
             (pDp.data.cFMinCover.dDB_F_SS));

    lMap.put(EnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.cFMinCover.dDB_F_SS));

    lMap.put(EnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.cFMinCover.dRSqForDB_F_SS));

    lMap.put(EnumDataFile.YINT_OPTIMAL,
             (pDp.data.cFMinCover.dYintForDB_F_SS));

    lMap.put(EnumDataFile.SE_OPTIMAL,
             (pDp.data.cFMinCover.dSEForDB_F_SS));

    lMap.put(EnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(EnumDataFile.DEFINITION_OF_Y,
             COUNT_FOR_COVER_THEN_SMOOTHED_FILTERS);
    // =========================================================================

    lMap.put(EnumDataFile.MEAN_LACUNARITY,

             (meanOfArray(pDp.data.cFMinCover.daF_SS_CvSq)));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.dSlopeCvSqPlus1FMinss));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================

    lMap.put(EnumDataFile.SIZES,
             (pDp.data.cFMinCover.daF_SS_SIZEsStats.dNum));
             //(pDp.data.cFMinCover.daF_SS_SIZEs.length));

    //int liMaxSIZEIndex = pDp.data.cFMinCover.daF_SS_SIZEs.length - 1;
    lMap.put(EnumDataFile.MAX_SIZE,
             (pDp.data.cFMinCover.daF_SS_SIZEsStats.dMax));
    // (pDp.data.cFMinCover.daF_SS_SIZEs[liMaxSIZEIndex]));

    lMap.put(EnumDataFile.MIN_SIZE,
             (pDp.data.cFMinCover.daF_SS_SIZEsStats.dMin));
    // (pDp.data.cFMinCover.daF_SS_SIZEs[0]));
    // =========================================================================

    lMap.put(EnumDataFile.PREFACTOR,
             (pDp.data.cFMinCover.dPrefactorForDB_F_SS));

  }

  /**
   * Puts data into the {@link #mapFSBFMinCover minimum cover map}, using
   * headings keys selected from the
   * {@link fraclac.writers.Headings.EnumDataFile EnumDataFile}. The values are
   * taken mostly from the passed in {@link DataProcessor}, from its
   * {@link fraclac.analyzer.Data#cFMinCover minimum cover calculator}. For the
   * individual value assigned to each key, see the code.
   *
   * <p>
   * This method assumes that the data have already been processed using a
   * {@link fraclac.utilities.DataFilter#filterFMinOrFMaxCover minimum cover}
   * and a smoothing filter (i.e.,
   * {@link fraclac.analyzer.Calculator#smoothF_SB(double[], double[][], boolean)}).
   *
   */
  private <extEnumDataFile extends EnumDataFile> //
      void makeFSBMinCoverMap(boolean pbRotationSummary,
                              DataProcessor pDp)
  {
    Map<EnumDataFile, Object> lMap = mapFSBFMinCover;
    // =========================================================================
    lMap.put(extEnumDataFile.SCAN_TYPE,
             DbFMinSB + is + VarsInfo.scanInfo(pDp.scan.vars,
                                               false)
             + " " + MINF + " with " + SBF);
    // =========================================================================
    lMap.put(extEnumDataFile.OPTIMAL_FD,
             (pDp.data.cFMinCover.dDB_F_SB));

    lMap.put(extEnumDataFile.MEAN_FRACTAL_DIMENSION,
             (pDp.data.cFMinCover.dDB_F_SB));

    lMap.put(extEnumDataFile.R_SQ_OPTIMAL,
             (pDp.data.cFMinCover.dRSqDB_F_SB));

    lMap.put(extEnumDataFile.YINT_OPTIMAL,
             (pDp.data.cFMinCover.dYintForDB_F_SB));

    lMap.put(extEnumDataFile.SE_OPTIMAL,
             (pDp.data.cFMinCover.dSEForDB_F_SB));

    lMap.put(extEnumDataFile.FORMULA_FOR_FRACTAL_DIMENSION,
             NEGATIVE_SLOPE_OF_LOG_LOG_REGRESSION_LINE);

    lMap.put(extEnumDataFile.DEFINITION_OF_Y,
             COUNT_FOR_COVER_THEN_SMOOTHED_FILTERS);
    // =========================================================================
    lMap.put(extEnumDataFile.MEAN_LACUNARITY,
             (meanOfArray(pDp.data.cFMinCover.daF_SB_CvSq)));

    lMap.put(EnumDataFile.SLOPE_LACUNARITY,
             (pDp.data.dSlopeCvSqPlus1FMinsB));

    lMap.put(EnumDataFile.FORMULA_FOR_lambda,
             selectDeltaIOrF(pDp.scan.vars.isGray()));

    lMap.put(EnumDataFile.FORMULA_FOR_LAMBDA_G,
             sFORMULA_FOR_LAMBDA_grid);
    // =========================================================================
    lMap.put(extEnumDataFile.SIZES,
             (pDp.data.cFMinCover.daF_SB_SIZEsStats.dNum));
             //(pDp.data.cFMinCover.daF_SB_SIZEs.length));

    // int liMaxSIZEIndex = pDp.data.cFMinCover.daF_SB_SIZEs.length - 1;
    lMap.put(extEnumDataFile.MAX_SIZE,
             (pDp.data.cFMinCover.daF_SB_SIZEsStats.dMax));
    // (pDp.data.cFMinCover.daF_SB_SIZEs[liMaxSIZEIndex]));

    lMap.put(extEnumDataFile.MIN_SIZE,
             (pDp.data.cFMinCover.daF_SB_SIZEsStats.dMin));
    // (pDp.data.cFMinCover.daF_SB_SIZEs[0]));
    // =========================================================================
    lMap.put(extEnumDataFile.PREFACTOR,
             (pDp.data.cFMinCover.dPrefactorDB_F_SB));

  }

  /**
   * Assigns a name to the key that holds the type of fractal dimension in the
   * fractal dimension and lacunarity data maps in this class.
   * <p>
   * The key is the Enum constant for "NAME" of each map, and the Strings for
   * the names are stored in {@link fraclac.writers.Headings.EnumDataFile
   * EnumDataFile}. The data maps are listed in
	 * {@link #addMapsToFractalDimensionMapLists }.
   */
  <EXT_EnumDataFile extends EnumDataFile, EXT_ENumLacData extends EnumLacData> //
      void nameMaps()
  {

    mapDB.put(EXT_EnumDataFile.NAME,
              Db);

    mapDm.put(EXT_EnumDataFile.NAME,
              Dm);

    mapFAvgCover.put(EXT_EnumDataFile.NAME,
                     D_OVERBAR_X);

    mapFMaxCover.put(EXT_EnumDataFile.NAME,
                     DbFMax);

    mapFSS.put(EXT_EnumDataFile.NAME,
               DbFSS);

    mapFS.put(EXT_EnumDataFile.NAME,
              DbFS);

    mapFSB.put(EXT_EnumDataFile.NAME,
               DbFSB);

    mapFMinCover.put(EXT_EnumDataFile.NAME,
                     DbFMin);

    mapFSBFMaxCover.put(EXT_EnumDataFile.NAME,
                        DbFMaxSB);

    mapFSSFMaxCover.put(EXT_EnumDataFile.NAME,
                        DbFMaxSS);

    mapFSSFMinCover.put(EXT_EnumDataFile.NAME,
                        DbFMinSS);

    mapFSBFMinCover.put(EXT_EnumDataFile.NAME,
                        DbFMinSB);

    // .........................................................................
    // ...................... prob maps 
    // .........................................................................
    mapProb.put(EXT_ENumLacData.NAME,
                PROB);

    mapPD.put(EXT_ENumLacData.NAME,
              PD);

    mapPDOverBins.put(EXT_ENumLacData.NAME,
                      LACUNARITY_FOR_PD_OVER_BINS);

    mapProbOverBins
        .put(EXT_ENumLacData.NAME,
             LACUNARITY_FOR_PROB_OVER_BINS);

  }

  /**
   * Adds lacunarity maps to sublists and adds the sublists to the
   * {@link #listLac lacunarity List}.
   *
   * <pre>
   * listRegLac.add(mapLac);
   * listProbLac.add(mapProb);
   * listProbLac.add(mapPD);
   * listProbLac.add(mapProbOverBins);
   * listProbLac.add(mapPDOverBins);
   * listLac.addAll(listRegLac);
   * listLac.addAll(listProbLac);
   * </pre>
   */
  public void addMapsToLacList()
  {
    listRegLac.add(mapLac);

    listProbLac.add(mapProb);
    listProbLac.add(mapPD);
    listProbLac.add(mapProbOverBins);
    listProbLac.add(mapPDOverBins);

    listLac.addAll(listRegLac);
    listLac.addAll(listProbLac);
  }

  /**
   * Puts entries in the {@link #mapLac lacunarity data  map}, using headings
   * keys selected from
   * {@link fraclac.writers.Headings.EnumLacData EnumLacData}. The values are
   * taken mostly from the passed in {@link DataProcessor}, from its
   * {@link fraclac.analyzer.Data#statsLLMeanCvSqsAtSlice lacunarity Statistics}.
   * For the individual value assigned to each key, see the code. It calls {@link #loadProbabilityLacunarity(fraclac.analyzer.DataProcessor)
   * }
   * if the option to use probability density lacunarity is flagged (i.e.,
   * {@link fraclac.analyzer.Vars#iMaxFrequencies} in the passed
   * {@link DataProcessor pDp.scan.vars.iMaxFrequencies} > 0.
   */
  <EXT_EnumLacData extends EnumLacData> void makeLacMaps(
      boolean pbRotationSummary,
      DataProcessor pDp)
  {

    // this should match with the value for the mean of all
    // mean cv squareds; i.e., the mean of all uppercase Lambdas,
    // where uppercase Lambda is the mean
    // of all lowercase lambdas, which are cv squareds,
    // for all sizes at that grid
    mapLac.put(EXT_EnumLacData.MEAN_LAC,
               (pDp.data.statsLLMeanCvSqsAtSlice.dMean));
    mapLac.put(EXT_EnumLacData.MIN_LAC,
               (pDp.data.statsLLMeanCvSqsAtSlice.dMin));
    mapLac.put(EXT_EnumLacData.MAX_LAC,
               (pDp.data.statsLLMeanCvSqsAtSlice.dMax));

    mapLac.put(
        EXT_EnumLacData.DEFINITION_OF_lambda,
        pDp.scan.vars.isGray() ? GrayFormat
            .stringForIntensityCalculation(pDp.scan.vars)
            : pDp.scan.vars.isMvsD() ? "for Foreground pixels for all sizes"
                : FOR + " " + MASS);

    mapLac.put(EnumLacData.DEFINITION_OF_LAMBDA_G,
               sFORMULA_FOR_LAMBDA_grid);

    mapLac.put(EXT_EnumLacData.CV_FOR_MEAN_LAC,
               (pDp.data.statsLLMeanCvSqsAtSlice.dCV));

    if (!pDp.scan.vars.isMvsD() && !pDp.scan.vars.isDlc()) {

      mapLac.put(EXT_EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE,
                 (pDp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dMean));

      mapLac.put(
          EXT_EnumLacData.CV_OF_ALL_MEANS_OF_SLOPES_OF_CVSQ_PLUS1_VS_SIZE,
          (pDp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dCV));

      mapLac.put(
          EnumLacData.SLOPES_OF_CVSQ_PLUS1_VS_SIZE_avgcover,
          (pDp.data//
          .fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover.dFractalDimension));
    }

    // ........................ LOAD EMPTY PLUS MASS INFO
    // ........................................................................
    if (!pDp.scan.vars.isMvsD() && !pDp.scan.vars.isGray()
        && !pDp.scan.vars.isDlc()) {

      mapLac.put(
          EXT_EnumLacData.MEAN_LAC_FOR_OMEGA,
          (pDp.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID.dMean));

      mapLac.put(EXT_EnumLacData.DEFINITION_OF_lambda_FOR_OMEGA,
                 "includes empty (E) samples and foreground samples");

      mapLac.put(
          EXT_EnumLacData.CV_FOR_LAC_FOR_OMEGA,
          (pDp.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID.dCV));

    }
    if (!pDp.scan.vars.isGray() && !pDp.scan.vars.isDlc()) {
      mapLac.put(EXT_EnumLacData.MEAN_LAMBDAD,
                 (meanOfArray(pDp.data.daLambdaDAtGrid)));

    }

    if (pDp.scan.vars.iMaxFrequencies > 0) {
      loadProbabilityLacunarity(pDp);
    }

  }

  /**
   * Puts data into the {@link #mapProb probabilities map}, {@link #mapPD
   * probability distribution map}, {@link #mapProbOverBins adjusted
   * probabilities map},and {@link #mapProb adjusted probability distribution
   * map}.
   * <p>
   * If the DataProcessor is from a grayscale analysis, calls
   * {@link #loadDataForGrayScaleImagesForProbabilityLacunarity}.
   * <p>
   * The headings keys are of type {@link fraclac.writers.Headings.EnumLacData}.
   */
  <EXT_EnumLacData extends EnumLacData> void loadProbabilityLacunarity(
      DataProcessor pDp)
  {
    mapProb.put(EXT_EnumLacData.MEAN_LAC,
                (pDp.data.statsLLMeanCvSqsUnweightedProbAtSlice.dMean));

    mapProb.put(EXT_EnumLacData.CV_FOR_MEAN_LAC,
                (pDp.data.statsLLMeanCvSqsUnweightedProbAtSlice.dCV));
    // =========================================================================

    mapPD.put(EXT_EnumLacData.MEAN_LAC,
              (pDp.data.statsLLMeanCvSqsWeightedPDAtSlice.dMean));

    mapPD.put(EXT_EnumLacData.CV_FOR_MEAN_LAC,
              (pDp.data.statsLLMeanCvSqsWeightedPDAtSlice.dCV));
    // ========================================================================

    mapProbOverBins
        .put(EXT_EnumLacData.MEAN_LAC,
             (pDp.data//
             .statsLLMeanCvSqsOverBinsUnweightedProbAtSlice.dMean));

    mapProbOverBins
        .put(EXT_EnumLacData.CV_FOR_MEAN_LAC,
             (pDp.data.statsLLMeanCvSqsOverBinsUnweightedProbAtSlice.dCV));
    // ========================================================================

    mapPDOverBins
        .put(EXT_EnumLacData.MEAN_LAC,
             (pDp.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice.dMean));

    mapPDOverBins
        .put(EXT_EnumLacData.CV_FOR_MEAN_LAC,
             (pDp.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice.dCV));
    // ========================================================================

    if (!pDp.scan.vars.isGray()) {
      loadDataForGrayScaleImagesForProbabilityLacunarity(pDp);
    }

  }

  /**
   *
   * @param <EXT_EnumLacData>
   * @param pDp DataProcessor
   */
  public <EXT_EnumLacData extends EnumLacData> //
      void loadDataForGrayScaleImagesForProbabilityLacunarity(DataProcessor pDp)
  {
    mapProb
        .put(EXT_EnumLacData.MEAN_LAC_FOR_OMEGA,
             (pDp.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice.dMean));

    mapProb
        .put(EXT_EnumLacData.CV_FOR_LAC_FOR_OMEGA,
             (pDp.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice.dCV));
    // =========================================================================

    mapPD.put(EXT_EnumLacData.MEAN_LAC_FOR_OMEGA,
              (pDp.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice.dMean));

    mapPD.put(EXT_EnumLacData.CV_FOR_LAC_FOR_OMEGA,
              (pDp.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice.dCV));

    // =========================================================================
    mapProbOverBins
        .put(
            EXT_EnumLacData.MEAN_LAC_FOR_OMEGA,
            (pDp.data//
            .statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice.dMean));

    mapProbOverBins
        .put(
            EXT_EnumLacData.CV_FOR_LAC_FOR_OMEGA,
            (pDp.data//
            .statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice.dCV));
    // =========================================================================
    mapPDOverBins
        .put(EXT_EnumLacData.MEAN_LAC_FOR_OMEGA,
             (pDp.data//
             .statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice.dMean));

    mapPDOverBins
        .put(EXT_EnumLacData.CV_FOR_LAC_FOR_OMEGA,
             (pDp.data//
             .statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice.dCV));

  }

	// ***************************************************************************
}
