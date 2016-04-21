#Jayalakshmi Sureshkumar, Eihal Alowaisheq,Tan-Hsun Weng

import sys
import math
import random
import decimal

class reinforcementLearning:
	#initiating rewards, policies and values; and the information about the environment
    def __init__(self, reward):
        self.States = [[reward for x in range(4)] for x in range(5)]
        self.States[3][1] = -50
        self.States[4][3] = 10
        self.States[1][1] = "X"
        self.States[1][3] = "X"
        self.policyDic = {1:{1:1},
                     2:{2:0.8, 3:0.2},
                     3:{3:1},
                     4:{4:0.8, 1:0.2}}
        self.discounted = 0.9
        self.p = 0.5
        self.alpha = 0.1
        self.values = [[{1:0, 2:0, 3:0, 4:0} for x in range(4)] for x in range(5)]
        self.values[1][1] = {1:"XXXX", 2:"XXXX", 3:"XXXX", 4:"XXXX"}
        self.values[1][3] = {1:"XXXX", 2:"XXXX", 3:"XXXX", 4:"XXXX"}
        self.policyMatrix = [[0 for x in range(4)] for x in range(5)]
        self.policyMatrix[1][1] = "X"
        self.policyMatrix[1][3] = "X"


	#given the probability of an action, it picks between the given action and the other action	
    def actionPicker(self, prob):
        numberOfActual = int(float(prob) * 10)
        numberOfOpposite = 10 - numberOfActual
        x = 0
        y = 0
        l = []
        for i in range(0, 10):
            randomNumber = random.randint(0,1)
            if(randomNumber == 0 and x != numberOfActual):
                l.append(0)
                x+=1
            elif(randomNumber == 1 and y!=numberOfOpposite):
                l.append(1)
                y+=1
            elif(x==numberOfActual):
                l.append(1)
                y+=1
            elif(y==numberOfOpposite):
                l.append(0)
                x+=1
        pickedAction = random.randint(0,9)
        return l[pickedAction]

	#returns the increased probability of exploitation
    def increaseExploitation(self, p1):
        p2 = float(1-p1)/float(2-p1) #reduction on 1-p
        p1 = float(1 - p2) #resultant value of p
        return p1 #returning p and changing p - p corresponds to exploit

	#for actions up and right, it picks the action given the probability of the action occuring	
    def getRandomAction(self, action):
        possibleActions = self.policyDic.get(action)
        i = 0
        otherAction = action
        currentActionProb = 0
        for keys in possibleActions:
            if i == 0:
                currentActionProb = possibleActions.get(keys)
                i+=1
            else:
                otherAction = keys
        isOtherAction = self.actionPicker(currentActionProb)
        if(isOtherAction == 1):
            return otherAction
        else:
            return action

	#finds the maximum value of the next state 		
    def maxNextState(self, i, j, action):
        if(action == 2 or action == 4):
            action = self.getRandomAction(action) #sees what action actually happens when actions are up and right
        if(action == 1):
            if(i!=0 and self.States[i-1][j] != "X"):
                i = i-1
        if(action == 2):
            if(i!= 4 and self.States[i+1][j] != "X"):
                i = i+1
        if(action == 3):
            if(j!=0 and self.States[i][j-1] != "X"):
                j = j-1
        if(action == 4):
            if(j!=3 and self.States[i][j+1] != "X"):
                j = j+1
        qVals = self.values[i][j]
        return self.findMax(qVals)[0]
	
	#given the q values of a state, it finds the maximum state action value and the action
    def findMax(self, qVals):
        val = -float('inf')
        maxAction = -1
        for key in qVals:
            if qVals[key] > val:
                val = qVals[key]
                maxAction = key
        return val, maxAction

	#Q Value function
    def RL(self):
        b = 0
        conv = True
        noOfConv = 0
        n = 0
		
		#checks if convergence have occured
        while conv:
            b+=1 #number of iterations
            for i in range(0, len(self.States)):
                for j in range(0, len(self.States[i])):
                    if self.States[i][j] != "X":
                        action = 0
                        explore = self.actionPicker(self.p) #picks between explore and exploit based on probability
                        if (explore == 1): #explore
                            action = random.randint(1,4) #picks a random action 
                        else: #exploit
                            action = self.findMax(self.values[i][j])[1] #picks the action with maximum value 
                        maxQOfNextState = self.maxNextState(i, j, action) #finds the maximum value of the next state 
						
						#computing the q value 
                        val = self.values[i][j][action] + self.alpha * (int(self.States[i][j]) + self.discounted * maxQOfNextState - self.values[i][j][action])
                        val = str(val)
						
						#checks if the policy changes to reset convergence variables
                        if self.policyMatrix[i][j] != action:
                            conv = False
                            noOfConv = 0	
                        self.values[i][j][action] = round(decimal.Decimal(val), 1) 
                        self.policyMatrix[i][j] = action
						
			#increase probability for exploitation
            if b%10 == 0:
                self.p = self.increaseExploitation(self.p)
				
			#checks if the current iteration converged	
            if conv == True:
                noOfConv+=1
				
				#breaks on 5 continuous convergence
                if noOfConv == 5:
                    break
            else:
                conv = True
        self.convertPolicyMatrix()

	#changing action values to action description	
    def convertPolicyMatrix(self):
        for i in range(0, len(self.policyMatrix)):
            for j in range(0, len(self.policyMatrix[0])):
                if self.policyMatrix[i][j] == 1:
                    self.policyMatrix[i][j] = "down"
                elif self.policyMatrix[i][j] == 2:
                    self.policyMatrix[i][j] = "up"
                elif self.policyMatrix[i][j] == 3:
                    self.policyMatrix[i][j] = "left"
                elif self.policyMatrix[i][j] == 4:
                    self.policyMatrix[i][j] = "right"

#pretty print for an array
def prettyPrint(array):
    for i in range(len(array)-1, -1, -1):
        for j in range(0, len(array[0])):
            sys.stdout.write(str(array[i][j]) + "\t")
        print " "

def main():
    reward = sys.argv[1]
    obj1 = reinforcementLearning(reward) 
    obj1.RL() #calling the q learning  function 

    for i in range(len(obj1.values)-1, -1, -1):
        for j in range(0, len(obj1.values[i])):
            sys.stdout.write("down:" + str(obj1.values[i][j][1]) + " up:" + str(obj1.values[i][j][2]) + " left:" + str(obj1.values[i][j][3]) + " right:" + str(obj1.values[i][j][4]) + "\t")
        print " "

if __name__ == "__main__":
    main()
