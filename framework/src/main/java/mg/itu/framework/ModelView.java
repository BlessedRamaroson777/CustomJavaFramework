package itu.framework;

import java.util.Map;
import java.util.HashMap;

public class ModelView {
    private String view;
    private HashMap<String, Object> mapData = new HashMap<>();

    public ModelView() {
    }

    public ModelView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    // Expose HashMap explicitly as demandé
    public HashMap<String, Object> getMapData() {
        return mapData;
    }

    public void setMapData(HashMap<String, Object> mapData) {
        this.mapData = mapData;
    }

    // Compatibilité : alias pour anciennes versions / différents usages
    public Map<String, Object> getMaqData() {
        return mapData;
    }

    public Map<String, Object> getmapData() {
        return mapData;
    }

    // Ajoute une donnée et retourne l'objet pour permettre le chaînage
    public ModelView addData(String key, Object value) {
        this.mapData.put(key, value);
        return this;
    }

    // Overloads for common primitive/object types to maximize compatibility
    public ModelView addData(String key, String value) {
        return addData(key, (Object) value);
    }

    public ModelView addData(String key, int value) {
        return addData(key, Integer.valueOf(value));
    }
}
