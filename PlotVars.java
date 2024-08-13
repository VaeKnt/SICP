package fraclac.writers;

/**
 * FracLacProject
 *
 * @author Audrey Karperien akarpe01@postoffice.csu.edu.au
 */
public class PlotVars
{

  public boolean bShowFlippancy;
  public MultifractalDescription mf;

  public PlotVars()
  {

  }

  public PlotVars(
      boolean pbGraphOnOnePlot,
      boolean pbDoOptimalOnly,
      String psOptPrefixGridLocationAndDetailedTitleOrGridAndTitleAndSlice,
      String psTitleAndSlice,
      final String psOriginalImageTitle,
      String psLabelListingQRange,
      double ldAMin,
      double ldAMax,
      double ldFMin,
      double ldFMax,
      double[] pdaX,
      double[] pdaY,
      double ldMaxLine,
      double ldDAtQis0,
      double[] ldaPosA,
      double[] ldaPosF,
      double[] ldaNegA,
      double[] ldaNegF,
      boolean pbDrawAperture,
      double[] ldaApertureA,
      double[] ldaApertureF,
      boolean pbSave,
      String psDirectory,
      int piLoc,
      int piNumLocs,
      int piRoiManagerOrSubscanIndex,
      int piNumRois)
  {
  }
  public boolean bDecideOnMultifractality;

  public void setDaNegAlphas(double[] daNegAlphas)
  {
    this.daNegAlphas = daNegAlphas;
  }

  public void setDaApertureFAtAlpha(double[] daApertureFAtAlpha)
  {
    this.daApertureFAtAlpha = daApertureFAtAlpha;
  }

  public void setDaApertureAlpha(double[] daApertureAlpha)
  {
    this.daApertureAlpha = daApertureAlpha;
  }
  public boolean bGraphOnOnePlot;
  public boolean bDoOptimalOnly;
  public String sOptPrefixGridAndDetailedTitleOrGridAndTitleAndSlice;
  public String sTitleAndSlice;
  public String sOriginalImageTitle;
  public String sLabelListingQRange;
  public double dAlphaMin;
  public double dAlphaMax;
  public double dFAtAlphaMin;
  public double dFAtAlphaMax;
  public double[] daX;
  public double[] daY;
  public double dMaxLine;
  public double dDAtQis0;
  public double[] daPosAlphas;
  public double[] daPosFAtAlphas;
  public double[] daNegAlphas;
  public double[] daNegFAtAlphas;
  public boolean bDrawAperture;
  public double[] daApertureAlpha;
  public double[] daApertureFAtAlpha;
  public boolean bSave;
  public String sDirectory;
  public int iLoc;
  public int iNumLocs;
  public int iRoiManagerOrSubscanIndex;
  public int iNumRois;

}
