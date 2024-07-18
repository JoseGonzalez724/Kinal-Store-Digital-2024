package org.josegonzalez.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.josegonzalez.bean.Usuario;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class UsuarioController implements Initializable{
    
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    @FXML private JFXTextField txtCodigoUsuario;
    @FXML private JFXTextField txtNombres;
    @FXML private JFXTextField txtApellidos;
    @FXML private JFXTextField txtUsuario;
    @FXML private JFXPasswordField txtPasword;
    @FXML private JFXButton btnNuevo;
    @FXML private JFXButton btnEliminar;
    @FXML private MaterialDesignIconView imgNuevo;
    @FXML private MaterialDesignIconView imgEliminar;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("         Guardar");
                btnEliminar.setText("         Cancelar");
//                imgNuevo.setImage(new Image("/org/josegonzalez/image/Save.png"));
//                imgEliminar.setImage(new Image("/org/josegonzalez/image/Cancel.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("         Nuevo");
                btnEliminar.setText("         Eliminar");
//                imgNuevo.setImage(new Image("/org/josegonzalez/image/saveas_5165.png"));
//                imgEliminar.setImage(new Image("/org/josegonzalez/image/Delete.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void cancelar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("         Nuevo");
                btnEliminar.setText("         Eliminar");
//                imgNuevo.setImage(new Image("/org/josegonzalez/image/Save.png"));
//                imgEliminar.setImage(new Image("/org/josegonzalez/image/Cancel.png"));
                tipoDeOperacion = operaciones.GUARDAR;
        }
    }
    
    public void guardar(){
        Usuario registro = new Usuario();
        registro.setNombreUsuario(txtNombres.getText());
        registro.setApellidoUsuario(txtApellidos.getText());
        registro.setUsuarologin(txtUsuario.getText());
        registro.setContrasena(txtPasword.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarUsuario(?, ?, ?, ?)}");
            procedimiento.setString(1, registro.getNombreUsuario());
            procedimiento.setString(2, registro.getApellidoUsuario());
            procedimiento.setString(3, registro.getUsuarologin());
            procedimiento.setString(4, registro.getContrasena());
            procedimiento.executeQuery();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void desactivarControles(){
        txtCodigoUsuario.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtUsuario.setEditable(false);
        txtPasword.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoUsuario.setEditable(false);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtUsuario.setEditable(true);
        txtPasword.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoUsuario.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtUsuario.clear();
        txtPasword.clear();
    }
    
    public void usuario(){
        escenarioPrincipal.usuario();
    }
    
    public void login(){
        escenarioPrincipal.login();
    }
    
    public void abrirLogin(){
        login();
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
}
