//consolodate imports?
import java.util.Random;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
	private final int windowWidth = 500;
	
	private Stage primaryStage;
	//need to bring all labels & nodes outside as fields
	
	private int randomNumber = 1;
	
//	private boolean isGameOver = false;
//	private Random randomGenerator = new Random();
	
	public EvenOdd(){	//constructor
		
	}
	
	@Override
	public void start(Stage paramStage) throws Exception {
		primaryStage = paramStage;
		setUpGUI();		
		
	}
	
	public static void main(String[] args) {
//		EvenOdd game = new EvenOdd();
//		game.isUserGuessCorrect();
		
		Application.launch(args);
		//startAGame();
	}

	public void setUpGUI(){		//needs to show a GUI & be waiting for ENTER to start
		System.out.println("set up game");
		isUserGuessCorrect();
		//http://stackoverflow.com/questions/19174983/javafx-layout-that-scales-with-parent
		
		GridPane mainPane = new GridPane();
		mainPane.setStyle("-fx-background-color:white");
		StackPane gameStatusTopbar = new StackPane();
		
		Label gameStatusLabel = new Label("Game running/paused");
		gameStatusTopbar.getChildren().add(gameStatusLabel);
		gameStatusTopbar.setStyle("-fx-background-color:pink");
		
		
		//timer & num
		VBox timeScorePane = new VBox();
		timeScorePane.setAlignment(Pos.CENTER);		//center the entire pane (& therefore all it's nodes)
		timeScorePane.setStyle("-fx-background-color:green");
		Label timeLabel = new Label("10");
		timeLabel.setStyle("-fx-background-color:blue; -fx-font-size: 50px");
		timeLabel.setAlignment(Pos.CENTER);
		Label scoreLabel = new Label("0");
		scoreLabel.setStyle("-fx-background-color:red; -fx-text-fill: yellow; -fx-font-size: 30px");
		timeScorePane.getChildren().addAll(timeLabel,scoreLabel);
		
		
		StackPane numberArea = new StackPane();
		numberArea.setPrefHeight(200);
		numberArea.setStyle("-fx-background-color:black");
		
		
		Label randNumLabel = new Label( (int)(Math.random()*10) + "");
		randNumLabel.setStyle("-fx-background-color:darkblue; -fx-text-fill:lime; -fx-font-size: 100px");
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
		oddPane.setPrefWidth(windowWidth/2.0);		//take up half the window
		oddPane.setStyle("-fx-background-color:orange");
		oddPane.getChildren().add(oddLabel);
		GridPane evenOddPane = new GridPane();	//add thing in constructor here
		evenOddPane.setStyle("-fx-background-color:purple");
		evenOddPane.addRow(0, evenPane, oddPane);
		FlowPane evenOddContainer = new FlowPane();
		//StackPane evenOddContainer = new StackPane();
		evenOddContainer.getChildren().add(evenOddPane);
		
		mainPane.addColumn(0, gameStatusTopbar, timeScorePane, numberArea, evenOddContainer);
//		mainPane.add(gameStatusTopbar, 0, 0);
//		mainPane.add(evenOddContainer,0,2);
		
		mainPane.setPrefSize(300, windowWidth);
		Scene scene = new Scene(mainPane);
		
		setUpKeyPresses();
		
		primaryStage.setTitle("Even Odd");
        primaryStage.setScene(scene);
        //primaryStage.setResizable(false);		//this makes the window NON-resizable. For some reason this messed up my window size & "grew" by 12 pixels on the bottom & right edges of the black pane
        primaryStage.show();
		
		//maybe call restart a game?? this should just set up the GUI
//		animation = new Timeline(new KeyFrame(Duration.millis(MILLISEC), eventHandler));
//        animation.setCycleCount(Timeline.INDEFINITE);
//        animation.play();
		
		
	}
	
	public void setUpKeyPresses(){
		//switch cases
	}
	
	public void startAGame(){	//called by pressing Enter
		
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
	
	public boolean isUserGuessCorrect(){
//		tetrisBoard.setOnKeyPressed(e -> {		//tetrisBoard=pane
//        	System.out.println(e.getCode());	//print the key code. 
	//}
		/*
		 need some basic start method to set up GUI (splash screen, title, "Press ENTER to start")
		 want to ignore all bu important key presses. Use switch like in Tetris
		code scenarios for each important key. LEFT or RIGHT both call the isUserGuessCorrect() method to validate, then it should refresh the display & make another number
		P should pause, R   should restart, but bring up dialog
		ENTER @ the start of the game should call the start method, or something to actually get the game rolling
		need variable to see if the game is currently running, in each of the pause, restart and isUserGuessCorrect methods need to check if game is running. Only execute if it is running
		Enter should only happen if the game isn't running (i.e. it's the splash screen)
		 */
		
		System.out.println(randomNumber);
		return true;
	}
	
	public void displayNewNumber(){
		//some gui stuff to pick a new random & display it
	}
	
	public void pauseGame(){
		//called by pressing P. Do checking here to see check status. if(paused), then resume, else (no need to check if NOT paused since boolean, speed up by eliminating another equality check)
	}
	
	public void restartGame(){	//possibly need params, or just do something magical & wipe everything, maybe just call the "set up game"
		//
		//maybe don't close the entire stage, just paint over with yellow & start again
		
	}


}