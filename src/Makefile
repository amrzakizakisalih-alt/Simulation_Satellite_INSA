# Makefile pour la Simulation Spatiale

# Compilateur Java
JC = javac
JFLAGS = -d .

# Lanceur Java
JVM = java

# Classe principale
MAIN = systeme.SystemePrincipal

# Dossiers contenant les sources
SRC_DIRS = systeme modele ui physique physique/collision reseau data Trajectoires

# Trouver tous les fichiers .java
SOURCES = $(foreach dir,$(SRC_DIRS),$(wildcard $(dir)/*.java))

# Fichiers .class correspondants
CLASSES = $(SOURCES:.java=.class)

# Cible par défaut
all: compile

# Compilation de tous les fichiers Java
compile: $(SOURCES)
	@echo "Compilation du projet"
	$(JC) $(JFLAGS) $(SOURCES)
	@echo "Compilation terminée avec succès!"

# Lancer la simulation
run: compile
	@echo "Lancement de la simulation"
	$(JVM) $(MAIN)

# Nettoyer les fichiers compilés
clean:
	@echo "Nettoyage des fichiers .class"
	find . -name "*.class" -type f -delete
	@echo "Nettoyage terminé!"

# Recompiler complètement
rebuild: clean compile

# Afficher l'aide
help:
	@echo "Commandes disponibles:"
	@echo "  make          - Compiler le projet"
	@echo "  make run      - Compiler et lancer la simulation"
	@echo "  make clean    - Supprimer les fichiers compilés"
	@echo "  make rebuild  - Nettoyer puis recompiler"
	@echo "  make help     - Afficher cette aide"

.PHONY: all compile run clean rebuild help
