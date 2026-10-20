# Autonomous Satellite Simulation & Trajectory Optimization (Simulation Satellite — Projet 13)

A modular, physics-driven orbital simulation and autonomous trajectory planner developed in **Java**, featuring gravitational N-body interaction, debris field modeling, orbital path generation, and real-time collision detection/resolution.

This project was developed as part of the GM4 (Mathematical Engineering) curriculum at **INSA Rouen Normandie** (Academic Year 2025-2026), supervised by **Mr. A. Osmani**, and created by **Mouad Sheradj Drissi** and **Amr Zaki Salih**.

---

## Overview

The purpose of this project is to model, simulate, and visualize the autonomous navigation of satellite systems in complex gravitational fields, while handling orbital perturbations, communication relays, dynamic obstacles (asteroids, moons), and space debris.

### Key Capabilities
- **Gravitational Physics & Celestial Mechanics:** Newton's law of universal gravitation applied across central planetary bodies, moons, asteroids, and active satellite constellations.
- **Autonomous Trajectory Generation & Penalties:** Node-based pathfinding (`Noeud`, `Trajectoire`) calculating gravity-assisted waypoints and gravity penalty heuristics (`CalculPenaliteGravite`).
- **Collision Detection & Impulse Resolution:** Real-time spatial intersection tracking (`GestionnaireCollisions`), debris proliferation modeling, and post-impact kinematic resolution (`ResolveurCollisionsPhysiques`).
- **Satellite Inter-Network (ISL):** Constellation communication and ground station relay links (`ReseauSatellites`, `Antenne`).
- **Interactive Swing GUI:** Real-time orbital visualization engine (`SpacePanel`, `Interface2D`) coupled with simulation monitoring and parameter adjustment panels (`ControlPanel`).

---

## Project Architecture

The architecture adheres to strict object-oriented design patterns with separation of celestial models, numerical physics, networking, and UI:

```
Simulation_Satellite_Projet_13/
├── src/
│   ├── Makefile                   # Build, execution, and cleanup recipes
│   ├── systeme/
│   │   └── SystemePrincipal.java   # Simulation orchestrator and main execution loop
│   ├── modele/                    # Celestial and artificial space objects
│   │   ├── ObjetSpatial.java      # Base abstract spatial entity
│   │   ├── Planete.java           # Central primary gravitational bodies
│   │   ├── Lune.java              # Orbiting natural satellites
│   │   ├── Asteroides.java        # Dynamic orbital debris and hazard entities
│   │   └── Satellites.java        # Controllable satellite agents
│   ├── physique/                  # Physics engine and collision resolution
│   │   ├── ForceGravitationnelle.java # N-body gravitational force computations
│   │   └── collision/
│   │       ├── Collisionn.java                 # Collision event descriptors
│   │       ├── GestionnaireCollisions.java     # Proximity and intersection detection
│   │       ├── GestionnaireSatellites.java     # Satellite fleet state manager
│   │       └── ResolveurCollisionsPhysiques.java# Elastic/inelastic impulse resolution
│   ├── Trajectoires/              # Path optimization and orbital planning
│   │   ├── Noeud.java             # Graph nodes and spatial waypoints
│   │   └── Trajectoire.java       # Parametric trajectory curves and state logs
│   │   └── CalculPenaliteGravite.java # Gravitational cost function & penalty evaluator
│   ├── reseau/                    # Inter-satellite communication
│   │   ├── Antenne.java           # Radio/laser communication transceivers
│   │   └── ReseauSatellites.java  # Graph topology and routing between orbital nodes
│   ├── data/                      # Persistence and orbital ephemeris
│   │   └── BaseDeDonnes.java      # Data access layer for telemetry and configurations
│   └── ui/                        # Graphical User Interface (Swing/AWT)
│       ├── Interface2D.java       # Top-level application window and canvas
│       ├── SpacePanel.java        # Double-buffered 2D orbital rendering canvas
│       └── ControlPanel.java      # Play/pause, step controls, and physics parameters
├── Cahier_des_charges_projet_Java_S7-1.pdf # Official project specifications and design document
├── calcul-de-trajectoire.pdf      # Theoretical notes on trajectory mathematics
├── use-case.pdf                   # UML Use Case diagrams and specifications
└── README.md
```

---

## Compilation & Execution

### Prerequisites
- **Java Development Kit (JDK 17 or higher)**
- **GNU Make**

### Building and Running

1. Navigate to the `src` directory:
   ```bash
   cd src
   ```

2. Compile all modules:
   ```bash
   make
   ```

3. Launch the orbital simulation:
   ```bash
   make run
   ```

4. Clean up compiled bytecode:
   ```bash
   make clean
   ```

---

## Documentation & Reports

The project repository includes complete technical documentation and specifications:
- **`Cahier_des_charges_projet_Java_S7-1.pdf`**: Comprehensive architectural specifications, requirements, and design document.
- **`calcul-de-trajectoire.pdf`**: Mathematical formulations and penalty metrics used for trajectory optimization.
- **`use-case.pdf`**: UML use case diagrams illustrating system interactions.

---

## Authors & Acknowledgments

- **Mouad Sheradj Drissi**
- **Amr Zaki Salih**

**Academic Advisor:** **Mr. A. Osmani** — INSA Rouen Normandie

---

## Institution

**INSA Rouen Normandie**  
Department of Mathematical Engineering (GM4)
