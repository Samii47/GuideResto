package ch.hearc.ig.guideresto.business;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cedric.baudet
 */
public class City {

    private Integer id;
    private String zipCode;
    private String cityName;
    private Set<Restaurant> restaurants = null;

    public City() {
        this(null, null);
    }

    public City(String zipCode, String cityName) {
        this(null, zipCode, cityName);
    }

    public City(Integer id, String zipCode, String cityName) {
        this.id = id;
        this.zipCode = zipCode;
        this.cityName = cityName;
        this.restaurants = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String city) {
        this.cityName = city;
    }

    public Set<Restaurant> getRestaurants() {
        if(restaurants == null) {
            loadRestaurants();
        }
        return restaurants;
    }

    private void loadRestaurants() {
        System.out.println("Lazy Load : Chargement des restaurants de " + this.cityName);
        this.restaurants = new HashSet<>();
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

}