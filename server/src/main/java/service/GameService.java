package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.results.CreateGameResult;
import model.results.ListGamesResult;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    DataAccess dataAccess;
    private final AtomicInteger gameIDs;

    public GameService() {
        this.dataAccess = new MemoryDataAccess();
        this.gameIDs = new AtomicInteger(1);
    }

    public CreateGameResult createGame(CreateGameRequest creationRequest, String auth) throws UnauthorizedException, BadRequestException {
        if (creationRequest.gameName()==null) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getAuthData(auth)==null) {
            throw new UnauthorizedException("unauthorized");
        }
        int newGameID = gameIDs.getAndIncrement();
        GameData game = new GameData(newGameID, "", "", creationRequest.gameName());
        dataAccess.addGame(game);
        return new CreateGameResult(newGameID);
    }

    public ListGamesResult listGames(String auth) throws UnauthorizedException {
        // check auth
        if (dataAccess.getAuthData(auth)==null) {
            throw new UnauthorizedException("unauthorized");
        }
        Collection<GameData> games = dataAccess.getAllGames();

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

        if ((joinRequest.playerColor().equals("WHITE")&&!gameToJoin.playerWhite().isEmpty()
        || (joinRequest.playerColor().equals("BLACK")&&!gameToJoin.playerBlack().isEmpty()))) {
            throw new AlreadyTakenException(String.format("%s is already taken", joinRequest.playerColor()));
        }

        // okay, now create a new GameData and replace the old one
        String username = dataAccess.getAuthData(auth).username();
        String playerBlack = gameToJoin.playerBlack();
        String playerWhite = gameToJoin.playerWhite();
        if (joinRequest.playerColor().equals("WHITE")) {
            playerWhite = username;
        }
        else {
            playerBlack = username;
        }
        GameData updatedGame = new GameData(gameToJoin.gameID(), playerWhite, playerBlack, gameToJoin.name());
        dataAccess.addGame(updatedGame);
    }

}