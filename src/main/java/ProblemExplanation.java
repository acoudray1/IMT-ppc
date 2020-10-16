import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.ArrayList;
import java.util.HashMap;
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
        try {
            this.greedyExplanation();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    /*
    Constraint fact1 = norge.ne(oj).decompose(); // définir un fait
    model.post(fact1); // l'ajouter au model
    model.unpost(fact1); // le retirer
     */


    private void greedyExplanation() throws ContradictionException {
        HashMap<Integer, ArrayList<IntVar>> listeEquivalence = new HashMap<Integer, ArrayList<IntVar>>();
        this.model.getSolver().propagate();
        System.out.println(this.model.toString());

        // Récupération des variables instanciées par maisons
        for (int i =  0; i < this.attr.length ; i++) {
            for(int j =  0 ; j < this.attr[i].length ; j++) {
                if (this.attr[i][j].isInstantiated()) {
                    if (!listeEquivalence.containsKey(this.attr[i][j].getValue())) {
                        listeEquivalence.put(this.attr[i][j].getValue(), new ArrayList<IntVar>());
                    }
                    listeEquivalence.get(this.attr[i][j].getValue()).add(this.attr[i][j]);
                }
            }
        }

        // Création des nouvelles contraintes
        for(Map.Entry<Integer, ArrayList<IntVar>> entry : listeEquivalence.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<IntVar> value = entry.getValue();
            System.out.println(key + " - " + value);
            for (int i =  0; i < value.size() ; i++) {
                IntVar cst1 = value.get(i);
                cst1.eq(key).post();
            }
        }
        System.out.println(this.model.toString());
        // regarder comment faire évoluer le propagate() en fonction des nouvelles contraintes créées
        // car la le propagate() n'avance pas même après création des nouvelles contraintes.
        this.model.getSolver().propagate();
        System.out.println(this.model.toString());
    }

    private void candidateExplanation() {

    }

    private void minExplanation() {

    }
}
