/*Name: AnimateQuickSort
Authors: Rory Abraham and Parth Bansal
Date: 12/19/17
Description: Generates an array of 20 random integers between 0 and 999. Animates one pass of the partition algorithm
        in QuickSort. */

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.Random;

public class AnimateQuickSort extends javafx.application.Application {

    private final double STAGE_WIDTH = 1000;
    private final double STAGE_HEIGHT = 500;

    //declared static so it can be manipulated from displayList method
    private static HBox listBox;

    //declared static so it can be reset from reset() method and passed again to step() method
    private static SpecialList specialList;

    //generates list of 20 random integers between 1 and 999
    private static int[] generateRandomList() {
        //create random number generator
        Random random = new Random();

        //initialize the array
        int[] list = new int[20];

        //fill the list with 20 random integers
        for (int i = 0; i < 20; i++) {
            //generate random integers
            int randInt = random.nextInt(999);
            list[i] = randInt;
        }
        return list;
    }

    public void start(Stage primaryStage) {
        //set title
        primaryStage.setTitle("Partition Algorithm for QuickSort");

        //container pane for main scene
        BorderPane border = new BorderPane();

        //main scene
        Scene scene = new Scene(border, STAGE_WIDTH, STAGE_HEIGHT);

        //buttons
        Button resetButton = new Button("Reset");
        resetButton.setMinWidth(150);
        resetButton.setMinHeight(50);
        Button stepButton = new Button("Step");
        stepButton.setMinWidth(150);
        stepButton.setMinHeight(50);

        //container for buttons
        HBox buttons = new HBox(65, stepButton, resetButton);
        buttons.setAlignment(Pos.CENTER);

        //elements for legend
        Circle pivotCircle = new Circle(10, Color.rgb(255,253,56));
        Label pivotLabel = new Label("Pivot:");
        HBox pivotHBox = new HBox(10, pivotLabel, pivotCircle);

        Circle lowCircle = new Circle(10, Color.LIME);
        Label lowLabel = new Label("Low:");
        HBox lowHBox = new HBox(10, lowLabel, lowCircle);

        Circle highCircle = new Circle(10, Color.RED);
        Label highLabel = new Label("High:");
        HBox highHBox = new HBox(10, highLabel, highCircle);

        Circle checkedCircle = new Circle(10,Color.BLUE);
        Label checkedLabel = new Label("Checked:");
        HBox checkedHBox = new HBox(10,checkedLabel,checkedCircle);

        Circle uncheckedCircle = new Circle(10,Color.BLACK);
        Label uncheckedLabel = new Label("Unchecked:");
        HBox uncheckedHBox = new HBox(10,uncheckedLabel,uncheckedCircle);

        //container for legend
        VBox legend = new VBox(35, pivotHBox, lowHBox, highHBox,uncheckedHBox,checkedHBox);
        legend.setAlignment(Pos.CENTER);

        //initialize list
        int[] list = generateRandomList();

        //initialize specialList
        specialList = new SpecialList(list,0,1,list.length-1);

        //container for values
        listBox = new HBox(15);
        listBox.setAlignment(Pos.BASELINE_CENTER);
        //listBox.setBackground(new Background(new BackgroundFill(Color.rgb(238,238,238),CornerRadii.EMPTY,Insets.EMPTY)));

        //add list values to container
        displayList(specialList);

        //add container nodes to scene layout
        border.setBottom(buttons);
        border.setRight(legend);
        border.setCenter(listBox);

        //position center node
        BorderPane.setMargin(listBox, new Insets(100, 0, 0, 0));

        //create event handler for step button
        stepButton.setOnAction(event -> {
            step(specialList);
        });

        //create event handler for reset button
        resetButton.setOnAction(event -> {
            reset();
        });

        //add scene to stage, show
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayList(SpecialList specialList) {
        //set minimum height of rectangle
        final int MIN_HEIGHT = 14;
        listBox.getChildren().clear();

        for (int i =0; i<specialList.getList().length; i++) {
            //create rectangle with height proportional to int value
            double height = (specialList.valueAt(i) / 3) + MIN_HEIGHT;

            //partition
            if(i==specialList.getPivotIndex())
            {
                //create rectangle with appropriate height and color
                Rectangle rect = new Rectangle(30,height,Color.rgb(255,253,56));

                //create a label to display int value
                String text = Integer.toString(specialList.valueAt(i));
                Label label = new Label(text);
                label.setTextFill(Color.BLACK);

                //stack label on top of rectangle
                StackPane stack = new StackPane(rect, label);

                //add stack object to HBox
                listBox.getChildren().add(stack);
            }

            //low
            else if(i==specialList.getLowIndex())
            {
                //create rectangle with appropriate height and color
                Rectangle rect = new Rectangle(30,height,Color.LIME);

                //create a label to display int value
                String text = Integer.toString(specialList.valueAt(i));
                Label label = new Label(text);
                label.setTextFill(Color.WHITE);

                //stack label on top of rectangle
                StackPane stack = new StackPane(rect, label);

                //add stack object to HBox
                listBox.getChildren().add(stack);
            }

            //high
            else if(i==specialList.getHighIndex())
            {
                //create rectangle with appropriate height and color
                Rectangle rect = new Rectangle(30,height,Color.RED);

                //create a label to display int value
                String text = Integer.toString(specialList.valueAt(i));
                Label label = new Label(text);
                label.setTextFill(Color.WHITE);

                //stack label on top of rectangle
                StackPane stack = new StackPane(rect, label);

                //add stack object to HBox
                listBox.getChildren().add(stack);
            }

            //checked
            else if((i < specialList.getLowIndex() && i > specialList.getPivotIndex()) ||
                        i > specialList.getHighIndex())
            {
                //create checked rectangle
                Rectangle rect = new Rectangle(30, height, Color.BLUE);

                //create a label to display int value
                String text = Integer.toString(specialList.valueAt(i));
                Label label = new Label(text);
                label.setTextFill(Color.WHITE);

                //stack label on top of rectangle
                StackPane stack = new StackPane(rect, label);

                //add stack object to HBox
                listBox.getChildren().add(stack);
            }


            //default unchecked
            else
            {
                //created default rectangle
                Rectangle rect = new Rectangle(30,height,Color.BLACK);

                //create a label to display int value
                String text = Integer.toString(specialList.valueAt(i));
                Label label = new Label(text);
                label.setTextFill(Color.WHITE);

                //stack label on top of rectangle
                StackPane stack = new StackPane(rect, label);

                //add stack object to HBox
                listBox.getChildren().add(stack);
            }
        }
    }

    private void step(SpecialList specialList)
    {

        //if high index is greater than low index
        if(specialList.getHighIndex() > specialList.getLowIndex())
        {
            //if value of low is <= value of pivot
            if(specialList.valueAt(specialList.getLowIndex()) <= specialList.valueAt(specialList.getPivotIndex()))
            {
                //increment low index
                specialList.setLowIndex(specialList.getLowIndex()+1);

                //redisplay list
                displayList(specialList);
            }

            //if value of high is > value of pivot
            else if(specialList.valueAt(specialList.getHighIndex()) > specialList.valueAt(specialList.getPivotIndex()))
            {
                //decrement high index
                specialList.setHighIndex(specialList.getHighIndex()-1);

                //redisplay list
                displayList(specialList);
            }

            //if value of low is > value of pivot AND value of high is < value of pivot
            else
            {
                //perform swap
                    int temp = specialList.valueAt(specialList.getHighIndex());

                    //set value at high index to value at low index
                    specialList.setValueAt(specialList.getHighIndex(),specialList.valueAt(specialList.getLowIndex()));

                    //set value at low index to value at high index
                    specialList.setValueAt(specialList.getLowIndex(),temp);

                    //redisplay list
                    displayList(specialList);
            }
        }

        //if index of low = index of high
        if(specialList.getHighIndex() == specialList.getLowIndex())
        {
            //if value of pivot > value of low
            if(specialList.valueAt(specialList.getPivotIndex()) > specialList.valueAt(specialList.getLowIndex()-1))
            {
                //swap pivot and low (values)
                    int temp = specialList.valueAt(specialList.getPivotIndex());

                    //set value at pivot to value at high
                    specialList.setValueAt(specialList.getPivotIndex(),specialList.valueAt(specialList.getLowIndex()-1));

                    //set value at high index to pivot
                    specialList.setValueAt(specialList.getLowIndex()-1,temp);

                //swap pivot and low (indexes)---for color/styling purposes
                    specialList.setPivotIndex(specialList.getLowIndex()-1);
                    specialList.setLowIndex(0);

                //redisplay list
                displayList(specialList);

                //tell user we're done and close program
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed!");
                alert.setHeaderText("All values to the left of the pivot are smaller than pivot, " +
                        "all values to the right are greater");
                alert.showAndWait();
                System.exit(0);
            }
        }
    }

    private void reset()
    {
        //initialize list
        int[] list = generateRandomList();

        //initialize SpecialList
        specialList = new SpecialList(list,0,1,list.length-1);

        //call displayList with new SpecialList
        displayList(specialList);
    }
}
