package hk.ust.comp3021.entities;


/**
 * An entity in the Sokoban game.
 */
public abstract sealed class Entity permits Box, Empty, Player, Wall {

}
