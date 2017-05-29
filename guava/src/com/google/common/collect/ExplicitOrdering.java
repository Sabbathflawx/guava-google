/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

/** An ordering that compares objects according to a given order. */
@GwtCompatible(serializable = true)
final class ExplicitOrdering<T> extends Ordering<T> implements Serializable {
  static enum UnknownOrdering {
	  UNKNOWN_FIRST,
	  UNKNOWN_LAST
  }

  final ImmutableMap<T, Integer> rankMap;
  final UnknownOrdering unknownOrdering;

  ExplicitOrdering(List<T> valuesInOrder) {
    this(valuesInOrder, null);
  }

  ExplicitOrdering(List<T> valuesInOrder, UnknownOrdering unknownOrdering) {
	  this(Maps.indexMap(valuesInOrder), unknownOrdering);
  }

  ExplicitOrdering(ImmutableMap<T, Integer> rankMap) {
    this(rankMap, null);
  }

  ExplicitOrdering(ImmutableMap<T, Integer> rankMap, UnknownOrdering unknownOrdering) {
	  this.rankMap = rankMap;
	  this.unknownOrdering = unknownOrdering;
  }

  @Override
  public int compare(T left, T right) {
    return rank(left) - rank(right); // safe because both are nonnegative
  }

  private int rank(T value) {
    Integer rank = rankMap.get(value);
    if (rank == null) {
      if (unknownOrdering != null) {
        switch (unknownOrdering) {
          case UNKNOWN_FIRST:
            return -1;
          case UNKNOWN_LAST:
            return rankMap.size();
        }
      }
      throw new IncomparableValueException(value);
    }
    return rank;
  }

  @Override
  public boolean equals(@Nullable Object object) {
    if (object instanceof ExplicitOrdering) {
      ExplicitOrdering<?> that = (ExplicitOrdering<?>) object;
      return this.rankMap.equals(that.rankMap);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return rankMap.hashCode();
  }

  @Override
  public String toString() {
    return "Ordering.explicit(" + rankMap.keySet() + ")";
  }

  private static final long serialVersionUID = 0;
}
