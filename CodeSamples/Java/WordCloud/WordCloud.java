package sample;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;


public class WordCloud extends Application {

    //My Absolute FilePaths:
    ///Users/rory/IdeaProjects/WordCloud/src/sample/Raven.txt
    ///Users/rory/IdeaProjects/WordCloud/src/sample/stop-words.txt


    //make these GUI elements static so they can be accessed from displayWords()
    private static FlowPane flow = new FlowPane();
    private Scene scene = new Scene(flow, 1000, 800, Color.CORNSILK);
    private TreeMap<String, Integer> treeMap = new TreeMap<>();


    @Override
    public void start(Stage primaryStage) throws Exception{
        //set up opening stage/scene
        primaryStage.setTitle("WorldCloud");
        AnchorPane anchor = new AnchorPane();
        Scene inputScene = new Scene(anchor,600, 300, Color.CORNSILK);
        primaryStage.setScene(inputScene);

        //set up stage for fileChooser
        Stage chooserStage = new Stage();

        //create nodes for opening stage
        Label welcome = new Label("Welcome to WordCloud");
        Label prompt = new Label("Please enter a filepath or find a text file on your computer.");
        Label pathLabel = new Label("Enter pathname...");
        Label orLabel = new Label("Or...");
        TextField pathname = new TextField();
        Button search = new Button("Search");
        Button cancel = new Button("Cancel");
        Button findFile = new Button("Find a file on my computer");

        //create container nodes
        FlowPane labels = new FlowPane(Orientation.VERTICAL,welcome,prompt);
        FlowPane searchBar = new FlowPane(Orientation.VERTICAL,pathLabel,pathname);
        HBox buttons = new HBox(30,cancel,search);
        BorderPane searchSide = new BorderPane();


        //style the nodes
        welcome.setFont(new Font("Impact", 28));
        welcome.setTextAlignment(TextAlignment.CENTER);
        prompt.setFont(Font.font("SansSerif", FontWeight.EXTRA_LIGHT,16));
        labels.setAlignment(Pos.TOP_CENTER);
        labels.setColumnHalignment(HPos.CENTER);
        pathLabel.setFont(Font.font("SansSerif", FontWeight.EXTRA_LIGHT,12));
        orLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, FontPosture.ITALIC,18));
        searchBar.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);


        //add nodes from the opening scene to pane(s)
        searchSide.setCenter(searchBar);
        searchSide.setBottom(buttons);

        //IF PROGRAM DOESN'T COMPILE, COMMENT OUT LINES 88-96
        //Create background image, doesn't account for fileNotFound error handling
            Image image = new Image(
                    "/sample/clouds.jpg",
                    600.0,300.0,false, false);
        BackgroundImage bg = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        //add background image to opening stage
        anchor.setBackground(new Background(bg));

        //add completed panes to AnchorPane
        anchor.getChildren().add(labels);
        anchor.getChildren().add(searchSide);
        anchor.getChildren().add(orLabel);
        anchor.getChildren().add(findFile);

        //position the nodes
        AnchorPane.setLeftAnchor(labels,40.0);
        AnchorPane.setRightAnchor(labels, AnchorPane.getLeftAnchor(labels));

        AnchorPane.setTopAnchor(searchSide,50.0);
        AnchorPane.setBottomAnchor(searchSide, AnchorPane.getTopAnchor(searchSide));
        AnchorPane.setLeftAnchor(searchSide, 50.0);

        AnchorPane.setLeftAnchor(orLabel,275.0);
        AnchorPane.setRightAnchor(orLabel, AnchorPane.getLeftAnchor(orLabel));
        AnchorPane.setTopAnchor(orLabel,125.0);
        AnchorPane.setBottomAnchor(orLabel,AnchorPane.getTopAnchor(orLabel));

        AnchorPane.setTopAnchor(findFile,125.0);
        AnchorPane.setBottomAnchor(findFile,AnchorPane.getTopAnchor(findFile));
        AnchorPane.setRightAnchor(findFile,65.0);


        //use Lambda functions instead of inner classes to define button event handlers
        search.setOnAction(event -> {
            //get the searchTerm from the text field
            String searchTerm = pathname.getText();
            //find a file
            File source = getFile(searchTerm);

            //set up the scene for the WordCloud (because now we're done with the opening scene)
            primaryStage.setTitle("WordCloud");
            primaryStage.setScene(scene);

            //get treeMap of words/frequency from file
            try{
                treeMap = getWordsFromFile(source);
            }
            catch(FileNotFoundException e){
                System.out.println(e.getMessage());
            }

            //sort map in order of descending value
            TreeMap<String,Integer> sortedMap = sortMapByValue(treeMap);

            //create the WordCloud
            displayWords(sortedMap);

            primaryStage.show();
        });

        //another lambda function for the Find File button
        findFile.setOnAction(event -> {
            //create a new FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Text File");

            //set up an extension filter so that only .txt or .html files can be selected
            String extensionFilterDescription = "Please use only .txt or .html files";
            List<String> extensionTypes = new ArrayList<>();
                //chose these two filetypes sort of arbitrarily... but .pdf .doc or .docx files can't be read by Scanner
                //while these two for sure can be read
                extensionTypes.add(".txt");
                extensionTypes.add(".html");
            //create the extension filter object
            FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter(extensionFilterDescription,extensionTypes);
            //set the extension filter on the fileChooser instance
            fileChooser.setSelectedExtensionFilter(extensionFilter);

            //fetch a file from the fileChooser---shouldn't have to worry about FileNotFoundException
            File source = fileChooser.showOpenDialog(chooserStage);

            //set up the scene for the WordCloud
            primaryStage.setTitle("WordCloud");
            primaryStage.setScene(scene);

            //get treeMap of words/frequency from file
            try{
                treeMap = getWordsFromFile(source);
            }
            catch(FileNotFoundException e){
                System.out.println(e.getMessage());
            }

            //sort map in order of descending value
            TreeMap<String,Integer> sortedMap = sortMapByValue(treeMap);

            //Create the WordCloud
            displayWords(sortedMap);

            primaryStage.show();
        });
        cancel.setCancelButton(true);

        //...now that we've taken care of our event handlers...

        //show input scene
        primaryStage.show();
    }


    //method to get the file
    public File getFile(String searchTerm){
        //create an instance of the file class
        File textFile = new File(searchTerm);
        try{
            //if the file exists and is not a directory
            if(textFile.exists() && !textFile.isDirectory())
            {
                //return the file
                return textFile;
            }
            //else, throw error
            else{ throw new FileNotFoundException();}
        }
        //error handling, quit program for simplicity
        catch(FileNotFoundException e)
        {
            //alert that file was not found
            Alert badAlert = new Alert(Alert.AlertType.INFORMATION);
            badAlert.setTitle("File Not Found!");
            badAlert.setHeaderText(null);
            badAlert.setContentText("Error: File not found!");
            badAlert.showAndWait();

            //exit the program
            System.exit(0);
        }
        //return file
        return textFile;
    }

    public TreeMap<String,Integer> getWordsFromFile(File file) throws FileNotFoundException
    {
        //Scanner for file
        Scanner fileScan = new Scanner(file);

        //list to contain unique words
        List<String> words = new ArrayList<>();

        //scan through file
        while(fileScan.hasNext())
        {
            //add all the tokens to an ArrayList, all lower case
            words.add(fileScan.next().toLowerCase());
        }

        for(int i = 0; i<words.size(); i++)
        {
            //remove all punctuations except apostrophes
            String s = words.get(i);
            words.set(i,s.replaceAll("[^a-zA-Z’]",""));
        }

        //remove stop words from list
        //first, find the stopWords file
        try{
            File stopWords = new File("/Users/rory/IdeaProjects/WordCloud/src/sample/stop-words.txt");
            if(!stopWords.exists()||stopWords.isDirectory())
            {
                throw new FileNotFoundException();
            }


            //loop through each element of stopWords
            Scanner stopWordsScanner = new Scanner(stopWords);
            while(stopWordsScanner.hasNext()){
                //convert to lowercase, declare local variable
                String token = stopWordsScanner.next().toLowerCase();

                //if a word is in stopWords
                if(words.contains(token))
                {
                    //remove it from our List words
                    words.removeAll(Collections.singleton(token));
                }
            }
        }
        catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        //treemap to map unique words to their number of occurrences
        TreeMap<String, Integer> frequencyMap = new TreeMap<>();

        //loop for each string in ArrayList
        for(String s: words)
        {
            //if the word has been looped over before (if the word is already a key in our treemap)
            if (frequencyMap.containsKey(s))
            {
                //create a new key/value with the same key, but incrementing int value by 1
                //to document a repeated occurrence
                frequencyMap.put(s,frequencyMap.get(s)+1);
            }
            //else (this is the first occurrence of the word in the list)
            else{
                frequencyMap.put(s,1);
            }
        }

        //a useful method for testing
        //printMap(frequencyMap);

        return frequencyMap;
    }

    //this method builds the WordCloud from the TreeMap
    public static void displayWords(TreeMap<String, Integer> map)
    {
        Point2D center = new Point2D(500,400);
        Circle circle = new Circle(500);

        //loop through each map entry in map.entrySet()
        for (Map.Entry<String, Integer> entry : map.entrySet()){

            //create temporary variables for the String key and int Value
            String temp = entry.getKey();
            int tempInt = entry.getValue();

            //Depending on length of the file, it may be necessary to disinclude unique words used only once (hence line 317)
            //if(entry.getValue() > 1)
            {
                //build the label with the String key
                Label myLabel = new Label(temp);
                //set Font size according to int value
                myLabel.setFont(new Font("Impact",(9*tempInt)));
                //set random color (from custom method) to each label
                myLabel.setTextFill(getRandomColor());
                //set a random opacity
                double randOpacity = 0.2 + (Math.random()/1.25);
                myLabel.setOpacity(randOpacity);


                flow.setHgap(10);
                flow.setVgap(2);
                flow.getChildren().add(myLabel);
            }
        }
    }

    //method returns a random integer 1-5
    public static int getRandomInt(int min, int max)
    {
        Random rand = new Random();
        int random = rand.nextInt((max-min)+1)+min;
        return random;
    }

    public static Color getRandomColor()
    {
        Color randomColor = Color.BLUEVIOLET;


        int rand = getRandomInt(1,7);

        switch(rand)
        {
            case 1:
                randomColor = Color.POWDERBLUE;
                break;
            case 2:
                randomColor = Color.MEDIUMTURQUOISE;
                break;
            case 3:
                randomColor = Color.KHAKI;
                break;
            case 4:
                randomColor = Color.LIGHTSALMON;
                break;
            case 5:
                randomColor = Color.ORANGERED;
                break;
            case 6:
                randomColor = Color.CORNFLOWERBLUE;
                break;
            case 7:
                randomColor = Color.PALEVIOLETRED;
                break;
        }

        return randomColor;
    }


    public static void printMap(Map<String,Integer> map) {
        // Get a set of the entries
        Set set = map.entrySet();

        // Create an iterator
        Iterator it = set.iterator();

        // Display elements
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            System.out.print("Key is: " + me.getKey() + " & ");
            System.out.println("Value is: " + me.getValue());
        }
    }

    //sorts TreeMap by value (number of occurrences) in descending order
    public TreeMap<String,Integer> sortMapByValue(TreeMap<String, Integer> initialMap)
    {
        Comparator<String> valueComparator = new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2) {
                    int compare = -initialMap.get(o1).compareTo(initialMap.get(o2));
                    if (compare == 0) {
                        return -1;
                    }
                    else
                        return compare;
                }
            };
        TreeMap<String,Integer> sortedMap = new TreeMap<>(valueComparator);
        sortedMap.putAll(initialMap);

        //this is the map we will be using to make our WordCloud
        printMap(sortedMap);
        return sortedMap;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
