package fraclac.writers;

import fraclac.analyzer.*;
import fraclac.utilities.ArrayMethods;
import static fraclac.utilities.Symbols.*;
import fraclac.utilities.Utils;
import static fraclac.utilities.Utils.fnum;
import ij.IJ;
import ij.WindowManager;
import ij.io.DirectoryChooser;
import ij.io.OpenDialog;
import ij.text.TextWindow;
import java.io.*;

/**
 *
 * @version FracLac 2014Jan $Rev: 245 $
 * @version Revision $Id: ResultsFilesWriter.java 53 2013-02-01 09:09:49Z audrey
 * $
 */
public class ResultsFilesWriter extends AllGsHeadings
{

  // ==========================================================================
  //                               Fields 
  // ==========================================================================
  /**
   *
   */
  public static int textWindowHeight = 500,
      /**
       *
       */
      textWindowWidth = 700;

  /**
   *
   */
  public static boolean //
      /**
       *
       */
      bIsFirstLCFDWrite = true;

  /**
   * TextWindow for displaying results of analyses in FracLac for ImageJ. The
   * FracLac TextWindows generally need UTF-8 encoding because they contain
   * headings with symbols that have to be interpreted correctly. The
   * TextWindows are not used if files are being saved instead of shown on the
   * screen.
   */
  public static TextWindow //
      summaryWindowShortStrings,
      lcfdSummarizedDataWindow,
      lcfdWindow,
      dataFileWindow;

  // ==========================================================================
  //                              Methods 
  // ==========================================================================
  /**
   * Shows or saves data for each SIZE from a Box Count (standard box count). To
   * save, it calls {@link #saveDataFileOrAppend }, or else it displays a new
   * dataFileWindow, both based on the
   * {@link fraclac.analyzer.Vars#sbDataFileBoxCountSmoothed}
   *
   *
   * @param pVars Vars
   */
  public static void showOrSaveDataFileForSmoothedBoxCount(Vars pVars)
  {
    String lsFileName = RESULTS_WINDOW_TITLE_SMOOTHED_BOX_COUNT_DATA;
    StringBuilder lsbData = pVars.sbDataFileBoxCountSmoothed;
    String lsHeadings = Headings.S_TABBED_DATA_FILE_HEADINGS_SMOOTHED;
    if (pVars.bSaveResults) {

      saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                           lsFileName + pVars.getsDotXlsOrTxt(),
                           lsHeadings,
                           lsbData);
    } else {
      dataFileWindow = new TextWindow(lsFileName,
                                      lsHeadings,
                                      lsbData.toString(),
                                      textWindowWidth,
                                      textWindowHeight);
      dataFileWindow.setVisible(true);
    }
  }

  /**
   * Returns a string of data from the convex hull and minimum bounding circle.
   * The string has no TAB nor new line at the end.
   *
   *
   * @param pVars Vars is not changed
   * @param pCircStatsForCircAndHull CircStats instance with data precalculated
   *
   *
   * @return String of data for the hull and circle data stored in the
   * {@link CircStats} instance
   */
  public static String hullandCircleDataWriter(
      Vars pVars,
      CircStats pCircStatsForCircAndHull)
  {
    String lsData = fnum(pVars.getdTotalForegroundPixels())
        + TAB
        + fnum(pVars.getdTotalPixelsInImageArea())
        + TAB
        + fnum(pVars.getdTotalForegroundPixels()
            / pVars.getdAreaOfHull()) + TAB
        + pCircStatsForCircAndHull.sSpanRatio + TAB
        + pCircStatsForCircAndHull.sMassCentre + TAB
        + pCircStatsForCircAndHull.sMaxSpanOfPixels + TAB
        + pCircStatsForCircAndHull.sArea + TAB
        + pCircStatsForCircAndHull.sPerimeter + TAB
        + pCircStatsForCircAndHull.sCircularity + TAB
        + pCircStatsForCircAndHull.sMargWidth + TAB
        + pCircStatsForCircAndHull.sMargHeight + TAB
        + pCircStatsForCircAndHull.sMaxRadius + TAB
        + pCircStatsForCircAndHull.sMaxOverMinRadii + TAB
        + pCircStatsForCircAndHull.sCVRadii + TAB
        + pCircStatsForCircAndHull.sMeanOfHullRadii + TAB
        + pCircStatsForCircAndHull.sCircleCentre + TAB
        + pCircStatsForCircAndHull.sCircleDiameter + TAB
        + pCircStatsForCircAndHull.sMaxRadiusCircle + TAB
        + pCircStatsForCircAndHull.sMaxOverMinRadiiCircle + TAB
        + pCircStatsForCircAndHull.sCVRadiiCircle + TAB
        + pCircStatsForCircAndHull.sMeanOfHullCircleRadii + TAB
        + pVars.getsMethodUsedForBoundingCircle();

    return lsData;
  }

  /**
   * Puts up a text window or saves a file showing the masses and their
   * frequencies stored in {@link fraclac.analyzer.Data#sb2dBinnedFrequencies}
   * and {@link fraclac.analyzer.Data#sb2dBinnedMasses}.
   *
   *
   * @param pGridSet
   * @param psbaTitles
   * @param pDP
   * @param pVars Vars
   */
  public static void writeBinnedFrequenciesAndMasses(GridSet pGridSet,
                                                     StringBuilder[] psbaTitles,
                                                     DataProcessor pDP,
                                                     Vars pVars)
  {
    StringBuilder lsbHeadings = new StringBuilder(ELEMENT_SIZE_SIZE + TAB
        + epsilon + TAB + MEAN_PROB);

    for (int i = 0; i < pVars.iMaxFrequencies; i++) {
      lsbHeadings.append(TAB + "Probability or Bin");
    }

    StringBuilder lsbDataToWrite = makeFrequenciesString(psbaTitles,
                                                         pGridSet,
                                                         pDP,
                                                         pVars);

    String lsName = "Probabilities & Masses";

    if (pVars.bSaveResults) {
      saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                           lsName
                           + pVars.getsDotXlsOrTxt(),
                           lsbHeadings.toString(),
                           lsbDataToWrite);
    } else {
      TextWindow lTW = new TextWindow(lsName,
                                      lsbHeadings.toString(),
                                      lsbDataToWrite.toString(),
                                      700,
                                      500);
      lTW.setVisible(true);

    }
  }

  /**
   *
   * @param psbaTitles
   * @param pGridSet
   * @param pDP
   * @param pVars
   *
   * @return
   */
  public static StringBuilder makeFrequenciesString(
      StringBuilder[] psbaTitles,
      GridSet pGridSet,
      DataProcessor pDP,
      Vars pVars)
  {
    StringBuilder lsbBinnedFreqAndMass = new StringBuilder(psbaTitles[0]
        + newline);

    String lsWhatMassIs = pVars.isGray() ? GrayFormat
        .stringForIntensityCalculation(pVars) : "Foreground Pixels at "
        + epsilon;

    StringBuilder lsbHeads = new StringBuilder("" + newline + newline
        + "Binned Probabilities" + newline + ELEMENT_SIZE_SIZE + TAB
        + epsilon + TAB + MEAN_PROB);

    for (int i = 0; i < pVars.iMaxFrequencies; i++) {
      lsbHeads.append(TAB).append("Probability");
    }

    lsbHeads.append(newline);

    int liLongestArraySize = ArrayMethods
        .sizeOfLongestArray(pDP.data.sb2dBinnedFrequencies);

    liLongestArraySize = Math
        .max(pVars.iMaxFrequencies,
             liLongestArraySize);

    StringBuilder lsbMH = new StringBuilder("" + newline + newline
        + "Bin Value = " + lsWhatMassIs + newline + ELEMENT_SIZE_SIZE
        + TAB + epsilon + TAB + " ");

    for (int i = 0; i < liLongestArraySize; i++) {

      lsbMH.append(TAB).append("Midpoint of Bin ").append(epsilon);
    }

    lsbMH.append(newline);

    for (int liGRID = 0; liGRID < pVars.iNumGrids; liGRID++) {

      StringBuilder lsbDataString = new StringBuilder("");
      StringBuilder lsbMasses = new StringBuilder("");

      for (int liSIZEIndex = 0;
          liSIZEIndex < pGridSet.i2dSizes[liGRID].length; liSIZEIndex++) {

        lsbDataString
            .append(fnum(pGridSet.i2dSizes[liGRID][liSIZEIndex]))
            .append(TAB)
            .append(fnum(pGridSet.d2dEpsilons[liGRID][liSIZEIndex]))
            .append(TAB)
            .append(fnum(
                    pDP.data.d2dMeanOfUnweightedProbAtSIZEOnGRID//
                    [liGRID][liSIZEIndex])).
            append(TAB)
            .append(pDP.data.sb2dBinnedFrequencies[liGRID][liSIZEIndex])
            .append(newline);

        lsbMasses
            .append(fnum(pGridSet.i2dSizes[liGRID][liSIZEIndex]))
            .append(TAB)
            .append(fnum(pGridSet.d2dEpsilons[liGRID][liSIZEIndex]))
            .append(TAB).append("").append(TAB)
            .append(pDP.data.sb2dBinnedMasses[liGRID][liSIZEIndex])
            .append(newline);
      }
      lsbBinnedFreqAndMass.append("Grid Location ").append(liGRID)
          .append(newline);

      lsbBinnedFreqAndMass.append(lsbHeads.toString())
          .append(lsbDataString.toString()).append(newline);

      lsbBinnedFreqAndMass.append(lsbMH.toString())
          .append(lsbMasses.toString()).append(newline);

    }
    return lsbBinnedFreqAndMass;
  }

  /**
   *
   * @param pVars the value of pVars
   */
  public static void writeDlcByPixel(Vars pVars)
  {
    String lsFileName = Dlc_PIXEL_RESULTS_TITLE + " "
        + pVars.getsTitleAndSliceLabelAndSliceNumber();
    String lsHeadings = WriteUtilities
        .toTabbedString(DLCWriter.saDlcPixelSummaryHeadings);
    StringBuilder lsbData = new StringBuilder(pVars.sbDlcByPixel);
    if (pVars.bSaveResults) {

      saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                           lsFileName + pVars.getsDotXlsOrTxt(),
                           lsHeadings,
                           lsbData);
    } else {

      lcfdSummarizedDataWindow = new TextWindow(lsFileName,
                                                lsHeadings,
                                                lsbData.toString(),
                                                textWindowWidth,
                                                textWindowHeight);

      lcfdSummarizedDataWindow.setVisible(true);
    }
  }

  /**
   * Shows or saves
   * {@link fraclac.analyzer.Vars#sbDataFileForEachGridOnThisSlice data} for
   * each size SIZE from a Box Count (standard box count). To save, it calls
   * {@link #saveDataFileOrAppend}, or else it displays a new dataFileWindow.
   * Both use {@link fraclac.analyzer.Vars#sbDataFileForEachGridOnThisSlice}.
   *
   *
   * @param pVars Vars
   */
  public static void showOrSaveDataFileForStandardOrSLacBoxCount(Vars pVars)
  {
    if (pVars.sbDataFileForEachGridOnThisSlice == null) {
      return;
    }
    if (pVars.sbDataFileForEachGridOnThisSlice.length() == 0) {
      return;
    }
    // .....................................................................
    // Adjust the headings string to match the number of grid orientations.
    // It is always 1 for sliding box lacunarity.
    // .....................................................................
    StringBuilder lsbHeadings = gridDataFileHeadings(pVars);
    // .....................................................................
    String lsFileName = pVars.isSLAC() ? PER_SCAN_DATA_FILE_TITLE_SLAC
        : PER_SCAN_DATA_FILE_TITLE;
    // .....................................................................
    StringBuilder lsbData = new StringBuilder(
        pVars.sbDataFileForEachGridOnThisSlice);
    // =========================================================================
    if (pVars.bSaveResults) //
    {
      saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                           lsFileName + pVars.getsDotXlsOrTxt(),
                           lsbHeadings.toString(),
                           lsbData);
    } else //
    {
      dataFileWindow = new TextWindow(lsFileName,
                                      lsbHeadings.toString(),
                                      lsbData.toString(),
                                      textWindowWidth,
                                      textWindowHeight);

      dataFileWindow.setVisible(true);
    }
  }

  /**
   * @param pVars
   */
  public static void writeImageTxtFile(Vars pVars)
  {
    {
      if (pVars.bSaveResults) {

        String lsTimeStamp = fnum(System.currentTimeMillis());

        saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                             "TxtImg" + lsTimeStamp + pVars.
                             getsTitleAndSliceLabelAndSliceNumber()
                             + ".txt",
                             "",
                             pVars.sbTextImage);
      } else {

        String lsHeadings = "";
        // int end = pVars.sbTextImage.indexOf("");//"\n");
        //
        // for (int i = 0; i < end; i++)
        // {
        // lsHeadings += TAB;
        // }2014 jan ak removed because now text image was
        // throwing null pointer exception when it got here
        // and tried to write the file but it works
        // without the starting line
        TextWindow lRWtxt = new TextWindow("textImage"
            + pVars.getsTitleAndSliceLabelAndSliceNumber(),
                                           lsHeadings,
                                           pVars.sbTextImage.toString(),
                                           textWindowWidth,
                                           textWindowHeight);
        lRWtxt.setVisible(true);
      }
    }
  }

  /**
   * @param pVars
   */
  public static void showOrSaveShortSummaryFile(Vars pVars)
  {
    if (pVars.isMF() && !pVars.bShowDataForEachGrid) {
      return;
    }
    StringBuilder lsHeadings = new StringBuilder(
        pVars.sbShortSummaryFileHeadings.toString());
    StringBuilder lsbData = new StringBuilder(pVars.sbShortSummaryFileLine);
    String lsTitle = FracLacV + " Scan Types";

    showOrSaveDataFile(lsbData,
                       lsHeadings,
                       lsTitle,
                       pVars);
  }

  /**
   * Returns a matrix string that can be imported into ImageJ as an image using
   * Import>TextImage.
   *
   *
   * @param pd2dPixelXY double [][] with coordinates for each pixel as x and y
   * @param pdaFractalDimensions double [] of fractal dimensions calculated for
   * each pixel from a local connected fractal dimension scan
   * @param piUserForeground a colour for foreground - it will be used to create
   * the filler colour, which is inverted to either 255 or 0
   *
   *
   * @return a string that can be saved as a file and opened as an image showing
   * colour coding according to the passed values
   */
  public static StringBuilder makeTextImageString(double[][] pd2dPixelXY,
                                                  double[] pdaFractalDimensions,
                                                  int piUserForeground)
  {
    int liMaxX = (int) maxInArray(pd2dPixelXY[0]);

    int liMaxY = (int) maxInArray(pd2dPixelXY[1]);

    int liMinX = (int) minArray(pd2dPixelXY[0]);

    int liMinY = (int) minArray(pd2dPixelXY[1]);

    int liFiller = piUserForeground == 0 ? 255 : 0;

    int liSzY = liMaxY - liMinY + 1;
    int liSzX = liMaxX - liMinX + 1;

    int[][] li2dColourCodes = new2dArray(liSzY,
                                         liSzX,
                                         liFiller);

    for (int i = 0; i < pd2dPixelXY[0].length; i++) {

      int x = (int) pd2dPixelXY[0][i] - liMinX;
      int y = (int) pd2dPixelXY[1][i] - liMinY;
      int newcolour = (int) (255.0F * (pdaFractalDimensions[i] / 3.0F));
      li2dColourCodes[y][x] = newcolour;

    }

    return fraclac.writers.WriteUtilities.makeString(li2dColourCodes);

  }

  /**
   * Shows or saves data currently stored in the data StringBuffer for a
   * {@link Vars#sbDataFileBoxCountFMinCover} minimum cover box count. To save,
   * it calls {@link #saveDataFileOrAppend}, or else it displays a new
   * dataFileWindow, both using the same
   * {@link Headings#S_TABBED_DATA_FILE_HEADINGS_MIN_COVER headings}.
   *
   *
   * @param pVars Vars
   */
  public static void showOrSaveDataFileForFMinCoverBoxCount(Vars pVars)
  {
    String lsFileName = RESULTS_WINDOW_TITLE_MINIMUM_COVER_DATA;
    String lsHeadings = Headings.S_TABBED_DATA_FILE_HEADINGS_MIN_COVER;
    StringBuilder lsbData = new StringBuilder(
        pVars.sbDataFileBoxCountFMinCover);

    if (pVars.bSaveResults) {

      saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                           lsFileName + pVars.getsDotXlsOrTxt(),
                           lsHeadings,
                           lsbData);

    } else {

      dataFileWindow = new TextWindow(lsFileName,
                                      lsHeadings,
                                      lsbData.toString(),
                                      textWindowWidth,
                                      textWindowHeight);

      dataFileWindow.setVisible(true);
    }
  }

  /**
   * Decides to show or save the data from box counting, depending on options to
   * use minimum cover or smoothing.
   *
   *
   * @param pVars Vars
   *
   *
   * @see fraclac.analyzer.Vars#bDoFilterMinCover
   * @see fraclac.analyzer.Vars#bDoSmoothed
   * @see #showOrSaveDataFileForStandardOrSLacBoxCount
   * @see #showOrSaveDataFileForSmoothedBoxCount
   * @see #showOrSaveDataFileForFMinCoverBoxCount
   */
  public static void callFxnsToShowOrSaveDataFileMinCovOrSmOrBCEachGRID(
      Vars pVars)
  {
    showOrSaveDataFileForStandardOrSLacBoxCount(pVars);

    if (pVars.bDoFilterMinCover) {
      showOrSaveDataFileForFMinCoverBoxCount(pVars);
    }

    if (pVars.bDoSmoothed) {
      showOrSaveDataFileForSmoothedBoxCount(pVars);
    }

  }

  /**
   * Writes the passed headings and string to a text file called name, at the
   * given path. If the file exists, the string is appended without the
   * headings.
   *
   *
   * @param psbNewData StringBuffer of data to save to a file
   * @param psHeadings String of headings for the columns corresponding to the
   * data
   * @param psNewFileName String for the file name
   * @param psPath the path to the location for the saved file
   * @return
   */
  public static boolean saveDataFileOrAppend(String psPath,
                                             String psNewFileName,
                                             String psHeadings,
                                             StringBuilder psbNewData)
  {
    boolean lbError = false;
    try {

      File lfDirectory = new File(psPath);

      String[] lsaListOfFiles = lfDirectory.list();

      boolean lbNewFileExists = false;

      if (!(lsaListOfFiles == null)) {
        for (String lsNameOfNextFileFound : lsaListOfFiles) {
          if (lsNameOfNextFileFound.equals(psNewFileName)) {
            lbNewFileExists = true;
          }
        }
      }

      StringBuilder lsbNewData = new StringBuilder(psbNewData);

      if (!lbNewFileExists) {
        // If the file does not exist,
        // then the headings have to be included once, and the
        // file has to be created.

        lsbNewData = new StringBuilder(psHeadings).append(
            psHeadings == "" ? "" : newline).append(psbNewData);

        if (lsaListOfFiles == null) {

          if (!lfDirectory.mkdirs()) {
            IJ.showMessage("There may be an error "
                + "in the file directories created.");
          }
        }
      }

      writeToEndOfTextFile(psPath + psNewFileName,
                           lsbNewData.toString());

    } catch (SecurityException s) {

      IJ.showMessage("Sorry, but security says no to " + psPath
          + psNewFileName);
      lbError = true;

    } catch (IOException ioe) {

      IJ.showMessage("Sorry, but I cannot write " + psPath
          + psNewFileName);
      lbError = true;
    }
    return !lbError;
  }

  // ==========================================================================
  /**
   * Writes the passed headings and string to a text file called name, at the
   * given path. If the file exists, a message asks to overwrite it.
   *
   *
   * @param psbNewData StringBuffer of data to save to a file
   * @param psHeadings String of headings for the columns corresponding to the
   * data
   * @param psNewFileName String for the file name
   * @param psPath the path to the location for the saved file
   * <p>
   * @return true if no error was detected
   */
  public static boolean saveDataFileMakeDir(String psPath,
                                            String psNewFileName,
                                            String psHeadings,
                                            StringBuilder psbNewData)
  {
    // .........................................................................
    boolean lbFileWasWritten = true;
    // .........................................................................
    try {

      File lfDirectory = new File(psPath);

      String[] lsaListOfFiles = lfDirectory.list();

      boolean lbNewFileExists = false;

      if (!(lsaListOfFiles == null)) {
        for (String lsNameOfNextFileFound : lsaListOfFiles) {
          if (lsNameOfNextFileFound.equals(psNewFileName)) {
            lbNewFileExists = true;
          }
        }
      }

      StringBuilder lsbNewData = new StringBuilder(psbNewData);

      if (!lbNewFileExists) {
        // If the file does not exist,
        // then make headings and directory 

        lsbNewData = new StringBuilder(psHeadings).append(
            psHeadings == "" ? "" : newline).append(psbNewData);

        if (lsaListOfFiles == null) {
          if (!lfDirectory.mkdirs()) {
            IJ.showMessage("There may be an error "
                + "in the directories created.");
          }
        }
      }
      lbFileWasWritten = writeToStartOfTextFile(psPath + psNewFileName,
                                                lsbNewData.toString());

    } catch (SecurityException ex) {
      IJ.showMessage("Sorry, but security says no to " + psPath
          + psNewFileName);
      IJ.log(ex.toString() + ResultsFilesWriter.class.getSimpleName()
          + " " + (ex.getStackTrace()[0].getLineNumber()));
      lbFileWasWritten = false;
    } catch (IOException ex) {
      IJ.showMessage("Sorry, but I cannot write " + psPath
          + psNewFileName);
      IJ.log(ex.toString() + ResultsFilesWriter.class.getSimpleName()
          + " " + (ex.getStackTrace()[0].getLineNumber()));
      lbFileWasWritten = false;
    }
    // .........................................................................
    return lbFileWasWritten;
    // .........................................................................

  }

  public static String getTimeStampedDirectoryToSaveSettingsTo()
  {
    String lsDirectoryWithFileSeparator = OpenDialog.getDefaultDirectory();

    DirectoryChooser lDirectoryChooser = new DirectoryChooser(
        "Select Folder to Save Settings File To.");

    //lDirectoryChooser.setDefaultDirectory(lsDirectoryWithFileSeparator);
    if (lDirectoryChooser.getDirectory() == null) {
      return null;
    }

    return (lDirectoryChooser.getDirectory() + Utils.detailedTimeDate());

  }

//  public static String saveSettingsFile(String psSettingsSuffix,
//                                        String psFirstEntryAsMarker,
//                                        StringBuilder psText)
//  {
//    String lsDirectory = getTimeStampedDirectoryToSaveSettingsTo();
//    if ((lsDirectory == null)) {
//      IJ.log(ResultsFilesWriter.class.getSimpleName() + ": "
//          + new Exception().getStackTrace()[0].getLineNumber());
//      return null;
//    }
//    saveDataFileMakeDir(
//        lsDirectory,
//        "Settings" + System.currentTimeMillis() + psSettingsSuffix,
//        psFirstEntryAsMarker,
//        psText);
//    return lsDirectory;
//  }
  /**
   * Calls a method that shows a
   * {@link #showOrSaveDataFile text window or saves} a file displaying data
   * from two stored StringBuilders (the
   * {@link fraclac.analyzer.Vars#sbLongSummaryFileLine summary} and the
   * {@link fraclac.analyzer.Vars#sbLongSummaryFileLineRotations rotations}
   * strings stored in the passed {@link fraclac.analyzer.Vars Vars}), both
   * using headings from
   * {@link fraclac.analyzer.Vars#sbLongSummaryFileHeadings}. Passes the Vars
   * instance into the show or save method. Returns without doing anything if
   * this is a {@link fraclac.analyzer.Vars#isMF() multifractal scan} and the
   * option to
   * {@link fraclac.analyzer.Vars#bShowDataForEachGrid show data files} is not
   * selected.
   *
   *
   * @param pVars Vars
   */
  public static void showOrSaveLongSummaryFile(Vars pVars)
  {
    // =====================================================================
    if (pVars.isMF() && !pVars.bShowDataForEachGrid) {
      return;
    }
    //======================================================================
    StringBuilder lsHeadings = (pVars.sbLongSummaryFileHeadings);
    // ---------------------------------------------------------------------
    StringBuilder lsbData = new StringBuilder(pVars.sbLongSummaryFileLine);
    if (!lsbData.toString().isEmpty()) {
      String lsFileName = pVars.isSLAC()
          ? SLAC_SUMMARY_FILE_TITLE
          : BC_SUMMARY_FILE_TITLE;
      showOrSaveDataFile(lsbData,
                         lsHeadings,
                         lsFileName,
                         pVars);
    }
    // ---------------------------------------------------------------------
    StringBuilder lsbDataRotate = new StringBuilder(
        pVars.sbLongSummaryFileLineRotations);
    if (!(lsbDataRotate.toString().isEmpty())) {
      String lsFileNameRotate = pVars.isSLAC()
          ? SLAC_SUMMARY_FILE_TITLE
          : "ROT" + BC_SUMMARY_FILE_TITLE;
      showOrSaveDataFile(lsbDataRotate,
                         lsHeadings,
                         lsFileNameRotate,
                         pVars);
    }
  }

  /**
   * Calls the appropriate function to show or save summary results from a box
   * counting scan in the FracLac plugin. Within the plugin, it is called from
   * {@link fraclac.gui.GUI#initiateWritingOfSummaryAndDataFilesForThisSlice}.
   *
   * <h5>Basic Structure</h5>
   *
   * It uses the value for
   * {@link fraclac.analyzer.Vars#iNumGrids grid orientations} in the passed
   * Vars object as a flag; if the value is &gt; 0 this signifies that scanning
   * was done, whereas if it is 0 that signifies that only the hull and bounding
   * circle were assessed.
   *
   * <ol>
   * <li>If grids was &gt; 0:
   * <ul>
   * <li>it calls {@link #showOrSaveLongSummaryFile}
   * <li>it calls {@link #showOrSaveShortSummaryFile}
   * </ul>
   * <li>
   * If pVars is flagged to write data for the bounding circle or convex hull,
   * it calls {@link #writeCirc}.
   * </ol>
   *
   * @param pVars Vars with flags and strings; fill the appropriate strings
   * before calling this method. When called within FracLac, the Vars object
   * will have been filled prior to the call.
   */
  public static void showOrSaveBoxCountSummaryAndCircleDataThisSlice(
      Vars pVars)
  {
    if (pVars.iNumGrids > 0) {
      showOrSaveLongSummaryFile(pVars);
      showOrSaveShortSummaryFile(pVars);
    }

    if (pVars.bDoCircleAndHullCalculations) {
      writeCirc(pVars);
    }
  }

  /**
   * Writes the passed string to the end of the passed file using UTF-8 encoding
   * Assumes the file already exists.
   *
   * @param pFileName string for the new file's name
   * @param psToWrite string for what is being written to the file
   *
   *
   * @throws IOException
   */
  public static void writeToEndOfTextFile(String pFileName,
                                          String psToWrite)
      throws IOException
  {

    final Writer lwriterOut
        = new OutputStreamWriter(new FileOutputStream(pFileName,
                                                      true),
                                 "UTF-8");
    try {

      lwriterOut.write(psToWrite);

    } catch (IOException i) {

      IJ.log("Error writing file. "
          + ResultsFilesWriter.class.getSimpleName()
          + (new Exception().getStackTrace()[0].getLineNumber()));
      // if (lwriterOut != null) lwriterOut.close();

    } finally {
      if (lwriterOut != null) {
        lwriterOut.close();
      }
    }
  }

  /**
   * Writes the passed string to the end of the passed file using UTF-8 encoding
   * Assumes the file already exists.
   *
   * @param pFileName string for the new file's name
   * @param psToWrite string for what is being written to the file
   * <p>
   * @return boolean true if no error is caught
   *
   *
   * @throws IOException
   */
  public static boolean writeToStartOfTextFile(
      String pFileName,
      String psToWrite) throws IOException
  {
    boolean lbWorked = true;

    final Writer lwriterOut
        = new OutputStreamWriter(new FileOutputStream(pFileName),
                                 "UTF-8");
    try {
      lwriterOut.write(psToWrite);

    } catch (IOException i) {
      IJ.log("Error writing file. "
          + ResultsFilesWriter.class
          .getSimpleName()
          + (new Exception().getStackTrace()[0].getLineNumber()));
      // if (lwriterOut != null) lwriterOut.close();
      lbWorked = false;
    } finally {
      if (lwriterOut == null) {
      } else {
        lwriterOut.close();
      }
    }
    // .........................................................................
    return lbWorked;
    // .........................................................................

  }

  /**
   * Write the masses recorded for each location for each file. Save a new file
   * for each. {@link IJ#log logs} SecurityException and IOException.
   *
   *
   * @param psPath
   * @param psName
   * @param psbufferData
   */
  public static void saveMassesFile(String psPath,
                                    String psName,
                                    StringBuffer psbufferData)
  {
    try {

      File lFile = new File(psPath);
      String[] lsaArray = lFile.list();
      boolean lbFileExists = false;

      if (!(lsaArray == null)) {
        for (String lStringInArray : lsaArray) {
          if (lStringInArray.equals(psName)) {
            lbFileExists = true;
          }
        }
      }
      if (!lbFileExists) {
        lFile.mkdirs();
      }

      writeToEndOfTextFile(psPath + psName,
                           psbufferData.toString());

    } catch (SecurityException s) {

      IJ.log(s.toString() + "Error writing" + psPath + psName
          + ResultsFilesWriter.class.getSimpleName()
          + (new Exception().getStackTrace()[0].getLineNumber()));

    } catch (IOException ioe) {

      IJ.log(ioe.toString() + "Error2 writing" + psPath + psName
          + ResultsFilesWriter.class.getSimpleName()
          + (new Exception().getStackTrace()[0].getLineNumber()));
    }
  }

  /**
   *
   * @param pVars
   */
  public static void writeMF(Vars pVars)
  {
    showOrSaveDataFile(new StringBuilder(pVars.sbMultifractalSpectraFile),
                       Headings.headingsForMFSpectra(),
                       MULTIFRACTAL_RESULTS_TITLE,
                       pVars);
  }

  /**
   * Writes a {@link #saveDataFileOrAppend file} or shows on screen a TextWindow
   * from the passed strings.
   *
   * @param psbDataTabSeparated
   * @param psFileName
   * @param psbHeadingsTabSeparated
   * @param pVars
   */
  public static void showOrSaveDataFile(
      StringBuilder psbDataTabSeparated,
      StringBuilder psbHeadingsTabSeparated,
      String psFileName,
      Vars pVars)
  {
    // =====================================================================
    // Send it to a function that decides to make a new file or append data
    // and also deals with showing or saving options if we are saving.
    //======================================================================
    if (pVars.bSaveResults) {

      saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                           psFileName + pVars.getsDotXlsOrTxt(),
                           psbHeadingsTabSeparated.toString(),
                           psbDataTabSeparated);

      return;
    }
    // ======================================================================
    // If not saving, append the data to the window if it exists or
    // ======================================================================
    TextWindow lTextWindow = (TextWindow) WindowManager.getWindow(psFileName);
    if (lTextWindow != null) {
      lTextWindow.append(psbDataTabSeparated.toString());
      lTextWindow.setVisible(true);
      return;
    }
    //=======================================================================
    // if it does not exist, create it and write to it.
    //=======================================================================
    lTextWindow = new TextWindow(psFileName,
                                 psbHeadingsTabSeparated.toString(),
                                 psbDataTabSeparated.toString(),
                                 textWindowWidth,
                                 textWindowHeight);
    lTextWindow.setVisible(true);
  }

  /**
   *
   * @param psStrings
   * @param pdaQs
   * @param psQRange
   * @param pVars
   */
  public static void writeMFDataAsRowsOfQs(StringBuilder[] psStrings,
                                           double[] pdaQs,
                                           String psQRange,
                                           Vars pVars)
  {
    //================================================================
    // Make a title for each file. They will be initialized 
    // if they do not exist, or shown/saved and re-initialized if
    // the Q-range array changes in any way.
    // ---------------------------------------------------------------
    String[] lsFileNames = new String[MF_NUM_DATATYPES];
    lsFileNames[MF_DQ_INDEX] = psQRange + MF_DQ_RESULTS_TITLE;
    lsFileNames[MF_D_DQ_INDEX] = psQRange + MF_D_DQ_RESULTS_TITLE;
    lsFileNames[MF_TAU_INDEX] = psQRange + MF_TAU_RESULTS_TITLE;
    lsFileNames[MF_ALPHA_Q_INDEX] = psQRange + MF_ALPHA_RESULTS_TITLE;
    lsFileNames[MF_FAT_ALPHA_Q_INDEX] = psQRange + MF_FATALPHA_RESULTS_TITLE;

    String[] lsaDataTypes = {MF_DQ_RESULTS_TITLE,
      MF_D_DQ_RESULTS_TITLE,
      MF_TAU_RESULTS_TITLE,
      MF_ALPHA_RESULTS_TITLE,
      MF_FATALPHA_RESULTS_TITLE,};
    // ================================================================
    // We need a new string of headings if this is the first 
    // or a reinitialized run.  
    // ----------------------------------------------------------------
    String lsHeadingsOfQValuesTabSeparatedWithNewlineAtEnd = "";
    for (int liQIndex = 0; liQIndex < pdaQs.length; liQIndex++) {
      lsHeadingsOfQValuesTabSeparatedWithNewlineAtEnd += TAB + pdaQs[liQIndex];
    }
    lsHeadingsOfQValuesTabSeparatedWithNewlineAtEnd += newline;
    //=====================================================================
    // Send it to a function that decides to make a new file or append data
    // and also deals with showing or saving options.
    //======================================================================
    for (int liDataType = 0;
        liDataType < lsFileNames.length; liDataType++) {
      showOrSaveDataFile(psStrings[liDataType],
                         new StringBuilder(lsaDataTypes[liDataType]
                             + lsHeadingsOfQValuesTabSeparatedWithNewlineAtEnd),
                         lsFileNames[liDataType],
                         pVars);
    }
  }

  /**
   * @param pVars Vars
   */
  public static void writeDlcFrequencies(Vars pVars)
  {
    String lsFileName = "Frequencies for "
        + pVars.getsTitleAndSliceLabelAndSliceNumber();
    StringBuilder lsbData = new StringBuilder(pVars.sbDlcFrequency);
    StringBuilder lsHeadings = new StringBuilder("DF" + TAB + "Frequency");

    showOrSaveDataFile(lsbData,
                       lsHeadings,
                       lsFileName,
                       pVars);

  }

  /**
   * Shows or saves a text file of raw data results in the passed {@link Scan
   * Scan}.
   * <p>
   * Makes a string of data for writing all of the box masses that were measured
   * then displays it in a new text window for each
   * {@link fraclac.analyzer.Vars#iNumGrids}.
   *
   *
   * @param fl Scan preloaded with box counting data and flags
   */
  public static void recordRawData(Scan fl)
  {
    String lsType = fl.vars.isGray() ? "Grayscale-" : "Binary ";

    lsType += ((fl.vars.isMF()) ? "Multifractal "
        : fl.vars.isSLAC() ? "Overlapping " : "Nonoverlapping ");

    StringBuffer lsbSIZEs;

    for (int liGRID = 0; liGRID < fl.vars.iNumGrids; liGRID++) {

      lsbSIZEs = new StringBuffer("");

      for (int liSIZEIndex = 0;
          liSIZEIndex < fl.gridSet.i2dSizes[liGRID].length; liSIZEIndex++) {

        lsbSIZEs.append(fl.gridSet.i2dSizes[liGRID][liSIZEIndex])
            .append(TAB);

      }

      StringBuffer masses = new StringBuffer("");

      double longestlist = ArrayMethods
          .sizeOfLongestArray(fl.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs[liGRID]);

      StringBuffer lsbRowOfMasses;

      showProgress("Writing box masses for location " + liGRID);

      for (int liRow = 0; liRow < longestlist; liRow++) {

        lsbRowOfMasses = new StringBuffer("");
        for (int b = 0; b < fl.gridSet.i2dSizes[liGRID].length; b++) {

          if (fl.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs[liGRID][b].length
              > liRow) {
            lsbRowOfMasses
                .append(
                    fl.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs[liGRID][b][liRow])
                .append(TAB);
          } else {
            lsbRowOfMasses.append(" ").append(TAB);
          }
        }
        masses.append(newline).append(lsbRowOfMasses);
      }

      String lsTitle = "Masses at " + liGRID + " (" + lsType + ")"
          + fl.sbaTitles[liGRID];

      if (!fl.vars.bSaveResults) {
        TextWindow tw = new TextWindow(lsTitle,
                                       lsbSIZEs.toString(),
                                       masses.toString(),
                                       700,
                                       500);
        tw.setVisible(true);
      } else {
        if (fl.vars.bSaveResults) {

          int lig = fl.sbaTitles[liGRID].indexOf(" (");

          String sg = "Masses"
              + fl.sbaTitles[liGRID].substring(0,
                                               lig)
              + fl.vars.getsDotXlsOrTxt();

          StringBuffer sb = lsbSIZEs.append(masses);

          saveMassesFile(fl.vars.getDirectoryForSavingResults(),
                         sg,
                         sb);
        }
      }
    }
  }

  /**
   *
   * @param pVars Vars
   */
  public static void writeDlcBatchFrequenciesAsRows(Vars pVars)
  {

    StringBuilder lsbHeadings = new StringBuilder("File" + TAB);
    for (int i = 0; i < pVars.getDaBinsForDlc().length; i++) {
      lsbHeadings.append(pVars.getDaBinsForDlc()[i]).append(TAB);
    }
    makeStringForAllFilesAnalyzed:
    {
      pVars.sbDlcFrequency = new StringBuilder(
          pVars.getSaBatchFiles()[pVars.getiFileNumber()] + TAB);

      for (int j = 0; j < pVars.getDaBinsForDlc().length; j++) {

        pVars.sbDlcFrequency.append(
            pVars.getD2dBatchData()[pVars.getiFileNumber()][j])
            .append(TAB);
      }

      pVars.sbDlcFrequency.append(newline);
    }

    String lsFileName = "LCFDBatchFrequencies";
    StringBuilder lsbData = new StringBuilder(pVars.sbDlcFrequency);

    showOrSaveDataFile(lsbData,
                       lsbHeadings,
                       lsFileName,
                       pVars);
  }

  /**
   *
   * @param pVars the value of pVars
   */
  public static void writeCirc(Vars pVars)
  {
    StringBuilder lsHeadings = new StringBuilder(WriteUtilities
        .toTabbedString(Headings.SA_HULL_AND_CIRCLE_HEADINGS));

    StringBuilder lsbData = new StringBuilder(
        pVars.sbTabbedStrOfConvexHullAndBoundingCircleData);

    String lsFileName = HULL_AND_CIRC_RESULTS_TITLE;

    showOrSaveDataFile(lsbData,
                       lsHeadings,
                       lsFileName,
                       pVars);
  }

  private static StringBuilder gridDataFileHeadings(Vars pVars)
  {
    return pVars.isSLAC() ? SLACWriter.sbTabbedSLacDataFileHeadings
        : Utils.elongateStringWithEmptyTabs(
            pVars.getsDataFileHeadings(),
            pVars.iNumGrids,
            Headings.SA_DATA_COLUMN_HEADINGS.length);
  }

  public static void writeGridStringToBatchFile(String psGridString,
                                         Vars pVars)
  {
    saveDataFileOrAppend(pVars.getDirectoryForSavingResults(),
                         "BatchGridData.txt",
                         gridDataFileHeadings(pVars).toString(),
                         new StringBuilder(psGridString));
  }

 
}
