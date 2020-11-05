import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ProblemExplanation {
    protected Model model;
    protected IntVar[][] attr;
    protected boolean isDone;
    protected ArrayList<Constraint> goodCstrs;
    protected Model candidateModel;
    protected IntVar[][] candidateAttr;
    protected HashMap<Constraint, ContradictionException> explanation;

    /**
     * ProblemExplanation constructor
     *
     * @param m
     * @param attributes
     */
    public ProblemExplanation(Model m, IntVar[][] attributes, Model m2, IntVar[][] attributes2) {
        this.model = m;
        this.attr = attributes;
        this.isDone = false;
        this.goodCstrs = new ArrayList<Constraint>();
        this.candidateModel = m2;
        this.candidateAttr = attributes2;
        this.explanation = new HashMap<Constraint, ContradictionException>();
    }

    /**
     * explain explique la résolution du problème
     */
    public void explain() {
        this.greedyExplanation();
        System.out.println(this.goodCstrs.toString());
        this.candidateExplanation();
        System.out.println(this.explanation.toString());
    }

    /**
     * greedyExplanation utilise la fonction recursiveSearch() pour boucler sur notre modele
     */
    private void greedyExplanation() {
        try {
            this.model.getSolver().propagate();
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
            Constraint candidateCst = this.candidateAttr[i][j].eq(priceIndex).decompose();
            this.model.post(cst);
            this.goodCstrs.add(candidateCst);
            System.out.println("contrainte: " + cst.toString());
            try {
                // sauvegarde de l'état avant propagation
                this.model.getEnvironment().worldPush();
                System.out.println("current world index: (after push) " + this.model.getEnvironment().getWorldIndex());

                this.model.getSolver().propagate();
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
                        this.model.unpost(cst);
                        this.goodCstrs.remove(candidateCst);
                    }
                }
            } catch (ContradictionException e) {
                // erreur lors de la propagation
                System.out.println("contraint failed");
                e.printStackTrace();
                // suppression de la contrainte si choix de la valeur fixée précédemment n'abouti à rien
                this.model.unpost(cst);
                this.goodCstrs.remove(candidateCst);
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
        ArrayList<Constraint> cstrToRemove = new ArrayList<Constraint>();
        try {
            this.candidateModel.getSolver().propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }

        System.out.println(this.candidateModel.toString());
        this.candidateModel.getEnvironment().worldPush();

        for(int i = 0; i < this.goodCstrs.size(); i++) {
            System.out.println("----- debut -----");
            Constraint goodCstr = this.goodCstrs.get(i);
            System.out.println("index: " + i + " ; goodConstraint: " + goodCstr);
            Constraint oppositeCstr = goodCstr.getOpposite();
            System.out.println("oppositeConstraint: " + oppositeCstr);
            this.candidateModel.post(oppositeCstr);

            try {
                this.candidateModel.getSolver().propagate();
                for (int j = i + 1; j < this.goodCstrs.size(); j++) {
                    Constraint goodCstr2 = this.goodCstrs.get(j);
                    System.out.println("jndex: " + j + " ; goodConstraint: " + goodCstr2);
                    cstrToRemove.add(goodCstr2);
                    this.candidateModel.post(goodCstr2);
                    this.candidateModel.getSolver().propagate();
                }
            } catch (ContradictionException e) {
                e.printStackTrace();
                // save de la contrainte bonne et de pq elle est bonne
                this.explanation.put(goodCstr, e);
                System.out.println("explanation: " + explanation);
                // suppression de toutes les mauvaises contraintes
                for (Constraint cstr: cstrToRemove) {
                    this.candidateModel.unpost(cstr);
                }
                cstrToRemove.clear();
                this.candidateModel.unpost(oppositeCstr);
                // retour a l'etat sauvegardé
                this.model.getEnvironment().worldPop();
                // ajout bonne contrainte
                this.candidateModel.post(goodCstr);
                // propag et push
                try {
                    this.candidateModel.getSolver().propagate();
                    this.candidateModel.getEnvironment().worldPush();
                } catch (ContradictionException contradictionException) {
                    // erreur ne devant pas arriver
                    contradictionException.printStackTrace();
                }
            }
            System.out.println("----- fin -----");
        }

        // hypothese 1 : on prend la contradiction, on part du principe qu'elle est vraie, on applique les autres
        //      contraintes connues une à une jusqu'à trouver une contradiction que l'on sauvegarde.
        // (implémentée)

        // hypothese 2 : on prend la contradiction, on part du principe qu'elle est vraie et on recherche les solutions
        //      possibles jusqu'à vérifier que tout est contradiction et qu'aucune solution n'est possible
    }

    /**
     * minExplanation
     */
    private void minExplanation() {

    }
}
