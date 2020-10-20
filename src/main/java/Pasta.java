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
    private IntVar zebra;

    public void buildModel() {

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

        IntVar elisa  = attr[PERSONNES][0];
        IntVar claudia    = attr[PERSONNES][1];
        IntVar damon   = attr[PERSONNES][2];
        IntVar angie   = attr[PERSONNES][3];

        model.allDifferent(attr[PATES]).post();
        model.allDifferent(attr[SAUCE]).post();
        model.allDifferent(attr[PERSONNES]).post();

        // 1. The person who ordered capellini paid less than the person who chose arrabiata sauce
        cape.lt(arra).post();

        // 2. The person who ordered tagliolini paid more than Angie
        taglio.gt(angie).post();

        // 3. The person who ordered tagliolini paid less than the person who chose marinara sauce
        taglio.lt(mari).post();

        // 4. Claudia did not choose puttanesca sauce
        claudia.ne(putta).post();

        // 5. The person who ordered rotini is either the person who paid $8 more than Damon or the person who paid $8 less than Damon
        roti.dist(damon).eq(2).post();

        // 6. The person who ordered capellini is either Damon or Claudia
        cape.in(claudia, damon).post();

        // 7. The person who chose arrabiata sauce is either Angie or Elisa
        arra.in(angie, elisa).post();

        // 8. The person who chose arrabiata sauce ordered farfalle
        arra.eq(farfa).post();
    }

    @Override
    public void configureSearch() {
    }

    public void solve() {
        ProblemExplanation pe = new ProblemExplanation(model, attr);
        pe.explain();
        print(attr);

        /*
        try {
            model.getSolver().propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------\n----------------------");
        System.out.println(model.toString());
        print(attr);
        */
        /*
        while (model.getSolver().solve()) {
            System.out.println("--------------------------\n----------------------");
            System.out.println(model.toString());
            print(attr);
        }
        */
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