import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import java.util.Iterator;

public class ProblemExplanation {
    protected Model model;
    protected IntVar[][] attr;
    protected Solver solver;
    protected boolean isDone;

    /**
     * ProblemExplanation constructor
     *
     * @param m
     * @param attributes
     */
    public ProblemExplanation(Model m, IntVar[][] attributes) {
        this.model = m;
        this.attr = attributes;
        this.solver = model.getSolver();
        this.isDone = false;
    }

    /**
     * explain explique la résolution du problème
     */
    public void explain() {
        this.greedyExplanation();
    }

    /**
     * greedyExplanation utilise la fonction recursiveSearch() pour boucler sur notre modele
     */
    private void greedyExplanation() {
        try {
            this.solver.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println(this.model.toString());
        this.model.getEnvironment().worldPush();
        System.out.println("current world index: (before rec) " + this.model.getEnvironment().getWorldIndex());

        this.recursiveSearch(0, 0);
    }

    /**
     * recursiveSearch - récursion sur notre tableau d'attributs afin de trouver une solution au problème
     * @param i
     * @param j
     */
    private void recursiveSearch(int i, int j) {
        System.out.println("----- debut -----");
        System.out.println("recursive: " + i + " ; " + j);
        System.out.println("attributs: " + this.attr[i][j]);

        Iterator it = this.attr[i][j].iterator();

        // itération sur les valeurs possibles d'un attribut
        while (it.hasNext() && !isDone) {
            System.out.println("-- iteration");
            Integer priceIndex = (Integer) it.next();
            System.out.println("priceIndex: " + priceIndex + " ; hasNext? " + it.hasNext());

            // création de la contrainte fixant une valeur à notre attribut et ajout dans le modèle
            Constraint cst = this.attr[i][j].eq(priceIndex).decompose();
            model.post(cst);
            System.out.println("contrainte: " + cst.toString());
            try {
                // sauvegarde de l'état avant propagation
                this.model.getEnvironment().worldPush();
                System.out.println("current world index: (after push) " + this.model.getEnvironment().getWorldIndex());

                this.solver.propagate();
                System.out.println("contraint ok");

                // vérification de l'atteinte des limites du tableau (une valeur set sur chaque attribut et donc pb résolu)
                if (i == attr.length - 1 && j == attr[i].length - 1) {
                    System.out.println("isCompleted");
                    this.isDone = true;
                } else {
                    int x = i;
                    int y = j;
                    if (j < this.attr[i].length - 1) {
                        y = j + 1;
                    } else {
                        y = 0;
                        x = i + 1;
                    }
                    System.out.println("avance - " + x + " ; " + y);
                    // appel réccursif pour fixer une valeur sur le prochain attribut
                    this.recursiveSearch(x, y);
                    if (!isDone) {
                        // suppression de la contrainte si choix de la valeur fixée précédemment n'abouti à rien
                        model.unpost(cst);
                    }
                }
            } catch (ContradictionException e) {
                // erreur lors de la propagation
                System.out.println("contraint failed");
                e.printStackTrace();
                // suppression de la contrainte si choix de la valeur fixée précédemment n'abouti à rien
                model.unpost(cst);
                // retour a l'etat sauvegardé
                this.model.getEnvironment().worldPop();
                System.out.println("current world index: (after pop) " + this.model.getEnvironment().getWorldIndex());
            }
        }

        if (!isDone) {
            // aucune des valeurs possibles pour l'attribut, retour au world initial et rebouclage sur les valeurs suivantes de notre attribut précédent
            // retour a l'etat sauvegardé
            this.model.getEnvironment().worldPop();
            System.out.println("current world index: (!isDone) " + this.model.getEnvironment().getWorldIndex());
            System.out.println("recule - " + i + " ; " + j);
        }

        System.out.println("----- fin -----");
    }

    /**
     * candidateExplanation
     */
    private void candidateExplanation() {

    }

    /**
     * minExplanation
     */
    private void minExplanation() {

    }
}
