package org.josegonzalez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.josegonzalez.bean.Proveedor;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.report.GenerarReporte;
import org.josegonzalez.system.Principal;

public class ProveedorController implements Initializable{

    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<Proveedor>listaProveedor;
    @FXML private TextField txtCodigoProveedor;
    @FXML private TextField txtNITProveedor;
    @FXML private TextField txtNombresProveedor;
    @FXML private TextField txtApellidosProveedor;
    @FXML private TextField txtDireccionProveedor;
    @FXML private TextField txtRazonSocialProveedor;
    @FXML private TextField txtContactoPrincipalProveedor;
    @FXML private TextField txtPaginaWebProveedor;
    @FXML private TableView tblProveedores;
    @FXML private TableColumn colCodigoProveedor;
    @FXML private TableColumn colNITProveedor;
    @FXML private TableColumn colNombresProveedor;
    @FXML private TableColumn colApellidosProveedor;
    @FXML private TableColumn colDireccionProveedor;
    @FXML private TableColumn colRazonSocialProveedor;
    @FXML private TableColumn colContactoPrincipalProveedor;
    @FXML private TableColumn colPaginaWebProveedor;
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
    }
    
    public void seleccionarElemento(){
        if(tblProveedores.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoProveedor.setText(String.valueOf(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
            txtNITProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getNITProveedor());
            txtNombresProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getNombresProveedor());
            txtApellidosProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getApellidosProveedor());
            txtDireccionProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getDireccionProveedor());
            txtRazonSocialProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getRazonSocial());
            txtContactoPrincipalProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getContactoPrincipal());
            txtPaginaWebProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getPaginaWeb());
        }
    }
    
    public void cargarDatos(){
        tblProveedores.setItems(getProveedor());
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, Integer>("CodigoProveedor"));
        colNITProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("NITProveedor"));
        colNombresProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("nombresProveedor"));
        colApellidosProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("apellidosProveedor"));
        colDireccionProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("direccionProveedor"));
        colRazonSocialProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("razonSocial"));
        colContactoPrincipalProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("contactoPrincipal"));
        colPaginaWebProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("paginaWeb"));
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
        Proveedor registro = new Proveedor();
        registro.setNITProveedor(txtNITProveedor.getText());
        registro.setNombresProveedor(txtNombresProveedor.getText());
        registro.setApellidosProveedor(txtApellidosProveedor.getText());
        registro.setDireccionProveedor(txtDireccionProveedor.getText());
        registro.setRazonSocial(txtRazonSocialProveedor.getText());
        registro.setContactoPrincipal(txtContactoPrincipalProveedor.getText());
        registro.setPaginaWeb(txtPaginaWebProveedor.getText());
        if (registro.getNITProveedor().isEmpty() ||
            registro.getNombresProveedor().isEmpty() ||
            registro.getApellidosProveedor().isEmpty() ||
            registro.getDireccionProveedor().isEmpty() ||
            registro.getRazonSocial().isEmpty() ||
            registro.getContactoPrincipal().isEmpty() ||
            registro.getPaginaWeb().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios de llenar");
        } else {
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarProveedor(?, ?, ?, ?, ?, ?, ?)}");
                procedimiento.setString(1, registro.getNITProveedor());
                procedimiento.setString(2, registro.getNombresProveedor());
                procedimiento.setString(3, registro.getApellidosProveedor());
                procedimiento.setString(4, registro.getDireccionProveedor());
                procedimiento.setString(5, registro.getRazonSocial());
                procedimiento.setString(6, registro.getContactoPrincipal());
                procedimiento.setString(7, registro.getPaginaWeb());
                procedimiento.executeQuery();
                listaProveedor.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
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
                if(tblProveedores.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Proveedor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarProveedor(?)}");
//                            procedimiento.setInt(1, ((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente()); parte complicada del seleccionador de item
                            procedimiento.setInt(1, ((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor());
                            procedimiento.execute();
                            listaProveedor.remove(tblProveedores.getSelectionModel().getSelectedIndex());
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
                if(tblProveedores.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarProveedor(?, ?, ?, ?, ?, ?, ?, ?)}");
            Proveedor registro = (Proveedor)tblProveedores.getSelectionModel().getSelectedItem();
            registro.setNITProveedor(txtNITProveedor.getText());
            registro.setNombresProveedor(txtNombresProveedor.getText());
            registro.setApellidosProveedor(txtApellidosProveedor.getText());
            registro.setDireccionProveedor(txtDireccionProveedor.getText());
            registro.setRazonSocial(txtRazonSocialProveedor.getText());
            registro.setContactoPrincipal(txtContactoPrincipalProveedor.getText());
            registro.setPaginaWeb(txtPaginaWebProveedor.getText());
            procedimiento.setInt(1, registro.getCodigoProveedor());
            procedimiento.setString(2, registro.getNITProveedor());
            procedimiento.setString(3, registro.getNombresProveedor());
            procedimiento.setString(4, registro.getApellidosProveedor());
            procedimiento.setString(5, registro.getDireccionProveedor());
            procedimiento.setString(6, registro.getRazonSocial());
            procedimiento.setString(7, registro.getContactoPrincipal());
            procedimiento.setString(8, registro.getPaginaWeb());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporte(){
        switch(tipoOperacion){
            case NINGUNO:
                imprimirReporte();
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
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoProveedor", null);
        String imagePath = getClass().getClassLoader().getResource("org/josegonzalez/image/Membrete.png").getPath();
        parametros.put("IMAGE_PATH", imagePath);
        GenerarReporte.mostrarReporte("ReporteProveedor.jasper", "Reporte de proveedor", parametros);
    }
    
    public void desactivarControles(){
        txtCodigoProveedor.setEditable(false);
        txtNITProveedor.setEditable(false);
        txtNombresProveedor.setEditable(false);
        txtApellidosProveedor.setEditable(false);
        txtDireccionProveedor.setEditable(false);
        txtRazonSocialProveedor.setEditable(false);
        txtContactoPrincipalProveedor.setEditable(false);
        txtPaginaWebProveedor.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoProveedor.setEditable(false);
        txtNITProveedor.setEditable(true);
        txtNombresProveedor.setEditable(true);
        txtApellidosProveedor.setEditable(true);
        txtDireccionProveedor.setEditable(true);
        txtRazonSocialProveedor.setEditable(true);
        txtContactoPrincipalProveedor.setEditable(true);
        txtPaginaWebProveedor.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoProveedor.clear();
        txtNITProveedor.clear();
        txtNombresProveedor.clear();
        txtApellidosProveedor.clear();
        txtDireccionProveedor.clear();
        txtRazonSocialProveedor.clear();
        txtContactoPrincipalProveedor.clear();
        txtPaginaWebProveedor.clear();
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaTelefonoProveedor(){
        escenarioPrincipal.ventanaTelefonoProveedor();
    }
    
    public void ventanaEmailProveedor(){
        escenarioPrincipal.ventanaEmailProveedor();
    }
}
