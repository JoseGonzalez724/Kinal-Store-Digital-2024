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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josegonzalez.bean.Compra;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class CompraController implements Initializable{

    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO, ELIMINAR, ACTUALIZAR, GUARDAR, CANCELAR, NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<Compra> listaCompra;
    private DatePicker fecha;
    @FXML private TextField txtNumeroDocumento;
    @FXML private GridPane grpFecha;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtTotalDocumento;
    @FXML private TableView tblCompras;
    @FXML private TableColumn colNumeroDocumento;
    @FXML private TableColumn colFechaDocumento;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colTotalDocumento;
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
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(true);
        fecha.getStylesheets().add("/org/josegonzalez/resource/KinalStoreCSS.css");
        fecha.setDisable(true);
        grpFecha.add(fecha, 3, 0);
    }

    public void seleccionarElemento(){
        if(tblCompras.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtNumeroDocumento.setText(String.valueOf(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getNumeroDocumento()));
            fecha.selectedDateProperty().set(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getFechaDocumento());
            txtDescripcion.setText(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getDescripcion());
            txtTotalDocumento.setText(String.format("%.2f", ((Compra)tblCompras.getSelectionModel().getSelectedItem()).getTotalDocumento()));
        }
    }
    
    public void cargarDatos() {
    tblCompras.setItems(getCompra());
    colNumeroDocumento.setCellValueFactory(new PropertyValueFactory<Compra, Integer>("numeroDocumento"));
    colFechaDocumento.setCellValueFactory(new PropertyValueFactory<Compra, Date>("fechaDocumento"));
    colDescripcion.setCellValueFactory(new PropertyValueFactory<Compra, String>("descripcion"));
    colTotalDocumento.setCellValueFactory(new PropertyValueFactory<Compra, Double>("totalDocumento"));
    colTotalDocumento.setCellFactory(column -> {
            return new TableCell<Compra, Double>() {
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
        Compra registro = new Compra();
        if (txtNumeroDocumento.getText().isEmpty() || fecha.getSelectedDate() == null || txtDescripcion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios de llenar");
        } else {
            registro.setNumeroDocumento(Integer.parseInt(txtNumeroDocumento.getText()));
            registro.setFechaDocumento(fecha.getSelectedDate());
            registro.setDescripcion(txtDescripcion.getText());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCompra(?, ?, ?)}");
                procedimiento.setInt(1, registro.getNumeroDocumento());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaDocumento().getTime()));
                procedimiento.setString(3, registro.getDescripcion());
                procedimiento.executeQuery();
                listaCompra.add(registro);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public ObservableList<Compra> getCompra(){
        ArrayList<Compra> lista = new ArrayList<Compra>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarCompras}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new Compra(resultado.getInt("numeroDocumento"),
                                resultado.getDate("fechaDocumento"),
                                resultado.getString("descripcion"),
                                resultado.getDouble("totalDocumento")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaCompra = FXCollections.observableArrayList(lista);
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
                if(tblCompras.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Proveedor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarCompra(?)}");
                            procedimiento.setInt(1, ((Compra)tblCompras.getSelectionModel().getSelectedItem()).getNumeroDocumento());
                            procedimiento.execute();
                            listaCompra.remove(tblCompras.getSelectionModel().getSelectedIndex());
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
                if(tblCompras.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("         Actualizar");
                    btnReporte.setText("         Cancelar");
                    imgEditar.setImage(new Image("/org/josegonzalez/image/Save.png"));
                    imgReporte.setImage(new Image("/org/josegonzalez/image/Cancel.png"));
                    activarControles();
                    txtTotalDocumento.setEditable(true);
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarCompra(?, ?, ?, ?)}");
            Compra registro = (Compra)tblCompras.getSelectionModel().getSelectedItem();
            registro.setNumeroDocumento(Integer.parseInt(txtNumeroDocumento.getText()));
            registro.setFechaDocumento(fecha.getSelectedDate());
            registro.setDescripcion(txtDescripcion.getText());
            registro.setTotalDocumento(Double.parseDouble(txtTotalDocumento.getText()));
            procedimiento.setInt(1, registro.getNumeroDocumento());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaDocumento().getTime()));
            procedimiento.setString(3, registro.getDescripcion());
            procedimiento.setDouble(4, registro.getTotalDocumento());
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
        txtNumeroDocumento.setEditable(false);
        fecha.setDisable(true);
        txtDescripcion.setEditable(false);
        txtTotalDocumento.setEditable(false);
    }
    
    public void activarControles(){
        txtNumeroDocumento.setEditable(true);
        fecha.setDisable(false);
        txtDescripcion.setEditable(true);
        txtTotalDocumento.setEditable(false);
    }
    
    public void limpiarControles(){
        txtNumeroDocumento.clear();
        fecha.setSelectedDate(null);
        txtDescripcion.clear();
        txtTotalDocumento.clear();
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
    
    public void ventanaDetalleCompra(){
        escenarioPrincipal.ventanaDetalleCompra();
    }
}
