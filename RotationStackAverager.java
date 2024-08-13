package fraclac.writers;

import fraclac.analyzer.*;
import fraclac.utilities.Statistics;
import fraclac.utilities.Symbols;
import ij.IJ;
import ij.gui.Roi;
import static java.lang.System.out;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * FracLacProject
 *
 * @author Audrey Karperien akarpe01@postoffice.csu.edu.au Rev: $Id: $ $HeadURL&
 */
public class RotationStackAverager extends Symbols
{

  private double[] daElongated;
  private int iSliceHoldingMax;
  private double dLastMax;

  /**
   * Constructor that initializes the {@link #iNumSlices number of slices} and
   * the local {@link #aRotationData array} of {@link RotationStackData} to an
   * array holding the passed number of slices.
   *
   * @param piNumberOfSlices
   * @param pDp
   * @param pMotherRoi
   */
  public RotationStackAverager(int piNumberOfSlices,
                               DataProcessor pDp,
                               Roi pMotherRoi)
  {
    iNumSlices = piNumberOfSlices;
    aRotationData = new RotationStackData[piNumberOfSlices];
    dataProcessor = new DataProcessor();
    dataProcessor.setUpToReceiveRotationData(pDp);
    roiMotherRoi = pMotherRoi;

  }
  int iNumSlices;
  RotationStackData[] aRotationData;
  public DataProcessor dataProcessor;
  public Roi roiMotherRoi;

  //======================================================================
  //                            METHODS
  //======================================================================
  /**
   * Makes an internal instance of a {@link RotationStackData} from the passed
   * {@link DataProcessor}, then adds it to the class {@link #aRotationData
   * array} of {@link RotationStackData} at an index calculated as
   * <code>(piSliceNumber - 1)</code>.
   *
   * @param pDp DataProcessor used to create a RotationStackData that will be
   * added to a class array of them
   * @param piSliceNumber int for the slice number; it is used to determine the
   * index at which to add the new data (i.e., the index is piSliceNumber - 1)
   */
  public void addNewDataProcessor(DataProcessor pDp,
                                  int piSliceNumber)
  {
    // ===================================================================
    // Handle error if adding more than prepared for.
    // ===================================================================
    if ((piSliceNumber > iNumSlices)) {
      IJ.log(getClass().getSimpleName() + ": Too many slices added."
          + piSliceNumber + "/" + iNumSlices
          + new Exception().getStackTrace()[0].getLineNumber());
      return;
    }
    // ---------------------------------------------------------------------
    // Make a local RotationStackData based on the passed DataProcessor.
    // This constructor copies specific fields from the passed instance.
    // ---------------------------------------------------------------------
    RotationStackData lRotationStackData = new RotationStackData(pDp);
    // ----------------------------------------------------------------------
    //  Always add the new instance to the class array.
    // ----------------------------------------------------------------------
    aRotationData[piSliceNumber - 1] = lRotationStackData;
  }

  /**
   *
   * @param psFieldName
   * @return
   */
  Statistics getStatsForAllOccurrencesOfField(String psFieldName)
  {
    return getStatsForAllOccurrencesOfField(null,
                                            psFieldName,
                                            NOTVARS,
                                            DOUBLE);
  }

  /**
   *
   * @param psStatistics
   * @return
   */
  Statistics getStatsForAllOccurrencesOfField(Statistics psStatistics)
  {
    return getStatsForAllOccurrencesOfField(psStatistics.getName(),
                                            dmean,
                                            NOTVARS,
                                            DOUBLE);
  }

  /**
   *
   * @param psNameOfClassContainingField
   * @param psFieldName
   * @return
   */
  Statistics getStatsForAllOccurrencesOfField(
      String psNameOfClassContainingField,
      String psFieldName)
  {
    return getStatsForAllOccurrencesOfField(psNameOfClassContainingField,
                                            psFieldName,
                                            NOTVARS,
                                            DOUBLE);
  }

  /**
   *
   * @param psFieldName
   * @param plIsDouble
   * @return
   */
  Statistics getStatsForAllOccurrencesOfField(String psFieldName,
                                              long plIsDouble)
  {
    return getStatsForAllOccurrencesOfField(null,
                                            psFieldName,
                                            NOTVARS,
                                            plIsDouble);
  }

  /**
   *
   * @param psFieldName
   * @param pbIsVars
   * @param plIsDouble
   * @return
   */
  Statistics getStatsForAllOccurrencesOfField(String psFieldName,
                                              boolean pbIsVars,
                                              long plIsDouble)
  {
    return getStatsForAllOccurrencesOfField(null,
                                            psFieldName,
                                            pbIsVars,
                                            plIsDouble);
  }
  /**
   *
   */
  double[] daMeanOfArrayOrValueForThisField;

  /**
   *
   * Returns a {@link fraclac.utilities.Statistics} object created from all of
   * the entries for the passed field, currently in the array of data objects
   * ({@link #aRotationData}).
   *
   * Assumes that the aRotationData array has been filled previously using
   * {@link #addNewDataProcessor}, to the number of slices in the passed int
   * (<code>liNumSlices</code>).
   *
   * @param liNumSlices
   * @param psNameOfOuterFieldContainingField
   * @param psFieldName the field to be tallied, entered as a string (e.g.,
   * "field" not "someClass.field"); the string must be the name of a public
   * double field in the {@link RotationStackData} class. It can also be in one
   * of the {@link Statistics}, {@link FracStats}, or {@link Calculator} class
   * instances within that class.
   * @param pbisVars true if the field is within the {@link Vars} in the
   * RotationStackData class; false if it is in the outer class or within the
   * {@link DataProcessor} object.
   * @param pbIsDouble true if the field is a double; false for an int
   * @return Statistics instance
   */
  Statistics getStatsForAllOccurrencesOfField(
      String psNameOfOuterFieldContainingField,
      String psFieldName,
      boolean pbisVars,
      final long pdIsDouble)
  {
    // -------------------------------------------------------------
    // Make the array that will be used to store data and 
    // be the basis for the Statistics instance returned.
    // -------------------------------------------------------------
    boolean lbIsDouble = (pdIsDouble == DOUBLE);
    daMeanOfArrayOrValueForThisField = new double[iNumSlices];
    String lsErrorID = "Error string not initialized yet for " + psFieldName
        + " class=" + psNameOfOuterFieldContainingField;
    Field lFieldForSecondStringMeaningDesiredFinalField = null;
    double ldFieldValue = INF;

    Statistics lStatistics;
    Calculator lCalculator;
    FracStats lFracStats;
    // -----------------------------------------------------------------
    for (int liSlice = 0; liSlice < iNumSlices; liSlice++) {
      // ---------------------------------------------------------------
      // Go through the array of RotationStackData instances
      // and store in an array the value of the passed field from each.
      //----------------------------------------------------------------
      // Get the data instance for this slice.
      RotationStackData lRotationDataForThisSlice = aRotationData[liSlice];
      Class lClassEitherVarsOrData = pbisVars
          ? lRotationDataForThisSlice.Dp.scan.vars.getClass()
          : lRotationDataForThisSlice.Dp.data.getClass();
      // --------------------------------------------------------------
      // Get the value in the right form, convert it to a double, 
      // and store it in a local array or else do the array function.
      // --------------------------------------------------------------
      // We need the object from which to extract the value.
      // The object is the class that directly holds the field.
      // --------------------------------------------------------------
      Object lVarsOrDataObjectToGetValueOfFieldFrom = pbisVars
          ? lRotationDataForThisSlice.Dp.scan.vars
          : lRotationDataForThisSlice.Dp.data;
      if (psFieldName.contains("scan.")) {
        lVarsOrDataObjectToGetValueOfFieldFrom
            = lRotationDataForThisSlice.Dp.scan;
      }
      try {
        // ------------------------------------------------------------------
        // If there is a first string, get the object that it specifies,
        // for this slice, because the field is in that object.
        // ------------------------------------------------------------------
        if ((!(psNameOfOuterFieldContainingField == null))
            && (!psNameOfOuterFieldContainingField.isEmpty())) {
          // --------------------------------------------------------------
          Field lOuterFieldContainingField = lClassEitherVarsOrData.getField(
              psNameOfOuterFieldContainingField);
          String lsTypeOfContainingField
              = lOuterFieldContainingField.getType().getCanonicalName();
          // -----------------------------------------------------------------
          // To cast the object, we need to know its type.
          // Assume the possible types for the first string 
          // are Statistics, FracStats, or Calculator.
          // -----------------------------------------------------------------
          if ((lsTypeOfContainingField.contains("Statistics"))) {
            // --------------------------------------------------------------
            lVarsOrDataObjectToGetValueOfFieldFrom
                = lStatistics
                = (Statistics) lOuterFieldContainingField.get(
                    lVarsOrDataObjectToGetValueOfFieldFrom);
            lFieldForSecondStringMeaningDesiredFinalField
                = lStatistics.getClass().getField(psFieldName);
          } else if ((lsTypeOfContainingField.contains("FracStats"))) {
            // --------------------------------------------------------------
            lVarsOrDataObjectToGetValueOfFieldFrom
                = lFracStats
                = (FracStats) lOuterFieldContainingField.get(
                    lVarsOrDataObjectToGetValueOfFieldFrom);
            lFieldForSecondStringMeaningDesiredFinalField
                = lFracStats.getClass().getField(psFieldName);
          } else {
            // ---------------------------------------------------------------
            lVarsOrDataObjectToGetValueOfFieldFrom
                = lCalculator
                = (Calculator) lOuterFieldContainingField.get(
                    lVarsOrDataObjectToGetValueOfFieldFrom);
            lFieldForSecondStringMeaningDesiredFinalField
                = lCalculator.getClass().getField(psFieldName);
          }
        }// ----------------------------------------------------------------
        // But if there is no first string, get the field directly.
        // ----------------------------------------------------------------
        else {
          lFieldForSecondStringMeaningDesiredFinalField
              = lClassEitherVarsOrData.getField(psFieldName);
        }
        // =================================================================
        // Now we have the field, so we get its value.
        // =================================================================
        // -----------------------------------------------------------------
        // If the field is an array, then run the array method.
        // -----------------------------------------------------------------
        if (lFieldForSecondStringMeaningDesiredFinalField.getType().isArray()) {
          // ---------------------------------------------------------------
          // Assume it is either a [][][], [][], or [] array.
          // ---------------------------------------------------------------
          Class lClassOfField = lFieldForSecondStringMeaningDesiredFinalField.
              getType();
          if (lClassOfField.toString().contains("[[[")) {
            double[][][] ld3d
                = (double[][][]) (lFieldForSecondStringMeaningDesiredFinalField.
                get(lVarsOrDataObjectToGetValueOfFieldFrom));
            ldFieldValue = sizeOfLongestArray(ld3d);
            store3dArraysInLocald3dArray(liSlice,
                                         ld3d);

          }
          // ---------------------------------------------------------------
          // If it is a 2d array, load each slice's 2d array into  
          // a class level 3d array.
          // ---------------------------------------------------------------
          if (lClassOfField.toString().contains("[[")) {

            double[][] ld2d
                = (double[][]) (lFieldForSecondStringMeaningDesiredFinalField.
                get(lVarsOrDataObjectToGetValueOfFieldFrom));

            ldFieldValue = meanOfArray(ld2d);

            store2dArraysInLocald3dArray(liSlice,
                                         ld2d);
            // ---------------------------------------------------
            // When the last array is added, write a new array
            // consisting of the 2d arrays in sequence. We now
            // have a 3d array and a long 2d array.
            // ---------------------------------------------------
            if (liSlice == (iNumSlices - 1)) {
              d2dElongated = convert3dArrayTo2dArray(d3d);
              return null;
            }

          } // -----------------------------------------------------------------
          // If it is a [] array
          // -----------------------------------------------------------------
          else {
            if ((liSlice == 0)) {
              d2dElongated = new double[iNumSlices][];
            }
            d2dElongated[liSlice]
                = (double[]) (lFieldForSecondStringMeaningDesiredFinalField.
                get(lVarsOrDataObjectToGetValueOfFieldFrom));
            // Get the mean of the array and store it in the main data processor. 
            ldFieldValue = meanOfArray(d2dElongated[liSlice]);
            if ((liSlice == iNumSlices)) {
              // on the last slice, rewrite the array
              convert2DArrayToDoubleArray(d2dElongated);
            }
          }
        } else//
        // -----------------------------------------------------------------
        // If it is not an array, cast it to a double. Assume it is int or
        // double if not array.
        // -----------------------------------------------------------------
        {
          ldFieldValue = lbIsDouble
              ? lFieldForSecondStringMeaningDesiredFinalField.getDouble(
                  lVarsOrDataObjectToGetValueOfFieldFrom)
              : (double) (lFieldForSecondStringMeaningDesiredFinalField.getInt(
                  lVarsOrDataObjectToGetValueOfFieldFrom));

          if ((liSlice == 0)) {
            dLastMax = ldFieldValue;
          }

          if (ldFieldValue > dLastMax) {
            iSliceHoldingMax = liSlice;
            dLastMax = ldFieldValue;
          }
        }
        // -----------------------------------------------------------------
      } catch (NoSuchFieldException ex) {
        IJ.log(getClass().getSimpleName() + ": " + ex.getMessage() + lsErrorID
            + new Exception().getStackTrace()[0].getLineNumber());
      } catch (SecurityException ex) {
        IJ.log(getClass().getSimpleName() + ": " + ex.getMessage() + lsErrorID
            + new Exception().getStackTrace()[0].getLineNumber());
      } catch (IllegalArgumentException ex) {
        IJ.log(getClass().getSimpleName() + ": " + lsErrorID + ex.getMessage()
            + new Exception().getStackTrace()[0].getLineNumber());
      } catch (IllegalAccessException ex) {
        IJ.log(ex.getMessage() + getClass().getSimpleName() + ": " + lsErrorID
            + new Exception().getStackTrace()[0].getLineNumber());
      }
      // -------------------------------------------------------------------
      daMeanOfArrayOrValueForThisField[liSlice] = ldFieldValue;
    }
    // ---------------------------------------------------------------------
    // Return a new Statistics instance based on the array
    // of all values this field holds from all of the rotation data.
    // ---------------------------------------------------------------------
    return new Statistics(daMeanOfArrayOrValueForThisField,
                          psNameOfOuterFieldContainingField);
  }

  /**
   *
   */
  public double[][][] d3d;
  /**
   *
   */
  public double[][] d2dElongated;

  /**
   *
   * @param pi
   * @param pd2d
   */
  public void store2dArraysInLocald3dArray(int pi,
                                           double[][] pd2d)
  {
    // ---------------------------------------------------
    // On the first iteration only, 
    // make an array of the desired length and in 3d.
    // ---------------------------------------------------
    if ((pi == 0)) {
      d3d = new double[iNumSlices][][];
    }
    // ---------------------------------------------------
    // Copy the passed 2d array into it sequentially.
    // ---------------------------------------------------
    d3d[pi] = new double[pd2d.length][];
    System.arraycopy(pd2d,
                     srcPos,
                     d3d[pi],
                     destPos,
                     pd2d.length);

  }

  /**
   *
   * @param piThisSlice
   * @param pd3d
   */
  public void store3dArraysInLocald3dArray(int piThisSlice,
                                           double[][][] pd3d)
  {
    // ---------------------------------------------------
    // On the first iteration only, 
    // make an array of the desired length and in 3d.
    // ---------------------------------------------------
    if ((piThisSlice == 0)) {
      d3d = new double[iNumSlices][][];
    }
    // ---------------------------------------------------
    // Copy the passed 3d array into it sequentially.
    // ---------------------------------------------------
    concatenateArray(pd3d,
                     d3d,
                     piThisSlice);

  }

  /**
   *
   * @param pd2d
   */
  public void convert2DArrayToDoubleArray(double[][] pd2d)
  {

    // Initialize the new array to be as long as all of the entries.
    int liLength = 0;
    for (int i = 0; i < pd2d.length; i++) {
      for (int j = 0; j < pd2d[i].length; j++) {
        liLength++;
      }
    }

    daElongated = new double[liLength];
    // ---------------------------------------------------
    // Copy the passed 2d array into it sequentially.
    // ---------------------------------------------------
    int liCount = 0;
    for (int m = 0; m < liLength; m++) {
      for (int k = 0; k < pd2d[m].length; k++) {

        daElongated[liCount] = pd2d[m][k];
        liCount++;
      }
    }

  }

  /**
   *
   * @param args
   */
  public static void main(String... args)
  {
//    int[] x = {5};
//    Array.newInstance(componentType,                      x);
    int rows = 3, cols = 2;
    Object matrix = Array.newInstance(int.class,
                                      rows,
                                      cols);
    Object row0 = Array.get(matrix,
                            0);
    Object row2 = Array.get(matrix,
                            2);

    Array.setInt(row0,

                 0,
                 1);
    Array.setInt(row0,

                 1,
                 2);
    //Array.setInt(row2,
    //0,
    //3);
    Array.setInt(row2,

                 1,
                 4);

    for (int row = 0;
        row < rows;
        row++) {
      for (int col = 0; col < cols; col++) {
        out.format("matrix[%d][%d] = %d%n",
                   row,
                   col,
                   ((int[][]) matrix)[row][col]);
      }
    }
  }

  /**
   *
   * @param pClass
   * @param psFieldName
   * @return
   */
  boolean hasField(Class pClass,
                   String psFieldName)
  {
    // Iterate through all fields in class and 
    // and find the passed Field.
    Field[] fields = pClass.getDeclaredFields();

    for (Field field : fields) {
      if (field.getName() == psFieldName) {
        return true;
      }
    }
    return false;
  }
  /**
   *
   */
  final static public boolean VARS = true, NOTVARS = false;
  /**
   *
   */
  /**
   *
   */
  final static public long DOUBLE = 1, INT = 2;

  /**
   * Calculates averages and statistics for each field, and loads them into the
   * class data processor. Assumes the {@link #dataProcessor} has been
   * initialized and the {@link #aRotationData array} of data objects is full to
   * the number of liNumSlices passed in.
   *
   */
  public void calculateAverages()
  {
    //---------------------------------------------------------------
    // Calculate averages and load them into the class data processor.
    //---------------------------------------------------------------
    Statistics lStats = getStatsForAllOccurrencesOfField("dMeanSIZEs");
    dataProcessor.data.dMeanSIZEs = lStats.dMean;
    dataProcessor.data.dStdDevSIZE = lStats.dStdDev;
    // -------------------------------------------------------------------
    // Calculate the standard deviation for foreground pixels because
    // with each rotation we may be seeing a difference in pixels 
    // rendered. The number of fg pixels is registered in the vars 
    // instance in each data processor. There is also a Statistics 
    // instance in the DataProcessor for fg pixels. Initiate it 
    // using all of the values for foreground pixels from the collection 
    // of DataProcessors vars.dtotalForeground
    // -------------------------------------------------------------------
    dataProcessor.data.statsForegroundPixels
        = getStatsForAllOccurrencesOfField(null,
                                           "dTotalForegroundPixels",
                                           VARS,
                                           DOUBLE);

    lStats = getStatsForAllOccurrencesOfField("iMinSIZE",
                                              INT);
    dataProcessor.data.iMinSIZE = (int) lStats.dMin;
    // -------------------------------------------------------------------
    lStats = getStatsForAllOccurrencesOfField("iMaxSIZE",
                                              INT);
    dataProcessor.data.iMaxSIZE = (int) lStats.dMax;
    //------------------------------------------------------------------------    
    if (!dataProcessor.scan.vars.isGray()
        && !dataProcessor.scan.vars.isMvsD()
        && !dataProcessor.scan.vars.isDlc()) {
      dataProcessor.data.statsCVForCountsOrSumsdeltaIAllGRIDs
          = getStatsForAllOccurrencesOfField(
              dataProcessor.data.statsCVForCountsOrSumsdeltaIAllGRIDs);
      // -----------------------------------------------------------------
      dataProcessor.data.statsCVForOMEGACountAllGRIDs
          = getStatsForAllOccurrencesOfField(
              dataProcessor.data.statsCVForOMEGACountAllGRIDs);
    }
    // ----------------------------------------------------------------------
    if (dataProcessor.scan.vars.isSLAC() || dataProcessor.scan.vars.isDlc()) {
      lStats = getStatsForAllOccurrencesOfField("iPixelsToSlideHorizontally",
                                                VARS,
                                                INT);
      dataProcessor.scan.vars.iPixelsToSlideHorizontally = (int) lStats.dMean;

      lStats = getStatsForAllOccurrencesOfField("iPixelsToSlideVertically",
                                                VARS,
                                                INT);
      dataProcessor.scan.vars.iPixelsToSlideVertically = (int) lStats.dMean;
    }
    // -----------------------------------------------------------------------
    if (!dataProcessor.scan.vars.isSLAC()
        && !dataProcessor.scan.vars.isMvsD()) {
      sumFAvgCoverMap();
    }
    if (!(dataProcessor.scan.vars.isMvsD()
        && !dataProcessor.scan.vars.isGray())) {
      sumDbMap();
    }

    sumDmMap();

    if (dataProcessor.scan.vars.bDoSmoothed) {
      sumFSBMap();
      sumF_SS_Map();
      sumFSMap();
    }

    if (dataProcessor.scan.vars.bDoFilterMinCover) {
      sumFMinCoverMap();
      sumFMaxCoverMap();
      if (dataProcessor.scan.vars.bDoSmoothed) {
        sumF_SS_MinCoverMap();
        sumFsBMinCoverMap();
        sumF_SS_MaxCoverMap();
        sumFsBMaxCoverMap();
      }
    }
    sumLacMaps();
  }

  /**
   * Puts data into the {@link #mapFMaxCover map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void sumFMaxCoverMap()
  {
//    Statistics lStats = getStatsForAllOccurrencesOfField(s_cFMaxCover,
//                                                         dFractalDimension);
//    dataProcessor.data.cFMaxCover.dFractalDimension = lStats.dMean;
//    dataProcessor.data.cFMaxCover.dRSq = INF;
//    dataProcessor.data.cFMaxCover.dStdErr = INF;
//    dataProcessor.data.cFMaxCover.dYIntercept = INF;
//    dataProcessor.data.cFMaxCover.dPrefactor = INF;
//    // ========================================================================= 
//    lStats = getStatsForAllOccurrencesOfField("dSlopeCvSqPlus1FMax");
//    dataProcessor.data.dSlopeCvSqPlus1FMax = lStats.dMean;
//    // ========================================================================= 
//    lStats = getStatsForAllOccurrencesOfField("dalambdaFMaxCvSqPixPerSIZE");
//    int length = daMeanOfArrayOrValueForThisField.length;
//    dataProcessor.data.dalambdaFMaxCvSqPixPerSIZE = new double[length];
//    System.arraycopy(daMeanOfArrayOrValueForThisField,
//                     srcPos,
//                     dataProcessor.data.dalambdaFMaxCvSqPixPerSIZE,
//                     destPos,
//                     length);
  }

  /**
   *
   * @return
   */
  double[][][][] combineMassSets()
  {
    double[][][][] li4d = new double[iNumSlices][][][];
    for (int liSlice = 0; liSlice < iNumSlices; liSlice++) {
      int liLength = aRotationData[liSlice].//
          Dp.scan.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs.length;
      li4d[liSlice] = new double[liLength][][];
      System.arraycopy(
          aRotationData[liSlice].Dp.scan.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs,
          srcPos,
          li4d[liSlice],
          destPos,
          liLength);
    }
    return li4d;
  }

  /**
   *
   * @return
   */
  int[][][] combineGridSets()
  {
    int[][][] li3d = new int[iNumSlices][][];
    for (int liSlice = 0; liSlice < iNumSlices; liSlice++) {
      int length = aRotationData[liSlice].Dp.scan.gridSet.i2dSizes.length;
      li3d[liSlice] = new int[length][];
      System.arraycopy(aRotationData[liSlice].Dp.scan.gridSet.i2dSizes,
                       srcPos,
                       li3d[liSlice],
                       destPos,
                       length);
    }
    return li3d;
  }

  public int[][] concatenateGridSets()
  {
    // ===================================================================
    // Make a new array by combining all of the arrays from the dataset.
    // ===================================================================
    int[][][] liaAllGridSets = combineGridSets();
    //int liNumberOf1DArrays = numberOf1DArrays(liaAllGridSets);
    // -------------------------------------------------------------------
    // Copy each 2d array in the 3d array into the new, long 2d array.
    // -------------------------------------------------------------------
    return convert3dArrayTo2dArray(liaAllGridSets);
  }

  /**
   *
   * @return
   */
  public double[][][] concatenateMassSets()
  {
    // ===================================================================
    // Make a new array by combining all of the arrays from the dataset.
    // ===================================================================
    double[][][][] ld4dAllMassSets = combineMassSets();
    //int liNumberOf1DArrays = numberOf1DArrays(liaAllGridSets);
    // -------------------------------------------------------------------
    // Copy each 2d array in the 3d array into the new, long 2d array.
    // -------------------------------------------------------------------
    return concatenate3DArraysIn4DArray(ld4dAllMassSets);
  }

  /**
   * Puts data into the {@link #mapFMinCover}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void sumFMinCoverMap()
  {
    // ====================================================================
    // Make a new 2d array by concatenating sizes arrays from the dataset.
    // ====================================================================
    int[][] li2dAllSizesSets = concatenateGridSets();
    // ====================================================================
    // Make a new 3d array by combining the mass arrays from the dataset.
    // ====================================================================
    double[][][] liaAllMasses = concatenateMassSets();
    // --------------------------------------------------------------------
    // 
    // --------------------------------------------------------------------
    dataProcessor.storeFractalDimensionAndStatsForFMinAndFMaxCovers(
        liaAllMasses,
        li2dAllSizesSets,
        dataProcessor.scan.vars.bCheckPixRatio);

    // ===================================================================
//    Statistics lStats = getStatsForAllOccurrencesOfField(s_cFMinCover,
//                                                         dFractalDimension);
//    dataProcessor.data.cFMinCover.dFractalDimension = lStats.dMean;
//    dataProcessor.data.cFMinCover.dRSq = INF;
//    dataProcessor.data.cFMinCover.dStdErr = INF;
//    // =========================================================================
//    lStats = getStatsForAllOccurrencesOfField(s_cFMinCover,
//                                              "dPrefactor");
//    dataProcessor.data.cFMinCover.dPrefactor = lStats.dMean;
//    dataProcessor.data.cFMinCover.dYIntercept = INF;
//    // =========================================================================
//    lStats = getStatsForAllOccurrencesOfField("dSlopeCvSqPlus1FMin");
//    dataProcessor.data.dSlopeCvSqPlus1FMin = lStats.dMean;
//    // ========================================================================= 
//    //  There is a collection of double [] for this.
//    lStats = getStatsForAllOccurrencesOfField("dalambdaFMinCvSqPixPerSIZE");
//    int length = daMeanOfArrayOrValueForThisField.length;
//    dataProcessor.data.dalambdaFMinCvSqPixPerSIZE = new double[length];
//    System.arraycopy(daMeanOfArrayOrValueForThisField,
//                     srcPos,
//                     dataProcessor.data.dalambdaFMinCvSqPixPerSIZE,
//                     destPos,
//                     length);
  }

  /**
   * Puts data into the {@link #mapF_SS_ map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void sumF_SS_Map()
  {
    dataProcessor.data.statsDB_FSS_ForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsDB_FSS_ForSlice);

    // =========================================================================
    // The number of sizes to report for the average of average covers.
    // -------------------------------------------------------------------------
    getStatsForAllOccurrencesOfField("d2dSIZEsForF_SS_AtSIZEOnGRID");
    int length = d2dElongated.length;
    dataProcessor.data.d2dSIZEsForF_SS_AtSIZEOnGRID = new double[length][];
    System.arraycopy(d2dElongated,
                     srcPos,
                     dataProcessor.data.d2dSIZEsForF_SS_AtSIZEOnGRID,
                     destPos,
                     length);
    dataProcessor.data.statsDBFSSForSliceSizes
        = new Statistics(s_statsDBFSSForSliceSizes);
    dataProcessor.data.statsDBFSSForSliceSizes.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.statsDBFSSForSliceSizes.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.statsDBFSSForSliceSizes.dMin
        = minInArray(d2dElongated);
    dataProcessor.data.statsDBFSSForSliceSizes.dStdDev
        = stdDevForArrayLengths(d2dElongated);
// =========================================================================
    Statistics lStats = getStatsForAllOccurrencesOfField(
        "optimizedRSqForDB_FSS_");
    dataProcessor.data.optimizedDB_FSS_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedDB_FSS_;
    dataProcessor.data.optimizedRSqForDB_FSS_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedRSqForDB_FSS_;
    dataProcessor.data.optimizedSEForDB_FSS_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedSEForDB_FSS_;
    dataProcessor.data.optimizedYintForDB_FSS_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedYintForDB_FSS_;
    // =========================================================================    
    dataProcessor.data.statsLLMeanCvSqsF_SS_ForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsF_SS_ForSlice);
    // ------------------------------------------------------------------------   
    dataProcessor.data.statsLLSlopeCvSqPlus1F_SS_ForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLSlopeCvSqPlus1F_SS_ForSlice);
    // =========================================================================
  }

  /**
   * Puts data into the {@link #mapF_SS_ map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void sumFSMap()
  {
    // =========================================================================    
    dataProcessor.data.statsDB_FS_ForSlice = getStatsForAllOccurrencesOfField(
        dataProcessor.data.statsDB_FS_ForSlice);
    // =========================================================================
    // The number of sizes to report for the average of average covers.
    // -------------------------------------------------------------------------
    getStatsForAllOccurrencesOfField("d2dSizesForFSAtSizeOnGRID");
    int length = d2dElongated.length;
    dataProcessor.data.d2dSizesForFSAtSizeOnGRID = new double[length][];
    System.arraycopy(d2dElongated,
                     srcPos,
                     dataProcessor.data.d2dSizesForFSAtSizeOnGRID,
                     destPos,
                     length);
    dataProcessor.data.statsDBFSForSliceSizes
        = new Statistics(s_statsDBFSForSliceSizes);
    dataProcessor.data.statsDBFSForSliceSizes.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.statsDBFSForSliceSizes.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.statsDBFSForSliceSizes.dMin
        = minInArray(d2dElongated);
    dataProcessor.data.statsDBFSForSliceSizes.dStdDev
        = stdDevForArrayLengths(d2dElongated);
    // =========================================================================
    getStatsForAllOccurrencesOfField("optimizedRSqForDB_FS");
    dataProcessor.data.optimizedDBFS
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedDBFS;
    dataProcessor.data.optimizedRSqForDB_FS
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedRSqForDB_FS;
    dataProcessor.data.optimizedSEForDB_FS
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedSEForDB_FS;
    dataProcessor.data.optimizedYintForDB_FS
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedYintForDB_FS;
    // =========================================================================    
    dataProcessor.data.statsLLMeanCvSqsFSForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsFSForSlice);
    // -------------------------------------------------------------------------    
    dataProcessor.data.statsLLSlopeCvSqPlus1FSForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLSlopeCvSqPlus1FSForSlice);
    // =========================================================================
  }

  /**
   * Puts data into the {@link #mapFsB map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void sumFSBMap()
  {
    // =========================================================================
    dataProcessor.data.statsDB_FSB_ForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsDB_FSB_ForSlice);
    // -----------------------------------------------------------------------
    // Search the collection of data objects and the 
    // find the index of the sample with the highest r sq for the Db, then 
    // store the corresponding values for db, etc. using the index 
    // to retrieve the individual fields.
    // -----------------------------------------------------------------------
    getStatsForAllOccurrencesOfField("optimizedRSqForDB_FSB_");
    dataProcessor.data.optimizedDB_FSB_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedDB_FSB_;
    dataProcessor.data.optimizedRSqForDB_FSB_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedRSqForDB_FSB_;
    dataProcessor.data.optimizedSEForDB_FSB_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedSEForDB_FSB_;
    dataProcessor.data.optimizedYintForDB_FSB_
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedYintForDB_FSB_;
    // =========================================================================
    // The number of sizes to report for the average of average covers.
    // -------------------------------------------------------------------------
    getStatsForAllOccurrencesOfField("d2dSIZEsForFsBAtSIZEOnGRID");
    int length = d2dElongated.length;
    dataProcessor.data.d2dSIZEsForFsBAtSIZEOnGRID = new double[length][];
    System.arraycopy(d2dElongated,
                     srcPos,
                     dataProcessor.data.d2dSIZEsForFsBAtSIZEOnGRID,
                     destPos,
                     length);
    dataProcessor.data.statsDbFsbForSliceSizes
        = new Statistics(s_statsDbFsbForSliceSizes);
    dataProcessor.data.statsDbFsbForSliceSizes.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.statsDbFsbForSliceSizes.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.statsDbFsbForSliceSizes.dMin
        = minInArray(d2dElongated);
    dataProcessor.data.statsDbFsbForSliceSizes.dStdDev
        = stdDevForArrayLengths(d2dElongated);
    // =========================================================================
    dataProcessor.data.statsLLMeanCvSqsFsBForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsFsBForSlice);
    //------------------------------------------------------------------------    
    dataProcessor.data.statsLLSlopeCvSqPlus1FsBForSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLSlopeCvSqPlus1FsBForSlice);
  }

  /**
   * Puts data into the {@link #mapDm map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void sumDmMap()
  {
    dataProcessor.data.statsDmAtSlice = getStatsForAllOccurrencesOfField(
        dataProcessor.data.statsDmAtSlice);
    // =========================================================================
    getStatsForAllOccurrencesOfField("optimizedRSqForDm");
    dataProcessor.data.optimizedDm
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedDm;
    dataProcessor.data.optimizedRSqForDm
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedRSqForDm;
    dataProcessor.data.optimizedSEForDm
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedSEForDm;
    dataProcessor.data.optimizedYintForDm
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedYintForDm;
    // =========================================================================
    dataProcessor.data.statsLLMeanCvSqsAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsAtSlice);

    if (dataProcessor.scan.vars.isDlc()) {
//      int length = pDp.data.daPrefactorForDLnc_.length;
//      dataProcessor.data.daPrefactorForDLnc_ = new double[length];
//      System.arraycopy(pDp.data.daPrefactorForDLnc_,
//                       srcPos,
//                       dataProcessor.data.daPrefactorForDLnc_,
//                       destPos,
//                       length);
    } else {

      Statistics lStats = getStatsForAllOccurrencesOfField(
          "dLLPrefactorDmForSlice");
      dataProcessor.data.dLLPrefactorDmForSlice = lStats.dMean;
    }

    if (!(dataProcessor.scan.vars.isMvsD()
        || dataProcessor.scan.vars.isDlc())) {

      dataProcessor.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice
          = getStatsForAllOccurrencesOfField(
              dataProcessor.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice);
    }
    // =========================================================================

  }

  /**
   * Puts data into the fractal dimension {@link #mapDB (Db) map}.
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
   * @param pDp
   */
  public void sumDbMap()
  {

    dataProcessor.data.statsDBAtSlice = getStatsForAllOccurrencesOfField(
        dataProcessor.data.statsDBAtSlice);
    // .........................................................................
    // call this method to set the index of the highest r sqrd value 
    // for the Db
    // .........................................................................
    getStatsForAllOccurrencesOfField("optimizedRSqForDB");
    dataProcessor.data.optimizedRSqForDB
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedRSqForDB;
    dataProcessor.data.optimizedDB
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedDB;
    dataProcessor.data.optimizedSEForDB
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedSEForDB;
    dataProcessor.data.optimizedYintForDB
        = aRotationData[iSliceHoldingMax].Dp.data.optimizedYintForDB;
    // .....................................................................
    // Load the prefactor lacunarity value; lacunarity for other types
    // of lacunarity calculations are loaded in the lac map; this one is
    // loaded here because it depends on the fractal dimension itself.
    // .....................................................................
    if ((dataProcessor.scan.vars.isDlc())) {
      //     No average value is given for this.
      //     The summary string writer puts null in the field 
      //     for pDp.data.daPrefactorForDlcPerPixel.length so 
      //     it is left alone in this method.
    } else {
      Statistics lStats
          = getStatsForAllOccurrencesOfField("dLLPrefactorDBForSlice");
      dataProcessor.data.dLLPrefactorDBForSlice = lStats.dMean;
    }
  }

  /**
   * Puts data into the filtered average cover {@link #mapFAvgCover map}. The
   * headings keys are of type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void sumFAvgCoverMap()
  {
    // =========================================================================
    // Calculate the average fractal dimension over all rotations,
    // from the averages that were found from all grids for each rotation.
    // Just to be clear, then, if we did 3 rotations, and each had 2 grids, 
    // there would be an average , a, over 2 grids for each of the three 
    // rotations (i.e., a1, a2, and a3). Then, the value here is the average of 
    // (a1, a2, and a3).  The rsq and other stats are not calculated, 
    // because they are relevant to the original averages but not the 
    // overall average of averages.
    // -------------------------------------------------------------------------
    Statistics lStats
        = getStatsForAllOccurrencesOfField(s_fsCountsDavg,
                                           dFractalDimension);
    dataProcessor.data.fsCountsDavg.dFractalDimension = lStats.dMean;
    dataProcessor.data.fsCountsDavg.dRSq = INF;
    dataProcessor.data.fsCountsDavg.dStdErr = INF;
    dataProcessor.data.fsCountsDavg.dYIntercept = INF;
    // =========================================================================
    // Feeding this in automatically uses the dMean fields of the 
    // original objects as the basis
    // for calculating a new statistics object.
    dataProcessor.data//
        .statsLLisAlsoLAMBDAFromlambdaCvSqsForPixAtSIZEsFAvgCover
        = getStatsForAllOccurrencesOfField(dataProcessor.data//
            .statsLLisAlsoLAMBDAFromlambdaCvSqsForPixAtSIZEsFAvgCover);
    //--------------------------------------------------------------------------
    // Here we are finding the average over all rotations, 
    // of the average value of slope 
    // lacunarity that was found for each rotation.
    // -------------------------------------------------------------------------
    lStats = getStatsForAllOccurrencesOfField(
        "fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover",
        dFractalDimension);
    dataProcessor.data.fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover//
        .dFractalDimension = lStats.dMean;
    // =========================================================================
    // The number of sizes to report for the average of average covers.
    // -------------------------------------------------------------------------
    getStatsForAllOccurrencesOfField(s_fsCountsDavg,
                                     "daSizes");
    dataProcessor.data.fsCountsDavg.daSizesStats
        = new Statistics(s_daSizesStats);
    dataProcessor.data.fsCountsDavg.daSizesStats.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.fsCountsDavg.daSizesStats.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.fsCountsDavg.daSizesStats.dMin
        = minInArray(d2dElongated);
    dataProcessor.data.fsCountsDavg.daSizesStats.dStdDev
        = stdDevForArrayLengths(d2dElongated);
    // =========================================================================
    dataProcessor.data.fsCountsDavg.dPrefactor = INF;
  }

  /**
   * Puts data into the {@link #mapFsBFMaxCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private void sumFsBMaxCoverMap()
  {
    Statistics lStats = getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                                         "dDB_F_SB");
    dataProcessor.data.cFMaxCover.dDB_F_SB = lStats.dMean;
    dataProcessor.data.cFMaxCover.dRSqDB_F_SB = INF;
    dataProcessor.data.cFMaxCover.dYintForDB_F_SB = INF;
    dataProcessor.data.cFMaxCover.dSEForDB_F_SB = INF;
    // =========================================================================
    // Get the mean for all means, in one array.
    // -------------------------------------------------------------------------
    getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                     "daF_SB_CvSq");
    int length = daMeanOfArrayOrValueForThisField.length;
    dataProcessor.data.cFMaxCover.daF_SB_CvSq = new double[length];
    System.arraycopy(daMeanOfArrayOrValueForThisField,
                     srcPos,
                     dataProcessor.data.cFMaxCover.daF_SB_CvSq,
                     destPos,
                     length);
    //-------------------------------------------------------------------------
    lStats = getStatsForAllOccurrencesOfField("dSlopeCvSqPlus1FMaxsB");
    dataProcessor.data.dSlopeCvSqPlus1FMaxsB = lStats.dMean;
    // =========================================================================
    // 
    // -------------------------------------------------------------------------
    getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                     "daF_SB_SIZEs");
    dataProcessor.data.cFMaxCover.daF_SB_SIZEsStats
        = new Statistics(s_daF_SB_SIZEsStats);
    dataProcessor.data.cFMaxCover.daF_SB_SIZEsStats.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.cFMaxCover.daF_SB_SIZEsStats.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.cFMaxCover.daF_SB_SIZEsStats.dMin
        = minInArray(d2dElongated);
    dataProcessor.data.cFMaxCover.daF_SB_SIZEsStats.dStdDev
        = stdDevForArrayLengths(d2dElongated);
//--------------------------------------------------------------------------
    lStats = getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                              "dPrefactorDB_F_SB");
    dataProcessor.data.cFMaxCover.dPrefactorDB_F_SB = lStats.dMean;

  }

  /**
   * Puts data into the {@link #mapF_SS_FMaxCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private void sumF_SS_MaxCoverMap()
  {
    Statistics lStats = getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                                         "dDB_F_SS");
    dataProcessor.data.cFMaxCover.dDB_F_SS = lStats.dMean;
    dataProcessor.data.cFMaxCover.dRSqForDB_F_SS = INF;
    dataProcessor.data.cFMaxCover.dYintForDB_F_SS = INF;
    dataProcessor.data.cFMaxCover.dSEForDB_F_SS = INF;
    // =========================================================================
    getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                     "daF_SS_CvSq");
    int length = daMeanOfArrayOrValueForThisField.length;
    dataProcessor.data.cFMaxCover.daF_SS_CvSq = new double[length];
    System.arraycopy(daMeanOfArrayOrValueForThisField,
                     srcPos,
                     dataProcessor.data.cFMaxCover.daF_SS_CvSq,
                     destPos,
                     length);
    //-------------------------------------------------------------------------
    lStats = getStatsForAllOccurrencesOfField("dSlopeCvSqPlus1FMaxss");
    dataProcessor.data.dSlopeCvSqPlus1FMaxss = lStats.dMean;
    // =========================================================================
    getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                     "daF_SS_SIZEs");
    dataProcessor.data.cFMaxCover.daF_SS_SIZEsStats
        = new Statistics(s_daF_SS_SIZEsStats);
    dataProcessor.data.cFMaxCover.daF_SS_SIZEsStats.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.cFMaxCover.daF_SS_SIZEsStats.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.cFMaxCover.daF_SS_SIZEsStats.dMin
        = minInArray(d2dElongated);
    // =========================================================================
    lStats = getStatsForAllOccurrencesOfField(s_cFMaxCover,
                                              "dPrefactorForDB_F_SS");
    dataProcessor.data.cFMaxCover.dPrefactorForDB_F_SS = lStats.dMean;

  }

  /**
   * Puts data into the {@link #mapF_SS_FMinCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private void sumF_SS_MinCoverMap()
  {
    Statistics lStats = getStatsForAllOccurrencesOfField(s_cFMinCover,
                                                         "dDB_F_SS");
    dataProcessor.data.cFMinCover.dDB_F_SS = lStats.dMean;
    dataProcessor.data.cFMinCover.dRSqForDB_F_SS = INF;
    dataProcessor.data.cFMinCover.dYintForDB_F_SS = INF;
    dataProcessor.data.cFMinCover.dSEForDB_F_SS = INF;
    // =========================================================================
    getStatsForAllOccurrencesOfField(s_cFMinCover,
                                     "daF_SS_CvSq");
    int length = daMeanOfArrayOrValueForThisField.length;
    dataProcessor.data.cFMinCover.daF_SS_CvSq = new double[length];
    System.arraycopy(daMeanOfArrayOrValueForThisField,
                     srcPos,
                     dataProcessor.data.cFMinCover.daF_SS_CvSq,
                     destPos,
                     length);
    //--------------------------------------------------------------------------
    lStats = getStatsForAllOccurrencesOfField("dSlopeCvSqPlus1FMinss");
    dataProcessor.data.dSlopeCvSqPlus1FMinss = lStats.dMean;
    // =========================================================================
    getStatsForAllOccurrencesOfField(s_cFMinCover,
                                     "daF_SS_SIZEs");
    dataProcessor.data.cFMinCover.daF_SS_SIZEsStats = new Statistics(
        s_daF_SS_SIZEsStats);
    dataProcessor.data.cFMinCover.daF_SS_SIZEsStats.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.cFMinCover.daF_SS_SIZEsStats.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.cFMinCover.daF_SS_SIZEsStats.dMin
        = minInArray(d2dElongated);

    // =========================================================================
    lStats = getStatsForAllOccurrencesOfField(s_cFMinCover,
                                              "dPrefactorForDB_F_SS");
    dataProcessor.data.cFMinCover.dPrefactorForDB_F_SS = lStats.dMean;
  }

  /**
   *
   * @param psFirstField
   * @param pd2daNameOfArray
   * @param pd2dToCopyInto
   */
  void storeElongatedArrayForAllOccurrencesOfField(String psFirstField,
                                                   String pd2daNameOfArray,
                                                   double[][] pd2dToCopyInto)
  {
    getStatsForAllOccurrencesOfField(psFirstField,
                                     pd2daNameOfArray);
    int length = d2dElongated.length;
    pd2dToCopyInto = new double[length][];

    System.arraycopy(d2dElongated,
                     srcPos,
                     pd2dToCopyInto,
                     destPos,
                     length);
  }

  void storeAllResultsSequentiallyForAllOccurrencesOfField(String psFirstField,
                                                           String pdaNameOfArray,
                                                           double[] pd2dToCopyInto)
  {
    getStatsForAllOccurrencesOfField(psFirstField,
                                     pdaNameOfArray);
    int length = daElongated.length;
    pd2dToCopyInto = new double[length];

    System.arraycopy(daElongated,
                     srcPos,
                     pd2dToCopyInto,
                     destPos,
                     length);

  }

  /**
   * Puts data into the {@link #mapFsBFMinCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private void sumFsBMinCoverMap()
  {

    // =========================================================================
    Statistics lStats = getStatsForAllOccurrencesOfField(s_cFMinCover,
                                                         "dDB_F_SB");
    dataProcessor.data.cFMinCover.dDB_F_SB = lStats.dMean;
    dataProcessor.data.cFMinCover.dRSqDB_F_SB = INF;
    dataProcessor.data.cFMinCover.dYintForDB_F_SB = INF;
    dataProcessor.data.cFMinCover.dSEForDB_F_SB = INF;
    // =========================================================================
    getStatsForAllOccurrencesOfField(s_cFMinCover,
                                     "daF_SB_CvSq");
    int length = daMeanOfArrayOrValueForThisField.length;
    dataProcessor.data.cFMinCover.daF_SB_CvSq = new double[length];
    System.arraycopy(daMeanOfArrayOrValueForThisField,
                     srcPos,
                     dataProcessor.data.cFMinCover.daF_SB_CvSq,
                     destPos,
                     length);
    //--------------------------------------------------------------------------
    lStats = getStatsForAllOccurrencesOfField("dSlopeCvSqPlus1FMinsB");
    dataProcessor.data.dSlopeCvSqPlus1FMinsB = lStats.dMean;
    // =========================================================================  
    getStatsForAllOccurrencesOfField(s_cFMinCover,
                                     "daF_SB_SIZEs");
    dataProcessor.data.cFMinCover.daF_SB_SIZEsStats
        = new Statistics(s_daF_SB_SIZEsStats);
    dataProcessor.data.cFMinCover.daF_SB_SIZEsStats.dNum
        = meanLengthOfArrays(d2dElongated);
    dataProcessor.data.cFMinCover.daF_SB_SIZEsStats.dMax
        = maxInArray(d2dElongated);
    dataProcessor.data.cFMinCover.daF_SB_SIZEsStats.dMin
        = minInArray(d2dElongated);
    // =========================================================================
    lStats = getStatsForAllOccurrencesOfField(s_cFMinCover,
                                              "dPrefactorDB_F_SB");
    dataProcessor.data.cFMinCover.dPrefactorDB_F_SB = lStats.dMean;

  }

  /**
   * Puts entries in the lacunarity data {@link #mapLac map}.
   */
  void sumLacMaps()
  {

    // this should match with the value for the mean of all
    // mean cv squareds; i.e., the mean of all uppercase Lambdas,
    // where uppercase Lambda is the mean
    // of all lowercase lambdas, which are cv squareds,
    // for all sizes at that grid
    dataProcessor.data.statsLLMeanCvSqsAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsAtSlice);

    if (!dataProcessor.scan.vars.isMvsD() && !dataProcessor.scan.vars.isDlc()) {

      dataProcessor.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice
          = getStatsForAllOccurrencesOfField(
              dataProcessor.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice);
      //------------------------------------------------------------------
      Statistics lStats = getStatsForAllOccurrencesOfField(
          s_fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover,
          dFractalDimension);
      dataProcessor.data.fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover.//
          dFractalDimension = lStats.dMean;
    }
    // ........................ LOAD EMPTY PLUS MASS INFO.......................
    if (!dataProcessor.scan.vars.isMvsD() && !dataProcessor.scan.vars.isGray()
        && !dataProcessor.scan.vars.isDlc()) {
      //-----------------------------------------------------------------
      dataProcessor.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID
          = getStatsForAllOccurrencesOfField(
              dataProcessor.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID);
    }
    if (!dataProcessor.scan.vars.isGray() && !dataProcessor.scan.vars.isDlc()) {
      // The value reported in the results file is the mean of the array,
      // so for this one we have to make one array in the summary file, 
      // where each element is the average from the slice.
      getStatsForAllOccurrencesOfField("daLambdaDAtGrid");
      dataProcessor.data.daLambdaDAtGrid = new double[iNumSlices];
      System.arraycopy(daMeanOfArrayOrValueForThisField,
                       srcPos,
                       dataProcessor.data.daLambdaDAtGrid,
                       destPos,
                       iNumSlices);
    }
    if (dataProcessor.scan.vars.iMaxFrequencies > 0) {
      sumProbabilityLacunarity(dataProcessor.scan.vars.isGray());
    }

  }

  /**
   * Puts data into the {@link #mapProb probabilities map}, {@link #mapPD
   * probability distribution map}, {@link #mapProbOverBins adjusted
   * probabilities map},and {@link #mapProb adjusted probability distribution
   * map}.
   * <p>
   * If the DataProcessor is from a grayscale analysis, calls
   * {@link #sumDataForOmegaForProbabilityLacunarity}.
   * <p>
   * The headings keys are of type {@link fraclac.writers.Headings.EnumLacData}.
   */
  void sumProbabilityLacunarity(boolean pbIsGray)
  {
    dataProcessor.data.statsLLMeanCvSqsUnweightedProbAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsUnweightedProbAtSlice);
    // =========================================================================
    dataProcessor.data.statsLLMeanCvSqsWeightedPDAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsWeightedPDAtSlice);
    // ========================================================================
    dataProcessor.data.statsLLMeanCvSqsOverBinsUnweightedProbAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsOverBinsUnweightedProbAtSlice);
    // ========================================================================
    dataProcessor.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice);
    // ========================================================================
    if (!pbIsGray) {
      sumDataForOmegaForProbabilityLacunarity();
    }

  }

  /**
   *
   * @param pDp DataProcessor
   */
  void sumDataForOmegaForProbabilityLacunarity()
  {
    dataProcessor.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice);
    // =========================================================================
    dataProcessor.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice);
    // =========================================================================
    dataProcessor.data.statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.//
            statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice);
    //--------------------------------------------------------------------------    
    dataProcessor.data.statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice
        = getStatsForAllOccurrencesOfField(
            dataProcessor.data.statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice);
  }

}
