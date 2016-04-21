#NAME: JAYALAKSHMI SURESHKUMAR
#USERNAME: jsureshk

'''
This file contains support code for B551 Hw6                                 # File version:  November 19, 2015                                            #

For questions related to genetic algorithms or the knapsack problem, any AI can be of help. For questions related to the support code itself, contact Alex at aseewald@indiana.edu.
'''
import bisect
import math
import random
import pickle
import numpy as np
import pandas as pd
from scipy.stats import norm

def removeItem(volumes):
    '''
    Removes item from a bag randomly given a set of volumes in the bag
    '''
    itemsInBag = []
    for i in range(0, len(volumes)):
        if(volumes[i] != 0):
            itemsInBag.append(i)
    index = random.randint(0,len(itemsInBag)-1)
    return itemsInBag[index]


def fitness(max_volume,volumes,prices):
    '''
    This should return a scalar which is to be maximized.
    max_volume is the maximum volume that the knapsack can contain.
    volumes is a list containing the volume of each item in the knapsack.
    prices is a list containing the price of each item in the knapsack, which is aligned with 'volumes'.

    Returns volume and the value to be maximized. Value returned so that the chromosome from which item 
    is taken out to balance the volume is updated.

    '''
    sumOfVolume = 0
    sumOfPrice = 0
    for i in range(0, len(volumes)):
        sumOfVolume += volumes[i]
        sumOfPrice += prices[i]

    while(sumOfVolume > max_volume):
        item = removeItem(volumes)
        sumOfVolume = sumOfVolume - volumes[item]
        sumOfPrice = sumOfPrice - prices[item]
        volumes[item] = 0
        prices[item] = 0
    return volumes, sumOfPrice


def randomSelection(population,fitnesses):
    '''
    This should return a single chromosome from the population. The selection process should be random, but with weighted probabilities proportional
    to the corresponding 'fitnesses' values.

    Selects based on weighted probability given by the fitness of the chromosome. 
    '''
    probChromosomes = []
    sumOfFitnesses = sum(fitnesses)
    for x in fitnesses:
	if(sumOfFitnesses!=0):
            probability = float(x)/float(sumOfFitnesses)
        else:
            probability = 1/len(fitnesses) #this is to handle divide by 0 error in case all chromosomes are empty bags.
        probChromosomes.append(probability)
    r = random.random()
    index = 0
    while(r >= 0 and index < len(probChromosomes)):
        r -= probChromosomes[index]
        index += 1
    return population[index - 1]


def reproduce(mom,dad):
    "This does genetic algorithm crossover. This takes two chromosomes, mom and dad, and returns two chromosomes."
    crossoverPoint = random.randint(1, len(mom))
    for i in range(crossoverPoint-1, len(mom)):
        temp = mom[i]
        mom[i] = dad[i]
        dad[i] = temp
    return mom, dad

def mutate(child, mutation_probability):
    "Takes a child, produces a mutated child."
    for i in range(0, len(child)):
        r = random.random()
	if(r<=mutation_probability):
            if child[i] == 1:
                child[i] = 0
            else:
                child[i] = 1
    return child

def compute_fitnesses(world,chromosomes):
    '''
    Takes an instance of the knapsack problem and a list of chromosomes and returns the fitness of these chromosomes, according to your 'fitness' function.
    Using this is by no means required, if you want to calculate the fitnesses in your own way, but having a single definition for this is convenient because
    (at least in my solution) it is necessary to calculate fitnesses at two distinct points in the loop (making a function abstraction desirable).

    Note, 'chromosomes' is required to be a 2D numpy array of boolean values (a fixed-size boolean array is the recommended encoding of a chromosome, and there should be multiple of these arrays, hence the matrix).
    
    Modified the code to handle changes in chromosome in case the bag exceeds maximum volume
    
    '''
    fitnesses = []
    for chromosome in chromosomes:
        volumes = []
        prices = []
        for i in range(0, len(world[1])):
            volumes.append(world[1][i] * chromosome[i])
            prices.append(world[2][i] * chromosome[i])
        volumes, value = fitness(world[0], volumes, prices)
        for i in range(0, len(volumes)):
            if(volumes[i]==0):
                chromosome[i] = 0
            else:
                chromosome[i] = 1
        fitnesses.append(value)
    return chromosomes, fitnesses


def initChromosomes(world, popsize):
    '''
    Initiates chromosomes with random samples
    '''

    chromosomes = []
    i = 0
    while( i < popsize):
        i+=1
        chromosome = []
        j = 0
        while(j < len(world[1])):
            j+=1
            if(random.randint(0,1) == 0):
                chromosome.append(0)
            else:
                chromosome.append(1)
        chromosomes.append(chromosome)
    return chromosomes



def genetic_algorithm(world,popsize,max_years,mutation_probability, elitism):
    '''
    world is a data structure describing the problem to be solved, which has a form like 'easy' or 'medium' as defined in the 'run' function.
    The other arguments to this function are what they sound like.
    genetic_algorithm *must* return a list of (chromosomes,fitnesses) tuples, where chromosomes is the current population of chromosomes, and fitnesses is
    the list of fitnesses of these chromosomes.

    Step1: initiates chromosomes and computes fitness
    Step2: Sorts the chromosomes based on fitnesses
    Step3: Just copies the current chromosome or performs crossover based on crossover ratio given as an input "elitism"
    Step4: If crossover, selects randomly two chromosomes based on the weighted probabilities from fitness of the chromosomes
    Step5: Performs crossover and mutation
    Step6: Adds the new chromosome and computes fitnesses and add that to the year_chromosomes which keeps all the chromosomes of all the years
    Step7: Go back to step 2 if the max years is not reached


    '''
    indices = []
    year_chromosomes = {}
    for x in range(0, popsize):
        indices.append(x)
    chromosomes = initChromosomes(world, popsize)
    chromosomes, fitnesses = compute_fitnesses(world, chromosomes)
    chromosomeFitness = (chromosomes, fitnesses)
    year_chromosomes[0] = chromosomeFitness
    i = 1
    while i < max_years:
        zippedArray = zip(year_chromosomes[i-1][1], indices)
        zippedArray.sort()
        sortedChromosomes = [x for y,x in zippedArray]
        sortedFitnesses = [y for y,x in zippedArray]
        j = 0
        chromosomes = []
        while(j<popsize):
            j+=1
            if(j<elitism*popsize):
                chromosomes.append(year_chromosomes[i-1][0][j-1])
            else:
                mom = randomSelection(sortedChromosomes, sortedFitnesses)
                dad = randomSelection(sortedChromosomes, sortedFitnesses)
                child1, child2 = reproduce(year_chromosomes[i-1][0][mom], year_chromosomes[i-1][0][dad])
                child1 = mutate(child1, mutation_probability)
		child2 = mutate(child2, mutation_probability)
                chromosomes.append(child1)
                if(j<popsize):
		    chromosomes.append(child2)
                    j+=1
        chromosomes, fitnesses = compute_fitnesses(world, chromosomes)
        chromosomeFitness = (chromosomes, fitnesses)
        year_chromosomes[i] = chromosomeFitness
	i+=1
    return year_chromosomes


def run(popsize,max_years,mutation_probability, elitism):
    '''
    The arguments to this function are what they sound like.
    Runs genetic_algorithm on various knapsack problem instances and keeps track of tabular information with this schema:
    DIFFICULTY YEAR HIGH_SCORE AVERAGE_SCORE BEST_PLAN

    Modified to include parameter elitism
    '''
    table = pd.DataFrame(columns=["DIFFICULTY", "YEAR", "HIGH_SCORE", "AVERAGE_SCORE", "BEST_PLAN"])
    sanity_check = (10, [10, 5, 8], [100,50,80])
    chromosomes = genetic_algorithm(sanity_check,popsize,max_years,mutation_probability, elitism)
    for year, data in enumerate(chromosomes):
	year_chromosomes, fitnesses = chromosomes[year]
        table = table.append({'DIFFICULTY' : 'sanity_check', 'YEAR' : year, 'HIGH_SCORE' : max(fitnesses),
            'AVERAGE_SCORE' : np.mean(fitnesses), 'BEST_PLAN' : year_chromosomes[np.argmax(fitnesses)]}, ignore_index=True)
    easy = (20, [20, 5, 15, 8, 13], [10, 4, 11, 2, 9] )
    chromosomes = genetic_algorithm(easy,popsize,max_years,mutation_probability, elitism)
    for year, data in enumerate(chromosomes):
        year_chromosomes, fitnesses = chromosomes[year]
        table = table.append({'DIFFICULTY' : 'easy', 'YEAR' : year, 'HIGH_SCORE' : max(fitnesses),
            'AVERAGE_SCORE' : np.mean(fitnesses), 'BEST_PLAN' : year_chromosomes[np.argmax(fitnesses)]}, ignore_index=True)
    medium = (100, [13, 19, 34, 1, 20, 4, 8, 24, 7, 18, 1, 31, 10, 23, 9, 27, 50, 6, 36, 9, 15],
                   [26, 7, 34, 8, 29, 3, 11, 33, 7, 23, 8, 25, 13, 5, 16, 35, 50, 9, 30, 13, 14])
    chromosomes = genetic_algorithm(medium,popsize,max_years,mutation_probability, elitism)
    for year, data in enumerate(chromosomes):
        year_chromosomes, fitnesses = chromosomes[year]
        table = table.append({'DIFFICULTY' : 'medium', 'YEAR' : year, 'HIGH_SCORE' : max(fitnesses),
            'AVERAGE_SCORE' : np.mean(fitnesses), 'BEST_PLAN' : year_chromosomes[np.argmax(fitnesses)]}, ignore_index=True)
    hard = (5000, norm.rvs(50,15,size=100), norm.rvs(200,60,size=100))
    chromosomes = genetic_algorithm(hard,popsize,max_years,mutation_probability, elitism)
    for year, data in enumerate(chromosomes):
        year_chromosomes, fitnesses = chromosomes[year]
        table = table.append({'DIFFICULTY' : 'hard', 'YEAR' : year, 'HIGH_SCORE' : max(fitnesses),
            'AVERAGE_SCORE' : np.mean(fitnesses), 'BEST_PLAN' : year_chromosomes[np.argmax(fitnesses)]}, ignore_index=True)
    for difficulty_group in ['sanity_check','easy','medium','hard']:
        group = table[table['DIFFICULTY'] == difficulty_group]
        bestrow = group.ix[group['HIGH_SCORE'].argmax()]
        print("Best year for difficulty {} is {} with high score {} and chromosome {}".format(difficulty_group,int(bestrow['YEAR']), bestrow['HIGH_SCORE'], bestrow['BEST_PLAN']))
    table.to_pickle("results.pkl") #saves the performance data, in case you want to refer to it later. pickled python objects can be loaded back at any later point.


def main():
    print "popsize = 10, max year = 10, mutation_probability = 0.01, elitism ratio = 0.15"
    run(10,10,0.01, 0.15)
    print "_______________________________________________________________________________"
    print "popsize = 10, max year = 20, mutation_probability = 0.1, elitism ratio = 0.15"
    run(10,20,0.1, 0.15)
    print "_______________________________________________________________________________"
    print "popsize = 20, max year = 20, mutation_probability = 0.1, elitism ratio = 0.15"
    run(20,20,0.1, 0.15)
    print "_______________________________________________________________________________"
    print "popsize = 30, max year = 20, mutation_probability = 0.1, elitism ratio = 0.15"
    run(30,20,0.1, 0.15)
    print "_______________________________________________________________________________"
    print "popsize = 40, max year = 20, mutation_probability = 0.1, elitism ratio = 0.15"
    run(40,20,0.1, 0.15)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 20, mutation_probability = 0.1, elitism ratio = 0.15"
    run(50,20,0.1, 0.15)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 20, mutation_probability = 0.1, elitism ratio = 0.25"
    run(50,20,0.1, 0.25)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 20, mutation_probability = 0.1, elitism ratio = 0.50"
    run(50,20,0.1, 0.50)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 30, mutation_probability = 0.1, elitism ratio = 0.50"
    run(50,30,0.1, 0.50)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 40, mutation_probability = 0.1, elitism ratio = 0.50"
    run(50,40,0.1, 0.50)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 50, mutation_probability = 0.1, elitism ratio = 0.50"
    run(50,50,0.1, 0.50)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 50, mutation_probability = 0.01, elitism ratio = 0.50"
    run(50,50,0.01, 0.50)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 50, mutation_probability = 0.001, elitism ratio = 0.50"
    run(50,50,0.001, 0.50)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 50, mutation_probability = 0.0001, elitism ratio = 0.50"
    run(50,50,0.0001, 0.50)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 50, mutation_probability = 0.0001, elitism ratio = 0.15"
    run(50,50,0.0001, 0.15)
    print "_______________________________________________________________________________"
    print "popsize = 50, max year = 50, mutation_probability = 0.0001, elitism ratio = 0"
    run(50,50,0.0001, 0)

if __name__ == "__main__":
    main()