package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.FakeItems;
import ch.hearc.ig.guideresto.business.RestaurantBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.beryx.textio.TextIO;

import org.beryx.textio.TextIoFactory;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.*;
import org.apache.commons.codec.*;
import ch.hearc.ig.userconsoleex5.Console;

/**
 * @author sami.cogur
 */
public class Application {
    private static TextIO textIO = TextIoFactory.getTextIO();


    public static void main(String[] args) {

        TextIO textIO = TextIoFactory.getTextIO();
        textIO.getTextTerminal().println("Bienvenue dans GuideResto !");
        int choice;
        do {
            choice = printMainMenu();
            proceedMainMenu(choice);
        } while (choice != 0);
        textIO.dispose();
    }

    /**
     * Affichage du menu principal de l'application
     */
    private static int printMainMenu() {
// instantiation de la librairie
        Console console = Console.getInstance();

        // Ajoute les options du menu

        console.setEntries(new TreeSet<>());

        console.setEntry("Afficher la liste de tous les restaurants");
        console.setEntry("Rechercher un restaurant par son nom");
        console.setEntry("Rechercher un restaurant par ville");
        console.setEntry("Saisir un restaurant par son type");
        console.setEntry("Saisir un nouveau restaurant");
        console.setEntry("Exporter la liste des restaurants en CSV");
        console.setEntry("Quitter l'application");

        TextIO textIO = TextIoFactory.getTextIO();
        String choice = textIO.newStringInputReader()
                .withNumberedPossibleValues(console.getEntries().toArray(new String[0]))
                .read("Veuillez choisir une option");

        textIO.getTextTerminal().println("Vous avez choisi : " + choice);

        // Convertir le texte en numéro (1-7)
        int index = 1;
        for (String entry : console.getEntries()) {
            if (entry.equals(choice)) {
                return index;
            }
            index++;
        }
        return 0;
    }

    /**
     * On gère le choix saisi par l'utilisateur
     *
     * @param choice Un nombre entre 0 et 5.
     */
    private static void proceedMainMenu(int choice) {
        switch (choice) {
            case 1:
                showRestaurantsList();
                break;
            case 2:
                exportRestaurantsToCSV("Restaurants.csv");
                break;
            case 3:
                textIO.getTextTerminal().println("Au revoir !");
                textIO.dispose();  // Ferme la fenêtre TextIO
                System.exit(0);    // Arrête complètement l'application
                break;
            case 4:
                searchRestaurantByName();
                break;
            case 5:
                searchRestaurantByCity();
                break;
            case 6:
                addNewRestaurant();
                break;
            case 7:
                searchRestaurantByType();
                break;
            default:
                textIO.getTextTerminal().println("Erreur : saisie incorrecte. Veuillez réessayer");
                break;
        }
    }

    private static List<Restaurant> restaurants = new ArrayList<>();

    private static void exportRestaurantsToCSV(String fileName) {

        try (FileWriter writer = new FileWriter("restaurants.csv"); /** try ouvre le fichier et ferme automatiquement à la fin, création du fichier csv */
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT /** csvPrinter provient de la librairie, CSVFormat.DEFAULT(sépare avec des ,) */
                .withDelimiter(';')
                .withHeader("ID", "Name", "Description", "Website", "Adresse", "Type"))) { /** en-têtes fixe */

            for (Restaurant r : FakeItems.getAllRestaurants()) { /** On récupère tous les restaurants depuis FakeItems.getAllRestaurants() */

                csvPrinter.printRecord( /** Chaque appel à printRecord() écrit une nouvelle ligne dans le fichier CSV avec les données du restaurant.*/
                        r.getId(),
                        r.getName(),
                        r.getDescription(),
                        r.getWebsite(),
                        r.getAddress(),
                        r.getType()
                );
            }


            textIO.getTextTerminal().println("Export CSV termine : restaurants.csv genere.");
        } catch (IOException e) {
            textIO.getTextTerminal().println("Erreur lors de l'export CSV : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * On affiche à l'utilisateur une liste de restaurants numérotés, et il doit en sélectionner un !
     *
     * @param restaurants Liste à afficher
     * @return L'instance du restaurant choisi par l'utilisateur
     */
    private static Restaurant pickRestaurant(Set<Restaurant> restaurants) {
        if (restaurants.isEmpty()) { // Si la liste est vide on s'arrête là
            textIO.getTextTerminal().println("Aucun restaurant n'a été trouvé !");
            return null;
        }

        String result;
        for (Restaurant currentRest : restaurants) {
            result = "";
            result = "\"" + result + currentRest.getName() + "\" - " + currentRest.getAddress().getStreet() + " - ";
            result = result + currentRest.getAddress().getCity().getZipCode() + " " + currentRest.getAddress().getCity().getCityName();
            textIO.getTextTerminal().println(result);
        }

        textIO.getTextTerminal().println("Veuillez saisiir le nom exact du restaurant dont vous voulez voir le détail, ou appuyez sur Enter pour revenir en arrière");
        String choice = readString();

        return searchRestaurantByName(restaurants, choice);
    }

    /**
     * Affiche la liste de tous les restaurants, sans filtre
     */
    private static void showRestaurantsList() {
        textIO.getTextTerminal().println("Liste des restaurants : ");

        Restaurant restaurant = pickRestaurant(FakeItems.getAllRestaurants());

        if (restaurant != null) { // Si l'utilisateur a choisi un restaurant, on l'affiche, sinon on ne fait rien et l'application va réafficher le menu principal
            showRestaurant(restaurant);
        }
    }

    /**
     * Affiche une liste de restaurants dont le nom contient une chaîne de caractères saisie par l'utilisateur
     */
    private static void searchRestaurantByName() {
        textIO.getTextTerminal().println("Veuillez entrer une partie du nom recherché : ");
        String research = readString();

        // Comme on ne peut pas faire de requête SQL avec la classe FakeItems, on trie les données manuellement.
        // Il est évident qu'une fois que vous utiliserez une base de données, il ne faut PAS garder ce système.
        Set<Restaurant> fullList = FakeItems.getAllRestaurants();
        Set<Restaurant> filteredList = new LinkedHashSet();

        for (Restaurant currentRestaurant : fullList) { // On parcourt la liste complète et on ajoute les restaurants correspondants à la liste filtrée.
            if (currentRestaurant.getName().toUpperCase().contains(research.toUpperCase())) { // On met tout en majuscules pour ne pas tenir compte de la casse
                filteredList.add(currentRestaurant);
            }
        }

        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Affiche une liste de restaurants dont le nom de la ville contient une chaîne de caractères saisie par l'utilisateur
     */
    private static void searchRestaurantByCity() {
        textIO.getTextTerminal().println("Veuillez entrer une partie du nom de la ville désirée : ");
        String research = readString();

        // Comme on ne peut pas faire de requête SQL avec la classe FakeItems, on trie les données manuellement.
        // Il est évident qu'une fois que vous utiliserez une base de données, il ne faut PAS garder ce système.
        Set<Restaurant> fullList = FakeItems.getAllRestaurants();
        Set<Restaurant> filteredList = new LinkedHashSet();

        for (Restaurant currentRestaurant : fullList) { // On parcourt la liste complète et on ajoute les restaurants correspondants à la liste filtrée.
            if (currentRestaurant.getAddress().getCity().getCityName().toUpperCase().contains(research.toUpperCase())) { // On met tout en majuscules pour ne pas tenir compte de la casse
                filteredList.add(currentRestaurant);
            }
        }

        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * L'utilisateur choisit une ville parmi celles présentes dans le système.
     *
     * @param cities La liste des villes à présnter à l'utilisateur
     * @return La ville sélectionnée, ou null si aucune ville n'a été choisie.
     */
    private static City pickCity(Set<City> cities) {
        textIO.getTextTerminal().println("Voici la liste des villes possibles, veuillez entrer le NPA de la ville désirée : ");

        for (City currentCity : cities) {
            textIO.getTextTerminal().println(currentCity.getZipCode() + " " + currentCity.getCityName());
        }
        textIO.getTextTerminal().println("Entrez \"NEW\" pour créer une nouvelle ville");
        String choice = readString();

        if (choice.equals("NEW")) {
            City city = new City();
            city.setId(1); // A modifier quand on a la connexion avec la BDD.
            textIO.getTextTerminal().println("Veuillez entrer le NPA de la nouvelle ville : ");
            city.setZipCode(readString());
            textIO.getTextTerminal().println("Veuillez entrer le nom de la nouvelle ville : ");
            city.setCityName(readString());
            FakeItems.getCities().add(city);
            return city;
        }

        return searchCityByZipCode(cities, choice);
    }

    /**
     * L'utilisateur choisit un type de restaurant parmis ceux présents dans le système.
     *
     * @param types La liste des types de restaurant à présnter à l'utilisateur
     * @return Le type sélectionné, ou null si aucun type n'a été choisi.
     */
    private static RestaurantType pickRestaurantType(Set<RestaurantType> types) {
        textIO.getTextTerminal().println("Voici la liste des types possibles, veuillez entrer le libellé exact du type désiré : ");
        for (RestaurantType currentType : types) {
            textIO.getTextTerminal().println("\"" + currentType.getLabel() + "\" : " + currentType.getDescription());
        }
        String choice = readString();

        return searchTypeByLabel(types, choice);
    }

    /**
     * L'utilisateur commence par sélectionner un type de restaurant, puis sélectionne un des restaurants proposés s'il y en a.
     * Si l'utilisateur sélectionne un restaurant, ce dernier lui sera affiché.
     */
    private static void searchRestaurantByType() {
        // Comme on ne peut pas faire de requête SQL avec la classe FakeItems, on trie les données manuellement.
        // Il est évident qu'une fois que vous utiliserez une base de données, il ne faut PAS garder ce système.
        Set<Restaurant> fullList = FakeItems.getAllRestaurants();
        Set<Restaurant> filteredList = new LinkedHashSet();

        RestaurantType chosenType = pickRestaurantType(FakeItems.getRestaurantTypes());

        if (chosenType != null) { // Si l'utilisateur a sélectionné un type, sinon on ne fait rien et la liste sera vide.
            for (Restaurant currentRestaurant : fullList) {
                if (currentRestaurant.getType() == chosenType) {
                    filteredList.add(currentRestaurant);
                }
            }
        }

        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Le programme demande les informations nécessaires à l'utilisateur puis crée un nouveau restaurant dans le système.
     */
    private static void addNewRestaurant() {
        textIO.getTextTerminal().println("Vous allez ajouter un nouveau restaurant !");
        textIO.getTextTerminal().println("Quel est son nom ?");
        String name = readString();
        textIO.getTextTerminal().println("Veuillez entrer une courte description : ");
        String description = readString();
        textIO.getTextTerminal().println("Veuillez entrer l'adresse de son site internet : ");
        String website = readString();
        textIO.getTextTerminal().println("Rue : ");
        String street = readString();
        City city = null;
        do
        { // La sélection d'une ville est obligatoire, donc l'opération se répètera tant qu'aucune ville n'est sélectionnée.
            city = pickCity(FakeItems.getCities());
        } while (city == null);
        RestaurantType restaurantType = null;
        do
        { // La sélection d'un type est obligatoire, donc l'opération se répètera tant qu'aucun type n'est sélectionné.
            restaurantType = pickRestaurantType(FakeItems.getRestaurantTypes());
        } while (restaurantType == null);

        // Création du Builder et du Director
        RestaurantBuilder builder = new RestaurantBuilder();
        RestaurantDirector director = new RestaurantDirector(builder);

        // Construction du restaurant via le Director
        Restaurant restaurant = director.constructCompleteRestaurant(
                1,
                name,
                description,
                website,
                street,
                city,
                restaurantType
        );

        FakeItems.getAllRestaurants().add(restaurant);

        showRestaurant(restaurant);
    }

    /**
     * Affiche toutes les informations du restaurant passé en paramètre, puis affiche le menu des actions disponibles sur ledit restaurant
     *
     * @param restaurant Le restaurant à afficher
     */
    private static void showRestaurant(Restaurant restaurant) {
        textIO.getTextTerminal().println("Affichage d'un restaurant : ");
        StringBuilder sb = new StringBuilder();
        sb.append(restaurant.getName()).append("\n");
        sb.append(restaurant.getDescription()).append("\n");
        sb.append(restaurant.getType().getLabel()).append("\n");
        sb.append(restaurant.getWebsite()).append("\n");
        sb.append(restaurant.getAddress().getStreet()).append(", ");
        sb.append(restaurant.getAddress().getCity().getZipCode()).append(" ").append(restaurant.getAddress().getCity().getCityName()).append("\n");
        sb.append("Nombre de likes : ").append(countLikes(restaurant.getEvaluations(), true)).append("\n");
        sb.append("Nombre de dislikes : ").append(countLikes(restaurant.getEvaluations(), false)).append("\n");
        sb.append("\nEvaluations reçues : ").append("\n");

        String text;
        for (Evaluation currentEval : restaurant.getEvaluations()) {
            text = getCompleteEvaluationDescription(currentEval);
            if (text != null) { // On va recevoir des null pour les BasicEvaluation donc on ne les traite pas !
                sb.append(text).append("\n");
            }
        }

        textIO.getTextTerminal().println(String.valueOf(sb));

        int choice;
        do { // Tant que l'utilisateur n'entre pas 0 ou 6, on lui propose à nouveau les actions
            showRestaurantMenu();
            choice = readInt();
            proceedRestaurantMenu(choice, restaurant);
        } while (choice != 0 && choice != 6); // 6 car le restaurant est alors supprimé...
    }

    /**
     * Parcourt la liste et compte le nombre d'évaluations basiques positives ou négatives en fonction du paramètre likeRestaurant
     *
     * @param evaluations    La liste des évaluations à parcourir
     * @param likeRestaurant Veut-on le nombre d'évaluations positives ou négatives ?
     * @return Le nombre d'évaluations positives ou négatives trouvées
     */
    private static int countLikes(Set<Evaluation> evaluations, Boolean likeRestaurant) {
        int count = 0;
        for (Evaluation currentEval : evaluations) {
            if (currentEval instanceof BasicEvaluation && ((BasicEvaluation) currentEval).getLikeRestaurant() == likeRestaurant) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retourne un String qui contient le détail complet d'une évaluation si elle est de type "CompleteEvaluation". Retourne null s'il s'agit d'une BasicEvaluation
     *
     * @param eval L'évaluation à afficher
     * @return Un String qui contient le détail complet d'une CompleteEvaluation, ou null s'il s'agit d'une BasicEvaluation
     */
    private static String getCompleteEvaluationDescription(Evaluation eval) {
        StringBuilder result = new StringBuilder();

        if (eval instanceof CompleteEvaluation) {
            CompleteEvaluation ce = (CompleteEvaluation) eval;
            result.append("Evaluation de : ").append(ce.getUsername()).append("\n");
            result.append("Commentaire : ").append(ce.getComment()).append("\n");
            for (Grade currentGrade : ce.getGrades()) {
                result.append(currentGrade.getCriteria().getName()).append(" : ").append(currentGrade.getGrade()).append("/5").append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Affiche dans la console un ensemble d'actions réalisables sur le restaurant actuellement sélectionné !
     */
    private static void showRestaurantMenu() {
        textIO.getTextTerminal().println("======================================================");
        textIO.getTextTerminal().println("Que souhaitez-vous faire ?");
        textIO.getTextTerminal().println("1. J'aime ce restaurant !");
        textIO.getTextTerminal().println("2. Je n'aime pas ce restaurant !");
        textIO.getTextTerminal().println("3. Faire une évaluation complète de ce restaurant !");
        textIO.getTextTerminal().println("4. Editer ce restaurant");
        textIO.getTextTerminal().println("5. Editer l'adresse du restaurant");
        textIO.getTextTerminal().println("6. Supprimer ce restaurant");
        textIO.getTextTerminal().println("0. Revenir au menu principal");
    }

    /**
     * Traite le choix saisi par l'utilisateur
     *
     * @param choice     Un numéro d'action, entre 0 et 6. Si le numéro ne se trouve pas dans cette plage, l'application ne fait rien et va réafficher le menu complet.
     * @param restaurant L'instance du restaurant sur lequel l'action doit être réalisée
     */
    private static void proceedRestaurantMenu(int choice, Restaurant restaurant) {
        switch (choice) {
            case 1:
                addBasicEvaluation(restaurant, true);
                break;
            case 2:
                addBasicEvaluation(restaurant, false);
                break;
            case 3:
                evaluateRestaurant(restaurant);
                break;
            case 4:
                editRestaurant(restaurant);
                break;
            case 5:
                editRestaurantAddress(restaurant);
                break;
            case 6:
                deleteRestaurant(restaurant);
                break;
            case 0:
                break;
            default:
                break;
        }
    }

    /**
     * Ajoute au restaurant passé en paramètre un like ou un dislike, en fonction du second paramètre.
     * L'IP locale de l'utilisateur est enregistrée. S'il s'agissait d'une application web, il serait préférable de récupérer l'adresse IP publique de l'utilisateur.
     *
     * @param restaurant Le restaurant qui est évalué
     * @param like       Est-ce un like ou un dislike ?
     */
    private static void addBasicEvaluation(Restaurant restaurant, Boolean like) {
        String ipAddress;
        try {
            ipAddress = Inet4Address.getLocalHost().toString(); // Permet de retrouver l'adresse IP locale de l'utilisateur.
        } catch (UnknownHostException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.INFO, "Error - Couldn't retreive host IP address");
            ipAddress = "Indisponible";
        }
        BasicEvaluation eval = new BasicEvaluation(1, new Date(), restaurant, like, ipAddress);
        restaurant.getEvaluations().add(eval);
        textIO.getTextTerminal().println("Votre vote a été pris en compte !");
    }

    /**
     * Crée une évaluation complète pour le restaurant. L'utilisateur doit saisir toutes les informations (dont un commentaire et quelques notes)
     *
     * @param restaurant Le restaurant à évaluer
     */
    private static void evaluateRestaurant(Restaurant restaurant) {
        textIO.getTextTerminal().println("Merci d'évaluer ce restaurant !");
        textIO.getTextTerminal().println("Quel est votre nom d'utilisateur ? ");
        String username = readString();
        textIO.getTextTerminal().println("Quel commentaire aimeriez-vous publier ?");
        String comment = readString();

        CompleteEvaluation eval = new CompleteEvaluation(1, new Date(), restaurant, comment, username);
        restaurant.getEvaluations().add(eval);

        Grade grade; // L'utilisateur va saisir une note pour chaque critère existant.
        textIO.getTextTerminal().println("Veuillez svp donner une note entre 1 et 5 pour chacun de ces critères : ");
        for (EvaluationCriteria currentCriteria : FakeItems.getEvaluationCriterias()) {
            textIO.getTextTerminal().println(currentCriteria.getName() + " : " + currentCriteria.getDescription());
            Integer note = readInt();
            grade = new Grade(1, note, eval, currentCriteria);
            eval.getGrades().add(grade);
        }

        textIO.getTextTerminal().println("Votre évaluation a bien été enregistrée, merci !");
    }

    /**
     * Force l'utilisateur à saisir à nouveau toutes les informations du restaurant (sauf la clé primaire) pour le mettre à jour.
     * Par soucis de simplicité, l'utilisateur doit tout resaisir.
     *
     * @param restaurant Le restaurant à modifier
     */
    private static void editRestaurant(Restaurant restaurant) {
        textIO.getTextTerminal().println("Edition d'un restaurant !");

        textIO.getTextTerminal().println("Nouveau nom : ");
        restaurant.setName(readString());
        textIO.getTextTerminal().println("Nouvelle description : ");
        restaurant.setDescription(readString());
        textIO.getTextTerminal().println("Nouveau site web : ");
        restaurant.setWebsite(readString());
        textIO.getTextTerminal().println("Nouveau type de restaurant : ");

        RestaurantType newType = pickRestaurantType(FakeItems.getRestaurantTypes());
        if (newType != null && newType != restaurant.getType()) {
            restaurant.getType().getRestaurants().remove(restaurant); // Il faut d'abord supprimer notre restaurant puisque le type va peut-être changer
            restaurant.setType(newType);
            newType.getRestaurants().add(restaurant);
        }

        textIO.getTextTerminal().println("Merci, le restaurant a bien été modifié !");
    }

    /**
     * Permet à l'utilisateur de mettre à jour l'adresse du restaurant.
     * Par soucis de simplicité, l'utilisateur doit tout resaisir.
     *
     * @param restaurant Le restaurant dont l'adresse doit être mise à jour.
     */
    private static void editRestaurantAddress(Restaurant restaurant) {
        textIO.getTextTerminal().println("Edition de l'adresse d'un restaurant !");

        textIO.getTextTerminal().println("Nouvelle rue : ");
        restaurant.getAddress().setStreet(readString());

        City newCity = pickCity(FakeItems.getCities());
        if (newCity != null && newCity != restaurant.getAddress().getCity()) {
            restaurant.getAddress().getCity().getRestaurants().remove(restaurant); // On supprime l'adresse de la ville
            restaurant.getAddress().setCity(newCity);
            newCity.getRestaurants().add(restaurant);
        }

        textIO.getTextTerminal().println("L'adresse a bien été modifiée ! Merci !");
    }

    /**
     * Après confirmation par l'utilisateur, supprime complètement le restaurant et toutes ses évaluations du référentiel.
     *
     * @param restaurant Le restaurant à supprimer.
     */
    private static void deleteRestaurant(Restaurant restaurant) {
        textIO.getTextTerminal().println("Etes-vous sûr de vouloir supprimer ce restaurant ? (O/n)");
        String choice = readString();
        if (choice.equals("o") || choice.equals("O")) {
            FakeItems.getAllRestaurants().remove(restaurant);
            restaurant.getAddress().getCity().getRestaurants().remove(restaurant);
            restaurant.getType().getRestaurants().remove(restaurant);
            textIO.getTextTerminal().println("Le restaurant a bien été supprimé !");
        }
    }

    /**
     * Recherche dans le Set le restaurant comportant le nom passé en paramètre.
     * Retourne null si le restaurant n'est pas trouvé.
     *
     * @param restaurants Set de restaurants
     * @param name        Nom du restaurant à rechercher
     * @return L'instance du restaurant ou null si pas trouvé
     */
    private static Restaurant searchRestaurantByName(Set<Restaurant> restaurants, String name) {
        for (Restaurant current : restaurants) {
            if (current.getName().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }

    /**
     * Recherche dans le Set la ville comportant le code NPA passé en paramètre.
     * Retourne null si la ville n'est pas trouvée
     *
     * @param cities  Set de villes
     * @param zipCode NPA de la ville à rechercher
     * @return L'instance de la ville ou null si pas trouvé
     */
    private static City searchCityByZipCode(Set<City> cities, String zipCode) {
        for (City current : cities) {
            if (current.getZipCode().equalsIgnoreCase(zipCode)) {
                return current;
            }
        }
        return null;
    }

    /**
     * Recherche dans le Set le type comportant le libellé passé en paramètre.
     * Retourne null si aucun type n'est trouvé.
     *
     * @param types Set de types de restaurant
     * @param label Libellé du type recherché
     * @return L'instance RestaurantType ou null si pas trouvé
     */
    private static RestaurantType searchTypeByLabel(Set<RestaurantType> types, String label) {
        for (RestaurantType current : types) {
            if (current.getLabel().equalsIgnoreCase(label)) {
                return current;
            }
        }
        return null;
    }

    /**
     * readInt ne repositionne pas le scanner au début d'une ligne donc il faut le faire manuellement sinon
     * des problèmes apparaissent quand on demande à l'utilisateur de saisir une chaîne de caractères.
     *
     * @return Un nombre entier saisi par l'utilisateur au clavier
     */
    private static int readInt() {
        TextIO textIO = TextIoFactory.getTextIO();
        return textIO.newIntInputReader().read();
    }

    /**
     * Méthode readString pour rester consistant avec readInt !
     *
     * @return Une chaîne de caractères saisie par l'utilisateur au clavier
     */
    private static String readString() {
        TextIO textIO = TextIoFactory.getTextIO();
        return textIO.newStringInputReader().read();
    }

}
