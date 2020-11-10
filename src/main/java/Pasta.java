/*
 * This file is part of examples, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */

import org.chocosolver.examples.AbstractProblem;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

/**
 * Simple example which solve Zebra puzzle
 * <br/>
 *
 * @author GK
 * @since 29/01/19
 */
public class Pasta extends AbstractProblem {

    private final String[] sPrix = {"4", "8", "12", "16"};
    private final int SIZE = sPrix.length;
    private final int SAUCE = 0, PATES = 1, PERSONNES = 2;
    private final String [] sAttrTitle = {"Sauce", "Pates", "Personnes"};
    private final String [][] sAttr = {
            {"The Other Type", "Arrabiata", "Marinara", "Puttanesca"},
            {"Capellini", "Farfalle", "Tagliolini", "Rotini"},
            {"Elisa", "Claudia", "Damon", "Angie"}
    };
    private IntVar[][] attr;
    private IntVar[][] candidateAttr;
    private Model candidateModel;

    public void buildModel() {
        // first model building
        model = new Model();
        attr = model.intVarMatrix("attr", SIZE, SIZE, 1, SIZE);

        IntVar other   = attr[SAUCE][0];
        IntVar arra    = attr[SAUCE][1];
        IntVar mari    = attr[SAUCE][2];
        IntVar putta   = attr[SAUCE][3];

        IntVar cape    = attr[PATES][0];
        IntVar farfa   = attr[PATES][1];
        IntVar taglio  = attr[PATES][2];
        IntVar roti    = attr[PATES][3];

        IntVar elisa   = attr[PERSONNES][0];
        IntVar claudia = attr[PERSONNES][1];
        IntVar damon   = attr[PERSONNES][2];
        IntVar angie   = attr[PERSONNES][3];

        model.allDifferent(attr[PATES]).post();
        model.allDifferent(attr[SAUCE]).post();
        model.allDifferent(attr[PERSONNES]).post();


        cape.lt(arra).post();   // 1. The person who ordered capellini paid less than the person who chose arrabiata sauce
        taglio.gt(angie).post();    // 2. The person who ordered tagliolini paid more than Angie
        taglio.lt(mari).post(); // 3. The person who ordered tagliolini paid less than the person who chose marinara sauce
        claudia.ne(putta).post();   // 4. Claudia did not choose puttanesca sauce
        roti.dist(damon).eq(2).post();  // 5. The person who ordered rotini is either the person who paid $8 more than Damon or the person who paid $8 less than Damon
        cape.in(claudia, damon).post(); // 6. The person who ordered capellini is either Damon or Claudia
        arra.in(angie, elisa).post();   // 7. The person who chose arrabiata sauce is either Angie or Elisa
        arra.eq(farfa).post();  // 8. The person who chose arrabiata sauce ordered farfalle

        // second model building
        candidateModel = new Model();
        candidateAttr = candidateModel.intVarMatrix("candidateAttr", SIZE, SIZE, 1, SIZE);

        IntVar other2   = candidateAttr[SAUCE][0];
        IntVar arra2    = candidateAttr[SAUCE][1];
        IntVar mari2    = candidateAttr[SAUCE][2];
        IntVar putta2   = candidateAttr[SAUCE][3];

        IntVar cape2    = candidateAttr[PATES][0];
        IntVar farfa2   = candidateAttr[PATES][1];
        IntVar taglio2  = candidateAttr[PATES][2];
        IntVar roti2    = candidateAttr[PATES][3];

        IntVar elisa2   = candidateAttr[PERSONNES][0];
        IntVar claudia2 = candidateAttr[PERSONNES][1];
        IntVar damon2   = candidateAttr[PERSONNES][2];
        IntVar angie2   = candidateAttr[PERSONNES][3];

        candidateModel.allDifferent(candidateAttr[PATES]).post();
        candidateModel.allDifferent(candidateAttr[SAUCE]).post();
        candidateModel.allDifferent(candidateAttr[PERSONNES]).post();


        cape2.lt(arra2).post();   // 1. The person who ordered capellini paid less than the person who chose arrabiata sauce
        taglio2.gt(angie2).post();    // 2. The person who ordered tagliolini paid more than Angie
        taglio2.lt(mari2).post(); // 3. The person who ordered tagliolini paid less than the person who chose marinara sauce
        claudia2.ne(putta2).post();   // 4. Claudia did not choose puttanesca sauce
        roti2.dist(damon2).eq(2).post();  // 5. The person who ordered rotini is either the person who paid $8 more than Damon or the person who paid $8 less than Damon
        cape2.in(claudia2, damon2).post(); // 6. The person who ordered capellini is either Damon or Claudia
        arra2.in(angie2, elisa2).post();   // 7. The person who chose arrabiata sauce is either Angie or Elisa
        arra2.eq(farfa2).post();  // 8. The person who chose arrabiata sauce ordered farfalle
    }

    @Override
    public void configureSearch() {
    }

    public void solve() {
        ProblemExplanation pe = new ProblemExplanation(model, attr, candidateModel, candidateAttr);
        pe.explain();
        print(attr);
        print(candidateAttr);
    }

    private void print(IntVar[][] pos) {
        System.out.printf("%-13s%-13s%-13s%-13s%-13s%n", "",
                sPrix[0], sPrix[1], sPrix[2], sPrix[3]);
        for (int i = 0; i < SIZE-1; i++) {
            String[] sortedLine = new String[SIZE];
            for (int j = 0; j < SIZE; j++) {
                sortedLine[pos[i][j].getValue() - 1] = sAttr[i][j];
            }
            System.out.printf("%-13s", sAttrTitle[i]);
            for (int j = 0; j < SIZE; j++) {
                System.out.printf("%-13s", sortedLine[j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new Pasta().execute(args);
    }
}