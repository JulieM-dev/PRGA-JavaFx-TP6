package fr.mongault.iradukunda.controller;

import fr.mongault.iradukunda.MainApp;

public interface Controller {
   void setMainApp(MainApp mainApp);
   
   void init();

   String getNOMMETHODE();
}