package fr.mongault.iradukunda;


import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import fr.mongault.iradukunda.model.ChargerGrilleAutre;
import fr.mongault.iradukunda.model.MotsCroises;

class ChargerGrilleTest {

	@Test
	void test() throws SQLException {
		String solution = "ARCHERDOUANEJUIN*BONT*VOID*GINN*RENDTIFOSI";
		ChargerGrilleAutre chargerGrille = new ChargerGrilleAutre();
		MotsCroises mots = chargerGrille.extraireGrille(10);
		String reponse = "";
		for (int i = 1; i < mots.getHauteur()+1; i++) {
			for (int j = 1; j < mots.getLargeur()+1; j++) {
				if(!mots.estCaseNoire(i, j))
				{
					char c = mots.getSolution(i, j);
					reponse += String.valueOf(c);
				}
			}
		}
		System.out.println(solution);
		System.out.println(reponse);
		assertEquals(solution,reponse);
	}

}
