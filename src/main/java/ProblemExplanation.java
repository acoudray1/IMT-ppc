import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
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
    protected Solver solver;
    protected boolean isDone;

    /**
     * ProblemExplanation constructor
     *
     * @param m
     */
    public ProblemExplanation(Model m, IntVar[][] attributes) {
        this.model = m;
        this.attr = attributes;
        this.solver = model.getSolver();
        this.isDone = false;
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
            this.solver.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println(this.model.toString());

        // Récupération des variables instanciées par colonnes
        /*for (int i =  0; i < this.attr.length ; i++) {
            for(int j =  0 ; j < this.attr[i].length ; j++) {

            }
        }*/

        this.recursiveSearch(0, 0);

        //System.out.println(this.model.toString());
        // faire le propager() en récursif pour tester l'ensemble des solutions avant de valider le modele
        // et donc pouvoir retourner en arrière sur un état ancien
        // tester en récursif

        // sauvegarder un modele : Solver.moveForward() && Solver.moveBackward()
        // -> sauvegarde d'un etat (wordPush()) avant d'appliquer une modification grâce à la méthode moveForward()
        // -> restauration d'un état (wordPop())

        // voir objet environnement du modèle - model.getEnvironment().wordPush()
    }

    private void recursiveSearch(int i, int j) {
        System.out.println("----- debut -----");
        System.out.println("recursive: " + i + " ; " + j);
        System.out.println("current world index: (before push) " + this.model.getEnvironment().getWorldIndex());
        // sauvegarde de l'état avant propagation
        this.model.getEnvironment().worldPush();

        Iterator it = this.attr[i][j].iterator();

        while (it.hasNext() && !isDone) {
            Integer priceIndex = (Integer) it.next();
            System.out.println("priceIndex: " + priceIndex + " ; hasNext? " + it.hasNext());
            Constraint cst = this.attr[i][j].eq(priceIndex).decompose();
            model.post(cst);
            System.out.println("contrainte: " + cst.toString());
            try {
                this.solver.propagate();
                System.out.println("contraint ok");
                if (this.solver.isSearchCompleted()) {
                    System.out.println("isCompleted");
                    this.isDone = true;
                } else {
                    int x = i;
                    int y = j;
                    if (j < this.attr[i].length) {
                        y = j + 1;
                    } else {
                        y = 0;
                        x = i + 1;
                    }
                    System.out.println("avance - " + x + " ; " + y);
                    this.recursiveSearch(x, y);
                    if (!isDone) {
                        // retour a l'etat sauvegarder
                        this.model.getEnvironment().worldPop();
                        System.out.println("current world index: (!isDone) " + this.model.getEnvironment().getWorldIndex());
                        System.out.println("recule - " + i + " ; " + j);
                    }
                }
            } catch (ContradictionException e) {
                System.out.println("contraint failed");
                e.printStackTrace();
                model.unpost(cst);
                // retour a l'etat sauvegarder
                this.model.getEnvironment().worldPop();
                this.model.getEnvironment().worldPush();
                System.out.println("current world index: (after pop & push) " + this.model.getEnvironment().getWorldIndex());
            }
        }

        if (!isDone) {
            // retour a l'etat sauvegarder
            this.model.getEnvironment().worldPop();
            System.out.println("current world index: (!isDone) " + this.model.getEnvironment().getWorldIndex());
            System.out.println("recule - " + i + " ; " + j);
        }

        System.out.println("----- fin -----");
    }

    private void candidateExplanation() {

    }

    private void minExplanation() {

    }
}
