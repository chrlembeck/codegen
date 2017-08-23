package de.chrlembeck.codegen;

/**
 * Definiert die Scopes, zu denen ein Generiertes Artefakt im Rahmen eines Maven builds hinzugefügt werden kann.
 * 
 * @author Christoph Lembeck
 */
public enum ArtifactScope {
    /**
     * Entspricht dem Compile-Scope eines Maven-Builds. Artefakte in diesem Scope werden wie normale Java-Klassen in das
     * Projekt compiliert.
     */
    COMPILE,

    /**
     * Entspricht dem Test-Scope eines Maven-Builds. Artefakte in diesem Scope werden wie Test-Klassen in das Projekt
     * compiliert.
     */
    TEST,

    /**
     * Entspricht dem Script-Scope eines Maven-Builds. Artefkate in diesem Scope werden wir scripte behandelt.
     */
    SCRIPT;
}