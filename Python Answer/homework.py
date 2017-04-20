#!/usr/bin/python
import sys

def readInputFile(fileName):
	file = open(fileName, 'r')
	returnVal = {}
	returnVal['n'] = int(file.readline().rstrip('\n').rstrip('\r'))
	returnVal['algo'] = file.readline().rstrip('\n').rstrip('\r')
	returnVal['myPlay'] = file.readline().rstrip('\n').rstrip('\r')
	returnVal['depth'] = int(file.readline().rstrip('\n').rstrip('\r'))

	i = 0
	cellValues = [[0 for x in range(returnVal['n'])] for y in range(returnVal['n'])]
	while i < returnVal['n']:
		cellValues[i] = map(int, file.readline().rstrip('\n').rstrip('\r').split(' '))
		i += 1

	i = 0
	cellPlay = [[0 for x in range(returnVal['n'])] for y in range(returnVal['n'])]
	while i < returnVal['n']:
		cellPlay[i] = list(file.readline().rstrip('\n').rstrip('\r'))
		i += 1

	returnVal['cellValues'] = cellValues
	returnVal['boardState'] = cellPlay
	file.close()
	return returnVal

def evalGameScore(myPlay, boardState, cellValues, n):
	#print "Eval score for : "+myPlay
	score = 0
	for i in range(n):
		for j in range(n):
			if boardState[i][j] == '.':
				continue
			elif boardState[i][j] == myPlay:
				score += cellValues[i][j]
			else:
				score -= cellValues[i][j]
	return score

def getOpponentMove(play):
	if play == 'X':
		return 'O'
	return 'X'

def isTerminalState(state, n):
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				return False
	return True

def decideNextMove(fileData):
	if fileData['algo'] == 'MINIMAX':
		return minimaxDecision(fileData)
	elif fileData['algo'] == 'ALPHABETA':
		return alphaBetaDecision(fileData)

def performStakeAtIJ(state, myPlay, i, j):
	nextState = [row[:] for row in state]	#make a copy of current state as the state is passed by reference
	nextState[i][j] = myPlay
	return nextState

def checkRaidPossibility(state, myPlay, i, j, n):
	if i-1 >= 0 and state[i-1][j] == myPlay:
		return True
	elif i+1 < n and state[i+1][j] == myPlay:
		return True
	elif j-1 >= 0 and state[i][j-1] == myPlay:
		return True
	elif j+1 < n and state[i][j+1] == myPlay:
		return True
	return False

def performRaidAtIJ(state, myPlay, i, j, n):
	nextState = [row[:] for row in state]	#make a copy of current state as the state is passed by reference
	if checkRaidPossibility(nextState, myPlay, i, j, n):
		nextState[i][j] = myPlay
		conquer = False
		if i-1 >= 0 and not nextState[i-1][j] == myPlay and not nextState[i-1][j] == '.':
			nextState[i-1][j] = myPlay
			conquer = True
		if i+1 < n and not nextState[i+1][j] == myPlay and not nextState[i+1][j] == '.':
			nextState[i+1][j] = myPlay
			conquer = True
		if j-1 >= 0 and not nextState[i][j-1] == myPlay and not nextState[i][j-1] == '.':
			nextState[i][j-1] = myPlay
			conquer = True
		if j+1 < n and not nextState[i][j+1] == myPlay and not nextState[i][j+1] == '.':
			nextState[i][j+1] = myPlay
			conquer = True
		if conquer:
			return nextState
		return conquer
	return False

def alphaBetaDecision(fileData):
	return maxValueAlphaBeta(fileData['boardState'], fileData['depth'], fileData['myPlay'], fileData['cellValues'], fileData['n'], -sys.maxint -1, sys.maxint)

def maxValueAlphaBeta(state, depth, myPlay, cellValues, n, alpha, beta):
	#print "In Max Value Alpha Beta :"
	#print "Entered in state : "+str(state)
	returnMaxValue = {}
	if depth == 0 or isTerminalState(state, n):	#add condition for game over
		#print "HEREeeeeeeeeeeeeeeeeeeeee"
		returnMaxValue['val'] = evalGameScore(myPlay, state, cellValues, n)
		returnMaxValue['state'] = state
		return returnMaxValue
	returnMaxValue['val'] = - sys.maxint - 1
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performStakeAtIJ(state, myPlay, i, j)
				#print "Performed stake in MAX AB : "+str(successorState)
				minReturn = minValueAlphaBeta(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n, alpha, beta)
				#print "Returned min : "+str(minReturn)
				if (minReturn['val'] > returnMaxValue['val']):
					returnMaxValue['row'] = i
					returnMaxValue['column'] = j
					returnMaxValue['move'] = 'Stake'
					returnMaxValue['state'] = successorState
					returnMaxValue['val'] = minReturn['val']
				if returnMaxValue['val'] >= beta:
					return returnMaxValue
				alpha = max(alpha, returnMaxValue['val'])
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performRaidAtIJ(state, myPlay, i, j, n)
				if successorState:
					#print "Performed Raid in MAX AB : "+str(successorState)
					minReturn = minValueAlphaBeta(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n, alpha, beta)
					#print "Returned min : "+str(minReturn)
					if (minReturn['val'] > returnMaxValue['val']):
						returnMaxValue['row'] = i
						returnMaxValue['column'] = j
						returnMaxValue['move'] = 'Raid'
						returnMaxValue['state'] = successorState
						returnMaxValue['val'] = minReturn['val']
				if returnMaxValue['val'] >= beta:
					return returnMaxValue
				alpha = max(alpha, returnMaxValue['val'])
	return returnMaxValue

def minValueAlphaBeta(state, depth, myPlay, cellValues, n, alpha, beta):
	#print "In Min Value Alpha Beta : "
	#print "Entered state : "+str(state)
	returnMinValue = {}
	if depth == 0 or isTerminalState(state, n):	#add condition for game over
		returnMinValue['val'] = evalGameScore(getOpponentMove(myPlay), state, cellValues, n)	#using getOpponentMove becoz here my play is actually opponent's player
		returnMinValue['state'] = state
		return returnMinValue
	returnMinValue['val'] = sys.maxint
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performStakeAtIJ(state, myPlay, i, j)
				#print "Performed stake in MIN AB : "+str(successorState)
				maxReturn = maxValueAlphaBeta(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n, alpha, beta)
				#print "Returned max : "+str(maxReturn)
				if (maxReturn['val'] < returnMinValue['val']):
					returnMinValue['row'] = i
					returnMinValue['column'] = j
					returnMinValue['move'] = 'Stake'
					returnMinValue['state'] = successorState
					returnMinValue['val'] = maxReturn['val']
				if returnMinValue['val'] <= alpha:
					return returnMinValue
				beta = min(beta, returnMinValue['val'])
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performRaidAtIJ(state, myPlay, i, j, n)
				if successorState:
					#print "Performed Raid in MIN AB : "+str(successorState)
					maxReturn = maxValueAlphaBeta(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n, alpha, beta)
					#print "Returned max : "+str(maxReturn)
					if (maxReturn['val'] < returnMinValue['val']):
						returnMinValue['row'] = i
						returnMinValue['column'] = j
						returnMinValue['move'] = 'Raid'
						returnMinValue['state'] = successorState
						returnMinValue['val'] = maxReturn['val']
				if returnMinValue['val'] <= alpha:
					return returnMinValue
				beta = min(beta, returnMinValue['val'])
	return returnMinValue

def minimaxDecision(fileData):
	return maxValue(fileData['boardState'], fileData['depth'], fileData['myPlay'], fileData['cellValues'], fileData['n'])

def maxValue(state, depth, myPlay, cellValues, n):
	#print "In Max Value :"
	#print "Entered in state : "+str(state)
	returnMaxValue = {}
	if depth == 0 or isTerminalState(state, n):	#add condition for game over
		#print "HEREeeeeeeeeeeeeeeeeeeeee"
		returnMaxValue['val'] = evalGameScore(myPlay, state, cellValues, n)
		returnMaxValue['state'] = state
		return returnMaxValue
	returnMaxValue['val'] = - sys.maxint - 1
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performStakeAtIJ(state, myPlay, i, j)
				#print "Performed stake : "+str(successorState)
				minReturn = minValue(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n)
				if (minReturn['val'] > returnMaxValue['val']):
					returnMaxValue['row'] = i
					returnMaxValue['column'] = j
					returnMaxValue['move'] = 'Stake'
					returnMaxValue['state'] = successorState
					returnMaxValue['val'] = minReturn['val']
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performRaidAtIJ(state, myPlay, i, j, n)
				if successorState:
					#print "Performed Raid : "+str(successorState)
					minReturn = minValue(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n)
					if (minReturn['val'] > returnMaxValue['val']):
						returnMaxValue['row'] = i
						returnMaxValue['column'] = j
						returnMaxValue['move'] = 'Raid'
						returnMaxValue['state'] = successorState
						returnMaxValue['val'] = minReturn['val']
	return returnMaxValue

def minValue(state, depth, myPlay, cellValues, n):
	#print "In Min Value : "
	#print "Entered state : "+str(state)
	returnMinValue = {}
	if depth == 0 or isTerminalState(state, n):	#add condition for game over
		returnMinValue['val'] = evalGameScore(getOpponentMove(myPlay), state, cellValues, n)	#using getOpponentMove becoz here my play is actually opponent's player
		returnMinValue['state'] = state
		return returnMinValue
	returnMinValue['val'] = sys.maxint
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performStakeAtIJ(state, myPlay, i, j)
				#print "Performed stake : "+str(successorState)
				maxReturn = maxValue(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n)
				if (maxReturn['val'] < returnMinValue['val']):
					returnMinValue['row'] = i
					returnMinValue['column'] = j
					returnMinValue['move'] = 'Stake'
					returnMinValue['state'] = successorState
					returnMinValue['val'] = maxReturn['val']
	for i in range(n):
		for j in range(n):
			if state[i][j] == '.':
				successorState = performRaidAtIJ(state, myPlay, i, j, n)
				if successorState:
					#print "Performed Raid : "+str(successorState)
					maxReturn = maxValue(successorState, depth - 1, getOpponentMove(myPlay), cellValues, n)
					if (maxReturn['val'] < returnMinValue['val']):
						returnMinValue['row'] = i
						returnMinValue['column'] = j
						returnMinValue['move'] = 'Raid'
						returnMinValue['state'] = successorState
						returnMinValue['val'] = maxReturn['val']
	return returnMinValue

def createOutputFile(decision):
	outputFile = open("output.txt", 'w')
	outputLine = chr(decision['column'] + ord('A')) + str(decision['row'] + 1) + " " + decision['move'] + "\n"
	outputFile.write(outputLine)
	for row in decision['state']:
		outputLine = ''.join(row) + "\n"
		outputFile.write(outputLine)
	outputFile.close()

def main():
	#Flow starts here
	fileData = readInputFile("input.txt")

	#print fileData

	decision = decideNextMove(fileData)

	#print decision

	createOutputFile(decision)

main()
