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
        ResultSet reqSelection = execReqSelection("select num_grille, nom_grille from tp5_grille");
        try {
            while (reqSelection.next()) {
                map.put(reqSelection.getInt(1), reqSelection.getString(2));
            }
        } catch (Exception e) {
            System.out.println("erreur reqSelection.next() pour la requête - select num_grille, nom_grille from tp5_grille");
            e.printStackTrace();
        }
        fermerConnexionBd();
        return map;
	}
	public MotsCroisesTP6 extraireGrille(int numGrille)
	{
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
            	
            	char[] sols = reqSelection.getString("solution").toCharArray();
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
	
	public void fermerConnexionBd() {
        try {
            connexion.close();
        } catch (Exception e) {
            System.out.println("Erreur sur fermeture connexion");
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

    // Fonction pour insérer/update des requêtes SQL
    public int execReqMaj(String laRequete) {
        new ChargerGrilleAutre();
        int nbMaj = 0;
        try {
            Statement s = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            nbMaj = s.executeUpdate(laRequete);
            s.close();
        } catch (Exception er) {
            er.printStackTrace();
            System.out.println("Échec requête : " + laRequete);
        }
        return nbMaj;
    }
	
}