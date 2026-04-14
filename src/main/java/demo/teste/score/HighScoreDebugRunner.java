package demo.teste.score;

public final class HighScoreDebugRunner {
    private static final String[] GAMES = {"snake", "flappybird", "roadrush", "trueorfalse"};

    private HighScoreDebugRunner() {
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String game = args[0];
            int score = Integer.parseInt(args[1]);
            HighScoreService.saveScore(game, score);
            System.out.println("Score enregistre pour " + game + " = " + score);
        }

        for (String game : GAMES) {
            System.out.println(game + " -> meilleur score: " + HighScoreService.getBestScore(game));
        }
    }
}

