
/**
 * Represents a player in this simplified version of the
 * "21" card game.
 */

public class Player {

    /** The name of the player (used for printing purposes). */
    private String name;
    private Hand hand;
   
    
    public Player (String n) {
        name = n;
        hand = new Hand();
    }

    /**
     * Return the value of the player's hand. 
     */
    public int getScore() {
        int v;
        v = hand.getTotalValue();
        if(v>21){
            //for each card in a player's hand
            for(Card card: hand.getCards())
            {
                //if the card is an ace
                if(card.getValue()==11)
                {
                    //change the value to an ace
                    card.setValue(1);
                    if(hand.getTotalValue()<=21)
                    {
                        v = hand.getTotalValue();
                        break;
                    }
                }
            }
        }
        return v;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//use this method to access hand object associated with player (used for adding cards to their hand)
	public Hand getHand() {
        this.hand.updateTotalValue();
        return this.hand;
    }

    //method to check if player has busted (to be used after every time hit method is called)
    public boolean isBusted()
    {
        if(this.getScore()>21) {
            return true;
        }
        else {
            return false;
        }
    }

    public void hit()
    {
        //add the top card from the deck to the player's hand
        this.getHand().addCard(Game.getTopCardFromDeck());

        System.out.println(this.getHand().getTotalValue()+"HIT EVENT");

        if(Game.getCurrentPlayer().isBusted())
        {
            System.out.println("BUSTED");
        }
    }
}
