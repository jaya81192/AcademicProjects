#Jayalakshmi Sureshkumar
#Eihal Alowaisheq
#Tan-Hsun Weng

import numpy as np
import random
import math
import copy
import sys

def checkConvergence(g, prevG):
	conv = True
	# print g , prevG
	for j in range(0,len(g)):
		gi = float("{0:.5f}".format(g[j]))
		prevGi = float("{0:.5f}".format(prevG[j]))
		if gi!= prevGi:
			conv = False
	return conv 

# calculate the sigmoid
def sigmoid (w, x):
	prod = 0
	for j in range (0, len(w)):
		prod = prod + int(x[j]) * w[j]
	sig = 1/(1 + math.exp(-prod))
	return sig

def trainLR(data, eta): 
	w = []
	g = []
	prvG = []
	for i in range(0,len(data[0]) -1):
		w.append(0)
		g.append(0)
		
	con = False 
	while not con:
		prvG = copy.deepcopy(g);
		for i in range(0,len(data)):
			# 1. calculate sigmoid
			p = sigmoid (w, data[i])

			# 2. calculate error
			error = int(data[i][-1])  - p

			# 3. gradient 
			for j in range(0,len(g)):
				g[j] =  g[j] + error * int(data[i][j])
			
			# 4. update weights 
			for j in range(0,len(g)):
				w[j] = w[j] + (eta * g[j])
		con = checkConvergence(g, prvG)
	return w

def testLR(test, w):
	predicted =  []
	uniqueLabel = set()

	for i in range(0,len(test)):
		predicted.append(round(sigmoid (w, test[i])))
		uniqueLabel.add(test[i][-1])
		
	confusionMatrix = []
	for i in range(0, len(uniqueLabel)+1):
		tempRow = []
		for j in range(0, len(uniqueLabel)+1):
			if i == 0:
				if j == 0:
					tempRow.append("")
				else:
					tempRow.append('PL'+ str(j))
			else: 
				if j == 0:
					tempRow.append('TL'+ str(i))
				else:
					tempRow.append(0)
		confusionMatrix.append(tempRow)

	misclassifed = 0

	for i in range (len(test)):
		confusionMatrix[int (test[i][-1])+1][int(predicted[i])+1] = confusionMatrix[int (test[i][-1])+1][int(predicted[i])+1] +1 
		# computing accuracy 
		if (test[i][-1] != predicted[i]):
			misclassifed = misclassifed + 1
	accuracy = 1 - (float (misclassifed) /float (len(test)))

	#doing a pretty print of the confusion matrix
	print 'The confusion matrix is'
	s = [[str(e) for e in row] for row in confusionMatrix]
	lens = [max(map(len,col)) for col in zip(*s)]
	fmt = '\t'.join('{{:{}}}'.format(x) for x in lens)
	table = [fmt.format(*row) for row in s]
	print '\n'.join(table)
	print "Accuracy: " , accuracy
	
def main():
    learningRate = sys.argv[1]
    eta = float(learningRate)
    data = np.genfromtxt('zoo-train.csv', delimiter=',', dtype=None)
    test = np.genfromtxt('zoo-test.csv', delimiter=',', dtype=None)
    #eta = 0.00005 #0.005 #0.002 # 0.0005 #0.001 
    w = trainLR(data, eta)
    print "Trained the model with learning rate :", eta
    testLR(test, w)

if __name__ == "__main__":
    main()

#http://aimotion.blogspot.com/2011/11/machine-learning-with-python-logistic.html
