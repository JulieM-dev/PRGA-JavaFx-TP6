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
		Connection connect ;
		connect = DriverManager.getConnection("xxx","user_12345678","yyy");
		return connect ;
	}
	// Retourne la liste des grilles disponibles dans la B.D.
	// Chaque grille est décrite par la concaténation des valeurs
	// respectives des colonnes nom_grille, hauteur et largeur.
	// L’élément de liste ainsi obtenu est indexé par le numéro de
	// la grille (colonne num_grille).
	// Ainsi "Français débutants (7x6)" devrait être associé à la clé 10
	public Map<Integer, String> grillesDisponibles()
	{
		Map<Integer, String> map = new HashMap<>();
        ResultSet result = execReqSelection("select num_grille, nom_grille from tp5_grille");
        try {
            while (result.next()) {
                map.put(result.getInt(1), result.getString(2));
            }
        } catch (Exception e) {
            System.out.println("erreur result.next() pour la requête - select num_grille, nom_grille from tp5_grille");
            e.printStackTrace();
        }
        fermerConnexionBd();
        return map;
	}
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
         	
            	char[] solArr = result.getString("solution").toCharArray();
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
        new ChargerGrilleAutre();
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