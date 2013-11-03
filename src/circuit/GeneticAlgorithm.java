package circuit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe que "implementa" o algoritmo genético
 */
public class GeneticAlgorithm {

	private static int DEFAULT_TIME = 5; //segundos
	private static boolean elitism = false;
	private static int defaultSize;

	private Individual elite;
	private Population pop;
	private float pcrossover;
	private float pmutate;
	private double time, startTime;
	Random r;

	/**
	 * Construtor
	 * 
	 * @param pop
	 *            uma população
	 * @param pcrossover
	 *            a probabilidade de crossover
	 * @param pmutate
	 *            a probabilidade de mutação
	 */
	GeneticAlgorithm(Population pop, float pcrossover, float pmutate) {
		this.pop = pop;
		this.pcrossover = pcrossover;
		this.pmutate = pmutate;
		elite = pop.getBestIndividual();
		time = DEFAULT_TIME;
		defaultSize = pop.getSize()/2;
		r= new Random();
	}

	/**
	 * Construtor 2
	 * 
	 * @param pop
	 *            uma população
	 * @param pcrossover
	 *            a probabilidade de crossover
	 * @param pmutate
	 *            a probabilidade de mutação
	 * @param tempo
	 *            limite
	 */
	GeneticAlgorithm(Population pop, float pcrossover, float pmutate, int time) {
		this.pop = pop;
		this.pcrossover = pcrossover;
		this.pmutate = pmutate;
		elite = pop.getBestIndividual();
		this.time = time;
	}

	/**
	 * Método que pesquisa e devolve o melhor indivíduo encontrado
	 * 
	 * @return pop.getBestIndividual(), o melhor indivíduo
	 */
	public Object[] search() {
		
		List<Double> bestFitness = new ArrayList<Double>();
		
		System.out.println("Crossover: "+pcrossover*100+"%");
		System.out.println("Mutation: "+pmutate*100+"%");
		System.out.println("Elitism: "+elitism);
		System.out.println("---------------------------------");
		System.out.println("Initial population");
		System.out.println(pop.toString());
		
		startTime = System.currentTimeMillis();
		do {
			Individual worstTmp = null;
			Population newPop = new Population();
			// se impar individuo ia faltar no for a seguir
			if (pop.getSize() % 2 != 0) {
				worstTmp =  pop.getWorstIndividual();
				pop.removeIndividual(worstTmp);
			}
			
			for (int i = 0; i < defaultSize; i++) {
				Individual x = pop.selectIndividual();
				pop.removeIndividual(x);
				Individual y = pop.selectIndividual();	
				pop.removeIndividual(y);
				Individual[] children = new Individual[2];
	
				// crossover probability
				if (r.nextFloat() <= pcrossover) {
					//System.out.println("before cross");
					children = x.crossover(y);
					//System.out.println("after cross");
				}
				else {
					children[0] = x;
					if (pop.getSize() % 2 == 0)children[1] = y;
				}

				// mutation probability
				if (r.nextFloat() <= pmutate)
					children[0].mutate();

				if (r.nextFloat() <= pmutate)
					children[1].mutate();

				newPop.addIndividual(children[0]); 												
				newPop.addIndividual(children[1]);
			}	
			pop = newPop;
			if(worstTmp!=null)
				pop.addIndividual(worstTmp);
			
			
			// Elitismo
			if (elitism) {

				if (elite.fitness() > pop.getBestIndividual().fitness()) {
					// REMOVER PIOR E METER ELITE, PORQUE ASSIM VAI ADICIONANDO 1 A CADA GERACAO
					pop.addIndividual(elite);
				}

				else
					elite = pop.getBestIndividual();
			}
			
			
		bestFitness.add(pop.getBestIndividual().fitness());
		} while (!done());
		System.out.println("Final population");
		System.out.println(pop.toString());
		System.out.println("---------------------------------");

		return new Object[] {pop.getBestIndividual(),pop.getWorstIndividual(),bestFitness};
	}
	
	/**
	 * Determina se o algoritmo acaba
	 * @return booleano, acaba ou nao
	 */
	private boolean done() {
		
		if((System.currentTimeMillis()-startTime)/1000 >= time)
			return true;
		
		return false;
	}

}
