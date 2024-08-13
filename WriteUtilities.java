package fraclac.writers;

import fraclac.analyzer.Calculator;
import fraclac.analyzer.Vars;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static fraclac.utilities.Utils.fnum;

/**
 * Utility methods for writing data to Strings for summary files from fractal
 * analysis with FracLac for ImageJ.
 *
 * @author Audrey Karperien
 * @since IJ 1.49o
 * @since jdk 1.6
 * @version FracLac 2014Jan $Rev: 244 $
 * @version Revision $Id: WriteUtilities.java 244 2015-04-19 01:20:59Z audrey $
 */
public class WriteUtilities extends AllGsHeadings
{

  // ..........................................................................
  // ................ UTILITY METHODS FOR STRINGS ..................
  // ..........................................................................
  /**
   * Returns a StringBuilder with the values in the passed Map appended, each
   * followed by a TAB. There is no newline.
   *
   * @param pMap
   *
   * @return StringBuilder
   */
  public static StringBuilder appendTabbedData(Map pMap)
  {
    StringBuilder lStringBuilder = new StringBuilder();

    for (Object key : pMap.keySet()) {

      lStringBuilder.append(fnum(pMap.get(key))).append(TAB);

    }

    return lStringBuilder;
  }

  /**
   * Returns a StringBuilder made from the passed String and keySet. The keySet
   * must be a DataTypesInterface instance, because the method calls that
   * interfaces's {@link DataTypesInterface#sOneGRID sOneGRID} method to get the
   * appropriate heading.
   *
   * @param psName an enum constant that is the key that maps to the value for
   * "NAME" in the Maps for which the headings are being made
   * @param keySet the keySet for unmapping the values
   *
   * @return StringBuilder of tabbed strings from the passed keySet, with "for
   * psName" appended after each string, before the tab
   */
  public static StringBuilder appendTabbedHeadingsForSingleGrid(
      String psName,
      Set<? extends DataTypesInterface> keySet)
  {

    StringBuilder lStringBuilder = new StringBuilder();

    for (DataTypesInterface key : keySet) {

      lStringBuilder.append(key.sOneGRID()).append(" for ")
          .append(psName).append(TAB);

    }

    return lStringBuilder;
  }

  /**
   * Returns the passed array of strings as one tabbed string that starts with a
   * TAB and ends with the last element in the passed array.
   *
   *
   * @return string of headings.
   *
   *
   * @param psaHeadings an array of strings
   */
  public static String toTabbedString(String[] psaHeadings)
  {
    String lsHeadings = psaHeadings[0];
    for (int i = 1; i < psaHeadings.length; i++) {
      lsHeadings = lsHeadings + TAB + psaHeadings[i];
    }
    return lsHeadings;
  }

  /**
   * Returns the passed String [] as a string with each element numbered,
   * starting from 1, with a ". " after the number. Thus, if {{cat}, {dog}} is
   * passed in, then "1. cat TAB 2. dog" is returned.
   *
   * @param psaHeadings
   *
   * @return numbered and tabbed StringBuilder
   */
  public static StringBuilder toTabbedAndNumberedString(String[] psaHeadings)
  {
    StringBuilder lsbHeadings = new StringBuilder();
    for (int i = 1; i < psaHeadings.length; i++) {
      lsbHeadings.append(i).append(". ").append(psaHeadings[i])
          .append(TAB);
    }
    return lsbHeadings;
  }

  /**
   * Appends to the passed StringBuilder one line for each map in the passed
   * List. Each line starts with psInfo unless that string is null. Then each
   * line consists of the values in the map, with a tab after each, including
   * only those values matched to a key in the passed keySet, in the order of
   * the passed keySet, with a newline at the end. If a request to get a key
   * returns null, then {@value fraclac.utilities.Symbols#NC} is appended at
   * that position. The value is read by being passed to
   * {@link fraclac.utilities.Utils#fnum(java.lang.Object)}.
   *
   * @param psInfo String to append to the start of each line; string should
   * probably end with a tab so it does not run into the next string
   * @param pMapList ArrayList of data maps to append data from
   * @param keySet {@literal Set<? extends DataTypesInterface>} one set of keys
   * for all of the maps corresponding to the values that are to be appended to
   * the string
   *
   * @return the increased StringBuilder
   */
  public static StringBuilder appendTabbedData(
      String psInfo,
      ArrayList<Map<? extends DataTypesInterface, Object>> pMapList,
      Set<? extends DataTypesInterface> keySet)
  {
    //---------------------------------------------------------------------
    // Make an empty StringBuilder. We will go through the passed list of 
    // maps and for each map append to the StringBuilder a line of 
    // data that starts with the passed info string, then lists 
    // each map value and a tab, and ends with a newline.
    // --------------------------------------------------------------------
    StringBuilder lsBuilder = new StringBuilder();
    // --------------------------------------------------------------------
    // Go through the list of maps.
    // --------------------------------------------------------------------
    for (int i = 0; i < pMapList.size(); i++) {
      // Append the info string at the start of each row from each map.
      if (!(psInfo == null)) {
        lsBuilder.append(psInfo);
      }
      // -----------------------------------------------------------------
      // Get each map in the list of maps.
      // -----------------------------------------------------------------
      Map<? extends DataTypesInterface, Object> thisMap = pMapList.get(i);
      // ------------------------------------------------------------------
      // For each key and value pair in the current map, append the value. 
      // The only values returned are those that are mapped to the 
      // members of the passed keySet. They are appended in the order 
      // of the key set.
      // ------------------------------------------------------------------
      for (DataTypesInterface key : keySet) {
        // The value is a string or a number that has to be formatted 
        // to a printable form. There is a tab between entries.
        // ------------------------------------------------------------------
        String lString = fnum(thisMap.get(key));
        lsBuilder.append(lString).append(TAB);
      }
      // --------------------------------------------------------------------
      // Put a newline at the end of the tabbed data.
      // --------------------------------------------------------------------
      lsBuilder.append(newline);
    }
    return lsBuilder;
  }

  /**
   * Returns the string "slope ln(topTerm) vs ln(&epsilon;)";
   *
   *
   * @param psTopTerm String for slope string
   *
   *
   * @return String
   */
  static String slopeVsE(String psTopTerm)
  {
    return "slope ln(" + psTopTerm + ") vs ln(" + epsilon + ")";
  }

  /**
   * Inserts head above old and returns new StringBuilder.
   *
   *
   * @param psbOldString
   * @param psStringToInsertBeforeOldString string
   *
   *
   * @return StringBuilder with new string inserted a line above the old.
   */
  static StringBuilder prependLine(StringBuilder psbOldString,
                                   String psStringToInsertBeforeOldString)
  {
    StringBuilder b = new StringBuilder("");
    b.append(psStringToInsertBeforeOldString);
    b.append(newline);
    b.append(psbOldString);
    return b;
  }

  /**
   * Returns a numbered, TAB separated string from the passed array of strings,
   * where each member of that array is numbered with
   * <i>" (psPrefix #)"</i> and appended the specified number of times.
   * <p>
   * If the input is, for instance, <code>({Cats, Dogs, Mice}, 2, "Q"}</code>,
   * then the output is the TAB delimited string:<br>
   *
   * <pre>
   * Cats (Q1)  Cats (Q2)  Dogs (Q1)  Dogs (Q2)  Mice (Q1)  Mice (Q2)
   * </pre>
   *
   *
   * @return String of headings for printing data
   *
   *
   * @param psaPhrasesToNumberAndTabTogether String array to make into a tabbed
   * string
   * @param piTimesToRepeatEachElement int for number of times to repeat a
   * heading
   * @param psPrefix String to include with the number
   */
  public static String numberTabAndRepeatEachElementXTimes(
      String[] psaPhrasesToNumberAndTabTogether,
      int piTimesToRepeatEachElement,
      String psPrefix)
  {
    String lsTabbedString = "";

    for (int p = 0; p < psaPhrasesToNumberAndTabTogether.length; p++) {
      String lsThisPhrase = psaPhrasesToNumberAndTabTogether[p];
      for (int liTimesRepeated = 1; liTimesRepeated <= piTimesToRepeatEachElement; liTimesRepeated++) {
        lsTabbedString += lsThisPhrase + " (" + psPrefix
            + liTimesRepeated + ")" + TAB;
      }
    }
    return lsTabbedString;
  }

  /**
   * Returns a tabbed string matrix from the passed 2d array of ints.
   *
   *
   * @param piA
   * @return matrix of tabbed lines as rows
   */
  public static StringBuilder makeString(int piA[][])
  {
    StringBuilder lsbRowsAndColumns = new StringBuilder("");

    for (int liRow = 0; liRow < piA.length; liRow++) {

      for (int liColumn = 0; liColumn < piA[liRow].length; liColumn++) {

        lsbRowsAndColumns.append(piA[liRow][liColumn]).append(TAB);
      }

      lsbRowsAndColumns.append(newline);
    }
    return lsbRowsAndColumns;
  }

  /**
   *
   * @param pVars
   * @return String
   */
  static String elementName(Vars pVars)
  {
    return (pVars.bUseOvalForInnerSampleNotOuterSubscan ? "Oval"
        : "Rectangle");
  }

  /**
   * Returns a string of formatted and tabbed values for each array at A[][e].
   * If the passed array is {{m00,m01,m02}, {m10,m11,m12}} and e is 1, the
   * string is "m01+TAB+m11+TAB". The string ends with a TAB. Adds
   * {@value fraclac.utilities.Symbols#NC} if the index is out of bounds for an
   * array. The final result is formatted using a ####.####
   * {@link fraclac.utilities.Utils#fnum formatter}. <h5>Example</h5> If the
   * passed array is <br>
   * {{ 2, 3, 4}, {20, 30, 40}, {200, 300, 400}}
   * <p>
   * Then calling the method for each index would yield:
   *
   * <pre>
   * fxn(array, 0) gives "2 TAB 20 TAB 200 TAB"
   * fxn(array, 1) gives "3 TAB 30 TAB 300 TAB"
   * fxn(array, 2) gives "4 TAB 40 TAB 400 TAB"
   * </pre>
   *
   * @param piE int for the index of the values to string together
   * @param pd2dA double [][] array to make a string from
   *
   * @return string for each A[i to length], the value in A[i][g][e]+TAB for
   * each A[i][g to length]
   *
   * @see fraclac.utilities.Utils#fnum formatter
   */
  public static String stringOfNthElementsFromAllArrays(double[][] pd2dA,
                                                        int piE)
  {
    String lsTabbedStringOfEachElementAtpiE = "";
    for (int i = 0; i < pd2dA.length; i++) {
      if (pd2dA[i] == null || piE >= pd2dA[i].length) {
        lsTabbedStringOfEachElementAtpiE = lsTabbedStringOfEachElementAtpiE
            + NC + TAB;
      } else {
        lsTabbedStringOfEachElementAtpiE = lsTabbedStringOfEachElementAtpiE
            + fnum(pd2dA[i][piE]) + TAB;
      }
    }
    return lsTabbedStringOfEachElementAtpiE;
  }

  /**
   * Returns a string for constructing a matrix from the passed 3d array. Each
   * row returned has the element at the passed index joined by tabs from all of
   * the subarrays up to the passed number of subarrays to stop at, for each
   * main array, with no newlines. The string ends with a TAB.
   *
   * Adds {@value fraclac.utilities.Symbols#NC} if the index is out of bounds
   * for an array. The final result is formatted using a ####.####
   * {@link fraclac.utilities.Utils#fnum formatter}.
   * <p>
   * For example,
   *
   * <pre>
   * Let gridArray correspond to the doubles:
   * DataType 1: grid 1: { 2, 3, 4 } grid 2: { 20, 30, 40 }
   * DataType 2: grid 1: { 6, 7, 8 } grid 2: { 60, 70, 80 }
   * </pre>
   *
   * Then the returned strings for calling the function once for each index
   * would be:<br>
   *
   * <pre>
   * <code>
   * fxn(gridArray, 0, 2) gives "2 TAB 20 TAB 6 TAB 60 TAB"
   * fxn(gridArray, 1, 2) gives "3 TAB 30 TAB 7 TAB 70 TAB"
   * fxn(gridArray, 2, 2) gives "4 TAB 40 TAB 8 TAB 80 TAB"
   * </code>
   * </pre>
   *
   * @return string for each A[i to length], the value in A[i][g][e]+TAB for
   * each A[i][g to length], formatted to a string as ####.####
   *
   * @param pd3dA 3d double [][][] to write from
   * @param piE int for the index of the Element to write to the string
   * @param piLengthOfGrid int for the number
   * @param piGRIDs int for the number of subArrays to write
   */
  public static String stringOfNthElementFromAllArrays(double[][][] pd3dA,
                                                       int piE,
                                                       int piLengthOfGrid,
                                                       int piGRIDs)
  {
    int liNumberOfDataTypes = pd3dA.length;
    StringBuilder lsbStringOfAllElementsAtE = new StringBuilder("");
    for (int liDataTypeIndex = 0; liDataTypeIndex < liNumberOfDataTypes; liDataTypeIndex++) {
      for (int liGridNumber = 0; liGridNumber < piGRIDs; liGridNumber++) {
        // if there are not enough grids for this datatype,
        // append NC once for each grid expected
        if (pd3dA[liDataTypeIndex] == null
            || (liGridNumber >= pd3dA[liDataTypeIndex].length)) {
          lsbStringOfAllElementsAtE.append(NC).append(TAB);
        } // or if the desired datatype and grid exist, if so does
        // the desired number of elements, append the element else NC
        else {
          if (piE < pd3dA[liDataTypeIndex][liGridNumber].length) {
            lsbStringOfAllElementsAtE
                .append(fnum(pd3dA[liDataTypeIndex][liGridNumber][piE]))
                .append(TAB);
          } else {
            lsbStringOfAllElementsAtE.append(NC).append(TAB);
          }// end if the array was too short
        }// end if there is an array for this datatype, rather than null
      }// end do each grid in the datatype
    }// end do each datatype
    return lsbStringOfAllElementsAtE.toString();
  }

  /**
   * Returns strings, separated by tabs, of numbers formatted to 4 decimal
   * places, representing the log vs log slopes of the passed arrays against the
   * array in pd2dEpsilons at [0]. Appends {@value fraclac.utilities.Symbols#NC}
   * for any arrays that are equal to null.
   *
   * @param pd2dCounts double [datatypes][] of values at &epsilon; from which to
   * find the {@link Calculator#slopeOfPowerRegress log vs log slopes}.
   * @param pd2dEpsilons double [][]
   *
   * @return String of slopes with tabs between them
   */
  public static String stringOfSlopeYVsEpsilonForYArrays(
      double pd2dCounts[][],
      double[][] pd2dEpsilons)
  {
    Calculator lCalc = new Calculator();
    StringBuilder lStrBuilder = new StringBuilder("");
    for (int i = 0; i < pd2dCounts.length; i++) {
      if (pd2dCounts[i] == null) {
        lStrBuilder.append(NC + TAB);
      } else {
        lStrBuilder
            .append(
                fnum(lCalc
                    .slopeOfPowerRegression(pd2dCounts[i],
                                            pd2dEpsilons[0],
                                            pd2dEpsilons[0].length))).
            append(TAB);
      }
    }
    return lStrBuilder.toString();
  }

  /**
   * Returns a string that has the slopes of the passed dependent variables
   * against their matching array's independent variables, separated by tabs,
   * with a TAB at the end. If one of the arrays is &lt;2 elements long, a
   * string for "not calculated" is returned. <h5>Example</h5>
   * <p>
   * Independent Variables: { A { 3, 9, 27, 81} , B {3, 9, 27, 81}}<br>
   * Dependent Variables:
   *
   * <pre>
   * {
   * datatype 1:	{ a{ 4, 16,  64, 256 }, b{ 6, 12, 18, 24 }},
   * datatype 2:	{ a{ 2,  4,   8,  16 }, b{ 3,  9, 27, 81 }},
   * datatype 3:	{ a{ 5, 25, 125, 625 }, b{ 2,  3,  4,  5 }}
   * }
   * </pre>
   *
   * Then the returned tabbed string of slopes will be:
   * <table>
   * <tr>
   * <td>1.2619</td>
   * <td>0.4155</td>
   * <td>0.6309</td>
   * <td>1.000</td>
   * <td>1.4650</td>
   * <td>0.2764</td>
   * </tr>
   * <tr>
   * <td>(1a vs A)</td>
   * <td>(1b vs B)</td>
   * <td>(2a vs A)</td>
   * <td>(2b vs B)</td>
   * <td>(3a vs A)</td>
   * <td>(3b vs B)</td>
   * </tr>
   * </table>
   * <p>
   * The table below shows the basic structure to use for an example with 2
   * datatypes:
   * <table border=1>
   * <th>Datatype</th>
   * <th>Orientation</th>
   * <th>Value At Box Size</th>
   * <th>Slope Returned</th>
   * <tr>
   * <td rowspan = 6>count</td>
   * <td rowspan = 3>G1</td>
   * <td>count at Size 1</td>
   * <td * rowspan=3>G1. count vs size</td>
   * </tr>
   * <tr>
   * <td>count at Size 2</td>
   * </tr>
   * <tr>
   * <td>count at Size 3</td>
   * </tr>
   * <tr>
   * <td rowspan = 3>G2</td>
   * <td>count at Size 1</td>
   * <td rowspan=3>G2. count vs size</td>
   * </tr>
   * <td>count at Size 2</td></tr>
   * <tr>
   * <td>count at Size 3</td>
   * </tr>
   * <tr>
   * <td rowspan = 6>average mass</td>
   * <td rowspan = 3>G1</td>
   * <td>average mass at Size 1</td>
   * <td * rowspan=3>G1. average mass vs size</td>
   * </tr>
   * <tr>
   * <td>average mass at Size 2</td>
   * </tr>
   * <tr>
   * <td>average mass at Size 3</td>
   * </tr>
   * <tr>
   * <td rowspan = 3>G2</td>
   * <td>average mass at Size 1</td>
   * <td rowspan=3>G2. average mass vs size</td>
   * </tr>
   * <tr>
   * <td>average mass at Size 2</td>
   * </tr>
   * <tr>
   * <td>average mass at Size 3</td>
   * </tr>
   * </table>
   *
   * @param pd2dIndependentVariables double [][]
   *
   * @param pd3dDependentVariables double [][][] array; for box counting data in
   * FracLac, the array should correspond to [datatype][grid][box size];
   *
   * @return String of slopes with tabs between them
   */
  public static String rowOfSlopes(double[][][] pd3dDependentVariables,
                                   double[][] pd2dIndependentVariables)
  {
    int liNumberOfIndependentVariableArrays = pd2dIndependentVariables.length;
    Calculator f = new Calculator();
    StringBuilder P = new StringBuilder("");
    for (int liDataType = 0; liDataType < pd3dDependentVariables.length; liDataType++) {
      if (pd3dDependentVariables[liDataType] == null) {
        for (int liCounter = 1; liCounter <= liNumberOfIndependentVariableArrays; liCounter++) {
          P.append(NC).append(TAB);
        }
      } else {
        for (int liGrid = 0; liGrid < pd3dDependentVariables[liDataType].length; liGrid++) {
          // if the array is empty write a default value
          if (pd3dDependentVariables[liDataType][liGrid] == null
              || pd3dDependentVariables[liDataType][liGrid].length < 2) {
            P.append(NC).append(TAB);
          } // otherwise write the slope
          else {
            P.append(
                fnum(f.slopeOfPowerRegression(
                        pd3dDependentVariables[liDataType][liGrid],
                        pd2dIndependentVariables[liGrid],
                        pd2dIndependentVariables[liGrid].length)))
                .append(TAB);
          }
        }
      }
    }
    return P.toString();
  }

  /**
   * Returns the index of the passed string in the passed array or else returns
   * -1 if the string is not found.
   *
   * @param pString
   * @param psArray a
   *
   * @return int for index in array
   */
  public static int here(String pString,
                         String[] psArray)
  {
    for (int i = 0; i < psArray.length; i++) {
      if (psArray[i].equalsIgnoreCase(pString)) {
        return i;
      }
    }

    return -1;
  }

}
