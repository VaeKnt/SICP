package fraclac.writers;

import fraclac.utilities.Utils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Methods for writing formatted enumStrings of results from fractal analysis
 * with the FracLac plugin. Has two chief methods, one for writing a long string
 * of data that lists all analysis types in a single row, and one for writing
 * essentially the same data as several shorter enumStrings describing each type
 * of analysis done on a scan.
 *
 * The alternate formats are for visually assessing differences. Each column of
 * long enumStrings compares different images, whereas each column of short
 * enumStrings compares different types of analysis for one image.
 *
 * @version FracLac 2014Jan $Rev: 242 $
 * @version Revision $Id: SummaryStringFormatter.java 50 2013-02-01 03:21:47Z
 * audrey $
 * @author Audrey Karperien akarpe01@postoffice.csu.edu.au
 */
public class SummaryStringFormatter extends AllGsHeadings
{

  /**
   *
   */
  public static StringBuilder lsbLongSummaryFileLine,
      lsbLongSummaryFileLineRotations;

  /**
   *
   */
  public static StringBuilder lsbLongSummaryFileHeadings;

  /**
   *
   */
  public static StringBuilder sbShortSummaryFileLine;

  /**
   *
   */
  public static StringBuilder sbShortSummaryFileHeadings;

  // ...........................................................................
  // ...................... constructor ............................
  // ...........................................................................
  /**
   * Constructor calls functions to make short and long summary enumStrings and
   * headings.
   *
   * @param pbRotateToIndividualFile
   * @param pbRotated
   * @param pMaps
   * @param pbDoFMinCover
   * @param pbDoSmoothed
   * @param isMvsD
   * @param isSLAC
   *
   * @see #storeLongSummaryFileStringAndHeadings
   * @see #storeShortSummaryFileStringsAndHeadings
   */
  public SummaryStringFormatter(boolean pbRotateToIndividualFile,
                                SummaryStringDataMapper pMaps,
                                boolean pbDoFMinCover,
                                boolean pbDoSmoothed,
                                boolean isMvsD,
                                boolean isSLAC)
  {

    storeLongSummaryFileStringAndHeadings(pbRotateToIndividualFile,
                                          pMaps);

    storeShortSummaryFileStringsAndHeadings(pMaps,
                                            pbDoFMinCover,
                                            pbDoSmoothed,
                                            isMvsD,
                                            isSLAC);

  }

  /**
   * Controller method makes a very long tabbed string of data and a correlated
   * string of headings, using the passed data object.
   *
   * The enumStrings are stored in local variables: (
   * {@link fraclac.analyzer.Vars#sbLongSummaryFileLine} and
   * {@link fraclac.analyzer.Vars#sbLongSummaryFileHeadings}.
   *
   * Calls {@link #storeLongSummaryString} several times with different
   * parameters to print various lists of data maps stored in the object.
   *
   * Uses the int returned internally from each such call as a parameter to the
   * next call so that the headings in the string are numbered consecutively.
   *
   * @param pbRotateToIndividualFile
   * @param pMaps SummaryStringDataMapper
   */
  public static void storeLongSummaryFileStringAndHeadings(
      boolean pbRotateToIndividualFile,
      SummaryStringDataMapper pMaps)
  {
    // -----------------------------------------------------------------------
    // Start by initializing the String holding objects where the data will be 
    // inserted.
    // -----------------------------------------------------------------------
    lsbLongSummaryFileLine = new StringBuilder();
    lsbLongSummaryFileLineRotations = new StringBuilder();
    lsbLongSummaryFileHeadings = new StringBuilder();
    // ------------------------------------------------------------------------
    // Initialize a column number so the first heading has "1" prepended to it. 
    // The column number is passed in and returned for each
    // string so that the headings columns are numbered consecutively.
    // ------------------------------------------------------------------------
    int liCol = 1;
    // ------------------------------------------------------------------------
    // Now make a series of calls to a function that retrieves information
    // for a headings string collection of entries and a corresponding data 
    // collection. The headings and data entries are correlated by being 
    // inserted in the same order for each entry.
    // ------------------------------------------------------------------------
    // Start by adding general file description information.
    // ------------------------------------------------------------------------    
    liCol = storeLongSummaryString(
        pbRotateToIndividualFile,
        pMaps.listGeneralInfo,
        liCol,
        null,
        EnumSet.of(Headings.EnumInfoData.INFO,
                   Headings.EnumInfoData.SCAN_POSITIONS));
    // Add data for a standard mass and count scan.
    liCol = storeLongSummaryString(pbRotateToIndividualFile,
                                   pMaps.listMassAndCount,
                                   liCol,
                                   Headings.EnumDataFile.NAME,
                                   SummaryStringDataMapper.eSetMassAndCount);
    // Add data for average cover.
    liCol = storeLongSummaryString(pbRotateToIndividualFile,
                                   pMaps.listFAverageCover,
                                   liCol,
                                   Headings.EnumDataFile.NAME,
                                   SummaryStringDataMapper.eSetFiltered);
    // Add data for minimum cover.
    liCol = storeLongSummaryString(pbRotateToIndividualFile,
                                   pMaps.listFMinFMaxCover,
                                   liCol,
                                   Headings.EnumDataFile.NAME,
                                   SummaryStringDataMapper.eSetFiltered);
    // Add data for smoothed filtered scans.
    liCol = storeLongSummaryString(pbRotateToIndividualFile,
                                   pMaps.listFS,
                                   liCol,
                                   Headings.EnumDataFile.NAME,
                                   SummaryStringDataMapper.eSetSmoothed);
    // Add data for filtered scans.
    liCol = storeLongSummaryString(pbRotateToIndividualFile,
                                   pMaps.listFSFMinFMaxCover,
                                   liCol,
                                   Headings.EnumDataFile.NAME,
                                   SummaryStringDataMapper.eSetFiltered);
    // Add data for lacunarity.
    liCol = storeLongSummaryString(pbRotateToIndividualFile,
                                   pMaps.listRegLac,
                                   liCol,
                                   null,
                                   EnumSet.complementOf(EnumSet.of(
                                           Headings.EnumLacData.NAME)));
    // Add data for probability density lacunarity.
    liCol = storeLongSummaryString(pbRotateToIndividualFile,
                                   pMaps.listProbLac,
                                   liCol,
                                   Headings.EnumLacData.NAME,
                                   SummaryStringDataMapper.eSetProbLac);
    // Add general information about the scan.
    storeLongSummaryString(
        pbRotateToIndividualFile,
        pMaps.listGeneralInfo,
        liCol,
        null,
        EnumSet.of(
            Headings.EnumInfoData.FOREGROUND_PIX,
            Headings.EnumInfoData.TOTAL_PIX,
            Headings.EnumInfoData.STD_DEV_FG_PIX,
            Headings.EnumInfoData.SCAN_ELEMENT,
            Headings.EnumInfoData.MIN_PIX_DENSITY,
            Headings.EnumInfoData.MAX_PIX_DENSITY,
            Headings.EnumInfoData.SIZES,
            Headings.EnumInfoData.MIN_SIZE,
            Headings.EnumInfoData.MAX_SIZE,
            Headings.EnumInfoData.SIGMA_FOR_SIZES,
            Headings.EnumInfoData.MEAN_CV_FOR_COUNT_FROM_ALL_GRID_SCANS,
            Headings.EnumInfoData.MEAN_CV_COUNT_VS_MEAN_CV_OMEGA_ALL_GRIDS,
            Headings.EnumInfoData.SLIDE_X,
            Headings.EnumInfoData.SLIDE_Y,
            Headings.EnumInfoData.FOREGROUND_COLOUR,
            Headings.EnumInfoData.DENSITY,
            Headings.EnumInfoData.SPAN_RATIO,
            Headings.EnumInfoData.HULL_CENTRE_OF_MASS,
            Headings.EnumInfoData.HULL_MAX_SPAN,
            Headings.EnumInfoData.AREA,
            Headings.EnumInfoData.PERIMETER,
            Headings.EnumInfoData.CIRCULARITY,
            Headings.EnumInfoData.BOUNDING_RECT_WIDTH,
            Headings.EnumInfoData.BOUNDING_RECT_HEIGHT,
            Headings.EnumInfoData.HULL_MAX_RADIUS,
            Headings.EnumInfoData.HULL_MAX_OVER_MIN_RADII,
            Headings.EnumInfoData.HULL_CV_FOR_RADII,
            Headings.EnumInfoData.HULL_MEAN_RADIUS,
            Headings.EnumInfoData.CIRCLE_CENTRE,
            Headings.EnumInfoData.CIRCLE_DIAMETER,
            Headings.EnumInfoData.CIRCLE_MAX_RADIUS,
            Headings.EnumInfoData.CIRCLE_MAX_OVER_MIN,
            Headings.EnumInfoData.CIRCLE_CV_RADII,
            Headings.EnumInfoData.CIRCLE_MEAN_RADIUS,
            Headings.EnumInfoData.CIRCLE_METHOD
        ));

  }

  

  /**
   * Appends tabbed Strings to the
   * {@link fraclac.analyzer.Vars#sbLongSummaryFileLine summary file}
   * StringBuilder and its associated
   * {@link fraclac.analyzer.Vars#sbLongSummaryFileHeadings headings}
   * StringBuilder.
   *
   * One tabbed string ending in a tab, with no newline characters, is appended
   * for each Map in the passed List. The headings string is also numbered.
   *
   * Data and headings are selected according to the passed {@code keySet}. The
   * values appended to the headings string are from the key.toString method,
   * which the function assumes has been overridden in the original enum to
   * return the appropriate string. In the FraLac plugin, the passed lists are
   * based on one of the data enum sets in {@link fraclac.writers.Headings}.
   *
   * @param <T>
   * @param pbRotateToIndividualFile
   * @param pbRotated * @param pListOfDataMaps ArrayList holding data maps to
   * read in to the StringBuilder
   * @param pListOfDataMaps
   * @param piHeadingNumber int for the starting number for numbering each
   * heading; is incremented for each string value appended then returned
   * @param NAME a T (an Enum constant) that identifies the value (a String) in
   * the passed map that stands for the name of its data type; the value is
   * appended as " for NAME" to each map value unless NAME or its value is
   * {@code null}
   * @param pKeySet the values appended to the StringBuilder are only and all of
   * the values mapped to the keys in {@code keySet} except for any value mapped
   * under {@code NAME}, which is skipped
   *
   * @return int value for the last number for numbering the headings, plus 1;
   * this value is returned so that when the method is called in sequence the
   * next call can use the previously returned value to continue numbering
   * headings in order
   */
  public static <T extends Enum<?>> int storeLongSummaryString(
      boolean pbRotateToIndividualFile,
      ArrayList<Map<? extends DataTypesInterface, Object>> pListOfDataMaps,
      int piHeadingNumber,
      T NAME,
      Set<? extends DataTypesInterface> pKeySet)
  {

    // Go through the passed list of data maps
    for (Map<? extends DataTypesInterface, Object> pListOfDataMap : pListOfDataMaps) {
      // ===============================================================
      Map<?, ?> thisMap = (Map) pListOfDataMap;
      String lsAbbreviatedDataTypeName = NAME == null ? null
          : thisMap.get(NAME).toString();
      for (Object lKey : pKeySet) {
        // ================================================================
        if (lKey.equals(NAME)) {
          continue;
        }

        String lsValue = Utils.fnum(thisMap.get(lKey));

        if (pbRotateToIndividualFile) {
          lsbLongSummaryFileLineRotations.append(lsValue).append(TAB);
        } else {
          lsbLongSummaryFileLine.append(lsValue).append(TAB);
        }

        lsbLongSummaryFileHeadings
            .append(piHeadingNumber++)
            .append(". ")
            .append(lKey.toString())
            // assumes that toString has been overridden
            // to return the heading associated with the summary
            // file over all GRIDs
            .append(lsAbbreviatedDataTypeName == null ? TAB
                    : FOR + lsAbbreviatedDataTypeName + TAB);
        // ===============================================================
      }// end for every key
    } // end for every map

    return piHeadingNumber;

  }

  /**
   * Calls a function repeatedly to append lines of data to the internal
   * StringBuilder for the
   * {@link fraclac.analyzer.Vars#sbShortSummaryFileLine short summary file}.
   * Each line starts with a tabbed string of the values stored in the INFO and
   * SCAN_POSITIONS keys of the
   * {@link SummaryStringDataMapper#mapGeneralInfo info map} from the passed
   * SummaryStringDataMapper object.
   *
   * Lines are for all of the following, depending on the passed booleans:
   * <ul>
   * <li>{@link SummaryStringDataMapper#listMassAndCount }
   * <li>{@link SummaryStringDataMapper#listFAverageCover }
   * <li>{@link SummaryStringDataMapper#listFMinFMaxCover }
   * <li>{@link SummaryStringDataMapper#listFS }
   * <li>{@link SummaryStringDataMapper#listFSFMinFMaxCover }
   * </ul>
   *
   * The data enumStrings are made by passing a keySet that includes all of the
   * pairs in {@link Headings.EnumDataFile}, except the NAME key.
   * <p>
   *
   * It also makes a string of numbered and tabbed
   * {@link #makeShortSummaryFileHeadings headings} string corresponding to the
   * keySet used to make the data enumStrings. Prior to making any of the calls,
   * it initializes the data and headings StringBuilders.
   *
   * @param pMaps SummaryStringDataMapper previously filled with data from box
   * counting
   * @param pbDoFMinCover boolean
   * @param pbDoSmoothed boolean
   * @param pbIsMvsD boolean
   * @param pbIsSLAC boolean
   */
  public static void storeShortSummaryFileStringsAndHeadings(
      SummaryStringDataMapper pMaps,
      boolean pbDoFMinCover,
      boolean pbDoSmoothed,
      boolean pbIsMvsD,
      boolean pbIsSLAC)
  {
    // .........................................................................
    // ..................... INITIALIZE ..........................
    // .........................................................................
    sbShortSummaryFileHeadings = new StringBuilder();
    sbShortSummaryFileLine = new StringBuilder();
    // =========================================================================
    // Make enumSet defining the mapped data to write.
    Set<? extends DataTypesInterface> lKeySet = EnumSet
        .complementOf(EnumSet.of(Headings.EnumDataFile.NAME));
    // .........................................................................
    // ............. MAKE A HEADINGS STRING. ......................
    // .........................................................................
    // =========================================================================
    // Start with the INFO and SCAN_POSITIONS headings; remember to match
    // them to the data string below.
    Set<? extends DataTypesInterface> infoKeySet = EnumSet.of(
        Headings.EnumInfoData.INFO,
        Headings.EnumInfoData.SCAN_POSITIONS);
    int piHeadingNumber = 1;
    for (DataTypesInterface key : infoKeySet) {
      sbShortSummaryFileHeadings.append(piHeadingNumber++).append(". ")
          .append(key.toString()).append(TAB);
    }
    // =========================================================================
    // Add headings using the same enumSet as for the data,
    // writing the enumConstant values instead of the mapped data values.
    for (DataTypesInterface key : lKeySet) {
      sbShortSummaryFileHeadings.append(piHeadingNumber++).append(". ")
          .append(key.toString()).append(TAB);
    }
    // =========================================================================
    // .........................................................................
    // ................. WRITE THE MAPPED DATA TO THE STRING ..................
    // .........................................................................
    // =========================================================================
    // Make a string with the first two pieces of information about the scan
    // in general; these apply to all of the data appended below so are passed
    // to the function that makes each line, so they can be prepended to
    // match their position in the headings string.
    String lsTabbedInfoString = pMaps.mapGeneralInfo
        .get(Headings.EnumInfoData.INFO)
        + TAB
        + pMaps.mapGeneralInfo
        .get(Headings.EnumInfoData.SCAN_POSITIONS) + TAB;
    // =========================================================================
    // There is an exception for MvsD scans, for which only the mass list is
    // used
    ArrayList<Map<? extends DataTypesInterface, Object>> lalTempMassList
        = new ArrayList<Map<? extends DataTypesInterface, Object>>();
    if (pbIsMvsD) {
      lalTempMassList.add(pMaps.mapDm);
    }
    // =========================================================================
    sbShortSummaryFileLine.append(WriteUtilities.appendTabbedData(
        lsTabbedInfoString,
        pbIsMvsD ? lalTempMassList : pMaps.listMassAndCount,
        lKeySet));
    // =========================================================================
    // Append lines for optional data according to the passed booleans.
    if (!(pbIsMvsD || pbIsSLAC)) {

      sbShortSummaryFileLine.append(WriteUtilities.appendTabbedData(
          lsTabbedInfoString,
          pMaps.listFAverageCover,
          lKeySet));
    }
    // =========================================================================
    if (pbDoFMinCover || pbDoSmoothed) {
      if (pbDoFMinCover && pbDoSmoothed) {

        sbShortSummaryFileLine.append(WriteUtilities.appendTabbedData(
            lsTabbedInfoString,
            pMaps.listFMinFMaxCover,
            lKeySet));
        sbShortSummaryFileLine.append(WriteUtilities.appendTabbedData(
            lsTabbedInfoString,
            pMaps.listFS,
            lKeySet));
        sbShortSummaryFileLine
            .append(WriteUtilities.appendTabbedData(
                    lsTabbedInfoString,
                    pMaps.listFSFMinFMaxCover,
                    lKeySet));
      } // =====================================================================
      else if (pbDoFMinCover) {

        sbShortSummaryFileLine.append(WriteUtilities.appendTabbedData(
            lsTabbedInfoString,
            pMaps.listFMinFMaxCover,
            lKeySet));
      } // =====================================================================
      else {
        sbShortSummaryFileLine.append(WriteUtilities.appendTabbedData(
            lsTabbedInfoString,
            pMaps.listFS,
            lKeySet));
      }
    }
  }

}
