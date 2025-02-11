# **Code Katas**

A collection of coding exercises (katas) to practice problem-solving, design, and development skills using Java.

---

## **Description**

This repository contains various coding katas designed to improve your programming skills through practice and
problem-solving.

---

## **Table of Contents**

1. [Mars Rover Kata](#mars-rover-kata)

---

## **1. Mars Rover Kata**

[https://kata-log.rocks/mars-rover-kata](https://kata-log.rocks/mars-rover-kata)

### **Your Task**

You’re part of the team that explores Mars by sending remotely controlled vehicles to the surface of the planet. Develop
an API that translates the commands sent from Earth into instructions that are understood by the rover.

---

### **Requirements**

1. **Initial Setup**:
    - The rover starts at a given position `(x, y)` on a grid.
    - The rover is initially facing one of the four cardinal directions: `N` (North), `S` (South), `E` (East), or `W` (
      West).

2. **Commands**:
    - The rover receives a sequence of commands as a character array.
    - Implement the following commands:
        - `f`: Move forward.
        - `b`: Move backward.
        - `l`: Turn left.
        - `r`: Turn right.

3. **Edge Wrapping**:
    - The planet is spherical, so the rover must wrap around the edges of the grid.
    - For example:
        - Moving forward from the top edge of the grid wraps the rover to the bottom edge.
        - Moving backward from the left edge of the grid wraps the rover to the right edge.

4. **Obstacle Detection**:
    - The rover must detect obstacles before moving to a new square.
    - If an obstacle is encountered:
        - The rover stops at the last possible point.
        - The sequence of commands is aborted.
        - The rover reports the position of the obstacle.

---

### **Rules**

1. **Hardcore TDD**:
    - Follow the principles of Test-Driven Development (TDD) strictly.
    - Write tests before writing the implementation.

2. **Role Switching**:
    - If working in pairs, switch roles (driver and navigator) after each TDD cycle:
        - **Driver**: Writes the code.
        - **Navigator**: Reviews the code and ensures it aligns with the requirements.

3. **No Red Phases While Refactoring**:
    - Ensure all tests pass (green) before refactoring the code.
    - Do not introduce failing tests during the refactoring phase.

4. **Handle Edge Cases and Exceptions**:
    - Ensure the code handles null pointers, invalid commands, and other edge cases gracefully.
    - The rover must not fail due to overlooked scenarios.

---

### **Example**

#### **Input**:

- Starting position: `(0, 0)`
- Direction: `N`
- Commands: `"ffrff"`
- Obstacles: `[(0, 2), (2, 2)]`

#### **Output**:

- Final position: `(1, 1)`
- Direction: `E`
- Obstacle encountered: `(0, 2)`

---

### **Development Process**

1. **TDD Workflow**:
    - Write a failing test for a specific feature.
    - Implement the feature to make the test pass.
    - Refactor the code while ensuring all tests remain green.

2. **Edge Cases to Consider**:
    - Null or empty commands.
    - Invalid commands (e.g., characters other than `f`, `b`, `l`, `r`).
    - Wrapping at grid edges.
    - Obstacles at the starting position.

3. **Design Patterns Used**:
    - **Command Pattern**: To handle commands (`f`, `b`, `l`, `r`) as separate objects.
    - **Strategy Pattern**: To encapsulate movement logic for each direction (`N`, `S`, `E`, `W`).
    - **Factory Pattern**: To create direction objects dynamically.

### **Development Steps**

1. **Setup**:
    - Create a new Java project.
    - Add JUnit and Mockito dependencies for testing.
2. Wrote unit tests for the following scenarios:
    - Accepting commands and moving the rover.
    - Moving forward and backward.
    - Turning left and right.
    - Wrapping around the grid edges.
    - Detecting obstacles.
    - Handling invalid commands.
3. Implemented basic working solution to pass all unit tests
    - Built `MarsRover` class with the following methods:
        - `executeCommands`: Executes the sequence of commands.
        - `moveForward`, `moveBackward`, `turnLeft`, `turnRight`: Implement the movement logic.
        - `detectObstacle`: Checks for obstacles before moving.
4. Refactored the code to improve readability and maintainability.
    - Implemented Coordinate class to represent the position of the rover.
    - Extracted object detection logic to a separate class: `ObstacleDetector`.
    - Refactored movement logic into separate classes for each direction.
    - Implemented Factory pattern to create direction objects dynamically.
    - Implement MovementValidator for obstacle validation and dynamic obstacle support
    - Use enums for directions while accepting string input for flexibility and type safety
    - Improved exception handling and added custom exceptions for better error reporting
    - Implement support for multiple movement strategies i.e; TorusMovementStrategy and PolarMovementStrategy
