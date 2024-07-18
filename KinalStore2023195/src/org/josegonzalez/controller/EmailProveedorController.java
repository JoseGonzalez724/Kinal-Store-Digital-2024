package org.josegonzalez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.josegonzalez.bean.Proveedor;
import org.josegonzalez.bean.EmailProveedor;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class EmailProveedorController implements Initializable{
    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<EmailProveedor>listaTelefonoProveedor;
    public ObservableList<Proveedor>listaProveedor;
    @FXML private TextField txtCodigoEmailProveedor;
    @FXML private TextField txtEmailProveedor;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox cmbCodigoProveedor;
    @FXML private TableView tblEmailProveedores;
    @FXML private TableColumn colCodigoEmailProveedor;
    @FXML private TableColumn colEmailProveedor;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colCodigoProveedor;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoProveedor.setDisable(true);
        cmbCodigoProveedor.setItems(getProveedor());
    }
    
    public void seleccionarElemento(){
        if(tblEmailProveedores.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoEmailProveedor.setText(String.valueOf(((EmailProveedor)tblEmailProveedores.getSelectionModel().getSelectedItem()).getCodigoEmailProveedor()));
            txtEmailProveedor.setText(((EmailProveedor)tblEmailProveedores.getSelectionModel().getSelectedItem()).getEmailProveedor());
            txtDescripcion.setText(((EmailProveedor)tblEmailProveedores.getSelectionModel().getSelectedItem()).getDescripcion());
           cmbCodigoProveedor.getSelectionModel().select(buscarProveedor(((EmailProveedor)tblEmailProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
        }
    }
    
    public void cargarDatos(){
        tblEmailProveedores.setItems(getTelefonoProveedor());
        colCodigoEmailProveedor.setCellValueFactory(new PropertyValueFactory<EmailProveedor, Integer>("codigoEmailProveedor"));
        colEmailProveedor.setCellValueFactory(new PropertyValueFactory<EmailProveedor, String>("emailProveedor"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<EmailProveedor, String>("descripcion"));
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<EmailProveedor, Integer>("codigoProveedor"));
    }
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("         Guardar");
                btnEliminar.setText("         Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                cmbCodigoProveedor.setDisable(false);
                imgNuevo.setImage(new Image("/org/josegonzalez/image/Save.png"));
                imgEliminar.setImage(new Image("/org/josegonzalez/image/Cancel.png"));
                tipoOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("         Nuevo");
                btnEliminar.setText("         Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/josegonzalez/image/saveas_5165.png"));
                imgEliminar.setImage(new Image("/org/josegonzalez/image/Delete.png"));
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void guardar(){
        EmailProveedor registro = new EmailProveedor();
        if (txtEmailProveedor.getText().isEmpty() || txtDescripcion.getText().isEmpty() || cmbCodigoProveedor.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios de llenar");
        } else {
            registro.setEmailProveedor(txtEmailProveedor.getText());
            registro.setDescripcion(txtDescripcion.getText());
            registro.setCodigoProveedor(((Proveedor)cmbCodigoProveedor.getSelectionModel().getSelectedItem()).getCodigoProveedor());
            try{
                 PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmailProveedor(?, ?, ?)}");   
                procedimiento.setString(1, registro.getEmailProveedor());
                procedimiento.setString(2, registro.getDescripcion());
                 procedimiento.setInt(3, registro.getCodigoProveedor());
                 procedimiento.executeQuery();
                 listaTelefonoProveedor.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
    }
    
    public ObservableList<EmailProveedor> getTelefonoProveedor(){
        ArrayList<EmailProveedor> lista = new ArrayList<EmailProveedor>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEmailsProveedores}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new EmailProveedor(resultado.getInt("codigoEmailProveedor"),
                                resultado.getString("emailProveedor"),
                                resultado.getString("descripcion"),
                                resultado.getInt("codigoProveedor")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTelefonoProveedor = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Proveedor> getProveedor(){
        ArrayList<Proveedor> lista = new ArrayList<Proveedor>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarProveedores}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new Proveedor(resultado.getInt("CodigoProveedor"),
                                resultado.getString("NITProveedor"),
                                resultado.getString("nombresProveedor"),
                                resultado.getString("apellidosProveedor"),
                                resultado.getString("direccionProveedor"),
                                resultado.getString("razonSocial"),
                                resultado.getString("contactoPrincipal"),
                                resultado.getString("paginaWeb")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaProveedor = FXCollections.observableArrayList(lista);
    }
    
    public Proveedor buscarProveedor(int codigoProveedor){
        Proveedor proveedor = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarProveedor(?)}");
            procedimiento.setInt(1, codigoProveedor);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                proveedor = new Proveedor(resultado.getInt("CodigoProveedor"),
                                resultado.getString("NITProveedor"),
                                resultado.getString("nombresProveedor"),
                                resultado.getString("apellidosProveedor"),
                                resultado.getString("direccionProveedor"),
                                resultado.getString("razonSocial"),
                                resultado.getString("contactoPrincipal"),
                                resultado.getString("paginaWeb"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return proveedor;
    }
    
    public void eliminar(){
        switch(tipoOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("         Nuevo");
                btnEliminar.setText("         Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/josegonzalez/image/saveas_5165.png"));
                imgEliminar.setImage(new Image("/org/josegonzalez/image/Delete.png"));
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
            default:
                if(tblEmailProveedores.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Email Proveedor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarEmailProveedor(?)}");
                            procedimiento.setInt(1, ((EmailProveedor)tblEmailProveedores.getSelectionModel().getSelectedItem()).getCodigoEmailProveedor());
                            procedimiento.execute();
                            listaTelefonoProveedor.remove(tblEmailProveedores.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else if(respuesta == JOptionPane.NO_OPTION){
                        cargarDatos();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe selecionar un registro valido.");
                }
        }    
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblEmailProveedores.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoProveedor.setDisable(true);
                    btnEditar.setText("         Actualizar");
                    btnReporte.setText("         Cancelar");
                    imgEditar.setImage(new Image("/org/josegonzalez/image/Save.png"));
                    imgReporte.setImage(new Image("/org/josegonzalez/image/Cancel.png"));
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe Selecionar un elemento.");
                }
               break;
            case ACTUALIZAR:
                actualizar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("         Editar");
                btnReporte.setText("         Reporte");
                imgEditar.setImage(new Image("/org/josegonzalez/image/Create.png"));
                imgReporte.setImage(new Image("/org/josegonzalez/image/Graph Report.png"));
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarEmailProveedor(?, ?, ?)}");
            EmailProveedor registro = (EmailProveedor)tblEmailProveedores.getSelectionModel().getSelectedItem();
            registro.setEmailProveedor(txtEmailProveedor.getText());
            registro.setDescripcion(txtDescripcion.getText());
            registro.setCodigoProveedor(((Proveedor)cmbCodigoProveedor.getSelectionModel().getSelectedItem()).getCodigoProveedor());
            procedimiento.setInt(1, registro.getCodigoEmailProveedor());
            procedimiento.setString(2, registro.getEmailProveedor());
            procedimiento.setString(3, registro.getDescripcion());
//            procedimiento.setInt(4, registro.getCodigoProveedor());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporte(){
        switch(tipoOperacion){
            case NINGUNO:
                //imprimirReporte();
                break;
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("         Editar");
                btnReporte.setText("         Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("/org/josegonzalez/image/Create.png"));
                imgReporte.setImage(new Image("/org/josegonzalez/image/Graph Report.png"));
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void desactivarControles(){
        txtCodigoEmailProveedor.setEditable(false);
        txtEmailProveedor.setEditable(false);
        txtDescripcion.setEditable(false);
        cmbCodigoProveedor.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoEmailProveedor.setEditable(false);
        txtEmailProveedor.setEditable(true);
        txtDescripcion.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoEmailProveedor.clear();
        txtDescripcion.clear();
        txtEmailProveedor.clear();
        txtDescripcion.clear();
        cmbCodigoProveedor.valueProperty().set(null);
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaProveedor(){
        escenarioPrincipal.ventanaProveedor();
    }
}
