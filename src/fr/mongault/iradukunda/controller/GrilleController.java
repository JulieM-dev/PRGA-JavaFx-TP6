package fr.mongault.iradukunda.controller;

import fr.mongault.iradukunda.MainTP6;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class GrilleController implements Controller
{
	private MainTP6 mainApp;
	private static final String NOMMETHODE = " - Grille";
	
	@FXML
	private GridPane monGridPane;
	
	@Override
	public void init()
	{
		
	}
	
	@Override
	public void setMainApp(MainTP6 mainApp)
	{
		this.mainApp = mainApp;
	}

	@Override
	public String getNOMMETHODE()
	{
		return NOMMETHODE;
	}


	
}
