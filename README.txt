Pour lancer l'application, ouvrez une interface en ligne de commande dans le dossier courant et exécutez la commande "java -jar StateMachine.jar".
Alternativement, double-cliquez sur le script "launch.bat" pour le lancer (Windows uniquement)

L'application vous demandera alors de sélectionner une des méthodes de démonstration fournies en écrivant votre choix. 
Les entrées valides sont "smileys", "hours", "mails", "file", "action", "double", "compiler" "quit"

Smileys :
Un automate basique détectant des smileys, basé sur le premier exercice.


Hours :
Un automate détectant des heures valides, basé sur le deuxième exercice.

Mails :
Un automate détectant des adresses mails valides, basé sur le troisième exercice.

File :
Charge un automate décrit dans un fichier csv contenu dans le dossier "data". 
Ces automates sont décrits sous la forme d'une matrice de transitions dont les lignes et les colonnes sont les états et les caractères sont la transition entre l'état en abcisse et l'état en ordonnée.
La première ligne correspond à l'état initial tandis que la dernière correspond à l'état final.
ex:
===== 
a,b
 , 
=====
Cette matrice décrit un automate à deux états reconnaissant toute chaîne de caractères correspondant à l'expression régulière "a*b"
Le fichier "smileys.csv" déjà présent dans le dossier data est inspiré de l'automate de reconnaissance de smileys.

Action :
Cette automate est la version la plus basique d'un automate à actions. Un premier état stock la valeur 3 dans le tas d'une machine virtuelle.
Un deuxième état récupère cette valeur et l'affiche.

Double :
Un automate basique ayant deux états finaux, montrant que l'automate ne s'arrête pas au premier état final

Compiler :
Un automate reconnaissant et interprétant des assignations. Les règles sont les suivantes :
- les seules variables connues du programme sont "v", "x" et "y"
- s'il n'y a pas de point-virgule à la fin, l'assignation est reconnue mais pas exécutée
- une assignation de la forme "x=1;" donne directement à x la valeur du nombre
- une assignation de la forme "v=x+y;" effectue l'opération, assigne le résultat à v et affiche ce dernier.
- une assignation de la forme "v=x+y+x-y-x+y;" effectue toutes les opérations de la gauche vers la droite avant d'assigner le résultat à v. Le programme affiche également toutes les valeurs intermédiaires du calcul.
- tant que la dernière formule entrée a été reconnue comme une assignation, le programme recommence à demander une formule. Les valeurs assignées précédemment sont conservées tant que le programme ne revient pas au menu principal.


Quit: quitte le programme.