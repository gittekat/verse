verse readme
============

eclipse trouble shooting:
- GC overhead limit exceeded
	http://docs.oseems.com/application/eclipse/fix-gc-overhead-limit-exceeded
	eclipse.ini
	-Xms512m
	-Xmx1024m
	
smartfox server:
- create VerseExtension folder and deploy server jar there
- deploy.jardesc -> create jar
- gdx.jar und guava.jar in lib folder of smartfox or in VerseExtension folder
- mysql connector in lib folder of smartfox