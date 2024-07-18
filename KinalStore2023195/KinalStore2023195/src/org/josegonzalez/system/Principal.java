/*
* José Francisco González Ordoñez
* IN5AV
* fecha de creación: 
*       30/01/2024
* fechas de modificación:
*       
*/
package org.josegonzalez.system;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/josegonzalez/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal) throws IOException{
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("KinalStore 2024");
        escenarioPrincipal.getIcons().add(new Image("/org/josegonzalez/image/Logo.png"));
        Parent root = FXMLLoader.load(getClass().getResource("/org/josegonzalez/view/MenuPrincipalView.fxml"));
        
        Scene escena = new Scene(root);
        
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
