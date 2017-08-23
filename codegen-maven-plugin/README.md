# codegen-maven-plugin

### Download und Installation

Um die Komponenten aus dem Projekt antlr-editorkit zu verwenden, reicht es aus, diese herunterzuladen, mit maven zu compilieren und in sein Projekt zu integrieren. Das geht ungefähr wie folgt:

```
git clone https://github.com/chrlembeck/codegen.git
cd codgen-maven-plugin
mvn clean install
```

Um sich die Hilfe zum Plugin anzeigen zu lassen:

```
mvn de.chrlembeck.codegen:codegen-maven-plugin:0.0.1-SNAPSHOT:help -Ddetail=true
```