package fraclac.writers;

import fraclac.analyzer.*;
import java.awt.Dimension;

/**
 * A data object that holds data for creating results files from fractal
 * analysis done with the FracLac plugin.
 *
 *
 * @author Audrey Karperien
 * @version FracLac 2014Jan $Rev: 243 $
 * @version Revision $Id: SummaryStringDataMapper.java 54 2013-02-03 08:45:01Z
 * audrey $
 *
 */
public class RotationStackData extends AllGsHeadings
{

  DataProcessor Dp;

  /**
   * Constructor initializes class variables using the passed DataProcessor
   * instance. The class variables updated are only those used for writing the
   * results files, so access with care. The class is intended to be used to
   * store multiple instances of summarized data and find average values, then
   * return an instance containing the average values.
   *
   * @param pDp
   * @param pRoi
   */
  public RotationStackData(DataProcessor pDp)
  {
    Dp = new DataProcessor();
    Dp.setUpToReceiveRotationData(pDp);

    Dp.data.dMeanSIZEs = pDp.data.dMeanSIZEs;
    Dp.data.iMinSIZE = pDp.data.iMinSIZE;
    Dp.data.iMaxSIZE = pDp.data.iMaxSIZE;
    Dp.data.dStdDevSIZE = pDp.data.dStdDevSIZE;

    if (!pDp.scan.vars.isGray() && !pDp.scan.vars.isMvsD()
        && !pDp.scan.vars.isDlc()) {
      Dp.data.statsCVForCountsOrSumsdeltaIAllGRIDs.dMean
          = (pDp.data.statsCVForCountsOrSumsdeltaIAllGRIDs.dMean);
      Dp.data.statsCVForCountsOrSumsdeltaIAllGRIDs.dMean
          = pDp.data.statsCVForCountsOrSumsdeltaIAllGRIDs.dMean;
      Dp.data.statsCVForOMEGACountAllGRIDs.dMean
          = pDp.data.statsCVForOMEGACountAllGRIDs.dMean;
    }

    if (pDp.scan.vars.isSLAC() || pDp.scan.vars.isDlc()) {
      Dp.scan.vars.iPixelsToSlideHorizontally
          = (pDp.scan.vars.iPixelsToSlideHorizontally);
      pDp.scan.vars.iPixelsToSlideVertically
          = (pDp.scan.vars.iPixelsToSlideVertically);
    }
    addGridSet(pDp);
    addMassSet(pDp);
    if (!pDp.scan.vars.isSLAC() && !pDp.scan.vars.isMvsD()) {
      makeFAvgCoverMap(pDp);
    }

    if (!(pDp.scan.vars.isMvsD() && !pDp.scan.vars.isGray())) {
      makeDbMap(pDp);
    }

    makeDmMap(pDp);

    if (pDp.scan.vars.bDoSmoothed) {
      makeFsBMap(pDp);
      makeF_SS_Map(pDp);
      makeFSMap(pDp);
    }

    if (pDp.scan.vars.bDoFilterMinCover) {
      makeFMinCoverMap(pDp);
      makeFMaxCoverMap(pDp);

      if (pDp.scan.vars.bDoSmoothed) {
        makeF_SS_MinCoverMap(pDp);
        makeFsBMinCoverMap(pDp);
        makeF_SS_MaxCoverMap(pDp);
        makeFsBMaxCoverMap(pDp);
      }
    }
    makeLacMaps(pDp);
  }

  /**
   * Puts data into the {@link #mapFMaxCover map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void makeFMaxCoverMap(final DataProcessor pDp)
  {
    // =========================================================================
    Dp.data.cFMaxCover.dFractalDimension
        = (pDp.data.cFMaxCover.dFractalDimension);
    Dp.data.cFMaxCover.dFractalDimension
        = (pDp.data.cFMaxCover.dFractalDimension);
    Dp.data.cFMaxCover.dRSq = (pDp.data.cFMaxCover.dRSq);
    Dp.data.cFMaxCover.dStdErr = (pDp.data.cFMaxCover.dStdErr);
    Dp.data.cFMaxCover.dYIntercept = (pDp.data.cFMaxCover.dYIntercept);
    // =========================================================================
    Dp.data.cFMaxCover.dPrefactor = (pDp.data.cFMaxCover.dPrefactor);
    // =========================================================================
    int liLength = (pDp.data.daSIZEsForFMax.length);
    Dp.data.daSIZEsForFMax = new double[liLength];
    System.arraycopy(pDp.data.daSIZEsForFMax,
                     0,
                     Dp.data.daSIZEsForFMax,
                     0,
                     liLength);
    // =========================================================================    
    liLength = pDp.data.dalambdaFMaxCvSqPixPerSIZE.length;
    Dp.data.dalambdaFMaxCvSqPixPerSIZE = new double[liLength];
    System.arraycopy(pDp.data.dalambdaFMaxCvSqPixPerSIZE,
                     srcPos,
                     Dp.data.dalambdaFMaxCvSqPixPerSIZE,
                     destPos,
                     liLength);
    Dp.data.dSlopeCvSqPlus1FMax = (pDp.data.dSlopeCvSqPlus1FMax);
    // =========================================================================    
  }
  int srcPos = 0, destPos = 0;

  /**
   * Puts data into the {@link #mapFMinCover}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void makeFMinCoverMap(final DataProcessor pDp)
  {
    Dp.data.cFMinCover.dFractalDimension
        = (pDp.data.cFMinCover.dFractalDimension);
    Dp.data.cFMinCover.dFractalDimension
        = (pDp.data.cFMinCover.dFractalDimension);
    Dp.data.cFMinCover.dRSq = (pDp.data.cFMinCover.dRSq);
    Dp.data.cFMinCover.dStdErr = (pDp.data.cFMinCover.dStdErr);
    // =========================================================================
    Dp.data.cFMinCover.dPrefactor = (pDp.data.cFMinCover.dPrefactor);
    Dp.data.cFMinCover.dYIntercept = (pDp.data.cFMinCover.dYIntercept);
    // =========================================================================
    int length = pDp.data.dalambdaFMinCvSqPixPerSIZE.length;
    Dp.data.dalambdaFMinCvSqPixPerSIZE = new double[length];
    System.arraycopy(pDp.data.dalambdaFMinCvSqPixPerSIZE,
                     srcPos,
                     Dp.data.dalambdaFMinCvSqPixPerSIZE,
                     destPos,
                     length);
    Dp.data.dSlopeCvSqPlus1FMin = (pDp.data.dSlopeCvSqPlus1FMin);
    // =========================================================================    
    length = pDp.data.daSIZEsForFMin.length;
    Dp.data.daSIZEsForFMin = new double[length];
    System.arraycopy(pDp.data.daSIZEsForFMin,
                     srcPos,
                     Dp.data.daSIZEsForFMin,
                     destPos,
                     length);
    // =========================================================================
  }

  /**
   * Puts data into the {@link #mapF_SS_ map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void makeF_SS_Map(DataProcessor pDp)
  {
    Dp.data.statsDB_FSS_ForSlice.dMean = (pDp.data.statsDB_FSS_ForSlice.dMean);
    Dp.data.statsDB_FSS_ForSlice.dStdDev
        = (pDp.data.statsDB_FSS_ForSlice.dStdDev);
    Dp.data.statsDB_FSS_ForSlice.dCV = (pDp.data.statsDB_FSS_ForSlice.dCV);
    // =========================================================================
    int length = pDp.data.d2dSIZEsForF_SS_AtSIZEOnGRID.length;
    Dp.data.d2dSIZEsForF_SS_AtSIZEOnGRID = new double[length][];
    System.arraycopy(pDp.data.d2dSIZEsForF_SS_AtSIZEOnGRID,
                     srcPos,
                     Dp.data.d2dSIZEsForF_SS_AtSIZEOnGRID,
                     destPos,
                     length);
    // =========================================================================
    Dp.data.optimizedDB_FSS_ = (pDp.data.optimizedDB_FSS_);
    Dp.data.optimizedRSqForDB_FSS_ = (pDp.data.optimizedRSqForDB_FSS_);
    Dp.data.optimizedSEForDB_FSS_ = (pDp.data.optimizedSEForDB_FSS_);
    Dp.data.optimizedYintForDB_FSS_ = (pDp.data.optimizedYintForDB_FSS_);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsF_SS_ForSlice.dMean
        = (pDp.data.statsLLMeanCvSqsF_SS_ForSlice.dMean);
    Dp.data.statsLLSlopeCvSqPlus1F_SS_ForSlice.dMean
        = (pDp.data.statsLLSlopeCvSqPlus1F_SS_ForSlice.dMean);
    // =========================================================================

  }

  /**
   * Puts data into the {@link #mapF_SS_ map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void makeFSMap(final DataProcessor pDp)
  {
    // =========================================================================
    Dp.data.statsDB_FS_ForSlice.dMean = (pDp.data.statsDB_FS_ForSlice.dMean);
    Dp.data.statsDB_FS_ForSlice.dStdDev = (pDp.data.statsDB_FS_ForSlice.dStdDev);
    Dp.data.statsDB_FS_ForSlice.dCV = (pDp.data.statsDB_FS_ForSlice.dCV);
    // =========================================================================
    Dp.data.d2dSizesForFSAtSizeOnGRID
        = copyArray(pDp.data.d2dSizesForFSAtSizeOnGRID);
    // =========================================================================
    Dp.data.optimizedDBFS = (pDp.data.optimizedDBFS);
    Dp.data.optimizedRSqForDB_FS = (pDp.data.optimizedRSqForDB_FS);
    Dp.data.optimizedSEForDB_FS = (pDp.data.optimizedSEForDB_FS);
    Dp.data.optimizedYintForDB_FS = (pDp.data.optimizedYintForDB_FS);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsFSForSlice.dMean
        = (pDp.data.statsLLMeanCvSqsFSForSlice.dMean);
    Dp.data.statsLLSlopeCvSqPlus1FSForSlice.dMean
        = (pDp.data.statsLLSlopeCvSqPlus1FSForSlice.dMean);
    // =========================================================================
  }

  /**
   * Puts data into the {@link #mapFsB map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void makeFsBMap(final DataProcessor pDp)
  {
    // =========================================================================
    Dp.data.statsDB_FSB_ForSlice.dMean = (pDp.data.statsDB_FSB_ForSlice.dMean);
    Dp.data.statsDB_FSB_ForSlice.dStdDev
        = (pDp.data.statsDB_FSB_ForSlice.dStdDev);
    Dp.data.statsDB_FSB_ForSlice.dCV = (pDp.data.statsDB_FSB_ForSlice.dCV);
    Dp.data.optimizedDB_FSB_ = (pDp.data.optimizedDB_FSB_);
    Dp.data.optimizedRSqForDB_FSB_ = (pDp.data.optimizedRSqForDB_FSB_);
    Dp.data.optimizedSEForDB_FSB_ = (pDp.data.optimizedSEForDB_FSB_);
    Dp.data.optimizedYintForDB_FSB_ = (pDp.data.optimizedYintForDB_FSB_);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsFsBForSlice.dMean
        = (pDp.data.statsLLMeanCvSqsFsBForSlice.dMean);
    Dp.data.statsLLSlopeCvSqPlus1FsBForSlice.dMean
        = (pDp.data.statsLLSlopeCvSqPlus1FsBForSlice.dMean);
    // =========================================================================
    int length = pDp.data.d2dSIZEsForFsBAtSIZEOnGRID.length;
    Dp.data.d2dSIZEsForFsBAtSIZEOnGRID = new double[length][];
    System.arraycopy(pDp.data.d2dSIZEsForFsBAtSIZEOnGRID,
                     srcPos,
                     Dp.data.d2dSIZEsForFsBAtSIZEOnGRID,
                     destPos,
                     length);
  }

  /**
   * Puts data into the {@link #mapDm map}. The headings keys are of type
   * {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void makeDmMap(final DataProcessor pDp)
  {

    Dp.data.statsDmAtSlice.dMean = (pDp.data.statsDmAtSlice.dMean);
    Dp.data.statsDmAtSlice.dStdDev = (pDp.data.statsDmAtSlice.dStdDev);
    Dp.data.statsDmAtSlice.dCV = (pDp.data.statsDmAtSlice.dCV);
    // =========================================================================
    Dp.data.optimizedDm = (pDp.data.optimizedDm);
    Dp.data.optimizedRSqForDm = (pDp.data.optimizedRSqForDm);
    Dp.data.optimizedSEForDm = (pDp.data.optimizedSEForDm);
    Dp.data.optimizedYintForDm = (pDp.data.optimizedYintForDm);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsAtSlice.dMean);

    if (pDp.scan.vars.isDlc()) {
      int length = pDp.data.daPrefactorForDLnc_.length;
      Dp.data.daPrefactorForDLnc_ = new double[length];
      System.arraycopy(pDp.data.daPrefactorForDLnc_,
                       srcPos,
                       Dp.data.daPrefactorForDLnc_,
                       destPos,
                       length);
    } else {
      Dp.data.dLLPrefactorDmForSlice = pDp.data.dLLPrefactorDmForSlice;
    }

    if (!(pDp.scan.vars.isMvsD() || pDp.scan.vars.isDlc())) {
      Dp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dMean
          = (pDp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dMean);
    }
    // =========================================================================
    Dp.data.dMeanSIZEs = (pDp.data.dMeanSIZEs);
    Dp.data.iMinSIZE = (pDp.data.iMinSIZE);
    Dp.data.iMaxSIZE = (pDp.data.iMaxSIZE);
    Dp.data.dStdDevSIZE = (pDp.data.dStdDevSIZE);
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
  public void makeDbMap(final DataProcessor pDp)
  {

    Dp.data.statsDBAtSlice.dMean = (pDp.data.statsDBAtSlice.dMean);
    //pDp.data.da
    // .........................................................................
    Dp.data.statsDBAtSlice.dCV = (pDp.data.statsDBAtSlice.dCV);
    // .........................................................................
    Dp.data.statsDBAtSlice.dStdDev = (pDp.data.statsDBAtSlice.dStdDev);
    Dp.data.optimizedDB = (pDp.data.optimizedDB);
    Dp.data.optimizedRSqForDB = (pDp.data.optimizedRSqForDB);
    Dp.data.optimizedSEForDB = (pDp.data.optimizedSEForDB);
    Dp.data.optimizedYintForDB = (pDp.data.optimizedYintForDB);
    // =========================================================================
    // .....................................................................
    // Load the prefactor lacunarity value; lacunarity for other types
    // of lacunarity calculations are loaded in the lac map; this one is
    // loaded here because it depends on the fractal dimension itself.
    // .....................................................................
    if ((pDp.scan.vars.isDlc())) {
      int length = pDp.data.daPrefactorForDlcPerPixel.length;
      Dp.data.daPrefactorForDlcPerPixel = new double[length];
      System.arraycopy(pDp.data.daPrefactorForDlcPerPixel,
                       srcPos,
                       Dp.data.daPrefactorForDlcPerPixel,
                       destPos,
                       length);
    } else {
      Dp.data.dLLPrefactorDBForSlice = pDp.data.dLLPrefactorDBForSlice;
    };
    // .....................................................................
    // Load key-value pairs for the box sizes used in the scan.
    // .....................................................................
    Dp.data.dMeanSIZEs = (pDp.data.dMeanSIZEs);
    Dp.data.iMinSIZE = (pDp.data.iMinSIZE);
    Dp.data.iMaxSIZE = (pDp.data.iMaxSIZE);
    Dp.data.dStdDevSIZE = (pDp.data.dStdDevSIZE);
  }

  /**
   * Puts data into the filtered average cover {@link #mapFAvgCover map}. The
   * headings keys are of type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  void makeFAvgCoverMap(final DataProcessor pDp)
  {
    // =========================================================================
    Dp.data.fsCountsDavg = new FracStats(pDp.data.fsCountsDavg);
    // =========================================================================
    Dp.data.statsLLisAlsoLAMBDAFromlambdaCvSqsForPixAtSIZEsFAvgCover.dMean
        = (pDp.data.statsLLisAlsoLAMBDAFromlambdaCvSqsForPixAtSIZEsFAvgCover//
        .dMean);
    Dp.data.fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover
        = new FracStats(pDp.data.fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover);
    // =========================================================================    
    Dp.data.dStdDevSIZE = (pDp.data.dStdDevSIZE);
    // =========================================================================

  }

  /**
   * Puts data into the {@link #mapFsBFMaxCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private void makeFsBMaxCoverMap(DataProcessor pDp)
  {
    Dp.data.cFMaxCover.dDB_F_SB = (pDp.data.cFMaxCover.dDB_F_SB);
    Dp.data.cFMaxCover.dDB_F_SB = (pDp.data.cFMaxCover.dDB_F_SB);
    Dp.data.cFMaxCover.dRSqDB_F_SB = (pDp.data.cFMaxCover.dRSqDB_F_SB);
    Dp.data.cFMaxCover.dYintForDB_F_SB = (pDp.data.cFMaxCover.dYintForDB_F_SB);
    Dp.data.cFMaxCover.dSEForDB_F_SB = (pDp.data.cFMaxCover.dSEForDB_F_SB);
    // =========================================================================
    int length = pDp.data.cFMaxCover.daF_SB_CvSq.length;
    Dp.data.cFMaxCover.daF_SB_CvSq = new double[length];
    System.arraycopy(pDp.data.cFMaxCover.daF_SB_CvSq,
                     srcPos,
                     Dp.data.cFMaxCover.daF_SB_CvSq,
                     destPos,
                     length);
    Dp.data.dSlopeCvSqPlus1FMaxsB = (pDp.data.dSlopeCvSqPlus1FMaxsB);
    // =========================================================================
    length = pDp.data.cFMaxCover.daF_SB_SIZEs.length;
    Dp.data.cFMaxCover.daF_SB_SIZEs = new double[length];
    System.arraycopy(pDp.data.cFMaxCover.daF_SB_SIZEs,
                     srcPos,
                     Dp.data.cFMaxCover.daF_SB_SIZEs,
                     destPos,
                     length);
    Dp.data.cFMaxCover.dPrefactorDB_F_SB
        = (pDp.data.cFMaxCover.dPrefactorDB_F_SB);

  }

  /**
   * Puts data into the {@link #mapF_SS_FMaxCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private void makeF_SS_MaxCoverMap(final DataProcessor pDp)
  {

    Dp.data.cFMaxCover.dDB_F_SS = (pDp.data.cFMaxCover.dDB_F_SS);
    Dp.data.cFMaxCover.dRSqForDB_F_SS = (pDp.data.cFMaxCover.dRSqForDB_F_SS);
    Dp.data.cFMaxCover.dYintForDB_F_SS = (pDp.data.cFMaxCover.dYintForDB_F_SS);
    Dp.data.cFMaxCover.dSEForDB_F_SS = (pDp.data.cFMaxCover.dSEForDB_F_SS);
    // =========================================================================
    int length = pDp.data.cFMaxCover.daF_SS_CvSq.length;
    Dp.data.cFMaxCover.daF_SS_CvSq = new double[length];
    System.arraycopy(pDp.data.cFMaxCover.daF_SS_CvSq,
                     srcPos,
                     Dp.data.cFMaxCover.daF_SS_CvSq,
                     destPos,
                     length);
    Dp.data.dSlopeCvSqPlus1FMaxss = (pDp.data.dSlopeCvSqPlus1FMaxss);
    // =========================================================================
    length = pDp.data.cFMaxCover.daF_SS_SIZEs.length;
    Dp.data.cFMaxCover.daF_SS_SIZEs = new double[length];
    System.arraycopy(pDp.data.cFMaxCover.daF_SS_SIZEs,
                     srcPos,
                     Dp.data.cFMaxCover.daF_SS_SIZEs,
                     destPos,
                     length);
    // =========================================================================
    Dp.data.cFMaxCover.dPrefactorForDB_F_SS
        = (pDp.data.cFMaxCover.dPrefactorForDB_F_SS);

  }

  /**
   * Puts data into the {@link #mapF_SS_FMinCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private void makeF_SS_MinCoverMap(final DataProcessor pDp)
  {

    Dp.data.cFMinCover.dDB_F_SS = (pDp.data.cFMinCover.dDB_F_SS);
    Dp.data.cFMinCover.dDB_F_SS = (pDp.data.cFMinCover.dDB_F_SS);
    Dp.data.cFMinCover.dRSqForDB_F_SS = (pDp.data.cFMinCover.dRSqForDB_F_SS);
    Dp.data.cFMinCover.dYintForDB_F_SS = (pDp.data.cFMinCover.dYintForDB_F_SS);
    Dp.data.cFMinCover.dSEForDB_F_SS = (pDp.data.cFMinCover.dSEForDB_F_SS);
    // =========================================================================
    int length = pDp.data.cFMinCover.daF_SS_CvSq.length;
    Dp.data.cFMinCover.daF_SS_CvSq = new double[length];
    System.arraycopy(pDp.data.cFMinCover.daF_SS_CvSq,
                     srcPos,
                     Dp.data.cFMinCover.daF_SS_CvSq,
                     destPos,
                     length);
    Dp.data.dSlopeCvSqPlus1FMinss = (pDp.data.dSlopeCvSqPlus1FMinss);
    // =========================================================================
    length = pDp.data.cFMinCover.daF_SS_SIZEs.length;
    Dp.data.cFMinCover.daF_SS_SIZEs = new double[length];
    System.arraycopy(pDp.data.cFMinCover.daF_SS_SIZEs,
                     srcPos,
                     Dp.data.cFMinCover.daF_SS_SIZEs,
                     destPos,
                     length);
    // =========================================================================
    Dp.data.cFMinCover.dPrefactorForDB_F_SS
        = (pDp.data.cFMinCover.dPrefactorForDB_F_SS);

  }

  /**
   * Puts data into the {@link #mapFsBFMinCover map}. The headings keys are of
   * type {@link fraclac.writers.Headings.EnumDataFile}.
   */
  private
      void makeFsBMinCoverMap(final DataProcessor pDp)
  {

    // =========================================================================
    Dp.data.cFMinCover.dDB_F_SB = (pDp.data.cFMinCover.dDB_F_SB);
    Dp.data.cFMinCover.dRSqDB_F_SB = (pDp.data.cFMinCover.dRSqDB_F_SB);
    Dp.data.cFMinCover.dYintForDB_F_SB = (pDp.data.cFMinCover.dYintForDB_F_SB);
    Dp.data.cFMinCover.dSEForDB_F_SB = (pDp.data.cFMinCover.dSEForDB_F_SB);
    // =========================================================================
    int length = pDp.data.cFMinCover.daF_SB_CvSq.length;
    Dp.data.cFMinCover.daF_SB_CvSq = new double[length];
    System.arraycopy(pDp.data.cFMinCover.daF_SB_CvSq,
                     srcPos,
                     Dp.data.cFMinCover.daF_SB_CvSq,
                     destPos,
                     length);
    Dp.data.dSlopeCvSqPlus1FMinsB = (pDp.data.dSlopeCvSqPlus1FMinsB);
    // =========================================================================
    length = pDp.data.cFMinCover.daF_SB_SIZEs.length;
    Dp.data.cFMinCover.daF_SB_SIZEs = new double[length];
    System.arraycopy(pDp.data.cFMinCover.daF_SB_SIZEs,
                     srcPos,
                     Dp.data.cFMinCover.daF_SB_SIZEs,
                     destPos,
                     length);
    // =========================================================================
    Dp.data.cFMinCover.dPrefactorDB_F_SB
        = (pDp.data.cFMinCover.dPrefactorDB_F_SB);

  }

  /**
   * Puts entries in the lacunarity data {@link #mapLac map}.
   */
  void makeLacMaps(final DataProcessor pDp)
  {

    // this should match with the value for the mean of all
    // mean cv squareds; i.e., the mean of all uppercase Lambdas,
    // where uppercase Lambda is the mean
    // of all lowercase lambdas, which are cv squareds,
    // for all sizes at that grid
    Dp.data.statsLLMeanCvSqsAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsAtSlice.dMean);
    Dp.data.statsLLMeanCvSqsAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsAtSlice.dCV);

    if (!pDp.scan.vars.isMvsD() && !pDp.scan.vars.isDlc()) {

      Dp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dMean
          = (pDp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dMean);
      Dp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dCV
          = (pDp.data.statsLLSlopesCvSqPlus1VsSIZEAtSlice.dCV);
      Dp.data.fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover
          = new FracStats(pDp.data.fsLLisLAMBDASlopeCvSqPlus1VsSIZEFAvgCover);
    }
    // ........................ LOAD EMPTY PLUS MASS INFO.......................
    if (!pDp.scan.vars.isMvsD() && !pDp.scan.vars.isGray()
        && !pDp.scan.vars.isDlc()) {
      Dp.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID.dMean
          = (pDp.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID.dMean);
      Dp.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID.dCV
          = (pDp.data.statsLLMeanCVSqForOMEGAPixOrdeltaIAllGRID.dCV);
    }
    if (!pDp.scan.vars.isGray() && !pDp.scan.vars.isDlc()) {
      // This is reported in the results as the mean of the array, calculated 
      // from the array in storage at that time. So, for the 
      // rotation data, copy the array; then when it is averaged, 
      // ensure that the mean of each array is put into the summary array.
      int length = pDp.data.daLambdaDAtGrid.length;
      Dp.data.daLambdaDAtGrid = new double[length];
      System.arraycopy(pDp.data.daLambdaDAtGrid,
                       srcPos,
                       Dp.data.daLambdaDAtGrid,
                       destPos,
                       length);
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
   * {@link #loadDataForOmegaForProbabilityLacunarity}.
   * <p>
   * The headings keys are of type {@link fraclac.writers.Headings.EnumLacData}.
   */
  void loadProbabilityLacunarity(final DataProcessor pDp)
  {
    Dp.data.statsLLMeanCvSqsUnweightedProbAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsUnweightedProbAtSlice.dMean);
    Dp.data.statsLLMeanCvSqsUnweightedProbAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsUnweightedProbAtSlice.dCV);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsWeightedPDAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsWeightedPDAtSlice.dMean);
    Dp.data.statsLLMeanCvSqsWeightedPDAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsWeightedPDAtSlice.dCV);
    // ========================================================================
    Dp.data.statsLLMeanCvSqsOverBinsUnweightedProbAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsOverBinsUnweightedProbAtSlice.dMean);
    Dp.data.statsLLMeanCvSqsOverBinsUnweightedProbAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsOverBinsUnweightedProbAtSlice.dCV);
    // ========================================================================
    Dp.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice.dMean);

    Dp.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsOverBinsWeightedPDAtSlice.dCV);
    // ========================================================================
    if (!pDp.scan.vars.isGray()) {
      loadDataForOmegaForProbabilityLacunarity(pDp);
    }

  }

  /**
   *
   * @param pDp DataProcessor
   */
  void loadDataForOmegaForProbabilityLacunarity(final DataProcessor pDp)
  {
    Dp.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice.dMean);

    Dp.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsUnweightedProbOMEGAAtSlice.dCV);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice.dMean);
    Dp.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsWeightedPDOMEGAAtSlice.dCV);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice.dMean);
    Dp.data.statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsOverBinsUnweightedProbOMEGAAtSlice.dCV);
    // =========================================================================
    Dp.data.statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice.dMean
        = (pDp.data.statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice.dMean);
    Dp.data.statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice.dCV
        = (pDp.data.statsLLMeanCvSqsOverBinsWeightedPDOMEGAAtSlice.dCV);
  }

  // ***************************************************************************
  private void addGridSet(DataProcessor pDp)
  {
    int liLength = pDp.scan.gridSet.i2dSizes.length;
    Dp.scan.gridSet.i2dSizes = new Dimension[liLength][];
    System.arraycopy(pDp.scan.gridSet.i2dSizes,
                     srcPos,
                     Dp.scan.gridSet.i2dSizes,
                     destPos,
                     liLength);
  }

  private void addMassSet(DataProcessor pDp)
  {
    int liLength = pDp.scan.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs.length;
    Dp.scan.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs = new double[liLength][][];
    System.arraycopy(pDp.scan.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs,
                     srcPos,
                     Dp.scan.d3dPixOrDeltaIInSampleAtSIZEsOnGRIDs,
                     destPos,
                     liLength);
  }
}
