package de.chrlembeck.codegen.generator;

import java.io.Serializable;

import org.antlr.v4.runtime.Token;

/**
 * Stellt die Position (Zeile/Spalte) innerhalb einer Template-Datei dar.
 * 
 * @author Christoph Lembeck
 */
public class Position implements Comparable<Position>, Serializable {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 8736053695394885050L;

    /**
     * Spalte innerhalb einer Zeile. Die erste Spalte wird dabei als column 1 bezeichnet.
     */
    private final int column;

    /**
     * Zeile innerhalb der Datei. Die erste Zeile hat den Index 1.
     */
    private final int line;

    /**
     * Extrahiert die Position aus dem ANTLR-Token.
     * 
     * @param start
     *            Token, dessen Position durch dieses Objekt repräsentiert werden soll.
     */
    public Position(final Token start) {
        this.line = start.getLine();
        this.column = 1 + start.getCharPositionInLine();
    }

    /**
     * Erstellt ein neuen Objekt mit Hilfe der übergebenen Positionsbeschreibung.
     * 
     * @param line
     *            Zeile innerhalb der Datei. Die erste Zeile hat den Index 1.
     * @param column
     *            Spalte innherhalb der Zeile. Das erste Zeichen hat den Index 1.
     */
    public Position(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Gibt die Position in lesbarer Form aus.
     */
    @Override
    public String toString() {
        return "Line " + line + ", Column " + column;
    }

    /**
     * Gibt die Position in Kurzform &lt;{@code line:column}&gt; aus.
     * 
     * @return
     */
    public String toShortString() {
        return line + ":" + column;
    }

    /**
     * Vergleicht die aktuelle Position mit der übergebenen. Kann zum aufsteigenden Sortieren verwendet werden. Es wird
     * zunächst nach Zeilen, danach nach Spalten sortiert.
     */
    @Override
    public final int compareTo(final Position other) {
        if (this.line != other.line) {
            return this.line - other.line;
        }
        return this.column - other.column;
    }

    /**
     * Gibt die Zeile der Position zurück. Die erste Zeile einer Datei hat den Index 1.
     * 
     * @return Zeile der Position.
     */
    public int getLine() {
        return line;
    }

    /**
     * Gibt die Spalte der Position innerhalb einer Zeile zurück. Die erste Spalte hat den Index 1.
     * 
     * @return Spalte innerhalb der Zeile.
     */
    public int getColumn() {
        return column;
    }
}