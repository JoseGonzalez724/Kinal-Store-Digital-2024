package org.josegonzalez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.josegonzalez.bean.DetalleFactura;
import org.josegonzalez.bean.Producto;
import org.josegonzalez.bean.Factura;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class DetalleFacturaController implements Initializable{
    
    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<DetalleFactura>listaDetalleFactura;
    public ObservableList<Factura>listaFactura;
    public ObservableList<Producto>listaProducto;
    @FXML private TextField txtCodigoDetalleFactura;
    @FXML private TextField txtPrecioUnitario;
    @FXML private Spinner spnCantidad;
    @FXML private ComboBox cmbNumeroFactura;
    @FXML private ComboBox cmbCodigoProducto;
    @FXML private TableView tblDetalleFacturas;
    @FXML private TableColumn colCodigoDetalelF;
    @FXML private TableColumn colPrecioU;
    @FXML private TableColumn colCantidad;
    @FXML private TableColumn colNumeroFactura;
    @FXML private TableColumn colCodigoProducto;
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
        cmbCodigoProducto.setDisable(true);
        cmbCodigoProducto.setItems(getProducto());
        cmbNumeroFactura.setDisable(true);
        cmbNumeroFactura.setItems(getFactura());
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        spnCantidad.setValueFactory(valueFactory);
        spnCantidad.setDisable(true);
    }

    public void seleccionarElemento(){
        if(tblDetalleFacturas.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoDetalleFactura.setText(String.valueOf(((DetalleFactura)tblDetalleFacturas.getSelectionModel().getSelectedItem()).getCodigoDetalleFactura()));
            txtPrecioUnitario.setText(String.format("%.2f", ((DetalleFactura)tblDetalleFacturas.getSelectionModel().getSelectedItem()).getPrecioUnitario()));
            spnCantidad.getValueFactory().setValue(((DetalleFactura) tblDetalleFacturas.getSelectionModel().getSelectedItem()).getCantidad());
            cmbNumeroFactura.getSelectionModel().select(buscarFactura(((DetalleFactura)tblDetalleFacturas.getSelectionModel().getSelectedItem()).getNumeroFactura()));
            String codigoProductoString = ((DetalleFactura)tblDetalleFacturas.getSelectionModel().getSelectedItem()).getCodigoProducto();
            cmbCodigoProducto.getSelectionModel().select(codigoProductoString);
        }
    }
    
    public void cargarDatos(){
        tblDetalleFacturas.setItems(getDetalleFactura());
        colCodigoDetalelF.setCellValueFactory(new PropertyValueFactory<DetalleFactura, Integer>("codigoDetalleFactura"));
        colPrecioU.setCellValueFactory(new PropertyValueFactory<DetalleFactura, String>("precioUnitario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<DetalleFactura, Integer>("cantidad"));
        colNumeroFactura.setCellValueFactory(new PropertyValueFactory<DetalleFactura, Integer>("numeroFactura"));
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<DetalleFactura, String>("codigoProducto"));
        colPrecioU.setCellFactory(column -> {
            return new TableCell<DetalleFactura, Double>() {
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
                cmbCodigoProducto.setDisable(false);
                cmbNumeroFactura.setDisable(false);
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
        DetalleFactura registro = new DetalleFactura();
        if (spnCantidad.getValue() == null || cmbNumeroFactura.getSelectionModel().getSelectedItem() == null || cmbCodigoProducto.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Algunos campos son obligatorios de llenar");
        } else {
            registro.setCantidad((Integer) spnCantidad.getValue());
            registro.setNumeroFactura(((Factura)cmbNumeroFactura.getSelectionModel().getSelectedItem()).getNumeroFactura());
            registro.setCodigoProducto(((Producto)cmbCodigoProducto.getSelectionModel().getSelectedItem()).getCodigoProducto());
            try{
                 PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarDetalleFactura(?, ?, ?)}");
                 procedimiento.setInt(1, registro.getCantidad());
                 procedimiento.setInt(2, registro.getNumeroFactura());
                 procedimiento.setString(3, registro.getCodigoProducto());
                 procedimiento.executeQuery();
                 listaDetalleFactura.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
    }
    
    public ObservableList<DetalleFactura> getDetalleFactura(){
        ArrayList<DetalleFactura> lista = new ArrayList<DetalleFactura>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDetallesFacturas}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new DetalleFactura(resultado.getInt("codigoDetalleFactura"),
                                resultado.getDouble("precioUnitario"),
                                resultado.getInt("cantidad"),
                                resultado.getInt("numeroFactura"),
                                resultado.getString("codigoProducto")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaDetalleFactura = FXCollections.observableArrayList(lista);
    }    
    
    public ObservableList<Producto> getProducto(){
        ArrayList<Producto> lista = new ArrayList<Producto>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarProductos}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new Producto(resultado.getString("codigoProducto"),
                                resultado.getString("descripcionProducto"),
                                resultado.getDouble("precioUnitario"),
                                resultado.getDouble("precioDocena"),
                                resultado.getDouble("precioMayor"),
                                resultado.getString("imagenProducto"),
                                resultado.getInt("existencia"),
                                resultado.getInt("codigoTipoProducto"),
                                resultado.getInt("codigoProveedor")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaProducto = FXCollections.observableArrayList(lista);
    }
    
    public Producto buscarProducto(int codigoProducto){
        Producto P = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarProducto(?)}");
            procedimiento.setInt(1, codigoProducto);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                P = new Producto(resultado.getString("codigoProducto"),
                                resultado.getString("descripcionProducto"),
                                resultado.getDouble("precioUnitario"),
                                resultado.getDouble("precioDocena"),
                                resultado.getDouble("precioMayor"),
                                resultado.getString("imagenProducto"),
                                resultado.getInt("existencia"),
                                resultado.getInt("codigoTipoProducto"),
                                resultado.getInt("codigoProveedor"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return P;
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
    
    public Factura buscarFactura(int numeroFactura){
        Factura C = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarFactura(?)}");
            procedimiento.setInt(1, numeroFactura);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                C = new Factura(resultado.getInt("numeroFactura"),
                                resultado.getString("estado"),
                                resultado.getDouble("totalFactura"),
                                resultado.getDate("fechaFactura"),
                                resultado.getInt("codigoCliente"),
                                resultado.getInt("codigoEmpleado"));
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
                if(tblDetalleFacturas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarDetalleFactura(?)}");
                            procedimiento.setInt(1, ((DetalleFactura)tblDetalleFacturas.getSelectionModel().getSelectedItem()).getCodigoDetalleFactura());
                            procedimiento.execute();
                            listaDetalleFactura.remove(tblDetalleFacturas.getSelectionModel().getSelectedIndex());
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
                if(tblDetalleFacturas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoProducto.setDisable(true);
                    cmbNumeroFactura.setDisable(true);
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarDetalleFactura(?, ?, ?)}");
            DetalleFactura registro = (DetalleFactura)tblDetalleFacturas.getSelectionModel().getSelectedItem();
            registro.setPrecioUnitario(Double.parseDouble(txtPrecioUnitario.getText()));           
            registro.setCantidad((Integer) spnCantidad.getValue());
            procedimiento.setInt(1, registro.getCodigoDetalleFactura());
            procedimiento.setDouble(2, registro.getPrecioUnitario());
            procedimiento.setInt(3, registro.getCantidad());
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
        txtCodigoDetalleFactura.setEditable(false);
        txtPrecioUnitario.setEditable(false);
        spnCantidad.setDisable(true);
        cmbCodigoProducto.setDisable(true);
        cmbNumeroFactura.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoDetalleFactura.setEditable(true);
        txtPrecioUnitario.setEditable(true);
        spnCantidad.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoDetalleFactura.clear();
        txtPrecioUnitario.clear();
        spnCantidad.getValueFactory().setValue(0);
        cmbCodigoProducto.valueProperty().set(null);
        cmbNumeroFactura.valueProperty().set(null);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaFactura(){
        escenarioPrincipal.ventanaFactura();
    }
}
