/**
 * JavaFX Template to make easy applications fast!
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
	

	@Override
	public void start(Stage primaryStage) 
	{
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle(TITLE);
        this.primaryStage.setResizable(true);
        
        //The root of your application
        initRootLayout();
        
        //TODO Place here your home page
        showView("VueTP6");
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
            primaryStage.setScene(scene);

            // Donne l'accès au controleur à l'application main.
            RootController controleur = loader.getController();
            controleur.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showView(String view) {
        try {
            // Load the view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainTP6.class.getResource("view/" + view + ".fxml"));
            Pane theView = (Pane) loader.load();

            // Positionne la view au centre de l'affichage racine.
            rootLayout.setCenter(theView);

            // Récupère le controller et lui donne l'accès à l'application MainApp.
            Controller controller = loader.getController();
            controller.setMainApp(this);
            this.primaryStage.setTitle(TITLE + controller.getNOMMETHODE());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	
}
