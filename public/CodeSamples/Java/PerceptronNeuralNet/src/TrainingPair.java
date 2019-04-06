/**
 * A class to hold both an array of inputs and a target array of intended outputs. <br>
 * For use in training neural nets with Perceptron.java and ProjectDriver.java <br>
 * This encapsulation is useful because it allows for iteration over input/target output pairs s:t <br>
 *
 * @author Rory Abraham
 * @author Powell Vince
 * @since 10/15/18
 */
public class TrainingPair
{
    private int[] inputArray;
    private int[] targetArray;

    /**
     * the name of the pattern associated with target array
     */
    private char patternID;

    /**
     * A constructor for a TrainingPair
     * @param s the input array
     * @param t the target array
     * @param patternID the name of the pattern associated with target array
     */
    public TrainingPair(int[] s, int[] t, char patternID)
    {
        this.inputArray = s;
        this.targetArray = t;
        this.patternID = patternID;
    }

    public int[] getInputArray() { return inputArray; }
    public void setInputArray(int[] s) { this.inputArray = s; }
    public int[] getTargetArray() { return targetArray; }
    public void setTargetArray(int[] t) { this.targetArray = t; }
    public char getPatternID() { return patternID; }
}
