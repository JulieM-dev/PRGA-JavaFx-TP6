package fr.mongault.iradukunda.controller;

import fr.mongault.iradukunda.MainApp;
import javafx.fxml.FXML;

public class MenuController implements Controller
{
	   private static final String NOMMETHODE = "  - MENU";
	   MainApp mainApp;

	   @FXML
	   private void handleHome()
	   {
		   mainApp.showView("Home");
	   }
	   
	   @FXML
	   private void handleB1()
	   {
		   mainApp.showView("GrilleView");
	   }
	   
	   @FXML
	   private void handleB2()
	   {
		   mainApp.showView("b2");
	   }
	   
	   
	   @FXML
	   private void handleB3()
	   {
		   mainApp.showView("b3");
	   }
	   
		@Override
		public void setMainApp(MainApp mainApp)
		{
			this.mainApp = mainApp;
			
		}
	
		@Override
		public String getNOMMETHODE()
		{
			return NOMMETHODE;
		}

		@Override
		public void init()
		{
			// TODO Auto-generated method stub
			
		}
}
