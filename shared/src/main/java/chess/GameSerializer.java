package chess;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.results.ListGamesResult;

import java.util.ArrayList;

public class GameSerializer {

    public Gson createChessSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessPiece.class,
                (JsonDeserializer<ChessPiece>) (jsonString, _, ctx) -> {
                    JsonObject jsonObject = jsonString.getAsJsonObject();
                    ChessGame.TeamColor color = ctx.deserialize(jsonObject.get("color"), ChessGame.TeamColor.class);
                    ChessPiece.PieceType type = ctx.deserialize(jsonObject.get("type"), ChessPiece.PieceType.class);
                    return new ChessPiece(color, type);
                });

        gsonBuilder.registerTypeAdapter(ChessGame.class,
                (JsonDeserializer<ChessGame>) (jsonString, _, ctx) -> {
                    JsonObject jsonObject = jsonString.getAsJsonObject();
                    ChessBoard board = ctx.deserialize(jsonObject.get("board"), ChessBoard.class);
                    ChessGame.TeamColor team = ctx.deserialize(jsonObject.get("team"), ChessGame.TeamColor.class);
                    return new ChessGame(board, team);
                });

        return gsonBuilder.create();
    }

    public ChessGame deserializeGame(String jsonGameInfo) {
        Gson gameSerializer = new GameSerializer().createChessSerializer();
        return gameSerializer.fromJson(jsonGameInfo, ChessGame.class);
    }

    public ListGamesResult deserializeGamesList(String jsonGamesListInfo) {
        Gson chessGameSerializer = createChessSerializer();
        return chessGameSerializer.fromJson(jsonGamesListInfo, ListGamesResult.class);
    }
}
