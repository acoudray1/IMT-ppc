# Sensibilisation à la recherche - Programmation par contrainte

    classe : FIL A3
    groupe : CARREZ Robin - COUDRAY Axel
    date : Septembre - Novembre 2020
    
## Présentation du sujet et travail à faire
    
    La programmation par contrainte est un sous-domaine de l'intellignece artificielle dont l'objectif est de résoudre 
    automatiquement un problème décrit par l'utilisateur. Il s'agit de trouver unes solution qui satisfait l'ensemble 
    des règles (définies par les contraintes), cette solution peut également optimiser une fonction objectif. 
    
    Travail d'implémentation d'algorithmes de recherche permettant de résoudre et d'expliquer des problèmes complexes.
    Le travail est basé sur Choco-solver, un solveur de contraintes libre.
    
## Description du problème

    Le but du problème est de réussir à lier chaques personnes à un plat, un prix et un type de sauce en se basant sur 
    les contraintes suivantes : 
    
    1. The person who ordered capellini paid less than the person who chose arrabiata sauce
    2. The person who ordered tagliolini paid more than Angie
    3. The person who ordered tagliolini paid less than the person who chose marinara sauce
    4. Claudia did not choose puttanesca sauce
    5. The person who ordered rotini is either the person who paid $8 more than Damon or the person who paid $8 less than Damon
    6. The person who ordered capellini is either Damon or Claudia
    7. The person who chose arrabiata sauce is either Angie or Elisa
    8. The person who chose arrabiata sauce ordered farfalle
    
## Implémentation actuelle et Reste à faire

    Pour le moment notre solution comporte la résolution complète du problème ainsi que la recherche des contraintes ayant
    amenées à cette résolution.
    
    Il faut désormais réussir à implémenter l'explication des contraintes en langage humain afin de mieux comprendre ce qui a mené
    à cette solution.