package com.imminentmeals.android.guava;

import java.util.NoSuchElementException;

/**
 * This class provides a skeletal implementation of the {@code Iterator}
 * interface, to make this interface easier to implement for certain types of
 * data sources.
 * <p/>
 * <p>{@code Iterator} requires its implementations to support querying the
 * end-of-data status without changing the iterator's state, using the {@link
 * #hasNext} method. But many data sources, such as {@link
 * java.io.Reader#read()}, do not expose this information; the only way to
 * discover whether there is any data left is by trying to retrieve it. These
 * types of data sources are ordinarily difficult to write iterators for. But
 * using this class, one must implement only the {@link #computeNext} method,
 * and invoke the {@link #endOfData} method when appropriate.
 * <p/>
 * <p>Another example is an iterator that skips over null elements in a backing
 * iterator. This could be implemented as: <pre>   {@code
 * <p/>
 *   public static Iterator<String> skipNulls(final Iterator<String> in) {
 *     return new AbstractIterator<String>() {
 *       protected String computeNext() {
 *         while (in.hasNext()) {
 *           String s = in._next();
 *           if (s != null) {
 *             return s;
 *           }
 *         }
 *         return endOfData();
 *       }
 *     };
 *   }}</pre>
 * <p/>
 * <p>This class supports iterators that include null elements.
 *
 * @author Kevin Bourrillion
 * @since 2.0 (imported from Google Collections Library)
 */
public abstract class AbstractIterator<T> extends UnmodifiableIterator<T> {

  /** Constructor for use by subclasses. */
  protected AbstractIterator() {}

  private enum State {
    /** We have computed the next element and haven't returned it yet. */
    READY,

    /** We haven't yet computed or have already returned the element. */
    NOT_READY,

    /** We have reached the end of the data and are finished. */
    DONE,

    /** We've suffered an exception and are kaput. */
    FAILED,
  }

  /**
   * Returns the next element. <b>Note:</b> the implementation must call {@link
   * #endOfData()} when there are no elements left in the iteration. Failure to
   * do so could result in an infinite loop.
   * <p/>
   * <p>The initial invocation of {@link #hasNext()} or {@link #next()} calls
   * this method, as does the first invocation of {@code hasNext} or {@code
   * _next} following each successful call to {@code _next}. Once the
   * implementation either invokes {@code endOfData} or throws an exception,
   * {@code computeNext} is guaranteed to never be called again.
   * <p/>
   * <p>If this method throws an exception, it will propagate outward to the
   * {@code hasNext} or {@code _next} invocation that invoked this method. Any
   * further attempts to use the iterator will result in an {@link
   * IllegalStateException}.
   * <p/>
   * <p>The implementation of this method may not invoke the {@code hasNext},
   * {@code _next}, or {@link #peek()} methods on this instance; if it does, an
   * {@code IllegalStateException} will result.
   *
   * @return the next element if there was one. If {@code endOfData} was called
   * during execution, the return value will be ignored.
   * @throws RuntimeException if any unrecoverable error happens. This exception
   *                          will propagate outward to the {@code hasNext()}, {@code _next()}, or
   *                          {@code peek()} invocation that invoked this method. Any further
   *                          attempts to use the iterator will result in an
   *                          {@link IllegalStateException}.
   */
  protected abstract T computeNext();

  /**
   * Implementations of {@link #computeNext} <b>must</b> invoke this method when
   * there are no elements left in the iteration.
   *
   * @return {@code null}; a convenience so your {@code computeNext}
   * implementation can use the simple statement {@code return endOfData();}
   */
  protected final T endOfData() {
    _state = State.DONE;
    return null;
  }

  @Override
  public final boolean hasNext() {
    Preconditions.checkState(_state != State.FAILED);
    switch (_state) {
      case DONE:
        return false;
      case READY:
        return true;
      default:
    }
    return tryToComputeNext();
  }

  private boolean tryToComputeNext() {
    _state = State.FAILED; // temporary pessimism
    _next = computeNext();
    if (_state != State.DONE) {
      _state = State.READY;
      return true;
    }
    return false;
  }

  @Override public final T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    _state = State.NOT_READY;
    final T result = _next;
    _next = null;
    return result;
  }

  /**
   * Returns the _next element in the iteration without advancing the iteration.
   * <p/>
   * <p>Implementations of {@code AbstractIterator} that wish to expose this
   * functionality should implement {@code PeekingIterator}.
   */
  public final T peek() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return _next;
  }

  private State _state = State.NOT_READY;
  private T _next;
}
