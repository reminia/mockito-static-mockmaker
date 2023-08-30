# mockito-static-mockmaker ![ci](https://github.com/reminia/mockito-static-mockmaker/actions/workflows/pr.yml/badge.svg)

This is a naive mockito mockmaker which supports `mockStatic` globally.

`mockStatic` stubbing in mockito-core has no effect if invoking the method in new threads.

This custom mock maker makes it work in multiple threads context.

Check the [unit test](src/test/java/GlobalStaticMockMakerTest.java) for usage.
