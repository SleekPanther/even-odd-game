//consolodate imports?
import java.util.Random;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EvenOdd extends Application {
	private Stage primaryStage;
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
		FlowPane gameStatusTopbar = new FlowPane();
		StackPane numberArea = new StackPane();
		
		Label gameStatusLabel = new Label("Game running/paused");
		gameStatusTopbar.getChildren().add(gameStatusLabel);
		gameStatusTopbar.setStyle("-fx-background-color:red");
		
		Label evenLabel = new Label("Even \n(left)");
		Label oddLabel = new Label("Odd \n(right)");
		FlowPane evenPane = new FlowPane();
		evenLabel.setStyle("-fx-background-color:yellow");
		FlowPane oddPane = new FlowPane();
		evenPane.getChildren().add(evenLabel);
		oddLabel.setStyle("-fx-background-color:orange");
		oddPane.getChildren().add(oddLabel);
		GridPane evenOddPane = new GridPane();	//add thing in constructor here
		evenOddPane.addRow(0, evenPane, oddPane);
		FlowPane evenOddContainer = new FlowPane();
		evenOddContainer.getChildren().add(evenOddPane);
		
		mainPane.add(gameStatusTopbar, 0, 0);
		mainPane.add(evenOddContainer,0,2);
		
		mainPane.setPrefSize(300, 500);
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