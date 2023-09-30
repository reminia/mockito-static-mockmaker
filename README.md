# mockito-static-mockmaker ![ci](https://github.com/reminia/mockito-static-mockmaker/actions/workflows/push.yml/badge.svg)

This is a naive mockito mockmaker to support `mockStatic` globally.

`mockStatic` stubbing in mockito-core has no effects if the stub method is invoked in new threads.

This extension makes it work in multiple threads context and supports both mockito3 & mockito5.

# Setup

Include the dependence in your project, the plugin is enabled by [default](src/main/resources/mockito-extensions/org.mockito.plugins.MockMaker).

```xml
<dependency>
  <groupId>me.yceel.mockito</groupId>
  <artifactId>mockito-static-mockmaker</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

# Usage

It's a plugin, no any change to the original mockito api, check the [unit test](src/test/java/GlobalStaticMockMakerTest.java) for examples.
