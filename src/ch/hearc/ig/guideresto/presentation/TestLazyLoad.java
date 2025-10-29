package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.FakeItems;

public class TestLazyLoad {

    public static void main(String[] args) {
        System.out.println("=== DÉBUT DU TEST LAZY LOAD ===\n");

        // 1. Récupérer un restaurant (sans accéder aux évaluations)
        System.out.println("1. Récupération d'un restaurant...");
        Restaurant restaurant = FakeItems.getAllRestaurants().iterator().next();
        System.out.println("   Restaurant récupéré : " + restaurant.getName());
        System.out.println("   ⚠️ Pas encore de chargement des évaluations !\n");

        // 2. Accéder aux évaluations (déclenche le Lazy Load)
        System.out.println("2. Maintenant on accède aux évaluations...");
        int nbEval = restaurant.getEvaluations().size();
        System.out.println("   Nombre d'évaluations : " + nbEval);
        System.out.println("   ✅ Les évaluations ont été chargées !\n");

        // 3. Re-accéder aux évaluations (pas de rechargement)
        System.out.println("3. On accède à nouveau aux évaluations...");
        nbEval = restaurant.getEvaluations().size();
        System.out.println("   Nombre d'évaluations : " + nbEval);
        System.out.println("   ✅ Pas de rechargement (déjà en mémoire) !\n");

        // 4. Tester les grades d'une CompleteEvaluation
        System.out.println("4. Test des grades d'une évaluation complète...");
        for (Evaluation eval : restaurant.getEvaluations()) {
            if (eval instanceof CompleteEvaluation) {
                CompleteEvaluation ce = (CompleteEvaluation) eval;
                System.out.println("   Évaluation de : " + ce.getUsername());
                int nbGrades = ce.getGrades().size();
                System.out.println("   Nombre de notes : " + nbGrades);
                break;
            }
        }

        System.out.println("\n=== FIN DU TEST ===");
    }
}