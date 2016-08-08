import javafx.application.Application;
import javafx.animation.Timeline;			//Timeline animations
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.text.DecimalFormat;
import javafx.event.ActionEvent;				//keyboard & events
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;						//gui stuff
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import java.util.Random;						//actually creating random numbers
import java.io.File;								//mostly for saving high scores
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
/**
 * This game replicates a simple app which displays a random number and the user must decide if it's even or odd.
 * A time limit is imposed, & they get bonus time if they get 10 correct in a row.
 * The game ends immediately when they guess incorrectly
 * 
 *  -------------GENERAL FLOW OF THE PROGRAM--------------
 *  The class extends application, so start() is called (A main method exists just in case & calls Application.launch(args) )
 *  setUpGUI() created the labels, scenes & panes for all stages of the game, then calls 3 methods once it's finished
 *  setUpAnimation() creates timelines for updating the timer (counting down) & displaying a bonus message when the user gets 10 points. Only call this method once
 *  setUpKeyAssociations() assigns key events to both panes
 *  setPaneFocus() sets the focus to the correct pane so the appropriate key pressed work
 *  The game initially waits for the user to press a key
 *  identifyKeyPress() determines what key is pressed and what to do with that information (if the game is running it checks the user's guess & if it's "over" or "waiting" a new game is started
 *  startAGame() starts the first game & displays a new random number, but also resets score and timer when the game is restarted
 *  updateTimer() is called by the animation timeline & creates the countdown clock. Changes to game over if no time remains
 *  displayNewNumber() creates a new random number (different from the previous one)
 *  isUserGuessCorrect() is called on every keypress when the game is "running". Checks if the keypress matches even/odd, update the score & display a new number. Can end the game if they guess wrong
 *  updateScore() increases overall score & displays bonus message if they got 10 in a row correct
 *  showGameOver() switches scenes & prompts the use to restart
 *  readWriteHighScore() reads high scores from a file, updates the high score label & saves the new high score (but only if it actually is a new high score. Don't bother changing the file if the score is NOT a high Score)
 * @author Noah Patullo
 */
public class EvenOdd extends Application {
	//GUI components (only those that need to be updated while to game is playing. Many more are local to setUpGUI)
	private final int windowWidth = 360;		//constants for size of the window
	private final int windowHeight = 440;
	private Stage primaryStage;
	private Scene gameScene;				//main "game is running" scene
	private Scene gameOverScene;		//this scene displays the score
	private Label timeLabel;
	private VBox gameOverPane;
	private GridPane mainGamePane;
	private Label randNumLabel;
	private Label scoreLabel;
	private Label bonusTimeLabel;
	private Label actualFinalScore;
	private Label actualHighScore;
	
	//Timeline Animations: countown clock & bonus time indicator
	private static final double MILLISEC = 100;				//update timer every 100 ms, or 10th of a second
	private Timeline timerAnimation;									//timeline is called every MILISEC to update the contents of the timer label & count down the time
	private static final double BONUS_TIME_MILLISEC = 2000;		//display bonus message for 2 seconds
	private Timeline bonusTimeAnimation;							//timeline for bonus "+10 Sec" message (only called if they get 10 in a row, then on every successive 10
	private final int INITIAL_TIME_REMAINING = 10;			//how long the player has when the game starts, can increase to make it easier. This value is added to timeRemaining every time the user gets 10 in a row
	private double timeRemaining = INITIAL_TIME_REMAINING;			//this value is changed rapidly via the Timeline animation creating the "countdown clock" value which is used to update the contents of a label
	private DecimalFormat dFormatter = new DecimalFormat("0.0");		//always want 2 decimals for the "countdown clock", so need formatter & convert timeRemaining to string before displaying to avoid rounding 9.80 to just 9.8
	
	private int randomNumber = (int)(Math.random()*10);		//initialize just in case, but its value should be set in displayNewNumber @ the start of a game
	private Random generator = new Random();
	private final int RANDOM_LOWER_BOUND = 1;
	private final int RANDOM_UPPER_BOUND = 1000;		//this must be 1 larger than the actual desired max value
	private String currentUserGuess;		//need to initialize? or would that cause problems if they didn't press any keys...
	
	private String gameMode = "waiting";		//"waiting" = start screen, "running"=currently being played & new number show up, "over"=switch scene & display score
	
	private int finalScore = 0;			//holds the RUNNING TOTAL of their score for each game played. Reset on each game (maybe work in highscore somehow...)
	private int highscore = 0;
	private int bonusTimeCounter = 0;		//counts from 0 to 10 when the finalScore increases, gives 10 seconds to timeRemaining & then resets to 0 & starts counting again
	
	//constructor unnecessary
	/**
	 * Calls setUpGUI to get the game running
	 */
	@Override
	public void start(Stage paramStage) throws Exception {
		primaryStage = paramStage;		//set parameter equal to field. This way other methods like setUpGUI can add & remove things from primaryStage
		setUpGUI();		//call method which creates the GUI, and then calls a succession of other methods which essentially start the gameplay
	}
	
	/**
	 * Since this class extends Application, start() is called so main() is mostly optional for editors that don't natively support JavaFX
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	/*
	 * Create a GUI then wait for keyPresse to start gameplay
	 */
	public void setUpGUI(){
		//timer & random numbers
		VBox timeScorePane = new VBox();
		timeScorePane.getStyleClass().addAll("align-center");		//centers all children elements (timer, score & bonus)
		timeScorePane.setId("timeScorePane");
		timeLabel = new Label(timeRemaining + "");		//create label & set text. Kind of unnecessary since updateTimer() is called almost immediately which starts counting down from 10 anyway
		timeLabel.setId("timeLabel");
		timeLabel.getStyleClass().addAll("largeText");
		bonusTimeLabel = new Label("");		//create label to hold "+10 Sec" when a time bonus is reached
		bonusTimeLabel.setId("bonusTimeLabel");
		bonusTimeLabel.getStyleClass().addAll("smallText");
		scoreLabel = new Label(finalScore + "");		//set initial score to 0.
		scoreLabel.getStyleClass().addAll("mediumText");
		scoreLabel.setId("scoreLabel");
		timeScorePane.getChildren().addAll(timeLabel, bonusTimeLabel, scoreLabel);
		
		StackPane numberAreaPane = new StackPane();
		numberAreaPane.setPrefHeight(200);		//maybe just leave this up to the pixel size of "randNumLabel" css fx
		numberAreaPane.setId("numberAreaPane");
		randNumLabel = new Label("Press any key to start");
		randNumLabel.getStyleClass().addAll("mediumText");
		randNumLabel.setId("randNumLabel");
		numberAreaPane.getChildren().add(randNumLabel);
		
		Label evenLabel = new Label("       Even\n(right arrow)");
		evenLabel.getStyleClass().addAll("oddEvenLabels");
		StackPane evenPane = new StackPane();
		evenPane.setPrefWidth(windowWidth/2.0);
		evenPane.setId("evenPane");
		evenPane.getChildren().add(evenLabel);
		Label oddLabel = new Label("     Odd\n(left arrow)");
		oddLabel.getStyleClass().addAll("oddEvenLabels");
		StackPane oddPane = new StackPane();
		oddPane.setPrefWidth(windowWidth/2.0);		//take up half the window. +40 @ 360px width does a fix, but expands the entire window as  well
		oddPane.getStyleClass().addAll("odd-and-even-pane");		//needed?
		oddPane.setId("oddPane");
		oddPane.getChildren().add(oddLabel);
		GridPane evenOddPane = new GridPane();
		evenOddPane.addRow(0, oddPane, evenPane);
		StackPane evenOddContainerPane = new StackPane();
		evenOddContainerPane.getChildren().add(evenOddPane);
		
		mainGamePane = new GridPane();			//actually create the pane that everything is added to
		mainGamePane.addColumn(0, timeScorePane, numberAreaPane, evenOddContainerPane);
		gameScene = new Scene(mainGamePane);				//create game scene (already declared as an instance field)
		gameScene.getStylesheets().add("styles.css");			//link to external css
		
		
		//--------------------------------------------------------------start creating "Game Over" scene --------------------------------------------------------------
		gameOverPane = new VBox();
		gameOverPane.getStyleClass().addAll("align-center");
		gameOverPane.setId("gameOverPane");
		VBox finalScorePane = new VBox();
		finalScorePane.setId("finalScorePane");
		finalScorePane.setPrefWidth(50);
		Label gameOverLabel = new Label("Game Over");
		gameOverLabel.getStyleClass().addAll("largeText");
		Label finalScoreLabel = new Label("Score: ");
		finalScoreLabel.getStyleClass().addAll("mediumText");
		actualFinalScore = new Label(finalScore + "");
		actualFinalScore.getStyleClass().addAll("largeText");
		Label highScoreLabel = new Label("High Score:");
		highScoreLabel.getStyleClass().addAll("mediumText");
		actualHighScore = new Label(highscore + "");			//kind of don't need a default value
		actualHighScore.getStyleClass().addAll("largeText");
		finalScorePane.getChildren().addAll(finalScoreLabel, actualFinalScore, highScoreLabel, actualHighScore);
		VBox finalScorePaneContainer = new VBox();
		finalScorePaneContainer.getChildren().addAll(finalScorePane);
		finalScorePaneContainer.getStyleClass().addAll("align-center");
		finalScorePaneContainer.setId("finalScorePaneContainer");
		Label restartInstructionsLabel = new Label("Press SPACE to restart");
		restartInstructionsLabel.getStyleClass().addAll("mediumText");
		restartInstructionsLabel.setId("restartInstructionsLabel");
		gameOverPane.getChildren().addAll(gameOverLabel, finalScorePaneContainer, restartInstructionsLabel);
		gameOverScene = new Scene(gameOverPane);
		gameOverScene.getStylesheets().add("styles.css");				//add external css styles 
		//-------------------------------------------------end creating game over scene -------------------------------------------------------------------
		
		primaryStage.setTitle("Even Odd");
        primaryStage.setScene(gameScene);		//sets the initial scene when the game is "waiting". Can easily be changed to gameOverScene to test GUI
        primaryStage.setHeight(windowHeight);
        primaryStage.setWidth(windowWidth);
        primaryStage.setResizable(false);		//this makes the window NON-resizable.
        primaryStage.show();
        
        
        setUpAnimation();			//only need to call once!
        setUpKeyAssociations();	//associate both panes to call identifyKeypress() when a key is pressed
        setPaneFocus();				//start a game once the GUI is set up. Calls subsequent methods once keypresses occur
	}
	
	/**
	 * sets up the animations (Timelines), but doesn't actually start counting down until a keypress happens and the gameMode allows the game to start
	 */
	private void setUpAnimation() {
        EventHandler<ActionEvent> timerEventHandler = (ActionEvent e) -> {		// Create a handler. (what to do when the timeline "updates")
        	updateTimer();
        };
        // Create an animation for "countdown timer"
        timerAnimation = new Timeline(new KeyFrame(Duration.millis(MILLISEC), timerEventHandler));
        timerAnimation.setCycleCount(Timeline.INDEFINITE);
        
        EventHandler<ActionEvent> bonusTimeEventHandler = (ActionEvent e) -> {		// Handler to add bonus message
        	bonusTimeLabel.setText("+10 Sec");
        };
        EventHandler<ActionEvent> bonusTimeEventHandler2 = (ActionEvent e) -> {		// handler to set text back to nothing
        	bonusTimeLabel.setText("");
        };
        // animation for bonus message. 1st keyframe is to display message & time=0. 2nd keyframe is to set text to empty, & this lasts length of BONUS_TIME_MILLISEC. Impossible to detect changes on INDEFINITE, but works for just 1 cycle
        bonusTimeAnimation = new Timeline(new KeyFrame(Duration.millis(0), bonusTimeEventHandler),  new KeyFrame(Duration.millis(BONUS_TIME_MILLISEC), bonusTimeEventHandler2) );
        bonusTimeAnimation.setCycleCount(1);		//only repeat the
    }
	
	/**
	 * Associate a setOnKeyPressed event for both panes. Both respond the same to a key press & call the identifyKeyPress(e) method, passing in the key that was pressed
	 */
	public void setUpKeyAssociations(){
		gameOverPane.setOnKeyPressed(e -> {			//use lambda expression to call a method when ANY key is pressed
			identifyKeyPress(e);
		});
		
		mainGamePane.setOnKeyPressed(e -> {
			identifyKeyPress(e);
		});
	}
	
	/**
	 * Find out what key pressed and verify the user's guess is correct if the game is "running", or else start a new game
	 * When game is running, it successfully ignores all keys other than Left/Right arrow
	 * @param e the key that was pressed (so e.getCode() can be used to find the exact key)
	 */
	public void identifyKeyPress(KeyEvent e){
		if(gameMode.equals("waiting")){		//if the game ISN't running yet...
			startAGame();
		}
		else if(gameMode.equals("running")){
			switch (e.getCode()) {
            case LEFT:		//they pressed the left arrow, so set the guess to "odd" & call method to check if correct
            	currentUserGuess = "ODD";
            	isUserGuessCorrect();
                break;
            case RIGHT:		//they pressed the right arrow, so set the guess to "odd" & call method to check if correct
            	currentUserGuess = "EVEN";
            	isUserGuessCorrect();
                break;
			}
		}
		else if( gameMode.equals("over") ){		//if game is over, only want to restrart when they press SPACE (otherwise they might accidentally hit another arrow key & miss their final score/gameOver screen)
			if( e.getCode() == KeyCode.SPACE  ){
				startAGame();
			}
		}
	}
	
	/**
	 * This method sets the focus to the correct pane (Button presses do different things in different game modes, so make sure only 1 pane @ a time accesses the keys)
	 */
	public void setPaneFocus(){
		if (gameMode.equals("over")) {		//if the game is over, gameOverPane should get the focus
			gameOverPane.requestFocus();
		} else {					//else case takes care of "running" & "waiting" game modes
			mainGamePane.requestFocus();
		}
	}
	
	/**
	 * Starts an initial game as well as resetting score and timer when the game is restarted (so this method can be reused)
	 */
	public void startAGame(){
		finalScore = 0;							//reset scores & timer (doesn't really apply for the very 1st game)
		bonusTimeCounter = 0;
		timeRemaining = INITIAL_TIME_REMAINING;
		
		primaryStage.setScene(gameScene);		//make sure the scene switches from "gameOverScene" when a new game starts. (If it's the first time the game is being played, this line is already executed in setUPGUI, so it's a little redundant, but it doens't matter)
		gameMode = "running";		//change the "mode" back to "running" if the game has been restarted (doesn't really apply for the very 1st game)
		timerAnimation.play();			//& start the countdown
		
		scoreLabel.setText(finalScore + "");			//set the text of the score (mostly for game restarts)
		randNumLabel.setId("randNumLabelRun");		//make it bigger since it only holds a few digits now, but also duplicate text color
		//randNumLabel.setStyle(" -fx-text-fill:lime; -fx-font-size: 100px");		//make it bigger since it only holds a few digits now, but also duplicate text color
		displayNewNumber();		//pick a new number and essentially start the game
	}
	
	/**
	 * Subtract a small number from the timeRemaining & update the label
	 * It's called rapidly by the timeline & essentially creates the countdown clock
	 */
	public void updateTimer(){
		if( !gameMode.equals("over") ){		//IMPORTANT! only update the timer if the game ISN'T over. Otherwise when time is 0, it keeps calling showGameOver until a new game starts
			if(timeRemaining>0){			//execute while >0, & the containing block only call this method while gameMode is "running"
				timeRemaining -= .1;		//decrement by .01 for every 100th of a second
				timeRemaining = (Math.round(timeRemaining * 10) ) /10.0;		//round the result just in case, maybe optional
				
				timeLabel.setText( dFormatter.format(timeRemaining) ); 		//update the time remaining, must do string concatenation
			}
			else{		//game is over once time remaining is 0
				gameMode = "over";
				showGameOver();				//make sure to switch the scenes
			}
		}
	}
	
	/**
	 * Create a new random number (must be different from the previous one) & display it
	 */
	public void displayNewNumber(){
		int tempOldRand = randomNumber;		//save old value so new value can be compared & make sure the same number isn't picked twice
		randomNumber = generator.nextInt(RANDOM_UPPER_BOUND) + RANDOM_LOWER_BOUND;		//between Creates random numer in a range
		while( randomNumber == tempOldRand){		//if the new pseudoRandom number is the same, pick a new one
			randomNumber = generator.nextInt(RANDOM_UPPER_BOUND);		//between 0 & 101
		}
		randNumLabel.setText(randomNumber + "");		//update label text
	}
	
	/**
	 * Checks if the keypress matches even/odd, update the score & display a new number. Game is over if they guess wrong, but ignores all keys other than left/right arrow (filtered out in identifyKeyPress)
	 */
	public void isUserGuessCorrect(){
		if( currentUserGuess.equals("EVEN") && (randomNumber%2==0 ) ){		//check if the number is even & their guess matched
			updateScore();			//increase the score, & potentially add time bonus for 10 in a row
			displayNewNumber();	//display anew random number
		}
		else if( currentUserGuess.equals("ODD") && (randomNumber%2 !=0 ) ){	//check if the number is odd & their guess matched
			updateScore();
			displayNewNumber();
		}
		else{		//if they guessed wrong, end the game
			gameMode = "over";	//change the state of the game
			showGameOver();			//switch scenes if they guess wrong
		}
	}
	
	/**
	 * Increase score. Also has a separate counter which resets if they get 10 correct, then a 10 second bonus is added to timeRemaining & a message is displayed notifying the player of the bonus
	 */
	public void updateScore(){
		finalScore++;		//increment score
		scoreLabel.setText(finalScore + "");
		bonusTimeCounter++;		//increment the bonus time counter as well as finalScore
		if(bonusTimeCounter == 10){
			bonusTimeAnimation.play();			//displays a bonus message for for a few seconds, then diappear. Set's the text in a label that is blank most of the time (essentially bonusTimeLabel.setText("+10 Sec"); ). Animation only cycles once, so it's ok that I call "play()" multiple times since the previous cycle should have finished& they shouldn't overlap
			timeRemaining += 10;			//add 10 to time remaining. Since the animation is still running, this new time will be updated in 10 miliseconds when updateTimer is called
			bonusTimeCounter = 0;		//reset bonus
		}
	}
	
	/**
	 * Switches the scene to "gameOverScene" & sets the focus so the kayPresses register with the current pane
	 * Hitting any button will now call identifyKeyPress which has a case to check if the game is over & will start a new game
	 * @throws FileNotFoundException
	 */
	public void showGameOver(){
		readWriteHighScore();
		primaryStage.setScene(gameOverScene);
		actualFinalScore.setText(finalScore +"");
		setPaneFocus();		//sets the focus to the appropriate pane, or keys would still be registering on the mainGamePane
	}
	
	/**
	 * This method reads the previous high score from a file (or sets it equal to the current game score if no file exists & this is the very first game)
	 * It then updates a label with the high score & saves the new highscore to the file
	 * Deals with hidden files. For some reason Java wouldn't write a to a hidden file, so the hack is to get the highscore, then if a new one needs to be added, delete the old file & make a new one
	 */
	public void readWriteHighScore(){
		boolean needToUpdateFile = true;		//keep track of if a new score should actually be added (assume true for first game)
		String scoresFileName = ".CONFIG_DO_NOT_MODIFY";		//file name or path (identical when in the same folder)
		Path highScoreFilePath = Paths.get(scoresFileName);			//used to delete the file & set HIDDEN
		File scoresFile = new File(scoresFileName);							//create a file object, but doesn't actually make a file yet
		
		if (scoresFile.exists()) {					//only read from file if they've played a previous game
			needToUpdateFile = false;		//if the file exists, assume they didn't beat the high score
			try (Scanner inputFile = new Scanner(scoresFile);) {		//scanner object in try block to autoclose
				highscore = inputFile.nextInt();		//get the current high score from the file
				if(finalScore > highscore){			//change the highscore if they the current game's score was higher
					highscore = finalScore;
					needToUpdateFile = true;		//a new high score was found & needs to be written to the file
				}
			}
			catch(FileNotFoundException e){
				System.out.println("Error reading file");
			}
		}
		else{		//if it's the 1st game, no score file exists so the highscore is the current game score
			highscore=finalScore;
		}
		actualHighScore.setText(highscore + "");		//display the high score in the label
		
		if(needToUpdateFile){		//only write to the file if a new high score is found. Initially true for 1st game, the "scoresFile.exists()" is skipped so the 1st score will always be added
			if (scoresFile.exists()) {			//delete the old score file but only if it exists (Java wouldn't allow writing to hidden files, so just delete & make a new one)
				try {
					Files.delete(highScoreFilePath);
				} catch (IOException e) {
					System.out.println("Error: Couldn't delete file before updating high score");
				}
			}
			
			//add the high score to the file & set the file to HIDDEN
			try (PrintWriter actualScoreFile = new PrintWriter(scoresFile);) {		//create printWriter in try to autoclose file & update the score
				actualScoreFile.println(highscore);		//print the highscore to empty new file
				Files.setAttribute(highScoreFilePath, "dos:hidden", true);		//attempt to make it a hidden file
			}
			catch(FileNotFoundException e){
				System.out.println("Error saving high score, FileNotFoundException");
			} catch (IOException e) {
				System.out.println("Invalid permissions when saving hidden file");
			}
		}//end if(needToUpdateFile)
	}

}