package fr.mongault.iradukunda.controller;

import java.util.List;
import java.util.Map;

import com.sun.webkit.ContextMenu.ShowContext;

import fr.mongault.iradukunda.MainTP6;
import fr.mongault.iradukunda.model.ChargerGrille;
import fr.mongault.iradukunda.model.GrilleInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuController implements Controller
{
	private static final String NOMMETHODE = "";
	MainTP6 mainApp;
	
	@FXML
	public MenuItem menuIDefault;
	
	@FXML
	public ComboBox comboChoix;
	
   	@FXML
   	public void handleRandom()
   	{
   		mainApp.setNumGrille(0);
   		mainApp.showView("VueTP6");
   	}
   	
	
   	@FXML
   	public void handleQuit()
   	{
   		Platform.exit();
   	}
   	
   	@FXML
   	public void handleHome()
   	{
   		mainApp.showView("MenuView");
   	}
   	
   	@FXML
   	public void handleChoix()
   	{
   		GrilleInfo grille = (GrilleInfo) comboChoix.getValue();
   		mainApp.setNumGrille(grille.getNum());
   		mainApp.showView("VueTP6");
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

	@Override
	public void initialize()
	{
		if(mainApp != null)
		{
			ChargerGrille chargerGrille = new ChargerGrille();
			List<GrilleInfo> grilles = chargerGrille.grillesDisponibles();
			
			if(comboChoix != null)
			{
				for(GrilleInfo grille : grilles)
				{
					comboChoix.getItems().add(grille);
				}
			}
			
		}
		
	}
}
