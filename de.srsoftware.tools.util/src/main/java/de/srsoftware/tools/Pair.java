package de.srsoftware.tools;

/**
 * simple wrapper holding two objects of the same type
 * @param left on element of the pair
 * @param right the other element of the pair
 * @param <T> the type of the wrapped objects
 */
public record Pair<T>(T left, T right){

}
