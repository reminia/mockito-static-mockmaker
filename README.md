# mockito-static-mockmaker ![ci](https://github.com/reminia/mockito-static-mockmaker/actions/workflows/push.yml/badge.svg)

This is a naive mockito mockmaker which supports `mockStatic` globally.

`mockStatic` stubbing in mockito-core has no effect if invoking the method in new threads.

This extension makes it work in multiple threads context and supports both mockito3 & mockito5.

Check the [unit test](src/test/java/GlobalStaticMockMakerTest.java) for examples.

# Usage

Add below dependency to your pom.xml.

```xml
<dependency>
  <groupId>me.yceel.mockito</groupId>
  <artifactId>mockito-static-mockmaker</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```
