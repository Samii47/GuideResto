package ch.hearc.ig.guideresto.business;

/**
 * @author cedric.baudet
 */

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CompleteEvaluation extends Evaluation {

    private String comment;
    private String username;
    private Set<Grade> grades = null;

    public CompleteEvaluation() {
        this(null, null, null, null);
    }

    public CompleteEvaluation(Date visitDate, Restaurant restaurant, String comment, String username) {
        this(null, visitDate, restaurant, comment, username);
    }

    public CompleteEvaluation(Integer id, Date visitDate, Restaurant restaurant, String comment, String username) {
        super(id, visitDate, restaurant);
        this.comment = comment;
        this.username = username;
        this.grades = null;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Grade> getGrades() {
        if (grades == null) {
            loadGrades();
        }
        return grades;
    }

    private void loadGrades() {
        System.out.println("Lazy Load : Chargement des notes de l'évaluation #" + this.getId());
        this.grades = new HashSet<>();
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }
}