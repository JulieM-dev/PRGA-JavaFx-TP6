/**
 * JavaFX TP6 PRGA
 * 
 * @author JulienMongault
 */
package fr.mongault.iradukunda.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.mongault.iradukunda.MainTP6;
import fr.mongault.iradukunda.model.ChargerGrille;
import fr.mongault.iradukunda.model.GrilleInfo;
import fr.mongault.iradukunda.model.MotsCroisesTP6;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class GrilleController implements Controller
{
	private MainTP6 mainApp;
	private final static String NOMMETHODE = " - ";
	
	// Le nom et l'id de la grille
	private GrilleInfo infos;
	
	// L'objet de la grille
	private MotsCroisesTP6 motsCroises;
	
	// Si vrai, le focus avance sur l'axe horizontal, vertical si faux
	// Qu'importe dirH, on avance pas si doubleDir est vrai!
	private boolean dirH = true;
	
	// Dans le cas où on ne peux pas déterminer l'axe, on n'avancera pas 
	// sur un axe précis tant que l'utilisateur n'en aura pas choisi un avec les flèches
	private boolean doubleDir = false;
	
	// Le focus ne peux plus avancer (quand on écris)
	private boolean blocked = false;
	
	// Le focus ne peux plus reculer (quand on supprime)
	private boolean backBlocked = false;
	
	// Les positions de la case en focus (par rapport à motsCroises, donc on commence à 1,1)
	private int posLig;
	private int posCol;
	
	TextField nextCase; 
	TextField actualCase; 
	
	@FXML
	// La grille contenant les TextFields (pas de textField = case noire) 
	private GridPane monGridPane;
	
	@FXML
	private Label labelToutBon;
	
// --------- INITIALISATIONS ----------------
	
	@FXML
	@Override
	// Est lancé par le mainApp pour initialiser une grille de MotsCroisesTP6
	public void initialize()
	{
		if(mainApp != null)
		{
			ChargerGrille chargerGrille = new ChargerGrille();
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
	
	// Initialise les propriétés des textFields
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
				tf.setId("tfCase");

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
					
				
				// 1.5 révélation d'une case sur un clic molette
				tf.setOnMouseClicked((e) -> {this.clicCase(e);});
				
				// ----Autre handler et filtres
				
				// Listener sur le focus d'une case
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
				
				// Listener sur le textProperty pour animer une écriture
				tf.textProperty().addListener(new ChangeListener<String>()
						{

							@Override
							public void changed(ObservableValue<? extends String> observable, String oldValue,
									String newValue)
							{
								ScaleTransition st = new ScaleTransition(Duration.millis(200), tf);
						        st.setFromX(1.2);
						        st.setFromY(1.2);
						        st.setToX(1);
						        st.setToY(1);
						        st.play();
							}
							
						}
					);
				
				// Filter qui évite que l'utilisateur ne tape n'importe quoi
				tf.addEventFilter(KeyEvent.KEY_TYPED, (event) -> 
				{
					typedCaseFilter(event);
				});
				
				// Handler qui effectue différentes action en fonction de la touche pressée
				// ex: ENTER, BACK_SPACE, LEFT...
				tf.setOnKeyPressed((event) -> 
				{
					keyPressedCase(event);
				});
				
		        
			}
		}
	}
	
	// Vide la grille
		private void clearGrille()
		{
			monGridPane.getChildren().clear();
		}
		
		// Rempli le gridPane de TextFields en fonction de motsCroises
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
					// Si la case n'est pas noire on ajoute un textField au gridPane
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
	
// --------- HANDLERS ET FILTRES ----------------
	
	@FXML
	// Se lance lorsqu'on clique sur une case
	// Cette méthode va dévoiler la réponse lors d'un clic molette
	// Et vérifie dans le cas d'un clic quelconque si l'utilisateur compte écrire sa réponse sur un axe précis 
	public void clicCase(MouseEvent e) 
	{
		TextField caseFX = (TextField) e.getSource();
		posLig = ((int) caseFX.getProperties().get("gridpane-row")) + 1 ;
		posCol = ((int) caseFX.getProperties().get("gridpane-column")) + 1 ;
		// C'est un clic "central" (de la molette de souris)
		if (e.getButton() == MouseButton.MIDDLE)
		{
			// demande de révélation de la solution sur (lig,col)
			motsCroises.reveler(posLig, posCol);
		}
		
		// Si la case actuelle a une définition sur l'axe horizontal seulement
		if(motsCroises.getDefinition(posLig, posCol, true) != null && motsCroises.getDefinition(posLig, posCol, false) == null)
		{
			// Choisir l'axe horizontal comme axe actuel
			dirH = true;
			doubleDir = false;	
		}
		else if(motsCroises.getDefinition(posLig, posCol, false) != null && motsCroises.getDefinition(posLig, posCol, true) == null)
		{
			// Choisir l'axe vertical comme axe actuel
			dirH = false;
			doubleDir = false;
		}
		else
		{
			// Dans le cas où on ne peux pas déterminer l'axe, le focus n'avancera pas
			// sur un axe précis tant que l'utilisateur n'en aura pas choisi un avec les flèches
			doubleDir = true;
		}
		// On vérifie si le focus peux avancer ou reculer sur l'axe actuel
		checkIfBlocked();
		colorNextCase();
	}

	@FXML
	//Se lance lorsqu'une case est mise en focus
	public void focusCase(TextField tf)
	{

		// On récupère les positions de la case
		posLig = ((int) tf.getProperties().get("gridpane-row"))+1;
		posCol = ((int) tf.getProperties().get("gridpane-column"))+1;
		// On vérifie si le focus peux avancer ou reculer sur l'axe actuel
		checkIfBlocked();
		colorNextCase();
	}
	
	@FXML
	// Ce filtre est lancé à chaque fois qu'un charactère est écrit dans un textField
	// On va stopper l'écriture par défaut en contournant le système d'écriture 
	// afin de bloquer l'insertion d'un charactère non voulu
	public void typedCaseFilter(KeyEvent e)
	{
		// Une simple expression régulière pour vérifier si le charactère est bien entre a et z
		Pattern regex = Pattern.compile("[a-zA-Z]");
		Matcher m = regex.matcher(e.getCharacter());
		TextField tf = (TextField) e.getSource();
		// Si le charactère est entre a et Z
		if(m.matches())
		{
			// On remplace le texte du texField avec le charactère en majuscule
			tf.setText(e.getCharacter().toUpperCase());
			if(!motsCroises.propositionEstCorrecte(posLig, posCol))
			{
				tf.setId("tfCase");
			}
			// Si on a un axe précisé et libre alors on avance le focus
			if(!doubleDir && dirH && !blocked)
			{
				TextField n = (TextField) getCase(posLig, posCol+1);
				n.requestFocus();
			}
			else if(!doubleDir && !dirH && !blocked)
			{
				TextField n = (TextField) getCase(posLig+1, posCol);
				n.requestFocus();
			}
		}
		// On stop l'évènement d'écriture 
		e.consume();
		
	}
	
	
	@FXML
	// On effectue différentes actions en fonction de la touche préssée
	public void keyPressedCase(KeyEvent e)
	{
		switch(e.getCode())
		{
			case BACK_SPACE:
				actualCase.setText("");
				actualCase.setId("tfCase");
				// Si on a un axe précisé et libre alors on recule le focus
				if(!doubleDir && dirH && !backBlocked)
				{
					TextField n = (TextField) getCase(posLig, posCol-1);
					n.requestFocus();
				}
				else if(!doubleDir && !dirH && !backBlocked)
				{
					TextField n = (TextField) getCase(posLig-1, posCol);
					n.requestFocus();
				}
				break;
			case ENTER:
				labelToutBon.setVisible(verifierToutesLesCases());
				break;
			case RIGHT:
				dirH = true; doubleDir = false;
				checkIfBlocked();
				if(!blocked)
				{
					TextField n = (TextField) getCase(posLig, posCol+1);
					n.requestFocus();
				}
				break;
			case DOWN:
				dirH = false; doubleDir = false;
				checkIfBlocked();
				if(!blocked)
				{
					TextField n = (TextField) getCase(posLig+1, posCol);
					n.requestFocus();
				}
				break;
			case LEFT:
				dirH = true; doubleDir = false;
				checkIfBlocked();
				if(!backBlocked)
				{
					TextField n = (TextField) getCase(posLig, posCol-1);
					n.requestFocus();
				}
				break;
			case UP:
				dirH = false; doubleDir = false;
				checkIfBlocked();
				if(!backBlocked)
				{
					TextField n = (TextField) getCase(posLig-1, posCol);
					n.requestFocus();
				}
				break;
		}
			
	}
	
	
// -------------- MÉTHODES CONCERNANT LE DÉPLACEMENT DU FOCUS ---------- 
	
	// Cette méthode vérifie si le focus peux avancer en fonction de l'axe
	private void checkIfBlocked()
	{
		// Vérifications sur l'axe horizontal
		if(dirH)
		{
			//Vérifie si on peux aller vers la DROITE
			if(canMove(posLig, posCol+1) ) blocked = false;
			else blocked = true;
			
			//Vérifie si on peux aller vers la GAUCHE
			if(canMove(posLig, posCol-1)) backBlocked = false;
			else backBlocked = true;
		}
		//Vérifications sur l'axe vertical
		else
		{
			//Vérifie si on peux aller vers le BAS
			if(canMove(posLig+1, posCol) ) blocked = false;
			else blocked = true;
			
			//Vérifie si on peux aller vers le HAUT
			if(canMove(posLig-1, posCol)) backBlocked = false;
			else backBlocked = true;
		}

	}
	
	// Retourne vrai si la case existe et n'est pas noire
	private boolean canMove(int lig, int col)
	{
		return motsCroises.coordCorrectes(lig, col) && !motsCroises.estCaseNoire(lig, col);
	}
	
	
	// On commence par réinitialiser les styles des cases courantes
	// On récupère la prochaine case en fonction de l'axe (aucune si doubleDire est vrai)
	// On applique les styles sur la case courante et la prochaine
	private void colorNextCase()
	{
		if(nextCase != null)
		{
			nextCase.setStyle("");
			nextCase = null;
		}
		if(actualCase != null)
		{
			actualCase.setStyle("");
		}
		if(!doubleDir && !blocked)
		{
			if(dirH)
			{
				nextCase = (TextField) getCase(posLig, posCol+1);
			}
			else
			{
				nextCase = (TextField) getCase(posLig+1, posCol);
			}
			// Ajoute un bord de case bleu clair en pointillés
			nextCase.setStyle("-fx-border-color: #2980b9 ; -fx-border-width: 4px; -fx-border-style: dotted;");
		}
		actualCase = (TextField) getCase(posLig, posCol);
		// Ajoute un bord bleu
		actualCase.setStyle("-fx-border-color: blue ; -fx-border-width: 4px;");
		
	}
	
// --------------- METHODES POUR VÉRIFIER LES PROPOSITIONS ET RÉCUPÉRER UNE CASE -------
	
	// Vérifie toutes les cases, applique l'id tfValidCase appliquant 
	// une couleur verte de fond aux réponses valides (couleur déterminée dans le css)
	public boolean verifierToutesLesCases()
	{
		boolean ret = true;
		for(Node n : monGridPane.getChildren())
		{
			if(n instanceof TextField)
			{
				TextField tf = (TextField) n ;
				int lig = ((int) n.getProperties().get("gridpane-row")) + 1 ;
				int col = ((int) n.getProperties().get("gridpane-column")) + 1 ;
				if(motsCroises.propositionEstCorrecte(lig, col))
				{
					tf.setId("tfValidCase");
				}
				else
				{
					tf.setId("tfCase");
					ret = false;
				}
			}
		}
		return ret;
	}
	

	
	//Récupère le textField aux coordonées indiquées
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

	
	// ----------- MÉTHODES GÉNÉRIQUES DE L'INTERFACE CONTROLLER -------------
	
	@Override
	public void setMainApp(MainTP6 mainApp)
	{
		this.mainApp = mainApp;
	}

	// Le nom qui sera affiché en haut de la fenêtre
	@Override
	public String getNOMMETHODE()
	{
		return NOMMETHODE + infos.getNom();
	}

	
}
