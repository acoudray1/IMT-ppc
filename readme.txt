solve :
avec or
             4            8            12           16           
Sauce        Puttanesca   The Other TypeMarinara     Arrabiata    
Pates        Rotini       Tagliolini   Capellini    Farfalle     
Personnes    Angie        Claudia      Damon        Elisa      

avec in
             4            8            12           16           
Sauce        Puttanesca   The Other TypeMarinara     Arrabiata    
Pates        Rotini       Tagliolini   Capellini    Farfalle     
Personnes    Angie        Claudia      Damon        Elisa     

------------------------------------------------------------------------------------

propagate :
avec or
             4            8            12           16           
Sauce        Puttanesca   Arrabiata    Marinara     null         
Pates        Rotini       Tagliolini   null         null         
Personnes    Angie        null         null         null   

avec in
             4            8            12           16           
Sauce        Puttanesca   Arrabiata    Marinara     null         
Pates        Rotini       Tagliolini   null         null         
Personnes    Angie        null         null         null 

------------------------------------------------------------------------------------
ProblemExplanation.greedyExplanation()

/*if (this.attr[i][j].isInstantiated()) {
                    if (!listeEquivalence.containsKey(this.attr[i][j].getValue())) {
                        listeEquivalence.put(this.attr[i][j].getValue(), new ArrayList<IntVar>());
                    }
                    listeEquivalence.get(this.attr[i][j].getValue()).add(this.attr[i][j]);
                }*/

// Création des nouvelles contraintes
        /*for(Map.Entry<Integer, ArrayList<IntVar>> entry : listeEquivalence.entrySet()) {
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
        System.out.println(this.model.toString());*/
        
        
        