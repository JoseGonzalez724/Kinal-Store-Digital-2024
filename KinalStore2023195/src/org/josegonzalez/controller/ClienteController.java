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
import org.josegonzalez.bean.Cliente;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.report.GenerarReporte;
import org.josegonzalez.system.Principal;

public class ClienteController implements Initializable{

    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO, ELIMINAR, ACTUALIZAR, GUARDAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    public ObservableList<Cliente> listarCliente;
    @FXML private TextField txtCodigoCliente;
    @FXML private TextField txtNITCliente;
    @FXML private TextField txtNombresCliente;
    @FXML private TextField txtApellidosCliente;
    @FXML private TextField txtDireccionCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtCorreoCliente;
    @FXML private TableView tblClientes;
    @FXML private TableColumn colCodigoCliente;
    @FXML private TableColumn colNITCliente;
    @FXML private TableColumn colNombresCliente;
    @FXML private TableColumn colApellidosCliente;
    @FXML private TableColumn colDireccionCliente;
    @FXML private TableColumn colTelefonoCliente;
    @FXML private TableColumn colCorreoCliente;
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
    /* tarea:
        tener todo hasta aca
    */
    public void seleccionarElemento(){
        if(tblClientes.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoCliente.setText(String.valueOf(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente()));
            txtNITCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getNITCliente());
            txtNombresCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getNombresCliente());
            txtApellidosCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getApellidosCliente());
            txtDireccionCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getDireccionCliente());
            txtTelefonoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getTelefonoCliente());
            txtCorreoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCorreoCliente());
        }
    }
    
    public void cargarDatos(){
        tblClientes.setItems(getCliente());
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<Cliente, Integer>("codigoCliente"));
        colNITCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("NITCliente"));
        colNombresCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("nombresCliente"));
        colApellidosCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("apellidosCliente"));
        colDireccionCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("direccionCliente"));
        colTelefonoCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("telefonoCliente"));
        colCorreoCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("correoCliente"));
    }
    
    public ObservableList<Cliente> getCliente(){
        ArrayList<Cliente> lista = new ArrayList<Cliente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarClientes}");// las {} son una instruccion de sql
            ResultSet resultado = procedimiento.executeQuery(); // cuando hay datos que regresan a Java usar executeQuery, y cuando no regresan datos se usa solo execute
            while(resultado.next()){
                lista.add(new Cliente(resultado.getInt("codigoCliente"),
                                    resultado.getString("NITCliente"),
                                    resultado.getString("nombresCliente"),
                                    resultado.getString("apellidosCliente"),
                                    resultado.getString("direccionCliente"),
                                    resultado.getString("telefonoCliente"),
                                    resultado.getString("correoCliente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listarCliente = FXCollections.observableArrayList(lista);
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("         Guardar");
                btnEliminar.setText("         Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("/org/josegonzalez/image/Save.png"));
                imgEliminar.setImage(new Image("/org/josegonzalez/image/Cancel.png"));
                tipoDeOperacion = operaciones.GUARDAR;
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
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void guardar(){
        Cliente registro = new Cliente();
//        registro.setCodigoCliente(Integer.parseInt(txtCodigoCliente.getText()));
        registro.setNITCliente(txtNITCliente.getText());
        registro.setNombresCliente(txtNombresCliente.getText());
        registro.setApellidosCliente(txtApellidosCliente.getText());
        registro.setDireccionCliente(txtDireccionCliente.getText());
        registro.setTelefonoCliente(txtTelefonoCliente.getText());
        registro.setCorreoCliente(txtCorreoCliente.getText());
        if (registro.getNITCliente().isEmpty() ||
            registro.getNombresCliente().isEmpty() ||
            registro.getApellidosCliente().isEmpty() ||
            registro.getDireccionCliente().isEmpty() ||
            registro.getTelefonoCliente().isEmpty() ||
            registro.getCorreoCliente().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios de llenar");
        } else {
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCliente(?, ?, ?, ?, ?, ?)}");
                procedimiento.setString(1, registro.getNITCliente());
                procedimiento.setString(2, registro.getNombresCliente());
                procedimiento.setString(3, registro.getApellidosCliente());
                procedimiento.setString(4, registro.getDireccionCliente());
                procedimiento.setString(5, registro.getTelefonoCliente());
                procedimiento.setString(6, registro.getCorreoCliente());
                procedimiento.executeQuery();
                listarCliente.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("         Nuevo");
                btnEliminar.setText("         Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/josegonzalez/image/saveas_5165.png"));
                imgEliminar.setImage(new Image("/org/josegonzalez/image/Delete.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
            default:
                if(tblClientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Cliente", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarCliente(?)}");
//                            procedimiento.setInt(1, ((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente()); parte complicada del seleccionador de item
                            procedimiento.setInt(1, ((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente());
                            procedimiento.execute();
                            listarCliente.remove(tblClientes.getSelectionModel().getSelectedIndex());
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
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblClientes.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("         Actualizar");
                    btnReporte.setText("         Cancelar");
                    imgEditar.setImage(new Image("/org/josegonzalez/image/Save.png"));
                    imgReporte.setImage(new Image("/org/josegonzalez/image/Cancel.png"));
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
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
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimineto = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarClientes(?, ?, ?, ?, ?, ?, ?)}");
            Cliente registro = (Cliente)tblClientes.getSelectionModel().getSelectedItem();
            registro.setNITCliente(txtNITCliente.getText());
            registro.setNombresCliente(txtNombresCliente.getText());
            registro.setApellidosCliente(txtApellidosCliente.getText());
            registro.setDireccionCliente(txtDireccionCliente.getText());
            registro.setTelefonoCliente(txtTelefonoCliente.getText());
            registro.setCorreoCliente(txtCorreoCliente.getText());
            procedimineto.setInt(1, registro.getCodigoCliente());
            procedimineto.setString(2, registro.getNITCliente());
            procedimineto.setString(3, registro.getNombresCliente());
            procedimineto.setString(4, registro.getApellidosCliente());
            procedimineto.setString(5, registro.getDireccionCliente());
            procedimineto.setString(6, registro.getTelefonoCliente());
            procedimineto.setString(7, registro.getCorreoCliente());
            procedimineto.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporte(){
        switch(tipoDeOperacion){
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
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoCliente", null); // donde esta el null remplazarlo por un valor en especifio esa es la clave si el caso es mostrar un valor en especifico.
        String imagePath = getClass().getClassLoader().getResource("org/josegonzalez/image/Membrete.png").getPath();
        parametros.put("IMAGE_PATH", imagePath);
        GenerarReporte.mostrarReporte("ReporteClientes.jasper", "Reporte de Clientes", parametros);
    }
    
    public void desactivarControles(){
        txtCodigoCliente.setEditable(false);
        txtNITCliente.setEditable(false);
        txtNombresCliente.setEditable(false);
        txtApellidosCliente.setEditable(false);
        txtDireccionCliente.setEditable(false);
        txtTelefonoCliente.setEditable(false);
        txtCorreoCliente.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoCliente.setEditable(false);
        txtNITCliente.setEditable(true);
        txtNombresCliente.setEditable(true);
        txtApellidosCliente.setEditable(true);
        txtDireccionCliente.setEditable(true);
        txtTelefonoCliente.setEditable(true);
        txtCorreoCliente.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoCliente.clear();
        txtNITCliente.clear();
        txtNombresCliente.clear();
        txtApellidosCliente.clear();
        txtDireccionCliente.clear();
        txtTelefonoCliente.clear();
        txtCorreoCliente.clear();
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
}