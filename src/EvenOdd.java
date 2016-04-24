import java.util.Random;
import java.util.Scanner;

public class EvenOdd {
//	private boolean isGameOver = false;
//	private Random randomGenerator = new Random();
	
	public static void main(String[] args) {
		startAGame();
	}

	public static void startAGame(){
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
}
