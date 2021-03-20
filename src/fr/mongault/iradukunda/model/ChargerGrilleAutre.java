package fr.mongault.iradukunda.model;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargerGrilleAutre {

    private static Connection connection;

    public ChargerGrilleAutre() {
        try {
            connection = connecterBD();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection connecterBD() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/base_bousse?autoReconnect=true&useSSL=false", "root", "root");
    }

    public List<GrilleInfo> grillesDisponibles() {
        List<GrilleInfo> grilles = new ArrayList<GrilleInfo>();
        ResultSet reqSelection = execReqSelection("select num_grille, nom_grille from tp5_grille");
        try {
            while (reqSelection.next()) {
                grilles.add(new GrilleInfo(reqSelection.getInt(1), reqSelection.getString(2)));
            }
        } catch (Exception e) {
            System.out.println("erreur reqSelection.next() pour la requête - select num_grille, nom_grille from tp5_grille");
            e.printStackTrace();
        }
        fermerConnexionBd();
        return grilles;
    }
    
    public GrilleInfo getRandomGrille()
    {
    	ResultSet infos = execReqSelection("SELECT num_grille, nom_grille FROM tp5_grille ORDER BY RAND() LIMIT 1");
    	try
		{
    		if(infos.next())
    		{
    			return new GrilleInfo(infos.getInt(1), infos.getString(2));
    		}
			
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public String getNomGrille(int num_grille)
    {
    	ResultSet nom = execReqSelection("SELECT nom_grille FROM tp5_grille WHERE num_grille = '" + num_grille + "'");
    	try
		{
    		if(nom.next())
    		{
    			return nom.getString(1);
    		}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    public static MotsCroisesTP6 extraireGrille(int numGrille) {
        MotsCroisesTP6 motsCroises = null;
        ResultSet reqSelection = execReqSelection("select hauteur, largeur from tp5_grille where num_grille = '" + numGrille + "'");
        try {
            if (reqSelection.next()) motsCroises = new MotsCroisesTP6(reqSelection.getInt(1), reqSelection.getInt(2));
        } catch (Exception e) {
            System.out.println("erreur reqSelection.next() pour la requête - select largeur, hauteur from tp5_grille where num_grille = '" + numGrille + "'");
            e.printStackTrace();
        }
        try {
			System.out.println(reqSelection.getInt(1));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        reqSelection = execReqSelection("select ligne, colonne, horizontal, solution, definition from tp5_grille\n" +
                "inner join tp5_mot t5m on tp5_grille.num_grille = t5m.num_grille where tp5_grille.num_grille = '" + numGrille + "'");
        assert motsCroises != null;
        try {
            while (reqSelection.next()) {
            	
            	char[] sols = reqSelection.getString("solution").toUpperCase().toCharArray();
                int lig = reqSelection.getInt("ligne");
                int col = reqSelection.getInt("colonne");
                
                int maxIndx = sols.length;
                if (reqSelection.getInt("horizontal") == 1) 
                {
                	motsCroises.setDefinition(lig, col, true, reqSelection.getString("definition"));
                	for (int colIndx = 0; colIndx < maxIndx; colIndx++) 
                	{
                		motsCroises.setCaseNoire(lig, col + colIndx, false);
                        motsCroises.setSolution(lig, col + colIndx, sols[colIndx]); // Horizontal
                	}
                }
                else
                {
                	motsCroises.setDefinition(lig, col, false, reqSelection.getString("definition"));
                	for (int ligIndx = 0; ligIndx < maxIndx; ligIndx++) 
                	{
                		motsCroises.setCaseNoire(lig + ligIndx, col, false);
                        motsCroises.setSolution(lig + ligIndx, col, sols[ligIndx]); // Vertical
                	}
                }
                
                for (int i = 0; i < sols.length; i++) {
                    if (reqSelection.getInt("horizontal") == 1) {
                    	
                    } else {
                    	
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("erreur extraireGrille()");
            e.printStackTrace();
        }
        fermerConnexionBd();
        return motsCroises;
    }

    public static void fermerConnexionBd() {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println("Erreur sur fermeture connexion");
        }
    }

    // Fonction pour select des requêtes SQL
    public static ResultSet execReqSelection(String laRequete) {
        new ChargerGrilleAutre();
        ResultSet resultatReq = null;
        try {
            Statement requete = connection.createStatement();
            resultatReq = requete.executeQuery(laRequete);
        } catch (Exception e) {
            System.out.println("Erreur requête : " + laRequete);
        }
        return resultatReq;
    }

    // Fonction pour insérer/update des requêtes SQL
    public static int execReqMaj(String laRequete) {
        new ChargerGrilleAutre();
        int nbMaj = 0;
        try {
            Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            nbMaj = s.executeUpdate(laRequete);
            s.close();
        } catch (Exception er) {
            er.printStackTrace();
            System.out.println("Échec requête : " + laRequete);
        }
        return nbMaj;
    }

}
