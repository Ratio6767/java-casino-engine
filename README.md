# Java Casino Engine

A modular, object-oriented Java application featuring classic casino games. This project was developed to demonstrate core Software Engineering principles and clean code architecture.

## 🎰 Features
* **Blackjack:** Standard rules with dealer logic.
* **Roulette:** Betting system with randomized outcomes.
* **Extensible Framework:** Built to easily add new games.

## 🛠️ Technical Highlights (OOP)
This project focuses on **Object-Oriented Programming (OOP)** concepts:
* **Polymorphism:** Utilized through abstract methods like `startRound()` and `resetGame()`.
* **Abstraction:** The core logic is built around an abstract `CasinoGame` class.
* **Interfaces:** Implementation of a `Bettable` interface to standardize betting logic across different game types.
* **Encapsulation:** Strict use of private fields and public getters/setters to protect game state.

## 🚀 How to Run
1. Clone the repository:
   `git clone https://github.com/Ratio6767/java-casino-engine.git`
2. Compile the files:
   `javac src/*.java`
3. Run the Main class:
   `java src/Main`
