# Scores DB

Cette couche sauvegarde uniquement le meilleur score de chaque jeu dans une base SQLite locale.

## Emplacement de la base

`<racine-projet>/data/scores.db`

## Jeux utilises

- `snake`
- `flappybird`
- `roadrush`
- `trueorfalse`

## Stockage

Une seule ligne par jeu (pas de pseudo, pas d'historique complet).

## API utilisee

- `HighScoreService.saveScore(gameKey, score)`
- `HighScoreService.getBestScore(gameKey)`

## Test rapide (runner)

Lancer `demo.teste.score.HighScoreDebugRunner`.

- Sans argument: affiche les records.
- Avec arguments `<jeu> <score>`: enregistre un score puis affiche les records.

