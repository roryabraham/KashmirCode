import java.io.File;

/**
 * An object to hold the training settings and data.
 * @author Rory Abraham
 * @author Powell Vince
 * @since 10/15/18
 */
public class TrainingProfile
{
    /**
     * the file from which training data should be pulled
     */
    private File trainingFile;

    /**
     * the file to save weight settings for easy future use
     */
    private File weightSaveFile;

    /**
     * a boolean flag for whether or not the user wants weights initialized randomly
     */
    private boolean randomWeights;

    /**
     * the maximum allowable training epochs
     */
    private int maxEpochs;

    /**
     * the learning rate "alpha"
     */
    private double learningRate;

    /**
     * the activation threshold "theta"
     */
    private double activationThreshold;

    /**
     * the minimum value for which a weight change is not considered negligible
     */
    private double weightChangeThreshold;

    /**
     * A default constructor for TrainingProfile
     * Sets files to null
     * All other fields are initialized to "typical" values by default
     */
    public TrainingProfile()
    {
        this.trainingFile = null;
        this.weightSaveFile = null;
        this.randomWeights = false;
        this.maxEpochs = 100;
        this.learningRate = 1;
        this.activationThreshold = 1;
        this.weightChangeThreshold = 0.00001;
    }

    /**
     * A constructor that could be used to initialize all fields at once
     * @param trainingFile the file from which training data should be pulled
     * @param weightSaveFile the file to save weight settings for easy future use
     * @param randomWeights a boolean flag for whether or not the user wants weights initialized randomly
     * @param maxEpochs the maximum allowable training epochs
     * @param learningRate the learning rate "alpha"
     * @param activationThreshold the activation threshold "theta"
     * @param weightChangeThreshold the minimum value for which a weight change is not considered negligible
     */
    public TrainingProfile(File trainingFile,
                           File weightSaveFile,
                           boolean randomWeights,
                           int maxEpochs,
                           double learningRate,
                           double activationThreshold,
                           double weightChangeThreshold)
    {
        this.trainingFile = trainingFile;
        this.weightSaveFile = weightSaveFile;
        this.randomWeights = randomWeights;
        this.maxEpochs = maxEpochs;
        this.learningRate = learningRate;
        this.activationThreshold = activationThreshold;
        this.weightChangeThreshold = weightChangeThreshold;
    }

    @Override
    public String toString() {
        return "TrainingProfile{\n" +
                "trainingFile = " + trainingFile +
                ", \nweightSaveFile = " + weightSaveFile +
                ", \nrandomWeights = " + randomWeights +
                ", \nmaxEpochs = " + maxEpochs +
                ", \nlearningRate = " + learningRate +
                ", \nactivationThreshold = " + activationThreshold +
                ", \nweightChangeThreshold = " + weightChangeThreshold +
                '}';
    }

    public File getTrainingFile() { return trainingFile; }
    public void setTrainingFile(File trainingFile) { this.trainingFile = trainingFile; }

    public File getWeightSaveFile() { return weightSaveFile; }
    public void setWeightSaveFile(File weightSaveFile) { this.weightSaveFile = weightSaveFile; }

    public boolean randomWeights() { return randomWeights; }
    public void setRandomWeights(boolean randomWeights) { this.randomWeights = randomWeights; }

    public int getMaxEpochs() { return maxEpochs; }
    public void setMaxEpochs(int maxEpochs) { this.maxEpochs = maxEpochs; }

    public double getLearningRate() { return learningRate; }
    public void setLearningRate(double learningRate) { this.learningRate = learningRate; }

    public double getActivationThreshold() { return activationThreshold; }
    public void setActivationThreshold(double activationThreshold) { this.activationThreshold = activationThreshold; }

    public double getWeightChangeThreshold() { return weightChangeThreshold; }
    public void setWeightChangeThreshold(double weightChangeThreshold) { this.weightChangeThreshold = weightChangeThreshold; }
}
