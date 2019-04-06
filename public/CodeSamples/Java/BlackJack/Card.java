
/**
 * Represents a card in this simplified version of Blackjack.
 */

public class Card {
	private String name;
	private String imagePath;
	private int value;

	//included this boolean value to be able to easily display the dealer's face-down card
	private boolean faceUp = true;
	
	public Card() {
		setName("");
		setImagePath("");
		setValue(0);
	}
	
	public Card(String n, String p, int v) {
		setName(n);
		setImagePath(p);
		setValue(v);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public String toString() {
		String r;
		r = "Name: " + name + " imagePath = " + imagePath + " value: " + value;
		return r;
	}

	public boolean isFaceUp(){ return faceUp; }
	public void setFacing(boolean upIsTrue){ faceUp = upIsTrue; }
}
