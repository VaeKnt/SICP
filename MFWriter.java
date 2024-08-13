package fraclac.writers;

import fraclac.analyzer.*;
import static fraclac.analyzer.Plots.getPlotStack;
import fraclac.utilities.ArrayMethods;
import fraclac.utilities.DataFilter;
import fraclac.utilities.Statistics;
import static fraclac.utilities.Statistics.getCoefficientOfVariation;
import fraclac.utilities.Utils;
import static fraclac.utilities.Utils.fnum;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Plot;

/**
 * Methods for processing results of, and creating StringBuilders for,
 * multifractal analysis with FracLac.
 *
 * To process data from a multifractal analysis, call the
 * {@link #multifractalDataProcessor data processor}.
 *
 * See individual method documentation or <a target = "parent" href =
 * "../../overview-summary.html#multifractal">
 * the overview of multifractal calculations</a> for details of how results are
 * calculated.
 *
 *
 * @author Audrey Karperien
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 244 $
 * @version Revision $Id: MFWriter.java 244 2015-04-19 01:20:59Z audrey $
 */
public class MFWriter
    extends DataStringFormatter
{

  private static boolean bFirstCVandDivCheck = true;

  /**
   * Index for marking the optimal dataset from a multifractal scan. The value
   * is set in calls to
   * {@link #assessMFSpectraReturnDescriptionHeadings0Data1 optimize} the data
   * and used in outputting {@link #showOptimizedMFSet graphic} results.
   */
  public static int iBestLocation = 0;

  public static MultifractalDescription bestMF;
  static MultifractalDescription thisMF;
  static MultifractalDescription worstMF;
  //private FLFrame progressFrame;

  /**
   * Main controller for processing multifractal data and returning a
   * StringBuilder of data. In the FracLac plugin, the StringBuilder that the
   * method returns is used to fill the {@link
   * fraclac.analyzer.Vars#sbMultifractalSpectraFile multifractal spectra string}.
   * The string has several sections with blank lines between and differing data
   * types in columns. The first set of data is several columns of multifractal
   * spectra data, having no column headers. The headings correspond to an enum
   * map. To write the returned StringBuilder to a file or text window the
   * headings string used must be padded to match the longest line in the string
   * or the results will not display properly.
   *
   * The method is called from within a {@link
   * fraclac.analyzer.Scan#selectMethodsToStoreResultsAllGRIDsThisSlice
   * scanner} and is invoked as a separate data processing method apart from the
   * main FracLac {@link fraclac.analyzer.DataProcessor data processor}.
   * <p>
   * The StringBuilder it returns represents only part of the work that it
   * initiates, in that it calls a
   * {@link #doGridAndOptReturnQMatrixQDescriptQDataTypes calculator function}
   * and dqVsQPlots various graphics.
   *
   * The details of how the data are processed depend on the passed parameters
   * and a local variable, {@link #iBestLocation}, that changes with each call
   * to the calculator function.
   *
   * <h5>Basic Structure</h5>
   * Does one of 3 things, based on the values in
   * {@link fraclac.analyzer.Vars#sDataFilterOption} and
   * {@link fraclac.analyzer.Vars#sOptimizingOption} in the passed
   * {@link Vars Vars} object:
   * <ol type = "I">
   * <li>
   * If the data processing option is to use a minimum cover or a smoothed
   * minimum cover, {calls {@link #processFMinCoverAndSmoothed }.
   * <li>
   * If the data processing option is to not use a minimum cover and to not
   * optimize, calls {@link #processNotOptimizedNoFMinCover}.
   * <li>
   * If the data processing option is to not use a minimum cover and to
   * optimize, then calls {@link #processOptimizedNoFMinCover}.
   * </ol>
   *
   * It always sets {@link #bFirstCVandDivCheck} to true.fixme
   *
   *
   * @param piNumSlices
   * @param pbGraphOnOnePlot
   * @param psbaTitles StringBuilder array of image titles
   * @param piThisSliceStartsAt1Not0 int for the current slice
   * @param pDP DataProcessor with multifractal data in place
   * @param pVars Vars containing flags, fields, etc.
   * @param pBoxes GridSet holding sizes of boxes to use
   * @param pd3dPix double [][][] of pixel counts at each grid and box size
   * @param piRoiManagerOrSubScanIndex
   * @param piNumRois
   *
   *
   * @return String of data for summary file
   */
  public StringBuilder[][] multifractalDataProcessor(
      //FLFrame pProgressFrame,
      int piNumSlices,
      final boolean pbGraphOnOnePlot,
      StringBuilder[] psbaTitles,
      final int piThisSliceStartsAt1Not0,
      DataProcessor pDP,
      Vars pVars,
      GridSet pBoxes,
      double[][][] pd3dPix,
      final int piRoiManagerOrSubScanIndex,
      final int piNumRois)
  {
    //progressFrame = pProgressFrame;
    bFirstCVandDivCheck = true;
    //===================================================================
    // 
    //     Make an array to hold a long string that actually 
    //     writes with newlines as a matrix at the first 
    //     index and then strings of Q values 
    //     and data set descriptions including the aperture and slope
    //     and headings.
    //
    //===================================================================
    StringBuilder[][] lsbaQMatrixAt00DescriptionAt01QRowsAt1
        = new StringBuilder[2][MF_NUM_DATATYPES];

    for (int i = 0; i < lsbaQMatrixAt00DescriptionAt01QRowsAt1.length; i++) {
      lsbaQMatrixAt00DescriptionAt01QRowsAt1[i]
          = new StringBuilder[MF_NUM_DATATYPES];

      for (int liDataTypes = 0;
          liDataTypes < lsbaQMatrixAt00DescriptionAt01QRowsAt1[i].length;
          liDataTypes++) {
        lsbaQMatrixAt00DescriptionAt01QRowsAt1[i][liDataTypes]
            = new StringBuilder();
      }
    }
    int liGrids = pVars.iNumGrids;
    //====================================================================
    // Do filtering options. The passed StringBuilder is passed in 
    // and remade and returned.
    //====================================================================
    if (pVars.sDataFilterOption == CAPTION_FILTER_MINIMUM_COVER
        || pVars.sDataFilterOption == CAPTION_FILTER_SMOOTH_MIN_COVER) {

      lsbaQMatrixAt00DescriptionAt01QRowsAt1
          = processFMinCoverAndSmoothed(piNumSlices,
                                        pVars.sDataFilterOption,
                                        pbGraphOnOnePlot,
                                        lsbaQMatrixAt00DescriptionAt01QRowsAt1,
                                        piThisSliceStartsAt1Not0,
                                        psbaTitles,
                                        pVars,
                                        pDP,
                                        piRoiManagerOrSubScanIndex,
                                        piNumRois);
    }

    //you can't optimize if you are doing a FMinCover or a
    //smoothed FMinCover because there is only one array to choose from
    if ((pVars.sDataFilterOption != CAPTION_FILTER_MINIMUM_COVER)
        && (pVars.sOptimizingOption == CAPTION_DONT_OPTIMIZE)) {
      lsbaQMatrixAt00DescriptionAt01QRowsAt1 = processNotOptimizedNoFMinCover(
          piNumSlices,
          pbGraphOnOnePlot,
          lsbaQMatrixAt00DescriptionAt01QRowsAt1,
          piThisSliceStartsAt1Not0,
          psbaTitles,
          pVars,
          liGrids,
          pd3dPix,
          pBoxes.dimensionsAsIntArray(pVars.bSpecifyWidthAndHeightOfBoxes),//.i2dSizes,
          piRoiManagerOrSubScanIndex,
          piNumRois);
    }

    if ((pVars.sDataFilterOption != CAPTION_FILTER_MINIMUM_COVER)
        && (pVars.sOptimizingOption != CAPTION_DONT_OPTIMIZE)) {
      lsbaQMatrixAt00DescriptionAt01QRowsAt1
          = processOptimizedNoFMinCover(piNumSlices,
                                        pbGraphOnOnePlot,
                                        lsbaQMatrixAt00DescriptionAt01QRowsAt1,
                                        piThisSliceStartsAt1Not0,
                                        psbaTitles,
                                        pVars,
                                        liGrids,
                                        pd3dPix,
                                        pBoxes.dimensionsAsIntArray(
                                            pVars.bSpecifyWidthAndHeightOfBoxes),//i2dSizes,
                                        piRoiManagerOrSubScanIndex,
                                        piNumRois);
    }

    return lsbaQMatrixAt00DescriptionAt01QRowsAt1;
  }

  /**
   * Appends data for the minimum cover, and if smoothing is selected, further
   * appends data for that filter. The appended strings are made through calls
   * to {@link #doGridAndOptReturnQMatrixQDescriptQDataTypes} that depend on 2
   * arrays in the DataProcessor's data object being previously filled:
   * {@link Data#d2dPixArraysAtSIZEsMFFMinCover} and
   * {@link Data#iaSIZEsMultifractalFMinCover}.
   *
   * A {@link fraclac.utilities.DataFilter#smoothFilter smoothing filter} is
   * applied to the former to make an array to pass to the calculator method for
   * smoothed min cover data.
   *
   * @param piNumSlices
   * @param psDataOption
   * @param pbGraphOnOnePlot
   * @param psbData
   * @param piThisSliceStartsAt1Not0
   * @param psbaTitles
   * @param pVars
   * @param pDP
   * @param piRoiManagerOrSubScanIndex
   * @param piNumRois
   * @return
   */
  public StringBuilder[][] processFMinCoverAndSmoothed(
      int piNumSlices,
      String psDataOption,
      final boolean pbGraphOnOnePlot,
      StringBuilder[][] psbData,
      final int piThisSliceStartsAt1Not0,
      StringBuilder[] psbaTitles,
      Vars pVars,
      DataProcessor pDP,
      final int piRoiManagerOrSubScanIndex,
      final int piNumRois)
  {

    protectSB:
    {
      StringBuilder[][] lsbaQMatrixAndQRows
          = doGridAndOptReturnQMatrixQDescriptQDataTypes(
              piNumSlices,
              pbGraphOnOnePlot,
              psbaTitles,
              pVars,
              pDP.data.d2dPixArraysAtSIZEsMFFMinCover,
              pDP.data.iaSIZEsMultifractalFMinCover,
              -1,
              piThisSliceStartsAt1Not0,
              piRoiManagerOrSubScanIndex,
              piNumRois);
      //====================================================================
      // Record the Q matrix of q along the vertical in the first array
      // and the Q rows for each data type in the second array.
      //====================================================================
      psbData[0][0].append(lsbaQMatrixAndQRows[0][0]);
      psbData[0][1].append(lsbaQMatrixAndQRows[0][1]);
      psbData[0][2].append(lsbaQMatrixAndQRows[0][2]);

      for (int li = 0; li < lsbaQMatrixAndQRows[1].length; li++) {
        psbData[1][li].append(lsbaQMatrixAndQRows[1][li]);
      }
    }
    //==============================================================
    if (pVars.sDataFilterOption == CAPTION_FILTER_SMOOTH_MIN_COVER) {
      //==========================================================
      // Filter the data by smoothing.
      //==========================================================
      fraclac.analyzer.BoxCount lbcSmooth = DataFilter
          .smoothFilter(pDP.data.iaSIZEsMultifractalFMinCover,
                        pDP.data.d2dPixArraysAtSIZEsMFFMinCover,
                        true,//pbF_SS_,
                        true,//pbSort
                        pVars.bLegacy);
      //===========================================================
      // Get the multifractal spectra from the filtered array 
      // and tack the data on to the strings from the first call.
      //===========================================================
      StringBuilder[][] lsbaSmoothedQMatrixAndQRows
          = doGridAndOptReturnQMatrixQDescriptQDataTypes(
              piNumSlices,
              //CAPTION_FILTER_SMOOTH_MIN_COVER,
              pbGraphOnOnePlot,
              psbaTitles,
              pVars,
              lbcSmooth.d2dPixArraysAtSIZEs,
              lbcSmooth.daSIZEs,
              -1,
              piThisSliceStartsAt1Not0,
              piRoiManagerOrSubScanIndex,
              piNumRois);

      psbData[0][0].append(lsbaSmoothedQMatrixAndQRows[0][0]);
      psbData[0][1].append(lsbaSmoothedQMatrixAndQRows[0][1]);
      psbData[0][2].append(lsbaSmoothedQMatrixAndQRows[0][2]);

      for (int li = 0; li < lsbaSmoothedQMatrixAndQRows[1].length; li++) {
        psbData[1][li].append(lsbaSmoothedQMatrixAndQRows[1][li]);
      }
    }
    //==============================================================
    return psbData;
  }//end1 min or smooth min cover

  /**
   * Strings are appended for each grid location, by calling the calculator
   * method once for each
   * {@link fraclac.analyzer.Vars#iNumGrids grid orientation} in the passed
   * Vars. All of the formatting is done by the called functions,
   * {@link #doGridAndOptReturnQMatrixQDescriptQDataTypes}. The data may be
   * {@link DataFilter#smoothFilter filtered} first if the flag is set in the
   * passed Vars instance.
   *
   * @param piNumSlices
   * @param pVars
   * @param pbGraphOnOnePlot
   * @param piGRIDs
   * @param psbData
   * @param pd3dPix
   * @param piThisSliceStartsAt1Not0
   * @param pi2dSIZEs
   * @param psbaTitles
   * @param piRoiManagerOrSubScanIndex
   * @param piNumRois
   * @return
   */
  public StringBuilder[][] processNotOptimizedNoFMinCover(
      int piNumSlices,
      boolean pbGraphOnOnePlot,
      StringBuilder[][] psbData,
      int piThisSliceStartsAt1Not0,
      StringBuilder[] psbaTitles,
      Vars pVars,
      int piGRIDs,
      double[][][] pd3dPix,
      int[][] pi2dSIZEs,
      int piRoiManagerOrSubScanIndex,
      int piNumRois)
  {

    //===============================================================
    // Calculate for every grid and append a set of strings showing 
    // multifractal spectra data.
    //===============================================================
    for (int liGrid = 0; liGrid < piGRIDs; liGrid++) {
      StringBuilder[][] lsba;
      //==============================================================
      if (pVars.sDataFilterOption == CAPTION_FILTER_SMOOTH) {
        //============================================================
        // Smooth filter the data first if requested.
        //============================================================
        BoxCount lbchSm
            = DataFilter.smoothFilter(
                intToDoubleArray(pi2dSIZEs[liGrid]),
                pd3dPix[liGrid],
                false,
                true,
                pVars.bLegacy);
        //============================================================
        // Get the multifractal spectra for the smoothed data.
        //============================================================
        lsba
            = doGridAndOptReturnQMatrixQDescriptQDataTypes(
                piNumSlices,
                pbGraphOnOnePlot,
                psbaTitles,
                pVars,
                lbchSm.d2dPixArraysAtSIZEs,
                lbchSm.daSIZEs,
                liGrid,
                piThisSliceStartsAt1Not0,
                piRoiManagerOrSubScanIndex,
                piNumRois);
      }//
      //==============================================================
      // End smoothing filter, no min cover, no optimizing and 
      // instead do no filter, no min cover, no optimizing.
      //==============================================================
      else {
        lsba = (doGridAndOptReturnQMatrixQDescriptQDataTypes(
            piNumSlices,
            pbGraphOnOnePlot,
            psbaTitles,
            pVars,
            pd3dPix[liGrid],
            intToDoubleArray(pi2dSIZEs[liGrid]),
            liGrid,
            piThisSliceStartsAt1Not0,
            piRoiManagerOrSubScanIndex,
            piNumRois));
      }
      // End else do not smoothing filter, no min cover, no optimizing.
      //=============================================================
      // Append a set of strings for each grid, because we are not
      // optimizing. They are appended in sequence. If we were 
      // optimizing, then they would be condensed to one set
      // from all grids.
      //=============================================================
      psbData[0][0].append(lsba[0][0]);
      psbData[0][1].append(lsba[0][1]);
      psbData[0][2].append(lsba[0][2]);
      //=============================================================
      // The 0th index in the 0th array is for the matrix of q 
      // values along the vertical, which is a huge string 
      // with many newlines to separate the q values. 
      // The 1st index is for an array of 5 rows 
      // of data against q along the horizontal; each row is for 
      // a separate file so they are kept in their own array slot.
      //==============================================================
      for (int li = 0; li < lsba[1].length; li++) {
        psbData[1][li].append(lsba[1][li]);
      }
    }//end for each grid, not min and not opt
    //==============================================================
    return psbData;
  }//end2 not FMinCover and not optimizing

  /**
   * Gets
   * {@link #doGridAndOptReturnQMatrixQDescriptQDataTypes multifractal data} for
   * each grid location based on the passed 3d array of doubles and 2d array of
   * ints. Either all locations are recorded and one is marked as optimal or
   * only one string is appended, according to the data set that is deemed
   * optimal within the calculator method, as noted by the value in the local
   * variable {@link #iBestLocation}.
   * <p>
   * The returned StringBuilder [][] is the one passed in with the final
   * information appended to it. Specifically, it has appended to it
   * <ul>
   * <li>at [0][0] the matrix string of Q values and the corresponding data
   * types for each image or roi, which goes in the data file for
   * {@link #matrixOfQsAndMultifractalSpectraData}
   * </li>
   * <li>at [0][1] the data description string for
   * {@link fraclac.writers.ResultsFilesWriter#writeMFDataDescription}
   * </li>
   * <li>at [1] an array of strings, one appended to each element
   * [1][<em>i</em>] in that subarray, corresponding to each <em>i</em> as a
   * data type (such as D<sub>Q</sub>
   * and &#402;(&alpha;)) of the
   * {@link #linesFileNameTabbedMFSpectraDataAtEachQNoHeadings Q(Min to Max) files}
   * for writing with
   * {@link fraclac.writers.ResultsFilesWriter#writeMFDataAsRowsOfQs}
   * </li>
   * </ul>
   * <p>
   * The method also calls a method to display graphics if the user wants to,
   * using {@link #showOptimizedMFSet}.
   * <p>
   * Depending on the options flagged, the method will first filter the data
   * using {@link DataFilter.smoothFilter}.
   *
   * @param piNumSlices
   * @param pbGraphOnOnePlot
   * @param psbData [][] this array of StringBuilders is written to and returned
   * @param piThisSliceStartsAt1Not0
   * @param psbaTitles
   * @param pVars
   * @param piGRIDs int number of {@link fraclac.analyzer.Vars#iNumGrids grids}
   * @param pd3dPix double 3d array holding box counting data for the number of
   * {@link fraclac.analyzer.BoxCount#d2dPixArraysAtSIZEs pixels} per
   * {@link fraclac.analyzer.BoxCount#daSIZEs sampling element}, over several
   * {@link fraclac.analyzer.GridSet grid locations}
   * @param pi2dSIZEs int array of sampling element sizes used to gather the
   * data in the Pix arrays
   * @param piRoiManagerOrSubScanIndex
   * @param piNumRois
   *
   * @return StringBuilder
   */
  public StringBuilder[][] processOptimizedNoFMinCover(
      int piNumSlices,
      final boolean pbGraphOnOnePlot,
      StringBuilder[][] psbData,
      final int piThisSliceStartsAt1Not0,
      StringBuilder[] psbaTitles,
      Vars pVars,
      final int piGRIDs,
      double[][][] pd3dPix,
      int[][] pi2dSIZEs,
      final int piRoiManagerOrSubScanIndex,
      final int piNumRois)
  {

    StringBuilder[][][] lsbaMFData_AllGrids
        = new StringBuilder[piGRIDs][2][MF_NUM_DATATYPES];
    //==============================================================
    // Go through for each dataset from each grid and filter 
    // the data as requested, then record the multifractal spectra 
    // values in temporary arrays. 
    // As the functions to do these things are called, the optimal 
    // dataset will be  selected and its index recorded so it 
    // can be used at the end of the loop. That is, outside
    // methods will mark it and 
    // we will select later after all grids have been tried, but 
    // here we save all of the data for each grid since we don't 
    // know ahead of time which one we will want to keep.
    //===============================================================
    for (int liG = 0; liG < piGRIDs; liG++) {
      StringBuilder lsbaMatrixStringAt0ndRowsArrayAt1PerGrid[][];
      //*************************************************************
      // We will be adding 3 strings to the 0th index and
      // one for each datatype to the 1st index, so 
      // initialize the array to accommodate that.
      //**************************************************************
      lsbaMFData_AllGrids[liG][0] = new StringBuilder[3];
      lsbaMFData_AllGrids[liG][0][0] = new StringBuilder();
      lsbaMFData_AllGrids[liG][0][1] = new StringBuilder();
      lsbaMFData_AllGrids[liG][0][2] = new StringBuilder();
      lsbaMFData_AllGrids[liG][1] = new StringBuilder[MF_NUM_DATATYPES];
      // MF_NUM_DATATYPES is the length of the multifractal spectra array
      for (int liSB = 0; liSB < lsbaMFData_AllGrids[liG][1].length; liSB++) {
        lsbaMFData_AllGrids[liG][1][liSB] = new StringBuilder();
      }
      //==============================================================
      // Choose one of two options to get the data processed and 
      // to store it in a temporary array with a slot for each grid.
      //==============================================================
      if (pVars.sDataFilterOption == CAPTION_FILTER_SMOOTH) {
        // -----------------------------------------------------------
        // Option 1. Smooth filter the data
        // -----------------------------------------------------------
        BoxCount lbchSm
            = DataFilter.smoothFilter(ArrayMethods
                .intToDoubleArray(pi2dSIZEs[liG]),
                                      pd3dPix[liG],
                                      false,
                                      true,
                                      pVars.bLegacy);
        //-------------------------------------------------------------
        // then get the multifractal spectra arrays in a local array.
        //-------------------------------------------------------------
        lsbaMatrixStringAt0ndRowsArrayAt1PerGrid
            = doGridAndOptReturnQMatrixQDescriptQDataTypes(
                piNumSlices,
                pbGraphOnOnePlot,
                psbaTitles,
                pVars,
                lbchSm.d2dPixArraysAtSIZEs,
                lbchSm.daSIZEs,
                liG,
                piThisSliceStartsAt1Not0,
                piRoiManagerOrSubScanIndex,
                piNumRois);
      }//end optimize, smoothed, no min cover      
      else {
        //------------------------------------------------------------
        // Option 2. 
        // Otherwise, don't filter the data. Process it as is for 
        // later optimizing.
        //------------------------------------------------------------
        lsbaMatrixStringAt0ndRowsArrayAt1PerGrid
            = doGridAndOptReturnQMatrixQDescriptQDataTypes(
                piNumSlices,
                //pVars.sOptimizingOption + pVars.sDataFilterOption,
                pbGraphOnOnePlot,
                psbaTitles,
                pVars,
                pd3dPix[liG],
                intToDoubleArray(pi2dSIZEs[liG]),
                liG,
                piThisSliceStartsAt1Not0,
                piRoiManagerOrSubScanIndex,
                piNumRois);
      }//end opt, no min cover, no smooth
      //----------------------------------------------------------------
      // Put the data string that is a matrix with Q values along the 
      // vertical into the first slot (indices [0][0]) and put the 
      // data set description string into the second slot (indices[0][1]).
      // Both are in the first array at index 0.
      //----------------------------------------------------------------
      (lsbaMFData_AllGrids[liG][0][0])
          .append(lsbaMatrixStringAt0ndRowsArrayAt1PerGrid[0][0]);
      lsbaMFData_AllGrids[liG][0][1]
          .append(lsbaMatrixStringAt0ndRowsArrayAt1PerGrid[0][1]);
      lsbaMFData_AllGrids[liG][0][2]
          .append(lsbaMatrixStringAt0ndRowsArrayAt1PerGrid[0][2]);
      // The next information to save is an array of strings, one 
      // for each datatype, for the current grid on this image.        
      lsbaMFData_AllGrids[liG][1]
          = lsbaMatrixStringAt0ndRowsArrayAt1PerGrid[1];
    }//end for each grid
    //***********************************************************************
    //
    //   NOW WE HAVE STORED A DATA SET FOR EACH GRID, AND WE WILL CHOOSE 
    //   1 TO KEEP AND DISCARD ALL OF THE REST.
    //
    // *********************************************************************
    // Append the appropriate data, show graphs, and return the string.
    // ======================================================================
    // 1. EITHER APPEND THE DATA FOR THE BEST LOCATION,
    // There are 2 possibilities - the user is showing only the optimized
    // or showing all but marking the optimized. For the first we write 
    // only one data set, which has been selected by the processing 
    // calls above and marked by the value of iBestLocation.
    //========================================================================
    if (pVars.bDecideOnMultifractality) {
      if (worstMF.isProbablyNotMF()) {
        iBestLocation = worstMF.iThisGrid;
        bestMF.update(worstMF);
      }
    }
    if (pVars.sOptimizingOption == CAPTION_SHOW_ONLY_OPTIMAL) {
      //
      // i. Append the very long and multilined optimized string 
      //    for the matrix file
      //
      psbData[0][0].append(lsbaMFData_AllGrids[iBestLocation][0][0]);
      //
      // ii. Append the optimized heading string 
      //     for the data set description file
      //
      psbData[0][1].append(lsbaMFData_AllGrids[iBestLocation][0][1]);
      //
      // iii. Append the optimized data string 
      //      for the data set description file
      //
      psbData[0][2].append(lsbaMFData_AllGrids[iBestLocation][0][2]);
      //
      // iv. Append the optimized rows 
      //     for each of the Q data files. These are files that 
      //     list Q along the top and then one datatype's value 
      //     at each Q in the file's rows. The datatypes are f(alpha), 
      //     alpha, tau, Dq, and dDq.
      //
      for (int liMFDataType = 0;
          liMFDataType < lsbaMFData_AllGrids[iBestLocation][1].length;
          liMFDataType++) {
        psbData[1][liMFDataType]
            .append(lsbaMFData_AllGrids[iBestLocation][1][liMFDataType]);
      }
    }// End store the matrix, description, and data type rows if we 
    // are optimizing to one location.
    //======================================================================
    // 2. OR APPEND THE DATA FOR ALL LOCATIONS.
    // If we are showing all but just marking the optimal, then 
    // put in one value for each grid, but mark the optimal choice.
    //======================================================================
    else {
      for (int liGrid = 0; liGrid < piGRIDs; liGrid++) {
        //
        // i. append matrix string
        //
        psbData[0][0].append(lsbaMFData_AllGrids[liGrid][0][0]);
        if (liGrid < (piGRIDs - 1)) {
          psbData[0][0].append(newline).append(newline);
        }
        //
        // ii. append data description headings once
        if (liGrid == 0) {
          psbData[0][1]
              .append(lsbaMFData_AllGrids[liGrid][0][1]);
        }
        //
        // iii. append data description; assume newline
        //
        psbData[0][2]
            .append((iBestLocation == liGrid ? sOPTIMUM : ""))
            .append(lsbaMFData_AllGrids[liGrid][0][2]);
        //
        // iv. append data types strings for 5 files    
        //
        for (int liMFDataType2 = 0;
            liMFDataType2 < lsbaMFData_AllGrids[liGrid][1].length;
            liMFDataType2++) {
          psbData[1][liMFDataType2]
              .append((iBestLocation == liGrid ? sOPTIMUM : ""))
              .append(lsbaMFData_AllGrids[liGrid][1][liMFDataType2]);
        }
      }// end grid loop
    }//end put in data from all grids but mark optimal
    //**********************************************************************
    //======================================================================
    // Append some data describing the optimal grid.
    //======================================================================
    psbData[0][0]
        .append("Optimal Grid Position" + TAB)
        .append("Location ")
        .append(1 + iBestLocation)
        .append(" of ")
        .append(pVars.iNumGrids)
        .append(newline + TAB);
    //**********************************************************************
    //
    //  CALL A METHOD TO SHOW THE OPTIMIZED GRAPHS IF THE USER 
    //  WANTS TO SEE THEM OR SAVE THEM. This call is an anomaly
    //  in that we are making it now for the optimized set, but 
    //  when we are not optimizing, the calls are made outside
    //  of the data processing method.
    //
    //***********************************************************************
    showOptimizedMFSet(piNumSlices,
                       pbGraphOnOnePlot,
                       pVars,
                       psbaTitles,
                       piRoiManagerOrSubScanIndex,
                       piNumRois,
                       piThisSliceStartsAt1Not0);
    //=======================================================================
    return psbData;

  }//end method for optimizing, no min cover

  /**
   * @param pdaQs
   * @param pdaAlphas
   * @param pdaFAtAlphas
   * @return
   */
  public static StringBuilder[]
      getApertureSBATabsAt1HeadingsAt0(double[] pdaQs,
                                       double[] pdaFAtAlphas,
                                       double[] pdaAlphas)
  {
    MultifractalAperture lAperture
        = new MultifractalAperture(pdaQs,
                                   pdaFAtAlphas,
                                   pdaAlphas
        );

    StringBuilder lsApertureString = new StringBuilder();
    StringBuilder lsApertureHeadingsString = new StringBuilder();
    for (int i = 0; i < SA_APERTURE_HEADINGS.length; i++) {
      lsApertureHeadingsString
          .append(SA_APERTURE_HEADINGS[i])
          .append(TAB);
      lsApertureString
          .append(lAperture.getApertureElementAsString(i))
          .append(TAB);
    }
    return new StringBuilder[]{lsApertureHeadingsString,
      lsApertureString};
  }

  /**
   * Returns a string that is a matrix of tabbed strings (i.e., separated by
   * newlines), the entire thing also ending in a newline. The matrix has 6
   * columns with one row for each Q. Each row starts with the element from the
   * Qs array, then continues linking by tabs the corresponding element from
   * each subarray in the GenDimension array (this is shown in the example
   * below.
   *
   * <h5>Example</h5>
   * <pre>
   * Qs = {0,1}
   * GD = {{3,4}, {6,7}, {9,10}, {12,13}, {15,16}};
   * String result =
   * 0 TAB 3 TAB 6 TAB 9  TAB 12 TAB 15
   * 1 TAB 4 TAB 7 TAB 10 TAB 13 TAB 16
   * newline
   * </pre>
   * <h4>Use</h4>
   * As used in the FracLac plugin, this is a utility to print a matrix of
   * multifractal spectra after they have been calculated and stored in the
   * passed arrays. The method is called by
   * {@link #getMFSpectraForThisGridAndOptimizingOption}. The headings for the
   * matrix are in {@link fraclac.writers.Headings#headingsForMFSpectra}.
   *
   * @param pd2dDGen [][] double; Generalized Dimensions and multifractal Stats;
   * the function will look for 5 arrays with the same number of elements as the
   * Qs array so will throw a null pointer exception if there are not at least 5
   * arrays with the appropriate number of elements in each. Anything beyond
   * that is ignored. For multifractal analysis results files, the data arrays
   * should correspond to the indices for:
   * <ul><li>{@link #MF_DQ_INDEX}={@value #MF_DQ_INDEX}
   * <li>{@link #MF_D_DQ_INDEX}={@value #MF_D_DQ_INDEX}
   * <li>{@link #MF_TAU_INDEX}={@value #MF_TAU_INDEX}
   * <li>{@link #MF_ALPHA_Q_INDEX}={@value #MF_ALPHA_Q_INDEX}
   * <li>{@link #MF_FAT_ALPHA_Q_INDEX}={@value #MF_FAT_ALPHA_Q_INDEX}
   * </ul>
   *
   * @param pdaQs double []; the values should correspond to the
   * {@link fraclac.analyzer.Vars#daQs exponents} from the multifractal analysis
   * being shown
   *
   *
   * @return String matrix of the passed arrays
   */
  public static String matrixOfQsAndMultifractalSpectraData(
      double[][] pd2dDGen,
      double[] pdaQs)
  {
    String lsQString = "";

    for (int liq = 0; liq < pdaQs.length; liq++) {

      lsQString = lsQString + pdaQs[liq] + TAB
          + Utils.fnum(pd2dDGen[MF_DQ_INDEX][liq]) + TAB
          + Utils.fnum(pd2dDGen[MF_D_DQ_INDEX][liq]) + TAB
          + Utils.fnum(pd2dDGen[MF_TAU_INDEX][liq]) + TAB
          + Utils.fnum(pd2dDGen[MF_ALPHA_Q_INDEX][liq]) + TAB
          + Utils.fnum(pd2dDGen[MF_FAT_ALPHA_Q_INDEX][liq])
          + newline;
    }

    return lsQString;
  }

  /**
   *
   * @param piNumSlices
   * @param pbGraphOnOnePlot
   * @param piNumSIZEs
   * @param piGRID
   * @param piThisSliceStartsAt1Not0
   * @param psbaTitles
   * @param pdaSIZEs
   * @param pd2dDAtQForSIZE
   * @param pd2dMeanTauAtQForSIZE
   * @param pd2dAlphaAtQForSIZE
   * @param pd2dFAtAlphaAtQForSIZE
   * @param pVars
   * @param piRoiManagerOrSubscanIndex
   * @param piNumRois
   * @param psStatusString
   * @return
   */
  public StringBuilder[][] getMFSpectraForThisGridAndOptimizingOption(
      int piNumSlices,
      final boolean pbGraphOnOnePlot,
      int piNumSIZEs,
      int piGRID,//-1 if smoothed or mincover
      int piThisSliceStartsAt1Not0,
      StringBuilder[] psbaTitles,
      double[] pdaSIZEs,
      double[][] pd2dDAtQForSIZE,
      double[][] pd2dMeanTauAtQForSIZE,
      double[][] pd2dAlphaAtQForSIZE,
      double[][] pd2dFAtAlphaAtQForSIZE,
      Vars pVars,
      int piRoiManagerOrSubscanIndex,
      int piNumRois,
      String psStatusString)
  {

    final double[] ldaQs = pVars.getDaQs();
    progress(psStatusString + " Writing");
    //=====================================================================
    //
    //    Make a string of identifying information about the image.
    //    For -1 as the piGRID, use the stored value, otherwise 
    //    use the detailed information for grid number, etc.
    //
    //======================================================================
    String lsGridAndTitleAndSlice = (piGRID == -1)
        ? pVars.getsTitleAndSliceLabelAndSliceNumber()
        : ("G" + (piGRID + 1) + psbaTitles[piGRID].toString());
    //=====================================================================
    //
    //    Get the multifractal spectra arrays from the passed data arrays.
    //    
    //    First switch the sizes to epsilons.
    //
    //=====================================================================
    double[] ldaEpsilons = new double[piNumSIZEs];
    for (int i = 0; i < piNumSIZEs; i++) {
      ldaEpsilons[i] = pdaSIZEs[i] / pVars.getdGreaterOfHtAndWd();
    }
    //======================================================================
    //There is a switch in indices here; the output array 
    //has alpha at index 3 and f at alpha at index 4 now
    //whereas they were 2 and 3 respectively prior to this call.
    double[][] ld2dMultifractalSpectraArraysHasFAlphaAtIndex4
        = getMultifractalSpectraArrays(pd2dDAtQForSIZE,
                                       pd2dMeanTauAtQForSIZE,
                                       pd2dAlphaAtQForSIZE,
                                       pd2dFAtAlphaAtQForSIZE,
                                       ldaEpsilons,
                                       piNumSIZEs,
                                       ldaQs);
    //================================================================
    //
    //       Write strings of data from the multifractal spectra
    //       just calculated for this grid location.  First make
    //       strings for headings and descriptions.
    //
    //================================================================
    String lsDataFilterOp
        = pVars.sDataFilterOption == CAPTION_FILTER_MINIMUM_COVER
            ? AllGsHeadings.MINF
            : pVars.sDataFilterOption == CAPTION_FILTER_SMOOTH
                ? AllGsHeadings.SF
                : pVars.sDataFilterOption == CAPTION_FILTER_SMOOTH_MIN_COVER
                    ? AllGsHeadings.SF + FOR + AllGsHeadings.MINF
                    : "No Filter";

    String lsOptOp
        = pVars.sOptimizingOption == CAPTION_SHOW_ONLY_OPTIMAL
            ? sOPTIMUM : "";

    //=================================================================
    //  NB: This string is combined with another data string for 
    //  the final data description.
    //=================================================================
    StringBuilder[] lsbaApertureHeadingsAndDataString
        = getApertureSBATabsAt1HeadingsAt0(
            ldaQs,
            ld2dMultifractalSpectraArraysHasFAlphaAtIndex4[4],
            ld2dMultifractalSpectraArraysHasFAlphaAtIndex4[3]);
    //===================================================================
    //
    //    A Matrix of Q values horizontally and each data type 
    //    in successive rows.
    //
    //====================================================================
    String lsImageInfoAndQMatrixData
        = lsGridAndTitleAndSlice + newline
        + lsOptOp + "Position " + (piGRID + 1) + "/"
        + pVars.iNumGrids + " " + lsDataFilterOp
        + newline
        //+ lsbaApertureHeadingsAndDataString[0]
        //+ newline
        //+ lsbaApertureHeadingsAndDataString[1]
        + matrixOfQsAndMultifractalSpectraData(
            ld2dMultifractalSpectraArraysHasFAlphaAtIndex4,
            ldaQs);

    String lsSlopes
        = "SLOPES OF SPECTRA VS Q"
        + newline
        + tabbedMFStats(ld2dMultifractalSpectraArraysHasFAlphaAtIndex4,
                        ldaQs)
        + newline;

    lsImageInfoAndQMatrixData += lsSlopes;
    //=======================================================================
    String lsTitleString = lsGridAndTitleAndSlice + " "
        + lsOptOp + " Position " + (piGRID + 1) + "/"
        + pVars.iNumGrids + " " + lsDataFilterOp;
    //=======================================================================
    //
    //      An array of one row each for the datatypes.
    //
    //=======================================================================
    StringBuilder[] lsbaDataTypesPerQ
        = linesFileNameTabbedMFSpectraDataAtEachQNoHeadings(
            lsTitleString,
            ld2dMultifractalSpectraArraysHasFAlphaAtIndex4,
            ldaQs,
            pVars);
    //==============================================================
    //
    //        MAKE THE DATASET SUMMARY STRING
    //
    //  Call a method that calculates some features of the 
    //  scaling in the data set and returns a string. Also, 
    //  if the flag is set, the method called will 
    //  Optimize. That is, if the user wants the program 
    //  to determine how the
    //  grid locations compare and select an optimal one,
    //  then it compares and stores the assessment data.
    //
    //  At the end of this method, this string is combined 
    //  with the aperture string made above,  
    //  to make the final data description.
    //
    //==============================================================
    StringBuilder[] lsbaHeadings0DataDescription1
        = assessMFSpectraReturnDescriptionHeadings0Data1(
            piGRID,
            ldaQs,
            ld2dMultifractalSpectraArraysHasFAlphaAtIndex4,
            (pVars.sOptimizingOption != CAPTION_DONT_OPTIMIZE),
            pVars.bDecideOnMultifractality);
    //================================================================
    //
    //  Graph the multifractal spectra for this grid location
    //  if the user doesn't want to limit the results to the optimized
    //  location only.fixme
    //
    //================================================================
    //
    //          Graph these if we are not optimizing, so 
    //          we will need to notify when to 
    //          initialize and notify when to show.
    //
    //================================================================
    if (pVars.sOptimizingOption != CAPTION_SHOW_ONLY_OPTIMAL) {

      doMFPlots(thisMF,
                piNumSlices,
                piRoiManagerOrSubscanIndex,
                piGRID,
                piThisSliceStartsAt1Not0,
                piNumRois,
                pbGraphOnOnePlot,
                false,//optimizing flag is false here
                pVars,
                ld2dMultifractalSpectraArraysHasFAlphaAtIndex4,
                lsGridAndTitleAndSlice);
    }
    //********************************************************************
    //
    //  ASSEMBLE THE DATA STRINGS INTO AN ARRAY TO RETURN.
    //
    //  The array to be returned has to match the needs of the 
    //  writer function. It has to have at the 0th index [0][0]
    //  the matrix string, at [0][1] the aperture and description string, 
    //  and at [1][i] for all i, the Q(Min to Max) strings, one for 
    //  each data type, meaning fofalpha, Dq, tau, etc.
    //
    //********************************************************************
    StringBuilder[] lsbaQMatrixString0DataDescHeadings1DataDesc2
        = new StringBuilder[3];
    lsbaQMatrixString0DataDescHeadings1DataDesc2[0]
        = new StringBuilder(lsImageInfoAndQMatrixData);
    //----------------------------------------------------------------------
    //
    //      Make a tabbed string of description headings 
    //      then aperture headings.
    //      The string starts with a column for "File Info".
    //
    //----------------------------------------------------------------------
    protectIt:
    {
      StringBuilder lsbAllHeadingsTogether
          = new StringBuilder("File Info" + TAB);

      lsbAllHeadingsTogether
          .append(lsbaApertureHeadingsAndDataString[0])
          .append(lsbaHeadings0DataDescription1[0]);

      lsbaQMatrixString0DataDescHeadings1DataDesc2[1] = lsbAllHeadingsTogether;
    }
    //---------------------------------------------------------------
    //
    //     Make a string of description data and aperture data.
    //     The string starts with the name and other identifying 
    //     info of the file.
    //
    //---------------------------------------------------------------
    protectThisAlso:
    {
      StringBuilder lsbAllDescriptionAndApertureDataTogether
          = new StringBuilder(lsTitleString + TAB);
      lsbAllDescriptionAndApertureDataTogether
          .append(lsbaApertureHeadingsAndDataString[1])
          .append(lsbaHeadings0DataDescription1[1])
          .append(newline);
      // Put the desc/aperture headings and desc/aperture data 
      // into the 2nd and 3rd slots in the qmatrix array

      lsbaQMatrixString0DataDescHeadings1DataDesc2[2]
          = lsbAllDescriptionAndApertureDataTogether;
    }
    // =======================================================================
    return new StringBuilder[][]{lsbaQMatrixString0DataDescHeadings1DataDesc2,
      lsbaDataTypesPerQ};
  }

  /**
   * Returns a string array [] that has a line for each data type. Each line is
   * for a separate file, and consists of the image title plus one entry for
   * each Q. The string varies with the Q array so cannot be written
   * sequentially if the Q-array changes. If it changes, then the string will
   * have the wrong number of entries for header. Access them using the indices
   * in the Symbols file for {@link fraclac.utilities.Symbols#MF_D_DQ_INDEX},
   * {@link fraclac.utilities.Symbols#MF_DQ_INDEX},
   * {@link fraclac.utilities.Symbols#MF_FAT_ALPHA_Q_INDEX},
   * {@link fraclac.utilities.Symbols#MF_ALPHA_Q_INDEX}, and
   * {@link fraclac.utilities.Symbols#MF_TAU_INDEX}.
   *
   * @param psImageRoiSliceGridTitle
   * @param pd2dDGen
   * @param pdaQs
   * @param pVars
   * @return
   */
  public static StringBuilder[]
      linesFileNameTabbedMFSpectraDataAtEachQNoHeadings//
      (String psImageRoiSliceGridTitle,
       double[][] pd2dDGen,
       double[] pdaQs,
       Vars pVars)
  {

    String lsTitleAndDqAtEachQ = psImageRoiSliceGridTitle + TAB;
    String lsTitleAndFAAtEachQ = psImageRoiSliceGridTitle + TAB;
    String lsTitleAndTAUAtEachQ = psImageRoiSliceGridTitle + TAB;
    String lsTitleAndALPHAAtEachQ = psImageRoiSliceGridTitle + TAB;
    String lsTitleAndDDqAtEachQ = psImageRoiSliceGridTitle + TAB;

    for (int liq = 0; liq < pdaQs.length; liq++) {

      lsTitleAndDqAtEachQ +=// pdaQs[liq] + TAB
          Utils.fnum(pd2dDGen[MF_DQ_INDEX][liq]) + TAB;
      lsTitleAndDDqAtEachQ += Utils.fnum(pd2dDGen[MF_D_DQ_INDEX][liq]) + TAB;
      lsTitleAndTAUAtEachQ += Utils.fnum(pd2dDGen[MF_TAU_INDEX][liq]) + TAB;
      lsTitleAndALPHAAtEachQ += Utils.fnum(
          pd2dDGen[MF_ALPHA_Q_INDEX][liq]) + TAB;
      lsTitleAndFAAtEachQ += Utils.fnum(
          pd2dDGen[MF_FAT_ALPHA_Q_INDEX][liq]) + TAB;
    }
    lsTitleAndALPHAAtEachQ += newline;
    lsTitleAndDDqAtEachQ += newline;
    lsTitleAndDqAtEachQ += newline;
    lsTitleAndFAAtEachQ += newline;
    lsTitleAndTAUAtEachQ += newline;

    StringBuilder[] lsStrings = new StringBuilder[5];
    lsStrings[MF_DQ_INDEX] = new StringBuilder(lsTitleAndDqAtEachQ);
    lsStrings[MF_TAU_INDEX] = new StringBuilder(lsTitleAndTAUAtEachQ);
    lsStrings[MF_ALPHA_Q_INDEX] = new StringBuilder(
        lsTitleAndALPHAAtEachQ);
    lsStrings[MF_D_DQ_INDEX] = new StringBuilder(lsTitleAndDDqAtEachQ);
    lsStrings[MF_FAT_ALPHA_Q_INDEX] = new StringBuilder(
        lsTitleAndFAAtEachQ);

    //write each line to the file
    // if the q range has changed, or this is the first run, make 
    // a new file for each and include a column for the image name 
    // and one for each q
    return lsStrings;
  }

  /**
   *
   * @param pMFDescription
   * @param pdFlippancy
   * @param piRedRises
   * @param pdDivergence
   * @param piNumSlices
   * @param piRoiManagerOrSubscanIndex
   * @param piLoc
   * @param piThisSliceStartsAt1Not0
   * @param piNumRois
   * @param pbGraphOnOnePlot
   * @param pbShowOnlyOpt
   * @param pVars
   * @param pd2dGenDimension
   * @param psGridAndTitleAndSlice
   */
  public void doMFPlots(final MultifractalDescription pMFDescription,
                        final int piNumSlices,
                        int piRoiManagerOrSubscanIndex,
                        int piLoc,
                        int piThisSliceStartsAt1Not0,
                        int piNumRois,
                        final boolean pbGraphOnOnePlot,
                        final boolean pbShowOnlyOpt,
                        final Vars pVars,
                        double[][] pd2dGenDimension,
                        String psGridAndTitleAndSlice)
  {
    //1
    double[] ldaQs = pVars.getDaQs();
    String lsDirectoryForSavingResults
        = pVars.getDirectoryForSavingResults();
    String lsTitleAndSlice = pVars.getsTitleAndSliceLabelAndSliceNumber();
    int liNumGrids = pVars.iNumGrids;
    boolean lbSaveResults = pVars.bSaveResults;
    boolean lbSameScale = pVars.bUseSameScale;
    //*******************************************************************
    //                                                                 //
    //              Graph f(alpha(Q)) vs alpha spectra                 //
    //                                                                 //
    //*******************************************************************
    if (pVars.bGraphFAtAlpha) {

      Plots lPlots = new Plots();
      lPlots.plotFAlphaWithAperture(pMFDescription,
                                    piThisSliceStartsAt1Not0,
                                    piNumSlices,
                                    pbGraphOnOnePlot,
                                    pbShowOnlyOpt,
                                    piLoc,
                                    liNumGrids,
                                    pd2dGenDimension[MF_ALPHA_Q_INDEX],
                                    pd2dGenDimension[MF_FAT_ALPHA_Q_INDEX],
                                    psGridAndTitleAndSlice,
                                    lsTitleAndSlice,
                                    pVars.sOriginalImageTitle,
                                    ldaQs,
                                    pVars.getQinc(),
                                    lbSaveResults,
                                    lsDirectoryForSavingResults,
                                    pVars.bDrawAperture,
                                    piRoiManagerOrSubscanIndex,
                                    piNumRois,
                                    pVars.bShowFlippancy,
                                    pVars.bDecideOnMultifractality);
    }
    //2
    //******************************************************************
    //                                                                //
    //               Set up flags for graphing the rest.              //
    //                                                                //
    //******************************************************************
    boolean lbInitialize = (piLoc == 0 || pbShowOnlyOpt);
    boolean lbShowNow = ((piLoc == (liNumGrids - 1)) || pbShowOnlyOpt);
    int liNumPlots = pbShowOnlyOpt ? 1 : liNumGrids;
    //*******************************************************************
    //                                                                 //
    //                      Graph D(Q) vs Q                            //
    //                                                                 //
    //*******************************************************************    
    if (pVars.bGraphQs) {
      graphGeneralizedDimensionDqVsQPlots(pbShowOnlyOpt,
                                          lbInitialize,
                                          liNumPlots,
                                          piLoc,
                                          ldaQs,
                                          psGridAndTitleAndSlice,
                                          pd2dGenDimension[MF_DQ_INDEX],
                                          pd2dGenDimension[MF_D_DQ_INDEX],
                                          lsDirectoryForSavingResults,
                                          lbSaveResults,
                                          lbSameScale,
                                          lbShowNow);

      graphDimensionalOrdering(pbShowOnlyOpt,
                               lbInitialize,
                               liNumPlots,
                               ldaQs,
                               pd2dGenDimension[MF_DQ_INDEX],
                               piLoc,
                               psGridAndTitleAndSlice,
                               lbSaveResults,
                               lsDirectoryForSavingResults,
                               lbShowNow);
    }
    //*******************************************************************
    //                                                                 //
    //                     GRAPH alpha(Q) vs Q                         //
    //                                                                 //
    //*******************************************************************
    if (pVars.bGraphAlphaVsQ) {
      graphAlphaVsQ(pbShowOnlyOpt,
                    lbInitialize,
                    liNumPlots,
                    piLoc,
                    lsDirectoryForSavingResults,
                    ldaQs,
                    pd2dGenDimension[MF_ALPHA_Q_INDEX],
                    lbSaveResults,
                    psGridAndTitleAndSlice,
                    lbSameScale,
                    lbShowNow);
    }
    //*******************************************************************
    //                                                                 //
    //                    Graph f (alpha(Q)) vs Q                      //
    //                                                                 //
    //*******************************************************************
    if (pVars.bGraphFAtAlphaVsQ) {
      graphFAtAlphaVsQ(pbShowOnlyOpt,
                       lbInitialize,
                       liNumPlots,
                       piLoc,
                       lbSaveResults,
                       psGridAndTitleAndSlice,
                       lsDirectoryForSavingResults,
                       ldaQs,
                       pd2dGenDimension[MF_FAT_ALPHA_Q_INDEX],
                       lbSameScale,
                       lbShowNow);
    }
    //*******************************************************************
    //                                                                 //
    //                     Graph Tau spectra                           //
    //                                                                 //
    //*******************************************************************
    if (pVars.bGraphTau) {
      graphThreeTaus(pbShowOnlyOpt,
                     piLoc,
                     lbInitialize,
                     liNumPlots,
                     pd2dGenDimension[MF_TAU_INDEX],
                     pd2dGenDimension[MF_D_DQ_INDEX],
                     pd2dGenDimension[MF_DQ_INDEX],
                     psGridAndTitleAndSlice,
                     ldaQs,
                     lbSaveResults,
                     lsDirectoryForSavingResults,
                     lbSameScale,
                     lbShowNow);
    }
  }

  /**
   *
   * @param pbInitialize
   * @param piNumPlots
   * @param piLoc
   * @param psDirectoryForSavingResults
   * @param pdaQs
   * @param pd2dGenDimensionMF_ALPHA_Q_INDEX
   * @param pbSave
   * @param psGridAndTitleAndSlice
   * @param pbSameScale
   * @param pbShowNow
   */
  void graphAlphaVsQ(boolean pbShowOnlyOpt,
                     boolean pbInitialize,
                     int piNumPlots,
                     int piLoc,
                     String psDirectoryForSavingResults,
                     double[] pdaQs,
                     double[] pd2dGenDimensionMF_ALPHA_Q_INDEX,
                     boolean pbSave,
                     String psGridAndTitleAndSlice,
                     boolean pbSameScale,
                     boolean pbShowNow)
  {
    // --------------------------------------------------------------------
    if (pbInitialize) {
      alphaVsQPlots = new Plot[piNumPlots];
    }
    // --------------------------------------------------------------------
    alphaVsQPlots[pbShowOnlyOpt ? 0 : piLoc]
        = Visuals.GraphAlphaVsQ(pdaQs,
                                pd2dGenDimensionMF_ALPHA_Q_INDEX,
                                psGridAndTitleAndSlice,
                                pbSameScale);
    // ---------------------------------------------------------------------
    if (pbShowNow) {
      if (pbSave) {
        Utils.saveImageOrStack(
            new ImagePlus(psGridAndTitleAndSlice,
                          getPlotStack(alphaVsQPlots)),
            psDirectoryForSavingResults,
            TITLE_FOR_ALPHA_VS_Q + psGridAndTitleAndSlice,
            alphaVsQPlots.length > 1);
      } else {

        Plots.showPlots(TITLE_FOR_ALPHA_VS_Q,
                        alphaVsQPlots,
                        TITLE_FOR_ALPHA_VS_Q);
      }
    }
  }

  /**
   * {@value }
   */
  public static final String TITLE_FOR_ALPHA_VS_Q = alpha + " vs Q",
      TITLE_FOR_F_At_ALPHA_VS_Q = fOfAlpha + " vs Q",
      TITLE_FOR_DQ_VS_Q = "D(Q) vs Q",
      TITLE_FOR_DIMENSIONAL_ORDERING = "Dimensional Ordering",
      TITLE_FOR_TAU_VS_Q = tau + " vs. Q",
      TITLE_FOR_DTAU_VS_Q = "d " + tau + " vs. Q",
      TITLE_FOR_BTAU_VS_Q = "b " + tau + " vs. Q";

  /**
   *
   * @param pbInitialize
   * @param piNumPlots
   * @param piLoc
   * @param pbSaveResults
   * @param psGridAndTitleAndSlice
   * @param psDirectoryForSavingResults
   * @param pdaQs
   * @param pd2dGenDimensionMF_FAT_ALPHA_Q_INDEX
   * @param pbSameScale
   * @param pbShowNow
   */
  void graphFAtAlphaVsQ(boolean pbShowOnlyOpt,
                        boolean pbInitialize,
                        int piNumPlots,
                        int piLoc,
                        boolean pbSaveResults,
                        String psGridAndTitleAndSlice,
                        String psDirectoryForSavingResults,
                        double[] pdaQs,
                        double[] pd2dGenDimensionMF_FAT_ALPHA_Q_INDEX,
                        boolean pbSameScale,
                        boolean pbShowNow)
  {
    // --------------------------------------------------------------------
    if (pbInitialize) {
      fAtAlphaVsQPlots = new Plot[piNumPlots];
    }
    // --------------------------------------------------------------------
    fAtAlphaVsQPlots[pbShowOnlyOpt ? 0 : piLoc]
        = Visuals.graphFAtAlphaVsQ(
            psGridAndTitleAndSlice,
            pdaQs,
            pd2dGenDimensionMF_FAT_ALPHA_Q_INDEX,
            pbSameScale);
    // ---------------------------------------------------------------------
    if (pbShowNow) {
      if (pbSaveResults) {
        Utils.saveImageOrStack(
            new ImagePlus(psGridAndTitleAndSlice,
                          getPlotStack(fAtAlphaVsQPlots)),
            psDirectoryForSavingResults,
            TITLE_FOR_F_At_ALPHA_VS_Q + psGridAndTitleAndSlice,
            fAtAlphaVsQPlots.length > 1);
      } else {
        Plots.showPlots(TITLE_FOR_F_At_ALPHA_VS_Q,
                        fAtAlphaVsQPlots,
                        TITLE_FOR_F_At_ALPHA_VS_Q);
      }
    }
  }

  /**
   *
   * @param pbInitialize
   * @param piNumPlots
   * @param piLoc
   * @param pdaQs
   * @param psGridAndTitleAndSlice
   * @param pd2dGenDimensionMF_DQ_INDEX
   * @param pd2dGenDimensionMF_D_DQ_INDEX
   * @param psDirectoryForSavingResults
   * @param pbSaveResults
   * @param pbSameScale
   * @param pbShowNow
   */
  void graphGeneralizedDimensionDqVsQPlots(
      boolean pbShowOnlyOpt,
      boolean pbInitialize,
      int piNumPlots,
      int piLoc,
      double[] pdaQs,
      String psGridAndTitleAndSlice,
      double[] pd2dGenDimensionMF_DQ_INDEX,
      double[] pd2dGenDimensionMF_D_DQ_INDEX,
      String psDirectoryForSavingResults,
      boolean pbSaveResults,
      boolean pbSameScale,
      boolean pbShowNow)
  {
    // ---------------------------------------------------------------------
    if (pbInitialize) {
      dqVsQPlots = new Plot[piNumPlots];
    }
    // ---------------------------------------------------------------------
    dqVsQPlots[pbShowOnlyOpt ? 0 : piLoc]
        = Visuals.graphQs(pdaQs,
                          psGridAndTitleAndSlice,
                          pd2dGenDimensionMF_DQ_INDEX,
                          pd2dGenDimensionMF_D_DQ_INDEX,
                          pbSameScale);
    // ---------------------------------------------------------------------
    if (pbShowNow) {
      if (pbSaveResults) {
        Utils.saveImageOrStack(new ImagePlus(psGridAndTitleAndSlice,
                                             getPlotStack(dqVsQPlots)),
                               psDirectoryForSavingResults,
                               TITLE_FOR_DQ_VS_Q + psGridAndTitleAndSlice,
                               dqVsQPlots.length > 1);
      } else {
        Plots.showPlots(TITLE_FOR_DQ_VS_Q,
                        dqVsQPlots,
                        TITLE_FOR_DQ_VS_Q);
      }
    }
  }

  /**
   *
   * @param piLoc
   * @param lbInitialize
   * @param liNumPlots
   * @param pd2dGenDimensionMF_TAU_INDEX
   * @param pd2dGenDimensionMF_D_DQ_INDEX
   * @param pd2dGenDimensionMF_DQ_INDEX
   * @param psGridAndTitleAndSlice
   * @param ldaQs
   * @param lbSaveResults
   * @param lsDirectoryForSavingResults
   * @param lbSameScale
   * @param lbShowNow
   */
  void graphThreeTaus(boolean pbShowOnlyOpt,
                      int piLoc,
                      boolean lbInitialize,
                      int liNumPlots,
                      double[] pd2dGenDimensionMF_TAU_INDEX,
                      double[] pd2dGenDimensionMF_D_DQ_INDEX,
                      double[] pd2dGenDimensionMF_DQ_INDEX,
                      String psGridAndTitleAndSlice,
                      double[] ldaQs,
                      boolean lbSaveResults,
                      String lsDirectoryForSavingResults,
                      boolean lbSameScale,
                      boolean lbShowNow)
  {
    graphTauWithGenDimensions(pbShowOnlyOpt,
                              piLoc,
                              lbInitialize,
                              liNumPlots,
                              pd2dGenDimensionMF_D_DQ_INDEX,
                              pd2dGenDimensionMF_DQ_INDEX,
                              psGridAndTitleAndSlice,
                              ldaQs,
                              lbSaveResults,
                              lsDirectoryForSavingResults,
                              lbSameScale,
                              lbShowNow);
    // ---------------------------------------------------------------------
    graphBothTaus(pbShowOnlyOpt,
                  piLoc,
                  lbInitialize,
                  liNumPlots,
                  ldaQs,
                  lbSameScale,
                  pd2dGenDimensionMF_TAU_INDEX,
                  pd2dGenDimensionMF_D_DQ_INDEX,
                  psGridAndTitleAndSlice,
                  lbSaveResults,
                  lsDirectoryForSavingResults,
                  lbShowNow);

  }

  /**
   *
   * @param pbShowOnlyOpt
   * @param pbInitialize
   * @param piNumPlots
   * @param pdaQs
   * @param pd2dMF_DQ_INDEX
   * @param piLoc
   * @param psGridAndTitleAndSlice
   * @param pbSaveResults
   * @param psDirectoryForSavingResults
   * @param pbShowNow
   */
  public void graphDimensionalOrdering(boolean pbShowOnlyOpt,
                                       boolean pbInitialize,
                                       int piNumPlots,
                                       double[] pdaQs,
                                       double[] pd2dMF_DQ_INDEX,
                                       int piLoc,
                                       String psGridAndTitleAndSlice,
                                       boolean pbSaveResults,
                                       String psDirectoryForSavingResults,
                                       boolean pbShowNow)
  {
    // --------------------------------------------------------------------
    if (pbInitialize) {
      dimensionalOrderingDqVsQPlots = new Plot[piNumPlots];
    }
    // --------------------------------------------------------------------
    double[][] ld2dQAndDfOverGenDimensionOrderingInterval
        = matchedSetOverDimensionalOrderingInterval(pdaQs,
                                                    pd2dMF_DQ_INDEX);
    // --------------------------------------------------------------------
    dimensionalOrderingDqVsQPlots[pbShowOnlyOpt ? 0 : piLoc]
        = Plots.plotXY(true,
                       ld2dQAndDfOverGenDimensionOrderingInterval[0],
                       ld2dQAndDfOverGenDimensionOrderingInterval[1],
                       "DQ vs Q Ordering",
                       psGridAndTitleAndSlice,
                       "Q",
                       "D(Q)",
                       .9,
                       2.1,
                       myPurple);
    // ---------------------------------------------------------------------
    if (pbShowNow) {
      if (pbSaveResults) {
        Utils.saveImageOrStack(
            new ImagePlus(psGridAndTitleAndSlice,
                          getPlotStack(
                              dimensionalOrderingDqVsQPlots)),
            psDirectoryForSavingResults,
            TITLE_FOR_DIMENSIONAL_ORDERING + psGridAndTitleAndSlice,
            dimensionalOrderingDqVsQPlots.length > 1);
      } else {
        Plots.showPlots(TITLE_FOR_DIMENSIONAL_ORDERING,
                        dimensionalOrderingDqVsQPlots,
                        TITLE_FOR_DIMENSIONAL_ORDERING);
      }
    }
  }

  /**
   *
   * @param piLoc
   * @param pbInitialize
   * @param piNumPlots
   * @param pd2dGenDimensionMF_D_DQ_INDEX
   * @param pd2dGenDimensionMF_DQ_INDEX
   * @param psGridAndTitleAndSlice
   * @param pdaQs
   * @param pbSaveResults
   * @param psDirectoryForSavingResults
   * @param pbSameScale
   * @param pbShowNow
   */
  void graphTauWithGenDimensions(boolean pbShowOnlyOpt,
                                 int piLoc,
                                 boolean pbInitialize,
                                 int piNumPlots,
                                 double[] pd2dGenDimensionMF_D_DQ_INDEX,
                                 double[] pd2dGenDimensionMF_DQ_INDEX,
                                 String psGridAndTitleAndSlice,
                                 double[] pdaQs,
                                 boolean pbSaveResults,
                                 String psDirectoryForSavingResults,
                                 boolean pbSameScale,
                                 boolean pbShowNow)
  {
    // ---------------------------------------------------------------------
    if (pbInitialize) {
      tauVsQPlots = new Plot[piNumPlots];
    }
    // ---------------------------------------------------------------------
    tauVsQPlots[pbShowOnlyOpt ? 0 : piLoc] = Visuals.doTau(
        psGridAndTitleAndSlice,
        pdaQs,
        pd2dGenDimensionMF_D_DQ_INDEX,
        pd2dGenDimensionMF_DQ_INDEX,
        pbSameScale);
    // ---------------------------------------------------------------------
    if (pbShowNow) {
      if (pbSaveResults) {
        Utils.saveImageOrStack(
            new ImagePlus(psGridAndTitleAndSlice,
                          getPlotStack(tauVsQPlots)),
            psDirectoryForSavingResults,
            TITLE_FOR_TAU_VS_Q + psGridAndTitleAndSlice,
            tauVsQPlots.length > 1);
      } else {
        Plots.showPlots(TITLE_FOR_TAU_VS_Q,
                        tauVsQPlots,
                        TITLE_FOR_TAU_VS_Q);
      }
    }
  }

  /**
   *
   * @param piLoc
   * @param pbInitialize
   * @param piNumPlots
   * @param pdaQs
   * @param pbSameScale
   * @param pd2dMF_TAU_INDEX
   * @param pd2dMF_D_DQ_INDEX
   * @param psGridAndTitleAndSlice
   * @param pbSaveResults
   * @param psDirectoryForSavingResults
   * @param pbShowNow
   */
  void graphBothTaus(boolean pbShowOnlyOpt,
                     int piLoc,
                     boolean pbInitialize,
                     int piNumPlots,
                     double[] pdaQs,
                     boolean pbSameScale,
                     double[] pd2dMF_TAU_INDEX,
                     double[] pd2dMF_D_DQ_INDEX,
                     String psGridAndTitleAndSlice,
                     boolean pbSaveResults,
                     String psDirectoryForSavingResults,
                     boolean pbShowNow)
  {
    // ----------------------------------------------------------------------
    if (pbInitialize) {
      bigTauVsQPlots = new Plot[piNumPlots];
    }
    // ----------------------------------------------------------------------
    double ldMin = minArray(pdaQs) * 2f;
    double ldMax = maxInArray(pdaQs) * 2f;
    // ----------------------------------------------------------------------
    bigTauVsQPlots[pbShowOnlyOpt ? 0 : piLoc]
        = Plots.plotXY(pbSameScale,
                       pdaQs,
                       pd2dMF_TAU_INDEX,
                       TITLE_FOR_BTAU_VS_Q,
                       psGridAndTitleAndSlice,
                       "Q",
                       tau,

                       ldMin,
                       ldMax,
                       COLOR_DK_TEAL_CUSTOM_COLOURS_ARRAY);
    // ----------------------------------------------------------------------
    if (pbShowNow) {
      if (pbSaveResults) {
        Utils.saveImageOrStack(
            new ImagePlus(psGridAndTitleAndSlice,
                          getPlotStack(bigTauVsQPlots)),
            psDirectoryForSavingResults,
            TITLE_FOR_BTAU_VS_Q + psGridAndTitleAndSlice,
            bigTauVsQPlots.length > 1);
      } else {
        Plots.showPlots(TITLE_FOR_BTAU_VS_Q,
                        bigTauVsQPlots,
                        TITLE_FOR_BTAU_VS_Q);
      }
    }
    // =====================================================================
    if (pbInitialize) {
      bigDTauVsQPlots = new Plot[piNumPlots];
    }
    // ---------------------------------------------------------------------
    bigDTauVsQPlots[pbShowOnlyOpt ? 0 : piLoc]
        = Plots.plotXY(pbSameScale,
                       pdaQs,
                       pd2dMF_D_DQ_INDEX,
                       TITLE_FOR_DTAU_VS_Q,
                       psGridAndTitleAndSlice,
                       "Q",
                       tau,
                       ldMin,
                       ldMax,
                       COLOR_TEAL);
    // ----------------------------------------------------------------------
    if (pbShowNow) {
      if (pbSaveResults) {
        Utils.saveImageOrStack(new ImagePlus(psGridAndTitleAndSlice,
                                             getPlotStack(bigDTauVsQPlots)),
                               psDirectoryForSavingResults,
                               TITLE_FOR_DTAU_VS_Q + psGridAndTitleAndSlice,
                               bigDTauVsQPlots.length > 1);
      } else {
        Plots.showPlots(TITLE_FOR_DTAU_VS_Q,
                        bigDTauVsQPlots,
                        TITLE_FOR_DTAU_VS_Q);
      }
    }
  }

  /**
   *
   */
  public static final int AMPLITUDE_Q_MAX = 3, AMPLITUDE_Q_MIN = -1;

  /**
   *
   * @param pdaQ
   * @param pdaDf
   * @return
   */
  double[][] matchedSetOverDimensionalOrderingInterval(double[] pdaQ,
                                                       double[] pdaDf)
  {
    int liLength = pdaQ.length;
    int liCounter = 0;
    // ---------------------------------------------------------------------
    for (int i = 0; i < liLength; i++) {
      if (pdaQ[i] >= AMPLITUDE_Q_MIN && pdaQ[i] <= AMPLITUDE_Q_MAX) {
        liCounter++;
      }
    }
    // ---------------------------------------------------------------------
    double[][] ld2dMatched = new double[2][liCounter];

    int liMatchedIndex = 0;
    for (int i = 0; i < liLength; i++) {
      if (pdaQ[i] >= AMPLITUDE_Q_MIN && pdaQ[i] <= AMPLITUDE_Q_MAX) {
        ld2dMatched[0][liMatchedIndex] = pdaQ[i];
        ld2dMatched[1][liMatchedIndex] = pdaDf[i];
        liMatchedIndex++;
      }
    }
    return ld2dMatched;
  }

  /**
   * Calls various functions and assigns values to several variables if the
   * passed set of multifractal spectra arrays represent an improvement over the
   * last noted set. The key variable it sets is {@link #iBestLocation}.
   * Criteria to assess the data include D<sub>Q</sub>
   * and &alpha; is {@link #neverIncreasing},
   *  {@link #isHumped curvature of &#402;(&alpha;)}, and dimensional ordering.
   *
   *
   * @param piGrid
   * @param pdaQs
   * @param pd2dGenDimension
   * @param pVars
   * @param pbDoOptimizing
   *
   * @return Returns a string describing the data set for the passed
   * {@link fraclac.analyzer.Vars#iNumGrids GRID}. First it appends a newline
   * and title, then {@link #tabbedMFStats tabbed} rows from the passed
   * Generalized Dimension array, followed by a matrix describing it, then 2
   * newlines.
   */
  public static StringBuilder[]
      assessMFSpectraReturnDescriptionHeadings0Data1//
      (final int piGrid,
       final double[] pdaQs,
       final double[][] pd2dGenDimension,
       boolean pbDoOptimizing,
       boolean pbDecideOnMultifractality)
  {
    //******************************************************************
    //
    //         1. ASSESS THE PASSED MULTIFRACTAL DATA SET
    //
    //******************************************************************
    thisMF = new MultifractalDescription(pdaQs,
                                         pd2dGenDimension,
                                         piGrid);
    //********************************************************************
    //
    //    2. COMPARE IT TO THE LAST STORED VALUE
    //       Compare this dataset to the stored optimum set and record 
    //       it if it has the best values so far. The method arbitrarily
    //       keeps values if tied so precedence can matter.
    //
    //====================================================================
    if (pbDoOptimizing) {
      if (bFirstCVandDivCheck) {
        setUpFirstCheck(pdaQs,
                        pd2dGenDimension,
                        piGrid);
      } //==============================================================
      else {
        compareDescriptions(piGrid);
      }
    }
    //******************************************************************
    //
    //    3. RETURN STRINGS DESCRIBING THE DATA SET.
    //       These are strings describing features of the data set 
    //       that were just determined in the previous calls.
    //
    //*******************************************************************  
    return thisMF.getDescriptionAsSbaHeadings0Description1(
        pbDecideOnMultifractality);
  }

  /**
   *
   * @param piGrid
   */
  static void compareDescriptions(int piGrid)
  {
    //*****************************************************************
    //     
    //     This caches values describing the aperture, where the 
    //     red and green sides meet, or crossover.
    //
    //***************************************************************** 
    double ldNewApertureLength = thisMF.getApertureLength();
    double ldbestApertureLengthSoFar = bestMF.getApertureLength();
    double ldNewQXDif = thisMF.getQAt0MinusQAt1X();
    double ldOldQXDif = bestMF.getQAt0MinusQAt1X();
    boolean lbThereWasNoCrossingOver = ldNewQXDif >= 0;
    double ldBestFlippiness = bestMF.setFlip();
    double ldThisFlippiness = thisMF.setFlip();
    double ldWorstFlippiness = worstMF.setFlip();
    double ldThisRedRises = thisMF.dNumRedRises;
    double ldBestRedRises = bestMF.dNumRedRises;
    double ldThisCrossOverOfGreenAlphaAtMax = thisMF.dCrossOverOfGreenAlphaAtMax;
    double ldBestdCrossOverOfGreenAlphaAtMax = bestMF.dCrossOverOfGreenAlphaAtMax;
    double ldThisGreenDivergence = thisMF.dGreenDivergence;
    double ldBestGreenDivergence = bestMF.dGreenDivergence;
    double ldWorstGreenDivergence = worstMF.dGreenDivergence;
    // ----------------------------------------------------------------
    // If the image is not a multifractal, it is likely to have 
    // low convergence, so note this possibility for later use.
    // ----------------------------------------------------------------
    if ((ldThisGreenDivergence < ldWorstGreenDivergence)) {
      worstMF.update(thisMF);
      worstMF.iThisGrid = piGrid;
    }
    //******************************************************************
    // 
    //      We almost always keep the longer aperture. The exception
    //      is when there is crossing over, because then the 
    //      crossover may be responsible for the broader aperture
    //      instead of a truly broader curve.
    //      Keep the new one if the last one was flippier.
    //
    //******************************************************************
    check1:
    if ((ldThisFlippiness > ldBestFlippiness)) {
      return;
    }
    check2:
    if (ldThisFlippiness < ldBestFlippiness)//
    {
      bestMF.update(thisMF);
      iBestLocation = piGrid;
      return;
    }
    check3_TheyAreOfEqualFlippancy:
    if (ldNewApertureLength >= ldbestApertureLengthSoFar) {
      if (lbThereWasNoCrossingOver) {
        bestMF.update(thisMF);
        iBestLocation = piGrid;
        return;
      } else {
        //**********************************************************
        //     
        //     This selects the graph that had the least crossover
        //     if there was any crossover between the red and green.
        //
        //**********************************************************     
        if (ldNewQXDif > ldOldQXDif) {
          bestMF.update(thisMF);
          iBestLocation = piGrid;
          return;
        }
      }
    }// end The aperture was better so we are done.
    //**************************************************************
    //
    //     We only do this if the new aperture was not longer. 
    //     This assesses the new multifractal data 
    //     and selects it if it
    //     is an improvement over the currently stored best data.
    //
    //**************************************************************
    check4:
    if (((ldNewApertureLength < 0)
        && (bestMF.getApertureLength() < 0)
        && (Math.abs(ldNewApertureLength)
        < Math.abs(bestMF.getApertureLength())))) {
      bestMF.update(thisMF);
      iBestLocation = piGrid;
      return;
    }
    check5:
    if (!(thisMF.getApertureLength() == bestMF.getApertureLength())) {
      return;
    }
    check6:
    if (!(thisMF.getLdThisDif() <= bestMF.getLdThisDif())) {
      return;
    }
    //---------------------------------------------------------
    //    If higher, reject it and move on.
    //---------------------------------------------------------
    check7:
    if (thisMF.getLdThisDif() < bestMF.getLdThisDif()) {
      //-----------------------------------------------------
      //     Accept it if it is better aligned across the top
      //-----------------------------------------------------
      bestMF.update(thisMF);
      iBestLocation = piGrid;
      return;
      //=====================================================
    }
    check8://if this is as aligned
    if (!(thisMF.getLdThisDif() == bestMF.getLdThisDif())) {
      return;
    }
    check9:
    if (newOneHasGoodAndImprovedOrdering(thisMF.isHumpy(),
                                         thisMF.isDQNeverIncreased(),
                                         thisMF.isAlphaNeverIncreases())) {
      //if this one meets all the
      //criteria but the last one didn't meet one
      //or if this one is humped properly but not the last
      //or this one beats the other one on either of the
      //last two criteria only, then keep it
      bestMF.update(thisMF);
      iBestLocation = piGrid;
      return;
    }//end what to do if if the dif was same but this one
    //met something the last one didn't
    //====================================================
    check10:
    if ((thisMF.isHumpy()
        && thisMF.isDQNeverIncreased()
        && thisMF.isAlphaNeverIncreases())
        && (bestMF.isHumpy()
        && bestMF.isDQNeverIncreased()
        && bestMF.isAlphaNeverIncreases())) {
      //if they are equivalently perfect so far
      if (((thisMF.getDimensionalOrdering() == IS_ORDERED)
          && (bestMF.getDimensionalOrdering() == NOT_ORDERED))
          || (thisMF.lowestPositiveSum() < bestMF.lowestPositiveSum())) {
        //choose based on the positives
        bestMF.update(thisMF);
        iBestLocation = piGrid;
      }
    }
  }

  /**
   * Returns a string arranged as rows of statistics of the passed arrays, each
   * row headed by a title, and its contents separated by tabs, in columns
   * corresponding to the passed Y array's data arrays.
   *
   *
   * @param pdaX double [] for determining power regression
   * @param pd2dY double [g][e][value] of values at e for each g, from which to
   * find the {@link Calculator#plainLinearRegression
   *              slopes}
   *
   *
   * @return String of slopes, correlation coefficient, and standard error for
   * the {@link Calculator#plainLinearRegression regression
   *         line} vs Q, and the {@link
   * fraclac.utilities.Statistics#dCV coefficient of
   *         variation (&sigma;/&mu;)} for the data
   */
  public static String tabbedMFStats(final double pd2dY[][],
                                     final double pdaX[])
  {
    String[] lsaHeadings = {
      "Slope: Y vs Q",
      R_SQ + "vs Q",
      "Standard Error (vs Q)",
      sigma_over_mu
    };
    Calculator lFracStats = new Calculator();
    int[] liaIndices = {
      Calculator.SLOPE_INDEX,
      Calculator.R_SQ_INDEX,
      Calculator.STD_ERR_INDEX
    };

    double[][] ld2dRSq = new double[pd2dY.length][];
    double[] ldaCV = new double[pd2dY.length];

    for (int i = 0; i < pd2dY.length; i++) {

      ld2dRSq[i] = lFracStats.plainLinearRegression(pd2dY[i],
                                                    pdaX,
                                                    pdaX.length);

      ldaCV[i] = getCoefficientOfVariation(pd2dY[i],
                                           pd2dY[i].length);
    }
    //make a string of tabbed regression stats for each array
    StringBuilder lsbData = new StringBuilder();
    for (int i = 0; i < lsaHeadings.length; i++) {

      lsbData.append(lsaHeadings[i]).
          append(TAB);
      //print the heading in every first column of each row
      //then print a row of the same stat for each column,
      //and a row of CVs for each column in the last row
      for (int j = 0; j < pd2dY.length; j++) {
        if (i == lsaHeadings.length - 1) {
          lsbData.append(fnum(ldaCV[j])).
              append(TAB);
        } else {
          lsbData.append(fnum(ld2dRSq[j][liaIndices[i]])).
              append(TAB);
        }
      }
      lsbData.append(newline);
    }
    //Once for each statistic, write a row
    //once for each array, make a column
    //each row also has a heading and CV_FORMULA, slope, R_SQ, dStdErr
    return lsbData.toString();
  }
//==============================================================================
//******************************************************************************
//==============================================================================

  /**
   *
   * @param pbThisIsHumpy
   * @param pbThisDQNeverIncreases
   * @param pbThisAlphaNeverIncreases
   *
   * @return
   */
  static boolean newOneHasGoodAndImprovedOrdering(
      boolean pbThisIsHumpy,
      boolean pbThisDQNeverIncreases,
      boolean pbThisAlphaNeverIncreases)
  {
    boolean bBestWasHumpy = bestMF.isHumpy();
    boolean bBestDQNeverIncreased = bestMF.isDQNeverIncreased();
    boolean bBestAlphaNeverIncreased = bestMF.isAlphaNeverIncreases();
    return (((pbThisIsHumpy && pbThisDQNeverIncreases
        && pbThisAlphaNeverIncreases)
        && (!bBestWasHumpy
        || !bBestDQNeverIncreased
        || !bBestAlphaNeverIncreased))
        || ((pbThisIsHumpy && !bBestWasHumpy))
        || ((!pbThisIsHumpy && !bBestWasHumpy)
        && (!bBestDQNeverIncreased && !bBestAlphaNeverIncreased)
        && (pbThisAlphaNeverIncreases || pbThisDQNeverIncreases))
        || ((!pbThisIsHumpy && !bBestWasHumpy)
        && (!bBestDQNeverIncreased && !pbThisDQNeverIncreases)
        && (!bBestAlphaNeverIncreased && pbThisAlphaNeverIncreases))
        || ((!pbThisIsHumpy && !bBestWasHumpy)
        && (!bBestDQNeverIncreased && pbThisDQNeverIncreases)
        && (!bBestAlphaNeverIncreased && !pbThisAlphaNeverIncreases)));

  }
//==============================================================================
//******************************************************************************
//==============================================================================

  /**
   *
   * @param pVars
   * @param pd2dGenDimension
   * @param piLocation
   */
  static void setUpFirstCheck(final double[] pdaQs,
                              final double[][] pd2dGenDimension,
                              final int piLocation)
  {
    bFirstCVandDivCheck = false;
    iBestLocation = piLocation;

    bestMF = new MultifractalDescription(pdaQs,
                                         pd2dGenDimension,
                                         piLocation);
    worstMF = new MultifractalDescription(pdaQs,
                                          pd2dGenDimension,
                                          piLocation);

  }//end set up the first check

//==============================================================================
//******************************************************************************
//==============================================================================
  /**
   * Returns an array of 4 arrays for calculating multifractal spectra according
   * to the method of Chhabra and Jensen. The method is called within the
   * FracLac plugin from {@link #doGridAndOptReturnQMatrixQDescriptQDataTypes}.
   * To use it independently of the plugin, refer to the Basic Structure section
   * below.
   *
   * <h5>Basic Structure</h5>
   * The returned array is made from the arrays passed in.
   * <ul>
   * <li>pdaM is assumed to have been obtained using
   * {@link Scan#scanBoxCount1SIZE1GRID1Slice box counting} at one sampling
   * {@link GridSet#i2dSizes size}. Each double in that array is the value
   * measured by placing a sampling element at different locations on an image,
   * and is either the count of
   * {@link fraclac.analyzer.Vars#iUserForeground foreground} pixels or the
   *
   * {@link fraclac.analyzer.GrayCounter#grayScaledeltaIOfSample
   * grayscale intensity difference} within the sampling element.
   * <li>pdaP is an array that corresponds to pdaM's elements divided by their
   * sum.
   * <li>daTestQ is an array of arbitrary values to be used as exponents in
   * calculating the returned array. When the function is called within the
   * FracLac plugin, the {@link fraclac.analyzer.Vars#daQs Qs array} is set by
   * the user.
   * </ul><p>
   * The returned arrays in the master array are all as long as the length of
   * the passed Q array. They hold at each index a sum of values raised to the
   * value of Q at the corresponding index, or a value related to that sum. The
   * way each array's values are calculated as follows:
   * <ol>
   * <LI>
   * Let i<sub>&epsilon;</sub> = the i<sup>th</sup> sample of j samples at some
   * size, &epsilon;, and P<sub>i, &epsilon;</sub> = (mass<sub>i</sub>
   * / &Sum; mass<sub>i to j</sub>) so &Sum;P<sub>i</sub>=1
   * </LI>
   * <li>D<sub>Q</sub> = &Sum;P<sub>i,&epsilon;</sub><sup>Q</sup> for all Q,
   * except at Q = 1, where &Sum;P<sub>i,&epsilon;</sub><sup>Q</sup>
   * = &Sum; [(ln P<sub>i,&epsilon;</sub>) P<sub>i,&epsilon;</sub>]
   * <ul>
   * <LI>
   * find &mu;<sub>i,Q,&epsilon;</sub> =
   * P<sub>i,&epsilon;</sub><sup>Q</sup>/&Sum;P<sub>i to j, &epsilon;
   * </sub><sup>Q</sup>
   * </LI>
   * </ul>
   * <LI>
   * &alpha;(Q) = &Sum;(&mu;<sub>i,Q,&epsilon;</sub> *
   * lnP<sub>i,&epsilon;</sub>) (later to be /ln &epsilon; when used for
   * multifractal spectra)
   * </LI>
   * <LI>
   * &#402;(&alpha;<sub>Q</sub>) = &Sum;(&mu;<sub>i,Q,&epsilon;</sub> * ln
   * &mu;<sub>i,Q,&epsilon;</sub>) (later to be /ln &epsilon; when used for
   * multifractal spectra)
   * </LI>
   * <LI> &tau; mean <sub>Q</sub> = &Sum; (mass<sub>i</sub> / &Sum;
   * mass)<sup>Q-1</sup>
   * / &Sum; Mass<sup>0</sup> (note that &Sum; Mass<sup>0</sup> = count of
   * samples or length of the Mass array)
   * </LI>
   * </ol>
   * To access the arrays, use the appropriate index:
   * <ul>
   * <li>{@link #MF_DGENERAL_DIM_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE} =
   * {@value #MF_DGENERAL_DIM_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE}
   * <li>{@link #MF_TAU_MEAN_METHOD_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE}
   * = {@value #MF_TAU_MEAN_METHOD_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE}
   * <li>{@link #MF_ALPHA_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE}
   * = {@value #MF_ALPHA_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE}
   * <li>{@link #MF_FAT_ALPHA_ARRAY_INDEX_ONLY_SPECIAL_CASES_DO_NOT_USE}
   * = {@value #MF_FAT_ALPHA_ARRAY_INDEX_ONLY_SPECIAL_CASES_DO_NOT_USE}
   * </ul>
   *
   *
   * @param piNumQs
   * @param liNumPs
   * @param liNumMs
   * @param psStatusString
   * @return double [][]
   * @param pdaP double [] of probability values that should correspond to the
   * frequency of each mass value divided by the sum of the masses array
   * @param pdaQ double [] of exponents
   * @param pdaM double [] of masses presumed to be pixel counts or grayscale
   * intensity differences gathered during box counting sampling using one
   * sampling {@link fraclac.analyzer.GridSet#i2dSizes size}
   * @param pdSummedMass
   */
  public static double[][] sumForSIZEOfAllPToExponentQForEachQ(
      final double[] pdaP,
      final int liNumPs,
      final double[] pdaQ,
      final int piNumQs,
      final double[] pdaM,
      final int liNumMs,
      final double pdSummedMass,
      String psStatusString)
  {

    double[] ldaSummedProbsToQ = newArray(piNumQs,
                                          0.0);
    double[] ldaDS = newArray(piNumQs,
                              0.0);
    double[] ldaTauMass = newArray(piNumQs,
                                   0.0);
    double[] ldaAlpha = newArray(piNumQs,
                                 0.0);
    double[] ldaFAtAlpha = newArray(piNumQs,
                                    0.0);
    //..................................................................
    //
    //           For each epsilon that was used at this box size
    //           Sum all the probabilities in the array
    //           to the exponent Q, unless Q=1.
    //
    //..................................................................
    //================================================================
    // The outer loop sets the value of Q.
    //================================================================
    //long llnow = System.currentTimeMillis();

    for (int liQIndex = 0; liQIndex < piNumQs; liQIndex++) {
      double ldThisQ = pdaQ[liQIndex];
      progress(psStatusString + " q=" + ldThisQ);
      //==============================================================
      // 1. Sum each probability raised to this Q.
      //==============================================================
      for (int liPIndex = 0; liPIndex < liNumPs; liPIndex++) {
        //FIXME try speed of caching variable instead of 
        //accessing it twice per run 
        //------------------------------------------------------------
        double ldThisProbability = pdaP[liPIndex];
        //------------------------------------------------------------
        // For the special case where Q = 1, to avoid division 
        // by zero and also making the numerator in the expression 
        // for the generalized dimension into 0, use log X probability
        // but otherwise use the probability raised to Q as the 
        // value to sum.
        //-------------------------------------------------------------
        if (ldThisQ == 1f) {
          ldaDS[liQIndex]
              += Math.log(ldThisProbability) * ldThisProbability;
        } else {
          ldaDS[liQIndex] += Math.pow(ldThisProbability,
                                      ldThisQ);

        }
        //--------------------------------------------------------------
        // Now sum this log or power.
        //--------------------------------------------------------------
        ldaSummedProbsToQ[liQIndex] += Math.pow(ldThisProbability,
                                                ldThisQ);
      }// End the loop to sum every P^Q
      //================================================================
      // Sum the masses for this Q relative to the total mass.
      //================================================================
      for (int liMIndex = 0; liMIndex < pdaM.length; liMIndex++) {
        //sum all of the mass values for this Q and epsilon
        ldaTauMass[liQIndex] += Math.
            pow((pdaM[liMIndex] / pdSummedMass),
                ldThisQ - 1.0f);
      }
      //==============================================================
      for (int liPIndex2 = 0; liPIndex2 < liNumPs; liPIndex2++) {
        // Sum the alphas and f(alphas)
        //using the method of Chhabra and Jensen:
        //mu(i) = [lPlotWindowForGettingResults(i)^Q]/SUM[P(i to j)^Q]
        //alpha = SUM[mu*ln{P(i)}]/ln{epsilon} 
        //     divide by e done by calling fxn
        //f(alpha) = SUM[mu*ln{mu}]/ln{epsilon} 
        //     divide by e done by calling fxn
        double ldThisP = pdaP[liPIndex2];
        double ldmu = Math.pow(ldThisP,
                               ldThisQ)
            / ldaSummedProbsToQ[liQIndex];

        ldaAlpha[liQIndex] += (ldmu * Math.log(ldThisP));

        ldaFAtAlpha[liQIndex] += (ldmu * Math.log(ldmu));
      }
      //==============================================================
      //          store the mean also
      ldaTauMass[liQIndex] /= (double) (liNumMs);
    }//end for each q
    //......................................................................
    //..................Return the results       ...........................
    //......................................................................
    double[][] ld2dResultArrays = new double[4][piNumQs];
    ld2dResultArrays[MF_DGENERAL_DIM_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE]
        = ldaDS;
    ld2dResultArrays[MF_TAU_MEAN_METHOD_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE]
        = ldaTauMass;
    ld2dResultArrays[MF_ALPHA_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE]
        = ldaAlpha;
    ld2dResultArrays[MF_FAT_ALPHA_ARRAY_INDEX_ONLY_SPECIAL_CASES_DO_NOT_USE]
        = ldaFAtAlpha;
    //the multifractal spectrum array

//        long llater = System.currentTimeMillis();
//        IJ.log(
//            (llater - llnow) + " ms for G " +
//                psStatusString + " Math " 
//                + 
//            sumArray(ld2dResultArrays[0]));
    return ld2dResultArrays;
  }
//==============================================================================
//******************************************************************************
//==============================================================================

  /**
   * Returns a double [5][pdaQs.length] holding five arrays for each of 5
   * multifractal analysis data types, each array having the same number of
   * elements as the passed pdaQs array.
   *
   * The returned arrays can be
   * {@link  fraclac.analyzer.Plots#plotFAlpha graphed} to reveal the
   * multifractal spectra. When called within the FracLac plugin, the method
   * finishes preparing data gathered during box counting and preprocessed by
   * the {@link #doGridAndOptReturnQMatrixQDescriptQDataTypes
   * multifractal calculation controller}, which calls a method that raises the
   * data to a set of exponents and
   * {@link #sumForSIZEOfAllPToExponentQForEachQ sums} them to be passed to the
   * current function.
   *
   * <p>
   * To use the function independently of the plugin, once for each size call
   * the {@link #sumForSIZEOfAllPToExponentQForEachQ summing} function, which
   * returns the necessary arrays.
   *
   * <h5>Basic Structure</h5>
   * The list below summarizes how each array at the given index is filled.
   * <ol start = 0>
   * <LI>D<sub>Q</sub >= &delta;D<sub>Q</sub> / (Q - 1)</LI>
   *
   * <LI>&delta;D<sub>Q</sub> = (Q * &alpha;<sub>Q</sub>) &minus;
   * &fnof;(&alpha;<sub>Q</sub>)<!--- f at alpha ---></LI>
   *
   * <LI>&tau;<sub>Q</sub> = -{@link Calculator#dFractalDimension slope} ln
   * &Tau; vs ln &epsilon;</LI>
   *
   * <LI>&alpha;<sub>Q</sub> = -{@link Calculator#dPlainRegressSlope slope}
   * &Alpha; vs ln &epsilon;<sup>-1</sup> </LI>
   *
   * <LI>&fnof;(&alpha;<sub>Q</sub>)<!--- f at alpha ---> =
   * -{@link Calculator#dPlainRegressSlope slope} F vs ln &epsilon;
   * <sup>-1</sup>
   * </LI>
   *
   * </ol>
   *
   * The function uses a {@link Calculator Calculator} object to calculate
   * slopes.
   *
   *
   * @return double [][]
   *
   *
   * @see <ul>
   * <LI>A. Chhabra and R.vars. Jensen, <cite>Direct Determination of the
   * fOfalpha singularity spectrum</cite>, Phys. Rev. Lett. 62: 1327, 1989.</LI>
   *
   * <LI>A. N. D. Posadas, D. Gime'nez, M. Bittelli, C. M. P. Vaz, and M. Flury,
   * <cite>Multifractal Characterization of Soil Particle-Size
   * Distributions</cite>, Soil Sci. Soc. Am. J. 65:1361- 1367 2001</LI>
   * </ul>
   *
   * @param pdaEpsilons double array for {@link GridSet#i2dSizes sizes}
   * (&epsilon;) that were used to gather the data during box counting
   *
   * @param piNumEpsilonsToProcess int for the number of elements to use from
   * the &epsilon; and data arrays
   *
   * @param pdaQs floats for values of the arbitrary exponent
   * {@link fraclac.analyzer.Vars#daQs Q}
   *
   * @param pd2dDAtQForSIZE data array; double
   * [piNumEpsilonsToProcess][Qs.length] of &Sum; probabilities<sup>Q</sup>
   * per &epsilon;
   *
   * @param pd2dMeanTauAtQForSIZE data array: double
   * [piNumEpsilonsToProcess][Qs.length] of mean &Tau;<sub>Q</sub>
   * values
   *
   * @param pd2dAlphaAtQForSIZE data array; double
   * [piNumEpsilonsToProcess][Qs.length] of &Alpha;<sub>Q</sub>
   *
   * @param pd2dFOfAlphaAtQForSIZE data array: double
   * [piNumEpsilonsToProcess][Qs.length] of &fnof;(&alpha;)<sub>Q</sub>
   */
  public static double[][] getMultifractalSpectraArrays(
      double[][] pd2dDAtQForSIZE,
      double[][] pd2dMeanTauAtQForSIZE,
      double[][] pd2dAlphaAtQForSIZE,
      double[][] pd2dFOfAlphaAtQForSIZE,
      double[] pdaEpsilons,
      int piNumEpsilonsToProcess,
      double[] pdaQs)
  {

    final int finalliQLength = pdaQs.length;
    //......................................................................
    //...................... Initialize arrays .............................
    //......................................................................
    double[][] ld2dSumPExpQAtQForSIZE
        = new2dArray(finalliQLength,
                     piNumEpsilonsToProcess,
                     0.0d);
    double[][] ld2dSumTauAtQAForSIZE
        = new2dArray(finalliQLength,
                     piNumEpsilonsToProcess,
                     0.0d);
    double[][] ld2dSumAlphaAtQForSIZE
        = new2dArray(finalliQLength,
                     piNumEpsilonsToProcess,
                     0.0d);
    double[][] ld2dSumFOfAlphaAtQForSIZE
        = new2dArray(finalliQLength,
                     piNumEpsilonsToProcess,
                     0.0d);

    double[] ldaLogInvEpsilons
        = logInverseArray(pdaEpsilons,
                          piNumEpsilonsToProcess);

    //==============================================================
    for (int loopSIZEIndex = 0;
        loopSIZEIndex < piNumEpsilonsToProcess;
        loopSIZEIndex++) {

      for (int loopQIndex = 0; loopQIndex < finalliQLength; loopQIndex++) {

        ld2dSumPExpQAtQForSIZE[loopQIndex][loopSIZEIndex]
            = pd2dDAtQForSIZE[loopSIZEIndex][loopQIndex];

        ld2dSumTauAtQAForSIZE[loopQIndex][loopSIZEIndex]
            = pd2dMeanTauAtQForSIZE[loopSIZEIndex][loopQIndex];

        ld2dSumAlphaAtQForSIZE[loopQIndex][loopSIZEIndex]
            = pd2dAlphaAtQForSIZE[loopSIZEIndex][loopQIndex];

        ld2dSumFOfAlphaAtQForSIZE[loopQIndex][loopSIZEIndex]
            = pd2dFOfAlphaAtQForSIZE[loopSIZEIndex][loopQIndex];
      }
    }//end switch indices

    //==============================================================
    double[] ldaXDAtQAsQxAlphaThenMinusFOfAlpha
        = newArray(finalliQLength,
                   0.0d);
    double[] ldaDAtQAsXDqOverQMinus1 = newArray(finalliQLength,
                                                0.0d);
    double[] ldaTauAtQAsNegSlopeOfLogVsLogSIZE
        = newArray(finalliQLength,
                   0.0d);
    double[] ldaAlphaAtQAsNegSlopeVsLogInvSIZE
        = newArray(finalliQLength,
                   0.0d);
    double[] ldaFOfAlphaAtQsAsNegSlopeVsLogInvSIZE
        = newArray(finalliQLength,
                   0.0d);
    //..................................................................
    //....GET SLOPE OR OTHER VALUE AT EACH Q FOR EACH DATA TYPE.........
    //..................................................................
    for (int liQIndex = 0; liQIndex < finalliQLength; liQIndex++) {
      // In this section, we are taking linear regression slopes
      // rather than the expected log-log, because the variables
      // are already logs. The passed f_alpha values are themselves
      // fractal dimensions, calculated as alpha = ln Pi/ln epsilon.
      // The epsilon arrays are converted here prior to being
      // sent to the linear regresser.
      if (pdaQs[liQIndex] == 1) {

        //==============================================================
        {
          Calculator lfsc = new Calculator();
          lfsc.plainLinearRegression(ld2dSumPExpQAtQForSIZE[liQIndex],
                                     ldaLogInvEpsilons,
                                     ldaLogInvEpsilons.length);

          ldaDAtQAsXDqOverQMinus1[liQIndex] = -lfsc.dPlainRegressSlope;

          ldaXDAtQAsQxAlphaThenMinusFOfAlpha[liQIndex] = 0.0f;

          ldaTauAtQAsNegSlopeOfLogVsLogSIZE[liQIndex] = 0.0f;
        }
        //==============================================================
        {
          Calculator lfsc = new Calculator();
          lfsc.plainLinearRegression(ld2dSumAlphaAtQForSIZE[liQIndex],
                                     ldaLogInvEpsilons,
                                     ldaLogInvEpsilons.length);

          ldaAlphaAtQAsNegSlopeVsLogInvSIZE[liQIndex]
              = -lfsc.dPlainRegressSlope;
        }
        //==============================================================
        {
          Calculator lfsc = new Calculator();
          lfsc.
              plainLinearRegression(ld2dSumFOfAlphaAtQForSIZE[liQIndex],
                                    ldaLogInvEpsilons,
                                    ldaLogInvEpsilons.length);

          ldaFOfAlphaAtQsAsNegSlopeVsLogInvSIZE[liQIndex]
              = -lfsc.dPlainRegressSlope;
        }
      } //end Q is equal to 1
      else {
        //==============================================================
        {
          FracStats lfsQ1Stats = new FracStats(
              "",
              ld2dSumTauAtQAForSIZE[liQIndex],
              pdaEpsilons);

          ldaTauAtQAsNegSlopeOfLogVsLogSIZE[liQIndex]
              = -lfsQ1Stats.dFractalDimension;
        }
        //==============================================================
        {
          Calculator lfsc = new Calculator();
          lfsc.plainLinearRegression(ld2dSumAlphaAtQForSIZE[liQIndex],
                                     ldaLogInvEpsilons,
                                     ldaLogInvEpsilons.length);

          ldaAlphaAtQAsNegSlopeVsLogInvSIZE[liQIndex]
              = -lfsc.dPlainRegressSlope;
        }
        //==============================================================
        {
          Calculator lfsc = new Calculator();
          lfsc.
              plainLinearRegression(ld2dSumFOfAlphaAtQForSIZE[liQIndex],
                                    ldaLogInvEpsilons,
                                    ldaLogInvEpsilons.length);

          ldaFOfAlphaAtQsAsNegSlopeVsLogInvSIZE[liQIndex]
              = -lfsc.dPlainRegressSlope;

          ldaXDAtQAsQxAlphaThenMinusFOfAlpha[liQIndex]
              = (pdaQs[liQIndex]
              * ldaAlphaAtQAsNegSlopeVsLogInvSIZE[liQIndex])
              - ldaFOfAlphaAtQsAsNegSlopeVsLogInvSIZE[liQIndex];

          ldaDAtQAsXDqOverQMinus1[liQIndex]
              = ldaXDAtQAsQxAlphaThenMinusFOfAlpha[liQIndex]
              / (pdaQs[liQIndex] - 1.0f);
        }
      }//end Q is not equal to 1

    }//end do every Q
    //==============================================================
    return new double[][]{
      ldaDAtQAsXDqOverQMinus1,
      ldaXDAtQAsQxAlphaThenMinusFOfAlpha,
      ldaTauAtQAsNegSlopeOfLogVsLogSIZE,
      ldaAlphaAtQAsNegSlopeVsLogInvSIZE,
      ldaFOfAlphaAtQsAsNegSlopeVsLogInvSIZE
    };
  }
//==============================================================================
//******************************************************************************
//==============================================================================

  /**
   * Plots the data for the grid orientation chosen as
   * {@link #iBestLocation optimum}.
   *
   *
   * @param piNumSlices
   * @param pbGraphOnOnePlot
   * @param pVars
   * @param psbaTitles
   * @param piRoiManagerOrSubScanIndex
   * @param piNumRois
   * @param piThisSlice
   *
   *
   */
  public void showOptimizedMFSet(int piNumSlices,
                                 final boolean pbGraphOnOnePlot,
                                 Vars pVars,
                                 StringBuilder[] psbaTitles,
                                 final int piRoiManagerOrSubScanIndex,
                                 final int piNumRois,
                                 final int piThisSlice)
  {

    //------------------------------------------------------------------
    // Everytime through, record the title of the image that 
    // was selected as the previous best and pre-pend a note
    // saying which location it is for. The default is the 
    // first String in the names array.
    //------------------------------------------------------------------
    String lsOptPrefixAndGridLocationAndDetailedTitle
        = psbaTitles[0].toString();

    String lsOriginalTitleAndSlice = pVars.
        getsTitleAndSliceLabelAndSliceNumber();

    if (iBestLocation >= 0) {
      lsOptPrefixAndGridLocationAndDetailedTitle
          = psbaTitles[iBestLocation].toString();
    }
    lsOptPrefixAndGridLocationAndDetailedTitle
        = "Opt G" + (iBestLocation + 1)
        + lsOptPrefixAndGridLocationAndDetailedTitle;

    //---------------------------------------------------------------------
    double[][] ldaCopyOfBestMFSpectra = bestMF.copyd2dMFSpectraArray();

    doMFPlots(bestMF,
              piNumSlices,
              piRoiManagerOrSubScanIndex,
              iBestLocation,
              piThisSlice,
              piNumRois,
              pbGraphOnOnePlot,
              true,
              pVars,
              ldaCopyOfBestMFSpectra,
              lsOptPrefixAndGridLocationAndDetailedTitle);
  }

  Plot[] dimensionalOrderingDqVsQPlots;
  Plot[] dqVsQPlots;
  Plot[] tauVsQPlots;
  Plot[] bigTauVsQPlots;
  Plot[] bigDTauVsQPlots;
  Plot[] alphaVsQPlots;
  Plot[] fAtAlphaVsQPlots;

  /**
   * Returns true if the passed array is decreasing or flat in the values
   * corresponding to Q=0, Q=1, Q=2.
   *
   * Check if the generalized dimension varies as d>DC>=DH>=DI>=DK>=Dt where d
   * is the euclidean dimension, DC the capacity dimension, where Q=0, DH, the
   * Hausdorf dimension, DI the information dimension where Q=1, DK the
   * korrelation dimension where q=2, and Dt the topological dimension if these
   * three values are not strictly there, then look at the interval using a
   * different function
   *
   * @param pdaF
   * @param pdaQs double []
   *
   *
   * @return int
   */
  public String findDimensionOrderingStrict(
      double[] pdaF,
      double pdaQs[])
  {
    //check if the generalized dimension varies as
    //d>DC>=DH>=DI>=DK>=Dt where d is the euclidean dimension,
    //DC the capacity dimension, where Q=0, DH, the Hausdorf dimension,
    //DI the information dimension where Q=1, 
    //DK the korrelation dimension where
    //q=2, and Dt the topological dimension
    //if these three values are not strictly there,
    //then look at the interval using a different function

    double ldCapacityDim = 0f, ldInformationDim = 0f, ldKorrelationDim = 0f;
    boolean lbHasDC = false, lbHasDI = false, lbHasDK = false;
    for (int i = 0; i < pdaQs.length; i++) {
      if (pdaQs[i] == 0) {
        ldCapacityDim = pdaF[i];
        lbHasDC = true;
      }
      if (pdaQs[i] == 1) {
        ldInformationDim = pdaF[i];
        lbHasDI = true;
      }
      if (pdaQs[i] == 2) {
        ldKorrelationDim = pdaF[i];
        lbHasDK = true;
      }

    }
    if (!lbHasDC && !lbHasDI && !lbHasDK) {
      return UNKNOWN_ORDER;
    }
    if (!lbHasDC && !lbHasDI) {
      return UNKNOWN_ORDER;
    }
    if (!lbHasDC && !lbHasDK) {
      return UNKNOWN_ORDER;
    }
    if (!lbHasDI && !lbHasDK) {
      return UNKNOWN_ORDER;
    }
    if (lbHasDC && lbHasDI && lbHasDK) {
      if ((ldCapacityDim >= ldInformationDim)
          && (ldInformationDim >= ldKorrelationDim)) {
        return IS_ORDERED;
      } else {
        return NOT_ORDERED;
      }
    }
    if (lbHasDC && !lbHasDI && lbHasDK) {
      if ((ldCapacityDim >= ldKorrelationDim)) {
        return IS_ORDERED;
      } else {
        return NOT_ORDERED;
      }
    }

    if (lbHasDC && lbHasDI && !lbHasDK) {
      if ((ldCapacityDim >= ldInformationDim)) {
        return IS_ORDERED;
      } else {
        return NOT_ORDERED;
      }
    }
    if (!lbHasDC && lbHasDI && lbHasDK) {
      if (ldInformationDim >= ldKorrelationDim) {
        return IS_ORDERED;
      } else {
        return NOT_ORDERED;
      }
    }
    return NOT_ORDERED;
  }

  /**
   * Calculates multifractal spectra data for the passed array and grid
   * position. It calls functions to plot the results, and returns a string of
   * multifractal spectra and optionally optimizing information from a call to
   * {@link #getMFSpectraForThisGridAndOptimizingOption}.
   *
   * It is called by
   * {@link #multifractalDataProcessor multifractal data processor}. The
   * essential algorithm is:
   * <OL>
   * <li>initialize local arrays</li>
   * <li>For each size
   * <ol type = i>
   * <li>convert the passed masses into
   * {@link fraclac.utilities.ArrayMethods#divideArrayBy densities}
   * </li>
   * <li>Update the progress bar</li>
   * <li>get arrays of summed
   * {@link #sumForSIZEOfAllPToExponentQForEachQ probabilities to the Q}
   * </li></ol></li>
   * <li>Get {@link #getMultifractalSpectraArrays arrays} for multifractal
   * spectra</li>
   * <li>Call a {@link #matrixOfQsAndMultifractalSpectraData function} to
   * generate a string of multifractal spectra data for writing results from the
   * multifractal spectra arrays</li>
   * <li>Depending on {@link fraclac.analyzer.Vars#bGraphFAtAlpha graphing} and
   * {@link fraclac.analyzer.Vars#sOptimizingOption optimizing} options set in
   * the passed Vars, modify the string for the
   * {@link #assessMFSpectraReturnDescriptionHeadings0Data1 optimized} results
   * and {@link Plots#plotFAlpha graph &fnof;(&alpha;)} or
   * {@link Plots#plotXY plot} other relationships</li>
   * <li>Return the string.</li></OL>
   *
   * @param piNumSlices
   * @param psDataOption
   *
   *
   * @param pbGraphOnOnePlot
   * @param psbaTitles
   * @param pVars
   * @param piThisSliceStartsAt1Not0 int for which slice this is in the stack
   * @param pdaSIZEs array of box sizes to use
   * @param pd2dMasses array of d2dPixArraysAtSIZEs to use
   * @param piGRID int for the current grid position
   * @param piRoiManagerOrSubScanIndex
   * @param piNumRois
   *
   * @return String from a call to
   * {@link #getMFSpectraForThisGridAndOptimizingOption}
   *
   * @see #sumForSIZEOfAllPToExponentQForEachQ
   * @see #getMultifractalSpectraArrays
   * @see Plots#plotFAlpha
   * @see Plots#plotXY
   */
  public StringBuilder[][]
      doGridAndOptReturnQMatrixQDescriptQDataTypes(
          int piNumSlices,
          final boolean pbGraphOnOnePlot,
          StringBuilder[] psbaTitles,
          Vars pVars,
          double[][] pd2dMasses,
          double[] pdaSIZEs,
          final int piGRID,//is -1 if from smoothed or mincover
          final int piThisSliceStartsAt1Not0,
          final int piRoiManagerOrSubScanIndex,
          final int piNumRois)
  {

    // ============================================================
    //
    //               1. INITIALIZE NEW ARRAYS
    //
    // ============================================================
    final int liNumSIZEs = pdaSIZEs.length;
    final double[] ldaQs = pVars.getDaQs();
    final int liNumQs = ldaQs.length;

    double[][] ld2dDAtQThisSIZE = new2dArray(liNumSIZEs,
                                             liNumQs,
                                             0d);
    double[][] ld2dMeanTauQThisSIZE
        = new2dArray(liNumSIZEs,
                     liNumQs,
                     0d);
    double[][] ld2dAlphaAtQThisSIZE
        = new2dArray(liNumSIZEs,
                     liNumQs,
                     0d);
    double[][] ld2dFATAlphaAtQThisSIZE = new2dArray(
        liNumSIZEs,
        liNumQs,
        0d);

    String sStatusString = "MF Data Processor S-"
        + piThisSliceStartsAt1Not0
        + " G-" + piGRID + "/" + pVars.iNumGrids
        + " roi-" + (piRoiManagerOrSubScanIndex + 1) + "/" + piNumRois;

    progress(sStatusString);

    // ==============================================================
    //
    //      2. FOR EACH SAMPLING SIZE, MAKE ARRAY OF SUM P^Q 
    //
    // ==============================================================
    for (int liSIZEIndex = 0; liSIZEIndex < liNumSIZEs; liSIZEIndex++) {
      //System.gc();

      progress(sStatusString + " size-" + liSIZEIndex);
      IJ.showProgress(liSIZEIndex,
                      liNumSIZEs);
      //==============================================================
      double ldSumMassAtSIZE
          = sumArray(pd2dMasses[liSIZEIndex],
                     pd2dMasses[liSIZEIndex].length);

      double[] ldaDensities = divideArrayBy(
          pd2dMasses[liSIZEIndex],
          pd2dMasses[liSIZEIndex].length,
          ldSumMassAtSIZE);

      double[] ldaMasses = pd2dMasses[liSIZEIndex];
      boolean lbRemoveZeros = true;
      boolean lbRemoveZerosFromReturnedDistribution = true;
      //=================================================================
      //
      //  If user has selected to use a binned probability distribution
      //  in which they determine the bin limits and number of bins
      //  then make the distribution accordingly.
      //
      //==================================================================
      if (pVars.iMaxFrequencies > 0) {
        double[][] ldaBinsAndProbs = Statistics
            .probabilityDistributionWithBinIncrements1OrGreater(
                pd2dMasses[liSIZEIndex],
                pd2dMasses[liSIZEIndex].length,
                pVars.iMaxFrequencies,
                lbRemoveZerosFromReturnedDistribution,
                lbRemoveZeros);

        ldaMasses = ldaBinsAndProbs[0];
        ldaDensities = ldaBinsAndProbs[1];

        ldSumMassAtSIZE = sumArray(ldaMasses);

      }
      //==============================================================
      //
      //    BOTTLENECK - THIS IS WHERE THE MULTIFRACTAL DATA
      //    ARE EXAGGERATED - THE HEART OF THE MATTER
      //    IT IS A PROCESSING CHOKER. SEE METHOD FOR DETAILS.
      //
      //===============================================================
      sumPLoop:
      {
        double[][] ld2dA =//FIXME nov 2012 this is still the slowest!!!!
            // probably the high number of uses of math.pow & log
            sumForSIZEOfAllPToExponentQForEachQ(
                ldaDensities,
                ldaDensities.length,
                ldaQs,
                liNumQs,
                ldaMasses,
                ldaMasses.length,
                ldSumMassAtSIZE,
                sStatusString);

        ld2dDAtQThisSIZE[liSIZEIndex]
            = ld2dA[MF_DGENERAL_DIM_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE];
        ld2dMeanTauQThisSIZE[liSIZEIndex]
            = ld2dA[MF_TAU_MEAN_METHOD_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE];
        ld2dAlphaAtQThisSIZE[liSIZEIndex]
            = ld2dA[MF_ALPHA_ARRAY_ONLY_SPECIAL_CASES_DO_NOT_USE];
        ld2dFATAlphaAtQThisSIZE[liSIZEIndex]
            = ld2dA[MF_FAT_ALPHA_ARRAY_INDEX_ONLY_SPECIAL_CASES_DO_NOT_USE];
      }
    }//end do each size
    //..................................................................
    //   Return a string that may include optimization information
    //   for this grid location.
    // .................................................................
    return getMFSpectraForThisGridAndOptimizingOption(
        piNumSlices,
        pbGraphOnOnePlot,
        liNumSIZEs,
        piGRID,
        piThisSliceStartsAt1Not0,
        psbaTitles,
        pdaSIZEs,
        ld2dDAtQThisSIZE,
        ld2dMeanTauQThisSIZE,
        ld2dAlphaAtQThisSIZE,
        ld2dFATAlphaAtQThisSIZE,
        pVars,
        piRoiManagerOrSubScanIndex,
        piNumRois,
        sStatusString);
      }

      public static void progress(String pString)
      {
        IJ.showStatus(pString);
      }

}//End Class
