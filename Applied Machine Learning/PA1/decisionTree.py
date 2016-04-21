#Jayalakshmi Sureshkumar
#Eihal Alowaisheq

import math
import sys

class node:
    def __init__(self, depth, splitfeatureavail, recordlist, splitFeature, splitFeatureValue):
        self.children = {} #child nodes for the current node 
        self.uniqueLabelsDict = {} #a dictionary with all possible label values and their count
        self.data = recordlist #the list with the tuples of training data in the current node
        self.depth = depth #remaining depth for split
        self.parentSplitFeature = splitFeature #the feature the parent of this node is split on
        self.parentSplitFeatureValue = splitFeatureValue #the value for the feature the parent of this node is split on - associated with this node
        self.listOfFeaturesRemaining = splitfeatureavail #remaining features for splitting the current node
        self.featureToSplit = "" #the feature selected to split the current node with 
        
        #splitting the node if split condition suffices.
        if self.depth != 0 and self.splitMore(): #stop condition
            self.featureToSplit = self.selectFeature() #calling the function that returns the feature best for split
            remainingFeatureList = []

            #for the children of current node - getting the list of feature except the feature going to be used for splitting
            for j in self.listOfFeaturesRemaining: 
                if j != self.featureToSplit:
                    remainingFeatureList.append(j)
            
            #for each of the feature value/children, creating a list of tuples
            for i in self.children: 
                tempdata = []
                for j in self.data:
                    if j[self.featureToSplit] == i:
                        tempdata.append(j)
                self.children[i] = node(self.depth-1, remainingFeatureList, tempdata, self.featureToSplit , i) #creating a child node for the current node

        #generating the list of possible labels and their counts at the current node
        for j in self.data:
            if(j[len(self.data[0])-1] in self.uniqueLabelsDict):
                self.uniqueLabelsDict[j[len(self.data[0])-1]]+=1
            else:
                self.uniqueLabelsDict[j[len(self.data[0])-1]]=1



        #bunch of print statements to illustrate the decision tree
        print '-------------------------------------------------------------------------'
        print 'Height: ', self.depth
        if self.parentSplitFeature == "":
            print 'Parent is split on feature: None'
        else:
            print 'Parent is split on feature: ', self.parentSplitFeature
        if self.parentSplitFeatureValue == "":
            print 'Value for the feature parent is split on: None'
        else:
            print 'Value for the feature parent is split on: ', self.parentSplitFeatureValue
        if self.featureToSplit == "":
            print 'Feature Split on: None'
        else:
            print 'Feature Split on: ', self.featureToSplit
        if(self.depth == 0):
            print 'Leaf Node Label counts: ', self.uniqueLabelsDict
            label = ""
            countOfLabel=0
            for i in self.uniqueLabelsDict:
                if self.uniqueLabelsDict[i] > countOfLabel:
                    label = i
                    countOfLabel = self.uniqueLabelsDict[i]
            print 'Leaf Node Label is: ', label
        print '-------------------------------------------------------------------------'
        



    #function checks if it is useful to split the current node into children. returns false if all the data at the node have the same label
    def splitMore(self):
        for key in self.uniqueLabelsDict:
            if self.uniqueLabelsDict[key] == len(self.data):
                return False
        return True


    #given a list of count distribution, it returns the entropy
    def calcEntropy(self, countList):
        entropy = 0
        for key in sorted(countList):
            prob=float(countList[key])/float(len(self.data))
            entropy = float(entropy) - float(prob) * math.log(prob,2)
        return entropy


    #selects the feature based on the information gain value
    def selectFeature(self):
        
        feature = ""
        maxInfoGain = 0


        #gets the count of all possible feature value to compute the probability of the feature in the data
        for i in range(0, len(self.data[0])-1):
            uniqueFeaturesCounts = {}
            for j in range(0, len(self.data)):
                if(self.data[j][i] in uniqueFeaturesCounts):
                    uniqueFeaturesCounts[self.data[j][i]]+=1
                else:
                    uniqueFeaturesCounts[self.data[j][i]]=1


            #to compute conditional entropy, taking the counts of labels with a specific feature value and adding it to the dict. eg. gets count of feature value 1 and label value 1. 
            #does so for possible combinations of feature and label
            condEntropy = 0
            for x in sorted(uniqueFeaturesCounts):
                featureLabel = {}
                for j in range(0, len(self.data)):
                    if(self.data[j][i] == x):
                        k = str(self.data[j][i]) +  str(self.data[j][len(self.data[0])-1])
                        if k not in featureLabel:
                            featureLabel[k]=1
                        else:
                            featureLabel[k]+=1


               
                entropy = self.calcEntropy(uniqueFeaturesCounts) #entropy for a particular feature value - eg H(Y|X=0)
                prob = float(uniqueFeaturesCounts[x])/float(len(self.data)) #getting probability eg P(X=0)
                condEntropy = float(condEntropy) + float(prob) * float(entropy) #adding to the cond entropy - H(Y|X) = P(X=0)H(Y|X=0) + P(X=1)H(Y|X=1) + ....
            myEntropy = self.calcEntropy(self.uniqueLabelsDict) #entropty of the current node
            infoGain = condEntropy - myEntropy #information gain - current node entropy - entropy after split on the feature.
            
            #to keep track of maximum info gain, to get the right feature with max info gain
            if infoGain > maxInfoGain: 
                feature = i
                maxInfoGain = infoGain
                self.children = uniqueFeaturesCounts
        return feature



#to predict the label of a tuple, given the tree and the tuple to be classified
def testClassify(record, node):
    #traverses to the leaf node
    while len(node.children) != 0:
        temp = node.children.keys()
        #checks if the feature value is available in the tree, in cases the test data has feature values which wasn't there in the training data
        if record[node.featureToSplit] in temp:
            node = node.children[record[node.featureToSplit]]
        else:
            break

    #checks the count of labels in the leaf node, and returns the label with the maximum count
    countOfLabel = 0
    label = ""
    for i in node.uniqueLabelsDict:
        if node.uniqueLabelsDict[i] > countOfLabel:
            label = i
            countOfLabel = node.uniqueLabelsDict[i]
    return label

def main():

    #getting filename, depth and test file name from the command line 
    filename = sys.argv[1] 
    d = sys.argv[2]
    testfilename = sys.argv[3]
    depth = int(d)
    
    f = open(filename,'r')
    out = f.readlines()
    data = []
    features = []
	
    #putting train data into a 2D list 
    for line in out:
        record = line.split(",")
        record[-1] = record[-1].strip()
        data.append(record)
    
    #getting the list of features
    for i in range(0,len(data[0])-1):
        features.append(i)

    #creating the tree into the root node
    root = node(depth, features, data, "", "")
    print "The tree is trained"

    
    f = open(testfilename,'r')
    out = f.readlines()
    misclassification = 0

    #generating an empty confusion matrix
    confusionMatrix = []
    for i in range(0, len(root.uniqueLabelsDict)+1):
        tempRow = []
        for j in range(0, len(root.uniqueLabelsDict)+1):
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


    #getting each row of the test file and calling the classify function
    for line in out:
        record = line.split(",")
        record[-1] = record[-1].strip()

        label = testClassify(record, root) #classifies the tuple 'record' - 'label' is the predicted label
        confusionMatrix[int(record[-1])][int(label)]+=1 #based on the predicted and true label, adds value to the confusion matrix.

        #increment misclassification if predicted label and true label aren't the same 
        if (label != record[-1]):
            misclassification+=1

    
    error = float(misclassification)/float(len(data)) * 100
    accuracy = 100 - float(error)
    #doing a pretty print of the confusion matrix
    print 'The confusion matrix is'
    s = [[str(e) for e in row] for row in confusionMatrix]
    lens = [max(map(len,col)) for col in zip(*s)]
    fmt = '\t'.join('{{:{}}}'.format(x) for x in lens)
    table = [fmt.format(*row) for row in s]
    print '\n'.join(table)

    print 'Depth of the tree', depth
    print 'Accuracy of classification', accuracy

if __name__ == "__main__":
    main()
