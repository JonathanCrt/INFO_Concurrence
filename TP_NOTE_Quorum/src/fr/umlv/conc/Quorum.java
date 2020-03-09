package fr.umlv.conc;

public class Quorum {

	/**
	 * Toujours final sinon une thread peut le voir le champ null - Le constructeur
	 * va d�marrer ! Le champ est visible que si il est final
	 */
	private final Object lock = new Object();
	private int nbVotes;
	private int vote1;
	private int vote2;
	private int winner;

	public Quorum() {
		this.nbVotes = 0;
		this.vote1 = 0;
		this.vote2 = 0;
		this.winner = 0;
	}

	/*
	 * Cette classe doit �tre thread-safe. Qu'est qui doit �tre thread-safe ? Quels
	 * etats on va mettre ? Plusieurs cas peuvent arriver Combien de champs ? Au
	 * moins de deux champs pour comparer les valeurs (2 cases m�moires) Soit on
	 * stocke le gagnant dans une variable ou alors on consid�re que vote1 est le
	 * gagnant 2. on doit g�rer les interrupted exceptions
	 */
	public int vote(int value) throws IllegalStateException {

		synchronized (lock) { // Car d�s qu'une valeur va changer par plusieurs threads = synchronized

			try { // un seul block car on deux fois des wait / evite de le traiter deux fois
				switch (nbVotes) {
				case 0:
					nbVotes++;
					vote1 = value;
					while (nbVotes < 3) { // si on en un que 2 qui ont vot� on a fini
						lock.wait(); // on rend le lock avant de se d�-sch�duler
					}
					break; // end switch
				case 1:
					if (value == this.vote1) {
						winner = value;
						lock.notify(); // 1 seul thread est "endormi"
						this.nbVotes = 3;
						return winner;
					}
					nbVotes++;
					vote2 = value;
					while (nbVotes < 3) {
						lock.wait();
					}
					break;
				case 2:
					if (value == vote1 || value == vote2) {
						winner = value; // Combien de threads en attente ? 2.
						lock.notifyAll();
						nbVotes = 3;
						return winner;
					}
					nbVotes = 4; // on cr�er un autre cas d'erreur
					lock.notifyAll(); // Reveille tous les autres threads
					throw new IllegalStateException(); // C'est la trois�me thread qui l�ve l'ISE si l'instruction est
														// seule donc il faut reveiller les autres
				default:
					// on met rien pour sortir du switch
				}

				if (nbVotes == 4) { //  cas d'erreur
					throw new IllegalStateException();
				}
				return winner; // Cela signifie que les autres threads ont d�ja calcul�s (on revient)

			} catch (InterruptedException e) {
				nbVotes = 4;
				lock.notifyAll(); // Reveiller tous les threads en attente pour 'planter'
				throw new IllegalStateException();
			}

		}
	}
}