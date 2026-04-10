# Projet Java MiniGames

## Jeu disponible

### Snake

Le menu principal lance maintenant un jeu Snake complet (nouvelle fenetre JavaFX).

### Fonctionnalites

- Choix du niveau au lancement: `Facile` (sans obstacles) ou `Difficile` (avec obstacles)
- Deplacement au clavier (`fleches` ou `WASD`)
- Score qui augmente en mangeant les pommes
- Collision murs + corps + obstacles (mode difficile)
- Ecran `Game Over`
- Relance rapide avec la touche `R`
- Sprites charges depuis `src/main/resources/img/snake`

## Structure utile

- `src/main/java/demo/teste/HelloController.java` : bouton menu -> lancement Snake
- `src/main/java/demo/teste/snake/SnakeGamePane.java` : boucle et rendu du jeu
- `src/main/java/demo/teste/snake/SnakeGameLauncher.java` : ouverture du jeu depuis le menu
- `src/main/java/demo/teste/snake/SnakeStandaloneApplication.java` : runner standalone

## Lancer le projet

Depuis IntelliJ :

1. Ouvrir `demo.teste.Launcher`
2. Cliquer sur **Jeu du Snake**

Option Maven (si Maven est installe localement) :

- Application menu: `mvn javafx:run`

Pour lancer Snake directement, definir temporairement la classe principale JavaFX sur
`demo.teste.snake.SnakeStandaloneApplication` dans la configuration d'execution.
