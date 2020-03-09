package fr.umlv.conc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Quorum2 {

	/**
	 * Toujours final sinon une thread peut le voir le champ null - Le constructeur
	 * va démarrer ! Le champ est visible que si il est final
	 */
	private final ReentrantLock rLock = new ReentrantLock();
	private Condition cond = rLock.newCondition();
	private int nbVotes;
	private int vote1;
	private int vote2;
	private int winner;

	public Quorum2() {
		this.nbVotes = 0;
		this.vote1 = 0;
		this.vote2 = 0;
		this.winner = 0;
	}

	/*
	 * Cette classe doit être thread-safe. Qu'est qui doit être thread-safe ? Quels
	 * etats on va mettre ? Plusieurs cas peuvent arriver Combien de champs ? Au
	 * moins de deux champs pour comparer les valeurs (2 cases mémoires) Soit on
	 * stocke le gagnant dans une variable ou alors on considère que vote1 est le
	 * gagnant 2. on doit gérer les interrupted exceptions
	 */
	public int vote(int value) throws IllegalStateException {

		rLock.lock();
		try { // un seul block car on deux fois des wait / evite de le traiter deux fois
			switch (nbVotes) {
			case 0:
				nbVotes++;
				vote1 = value;
				while (nbVotes < 3) { // si on en un que 2 qui ont voté on a fini
					cond.await(); // on rend le lock avant de se dé-schéduler
				}
				break; // end switch
			case 1:
				if (value == this.vote1) {
					winner = value;
					cond.signal(); // 1 seul thread est "endormi"
					this.nbVotes = 3;
					return winner;
				}
				nbVotes++;
				vote2 = value;
				while (nbVotes < 3) {
					cond.signal();
				}
				break;
			case 2:
				if (value == vote1 || value == vote2) {
					winner = value; // Combien de threads en attente ? 2.
					cond.signalAll();
					nbVotes = 3;
					return winner;
				}
				nbVotes = 4; // on créer un autre cas d'erreur
				cond.signalAll(); // Reveille tous les autres threads
				throw new IllegalStateException(); // C'est la troisème thread qui léve l'ISE si l'instruction est
													// seule donc il faut reveiller les autres
			default:
				// on met rien pour sortir du switch
			}

			if (nbVotes == 4) { // cas d'erreur
				throw new IllegalStateException();
			}
			return winner; // Cela signifie que les autres threads ont déja calculés (on revient)

		} catch (InterruptedException e) {
			nbVotes = 4;
			cond.signalAll(); // Reveiller tous les threads en attente pour 'planter'
			throw new IllegalStateException();
		} finally {
			rLock.unlock();
		}
	}
}