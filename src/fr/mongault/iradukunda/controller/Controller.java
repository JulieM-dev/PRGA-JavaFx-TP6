package fr.mongault.iradukunda.controller;

import fr.mongault.iradukunda.MainTP6;

public interface Controller {
   void setMainApp(MainTP6 mainApp);
   
   void initialize();

   String getNOMMETHODE();
}