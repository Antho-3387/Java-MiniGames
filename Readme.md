# Projet Java MiniGames

## Jeux disponibles

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

### Flappy Bird

Le menu principal permet aussi de lancer un Flappy Bird jouable dans une nouvelle fenetre JavaFX.

#### Fonctionnalites

- Deplacement avec `espace`, `W`, `fleche haut` ou le clic de souris
- Tuyaux et collision
- Score qui augmente quand on passe un obstacle
- Ecran `Game Over`
- Relance avec `R` ou un clic
- Sprites charges depuis `src/main/resources/img/flappybird`

## Structure utile

- `src/main/java/demo/teste/HelloController.java` : boutons menu -> lancement Snake / Flappy Bird
- `src/main/java/demo/teste/snake/SnakeGamePane.java` : boucle et rendu du jeu
- `src/main/java/demo/teste/snake/SnakeGameLauncher.java` : ouverture du jeu depuis le menu
- `src/main/java/demo/teste/snake/SnakeStandaloneApplication.java` : runner standalone
- `src/main/java/demo/teste/flappybird/FlappyBirdGamePane.java` : boucle et rendu du Flappy Bird
- `src/main/java/demo/teste/flappybird/FlappyBirdGameLauncher.java` : ouverture du jeu depuis le menu
- `src/main/java/demo/teste/flappybird/FlappyBirdStandaloneApplication.java` : runner standalone

## Lancer le projet

Depuis IntelliJ :

1. Ouvrir `demo.teste.Launcher`
2. Cliquer sur **Jeu du Snake**
3. Cliquer sur **Flappy Bird**

Option Maven (si Maven est installe localement) :

- Application menu: `mvn javafx:run`

Pour lancer Snake directement, definir temporairement la classe principale JavaFX sur
`demo.teste.snake.SnakeStandaloneApplication` dans la configuration d'execution.

Pour lancer Flappy Bird directement, definir temporairement la classe principale JavaFX sur
`demo.teste.flappybird.FlappyBirdStandaloneApplication` dans la configuration d'execution.

