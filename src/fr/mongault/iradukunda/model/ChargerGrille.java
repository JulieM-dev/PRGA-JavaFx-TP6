/**
 * JavaFX TP6 PRGA
 * 
 * @author JulienMongault
 */
package fr.mongault.iradukunda.model;

import java.sql.* ;
import java.util.* ;
public class ChargerGrille
{
	private Connection connexion ;
	public ChargerGrille()
	{
		try { connexion = connecterBD() ; }
		catch (SQLException e) { e.printStackTrace(); }
	}
	
	public static Connection connecterBD() throws SQLException
	{
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/base_bousse?autoReconnect=true&useSSL=false", "root", "");
	}
	
	// Retourne la liste des grilles disponibles dans la B.D.
	// Chaque grille est décrite par la concaténation des valeurs
	// respectives des colonnes nom_grille, hauteur et largeur.
	// L’élément de liste ainsi obtenu est indexé par le numéro de
	// la grille (colonne num_grille).
	// Ainsi "Français débutants (7x6)" devrait être associé à la clé 10
	public List<GrilleInfo> grillesDisponibles() {
		// GrilleInfo contient l'identifiant d'une grille avec son nom
		List<GrilleInfo> grilles = new ArrayList<GrilleInfo>();
        ResultSet reqSelection = execReqSelection("select num_grille, nom_grille from tp5_grille");
        try {
            while (reqSelection.next()) {
                grilles.add(new GrilleInfo(reqSelection.getInt(1), reqSelection.getString(2)));
            }
        } catch (Exception e) {
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
    
    // Retourne le nom d'une grille en fonction de son ID
    public String getNomGrille(int numGrille)
    {
    	ResultSet nom = execReqSelection(String.format("SELECT nom_grille FROM tp5_grille WHERE num_grille = %s", numGrille));
    	try
		{
    		// pas de boucle car une seule valeur attendu
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

    // Retourne un objet MotsCroisesTP6 récupéré depuis la BDD
	public MotsCroisesTP6 extraireGrille(int numGrille)
	{
		MotsCroisesTP6 motsCroises = null;
		// Requetes 
		String queryGrille = String.format("SELECT hauteur, largeur FROM tp5_grille WHERE num_grille = %s", numGrille);
		String queryMots = String.format("SELECT ligne, colonne, horizontal, solution, definition FROM tp5_mot WHERE num_grille = %s", numGrille);

        try {
        	// On créé la base de la grille
        	ResultSet result = execReqSelection(queryGrille);
    		if (result.next()) motsCroises = new MotsCroisesTP6(result.getInt("hauteur"), result.getInt("largeur"));
            
    		// On rempli la grille 
            result = execReqSelection(queryMots);
            while (result.next()) 
            {
         	
            	char[] solArr = result.getString("solution").toUpperCase().toCharArray();
                int lig = result.getInt("ligne");
                int col = result.getInt("colonne");
                
                int maxIndx = solArr.length;
                if (result.getInt("horizontal") == 1) 
                {
                	// On rempli selon l'axe horizontal
                	motsCroises.setDefinition(lig, col, true, result.getString("definition"));
                	for (int colIndx = 0; colIndx < maxIndx; colIndx++) 
                	{
                		motsCroises.setCaseNoire(lig, col + colIndx, false);
                        motsCroises.setSolution(lig, col + colIndx, solArr[colIndx]); // Horizontal
                	}
                }
                else
                {
                	// On rempli selon l'axe vertical
                	motsCroises.setDefinition(lig, col, false, result.getString("definition"));
                	for (int ligIndx = 0; ligIndx < maxIndx; ligIndx++) 
                	{
                		motsCroises.setCaseNoire(lig + ligIndx, col, false);
                        motsCroises.setSolution(lig + ligIndx, col, solArr[ligIndx]); // Vertical
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fermerConnexionBd();
        return motsCroises;	
	}
	
	public void fermerConnexionBd() {
        try {
            connexion.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    // Fonction pour select des requêtes SQL
    public ResultSet execReqSelection(String laRequete) {
        ResultSet resultatReq = null;
        try {
            Statement requete = connexion.createStatement();
            resultatReq = requete.executeQuery(laRequete);
        } catch (Exception e) {
            System.out.println("Erreur requête : " + laRequete);
        }
        return resultatReq;
    }

	
}