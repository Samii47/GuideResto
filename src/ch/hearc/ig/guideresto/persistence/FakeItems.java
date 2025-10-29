package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.*;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author cedric.baudet
 */
public class FakeItems {

    private static Set<RestaurantType> types;
    private static Set<Restaurant> restaurants;
    private static Set<EvaluationCriteria> criterias;
    private static Set<City> cities;

    private static boolean initDone = false;

    private static void init() {
        initDone = true;

        restaurants = new LinkedHashSet<>();
        types = new LinkedHashSet<>();
        criterias = new LinkedHashSet<>();
        cities = new LinkedHashSet<>();

        RestaurantType typeSuisse = new RestaurantType(1, "Cuisine suisse", "Cuisine classique et plats typiquement suisses");
        RestaurantType typeGastro = new RestaurantType(2, "Restaurant gastronomique", "Restaurant gastronomique de haut standing");

        types.add(typeSuisse);
        types.add(typeGastro);
        types.add(new RestaurantType(3, "Pizzeria", "Pizzas et autres spécialités italiennes"));

        EvaluationCriteria critService = new EvaluationCriteria(1, "Service", "Qualité du service");
        EvaluationCriteria critCuisine = new EvaluationCriteria(2, "Cuisine", "Qualité de la nourriture");
        EvaluationCriteria critCadre = new EvaluationCriteria(3, "Cadre", "L'ambiance et la décoration sont-elles bonnes ?");

        criterias.add(critService);
        criterias.add(critCuisine);
        criterias.add(critCadre);

        // Création du Builder et du Director pour construire les restaurants
        RestaurantBuilder builder = new RestaurantBuilder();
        RestaurantDirector director = new RestaurantDirector(builder);
        City city = new City(1, "2000", "Neuchatel");
        cities.add(city);

        Restaurant restaurant = director.constructCompleteRestaurant(
                1,
                "Fleur-de-Lys",
                "Pizzeria au centre de Neuchâtel",
                "http://www.pizzeria-neuchatel.ch/",
                "Rue du Bassin 10",
                city,
                typeSuisse
        );
        // LazyLoad --> Créer d'abord les évaluations dans un Set temporaire
        Set<Evaluation> evaluations1 = new LinkedHashSet<>();
        evaluations1.add(new BasicEvaluation(1, new Date(), restaurant, true, "1.2.3.4"));
        evaluations1.add(new BasicEvaluation(2, new Date(), restaurant, true, "1.2.3.5"));
        evaluations1.add(new BasicEvaluation(3, new Date(), restaurant, false, "1.2.3.6"));

        CompleteEvaluation ce = new CompleteEvaluation(1, new Date(), restaurant, "Génial !", "Toto");

        // LazyLoad --> Créer les grades dans un set temporaire
        Set<Grade> grades1 = new LinkedHashSet<>();
        grades1.add(new Grade(1, 4, ce, critService));
        grades1.add(new Grade(2, 5, ce, critCuisine));
        grades1.add(new Grade(3, 4, ce, critCadre));
        ce.setGrades(grades1);

        evaluations1.add(ce);

        CompleteEvaluation ce2 = new CompleteEvaluation(2, new Date(), restaurant, "Très bon", "Titi");

        Set<Grade> grades2 = new LinkedHashSet<>();
        grades2.add(new Grade(4, 4, ce2, critService));
        grades2.add(new Grade(5, 4, ce2, critCuisine));
        grades2.add(new Grade(6, 4, ce2, critCadre));
        ce2.setGrades(grades2);

        evaluations1.add(ce2);

        // LazyLoad : Utiliser setEvaluations au lieu de getEvaluations().add()
        restaurant.setEvaluations(evaluations1);

        restaurants.add(restaurant);

        Restaurant restaurant2 = director.constructCompleteRestaurant(
                2,
                "La Maison du Prussien",
                "Restaurant gastronomique renommé de Neuchâtel",
                "www.hotel-prussien.ch/‎",
                "Rue des Tunnels 11",
                city,
                typeGastro
        );

        // LazyLoad --> Créer d'abord les évaluations dans un Set temporaire
        Set<Evaluation> evaluations2 = new LinkedHashSet<>();
        evaluations2.add(new BasicEvaluation(4, new Date(), restaurant2, true, "1.2.3.7"));
        evaluations2.add(new BasicEvaluation(5, new Date(), restaurant2, true, "1.2.3.8"));
        evaluations2.add(new BasicEvaluation(6, new Date(), restaurant2, true, "1.2.3.9"));

        CompleteEvaluation ce3 = new CompleteEvaluation(3, new Date(), restaurant2, "Un régal !", "Dupont");
        Set<Grade> grades3 = new LinkedHashSet<>();
        grades3.add(new Grade(7, 5, ce3, critService));
        grades3.add(new Grade(8, 5, ce3, critCuisine));
        grades3.add(new Grade(9, 5, ce3, critCadre));
        ce3.setGrades(grades3);
        evaluations2.add(ce3);

        CompleteEvaluation ce4 = new CompleteEvaluation(4, new Date(), restaurant2, "Rien à dire, le top !", "Dupasquier");
        Set<Grade> grades4 = new LinkedHashSet<>();
        grades4.add(new Grade(10, 5, ce4, critService));
        grades4.add(new Grade(11, 5, ce4, critCuisine));
        grades4.add(new Grade(12, 5, ce4, critCadre));
        ce4.setGrades(grades4);
        evaluations2.add(ce4);

        restaurant2.setEvaluations(evaluations2);

        restaurants.add(restaurant2);
    }

    public static Set<Restaurant> getAllRestaurants() {
        if (!initDone) {
            init();
        }

        return restaurants;
    }

    public static Set<EvaluationCriteria> getEvaluationCriterias() {
        if (!initDone) {
            init();
        }

        return criterias;
    }

    public static Set<RestaurantType> getRestaurantTypes() {
        if (!initDone) {
            init();
        }

        return types;
    }

    public static Set<City> getCities() {
        if (!initDone) {
            init();
        }

        return cities;
    }

}
