import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class ProblemExplanation {
    protected Model model;
    protected IntVar[][] attr;
    protected boolean isDone;
    protected ArrayList<Constraint> goodCstrs;
    protected Model candidateModel;
    protected IntVar[][] candidateAttr;
    protected HashMap<Constraint, ArrayList<Constraint>> explanation;

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
        this.explanation = new HashMap<Constraint, ArrayList<Constraint>>();
    }

    /**
     * explain explique la résolution du problème
     */
    public void explain() {
        // resolution du pb
        this.greedyExplanation();

        // explication du pb
        this.candidateExplanation();
        this.printExplanation();
    }

    /**
     * greedyExplanation utilise la fonction recursiveSearch() pour boucler sur notre modele
     */
    private void greedyExplanation() {
        try {
            this.model.getSolver().propagate();
        } catch (ContradictionException e) {
            this.model.getSolver().getEngine().flush();
            e.printStackTrace();
        }
        // System.out.println(this.model.toString());
        this.model.getEnvironment().worldPush();
        // System.out.println("current world index: (before rec) " + this.model.getEnvironment().getWorldIndex());

        this.recursiveSearch(0, 0);
    }

    /**
     * recursiveSearch - récursion sur notre tableau d'attributs afin de trouver une solution au problème
     *
     * @param i
     * @param j
     */
    private void recursiveSearch(int i, int j) {
        // System.out.println("----- debut -----");
        // System.out.println("recursive: " + i + " ; " + j);
        // System.out.println("attributs: " + this.attr[i][j]);

        Iterator it = this.attr[i][j].iterator();

        // itération sur les valeurs possibles d'un attribut
        while (it.hasNext() && !isDone) {
            // System.out.println("-- iteration");
            Integer priceIndex = (Integer) it.next();
            // System.out.println("priceIndex: " + priceIndex + " ; hasNext? " + it.hasNext());

            // création de la contrainte fixant une valeur à notre attribut et ajout dans le modèle
            Constraint cst = this.attr[i][j].eq(priceIndex).decompose();
            Constraint candidateCst = this.candidateAttr[i][j].eq(priceIndex).decompose();
            this.model.post(cst);
            this.goodCstrs.add(candidateCst);
            // System.out.println("contrainte: " + cst.toString());
            try {
                // sauvegarde de l'état avant propagation
                this.model.getEnvironment().worldPush();
                // System.out.println("current world index: (after push) " + this.model.getEnvironment().getWorldIndex());

                this.model.getSolver().propagate();
                // System.out.println("contraint ok");

                // vérification de l'atteinte des limites du tableau (une valeur set sur chaque attribut et donc pb résolu)
                if (i == attr.length - 1 && j == attr[i].length - 1) {
                    // System.out.println("isCompleted");
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
                    // System.out.println("avance - " + x + " ; " + y);
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
                this.model.getSolver().getEngine().flush();
                // System.out.println("contraint failed");
                // e.printStackTrace();
                // suppression de la contrainte si choix de la valeur fixée précédemment n'abouti à rien
                this.model.unpost(cst);
                this.goodCstrs.remove(candidateCst);
                // retour a l'etat sauvegardé
                this.model.getEnvironment().worldPop();
                // System.out.println("current world index: (after pop) " + this.model.getEnvironment().getWorldIndex());
            }
        }

        if (!isDone) {
            // aucune des valeurs possibles pour l'attribut, retour au world initial et rebouclage sur les valeurs suivantes de notre attribut précédent
            // retour a l'etat sauvegardé
            this.model.getEnvironment().worldPop();
            // System.out.println("current world index: (!isDone) " + this.model.getEnvironment().getWorldIndex());
            // System.out.println("recule - " + i + " ; " + j);
        }

        // System.out.println("----- fin -----");
    }

    /**
     * candidateExplanation - on prend la contradiction, on part du principe qu'elle est vraie, on applique les autres
     *      contraintes connues une à une jusqu'à trouver une contradiction que l'on sauvegarde.
     *
     */
    private void candidateExplanation() {
        ArrayList<Constraint> cstrToRemove = new ArrayList<Constraint>();

        this.candidateModel.getEnvironment().worldPush();
        // System.out.println("current world index: (candidateExplanation first push) " + this.candidateModel.getEnvironment().getWorldIndex());

        try {
            this.candidateModel.getSolver().propagate();
        } catch (ContradictionException e) {
            this.candidateModel.getSolver().getEngine().flush();
            e.printStackTrace();
        }

        // System.out.println(this.candidateModel.toString());

        for(int i = 0; i < this.goodCstrs.size(); i++) {
            // System.out.println("----- debut -----");
            Constraint goodCstr = this.goodCstrs.get(i);
            // System.out.println("index: " + i + " ; goodConstraint: " + goodCstr);
            Constraint oppositeCstr = goodCstr.getOpposite();
            // System.out.println("oppositeConstraint: " + oppositeCstr);
            this.candidateModel.post(oppositeCstr);

            try {
                this.candidateModel.getSolver().propagate();
                for (int j = i + 1; j < this.goodCstrs.size(); j++) {
                    Constraint goodCstr2 = this.goodCstrs.get(j);
                    // System.out.println("jndex: " + j + " ; goodConstraint: " + goodCstr2);
                    cstrToRemove.add(goodCstr2);
                    this.candidateModel.post(goodCstr2);
                    this.candidateModel.getSolver().propagate();
                }
            } catch (ContradictionException e) {
                this.candidateModel.getSolver().getEngine().flush();
                //e.printStackTrace();
                // save de la contrainte bonne et de pq elle est bonne
                // explanation of pb
                this.explanation.put(goodCstr, this.mus(oppositeCstr));
                // System.out.println("explanation: " + explanation);
                // suppression de toutes les mauvaises contraintes
                for (Constraint cstr: cstrToRemove) {
                    this.candidateModel.unpost(cstr);
                }
                cstrToRemove.clear();
                this.candidateModel.unpost(oppositeCstr);
                // ajout bonne contrainte
                this.candidateModel.post(goodCstr);
            }
            // System.out.println("----- fin -----");
        }
        // propagate
        try {
            this.candidateModel.getSolver().propagate();
        } catch (ContradictionException contradictionException) {
            // erreur ne devant pas arriver
            this.candidateModel.getSolver().getEngine().flush();
            contradictionException.printStackTrace();
        }
    }

    /**
     * mus - recherche des contraintes créant notre contradiction
     *          On part de notre état où on a une contradiction
     *              -> on prend la liste des contraintes qui mènent à cet état
     *              -> on enlève les contraintes une à une pour ne garder que celles qui créent la contraction
     *                  (si on enlève ça change pas, on passe à la suivant, si quand on enlève il n'y a pu la contradiction on la
     *                  remet et on passe à la suivante) -> quand on a plus que les contraintes qui créent la contraction c'est fini
     *          Et on fait ça pour chaque contrainte que l'on rajoute à la main
     *
     * @param oppositeCstr - la contrainte à enlever à la fin
     *
     * @return mus - liste de contrainte permettant l'explication du pb
     */
    private ArrayList<Constraint> mus(Constraint oppositeCstr) {
        ArrayList<Constraint> mus = new ArrayList<Constraint>();
        this.candidateModel.getEnvironment().worldPop();
        // System.out.println("--- mus start ---");
        this.candidateModel.getEnvironment().worldPush();
        // System.out.println("current world index: (after push) " + this.candidateModel.getEnvironment().getWorldIndex());
        Constraint[] cstrs = this.candidateModel.getCstrs();

        for(Constraint cstr : cstrs) {
            // System.out.println("opCstr: " + oppositeCstr + "size: " + cstrs.length);
            this.candidateModel.unpost(cstr);
            try {
                this.candidateModel.getSolver().propagate();
                // si pas d'erreur
                mus.add(cstr);
                // System.out.println("cstr reinjected: " + cstr);
                this.candidateModel.getEnvironment().worldPop();
                // System.out.println("current world index: (after try pop) " + this.candidateModel.getEnvironment().getWorldIndex());
                this.candidateModel.post(cstr);
                this.candidateModel.getEnvironment().worldPush();
                // System.out.println("current world index: (after try push) " + this.candidateModel.getEnvironment().getWorldIndex());
            } catch (ContradictionException e) {
                this.candidateModel.getSolver().getEngine().flush();
                // System.out.println("cstr error: " + cstr);
                //e.printStackTrace();
            }
        }

        // reset
        this.candidateModel.getEnvironment().worldPop();
        this.candidateModel.getEnvironment().worldPush();
        for(Constraint cstr : cstrs) {
            if (!Arrays.asList(this.candidateModel.getCstrs()).contains(cstr)) {
                this.candidateModel.post(cstr);
            }
        }
        // System.out.println("current world index: (after pop & push) " + this.candidateModel.getEnvironment().getWorldIndex());

        // on supprime la contrainte inverse du mus car c'est elle qui nous permet de trouver l'explication sans réellement faire partie
        // de l'explication
        if (Arrays.asList(this.candidateModel.getCstrs()).contains(oppositeCstr)) {
            mus.remove(oppositeCstr);
        }
        return mus;
    }

    /**
     * printExplanation
     */
    private void printExplanation() {
        System.out.println("***** results *****");
        for (Constraint cstr : explanation.keySet()) {
            ArrayList<Constraint> cstrs = explanation.get(cstr);
            System.out.println("\n constraint: " + cstr + "\n explanations: ");
            for (int i = 0; i < cstrs.size(); i++) {
                System.out.println(i + ". " + cstrs.get(i));
            }
            System.out.println("-\n");
        }
    }

    /**
     * minExplanation
     */
    private void minExplanation() {

    }
}
