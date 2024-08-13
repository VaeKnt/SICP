package fraclac.writers;

/**
 * Class contains methods for returning one string based on a hierarchical
 * assessment of the passed strings. The class is intended for use with Enums
 * that implement the {@link DataTypesInterface}.
 *
 * @see fraclac.writers.Headings.EnumInfoData
 * @see fraclac.writers.Headings.EnumLacData
 * @see fraclac.writers.Headings.EnumDataFile
 * @see fraclac.writers.Headings.ENUM_MULTIFRACTAL_HEADINGS
 * @version FracLac 2014Jan $Rev: 239 $
 * @version Revision $Id: EnumString.java 239 2015-03-18 08:32:49Z audrey $
 */
public class EnumString
{

  /**
   * String initialized to UNINITIALIZED.
   */
  public String UNINITIALIZED = DataTypesInterface.UNINITIALIZED;

  /**
   *
   */
  public String sAllGRIDs,
      /**
       *
       */
      sOneGRID,
      /**
       *
       */
      sGrayAllGRIDs,
      /**
       *
       */
      sGrayOneGRID;


  /**
   *
   * @param psAllGRIDs
   * @param psOneGRID
   * @param psGrayAllGRIDs
   * @param psGrayOneGRID
   */
  public EnumString(String psAllGRIDs,
                    String psOneGRID,
                    String psGrayAllGRIDs,
                    String psGrayOneGRID)
  {
    sAllGRIDs = psAllGRIDs;
    sOneGRID = psOneGRID;
    sGrayAllGRIDs = psGrayAllGRIDs;
    sGrayOneGRID = psGrayOneGRID;
  }

  /**
   *
   */
  public EnumString()
  {
  }

  /**
   * Returns the passed string.
   *
   * @param psAllGRIDs
   *
   * @return String
   */
  public String sAllGRIDs(String psAllGRIDs)
  {
    return psAllGRIDs;
  }

  /**
   * Returns one of the headings associated with this enum constant, chosen in a
   * hierarchical manner. The first choice is the second heading used in the
   * constructor, which is intended to correspond to data summarized over one
   * {@link fraclac.analyzer.Vars#iGRIDs GRID} for
   * {@link fraclac.analyzer.Vars#iUserForeground binary} scans.
   *
   * In particular, it returns the String stored in this implementation's field
   * {@link #sOneGRID sOneGRID}, unless that field is equal to
   * {@link #UNINITIALIZED UNINITIALIZED}, in which case it returns the result
   * of a call to this implementation's {@link #sAllGRIDs sAllGRIDs} method.
   *
   * @param psAllGRIDs string corresponding to the first string used in the
   * constructor for the enum that is being considered
   * @param psOneGRID string corresponding to the second string used in the
   * constructor for the enum that is being considered
   * @param psGrayAllGRIDs string corresponding to the third string used in the
   * constructor for the enum that is being considered
   * @param psGrayOneGRID string corresponding to the fourth string used in the
   * constructor for the enum that is being considered
   *
   * @return String
   */
  public String sOneGRID(String psAllGRIDs,
                         String psOneGRID,
                         String psGrayAllGRIDs,
                         String psGrayOneGRID)
  {
    if (UNINITIALIZED.equals(psOneGRID)) {
      return sAllGRIDs(psAllGRIDs);
    }
    return psOneGRID;
  }

  /**
   *
   * Returns one of the headings associated with this enum constant, in a
   * hierarchical manner. The first choice is the 3rd heading in the
   * constructor, which is intended to correspond to data summarized over all
   * {@link fraclac.analyzer.Vars#iGRIDs GRID}s for
   * {@link fraclac.analyzer.GrayCounter gray scale} scans.
   *
   * In particular, it returns psGrayAllGRIDs unless it is
   * {@link #UNINITIALIZED}, in which case it returns psAllGRIDs.
   *
   * @param psAllGRIDs string corresponding to the first string used in the
   * constructor for the enum that is being considered
   * @param psOneGRID string corresponding to the second string used in the
   * constructor for the enum that is being considered
   * @param psGrayAllGRIDs string corresponding to the third string used in the
   * constructor for the enum that is being considered
   * @param psGrayOneGRID string corresponding to the fourth string used in the
   * constructor for the enum that is being considered
   *
   * @return String
   */
  public String sGrayAllGRIDs(String psAllGRIDs,
                              String psOneGRID,
                              String psGrayAllGRIDs,
                              String psGrayOneGRID)
  {
    if (UNINITIALIZED.equals(psGrayAllGRIDs)) {
      return psAllGRIDs;
    }
    return psGrayAllGRIDs;
  }

  /**
   * Returns one of the headings associated with this enum constant, in a
   * hierarchical manner. The first choice is the 4th heading in the
   * constructor, which is intended to correspond to data summarized over one
   * {@link fraclac.analyzer.Vars#iGRIDs GRID} for
   * {@link fraclac.analyzer.GrayCounter gray scale} scans.
   *
   * In particular, it returns the String stored in psGrayOneGRID unless it is
   * {@link #UNINITIALIZED UNINITIALIZED} and psGrayAllGRIDs is not, in which
   * case it returns the result from a call to {@link #sGrayAllGRIDs
   * sGrayAllGRIDs()}; but if psGrayAllGRIDs is also UNINITIALIZED, then it
   * returns the result of a call to {@link #sOneGRID sOneGRID}.
   *
   * @param psAllGRIDs string corresponding to the first string used in the
   * constructor for the enum that is being considered
   * @param psOneGRID string corresponding to the second string used in the
   * constructor for the enum that is being considered
   * @param psGrayAllGRIDs string corresponding to the third string used in the
   * constructor for the enum that is being considered
   * @param psGrayOneGRID string corresponding to the fourth string used in the
   * constructor for the enum that is being considered
   *
   * @return String
   */
  public String sGrayOneGRID(String psAllGRIDs,
                             String psOneGRID,
                             String psGrayAllGRIDs,
                             String psGrayOneGRID)
  {

    if (UNINITIALIZED.equals(psGrayOneGRID)
        && UNINITIALIZED.equals(psGrayAllGRIDs)) {

      return sOneGRID(psAllGRIDs,
                      psOneGRID,
                      psGrayAllGRIDs,
                      psGrayOneGRID);
    }

    if (UNINITIALIZED.equals(psGrayOneGRID)) {
      return sGrayAllGRIDs(psAllGRIDs,
                           psOneGRID,
                           psGrayAllGRIDs,
                           psGrayOneGRID);
    }

    return psGrayOneGRID;

  }

}
