# **Code Katas**

A collection of coding exercises (katas) to practice problem-solving, design, and development skills using Java.

---

## **Description**

This repository contains various coding katas designed to improve your programming skills through practice and
problem-solving.

---

## **Table of Contents**

1. [Mars Rover Kata](#1-mars-rover-kata)  
2. [Ordering System Kata](#2-ordering-system-kata)

---

## **1. Mars Rover Kata**

### **Problem Statement**

You’re part of the team that explores Mars by sending remotely controlled vehicles to the surface of the planet. Develop an API that translates the commands sent from Earth into instructions that are understood by the rover.

### **Summary of Changes**

1. Implemented a **command execution system** to process rover commands (`F`, `B`, `L`, `R`).
2. Added support for two grid systems:
   - **Torus/Donut Grid**: Handles edge wrapping in a 2D grid.
   - **Polar Coordinate System**: Simulates movement on a spherical grid.
3. Introduced **obstacle detection** to stop the rover when encountering obstacles.
4. Used design patterns for extensibility:
   - **Command Pattern**: Encapsulates commands as objects.
   - **Strategy Pattern**: Handles different grid systems.
   - **Factory Pattern**: Dynamically creates direction and command objects.
5. Followed **Test-Driven Development (TDD)** to ensure correctness and handle edge cases.

---

## **2. Ordering System Kata**

### **Problem Statement**

Develop a food ordering system that supports:
- Multiple payment methods (Wallet, Credit Card, Cash).
- Split payments across multiple methods.
- Dynamic discounts based on rules like weekends, age, and birthdays.
- Validation of payment details to ensure the total matches the order amount.
- Graceful error handling for payment failures and unexpected exceptions.

### **Summary of Changes**

1. Designed a **modular system** using design patterns:
   - **Strategy Pattern**: Handles multiple payment methods.
   - **Decorator Pattern**: Applies dynamic discounts.
   - **Factory Pattern**: Creates payment processors and discount decorators.
2. Added support for:
   - **Split Payments**: Customers can split payments across multiple methods.
   - **Dynamic Discounts**: Discounts are applied based on rules like weekends, age, and birthdays.
3. Enhanced error handling:
   - Validates payment details to ensure the total matches the order amount.
   - Handles payment failures and unexpected exceptions gracefully.
4. Followed **Test-Driven Development (TDD)**:
   - Wrote unit tests to cover all scenarios, including success, validation errors, and edge cases.
