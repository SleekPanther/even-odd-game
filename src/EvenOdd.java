//Consolidate/organize imports?
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EvenOdd extends Application {
	private final int windowWidth = 360;		//constants for size of the window
	private final int windowHeight = 500;
	private Stage primaryStage;
	//need to bring all labels & nodes outside as fields
	private static final double MILLISEC = 100;				//update timer every 100 ms, or 10th of a second
	private Timeline timerAnimation;									//timeline is called every MILISEC to update the contents of the timer label & count down the time
	private static final double BONUS_TIME_MILLISEC = 2000;		//display bonus message for 2 seconds
	private Timeline bonusTimeAnimation;							//timeline for bonus "+10 Sec" message (only called if they get 10 in a row, then on every successive 10
	
	private Scene gameOverScene;
	private Label timeLabel;
	private VBox gameOverPane;
	private GridPane mainGamePane;
	private Label randNumLabel;
	private Label scoreLabel;
	private Label bonusTimeLabel;
	
	
	private int finalScore = 0;			//holds the RUNNING TOTAL of their score for each game played. Reset on each game (maybe work in highscore somehow...)
	private int bonusTimeCounter = 0;
	
	private double timeRemaining = 40;
	private DecimalFormat dFormatter = new DecimalFormat("0.0");		//always want 2 decimals, so need formatter & convert timeRemaining to string before displaying to avoid rounding 9.80 to just 9.8
	
	private int randomNumber = 10;
	//private int randomNumber = (int)(Math.random()*10);		//initialize just in case, but its value should be set in displayNewNumber @ the start of a game 
	private Random generator = new Random();
	private final int RANDOM_UPPER_BOUND = 101;			//generate random number between 0 & 1 less than this number
	private String currentUserGuess;		//need to initialize? or would that cause problems if they didn't press any keys...
	
	private String gameMode = "waiting";		//"waiting" = start screen, "running"=currently being played & new number show up, "over"=switch scene & display score
	private boolean isGameOver = false;
	
	public EvenOdd(){	//constructor
	}
	
	@Override
	public void start(Stage paramStage) throws Exception {
		primaryStage = paramStage;		//set parameter equal to field. This way other methods like setUpGUI can add & remove things from primaryStage
		setUpGUI();		//call method which creates the GUI, and then calls a succession of other methods which essentially start the gameplay
	}
	
	public static void main(String[] args) {
		Application.launch(args);		//mostly optional for editors that don't natively support JavaFX
	}
	
	/*
	 * Create a GUI, then calls subsequent methods to actually start the gameplay
	 */
	public void setUpGUI(){
		//http://stackoverflow.com/questions/19174983/javafx-layout-that-scales-with-parent
		
		mainGamePane = new GridPane();
		mainGamePane.setStyle("-fx-background-color:white");
		StackPane gameStatusTopbar = new StackPane();
		
		Label gameStatusLabel = new Label("Might not need");
		gameStatusTopbar.getChildren().add(gameStatusLabel);
		gameStatusTopbar.setStyle("-fx-background-color:pink");
		
		
		//timer & random numers
		VBox timeScorePane = new VBox();
		timeScorePane.setAlignment(Pos.CENTER);		//center the entire pane (& therefore all it's nodes)
		timeScorePane.setStyle("-fx-background-color:green");
		timeLabel = new Label(timeRemaining + "");		//create label & set text. Kind of unnecessary since updateTimer() is called almost immediately which starts counting down from 10 anyway
		timeLabel.setStyle("-fx-background-color:blue; -fx-font-size: 50px");
		timeLabel.setAlignment(Pos.CENTER);
		bonusTimeLabel = new Label("");		//create label to hold "+10 Sec" when a time bonus is reached
		bonusTimeLabel.setStyle("-fx-font-size: 20px");
		bonusTimeLabel.setAlignment(Pos.CENTER);
		scoreLabel = new Label(finalScore + "");		//set initial score to 0. 
		scoreLabel.setStyle("-fx-background-color:red; -fx-text-fill: yellow; -fx-font-size: 30px");
		timeScorePane.getChildren().addAll(timeLabel,bonusTimeLabel,scoreLabel);
		
		StackPane numberArea = new StackPane();
		numberArea.setPrefHeight(200);		//maybe just leave this up to the pixel size of "randNumLabel" css fx
		numberArea.setStyle("-fx-background-color:black");
		
		
		//randNumLabel = new Label( randomNumber + "");
		randNumLabel = new Label("Press any key to start");
		randNumLabel.setStyle("-fx-background-color:darkblue; -fx-text-fill:lime; -fx-font-size: 30px");
		//randNumLabel.setStyle("-fx-font-size: 100px");		//make it biger in startAGame
		numberArea.getChildren().add(randNumLabel);
		
		
		Label evenLabel = new Label("Even \n(left)");
		evenLabel.setStyle("-fx-background-color:white");
		Label oddLabel = new Label("Odd \n(right)");
		oddLabel.setStyle("-fx-background-color:white");
		//FlowPane evenPane = new FlowPane();
		StackPane evenPane = new StackPane();
		evenPane.setPrefWidth(windowWidth/2.0);
		evenPane.setStyle("-fx-background-color:lime");
		//FlowPane oddPane = new FlowPane();
		StackPane oddPane = new StackPane();
		evenPane.getChildren().add(evenLabel);
		oddPane.setPrefWidth(windowWidth/2.0);		//take up half the window. +40 @ 360px width does a fix, but expands the entire window as  well
		oddPane.setStyle("-fx-background-color:orange");
		oddPane.getChildren().add(oddLabel);
		GridPane evenOddPane = new GridPane();	//add thing in constructor here
		evenOddPane.setStyle("-fx-background-color:purple");
		evenOddPane.addRow(0, oddPane, evenPane);
		FlowPane evenOddContainer = new FlowPane();
		//StackPane evenOddContainer = new StackPane();
		evenOddContainer.getChildren().add(evenOddPane);
		
		mainGamePane.addColumn(0, gameStatusTopbar, timeScorePane, numberArea, evenOddContainer);
//		mainGamePane.add(gameStatusTopbar, 0, 0);
//		mainGamePane.add(evenOddContainer,0,2);
		
		mainGamePane.setPrefSize(windowWidth, windowHeight);
		Scene gameScene = new Scene(mainGamePane);
		
		
		
		//stare creating "Game Over" scene --------------------------------------------------------------
		gameOverPane = new VBox();
		gameOverPane.setPrefSize(windowWidth, windowHeight);
		gameOverPane.setAlignment(Pos.CENTER);
		Label gameOverLabel = new Label("game over");
		Label finalScoreLbl = new Label("Score: ");
		Label actualFinalScore = new Label("100...");
		actualFinalScore.setStyle("-fx-font-size: 50px; ");
		gameOverPane.getChildren().addAll(gameOverLabel,finalScoreLbl,actualFinalScore);
		gameOverScene = new Scene(gameOverPane);
		//end creating game over scene -------------------------------------------------------------------
		
		
		primaryStage.setTitle("Even Odd");
        primaryStage.setScene(gameScene);
        //primaryStage.setResizable(false);		//this makes the window NON-resizable. For some reason this messed up my window size & "grew" by 12 pixels on the bottom & right edges of the black pane
        primaryStage.show();
        
        //used to be requesting focus here
        
        verifyGameState();		//start a game once the GUI is set up
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
        // animation from bonus message. 1st keyframe is to display message & time=0. 2nd keyframe is to set text to empty, & this lasts length of BONUS_TIME_MILLISEC. Impossible to detect changes on INDEFINITE, but works for just 1 cycle 
        bonusTimeAnimation = new Timeline(new KeyFrame(Duration.millis(0), bonusTimeEventHandler),  new KeyFrame(Duration.millis(2000), bonusTimeEventHandler2) );
        bonusTimeAnimation.setCycleCount(1);		//only repeat the 
    }
	
	public void identifyKeyPress(KeyEvent e){
		//switch cases for left/right
		System.out.println("keypress main method");
		
		if(gameMode.equals("waiting") || gameMode.equals("over")){		//if the game ISN't running yet...
			startAGame();
		}
		else if(gameMode.equals("running")){
			switch (e.getCode()) {
            case LEFT:		//they pressed the left arrow, so set the guess to "odd" & call method to check if correct
            	System.out.println("left run");
            	currentUserGuess = "ODD";
            	isUserGuessCorrect();
                break;
            case RIGHT:		//they pressed the right arrow, so set the guess to "odd" & call method to check if correct
            	System.out.println("Right run");
            	currentUserGuess = "EVEN";
            	isUserGuessCorrect();
                break;
			}
		}
	}
	
	public void verifyGameState(){
		setUpAnimation();		//sets up the animation, 
		
		
//		if (gameMode.equals("running")) {
//			mainGamePane.requestFocus();
//			mainGamePane.setOnKeyPressed(e -> {
//				identifyKeyPress(e);
//			});
//		}
//		System.out.println("aft run 1st");
//
//		System.out.println("aft run loop");
//		if (gameMode.equals("over")) {
//			showGameOver();
//			gameOverPane.requestFocus();
//			gameOverPane.setOnKeyPressed(e -> {
//				identifyKeyPress(e);
//			});
//		}
		
		//gameMode = "over";
		if (gameMode.equals("over")) {
			showGameOver();
			gameOverPane.requestFocus();
			gameOverPane.setOnKeyPressed(e -> {
				identifyKeyPress(e);
			});
		} else {
			mainGamePane.requestFocus();
			mainGamePane.setOnKeyPressed(e -> {
				identifyKeyPress(e);
			});
		}
	}
	
	public void startAGame(){
		gameMode = "running";		//change the "mode" back to "running"
		timerAnimation.play();			//& start the countdown
		
		randNumLabel.setStyle(" -fx-text-fill:lime; -fx-font-size: 100px");		//make it bigger since it only holds a few digits now, but also duplicate tet color
		displayNewNumber();
	}
	
	public void startAGame0(){	//called by pressing any key while in "waiting" mode
		
		boolean isGameOver = false;
		Random randomGenerator = new Random();
		randomGenerator.setSeed(System.currentTimeMillis());		//random seed based on time
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("[ is even ] is odd");
		
		while(!isGameOver){
			isGameOver = true;
			int random = randomGenerator.nextInt(100);
			System.out.println(random + ": ");
			String answer = scan.next();
			if( (answer.equals("[")) && (random%2==0) ){
				isGameOver = false;
			}
			if( (answer.equals("]")) && (random%2!=0) ){
				isGameOver = false;
			}
		}
		
	}
	
	public void updateTimer(){		
		if(timeRemaining>0){			//execute while >0, & the containing block only call this method while gameMode is "running"
			timeRemaining -= .1;		//decrement by .01 for every 100th of a second
			timeRemaining = (Math.round(timeRemaining * 10) ) /10.0;		//round the result just in case, maybe optional
			
			timeLabel.setText( dFormatter.format(timeRemaining) ); 		//update the time remaining, must do string concatenation
		}
		else{		//game is over once time remainins is 0
			gameMode = "over";
		}
	}
	
	public void isUserGuessCorrect(){
		if( currentUserGuess.equals("EVEN") && (randomNumber%2==0 ) ){		//check if the number is even & their guess matched
			System.out.println(randomNumber + "  is even, correct");
			updateScore();			//increase the score, & potentially add time bonus for 10 in a row
			displayNewNumber();	//display anew random number
		}
		else if( currentUserGuess.equals("ODD") && (randomNumber%2 !=0 ) ){	//check if the number is odd & their guess matched
			System.out.println(randomNumber + "  is odd, correct");
			updateScore();
			displayNewNumber();
		}
		else{		//if they guessed wrong, end the game 
			gameMode = "over";	//change the state of the game
			showGameOver();			//switch scenes if they guess wrong
		}
		
		/*
		 need some basic start method to set up GUI (splash screen, title, "Press SPACE to start")
		 want to ignore all bu important key presses. Use switch like in Tetris
		code scenarios for each important key. LEFT or RIGHT both call the isUserGuessCorrect() method to validate, then it should refresh the display & make another number
		R   should restart, but bring up dialog
		SPACE @ the start of the game should call the start method, or something to actually get the game rolling
		need variable to hold "gameState", use string. "welcome" "running" "over", over=switch scene. in each of the  restart and isUserGuessCorrect methods need to check if game is running. Only execute if it is running
		Space=start should only happen if the game isn't running (i.e. it's the splash screen)
		 */
	}
	
	public void updateScore(){
		finalScore++;		//increment score
		scoreLabel.setText(finalScore + "");
		bonusTimeCounter++;		//increment the bonus time counter as well as finalScore
		if(bonusTimeCounter == 10){
			bonusTimeAnimation.play();			//displays a bonus message for for a few seconds, then diappear. Set's the text in a label that is blank most of the time (essentially bonusTimeLabel.setText("+10 Sec"); ). Animation only cycles once, so it's ok that I call "play()" multiple times since the previous cycle should have finished& they shouldn't overlap  
			System.out.println("+10 Sec");
			timeRemaining += 10;			//add 10 to time remaining. Since the animation is still running, this new time will be updated in 10 miliseconds when updateTimer is called
			bonusTimeCounter = 0;		//reset bonus
		}
	}
	
	public void displayNewNumber(){
		int tempOldRand = randomNumber;		//save old value so new value can be compared & make sure the same number isn't picked twice
		randomNumber = generator.nextInt(RANDOM_UPPER_BOUND);		//between 0 & 101
		while( randomNumber == tempOldRand){		//if the new pseudoRandom number is the same, pick a new one
			randomNumber = generator.nextInt(RANDOM_UPPER_BOUND);		//between 0 & 101
		}
		randNumLabel.setText(randomNumber + "");		//update label text
	}
	
	public void showGameOver(){
		primaryStage.setScene(gameOverScene);
		
	}
	
	public void restartGame(){	//possibly need params, or just do something magical & wipe everything, maybe just call the "set up game"
		finalScore = 0;
		//primaryStage.setScene(gameScene);
		/* most important= switch scene, then start the countdown
		maybe don't even initialize the fields with text until here, then can call this from setUpGUI
		or since labels text can be set when doing  =  new Label("text"), maybe it's good to have placeholder	*/
		
	}


}