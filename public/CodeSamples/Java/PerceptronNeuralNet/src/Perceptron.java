import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * A Perceptron Neural Net!
 * The instantiable class Perceptron.java is a neural net object
 * <br>
 * Dr. Jiang Comp 380 Project 1
 * <br>
 * <br>
 * CONVENTION FOR THIS PROGRAM IS THAT ITERATION OVER INPUT NEURONS SHOULD USE i,
 * WHILE ITERATION OVER OUTPUT NEURONS SHOULD USE j
 *
 * @author Rory Abraham
 * @author Powell Vince
 * @since 10/12/18
 */
public class Perceptron
{
    /**
     * A constant for comparing two doubles for equality
     */
    private static final double EQUALITY_THRESHOLD = 0.0001;

    /**
     * The value for which small weight changes will be considered negligible
     */
    private double convergenceSensitivity;

    private double learningRate;
    private double activationThreshold;
    private boolean converged = false;

    /**
     * ixj Matrix of weights Wij (rows are input neurons, columns are output neurons)
     */
    private double weightMatrix[][];

    /**
     * Array of inputs Xi
     */
    private int inputNeurons[];

    /**
     * array of bias weights b0-bj
     */
    private double biasNeurons[];

    /**
     * array of outputs Yj
     */
    private int outputNeurons[];

    /**
     * Constructor for a perceptron object
     *
     * @param learningRate the learning rate alpha
     * @param activationThreshold the activation threshold theta
     * @param convergenceSensitivity the measurement to determine whether a small weight change is negligible
     * @param weightMatrix A matrix (2D array) that contains weights (i,j) from input neurons (i) to output neurons (j)
     * @param biasNeurons An Array of neurons that have scalar impact on the activation of output neurons
     * @param inputDimension An Array of neurons that contain input values. These will change with each new TrainingPair
     * @param outputDimension An Array of neurons that determines the vector output of this neural net
     */
    public Perceptron(double learningRate,
                         double activationThreshold,
                         double convergenceSensitivity,
                         double[][] weightMatrix,
                         double[] biasNeurons,
                         int inputDimension,
                         int outputDimension)
    {
        this.learningRate = learningRate;
        this.activationThreshold = activationThreshold;
        this.convergenceSensitivity = convergenceSensitivity;
        this.weightMatrix = weightMatrix;
        this.biasNeurons = biasNeurons;
        this.inputNeurons = new int[inputDimension];
        this.outputNeurons = new int[outputDimension];
    }

    /**
     * An activation function for a single output neuron
     * @param outputNeuronIndex the index of the desired output neuron
     * @return 1 if y_in GREATER_THAN activationThreshold <br>
     *         0 if -activationThreshold LESS_THAN y_in LESS_THAN activationThreshold <br>
     *        -1 if y_in LESS_THAN activationThreshold
     */
    private int activation(int outputNeuronIndex)
    {
        double y_in = biasNeurons[outputNeuronIndex];
        for(int i = 0; i < inputNeurons.length; i++)
        {
            y_in += (inputNeurons[i] * weightMatrix[i][outputNeuronIndex]);
        }

        int activationValue;
        if (y_in > activationThreshold)
          activationValue = 1;
        else if (y_in < activationThreshold)
          activationValue = -1;
        else
          activationValue = 0;

        return activationValue;
    }

    /**
     * A public function to compute the output neurons' responses to the given input is stored in inputNeurons <br>
     * Saves outcome into outputNeurons field
     */
    public void computeOutput()
    {
        //for each output neuron
        for(int j = 0; j < outputNeurons.length; j++)
        {
            //compute activation function
            outputNeurons[j] = activation(j);
        }
    }

    /**
     * A function to update all the weights according to the perceptron learning rule
     * @param targetVals an array of the correct target outputs for the given input
     * @return the maximum change in weight
     */
    public double updateWeights(int targetVals[])
    {
        double maxChangeInWeight = 0;

        //for each output neuron
        for(int j = 0; j < outputNeurons.length; j++)
        {
            //if Yj != Tj
            if(Math.abs(targetVals[j] - outputNeurons[j]) > EQUALITY_THRESHOLD)
            {
                //update biases
                biasNeurons[j] += (learningRate * targetVals[j]);

                //check max weight change
                if(Math.abs(learningRate * targetVals[j]) > maxChangeInWeight)
                    maxChangeInWeight = Math.abs(learningRate * targetVals[j]);

                //update weights
                for(int i = 0; i < inputNeurons.length; i++)
                {
                    weightMatrix[i][j] += (learningRate * targetVals[j] * inputNeurons[i]);

                    //check max weight change
                    if(Math.abs(learningRate * targetVals[j] * inputNeurons[i]) > maxChangeInWeight)
                        maxChangeInWeight = Math.abs(learningRate * targetVals[j] * inputNeurons[i]);
                }
            }
        }

        return maxChangeInWeight;
    }


    /**
     * A method to save this perceptron's weights to a file in a specific format
     * @param weightSaveFile the file to write weight settings to
     */
    public void saveWeights(File weightSaveFile)
    {
      BufferedWriter outputFile = null;
      try {
          FileWriter fstream = new FileWriter(weightSaveFile, true); //true tells to append data.
          outputFile = new BufferedWriter(fstream);
          outputFile.write(Integer.toString(inputNeurons.length)+ "\n");
          outputFile.write(Integer.toString(outputNeurons.length)+ "\n");
          outputFile.write(Double.toString(learningRate)+ "\n");
          outputFile.write(Double.toString(activationThreshold)+ "\n");
          outputFile.write(Double.toString(convergenceSensitivity)+ "\n");
          for (int j = 0; j < outputNeurons.length; j++)
          {
            for (int i = 0; i < inputNeurons.length; i++)
            {
              outputFile.write(Double.toString(weightMatrix[i][j]) + " ");
            }
            outputFile.write(Double.toString(biasNeurons[j]));
            outputFile.write("\n");
          }


        }
      catch (IOException e) {
          System.err.println("Error: " + e.getMessage());
      }
      finally {
        try
        {
          outputFile.close();
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
      }
    }


    public double getLearningRate() { return learningRate; }
    public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
    public int[] getOutputNeurons() { return outputNeurons; }
    public void setOutputNeurons(int[] outputNeurons) { this.outputNeurons = outputNeurons; }
    public double[] getBiasNeurons() { return biasNeurons; }
    public void setBiasNeurons(double[] bias) { this.biasNeurons = bias; }
    public int[] getInputNeurons() { return inputNeurons; }
    public void setInputNeurons(int[] inputNeurons) { this.inputNeurons = inputNeurons; }
    public double[][] getWeightMatrix() { return weightMatrix; }
    public void setWeightMatrix(double[][] weightMatrix) { this.weightMatrix = weightMatrix; }
    public boolean isConverged() { return converged; }
    public void setConverged(boolean converged) { this.converged = converged; }
    public double getConvergenceSensitivity() { return convergenceSensitivity; }
    public void setConvergenceSensitivity(double convergenceSensitivity) { this.convergenceSensitivity = convergenceSensitivity; }
    public double getActivationThreshold() { return activationThreshold; }
    public void setActivationThreshold(double activationThreshold) { this.activationThreshold = activationThreshold; }

    public int getInputDimensionality() { return this.inputNeurons.length; }
    public int getOutputDimensionality() { return this.outputNeurons.length; }
}
