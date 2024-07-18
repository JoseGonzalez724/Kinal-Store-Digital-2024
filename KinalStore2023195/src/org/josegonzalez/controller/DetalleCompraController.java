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
import org.josegonzalez.bean.DetalleCompra;
import org.josegonzalez.bean.Producto;
import org.josegonzalez.bean.Compra;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class DetalleCompraController implements Initializable{
    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<DetalleCompra>listaDetalleCompra;
    public ObservableList<Producto>listaProducto;
    public ObservableList<Compra>listaCompra;
    @FXML private TextField txtCodigoDetalleCompra;
    @FXML private TextField txtCostoUnitario;
    @FXML private Spinner spnCantidad;
    @FXML private ComboBox cmbCodigoProducto;
    @FXML private ComboBox cmbCodigoCompra;
    @FXML private TableView tblDetalleCompra;
    @FXML private TableColumn colCodigoDetalleCompra;
    @FXML private TableColumn colCostoUnitario;
    @FXML private TableColumn colCantidad;
    @FXML private TableColumn colCodigoProducto;
    @FXML private TableColumn colCodigoCompra;
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
        cmbCodigoCompra.setDisable(true);
        cmbCodigoCompra.setItems(getCompra());
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        spnCantidad.setValueFactory(valueFactory);
        spnCantidad.setDisable(true);
    }

    public void seleccionarElemento(){
        if(tblDetalleCompra.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoDetalleCompra.setText(String.valueOf(((DetalleCompra)tblDetalleCompra.getSelectionModel().getSelectedItem()).getCodigoDetalleCompra()));
            txtCostoUnitario.setText(String.format("%.2f", ((DetalleCompra)tblDetalleCompra.getSelectionModel().getSelectedItem()).getCostoUnitario()));
            spnCantidad.getValueFactory().setValue(((DetalleCompra) tblDetalleCompra.getSelectionModel().getSelectedItem()).getCantidad());
            String codigoProductoString = ((DetalleCompra)tblDetalleCompra.getSelectionModel().getSelectedItem()).getCodigoProducto();
            cmbCodigoProducto.getSelectionModel().select(codigoProductoString);
            cmbCodigoCompra.getSelectionModel().select(buscarCompra(((DetalleCompra)tblDetalleCompra.getSelectionModel().getSelectedItem()).getNumeroDocumento()));
        }
    }
    
    public void cargarDatos(){
        tblDetalleCompra.setItems(getDetalleCompra());
        colCodigoDetalleCompra.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Integer>("codigoDetalleCompra"));
        colCostoUnitario.setCellValueFactory(new PropertyValueFactory<DetalleCompra, String>("costoUnitario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Integer>("cantidad"));
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<DetalleCompra, String>("codigoProducto"));
        colCodigoCompra.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Integer>("numeroDocumento"));
        colCostoUnitario.setCellFactory(column -> {
            return new TableCell<DetalleCompra, Double>() {
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
                cmbCodigoCompra.setDisable(false);
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
        DetalleCompra registro = new DetalleCompra();
        if (txtCostoUnitario.getText().isEmpty() || spnCantidad.getValue() == null || cmbCodigoCompra.getSelectionModel().getSelectedItem() == null || cmbCodigoProducto.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Algunos campos son obligatorios de llenar");
        } else {
            registro.setCostoUnitario(Double.parseDouble(txtCostoUnitario.getText()));
            registro.setCantidad((Integer)spnCantidad.getValue());
            registro.setCodigoProducto(((Producto)cmbCodigoProducto.getSelectionModel().getSelectedItem()).getCodigoProducto());
            registro.setNumeroDocumento(((Compra)cmbCodigoCompra.getSelectionModel().getSelectedItem()).getNumeroDocumento());
            try{
                 PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarDetalleCompra(?, ?, ?, ?)}");
                 procedimiento.setDouble(1, registro.getCostoUnitario());
                 procedimiento.setInt(2, registro.getCantidad());
                 procedimiento.setString(3, registro.getCodigoProducto());
                 procedimiento.setInt(4, registro.getNumeroDocumento());
                 procedimiento.executeQuery();
                 listaDetalleCompra.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
    }
    
    public ObservableList<DetalleCompra> getDetalleCompra(){
        ArrayList<DetalleCompra> lista = new ArrayList<DetalleCompra>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDetallesCompras}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new DetalleCompra(resultado.getInt("codigoDetalleCompra"),
                                resultado.getDouble("costoUnitario"),
                                resultado.getInt("cantidad"),
                                resultado.getString("codigoProducto"),
                                resultado.getInt("numeroDocumento")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaDetalleCompra = FXCollections.observableArrayList(lista);
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
    
    public Compra buscarCompra(int numeroDocumento){
        Compra C = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarCompra(?)}");
            procedimiento.setInt(1, numeroDocumento);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                C = new Compra(resultado.getInt("numeroDocumento"),
                                resultado.getDate("fechaDocumento"),
                                resultado.getString("descripcion"),
                                resultado.getDouble("totalDocumento"));
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
                if(tblDetalleCompra.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarDetalleCompra(?)}");
                            procedimiento.setInt(1, ((DetalleCompra)tblDetalleCompra.getSelectionModel().getSelectedItem()).getCodigoDetalleCompra());
                            procedimiento.execute();
                            listaDetalleCompra.remove(tblDetalleCompra.getSelectionModel().getSelectedIndex());
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
                if(tblDetalleCompra.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoProducto.setDisable(true);
                    cmbCodigoCompra.setDisable(true);
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarDetalleCompra(?, ?, ?)}");
            DetalleCompra registro = (DetalleCompra)tblDetalleCompra.getSelectionModel().getSelectedItem();
            registro.setCostoUnitario(Double.parseDouble(txtCostoUnitario.getText()));           
            registro.setCantidad((Integer) spnCantidad.getValue());        
            registro.setCodigoProducto(cmbCodigoProducto.getSelectionModel().getSelectedItem().toString());

            registro.setNumeroDocumento(((Compra)cmbCodigoCompra.getSelectionModel().getSelectedItem()).getNumeroDocumento());
            procedimiento.setInt(1, registro.getCodigoDetalleCompra());
            procedimiento.setDouble(2, registro.getCostoUnitario());
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
        txtCodigoDetalleCompra.setEditable(false);
        txtCostoUnitario.setEditable(false);
        spnCantidad.setDisable(true);
        cmbCodigoProducto.setDisable(true);
        cmbCodigoCompra.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoDetalleCompra.setEditable(true);
        txtCostoUnitario.setEditable(true);
        spnCantidad.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoDetalleCompra.clear();
        txtCostoUnitario.clear();
        spnCantidad.getValueFactory().setValue(0);
        cmbCodigoProducto.valueProperty().set(null);
        cmbCodigoCompra.valueProperty().set(null);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaCompra(){
        escenarioPrincipal.ventanaCompra();
    }
}
