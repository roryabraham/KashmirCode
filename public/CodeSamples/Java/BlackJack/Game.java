import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This is a simplified version of a common card game, "21"
 * for Assignment 4. 
 *
 * The players take turns requesting cards, trying to get
 * as close to 21 as possible, but not going over 21. A player
 * may stand (ask for no more cards). Once a player has passed,
 * he or she cannot later ask for another card. When all three
 * players have passed, the game ends.
 *
 * The winner is the player who has come closest to 21 without
 * exceeding it. In the case of a tie, or if everyone goes over
 * 21, no one wins.
 * 
 * Per the assignment, we assume exactly three players. The game 
 * is only played once.
 */
public class Game extends Application {

	// An Array to store the (exactly) 3 Player objects
	private static Player[] players = new Player[3];
    private static int playerIndex=0;
    private static Player dealer;
    private static Player player1;
    private static Player player2;
    private static Player player3;

	//Player declaration to point to whichever player's turn it is currently
    private static Player currentPlayer;


    private static AnchorPane table;
    private static FlowPane turnFlow;
    private static Label currentScore;
    private static Button playButton;
    private static Button playAgainButton;
    private static Button exitButton;
    private static FlowPane dealerCards;
    private static FlowPane playerOneCards;
    private static FlowPane playerTwoCards;
    private static FlowPane playerThreeCards;

    private static Stage turnStage;

	// An ArrayList to store the deck of cards
    private static ArrayList<Card> cardDeck;

    private final double MAIN_SCENE_WIDTH=850.0;
    private final double MAIN_SCENE_HEIGHT=530.0;
    private double leftToCenterAnchor = 270;
    private final double edgeInset = 25;
	
	public void start(Stage primaryStage) {
		//Create players
        player1 = new Player("Player One");
        player2 = new Player("Player Two");
        player3 = new Player("Player Three");
        dealer = new Player("Dealer");

        //add players (but not dealer) to ArrayList<Player>
        players[0]=player1;
        players[1]=player2;
        players[2]=player3;

        //Create a deck
        cardDeck = new ArrayList<Card>();
		initDeck(cardDeck);

		//shuffle the deck
        Collections.shuffle(cardDeck);

        //deal two cards to each player
        deal();

        //create a borderPane for the main Stage
        BorderPane mainBorder = new BorderPane();
        mainBorder.setBackground(new Background(new BackgroundFill(Color.DARKGREEN.desaturate().desaturate()
                                                ,CornerRadii.EMPTY,
                                                Insets.EMPTY)));

        //Display title logo in mainBorder
        try{
            Image titleLogo = new Image(new FileInputStream("images/blackjack-logo-big.jpg"),700,80,true,true);
            ImageView titleLogoView = new ImageView();
            titleLogoView.setImage(titleLogo);
            mainBorder.setTop(titleLogoView);
            BorderPane.setAlignment(titleLogoView,Pos.CENTER);
        }
        catch(FileNotFoundException e)
        {
            e.getMessage();
        }

        //create Player labels
        Label playerOneLabel = new Label("Player One");
        playerOneLabel.setRotate(90.0);
        playerOneLabel.setFont(Font.font("HeadLineA",22));

        Label playerTwoLabel = new Label("Player Two");
        playerTwoLabel.setFont(Font.font("HeadLineA",22));

        Label playerThreeLabel = new Label("Player Three");
        playerThreeLabel.setRotate(270.0);
        playerThreeLabel.setFont(Font.font("HeadLineA",22));

        //add player labels to mainBorder
        mainBorder.setLeft(playerOneLabel);
        mainBorder.setBottom(playerTwoLabel);
        mainBorder.setRight(playerThreeLabel);

        //center labels in mainBorder regions
        BorderPane.setAlignment(playerOneLabel,Pos.CENTER_LEFT);
        BorderPane.setAlignment(playerTwoLabel,Pos.BOTTOM_CENTER);
        BorderPane.setAlignment(playerThreeLabel,Pos.CENTER_RIGHT);

        //create an AnchorPane for the main stage...goes in mainBorder center
        table = new AnchorPane();

        //Create a FlowPane for dealer's cards...add to AnchorPane (top)...
        dealerCards = new FlowPane();
        dealerCards.setPrefWrapLength(250.0);
        //displayHand(dealer,dealerCards);
        table.getChildren().add(dealerCards);
        AnchorPane.setTopAnchor(dealerCards,edgeInset);
        AnchorPane.setLeftAnchor(dealerCards,leftToCenterAnchor);
        AnchorPane.setRightAnchor(dealerCards,leftToCenterAnchor-(dealer.getHand().countCards()*70));

        //Create a FlowPane for Player one's cards...add to AnchorPane (Left)
        playerOneCards = new FlowPane(Orientation.VERTICAL);
        //displayHand(player1,playerOneCards);
        table.getChildren().add(playerOneCards);
        AnchorPane.setLeftAnchor(playerOneCards,edgeInset);
        AnchorPane.setBottomAnchor(playerOneCards,-75.0);

        //Create a FlowPane for Player Two's cards...add to AnchorPane (Bottom)
        playerTwoCards = new FlowPane();
        playerTwoCards.setPrefWrapLength(180);
        //displayHand(player2,playerTwoCards);
        table.getChildren().add(playerTwoCards);
        AnchorPane.setBottomAnchor(playerTwoCards,AnchorPane.getTopAnchor(dealerCards));
        AnchorPane.setLeftAnchor(playerTwoCards,leftToCenterAnchor);
        AnchorPane.setRightAnchor(playerTwoCards,leftToCenterAnchor-(player2.getHand().countCards()*70));

        //Create a FlowPane for Player Three's cards...add to AnchorPane (Right)
        playerThreeCards = new FlowPane(Orientation.VERTICAL);
        //displayHand(player3,playerThreeCards);
        table.getChildren().add(playerThreeCards);
        AnchorPane.setRightAnchor(playerThreeCards,edgeInset);
        AnchorPane.setBottomAnchor(playerThreeCards,-75.0);

        //create Play! button...add to AnchorPane
        playButton = new Button("Play!");
        table.getChildren().add(playButton);
        playButton.setOnAction(new PlayEventHandler());
        AnchorPane.setTopAnchor(playButton, 200.0);
        AnchorPane.setBottomAnchor(playButton,AnchorPane.getTopAnchor(playButton));
        AnchorPane.setLeftAnchor(playButton,leftToCenterAnchor+15);
        AnchorPane.setRightAnchor(playButton,AnchorPane.getLeftAnchor(playButton)-15);

        //create Play Again! button...add to AnchorPane
        playAgainButton = new Button("Play Again!");
        table.getChildren().add(playAgainButton);
        playAgainButton.setVisible(false);
        playAgainButton.setOnAction(new PlayAgainEventHandler());
        AnchorPane.setTopAnchor(playAgainButton, 200.0);
        AnchorPane.setBottomAnchor(playAgainButton,AnchorPane.getTopAnchor(playButton));
        AnchorPane.setLeftAnchor(playAgainButton,leftToCenterAnchor+15);
        AnchorPane.setRightAnchor(playAgainButton,AnchorPane.getLeftAnchor(playButton)-15);

        //create exit button...add to AnchorPane
        exitButton = new Button("Exit");
        table.getChildren().add(exitButton);
        exitButton.setVisible(false);
       exitButton.setOnAction(new ExitEventHandler());
        AnchorPane.setTopAnchor(exitButton, 200.0 + 30.0);
        AnchorPane.setLeftAnchor(exitButton,leftToCenterAnchor+15);
        AnchorPane.setRightAnchor(exitButton,AnchorPane.getLeftAnchor(playButton)-15);

        //Add AnchorPane to center of mainBorder
        mainBorder.setCenter(table);

        initializeTurnStage();

		// Create main Scene
		Scene scene = new Scene(mainBorder, MAIN_SCENE_WIDTH, MAIN_SCENE_HEIGHT);

		// Make changes to the main stage
		primaryStage.setTitle("Blackjack"); // Set the stage title

		// Place the scene in the stage
		primaryStage.setScene(scene);

		//prevent resizing of window
		primaryStage.setResizable(false);

		// Display the stage	
		primaryStage.show();

        //display cards in same order deal method occurs
        animateDeal();
	}

	public void initializeTurnStage()
    {
        //for now...
        currentPlayer=player1;

        //create Hit button and customize
        Button hitButton = new Button("Hit me!");
        hitButton.setPrefSize(200,50);
        hitButton.setStyle("-fx-font: 22 SignPainter");

        //create Stand button and customize
        Button standButton = new Button("Stand");
        standButton.setPrefSize(200,50);
        standButton.setStyle("-fx-font: 22 SignPainter");

        //set event handlers for buttons
        hitButton.setOnAction(new HitEventHandler());
        standButton.setOnAction(new StandEventHandler());

        //create Player's turn stage, borderPane layout
        turnStage = new Stage();
        BorderPane turnBorder = new BorderPane();

        //create FlowPane for card nodes in Player's turn stage
        turnFlow = new FlowPane();
        //set wrap width for cards, just in case
        turnFlow.setPrefWidth(300);

        //create HBox for hit and stand buttons
        HBox turnHBox = new HBox(35);
        turnHBox.setAlignment(Pos.CENTER);

        //add Buttons to HBox
        turnHBox.getChildren().add(hitButton);
        turnHBox.getChildren().add(standButton);

        //create label for current player's score
        currentScore = new Label("Score: "+Integer.toString(currentPlayer.getScore()));
        currentScore.setFont(Font.font("HeadLineA",32));

        //display current player's cards
        displayHand(currentPlayer,turnFlow);


        //add nodes to turnBorder
        turnBorder.setCenter(turnFlow);
        turnBorder.setBottom(turnHBox);
        turnBorder.setRight(currentScore);
        BorderPane.setAlignment(currentScore,Pos.CENTER_RIGHT);
        BorderPane.setAlignment(turnFlow,Pos.CENTER_LEFT);

        //create turn scene
        Scene turnScene = new Scene(turnBorder, 500, 400);


        //make changes to turn stage
        turnStage.setTitle(currentPlayer.getName() + "'s Turn");

        //place scene in stage
        turnStage.setScene(turnScene);

    }

	public static void initDeck(ArrayList<Card> d) {

		String[] suits = new String[] {"C", "D", "H", "S"};
		String[] faces = new String[] {"J", "Q", "K", "A"};
		
		for (String s: suits) {           
	        for(int i = 2; i < 11; i++) {
	        		// Create and add a new card for each of the numbered cards
	        		String name = i + s; 
	        		String imagePath = "images/" + name + ".png";
	        		Card c = new Card(name, imagePath, i);
	        		d.add(c);
	        }
	    }
		for (String s:suits) {           
	        for(String f:faces) {
	        		// Create and add a new card for each of the face cards
	        		String name = f + s; 
	        		String imagePath = "images/" + name + ".png";
	        		
	        		if( f.equals("A") )
	        			d.add(new Card(name, imagePath, 11));
	        		else	
	        			d.add(new Card(name, imagePath, 10));
	        }
	    } 
	}

	public static void deal(){
	    //go around the table passing cards from top of deck
        player1.getHand().addCard(getTopCardFromDeck());
        player2.getHand().addCard(getTopCardFromDeck());
        player3.getHand().addCard(getTopCardFromDeck());

        //dealer's first card must be face-down
        Card dealersFirstCard = getTopCardFromDeck();
        dealersFirstCard.setFacing(false);
        dealer.getHand().addCard(dealersFirstCard);

        player1.getHand().addCard(getTopCardFromDeck());
        player2.getHand().addCard(getTopCardFromDeck());
        player3.getHand().addCard(getTopCardFromDeck());
        dealer.getHand().addCard(getTopCardFromDeck());
    }

    //displays the cards in the order they're dealt in the deal method
    public static void animateDeal()
    {
        displayCard(player1.getHand().getCards().get(0),playerOneCards);
        displayCard(player2.getHand().getCards().get(0),playerTwoCards);
        displayCard(player3.getHand().getCards().get(0),playerThreeCards);
        displayCard(dealer.getHand().getCards().get(0),dealerCards);
        displayCard(player1.getHand().getCards().get(1),playerOneCards);
        displayCard(player2.getHand().getCards().get(1),playerTwoCards);
        displayCard(player3.getHand().getCards().get(1),playerThreeCards);
        displayCard(dealer.getHand().getCards().get(1),dealerCards);
    }

    public static Card getTopCardFromDeck()
    {
        //take top card from deck
        Card card = cardDeck.get(0);
        //remove card from deck, so it is not given out again
        cardDeck.remove(0);
        return card;
    }

    public static void displayCard(Card card, FlowPane flow)
    {
        if(card.isFaceUp())
        {
            try{
                //create imageView from card
                Image image = new Image(new FileInputStream(card.getImagePath()),70,100,false,false);
                ImageView cardImageView = new ImageView();
                cardImageView.setImage(image);

                //add imageView to flowPane
                flow.getChildren().add(cardImageView);

                //create path transition
                PathTransition pathTransition = new PathTransition(Duration.millis(1500),
                        new Line(cardImageView.getX()+35,
                                 cardImageView.getY()-500,
                                 cardImageView.getX()+35,
                                 cardImageView.getY()+50),
                        cardImageView);

                //create fade transition
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(1500),cardImageView);
                fadeTransition.setFromValue(0.01);
                fadeTransition.setToValue(1.0);

                //create parallel transition to play both animations at once
                ParallelTransition parallelTransition = new ParallelTransition();
                parallelTransition.getChildren().add(pathTransition);
                parallelTransition.getChildren().add(fadeTransition);
                parallelTransition.play();
            }
            catch (FileNotFoundException e){
                System.out.println(e.getMessage());
            }
        }
        else{
            try{
                //create imageView from card
                Image image = new Image(new FileInputStream("images/BACK.jpg"),70,100,false,false);
                ImageView cardImageView = new ImageView();
                cardImageView.setImage(image);

                //add imageView to flowPane
                flow.getChildren().add(cardImageView);

                //create path transition
                PathTransition pathTransition = new PathTransition(Duration.millis(1500),
                        new Line(cardImageView.getX()+35,
                                 cardImageView.getY()-500,
                                 cardImageView.getX()+35,
                                 cardImageView.getY()+50),
                        cardImageView);

                //create fade transition
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(1500),cardImageView);
                fadeTransition.setFromValue(0.01);
                fadeTransition.setToValue(1.0);

                //create parallel transition to play both animations at once
                ParallelTransition parallelTransition = new ParallelTransition();
                parallelTransition.getChildren().add(pathTransition);
                parallelTransition.getChildren().add(fadeTransition);
                parallelTransition.play();
            }
            catch (FileNotFoundException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void displayHand(Player player, FlowPane flow)
    {
        //create ImageView arrayList
        ArrayList<ImageView> images = new ArrayList<>();

        //display each card in the current player's hand in the flowPane
        for (Card card: player.getHand().getCards()){
            if(card.isFaceUp()) {
                try {
                    Image image = new Image(new FileInputStream(card.getImagePath()), 70, 100, false, false);
                    ImageView cardImageView = new ImageView();
                    cardImageView.setImage(image);
                    images.add(cardImageView);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }

            //if the card is face down display the images/BACK.jpg
            else{
                try{
                    Image image = new Image(new FileInputStream("images/BACK.jpg"),70,100,false,false);
                    ImageView cardImageView = new ImageView();
                    cardImageView.setImage(image);
                    images.add(cardImageView);
                }
                catch(FileNotFoundException e){
                    System.out.println(e.getMessage());
                }
            }
        }

        flow.getChildren().clear();

        for(ImageView i: images)
        {
            flow.getChildren().add(i);
        }
    }

    public static Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    public static void nextPlayer()
    {
        displayHand(players[0],playerOneCards);
        displayHand(players[1],playerTwoCards);
        displayHand(players[2],playerThreeCards);

        playerIndex++;

        if (playerIndex>2)
        {
            turnStage.close();

            //execute dealer's turn
            dealerTurn();

            //determine the winner
            switch(findWinner(dealer,player1,player2,player3))
            {
                case 0: createWinnerAlert(dealer);
                    break;
                case 1: createWinnerAlert(player1);
                    break;
                case 2: createWinnerAlert(player2);
                    break;
                case 3: createWinnerAlert(player3);
                    break;
                case 4: createTieAlert();
                    break;
            }
        }
        else {
            currentPlayer = players[playerIndex];
            displayHand(currentPlayer, turnFlow);
            turnStage.setTitle(currentPlayer.getName() + "'s Turn");
            currentScore.setText("Score: " + Integer.toString(currentPlayer.getScore()));
        }
    }

    public static void createWinnerAlert(Player winner)
    {
        Alert winnerAlert = new Alert(Alert.AlertType.INFORMATION);
        winnerAlert.setTitle(winner.getName()+ " wins!");
        winnerAlert.setHeaderText("Winner Winner Chicken Dinner!");
        winnerAlert.setContentText(winner.getName()+ " wins!");
        winnerAlert.showAndWait();

        //redisplay the play button, set up play button to create new deck, shuffle, and play game again
        playAgainButton.setVisible(true);
        exitButton.setVisible(true);
    }

    public static void createTieAlert() {
        Alert winnerAlert = new Alert(Alert.AlertType.INFORMATION);
        winnerAlert.setTitle("Tie Game!");
        winnerAlert.setHeaderText("Sorry! Go Home!");
        winnerAlert.showAndWait();

        playAgainButton.setVisible(true);
        exitButton.setVisible(true);
    }

    //iteration of while loop
    public static void dealerTurn()
    {
        //first, flip dealer's face-down card
        dealer.getHand().getCards().get(0).setFacing(true);
        displayHand(dealer,dealerCards);

        while(dealer.getScore()<=16)
        {
            //dealer must hit
            dealer.hit();
            ArrayList<Card> cards =currentPlayer.getHand().getCards();
            Game.displayCard(cards.get(cards.size()-1),dealerCards);
            AnchorPane.setLeftAnchor(dealerCards,AnchorPane.getLeftAnchor(dealerCards)-35);
            AnchorPane.setRightAnchor(dealerCards,AnchorPane.getRightAnchor(dealerCards)-35);
        }

        System.out.println("Game over");
    }

    public static int findWinner(Player dealer, Player player1, Player player2, Player player3) {
        //0 == dealer wins
        //1 == player1 wins
        //2 == player2 wins
        //3 == player3 wins
        //4 == tie

        int[] scores = new int[4];

        //final scores are determined by 21 - getScore()
        scores[0] = 21 - dealer.getScore();
        scores[1] = 21 - player1.getScore();
        scores[2] = 21 - player2.getScore();
        scores[3] = 21 - player3.getScore();

        double minimum = 21;
        int winner = 0;
        int a = 0;
        for(int i = 0; i < 4; i++){
            if(scores[i] < minimum && scores[i] >= 0 && scores[i] != scores[winner]){
                minimum = scores[i];
                winner = i;
                a++;
            }
        }

        if(a < 0){return 4;}
        else{return winner;}
    }

public class HitEventHandler implements EventHandler<ActionEvent>
{
    @Override
    public void handle(ActionEvent event)
    {
        Game.currentPlayer.hit();
        Game.currentScore.setText("Score: "+Integer.toString(currentPlayer.getScore()));
        Game.displayHand(currentPlayer, turnFlow);

        //center cards
        if(currentPlayer==player1)
        {
            ArrayList<Card> cards =currentPlayer.getHand().getCards();
            Game.displayCard(cards.get(cards.size()-1),playerOneCards);
            AnchorPane.setBottomAnchor(playerOneCards,AnchorPane.getBottomAnchor(playerOneCards)+50);
        }
        if(currentPlayer==player2){
            ArrayList<Card> cards =currentPlayer.getHand().getCards();
            Game.displayCard(cards.get(cards.size()-1),playerTwoCards);
            AnchorPane.setLeftAnchor(playerTwoCards,AnchorPane.getLeftAnchor(playerTwoCards)-35);
            AnchorPane.setRightAnchor(playerTwoCards,AnchorPane.getRightAnchor(playerTwoCards)-35);
        }
        if(currentPlayer==player3){
            ArrayList<Card> cards =currentPlayer.getHand().getCards();
            Game.displayCard(cards.get(cards.size()-1),playerThreeCards);
            AnchorPane.setBottomAnchor(playerThreeCards,AnchorPane.getBottomAnchor(playerThreeCards)+50);
        }
        if(currentPlayer.isBusted())
        {
            Alert bustedAlert = new Alert(Alert.AlertType.INFORMATION);
            bustedAlert.setTitle("Too bad!");
            bustedAlert.setHeaderText("BUSTED!");
            bustedAlert.setContentText("Better luck next time!");
            bustedAlert.showAndWait();

            Game.nextPlayer();
        }
    }
}
public class StandEventHandler implements EventHandler<ActionEvent>
{
    @Override
    public void handle(ActionEvent event)
    {
        Game.nextPlayer();
    }
}
public class PlayEventHandler implements EventHandler<ActionEvent>
{
    @Override
    public void handle(ActionEvent event)
    {
        playButton.setVisible(false);

        //display the turnStage
        turnStage.show();
    }
}

public class PlayAgainEventHandler implements EventHandler<ActionEvent>
{
    @Override
    public void handle(ActionEvent event)
    {
        playAgainButton.setVisible(false);
        exitButton.setVisible(false);

        //remove all cards from flowPanes on the table
        dealerCards.getChildren().clear();
        playerOneCards.getChildren().clear();
        playerTwoCards.getChildren().clear();
        playerThreeCards.getChildren().clear();

        //remove all cards from all Player's hands
        dealer.getHand().clearHand();
        player1.getHand().clearHand();
        player2.getHand().clearHand();
        player3.getHand().clearHand();

        //Create a new deck
        cardDeck = new ArrayList<Card>();
        initDeck(cardDeck);

        //shuffle
        Collections.shuffle(cardDeck);

        //deal again
        deal();
        animateDeal();

        //reset player index
        playerIndex = 0;

        initializeTurnStage();

        playButton.setVisible(true);
    }
}

public class ExitEventHandler implements EventHandler<ActionEvent>
{
    @Override
    public void handle(ActionEvent event)
    {
        System.exit(0);
    }
}

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
  public static void main(String[] args) {
      launch(args);
  }
  
}