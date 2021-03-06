#Jayalakshmi Sureshkumar
#Eihal Alowaisheq
#Tan-Hsun Weng

import numpy as np
import random
import math
import copy

conditional = {}
prior = {}

# calculate the prior
def priorFunc():
	for i in range(0, len(data)):
		if data[i][len(data[0]) - 1] in prior:
			prior[data[i][len(data[0]) - 1]] += 1
		else: 
			prior[data[i][len(data[0]) - 1]] = 1

# get possible values for each feature, it is generated by reading a descibtion file that contains 
#possible vlaues for each attribute 
def getPossibleValues(j):
    f = open('featureDesc','r')
    out = f.readlines()
    i = 0
    for line in out:
        if(i==j):
            possibleValues = line.split(",")
            possibleValues[-1] = possibleValues[-1].strip()
            for k in range(0, len(possibleValues)):
                possibleValues[k] = int(possibleValues[k])
            return possibleValues
        i+=1
    return possibleValues

def laplaceCorrection(posLables):
	# laplace for prior
	for k in  posLables:
		for j in range(0,len(data[0]) -1):
			posValue = list(getPossibleValues(j))
			for i in posValue:
				conditional[k][j][i]  = float(conditional[k][j][i] + 1) /float(prior[k] + len(posValue))  
	for i in prior:
		prior[i] = float(prior[i] + 1) /float(len(data) + len(prior))

	
def trainNB():
	priorFunc()
	posLabels = prior.keys() # get possible labels
	# construct a dictionary of two lists based on the label{0: list1, 1: list2}, each list is a 
	# list of dictionary in which each dictionary corresponds to each feature
	for i in  posLabels:
		listOfFeatures = []
		for j in range(0,len(data[0]) -1):
			posValue = getPossibleValues(j)
			tempDic = {}
			for k in  posValue:
				tempDic[k] = 0
			listOfFeatures.append(tempDic)
		conditional[i] = listOfFeatures

	# count positve and negative values for each feature
	for i in range(0, len(data)):
		for j in range(0,len(data[0]) -1):
			tempList = conditional[data[i][-1]]
			if data[i][j] in tempList[j]:
				conditional[data[i][-1]][j][data[i][j]] += 1
	laplaceCorrection(posLabels)

def testNB():
	uniqueLabel = prior.keys() # get possible labels 
	predicted =  []
	for i in test:
		maxPost = -float('inf')
		label = ""
		# calculating the poterior 
		for k in  uniqueLabel:
			post = 1
			for j in range(0,len(test[0]) -1):
				if i[j] in conditional[k][j]:
					post  = conditional[k][j][i[j]] * post 
			post = post * prior[k]
			if post > maxPost:
				maxPost = post
				label = k
		predicted.append(label)

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
		confusionMatrix[int (test[i][-1])+1][int(predicted[i])+1] = confusionMatrix[int (test[i][-1])+1][int(predicted[i])+1] + 1 
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

data = np.genfromtxt('zoo-train.csv', delimiter=',', dtype=None)
test = np.genfromtxt('zoo-test.csv', delimiter=',', dtype=None)
trainNB()
testNB()
