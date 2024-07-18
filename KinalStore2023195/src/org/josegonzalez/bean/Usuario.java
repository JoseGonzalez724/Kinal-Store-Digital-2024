package org.josegonzalez.bean;

public class Usuario {
    private int codigoUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String usuarologin;
    private String contrasena;

    public Usuario() {
    }

    public Usuario(int codigoUsuario, String nombreUsuario, String apellidoUsuario, String usuarologin, String contrasena) {
        this.codigoUsuario = codigoUsuario;
        this.nombreUsuario = nombreUsuario;
        this.apellidoUsuario = apellidoUsuario;
        this.usuarologin = usuarologin;
        this.contrasena = contrasena;
    }

    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(int codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getUsuarologin() {
        return usuarologin;
    }

    public void setUsuarologin(String usuarologin) {
        this.usuarologin = usuarologin;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    
}
