import java.io.*;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * This class is the main executable for using our Perceptron neural net. <br>
 * Dr. Jiang Project 1
 * @author Rory Abraham
 * @author Powell Vince
 * @since 10/15/18
 */
public class ProjectDriver
{
    public static void main(String[] args) throws FileNotFoundException //FileNotFoundException is handled in getFile()
    {
        Perceptron perceptron = null;   //if perceptron cannot be initialized program will quit
        Scanner userInput = new Scanner(System.in);

        System.out.println("Welcome to my first neural network - A Perceptron Net!\n");
        System.out.println("Enter 1 to train using a training data file, " +
                "or Enter 2 to use a pre-trained weight settings file:");
        String trainingChoice = userInput.nextLine();

        if(trainingChoice.contains("1"))
        {
            //TODO: Input Error Handling
            //Gather training settings/info
            TrainingProfile trainingProfile = new TrainingProfile();

            System.out.print("\nEnter the training data file name:\n");
            trainingProfile.setTrainingFile(getFile(userInput.nextLine(),userInput));

            System.out.print("\nEnter 0 to initialize weights to 0, enter 1 to initialize weights to random values " +
                    "between -0.5 and 0.5:\n");
            trainingProfile.setRandomWeights(userInput.nextLine().contains("1"));

            System.out.print("\nEnter the Maximum number of training epochs:\n");
            trainingProfile.setMaxEpochs(userInput.nextInt());

            userInput.nextLine(); // flush garbage

            System.out.print("\nEnter a file name to save the trained weight settings:\n");
            trainingProfile.setWeightSaveFile(makeFile(userInput.nextLine(), userInput));

            System.out.print("\nEnter the learning rate alpha from 0 to 1 but NOT including 0:\n");
            trainingProfile.setLearningRate(userInput.nextDouble());

            System.out.print("\nEnter the threshold theta:\n");
            trainingProfile.setActivationThreshold(userInput.nextDouble());

            System.out.print("\nEnter the threshold to be used for measuring weight changes:\n");
            trainingProfile.setWeightChangeThreshold(userInput.nextDouble());

            perceptron = train(trainingProfile);

        }
        else if (trainingChoice.contains("2"))
        {
            //Test using pre-trained weight settings
            System.out.print("\nEnter the training weight settings input data file name:\n");
            File trainedWeightsFile = getFile(userInput.nextLine(), userInput);

            perceptron = createNetFromTrainedWeights(trainedWeightsFile);

        }
        else
        {
          System.out.print("\nInvalid input");
          System.exit(0);
        }

        //Begin Testing Code Block
        userInput.nextLine();
        //userInput = new Scanner(System.in);

        System.out.println("\nEnter 1 to test/deploy using a testing/deploying data file, enter 2 to quit:");
        String testOrQuit = userInput.nextLine();

        if (testOrQuit.contains("1"))
        {
          System.out.print("\nEnter the testing/deploying data file name:\n");
          File inputTestingFile = getFile(userInput.nextLine(), userInput);

          System.out.print("\nEnter a file name to save the testing/deploying results:\n");
          File outputTestingFile = makeFile(userInput.nextLine(), userInput);

          test(perceptron, inputTestingFile, outputTestingFile);
        }
        else
        {
          System.out.println("Good bye!");
          System.exit(0);
        }

        userInput.close();
    }

    /**
     * A method to test the net for classification accuracy, given a set of inputs and target outputs
     *
     * @param perceptron the trained neural net we want to test
     * @param inputFile the file containing the input/targetOutputs (formatted like the training file), must have same
     *                  dimensionality as training file
     * @param outputFile the file to write the results to
     * @throws FileNotFoundException because that exception is handled in getFile()
     */
    public static void test(Perceptron perceptron, File inputFile, File outputFile) throws FileNotFoundException
    {
        //TODO: enforce data dimensionality same
        //First, get data dimensionality
        Scanner data = new Scanner(inputFile);

        int inputDimension = Integer.parseInt(data.nextLine().split("\t")[0]);
        int outputDimension = Integer.parseInt(data.nextLine().split("\t")[0]);
        int num_of_patterns = Integer.parseInt(data.nextLine().split("\t")[0]);

        //Next, get full Training set
        LinkedHashSet<TrainingPair> testingSet = new LinkedHashSet<>();

        //TODO: Test this block!
        //iterate over each training pattern given in data file
        for (int i = 0; i < num_of_patterns; i++)
        {
            int[] inputArray = new int[inputDimension];
            int[] targetArray = new int[outputDimension];

            data.nextLine();    //advance scanner according to given data format

            //read in input from data file
            for(int j = 0; j < inputDimension; j++)
            {
                inputArray[j] = data.nextInt();
            }

            data.nextLine();    //advance scanner according to given data format

            //read in target output from data file
            for(int j = 0; j < outputDimension; j++)
            {
                targetArray[j] = data.nextInt();
            }


            //read in patternID
            char patternName = data.next().charAt(0);

            //advance scanner one line lines
            data.nextLine();

            TrainingPair tp = new TrainingPair(inputArray,targetArray,patternName);
            testingSet.add(tp);
        }

        data.close();

        //Now, we have the full testing set
        perceptron.setConverged(true);

        FileWriter fileWriter =  null;
        PrintWriter writer = null;

        try
        {
            int correctCount = 0;

            fileWriter = new FileWriter(outputFile,true);    //true tells fileWriter to append
            writer = new PrintWriter(fileWriter);
            for (TrainingPair tp : testingSet)
            {
                writer.println("\nActual Output:");
                writer.printf("%s\n", tp.getPatternID());
                for (int j = 0; j < tp.getTargetArray().length; j++)
                    writer.printf("%d ", tp.getTargetArray()[j]);
                writer.print("\nClassified Output:\n");

                //test input to compute perceptron output
                perceptron.setInputNeurons(tp.getInputArray());
                perceptron.computeOutput();

                boolean correctlyClassified = true;

                //compare perceptron output to target output
                for (int j = 0; j < perceptron.getOutputNeurons().length; j++)
                {
                    if (tp.getTargetArray()[j] != perceptron.getOutputNeurons()[j])
                        correctlyClassified = false;
                }

                //if correctly classified
                if (correctlyClassified)
                {
                    correctCount++;
                    writer.printf("%s\n", tp.getPatternID());
                    for (int j = 0; j < perceptron.getOutputNeurons().length; j++)
                        writer.printf("%d ", perceptron.getOutputNeurons()[j]);

                    writer.print("\n");
                }

                // if mistakenly classified as a different pattern
                else if (isRecognizedPattern(perceptron.getOutputNeurons()))
                {
                    //find which pattern perceptron thought it was
                    int patternIDNUM = 0;
                    for(int j = 0; j < perceptron.getOutputDimensionality(); j++)
                    {
                        if(perceptron.getOutputNeurons()[j] == 1)
                            patternIDNUM = j;
                    }

                    switch(patternIDNUM)
                    {
                        case 0:
                            writer.println("A");
                            break;
                        case 1:
                            writer.println("B");
                            break;
                        case 2:
                            writer.println("C");
                            break;
                        case 3:
                            writer.println("D");
                            break;
                        case 4:
                            writer.println("E");
                            break;
                        case 5:
                            writer.println("J");
                            break;
                        case 6:
                            writer.println("K");
                            break;
                    }

                    for (int j = 0; j < perceptron.getOutputNeurons().length; j++)
                        writer.printf("%d ", perceptron.getOutputNeurons()[j]);

                    writer.print("\n");
                }

                //could not be recognized
                else
                {
                    writer.print("Unknown\n");
                    for (int j = 0; j < perceptron.getOutputDimensionality(); j++)
                        writer.printf("%d ", perceptron.getOutputNeurons()[j]);

                    writer.print("\n");
                }
            }

            double classificationAccuracy = ((double) correctCount / (double) testingSet.size()) * 100;

            writer.print("\nOverall Classification Accuracy:\n");
            writer.printf("%.2f\n", classificationAccuracy);
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        finally {
            try
            {
                writer.close();
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Validates that a given output pattern is one of the possible outputs
     * @param classifiedOutput the output produced by the perceptron
     * @return true if classifiedOutput has exactly one positive 1 and no zeros
     */
    private static boolean isRecognizedPattern(int[] classifiedOutput)
    {
        int posCount = 0;
        for(int i: classifiedOutput)
        {
            if(i == 0)
                return false;
            if(i == 1)
                posCount++;
        }

        return (posCount == 1);
    }

    /**
     * A method to initialize a perceptron from pre-determined weight settings
     * @param trainedWeightsFile a specially formatted file for saving weights and other data
     * @return a (pre)trained perceptron
     * @throws FileNotFoundException because that is handled in getFile()
     */
    private static Perceptron createNetFromTrainedWeights(File trainedWeightsFile) throws FileNotFoundException
    {
      Scanner data = new Scanner(trainedWeightsFile);

      //get data dimensionality
      int inputDimension = Integer.parseInt(data.nextLine());
      int outputDimension = Integer.parseInt(data.nextLine());

      double learningRate = Double.parseDouble(data.nextLine());
      double activationThreshold = Double.parseDouble(data.nextLine());
      double convergenceSensitivity = Double.parseDouble(data.nextLine());

      //get weights and biases for each output neuron
      double[] biasNeurons = new double[outputDimension];
      double[][] weightMatrix = new double[inputDimension][outputDimension];

      for (int j = 0; j < outputDimension; j++)
      {
        for (int i = 0; i < inputDimension; i++)
        {
          weightMatrix[i][j] = data.nextDouble();
        }
        biasNeurons[j] = data.nextDouble();
      }

      data.close();

      //initialize perceptron
      return new Perceptron(learningRate, activationThreshold, convergenceSensitivity,
                                             weightMatrix, biasNeurons, inputDimension, outputDimension);

    }

    /**
     * A method for creating and training a perceptron net based on user-specified TrainingProfile
     * @param profile A collection of user specifications, including filepath for training input
     * @return A trained perceptron net
     * @throws FileNotFoundException because that is handled by getFile()
     */
    private static Perceptron train(TrainingProfile profile) throws FileNotFoundException
    {
        //First, get data dimensionality
        Scanner data = new Scanner(profile.getTrainingFile());
        int inputDimension = Integer.parseInt(data.nextLine().split("\t")[0]);
        int outputDimension = Integer.parseInt(data.nextLine().split("\t")[0]);
        int num_of_patterns = Integer.parseInt(data.nextLine().split("\t")[0]);

        //Next, get full Training set
        LinkedHashSet<TrainingPair> trainingSet = new LinkedHashSet<>();

        //TODO: Test this block!
        //iterate over each training pattern given in data file
        for (int i = 0; i < num_of_patterns; i++)
        {
            int[] inputArray = new int[inputDimension];
            int[] targetArray = new int[outputDimension];

            data.nextLine();    //advance scanner according to given data format

            //read in input from data file
            for(int j = 0; j < inputDimension; j++)
            {
                inputArray[j] = data.nextInt();
            }

            data.nextLine();    //advance scanner according to given data format

            //read in target output from data file
            for(int j = 0; j < outputDimension; j++)
            {
                targetArray[j] = data.nextInt();
            }

            //read in patternID
            char patternName = data.next().charAt(0);
            //advance scanner one line lines
            data.nextLine();

            TrainingPair tp = new TrainingPair(inputArray,targetArray,patternName);
            trainingSet.add(tp);
        }

        data.close();

        //Now, we have the full training set

        //initialize perceptron fields
        double[][] weightMatrix = new double[inputDimension][outputDimension];
        double[] biasNeurons = new double[outputDimension];

        if(profile.randomWeights())
        {
            //set all weights and biases randomly between -0.5 and 0.5
            Random rand = new Random();
            for(int j = 0; j < outputDimension; j++)
            {
                biasNeurons[j] = rand.nextDouble() - 0.5;
                for(int i = 0; i < inputDimension; i++)
                    weightMatrix[i][j] = rand.nextDouble() - 0.5;
            }
        }
        else
        {
            //set all weights and biases to 0
            for(int j = 0; j < outputDimension; j++)
            {
                biasNeurons[j] = 0;
                for(int i = 0; i < inputDimension; i++)
                    weightMatrix[i][j] = 0;
            }
        }

        Perceptron perceptron = new Perceptron(profile.getLearningRate(),
                                                    profile.getActivationThreshold(),
                                                    profile.getWeightChangeThreshold(),
                                                    weightMatrix, biasNeurons, inputDimension, outputDimension);

        //Now, we have a Perceptron object!!!

        // Start training
        int epochCount = 0;
        long startTime = System.nanoTime();

        while(!perceptron.isConverged() && epochCount < profile.getMaxEpochs())
        {
            epochCount++;
            perceptron.setConverged(true);
            for(TrainingPair tp: trainingSet)
            {
              perceptron.setInputNeurons(tp.getInputArray());
              perceptron.computeOutput();
              if(perceptron.updateWeights(tp.getTargetArray()) > profile.getWeightChangeThreshold())
                  perceptron.setConverged(false);
            }
        }

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;

        if (perceptron.isConverged())
        {
          System.out.println("Training converged after " + epochCount + " epochs.");
          perceptron.saveWeights(profile.getWeightSaveFile());
        }
        else
        {
          System.out.println("Training did not converge after " + epochCount + " epochs.");
          System.exit(0);
        }

        return perceptron;
    }

    /**
     * A utility function to get a reference to a file from a string filepath<br>
     * Will continue prompting until a valid filepath is produced)
     * @param filepath A string filepath to the desired file from the working directory
     * @param userInput A scanner connected to System.in
     * @return File at address filepath
     */
    private static File getFile(String filepath, Scanner userInput)
    {
        boolean fileNotFound = false;
        File file = new File(filepath);
        do
        {
            try
            {
                fileNotFound = false;
                //if input file does not exist or is a directory
                if (!file.exists() || file.isDirectory())
                {
                    fileNotFound = true;
                    //throw FileNotFound
                    throw new FileNotFoundException();
                }
            }
            catch (FileNotFoundException e)
            {
                //if file not found program exits (for simplicity)
                System.out.println("File not found! Please enter a valid filepath!");
                file = new File(userInput.nextLine());
            }
        }
        while(fileNotFound);

        return file;
    }

    /**
     * A utility function to create a new file from specified filename.<br>
     * Gives user options if they want to overwrite a file with same name.
     * @param filename A string filepath for the file to create
     * @param scanner a scanner so user can interact
     * @return a new File object
     */
    private static File makeFile(String filename, Scanner scanner)
    {
        boolean overwrite = false;
        do {
            try {
                File file = new File(filename);
                {
                    if (file.createNewFile())
                    {
                        //System.out.println("File created!");
                        //System.out.print("Filepath: " + file.getAbsolutePath() + "\n");
                        System.out.print("\n");
                        return file;
                    }
                    else
                    {
                        System.out.println("File: " + filename + " already exists!" + "\n"
                                + "Would you like to overwrite existing file? (enter y for yes or n for no)");
                        String response = scanner.nextLine().toLowerCase();

                        if (response.contains("y"))
                        {
                            if (file.delete())
                            {
                                overwrite = true;
                            }
                            else
                            {
                                System.out.println("File " + filename + "could not be deleted. Exiting...");
                                System.exit(0);
                            }
                        }
                        else
                        {
                            makeFile(filename + "_new", scanner);
                        }
                    }
                }
            }
            catch (IOException io) {
                System.out.println("File could not be created. Exiting...");
                System.exit(0);
            }
        }
        while(overwrite);
        return null;
    }
}
