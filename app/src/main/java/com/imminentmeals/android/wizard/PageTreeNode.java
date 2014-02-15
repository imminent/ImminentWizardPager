package com.imminentmeals.android.wizard;

import com.imminentmeals.android.guava.Optional;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents a node in the page tree. Can either be a single page, or a page container.
 */
@ParametersAreNonnullByDefault
public interface PageTreeNode {

  public <T extends Page> Optional<T> findByKey(String key);

  public void flattenCurrentPageSequence(List<Page> dest);
}
