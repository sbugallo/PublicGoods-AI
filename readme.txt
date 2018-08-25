Name:Sergio Bugallo Enjamio
Lab account: psi4

#####################################################################
REINFORCEMENT LEARNING AND RISK BASED HYBRID ARTIFICIAL INTELLIGENCE
#####################################################################

1. Introduction

As I have already implemented Case Based Reasoning (CBR) in the C Group Project, I wanted to try diferent algorithms. 
Since the public goods game needs a fast learning speed rate and fas reaction to the environment changes, the easiest way
 to make the AIs adaptable and flexible to the multiples states is a reinforcement learning algorithm.

Now, the only thing left is to set the strategy. I did not want to create a static AI so I decided to implement a risk based 
algorithm so the player will decide the role depending on the results (he will take more risky decisons if he is losing).

2. Risk Based Algorithm

Let's start with the risk based algorithm. I defined 3 different states: Safe-Normal-Risky. In each state, the player will 
look a diferent stat of each role.

2.1 Safe State

In the safe state, the player will look how "profitable" the role is. When I say profitable, I am not refering to the benefit 
it generates, I am talking about a measure of how effective the role is (if it generate a positive result but not how positive 
it is).

2.2 Normal State

In the normal state, it will look at the times won/times played ratio of each role. With this, the player will pick a role depending 
of how positive the role was till then.

2.3 Risky State

In the risky state, the player will choose the role with the maximun benefit. When it calculates the benefit, the AI will use 
the overall number of roles played.

2.4 Performance and Risk

The problem of this algortih is how to set the risk. To solve it, I make the player save its "game evolution". That is how many rounds
it won.

The game evolution is used to calculate the performance of the AI, that is calculated dividing the game evolution by the number of 
rounds played. This will give us the percentage of rounds won.

If the performance is higher than 0.8, the AI is winning so it will go the safe state and play de most "profitable" role. If the performance
is lower than 0.2, it is losing so it will play the role with the maximum benefit. In another case, the AI will play the role with the best 
times won/times played ratio role.

2.4.1 Notes

I tried to multiply the performance by a function that maximizes or minimizes it depending of the remaining game percentage
(i.e. round^2 / total rounds^2) but the results were worse, so I decided to not use a scale factor.

3. Reinforcement Learning Algorithm

As I already explained in section 2, some of the decisions are taken depending on how reinforced is some stat, for example, the
profitableness, that is reinforced every time a role returns a positive result, but this is not the only thing that is uses reinforcement 
learning.

The AI has a list that saves the following information about every player:

[playerID | times caught defecting | times inspected]

Thanks to this list, the AI knows, by its own experience, who is the best player to inspect. Each time a player is caught by the AI defecting, 
it is reinforced as a potential objective to inspect next time.

3.1 Avoiding Auto-reinforcement

If the main problem in a risk based algorithm is how to set the risk, the problem in reinforcement learning is how to avoid that a option is 
auto-reinforced.

I solved this problem by setting a margin. For example, when the AI calculates the best option to inspect, it sees how many times the other 
player was inspected and sets a margin that at least twice as often as other players

3.1.1 Notes

As a supply of the solution, when two options are equally reinforced, the AI chooses ramdomly.
