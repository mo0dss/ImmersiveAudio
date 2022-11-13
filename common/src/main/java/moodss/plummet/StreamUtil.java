package moodss.plummet;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class StreamUtil {

    public static <T, U> T apply(Stream<U> stream, T output, BiFunction<T, U, T> factory) {
        for(Iterator<U> it = stream.iterator(); it.hasNext(); ) {
            var next = it.next();

            output = factory.apply(output, next);
        }

        return output;
    }
}
