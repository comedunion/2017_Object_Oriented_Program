import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.rmi.*;
import java.rmi.server.*;


class Utilities {
	public static BufferedImage getImage(String filePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException  e) {
			System.out.println("Erroring during loading and creating dog image with " + filePath);
		}
		return image;
	}

	public static boolean withinBoundary(BufferedImage img, Point dest, int rangeX, int rangeY) {
		return((dest.x >= 0) && (dest.y >= 0) && (dest.x+img.getWidth()<=rangeX) && (dest.y+img.getHeight()<=rangeY));
	}
	
	public static synchronized void resetRoamingVector(Object obj) {
		int tmpDelta;
	    Random rand = new Random();
		
		if (obj instanceof Dog) {
			// Getting new movement vector(deltaX) 
			do {
				tmpDelta = rand.nextInt(9)+1;
				if(Math.random() <0.5) tmpDelta *= -1;
			} while ( (((Math.abs(((Dog) obj).deltaX-tmpDelta)) < ((Dog) obj).deltaXChangeLimit)) 
					   || (((Math.abs(((Dog) obj).deltaX+tmpDelta)) < ((Dog) obj).deltaXChangeLimit)));
			((Dog) obj).deltaX = tmpDelta;
			
			// Getting new movement vector(deltaY) 
			do {
				tmpDelta = rand.nextInt(9)+1;
				if(Math.random() <0.5) tmpDelta *= -1;
			} while ( (((Math.abs(((Dog) obj).deltaY-tmpDelta)) < ((Dog) obj).deltaYChangeLimit)) 
					   || (((Math.abs(((Dog) obj).deltaY+tmpDelta)) < ((Dog) obj).deltaYChangeLimit)));
			((Dog) obj).deltaY = tmpDelta;

		} else if (obj instanceof Burglar) {
			
		}
	
	}
}


abstract class Dog {

	/*
	 * Add general properties such as lethality
	 */
	protected boolean chaseMode = false;
	protected int myPosX, myPosY;	// the current position of a dog
	protected BufferedImage myImage=null;
	
	protected int roamingSpeed;	// pixels per unit msec
	protected int chaseSpeed;	// pixels per unit msec
	
	// Movement Vector
	int deltaX, deltaY;
	int deltaXChangeLimit;
	int deltaYChangeLimit;

	abstract void renderYourself(Graphics g);
	
	public void setMode(boolean chaseMode) {
		this.chaseMode = chaseMode;
	}
	
	protected void calcRoamingMove() {
		if (deltaX < 0) 
			myPosX = myPosX - roamingSpeed;
		else
			myPosX = myPosX + roamingSpeed;

		if (deltaY < 0)
			myPosY = myPosY - (roamingSpeed * Math.abs(deltaY/deltaX));
			else
			myPosY = myPosY + (roamingSpeed * Math.abs(deltaY/deltaX));
	} 	
	
}


class Chiwawa extends Dog /*implements ImageObserver*/ {

	static final String MY_IMAGE_FILE_PATH = "/images/chiwawa.png";
	static final int INITIAL_POS_X = 30;
	static final int INITIAL_POS_Y = 30;
	//Movement vector change limits

	
	public Chiwawa() {
		super();
		myImage = Utilities.getImage(MY_IMAGE_FILE_PATH);
		myPosX = INITIAL_POS_X;
		myPosY = INITIAL_POS_Y;
		// Initialize movement vector
		deltaX = 1;
		deltaY = 10;
		deltaXChangeLimit = 5;
		deltaYChangeLimit = 5;
		roamingSpeed = 1;
		chaseSpeed = 3;
	}
	
	public void renderYourself(Graphics g) {
		int reserveMyPosX = myPosX;
		int reserveMyPosY = myPosY;
		
		if (myImage != null) {
			calcRoamingMove();
			while (!Utilities.withinBoundary(myImage, new Point(myPosX,myPosY), GuardArea.AREA_W,GuardArea.AREA_H)) {
				myPosX = reserveMyPosX;	
				myPosY = reserveMyPosY;
				Utilities.resetRoamingVector(this);
				calcRoamingMove();
			};
			
			g.drawImage(myImage, myPosX, myPosY, null);
		}	
	}
	
	/*
	 * Add more methods that each dog perform
	 */
}


class Rottweiler extends Dog /* implements ImageObjerver */ {
	static final String MY_IMAGE_FILE_PATH = "/images/rottweiler.png";
	static final int INITIAL_POS_X = 100;
	static final int INITIAL_POS_Y = 30;
	
	//Movement vector change limits

	public Rottweiler() {
		super();
		myImage = Utilities.getImage(MY_IMAGE_FILE_PATH);
		myPosX = INITIAL_POS_X;
		myPosY = INITIAL_POS_Y;
		// Initialize movement vector
		deltaX = 1;
		deltaY = 10;
		deltaXChangeLimit = 2;
		deltaYChangeLimit = 2;
		roamingSpeed = 2;	// pixels per unit msec
		chaseSpeed = 5;	// pixels per unit msec
	}
	


	public void renderYourself(Graphics g) {
		int reserveMyPosX = myPosX;
		int reserveMyPosY = myPosY;
		
		if (myImage != null) {
			calcRoamingMove();
			while (!Utilities.withinBoundary(myImage, new Point(myPosX,myPosY), GuardArea.AREA_W,GuardArea.AREA_H)) {
				myPosX = reserveMyPosX;	
				myPosY = reserveMyPosY;
				Utilities.resetRoamingVector(this);
				calcRoamingMove();
			};
			
			g.drawImage(myImage, myPosX, myPosY, null);
		}	
	}
	
	/*
	 * Add more methods that each dog perform
	 */
}


class BorderCollie extends Dog /* implements ImageObjerver */ {
	static final String MY_IMAGE_FILE_PATH = "/images/collie.png";
	static final int INITIAL_POS_X = 200;
	static final int INITIAL_POS_Y = 30;

	public BorderCollie() {
		super();
		myImage = Utilities.getImage(MY_IMAGE_FILE_PATH);
		myPosX = INITIAL_POS_X;
		myPosY = INITIAL_POS_Y;
		// Initialize movement vector
		deltaX = 1;
		deltaY = 10;
		deltaXChangeLimit = 3;
		deltaYChangeLimit = 3;
		roamingSpeed = 2;	// pixels per unit msec
		chaseSpeed = 3;	// pixels per unit msec
	}

	
	public void renderYourself(Graphics g) {
		int reserveMyPosX = myPosX;
		int reserveMyPosY = myPosY;
		
		if (myImage != null) {
			calcRoamingMove();
			while (!Utilities.withinBoundary(myImage, new Point(myPosX,myPosY), GuardArea.AREA_W,GuardArea.AREA_H)) {
				myPosX = reserveMyPosX;	
				myPosY = reserveMyPosY;
				Utilities.resetRoamingVector(this);
				calcRoamingMove();
			};
			
			g.drawImage(myImage, myPosX, myPosY, null);
		}	
	}
	
	/*
	 * Add more methods that each dog perform
	 */
}


class Burglar {
		static final String MY_IMAGE_FILE_PATH = "/images/burglar.png";
		static final int EntryPosX = 300;
		static final int EntryPosY = 300;

		BufferedImage myImage = null;
		int myPosX;
		int myPosY;
		
		public Burglar() {
			super();
			myImage = Utilities.getImage(MY_IMAGE_FILE_PATH);
			myPosX = EntryPosX;
			myPosY = EntryPosY - myImage.getHeight();
		}
		
		public void renderYourself(Graphics g) {
			g.drawImage(myImage, myPosX, myPosY, null);
		}
}


/*
 * GuardArea represents the inside of a house, where dogs patrol and guard
 */
class GuardArea extends JPanel {

	static final int AREA_W = 400;
	static final int AREA_H = 400;
	
	Dog[] dogs;
	Burglar burglar;

	public GuardArea(Dog[] dogs, Burglar burglar) {
		this.dogs = dogs;
		this.burglar = burglar;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(AREA_W, AREA_H);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(Dog d : dogs) {
			d.renderYourself(g);
		}
	}
}


class StatusDisplayArea extends JPanel {
	
	JTextArea infoDisplay;
//	Dog[] dogs;
	
	StatusDisplayArea() {
		super();
		infoDisplay = new JTextArea(6,40);
		infoDisplay.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(infoDisplay,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane);		
	}

	JTextArea getMessageBox() {
		return infoDisplay;
	}
	
}


class SecurityControl extends UnicastRemoteObject implements ActionListener, HomeRemoteProvision {
	public static final int TIMER_DELAY = 50;  // in milliseconds
	public static final int TIMER_RESET_INTERVAL = 1000;  //in milliseconds

	AppRemoteProvision appStub;
	
	Dog chiwawa = new Chiwawa();
	Dog rottweiler = new Rottweiler();
	Dog collie = new BorderCollie();
	Dog[] dogs = {chiwawa, rottweiler, collie};
	Burglar burglar = new Burglar();
	JPanel guardArea;
	JTextArea messageBox;
	Timer timer; 
	
	int accumulatedInterval = 0;
		
	public SecurityControl() throws RemoteException {
		super();
	}
	
	Dog[] getDogs() {
		return dogs;
	}
	
	Burglar getBurglar() {
		return burglar;
	}

	void setGuardArea(JPanel area) {
		guardArea = area;
	}
	
	void setStatusArea(JTextArea textArea) {
		messageBox = textArea;
	}
	
	void setTimer(Timer timer) {
		this.timer = timer;
	}

	public void startTimer() {
		timer.start();
	}
	
	public void stopTimer() {  // for now, it is not used
		timer.stop();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (accumulatedInterval > TIMER_RESET_INTERVAL) {
			/*
			 * Your logic should be put here, for example
			 * - Randomly assume whether burglar intruded or not
			 * - ,and accordingly do action on it
			 * Need to design logic, and make implementations as class and methods
 			 */
			accumulatedInterval = 0;

			// Following code shows a normal mode strolling of dogs
			for (Dog aDog : dogs) {
				Utilities.resetRoamingVector(aDog);
			}
			
		}
		else {
			accumulatedInterval += TIMER_DELAY;
			guardArea.repaint();
			
		}
	}

	
	/*
	 * Services for Remote Client. 
	 */
	
	public void makeConnectionToMe(String url, String serviceName) {
	}
	
	
	public void setGuardMode(boolean auto, String dog) {
		/*
		 * Following code is only for demonstration of RMI connection.
		 * Simply output message to JTextArea
		 */
		if (auto) {
			messageBox.append("Autonomous guarding mode set by remote owner\n");
		} else {
			messageBox.append("Owner designated "+dog+ " in charge\n");			
		}
	}
}


public class HomeSimulation {

	private static void createAndShowGUI(SecurityControl control) {
		GuardArea guardedArea = new GuardArea(control.getDogs(), control.getBurglar());
		guardedArea.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));

		StatusDisplayArea statusArea = new StatusDisplayArea();
		statusArea.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
		
		JFrame frame = new JFrame("Home being guarded");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().add(guardedArea,BorderLayout.CENTER);
		frame.getContentPane().add(statusArea,BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
		control.setGuardArea(guardedArea);
		control.setStatusArea(statusArea.getMessageBox());
		control.setTimer(new Timer(SecurityControl.TIMER_DELAY, control));
		control.startTimer();
	}
	
	public static void main(String[] args) {
		try {
			
			System.out.println("I'm binding my service to registry, then wait for 30seconds for app to register its service to registry");
			HomeRemoteProvision control = new SecurityControl();
			Naming.rebind("rmi://localhost:5000/skku_oop_home", control);

			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI((SecurityControl)control);
				}
			});
		} catch (RemoteException e) {
			System.out.println(e);
		} catch (MalformedURLException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
} //End of class HomeSimulation
