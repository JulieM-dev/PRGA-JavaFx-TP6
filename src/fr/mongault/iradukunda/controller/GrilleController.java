package fr.mongault.iradukunda.controller;

import fr.mongault.iradukunda.MainApp;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class GrilleController implements Controller
{
	private MainApp mainApp;
	private static final String NOMMETHODE = " - Grille";
	
	@FXML
	private GridPane monGridPane;
	
	@Override
	public void init()
	{
		
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


	
}
