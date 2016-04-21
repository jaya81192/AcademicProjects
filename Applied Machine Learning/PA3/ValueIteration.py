#Jayalakshmi Sureshkumar, Eihal Alowaisheq,Tan-Hsun Weng

import sys
class reinforcementLearning:

	#initiating environment variables and variables to learn 
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
        self.policyMatrix = [[0 for x in range(4)] for x in range(5)]
        self.values = [[0 for x in range(4)] for x in range(5)]
        self.policyMatrix[1][1] = "X"
        self.policyMatrix[1][3] = "X"
        self.values[1][1] = "XXXXXXXX"
        self.values[1][3] = "XXXXXXXX"

	#cumulatively calculates SumOf(Prob * ValueOfNextState) given the policy and current position	
    def computeNextStateValue(self, policy, i, j):
        total = 0
        for p in self.policyDic[policy].keys():
            val = self.values[i][j]
            if p == 1 and i != 0:
                if self.policyMatrix[i-1][j] != "X":
                    val = self.values[i-1][j]
            elif p == 2 and i != 4:
                if self.policyMatrix[i+1][j] != "X":
                    val = self.values[i+1][j]
            elif p == 3 and j != 0:
                if self.policyMatrix[i][j-1] != "X":
                    val = self.values[i][j-1]
            elif p == 4 and j != 3:
                if self.policyMatrix[i][j+1] != "X":
                    val = self.values[i][j+1]
            total = self.policyDic[policy][p] * val + total
        return total
	
	#the sequence of iteration based on policy; for left and down it goes from start of the grid to end; otherwise goes from end of grid to start
    def iIter(self, policy):
        if(policy == 1 or policy == 3):
            return 0, len(self.States), 1
        elif(policy == 2 or policy == 4):
            return len(self.States)-1, -1, -1
	#the sequence of iteration based on policy; for left and down it goes from start of the grid to end; otherwise goes from end of grid to start
    def jIter(self, policy):
        if(policy == 1 or policy == 3):
            return 0, len(self.States[0]), 1
        elif(policy == 2 or policy == 4):
            return len(self.States[0])-1, -1, -1
	
	#Value Iteration
    def RLValueIteration(self):
        b = 0
        conv = True #checks if the values have converged
        while conv: #till convergence
            for policy in self.policyDic: #trying out the four policies in one iteration
                for i in range(self.iIter(policy)[0],self.iIter(policy)[1], self.iIter(policy)[2] ):
                    for j in range(self.jIter(policy)[0], self.jIter(policy)[1], self.jIter(policy)[2]):
                        if(self.States[i][j] != "X"):
                            summedNextStateValue = self.computeNextStateValue(policy, i, j) #finds the value of next state
                            val = float(self.States[i][j]) + float(self.discounted * summedNextStateValue) #computing value of current state
                            if b == 0: #first iteration updates without checking if the value improves
                                self.values[i][j] = val
                                self.policyMatrix[i][j] = policy
                            else: #other iteration updates values and policy only if it improves the value of the state
                                if self.values[i][j] < val:
                                    self.values[i][j] = val
                                    if self.policyMatrix[i][j] != policy:
                                        self.policyMatrix[i][j] = policy
                                        conv = False #convergence is false if any of the value changes
                b+=1 
			
			#if convergence remains true for an entire iteration, breaks
            if conv == True:
                break
            else: #in case the convergence is false, it resets conv such as to go to new iteration
                conv = True
        self.convertPolicyMatrix()
	
	#resets policy values to policy descriptions
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
    RL = reinforcementLearning(reward)
    RL.RLValueIteration() #calling the value iteration function
    prettyPrint(RL.values) #printing values
    prettyPrint(RL.policyMatrix) #printing policies

if __name__ == "__main__":
    main()
