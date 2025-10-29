package ch.hearc.ig.guideresto.business;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cedric.baudet
 */
public class RestaurantType {

    private Integer id;
    private String label;
    private String description;
    private Set<Restaurant> restaurants = null;

    public RestaurantType() {
        this(null, null);
    }

    public RestaurantType(String label, String description) {
        this(null, label, description);
    }

    public RestaurantType(Integer id, String label, String description) {
        this.restaurants = null;
        this.id = id;
        this.label = label;
        this.description = description;
    }

    @Override
    public String toString() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Restaurant> getRestaurants() {
        if (restaurants == null) {
            loadRestaurants();
        }
        return restaurants;
    }

    private void loadRestaurants() {
        System.out.println("Lazy Load : Chargement des restaurants de type " + this.label);
        this.restaurants = new HashSet<>();
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

}