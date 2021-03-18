package fr.mongault.iradukunda.controller;

import fr.mongault.iradukunda.MainTP6;
import fr.mongault.iradukunda.model.ChargerGrilleAutre;
import fr.mongault.iradukunda.model.MotsCroisesFactory;
import fr.mongault.iradukunda.model.MotsCroisesTP6;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class GrilleController implements Controller
{
	private MainTP6 mainApp;
	private static final String NOMMETHODE = " - Grille";
	
	private MotsCroisesTP6 motsCroises;
	
	@FXML
	private GridPane monGridPane;
	
	@FXML
	public void clicCase(MouseEvent e) 
	{
		if (e.getButton() == MouseButton.MIDDLE)
		{
			// C'est un clic "central" (de la molette de souris)
			TextField caseFX = (TextField) e.getSource();
			int lig = ((int) caseFX.getProperties().get("gridpane-row")) + 1 ;
			int col = ((int) caseFX.getProperties().get("gridpane-column")) + 1 ;
			// demande de révélation de la solution sur (lig,col)
			motsCroises.reveler(lig, col);
		}
	}
	
	
	@FXML
	@Override
	public void initialize()
	{
		ChargerGrilleAutre chargerGrille = new ChargerGrilleAutre();
		motsCroises = chargerGrille.extraireGrille(6);
		clearGrille();
		createGrille();
		
		initProperties();
		
	}
	
	private void clearGrille()
	{
		monGridPane.getChildren().clear();
	}
	
	private void createGrille()
	{
		int maxLig = motsCroises.getHauteur();
		int maxCol= motsCroises.getLargeur();
		for(int lig = 1; lig <= maxLig; lig++)
		{
			for( int col = 1; col <= maxCol; col++)
			{
				if(!motsCroises.estCaseNoire(lig, col))
				{
					TextField tf = new TextField();
					tf.setPrefHeight(40);
					tf.setPrefWidth(40);
					monGridPane.add(tf ,col-1 ,lig-1);
				}
			}
		}
	}
	
	private void initProperties()
	{
		for (Node n : monGridPane.getChildren())
		{
			if (n instanceof TextField)
			{
				TextField tf = (TextField) n ;
				int lig = ((int) n.getProperties().get("gridpane-row")) + 1 ;
				int col = ((int) n.getProperties().get("gridpane-column")) + 1 ;
				
				// Initialisation du TextField tf ayant pour coordonnées (lig, col)
				// 1.3
				tf.textProperty().bindBidirectional(motsCroises.propositionProperty(lig, col));
				
				// 1.4
				String textH = motsCroises.getDefinition(lig, col, true);
				String textV = motsCroises.getDefinition(lig, col, false);
				
				if(textH != null && textV != null) 
				{
					tf.setTooltip(new Tooltip("(horizontal / vertical): " + textH + " / " + textV));
					System.out.println("Double sur : "+ lig + " " + col);
				}
				else if(textH != null) 
				{
					tf.setTooltip(new Tooltip("(horizontal): " + textH));
					System.out.println("Horiz sur : "+ lig + " " + col);
				}
				else if(textV != null) 
				{
					tf.setTooltip(new Tooltip("(vertical): " +textV));
					System.out.println("Verti sur : "+ lig + " " + col);
				}
					
				
				// 1.5 
				tf.setOnMouseClicked((e) -> {this.clicCase(e);});
				
			}
		}
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
