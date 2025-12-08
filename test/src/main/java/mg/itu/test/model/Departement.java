package mg.itu.test.model;

import java.util.ArrayList;
import java.util.List;

public class Departement {
    private int id;
    private String nom;
    private String localisation;
    private List<Employe> employes;

    public Departement() {
        this.employes = new ArrayList<>();
    }

    public Departement(int id, String nom, String localisation) {
        this.id = id;
        this.nom = nom;
        this.localisation = localisation;
        this.employes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public List<Employe> getEmployes() {
        return employes;
    }

    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }

    public void addEmploye(Employe employe) {
        this.employes.add(employe);
    }

    @Override
    public String toString() {
        return "Departement{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", localisation='" + localisation + '\'' +
                ", nombreEmployes=" + employes.size() +
                '}';
    }
}
