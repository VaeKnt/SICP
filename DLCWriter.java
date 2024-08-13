package fraclac.writers;

import fraclac.analyzer.DataProcessor;
import fraclac.utilities.Utils;

/**
 * Methods for generating Strings and StringBuilders using processed data from
 * {@link fraclac.analyzer.ScanDlc#scanDlcAllSIZEsSoleGRIDThisSlice local
 * connected fractal dimension} box counting scans done with the FracLac plugin.
 * 
 * 
 * @author Audrey Karperien
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 226 $
 * @version Revision $Id: DLCWriter.java 226 2015-03-13 07:15:20Z coder $
 */
public class DLCWriter extends DataStringFormatter {

	/**
   *
   */
	public static String[] saDlcPixelSummaryHeadings = { "X", "Y", "Dlc", R_SQ,
			"Standard Error", "y-intercept", "Local Fractal Dimension", R_SQ,
			"Standard Error", "y-intercept" };

	/**
	 * Returns a multiple line StringBuilder with data for each pixel from a
	 * local connected fractal dimension scan. Does not make headings for file.
	 * 
	 * Is called by the
	 * {@link fraclac.analyzer.Scan#appendDataFileStringsForEachGRIDOnThisSlice
	 * scanner} for a local connected fractal dimension scan.
	 * 
	 * <h5>Basic Structure</h5>
	 * <ol>
	 * <li>Masses for Matched Connected Sets
	 * <li>Masses for All Connected Sets
	 * <li>Total Masses
	 * </ol>
	 * 
	 * @param pd2dDlcPix
	 * @param pi2dSIZEs
	 * @param piBorder
	 * @param pd2dAllConnectedMassAtPixandSIZE
	 * @param pd2dMatchedConnectedMassAtPixandSIZE
	 * @param pd2dTotalMassAtPixandSIZE
	 *            double [][]
	 * 
	 * 
	 * @return StringBuilder
	 */
	public static StringBuilder getDlcDataFileTabbedRowsEachGRIDThisSlice(
			double[][] pd2dDlcPix, int[][] pi2dSIZEs, int piBorder,
			double[][] pd2dAllConnectedMassAtPixandSIZE,
			double[][] pd2dMatchedConnectedMassAtPixandSIZE,
			double[][] pd2dTotalMassAtPixandSIZE) {

		StringBuilder r = new StringBuilder("Masses for Matched Connected Sets")
				.append(newline);

		for (int i = 0; i < pd2dDlcPix[0].length; i++) {
			r.append(
					tabbedRowOfMatchedDlcDataAtThisPixel(i, pi2dSIZEs,
							pd2dDlcPix, pd2dMatchedConnectedMassAtPixandSIZE,
							piBorder)).append(newline);
		}

		r.append("Masses for All Connected Sets").append(newline);

		for (int i = 0; i < pd2dDlcPix[0].length; i++) {

			r.append(
					allDlcDataAtThisPixel(i, pi2dSIZEs, pd2dDlcPix, piBorder,
							pd2dAllConnectedMassAtPixandSIZE)).append(newline);
		}

		r.append("Total Masses").append(newline);

		for (int i = 0; i < pd2dDlcPix[0].length; i++) {

			r.append(
					tabbedRowOfDlcMassForPixel(i, pi2dSIZEs, piBorder,
							pd2dDlcPix, pd2dTotalMassAtPixandSIZE)).append(
					newline);
		} // followed by lines of slopes and columns of data
		return r;
	}

	/**
	 * 
	 * @param piPixelIndex
	 * @param pi2dSIZEs
	 * @param pd2dDlcPix
	 * @param piBorder
	 * @param pd2dAllConnMassAtPixAndSIZE
	 * @return
	 */
	public static String allDlcDataAtThisPixel(int piPixelIndex,
			int[][] pi2dSIZEs, double[][] pd2dDlcPix, int piBorder,
			double[][] pd2dAllConnMassAtPixAndSIZE) {// writes a string with all
														// of the Masses at each
														// epsilon for this
														// pixel
		int liNumBoxSizes = pi2dSIZEs[0].length;// the number of epsilons
		double ldHalfBorder = piBorder / 2;
		String lsDataString = Utils.fnum(pd2dDlcPix[0][piPixelIndex]
				- ldHalfBorder)
				+ TAB
				+ Utils.fnum(pd2dDlcPix[1][piPixelIndex] - ldHalfBorder)
				+ TAB;
		for (int i = 0; i < liNumBoxSizes; i++) {
			lsDataString = lsDataString
					+ pd2dAllConnMassAtPixAndSIZE[piPixelIndex][i] + TAB;
		}
		return lsDataString;
	}

	/**
	 * Returns one line of summary data per i, listing the Dlc, R_SQ, dStdErr,
	 * and yint for the connected set, and the local mass dimension.
	 * 
	 * 
	 * @return String the data in a string for display
	 * 
	 * 
	 * @param piIndex
	 *            int for this pixel location
	 * @param piBorder
	 * @param d2dDlcPix
	 * @param pDP
	 *            DataProcessor
	 */
	public static StringBuilder tabbedRowOfDlcSummaryByPixel(int piIndex,
			int piBorder, double[][] d2dDlcPix, DataProcessor pDP) {
		int jj = piBorder / 2;
		StringBuilder lsbData = new StringBuilder("");

		lsbData.append(d2dDlcPix[0][piIndex] - jj).append(TAB)
				.append(d2dDlcPix[1][piIndex] - jj).append(TAB)
				.append(Utils.fnum(pDP.data.daDlcPerPixel[piIndex]))
				.append(TAB)
				.append(Utils.fnum(pDP.data.daRSqForDlcPerPixel[piIndex]))
				.append(TAB)
				.append(Utils.fnum(pDP.data.daStdErrForDlcPerPixel[piIndex]))
				.append(TAB)
				.append(Utils.fnum(pDP.data.daYIntForDlcPerPixel[piIndex]))
				.append(TAB).append(Utils.fnum(pDP.data.daDLnc_[piIndex]))
				.append(TAB)
				.append(Utils.fnum(pDP.data.daRSqForDLnc_[piIndex]))
				.append(TAB)
				.append(Utils.fnum(pDP.data.daStdErrForDLnc_[piIndex]))
				.append(TAB)
				.append(Utils.fnum(pDP.data.daYInterceptForDLnc_[piIndex]))
				.append(TAB);

		return lsbData;
	}

	/**
	 * Returns a tab-delimited string of the values in
	 * pd2dLCFDpix[0][p]-border/2 and [1][p]-border/2 for the xy coordinates of
	 * pixels, followed by the values in d2dMatchedConnectedMassAtPixandE[p],
	 * for the mass at each size for this pixel. The value of <i>border</i> is
	 * typically the value stored in {@link fraclac.analyzer.Vars#iBorder} from
	 * a scan and used to offset the stored pixels in d2dLCFDpix because the
	 * iBorder is added during processing.
	 * 
	 * 
	 * @param piGRID
	 *            int
	 * @param pi2dSIZEs
	 *            int[][]
	 * @param pd2dDlcPix
	 *            double [][]
	 * @param pd2dMassInConnSetAndMaxSetAtPixAndSIZE
	 *            double [][]
	 * @param piBorder
	 *            int
	 * 
	 * 
	 * @return String of data
	 */
	public static StringBuilder tabbedRowOfMatchedDlcDataAtThisPixel(
			int piGRID, int[][] pi2dSIZEs, double[][] pd2dDlcPix,
			double[][] pd2dMassInConnSetAndMaxSetAtPixAndSIZE, int piBorder) {// writes
																				// string
																				// of
																				// all
																				// masses
																				// at
																				// each
																				// epsilon
																				// for
																				// this
																				// pixel
		int liNumBoxSizes = pi2dSIZEs[0].length;// the number of epsilons
		double ldHalfBorder = piBorder / 2d;
		StringBuilder lsDataString = new StringBuilder("");

		lsDataString.append(Utils.fnum(pd2dDlcPix[0][piGRID] - ldHalfBorder))
				.append(TAB)
				.append(Utils.fnum(pd2dDlcPix[1][piGRID] - ldHalfBorder))
				.append(TAB);

		for (int liSIZEIndex = 0; liSIZEIndex < liNumBoxSizes; liSIZEIndex++) {

			lsDataString
					.append(pd2dMassInConnSetAndMaxSetAtPixAndSIZE[piGRID][liSIZEIndex])
					.append(TAB);

		}
		return lsDataString;
	}

	/**
	 * Returns a TAB-delimited StringBuilder of values in d2dLCFDpix[0][p] and
	 * [1][p] for the pixel's x and y coordinates, followed by the values in
	 * d2dMassAtPixandE[p], for the mass at each size for this pixel. The
	 * coordinates are shifted by - Border/2.
	 * 
	 * 
	 * @param piIndex
	 *            int for the second index of d2dLCFDpix (d2dLCFDpix[0][p],
	 *            d2dLCFDpix[1][p])
	 * 
	 * @param pi2dSIZEs
	 *            int
	 * @param piBorder
	 *            int
	 * @param pd2dDlcPix
	 *            double [][]
	 * @param d2dMassAtPixAndSIZE
	 *            double [][]
	 * 
	 * 
	 * @return String of data
	 */
	public static StringBuilder tabbedRowOfDlcMassForPixel(int piIndex,
			int[][] pi2dSIZEs, int piBorder, double[][] pd2dDlcPix,
			double[][] d2dMassAtPixAndSIZE) {
		int liNumSizes = pi2dSIZEs[0].length;
		// assumes all iGRID are the same as first
		double ldHalfBorder = piBorder / 2d;
		StringBuilder lSb = new StringBuilder("");
		lSb.append(Utils.fnum(pd2dDlcPix[0][piIndex] - ldHalfBorder))
				.append(TAB)
				.append(Utils.fnum(pd2dDlcPix[1][piIndex] - ldHalfBorder))
				.append(TAB);
		for (int iTab = 0; iTab < liNumSizes; iTab++) {
			lSb.append(d2dMassAtPixAndSIZE[piIndex][iTab]).append(TAB);
		}
		return lSb;

	}

}
