package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import model.requests.CreateGameRequest;
import model.results.CreateGameResult;
import model.results.ListGamesResult;
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

}