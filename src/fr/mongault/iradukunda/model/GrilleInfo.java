/**
 * JavaFX TP6 PRGA
 * 
 * @author JulienMongault
 */
package fr.mongault.iradukunda.model;

public class GrilleInfo
{
	private String nom;
	private int num;
	
	public GrilleInfo(int num, String nom)
	{
		setNum(num);
		setNom(nom);
	}
	
	public int getNum()
	{
		return num;
	}
	
	private void setNum(int num)
	{
		this.num = num;
	}

	public String getNom()
	{
		return nom;
	}

	private void setNom(String nom)
	{
		this.nom = nom;
	}
	
	@Override
	public String toString() {
	    return getNom();
	}

	
}
