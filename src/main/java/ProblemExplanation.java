import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProblemExplanation {
    protected Model model;
    protected IntVar[][] attr;

    /**
     * ProblemExplanation constructor
     *
     * @param m
     */
    public ProblemExplanation(Model m, IntVar[][] attributes) {
        this.model = m;
        this.attr = attributes;
    }

    public void explain() {
        this.greedyExplanation();
    }

    /*
    Constraint fact1 = norge.ne(oj).decompose(); // définir un fait
    model.post(fact1); // l'ajouter au model
    model.unpost(fact1); // le retirer
     */


    private void greedyExplanation() {
        HashMap<Integer, ArrayList<IntVar>> listeEquivalence = new HashMap<Integer, ArrayList<IntVar>>();
        try {
            this.model.getSolver().propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println(this.model.toString());

        // Récupération des variables instanciées par colonnes
        for (int i =  0; i < this.attr.length ; i++) {
            for(int j =  0 ; j < this.attr[i].length ; j++) {
                Iterator it = this.attr[i][j].iterator();
                boolean isPropagated = false;
                while (it.hasNext() && !isPropagated) {
                    Integer priceIndex = (Integer) it.next();
                    Constraint cst = this.attr[i][j].eq(priceIndex).decompose();
                    model.post(cst);
                    System.out.println("contrainte: " + cst.toString());
                    try {
                        this.model.getSolver().propagate();
                        isPropagated = true;
                        System.out.println("(OK)");
                    } catch (ContradictionException e) {
                        e.printStackTrace();
                        model.unpost(cst);
                    }
                }
            }
        }
        System.out.println(this.model.toString());
        // faire le propager() en récursif pour tester l'ensemble des solutions avant de valider le modele
        // et donc pouvoir retourner en arrière sur un état ancien
        // tester en récursif

        // sauvegarder un modele : Solver.moveForward() && Solver.moveBackward()
        // -> sauvegarde d'un etat (wordPush()) avant d'appliquer une modification grâce à la méthode moveForward()
        // -> restauration d'un état (wordPop())

        // voir objet environnement du modèle - model.getEnvironment().wordPush()
    }

    private void candidateExplanation() {

    }

    private void minExplanation() {

    }
}
