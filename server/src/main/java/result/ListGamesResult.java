package result;

import model.GameData;

import java.util.Map;

public record ListGamesResult(
        Map<Integer, GameData> games,
        Boolean success) {
}
