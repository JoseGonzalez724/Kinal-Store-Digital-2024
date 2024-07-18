package org.josegonzalez.controller;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;
import org.josegonzalez.bean.Login;
import org.josegonzalez.bean.Usuario;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class LoginController implements Initializable {
    private Principal escenarioPrincipal;
    private ObservableList<Usuario> listarUsuario;

    @FXML private JFXTextField txtUsuario;
    @FXML private JFXPasswordField txtPasword;
    @FXML private FontAwesomeIconView iconPassword;
    @FXML private MaterialDesignIconView iconUsuario;
    
    private static final FontAwesomeIcon WARNING_ICON = FontAwesomeIcon.EXCLAMATION_TRIANGLE;
    private static final String MESSAGE = "Campos obligatorios";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toJFXTextField(txtUsuario);
        toJFXPasswordField(txtPasword);
    }

    public static void toJFXTextField(JFXTextField txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator(MESSAGE);
        validator.setIcon(new FontAwesomeIconView(WARNING_ICON));

        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }

    public static void toJFXPasswordField(JFXPasswordField txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator(MESSAGE);
        validator.setIcon(new FontAwesomeIconView(WARNING_ICON));

        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }
    
    public ObservableList<Usuario> getUsuario(){
        ArrayList<Usuario> lista = new ArrayList<Usuario>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarUsuarios()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Usuario(resultado.getInt("codigoUsuario"),
                        resultado.getString("nombreUsuario"),
                        resultado.getString("apellidoUsuario"),
                        resultado.getString("usuarologin"),
                        resultado.getString("contrasena")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listarUsuario = FXCollections.observableArrayList(lista);
    }
    
    @FXML
    public void sesion(){
        Login login = new Login();
        int x = 0;
        boolean bandera = false;
        login.setUsuarioMaster(txtUsuario.getText());
        login.setPasswordLogin(txtPasword.getText());
        while(x < getUsuario().size()){
            String user = getUsuario().get(x).getUsuarologin();
            String pass = getUsuario().get(x).getContrasena();
            if(user.equals(login.getUsuarioMaster()) && pass.equals(login.getPasswordLogin())){
                Image image = new Image("org/josegonzalez/image/Check Mark.png");
                Notifications msj = Notifications.create();
                msj.graphic(new ImageView(image));
                msj.title("Secion iniciada");
                msj.text("Bienvenido: "+ getUsuario().get(x).getNombreUsuario() + " " + getUsuario().get(x).getApellidoUsuario());
                msj.hideAfter(Duration.seconds(6));
                msj.position(Pos.BASELINE_RIGHT);
                msj.show();
                menuPrincipal();
                x = getUsuario().size();
                bandera = true;
                
            }
            x++;
        }
        if(bandera == false)
            error();
    }
    
    public void error(){
        Image image = new Image("org/josegonzalez/image/Close.png");
        Notifications msj = Notifications.create();
        msj.graphic(new ImageView(image));
        msj.title("Error");
        msj.text("Usuario o contraseña incorrectos");
        msj.hideAfter(Duration.seconds(5));
        msj.position(Pos.BASELINE_RIGHT);
        msj.show();
        shake(txtUsuario);
        shake(txtPasword);
        shake(iconUsuario);
        shake(iconPassword);
        txtUsuario.setStyle("-fx-prompt-text-fill: red; -fx-text-inner-color: red; -fx-text-fill: black");
        txtPasword.setStyle("-fx-prompt-text-fill: red; -fx-text-inner-color: red; -fx-text-fill: black");
        iconUsuario.setStyle("-fx-fill: red;");
        iconPassword.setStyle("-fx-fill: red;");
    }
    
    private void shake(Node node) {
        new Shake(node).play();
    }
    
    public void abrirRegistro(){
        usuario();
    }
    
    public void login(){
        escenarioPrincipal.login();
    }
    
    public void usuario(){
        escenarioPrincipal.usuario();
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
}
