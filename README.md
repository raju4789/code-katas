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

1. **Rover Initialization**:
    - The rover starts at a specified position `(x, y)` on a grid.
    - The rover is initially oriented in one of the four cardinal directions:
        - `N` (North)
        - `S` (South)
        - `E` (East)
        - `W` (West)

2. **Command Execution**:
    - The rover receives a sequence of commands as a string of characters.
    - Supported commands:
        - `F`: Move forward in the direction the rover is facing.
        - `B`: Move backward in the opposite direction the rover is facing.
        - `L`: Turn left
        - `R`: Turn right

3. **Grid System Interpretations**:
    - The API supports two interpretations of the grid system to align with different mental models:
        - **Torus/Donut Grid**:
            - The grid wraps around in both directions (horizontal and vertical), retaining Euclidean geometry.
            - Similar to games like Snake or Pac-Man, where the rover disappears on one edge and reappears on the
              opposite edge.
            - This approach does not introduce new edge cases but may require users to align their mental model with the
              concept of a toroidal grid.
        - **Polar Coordinate System**:
            - The grid is interpreted as a sphere, with latitude and longitude lines dividing the surface.
            - Latitude lines are equidistant, while longitude lines are evenly spaced from the North to the South Pole.
            - This model is intuitive for those thinking in terms of maps and planets.

4. **Edge Wrapping**:
    - Depending on the grid system:
        - **Torus/Donut Grid**:
            - Moving forward from the top edge of the grid places the rover at the bottom edge.
            - Moving backward from the left edge of the grid places the rover at the right edge.
        - **Polar Coordinate System**:
            - Moving forward or backward adjusts the latitude and longitude, wrapping around the poles and the equator
              as necessary.

5. **Obstacle Detection**:
    - The rover detects obstacles on the grid before moving to a new position.
    - If an obstacle is encountered:
        - The rover stops at its current position.
        - The remaining commands in the sequence are ignored.
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

### **Example Usage**

#### **Torus/Donut Grid Example**

##### **Input**:

- Grid size: `5 x 5`
- Starting position: `(0, 0)`
- Direction: `N`
- Commands: `"FFRFF"`
- Obstacles: `[(2, 2), (3, 3)]`

##### **Execution**:

1. Move forward (`F`): `(0, 0)` → `(0, 1)`
2. Move forward (`F`): `(0, 1)` → `(0, 2)`
3. Turn right (`R`): Facing `N` → Facing `E`
4. Move forward (`F`): `(0, 2)` → `(1, 2)`
5. Move forward (`F`): `(1, 2)` → `(2, 2)` (Obstacle detected)

##### **Output**:

- Final position: `(1, 2)`
- Direction: `E`
- Obstacle encountered: `(2, 2)`
- Command sequence aborted.

---

#### **Polar Coordinate System Example**

##### **Input**:

- Grid size: `5 x 5` (interpreted as latitude and longitude)
- Starting position: `(0, 0)` (latitude: `0`, longitude: `0`)
- Direction: `N`
- Commands: `"FFRFF"`
- Obstacles: `[(0, 2), (2, 2)]`

##### **Execution**:

1. Move forward (`F`): `(0, 0)` → `(0, 1)` (latitude increases)
2. Move forward (`F`): `(0, 1)` → `(0, 2)` (latitude increases)
3. Turn right (`R`): Facing `N` → Facing `E`
4. Move forward (`F`): `(0, 2)` → `(1, 2)` (longitude increases)
5. Move forward (`F`): `(1, 2)` → `(2, 2)` (Obstacle detected)

##### **Output**:

- Final position: `(1, 2)`
- Direction: `E`
- Obstacle encountered: `(2, 2)`
- Command sequence aborted.

---

#### **Torus/Donut Grid Wrapping Example**

##### **Input**:

- Grid size: `5 x 5`
- Starting position: `(4, 4)`
- Direction: `N`
- Commands: `"F"`
- Obstacles: `[]`

##### **Execution**:

1. Move forward (`F`): `(4, 4)` → `(4, 0)` (wraps around to the bottom edge)

##### **Output**:

- Final position: `(4, 0)`
- Direction: `N`

---

#### **Polar Coordinate System Wrapping Example**

##### **Input**:

- Grid size: `5 x 5` (interpreted as latitude and longitude)
- Starting position: `(4, 0)` (latitude: `4`, longitude: `0`)
- Direction: `N`
- Commands: `"F"`
- Obstacles: `[]`

##### **Execution**:

1. Move forward (`F`): `(4, 0)` → `(0, 0)` (wraps around the pole to the opposite latitude)

##### **Output**:

- Final position: `(0, 0)`
- Direction: `N`

### **Development Process**

1. **TDD Workflow**:
    - Write a failing test for a specific feature or edge case.
    - Implement the feature to make the test pass.
    - Refactor the code while ensuring all tests remain green.
    - Repeat the process for each feature, ensuring comprehensive test coverage.

2. **Edge Cases to Consider**:
    - Null or empty commands.
    - Invalid commands (e.g., characters other than `F`, `B`, `L`, `R`).
    - Wrapping at grid edges for both **Torus/Donut Grid** and **Polar Coordinate System**.
    - Obstacles at the starting position.
    - Obstacles encountered mid-sequence, ensuring the rover stops and reports the obstacle.
    - Handling of edge wrapping when moving forward or backward near the poles (for Polar Coordinate System).

3. **Design Patterns Used**:
    - **Command Pattern**:
        - Each command (`F`, `B`, `L`, `R`) is encapsulated as a separate object, making the system extensible for new
          commands.
        - Commands are dynamically mapped and executed using a `CommandFactory`.
    - **Strategy Pattern**:
        - Movement logic is encapsulated in a `MovementStrategy` interface.
        - Different implementations (`TorusMovementStrategy`, `PolarMovementStrategy`) handle movement based on the grid
          system.
    - **Factory Pattern**:
        - Used to create direction objects dynamically (`N`, `S`, `E`, `W`) via a `DirectionFactory`.
        - Also used to map commands to their respective `Command` objects in the `CommandFactory`.

4. **Grid System Flexibility**:
    - The implementation supports two grid systems:
        - **Torus/Donut Grid**: Handles edge wrapping in a 2D Euclidean grid.
        - **Polar Coordinate System**: Handles movement and wrapping based on latitude and longitude, simulating a
          spherical planet.

5. **Refactoring and Extensibility**:
    - The use of design patterns ensures the code is modular and easy to extend.
    - Adding new commands (e.g., diagonal movement) or new grid systems (e.g., hexagonal grids) can be done without
      modifying existing code, adhering to the **Open/Closed Principle**.

### **Development Steps**

1. **Setup**:
    - Created a new Java Maven project.
    - Added dependencies for:
        - **JUnit** and **Mockito** for unit testing.
        - **Logging libraries** for debugging and error reporting.

2. **Unit Testing**:
    - Wrote unit tests to cover the following scenarios:
        - Executing commands to move the rover.
        - Moving forward and backward.
        - Turning left and right.
        - Wrapping around the grid edges.
        - Detecting obstacles and stopping when encountered.
        - Handling invalid commands gracefully.

3. **Initial Implementation**:
    - Built a basic working solution to pass all unit tests.
    - Developed the `MarsRover` class with the following methods:
        - `executeCommands`: Processes and executes the sequence of commands.
        - `moveForward` and `moveBackward`: Handles forward and backward movement logic.
        - `turnLeft` and `turnRight`: Handles turning logic.
        - `detectObstacle`: Checks for obstacles before moving to a new position.

4. **Code Refactoring**:
    - Improved the code structure for better readability and maintainability:
        - Introduced a `Coordinate` class to represent the rover's position.
        - Extracted obstacle detection logic into a separate class: `ObstacleDetector`.
        - Refactored movement logic into separate classes for each direction.
        - Implemented the **Factory Pattern** to dynamically create direction objects.
        - Added a `MovementValidator` class to validate movements and support dynamic obstacle detection.
        - Used enums for directions (`N`, `S`, `E`, `W`) while allowing string input for flexibility and type safety.
        - Enhanced exception handling by introducing custom exceptions for better error reporting.

5. **Support for Multiple Movement Strategies**:
    - Added support for different grid systems by implementing multiple movement strategies:
        - **TorusMovementStrategy**: Handles edge wrapping in a toroidal (donut-shaped) grid.
        - **PolarMovementStrategy**: Handles movement and wrapping based on latitude and longitude in a spherical grid.

6. **Command Pattern Implementation**:
    - Refactored the command execution logic using the **Command Pattern**:
        - Encapsulated each command (`F`, `B`, `L`, `R`) as a separate object.
        - Used a `CommandFactory` to dynamically map and execute commands.

