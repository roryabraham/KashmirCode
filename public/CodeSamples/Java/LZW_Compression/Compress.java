import java.io.*;
import java.text.DecimalFormat;
import java.util.Scanner;

// DISCLAIMER: THIS PROGRAM CANNOT COMPRESS ANY UNICODE CHARACTERS, ONLY ASCII CODE
public class Compress
{
    final static int MIN_TABLE_SIZE = 501;

    public static void main(String[] args) throws IOException
    {
        //pass filename to compression method
        String inputFilePath = args[0];
        boolean compressAgain = false;

        compress(inputFilePath);

        System.out.println("Would you like to compress another file? (y for yes or n for no)");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine().toLowerCase();
        if(response.contains("y"))
            compressAgain = true;

        while(compressAgain)
        {
            compressAgain = false;
            System.out.println("Please enter the name of the file you would like to compress.");
            String filename = scanner.nextLine();
            compress(filename);

            System.out.println("Would you like to compress another file? (y for yes or n for no");
            String str = scanner.nextLine().toLowerCase();
            if(str.contains("y"))
                compressAgain = true;
        }

        //close System.in scanner
        scanner.close();
    }


    private static void compress(String inputFilePath) throws IOException
    {
        //method fields
        SeparateChainedHashTable dictionary;
        short nextCode;

        //open input file scanner
        File inputFile = getFile(inputFilePath);
        Scanner input = new Scanner(inputFile);

        //save output to file.zzz
        File outputFile = makeFile(inputFilePath+".zzz");
        DataOutputStream output = new DataOutputStream(new FileOutputStream(outputFile));

        //setup scanner to read one character of input at a time
        input.useDelimiter("");

        //log initial file size in bytes and starting time
        long rawFileSize = inputFile.length();
        float initialFileSize = (float) (inputFile.length()/1000);
        long startTime = System.currentTimeMillis();

        //Initialize dictionary for all 255 ASCII characters
        dictionary = new SeparateChainedHashTable(getTableSize(rawFileSize));
        for(short i = 0; i < 255; i++)
        {
            dictionary.insert(Character.toString((char) i), i);
        }

        //set nextCode value
        nextCode = 255;

        char pendingChar = '~';
        boolean hasPendingChar = false;

        //loop through entire input file
        while(input.hasNext())
        {
            //build a string prefix
            StringBuilder prefix = new StringBuilder();

            if(hasPendingChar)
            {
                prefix.append(pendingChar);
            }

            //let c be the next character after the prefix
            char c;

            //find longest (non-empty) prefix that is already a key in the dictionary
            while (input.hasNext())
            {
                c = input.next().charAt(0);

                //building prefix
                if (dictionary.containsKey(prefix.toString() + c) && input.hasNext())
                    { prefix.append(c); }
                else
                {
                    //set pending character
                    pendingChar = c;
                    hasPendingChar = true;

                    //if there is an entry for prefix
                    if(dictionary.get(prefix.toString()) >= 0)
                    {
                        //output code corresponding to prefix
                        output.writeShort(dictionary.get(prefix.toString()));
                    }
                    else
                    {
                        System.out.println("Compression Failed");
                        System.exit(0);
                    }

                    //create a new dictionary entry with key: prefix + c
                    dictionary.insert(prefix.toString() + c, nextCode);

                    nextCode++;

                    //System.out.println(prefix.toString() + c + " => " + dictionary.get(prefix.toString() + c));
                    break;
                }
            }
        }

        //output coded version of the pending character (condition hasNext is false)
        output.writeShort(dictionary.get(String.valueOf(pendingChar)));

        //close out
        output.close();
        input.close();

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        float compressedSize = (float) (outputFile.length()/1000);

        PrintWriter writer = new PrintWriter(makeFile(inputFilePath + ".zzz.log"));
        DecimalFormat df = new DecimalFormat("#.###");

        //Log entry
        StringBuilder log = new StringBuilder();
        log.append("Compression of ");
        log.append(inputFilePath);
        log.append("\n");

        log.append("Compressed from ");
        log.append(df.format(initialFileSize));
        log.append(" Kilobytes to ");
        log.append(df.format(compressedSize));
        log.append(" Kilobytes.");
        log.append("\n");

        log.append("Compression took ");
        log.append(executionTime);
        log.append(" milliseconds.");
        log.append("\n");

        log.append("Hash table has load factor of ");
        log.append(df.format(dictionary.loadFactor()));
        log.append("\n");

        log.append("The average linked list is ");
        log.append(df.format(dictionary.averageListLength()));
        log.append(" elements long.");
        log.append("\n");

        log.append("The longest linked list is ");
        log.append(dictionary.longestList());
        log.append(" elements long");
        log.append("\n");

        log.append("The dictionary contains ");
        log.append(dictionary.getSize());
        log.append(" total entries");
        log.append("\n");

        log.append("The table was rehashed ");
        log.append(dictionary.getRehashCount());
        log.append(" times");
        log.append("\n");

        writer.print(log);
        writer.close();
    }

    private static int getTableSize(long fileSize)
    {
        if(fileSize <= 1000)
            return MIN_TABLE_SIZE;
        else if(fileSize < 20000)
            return (int) (fileSize * 500);
        else
            return (int) (20000 + (fileSize * 500));
    }


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
