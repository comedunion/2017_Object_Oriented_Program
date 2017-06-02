import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
}


class UserAppControl extends UnicastRemoteObject implements ActionListener, AppRemoteProvision {
	
	static final String CMD_CALL_POLICE = "police";
	static final String CMD_GET_UPDATED = "update";
	static final String CMD_TOGGLE_GUARD_MODE = "mode";	
	static final String CMD_DELIGATE = "deligate";
	
	HomeRemoteProvision homeStub;
	
	// Control Fields
	AlarmDisplayArea alarmDisplay; 
	JTextArea infoDisplay;
	

	UserAppControl(HomeRemoteProvision stub) throws RemoteException {
		homeStub = stub;
	}
	
	public void registerAlarmDisplay(AlarmDisplayArea alarm) {
		alarmDisplay = alarm;
	}
	
	public void registerInfoDisplay(JTextArea info) {
		infoDisplay = info;
	}

	public void informHomeofModeChange(boolean auto, String whichDog) {
		try {
			homeStub.setGuardMode(auto, whichDog);
		} catch (RemoteException e) {
			infoDisplay.append("Unexpected RMI error to Home\n");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		
		switch(actionCmd) {
			case CMD_CALL_POLICE : 
				System.out.println("You have pushed a button for reporting to police");
				break;
			case CMD_GET_UPDATED :
				System.out.println("Contacting home and getting the lastest update!!");
				break;
		}
	}

	/*
	 * Following methods are provided as RMI service to home and police
	 */
	public void intrusionDetected() {
		alarmDisplay.initiateAlert();
	}
	
	public void situationFinished() {
		alarmDisplay.stopAlert();		
	}
	
	public void statusMessage(String strMsg) {
		infoDisplay.append(strMsg+"\n");
	}
	
} // End of class UserAppControl



class AlarmDisplayArea extends JPanel implements ActionListener {
	
	private boolean emergencyNotify = false;
	private int repaintCount = 0;
	private Timer timer;
	private BufferedImage normalImage, emergencyImage;
	UserAppControl control;
	
	AlarmDisplayArea(UserAppControl control) {
		super();
		this.control = control;
		setPreferredSize(new Dimension(100,100));
//		setBackground(Color.white);
		timer = new Timer(500, this);
		normalImage = Utilities.getImage(UserAppSimulation.IMG_LOCATION_NORMAL);
		emergencyImage = Utilities.getImage(UserAppSimulation.IMG_LOCATION_EMERGENCY);
		
	}

	
	public void initiateAlert() {
		emergencyNotify = true;
		timer.start();
		repaint();
	}
	
	public void stopAlert() {
		emergencyNotify = false;
		timer.stop();
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		//Got timer signal through ActionEvent
		System.out.println("Timer Signal for Emergency");
		repaint();
	}	
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (emergencyNotify) { 
			Font f = new Font("Arial Black", Font.BOLD, 12);
			g.setFont(f);
			
			repaintCount++;
				if (repaintCount % 2 == 0) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.magenta);
					g.drawImage(emergencyImage, 
							    getWidth()/2-emergencyImage.getWidth()/2, 
							    getHeight()-emergencyImage.getHeight()-30, null);
				}
				g.drawString("INTRUSION", (getWidth()/2)-45, getHeight()-12);
		
				if (repaintCount >= 50) {
					g.setColor(Color.red);
					g.drawImage(emergencyImage, 
						    getWidth()/2-emergencyImage.getWidth()/2, 
						    getHeight()-emergencyImage.getHeight()-30, null);
					g.drawString("INTRUSION", (getWidth()/2)-45, getHeight()-12);
					repaintCount = 0;
					timer.stop();
				}
		} else {
			// Put Regular Icon
			g.drawImage(normalImage, 
				    getWidth()/2-normalImage.getWidth()/2, 
				    (getHeight()- normalImage.getHeight())/2, null);
		}
	}
}


class SelectionArea extends JPanel implements ActionListener {
	
	UserAppControl control;
	JCheckBox cb1, cb2, cb3;
	ButtonGroup btnGroup;
	JButton btnDeligate;
	
	SelectionArea(UserAppControl control) {
		super();
		this.control = control;
		setPreferredSize(new Dimension(300,100));
		btnGroup = new ButtonGroup();
		JToggleButton tglBtn = new JToggleButton();
		tglBtn.setSelected(true);
		tglBtn.setText("Auto");
		tglBtn.setActionCommand(UserAppControl.CMD_TOGGLE_GUARD_MODE);
		tglBtn.addActionListener(this);
		tglBtn.setPreferredSize(new Dimension(70,90));
		JPanel wrapper = new JPanel();
		wrapper.setPreferredSize(new Dimension(275,90));
		wrapper.setLayout(new FlowLayout());
		wrapper.setBorder(BorderFactory.createLineBorder(Color.lightGray,1));
		cb1 = new JCheckBox("Chiwawa");
		cb1.setEnabled(false);
		cb1.setSelected(true);  // Default deligate dog is Chiwawa
		btnGroup.add(cb1);
		wrapper.add(cb1);
		cb2 = new JCheckBox("Rottweiler");
		cb2.setEnabled(false);
		btnGroup.add(cb2);
		wrapper.add(cb2);
		cb3 = new JCheckBox("Border Collie");
		cb3.setEnabled(false);
		btnGroup.add(cb3);
		wrapper.add(cb3);
		btnDeligate = new JButton("Confirm Deligation");
		btnDeligate.setEnabled(false);
		btnDeligate.setActionCommand(UserAppControl.CMD_DELIGATE);
		btnDeligate.addActionListener(this);
		wrapper.add(btnDeligate);
		add(tglBtn,BorderLayout.EAST);
		add(wrapper,BorderLayout.CENTER);
		JButton btnReport = new JButton("Police");
		btnReport.setActionCommand(UserAppControl.CMD_CALL_POLICE);
		btnReport.addActionListener(control);
		btnReport.setPreferredSize(new Dimension(90,90));
		add(btnReport,BorderLayout.WEST);
		
	}

	
    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		
		switch(actionCmd) {
			case UserAppControl.CMD_TOGGLE_GUARD_MODE :
				System.out.println("ToggleButton");
		        AbstractButton abstractButton = (AbstractButton) e.getSource();
		        ButtonModel buttonModel = abstractButton.getModel();
		        if (!buttonModel.isSelected()) {
		        	abstractButton.setText("Manual");
		        	System.out.println("Toggle button selected");
		        	cb1.setEnabled(true);
		        	cb2.setEnabled(true);
		        	cb3.setEnabled(true);
		        	btnDeligate.setEnabled(true);
		        }
		        else {
		        	abstractButton.setText("Auto");
		        	System.out.println("Toggle button NOT selected");
		        	cb1.setEnabled(false);
		        	cb2.setEnabled(false);
		        	cb3.setEnabled(false);
		        	btnDeligate.setEnabled(false);
					control.informHomeofModeChange(true, null);		        	
		        }
				break;
			case UserAppControl.CMD_DELIGATE :
				System.out.println("A dog is chosen to take care of guard");
				// Get the name(species) of Dog from check box group, and makes call to 
				// house to say manual model is chosen, through control
				control.informHomeofModeChange(false, getSelectedButtonText(btnGroup));
				break;
		}
	}

}


class StatusDisplayArea extends JPanel {
	
	JTextArea infoDisplay;
	UserAppControl control;
	
	StatusDisplayArea(UserAppControl control) {
		super();
		this.control = control;
		infoDisplay = new JTextArea(5,50);
		infoDisplay.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(infoDisplay,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane);		
	}
	
	public JTextArea getMessageBox() {
		return infoDisplay;
	}
}



public class UserAppSimulation {

	// Image resources under c:\images

	static final String IMG_LOCATION_NORMAL = "c:/images/normal.png";
	static final String IMG_LOCATION_EMERGENCY = "c:/images/emergency.png";

	
	
	
	private static void createAndShowGUI(UserAppControl control) {
		
		JFrame appFrame = new JFrame("Master's App Up & Running");
	    appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setResizable(false);

		AlarmDisplayArea alarmDisplay = new AlarmDisplayArea(control);
		alarmDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));

		SelectionArea userControls = new SelectionArea(control);
		userControls.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
		
		StatusDisplayArea statusDisplay = new StatusDisplayArea(control);
		statusDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));

		
		//Adding Alarm panel
		appFrame.getContentPane().add(alarmDisplay,BorderLayout.WEST);

		//Adding User selection/control panel 
		appFrame.getContentPane().add(userControls,BorderLayout.CENTER);
		
		//Adding panel for Uneditable JTextArea to receive various status (from home, police, 119 etc.)
		appFrame.getContentPane().add(statusDisplay,BorderLayout.SOUTH);
	
		control.registerAlarmDisplay(alarmDisplay);
		control.registerInfoDisplay(statusDisplay.getMessageBox());
		
		appFrame.pack();
		appFrame.setVisible(true);
		
		// For test only
		// alarmDisplay.alertMaster();
	
	}

	public static void main(String[] args) {
		
		try {
			// Lookup the remote service from the rmi registry, and get stub
			String myUrl = "rmi://localhost:5000";
			String myServiceName = "skku_oop_app";
			
			// Looup server service
			HomeRemoteProvision serverStub = (HomeRemoteProvision)Naming.lookup("rmi://localhost:5000/skku_oop_home");
			// Create my control object, with server stub as argument
			AppRemoteProvision control = new UserAppControl(serverStub);

			// Bind my service to registry
			String myRMIServiceURL = "rmi://localhost:5000/skku_oop_app";
			System.out.println("Binding my service to registry : " + myRMIServiceURL);
			Naming.rebind(myRMIServiceURL, control);
			System.out.println("Binding done successfully");
			
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI((UserAppControl)control);
	        	}
			});

		} catch (Exception e) {
			System.out.println(e);
		}

	}	
	
} //End of Class UserAppSimulation
