package org.josegonzalez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import javax.swing.JOptionPane;
import org.josegonzalez.bean.Empleado;
import org.josegonzalez.bean.CargoEmpleado;
import org.josegonzalez.db.Conexion;
import org.josegonzalez.report.GenerarReporte;
import org.josegonzalez.system.Principal;

public class EmpleadoController implements Initializable{
    
    private Principal escenarioPrincipal;
    
    private enum operaciones{NUEVO,EDITAR,ELIMINAR,ACTUALIZAR, GUARDAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    public ObservableList<Empleado>listaTelefonoProveedor;
    public ObservableList<CargoEmpleado>listaProveedor;
    @FXML private TextField txtCodigoEmpleado;
    @FXML private TextField txtNombreEmpleado;
    @FXML private TextField txtApellidosEmpleado;
    @FXML private TextField txtSueldo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTurno;
    @FXML private ComboBox cmbCargoEmpleado;
    @FXML private TableView tblEmpledados;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colNombresEmpleado;
    @FXML private TableColumn colApellidosEmpleado;
    @FXML private TableColumn colSueldo;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colTurno;
    @FXML private TableColumn colCargoEmpleado;
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
        cmbCargoEmpleado.setDisable(true);
        cmbCargoEmpleado.setItems(getCargoEmpleado());
    }
    
    public void seleccionarElemento(){
        if(tblEmpledados.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null, "Debes seleccionar un item obligatorio en la tabla");
        }else{
            txtCodigoEmpleado.setText(String.valueOf(((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            txtNombreEmpleado.setText(((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getNombresEmpleado());
            txtApellidosEmpleado.setText(((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getApellidosEmpleado());
            txtSueldo.setText(String.valueOf(((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getSueldo()));
            txtDireccion.setText(((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getDireccionEmpleado());
            txtTurno.setText(((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getTurno());
            cmbCargoEmpleado.getSelectionModel().select(buscarProveedor(((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getCodigoCargoEmpleado()));
        }
    }
    
    public void cargarDatos(){
        tblEmpledados.setItems(getEmpleado());
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("codigoEmpleado"));
        colNombresEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("nombresEmpleado"));
        colApellidosEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("apellidosEmpleado"));
        colSueldo.setCellValueFactory(new PropertyValueFactory<Empleado, Double>("sueldo"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Empleado, String>("direccionEmpleado"));
        colTurno.setCellValueFactory(new PropertyValueFactory<Empleado, String>("turno"));
        colCargoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("codigoCargoEmpleado"));
        colSueldo.setCellFactory(column -> {
        return new TableCell<Empleado, Double>() {
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
        Empleado registro = new Empleado();
        if (txtCodigoEmpleado.getText().isEmpty() || txtNombreEmpleado.getText().isEmpty() || txtApellidosEmpleado.getText().isEmpty() || txtSueldo.getText().isEmpty() || txtDireccion.getText().isEmpty() || txtTurno.getText().isEmpty() || cmbCargoEmpleado.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Algunos de los campos son obligatorios de llenar");
        } else {
            registro.setCodigoEmpleado(Integer.parseInt(txtCodigoEmpleado.getText()));
            registro.setNombresEmpleado(txtNombreEmpleado.getText());
            registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
            registro.setSueldo(Double.parseDouble(txtSueldo.getText()));
            registro.setDireccionEmpleado(txtDireccion.getText());
            registro.setTurno(txtTurno.getText());
            registro.setCodigoCargoEmpleado(((CargoEmpleado)cmbCargoEmpleado.getSelectionModel().getSelectedItem()).getCodigoCargoEmpleado());
            try{
                 PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmpleado(?, ?, ?, ?, ?, ?, ?)}");
                 procedimiento.setInt(1, registro.getCodigoEmpleado());
                 procedimiento.setString(2, registro.getNombresEmpleado());
                 procedimiento.setString(3, registro.getApellidosEmpleado());
                 procedimiento.setDouble(4, registro.getSueldo());
                 procedimiento.setString(5, registro.getDireccionEmpleado());
                 procedimiento.setString(6, registro.getTurno());
                 procedimiento.setInt(7, registro.getCodigoCargoEmpleado());
                 procedimiento.executeQuery();
                 listaTelefonoProveedor.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
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
        return listaTelefonoProveedor = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<CargoEmpleado> getCargoEmpleado(){
        ArrayList<CargoEmpleado> lista = new ArrayList<CargoEmpleado>();
        try{
         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarCargosEmpleados}");
         ResultSet resultado = procedimiento.executeQuery();
         while(resultado.next()){
             lista.add(new CargoEmpleado(resultado.getInt("codigoCargoEmpleado"),
                                resultado.getString("nombreCargo"),
                                resultado.getString("descripcionCargo")));
         }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaProveedor = FXCollections.observableArrayList(lista);
    }
    
    public CargoEmpleado buscarProveedor(int codigoCargoEmpleado){
        CargoEmpleado cargo = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarCargoEmpleado(?)}");
            procedimiento.setInt(1, codigoCargoEmpleado);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                cargo = new CargoEmpleado(resultado.getInt("codigoCargoEmpleado"),
                                resultado.getString("nombreCargo"),
                                resultado.getString("descripcionCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return cargo;
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
                if(tblEmpledados.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro seleccionado", "Eliminar Empleado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ElimiarEmpleado(?)}");
                            procedimiento.setInt(1, ((Empleado)tblEmpledados.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                            procedimiento.execute();
                            listaTelefonoProveedor.remove(tblEmpledados.getSelectionModel().getSelectedIndex());
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
                if(tblEmpledados.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarEmpleado(?, ?, ?, ?, ?, ?)}");
            Empleado registro = (Empleado)tblEmpledados.getSelectionModel().getSelectedItem();
            registro.setCodigoEmpleado(Integer.parseInt(txtCodigoEmpleado.getText()));
            registro.setNombresEmpleado(txtNombreEmpleado.getText());
            registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
            registro.setSueldo(Double.parseDouble(txtSueldo.getText()));
            registro.setDireccionEmpleado(txtDireccion.getText());
            registro.setTurno(txtTurno.getText());
            registro.setCodigoCargoEmpleado(((CargoEmpleado)cmbCargoEmpleado.getSelectionModel().getSelectedItem()).getCodigoCargoEmpleado());procedimiento.setInt(1, registro.getCodigoEmpleado());
            procedimiento.setString(2, registro.getNombresEmpleado());
            procedimiento.setString(3, registro.getApellidosEmpleado());
            procedimiento.setDouble(4, registro.getSueldo());
            procedimiento.setString(5, registro.getDireccionEmpleado());
            procedimiento.setString(6, registro.getTurno());
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
        parametros.put("codigoEmpleado", null);
        String imagePath = getClass().getClassLoader().getResource("org/josegonzalez/image/Membrete.png").getPath();
        parametros.put("IMAGE_PATH", imagePath);
        GenerarReporte.mostrarReporte("ReporteEmpleados.jasper", "Reporte de empleados de Kinal Store", parametros);
    }
    
    public void desactivarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNombreEmpleado.setEditable(false);
        txtApellidosEmpleado.setEditable(false);
        txtSueldo.setEditable(false);
        txtDireccion.setEditable(false);
        txtTurno.setEditable(false);
        cmbCargoEmpleado.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoEmpleado.setEditable(true);
        txtNombreEmpleado.setEditable(true);
        txtNombreEmpleado.setEditable(true);
        txtApellidosEmpleado.setEditable(true);
        txtSueldo.setEditable(true);
        txtDireccion.setEditable(true);
        txtTurno.setEditable(true);
        cmbCargoEmpleado.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoEmpleado.clear();
        txtNombreEmpleado.clear();
        txtApellidosEmpleado.clear();
        txtSueldo.clear();
        txtDireccion.clear();
        txtTurno.clear();
        cmbCargoEmpleado.valueProperty().set(null);
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaCargoEmpleado(){
        escenarioPrincipal.ventanaCargoEmpleado();
    }
    
    public void ventanaProveedor(){
        escenarioPrincipal.ventanaProveedor();
    }
}
