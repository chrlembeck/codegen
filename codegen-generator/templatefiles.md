# Template Files

## Einleitung

Template Dateien sind die wichtigsten Dokumente innerhalb eines Code-Generators. Sie enthalten die Regeln für die Transformation von Modellen in die gewünschten Ziel-Artefakte.

Innerhalb des CodeGen-Projekts entsprechen die Modelle beliebigen Java-Objekten und die Ziel-Artefakte können jegliche Arten von Text-Dateien beliebigen Encodings sein. Der Aufbau und die Funktionsweise der Template-Dateien ist in den folgenden Kapiteln beschrieben.

## Aufbau einer Template-Datei

### Struktur der Template-Datei
Eine Template-Datei besteht im groben aus zwei Abschnitten. Einem optionalen Header und den eigentlichen Template-Defintionen. Der Header beinhaltet für die Datei global gültige Definitionen, wie z.B. Imports von Templates aus anderen Template-Dateien.

In den anschließenden Template-Defintionen werden die eigentlichen Transformationsregeln von Modellen oder Modell-Teilen in Teile der Ziel-Artefakte definiert. Beide Abschnitte der Template-Datei können mit optionalen Kommentaren angereichert werden.

Innerhalb von Template-Definitionen kann beliebiger Text enthalten sein, der später in die zu erzeugenden Zieldokumente eingefügt wird. Zudem kann auf eine Liste von Befehlen zurückgegriffen werden, die den Kontrollfluss des Generators beeinflussen oder variable Werte in die Artefakte einfügen können.

### Kommentare
Kommentare werden in einer Template-Datei durch die beiden Tags `«COMMENT»` und `«ENDCOMMENT»` umschlossen. Der in ihnen enthaltene Text dient lediglich dem Verständnis des Lesers und wird vom Code-Generator ignoriert.

```
«COMMENT»Beispiel für ein Kommentar«ENDCOMMENT»

«COMMENT»
  Noch ein Beispiel für ein Kommentar.
«ENDCOMMENT»
```

Kommentare dürfen sowohl im Header der Template-Datei als auch zwischen oder sogar innerhalb von Template-Definitionen verwendet werden.

### Template-Definitionen
Template-Definitionen werden durch die Tags `«TEMPLATE»` und `«ENDTEMPLATE»` umrahmt und dienen der Transformation eines Modells oder Modellteils in einen Teil der zu erstellenden Artefakte. Jede Template-Definition besitzt dafür einen Namen und einen Java-Typ, dessen Objekte sie verarbeiten kann. Jede Kombination aus Name und Typ einer Template-Definition darf innerhalb einer Template-Datei nur einmal vorkommen, da der Generator sonst Probleme beim Auflösen zu verwendender Templates bekommt.

Name und Typ einer Template-Definition werden wie im folgenden Beispiel angegeben:

```
«COMMENT»Ein Template Namens 'name' für die Verarbeitung von Strings«ENDCOMMENT»
«TEMPLATE name FOR java.lang.String»
«ENDTEMPLATE»
```

Ein Generatorlauf wird in der Regel mit der Übergabe eines Modells, einer Template-Datei und einem initialen Templatenamen begonnen. Der Generator liest dann die entsprechende Template-Datei ein und sucht in ihr nach dem ersten Template, welches den gewünschten Namen besitzt und Objekte des Modelltyps verarbeiten kann. Der Typ der Template-Definition muss dabei nicht exakt dem Laufzeittyp-Des Modells entsprechen. Es reicht, wie bei einem Java-Methodenaufruf aus, wenn das Modell eine Subklasse des Typs der Template-Definition ist. 

Template-Definitionen mit angegebenen Typ `java.lang.Object` können somit sämtliche Modellelemente verarbeiten. Bei der Anordnung der Template-Definitionen innerhalb einer Template-Datei ist daher darauf zu achten, dass Defintionen mit generellen Typen unterhalb der Definitionen gleichen Namens mit spezielleren Typen angeordnet werden, da diese sonst nie zur Generierung harangezogen werden.

Die Inhalte einer Template-Definition werden in den folgenden Kapiteln beschrieben

### Das Output-Statement
Output-Statements steuern die Ausgabe der generierten Artefakte in einzelne Ausgabeströme. In der Regel entsprechen diese Ströme den zu erzeugenden Dateien. Es sind aber auch andere Ausgabekanäle, wie z.B. StringBuffer, Gui-Komponenten oder andere benutzerdefinierbare Ausgabekanäle nutzbar.

Jeder Ausgabestrom besitzt einen eindeutigen Namen. Über die Wahl des Output-Objekts und seine Konfiguration kann gesetuert werden, wohin die Daten zu einzelnen Strömen geschrieben werden. In der Standardkonfiguration verwendet man aus AusgabeObjekt eine Instanz von `FileOutput`, welche die Ausgabeströme in jeweils eigene Dateien lenkt. Der Name der Datei wird dabei aus dem Namen des Stroms abgeleitet.

Innerhalb des Output-Statements wird der Name für den Ausgabestrom über einen zur Laufzeit ausgewerteten Ausdruck bestimmt. So kann der Strom sowohl konstant vorgegeben werden, als auch über die jeweiligen Inhalte des verwendeten Modells beeinflusst werden.
Das OutputStatemant hat dazu die folgende Syntax:

```
«COMMENT»Definition eines Ausgabestroms«ENDCOMMENT»
«OUTPUT nameExpression»
    «COMMENT»Der Inhalt der Ausgabe steht zwischen den Output-Tags«ENDCOMMENT»
«ENDOUTPUT»
```

Konkret könnte ein Template mit Ausgabe dann wie folgt aussehen:
 
```
«TEMPLATE root FOR java.lang.Object»
  «OUTPUT "mydir/test.txt"»
    Dies ist der konstante Inhalt der Datei mydir/test.txt
  «ENDOUTPUT»
«ENDTEMPLATE»
```

Innerhalb eines Output-Statements darf natürlich nicht nur statischer Text verwendet werden, sondern eine beliebige Kombination aus Text und weiteren Statements, welche dann während der Generierung ausgeführt werden. So ist es z.B. möglich Ausdrücke aus den Inhalten des Modells auszuwerten oder weitere Sub-Templates auf zu rufen. Es ist sogar möglich Output-Statements zu verschachteln. Die Ausgabe erfolgt dann jedoch nur in den jeweils zuletzt definierten (innersten) Ausgabestrom. Wird ein inneres Output-Statement verlassen, erfolgt die weitere Ausgabe dann wieder in den vor der Verschachtelung verwendeten Strom.

### Das Expression-Statement
Mit Hilfe des Expression-Statements ist es möglich, auf Variablen und Elemente des Modells zuzugreifen, Java-Methoden aufzurufen oder einfache Berechnungen anzustellen. In den Ausgabestrom werden dann nur die jeweiligen Ergebnisse der Auswertung übernommen. Ein Expression-Statement wird von doppelten spitzen Klammern umrahmt und beinhaltet lediglich den darin auszuwertenden Ausdruck:

```
«expression»
```

Als Expression können hier nahezu beliebige Java-Expressions verwendet werden, wie man sie aus der normalen Programmierung gewohnt ist. Explizit nicht zulässig sind allerdings Variablenzuweisungen, Lambda-Expressions oder die Verwendung des `new`-Operators.

Um auf das Modell oder das Teil des Modells, welches einem Template übergeben wurde zuzugreifen, kann die Objektreferenz `this` verwendet werden. Einige Beispiele für Expression-Statements gibt es hier:

```
«TEMPLATE root FOR java.lang.String»
  1 + 2 ist: «1 + 2»
  Der Inhalt des Modells: «this»
  Die Länge des Strings: «this.length()»
  Die Aktuelle Zeit ist: «java.time.LocalTime.now()»
«ENDTEMPLATE»
```

### Das If-Statement

### Das For-Statement

### Das Execute-Statement

 

