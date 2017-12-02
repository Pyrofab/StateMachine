Pour lancer l'application, ouvrez une interface en ligne de commande dans le dossier courant et ex�cutez la commande "java -jar StateMachine.jar".
Alternativement, double-cliquez sur le script "launch.bat" pour le lancer (Windows uniquement)

L'application vous demandera alors de s�lectionner une des m�thodes de d�monstration fournies en �crivant votre choix. 
Les entr�es valides sont "smileys", "hours", "mails", "file", "action", "double", "compiler" "quit"

Smileys :
Un automate basique d�tectant des smileys, bas� sur le premier exercice.


Hours :
Un automate d�tectant des heures valides, bas� sur le deuxi�me exercice.

Mails :
Un automate d�tectant des adresses mails valides, bas� sur le troisi�me exercice.

File :
Charge un automate d�crit dans un fichier csv contenu dans le dossier "data". 
Ces automates sont d�crits sous la forme d'une matrice de transitions dont les lignes et les colonnes sont les �tats et les caract�res sont la transition entre l'�tat en abcisse et l'�tat en ordonn�e.
La premi�re ligne correspond � l'�tat initial tandis que la derni�re correspond � l'�tat final.
ex:
===== 
a,b
 , 
=====
Cette matrice d�crit un automate � deux �tats reconnaissant toute cha�ne de caract�res correspondant � l'expression r�guli�re "a*b"
Le fichier "smileys.csv" d�j� pr�sent dans le dossier data est inspir� de l'automate de reconnaissance de smileys.

Action :
Cette automate est la version la plus basique d'un automate � actions. Un premier �tat stock la valeur 3 dans le tas d'une machine virtuelle.
Un deuxi�me �tat r�cup�re cette valeur et l'affiche.

Double :
Un automate basique ayant deux �tats finaux, montrant que l'automate ne s'arr�te pas au premier �tat final

Compiler :
Un automate reconnaissant et interpr�tant des assignations. Les r�gles sont les suivantes :
- les seules variables connues du programme sont "v", "x" et "y"
- s'il n'y a pas de point-virgule � la fin, l'assignation est reconnue mais pas ex�cut�e
- une assignation de la forme "x=1;" donne directement � x la valeur du nombre
- une assignation de la forme "v=x+y;" effectue l'op�ration, assigne le r�sultat � v et affiche ce dernier.
- une assignation de la forme "v=x+y+x-y-x+y;" effectue toutes les op�rations de la gauche vers la droite avant d'assigner le r�sultat � v. Le programme affiche �galement toutes les valeurs interm�diaires du calcul.
- tant que la derni�re formule entr�e a �t� reconnue comme une assignation, le programme recommence � demander une formule. Les valeurs assign�es pr�c�demment sont conserv�es tant que le programme ne revient pas au menu principal.


Quit: quitte le programme.