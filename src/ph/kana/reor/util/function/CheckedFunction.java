package ph.kana.reor.util.function;

@FunctionalInterface
public interface CheckedFunction<T, R> {
	R apply(T t) throws Exception;
}
