package de.chrlembeck.codegen.generator.output;

public enum OverwritePreferences {

    /**
     * Zeigt an, dass eine existierende Datei ohne Fragen überschrieben werden darf.
     */
    OVERWRITE,

    /**
     * Sorgt vor dem Überschreiben einer Datei für das Werfen einer Exception.
     */
    THROW_EXCEPTION,

    /**
     * Überspringt das Überschreibend er Datei und behält statt dessen die existierende Datei.
     */
    KEEP_EXISTING
}