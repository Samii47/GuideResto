package ch.hearc.ig.guideresto.business;

/**
 * Director (GoF pattern)
 * Orchestre la construction d'un Restaurant en utilisant le Builder
 * Encapsule la logique de construction
 */
public class RestaurantDirector {

    private RestaurantBuilder builder;

    /**
     * Constructeur avec le builder
     */
    public RestaurantDirector(RestaurantBuilder builder) {
        this.builder = builder;
    }

    /**
     * Construit un restaurant complet avec tous ses attributs
     *
     * @return Le restaurant construit
     */
    public Restaurant constructCompleteRestaurant(Integer id, String name, String description,
                                                  String website, String street, City city,
                                                  RestaurantType type) {
        builder.reset();
        builder.buildId(id);
        builder.buildName(name);
        builder.buildDescription(description);
        builder.buildWebsite(website);
        builder.buildStreet(street);
        builder.buildCity(city);
        builder.buildType(type);
        return builder.getResult();
    }
}