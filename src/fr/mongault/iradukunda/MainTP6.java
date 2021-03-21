/**
 * JavaFX TP6 PRGA
 * 
 * @author JulienMongault
 */
package fr.mongault.iradukunda;

import java.io.IOException;

import fr.mongault.iradukunda.controller.Controller;
import fr.mongault.iradukunda.controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainTP6 extends Application 
{
	public int screenHeight = 600;
	public int screenWidth = 800;
	
	private final String TITLE = "TP6 Mongault-Iradukunda";

	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private static int num_grille = 0;
	

	@Override
	public void start(Stage primaryStage) 
	{
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle(TITLE);
        this.primaryStage.setResizable(true);
        
        //The root of your application
        initRootLayout();
        
        
        //TODO Place here your home page
        showView("MenuView");
	}
	
    private void initRootLayout() {
        try {
            // Load the root view from the fxml file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainTP6.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Affiche la scene dans le layout racine.
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(getClass().getResource("grille.css").toExternalForm());
            primaryStage.setScene(scene);

            // Donne l'accès au controleur à l'application main.
            RootController controleur = loader.getController();
            controleur.setMainApp(this);
            
            // Génère le menuBar et le positionne en haut de l'affichage racine
            Pane theView = generateView("MenuBarView");
            rootLayout.setTop(theView);
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showView(String view) {
        try {
            Pane theView = generateView(view);
            // Positionne la view au centre de l'affichage racine.
            rootLayout.setCenter(theView);
            
            if(view.equals("MenuView"))
            {
            	// Cache le menuBar
            	rootLayout.getChildren().get(0).setVisible(false);
            }
            else
            {
            	// Montre le menuBar
            	rootLayout.getChildren().get(0).setVisible(true);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Pane generateView(String view) throws IOException
    {
        // Load the view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainTP6.class.getResource("view/" + view + ".fxml"));
        Pane theView = (Pane) loader.load();

        // Récupère le controller et lui donne l'accès à l'application MainApp.
        Controller controller = loader.getController();
        controller.setMainApp(this);
        controller.initialize();
        this.primaryStage.setTitle(TITLE + controller.getNOMMETHODE());
        return theView;
    }
    
    public int getNumGrille()
    {
    	return num_grille;
    }
    
    public void setNumGrille(int num_grille)
    {
    	this.num_grille = num_grille;
    }
	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	
}
