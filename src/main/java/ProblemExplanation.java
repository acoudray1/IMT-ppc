import org.chocosolver.solver.Model;

public class ProblemExplanation {
    protected Model model;

    /**
     * ProblemExplanation constructor
     *
     * @param m
     */
    public ProblemExplanation(Model m) {
        this.model = m;
    }

    public void greedyExplanation() {
        System.out.println(this.model.toString());
    }

    public void candidateExplanation() {

    }

    public void minExplanation() {

    }
}
