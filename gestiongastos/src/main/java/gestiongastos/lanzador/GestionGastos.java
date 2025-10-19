package gestiongastos.lanzador;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gestiongastos.vista.LoginWindow;



public class GestionGastos {
	public static void main(final String[] args){
		 try {
	            //FlatMacLightLaf.setup();
	            UIManager.put("Component.arc", 24);
	            
	        } catch (Exception ex) {
	            System.err.println("Failed to initialize FlatLaf");
	        }
		 LoginWindow login = new LoginWindow();
		 login.setVisible(true);
	    
		
	}
}
