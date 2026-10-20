package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import model.requests.*;
import model.results.*;
import service.exceptions.*;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    DataAccess dataAccess;

    public GameService() {
        this.dataAccess = new MemoryDataAccess();
    }

    public CreateGameResult createGame(CreateGameRequest creationRequest, String auth) throws UnauthorizedException, BadRequestException {
        if (creationRequest.gameName()==null) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getAuthData(auth)==null) {
            throw new UnauthorizedException("unauthorized");
        }
        int gameID = dataAccess.createGame(creationRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String auth) throws UnauthorizedException {
        // check auth
        if (dataAccess.getAuthData(auth)==null) {
            throw new UnauthorizedException("unauthorized");
        }
        ArrayList<GameData> games = dataAccess.getAllGames();
        ListGamesResult listResult = new ListGamesResult(games);

        var serializer = new Gson();
        System.out.println(serializer.toJson(listResult));
        System.out.println("length: " + listResult.games().size());

        return new ListGamesResult(games);
    }

    public void joinGame(String auth, JoinGameRequest joinRequest) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        // check auth
        if (dataAccess.getAuthData(auth)==null) {
            throw new UnauthorizedException("unauthorized");
        }

        // check that it's not a bad request
        if (joinRequest.playerColor()==null||joinRequest.gameID()==null||(!joinRequest.playerColor().equals("BLACK")&&!joinRequest.playerColor().equals("WHITE"))) {
            throw new BadRequestException("bad request");
        }

        // check if it's already taken
        GameData gameToJoin = dataAccess.getGame(joinRequest.gameID());
        if ((joinRequest.playerColor().equals("WHITE")&&gameToJoin.whiteUsername()!=null
        || (joinRequest.playerColor().equals("BLACK")&&gameToJoin.blackUsername()!=null))) {
            throw new AlreadyTakenException(String.format("%s is already taken", joinRequest.playerColor()));
        }

        // okay, now create a new GameData and replace the old one
        String username = dataAccess.getAuthData(auth).username();
        String playerBlack = gameToJoin.blackUsername();
        String playerWhite = gameToJoin.whiteUsername();
        if (joinRequest.playerColor().equals("WHITE")) {
            playerWhite = username;
        }
        else {
            playerBlack = username;
        }
        GameData updatedGame = new GameData(gameToJoin.gameID(), playerWhite, playerBlack, gameToJoin.gameName());
        dataAccess.updateGame(updatedGame);
    }

}