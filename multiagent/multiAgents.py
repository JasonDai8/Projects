# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent
from pacman import GameState

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState: GameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState: GameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        score = successorGameState.getScore()
        foods = newFood.asList()
        caps = currentGameState.getCapsules()
        ghostpos = successorGameState.getGhostPosition(1)
        ghostdis = util.manhattanDistance(ghostpos,newPos)
        
        shortestDist = 1000
        for i in foods:
            distance = util.manhattanDistance(i, newPos)
            if distance < shortestDist:
                shortestDist = distance
        score += max(ghostdis,3)

        if len(foods) < len(currentGameState.getFood().asList()):
            score += 100
        
        score += 100 / shortestDist

        if newPos in caps:
            score += 200
        
        if action == Directions.STOP:
            score -= 10
        return score


def scoreEvaluationFunction(currentGameState: GameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
        return self.mx(gameState, 0, self.depth)[1]

    def mx(self, gameState, agentIndex, depth):
        if gameState.isWin() or gameState.isLose() or depth == 0:
            return ( self.evaluationFunction(gameState), "Stop")
        
        agentsNumber = gameState.getNumAgents()
        agentIndex %=  agentsNumber
        if agentIndex == agentsNumber - 1:
            depth -= 1

        if agentIndex == 0:
            return self.maxV(gameState, agentIndex, depth)
        else:
            return self.minV(gameState, agentIndex, depth)

    def maxV(self, gameState, agentIndex, depth):
        actions = []
        for action in gameState.getLegalActions(agentIndex):
            actions.append((self.mx(gameState.generateSuccessor(agentIndex, action), agentIndex + 1, depth)[0], action))   
        return max(actions)

    
    def minV(self, gameState, agentIndex, depth):
        actions = []
        for action in gameState.getLegalActions(agentIndex):
            actions.append((self.mx(gameState.generateSuccessor(agentIndex, action), agentIndex + 1, depth)[0], action))    
        return min(actions)
    
        util.raiseNotDefined()

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        return self.mx(gameState, 0, self.depth)[1]
        util.raiseNotDefined()
    
    def mx(self, gameState, agentIndex, depth, alpha = -999999, beta = 999999):
        if gameState.isWin() or gameState.isLose() or depth == 0:
            return ( self.evaluationFunction(gameState), "Stop")
        
        agentsNumber = gameState.getNumAgents()
        agentIndex %=  agentsNumber
        if agentIndex == agentsNumber - 1:
            depth -= 1

        if agentIndex == 0:
            return self.maxV(gameState, agentIndex, depth, alpha, beta)
        else:
            return self.minV(gameState, agentIndex, depth, alpha, beta)


    def maxV(self, gameState, agentIndex, depth, alpha, beta):
        actions = []
        for action in gameState.getLegalActions(agentIndex):
            v = self.mx(gameState.generateSuccessor(agentIndex, action), agentIndex + 1, depth, alpha, beta)[0]
            actions.append((v, action))
            if v > beta:
                return (v, action)
            alpha = max(alpha, v)
        return max(actions)
    

    def minV(self, gameState, agentIndex, depth, alpha, beta):
        actions = []
        for action in gameState.getLegalActions(agentIndex):
            v = self.mx(gameState.generateSuccessor(agentIndex, action), agentIndex + 1, depth, alpha, beta)[0]
            actions.append((v, action))
            if v < alpha:
                return (v, action)
            beta = min(beta, v)
        return min(actions)
    

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"
        action, score = self.get_v(gameState, 0, 0)
        return action
    
    def expected_v(self, game_state, index, depth):
        """
        Returns the max utility value-action for max-agent
        """
        legalMoves = game_state.getLegalActions(index)
        expected_value = 0
        expected_action = ""

        successor_probability = 1.0 / len(legalMoves)

        for action in legalMoves:
            successor = game_state.generateSuccessor(index, action)
            successor_index = index + 1
            successor_depth = depth

            if successor_index == game_state.getNumAgents():
                successor_index = 0
                successor_depth += 1

            current_action, current_value = self.get_v(successor, successor_index, successor_depth)

            expected_value += successor_probability * current_value

        return expected_action, expected_value

    def get_v(self, game_state, index, depth):
        """
        Returns value as pair of [action, score] based on the different cases:
        1. Terminal state
        2. Max-agent
        3. Expectation-agent
        """
        if len(game_state.getLegalActions(index)) == 0 or depth == self.depth:
            return "", self.evaluationFunction(game_state)

        if index == 0:
            return self.max_v(game_state, index, depth)
        else:
            return self.expected_v(game_state, index, depth)

    def max_v(self, game_state, index, depth):
        """
        Returns the max utility value-action for max-agent
        """
        legalMoves = game_state.getLegalActions(index)
        max_value = float("-inf")
        max_action = ""

        for action in legalMoves:
            successor = game_state.generateSuccessor(index, action)
            successor_index = index + 1
            successor_depth = depth

            if successor_index == game_state.getNumAgents():
                successor_index = 0
                successor_depth += 1

            current_action, current_value = self.get_v(successor, successor_index, successor_depth)

            if current_value > max_value:
                max_value = current_value
                max_action = action

        return max_action, max_value

        util.raiseNotDefined()

def betterEvaluationFunction(currentGameState: GameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    pacman_pos = currentGameState.getPacmanPosition()
    ghost_pos = currentGameState.getGhostPositions()

    food_list = currentGameState.getFood().asList()
    food_count = len(food_list)
    capsule_count = len(currentGameState.getCapsules())
    closest_food = 1

    game_score = currentGameState.getScore()
    food_distances = [manhattanDistance(pacman_pos, food_pos) for food_pos in food_list]

    if food_count > 0:
        closest_food = min(food_distances)

    for ghost_pos in ghost_pos:
        ghost_distance = manhattanDistance(pacman_pos, ghost_pos)

        if ghost_distance < 2:
            closest_food = 99999

    features = [1.0 / closest_food,
                game_score,
                food_count,
                capsule_count]

    weights = [10,
               200,
               -100,
               -10]

    return sum([feature * weight for feature, weight in zip(features, weights)])

    util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction
