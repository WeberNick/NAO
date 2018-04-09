# NAO
The goal of this project was to write software controlling the programmable robot [NAO](https://en.wikipedia.org/wiki/Nao_(robot)) and let him play the board game [Nine Men's Morris](https://en.wikipedia.org/wiki/Nine_Men%27s_Morris). The opponent could either be a human player or another robot. We worked with the real robot but with the simulator [Webots](https://www.cyberbotics.com), the software can also be used in a simulation.

We implemented the following software components:

* _User Interface:_ Track the course of the current game, visualize certain characteristics such as CPU usage and as a human player to play against the AI
* _Logic:_ Implementing the logic of the board game Nine Men's Morris
* _Mechanics:_ Programming the movements of the robot (walking on the board, grabing gaming pieces and looking around to analyze the board)
* _Artificial Intelligence:_ Teaching the robot to play and win the gamethe game and use its resources (computing capacity, memory and time) in the most effective way possible

For additional information, take a look in the documents directory. There you can find a user guide, requirements, {object, architecture, class, system sequence}-diagram, and so on.
