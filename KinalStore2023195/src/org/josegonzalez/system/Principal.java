/*
* José Francisco González Ordoñez
* IN5AV
* fecha de creación: 
*       30/01/2024
* fechas de modificación:
*       todo el mes de mayo
        03/06/2024
        04/06/2024
        05/06/2024
        10/06/2024
        11/06/2024
        12/06/2024
*/
package org.josegonzalez.system;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.josegonzalez.controller.CargoEmpleadoController;
import org.josegonzalez.controller.ClienteController;
import org.josegonzalez.controller.CompraController;
import org.josegonzalez.controller.DetalleCompraController;
import org.josegonzalez.controller.DetalleFacturaController;
import org.josegonzalez.controller.EmailProveedorController;
import org.josegonzalez.controller.EmpleadoController;
import org.josegonzalez.controller.FacturaController;
import org.josegonzalez.controller.LoginController;
import org.josegonzalez.controller.MenuPrincipalController;
import org.josegonzalez.controller.ProductoController;
import org.josegonzalez.controller.ProgramadorController;
import org.josegonzalez.controller.ProveedorController;
import org.josegonzalez.controller.UsuarioController;
import org.josegonzalez.controller.TelefonoProveedorController;
import org.josegonzalez.controller.TipoProductoController;


public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/josegonzalez/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal) throws IOException{
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("KinalStore 2024");
        escenarioPrincipal.getIcons().add(new Image("/org/josegonzalez/image/Logo.png"));
        /*Parent root = FXMLLoader.load(getClass().getResource("/org/josegonzalez/view/MenuPrincipalView.fxml"));
        
        Scene escena = new Scene(root);
        
        escenarioPrincipal.setScene(escena);*/
        login();
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public void login(){
        try{
            LoginController vistaLogin= (LoginController)cambiarEscena("LoginView.fxml", 1000, 600);
            vistaLogin.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void usuario(){
        try{
            UsuarioController vistaUsuario= (UsuarioController)cambiarEscena("UsuarioView.fxml", 1000, 600);
            vistaUsuario.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController vistaMenuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 400, 400);
            vistaMenuPrincipal.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void ventanaProveedor(){
        try{
            ProveedorController vistaProveedor = (ProveedorController)cambiarEscena("ProveedorView.fxml", 1100, 650);
            vistaProveedor.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void ventanaCliente(){
        try{
            ClienteController vistaCliente = (ClienteController)cambiarEscena("ClienteView.fxml", 1100, 650);
            vistaCliente.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCargoEmpleado(){
        try{
            CargoEmpleadoController vistaCargo = (CargoEmpleadoController)cambiarEscena("CargoEmpleadoView.fxml", 1100, 650);
            vistaCargo.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpleado(){
        try{
            EmpleadoController vistaEmpleado = (EmpleadoController)cambiarEscena("EmpleadoVIew.fxml", 1100, 650);
            vistaEmpleado.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCompra(){
        try{
            CompraController vistaCompra = (CompraController)cambiarEscena("CompraView.fxml", 1100, 650);
            vistaCompra.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaDetalleCompra(){
        try{
            DetalleCompraController vistaCompra = (DetalleCompraController)cambiarEscena("DetalleCompraView.fxml", 1100, 650);
            vistaCompra.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController vistaProgra = (ProgramadorController)cambiarEscena("ProgramadorView.fxml", 950, 500);
            vistaProgra.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProducto(){
        try{
            ProductoController vistaProducto = (ProductoController)cambiarEscena("ProductoView.fxml", 1300, 700);
            vistaProducto.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoProducto(){
        try{
            TipoProductoController vistaTProducto = (TipoProductoController)cambiarEscena("TipoProductoView.fxml", 1100, 650);
            vistaTProducto.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTelefonoProveedor(){
        try{
            TelefonoProveedorController vistaTelefonoProveedor = (TelefonoProveedorController)cambiarEscena("TelefonoProveedorView.fxml", 1100, 650);
            vistaTelefonoProveedor.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEmailProveedor(){
        try{
            EmailProveedorController vistaEmailProveedor = (EmailProveedorController)cambiarEscena("EmailProveedorView.fxml", 1100, 650);
            vistaEmailProveedor.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaFactura(){
        try{
            FacturaController vistaFactura = (FacturaController)cambiarEscena("FacturaVIew.fxml", 1100, 650);
            vistaFactura.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaDetalleFactura(){
        try{
            DetalleFacturaController vistaDFactura = (DetalleFacturaController)cambiarEscena("DetalleFacturaView.fxml", 1100, 650);
            vistaDFactura.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto)throws IOException{
        Initializable result = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA + fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA + fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        result = (Initializable)cargadorFXML.getController();
        return result;
    }
    
}
