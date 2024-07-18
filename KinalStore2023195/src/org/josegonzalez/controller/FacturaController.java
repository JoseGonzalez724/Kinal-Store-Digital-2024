package org.josegonzalez.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josegonzalez.bean.Factura;
import org.josegonzalez.bean.Cliente;
import org.josegonzalez.bean.Empleado;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.report.GenerarReporte;
import org.josegonzalez.system.Principal;

public class FacturaController implements Initializable{
    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<Factura>listaFactura;
    public ObservableList<Cliente>listaCliente;
    public ObservableList<Empleado>listaEmpleado;
    private DatePicker fecha;
    @FXML private TextField txtNumeroFactura;
    @FXML private TextField txtEstado;
    @FXML private TextField txtTotalF;
    @FXML private ComboBox cmbCodigoCliente;
    @FXML private ComboBox cmbCodigoEmpleado;
    @FXML private GridPane grpFecha;
    @FXML private TableView tblFacturas;
    @FXML private TableColumn colNumeroFactura;
    @FXML private TableColumn colEstado;
    @FXML private TableColumn colTotalFactura;
    @FXML private TableColumn colFechaFactura;
    @FXML private TableColumn colCodigoCliente;
    @FXML private TableColumn colCodigoEmpleado;
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
        cmbCodigoEmpleado.setDisable(true);
        cmbCodigoEmpleado.setItems(getEmpleado());
        cmbCodigoCliente.setDisable(true);
        cmbCodigoCliente.setItems(getCliente());
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(true);
        fecha.getStylesheets().add("/org/josegonzalez/resource/KinalStoreCSS.css");
        fecha.setDisable(true);
        grpFecha.add(fecha, 3, 1);
    }
    
    public void seleccionarElemento(){
        if(tblFacturas.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtNumeroFactura.setText(String.valueOf(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getNumeroFactura()));
            txtEstado.setText(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getEstado());
            txtTotalF.setText(String.format("%.2f", ((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getTotalFactura()));
            fecha.selectedDateProperty().set(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getFechaFactura());
            cmbCodigoCliente.getSelectionModel().select(buscarCliente(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getCodigoCliente()));
            cmbCodigoEmpleado.getSelectionModel().select(buscarEmpleado(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
        }
    }
    
    public void cargarDatos(){
        tblFacturas.setItems(getFactura());
        colNumeroFactura.setCellValueFactory(new PropertyValueFactory<Factura, Integer>("numeroFactura"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Factura, String>("estado"));
        colTotalFactura.setCellValueFactory(new PropertyValueFactory<Factura, Double>("totalFactura"));
        colFechaFactura.setCellValueFactory(new PropertyValueFactory<Factura, Date>("fechaFactura"));
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<Factura, Integer>("codigoCliente"));
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Factura, Integer>("codigoEmpleado"));
        colTotalFactura.setCellFactory(column -> {
            return new TableCell<Factura, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatearNumero(item));
                    }
                }
            };
        });
    }

    private String formatearNumero(double numero) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(numero);
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
                cmbCodigoEmpleado.setDisable(false);
                cmbCodigoCliente.setDisable(false);
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
        Factura registro = new Factura();
        if (txtNumeroFactura.getText().isEmpty() || txtEstado.getText().isEmpty() || fecha.getSelectedDate() == null || cmbCodigoCliente.getSelectionModel().getSelectedItem() == null || cmbCodigoEmpleado.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Algunos de los campos son obligatorios de llenar");
        } else {
            registro.setNumeroFactura(Integer.parseInt(txtNumeroFactura.getText()));
            registro.setEstado(txtEstado.getText());
            registro.setFechaFactura(fecha.getSelectedDate());
            registro.setCodigoCliente(((Cliente)cmbCodigoCliente.getSelectionModel().getSelectedItem()).getCodigoCliente());
            registro.setCodigoEmpleado(((Empleado)cmbCodigoEmpleado.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
            try{
                 PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarFactura(?, ?, ?, ?, ?)}");
                 procedimiento.setInt(1, registro.getNumeroFactura());
                 procedimiento.setString(2, registro.getEstado());
                 procedimiento.setDate(3, new java.sql.Date(registro.getFechaFactura().getTime()));
                 procedimiento.setInt(4, registro.getCodigoCliente());
                 procedimiento.setInt(5, registro.getCodigoEmpleado());
                 procedimiento.executeQuery();
                 listaFactura.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
    }
    
    public ObservableList<Factura> getFactura(){
        ArrayList<Factura> lista = new ArrayList<Factura>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarFacturas}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new Factura(resultado.getInt("numeroFactura"),
                                resultado.getString("estado"),
                                resultado.getDouble("totalFactura"),
                                resultado.getDate("fechaFactura"),
                                resultado.getInt("codigoCliente"),
                                resultado.getInt("codigoEmpleado")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaFactura = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Empleado> getEmpleado(){
        ArrayList<Empleado> lista = new ArrayList<Empleado>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEmpleados}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new Empleado(resultado.getInt("codigoEmpleado"),
                                resultado.getString("nombresEmpleado"),
                                resultado.getString("apellidosEmpleado"),
                                resultado.getDouble("sueldo"),
                                resultado.getString("direccionEmpleado"),
                                resultado.getString("turno"),
                                resultado.getInt("codigoCargoEmpleado")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEmpleado = FXCollections.observableArrayList(lista);
    }
    
    public Empleado buscarEmpleado(int codigoTipoProducto){
        Empleado E = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpleado(?)}");
            procedimiento.setInt(1, codigoTipoProducto);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                E = new Empleado(resultado.getInt("codigoEmpleado"),
                                resultado.getString("nombresEmpleado"),
                                resultado.getString("apellidosEmpleado"),
                                resultado.getDouble("sueldo"),
                                resultado.getString("direccionEmpleado"),
                                resultado.getString("turno"),
                                resultado.getInt("codigoCargoEmpleado"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return E;
    }
    
    public ObservableList<Cliente> getCliente(){
        ArrayList<Cliente> lista = new ArrayList<Cliente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarClientes}");
            ResultSet resultado = procedimiento.executeQuery(); 
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
        
        return listaCliente = FXCollections.observableArrayList(lista);
    }
    
    public Cliente buscarCliente(int codigoProveedor){
        Cliente C = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarCliente(?)}");
            procedimiento.setInt(1, codigoProveedor);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                C = new Cliente(resultado.getInt("codigoCliente"),
                                    resultado.getString("NITCliente"),
                                    resultado.getString("nombresCliente"),
                                    resultado.getString("apellidosCliente"),
                                    resultado.getString("direccionCliente"),
                                    resultado.getString("telefonoCliente"),
                                    resultado.getString("correoCliente"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return C;
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
                if(tblFacturas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarFactura(?)}");
                            procedimiento.setInt(1, ((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getNumeroFactura());
                            procedimiento.execute();
                            listaFactura.remove(tblFacturas.getSelectionModel().getSelectedIndex());
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
                if(tblFacturas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoEmpleado.setDisable(true);
                    cmbCodigoCliente.setDisable(true);
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarFactura(?, ?, ?, ?)}");
            Factura registro = (Factura)tblFacturas.getSelectionModel().getSelectedItem();
            registro.setEstado(txtEstado.getText());
            registro.setTotalFactura(Double.parseDouble(txtTotalF.getText()));
            registro.setFechaFactura(fecha.getSelectedDate());
            procedimiento.setInt(1, registro.getNumeroFactura());
            procedimiento.setString(2, registro.getEstado());
            procedimiento.setDouble(3, registro.getTotalFactura());
            procedimiento.setDate(4, new java.sql.Date(registro.getFechaFactura().getTime()));
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
        if(tblFacturas.getSelectionModel().getSelectedItem() != null){
            Map parametros = new HashMap();
            String imagePath = getClass().getClassLoader().getResource("org/josegonzalez/image/Membrete.png").getPath();
            parametros.put("IMAGE_PATH", imagePath);
            int numFact = Integer.valueOf(txtNumeroFactura.getText());
            parametros.put("noFactura", numFact);
            GenerarReporte.mostrarReporte("ReporteFactura.jasper", "Reporte de Factura", parametros);
        }else{
            JOptionPane.showMessageDialog(null, "Debes seleccionar un elemento");
        }
    }

    
    public void desactivarControles(){
        txtNumeroFactura.setEditable(false);
        txtEstado.setEditable(false);
        fecha.setDisable(true);
        txtTotalF.setEditable(false);
        cmbCodigoEmpleado.setDisable(true);
        cmbCodigoCliente.setDisable(true);
    }
    
    public void activarControles(){
        txtNumeroFactura.setEditable(true);
        txtEstado.setEditable(true);
        fecha.setDisable(false);
        txtTotalF.setEditable(true);
    }
    
    public void limpiarControles(){
        txtNumeroFactura.clear();
        txtEstado.clear();
        fecha.setSelectedDate(null);
        txtTotalF.clear();
        cmbCodigoEmpleado.valueProperty().set(null);
        cmbCodigoCliente.valueProperty().set(null);
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
    
    public void ventanaDetalleFactura(){
        escenarioPrincipal.ventanaDetalleFactura();
    }
}
