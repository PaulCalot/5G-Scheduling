Le fichier src est composée de :
	- Plusieurs classes de travail sur les fichiers:
		- Triplet : créé une instance d'un triplet. Champs = (n,k,m,p,r,x).
		- Instance : créé une instance du problème à partir du fichier text - champs = (N,K,M, listes de triplets).
		- Proprocessing : classe qui englobe les trois algorithmes de preprocessing sans les faire tourner. Champ = Instance
		- Processing : classe qui englobe les trois algorithmes de processing (greedy, DP, BB) sans les faire tourner . Champ = Instance.
	- D'une classe RunningClass.java, qui utilise les classes de travail et en affiche les résultats à l'aide des algorithmes des autres classes.
	- Une fichier python qui permet d'afficher les résultats du preprocessing. Il utilise les fichiers créés à l'aide de RunningClass.java (ces copiers sont déjà copiés au bon endroit pour le lancement du fichier python).

