Facade / Abstraction / Clojure API for `com.badlogic.gdx`
Each class one namespace with same name
So we can rewrite it later
And know what we depend on

=> THE IDEA IS WE CAN REWRITE IT TO CLOJUR EONE DAY OR STEP BY STEP WITHOUT TOUCHING EXISTING CODE!


```
lein run -m app-test
```

RULES:
* each ns exact class name (even if extra long)
* no cross dependencies, only imports
* no reflection warnings
* no exit type hints
