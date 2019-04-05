import java.util.ArrayList;

/**
 * Represents a hand of cards in this simplified version of Blackjack.
 */


public class Hand {
	private ArrayList<Card> cards;
	private int totalValue;

	public Hand() {
		cards = new ArrayList<Card>();
		totalValue = 0;
	}

	// Add a card to the hand
	public void addCard(Card c) {
		cards.add(c);
	}

	public int countCards(){
	    int count=0;
	    for(Card card: cards)
        {
            count++;
        }
        return count;
    }


	public ArrayList<Card> getCards() {
		return cards;
	}

	public void updateTotalValue() {
		int v = 0;
		
		for(Card c:cards) {
			v += c.getValue();
		}

		totalValue=v;
	}

	public int getTotalValue()
    {
        updateTotalValue();
	    return totalValue;
    }

    public void clearHand()
    {
        cards.clear();
    }

    }
