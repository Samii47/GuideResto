package ch.hearc.ig.guideresto.business;

/**
 * Builder (GoF pattern) pour construire des instances de Restaurant
 * Utilisé par le Director pour orchestrer la construction
 */
public class RestaurantBuilder {

    // Attributs du restaurant en cours de construction
    private Integer id;
    private String name;
    private String description = "";
    private String website = "";
    private String street = "";
    private City city;
    private RestaurantType type;

    /**
     * Constructeur
     */
    public RestaurantBuilder() {
    }

    /**
     * Réinitialise le builder pour une nouvelle construction
     */
    public void reset() {
        this.id = null;
        this.name = null;
        this.description = "";
        this.website = "";
        this.street = "";
        this.city = null;
        this.type = null;
    }

    /**
     * Définit l'identifiant du restaurant
     */
    public void buildId(Integer id) {
        this.id = id;
    }

    /**
     * Définit le nom du restaurant
     */
    public void buildName(String name) {
        this.name = name;
    }

    /**
     * Définit la description du restaurant
     */
    public void buildDescription(String description) {
        this.description = description;
    }

    /**
     * Définit le site web du restaurant
     */
    public void buildWebsite(String website) {
        this.website = website;
    }

    /**
     * Définit la rue du restaurant
     */
    public void buildStreet(String street) {
        this.street = street;
    }

    /**
     * Définit la ville du restaurant
     */
    public void buildCity(City city) {
        this.city = city;
    }

    /**
     * Définit le type du restaurant
     */
    public void buildType(RestaurantType type) {
        this.type = type;
    }

    /**
     * Construit et retourne le restaurant final
     * Gère automatiquement les relations bidirectionnelles
     */
    public Restaurant getResult() {
        // Construction du restaurant
        Restaurant restaurant = new Restaurant(id, name, description, website, street, city, type);

        // Gestion des relations bidirectionnelles
        if (city != null) {
            city.getRestaurants().add(restaurant);
        }
        if (type != null) {
            type.getRestaurants().add(restaurant);
        }

        return restaurant;
    }
}