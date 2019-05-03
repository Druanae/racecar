package client;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* InfoPanel
 * Class to display all of the race information.
 */
public class InfoDisplay
	{
		// JLabels to store race information.
		protected JLabel positionFirst = new JLabel(""); // displays the player in first place.
		protected JLabel positionSecond = new JLabel(""); // displays the player in second place.
		protected JLabel playerOneLaps = new JLabel(""); // displays the lap count if the red car.
		protected JLabel playerTwoLaps = new JLabel(""); // displays the lap count of the blue car.
		protected JLabel gameOver = new JLabel("GAME OVER"); // Game over message.
		
		// Class constructor
		public InfoDisplay(JPanel panel, int padding, int displayPos, int displayWidth, int displayHeight, int trackWidth)
		{
			// Height of labels is equal to the number of labels divided by the width of the display area.
			int labelHeight = (displayHeight - (trackWidth * 2)) / 4;  
			// Width of the labels is equal to the width of the display area.
			int labelWidth = (displayWidth - (displayPos * 2));
			
			// Set text colour to white.
			positionFirst.setForeground(Color.WHITE);
			positionSecond.setForeground(Color.WHITE);
			playerOneLaps.setForeground(Color.WHITE);
			playerTwoLaps.setForeground(Color.WHITE);
			gameOver.setForeground(Color.WHITE);
			
			// Add the labels to the JPanel.
			panel.add(positionFirst);
			panel.add(positionSecond);
			panel.add(playerOneLaps);
			panel.add(playerTwoLaps);
			panel.add(gameOver);
			
			// Set the size of each label.  
			positionFirst.setSize(labelWidth, labelHeight);
			positionSecond.setSize(labelWidth, labelHeight);
			playerOneLaps.setSize(labelWidth, labelHeight);
			playerTwoLaps.setSize(labelWidth, labelHeight);
			gameOver.setSize(labelWidth, labelHeight);
			
			// Position each label below each other.
			positionFirst.setLocation( displayPos + padding, displayPos );
			positionSecond.setLocation( displayPos + padding, positionFirst.getLocation().y + labelHeight);
			playerOneLaps.setLocation(displayPos + padding, positionSecond.getLocation().y + labelHeight);
			playerTwoLaps.setLocation(displayPos + padding, playerOneLaps.getLocation().y + labelHeight);
			
			// Position game over message in middle of the window. 
			gameOver.setLocation((displayWidth / 2) - (gameOver.getSize().width / 2), (displayHeight / 2) - (labelHeight / 2) );
			gameOver.setHorizontalAlignment(JLabel.CENTER);
			
			positionFirst.setFont(new Font("SansSerif", Font.PLAIN, 16));
			positionSecond.setFont(new Font("SansSerif", Font.PLAIN, 16));
			playerOneLaps.setFont(new Font("SansSerif", Font.PLAIN, 16));
			playerTwoLaps.setFont(new Font("SansSerif", Font.PLAIN, 16));
			gameOver.setFont(new Font("SansSerif", Font.BOLD, 32));
			gameOver.setVisible(false);
		}
		
		protected void displayGameOver( boolean winStatus, String winnerName )
		{
			if ( winStatus )
			{
				gameOver.setText(winnerName + " Player Wins!"); 
				gameOver.setVisible(true);
			}
		}
		
		protected void displayLaps( RaceCar playerOne, RaceCar playerTwo, int maxLaps )
		{
			playerOneLaps.setText(playerOne.colour + " Player: Lap " + playerOne.stats.laps + "/" + maxLaps);
			playerTwoLaps.setText(playerTwo.colour + " Player: Lap " + playerTwo.stats.laps + "/" + maxLaps);
		}
		
		protected void displayPositions( RaceCar[] players )
		{
			positionFirst.setText("1st Place: " + players[0].colour);
			positionSecond.setText("2st Place: " + players[1].colour);
		}
	}