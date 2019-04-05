import java.io.*;
import java.util.Scanner;

public class Decompress
{
    final static int MIN_TABLE_SIZE = 1001;
    public static void main(String[] args) throws IOException
    {
        //open input file scanner
        String inputFilePath = args[0];
        File inputFile = getFile(inputFilePath + ".zzz");

        decompress(inputFile, inputFilePath);

        Scanner userInput = new Scanner(System.in);
        boolean repeat = false;

        do{
            System.out.println("\n"+ "Would you like to decompress another file?");
            String response = userInput.nextLine().toLowerCase();
            if(response.contains("y"))
            {
                repeat = true;
                System.out.println("Please enter the name of the file you would like to decompress.");
                String filepath = userInput.nextLine();
                File file = getFile(filepath + ".zzz");
                decompress(file, filepath);
            }
            else
                repeat = false;
        }
        while(repeat);
    }

    private static void decompress(File inputFile, String outputFilePath) throws IOException
    {
        StringBuilder log = new StringBuilder();
        log.append("Decompression for file: ");
        log.append(outputFilePath + ".zzz");
        log.append("\n");

        long startTime = System.currentTimeMillis();

        long fileSize = inputFile.length();

        DataInputStream input = new DataInputStream(new FileInputStream(inputFile));
        StringBuilder output = new StringBuilder();

        //Initialize dictionary for all 255 ASCII characters
        Ideal_HashTable dictionary = new Ideal_HashTable(getTableSize(fileSize));

        for (short i = 0; i <  255; i++)
        {
            dictionary.insert(i, Character.toString((char) i));
        }

        short nextCode = 255;

        //Output the string corresponding to the first code in the input file
        short previousCode = input.readShort();
        output.append(dictionary.get(previousCode));
        //System.out.println(output.toString());


        while(true)
        {
            try
            {
                short p = input.readShort();

                if (dictionary.containsKey(p)) {
                    output.append(dictionary.get(p));
                    //System.out.println(output.toString());
                    dictionary.insert(nextCode, dictionary.get(previousCode) + dictionary.get(p).charAt(0));
                    //System.out.println(nextCode + " => " + dictionary.get(nextCode));
                    nextCode++;
                    previousCode = p;
                } else {
                    String str = dictionary.get(previousCode) + dictionary.get(previousCode).charAt(0);
                    output.append(str);
                    //System.out.println(output.toString());
                    dictionary.insert(p, str);
                    //System.out.println(p + " => " + str);
                    nextCode++;
                    previousCode = p;
                }
            }
            catch(EOFException e)
            {
                break;
            }
        }

        PrintWriter writer = new PrintWriter(makeFile(outputFilePath));
        writer.print(output.toString());
        writer.close();

        long endTime = System.currentTimeMillis();
        float executionTime = (float) (endTime - startTime);

        log.append("Decompression took ");
        log.append(executionTime);
        log.append(" milliseconds.");
        log.append("\n");

        log.append("The table was rehashed ");
        log.append(dictionary.getRehashCount());
        log.append(" times.");

        PrintWriter logWriter = new PrintWriter(makeFile(outputFilePath + ".log"));
        logWriter.print(log.toString());
        logWriter.close();
    }

    private static int getTableSize(long fileSize)
    {
        if(fileSize <= 1000)
            return MIN_TABLE_SIZE;
        else if(fileSize < 20000)
            return (int) (fileSize * 500);
        else if(fileSize < 50000)
            return (int) (20000 + (fileSize * 500));
        else
            return (int) (40000 + (fileSize * 500));
    }

    //attempts to open file, if cannot then prompts user for filename, returns a file
    private static File getFile(String filename)
    {
        File file = new File(filename);
        boolean fileNotFound;

        try
        {
            if(!file.exists() && !file.isDirectory())
            {
                throw new FileNotFoundException();
            }
            else
            {
                System.out.println("File Found!");
            }
        }
        catch(FileNotFoundException e)
        {
            fileNotFound = true;
            System.out.println("File not found");

            //Open scanner for console input
            Scanner scanner = new Scanner(System.in);

            while (fileNotFound) {
                //Prompt user for filename
                System.out.println("Please enter a filename for the text file you would like to compress.");

                //Attempt to open file
                file = new File(scanner.nextLine());

                fileNotFound = false;

                //Error handling
                try {

                    if (!file.exists() || file.isDirectory()) {
                        throw new FileNotFoundException();
                    } else {
                        System.out.println("File found!");
                    }
                } catch (FileNotFoundException fe) {
                    fileNotFound = true;
                    System.out.println("File not found");
                }
            }

            //close scanner
            scanner.close();
        }
        return file;
    }

    //creates a new file
    private static File makeFile(String filename)
    {
        boolean overwrite = false;
        do {
            try {
                File file = new File(filename);
                {
                    if (file.createNewFile())
                    {
                        System.out.println("File created!");
                        System.out.print("Filepath: " + file.getAbsolutePath() + "\n");
                        System.out.print("\n");
                        return file;
                    }
                    else
                    {
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("File: " + filename + " already exists!" + "\n"
                                + " Would you like to overwrite existing file? (enter y for yes or n for no)");
                        String response = scanner.nextLine().toLowerCase();

                        if (response.contains("y"))
                        {
                            if (file.delete()) {
                                System.out.print("\n");
                                overwrite = true;
                            }
                            else {
                                System.out.println("File " + filename + "could not be deleted. Exiting...");
                            }
                        }
                        else
                        {
                            makeFile(filename + "_new");
                        }
                    }
                }
            }
            catch (IOException io) {
                System.out.println("File could not be created. Exiting...");
            }
        }
        while(overwrite);
        return null;
    }
}
