package model;

import chess.ChessGame;

public record GameData(Integer gameID, String name, ChessGame game, String playerWhite, String playerBlack) {

}