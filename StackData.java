/*

 =============================================================================
 -------------------------------NOTICE----------------------------------------
 =============================================================================
 
 *  2015 FracLacProject
 *
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 The contents of this file are subject to the same license as the
 source code for ImageJ. 
  
 FracLac is in the public domain.  The initial
 developer of the original code and current 
 project lead is Audrey Karperien, Charles Sturt
 University, akarpe01@postoffice.csu.edu.au. If you
 use it to publish research, please cite the source.  
 
 To cite FracLac, use:
 
 - Karperien A, Ahammer H, Jelinek HF (2013)
 Quantitating the subtleties of microglial morphology 
 with fractal analysis. Front Cell Neurosci 7:3. 
 doi:10.3389/fncel.2013.00003
 
 - Kam, Y., Karperien, A., Weidow, B., Estrada, L., Anderson, 
 A. R., and  Quaranta, V. (2009). Nest expansion assay: 
 a cancer systems biology  approach to in vitro invasion 
 measurements. BMC Res. Notes 2:130.  
 doi: 10.1186/1756-0500-2-130
 
 - Karperien, A., FracLac for ImageJ. http://rsb.info.nih.
 gov/ij/plugins/fraclac/FLHelp/, 1999-2013.
 
 - see also the FracLac citations page for published work
 http://rsbweb.nih.gov/ij/plugins/fraclac/FLHelp/FLCitations.htm
 
 To cite ImageJ, use:
 
 - Rasband, W.S., ImageJ, U. S. National Institutes of Health, Bethesda,
 Maryland, USA, http://imagej.nih.gov/ij/, 1997-2013.
 
 OR
 
 - Schneider, C.A., Rasband, W.S., Eliceiri, K.W. "NIH Image to ImageJ:
 25 years of image analysis". Nature Methods 9, 671-675, 2012.
 =============================================================================
 Project:    FracLac for ImageJ                                                
 .............................................................................
 Lead:       Audrey Karperien                                                  
 .............................................................................
 Version:    FracLac 2013Dec; needs ImageJ 1.48k                                
 .............................................................................       
 Packages:   fraclac.gui; fraclac.setup; fraclac.analyzer;                     
 fraclac.utilities; fraclac.writers = Frac_Lac.jar  
 File:       $Id$
 .............................................................................       
 Program:    Masters (Honours) Research; 2004
 .............................................................................       
 Department: Neuroscience, School of Community Health,
 Faculty of Health Studies, Charles Sturt University, Australia
 .............................................................................
 Date:       2002-2015
 .............................................................................
 Purpose:    Plugin for ImageJ, analyzes binary and grayscale digital images
 using multiple grid scans.
 .............................................................................
 Returns:    Fractal dimensions, lacunarity, multifractal analysis,
 local connected fractal dimensions, mass vd distance,
 distribution of pixels, cell span, convex hull, and circularity.
 Also delivers regression statistics and distribution graphs.
 - text based reports; graphs; colour coded graphics;
 csv raw data files, and graphics showing grid placement.
 .............................................................................
 Features:   - The algorithm adds to standard box counting techniques
 options to find global and local dimensions, average
 dimensions over shifted grid positions, and a most efficient
 cover dimension.
 - As an additional feature, the plugin can automatically
 optimize scans based on each image (alternatively, it
 lets the user manually specify features of the scan).
 - Offers a practical solution to multifractal analysis with
 box counting by scanning to find an optimized multifractal
 spectrum.
 - Includes sliding box lacunarity.
 - Different types of scaling are available from the user
 interface, as well.
 - Allows the user to examine trends over an image using
 colour coding adjustable in real time.
 .............................................................................        
 History and
 Background: The algorithm is modified originally from ImageJ's box
 counting algorithm and H. Jelinek's NIHImage plugin.
 
 A full description of the basic techniques represented can
 be found in:
 - T.G. Smith, Jr., G. Lange and W.B. Marks,
 Fractal Methods and Results in Cellular Morphology,
 J. Neurosci. Methods, 69:1123-126, 1996.
 See also:
 - E. Fernandez et al., Are neurons multifractals?,
 J. Neurosci. Methods, 89:151-157, 1999
 - R.E. Plotnick, R.H. Gardner, and R.vars. O'Neil,
 Lacunarity indices as measures of landscape texture,
 in Landscape Ecology 8(3):201-211, 1993
 - Innaconne, Geometry in Biological Systems.
 The multifractal spectrum is calculated based on the ideas of
 - A. Chhabra and R. Jensen, Direct Determination of the f(α)
 singularity spectrum in Phys. Rev . Lett. 62: 1327, 1989.
 The convex hull algorithm was provided by Thomas Roy,
 University of Alberta, Canada.
 I referred also to
 - Costa and Cesar, Shape Analysis and Classification,
 CRC Press, 2001, for other info.
 The local connected fractal dimension algorithm
 was developed based on input from H. Jelinek and
 - "Local Connected Fractal Dimensions and Lacunarity
 Analyses of 60degree Fluorescein Angiograms", Gabriel
 Landini, Philip I. Murray, and Gary P. Missonf,
 Investigative Ophthalmology & Visual Science,
 December 1995, Vol. 36, No. 13
 Grayscale analysis is based on 
 - "Texture segmentation using fractal dimension",
 B. Chaudhuri and N. Sarkar, Pattern Analysis and
 Machine Intelligence, IEEE Transactions on 
 1995, 17 (1):72-77
 .............................................................................        
 Public
 Domain:     I wrote this plugin for ImageJ.  It is provided without
 warranty and is forever freely available
 to anyone to use or modify, in the same way that
 ImageJ is.  Please acknowledge it if you use it for published
 research, using the citations listed at the top of this notice.
 $HeadURL: $
 ==============================================================================
 ------------------------------------------------------------------------------
 ==============================================================================

 */
package fraclac.writers;

import fraclac.analyzer.DataProcessor;
import fraclac.utilities.ArrayMethods;
import fraclac.utilities.Statistics;
import fraclac.utilities.Symbols;
import java.util.ArrayList;

/**
 * FracLacProject
 *
 * @author Audrey Karperien akarpe01@postoffice.csu.edu.au Rev: $Id: $ $HeadURL&
 */
public class StackData extends Symbols
{

  public ArrayList<Slice> slices;

  public class Slice
  {

    int iGrids;
    SliceStats dimensions;
    SliceStats lacunarity;
    SliceStats circularity;
  }

  public class SliceStats
  {

    public SliceStats(double[] pValues)
    {
      values = new double[pValues.length];
      System.arraycopy(pValues,
                       0,
                       values,
                       0,
                       pValues.length);
    }
    int iGrids;
    double[] values;

    public double min()
    {
      return ArrayMethods.minArray(values);
    }

    public double max()
    {
      return ArrayMethods.maxInArray(values);
    }

    public double stdDev()
    {
      Statistics lStatistics = new Statistics(values,
                                              "");
      return lStatistics.dStdDev;
    }

    public double mean()
    {
      return ArrayMethods.meanOfArray(values);
    }

  }

  public void loadSlice(int piGRID,
                        DataProcessor pDP)
  {
    Slice lSlice = new Slice();
    lSlice.dimensions = new SliceStats(pDP.data.daDBFromCountOrSumdeltaIAtGRID);
    lSlice.lacunarity = new SliceStats(
        pDP.data.daCVForLAMBDACvSqForPixOrdeltaIAtGRID);
    // lSlice.circularity = new SliceStats(pDP.scan.vars.d)

    slices.add(piGRID,
               lSlice);
  }//

//  public void makeString()
//  {
//    StringBuilder psbHeadingsTabSeparated = new StringBuilder();
//    psbHeadingsTabSeparated.append(STACK_DATA_MEAN_Db)
//        .append(TAB)
//        .append(STACK_DATA_MIN_Db)
//        .append(TAB)
//        .append(STACK_DATA_MAX_Db)
//        .append(TAB)
//        .append(STACK_DATA_STD_DEV_Db)
//        .append(TAB);
//    ResultsFilesWriter.showOrSaveDataFile(psbDataTabSeparated,
//                                          psbHeadingsTabSeparated,
//                                          psFileName,
//                                          pVars);
//  }
}
