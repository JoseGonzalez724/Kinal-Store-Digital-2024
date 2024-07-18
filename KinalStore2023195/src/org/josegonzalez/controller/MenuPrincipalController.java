package org.josegonzalez.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.josegonzalez.system.Principal;

public class MenuPrincipalController implements Initializable{
    
    private Principal escenarioPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }

    public void ventanaProveedor(){
        escenarioPrincipal.ventanaProveedor();
    }
    
    public void ventanaCliente(){
        escenarioPrincipal.ventanaCliente();
    }
    
    public void ventanaProgramador(){
        escenarioPrincipal.ventanaProgramador();
    }
    
    public void ventanaTipoProducto(){
        escenarioPrincipal.ventanaTipoProducto();
    }
    
    public void ventanaCompra(){
        escenarioPrincipal.ventanaCompra();
    }
    
    public void ventanaCargoEmpleado(){
        escenarioPrincipal.ventanaCargoEmpleado();
    }
    
    public void ventanaFactura(){
        escenarioPrincipal.ventanaFactura();
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
}
