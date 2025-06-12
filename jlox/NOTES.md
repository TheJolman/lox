Use `java -cp app/build/classes/java/main <package>` to run .class files
- Ex: `java -cp app/build/classes/java/main tool.GenerateAst`
- Ex: `java -cp app/build/classes/java/main lox.Lox`

BUG: typing `/*` or `"` in the REPL results in 'Error: Unterminated multi-line comment' or
'Error: Unterminated string' when their should be multi-line support for both of
these.

`java -cp app/build/classes/java/main tool.GenerateAst ./app/src/main/java/lox`
