package modelo;

public class Funcionario extends Usuario {
    private String nombre;
    private String telefono;

    public Funcionario() {}

    public Funcionario(String id, String clave, Rol rol, String nombre, String telefono) {
        super(id, clave, rol);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}