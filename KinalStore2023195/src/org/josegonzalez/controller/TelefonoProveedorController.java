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
import org.josegonzalez.bean.TelefonoProveedor;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class TelefonoProveedorController implements Initializable{

    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<TelefonoProveedor>listaTelefonoProveedor;
    public ObservableList<Proveedor>listaProveedor;
    @FXML private TextField txtCodigoTelefonoProveedor;
    @FXML private TextField txtNumeroPricipal;
    @FXML private TextField txtNumeroSecundario;
    @FXML private TextField txtObservaciones;
    @FXML private ComboBox cmbCodigoProveedor;
    @FXML private TableView tblTelefonoProveedores;
    @FXML private TableColumn colCodigoTelefonoProveedor;
    @FXML private TableColumn colNumeroPrincipal;
    @FXML private TableColumn colNumeroSecundario;
    @FXML private TableColumn colObservaciones;
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
        if(tblTelefonoProveedores.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoTelefonoProveedor.setText(String.valueOf(((TelefonoProveedor)tblTelefonoProveedores.getSelectionModel().getSelectedItem()).getCodigoTelefonoProveedor()));
            txtNumeroPricipal.setText(((TelefonoProveedor)tblTelefonoProveedores.getSelectionModel().getSelectedItem()).getNumeroPrincipal());
            txtNumeroSecundario.setText(((TelefonoProveedor)tblTelefonoProveedores.getSelectionModel().getSelectedItem()).getNumeroSecundario());
            txtObservaciones.setText(((TelefonoProveedor)tblTelefonoProveedores.getSelectionModel().getSelectedItem()).getObservaciones());
            cmbCodigoProveedor.getSelectionModel().select(buscarProveedor(((TelefonoProveedor)tblTelefonoProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
        }
    }
    
    public void cargarDatos(){
        tblTelefonoProveedores.setItems(getTelefonoProveedor());
        colCodigoTelefonoProveedor.setCellValueFactory(new PropertyValueFactory<TelefonoProveedor, Integer>("codigoTelefonoProveedor"));
        colNumeroPrincipal.setCellValueFactory(new PropertyValueFactory<TelefonoProveedor, String>("numeroPrincipal"));
        colNumeroSecundario.setCellValueFactory(new PropertyValueFactory<TelefonoProveedor, String>("numeroSecundario"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<TelefonoProveedor, String>("observaciones"));
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<TelefonoProveedor, Integer>("codigoProveedor"));
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
        TelefonoProveedor registro = new TelefonoProveedor();
        if (txtNumeroPricipal.getText().isEmpty() || cmbCodigoProveedor.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Algunos de los campos son obligatorios de llenar");
        } else {
            registro.setNumeroPrincipal(txtNumeroPricipal.getText());
            registro.setNumeroSecundario(txtNumeroSecundario.getText());
            registro.setObservaciones(txtObservaciones.getText());
            registro.setCodigoProveedor(((Proveedor)cmbCodigoProveedor.getSelectionModel().getSelectedItem()).getCodigoProveedor());
            try{
                 PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarTelefonoProveedor(?, ?, ?, ?)}");
                 procedimiento.setString(1, registro.getNumeroPrincipal());
                 procedimiento.setString(2, registro.getNumeroSecundario());
                 procedimiento.setString(3, registro.getObservaciones());
                 procedimiento.setInt(4, registro.getCodigoProveedor());
                 procedimiento.executeQuery();
                 listaTelefonoProveedor.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
    }
    
    public ObservableList<TelefonoProveedor> getTelefonoProveedor(){
        ArrayList<TelefonoProveedor> lista = new ArrayList<TelefonoProveedor>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarTelefonoProveedores}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new TelefonoProveedor(resultado.getInt("codigoTelefonoProveedor"),
                                resultado.getString("numeroPrincipal"),
                                resultado.getString("numeroSecundario"),
                                resultado.getString("observaciones"),
                                resultado.getInt("codigoProveedor")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTelefonoProveedor = FXCollections.observableArrayList(lista);
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
                if(tblTelefonoProveedores.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Telefono Proveedor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarTelefonoProveedor(?)}");
                            procedimiento.setInt(1, ((TelefonoProveedor)tblTelefonoProveedores.getSelectionModel().getSelectedItem()).getCodigoTelefonoProveedor());
                            procedimiento.execute();
                            listaTelefonoProveedor.remove(tblTelefonoProveedores.getSelectionModel().getSelectedIndex());
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
                if(tblTelefonoProveedores.getSelectionModel().getSelectedItem() != null){
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
                desactivarControles();
                limpiarControles();
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarTelefonoProveedor(?, ?, ?, ?, ?)}");
            TelefonoProveedor registro = (TelefonoProveedor)tblTelefonoProveedores.getSelectionModel().getSelectedItem();
            registro.setNumeroPrincipal(txtNumeroPricipal.getText());
            registro.setNumeroSecundario(txtNumeroSecundario.getText());
            registro.setObservaciones(txtObservaciones.getText());
            registro.setCodigoProveedor(((Proveedor)cmbCodigoProveedor.getSelectionModel().getSelectedItem()).getCodigoProveedor());
            procedimiento.setInt(1, registro.getCodigoTelefonoProveedor());
            procedimiento.setString(2, registro.getNumeroPrincipal());
            procedimiento.setString(3, registro.getNumeroSecundario());
            procedimiento.setString(4, registro.getObservaciones());
            procedimiento.setInt(5, registro.getCodigoProveedor());
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
        txtCodigoTelefonoProveedor.setEditable(false);
        txtNumeroPricipal.setEditable(false);
        txtNumeroSecundario.setEditable(false);
        txtObservaciones.setEditable(false);
        cmbCodigoProveedor.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoTelefonoProveedor.setEditable(false);
        txtNumeroPricipal.setEditable(true);
        txtNumeroSecundario.setEditable(true);
        txtObservaciones.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoTelefonoProveedor.clear();
        txtNumeroPricipal.clear();
        txtNumeroSecundario.clear();
        txtObservaciones.clear();
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
