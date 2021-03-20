package fr.mongault.iradukunda.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.mongault.iradukunda.MainTP6;
import fr.mongault.iradukunda.model.ChargerGrilleAutre;
import fr.mongault.iradukunda.model.GrilleInfo;
import fr.mongault.iradukunda.model.MotsCroisesFactory;
import fr.mongault.iradukunda.model.MotsCroisesTP6;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class GrilleController implements Controller
{
	private MainTP6 mainApp;
	private final static String NOMMETHODE = " - ";
	private GrilleInfo infos;
	
	private MotsCroisesTP6 motsCroises;
	
	// 0 = bloqué; 1 = horizontal; 2 = vertical
	private int dir;
	
	private int posLig;
	private int posCol;
	
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
		dir = findDirection();
	}
	
	
	@FXML
	@Override
	public void initialize()
	{
		if(mainApp != null)
		{
			ChargerGrilleAutre chargerGrille = new ChargerGrilleAutre();
			int num_grille = mainApp.getNumGrille();
			// Si pas de grille choisie, choisir une grille au hasard
			if(num_grille <= 0)
			{
				infos = chargerGrille.getRandomGrille();
			}
			else
			{
				infos = new GrilleInfo(num_grille, chargerGrille.getNomGrille(num_grille));
			}
			
			motsCroises = chargerGrille.extraireGrille(infos.getNum());
			
			createGrille();
			
			initProperties();
		}
		
		
	}
	
	private void clearGrille()
	{
		monGridPane.getChildren().clear();
	}
	
	private void createGrille()
	{
		int maxLig = motsCroises.getHauteur();
		int maxCol= motsCroises.getLargeur();
		TextField modele = (TextField) monGridPane.getChildren().get(0);
		clearGrille();
		for(int lig = 1; lig <= maxLig; lig++)
		{
			for( int col = 1; col <= maxCol; col++)
			{
				if(!motsCroises.estCaseNoire(lig, col))
				{
					TextField tf = new TextField();
					for (Object cle : modele.getProperties().keySet())
					{
						tf.getProperties().put(cle, modele.getProperties().get(cle)) ;
					} 
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
				}
				else if(textH != null) 
				{
					tf.setTooltip(new Tooltip("(horizontal): " + textH));
				}
				else if(textV != null) 
				{
					tf.setTooltip(new Tooltip("(vertical): " +textV));
				}
					
				
				// 1.5 
				tf.setOnMouseClicked((e) -> {this.clicCase(e);});
				
				tf.focusedProperty().addListener(new ChangeListener<Boolean>()
				{
				    @Override
				    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
				    {
				        if (newPropertyValue)
				        {
				            focusCase(tf);
				        }
				    }
				});
				
				tf.addEventFilter(KeyEvent.KEY_TYPED, (event) -> 
				{
					typedCase(event);
				});
				tf.setOnKeyPressed((event) -> 
				{
					keyPressedCase(event);
				});
				
			}
		}
	}
	
	private int findDirection()
	{
		if(motsCroises.coordCorrectes(posLig, posCol+1) && !motsCroises.estCaseNoire(posLig, posCol+1))
		{
			return 1;
		}
		else if(motsCroises.coordCorrectes(posLig+1, posCol) && !motsCroises.estCaseNoire(posLig+1, posCol))
		{
			return 2;
		}
		else
		{
			return 0;
		}
	}
	
	@FXML
	public void focusCase(TextField tf)
	{
		posLig = ((int) tf.getProperties().get("gridpane-row"))+1;
		posCol = ((int) tf.getProperties().get("gridpane-column"))+1;
		int newDir = findDirection();
		if(newDir == 0)
		{
			dir = 0;
		}
		else if(!(dir == 2  && motsCroises.coordCorrectes(posLig+1, posCol) && !motsCroises.estCaseNoire(posLig+1, posCol)))
		{
			dir = newDir;
		}
	}
	
	@FXML
	public void typedCase(KeyEvent e)
	{
		Pattern p = Pattern.compile("[a-zA-Z]");
		Matcher m = p.matcher(e.getCharacter());
		TextField tf = (TextField) e.getSource();
		if(m.matches())
		{
			tf.setText(e.getCharacter().toUpperCase());
			if(dir == 1)
			{
				TextField n = (TextField) getCase(posLig, posCol+1);
				n.requestFocus();
			}
			else if(dir == 2)
			{
				TextField n = (TextField) getCase(posLig+1, posCol);
				n.requestFocus();
			}
		}
		e.consume();
		
	}
	
	@FXML
	public void keyPressedCase(KeyEvent e)
	{
		
		if(e.getCode().equals("BACK_SPACE"))
		{
			
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
		return NOMMETHODE + infos.getNom();
	}

	
	//GridPane specific functions
	private Node getCase(int lig, int col) {
		assert motsCroises.coordCorrectes(lig, col);
		assert !motsCroises.estCaseNoire(lig, col);
	    for (Node node : monGridPane.getChildren()) {
	        if (GridPane.getColumnIndex(node) == col-1 && GridPane.getRowIndex(node) == lig-1) {
	            return node;
	        }
	    }
	    return null;
	}

	
}
