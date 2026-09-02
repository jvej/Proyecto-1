package modelo;
//why Jeferson?

//Listo axel
public class Recurso {
    private String id;
    private String categoriaID;
    private String descripcion;

    public Recurso (){}
    public Recurso (String id,String categoriaId, String descripcion){
        this.id=id;
        this.categoriaID=categoriaId;
        this.descripcion = descripcion;
    }

    public String getId(){ return id;}
    public String getCategoriaId(){ return categoriaID;}
    public String getDescripcion(){ return descripcion;}

    public void setId(String id){ this.id=id;}
    public void setCategoriaId(String categoriaID){ this.id=categoriaID;}
    public void setDescripcion(String descripcion){ this.descripcion=descripcion;}
}
