package org.josegonzalez.db;

import java.sql.Connection;
import com.mysql.jdbc.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private Connection conexion;
    private static Conexion instancia;
    
    public Conexion(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBKinalStore2024?useSSL=false", "IN5AV", "admin"); //datos para la conexion en la entrega del proyecto: usuario:root, password:admin
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBKinalStore2024?useSSL=false", "Jose_Gonzalez", "rootPasword24"); //datos para la conexion en la entrega del proyecto: usuario:root, password:admin
//            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBKinalStore2024?useSSL=false", "root", "admin15"); //datos para la conexion en la entrega del proyecto: usuario:root, password:admin
            
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static Conexion getInstance(){
        if(instancia == null)
            instancia = new Conexion();
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }
    
    
}
