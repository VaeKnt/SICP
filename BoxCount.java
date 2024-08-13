package fraclac.analyzer;

import java.util.Arrays;

/**
 * Holds raw results of a box counting fractal analysis. Its 3 fields are arrays
 * holding data for the{@link #d2dPixArraysAtSIZEs mass},
 * {@link #daCountsAtSIZEs count}, and {@link #daSIZEs SIZE} (sampling element
 * size) from {@link fraclac.analyzer.Scan box counting}.
 * 
 * The significance of this object is that the
 * {@link fraclac.analyzer.FracStats#dFractalDimension fractal dimension} can be
 * calculated as the slope of the log vs log regression line of
 * SIZE<sup>-1</sup> on the x axis and count on the y. See the
 * {@link #BoxCount(int[], double[][]) masses constructor} for details of how
 * the object is used for box counting fractal analysis.
 * test: id was mac on 2015-03-11-145252Z; should be coder by 745
 * @author Audrey Karperien
 * @since IJ 1.49p
 * @since jdk 1.6 
 * @version FracLac 2014Jan $Rev: 226 $
 * @version Revision $Id: BoxCount.java 226 2015-03-13 07:15:20Z coder $
 * @see fraclac.analyzer.Scan Box Counting Methods
 * @see fraclac.analyzer.GridSet Box Counting Sampling Elements
 * $HeadURL: https://ak-pc/svn/FracLac/FracLacSource2012/fraclac/analyzer/BoxCount.java $
 */
public class BoxCount {

	/**
	 * Masses constructor creates a new instance and fills local
	 * {@link #daSIZEs SIZEs} and {@link #d2dPixArraysAtSIZEs masses} objects
	 * using the passed arrays, then fills the local {@link #daCountsAtSIZEs
	 * counts} array based on the masses.
	 * 
	 * <h5>Example</h5> Consider a
	 * {@link fraclac.analyzer.Scan#scanBoxCount1SIZE1GRID1Slice box count scan}
	 * :
	 * 
	 * <pre>
	 * SIZE{@link fraclac.analyzer.Vars#iGRIDs GRID}1: {4, 5, 8}
	 * Masses{@link fraclac.analyzer.Vars#iGRIDs GRID}1:
	 * &nbsp;&nbsp;&nbsp;Size 4: {12, 12, 9, 15, 5, 2}
	 * &nbsp;&nbsp;&nbsp;Size 5: {20, 10, 20, 5}
	 * &nbsp;&nbsp;&nbsp;Size 8: {40, 15}
	 * Then Counts{@link fraclac.analyzer.Vars#iGRIDs GRID}1: {6, 4, 2}
	 * </pre>
	 * 
	 * 
	 * @param piaSIZEs
	 *            int array of box sizes
	 * @param pd2dMasses
	 *            2d float array of the d2dPixArraysAtSIZEs per box size
	 */
	public BoxCount(int[] piaSIZEs, double[][] pd2dMasses) {
		daSIZEs = new double[piaSIZEs.length];// Boxes;
		d2dPixArraysAtSIZEs = new double[pd2dMasses.length][];// Masses;
		daCountsAtSIZEs = new double[pd2dMasses.length];

		for (int i = 0; i < pd2dMasses.length; i++) {

			daCountsAtSIZEs[i] = pd2dMasses[i].length;
			daSIZEs[i] = piaSIZEs[i];
			d2dPixArraysAtSIZEs[i] = new double[pd2dMasses[i].length];

			System.arraycopy(pd2dMasses[i], 0, d2dPixArraysAtSIZEs[i], 0,
					d2dPixArraysAtSIZEs[i].length);
		}
	}

	/**
	 * Counts constructor creates a new instance and fills local arrays for
	 * {@link #daSIZEs SIZEs} and {@link #daCountsAtSIZEs counts} but does not
	 * change the local {@link #d2dPixArraysAtSIZEs masses} array.
	 * 
	 * 
	 * @param piaSIZEs
	 *            int array of sampling element sizes
	 * @param pdaCounts
	 *            double array of the counts per SIZE
	 * 
	 * 
	 * @see #d2dPixArraysAtSIZEs Masses array
	 * @see #BoxCount(int[], double[][]) Details of how arrays are filled.
	 */
	public BoxCount(int[] piaSIZEs, double[] pdaCounts) {
		daSIZEs = new double[piaSIZEs.length];// Boxes;
		daCountsAtSIZEs = new double[piaSIZEs.length];// Boxes;
		for (int i = 0; i < piaSIZEs.length; i++) {
			daCountsAtSIZEs[i] = pdaCounts[i];
			daSIZEs[i] = piaSIZEs[i];
		}
	}

	/**
	 * Constructor does nothing so members are not initialized.
	 * 
	 * 
	 * @see #BoxCount(int[], double[]) Counts constructor
	 * @see #BoxCount(int[], double[][]) Masses constructor
	 */
	public BoxCount() {
	}

	/**
	 * Int array of sampling element sizes from a box counting fractal analysis
	 * image {@link fraclac.analyzer.Scan scan}. Is matched to the array of
	 * {@link #daCountsAtSIZEs counts} on instantiation with the counts
	 * {@link #BoxCount(int [], double[]) constructor} or to both that array and
	 * the {@link #d2dPixArraysAtSIZEs masses} array on instantiation with the
	 * masses {@link #BoxCount(int[], double[][]) constructor}. Value is not
	 * assigned in empty constructor.
	 */
	public double[] daSIZEs;

	/**
	 * Double array of measures from a box counting fractal analysis
	 * {@link fraclac.analyzer.Scan scan}.
	 * 
	 * The array is matched to the arrays of {@link #daCountsAtSIZEs counts} and
	 * {@link #daSIZEs SIZEs} on instantiation with the filling
	 * {@link #BoxCount(int [], double[]) constructor}; no value is assigned by
	 * the empty constructor.
	 * 
	 * The array is typically filled as [&oslash;][m], where &oslash; maps to
	 * the size of the sampling element (stored in {@link #daSIZEs}) and each m
	 * is a measured "{@link fraclac.analyzer.Scan#measureThisSpot mass}" in one
	 * of the samples at that SIZE.
	 * 
	 * In turn, the {@link #daCountsAtSIZEs counts} array has at each element
	 * corresponding to {@link fraclac.analyzer.GridSet#d2dEpsilons &epsilon;}
	 * or SIZE one value that is equal to the number of m elements in the masses
	 * array at that &oslash;.
	 * 
	 * <h5>Example</h5> For &oslash;[0]=20, &oslash;[1]=40 and m[0]={3, 2, 4, 6}
	 * and m[1] = {5, 6, 1}, then counts is c[0]<sub>&oslash;=20</sub> = 4,
	 * c[1]<sub>&oslash;=40</sub> = 3.
	 */
	public double[][] d2dPixArraysAtSIZEs;

	/**
	 * Double array that is matched to the array of {@link #daSIZEs SIZE}s or
	 * sampling sizes. It is mapped to the SIZEs array on instantiation with the
	 * filling {@link #BoxCount(int [], double[]) constructor}, or to both SIZEs
	 * and the {@link #d2dPixArraysAtSIZEs masses} array with the
	 * {@link #BoxCount(int[], double[][] ) masses constructor}, or not changed
	 * in the empty {@link #BoxCount constructor}.
	 * <p>
	 * Is used for calculating the
	 * {@link fraclac.analyzer.FracStats#dFractalDimension fractal dimension}.
	 * Should correspond to the counts from a box counting fractal analysis
	 * image {@link fraclac.analyzer.Scan scan}. That is, each entry should
	 * correspond to the number of samples
	 * {@link fraclac.analyzer.Scan#measureThisSpot measured}.
	 * 
	 * 
	 * @see fraclac.analyzer.GrayCounter Grayscale data sampling
	 * @see fraclac.analyzer.GridSet#i2dSIZEs SAMPLING ELEMENT SIZES SIZE
	 * @see fraclac.analyzer.Scan Box counting methods
	 */
	public double[] daCountsAtSIZEs;

	@Override
	public boolean equals(Object pObject) {
		Class<? extends Object> aClass = pObject.getClass();
		BoxCount bh = (BoxCount) pObject;
		if (!Arrays.deepEquals(bh.d2dPixArraysAtSIZEs, d2dPixArraysAtSIZEs)) {
			return false;
		}
		if (!Arrays.equals(bh.daCountsAtSIZEs, daCountsAtSIZEs)) {
			return false;
		}
		if (!Arrays.equals(bh.daSIZEs, daSIZEs)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	public boolean equalsNoMass(Object o) {
		Class<? extends Object> aClass = o.getClass();
		BoxCount bh = (BoxCount) o;

		if (!Arrays.equals(bh.daCountsAtSIZEs, daCountsAtSIZEs)) {
			return false;
		}
		if (!Arrays.equals(bh.daSIZEs, daSIZEs)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Arrays.hashCode(this.daSIZEs);
		hash = 29 * hash + Arrays.deepHashCode(this.d2dPixArraysAtSIZEs);
		hash = 29 * hash + Arrays.hashCode(this.daCountsAtSIZEs);
		return hash;
	}

}
