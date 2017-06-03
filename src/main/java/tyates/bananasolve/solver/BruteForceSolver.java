package tyates.bananasolve.solver;

import tyates.bananasolve.data.*;
import tyates.bananasolve.dictionary.Dictionary;
import tyates.bananasolve.dictionary.Restrictions;
import tyates.bananasolve.heuristics.LongestWordHeuristic;
import tyates.bananasolve.heuristics.OrderingHeuristic;
import tyates.bananasolve.util.Direction;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * A class the uses a brute-force approach to solve a Banagrams hand.
 */
public class BruteForceSolver implements Solver {
    private final Dictionary dictionary;
    private final OrderingHeuristic firstWordHeuristic;
    private final OrderingHeuristic subsequentWordHeuristic;

    private Board solution = null;

    /**
     * Creates a brute-force solver which will use the given Dictionary.
     *
     * @param dictionary the given dictionary
     */
    public BruteForceSolver(final Dictionary dictionary) {
        this.dictionary = dictionary;
        firstWordHeuristic = new LongestWordHeuristic();
        subsequentWordHeuristic = new LongestWordHeuristic();
    }

    @Override
    public Board solve(final TileGroup tiles) {
        solution = null;

        work(new ArrayBoard(dictionary), tiles);

        return solution;
    }

    private void work(final Board currentBoard, final TileGroup currentTiles) {
        // Make sure we have a board and tile group
        if (currentBoard == null || currentTiles == null) {
            return;
        }

        // If a solution has been found we can just kill this recursive call
        if (solution != null) {
            return;
        }

        // If we have used all tiles then we have found a solution
        if (currentTiles.isEmpty()) {
            solution = currentBoard;
            return;
        }

        // Placing the first word
        if (currentBoard.getTiles().isEmpty()) {
            final SortedSet<String> validWords = firstWordHeuristic.orderWords(
                    dictionary.validWordsPossible(currentTiles));
            for (final String word : validWords) {
                final Board newBoard = currentBoard.copy();
                final List<Character> addedChars = newBoard.addWord(word, 100, 100, Direction.RIGHT);
                work(newBoard, currentTiles.subtractedBy(new HashTileGroup(addedChars)));
            }
        } else {
            // Placing a word on an existing board
            for (final Tile tile : currentBoard.getTiles()) {
                placeWordsOnTile(currentBoard, currentTiles, tile);
            }
        }
    }

    private void placeWordsOnTile(final Board board, final TileGroup tiles, final Tile tile) {
        final TileGroup tilesPlusTile = tiles.combinedWith(tile);

        // Down and Right direction words must start with the tile character
        Set<String> restrictedWords = dictionary.validWordsPossible(tilesPlusTile,
                Restrictions.startsWith(tile.getCharacter()));
        SortedSet<String> words = subsequentWordHeuristic.orderWords(restrictedWords);
        for (final String word : words) {
            tryPlacingWord(word, tiles, board, tile.getRow(), tile.getCol(), Direction.RIGHT);
            tryPlacingWord(word, tiles, board, tile.getRow(), tile.getCol(), Direction.DOWN);
        }

        // Left and Up direction words must end with the tile character
        restrictedWords = dictionary.validWordsPossible(tilesPlusTile, Restrictions.endsWith(tile.getCharacter()));
        words = subsequentWordHeuristic.orderWords(restrictedWords);
        for (final String word : words) {
            tryPlacingWord(word, tiles, board, tile.getRow(), tile.getCol(), Direction.LEFT);
            tryPlacingWord(word, tiles, board, tile.getRow(), tile.getCol(), Direction.UP);
        }
    }

    private void tryPlacingWord(final String word, final TileGroup tiles, final Board board, final int r, final int c,
                                final Direction direction) {
        final Board newBoard = board.copy();
        final List<Character> addedChars = newBoard.addWord(word, r, c, direction);
        if (addedChars != null && !addedChars.isEmpty()) {
            work(newBoard, tiles.subtractedBy(new HashTileGroup(addedChars)));
        }
    }
}
