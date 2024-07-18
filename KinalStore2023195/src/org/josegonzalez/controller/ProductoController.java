package org.josegonzalez.controller;

import java.io.File;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.swing.JOptionPane;
import org.josegonzalez.bean.Producto;
import org.josegonzalez.bean.Proveedor;
import org.josegonzalez.bean.TipoProducto;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.system.Principal;

public class ProductoController implements Initializable{
        
    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<Producto>listaProducto;
    public ObservableList<Proveedor>listaProveedor;
    public ObservableList<TipoProducto>listaTipoProducto;
    private String imagenRuta;
    private Image image;
    @FXML private AnchorPane ContenedoPrincipal;
    @FXML private TextField txtCodigoProducto;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecioU;
    @FXML private TextField txtPrecioD;
    @FXML private TextField txtPrecioM;
    @FXML private TextField txtExistencias;
    @FXML private ComboBox cmbCodigoTipoProducto;
    @FXML private ComboBox cmbCodigoProveedor;
    @FXML private TableView tblProductos;
    @FXML private TableColumn colCodigoProducto;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colPrecioU;
    @FXML private TableColumn colPrecioD;
    @FXML private TableColumn colPrecioM;
    @FXML private TableColumn colExistencias;
    @FXML private TableColumn colCodigoTipoProducto;
    @FXML private TableColumn colCodigoProveedor;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnImportarImagen;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private ImageView imgImportarImagen;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoTipoProducto.setDisable(true);
        cmbCodigoTipoProducto.setItems(getTipoProducto());
        cmbCodigoProveedor.setDisable(true);
        cmbCodigoProveedor.setItems(getProveedor());
        btnImportarImagen.setDisable(true);
    }

    public void seleccionarElemento(){
        if(tblProductos.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoProducto.setText(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto());
            txtDescripcion.setText(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getDescripcionProducto());
            txtPrecioU.setText(String.format("%.2f", ((Producto)tblProductos.getSelectionModel().getSelectedItem()).getPrecioUnitario()));
            txtPrecioD.setText(String.format("%.2f", ((Producto)tblProductos.getSelectionModel().getSelectedItem()).getPrecioDocena()));
            txtPrecioM.setText(String.format("%.2f", ((Producto)tblProductos.getSelectionModel().getSelectedItem()).getPrecioMayor()));
            txtExistencias.setText(String.valueOf(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getExistencia()));
            cmbCodigoTipoProducto.getSelectionModel().select(buscarTipoProducto(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getCodigoTipoProducto()));
            cmbCodigoProveedor.getSelectionModel().select(buscarProveedor(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
            
            String imagePath = ((Producto)tblProductos.getSelectionModel().getSelectedItem()).getImagenProducto();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString(), 160, 160, false, true);
                    imgImportarImagen.setImage(image);
                } else {
                    imgImportarImagen.setImage(null);
                    System.out.println("Archivo de imagen no encontrado: " + imagePath);
                }
            } else {
                imgImportarImagen.setImage(null);
            }
        }
    }
    
    public void cargarDatos(){
        tblProductos.setItems(getProducto());
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("codigoProducto"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Producto, String>("descripcionProducto"));
        colPrecioU.setCellValueFactory(new PropertyValueFactory<Producto, Double>("precioUnitario"));
        colPrecioD.setCellValueFactory(new PropertyValueFactory<Producto, Double>("precioDocena"));
        colPrecioM.setCellValueFactory(new PropertyValueFactory<Producto, Double>("precioMayor"));
        colExistencias.setCellValueFactory(new PropertyValueFactory<Producto, String>("existencia"));
        colCodigoTipoProducto.setCellValueFactory(new PropertyValueFactory<Producto, String>("codigoTipoProducto"));
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("codigoProveedor"));
        
        formatearColumnaPrecio(colPrecioU);
        formatearColumnaPrecio(colPrecioD);
        formatearColumnaPrecio(colPrecioM);

    }

    private void formatearColumnaPrecio(TableColumn<Producto, Double> columna) {
        columna.setCellFactory(column -> {
            return new TableCell<Producto, Double>() {
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
                cmbCodigoTipoProducto.setDisable(false);
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
        Producto registro = new Producto();
        if (txtCodigoProducto.getText().isEmpty() || txtDescripcion.getText().isEmpty() || cmbCodigoProveedor.getSelectionModel().getSelectedItem() == null || cmbCodigoTipoProducto.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Algunos campos son obligatorios de llenar");
        } else {
            registro.setCodigoProducto(txtCodigoProducto.getText());
            registro.setDescripcionProducto(txtDescripcion.getText());
            registro.setImagenProducto(imagenRuta);
            registro.setExistencia(Integer.parseInt(txtExistencias.getText()));
            registro.setCodigoTipoProducto(((TipoProducto)cmbCodigoTipoProducto.getSelectionModel().getSelectedItem()).getCodigoTipoProducto());
            registro.setCodigoProveedor(((Proveedor)cmbCodigoProveedor.getSelectionModel().getSelectedItem()).getCodigoProveedor());
            try{
                 PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarProducto(?, ?, ?, ?, ?, ?)}");   
                 procedimiento.setString(1, registro.getCodigoProducto());
                 procedimiento.setString(2, registro.getDescripcionProducto());
                 procedimiento.setString(3, registro.getImagenProducto());
                 procedimiento.setInt(4, registro.getExistencia());
                 procedimiento.setInt(5, registro.getCodigoTipoProducto());
                 procedimiento.setInt(6, registro.getCodigoProveedor());
                 procedimiento.executeQuery();
                 listaProducto.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
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
    
    public ObservableList<TipoProducto> getTipoProducto(){
        ArrayList<TipoProducto> lista = new ArrayList<TipoProducto>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarTiposProductos}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new TipoProducto(resultado.getInt("CodigoTipoProducto"),
                                resultado.getString("Descripcion")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTipoProducto = FXCollections.observableArrayList(lista);
    }
    
    public TipoProducto buscarTipoProducto(int codigoTipoProducto){
        TipoProducto TP = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarTipoProducto(?)}");
            procedimiento.setInt(1, codigoTipoProducto);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                TP = new TipoProducto(resultado.getInt("CodigoTipoProducto"),
                                resultado.getString("Descripcion"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return TP;
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
                if(tblProductos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarProducto(?)}");
                            procedimiento.setString(1, ((Producto)tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto());
                            procedimiento.execute();
                            listaProducto.remove(tblProductos.getSelectionModel().getSelectedIndex());
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
                if(tblProductos.getSelectionModel().getSelectedItem() != null){
                    txtPrecioU.setEditable(true);
                    txtPrecioD.setEditable(true);
                    txtPrecioU.setEditable(true);
                    txtExistencias.setEditable(true);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoTipoProducto.setDisable(true);
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarProducto(?, ?, ?, ?, ?, ?, ?)}");
            Producto registro = (Producto)tblProductos.getSelectionModel().getSelectedItem();
            registro.setDescripcionProducto(txtDescripcion.getText());
            registro.setImagenProducto(imagenRuta);
            registro.setExistencia(Integer.parseInt(txtExistencias.getText()));
            procedimiento.setString(1, registro.getCodigoProducto());
            procedimiento.setString(2, registro.getDescripcionProducto());
            procedimiento.setString(3, registro.getImagenProducto());
            procedimiento.setDouble(4, registro.getPrecioUnitario());
            procedimiento.setDouble(5, registro.getPrecioDocena());
            procedimiento.setDouble(6, registro.getPrecioMayor());
            procedimiento.setInt(7, registro.getExistencia());
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
    
    public void ImportarImagen() {
        FileChooser abrirArchivo = new FileChooser();
        abrirArchivo.getExtensionFilters().add(
                new ExtensionFilter("Tipo de archivo permitido para abrir imagen", "*.png", "*.jpg"));
        File archivo = abrirArchivo.showOpenDialog(ContenedoPrincipal.getScene().getWindow());

        if (archivo != null) {
            imagenRuta = archivo.getAbsolutePath();
            image = new Image(archivo.toURI().toString(), 160, 160, false, true);
            imgImportarImagen.setImage(image);
        }
    }
    
    public void desactivarControles(){
        txtCodigoProducto.setEditable(false);
        txtDescripcion.setEditable(false);
        imgImportarImagen.setDisable(true);
        txtExistencias.setEditable(false);
        cmbCodigoTipoProducto.setDisable(true);
        cmbCodigoProveedor.setDisable(true);
        btnImportarImagen.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoProducto.setEditable(true);
        txtDescripcion.setEditable(true);
        imgImportarImagen.setDisable(false);
        txtExistencias.setEditable(true);
        btnImportarImagen.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoProducto.clear();
        txtDescripcion.clear();
        txtPrecioU.clear();
        txtPrecioD.clear();
        txtPrecioM.clear();
        txtExistencias.clear();
        imgImportarImagen.setImage(null);
        cmbCodigoTipoProducto.valueProperty().set(null);
        cmbCodigoProveedor.valueProperty().set(null);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaTipoProducto(){
        escenarioPrincipal.ventanaTipoProducto();
    }
}
