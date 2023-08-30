# mockito-static-mockmaker

This is a naive mockito mockmaker which supports `mockStatic` globally.
`mockStatic` stubbing in mockito-core has no effect if invoking the method in new threads.
This project makes it work in multiple threads context.

Check the [unit test](src/test/java/GlobalStaticMockMakerTest.java) for usage.
